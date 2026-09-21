/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11908
 *  net.minecraft.class_2477
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 *  org.lwjgl.glfw.GLFW
 */
package ru.byazen.input;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.class_11908;
import net.minecraft.class_2477;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import org.lwjgl.glfw.GLFW;
import ru.byazen.input.BindDevice;
import ru.byazen.input.BindInput;

public final class InputBindings {
    private static final Map<String, Integer> KEY_CODES = InputBindings.createKeyCodes();

    private InputBindings() {
    }

    public static boolean isPressed(BindInput input) {
        if (input == null || input.isUnbound()) {
            return false;
        }
        return switch (input.device()) {
            default -> throw new MatchException(null, null);
            case BindDevice.NONE -> false;
            case BindDevice.KEYBOARD -> InputBindings.isKeyPressed(input.code());
            case BindDevice.MOUSE -> InputBindings.isMouseButtonPressed(input.code());
        };
    }

    public static String displayName(BindInput input) {
        if (input == null || input.isUnbound()) {
            return "NONE";
        }
        return input.isMouse() ? InputBindings.mouseButtonName(input.code()) : InputBindings.keyName(input.code());
    }

    public static boolean isMouseButtonPressed(int button) {
        return GLFW.glfwGetMouseButton((long)class_310.method_1551().method_22683().method_4490(), (int)button) == 1;
    }

    public static boolean isKeyPressed(int keyCode) {
        return GLFW.glfwGetKey((long)class_310.method_1551().method_22683().method_4490(), (int)keyCode) == 1;
    }

    public static int keyCode(String name) {
        return name == null ? -1 : KEY_CODES.getOrDefault(name.toUpperCase(Locale.ROOT), -1);
    }

    public static List<String> keyNames() {
        ArrayList<String> names = new ArrayList<String>(KEY_CODES.keySet());
        names.sort(String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    public static String keyName(int keyCode) {
        String prefix;
        String translationKey = class_3675.method_15985((class_11908)new class_11908(keyCode, -1, 0)).method_1441();
        if (translationKey.startsWith(prefix = "key.keyboard.")) {
            translationKey = translationKey.substring(prefix.length());
        }
        return translationKey.replace('.', '_').replace("grave_accent", "`").toUpperCase(Locale.ROOT);
    }

    public static String mouseButtonName(int button) {
        return switch (button) {
            case 0 -> class_2477.method_10517().method_48307("key.mouse.left");
            case 1 -> class_2477.method_10517().method_48307("key.mouse.right");
            case 2 -> class_2477.method_10517().method_48307("key.mouse.middle");
            default -> "MOUSE" + (button + 1);
        };
    }

    private static Map<String, Integer> createKeyCodes() {
        int i;
        char key;
        LinkedHashMap<String, Integer> keys = new LinkedHashMap<String, Integer>();
        for (key = 'A'; key <= 'Z'; key = (char)(key + '\u0001')) {
            keys.put(String.valueOf(key), Integer.valueOf(key));
        }
        for (key = '0'; key <= '9'; key = (char)(key + '\u0001')) {
            keys.put(String.valueOf(key), Integer.valueOf(key));
        }
        for (i = 1; i <= 12; ++i) {
            keys.put("F" + i, 290 + i - 1);
        }
        for (i = 1; i <= 9; ++i) {
            keys.put("NUMPAD" + i, 321 + i - 1);
        }
        keys.put("SPACE", 32);
        keys.put("ENTER", 257);
        keys.put("ESCAPE", 256);
        keys.put("HOME", 268);
        keys.put("INSERT", 260);
        keys.put("DELETE", 261);
        keys.put("END", 269);
        keys.put("PAGEUP", 266);
        keys.put("PAGEDOWN", 267);
        keys.put("RIGHT", 262);
        keys.put("LEFT", 263);
        keys.put("DOWN", 264);
        keys.put("UP", 265);
        keys.put("RSHIFT", 344);
        keys.put("LSHIFT", 340);
        keys.put("RCONTROL", 345);
        keys.put("LCONTROL", 341);
        keys.put("RIGHT_ALT", 346);
        keys.put("LEFT_ALT", 342);
        keys.put("CAPSLOCK", 280);
        keys.put("APOSTROPHE", 39);
        keys.put("/", 47);
        keys.put("-", 45);
        keys.put("+", 61);
        keys.put("BACK", 259);
        keys.put("BACKSLASH", 92);
        keys.put(".", 46);
        keys.put("COMMA", 44);
        keys.put("[", 91);
        keys.put("]", 93);
        keys.put(";", 59);
        keys.put("`", 96);
        return Map.copyOf(keys);
    }
}

