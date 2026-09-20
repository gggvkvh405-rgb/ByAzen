/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.wexside.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.command.Command;
import ru.wexside.command.CommandUsageException;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.Waypoint;
import ru.wexside.misc.WaypointStore;

public final class WaypointCommand
extends Command {
    public WaypointCommand() {
        super("waypoint", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0432\u0435\u0439\u043f\u043e\u0438\u043d\u0442\u0430\u043c\u0438", "waypoint", "way");
    }

    @Override
    public String getUsage() {
        return ".waypoint <add <name> <x> <y> <z> | remove <name> | clear>";
    }

    @Override
    public void execute(String ... stringArray) throws CommandUsageException {
        if (stringArray.length < 1) {
            throw new CommandUsageException(this);
        }
        WaypointStore waypointStore2 = WexSideClient.getWaypointStore();
        if (waypointStore2 == null) {
            ClientChat.send("WaypointService \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d");
            return;
        }
        switch (stringArray[0].toLowerCase(Locale.ROOT)) {
            case "add": {
                int n;
                int n2;
                int n3;
                if (stringArray.length != 5) {
                    throw new CommandUsageException(this);
                }
                String string = stringArray[1];
                try {
                    n3 = Integer.parseInt(stringArray[2]);
                    n2 = Integer.parseInt(stringArray[3]);
                    n = Integer.parseInt(stringArray[4]);
                }
                catch (NumberFormatException numberFormatException) {
                    throw new CommandUsageException(this);
                }
                waypointStore2.add(new Waypoint(string, n3, n2, n));
                int n4 = n;
                int n5 = n2;
                int n6 = n3;
                String string2 = string;
                ClientChat.send("\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442 " + string2 + " \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d (" + n6 + ", " + n5 + ", " + n4 + ")");
                break;
            }
            case "remove": 
            case "del": 
            case "rm": {
                if (stringArray.length != 2) {
                    throw new CommandUsageException(this);
                }
                String string = stringArray[1];
                if (waypointStore2.removeByName(string)) {
                    String string3 = string;
                    ClientChat.send("\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442 " + string3 + " \u0443\u0434\u0430\u043b\u0451\u043d");
                    break;
                }
                String string4 = string;
                ClientChat.send("\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442 " + string4 + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                break;
            }
            case "clear": {
                waypointStore2.clear();
                ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u0432\u0435\u0439\u043f\u043e\u0438\u043d\u0442\u043e\u0432 \u043e\u0447\u0438\u0449\u0435\u043d");
                break;
            }
            default: {
                throw new CommandUsageException(this);
            }
        }
    }

    @Override
    public List<String> complete(int n, String[] stringArray) {
        if (n == 0) {
            return List.of("add", "remove", "clear");
        }
        if (stringArray.length < 1) {
            return List.of();
        }
        String string = stringArray[0].toLowerCase(Locale.ROOT);
        if (n == 1 && (string.equals("remove") || string.equals("del") || string.equals("rm"))) {
            WaypointStore waypointStore2 = WexSideClient.getWaypointStore();
            if (waypointStore2 == null) {
                return List.of();
            }
            ArrayList<String> arrayList = new ArrayList<String>();
            for (Waypoint waypoint2 : waypointStore2.getWaypoints()) {
                arrayList.add(waypoint2.name());
            }
            return arrayList;
        }
        if (string.equals("add") && n >= 2 && n <= 4) {
            class_746 player2 = class_310.method_1551().field_1724;
            if (player2 == null) {
                return List.of();
            }
            int n2 = switch (n) {
                case 2 -> player2.method_31477();
                case 3 -> player2.method_31478();
                default -> player2.method_31479();
            };
            return List.of(String.valueOf(n2));
        }
        return List.of();
    }
}

