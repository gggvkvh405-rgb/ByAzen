/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.command;

import java.util.List;
import ru.wexside.command.CommandUsageException;

public abstract class Command {
    private final String field12;
    private final String[] field16;
    private final String field20;

    protected Command(String string, String string2, String ... stringArray) {
        String[] stringArray2;
        this.field20 = string;
        this.field12 = string2;
        if (stringArray.length == 0) {
            String[] stringArray3 = new String[1];
            stringArray2 = stringArray3;
            stringArray3[0] = string;
        } else {
            stringArray2 = stringArray;
        }
        this.field16 = stringArray2;
    }

    public abstract String getUsage();

    public abstract void execute(String ... var1) throws CommandUsageException;

    public String[] getCommandAliases() {
        return this.field16;
    }

    public String getCommandDescription() {
        return this.field12;
    }

    public String getCommandName() {
        return this.field20;
    }

    public List<String> complete(int n, String[] stringArray) {
        return List.of();
    }
}

