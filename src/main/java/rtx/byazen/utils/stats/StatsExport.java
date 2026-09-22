package rtx.byazen.utils.stats;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Utils.DeathHistoryModule;
import rtx.byazen.api.modules.impl.Utils.SessionStatsModule;
import rtx.byazen.api.music.MusicLibrary;
import rtx.byazen.api.music.MusicTrack;
import rtx.byazen.utils.config.ChangeLog;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Экспорт статистики (идея №161 из IDEAS.md).
 * <p>
 * «Что было за сессию» выгружается одним нажатием: время, дистанция, добытое, убийства, смертельные
 * точки, музыка (избранное и недавнее), журнал настроек. Формат — CSV для таблиц и markdown для
 * заметок; файлы кладутся рядом с конфигом в папку `exports`.
 */
public final class StatsExport {

    /** Готовый отчёт в двух видах. */
    public static final class Bundle {

        public final String markdown;
        public final String csv;
        public final String stamp;

        Bundle(String markdown, String csv, String stamp) {
            this.markdown = markdown;
            this.csv = csv;
            this.stamp = stamp;
        }

        public int lines() {
            return StatsExport.lineCount(this.markdown);
        }
    }

    private StatsExport() {
    }

    public static String formatTime(long time) {
        return new SimpleDateFormat("dd.MM HH:mm", Locale.ROOT).format(new Date(time));
    }

    public static String stamp() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.ROOT).format(new Date());
    }

    /** Собирает отчёт из всего, что клиент знает о сессии. */
    public static StatsExport.Bundle build() {
        StringBuilder markdown = new StringBuilder();
        StringBuilder csv = new StringBuilder();
        String stamp = StatsExport.stamp();
        long dayAgo = System.currentTimeMillis() - 86400000L;
        markdown.append("# ByAzen — статистика сессии\n\n");
        markdown.append("Выгружено: ").append(StatsExport.formatTime(System.currentTimeMillis())).append("\n\n");
        csv.append("раздел;показатель;значение\n");

        SessionStatsModule session = ModuleManager.get().get(SessionStatsModule.class);
        markdown.append("## Игра\n\n");
        if (session == null) {
            markdown.append("* статистика сессии недоступна\n");
        }
        else {
            String playtime = SessionStatsModule.formatTime(session.playtimeMs());
            String distance = SessionStatsModule.formatDistance(session.distance());
            StatsExport.row(markdown, csv, "Игра", "Время в игре", playtime);
            StatsExport.row(markdown, csv, "Игра", "Пройдено", distance);
            StatsExport.row(markdown, csv, "Игра", "Добыто блоков", String.valueOf(session.blocks()));
            StatsExport.row(markdown, csv, "Игра", "Убийств", String.valueOf(session.kills()));
        }
        markdown.append("\n## Бои\n\n");
        DeathHistoryModule deaths = ModuleManager.get().get(DeathHistoryModule.class);
        List<DeathHistoryModule.Entry> entries = deaths == null ? null : deaths.entries();
        if (entries == null || entries.isEmpty()) {
            markdown.append("* смертей за сессию не записано\n");
        }
        else {
            markdown.append("* смертей: ").append(entries.size()).append("\n\n");
            markdown.append("| Когда | Мир | Координаты |\n|---|---|---|\n");
            int shown = 0;
            for (DeathHistoryModule.Entry entry : StatsExport.recent(entries, 12)) {
                markdown.append("| ").append(StatsExport.formatTime(entry.time)).append(" | ").append(entry.world)
                        .append(" | ").append(entry.x).append(' ').append(entry.y).append(' ').append(entry.z).append(" |\n");
                csv.append("Бои;смерть ").append(++shown).append(';')
                        .append(entry.x).append(' ').append(entry.y).append(' ').append(entry.z)
                        .append(" (").append(entry.world).append(", ").append(StatsExport.formatTime(entry.time)).append(")\n");
            }
        }
        markdown.append("\n## Музыка\n\n");
        MusicLibrary library = MusicLibrary.get();
        if (library == null) {
            markdown.append("* библиотека музыки недоступна\n");
        }
        else {
            StatsExport.row(markdown, csv, "Музыка", "В избранном", String.valueOf(library.favorites().size()));
            StatsExport.row(markdown, csv, "Музыка", "Своих ссылок", String.valueOf(library.custom().size()));
            StatsExport.row(markdown, csv, "Музыка", "Недавних", String.valueOf(library.recent().size()));
            int shown = 0;
            for (MusicTrack track : library.recent()) {
                if (shown++ >= 10) {
                    break;
                }
                markdown.append("* ").append(track.title()).append(shown == 1 ? " (последний)" : "").append('\n');
                csv.append("Музыка;недавнее ").append(shown).append(';').append(StatsExport.clean(track.title())).append('\n');
            }
            for (MusicTrack track : library.favorites()) {
                csv.append("Музыка;избранное;").append(StatsExport.clean(track.title())).append('\n');
            }
        }
        markdown.append("\n## Настройки\n\n");
        StatsExport.row(markdown, csv, "Настройки", "Всего записей в журнале", String.valueOf(ChangeLog.count()));
        StatsExport.row(markdown, csv, "Настройки", "Изменений за сутки", String.valueOf(ChangeLog.countSince(dayAgo)));
        StatsExport.row(markdown, csv, "Настройки", "Предупреждений в логе", String.valueOf(ClientLog.count(ClientLog.Level.WARN)));
        markdown.append("\nЖурнал изменений целиком — в окне «Диагностика», вкладка «Журнал».\n");
        return new StatsExport.Bundle(markdown.toString(), csv.toString(), stamp);
    }

    private static List<DeathHistoryModule.Entry> recent(List<DeathHistoryModule.Entry> entries, int max) {
        if (entries.size() <= max) {
            return entries;
        }
        return entries.subList(entries.size() - max, entries.size());
    }

    private static void row(StringBuilder markdown, StringBuilder csv, String section, String key, String value) {
        markdown.append("* ").append(key).append(": ").append(value).append('\n');
        csv.append(section).append(';').append(key).append(';').append(value).append('\n');
    }

    private static String clean(String text) {
        return text == null ? "" : text.replace(';', ',').replace('\n', ' ');
    }

    private static int lineCount(String text) {
        int lines = 0;
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == '\n') {
                ++lines;
            }
        }
        return lines + 1;
    }

    /** Пишет оба файла рядом с конфигом. Возвращает пути или пустую строку при ошибке. */
    public static String write(StatsExport.Bundle bundle) {
        try {
            Path directory = RepositoryStorage.configRoot().resolve("exports");
            Files.createDirectories(directory);
            Files.write(directory.resolve("stats-" + bundle.stamp + ".md"), bundle.markdown.getBytes(StandardCharsets.UTF_8));
            Files.write(directory.resolve("stats-" + bundle.stamp + ".csv"), bundle.csv.getBytes(StandardCharsets.UTF_8));
            return directory.toString();
        }
        catch (Throwable throwable) {
            return "";
        }
    }
}
