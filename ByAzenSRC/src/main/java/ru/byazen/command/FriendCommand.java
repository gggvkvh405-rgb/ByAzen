/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import ru.byazen.ByAzenClient;
import ru.byazen.command.Command;
import ru.byazen.command.CommandUsageException;
import ru.byazen.command.PlayerSuggestions;
import ru.byazen.misc.ClientChat;
import ru.byazen.misc.FriendList;

public class FriendCommand
extends Command {
    public FriendCommand() {
        super("friend", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0441\u043f\u0438\u0441\u043a\u043e\u043c \u0434\u0440\u0443\u0437\u0435\u0439", "friend", "fr", "frnd");
    }

    @Override
    public String getUsage() {
        return ".friend <add|remove|list|clear> [name]";
    }

    @Override
    public void execute(String ... stringArray) throws CommandUsageException {
        if (stringArray.length < 1) {
            throw new CommandUsageException(this);
        }
        FriendList friendList2 = ByAzenClient.getFriends();
        if (friendList2 == null) {
            ClientChat.send("FriendService \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d");
            return;
        }
        switch (stringArray[0].toLowerCase()) {
            case "add": {
                if (stringArray.length < 2) {
                    throw new CommandUsageException(this);
                }
                if (friendList2.add(stringArray[1])) {
                    String string = stringArray[1];
                    ClientChat.send("\u0414\u043e\u0431\u0430\u0432\u0438\u043b " + string + " \u0432 \u0434\u0440\u0443\u0437\u044c\u044f");
                    break;
                }
                String string = stringArray[1];
                ClientChat.send(string + " \u0443\u0436\u0435 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f\u0445");
                break;
            }
            case "remove": 
            case "del": 
            case "rm": {
                if (stringArray.length < 2) {
                    throw new CommandUsageException(this);
                }
                if (friendList2.remove(stringArray[1])) {
                    String string = stringArray[1];
                    ClientChat.send("\u0423\u0434\u0430\u043b\u0438\u043b " + string + " \u0438\u0437 \u0434\u0440\u0443\u0437\u0435\u0439");
                    break;
                }
                String string = stringArray[1];
                ClientChat.send(string + " \u043d\u0435 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f\u0445");
                break;
            }
            case "list": 
            case "ls": {
                Collection<String> collection = friendList2.getNames();
                if (collection.isEmpty()) {
                    ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u0434\u0440\u0443\u0437\u0435\u0439 \u043f\u0443\u0441\u0442");
                    break;
                }
                String string = String.join((CharSequence)", ", collection);
                int n = collection.size();
                ClientChat.send("\u0414\u0440\u0443\u0437\u044c\u044f (" + n + "): " + string);
                break;
            }
            case "clear": {
                friendList2.clear();
                ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u0434\u0440\u0443\u0437\u0435\u0439 \u043e\u0447\u0438\u0449\u0435\u043d");
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
                FriendList friendList2 = ByAzenClient.getFriends();
                return friendList2 == null ? List.of() : new ArrayList<String>(friendList2.getNames());
            }
        }
        return List.of();
    }
}

