package rtx.kimiko.api.notifications;

import net.minecraft.sound.SoundEvent;

public final class Notifications {
    private Notifications() {}

    public static void push(String title, String message, long durationMs, SoundEvent sound) {
    }

    public static void push(String title, String message, long durationMs) {
        push(title, message, durationMs, null);
    }
}
