package rtx.byazen.api.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import rtx.byazen.ByAzen;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Плейлисты игрока (идея №3 из IDEAS.md): свои наборы станций и треков, которые живут в конфиге
 * клиента и переживают перезапуск.
 * <p>
 * Плейлист — просто упорядоченный список ссылок: станция из библиотеки, найденный в каталоге трек,
 * своя ссылка или локальный файл. Из окна плеера плейлист играется как обычная очередь — с повтором,
 * перемешиванием, кроссфейдом и эквалайзером.
 * <p>
 * Всё хранится в читаемом JSON рядом с остальными конфигами (`music_playlists.byazen`).
 */
public final class MusicPlaylists {

    private static final String FILE = "music_playlists";
    /** Сколько плейлистов влезает в интерфейс (и в конфиг) без потери смысла. */
    public static final int MAX_PLAYLISTS = 40;
    private static final int MAX_TRACKS = 500;

    private static final MusicPlaylists INSTANCE = new MusicPlaylists();

    /** Один плейлист: название и список треков. */
    public static final class Playlist {

        private String name;
        private final List<MusicTrack> tracks = new ArrayList<MusicTrack>();

        Playlist(String name) {
            this.name = name;
        }

        public String name() {
            return this.name;
        }

        public List<MusicTrack> tracks() {
            return this.tracks;
        }

        public int size() {
            return this.tracks.size();
        }

        void rename(String value) {
            this.name = value;
        }
    }

    private final List<Playlist> playlists = new ArrayList<Playlist>();
    private int selected;

    private MusicPlaylists() {
        this.load();
    }

    public static MusicPlaylists get() {
        return INSTANCE;
    }

    public List<Playlist> all() {
        return new ArrayList<Playlist>(this.playlists);
    }

    public int count() {
        return this.playlists.size();
    }

    public Playlist get(int index) {
        return index >= 0 && index < this.playlists.size() ? this.playlists.get(index) : null;
    }

    public int selectedIndex() {
        if (this.playlists.isEmpty()) {
            return -1;
        }
        return Math.max(0, Math.min(this.playlists.size() - 1, this.selected));
    }

    public Playlist selected() {
        return this.get(this.selectedIndex());
    }

    public void select(int index) {
        if (index >= 0 && index < this.playlists.size()) {
            this.selected = index;
        }
    }

    /** Выбирает плейлист по имени (точное совпадение, потом по началу строки). */
    public int selectByName(String name) {
        int index = this.indexOf(name);
        if (index >= 0) {
            this.selected = index;
        }
        return index;
    }

    public int indexOf(String name) {
        if (name == null || name.isBlank()) {
            return -1;
        }
        String needle = name.trim().toLowerCase(Locale.ROOT);
        for (int i = 0; i < this.playlists.size(); ++i) {
            if (this.playlists.get(i).name().toLowerCase(Locale.ROOT).equals(needle)) {
                return i;
            }
        }
        for (int i = 0; i < this.playlists.size(); ++i) {
            if (this.playlists.get(i).name().toLowerCase(Locale.ROOT).startsWith(needle)) {
                return i;
            }
        }
        return -1;
    }

    /** Создаёт плейлист; пустое имя превращается в «Плейлист N». */
    public Playlist create(String name) {
        if (this.playlists.size() >= MAX_PLAYLISTS) {
            return null;
        }
        String title = name == null ? "" : name.trim();
        if (title.isBlank()) {
            title = this.freeName();
        }
        Playlist playlist = new Playlist(title);
        this.playlists.add(playlist);
        this.selected = this.playlists.size() - 1;
        this.save();
        return playlist;
    }

    private String freeName() {
        for (int number = 1; number <= MAX_PLAYLISTS + 1; ++number) {
            String candidate = "Плейлист " + number;
            if (this.indexOf(candidate) < 0) {
                return candidate;
            }
        }
        return "Плейлист";
    }

    public boolean rename(int index, String name) {
        Playlist playlist = this.get(index);
        if (playlist == null || name == null || name.isBlank()) {
            return false;
        }
        playlist.rename(name.trim());
        this.save();
        return true;
    }

