/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  net.minecraft.class_7439
 */
package ru.wexside.module.player;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_7439;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.misc.Waypoint;
import ru.wexside.misc.WaypointStore;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.server.FunTimeServerContext;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class AutoEventModule
extends Module
implements ConfigSerializable {
    private static final String EVENT_DELAY_COMMAND = "event delay";
    private static final Pattern COORD_PATTERN = Pattern.compile("-?\\d+");
    private static final List<String> EVENT_NAMES = List.of("\u0430\u0438\u0440\u0434\u0440\u043e\u043f", "airdrop", "\u0442\u0430\u043d\u043a", "\u043c\u0435\u0442\u0435\u043e\u0440\u0438\u0442", "\u0441\u0445\u0440\u043e\u043d", "\u0431\u043e\u0441\u0441", "\u0441\u0442\u0440\u0430\u0436", "\u0434\u0440\u0430\u043a\u043e\u043d", "\u0438\u0432\u0435\u043d\u0442");
    private final BooleanSetting enabledSetting;
    private final BooleanSetting checkEvents;
    private final NumberSetting commandDelay;
    private final ConcurrentLinkedQueue<Waypoint> waypoints = new ConcurrentLinkedQueue();
    private final ElapsedTimer commandTimer = new ElapsedTimer();
    private volatile String pendingEvent;
    private volatile boolean sendDelayOnJoin;

    public AutoEventModule(EventBus eventBus) {
        super(eventBus, "auto_event", "Auto Event", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043e\u0442\u043c\u0435\u0447\u0430\u0435\u0442 \u0438\u0432\u0435\u043d\u0442\u044b FT \u0432\u0435\u0439\u043f\u043e\u0438\u043d\u0442\u0430\u043c\u0438", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043e\u0442\u043c\u0435\u0447\u0430\u0435\u0442 \u0438\u0432\u0435\u043d\u0442\u044b \u0432\u0435\u0439\u043f\u043e\u0438\u043d\u0442\u0430\u043c\u0438").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.checkEvents = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Check Events").id("check_events").description("\u041f\u0435\u0440\u0438\u043e\u0434\u0438\u0447\u0435\u0441\u043a\u0438 \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0442\u044c /event delay").aliases("check events", "\u043f\u0440\u043e\u0432\u0435\u0440\u043a\u0430 \u0438\u0432\u0435\u043d\u0442\u043e\u0432")).build();
        this.registerSetting(this.checkEvents);
        this.commandDelay = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 10.0).defaultValue(3.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Command Delay").id("command_delay").description("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0436\u0434\u0443 /event delay (\u0432 \u043c\u0438\u043d\u0443\u0442\u0430\u0445)").aliases("command delay", "\u0437\u0430\u0434\u0435\u0440\u0436\u043a\u0430").visibleWhen(this.checkEvents::isEnabled)).build();
        this.registerSetting(this.commandDelay);
    }

    @Override
    protected void initialize() {
        this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
        this.listen(ClientTickEvent.class, this::onTick);
        this.listen(WorldSessionEvent.class, event -> {
            this.sendDelayOnJoin = true;
        });
    }

    private void onIncomingPacket(IncomingPacketEvent event) {
        class_7439 chat;
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_2596<?> class_25962 = event.getPacket();
        if (!(class_25962 instanceof class_7439) || (chat = (class_7439)class_25962).comp_906()) {
            return;
        }
        class_2561 content = chat.comp_763();
        if (content == null) {
            return;
        }
        String message = content.getString().toLowerCase(Locale.ROOT);
        String eventName = this.matchEventName(message);
        if (eventName != null) {
            this.pendingEvent = eventName;
        }
        if (this.pendingEvent != null && this.hasCoordinates(message)) {
            int[] xyz = this.parseCoordinates(message);
            if (xyz != null) {
                this.waypoints.add(new Waypoint(this.pendingEvent, xyz[0], xyz[1], xyz[2]));
            }
            this.pendingEvent = null;
        }
    }

    private void onTick(ClientTickEvent event) {
        WaypointStore waypointsStore = WexSideClient.getWaypointStore();
        if (waypointsStore != null) {
            Waypoint waypoint;
            while ((waypoint = this.waypoints.poll()) != null) {
                waypointsStore.add(waypoint);
            }
        }
        if (!this.enabledSetting.isEnabled()) {
            this.sendDelayOnJoin = false;
            return;
        }
        class_634 network = class_310.method_1551().method_1562();
        if (network == null) {
            return;
        }
        if (this.sendDelayOnJoin) {
            this.sendDelayOnJoin = false;
            if (FunTimeServerContext.isConnected()) {
                network.method_45730(EVENT_DELAY_COMMAND);
            }
        }
        if (this.checkEvents.isEnabled() && FunTimeServerContext.isConnected() && this.commandTimer.process((long)this.commandDelay.getIntValue() * 60000L)) {
            network.method_45730(EVENT_DELAY_COMMAND);
            this.commandTimer.update();
        }
    }

    private String matchEventName(String message) {
        for (String name : EVENT_NAMES) {
            if (!message.contains(name.toLowerCase(Locale.ROOT))) continue;
            return name;
        }
        return null;
    }

    private int[] parseCoordinates(String message) {
        int found;
        Matcher matcher = COORD_PATTERN.matcher(message);
        int regionStart = message.indexOf("\u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442");
        if (regionStart >= 0) {
            matcher.region(regionStart, message.length());
        }
        int[] xyz = new int[3];
        for (found = 0; matcher.find() && found < 3; ++found) {
            try {
                xyz[found] = Integer.parseInt(matcher.group());
                continue;
            }
            catch (NumberFormatException ignored) {
                return null;
            }
        }
        return (int[])(found == 3 ? xyz : null);
    }

    private boolean hasCoordinates(String message) {
        return message.contains("\u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b") || message.contains("\u043d\u0430 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0430\u0445");
    }
}

