package rtx.byazen.api.mods.pulsecosmetics;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import rtx.byazen.ByAzen;
import rtx.byazen.api.ui.UI;

/**
 * Reflection bridge to the bundled "Pulse Cosmetics" mod.
 * <p>
 * The mod ships inside the ByAzen jar as a nested (jar-in-jar) mod, so every call here is
 * optional by design: if the mod is missing or its classes changed, the bridge simply reports
 * itself as unavailable and the client keeps working.
 */
public final class PulseCosmeticsBridge {

    public static final String MOD_ID = "pulsecosmetics";

    private static final String CATEGORY_CLASS = "dev.pulse.cosmetics.CosmeticCategory";
    private static final String DISPLAY_CLASS = "dev.pulse.cosmetics.CosmeticDisplaySettings";
    private static final String SCREEN_CLASS = "dev.pulse.cosmetics.CosmeticsScreen";
    private static final String GRAFFITI_CLASS = "dev.pulse.cosmetics.GraffitiManager";

    private static boolean initialised;
    private static boolean available;
    private static Class<?> categoryClass;
    private static Constructor<?> screenConstructor;
    private static Method showOther;
    private static Method toggleOther;
    private static Method showOwnPetFirstPerson;
    private static Method toggleOwnPetFirstPerson;
    private static Method beginRemoval;

    private PulseCosmeticsBridge() {
    }

    private static synchronized boolean init() {
        if (initialised) {
            return available;
        }
        initialised = true;
        try {
            if (!FabricLoader.getInstance().isModLoaded(MOD_ID)) {
                return false;
            }
            categoryClass = Class.forName(CATEGORY_CLASS);
            Class<?> displayClass = Class.forName(DISPLAY_CLASS);
            showOther = displayClass.getMethod("showOther", categoryClass);
            toggleOther = displayClass.getMethod("toggleOther", categoryClass);
            showOwnPetFirstPerson = displayClass.getMethod("showOwnPetFirstPerson");
            toggleOwnPetFirstPerson = displayClass.getMethod("toggleOwnPetFirstPerson");
            screenConstructor = Class.forName(SCREEN_CLASS).getConstructor(categoryClass, int.class);
            beginRemoval = Class.forName(GRAFFITI_CLASS).getMethod("beginRemoval");
            available = true;
        }
        catch (Throwable throwable) {
            available = false;
            ByAzen.LOGGER.warn("[ByAzen] Pulse Cosmetics bridge is unavailable: {}", throwable.toString());
        }
        return available;
    }

    public static boolean available() {
        return init();
    }

    private static Object category(String name) {
        if (!init() || name == null) {
            return null;
        }
        try {
            Field field = categoryClass.getField(name);
            return field.get(null);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    /** Category keys used by the mod: WINGS, CAPE, HAT, BODYWEAR, PET, GRAFFITI. */
    public static boolean showOther(String categoryName) {
        Object category = category(categoryName);
        if (category == null) {
            return true;
        }
        try {
            return (Boolean) showOther.invoke(null, category);
        }
        catch (Throwable throwable) {
            return true;
        }
    }

    public static void setShowOther(String categoryName, boolean value) {
        Object category = category(categoryName);
        if (category == null) {
            return;
        }
        try {
            if ((Boolean) showOther.invoke(null, category) != value) {
                toggleOther.invoke(null, category);
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Failed to change Pulse Cosmetics visibility: {}", throwable.toString());
        }
    }

    public static boolean ownPetFirstPerson() {
        if (!init()) {
            return false;
        }
        try {
            return (Boolean) showOwnPetFirstPerson.invoke(null);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public static void setOwnPetFirstPerson(boolean value) {
        if (!init()) {
            return;
        }
        try {
            if ((Boolean) showOwnPetFirstPerson.invoke(null) != value) {
                toggleOwnPetFirstPerson.invoke(null);
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Failed to change Pulse Cosmetics pet mode: {}", throwable.toString());
        }
    }

    private static void open(Screen screen) {
        if (UI.isOpen()) {
            UI.closeInto(screen);
        }
        else {
            MinecraftClient.getInstance().setScreen(screen);
        }
    }

    /** Opens the cosmetic picker of the mod on the requested category tab. */
    public static boolean openMenu(String categoryName) {
        if (!init()) {
            return false;
        }
        Object category = category(categoryName);
        if (category == null) {
            return false;
        }
        try {
            Object screen = screenConstructor.newInstance(category, 0);
            if (!(screen instanceof Screen)) {
                return false;
            }
            PulseCosmeticsBridge.open((Screen) screen);
            return true;
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Failed to open the Pulse Cosmetics menu: {}", throwable.toString());
            return false;
        }
    }

    /** Puts the player into graffiti removal mode of the mod (look at a graffiti, then use the item). */
    public static boolean startGraffitiRemoval() {
        if (!init()) {
            return false;
        }
        try {
            if (UI.isOpen()) {
                UI.INSTANCE.close();
            }
            else {
                MinecraftClient.getInstance().setScreen(null);
            }
            beginRemoval.invoke(null);
            return true;
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Failed to start graffiti removal: {}", throwable.toString());
            return false;
        }
    }
}
