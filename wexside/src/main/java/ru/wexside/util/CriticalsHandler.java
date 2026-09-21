/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package ru.wexside.util;

import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.wexside.misc.RaycastMode;
import ru.wexside.util.VelocityTracker;

public final class CriticalsHandler {
    private int ticksSinceAttack = 99;
    private final VelocityTracker velocityTracker = new VelocityTracker();

    public void onAttack() {
        this.update();
    }

    public void tick() {
        this.update2();
    }

    public void update() {
        this.ticksSinceAttack = 0;
    }

    public boolean process(boolean bl, RaycastMode raycastMode) {
        if (!bl) {
            return true;
        }
        return this.process10(raycastMode);
    }

    public void update2() {
        if (this.ticksSinceAttack < 100) {
            ++this.ticksSinceAttack;
        }
    }

    public VelocityTracker getVelocityTracker() {
        return this.velocityTracker;
    }

    private boolean process2(class_746 player2) {
        if (!player2.method_5715() && !class_310.method_1551().field_1690.field_1903.method_1434()) {
            return true;
        }
        boolean bl2 = !player2.method_5715() && this.process7(player2) && this.process4(player2);
        boolean bl = player2.method_5765() || player2.method_5799() && !bl2 || player2.method_31549().field_7479;
        boolean bl3 = bl;
        if (!bl) {
            return !player2.method_24828() && player2.field_6017 > 0.0;
        }
        return true;
    }

    private boolean process3(class_746 player2) {
        class_638 world2 = class_310.method_1551().field_1687;
        if (world2 == null) {
            return false;
        }
        double d = player2.method_23318() + (player2.method_24828() ? 2.5 : 1.5);
        class_238 box = new class_238(player2.method_23317() - 0.3, player2.method_23318(), player2.method_23321() - 0.3, player2.method_23317() + 0.3, d, player2.method_23321() + 0.3);
        return !world2.method_8587((class_1297)player2, box);
    }

    private boolean process4(class_746 player2) {
        class_638 world2 = class_310.method_1551().field_1687;
        if (world2 == null) {
            return false;
        }
        class_2338 pos = player2.method_24515().method_10062();
        return world2.method_8320(pos).method_26215();
    }

    public int getIntType() {
        return this.ticksSinceAttack;
    }

    private boolean process5(class_746 player2) {
        return player2.method_6059(class_1294.field_5919) || player2.method_6059(class_1294.field_5902) || player2.method_6059(class_1294.field_5906) || player2.method_5771() || player2.method_6101() || player2.method_5765() || this.process6(player2) || player2.method_31549().field_7479;
    }

    private boolean process6(class_746 player2) {
        return player2.method_18798().method_1027() > 1.0E-7;
    }

    private boolean process7(class_746 player2) {
        class_638 world2 = class_310.method_1551().field_1687;
        if (world2 == null) {
            return false;
        }
        class_2338 pos = player2.method_24515();
        if (world2.method_8316(pos).method_15769()) {
            return false;
        }
        return world2.method_8316(pos.method_10084()).method_15769();
    }

    private boolean process8(class_746 player2) {
        if (player2.method_24828() || player2.field_6017 <= 0.0) {
            return false;
        }
        return !this.velocityTracker.isActive();
    }

    private boolean process9(class_746 player2) {
        if (!player2.method_24828()) {
            return false;
        }
        double d = player2.method_23318();
        boolean bl = d != Math.floor(d);
        boolean bl2 = bl;
        if (!bl) {
            return false;
        }
        return this.process3(player2);
    }

    public boolean process10(RaycastMode raycastMode) {
        class_746 player2 = class_310.method_1551().field_1724;
        if (player2 == null) {
            return false;
        }
        if (player2.method_5799()) {
            return this.process2(player2);
        }
        if (this.process5(player2)) {
            return true;
        }
        if (this.process9(player2)) {
            return true;
        }
        if (raycastMode == RaycastMode.THROUGH_WALLS) {
            boolean bl = class_310.method_1551().field_1690.field_1824.method_1434();
            boolean bl2 = !player2.method_24828() && player2.field_6017 > 0.0;
            return player2.method_24828() && !bl || bl2;
        }
        return this.process8(player2);
    }
}

