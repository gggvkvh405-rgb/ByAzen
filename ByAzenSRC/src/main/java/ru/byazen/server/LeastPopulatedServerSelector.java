/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_465
 *  net.minecraft.class_746
 *  net.minecraft.class_9290
 *  net.minecraft.class_9334
 */
package ru.byazen.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_465;
import net.minecraft.class_746;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import ru.byazen.misc.ClientChat;
import ru.byazen.misc.ServerPopulationEntry;

public final class LeastPopulatedServerSelector {
    private static final int TIMEOUT_TICKS = 1600;
    private static final int MENU_SETTLE_TICKS = 6;
    private static final int PAGE_SETTLE_TICKS = 30;
    private static final int MENU_CHANGE_TIMEOUT_TICKS = 60;
    private static final int MAX_PAGES = 12;
    private static final int[] COMMAND_CATEGORIES = new int[]{1, 2, 3, 5, 10};
    private final Pattern populationPattern = Pattern.compile("\u041e\u043d\u043b\u0430\u0439\u043d \u0440\u0435\u0436\u0438\u043c\u0430:\\s*(\\d+)");
    private final Pattern numberPattern = Pattern.compile("(\\d+)");
    private final List<ServerPopulationEntry> discoveredServers = new ArrayList<ServerPopulationEntry>();
    private final Set<String> discoveredNames = new HashSet<String>();
    private State state = State.IDLE;
    private State stateAfterMenuChange;
    private ServerPopulationEntry selectedServer;
    private int elapsedTicks;
    private int menuTicks;
    private int page;
    private int categoryIndex;
    private int expectedSyncId = -1;
    private int menuChangeTicks;

    public void tick() {
        if (this.state == State.IDLE) {
            return;
        }
        if (++this.elapsedTicks > 1600) {
            this.fail("\u0442\u0430\u0439\u043c\u0430\u0443\u0442");
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || !(client.field_1755 instanceof class_465)) {
            this.fail("\u043c\u0435\u043d\u044e \u0437\u0430\u043a\u0440\u044b\u0442\u043e");
            return;
        }
        switch (this.state.ordinal()) {
            case 1: {
                this.openVersionOrCategory();
                break;
            }
            case 2: {
                this.awaitMenuChange();
                break;
            }
            case 3: {
                this.scanCurrentCategory(false);
                break;
            }
            case 4: {
                this.scanCurrentCategory(true);
            }
        }
    }

