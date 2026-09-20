/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1309
 *  net.minecraft.class_1661
 *  net.minecraft.class_2596
 *  net.minecraft.class_2828$class_2830
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  net.minecraft.class_746
 */
package ru.wexside.module.player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1309;
import net.minecraft.class_1661;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public class AntiAFKModule
extends Module
implements ConfigSerializable {
    private static final int SWAP_INTERVAL = 5;
    private static final int CYCLE_TICKS = 1200;
    private static final float YAW_STEP = 6.2f;
    private static final int JUMP_INTERVAL = 40;
    private static final int MESSAGE_INTERVAL = 400;
    private static final int FLAG_INTERVAL = 600;
    private static final String ROTATION_OWNER = "Simple";
    private static final String JUMP = "Jump";
    private static final String ROTATION = "Rotation";
    private static final String JUMP_ROTATION = "Jump+Rotation";
    private static final String MESSAGE = "Message";
    private static final String SWAP_SLOT = "Swap-Slot";
    private static final String ANTI_CHEAT_FLAG = "Anti-Cheat-Flag";
    private static final String STRAFE = "Strafe";
    private static final String CHAT_MESSAGE = "/fackatrongeralol";
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final MultiSelectSetting actions;
    private int tickCounter;
    private int rememberedSlot = -1;
    private float yawOffset;
    private boolean jumpHeld;
    private boolean strafeHeld;
    private boolean rotating;

    public AntiAFKModule(EventBus eventBus) {
        super(eventBus, "anti_afk", "Anti AFK", "\u041f\u0440\u0435\u0434\u043e\u0442\u0432\u0440\u0430\u0449\u0430\u0435\u0442 \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0437\u0430 \u0431\u0435\u0437\u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting actionsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options(JUMP, ROTATION, JUMP_ROTATION, MESSAGE, SWAP_SLOT, ANTI_CHEAT_FLAG, STRAFE).selectAll(false).optionListEnabled(false).name("Actions").id("actions").description("\u0414\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0434\u043b\u044f \u043f\u0440\u0435\u0434\u043e\u0442\u0432\u0440\u0430\u0449\u0435\u043d\u0438\u044f AFK").aliases("actions", "\u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f")).build();
        actionsSetting.setOptions(new String[0]);
        this.actions = actionsSetting;
        this.registerSetting(actionsSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onTick);
        this.listen(WorldSessionEvent.class, event -> this.reset());
    }

    private void onTick(ClientTickEvent event) {
        boolean rotateOrStrafe;
        class_746 player = class_310.method_1551().field_1724;
        if (!this.enabledSetting.isEnabled() || player == null || class_310.method_1551().field_1687 == null) {
            this.reset();
            return;
        }
        this.tickCounter = (this.tickCounter + 1) % 1200;
        List<String> selected = this.actions.getSelectedOptions();
        boolean jumpRotation = selected.contains(JUMP_ROTATION);
        boolean bl = rotateOrStrafe = selected.contains(ROTATION) || selected.contains(STRAFE) || jumpRotation;
        if (selected.contains(JUMP) || rotateOrStrafe) {
            if (this.tickCounter % 40 == 0) {
                class_310.method_1551().field_1690.field_1903.method_23481(true);
                this.jumpHeld = true;
            } else {
                this.releaseJump();
            }
        } else {
            this.releaseJump();
        }
        if (jumpRotation) {
            this.spin(player);
        } else {
            this.releaseLook();
        }
        if (selected.contains(MESSAGE) && this.tickCounter % 400 == 0) {
            this.sendMessage(player);
        }
        if (selected.contains(SWAP_SLOT)) {
            this.swapSlot(player);
        } else {
            this.restoreSlot();
        }
        if (selected.contains(ANTI_CHEAT_FLAG) && this.tickCounter % 600 == 0) {
            this.sendFlagPacket(player);
        }
        if (selected.contains(STRAFE)) {
            class_310.method_1551().field_1690.field_1913.method_23481(true);
            this.strafeHeld = true;
        } else {
            this.releaseStrafe();
        }
    }

    private void reset() {
        this.tickCounter = 0;
        this.yawOffset = 0.0f;
        this.releaseAll();
    }

    private void releaseAll() {
        this.restoreSlot();
        this.releaseStrafe();
        this.releaseJump();
        this.releaseLook();
    }

    private void sendFlagPacket(class_746 player) {
        class_634 network = player.field_3944;
        if (network == null) {
            return;
        }
        Angle angle = this.currentAngle(player);
        network.method_52787((class_2596)new class_2828.class_2830(player.method_23317() + 1.0, player.method_23318() + 1.0, player.method_23321() + 1.0, angle.getYaw(), angle.getPitch(), true, false));
    }

    private void swapSlot(class_746 player) {
        class_1661 inventory = player.method_31548();
        if (this.rememberedSlot == -1) {
            this.rememberedSlot = inventory.method_67532();
        }
        if (this.tickCounter % 5 == 0) {
            inventory.method_61496(ThreadLocalRandom.current().nextInt(9));
        }
    }

    private void sendMessage(class_746 player) {
        class_634 network = player.field_3944;
        if (network == null) {
            return;
        }
        if (CHAT_MESSAGE.startsWith("/")) {
            network.method_45730(CHAT_MESSAGE.substring(1));
        } else {
            network.method_45729(CHAT_MESSAGE);
        }
    }

    private void restoreSlot() {
        if (this.rememberedSlot == -1) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player != null) {
            player.method_31548().method_61496(this.rememberedSlot);
        }
        this.rememberedSlot = -1;
    }

    private boolean rotationOwnedByOther(RotationController rotations, class_746 player) {
        RotationIntent intent = rotations.empty();
        return intent != null && intent.hasTarget() && intent.target() != player;
    }

    private void spin(class_746 player) {
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations == null || this.rotationOwnedByOther(rotations, player)) {
            return;
        }
        this.yawOffset = (this.yawOffset + 6.2f) % 360.0f;
        rotations.process2(new RotationIntent((class_1309)player, null, new Angle(this.yawOffset, player.method_36455()), AttackUrgency.HIT, CorrectionMode.FREE, true), ROTATION_OWNER);
        this.rotating = true;
        Angle applied = rotations.getAngle();
        if (applied != null) {
            player.method_36456(applied.getYaw());
        }
    }

    private Angle currentAngle(class_746 player) {
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations != null && rotations.isActive() && rotations.getAngle() != null) {
            return rotations.getAngle();
        }
        return new Angle(player.method_36454(), player.method_36455());
    }

    private void releaseJump() {
        if (!this.jumpHeld) {
            return;
        }
        class_310.method_1551().field_1690.field_1903.method_23481(false);
        this.jumpHeld = false;
    }

    private void releaseStrafe() {
        if (!this.strafeHeld) {
            return;
        }
        class_310.method_1551().field_1690.field_1913.method_23481(false);
        this.strafeHeld = false;
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

