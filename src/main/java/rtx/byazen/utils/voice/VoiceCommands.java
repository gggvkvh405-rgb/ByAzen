package rtx.byazen.utils.voice;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.sounds.SoundManager;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.web.WebBridge;

/**
 * Голосовые команды (идея №181 из IDEAS.md).
 * <p>
 * Клиент слушает микрофон лишь по нажатию (никакой записи «в фоне» и никакой отправки на серверы):
 * у каждой команды вы один раз произносите слово, клиент запоминает огибающую звука и дальше узнаёт
 * её по форме. Это сопоставление по образцу, а не распознавание речи словами — зато работает офлайн,
 * без внешних сервисов и не требует прав, кроме доступа к микрофону.
 * <p>
 * Для настоящего голосового чата клиент совместим с Simple Voice Chat / Plasmo Voice: если мод
 * установлен, модуль подсказывает это в чате и не мешает его кнопке «говорить».
 */
public final class VoiceCommands {

    private static final int RATE = 16000;
    private static final int WINDOWS = 12;
    private static final Map<String, float[]> TEMPLATES = new LinkedHashMap<String, float[]>();
    private static boolean loaded;

    private VoiceCommands() {
    }

    /** Встроенные команды: id, подпись, что делает. */
    public record Command(String id, String label, String hint) {
    }

    private static final List<Command> COMMANDS = List.of(
            new Command("coords", "Координаты", "скажите слово — и клиент покажет координаты в чате"),
            new Command("status", "Статус", "здоровье, голод и координаты одной строкой"),
            new Command("light", "Свет", "включает и выключает ночное зрение"),
            new Command("map", "Карта", "включает и выключает миникарту"),
            new Command("music", "Музыка", "пауза или продолжение плеера"),
            new Command("next", "Следующий трек", "переключает музыку вперёд"),
            new Command("quieter", "Тише", "убавляет громкость плеера"),
            new Command("louder", "Громче", "прибавляет громкость плеера"),
            new Command("screenshot", "Скриншот", "подсказывает нажать F2 (за клиента клавиши не жмём)"),
            new Command("home", "Дом", "пишет координаты как «дом» в чат для копирования")
    );

    public static List<Command> commands() {
        return COMMANDS;
    }

