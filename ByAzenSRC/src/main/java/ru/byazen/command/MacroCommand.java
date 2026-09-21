/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_124
 */
package ru.byazen.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_124;
import ru.byazen.ByAzenClient;
import ru.byazen.command.Command;
import ru.byazen.command.CommandUsageException;
import ru.byazen.input.InputBindings;
import ru.byazen.misc.ClientChat;
import ru.byazen.misc.MacroDefinition;
import ru.byazen.misc.MacroManager;
import ru.byazen.misc.MacroType;

public final class MacroCommand
extends Command {
    private static final String string21 = " ";

    @Override
    public String getUsage() {
        return ".macro <add <chat|command> <name> <key> \"<message>\" | remove <name> | list | clear>";
    }

    @Override
    public void execute(String ... stringArray) throws CommandUsageException {
        if (stringArray.length < 1) {
            throw new CommandUsageException(this);
        }
        MacroManager macroManager2 = ByAzenClient.getMacroManager();
        if (macroManager2 == null) {
            ClientChat.send("MacroService \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d");
            return;
        }
        switch (stringArray[0].toLowerCase(Locale.ROOT)) {
            case "add": {
                if (stringArray.length < 5) {
                    throw new CommandUsageException(this);
                }
                MacroType macroType = this.parseType(stringArray[1]);
                if (macroType == null) {
                    String string = String.valueOf(class_124.field_1070);
                    String string2 = String.valueOf(class_124.field_1061);
                    String string3 = String.valueOf(class_124.field_1070);
                    String string4 = String.valueOf(class_124.field_1061);
                    ClientChat.send("\u0422\u0438\u043f \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u0442\u044c " + string4 + "chat" + string3 + " \u0438\u043b\u0438 " + string2 + "command" + string + ".");
                    return;
                }
                String string = stringArray[2];
                int n = InputBindings.keyCode(stringArray[3].toUpperCase(Locale.ROOT));
                if (n == -1) {
                    ClientChat.send("\u041a\u043d\u043e\u043f\u043a\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430.");
                    return;
                }
                String string5 = this.process2(stringArray, 4);
                if (string5 == null || string5.isBlank()) {
                    String string6 = String.valueOf(class_124.field_1070);
                    String string7 = String.valueOf(class_124.field_1061);
                    ClientChat.send("\u0421\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u0432 \u043a\u0430\u0432\u044b\u0447\u043a\u0430\u0445, \u043d\u0430\u043f\u0440\u0438\u043c\u0435\u0440 " + string7 + "\"im win ezz\"" + string6 + ".");
                    return;
                }
                if (macroManager2.process(string)) {
                    String string8 = String.valueOf(class_124.field_1070);
                    String string9 = string;
                    String string10 = String.valueOf(class_124.field_1061);
                    ClientChat.send("\u041c\u0430\u043a\u0440\u043e\u0441 " + string10 + string9 + string8 + " \u0443\u0436\u0435 \u0435\u0441\u0442\u044c \u0432 \u0441\u043f\u0438\u0441\u043a\u0435 \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432.");
                    return;
                }
                macroManager2.process3(string, string5, n, macroType);
                String string11 = this.describeType(macroType);
                String string12 = String.valueOf(class_124.field_1070);
                String string13 = string;
                String string14 = String.valueOf(class_124.field_1061);
                ClientChat.send("\u041c\u0430\u043a\u0440\u043e\u0441 " + string14 + string13 + string12 + " (" + string11 + ") \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u0432 \u0441\u043f\u0438\u0441\u043e\u043a \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432.");
                break;
            }
            case "remove": 
            case "del": 
            case "rm": {
                if (stringArray.length != 2) {
                    throw new CommandUsageException(this);
                }
                String string = stringArray[1];
                if (macroManager2.process2(string)) {
                    String string15 = String.valueOf(class_124.field_1070);
                    String string16 = string;
                    String string17 = String.valueOf(class_124.field_1061);
                    ClientChat.send("\u041c\u0430\u043a\u0440\u043e\u0441 " + string17 + string16 + string15 + " \u0443\u0431\u0440\u0430\u043d \u0438\u0437 \u0441\u043f\u0438\u0441\u043a\u0430 \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432.");
                    break;
                }
                String string18 = String.valueOf(class_124.field_1070);
                String string19 = string;
                String string20 = String.valueOf(class_124.field_1061);
                ClientChat.send("\u041c\u0430\u043a\u0440\u043e\u0441\u0430 " + string20 + string19 + string18 + " \u043d\u0435\u0442 \u0432 \u0441\u043f\u0438\u0441\u043a\u0435 \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432.");
                break;
            }
            case "clear": {
                macroManager2.update();
                ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432 \u043e\u0447\u0438\u0449\u0435\u043d.");
                break;
            }
            case "list": 
            case "ls": {
                List<MacroDefinition> list = macroManager2.getList();
                if (list.isEmpty()) {
                    ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432 \u043f\u0443\u0441\u0442.");
                    break;
                }
                int n = list.size();
                ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432 (" + n + "):");
                for (MacroDefinition macroDefinition : list) {
                    ClientChat.send(this.process4(macroDefinition));
                }
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
        if (stringArray.length < 1) {
            return List.of();
        }
        String string = stringArray[0].toLowerCase(Locale.ROOT);
        if (n == 1 && string.equals("add")) {
            return List.of("chat", "command");
        }
        if (n == 1 && (string.equals("remove") || string.equals("del") || string.equals("rm"))) {
            MacroManager macroManager2 = ByAzenClient.getMacroManager();
            if (macroManager2 == null) {
                return List.of();
            }
            ArrayList<String> arrayList = new ArrayList<String>();
            for (MacroDefinition macroDefinition : macroManager2.getList()) {
                arrayList.add(macroDefinition.getName());
            }
            return arrayList;
        }
        if (n == 3 && string.equals("add")) {
            return InputBindings.keyNames();
        }
        return List.of();
    }

    private MacroType parseType(String string) {
        if (string == null) {
            return null;
        }
        return switch (string.toLowerCase(Locale.ROOT)) {
            case "chat", "\u0447\u0430\u0442", "msg", "message" -> MacroType.CHAT;
            case "command", "cmd", "\u043a\u043e\u043c\u0430\u043d\u0434\u0430", "\u043a\u043e\u043c\u043c\u0430\u043d\u0434\u0430" -> MacroType.COMMAND;
            default -> null;
        };
    }

    private String process2(String[] stringArray, int n) {
        int n2;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = n; i < stringArray.length; ++i) {
            if (i > n) {
                stringBuilder.append(string21);
            }
            stringBuilder.append(stringArray[i]);
        }
        String string = stringBuilder.toString().trim();
        if (string.length() >= 2 && string.charAt(0) == '\"' && (n2 = string.lastIndexOf(34)) > 0) {
            return string.substring(1, n2);
        }
        return string;
    }

    private String describeType(MacroType macroType) {
        return macroType == MacroType.COMMAND ? "\u043a\u043e\u043c\u0430\u043d\u0434\u0430" : "\u0447\u0430\u0442";
    }

    private String process4(MacroDefinition macroDefinition) {
        String string = InputBindings.keyName(macroDefinition.getKeyCode());
        String string2 = macroDefinition.getMessage();
        String string3 = this.describeType(macroDefinition.getType());
        String string4 = string;
        String string5 = String.valueOf(class_124.field_1070);
        String string6 = macroDefinition.getName();
        String string7 = String.valueOf(class_124.field_1061);
        return string7 + string6 + string5 + " [" + string4 + "] (" + string3 + ") " + string2;
    }

    public MacroCommand() {
        super("macro", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043c\u0430\u043a\u0440\u043e\u0441\u0430\u043c\u0438 (\u0442\u0435\u043a\u0441\u0442/\u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u043f\u043e \u043a\u043d\u043e\u043f\u043a\u0435)", "macro", "macros");
    }
}