    public boolean remove(int index) {
        if (index < 0 || index >= this.playlists.size()) {
            return false;
        }
        this.playlists.remove(index);
        if (this.selected >= this.playlists.size()) {
            this.selected = Math.max(0, this.playlists.size() - 1);
        }
        this.save();
        return true;
    }

    /** Добавляет трек; повторные ссылки не дублируются. */
    public boolean add(int index, MusicTrack track) {
        Playlist playlist = this.get(index);
        if (playlist == null || track == null || track.url().isBlank() || playlist.size() >= MAX_TRACKS) {
            return false;
        }
        for (MusicTrack existing : playlist.tracks()) {
            if (existing.key().equals(track.key())) {
                return false;
            }
        }
        playlist.tracks().add(track);
        this.save();
        return true;
    }

    public boolean removeTrack(int index, int trackIndex) {
        Playlist playlist = this.get(index);
        if (playlist == null || trackIndex < 0 || trackIndex >= playlist.size()) {
            return false;
        }
        playlist.tracks().remove(trackIndex);
        this.save();
        return true;
    }

    /** Переставляет трек внутри плейлиста (порядок сохраняется в конфиг). */
    public boolean move(int index, int from, int to) {
        Playlist playlist = this.get(index);
        if (playlist == null || from < 0 || from >= playlist.size()) {
            return false;
        }
        int target = Math.max(0, Math.min(playlist.size() - 1, to));
        if (target == from) {
            return false;
        }
        MusicTrack track = playlist.tracks().remove(from);
        playlist.tracks().add(target, track);
        this.save();
        return true;
    }

    /** Играет плейлист целиком; возвращает трек, с которого начали. */
    public MusicTrack play(int index, int startTrack) {
        Playlist playlist = this.get(index);
        if (playlist == null || playlist.size() == 0) {
            return null;
        }
        int start = Math.max(0, Math.min(playlist.size() - 1, startTrack));
        MusicTrack track = playlist.tracks().get(start);
        MusicEngine.get().play(playlist.tracks(), start);
        MusicLibrary.get().pushRecent(track);
        return track;
    }

    public MusicTrack play(int index) {
        return this.play(index, 0);
    }

    /** Сводка для чата и подсказок. */
    public String summary() {
        if (this.playlists.isEmpty()) {
            return "Плейлистов пока нет — создайте первый: .pl new Мой список";
        }
        StringBuilder builder = new StringBuilder("Плейлистов: ").append(this.playlists.size()).append(". ");
        int limit = Math.min(4, this.playlists.size());
        for (int i = 0; i < limit; ++i) {
            if (i > 0) {
                builder.append(" · ");
            }
            builder.append('«').append(this.playlists.get(i).name()).append("»(").append(this.playlists.get(i).size()).append(')');
        }
        if (this.playlists.size() > limit) {
            builder.append(" · …");
        }
        return builder.toString();
    }

    private void load() {
        try {
            JsonObject root = RepositoryStorage.readObject(FILE);
            JsonArray array = root.getAsJsonArray("playlists");
            if (array == null) {
                return;
            }
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                String name = object.has("name") && object.get("name").isJsonPrimitive()
                        ? object.get("name").getAsString() : "Плейлист";
                Playlist playlist = new Playlist(name);
                MusicLibrary.readTracks(object.getAsJsonArray("tracks"), playlist.tracks());
                this.playlists.add(playlist);
            }
            if (root.has("selected")) {
                this.selected = Math.max(0, root.get("selected").getAsInt());
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Music playlists could not be loaded: {}", throwable.toString());
        }
    }

    /** Сохраняет плейлисты; вызывается и с фоновых потоков, поэтому synchronized. */
    public synchronized void save() {
        try {
            JsonObject root = new JsonObject();
            JsonArray array = new JsonArray();
            for (Playlist playlist : this.playlists) {
                JsonObject object = new JsonObject();
                object.addProperty("name", playlist.name());
                object.add("tracks", MusicLibrary.writeTracks(playlist.tracks()));
                array.add(object);
            }
            root.add("playlists", array);
            root.addProperty("selected", this.selected);
            RepositoryStorage.write(FILE, root);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Music playlists could not be saved: {}", throwable.toString());
        }
    }
}
