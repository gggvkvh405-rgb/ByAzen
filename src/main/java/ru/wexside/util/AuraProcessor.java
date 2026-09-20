/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1802
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2596
 *  net.minecraft.class_2846
 *  net.minecraft.class_2846$class_2847
 *  net.minecraft.class_2868
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.wexside.util;

import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2596;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.misc.AttackOptions;
import ru.wexside.misc.HitCooldown;
import ru.wexside.misc.Inventories;
import ru.wexside.misc.MaceCheck;
import ru.wexside.misc.ReachHelper;
import ru.wexside.misc.ShieldDesync;
import ru.wexside.util.Angle;
import ru.wexside.util.CriticalsHandler;

public class AuraProcessor {
    private final HitCooldown hitCooldown = new HitCooldown();
    private final CriticalsHandler criticals;
    private boolean enabled;
    private final MaceCheck maceCheck;
    private final ShieldDesync shieldDesync = new ShieldDesync();

    public AuraProcessor(CriticalsHandler criticals) {
        this.maceCheck = new MaceCheck();
        this.criticals = criticals;
    }

    public boolean attack(class_1309 target, AttackOptions options) {
        return this.process(target, options);
    }

    public void tick() {
        this.update();
    }

    public boolean process(class_1309 entity2, AttackOptions iliiiillII2) {
        int n;
        if (entity2 == null || this.enabled) {
            return false;
        }
        class_746 player2 = class_310.method_1551().field_1724;
        if (player2 == null) {
            return false;
        }
        if (!this.process2(entity2, iliiiillII2)) {
            return false;
        }
        this.shieldDesync.setMode(iliiiillII2.sprintResetMode());
        boolean bl = player2.method_5624() && !player2.method_6115();
        boolean bl2 = bl;
        if (iliiiillII2.sprintResetEnabled()) {
            this.shieldDesync.prepareForAttack();
            if (this.shieldDesync.delaysAttackUntilSprintStops() && bl) {
                this.enabled = true;
                return false;
            }
        }
        if (iliiiillII2.desyncShield()) {
            this.releaseShieldUse(player2);
        }
        int n2 = -1;
        if (iliiiillII2.breakShield() && entity2.method_6039()) {
            n = Inventories.findAxeSlot();
            int n3 = player2.method_31548().method_67532();
            if (n != -1 && n != n3) {
                n2 = n3;
                player2.field_3944.method_52787((class_2596)new class_2868(n));
            }
        }
        n = iliiiillII2.accuracyPercent() < 100.0 && Math.random() * 100.0 > iliiiillII2.accuracyPercent() ? 1 : 0;
        int n4 = n;
        if (n != 0) {
            player2.method_6104(class_1268.field_5808);
        } else {
            class_310.method_1551().field_1761.method_2918((class_1657)player2, (class_1297)entity2);
            player2.method_6104(class_1268.field_5808);
        }
        if (n2 != -1) {
            player2.field_3944.method_52787((class_2596)new class_2868(n2));
        }
        if (iliiiillII2.sprintResetEnabled() && this.shieldDesync.sendsSprintPackets()) {
            this.shieldDesync.restoreSprint();
        }
        long l = iliiiillII2.legacyCombat() ? Math.max(50L, 1000L / (long)Math.max(1, iliiiillII2.clicksPerSecond())) : 500L;
        this.hitCooldown.setLongType(l);
        return n == 0;
    }

    public HitCooldown getHitCooldown() {
        return this.hitCooldown;
    }

    public void update() {
        this.enabled = false;
    }

    public MaceCheck getMaceCheck() {
        return this.maceCheck;
    }

    private void releaseShieldUse(class_746 player2) {
        if (!player2.method_6079().method_31574(class_1802.field_8255)) {
            return;
        }
        if (!player2.method_6115()) {
            return;
        }
        player2.field_3944.method_52787((class_2596)new class_2846(class_2846.class_2847.field_12974, class_2338.field_10980, class_2350.field_11033));
    }

    public ShieldDesync getShieldDesync() {
        return this.shieldDesync;
    }

    private boolean process2(class_1309 entity2, AttackOptions iliiiillII2) {
        if (iliiiillII2.raycastEnabled() && !this.process3(entity2, iliiiillII2.lookAngle(), iliiiillII2.range(), iliiiillII2.allowsThroughWalls())) {
            return false;
        }
        class_746 player2 = class_310.method_1551().field_1724;
        if (player2 != null && this.maceCheck.process(player2)) {
            return this.maceCheck.process2(player2, this.hitCooldown, iliiiillII2.maceFallDistance(), iliiiillII2.legacyCombat());
        }
        if (!this.hitCooldown.process(iliiiillII2.legacyCombat())) {
            return false;
        }
        return this.criticals.process(iliiiillII2.criticalsOnly(), iliiiillII2.raycastMode());
    }

    private boolean process3(class_1309 entity2, Angle angle, float f, boolean bl) {
        if (angle == null) {
            return false;
        }
        return ReachHelper.raycastEntity((class_1297)entity2, angle, f, bl) == entity2;
    }
}

