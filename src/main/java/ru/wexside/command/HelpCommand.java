/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.command;

import java.util.List;
import ru.wexside.command.Command;
import ru.wexside.command.CommandUsageException;
import ru.wexside.misc.ClientChat;

public final class HelpCommand
extends Command {
    private final List<Command> field24;

    public HelpCommand(List<Command> list) {
        super("help", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0441\u043f\u0438\u0441\u043e\u043a \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0445 \u043a\u043e\u043c\u0430\u043d\u0434", "help");
        this.field24 = list;
    }

    @Override
    public String getUsage() {
        return ".help";
    }

    @Override
    public void execute(String ... stringArray) throws CommandUsageException {
        ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0445 \u043a\u043e\u043c\u0430\u043d\u0434:");
        for (Command command : this.field24) {
            String string = command.getCommandAliases().length == 0 ? command.getCommandName() : command.getCommandAliases()[0];
            String string2 = command.getCommandDescription();
            String string3 = string;
            ClientChat.send("." + string3 + " - " + string2);
        }
    }
}

