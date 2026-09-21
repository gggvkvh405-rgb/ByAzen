/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1661
 *  net.minecraft.class_1747
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_2480
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_746
 */
package ru.byazen.misc;

import net.minecraft.class_1661;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2480;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_746;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.ClickPolicy;
import ru.byazen.misc.ClientChat;
import ru.byazen.misc.InventoryTask;
import ru.byazen.misc.PickupSlotAction;
import ru.byazen.misc.TaskFlag;
import ru.byazen.misc.TaskPriority;
import ru.byazen.util.InventoryController;

public class ServerHelperShulker {
    private final String field12;

    public ServerHelperShulker() {
        this.field12 = "server_helper_shulker";
    }

    public void update() {
        class_746 player2 = class_310.method_1551().field_1724;
        if (player2 == null) {
            return;
        }
        InventoryController inventory = ByAzenClient.getInventoryController();
        if (inventory == null) {
            return;
        }
        if (this.isActive()) {
            ClientChat.send("\u0412\u043e \u0432\u0440\u0435\u043c\u044f \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u0448\u0430\u043b\u043a\u0435\u0440\u0430 \u043d\u0435\u043b\u044c\u0437\u044f \u0434\u0432\u0438\u0433\u0430\u0442\u044c\u0441\u044f.");
            return;
        }
        class_1661 inv = player2.method_31548();
        int n = this.process(inv);
        if (n == -1) {
            ClientChat.send("\u0412 \u0412\u0430\u0448\u0435\u043c \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435 \u043d\u0435\u0442 \u0448\u0430\u043b\u043a\u0435\u0440\u0430.");
            return;
        }
        int n2 = n < 9 ? n + 36 : n;
        inventory.submit(InventoryTask.builder().action(new PickupSlotAction(n2, 1)).owner("server_helper_shulker").flag(TaskFlag.REPLACE).policy(ClickPolicy.SILENT).priority(TaskPriority.NORMAL).build());
    }

    private boolean isActive() {
        class_315 options = class_310.method_1551().field_1690;
        if (options == null) {
            return false;
        }
        return options.field_1894.method_1434() || options.field_1881.method_1434() || options.field_1913.method_1434() || options.field_1849.method_1434() || options.field_1903.method_1434();
    }

    private int process(class_1661 inv) {
        for (int i = 0; i < 36; ++i) {
            class_1747 blockItem2;
            class_1792 iiIilIIilI2;
            class_1799 stack = inv.method_5438(i);
            if (stack.method_7960() || !((iiIilIIilI2 = stack.method_7909()) instanceof class_1747) || !((blockItem2 = (class_1747)iiIilIIilI2).method_7711() instanceof class_2480)) continue;
            return i;
        }
        return -1;
    }
}

