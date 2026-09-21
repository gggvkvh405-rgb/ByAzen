package rtx.byazen.api.music;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Обёртка над потоком интернет-радио: радиостанции вставляют в аудиопоток блоки с метаданными
 * (ICY / Shoutcast). Класс вырезает эти блоки, чтобы MP3-декодер получал только звук, и попутно
 * разбирает заголовок текущего трека.
 * <p>
 * Идея №1 из IDEAS.md — «ICY-метаданные: показывать, что сейчас играет в эфире».
 */
public final class IcyStream
extends InputStream {

    private static final int MAX_METADATA = 4080;
    private static final int TITLE_LIMIT = 120;

    private final InputStream source;
    private final int interval;
    private final Consumer<String> titleCallback;
    private final byte[] metadata = new byte[MAX_METADATA];
    private final byte[] single = new byte[1];
    private int audioRemaining;
    private int metadataRemaining;
    private String lastTitle = "";

    public IcyStream(InputStream source, int interval, Consumer<String> titleCallback) {
        this.source = source;
        this.interval = Math.max(1, interval);
        this.titleCallback = titleCallback;
        this.audioRemaining = this.interval;
    }

    /** Размер блока метаданных из заголовка ответа (0, если станция их не отдаёт). */
    public static int metadataInterval(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return 0;
        }
        try {
            int value = Integer.parseInt(headerValue.trim());
            return value > 0 && value <= 1 << 20 ? value : 0;
        }
        catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    @Override
    public int read() throws IOException {
        int value = this.read(this.single, 0, 1);
        return value <= 0 ? -1 : this.single[0] & 0xFF;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (length <= 0) {
            return 0;
        }
        int written = 0;
        while (written < length) {
            if (this.metadataRemaining > 0) {
                int skipped = (int) this.source.skip(this.metadataRemaining);
                if (skipped <= 0) {
                    int next = this.source.read();
                    if (next < 0) {
                        return written == 0 ? -1 : written;
                    }
                    skipped = 1;
                }
                this.metadataRemaining -= skipped;
                continue;
            }
            if (this.audioRemaining <= 0) {
                if (!this.readMetadata()) {
                    return written == 0 ? -1 : written;
                }
                this.audioRemaining = this.interval;
                continue;
            }
            int want = Math.min(length - written, this.audioRemaining);
            int read = this.source.read(buffer, offset + written, want);
            if (read < 0) {
                return written == 0 ? -1 : written;
            }
            written += read;
            this.audioRemaining -= read;
        }
        return written;
    }

    /** Читает один блок метаданных; false — поток закончился. */
    private boolean readMetadata() throws IOException {
        int lengthByte = this.source.read();
        if (lengthByte < 0) {
            return false;
        }
        int length = (lengthByte & 0xFF) * 16;
        if (length == 0) {
            return true;
        }
        if (length > MAX_METADATA) {
            this.metadataRemaining = length;
            return true;
        }
        int read = 0;
        while (read < length) {
            int chunk = this.source.read(this.metadata, read, length - read);
            if (chunk < 0) {
                return false;
            }
            read += chunk;
        }
        this.publish(new String(this.metadata, 0, read, StandardCharsets.ISO_8859_1));
        return true;
    }

    private void publish(String raw) {
        int start = raw.indexOf("StreamTitle='");
        if (start < 0) {
            return;
        }
        start += "StreamTitle='".length();
        int end = raw.indexOf("';", start);
        String title = (end > start ? raw.substring(start, end) : raw.substring(start)).trim();
        if (title.isEmpty() || title.equals(this.lastTitle)) {
            return;
        }
        if (title.length() > TITLE_LIMIT) {
            title = title.substring(0, TITLE_LIMIT);
        }
        this.lastTitle = title;
        if (this.titleCallback != null) {
            this.titleCallback.accept(title);
        }
    }

    @Override
    public void close() throws IOException {
        this.source.close();
    }
}
