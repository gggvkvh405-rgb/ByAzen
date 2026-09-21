/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1792
 *  net.minecraft.class_1802
 */
package ru.wexside.server;

import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import ru.wexside.server.ServerHelperAction;

public final class ServerHelperActions {
    public static final ServerHelperAction GODS_AURA = ServerHelperActions.action("GodsAura", "\u0411\u043e\u0436\u044c\u044f \u0430\u0443\u0440\u0430", class_1802.field_8137, -732322, "\u0411\u043e\u0436\u044c\u044f \u0430\u0443\u0440\u0430", List.of("\u0411\u043e\u0436\u044c\u044f \u0430\u0443\u0440\u0430", "God's Aura"), false, false, 0.0f, false, false);
    public static final ServerHelperAction TRAP = ServerHelperActions.action("Trap", "\u0422\u0440\u0430\u043f\u043a\u0430", class_1802.field_8786, -2631721, "\u0422\u0440\u0430\u043f\u043a\u0430", List.of("\u0422\u0440\u0430\u043f\u043a\u0430", "Trap"), false, false, 4.0f, true, false);
    public static final ServerHelperAction PLAST = ServerHelperActions.action("Plast", "\u041f\u043b\u0430\u0441\u0442", class_1802.field_8777, -8921737, "\u041f\u043b\u0430\u0441\u0442", List.of("\u041f\u043b\u0430\u0441\u0442", "Plast"), false, false, 4.0f, true, false);
    public static final ServerHelperAction DISORIENTATION = ServerHelperActions.action("Disorientation", "\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", class_1802.field_8449, -6595099, "\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", List.of("\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", "Disorientation"), false, false, 5.0f, true, false);
    public static final ServerHelperAction VISIBLE_DUST = ServerHelperActions.action("VisibleDust", "\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", class_1802.field_8601, -11930, "\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", List.of("\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", "Visible Dust"), false, false, 5.0f, true, false);
    public static final ServerHelperAction FREEZE_BALL = ServerHelperActions.action("FreezeBall", "\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0438", class_1802.field_8543, -8333057, "\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0438", List.of("\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0438", "Freeze Ball"), false, false, 5.0f, true, false);
    public static final ServerHelperAction FIERY_TORNADO = ServerHelperActions.action("FieryTornado", "\u041e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0441\u043c\u0435\u0440\u0447", class_1802.field_8814, -38091, "\u041e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0441\u043c\u0435\u0440\u0447", List.of("\u041e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0441\u043c\u0435\u0440\u0447", "Fiery Tornado"), false, false, 6.0f, true, false);
    public static final ServerHelperAction WIND_CHARGE = ServerHelperActions.action("WindCharge", "Wind Charge", class_1802.field_49098, -1509377, "Wind Charge", List.of("Wind Charge", "\u0417\u0430\u0440\u044f\u0434 \u0432\u0435\u0442\u0440\u0430"), false, false, 6.0f, false, true);
    public static final ServerHelperAction ASSASSIN_POTION = ServerHelperActions.potion("PotionAssassin", "\u0417\u0435\u043b\u044c\u0435 \u0410\u0441\u0441\u0430\u0441\u0438\u043d\u0430", -9675545, false);
    public static final ServerHelperAction HOLY_WATER = ServerHelperActions.potion("PotionHolyWater", "\u0421\u0432\u044f\u0442\u0430\u044f \u0412\u043e\u0434\u0430", -9127425, false);
    public static final ServerHelperAction RAGE_POTION = ServerHelperActions.potion("PotionRage", "\u0417\u0435\u043b\u044c\u0435 \u0413\u043d\u0435\u0432\u0430", -2740175, false);
    public static final ServerHelperAction PALADIN_POTION = ServerHelperActions.potion("PotionPaladin", "\u0417\u0435\u043b\u044c\u0435 \u041f\u0430\u043b\u043b\u0430\u0434\u0438\u043d\u0430", -144530, false);
    public static final ServerHelperAction POPPER = ServerHelperActions.potion("PotionPopper", "\u0425\u043b\u043e\u043f\u0443\u0448\u043a\u0430", -35211, true);
    public static final ServerHelperAction RADIATION_POTION = ServerHelperActions.potion("PotionRadiation", "\u0417\u0435\u043b\u044c\u0435 \u0420\u0430\u0434\u0438\u0430\u0446\u0438\u0438", -11145276, true);
    public static final ServerHelperAction DROWSINESS_POTION = ServerHelperActions.potion("PotionDrowsiness", "\u0417\u0435\u043b\u044c\u0435 \u0421\u043d\u043e\u0442\u0432\u043e\u0440\u043d\u043e\u0433\u043e", -6120450, true);
    public static final List<ServerHelperAction> ALL = List.of(GODS_AURA, TRAP, PLAST, DISORIENTATION, VISIBLE_DUST, FREEZE_BALL, FIERY_TORNADO, WIND_CHARGE, ASSASSIN_POTION, HOLY_WATER, RAGE_POTION, PALADIN_POTION, POPPER, RADIATION_POTION, DROWSINESS_POTION);
    public static final List<ServerHelperAction> COOLDOWN_TRACKED = ALL.stream().filter(action -> action.activationDistance() > 0.0f && !action.donationItem()).toList();

    private ServerHelperActions() {
    }

    public static ServerHelperAction byId(String id) {
        if (id == null) {
            return null;
        }
        return ALL.stream().filter(action -> action.id().equals(id)).findFirst().orElse(null);
    }

    private static ServerHelperAction action(String id, String displayName, class_1792 icon, int color, String generalTag, List<String> alternateTags, boolean donationItem, boolean splashPotion, float distance, boolean debuff, boolean matchByItem) {
        return new ServerHelperAction(id, displayName, displayName, icon, color, generalTag, alternateTags, donationItem, splashPotion, distance, debuff, matchByItem);
    }

    private static ServerHelperAction potion(String id, String name, int color, boolean debuff) {
        return ServerHelperActions.action(id, name, class_1802.field_8436, color, name, List.of(name), true, true, 5.0f, debuff, false);
    }
}

