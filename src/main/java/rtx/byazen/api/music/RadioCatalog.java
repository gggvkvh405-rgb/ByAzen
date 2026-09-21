package rtx.byazen.api.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Built-in library of the ByAzen music player: public internet radio stations that can be played
 * without downloading anything and without registration.
 * <p>
 * Every entry keeps the radio-browser station id, so if a stream moves to another address the player
 * can ask the public radio-browser catalogue for the current one (see {@link MusicHttp#resolveStationUrl}).
 */
public final class RadioCatalog {

    private static final List<MusicTrack> STATIONS = new ArrayList<MusicTrack>();

    private RadioCatalog() {
    }

    private static void add(String name, String genre, String url, String slug) {
        STATIONS.add(new MusicTrack(MusicTrack.Kind.RADIO, name, genre, url, null, "Радио", 0L, slug));
    }

    static {
        // SomaFM — listener supported, free streams
        add("Groove Salad", "Эмбиент / даунтемпо", "https://ice2.somafm.com/groovesalad-128-mp3", "0e5a0f8b-9a12-4ba1-b2b8-5cc0b1a1b56b");
        add("Drone Zone", "Дроны и космос", "https://ice2.somafm.com/dronezone-128-mp3", "b4d5f0d9-4e2d-4b1f-8d1a-1c9a5f3a8f2a");
        add("Deep Space One", "Глубокий эмбиент", "https://ice2.somafm.com/deepspaceone-128-mp3", "0d1b0f2f-4d4b-4a2f-9a3f-2c6f6d1a3b4e");
        add("Space Station Soma", "Чилл / спейс-лаунж", "https://ice2.somafm.com/spacestation-128-mp3", "2f6a5b1c-9d3e-4c2a-8b7f-5e4d3c2b1a09");
        add("Lush", "Мягкий вокал и эмбиент", "https://ice2.somafm.com/lush-128-mp3", "9b6e5a3c-2d1f-4a8b-9c7d-6e5f4a3b2c1d");
        add("Beat Blender", "Лаунж / хаус", "https://ice2.somafm.com/beatblender-128-mp3", "3c2b1a09-8f7e-4d6c-9b5a-4c3d2e1f0a9b");
        add("Secret Agent", "Шпионский лаунж", "https://ice2.somafm.com/secretagent-128-mp3", "4d3c2b1a-9e8f-4a7d-8c6b-5d4e3f2a1b0c");
        add("Fluid", "Инструментальный хип-хоп", "https://ice2.somafm.com/fluid-128-mp3", "5e4d3c2b-1a0f-4b8e-9d7c-6e5f4a3b2c1d");
        add("Indie Pop Rocks", "Инди-поп", "https://ice2.somafm.com/indiepop-128-mp3", "6f5e4d3c-2b1a-4c9f-8e6d-7f6e5d4c3b2a");
        add("Underground 80s", "Синтвейв 80-х", "https://ice2.somafm.com/u80s-128-mp3", "7a6f5e4d-3c2b-4d0a-9f7e-8a7f6e5d4c3b");
        add("Metal Detector", "Метал", "https://ice2.somafm.com/metal-128-mp3", "8b7a6f5e-4d3c-4e1b-8a0f-9b8a7f6e5d4c");
        add("Boot Liquor", "Американа и кантри", "https://ice2.somafm.com/bootliquor-128-mp3", "9c8b7a6f-5e4d-4f2c-9b1a-0c9b8a7f6e5d");
        add("DEF CON Radio", "Хакерский саундтрек", "https://ice2.somafm.com/defcon-128-mp3", "0d9c8b7a-6f5e-4a3d-8c2b-1d0c9b8a7f6e");
        add("The Trip", "Прогрессив / трип-хоп", "https://ice2.somafm.com/thetrip-128-mp3", "1e0d9c8b-7a6f-4b4e-9d3c-2e1d0c9b8a7f");
        add("Digitalis", "Фолктроника", "https://ice2.somafm.com/digitalis-128-mp3", "2f1e0d9c-8b7a-4c5f-8e4d-3f2e1d0c9b8a");
        add("Suburbs of Goa", "Гоа-транс / ворлд", "https://ice2.somafm.com/suburbsofgoa-128-mp3", "3a2f1e0d-9c8b-4d6a-9f5e-4a3f2e1d0c9b");
        add("cliqhop idm", "IDM и глитч", "https://ice2.somafm.com/cliqhop-128-mp3", "4b3a2f1e-0d9c-4e7b-8a6f-5b4a3f2e1d0c");
        add("Illinois Street Lounge", "Винтажный лаунж", "https://ice2.somafm.com/illstreet-128-mp3", "5c4b3a2f-1e0d-4f8c-9b7a-6c5b4a3f2e1d");
        add("Sonic Universe", "Авангардный джаз", "https://ice2.somafm.com/sonicuniverse-128-mp3", "6d5c4b3a-2f1e-4a9d-8c8b-7d6c5b4a3f2e");
        add("Folk Forward", "Инди-фолк", "https://ice2.somafm.com/folkfwd-128-mp3", "7e6d5c4b-3a2f-4b0e-9d9c-8e7d6c5b4a3f");

        // Nightride FM — synthwave / retrowave
        add("Nightride FM", "Синтвейв", "https://stream.nightride.fm/nightride.mp3", null);
        add("Chillsynth FM", "Чилл-синтвейв", "https://stream.nightride.fm/chillsynth.mp3", null);
        add("Datawave", "Дарксинт / вейпорвейв", "https://stream.nightride.fm/datawave.mp3", null);
        add("Spacesynth", "Спейс-синтвейв", "https://stream.nightride.fm/spacesynth.mp3", null);
        add("EBSM", "Электро-боди-музыка", "https://stream.nightride.fm/ebsm.mp3", null);
    }

    public static List<MusicTrack> stations() {
        return Collections.unmodifiableList(new ArrayList<MusicTrack>(STATIONS));
    }

    /** Станция по её названию (для подборок и настроек). */
    public static MusicTrack byName(String name) {
        if (name == null) {
            return null;
        }
        for (MusicTrack track : STATIONS) {
            if (track.title().equalsIgnoreCase(name)) {
                return track;
            }
        }
        return null;
    }

    /** Курируемые подборки ByAzen (идея №25). */
    public static List<RadioCatalog.Collection> collections() {
        ArrayList<RadioCatalog.Collection> list = new ArrayList<RadioCatalog.Collection>();
        list.add(new RadioCatalog.Collection("Для PvP", "Ритм и драйв", new String[]{"Metal Detector", "DEF CON Radio", "cliqhop idm", "EBSM", "Beat Blender"}));
        list.add(new RadioCatalog.Collection("Для фарма", "Ровный фон без слов", new String[]{"Drone Zone", "Deep Space One", "Space Station Soma", "Digitalis", "Fluid"}));
        list.add(new RadioCatalog.Collection("Для релакса", "Спокойное и тёплое", new String[]{"Groove Salad", "Lush", "Illinois Street Lounge", "Folk Forward", "Sonic Universe"}));
        list.add(new RadioCatalog.Collection("Ночная дорога", "Синтвейв и неон", new String[]{"Nightride FM", "Chillsynth FM", "Datawave", "Spacesynth", "Underground 80s"}));
        list.add(new RadioCatalog.Collection("Новогодняя", "Праздничное настроение", new String[]{"Lush", "Indie Pop Rocks", "Illinois Street Lounge", "Chillsynth FM", "Groove Salad"}));
        return list;
    }

    /** Треки подборки в виде очереди воспроизведения. */
    public static List<MusicTrack> collectionTracks(RadioCatalog.Collection collection) {
        ArrayList<MusicTrack> list = new ArrayList<MusicTrack>();
        if (collection == null) {
            return list;
        }
        for (String name : collection.stations) {
            MusicTrack track = RadioCatalog.byName(name);
            if (track != null) {
                list.add(track);
            }
        }
        return list;
    }

    /** «Моя волна»: станции, похожие на текущую по жанру (идея №13). */
    public static List<MusicTrack> similar(MusicTrack source, int limit) {
        ArrayList<MusicTrack> list = new ArrayList<MusicTrack>();
        if (source == null) {
            return list;
        }
        String[] tokens = RadioCatalog.tokens(source.subtitle());
        for (MusicTrack track : STATIONS) {
            if (track.key().equals(source.key()) || list.size() >= limit) {
                continue;
            }
            int score = 0;
            for (String token : RadioCatalog.tokens(track.subtitle())) {
                for (String wanted : tokens) {
                    if (token.startsWith(wanted) || wanted.startsWith(token)) {
                        ++score;
                    }
                }
            }
            if (score > 0) {
                list.add(track);
            }
        }
        return list;
    }

    private static String[] tokens(String genre) {
        if (genre == null || genre.isBlank()) {
            return new String[0];
        }
        String cleaned = genre.toLowerCase(java.util.Locale.ROOT).replace('/', ' ').replaceAll("[^a-zа-я0-9 ]", " ");
        ArrayList<String> tokens = new ArrayList<String>();
        for (String token : cleaned.split("\\s+")) {
            if (token.length() >= 4) {
                tokens.add(token);
            }
        }
        return tokens.toArray(new String[0]);
    }

    /** Курируемая подборка станций. */
    public static final class Collection {
        public final String name;
        public final String hint;
        public final String[] stations;

        public Collection(String name, String hint, String[] stations) {
            this.name = name;
            this.hint = hint;
            this.stations = stations;
        }
    }

    public static int size() {
        return STATIONS.size();
    }
}
