package rtx.byazen.api.chat.commands.impl;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.music.Equalizer;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.api.music.MusicLibrary;
import rtx.byazen.api.music.MusicPlaylists;
import rtx.byazen.api.music.MusicTrack;

/**
 * Плеер из чата: {@code .music} (алиасы {@code .музыка}, {@code .mp3}).
 * <p>
 * Здесь то, что удобнее набрать руками, чем искать в окне: запуск станции по имени, управление
 * очередью, плейлисты (идея №3), кроссфейд (№4) и десятиполосный эквалайзер (№5). OGG-потоки
 * (№21) команда тоже понимает — движок сам выбирает декодер по содержимому ссылки.
 */
public final class MusicCommand
extends Command {

    public MusicCommand() {
        super("music", "Плеер музыки: очередь, плейлисты, эквалайзер",
                "музыка", "mp3", "audio");
    }

    @Override
    public void execute(String label, String[] args) {
        MusicEngine engine = MusicEngine.get();
        String sub = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "";
        if (sub.equals("play") || sub.equals("играть") || sub.equals("включить")) {
            this.play(join(args, 1));
            return;
        }
        if (sub.equals("next") || sub.equals("дальше") || sub.equals("следующий")) {
            engine.next();
            this.logDirect("Следующий трек", Formatting.GREEN);
            return;
        }
        if (sub.equals("prev") || sub.equals("previous") || sub.equals("назад")) {
            engine.previous();
            this.logDirect("Предыдущий трек", Formatting.GREEN);
            return;
        }
        if (sub.equals("stop") || sub.equals("стоп")) {
            engine.stop();
            this.logDirect("Плеер остановлен", Formatting.YELLOW);
            return;
        }
        if (sub.equals("pause") || sub.equals("пауза")) {
            engine.togglePause();
            this.logDirect(engine.isPaused() ? "Пауза" : "Играем дальше", Formatting.GREEN);
            return;
        }
        if (sub.equals("pl") || sub.equals("плейлист") || sub.equals("плейлисты") || sub.equals("playlist")) {
            this.playlist(args);
            return;
        }
        if (sub.equals("eq") || sub.equals("эквалайзер") || sub.equals("equalizer")) {
            this.equalizer(args);
            return;
        }
        if (sub.equals("crossfade") || sub.equals("кроссфейд") || sub.equals("xfade")) {
            this.crossfade(args);
            return;
        }
        if (sub.equals("volume") || sub.equals("громкость")) {
            this.volume(args);
            return;
        }
        if (sub.equals("list") || sub.equals("список")) {
            this.queue();
            return;
        }
        this.help();
    }

    /** Создаёт/выбирает плейлист, добавляет и убирает треки, запускает набор целиком. */
    private void playlist(String[] args) {
        MusicPlaylists playlists = MusicPlaylists.get();
        String action = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "";
        if (action.isEmpty() || action.equals("list") || action.equals("список")) {
            this.logDirect("Плейлисты ByAzen", Formatting.AQUA);
            this.logDirect(playlists.summary(), Formatting.GRAY);
            MusicPlaylists.Playlist selected = playlists.selected();
            if (selected != null) {
                this.logDirect("Выбран: «" + selected.name() + "» · треков: " + selected.size(), Formatting.WHITE);
            }
            this.logDirect("Команды: .music pl new <название> · .music pl add [название] · .music pl play <название> · .music pl remove <название>",
                    Formatting.DARK_GRAY);
            return;
        }
        if (action.equals("new") || action.equals("создать") || action.equals("новый")) {
            MusicPlaylists.Playlist playlist = playlists.create(join(args, 2));
            this.logDirect(playlist == null
                    ? "Предел плейлистов: " + playlists.count() + " — удалите ненужные"
                    : "Плейлист «" + playlist.name() + "» создан", playlist == null ? Formatting.RED : Formatting.GREEN);
            return;
        }
        if (action.equals("add") || action.equals("добавить")) {
            this.addToPlaylist(args);
            return;
        }
        if (action.equals("play") || action.equals("играть") || action.equals("включить")) {
            int index = playlists.selectByName(join(args, 2));
            if (index < 0) {
                this.logDirect("Плейлист не найден: " + join(args, 2), Formatting.RED);
                return;
            }
            MusicTrack track = playlists.play(index);
            this.logDirect(track == null
                    ? "Плейлист «" + playlists.get(index).name() + "» пуст — добавьте треки: .music pl add " + playlists.get(index).name()
                    : "Играем «" + playlists.get(index).name() + "» с трека «" + track.title() + "»",
                    track == null ? Formatting.YELLOW : Formatting.GREEN);
            return;
        }
        if (action.equals("remove") || action.equals("delete") || action.equals("удалить")) {
            int index = playlists.selectByName(join(args, 2));
            if (index < 0 || !playlists.remove(index)) {
                this.logDirect("Плейлист не найден: " + join(args, 2), Formatting.RED);
                return;
            }
            this.logDirect("Плейлист удалён", Formatting.YELLOW);
            return;
        }
        if (action.equals("show") || action.equals("показать")) {
            int index = playlists.selectByName(join(args, 2));
            MusicPlaylists.Playlist playlist = index < 0 ? playlists.selected() : playlists.get(index);
            if (playlist == null) {
                this.logDirect("Плейлистов пока нет: .music pl new Мой список", Formatting.YELLOW);
                return;
            }
            this.logDirect("«" + playlist.name() + "» · треков: " + playlist.size(), Formatting.AQUA);
            int limit = Math.min(12, playlist.size());
            for (int i = 0; i < limit; ++i) {
                MusicTrack track = playlist.tracks().get(i);
                this.logDirect(Formatting.GRAY + "" + (i + 1) + ". " + Formatting.WHITE + track.title()
                        + Formatting.DARK_GRAY + " · " + track.badge());
            }
            if (playlist.size() > limit) {
                this.logDirect("… и ещё " + (playlist.size() - limit), Formatting.DARK_GRAY);
            }
            return;
        }
        this.logDirect("Неизвестное действие: " + action, Formatting.RED);
        this.logDirect("Доступно: new · add · play · show · remove · list", Formatting.DARK_GRAY);
    }

    /** add — текущий трек, станция по имени или своя ссылка; без названия берётся выбранный плейлист. */
    private void addToPlaylist(String[] args) {
        MusicPlaylists playlists = MusicPlaylists.get();
        String rest = join(args, 2);
        String playlistName = "";
        String what = rest;
        int separator = rest.indexOf('|');
        if (separator > 0) {
            playlistName = rest.substring(0, separator).trim();
            what = rest.substring(separator + 1).trim();
        }
        MusicPlaylists.Playlist playlist = playlistName.isBlank()
                ? playlists.selected()
                : playlists.get(playlists.selectByName(playlistName));
        if (playlist == null) {
            this.logDirect("Плейлиста нет — создайте: .music pl new Мой список", Formatting.RED);
            return;
        }
        MusicTrack track;
        if (what.isBlank()) {
            track = MusicEngine.get().current();
            if (track == null) {
                track = MusicLibrary.get().lastPlayed();
            }
        }
        else {
            track = MusicLibrary.get().findByText(what);
        }
        if (track == null) {
            this.logDirect("Трек не найден: " + (what.isBlank() ? "сейчас ничего не играет" : what), Formatting.RED);
            return;
        }
        this.logDirect(playlists.add(playlists.indexOf(playlist.name()), track)
                ? "«" + track.title() + "» → плейлист «" + playlist.name() + "» (" + playlist.size() + ")"
                : "Уже есть в плейлисте «" + playlist.name() + "»",
                Formatting.GREEN);
    }

    /** Эквалайзер: включение, пресеты и ручные полосы. */
    private void equalizer(String[] args) {
        Equalizer equalizer = Equalizer.get();
        String action = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "";
        if (action.isEmpty() || action.equals("list") || action.equals("список")) {
            this.equalizerStatus();
            return;
        }
        if (action.equals("on") || action.equals("вкл")) {
            equalizer.setEnabled(true);
            this.logDirect("Эквалайзер включён: «" + equalizer.preset() + "»", Formatting.GREEN);
            return;
        }
        if (action.equals("off") || action.equals("выкл")) {
            equalizer.setEnabled(false);
            this.logDirect("Эквалайзер выключен", Formatting.YELLOW);
            return;
        }
        if (action.equals("preset") || action.equals("пресет")) {
            String name = join(args, 2);
            if (name.isBlank()) {
                this.logDirect("Пресеты: " + String.join(" · ", Equalizer.presets()), Formatting.GRAY);
                return;
            }
            int index = Equalizer.presetIndex(name);
            if (index < 0) {
                this.logDirect("Пресета нет: " + name + " · доступно: " + String.join(", ", Equalizer.presets()), Formatting.RED);
                return;
            }
            equalizer.setEnabled(true);
            equalizer.applyPreset(Equalizer.presets()[index]);
            this.logDirect("Пресет «" + Equalizer.presets()[index] + "» применён", Formatting.GREEN);
            return;
        }
        if (action.equals("set") || action.equals("полоса") || action.equals("gain")) {
            this.setBand(args);
            return;
        }
        if (action.equals("reset") || action.equals("сброс")) {
            equalizer.applyPreset("Плоский");
            this.logDirect("Полосы сброшены в ноль", Formatting.GREEN);
            return;
        }
        this.logDirect("Команды: .music eq on|off · eq preset <имя> · eq set <полоса> <дБ> · eq reset", Formatting.DARK_GRAY);
    }

    private void setBand(String[] args) {
        if (args.length < 4) {
            this.logDirect("Формат: .music eq set <полоса 1-" + Equalizer.BANDS + "> <дБ от -12 до +12>", Formatting.YELLOW);
            return;
        }
        int band;
        float value;
        try {
            band = Integer.parseInt(args[2]);
            value = Float.parseFloat(args[3].replace(',', '.'));
        }
        catch (NumberFormatException exception) {
            this.logDirect("Нужны числа: .music eq set 1 6", Formatting.RED);
            return;
        }
        if (band < 1 || band > Equalizer.BANDS) {
            this.logDirect("Полоса от 1 до " + Equalizer.BANDS, Formatting.RED);
            return;
        }
        Equalizer equalizer = Equalizer.get();
        equalizer.setEnabled(true);
        equalizer.setGain(band - 1, value);
        this.logDirect(Equalizer.label(band - 1) + ": " + String.format(Locale.ROOT, "%+.1f", value) + " дБ", Formatting.GREEN);
    }

    private void equalizerStatus() {
        Equalizer equalizer = Equalizer.get();
        this.logDirect("Эквалайзер " + (equalizer.enabled() ? "включён" : "выключен") + " · пресет «" + equalizer.preset() + "»",
                equalizer.enabled() ? Formatting.AQUA : Formatting.GRAY);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Equalizer.BANDS; ++i) {
            if (i > 0) {
                builder.append(" · ");
            }
            builder.append(Equalizer.label(i)).append(' ').append(String.format(Locale.ROOT, "%+.0f", equalizer.gain(i)));
        }
        this.logDirect(builder.toString(), Formatting.GRAY);
        this.logDirect("Пресеты: " + String.join(" · ", Equalizer.presets()), Formatting.DARK_GRAY);
    }

    /** Кроссфейд между треками (идея №4). */
    private void crossfade(String[] args) {
        MusicEngine engine = MusicEngine.get();
        if (args.length > 1) {
            try {
                int value = Integer.parseInt(args[1]);
                engine.setCrossfadeMs(value);
            }
            catch (NumberFormatException exception) {
                this.logDirect("Формат: .music crossfade <мс 0-8000>", Formatting.YELLOW);
                return;
            }
        }
        float seconds = engine.crossfadeMs() / 1000.0f;
        this.logDirect("Кроссфейд: " + (engine.crossfadeMs() == 0
                ? "выключен"
                : String.format(Locale.ROOT, "%.1f с", seconds)), Formatting.GREEN);
    }

    private void volume(String[] args) {
        MusicEngine engine = MusicEngine.get();
        if (args.length > 1) {
            try {
                float value = Float.parseFloat(args[1].replace(',', '.'));
                engine.setVolume(value > 1.0f ? value / 100.0f : value);
            }
            catch (NumberFormatException exception) {
                this.logDirect("Формат: .music volume <0-100>", Formatting.YELLOW);
                return;
            }
        }
        this.logDirect("Громкость: " + Math.round(engine.volume() * 100.0f) + "%", Formatting.GREEN);
    }

    private void play(String query) {
        if (query.isBlank()) {
            MusicTrack current = MusicEngine.get().current();
            this.logDirect(current == null ? "Сейчас ничего не играет" : "Играет: " + current.title(), Formatting.GRAY);
            return;
        }
        MusicTrack track = MusicLibrary.get().findByText(query);
        if (track == null) {
            this.logDirect("Ничего не нашлось по запросу «" + query + "»", Formatting.RED);
            return;
        }
        MusicEngine.get().play(track);
        this.logDirect("Играем: " + track.title(), Formatting.GREEN);
    }

    private void queue() {
        MusicEngine engine = MusicEngine.get();
        MusicTrack current = engine.current();
        this.logDirect(current == null
                ? "Очередь пуста — включите станцию: .music play <название>"
                : "Играет: " + current.title() + (engine.isPaused() ? " (пауза)" : ""),
                current == null ? Formatting.YELLOW : Formatting.AQUA);
        List<MusicTrack> queue = engine.queue();
        int limit = Math.min(10, queue.size());
        for (int i = 0; i < limit; ++i) {
            MusicTrack track = queue.get(i);
            this.logDirect(Formatting.GRAY + "" + (i + 1) + ". " + Formatting.WHITE + track.title()
                    + Formatting.DARK_GRAY + (i == engine.index() ? "  ← сейчас" : ""));
        }
    }

    private void help() {
        this.logDirect("Плеер ByAzen", Formatting.AQUA);
        this.logDirect(MusicLibrary.get().summary(), Formatting.GRAY);
        this.logDirect("Команды: .music play <название> · next · prev · pause · stop · list", Formatting.DARK_GRAY);
        this.logDirect(".music pl new/add/play/show/remove — свои плейлисты (плеер: вкладка «Плейлисты»)", Formatting.DARK_GRAY);
        this.logDirect(".music eq on/off/preset/set — баланс на " + Equalizer.BANDS + " полос", Formatting.DARK_GRAY);
        this.logDirect(".music crossfade <мс> · .music volume <0-100>", Formatting.DARK_GRAY);
    }

    private static String join(String[] args, int from) {
        StringBuilder builder = new StringBuilder();
        for (int i = from; i < args.length; ++i) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return builder.toString().trim();
    }

    /** Автодополнение подкоманд: база возвращает {@code Stream}, как и у остальных команд клиента. */
    @Override
    public Stream<String> tabComplete(String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("play", "next", "prev", "pause", "stop", "list", "pl", "eq", "crossfade", "volume");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("pl")) {
            return Stream.of("new", "add", "play", "show", "remove", "list");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("eq")) {
            return Stream.of("on", "off", "preset", "set", "reset");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("play")) {
            return MusicLibrary.get().all().stream().map(MusicTrack::title);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("crossfade") || args[0].equalsIgnoreCase("volume"))) {
            return Stream.of("0", "1000", "2000", "3000", "4000");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("pl") && (args[1].equalsIgnoreCase("play")
                || args[1].equalsIgnoreCase("show") || args[1].equalsIgnoreCase("remove")
                || args[1].equalsIgnoreCase("add"))) {
            return MusicPlaylists.get().all().stream().map(MusicPlaylists.Playlist::name);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("eq") && args[1].equalsIgnoreCase("preset")) {
            return Stream.of(Equalizer.presets());
        }
        return Stream.empty();
    }

    @Override
    public List<String> getLongDesc() {
        return List.of(
                "Управление встроенным плеером ByAzen прямо из чата.",
                "play <название|ссылка> — включить станцию или трек по имени;",
                "pl new/add/play/show/remove — свои плейлисты (идея №3);",
                "eq on/off/preset/set — эквалайзер на " + Equalizer.BANDS + " полос (идея №5);",
                "crossfade <мс> — плавный переход между треками (идея №4);",
                "next/prev/pause/stop/list — очередь и воспроизведение.");
    }
}
