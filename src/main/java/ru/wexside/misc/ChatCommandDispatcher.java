/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.misc;

import java.util.Arrays;
import java.util.List;
import ru.wexside.command.Command;
import ru.wexside.command.CommandUsageException;
import ru.wexside.event.EventBus;
import ru.wexside.event.OutgoingChatEvent;
import ru.wexside.misc.ClientChat;
import ru.wexside.module.misc.CommandCharacterModule;

public class ChatCommandDispatcher {
    private final List<Command> field12;

    public ChatCommandDispatcher(EventBus eventBus, List<Command> list) {
        eventBus.subscribe(OutgoingChatEvent.class, this::member5787);
        this.field12 = list;
    }

    public String getString() {
        return CommandCharacterModule.getString();
    }

    void member5787(OutgoingChatEvent gameEvent15) {
        String string;
        String string2 = gameEvent15.getString();
        if (!string2.startsWith(string = this.getString())) {
            return;
        }
        gameEvent15.update();
        String[] stringArray = string2.split(" ");
        for (Command command : this.field12) {
            String[] stringArray2 = command.getCommandAliases();
            int n = stringArray2.length;
            for (int i = 0; i < n; ++i) {
                String string5 = string;
                String string3 = stringArray2[i];
                String string4 = string3;
                if (!stringArray[0].equalsIgnoreCase(string5 + string4)) continue;
                try {
                    command.execute(Arrays.copyOfRange(stringArray, 1, stringArray.length));
                }
                catch (CommandUsageException iIililIlII2) {
                    String string6 = iIililIlII2.getMessage();
                    ClientChat.send("\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u0441\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441 \u043a\u043e\u043c\u0430\u043d\u0434\u044b: " + string6);
                }
                catch (RuntimeException runtimeException) {
                    String string7 = runtimeException.getMessage();
                    ClientChat.send("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b: " + string7);
                }
                return;
            }
        }
        ClientChat.send("\u041a\u043e\u043c\u0430\u043d\u0434\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430.");
    }
}

