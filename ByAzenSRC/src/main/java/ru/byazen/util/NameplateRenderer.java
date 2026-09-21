/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1528
 *  net.minecraft.class_1542
 *  net.minecraft.class_1548
 *  net.minecraft.class_1560
 *  net.minecraft.class_1588
 *  net.minecraft.class_1613
 *  net.minecraft.class_1627
 *  net.minecraft.class_1628
 *  net.minecraft.class_1642
 *  net.minecraft.class_1747
 *  net.minecraft.class_1753
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1887
 *  net.minecraft.class_1890
 *  net.minecraft.class_1893
 *  net.minecraft.class_2190
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_2588
 *  net.minecraft.class_266
 *  net.minecraft.class_269
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_4604
 *  net.minecraft.class_5250
 *  net.minecraft.class_5251
 *  net.minecraft.class_5321
 *  net.minecraft.class_6880
 *  net.minecraft.class_8646
 *  net.minecraft.class_9013
 *  net.minecraft.class_9015
 *  net.minecraft.class_9304
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 */
package ru.byazen.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1528;
import net.minecraft.class_1542;
import net.minecraft.class_1548;
import net.minecraft.class_1560;
import net.minecraft.class_1588;
import net.minecraft.class_1613;
import net.minecraft.class_1627;
import net.minecraft.class_1628;
import net.minecraft.class_1642;
import net.minecraft.class_1747;
import net.minecraft.class_1753;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2190;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2588;
import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_4604;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_8646;
import net.minecraft.class_9013;
import net.minecraft.class_9015;
import net.minecraft.class_9304;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.byazen.ByAzenClient;
import ru.byazen.event.EventBus;
import ru.byazen.event.HudRenderEvent;
import ru.byazen.event.WorldSessionEvent;
import ru.byazen.item.ItemBadge;
import ru.byazen.item.ItemBadgeCategory;
import ru.byazen.misc.BakedIconEntry;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.FriendList;
import ru.byazen.misc.NameTagSettings;
import ru.byazen.model.esp.EspTargetClassifier;
import ru.byazen.model.esp.EspTargetType;
import ru.byazen.module.player.NameProtectModule;
import ru.byazen.render.BakedItemIcon;
import ru.byazen.render.ItemIconCache;
import ru.byazen.render.NameplateLayout;
import ru.byazen.render.RenderCamera;
import ru.byazen.render.RenderProjection;
import ru.byazen.util.EspFeatureRegistry;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.MsdfFontRenderer;

public final class NameplateRenderer {
    private static final float value5 = 1.15f;
    private static final float value6 = 11.0f;
    private final int backgroundColor;
    private static final float value7 = 6.25f;
    private final int specialItemBackgroundColor;
    private static final float value8 = 5.0f;
    private static final int slot3 = 0xFF5555;
    private static final int process19 = 0xFFFF00;
    static volatile NameplateRenderer fire2;
    private static final float value9 = 2.0f;
    private static final int slot4 = -1;
    private static final MsdfFontRenderer enchantmentFont;
    private static final class_1304[] ARMOR_SLOTS;
    private static final int slot5 = 0x55FF55;
    private final class_310 mc = class_310.method_1551();
    private static final float value10 = 2.0f;
    private final class_2561 friendSuffix;
    private static final int slot6 = 0xAAAAAA;
    private static final float value11 = 1.15f;
    private final class_2561 separator;
    private static final MsdfFontRenderer nameFont;
    private final EspFeatureRegistry espFeatures;
    private final ItemIconCache itemIcons;
    private final int friendBackgroundColor;
    private static final int slot8 = 0x808080;

    public NameplateRenderer(EventBus eventBus, EspFeatureRegistry espFeatures) {
        this.espFeatures = espFeatures;
        this.itemIcons = new ItemIconCache();
        this.backgroundColor = -871362544;
        this.specialItemBackgroundColor = -869654000;
        this.friendBackgroundColor = -871355880;
        this.separator = class_2561.method_43470((String)" ");
        this.friendSuffix = class_2561.method_43470((String)"[Friend]").method_54663(0x55FF55);
        eventBus.subscribe(HudRenderEvent.class, this::setHudRenderEvent, -100);
        eventBus.subscribe(WorldSessionEvent.class, this::onWorldChanged);
        fire2 = this;
    }

