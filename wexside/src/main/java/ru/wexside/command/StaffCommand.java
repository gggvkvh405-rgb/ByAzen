/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import ru.wexside.WexSideClient;
import ru.wexside.command.Command;
import ru.wexside.command.CommandUsageException;
import ru.wexside.command.PlayerSuggestions;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.StaffNameStore;

public class StaffCommand
extends Command {
    public StaffCommand() {
        super("staff", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0441\u043f\u0438\u0441\u043a\u043e\u043c \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u0430", "staff", "staffs");
    }

    @Override
    public String getUsage() {
        return ".staff <add|remove|list|clear> [name]";
    }

    @Override
    public void execute(String ... stringArray) throws CommandUsageException {
        if (stringArray.length < 1) {
            throw new CommandUsageException(this);
        }
        StaffNameStore staffNameStore2 = WexSideClient.getInstance().getStaffNameStore();
        if (staffNameStore2 == null) {
            ClientChat.send("StaffService \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d");
            return;
        }
        switch (stringArray[0].toLowerCase()) {
            case "add": {
                if (stringArray.length < 2) {
                    throw new CommandUsageException(this);
                }
                if (staffNameStore2.add(stringArray[1])) {
                    String string = stringArray[1];
                    ClientChat.send("\u0414\u043e\u0431\u0430\u0432\u0438\u043b " + string + " \u0432 \u0441\u043f\u0438\u0441\u043e\u043a \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u0430");
                    break;
                }
                String string = stringArray[1];
                ClientChat.send(string + " \u0443\u0436\u0435 \u0432 \u0441\u043f\u0438\u0441\u043a\u0435 \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u0430");
                break;
            }
            case "remove": 
            case "del": 
            case "rm": {
                if (stringArray.length < 2) {
                    throw new CommandUsageException(this);
                }
                if (staffNameStore2.remove(stringArray[1])) {
                    String string = stringArray[1];
                    ClientChat.send("\u0423\u0434\u0430\u043b\u0438\u043b " + string + " \u0438\u0437 \u0441\u043f\u0438\u0441\u043a\u0430 \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u0430");
                    break;
                }
                String string = stringArray[1];
                ClientChat.send(string + " \u043d\u0435\u0442 \u0432 \u0441\u043f\u0438\u0441\u043a\u0435 \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u0430");
                break;
            }
            case "list": 
            case "ls": {
                Collection<String> collection = staffNameStore2.getNames();
                if (collection.isEmpty()) {
                    ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u0430 \u043f\u0443\u0441\u0442");
                    break;
                }
                String string = String.join((CharSequence)", ", collection);
                int n = collection.size();
                ClientChat.send("\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u043b (" + n + "): " + string);
                break;
            }
            case "clear": {
                staffNameStore2.clear();
                ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u0430 \u043e\u0447\u0438\u0449\u0435\u043d");
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
            return List.of("add", "remove", "list", "clear");
        }
        if (n == 1 && stringArray.length >= 1) {
            String string = stringArray[0].toLowerCase(Locale.ROOT);
            if (string.equals("add")) {
                return PlayerSuggestions.onlinePlayerNames();
            }
            if (string.equals("remove") || string.equals("del") || string.equals("rm")) {
                WexSideClient wexSideClient = WexSideClient.getInstance();
                StaffNameStore staffNameStore2 = wexSideClient == null ? null : wexSideClient.getStaffNameStore();
                return staffNameStore2 == null ? List.of() : new ArrayList<String>(staffNameStore2.getNames());
            }
        }
        return List.of();
    }
}

