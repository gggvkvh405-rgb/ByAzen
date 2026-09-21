/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1140$class_11518
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 */
package ru.wexside.misc;

import java.util.function.Consumer;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1140;
import net.minecraft.class_310;
import net.minecraft.class_3414;

public final class StartupMusicPlayer {
    private final class_3414 music;
    private final Consumer<String> statusSink;
    private int readyTicks;
    private boolean started;

    public StartupMusicPlayer(class_3414 music, Consumer<String> statusSink) {
        this.music = music;
        this.statusSink = statusSink;
    }

    public void tick(class_310 client) {
        if (this.started || client.method_1483().method_4869(this.music.comp_3319()) == null) {
            return;
        }
        if (++this.readyTicks < 10) {
            return;
        }
        class_1140.class_11518 result = client.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)this.music, (float)1.0f, (float)0.8f));
        if (result == class_1140.class_11518.field_60956) {
            this.readyTicks = 0;
            return;
        }
        this.started = true;
        this.statusSink.accept("Startup music playback: " + String.valueOf(result));
    }
}

