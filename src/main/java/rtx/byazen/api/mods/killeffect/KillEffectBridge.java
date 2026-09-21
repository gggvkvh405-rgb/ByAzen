package rtx.byazen.api.mods.killeffect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import rtx.byazen.ByAzen;
import rtx.byazen.api.ui.UI;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Reflection bridge to the bundled "Kill Effect" mod (3D Blockbench kill effects).
 * <p>
 * Every member of this class is defensive: a missing or changed mod only disables the bridge
 * instead of breaking the client.
 */
public final class KillEffectBridge {

    public static final String MOD_ID = "killeffect";

    private static final String CLIENT_CLASS = "com.killeffect.client.KilleffectClient";
    private static final String SCREEN_CLASS = "com.killeffect.client.EffectSelectScreen";
    private static final String MANAGER_CLASS = "com.killeffect.client.EffectManager";

    private static boolean initialised;
    private static boolean available;
    private static String lastError = "неизвестная ошибка";
    private static Field selectedEffectId;
    private static Field effectsManager;
    private static Method activeEffects;
    private static Constructor<?> screenConstructor;

    private KillEffectBridge() {
    }

    private static synchronized boolean init() {
        if (initialised) {
            return available;
        }
        initialised = true;
        try {
            if (!FabricLoader.getInstance().isModLoaded(MOD_ID)) {
                lastError = "мод Kill Effect не загружен клиентом";
                return false;
            }
            Class<?> clientClass = Class.forName(CLIENT_CLASS);
            selectedEffectId = clientClass.getField("selectedEffectId");
            effectsManager = clientClass.getField("EFFECTS");
            activeEffects = Class.forName(MANAGER_CLASS).getMethod("active");
            screenConstructor = Class.forName(SCREEN_CLASS).getConstructor();
            available = true;
        }
        catch (Throwable throwable) {
            available = false;
            lastError = throwable.toString();
            ByAzen.LOGGER.warn("[ByAzen] Kill Effect bridge is unavailable: {}", throwable.toString());
        }
        return available;
    }

    public static boolean available() {
        return init();
    }

    /** Human readable reason why the bridge is unavailable. */
    public static String lastError() {
        return lastError;
    }

    /** Currently selected effect id of the mod, or {@code null} when nothing is selected. */
    public static String selectedId() {
        if (!init()) {
            return null;
        }
        try {
            return (String) selectedEffectId.get(null);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public static void setSelectedId(String id) {
        if (!init()) {
            return;
        }
        try {
            selectedEffectId.set(null, id);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Failed to set the Kill Effect effect: {}", throwable.toString());
        }
    }

    /** Live list of running effects; cleared entries stop rendering immediately. */
    public static List<?> activeEffects() {
        if (!init()) {
            return Collections.emptyList();
        }
        try {
            Object manager = effectsManager.get(null);
            Object list = activeEffects.invoke(manager);
            return list instanceof List ? (List<?>) list : Collections.emptyList();
        }
        catch (Throwable throwable) {
            return Collections.emptyList();
        }
    }

    /** Opens the effect picker of the mod. */
    public static boolean openMenu() {
        if (!init()) {
            ChatMessage.error("Kill Effect: " + lastError);
            return false;
        }
        try {
            Object screen = screenConstructor.newInstance();
            if (!(screen instanceof Screen)) {
                ChatMessage.error("Kill Effect: не удалось создать окно выбора эффекта");
                return false;
            }
            if (UI.isOpen()) {
                UI.closeInto((Screen) screen);
            }
            else {
                MinecraftClient.getInstance().setScreen((Screen) screen);
            }
            return true;
        }
        catch (Throwable throwable) {
            lastError = throwable.toString();
            ChatMessage.error("Kill Effect: " + lastError);
            ByAzen.LOGGER.warn("[ByAzen] Failed to open the Kill Effect menu: {}", throwable.toString());
            return false;
        }
    }
}
