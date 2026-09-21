/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_124
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package ru.byazen.command;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_124;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import ru.byazen.ByAzenClient;
import ru.byazen.command.Command;
import ru.byazen.command.CommandUsageException;
import ru.byazen.misc.BlockedSoundList;
import ru.byazen.misc.ClientChat;

public final class SoundCommand
extends Command {
    public SoundCommand() {
        super("sound", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0437\u0430\u0433\u043b\u0443\u0448\u0430\u0435\u043c\u044b\u043c\u0438 \u0437\u0432\u0443\u043a\u0430\u043c\u0438 (\u043a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0435/\u0441\u0435\u0440\u0432\u0435\u0440\u043d\u044b\u0435 id)", "sound", "sounds", "\u0437\u0432\u0443\u043a", "\u0437\u0432\u0443\u043a\u0438");
    }

    @Override
    public String getUsage() {
        return ".sound <add <id> | remove <id> | list | clear>";
    }

    @Override
    public void execute(String ... stringArray) throws CommandUsageException {
        if (stringArray.length < 1) {
            throw new CommandUsageException(this);
        }
        BlockedSoundList blockedSoundList2 = ByAzenClient.getBlockedSoundList();
        if (blockedSoundList2 == null) {
            ClientChat.send("SoundRemoverService \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d");
            return;
        }
        switch (stringArray[0].toLowerCase(Locale.ROOT)) {
            case "add": {
                if (stringArray.length != 2) {
                    throw new CommandUsageException(this);
                }
                String string = this.process(stringArray[1]);
                if (string == null) {
                    String string2 = stringArray[1];
                    String string3 = String.valueOf(class_124.field_1061);
                    ClientChat.send("\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 id \u0437\u0432\u0443\u043a\u0430: " + string3 + string2);
                    return;
                }
                boolean bl = blockedSoundList2.add(string);
                this.setString(string);
                if (bl) {
                    String string4 = String.valueOf(class_124.field_1070);
                    String string5 = string;
                    String string6 = String.valueOf(class_124.field_1061);
                    ClientChat.send("\u0417\u0432\u0443\u043a " + string6 + string5 + string4 + " \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u0432 \u0437\u0430\u0433\u043b\u0443\u0448\u0451\u043d\u043d\u044b\u0435.");
                    break;
                }
                String string7 = String.valueOf(class_124.field_1070);
                String string8 = string;
                String string9 = String.valueOf(class_124.field_1061);
                ClientChat.send("\u0417\u0432\u0443\u043a " + string9 + string8 + string7 + " \u0443\u0436\u0435 \u0437\u0430\u0433\u043b\u0443\u0448\u0451\u043d.");
                break;
            }
            case "remove": 
            case "del": 
            case "rm": {
                if (stringArray.length != 2) {
                    throw new CommandUsageException(this);
                }
                String string = this.process(stringArray[1]);
                if (string != null && blockedSoundList2.remove(string)) {
                    String string10 = String.valueOf(class_124.field_1070);
                    String string11 = string;
                    String string12 = String.valueOf(class_124.field_1061);
                    ClientChat.send("\u0417\u0432\u0443\u043a " + string12 + string11 + string10 + " \u0443\u0431\u0440\u0430\u043d \u0438\u0437 \u0437\u0430\u0433\u043b\u0443\u0448\u0451\u043d\u043d\u044b\u0445.");
                    break;
                }
                String string13 = String.valueOf(class_124.field_1070);
                String string14 = stringArray[1];
                String string15 = String.valueOf(class_124.field_1061);
                ClientChat.send("\u0417\u0432\u0443\u043a\u0430 " + string15 + string14 + string13 + " \u043d\u0435\u0442 \u0432 \u0441\u043f\u0438\u0441\u043a\u0435.");
                break;
            }
            case "clear": {
                blockedSoundList2.clear();
                ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u0437\u0430\u0433\u043b\u0443\u0448\u0451\u043d\u043d\u044b\u0445 \u0437\u0432\u0443\u043a\u043e\u0432 \u043e\u0447\u0438\u0449\u0435\u043d.");
                break;
            }
            case "list": 
            case "ls": {
                List<String> list = blockedSoundList2.getBlockedSounds();
                if (list.isEmpty()) {
                    ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u0437\u0430\u0433\u043b\u0443\u0448\u0451\u043d\u043d\u044b\u0445 \u0437\u0432\u0443\u043a\u043e\u0432 \u043f\u0443\u0441\u0442.");
                    break;
                }
                int n = list.size();
                ClientChat.send("\u0417\u0430\u0433\u043b\u0443\u0448\u0451\u043d\u043d\u044b\u0435 \u0437\u0432\u0443\u043a\u0438 (" + n + "):");
                Iterator<String> iterator = list.iterator();
                while (iterator.hasNext()) {
                    String string;
                    String string16 = string = iterator.next();
                    String string17 = String.valueOf(class_124.field_1061);
                    ClientChat.send(string17 + string16);
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
        String string;
        if (n == 0) {
            return List.of("add", "remove", "list", "clear");
        }
        if (n == 1 && stringArray.length >= 1 && ((string = stringArray[0].toLowerCase(Locale.ROOT)).equals("remove") || string.equals("del") || string.equals("rm"))) {
            BlockedSoundList blockedSoundList2 = ByAzenClient.getBlockedSoundList();
            return blockedSoundList2 == null ? List.of() : blockedSoundList2.getBlockedSounds();
        }
        return List.of();
    }

    private String process(String string) {
        if (string == null || string.isBlank()) {
            return null;
        }
        class_2960 identifier = class_2960.method_12829((String)string.trim().toLowerCase(Locale.ROOT));
        return identifier == null ? null : identifier.toString();
    }

    private void setString(String string) {
        class_2960 identifier = class_2960.method_12829((String)string);
        if (identifier == null) {
            return;
        }
        class_310 mc = class_310.method_1551();
        mc.method_1483().method_4875(identifier, null);
    }
}

