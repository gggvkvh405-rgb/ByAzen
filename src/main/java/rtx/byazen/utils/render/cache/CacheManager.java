package rtx.byazen.utils.render.cache;

import java.util.ArrayList;
import java.util.List;
import rtx.byazen.api.music.MusicCovers;
import rtx.byazen.utils.render.LocalImages;
import rtx.byazen.utils.render.render2d.gif.GifRenderer;

/**
 * Кэш иконок и обложек с лимитом (идея №164 из IDEAS.md).
 * <p>
 * Картинки нельзя держать в памяти бесконечно: каждая обложка трека, локальная иконка и кадр GIF
 * занимают видеопамять. У каждого кэша есть предел и вытеснение старого, а здесь видно, сколько
 * сейчас лежит, и можно поднять или опустить предел — или очистить всё одной кнопкой.
 */
public final class CacheManager {

    /** Один кэш: что лежит, сколько можно, чем чистить. */
    public static final class Row {

        public final String key;
        public final String label;
        public final int size;
        public final int limit;
        public final long bytes;
        public final String note;

        Row(String key, String label, int size, int limit, long bytes, String note) {
            this.key = key;
            this.label = label;
            this.size = size;
            this.limit = limit;
            this.bytes = bytes;
            this.note = note;
        }

        public String sizeText() {
            return this.size + " / " + this.limit;
        }

        public String bytesText() {
            return this.bytes <= 0L ? this.note : CacheManager.bytes(this.bytes);
        }
    }

    private CacheManager() {
    }

    public static List<CacheManager.Row> rows() {
        ArrayList<CacheManager.Row> rows = new ArrayList<CacheManager.Row>();
        rows.add(new CacheManager.Row("covers", "Обложки треков", MusicCovers.cacheSize(), MusicCovers.cacheLimit(),
                0L, "текстуры в видеопамяти"));
        rows.add(new CacheManager.Row("images", "Локальные картинки", LocalImages.cacheSize(), LocalImages.cacheLimit(),
                0L, "текстуры в видеопамяти"));
        rows.add(new CacheManager.Row("gifs", "GIF-анимации", GifRenderer.cacheSize(), GifRenderer.cacheLimit(),
                GifRenderer.cacheBytes(), "кадры + видеопамять"));
        return rows;
    }

    public static int totalEntries() {
        int total = 0;
        for (CacheManager.Row row : CacheManager.rows()) {
            total += row.size;
        }
        return total;
    }

    public static long totalBytes() {
        long bytes = 0L;
        for (CacheManager.Row row : CacheManager.rows()) {
            bytes += row.bytes;
        }
        return bytes;
    }

    /** Меняет предел кэша. Возвращает false, если такого кэша нет. */
    public static boolean setLimit(String key, int limit) {
        int value = Math.max(4, Math.min(512, limit));
        switch (key) {
            case "covers": {
                MusicCovers.setCacheLimit(value);
                return true;
            }
            case "images": {
                LocalImages.setCacheLimit(value);
                return true;
            }
            case "gifs": {
                GifRenderer.setCacheLimit(value);
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public static int limit(String key) {
        switch (key) {
            case "covers": {
                return MusicCovers.cacheLimit();
            }
            case "images": {
                return LocalImages.cacheLimit();
            }
            case "gifs": {
                return GifRenderer.cacheLimit();
            }
            default: {
                return 0;
            }
        }
    }

    public static String label(String key) {
        switch (key) {
            case "covers": {
                return "Обложки треков";
            }
            case "images": {
                return "Локальные картинки";
            }
            case "gifs": {
                return "GIF-анимации";
            }
            default: {
                return key;
            }
        }
    }

    public static void clearAll() {
        MusicCovers.clear();
        LocalImages.clear();
        GifRenderer.clear();
    }

    public static void clear(String key) {
        switch (key) {
            case "covers": {
                MusicCovers.clear();
                break;
            }
            case "images": {
                LocalImages.clear();
                break;
            }
            case "gifs": {
                GifRenderer.clear();
                break;
            }
            default: {
                break;
            }
        }
    }

    public static String summary() {
        StringBuilder builder = new StringBuilder();
        for (CacheManager.Row row : CacheManager.rows()) {
            if (builder.length() > 0) {
                builder.append(" · ");
            }
            builder.append(row.label).append(' ').append(row.sizeText());
        }
        long bytes = CacheManager.totalBytes();
        if (bytes > 0L) {
            builder.append(" · кадры GIF: ").append(CacheManager.bytes(bytes));
        }
        return builder.toString();
    }

    private static String bytes(long size) {
        if (size < 1024L) {
            return size + " Б";
        }
        if (size < 1048576L) {
            return String.format(java.util.Locale.ROOT, "%.0f КБ", (double)size / 1024.0);
        }
        return String.format(java.util.Locale.ROOT, "%.1f МБ", (double)size / 1048576.0);
    }
}
