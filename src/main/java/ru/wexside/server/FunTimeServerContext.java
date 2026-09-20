/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_266
 *  net.minecraft.class_269
 *  net.minecraft.class_310
 *  net.minecraft.class_345
 *  net.minecraft.class_642
 *  net.minecraft.class_746
 *  net.minecraft.class_8646
 *  net.minecraft.class_9013
 *  net.minecraft.class_9015
 */
package ru.wexside.server;

import java.util.Locale;
import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_345;
import net.minecraft.class_642;
import net.minecraft.class_746;
import net.minecraft.class_8646;
import net.minecraft.class_9013;
import net.minecraft.class_9015;
import ru.wexside.misc.BossBarMapAccessor;

public final class FunTimeServerContext {
    private static final class_310 CLIENT = class_310.method_1551();
    private static final long PVP_STATUS_GRACE_PERIOD_MS = 3000L;
    private static volatile long lastPvpStatusTime;

    private FunTimeServerContext() {
    }

    public static int getBalance() {
        class_746 player = FunTimeServerContext.CLIENT.field_1724;
        if (player == null || FunTimeServerContext.CLIENT.field_1687 == null) {
            return -1;
        }
        try {
            class_269 scoreboard = FunTimeServerContext.CLIENT.field_1687.method_8428();
            class_266 objective = scoreboard.method_1189(class_8646.field_45157);
            if (objective == null) {
                return -1;
            }
            class_9013 score = scoreboard.method_55430((class_9015)player, objective);
            return score == null ? -1 : score.method_55397();
        }
        catch (RuntimeException ignored) {
            return -1;
        }
    }

    public static boolean isOnHub() {
        if (FunTimeServerContext.CLIENT.field_1705 == null) {
            return false;
        }
        BossBarMapAccessor bossBars = (BossBarMapAccessor)FunTimeServerContext.CLIENT.field_1705.method_1740();
        for (class_345 bossBar : bossBars.getMap().values()) {
            if (!bossBar.method_5414().getString().equals("\u0412\u044b \u0438\u0433\u0440\u0430\u0435\u0442\u0435 \u043d\u0430 \u0424\u0430\u043d\u0422\u0430\u0439\u043c!")) continue;
            return true;
        }
        return false;
    }

    public static boolean isConnected() {
        class_642 server = CLIENT.method_1558();
        return server != null && server.field_3761 != null && server.field_3761.toLowerCase(Locale.ROOT).contains("funtime");
    }

    public static boolean isPvpLocked() {
        if (FunTimeServerContext.CLIENT.field_1687 == null || FunTimeServerContext.CLIENT.field_1724 == null) {
            lastPvpStatusTime = 0L;
            return false;
        }
        BossBarMapAccessor bossBars = (BossBarMapAccessor)FunTimeServerContext.CLIENT.field_1705.method_1740();
        for (class_345 bossBar : bossBars.getMap().values()) {
            String title = bossBar.method_5414().getString().toLowerCase(Locale.ROOT);
            if (!title.contains("pvp") && !title.contains("\u043f\u0432\u043f")) continue;
            lastPvpStatusTime = System.currentTimeMillis();
            return true;
        }
        return lastPvpStatusTime != 0L && System.currentTimeMillis() - lastPvpStatusTime < 3000L;
    }

    public static int getAnarchyNumber() {
        if (FunTimeServerContext.CLIENT.field_1687 == null) {
            return -1;
        }
        try {
            class_266 objective = FunTimeServerContext.CLIENT.field_1687.method_8428().method_1170("TAB-Scoreboard");
            if (objective == null) {
                return -1;
            }
            String title = objective.method_1114().getString();
            int markerIndex = title.indexOf("\u0410\u043d\u0430\u0440\u0445\u0438\u044f-");
            if (markerIndex < 0) {
                return -1;
            }
            String suffix = title.substring(markerIndex + "\u0410\u043d\u0430\u0440\u0445\u0438\u044f-".length()).trim();
            StringBuilder number = new StringBuilder();
            for (int index = 0; index < suffix.length() && Character.isDigit(suffix.charAt(index)); ++index) {
                number.append(suffix.charAt(index));
            }
            return number.isEmpty() ? -1 : Integer.parseInt(number.toString());
        }
        catch (RuntimeException ignored) {
            return -1;
        }
    }

    public static void reset() {
        lastPvpStatusTime = 0L;
    }
}