    public static Command command(String id) {
        for (Command command : COMMANDS) {
            if (command.id().equalsIgnoreCase(id)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Настоящий голосовой чат клиент не подменяет: если стоит Simple Voice Chat или Plasmo Voice,
     * модуль это видит и не мешает их кнопке «говорить», а свои команды работают рядом.
     */
    public static String chatModStatus() {
        try {
            net.fabricmc.loader.api.FabricLoader loader = net.fabricmc.loader.api.FabricLoader.getInstance();
            if (loader.isModLoaded("voicechat")) {
                return "найден Simple Voice Chat: живой голос — через него, наш модуль добавляет только голосовые команды";
            }
            if (loader.isModLoaded("plasmovoice")) {
                return "найден Plasmo Voice: живой голос — через него, наш модуль добавляет только команды";
            }
        }
        catch (Throwable ignored) {
        }
        return "мода голосового чата рядом нет: для живого голоса поставьте Simple Voice Chat, команды работают и без него";
    }

    /** Что за микрофоны видит система. */
    public static List<String> devices() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            for (Mixer.Info info : AudioSystem.getMixerInfo()) {
                Mixer mixer = AudioSystem.getMixer(info);
                if (mixer.getTargetLineInfo().length > 0) {
                    list.add(info.getName());
                }
            }
        }
        catch (Throwable throwable) {
            list.add("микрофоны недоступны: " + throwable.getClass().getSimpleName());
        }
        if (list.isEmpty()) {
            list.add("микрофон не найден");
        }
        return list;
    }

    public static String microphoneSummary() {
        List<String> devices = VoiceCommands.devices();
        return devices.get(0) + (devices.size() > 1 ? " (всего " + devices.size() + ")" : "");
    }

    /**
     * Слушает микрофон заданное время и возвращает «отпечаток» звука: 12 окон по (громкость,
     * число переходов через ноль). {@code null} — если было слишком тихо.
     */
    public static float[] listen(int millis) {
        AudioFormat format = new AudioFormat((float)RATE, 16, 1, true, false);
        TargetDataLine line = null;
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                return null;
            }
            line = (TargetDataLine)AudioSystem.getLine(info);
            line.open(format, RATE);
            line.start();
            int total = RATE * millis / 1000;
            byte[] buffer = new byte[total * 2];
            int read = 0;
            long deadline = System.currentTimeMillis() + (long)millis + 500L;
            while (read < buffer.length && System.currentTimeMillis() < deadline) {
                int chunk = line.read(buffer, read, buffer.length - read);
                if (chunk <= 0) {
                    break;
                }
                read += chunk;
            }
            line.stop();
            if (read < buffer.length / 4) {
                return null;
            }
            return VoiceCommands.features(buffer, read);
        }
        catch (Throwable throwable) {
            ClientLog.warn("микрофон: " + throwable.getClass().getSimpleName() + " — " + throwable.getMessage());
            return null;
        }
        finally {
            if (line != null) {
                try {
                    line.close();
                }
                catch (Throwable ignored) {
                }
            }
        }
    }

    private static float[] features(byte[] buffer, int length) {
        int samples = length / 2;
        int perWindow = Math.max(1, samples / WINDOWS);
        float[] result = new float[WINDOWS * 2];
        float peak = 0.0f;
        for (int window = 0; window < WINDOWS; ++window) {
            int from = window * perWindow;
            int to = Math.min(samples, from + perWindow);
            double sum = 0.0;
            int crossings = 0;
            short previous = 0;
            for (int i = from; i < to; ++i) {
                short value = (short)(buffer[i * 2] & 255 | buffer[i * 2 + 1] << 8);
                sum += (double)value * (double)value;
                if (previous != 0 && (value >= 0) != (previous >= 0)) {
                    ++crossings;
                }
                previous = value;
            }
            int count = Math.max(1, to - from);
            float rms = (float)Math.sqrt(sum / (double)count) / 32768.0f;
            result[window * 2] = rms;
            result[window * 2 + 1] = (float)crossings / (float)count;
            peak = Math.max(peak, rms);
        }
        if (peak < 0.01f) {
            return null;
        }
        return result;
    }

    /** Запоминает образец для команды. Возвращает понятный итог. */
    public static String train(String id, int millis) {
        Command command = VoiceCommands.command(id);
        if (command == null) {
            return "Такой команды нет";
        }
        float[] features = VoiceCommands.listen(millis);
        if (features == null) {
            return "Слишком тихо — говорите ближе к микрофону и чуть громче";
        }
        VoiceCommands.load();
        TEMPLATES.put(command.id(), features);
        VoiceCommands.save();
        return "Запомнил команду «" + command.label() + "» — теперь скажите её в бою";
    }

    public static boolean hasTemplate(String id) {
        VoiceCommands.load();
        return TEMPLATES.containsKey(id);
    }

    public static int trainedCount() {
        VoiceCommands.load();
        return TEMPLATES.size();
    }

    public static void forget(String id) {
        VoiceCommands.load();
        TEMPLATES.remove(id);
        VoiceCommands.save();
    }

    public static void forgetAll() {
        TEMPLATES.clear();
        VoiceCommands.save();
    }

    /** Слушает один раз и, если узнал команду, выполняет её. */
    public static String recognizeAndRun(float threshold) {
        VoiceCommands.load();
        if (TEMPLATES.isEmpty()) {
            return "Сначала обучите хотя бы одну команду";
        }
        float[] features = VoiceCommands.listen(1200);
        if (features == null) {
            return "Ничего не услышал — попробуйте ещё раз";
        }
        String best = null;
        double bestScore = -1.0;
        double second = -1.0;
        for (Map.Entry<String, float[]> entry : TEMPLATES.entrySet()) {
            double score = VoiceCommands.similarity(features, entry.getValue());
            if (score > bestScore) {
                second = bestScore;
                bestScore = score;
                best = entry.getKey();
            }
            else if (score > second) {
                second = score;
            }
        }
        if (best == null || bestScore < threshold) {
            return String.format(Locale.ROOT, "Не узнал (лучшее совпадение %.0f %%, нужно %.0f %%)",
                    bestScore * 100.0, threshold * 100.0);
        }
        if (second > 0.0 && second / Math.max(0.0001, bestScore) > 0.97) {
            return "Слова слишком похожи — повторите обучение, произнося их чётче";
        }
        VoiceCommands.run(best);
        Command command = VoiceCommands.command(best);
        return String.format(Locale.ROOT, "Услышал «%s» (совпадение %.0f %%)",
                command == null ? best : command.label(), bestScore * 100.0);
    }

    /** Косинусная близость отпечатков после выравнивания по среднему. */
    private static double similarity(float[] left, float[] right) {
        if (left.length != right.length) {
            return 0.0;
        }
        double meanLeft = 0.0;
        double meanRight = 0.0;
        for (int i = 0; i < left.length; ++i) {
            meanLeft += left[i];
            meanRight += right[i];
        }
        meanLeft /= (double)left.length;
        meanRight /= (double)right.length;
        double dot = 0.0;
        double normLeft = 0.0;
        double normRight = 0.0;
        for (int i = 0; i < left.length; ++i) {
            double a = left[i] - meanLeft;
            double b = right[i] - meanRight;
            dot += a * b;
            normLeft += a * a;
            normRight += b * b;
        }
        if (normLeft <= 0.0 || normRight <= 0.0) {
            return 0.0;
        }
        return dot / Math.sqrt(normLeft * normRight);
    }

    /** Выполнение команды: только клиентские действия, ничего не отправляем на сервер. */
    private static void run(String id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.7f, 1.2f);
        switch (id.toLowerCase(Locale.ROOT)) {
            case "coords": {
                String coords = Math.round(client.player.getX()) + " " + Math.round(client.player.getY()) + " "
                        + Math.round(client.player.getZ());
                ChatMessage.send("§bГолосом: координаты §f" + coords);
                break;
            }
            case "home": {
                String coords = Math.round(client.player.getX()) + " " + Math.round(client.player.getY()) + " "
                        + Math.round(client.player.getZ());
                ChatMessage.send("§bДом: §f" + coords + " §7(скопируйте отсюда)");
                break;
            }
            case "status": {
                ChatMessage.send(String.format(Locale.ROOT, "§bСтатус: §fHP %.0f/%.0f, голод %s, координаты %d %d %d",
                        client.player.getHealth(), client.player.getMaxHealth(), VoiceCommands.hunger(client),
                        Math.round(client.player.getX()), Math.round(client.player.getY()), Math.round(client.player.getZ())));
                break;
            }
            case "light": {
                VoiceCommands.toggle("Fullbright", "Night Vision", "bright", "vision", "ярк");
                break;
            }
            case "map": {
                VoiceCommands.toggle("Minimap", "миникарта", "map");
                break;
            }
            case "music": {
                MusicEngine.get().togglePause();
                ChatMessage.send("§bГолосом: плеер " + (MusicEngine.get().isPaused() ? "на паузе" : "играет"));
                break;
            }
            case "next": {
                MusicEngine.get().next();
                break;
            }
            case "quieter": {
                MusicEngine.get().setVolume(Math.max(0.0f, MusicEngine.get().volume() - 0.1f));
                break;
            }
            case "louder": {
                MusicEngine.get().setVolume(Math.min(1.0f, MusicEngine.get().volume() + 0.1f));
                break;
            }
            case "screenshot": {
                NotificationsModule.notify("Нажмите F2 — скриншот сделает сама игра", 3000L);
                break;
            }
            default: {
                break;
            }
        }
        WebBridge.pushEvent("voice", "Голосовая команда: " + id);
    }

    /** Ищет модуль по нескольким названиям, а если точного нет — по части названия. */
    private static Module resolve(String... candidates) {
        ModuleManager manager = ModuleManager.get();
        for (String candidate : candidates) {
            Module module = manager.findByName(candidate);
            if (module != null) {
                return module;
            }
        }
        for (String candidate : candidates) {
            String needle = candidate.toLowerCase(Locale.ROOT);
            for (Module module : manager.getAll()) {
                if (module.getName().toLowerCase(Locale.ROOT).contains(needle)
                        || module.getDisplayName().toLowerCase(Locale.ROOT).contains(needle)) {
                    return module;
                }
            }
        }
        return null;
    }

    private static void toggle(String... candidates) {
        Module module = VoiceCommands.resolve(candidates);
        if (module == null) {
            ChatMessage.send("§eМодуль «" + candidates[0] + "» не найден");
            return;
        }
        module.toggle();
        ChatMessage.send("§bГолосом: " + module.getDisplayName() + " " + (module.isEnabled() ? "включён" : "выключен"));
    }

    private static String hunger(MinecraftClient client) {
        try {
            Object manager = client.player.getClass().getMethod("getHungerManager").invoke(client.player);
            Object level = manager.getClass().getMethod("getFoodLevel").invoke(manager);
            return String.valueOf(((Number)level).intValue());
        }
        catch (Throwable throwable) {
            return "?";
        }
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("voice_templates");
            if (!root.has("templates")) {
                return;
            }
            for (String id : root.getAsJsonObject("templates").keySet()) {
                JsonArray array = root.getAsJsonObject("templates").getAsJsonArray(id);
                float[] values = new float[array.size()];
                for (int i = 0; i < array.size(); ++i) {
                    values[i] = array.get(i).getAsFloat();
                }
                TEMPLATES.put(id, values);
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("голосовые команды: " + throwable.getClass().getSimpleName());
        }
    }

    private static void save() {
        try {
            JsonObject templates = new JsonObject();
            for (Map.Entry<String, float[]> entry : TEMPLATES.entrySet()) {
                JsonArray array = new JsonArray();
                for (float value : entry.getValue()) {
                    array.add(value);
                }
                templates.add(entry.getKey(), array);
            }
            JsonObject root = new JsonObject();
            root.add("templates", templates);
            RepositoryStorage.write("voice_templates", root);
        }
        catch (Throwable ignored) {
        }
    }

    /** Итог для чата: что обучено. */
    public static String summary() {
        VoiceCommands.load();
        StringBuilder builder = new StringBuilder();
        builder.append("обучено команд: ").append(TEMPLATES.size()).append(" из ").append(COMMANDS.size());
        if (!TEMPLATES.isEmpty()) {
            builder.append(" (").append(String.join(", ", TEMPLATES.keySet())).append(")");
        }
        return builder.toString();
    }
}
