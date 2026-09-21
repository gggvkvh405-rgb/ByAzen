/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_124
 *  net.minecraft.class_2558
 *  net.minecraft.class_2558$class_10609
 *  net.minecraft.class_2561
 *  net.minecraft.class_2568
 *  net.minecraft.class_2568$class_10613
 *  net.minecraft.class_2583
 *  net.minecraft.class_5250
 */
package ru.byazen.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_124;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import ru.byazen.command.Command;
import ru.byazen.command.CommandUsageException;
import ru.byazen.misc.ClientChat;
import ru.byazen.misc.ConfigManager;

public final class ConfigCommand
extends Command {
    private static final String string6 = ".byazen";
    ConfigManager field24;
    private static final int slot = 32;

    public ConfigCommand(ConfigManager configManager2) {
        super("cfg", "\u0423\u043f\u0440\u0430\u0432\u043b\u044f\u0435\u0442 \u043a\u043e\u043d\u0444\u0438\u0433\u0430\u043c\u0438 \u043a\u043b\u0438\u0435\u043d\u0442\u0430", "cfg", "config");
        this.field24 = configManager2;
    }

    private void setString(String string) {
        if (!this.field24.profileExists(string)) {
            String string2 = string;
            ClientChat.send("\u041a\u043e\u043d\u0444\u0438\u0433 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d: " + string2);
            return;
        }
        this.field24.loadProfile(string);
        String string3 = string;
        ClientChat.send("\u041a\u043e\u043d\u0444\u0438\u0433 \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d: " + string3);
        try {
            this.update();
        }
        catch (IOException iOException) {
            ClientChat.send("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u043a\u043e\u043d\u0444\u0438\u0433\u043e\u0432: " + iOException.getMessage());
        }
    }

    private void update() throws IOException {
        List<String> list = this.field24.listProfiles();
        if (list.isEmpty()) {
            ClientChat.send("\u041a\u043e\u043d\u0444\u0438\u0433\u0438 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u044b");
            return;
        }
        ClientChat.send("\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0435 \u043a\u043e\u043d\u0444\u0438\u0433\u0438:");
        String string = ConfigCommand.process2(this.field24.getCurrentProfileName());
        for (String string2 : list) {
            String string3 = ConfigCommand.process2(string2);
            boolean bl = string3.equals(string);
            ClientChat.send((class_2561)this.process(string3, bl));
        }
    }

    @Override
    public String getUsage() {
        return ".cfg <save/load/clear/list/dir> [name]";
    }

    @Override
    public void execute(String ... stringArray) throws CommandUsageException {
        if (stringArray.length == 0) {
            throw new CommandUsageException(this);
        }
        String string = stringArray[0].toLowerCase(Locale.ROOT);
        try {
            String string2;
            switch (string) {
                case "clear": {
                    this.update3();
                    return;
                }
                case "list": {
                    this.update();
                    return;
                }
                case "dir": {
                    this.update2();
                    return;
                }
            }
            String string3 = string2 = stringArray.length > 1 ? String.join((CharSequence)" ", Arrays.copyOfRange(stringArray, 1, stringArray.length)).trim() : "";
            if (string2.isBlank()) {
                String string4 = this.field24.getCurrentProfileName();
                string2 = string4 != null && !string4.isBlank() ? string4 : "default";
            }
            switch (string) {
                case "save": {
                    this.setString2(string2);
                    break;
                }
                case "load": {
                    this.setString(string2);
                    break;
                }
                default: {
                    throw new CommandUsageException(this);
                }
            }
        }
        catch (IOException iOException) {
            String string5 = iOException.getMessage();
            ClientChat.send("\u041e\u0448\u0438\u0431\u043a\u0430 \u043a\u043e\u043d\u0444\u0438\u0433\u0430: " + string5);
        }
    }

    private void update2() throws IOException {
        this.field24.openConfigFolder();
        ClientChat.send("\u041f\u0430\u043f\u043a\u0430 \u043a\u043e\u043d\u0444\u0438\u0433\u043e\u0432 \u043e\u0442\u043a\u0440\u044b\u0442\u0430");
    }

    private class_5250 process(String string, boolean bl) {
        String string2 = string;
        String string3 = string;
        class_2583 actionStyle = class_2583.field_24360.method_10958((class_2558)new class_2558.class_10609(".cfg load " + string2)).method_10949((class_2568)new class_2568.class_10613((class_2561)class_2561.method_43470((String)("\u041d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c " + string3))));
        class_5250 configName = class_2561.method_43470((String)string).method_10862(actionStyle);
        class_5250 status = bl ? class_2561.method_43470((String)" (\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d)").method_27692(class_124.field_1060) : class_2561.method_43470((String)" (\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c)").method_27692(class_124.field_1080);
        return class_2561.method_43470((String)"- ").method_10852((class_2561)configName).method_10852((class_2561)status);
    }

    @Override
    public List<String> complete(int n, String[] stringArray) {
        if (n == 0) {
            return List.of("save", "load", "clear", "list", "dir");
        }
        if (n == 1 && stringArray.length >= 1 && "load".equalsIgnoreCase(stringArray[0])) {
            try {
                ArrayList<String> arrayList = new ArrayList<String>();
                for (String string : this.field24.listProfiles()) {
                    arrayList.add(ConfigCommand.process2(string));
                }
                return arrayList;
            }
            catch (IOException iOException) {
                return List.of();
            }
        }
        return List.of();
    }

    private void update3() throws IOException {
        this.field24.resetProfile();
        ClientChat.send("\u041a\u043e\u043d\u0444\u0438\u0433 \u0441\u0431\u0440\u043e\u0448\u0435\u043d");
    }

    private void setString2(String string) throws IOException {
        if (string.length() > 32) {
            ClientChat.send("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043a\u043e\u043d\u0444\u0438\u0433\u0430 \u043d\u0435 \u0434\u043e\u043b\u0436\u043d\u043e \u043f\u0440\u0435\u0432\u044b\u0448\u0430\u0442\u044c 32 \u0441\u0438\u043c\u0432\u043e\u043b\u043e\u0432");
            return;
        }
        this.field24.saveProfile(string);
        String string2 = string;
        ClientChat.send("\u041a\u043e\u043d\u0444\u0438\u0433 \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d: " + string2);
    }

    private static String process2(String string) {
        if (string == null) {
            return null;
        }
        return string.endsWith(string6) ? string.substring(0, string.length() - string6.length()) : string;
    }
}

