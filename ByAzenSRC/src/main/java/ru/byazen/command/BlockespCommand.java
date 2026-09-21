/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2960
 *  net.minecraft.class_7923
 */
package ru.byazen.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import ru.byazen.ByAzenClient;
import ru.byazen.command.Command;
import ru.byazen.command.CommandUsageException;
import ru.byazen.misc.BlockEspStore;
import ru.byazen.misc.ClientChat;

public final class BlockespCommand
extends Command {
    private static final int slot = -1;

    public BlockespCommand() {
        super("blockesp", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0441\u043f\u0438\u0441\u043a\u043e\u043c \u0431\u043b\u043e\u043a\u043e\u0432 \u0434\u043b\u044f Block ESP", "blockesp", "be");
    }

    @Override
    public String getUsage() {
        return ".blockesp <add <blockId> [hexColor] | remove <blockId> | list | clear>";
    }

    @Override
    public void execute(String ... stringArray) throws CommandUsageException {
        if (stringArray.length < 1) {
            throw new CommandUsageException(this);
        }
        BlockEspStore blockEspStore2 = ByAzenClient.getBlockEspStore();
        if (blockEspStore2 == null) {
            ClientChat.send("BlockEspService \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d");
            return;
        }
        switch (stringArray[0].toLowerCase(Locale.ROOT)) {
            case "add": {
                if (stringArray.length < 2) {
                    throw new CommandUsageException(this);
                }
                String string = this.process3(stringArray[1]);
                if (string == null) {
                    String string2 = stringArray[1];
                    ClientChat.send("\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u0431\u043b\u043e\u043a: " + string2);
                    return;
                }
                int n = -1;
                if (stringArray.length >= 3) {
                    Integer n2 = this.process(stringArray[2]);
                    if (n2 == null) {
                        String string3 = stringArray[2];
                        ClientChat.send("\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 hex-\u0446\u0432\u0435\u0442: " + string3 + " (\u043f\u0440\u0438\u043c\u0435\u0440\u044b: ff0000, ff00ff00)");
                        return;
                    }
                    n = n2;
                }
                boolean bl = blockEspStore2.contains(string);
                blockEspStore2.put(string, n);
                String string4 = this.process4(n);
                String string5 = string;
                String string6 = bl ? "\u041e\u0431\u043d\u043e\u0432\u0438\u043b" : "\u0414\u043e\u0431\u0430\u0432\u0438\u043b";
                ClientChat.send(string6 + " \u0431\u043b\u043e\u043a " + string5 + " (#" + string4 + ")");
                break;
            }
            case "remove": 
            case "del": 
            case "rm": {
                String string;
                if (stringArray.length < 2) {
                    throw new CommandUsageException(this);
                }
                String string7 = this.process3(stringArray[1]);
                String string8 = string = string7 != null ? string7 : stringArray[1].trim().toLowerCase(Locale.ROOT);
                if (blockEspStore2.remove(string)) {
                    String string9 = string;
                    ClientChat.send("\u0423\u0434\u0430\u043b\u0438\u043b \u0431\u043b\u043e\u043a " + string9);
                    break;
                }
                String string10 = string;
                ClientChat.send(string10 + " \u043d\u0435 \u0432 \u0441\u043f\u0438\u0441\u043a\u0435");
                break;
            }
            case "list": 
            case "ls": {
                Map<String, Integer> map = blockEspStore2.getBlocks();
                if (map.isEmpty()) {
                    ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u0431\u043b\u043e\u043a\u043e\u0432 \u043f\u0443\u0441\u0442");
                    break;
                }
                int n = map.size();
                StringBuilder stringBuilder = new StringBuilder("\u0411\u043b\u043e\u043a\u0438 (" + n + "): ");
                boolean bl = true;
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    if (!bl) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(entry.getKey()).append(" #").append(this.process4(entry.getValue()));
                    bl = false;
                }
                ClientChat.send(stringBuilder.toString());
                break;
            }
            case "clear": {
                blockEspStore2.clear();
                ClientChat.send("\u0421\u043f\u0438\u0441\u043e\u043a \u0431\u043b\u043e\u043a\u043e\u0432 \u043e\u0447\u0438\u0449\u0435\u043d");
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
            ArrayList<String> arrayList = new ArrayList<String>();
            for (class_2960 identifier : class_7923.field_41175.method_10235()) {
                arrayList.add(this.process5(identifier));
            }
            return arrayList;
        }
        if (n == 2 && string.equals("add")) {
            return List.of("ffffff", "ff0000", "00ff00", "0000ff", "ffff00", "00ffff", "ff00ff");
        }
        if (n == 1 && (string.equals("remove") || string.equals("del") || string.equals("rm"))) {
            BlockEspStore blockEspStore2 = ByAzenClient.getBlockEspStore();
            if (blockEspStore2 == null) {
                return List.of();
            }
            ArrayList<String> arrayList = new ArrayList<String>();
            for (String string2 : blockEspStore2.getBlocks().keySet()) {
                arrayList.add(this.process2(string2));
            }
            return arrayList;
        }
        return List.of();
    }

    private Integer process(String string) {
        String string2 = string.trim();
        if (string2.startsWith("#")) {
            string2 = string2.substring(1);
        } else if (string2.startsWith("0x") || string2.startsWith("0X")) {
            string2 = string2.substring(2);
        }
        if (string2.length() != 6 && string2.length() != 8) {
            return null;
        }
        try {
            long l = Long.parseLong(string2, 16);
            return string2.length() == 6 ? (int)(0xFF000000L | l) : (int)l;
        }
        catch (NumberFormatException numberFormatException) {
            return null;
        }
    }

    private String process2(String string) {
        String string2 = "minecraft:";
        return string.startsWith(string2) ? string.substring(string2.length()) : string;
    }

    private String process3(String string) {
        class_2960 identifier = class_2960.method_12829((String)string.trim().toLowerCase(Locale.ROOT));
        if (identifier == null) {
            return null;
        }
        Optional<class_2248> optional = class_7923.field_41175.method_17966(identifier);
        if (optional.isEmpty() || optional.get() == class_2246.field_10124) {
            return null;
        }
        return class_7923.field_41175.method_10221(optional.get()).toString();
    }

    private String process4(int n) {
        return String.format("%08X", n);
    }

    private String process5(class_2960 identifier) {
        return "minecraft".equals(identifier.method_12836()) ? identifier.method_12832() : identifier.toString();
    }
}

