package rtx.byazen.api.music;

import java.util.Locale;

/**
 * One playable entry of the ByAzen music player: an internet radio station, a track from an
 * online catalogue or a local file.
 */
public final class MusicTrack {

    public enum Kind {
        RADIO,
        TRACK,
        LINK,
        LOCAL
    }

    private final Kind kind;
    private final String title;
    private final String subtitle;
    private final String url;
    private final String coverUrl;
    private final String badge;
    private final long durationMs;
    private final String sourceId;

    public MusicTrack(Kind kind, String title, String subtitle, String url, String coverUrl, String badge, long durationMs) {
        this(kind, title, subtitle, url, coverUrl, badge, durationMs, null);
    }

    public MusicTrack(Kind kind, String title, String subtitle, String url, String coverUrl, String badge, long durationMs, String sourceId) {
        this.kind = kind;
        this.title = title == null || title.isBlank() ? "Без названия" : title.trim();
        this.subtitle = subtitle == null ? "" : subtitle.trim();
        this.url = url == null ? "" : url.trim();
        this.coverUrl = coverUrl == null || coverUrl.isBlank() ? null : coverUrl.trim();
        this.badge = badge == null ? "" : badge;
        this.durationMs = Math.max(0L, durationMs);
        this.sourceId = sourceId == null || sourceId.isBlank() ? null : sourceId.trim();
    }

    public Kind kind() {
        return this.kind;
    }

    public String title() {
        return this.title;
    }

    public String subtitle() {
        return this.subtitle;
    }

    public String url() {
        return this.url;
    }

    public String coverUrl() {
        return this.coverUrl;
    }

    /** Short label of the source, e.g. "Радио" or "Audius". */
    public String badge() {
        return this.badge;
    }

    public long durationMs() {
        return this.durationMs;
    }

    /** Catalogue id (radio-browser station id) used to refresh a dead stream link. */
    public String sourceId() {
        return this.sourceId;
    }

    /** Same track with another stream url, e.g. after the station moved. */
    public MusicTrack withUrl(String newUrl) {
        return new MusicTrack(this.kind, this.title, this.subtitle, newUrl, this.coverUrl, this.badge, this.durationMs, this.sourceId);
    }

    /** Same track with a known length (online catalogues report it after the first play). */
    public MusicTrack withDuration(long newDurationMs) {
        return new MusicTrack(this.kind, this.title, this.subtitle, this.url, this.coverUrl, this.badge, newDurationMs, this.sourceId);
    }

    public boolean isLocal() {
        return this.kind == Kind.LOCAL;
    }

    public boolean isRadio() {
        return this.kind == Kind.RADIO;
    }

    /** Live streams have no known length. */
    public boolean isLive() {
        return this.durationMs <= 0L;
    }

    public String key() {
        return this.url;
    }

    @Override
    public String toString() {
        return this.title + (this.subtitle.isEmpty() ? "" : " — " + this.subtitle);
    }

    /** Builds a readable title for a raw link or a local path. */
    public static String describeUrl(String url) {
        if (url == null || url.isBlank()) {
            return "Ссылка";
        }
        String clean = url.trim();
        int query = clean.indexOf('?');
        if (query > 0) {
            clean = clean.substring(0, query);
        }
        while (clean.endsWith("/")) {
            clean = clean.substring(0, clean.length() - 1);
        }
        int slash = clean.lastIndexOf('/');
        String name = slash >= 0 ? clean.substring(slash + 1) : clean;
        if (name.isBlank()) {
            return clean;
        }
        String lower = name.toLowerCase(Locale.ROOT);
        for (String ext : new String[]{".mp3", ".ogg", ".wav", ".aiff", ".aif", ".au", ".m4a", ".flac"}) {
            if (lower.endsWith(ext)) {
                name = name.substring(0, name.length() - ext.length());
                break;
            }
        }
        return name.replace('_', ' ').trim();
    }
}
