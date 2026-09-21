/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 */
package ru.byazen.command;

import net.minecraft.class_310;
import net.minecraft.class_634;
import ru.byazen.ByAzenClient;
import ru.byazen.command.Command;
import ru.byazen.command.CommandUsageException;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.ClientChat;
import ru.byazen.misc.ElapsedTimer;
import ru.byazen.server.FunTimeServerContext;

public final class ReconnectCommand
extends Command {
    private static final long HUB_DELAY_MS = 1000L;
    private static final long TIMEOUT_MS = 30000L;
    private final ElapsedTimer reconnectTimer = new ElapsedTimer();
    private volatile boolean reconnecting;
    private volatile int anarchyNumber = -1;

    public ReconnectCommand() {
        super("RCT", "\u0411\u044b\u0441\u0442\u0440\u044b\u0439 \u043f\u0435\u0440\u0435\u0437\u0430\u0445\u043e\u0434 \u043d\u0430 \u0442\u0443 \u0436\u0435 \u0410\u043d\u0430\u0440\u0445\u0438\u044e FT", "rct", "reconnect", "\u0440\u0435\u043a\u043e\u043d\u043d\u0435\u043a\u0442", "\u0440\u043a\u0442");
        EventBus eventBus = ByAzenClient.getEventBus();
        if (eventBus != null) {
            eventBus.subscribe(ClientTickEvent.class, this::onClientTick);
        }
    }

    @Override
    public String getUsage() {
        return ".rct";
    }

    @Override
    public void execute(String ... stringArray) throws CommandUsageException {
        if (!FunTimeServerContext.isConnected()) {
            ClientChat.send("\u041a \u0441\u043e\u0436\u0430\u043b\u0435\u043d\u0438\u044e .rct \u043d\u0435 \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u043d\u0430 \u044d\u0442\u043e\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u0435.");
            return;
        }
        int currentAnarchy = FunTimeServerContext.getAnarchyNumber();
        if (currentAnarchy == -1) {
            ClientChat.send("\u0412\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435 \u0410\u043d\u0430\u0440\u0445\u0438\u0438.");
            return;
        }
        if (FunTimeServerContext.isPvpLocked()) {
            ClientChat.send("\u0412\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0435\u0441\u044c \u0432 \u0440\u0435\u0436\u0438\u043c\u0435 PVP.");
            return;
        }
        ClientChat.send("\u041f\u0435\u0440\u0435\u0437\u0430\u0445\u043e\u0436\u0443..");
        this.anarchyNumber = currentAnarchy;
        this.reconnectTimer.update();
        this.reconnecting = true;
        this.sendCommand("hub");
    }

    private void onClientTick(ClientTickEvent event) {
        if (!this.reconnecting) {
            return;
        }
        if (this.reconnectTimer.process(30000L)) {
            this.reconnecting = false;
            ClientChat.send("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0435\u0440\u0435\u0437\u0430\u0439\u0442\u0438 (\u0442\u0430\u0439\u043c\u0430\u0443\u0442).");
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.method_1562() == null) {
            return;
        }
        if (this.reconnectTimer.process(1000L) && FunTimeServerContext.isOnHub()) {
            this.sendCommand("an" + this.anarchyNumber);
            this.reconnecting = false;
        }
    }

    private void sendCommand(String command) {
        class_634 networkHandler = class_310.method_1551().method_1562();
        if (networkHandler != null) {
            networkHandler.method_45730(command);
        }
    }
}

