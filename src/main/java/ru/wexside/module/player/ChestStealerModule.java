/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_124
 *  net.minecraft.class_1263
 *  net.minecraft.class_1657
 *  net.minecraft.class_1703
 *  net.minecraft.class_1707
 *  net.minecraft.class_1713
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_442
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 */
package ru.wexside.module.player;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_124;
import net.minecraft.class_1263;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.server.FunTimeServerContext;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class ChestStealerModule
extends Module
implements ConfigSerializable {
    private static final int MISS_PERIOD = 30;
    private static final int MISS_JITTER = 15;
    private static final double MISS_CHANCE = 0.1;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting random;
    private final BooleanSetting missSlots;
    private final BooleanSetting closeIfEmpty;
    private final BooleanSetting leaveAfterLoot;
    private final NumberSetting delay;
    private final ElapsedTimer delayTimer = new ElapsedTimer();
    private int tickCounter;

    public ChestStealerModule(EventBus eventBus) {
        super(eventBus, "chest_stealer", "Chest Stealer", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0437\u0430\u0431\u0438\u0440\u0430\u0435\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0438\u0437 \u043e\u0442\u043a\u0440\u044b\u0442\u044b\u0445 \u0441\u0443\u043d\u0434\u0443\u043a\u043e\u0432", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.random = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Random").id("random").description("\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439 \u043f\u043e\u0440\u044f\u0434\u043e\u043a \u0438 \u0434\u0436\u0438\u0442\u0442\u0435\u0440 \u0437\u0430\u0434\u0435\u0440\u0436\u043a\u0438").aliases("random", "\u0440\u0430\u043d\u0434\u043e\u043c")).build();
        this.registerSetting(this.random);
        this.missSlots = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Miss Slots").id("miss_slots").description("\u0418\u043d\u043e\u0433\u0434\u0430 \u043f\u0440\u043e\u043c\u0430\u0445\u0438\u0432\u0430\u0442\u044c\u0441\u044f \u043f\u043e \u043f\u0443\u0441\u0442\u044b\u043c \u0441\u043b\u043e\u0442\u0430\u043c").aliases("miss slots", "\u043f\u0440\u043e\u043c\u0430\u0445\u0438")).build();
        this.registerSetting(this.missSlots);
        this.closeIfEmpty = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Close If Empty").id("close_if_empty").description("\u0417\u0430\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u0441\u0443\u043d\u0434\u0443\u043a \u043a\u043e\u0433\u0434\u0430 \u043e\u043d \u043f\u0443\u0441\u0442").aliases("close if empty", "\u0437\u0430\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u043f\u0443\u0441\u0442\u043e\u0439")).build();
        this.registerSetting(this.closeIfEmpty);
        this.leaveAfterLoot = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Leave After Loot").id("leave_after_loot").description("\u0412\u044b\u0445\u043e\u0434\u0438\u0442\u044c \u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u043f\u043e\u0441\u043b\u0435 \u043b\u0443\u0442\u0430").aliases("leave after loot", "\u0432\u044b\u0445\u043e\u0434\u0438\u0442\u044c \u043f\u043e\u0441\u043b\u0435 \u043b\u0443\u0442\u0430")).build();
        this.registerSetting(this.leaveAfterLoot);
        this.delay = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 500.0).defaultValue(50.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Delay").id("delay").description("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0436\u0434\u0443 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430\u043c\u0438 (\u043c\u0441)").aliases("delay", "\u0437\u0430\u0434\u0435\u0440\u0436\u043a\u0430")).build();
        this.registerSetting(this.delay);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        int slot;
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (!this.enabledSetting.isEnabled() || player == null || client.field_1687 == null) {
            return;
        }
        class_1703 handler = player.field_7512;
        if (!(handler instanceof class_1707)) {
            return;
        }
        class_1707 container = (class_1707)handler;
        class_1263 chest = container.method_7629();
        this.tickCounter = (this.tickCounter + 1) % 600;
        if (player.method_31548().method_7376() == -1) {
            ClientChat.send((class_2561)class_2561.method_43470((String)"\u0421\u0443\u043d\u0434\u0443\u043a \u0437\u0430\u043a\u0440\u044b\u0442: \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c \u043f\u043e\u043b\u043e\u043d.").method_27692(class_124.field_1061));
            this.enabledSetting.setEnabled(false);
            player.method_7346();
            return;
        }
        if (chest.method_5442()) {
            if (this.closeIfEmpty.isEnabled()) {
                player.method_7346();
            }
            if (this.leaveAfterLoot.isEnabled() && !FunTimeServerContext.isPvpLocked()) {
                this.leaveServer();
            }
            return;
        }
        long wait = this.delay.getIntValue();
        if (this.random.isEnabled()) {
            wait += (long)ThreadLocalRandom.current().nextInt(15);
        }
        if (this.delayTimer.process(wait) && (slot = this.nextFilledSlot(chest)) != -1) {
            this.click(container.field_7763, slot, class_1713.field_7794, player);
            this.delayTimer.update();
        }
        if (this.missSlots.isEnabled()) {
            this.maybeMiss(container, chest, player);
        }
    }

    private void leaveServer() {
        this.enabledSetting.setEnabled(false);
        class_310 client = class_310.method_1551();
        client.method_72099();
        client.method_1507((class_437)new class_442());
    }

    private int nextFilledSlot(class_1263 chest) {
        int size = chest.method_5439();
        if (!this.random.isEnabled()) {
            for (int i = 0; i < size; ++i) {
                if (chest.method_5438(i).method_7960()) continue;
                return i;
            }
            return -1;
        }
        ArrayList<Integer> filled = new ArrayList<Integer>();
        for (int i = 0; i < size; ++i) {
            if (chest.method_5438(i).method_7960()) continue;
            filled.add(i);
        }
        if (filled.isEmpty()) {
            return -1;
        }
        return (Integer)filled.get(ThreadLocalRandom.current().nextInt(filled.size()));
    }

    private void click(int member9959, int slot, class_1713 action, class_746 player) {
        class_636 interactions = class_310.method_1551().field_1761;
        if (interactions != null) {
            interactions.method_2906(member9959, slot, 0, action, (class_1657)player);
        }
    }

    private void maybeMiss(class_1707 container, class_1263 chest, class_746 player) {
        if (this.tickCounter % 30 != 0) {
            return;
        }
        int size = chest.method_5439();
        for (int i = 0; i < size; ++i) {
            if (!chest.method_5438(i).method_7960() || ThreadLocalRandom.current().nextDouble() >= 0.1) continue;
            this.click(container.field_7763, i, class_1713.field_7790, player);
            return;
        }
    }
}