    public void start() {
        if (this.state != State.IDLE) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || !(client.field_1755 instanceof class_465)) {
            return;
        }
        this.resetProgress();
        this.state = State.OPEN_VERSION;
        ClientChat.send("\u0418\u0449\u0435\u043c \u0441\u0430\u043c\u0443\u044e \u043f\u0443\u0441\u0442\u0443\u044e \u0430\u043d\u0430\u0440\u0445\u0438\u044e...");
    }

    private void openVersionOrCategory() {
        String title = this.currentTitle();
        if (title.contains("\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0441\u0435\u0440\u0432\u0435\u0440") || title.contains("\u0442\u0438\u043f \u0440\u0435\u0436\u0438\u043c\u0430")) {
            int categorySlot = this.findCategorySlot(COMMAND_CATEGORIES[0]);
            if (categorySlot < 0) {
                this.fail("\u043d\u0435\u0442 \u0432\u043a\u043b\u0430\u0434\u043a\u0438 \u043a\u043e\u043c\u0430\u043d\u0434\u044b");
                return;
            }
            this.categoryIndex = 0;
            this.page = 0;
            this.clickAndWait(categorySlot, State.SCAN_SERVERS);
        } else if (title.contains("\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0440\u0435\u0436\u0438\u043c")) {
            int versionSlot = this.findSlotContaining("1.21.11");
            if (versionSlot < 0) {
                this.fail("\u043d\u0435\u0442 \u0442\u0438\u043f\u0430 1.21.11");
                return;
            }
            this.clickAndWait(versionSlot, State.OPEN_VERSION);
        } else {
            this.fail("\u043d\u0435\u043e\u0436\u0438\u0434\u0430\u043d\u043d\u044b\u0439 \u044d\u043a\u0440\u0430\u043d: " + title);
        }
    }

    private void scanCurrentCategory(boolean locatingSelectedServer) {
        if (!this.currentTitle().contains("\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0441\u0435\u0440\u0432\u0435\u0440")) {
            this.fail("\u043e\u0436\u0438\u0434\u0430\u043b \u044d\u043a\u0440\u0430\u043d \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u0432");
            return;
        }
        ++this.menuTicks;
        if (!(this.menuTicks >= 30 || this.menuTicks >= 6 && this.containsAnarchyServers())) {
            return;
        }
        if (locatingSelectedServer) {
            int serverSlot = this.findExactSlot(this.selectedServer.name());
            if (serverSlot >= 0) {
                this.clickSlot(serverSlot);
                ClientChat.send("\u0417\u0430\u0445\u043e\u0434\u0438\u043c \u043d\u0430 " + this.selectedServer.name() + " (\u043e\u043d\u043b\u0430\u0439\u043d " + this.selectedServer.onlinePlayers() + ")");
                this.finish();
                return;
            }
        } else {
            this.collectCurrentPage();
        }
        int nextPageSlot = this.findSlotContaining("\u0421\u043b\u0435\u0434\u0443\u044e\u0449\u0430\u044f \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u0430");
        if (nextPageSlot >= 0 && this.page < 12) {
            ++this.page;
            this.clickAndWait(nextPageSlot, locatingSelectedServer ? State.FIND_SELECTED_SERVER : State.SCAN_SERVERS);
            return;
        }
        if (locatingSelectedServer) {
            this.fail("\u043d\u0435 \u043d\u0430\u0448\u0451\u043b \u0441\u0435\u0440\u0432\u0435\u0440 " + this.selectedServer.name());
            return;
        }
        ++this.categoryIndex;
        this.page = 0;
        if (this.categoryIndex < COMMAND_CATEGORIES.length) {
            int categorySlot = this.findCategorySlot(COMMAND_CATEGORIES[this.categoryIndex]);
            if (categorySlot < 0) {
                this.fail("\u043d\u0435\u0442 \u0432\u043a\u043b\u0430\u0434\u043a\u0438 \u043a\u043e\u043c\u0430\u043d\u0434\u044b " + COMMAND_CATEGORIES[this.categoryIndex]);
                return;
            }
            this.clickAndWait(categorySlot, State.SCAN_SERVERS);
            return;
        }
        this.selectedServer = this.findLeastPopulatedServer();
        if (this.selectedServer == null) {
            this.fail("\u0441\u0435\u0440\u0432\u0435\u0440\u043e\u0432 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e");
            return;
        }
        int selectedCategorySlot = this.findCategorySlot(COMMAND_CATEGORIES[this.selectedServer.categoryIndex()]);
        if (selectedCategorySlot < 0) {
            this.fail("\u043d\u0435\u0442 \u0432\u043a\u043b\u0430\u0434\u043a\u0438 \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u0434\u043b\u044f \u0437\u0430\u0445\u043e\u0434\u0430");
            return;
        }
        this.clickAndWait(selectedCategorySlot, State.FIND_SELECTED_SERVER);
    }

    private Integer readPopulation(class_1799 stack) {
        class_9290 lore = (class_9290)stack.method_58694(class_9334.field_49632);
        if (lore == null) {
            return null;
        }
        for (class_2561 line : lore.comp_2400()) {
            Matcher matcher = this.populationPattern.matcher(line.getString());
            if (!matcher.find()) continue;
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private void collectCurrentPage() {
        class_1703 handler = this.currentHandler();
        if (handler == null) {
            return;
        }
        for (class_1735 slot : handler.field_7761) {
            class_1799 stack;
            String name;
            Integer population;
            if (!slot.method_7681() || (population = (name = (stack = slot.method_7677()).method_7964().getString()).contains("\u0410\u043d\u0430\u0440\u0445\u0438\u044f-") ? this.readPopulation(stack) : null) == null || !this.discoveredNames.add(name)) continue;
            this.discoveredServers.add(new ServerPopulationEntry(name, population, this.categoryIndex));
        }
    }

    private boolean containsAnarchyServers() {
        class_1703 handler = this.currentHandler();
        if (handler == null) {
            return false;
        }
        for (class_1735 slot : handler.field_7761) {
            if (!slot.method_7681() || !slot.method_7677().method_7964().getString().contains("\u0410\u043d\u0430\u0440\u0445\u0438\u044f-") || this.readPopulation(slot.method_7677()) == null) continue;
            return true;
        }
        return false;
    }

    private int findExactSlot(String name) {
        class_1703 handler = this.currentHandler();
        if (handler == null) {
            return -1;
        }
        for (class_1735 slot : handler.field_7761) {
            if (!slot.method_7681() || !slot.method_7677().method_7964().getString().equals(name)) continue;
            return slot.field_7874;
        }
        return -1;
    }

    private int findSlotContaining(String text) {
        class_1703 handler = this.currentHandler();
        if (handler == null) {
            return -1;
        }
        for (class_1735 slot : handler.field_7761) {
            if (!slot.method_7681() || !slot.method_7677().method_7964().getString().contains(text)) continue;
            return slot.field_7874;
        }
        return -1;
    }

    private int findCategorySlot(int category) {
        class_1703 handler = this.currentHandler();
        if (handler == null) {
            return -1;
        }
        for (class_1735 slot : handler.field_7761) {
            String name;
            if (!slot.method_7681() || !(name = slot.method_7677().method_7964().getString()).contains("\u041a\u043e\u043c\u0430\u043d\u0434\u044b") || this.lastNumber(name) != category) continue;
            return slot.field_7874;
        }
        return -1;
    }

    private int lastNumber(String text) {
        Matcher matcher = this.numberPattern.matcher(text);
        int value = -1;
        while (matcher.find()) {
            value = Integer.parseInt(matcher.group(1));
        }
        return value;
    }

    private void clickAndWait(int slot, State nextState) {
        class_1703 handler = this.currentHandler();
        this.expectedSyncId = handler == null ? -1 : handler.field_7763;
        this.stateAfterMenuChange = nextState;
        this.menuChangeTicks = 0;
        this.menuTicks = 0;
        this.clickSlot(slot);
        this.state = State.WAIT_FOR_MENU;
    }

    private void awaitMenuChange() {
        class_1703 handler = this.currentHandler();
        if (handler != null && handler.field_7763 != this.expectedSyncId) {
            this.state = this.stateAfterMenuChange;
            this.menuChangeTicks = 0;
            this.menuTicks = 0;
            return;
        }
        if (++this.menuChangeTicks > 60) {
            this.fail("\u043e\u043a\u043d\u043e \u043d\u0435 \u043f\u0440\u0438\u0448\u043b\u043e");
        }
    }

    private void clickSlot(int slot) {
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        class_1703 handler = this.currentHandler();
        if (player != null && handler != null && client.field_1761 != null) {
            client.field_1761.method_2906(handler.field_7763, slot, 0, class_1713.field_7790, (class_1657)player);
        }
    }

    private ServerPopulationEntry findLeastPopulatedServer() {
        ServerPopulationEntry best = null;
        for (ServerPopulationEntry entry : this.discoveredServers) {
            if (best != null && entry.onlinePlayers() >= best.onlinePlayers()) continue;
            best = entry;
        }
        return best;
    }

    private String currentTitle() {
        class_310 client = class_310.method_1551();
        return client.field_1755 == null ? "" : client.field_1755.method_25440().getString();
    }

    private class_1703 currentHandler() {
        class_746 player = class_310.method_1551().field_1724;
        return player == null ? null : player.field_7512;
    }

    private void fail(String reason) {
        ClientChat.send("\u041f\u0443\u0441\u0442\u0430\u044f \u0430\u043d\u0430\u0440\u0445\u0438\u044f: " + reason);
        this.finish();
    }

    private void finish() {
        this.state = State.IDLE;
        this.resetProgress();
    }

    private void resetProgress() {
        this.discoveredServers.clear();
        this.discoveredNames.clear();
        this.selectedServer = null;
        this.stateAfterMenuChange = null;
        this.elapsedTicks = 0;
        this.menuTicks = 0;
        this.page = 0;
        this.categoryIndex = 0;
        this.expectedSyncId = -1;
        this.menuChangeTicks = 0;
    }

    private static enum State {
        IDLE,
        OPEN_VERSION,
        WAIT_FOR_MENU,
        SCAN_SERVERS,
        FIND_SELECTED_SERVER;

    }
}

