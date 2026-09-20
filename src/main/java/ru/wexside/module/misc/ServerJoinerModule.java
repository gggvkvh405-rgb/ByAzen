/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_124
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_2678
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_636
 *  net.minecraft.class_7439
 *  net.minecraft.class_746
 */
package ru.wexside.module.misc;

import java.util.Locale;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2678;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_636;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.ClickSlotAction;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.misc.HotbarSelectAction;
import ru.wexside.misc.Inventories;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.InventoryController;

public final class ServerJoinerModule
extends Module
implements ConfigSerializable {
    private static final String SERVER_SPOOKY_TIME = "SpookyTime";
    private static final String SERVER_REALLY_WORLD = "ReallyWorld";
    private static final String MODULE_OWNER = "server_joiner";
    private static final long GRIEF_CLICK_DELAY_MS = 150L;
    private static final long REALLY_WORLD_RETRY_MS = 500L;
    private static final long REALLY_WORLD_QUEUE_MS = 5000L;
    private final ElapsedTimer clickTimer = new ElapsedTimer();
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final ModeSetting server;
    private final NumberSetting griefNumber;
    private volatile long reallyWorldRetryAtMs;
    private boolean firstTick = true;

    public ServerJoinerModule(EventBus eventBus) {
        super(eventBus, MODULE_OWNER, "Server Joiner", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0432\u0445\u043e\u0434 \u043d\u0430 \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u044b\u0439 \u0441\u0435\u0440\u0432\u0435\u0440: SpookyTime \u0434\u0443\u044d\u043b\u0438 / ReallyWorld \u0433\u0440\u0438\u0444", ModuleCategory.valueOf("MISC"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.server = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options(SERVER_SPOOKY_TIME, SERVER_REALLY_WORLD).defaultOption(SERVER_SPOOKY_TIME).name("\u0421\u0435\u0440\u0432\u0435\u0440").id("server").description("\u0421\u0435\u0440\u0432\u0435\u0440 \u0434\u043b\u044f \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u043e\u0433\u043e \u0432\u0445\u043e\u0434\u0430")).build();
        this.registerSetting(this.server);
        this.griefNumber = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 42.0).defaultValue(1.0).multiplier(1.0).precision(0).animationSpeed(20.0f).snapTo(1.0).name("\u041d\u043e\u043c\u0435\u0440 \u0433\u0440\u0438\u0444\u0430").id("grief_number").description("\u041d\u043e\u043c\u0435\u0440 \u0433\u0440\u0438\u0444-\u0441\u0435\u0440\u0432\u0435\u0440\u0430 ReallyWorld").visibleWhen(() -> SERVER_REALLY_WORLD.equals(this.server.getSelectedOption()))).build();
        this.registerSetting(this.griefNumber);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onClientTick);
        this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
        this.listen(WorldSessionEvent.class, this::onWorldChange);
    }

    private void onIncomingPacket(IncomingPacketEvent event) {
        if (!this.enabledSetting.isEnabled() || !SERVER_REALLY_WORLD.equals(this.server.getSelectedOption())) {
            return;
        }
        class_2596<?> packet = event.getPacket();
        if (packet instanceof class_2678) {
            this.enabledSetting.setEnabled(false);
            return;
        }
        String message = this.extractMessage(packet);
        if (message == null) {
            return;
        }
        boolean waitMessage = message.contains("\u041f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435");
        boolean retryMessage = waitMessage || message.contains("\u041a \u0441\u043e\u0436\u0430\u043b\u0435\u043d\u0438\u044e \u0441\u0435\u0440\u0432\u0435\u0440 \u043f\u0435\u0440\u0435\u043f\u043e\u043b\u043d\u0435\u043d") || message.contains("\u0431\u043e\u043b\u044c\u0448\u043e\u0439 \u043f\u043e\u0442\u043e\u043a \u0438\u0433\u0440\u043e\u043a\u043e\u0432") || message.contains("\u0421\u0435\u0440\u0432\u0435\u0440 \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0430\u0435\u0442\u0441\u044f");
        boolean bl = retryMessage;
        if (!retryMessage) {
            return;
        }
        this.reallyWorldRetryAtMs = waitMessage ? System.currentTimeMillis() + 5000L : System.currentTimeMillis();
    }

    private void onClientTick(ClientTickEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            this.firstTick = true;
            this.reallyWorldRetryAtMs = 0L;
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null) {
            return;
        }
        boolean initialTick = this.firstTick;
        this.firstTick = false;
        if (SERVER_REALLY_WORLD.equals(this.server.getSelectedOption())) {
            this.handleReallyWorld(initialTick);
        } else {
            this.handleSpookyTime();
        }
    }

    private void handleSpookyTime() {
        class_465 handledScreen;
        class_437 screen;
        class_310 client = class_310.method_1551();
        if (client.field_1755 == null) {
            this.selectHotbarItem("\u0432\u044b\u0431\u043e\u0440 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", true);
        }
        if ((screen = client.field_1755) instanceof class_465 && (handledScreen = (class_465)screen).method_25440().getString().contains("\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0440\u0435\u0436\u0438\u043c:")) {
            this.clickModeSelectorSlot();
        }
        if (this.findHotbarSlot("\u0432\u0445\u043e\u0434 \u0432 \u043e\u0447\u0435\u0440\u0435\u0434\u044c") != -1) {
            this.enabledSetting.setEnabled(false);
        }
    }

    private void handleReallyWorld(boolean initialTick) {
        if (initialTick) {
            this.selectCompassSlot();
            this.clickTimer.update();
            return;
        }
        if (this.reallyWorldRetryAtMs != 0L && System.currentTimeMillis() >= this.reallyWorldRetryAtMs) {
            this.reallyWorldRetryAtMs = 0L;
            this.selectCompassSlot();
            this.clickTimer.update();
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null) {
            return;
        }
        if (client.field_1755 == null) {
            if (player.field_6012 < 5 && this.clickTimer.process(500L)) {
                this.selectCurrentHotbarSlot();
                this.clickTimer.update();
            }
            return;
        }
        if (client.field_1755 instanceof class_465) {
            this.clickMatchingGriefSlot(player);
        }
    }

    private void clickMatchingGriefSlot(class_746 player) {
        int griefNumber = this.griefNumber.getIntValue();
        for (int slotIndex = 0; slotIndex < player.field_7512.field_7761.size(); ++slotIndex) {
            String name;
            class_1735 slot = (class_1735)player.field_7512.field_7761.get(slotIndex);
            class_1799 stack = slot.method_7677();
            if (stack.method_7960() || !(name = stack.method_7964().getString()).contains("\u0413\u0420\u0418\u0424\u0415\u0420\u0421\u041a\u041e\u0415 \u0412\u042b\u0416\u0418\u0412\u0410\u041d\u0418\u0415") && !this.matchesGriefNumber(name, griefNumber) || !this.clickTimer.process(150L)) continue;
            this.clickContainerSlot(slotIndex);
            this.clickTimer.update();
        }
    }

    private boolean matchesGriefNumber(String name, int griefNumber) {
        String marker = "\u0413\u0420\u0418\u0424 #" + griefNumber;
        int index = name.indexOf(marker);
        if (index == -1) {
            return false;
        }
        int afterMarker = index + marker.length();
        if (afterMarker >= name.length()) {
            return true;
        }
        return !Character.isDigit(name.charAt(afterMarker));
    }

    private void clickModeSelectorSlot() {
        class_746 player = class_310.method_1551().field_1724;
        class_636 interactionManager = class_310.method_1551().field_1761;
        if (player == null || interactionManager == null) {
            return;
        }
        interactionManager.method_2906(player.field_7512.field_7763, 13, 0, class_1713.field_7790, (class_1657)player);
    }

    private String extractMessage(class_2596<?> packet) {
        class_2561 text = null;
        if (packet instanceof class_7439) {
            class_7439 chatPacket = (class_7439)packet;
            if (chatPacket.comp_906()) {
                return null;
            }
            text = chatPacket.comp_763();
        }
        if (text == null) {
            return null;
        }
        return class_124.method_539((String)text.getString());
    }

    private void selectCompassSlot() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        int slot = Inventories.findHotbarSlot(class_1802.field_8251);
        if (slot == -1) {
            slot = player.method_31548().method_67532();
        }
        this.selectHotbarSlot(slot, false);
    }

    private void selectCurrentHotbarSlot() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        this.selectHotbarSlot(player.method_31548().method_67532(), false);
    }

    private void selectHotbarItem(String namePart, boolean rightClick) {
        int slot = this.findHotbarSlot(namePart);
        if (slot == -1) {
            return;
        }
        this.selectHotbarSlot(slot, rightClick);
    }

    private void selectHotbarSlot(int slot, boolean rightClick) {
        InventoryController member6090 = WexSideClient.getInventoryController();
        if (member6090 == null) {
            return;
        }
        member6090.submit(InventoryTask.builder().action(new HotbarSelectAction(slot, rightClick)).owner(MODULE_OWNER).flag2(TaskFlag.DEFAULT).policy(ClickPolicy.SILENT).priority(TaskPriority.NORMAL).build());
    }

    private void clickContainerSlot(int slot) {
        InventoryController member6090 = WexSideClient.getInventoryController();
        if (member6090 == null) {
            return;
        }
        member6090.submit(InventoryTask.builder().action(new ClickSlotAction(slot, 0)).owner(MODULE_OWNER).flag2(TaskFlag.DEFAULT).policy(ClickPolicy.SILENT).priority(TaskPriority.NORMAL).build());
    }

    private int findHotbarSlot(String namePart) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return -1;
        }
        for (int slot = 0; slot < 9; ++slot) {
            class_1799 stack = player.method_31548().method_5438(slot);
            if (stack.method_7960() || !stack.method_7964().getString().toLowerCase(Locale.ROOT).contains(namePart)) continue;
            return slot;
        }
        return -1;
    }

    private void onWorldChange(WorldSessionEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || SERVER_REALLY_WORLD.equals(this.server.getSelectedOption())) {
            return;
        }
        if (client.field_1687.method_27983().method_29177().toString().contains(":spawn")) {
            this.enabledSetting.setEnabled(false);
        }
    }
}