    private void onWorldChanged(WorldSessionEvent event) {
        this.itemIcons.close();
    }

    private boolean process(class_1799 stack) {
        return stack.method_31573(class_3489.field_42611);
    }

    private class_5250 buildLabel(class_1297 entity, NameTagSettings settings) {
        class_2561 text;
        boolean localPlayer = entity == this.mc.field_1724;
        FriendList friends = ByAzenClient.getFriends();
        boolean friend = friends != null && friends.contains(entity.method_5477().getString());
        boolean bl = friend;
        if (entity instanceof class_1542) {
            class_1542 item = (class_1542)entity;
            text = item.method_6983().method_7964();
        } else {
            text = entity.method_5476();
        }
        class_2561 displayName = text;
        class_5250 label = displayName != null ? displayName.method_27661() : entity.method_5477().method_27661();
        class_5250 mutableText = label;
        if (displayName != null && displayName.method_10851() instanceof class_2588) {
            label = displayName.method_27661().method_27696(class_2583.field_24360.method_36139(0x808080));
        }
        if (friend && !localPlayer && NameProtectModule.isEnabled()) {
            label = class_2561.method_43470((String)NameProtectModule.getString());
        }
        if (settings.isHealthVisible() && entity instanceof class_1309) {
            class_1309 living = (class_1309)entity;
            label.method_10852(this.separator).method_10852((class_2561)this.bracketedText(0xFF5555, String.valueOf(this.getHealth(entity, living))));
        }
        int reward = settings.isMoneyVisible() && entity instanceof class_1588 ? this.getMobReward(entity) : 0;
        int n = reward;
        if (reward != 0) {
            label.method_10852(this.separator).method_10852((class_2561)this.bracketedText(0xFFFF00, "~" + reward + "$"));
        }
        if (friend && !localPlayer) {
            label.method_10852(this.separator).method_10852(this.friendSuffix);
        }
        return label;
    }

    private float process3(NameplateLayout nameplateLayout, int n) {
        return nameplateLayout.centerX + ((float)n - (float)(nameplateLayout.equipment.size() - 1) / 2.0f) * 11.0f;
    }

    private static int enchantmentLevel(class_1799 stack, class_5321<class_1887> enchantment) {
        class_9304 espFeatures = class_1890.method_57532((class_1799)stack);
        for (Object2IntMap.Entry entry : espFeatures.method_57539()) {
            if (!((class_6880)entry.getKey()).method_40225(enchantment)) continue;
            return entry.getIntValue();
        }
        return 0;
    }

    private float process5(GuiDrawApi drawApi, Matrix4f matrix4f, StringBuilder stringBuilder, float f, float f2, float f3, int n) {
        String string = stringBuilder.toString();
        stringBuilder.setLength(0);
        nameFont.process2(matrix4f, drawApi, string, f, f2, f3, n);
        return nameFont.process3(string, f3);
    }

