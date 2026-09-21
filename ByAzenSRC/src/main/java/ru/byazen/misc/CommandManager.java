/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.ArrayList;
import java.util.List;
import ru.byazen.ByAzenClient;
import ru.byazen.command.BlockespCommand;
import ru.byazen.command.Command;
import ru.byazen.command.ConfigCommand;
import ru.byazen.command.FriendCommand;
import ru.byazen.command.GpsCommand;
import ru.byazen.command.HelpCommand;
import ru.byazen.command.MacroCommand;
import ru.byazen.command.ReconnectCommand;
import ru.byazen.command.SoundCommand;
import ru.byazen.command.StaffCommand;
import ru.byazen.command.WaypointCommand;
import ru.byazen.misc.ChatCommandDispatcher;

public class CommandManager {
    private ChatCommandDispatcher dispatcher;
    private final List<Command> commands = new ArrayList<Command>();

    public List<Command> getCommands() {
        return this.commands;
    }

    public void initializeCommands() {
        this.commands.clear();
        this.commands.add(new HelpCommand(this.commands));
        this.commands.add(new ConfigCommand(ByAzenClient.getConfigManager()));
        this.commands.add(new FriendCommand());
        this.commands.add(new BlockespCommand());
        this.commands.add(new StaffCommand());
        this.commands.add(new WaypointCommand());
        this.commands.add(new GpsCommand());
        this.commands.add(new MacroCommand());
        this.commands.add(new ReconnectCommand());
        this.commands.add(new SoundCommand());
        this.dispatcher = new ChatCommandDispatcher(ByAzenClient.getEventBus(), this.commands);
    }

    public ChatCommandDispatcher getChatCommandDispatcher() {
        return this.dispatcher;
    }
}

