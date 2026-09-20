/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_310
 *  net.minecraft.class_5894
 *  net.minecraft.class_642
 *  net.minecraft.class_7439
 *  net.minecraft.class_746
 */
package ru.wexside.module.player;

import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_5894;
import net.minecraft.class_642;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.misc.PasswordStore;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class AutoAuthModule
extends Module
implements ConfigSerializable {
    private static final String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final long PROMPT_DELAY_MS = 1500L;
    private static final long COMMAND_COOLDOWN_MS = 3000L;
    private static final int PASSWORD_LENGTH = 10;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting showPassword;
    private final ElapsedTimer commandCooldown = new ElapsedTimer();
    private final ElapsedTimer promptDelay = new ElapsedTimer();
    private final Queue<AuthAction> pending = new ConcurrentLinkedQueue<AuthAction>();
    private boolean waiting;
    private boolean pendingLogin;
    private boolean pendingRegister;

    public AutoAuthModule(EventBus eventBus) {
        super(eventBus, "auto_auth", "Auto Auth", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0430\u0432\u0442\u043e\u0440\u0438\u0437\u0443\u0435\u0442\u0441\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0430\u0432\u0442\u043e\u0440\u0438\u0437\u0443\u0435\u0442\u0441\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.showPassword = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Show Password").id("show_password").description("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u0441 \u043f\u0430\u0440\u043e\u043b\u0435\u043c \u0432 \u0447\u0430\u0442\u0435")).build();
        this.registerSetting(this.showPassword);
    }

    @Override
    protected void initialize() {
        this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
        this.listen(ClientTickEvent.class, event -> this.onTick());
        this.listen(WorldSessionEvent.class, event -> {
            if (this.waiting) {
                this.promptDelay.update();
            }
        });
    }

    private void onTick() {
        AuthAction action;
        if (!this.enabledSetting.isEnabled()) {
            this.reset();
            return;
        }
        while ((action = this.pending.poll()) != null) {
            if (action == AuthAction.REGISTER) {
                this.pendingRegister = true;
            } else {
                this.pendingLogin = true;
            }
            if (this.waiting) continue;
            this.waiting = true;
            this.promptDelay.update();
        }
        if (!this.waiting) {
            return;
        }
        if (!this.promptDelay.process(1500L) || !this.commandCooldown.process(3000L)) {
            return;
        }
        boolean register = this.pendingRegister;
        boolean login = this.pendingLogin;
        this.pendingRegister = false;
        this.pendingLogin = false;
        this.waiting = false;
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null) {
            return;
        }
        PasswordStore passwords = WexSideClient.getPasswordStore();
        if (passwords == null) {
            return;
        }
        String username = client.method_1548().method_1676();
        String server = this.serverAddress();
        if (register) {
            String password = passwords.getPassword(server, username);
            if (password == null || password.isEmpty()) {
                password = this.generatePassword();
                passwords.savePassword(server, username, password);
                if (this.showPassword.isEnabled()) {
                    ClientChat.send("\u0412\u0430\u0448 \u043f\u0430\u0440\u043e\u043b\u044c: \u00a7c" + password + "\u00a7r. \u0423\u0441\u043f\u0435\u0448\u043d\u043e \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d.");
                } else {
                    ClientChat.send("\u041f\u0430\u0440\u043e\u043b\u044c \u0441\u0433\u0435\u043d\u0435\u0440\u0438\u0440\u043e\u0432\u0430\u043d \u0438 \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d.");
                }
            }
            player.field_3944.method_45730("register " + password + " " + password);
            this.commandCooldown.update();
            return;
        }
        if (!login) {
            return;
        }
        String saved = passwords.getPassword(server, username);
        if (saved == null || saved.isEmpty()) {
            return;
        }
        player.field_3944.method_45730("login " + saved);
        this.commandCooldown.update();
    }

    private void onIncomingPacket(IncomingPacketEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        String message = this.extractMessage(event.getPacket());
        if (message == null) {
            return;
        }
        String lower = message.trim().toLowerCase(Locale.ROOT);
        if (lower.contains("/reg") || lower.contains("/register")) {
            this.pending.add(AuthAction.REGISTER);
        } else if (lower.contains("/login") || lower.contains("/l ")) {
            this.pending.add(AuthAction.LOGIN);
        }
    }

    private void reset() {
        this.pending.clear();
        this.pendingRegister = false;
        this.pendingLogin = false;
        this.waiting = false;
    }

    private String extractMessage(class_2596<?> packet) {
        class_2561 text = null;
        if (packet instanceof class_7439) {
            class_7439 chat = (class_7439)packet;
            text = chat.comp_763();
        } else if (packet instanceof class_5894) {
            class_5894 overlay = (class_5894)packet;
            text = overlay.comp_2279();
        }
        return text == null ? null : text.getString();
    }

    private String serverAddress() {
        class_642 server = class_310.method_1551().method_1558();
        return server == null || server.field_3761 == null ? "" : server.field_3761;
    }

    private String generatePassword() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder builder = new StringBuilder(10);
        for (int i = 0; i < 10; ++i) {
            builder.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return builder.toString();
    }

    private static enum AuthAction {
        REGISTER,
        LOGIN;

    }
}

