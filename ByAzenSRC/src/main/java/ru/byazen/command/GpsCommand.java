/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.command;

import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.command.Command;
import ru.byazen.command.CommandUsageException;
import ru.byazen.misc.ClientChat;
import ru.byazen.misc.Gps;

public final class GpsCommand
extends Command {
    public GpsCommand() {
        super("gps", "GPS-\u0441\u0442\u0440\u0435\u043b\u043a\u0430 \u043a \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0430\u043c", "gps", "navigation");
    }

    @Override
    public String getUsage() {
        return ".gps <x> <z>  |  .gps (\u0431\u0435\u0437 \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432 \u2014 \u043e\u0442\u043a\u043b\u044e\u0447\u0438\u0442\u044c)";
    }

    @Override
    public void execute(String ... args) throws CommandUsageException {
        if (args.length == 0) {
            Gps.clear();
            ClientChat.send("GPS \u043e\u0442\u043a\u043b\u044e\u0447\u0451\u043d");
            return;
        }
        if (args.length != 2) {
            throw new CommandUsageException(this);
        }
        try {
            int x = Integer.parseInt(args[0].trim());
            int z = Integer.parseInt(args[1].trim());
            Gps.set(x, z);
            ClientChat.send("GPS \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d: " + x + ", " + z);
        }
        catch (NumberFormatException exception) {
            throw new CommandUsageException(this);
        }
    }

    @Override
    public List<String> complete(int index, String[] args) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return List.of();
        }
        if (index == 0) {
            return List.of(String.valueOf(player.method_31477()));
        }
        if (index == 1) {
            return List.of(String.valueOf(player.method_31479()));
        }
        return List.of();
    }
}

