/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1922
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 */
package ru.wexside.module.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.BlockBreakingAccessor;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public class NukerModule
extends Module
implements ConfigSerializable {
    private static final String ROTATION_OWNER = "Simple";
    private final BooleanSetting enabledSetting;
    private final NumberSetting speed;
    private final NumberSetting height;
    private final NumberSetting blocks;
    private final BooleanSetting rotate;
    private final List<class_2338> targets = new ArrayList<class_2338>();
    private boolean sessionActive;
    private boolean rotating;

    public NukerModule(EventBus eventBus) {
        super(eventBus, "nuker", "Nuker", "\u041b\u043e\u043c\u0430\u0435\u0442 \u0431\u043b\u043e\u043a\u0438 \u0432\u043e\u043a\u0440\u0443\u0433 \u0438\u0433\u0440\u043e\u043a\u0430", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.speed = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 100.0).defaultValue(70.0).multiplier(1.0).precision(0).animationSpeed(20.0f).showMarkers().name("Speed").id("speed").description("\u0423\u0441\u043a\u043e\u0440\u0435\u043d\u0438\u0435 (\u0432 % \u043e\u0442 \u0438\u0437\u043d\u0430\u0447\u0430\u043b\u044c\u043d\u043e\u0439 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u0438)")).build();
        this.registerSetting(this.speed);
        this.height = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 10.0).defaultValue(3.0).multiplier(1.0).precision(0).animationSpeed(20.0f).showMarkers().name("Height").id("height").description("\u0421\u043a\u043e\u043b\u044c\u043a\u043e \u0431\u043b\u043e\u043a\u043e\u0432 \u0432 \u0432\u044b\u0441\u043e\u0442\u0443 \u043b\u043e\u043c\u0430\u0442\u044c").aliases("height", "\u0432\u044b\u0441\u043e\u0442\u0430")).build();
        this.registerSetting(this.height);
        this.blocks = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 30.0).defaultValue(1.0).multiplier(1.0).precision(0).animationSpeed(20.0f).showMarkers().name("Multiply-Blocks").id("blocks").description("\u0421\u043a\u043e\u043b\u044c\u043a\u043e \u0431\u043b\u043e\u043a\u043e\u0432 \u043b\u043e\u043c\u0430\u0442\u044c \u043e\u0434\u043d\u043e\u0432\u0440\u0435\u043c\u0435\u043d\u043d\u043e")).build();
        this.registerSetting(this.blocks);
        this.rotate = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Rotate").id("rotate").description("\u041f\u043b\u0430\u0432\u043d\u043e \u043f\u043e\u0432\u043e\u0440\u0430\u0447\u0438\u0432\u0430\u0442\u044c\u0441\u044f \u043d\u0430 \u043b\u043e\u043c\u0430\u0435\u043c\u044b\u0439 \u0431\u043b\u043e\u043a")).build();
        this.registerSetting(this.rotate);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::setFloatType);
        this.listen(WorldSessionEvent.class, event -> this.reset());
    }

    private void setFloatType(ClientTickEvent event) {
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        class_636 interactions = client.field_1761;
        if (!this.enabledSetting.isEnabled() || player == null || client.field_1687 == null || interactions == null) {
            this.sessionActive = false;
            this.releaseLook();
            return;
        }
        if (!this.sessionActive) {
            this.sessionActive = true;
            this.targets.clear();
        }
        BlockBreakingAccessor breaking = (BlockBreakingAccessor)interactions;
        breaking.setBreakingCooldown(0);
        float speedBonus = (float)this.speed.getIntValue() / 100.0f;
        if (this.targets.isEmpty()) {
            this.scanTargets(player);
        }
        class_2338 lookTarget = null;
        Iterator<class_2338> iterator = this.targets.iterator();
        while (iterator.hasNext()) {
            class_2338 pos = iterator.next();
            if (client.field_1687.method_8320(pos).method_26215()) {
                iterator.remove();
                continue;
            }
            if (lookTarget == null) {
                lookTarget = pos;
            }
            player.method_6104(class_1268.field_5808);
            interactions.method_2902(pos, class_2350.field_11036);
            float progress = breaking.getBreakingProgress();
            if (!(progress > 1.0f - speedBonus) || !(progress < 0.99f)) continue;
            breaking.setBreakingProgress(0.99f);
        }
        if (this.rotate.isEnabled() && lookTarget != null) {
            this.lookAt(player, lookTarget);
        } else {
            this.releaseLook();
        }
    }

    private void reset() {
        this.targets.clear();
        this.sessionActive = false;
        this.releaseLook();
    }

    private void scanTargets(class_746 player) {
        int limit = this.blocks.getIntValue();
        int minY = (int)Math.floor(player.method_23318());
        int maxY = minY + this.height.getIntValue() - 1;
        double x = player.method_23317();
        double z = player.method_23321();
        class_310 client = class_310.method_1551();
        for (int y = minY; y <= maxY; ++y) {
            for (int offsetX = -3; offsetX <= 3; ++offsetX) {
                for (int offsetZ = -3; offsetZ <= 3; ++offsetZ) {
                    class_2338 pos;
                    class_2680 state;
                    if (this.targets.size() >= limit) {
                        return;
                    }
                    if (Math.sqrt((double)offsetX * (double)offsetX + (double)offsetZ * (double)offsetZ) > 4.0 || (state = client.field_1687.method_8320(pos = class_2338.method_49637((double)(x + (double)offsetX), (double)y, (double)(z + (double)offsetZ)))).method_26215() || this.unbreakable(state, pos) || !this.hasLineOfSight(player, pos)) continue;
                    this.targets.add(pos);
                }
            }
        }
    }

    private class_243 blockCenter(class_2338 pos) {
        return new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5);
    }

    private boolean rotationOwnedByOther(RotationController rotations, class_746 player) {
        RotationIntent intent = rotations.empty();
        return intent != null && intent.hasTarget() && intent.target() != player;
    }

    private boolean hasLineOfSight(class_746 player, class_2338 pos) {
        class_3959 context = new class_3959(player.method_33571(), this.blockCenter(pos), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)player);
        class_3965 hit = class_310.method_1551().field_1687.method_17742(context);
        return hit.method_17783() == class_239.class_240.field_1332 && hit.method_17777().equals((Object)pos);
    }

    private boolean unbreakable(class_2680 state, class_2338 pos) {
        return state.method_26204() == class_2246.field_9987 || state.method_26214((class_1922)class_310.method_1551().field_1687, pos) < 0.0f;
    }

    private void lookAt(class_746 player, class_2338 pos) {
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations == null || this.rotationOwnedByOther(rotations, player)) {
            return;
        }
        Angle angle = Angle.fromVectors(player.method_33571(), this.blockCenter(pos));
        rotations.process2(new RotationIntent((class_1309)player, null, angle, AttackUrgency.HIT, CorrectionMode.FREE, true), ROTATION_OWNER);
        this.rotating = true;
        Angle applied = rotations.getAngle();
        if (applied != null) {
            player.method_36456(applied.getYaw());
            player.method_36457(applied.getPitch());
        }
    }

    private void releaseLook() {
        if (!this.rotating) {
            return;
        }
        this.rotating = false;
        RotationController rotations = WexSideClient.getRotationController();
        class_746 player = class_310.method_1551().field_1724;
        if (rotations != null && rotations.isActive() && !this.rotationOwnedByOther(rotations, player)) {
            rotations.update3();
        }
    }
}