    private List<String> process6(class_1799 stack) {
        ArrayList<String> arrayList = new ArrayList<String>();
        int n = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9111);
        int n2 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9097);
        int n3 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9119);
        int n4 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9101);
        int n5 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9129);
        int n6 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9128);
        int n7 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9118);
        int n8 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9110);
        int n9 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9125);
        int n10 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9103);
        int n11 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9116);
        int n12 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9126);
        int n13 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9121);
        int n14 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9124);
        int n15 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9131);
        int n16 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9099);
        int n17 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9130);
        int n18 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9095);
        int n19 = NameplateRenderer.enchantmentLevel(stack, (class_5321<class_1887>)class_1893.field_9107);
        if (this.process35(stack)) {
            if (n7 > 0) {
                int n20 = n7;
                arrayList.add("Shr" + n20);
            }
            if (n15 > 0) {
                int n21 = n15;
                arrayList.add("Eff" + n21);
            }
            if (n3 > 0) {
                int n22 = n3;
                arrayList.add("Unb" + n22);
            }
        }
        if (this.process11(stack)) {
            if (n18 > 0) {
                int n23 = n18;
                arrayList.add("Fire" + n23);
            }
            if (n19 > 0) {
                int n24 = n19;
                arrayList.add("Bla" + n24);
            }
            if (n6 > 0) {
                int n25 = n6;
                arrayList.add("Dep" + n25);
            }
            if (n5 > 0) {
                int n26 = n5;
                arrayList.add("Fea" + n26);
            }
            if (n > 0) {
                int n27 = n;
                arrayList.add("Pro" + n27);
            }
            if (n2 > 0) {
                int n28 = n2;
                arrayList.add("Thr" + n28);
            }
            if (n4 > 0) {
                arrayList.add("Mn");
            }
            if (n3 > 0) {
                int n29 = n3;
                arrayList.add("Unb" + n29);
            }
        }
        if (this.process28(stack)) {
            if (n9 > 0) {
                int n30 = n9;
                arrayList.add("Inf" + n30);
            }
            if (n10 > 0) {
                int n31 = n10;
                arrayList.add("Pow" + n31);
            }
            if (n11 > 0) {
                int n32 = n11;
                arrayList.add("Pun" + n32);
            }
            if (n4 > 0) {
                arrayList.add("Mn");
            }
            if (n12 > 0) {
                int n33 = n12;
                arrayList.add("Fla" + n33);
            }
            if (n3 > 0) {
                int n34 = n3;
                arrayList.add("Unb" + n34);
            }
        }
        if (this.process(stack)) {
            if (n8 > 0) {
                int n35 = n8;
                arrayList.add("L" + n35);
            }
            if (n7 > 0) {
                int n36 = n7;
                arrayList.add("Shr" + n36);
            }
            if (n13 > 0) {
                int n37 = n13;
                arrayList.add("Kno" + n37);
            }
            if (n14 > 0) {
                int n38 = n14;
                arrayList.add("Fir" + n38);
            }
            if (n3 > 0) {
                int n39 = n3;
                arrayList.add("Unb" + n39);
            }
            if (n4 > 0) {
                arrayList.add("Mn");
            }
        }
        if (this.process23(stack)) {
            if (n3 > 0) {
                int n40 = n3;
                arrayList.add("Unb" + n40);
            }
            if (n4 > 0) {
                arrayList.add("Mn");
            }
            if (n15 > 0) {
                int n41 = n15;
                arrayList.add("Eff" + n41);
            }
            if (n16 > 0) {
                int n42 = n16;
                arrayList.add("Sil" + n42);
            }
            if (n17 > 0) {
                int n43 = n17;
                arrayList.add("For" + n43);
            }
        }
        return arrayList;
    }

    private void process7(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
        class_2561 iIiilliIII2 = nameplateLayout.badge.label();
        float f = this.process13(iIiilliIII2, 6.25f);
        this.process38(drawApi, matrix4f, iIiilliIII2, nameplateLayout.centerX - f / 2.0f, this.process17(nameplateLayout), 6.25f);
    }

    private void process8(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
        float f = nameFont.process4(nameplateLayout.label.getString(), nameplateLayout.fontSize);
        float f2 = nameplateLayout.labelWidth;
        float f3 = nameplateLayout.centerX - f2 / 2.0f;
        float f4 = f3 - 1.5f;
        float f5 = nameplateLayout.baselineY - f - 2.5f;
        float f6 = f3 + f2 + 1.5f - f4;
        float f7 = nameplateLayout.baselineY - 2.0f - f5;
        drawApi.fillRectangle(matrix4f, f4, f5, f6, f7, nameplateLayout.friend ? this.friendBackgroundColor : this.backgroundColor);
    }

    private boolean process11(class_1799 stack) {
        return stack.method_31573(class_3489.field_48303);
    }

    private boolean process12(class_1297 entity2, class_4604 frustum2) {
        EspTargetType espTargetType = EspTargetClassifier.targetType(entity2, this.mc.field_1724);
        NameTagSettings talisman = this.espFeatures.getNameTagSettings(espTargetType, EspTargetClassifier.relation(entity2));
        return espTargetType != null && talisman != null && talisman.isEnabled() && entity2.method_5805() && RenderProjection.isVisible(entity2, frustum2) && (entity2 != this.mc.field_1724 || !this.mc.field_1690.method_31044().method_31034());
    }

    private float process13(class_2561 iIiilliIII2, float f) {
        float[] fArray = new float[]{0.0f};
        StringBuilder stringBuilder = new StringBuilder(16);
        iIiilliIII2.method_27658((style, string) -> {
            String string2 = string;
            stringBuilder.setLength(0);
            for (int i = 0; i < string2.length(); ++i) {
                char c = string2.charAt(i);
                if ((c == '\u00a7' || c == '&') && i + 1 < string2.length() && class_124.method_544((char)Character.toLowerCase(string2.charAt(i + 1))) != null) {
                    ++i;
                    continue;
                }
                stringBuilder.append(c);
            }
            if (stringBuilder.length() > 0) {
                fArray[0] = fArray[0] + nameFont.process3(stringBuilder.toString(), f);
            }
            return Optional.empty();
        }, class_2583.field_24360);
        return fArray[0];
    }

    private float process14(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        float f9;
        float f10 = Math.max(0.0f, Math.min(f + f3, f5 + f7) - Math.max(f, f5));
        float f11 = f10 * (f9 = Math.max(0.0f, Math.min(f2 + f4, f6 + f8) - Math.max(f2, f6)));
        if (f11 == 0.0f) {
            return 0.0f;
        }
        return f11 / Math.min(f3 * f4, f7 * f8);
    }

    private float process15(NameplateLayout nameplateLayout) {
        if (nameplateLayout.badge != null) {
            return this.process17(nameplateLayout) - nameFont.process4("Ag", 6.25f) - 2.0f;
        }
        return nameplateLayout.baselineY - 23.0f - 10.0f;
    }

    public static boolean process16(class_1297 entity2) {
        NameplateRenderer fire = fire2;
        if (fire == null) {
            return false;
        }
        class_310 mc2 = class_310.method_1551();
        EspTargetType espTargetType = EspTargetClassifier.targetType(entity2, mc2.field_1724);
        NameTagSettings talisman = fire.espFeatures.getNameTagSettings(espTargetType, EspTargetClassifier.relation(entity2));
        return espTargetType != null && talisman != null && talisman.isEnabled() && (entity2 != mc2.field_1724 || !mc2.field_1690.method_31044().method_31034());
    }

    private float process17(NameplateLayout nameplateLayout) {
        return nameplateLayout.baselineY - 23.0f - 9.0f;
    }

    private int getHealth(class_1297 entity, class_1309 living) {
        class_269 scoreboard = this.mc.field_1687.method_8428();
        class_266 objective = scoreboard.method_1189(class_8646.field_45158);
        class_9013 score = objective == null ? null : scoreboard.method_55430((class_9015)entity, objective);
        class_9013 readableScoreboardScore = score;
        if (score != null) {
            return score.method_55397();
        }
        return Math.round(living.method_6032());
    }

    private List<class_1799> process20(class_1309 entity3) {
        ArrayList<class_1799> arrayList = new ArrayList<class_1799>();
        arrayList.add(entity3.method_6047());
        arrayList.add(entity3.method_6079());
        for (class_1304 iliiIIiliI2 : ARMOR_SLOTS) {
            arrayList.add(entity3.method_6118(iliiIIiliI2));
        }
        arrayList.removeIf(class_1799::method_7960);
        return arrayList;
    }

    private void process22(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
        float f = nameplateLayout.baselineY - 23.0f;
        for (int i = 0; i < nameplateLayout.equipment.size(); ++i) {
            float f2 = this.process3(nameplateLayout, i);
            boolean bl = this.process29(nameplateLayout.equipment.get(i));
            drawApi.fillRectangle(matrix4f, f2 - 5.0f, f, 10.0f, 10.0f, bl ? this.specialItemBackgroundColor : this.backgroundColor);
        }
    }

    private boolean process23(class_1799 stack) {
        return stack.method_31573(class_3489.field_42614) || stack.method_31573(class_3489.field_42615) || stack.method_31573(class_3489.field_42613);
    }

    private float process25(NameplateLayout nameplateLayout) {
        float f = 0.0f;
        for (class_1799 stack : nameplateLayout.equipment) {
            for (String string : this.process6(stack)) {
                f = Math.max(f, enchantmentFont.process3(string, 5.0f));
            }
        }
        float f2 = 10.5f;
        if (f > f2) {
            return 5.0f * f2 / f;
        }
        return 5.0f;
    }

    private boolean process26(class_1297 entity2) {
        if (entity2 == this.mc.field_1724) {
            return false;
        }
        FriendList friendList2 = ByAzenClient.getFriends();
        return friendList2 != null && friendList2.contains(entity2.method_5477().getString());
    }

    private int getMobReward(class_1297 entity2) {
        if (entity2 instanceof class_1628) {
            return 700;
        }
        if (entity2 instanceof class_1548) {
            return 2000;
        }
        if (entity2 instanceof class_1627) {
            return 4000;
        }
        if (entity2 instanceof class_1613 || entity2 instanceof class_1642) {
            return 600;
        }
        if (entity2 instanceof class_1560) {
            return 1000;
        }
        if (entity2 instanceof class_1528) {
            return 10000;
        }
        return 0;
    }

    private boolean process28(class_1799 stack) {
        return stack.method_7909() instanceof class_1753;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setHudRenderEvent(HudRenderEvent gameEvent20) {
        if (!this.espFeatures.hasEnabledNameTags()) {
            this.itemIcons.close();
            return;
        }
        if (this.mc.field_1687 == null || this.mc.field_1724 == null || RenderCamera.position() == null) {
            return;
        }
        class_4604 frustum = RenderProjection.frustum();
        Matrix4f viewProjection = RenderProjection.viewProjectionMatrix();
        ArrayList<class_1297> visibleEntities = new ArrayList<class_1297>();
        for (class_1297 entity2 : this.mc.field_1687.method_18112()) {
            if (!this.process12(entity2, frustum)) continue;
            visibleEntities.add(entity2);
        }
        if (visibleEntities.isEmpty()) {
            return;
        }
        visibleEntities.sort(Comparator.comparingDouble(entity -> this.mc.field_1724.method_73189().method_1025(entity.method_73189())));
        ArrayList<NameplateLayout> nameplates = new ArrayList<NameplateLayout>();
        ArrayList<float[]> occupiedBounds = new ArrayList<float[]>();
        for (class_1297 class_12972 : visibleEntities) {
            class_1309 living;
            List<class_1799> equipment;
            float f4;
            NameTagSettings settings = this.espFeatures.getNameTagSettings(EspTargetClassifier.targetType(class_12972, this.mc.field_1724), EspTargetClassifier.relation(class_12972));
            Vector2f projected = RenderProjection.projectEntityLabel(class_12972, viewProjection);
            if (settings == null || projected == null) continue;
            class_5250 label = this.buildLabel(class_12972, settings);
            String string = label.getString();
            float f = string.contains("\u2605") ? 9.0f : 7.0f;
            float f5 = f4 = this.process13((class_2561)label, f);
            float f6 = nameFont.process4(string, f) + 4.0f;
            float f7 = projected.x - f5 / 2.0f;
            float f8 = projected.y - f6;
            if (settings.shouldPreventOverlap()) {
                boolean overlaps = false;
                for (float[] bounds : occupiedBounds) {
                    if (!((double)this.process14(f7, f8, f5, f6, bounds[0], bounds[1], bounds[2], bounds[3]) > settings.getOverlapThreshold())) continue;
                    overlaps = true;
                    break;
                }
                if (overlaps) continue;
                occupiedBounds.add(new float[]{f7, f8, f5, f6});
            }
            NameplateLayout nameplate = new NameplateLayout(projected.x, projected.y, label, f, f4);
            nameplate.friend = this.process26(class_12972);
            nameplate.showEnchantments = settings.shouldShowEnchantments();
            if (settings.areItemsVisible() && class_12972 instanceof class_1309 && !(equipment = this.process20(living = (class_1309)class_12972)).isEmpty()) {
                nameplate.equipment = equipment;
            }
            if (class_12972 instanceof class_1309) {
                living = (class_1309)class_12972;
                nameplate.badge = NameplateRenderer.process40(living, settings.isTalismanVisible(), settings.isSphereVisible());
            }
            nameplates.add(nameplate);
        }
        if (nameplates.isEmpty()) {
            return;
        }
        float scale = this.mc.method_22683().method_4495();
        this.itemIcons.beginFrame();
        for (NameplateLayout nameplate : nameplates) {
            if (nameplate.equipment == null) continue;
            nameplate.equipmentIcons = new ArrayList<BakedItemIcon>(nameplate.equipment.size());
            for (class_1799 stack : nameplate.equipment) {
                nameplate.equipmentIcons.add(this.itemIcons.get(stack));
            }
        }
        ArrayList<BakedIconEntry> arrayList = new ArrayList<BakedIconEntry>();
        this.itemIcons.collectBakeEntries(scale, arrayList);
        if (!arrayList.isEmpty()) {
            ByAzenClient.getRenderPipeline2().setList(arrayList);
        }
        GuiDrawApi renderer = ByAzenClient.getHudRenderer();
        Matrix4f guiMatrix = new Matrix4f().scale(scale);
        renderer.begin();
        try {
            for (NameplateLayout nameplate : nameplates) {
                this.process8(renderer, guiMatrix, nameplate);
                if (nameplate.badge != null) {
                    this.process31(renderer, guiMatrix, nameplate);
                }
                if (nameplate.equipment == null) continue;
                this.process22(renderer, guiMatrix, nameplate);
            }
            for (NameplateLayout nameplate : nameplates) {
                if (nameplate.equipment == null) continue;
                this.process33(renderer, guiMatrix, nameplate);
            }
        }
        finally {
            renderer.end();
        }
        renderer.begin();
        try {
            for (NameplateLayout nameplate : nameplates) {
                this.process37(renderer, guiMatrix, nameplate);
                if (nameplate.badge == null) continue;
                this.process7(renderer, guiMatrix, nameplate);
            }
            for (NameplateLayout nameplate : nameplates) {
                if (nameplate.equipment == null || !nameplate.showEnchantments) continue;
                this.process30(renderer, guiMatrix, nameplate);
            }
        }
        finally {
            renderer.end();
        }
        this.itemIcons.evictUnused();
    }

    private boolean process29(class_1799 stack) {
        class_1747 text2;
        class_1792 iiIilIIilI2 = stack.method_7909();
        boolean bl = iiIilIIilI2 instanceof class_1747 && (text2 = (class_1747)iiIilIIilI2).method_7711() instanceof class_2190;
        return bl || stack.method_7909() == class_1802.field_8288;
    }

    private void process30(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
        float f = this.process25(nameplateLayout);
        float f2 = enchantmentFont.process4("Ag", f) + 1.0f;
        float f3 = this.process15(nameplateLayout);
        for (int i = 0; i < nameplateLayout.equipment.size(); ++i) {
            float f4 = this.process3(nameplateLayout, i);
            List<String> list = this.process6(nameplateLayout.equipment.get(i));
            for (int j = 0; j < list.size(); ++j) {
                String string = list.get(j);
                float f5 = enchantmentFont.process3(string, f);
                enchantmentFont.process2(matrix4f, drawApi, string, f4 - f5 / 2.0f, f3 - (float)j * f2, f, -1);
            }
        }
    }

    private void process31(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
        class_2561 iIiilliIII2 = nameplateLayout.badge.label();
        float f = this.process13(iIiilliIII2, 6.25f);
        float f2 = nameFont.process4(iIiilliIII2.getString(), 6.25f);
        float f3 = nameplateLayout.centerX - f / 2.0f;
        float f4 = this.process17(nameplateLayout);
        drawApi.fillRectangle(matrix4f, f3 - 1.5f, f4 - 1.0f, f + 3.0f, f2 + 2.0f, this.backgroundColor);
    }

    private void process33(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
        float f = nameplateLayout.baselineY - 23.0f;
        for (int i = 0; i < nameplateLayout.equipment.size(); ++i) {
            float f2 = this.process3(nameplateLayout, i);
            this.itemIcons.process3(drawApi, matrix4f, nameplateLayout.equipmentIcons.get(i), f2 - 4.5f, f + 0.5f, 9.0f);
        }
    }

    private boolean process35(class_1799 stack) {
        return stack.method_31573(class_3489.field_42612);
    }

    private class_5250 bracketedText(int color, String value) {
        return class_2561.method_43473().method_10852((class_2561)class_2561.method_43470((String)"[").method_54663(0xAAAAAA)).method_10852((class_2561)class_2561.method_43470((String)value).method_54663(color)).method_10852((class_2561)class_2561.method_43470((String)"]").method_54663(0xAAAAAA));
    }

    private void process37(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
        float f3 = nameFont.process4(nameplateLayout.label.getString(), nameplateLayout.fontSize);
        float f4 = nameplateLayout.labelWidth;
        float f5 = nameplateLayout.centerX - f4 / 2.0f;
        float f6 = nameplateLayout.baselineY - f3 - 2.5f;
        this.process38(drawApi, matrix4f, (class_2561)nameplateLayout.label, f5, f6, nameplateLayout.fontSize);
    }

    private void process38(GuiDrawApi drawApi, Matrix4f matrix4f, class_2561 iIiilliIII2, float f, float f2, float f3) {
        float[] fArray = new float[]{f};
        StringBuilder stringBuilder = new StringBuilder(16);
        iIiilliIII2.method_27658((style, string) -> {
            int baseColor;
            String string2 = string;
            class_5251 textColor = style.method_10973();
            int currentColor = baseColor = 0xFF000000 | (textColor != null ? textColor.method_27716() : 0xFFFFFF);
            for (int i = 0; i < string2.length(); ++i) {
                class_124 formatting;
                char c = string2.charAt(i);
                if ((c == '\u00a7' || c == '&') && i + 1 < string2.length() && (formatting = class_124.method_544((char)Character.toLowerCase(string2.charAt(i + 1)))) != null) {
                    int nextColor = formatting == class_124.field_1070 ? baseColor : (formatting.method_532() != null ? 0xFF000000 | formatting.method_532() : currentColor);
                    if (nextColor != currentColor && stringBuilder.length() > 0) {
                        fArray[0] = fArray[0] + this.process5(drawApi, matrix4f, stringBuilder, fArray[0], f2, f3, currentColor);
                    }
                    currentColor = nextColor;
                    ++i;
                    continue;
                }
                stringBuilder.append(c);
            }
            if (stringBuilder.length() > 0) {
                fArray[0] = fArray[0] + this.process5(drawApi, matrix4f, stringBuilder, fArray[0], f2, f3, currentColor);
            }
            return Optional.empty();
        }, class_2583.field_24360);
    }

    private static ItemBadge process40(class_1309 entity3, boolean bl, boolean bl2) {
        if (!bl && !bl2) {
            return null;
        }
        ItemBadge itemBadge = ItemBadge.fromStack(entity3.method_6079());
        if (itemBadge == null) {
            return null;
        }
        boolean enabledForCategory = itemBadge.category() == ItemBadgeCategory.TALISMAN ? bl : bl2;
        return enabledForCategory ? itemBadge : null;
    }

    static {
        enchantmentFont = FontRegistry.icons;
        ARMOR_SLOTS = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
        nameFont = FontRegistry.regularText;
    }
}

