/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1713
 *  net.minecraft.class_2596
 *  net.minecraft.class_2815
 *  net.minecraft.class_310
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 */
package ru.byazen.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_310;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.ActionSequence;
import ru.byazen.misc.ClickPolicy;
import ru.byazen.misc.ClickSlotAction;
import ru.byazen.misc.DropSlotAction;
import ru.byazen.misc.HotbarSelectAction;
import ru.byazen.misc.InventoryAction;
import ru.byazen.misc.InventoryTask;
import ru.byazen.misc.PickupSlotAction;
import ru.byazen.misc.QuickMoveAction;
import ru.byazen.misc.RunnableAction;
import ru.byazen.misc.SelectSlotAction;
import ru.byazen.misc.SwapSlotsAction;
import ru.byazen.misc.SwapTiming;
import ru.byazen.misc.TaskPhase;
import ru.byazen.misc.TimedAction;
import ru.byazen.misc.UseItemAction;

public class InventoryController {
    private final Map<Integer, List<InventoryTask>> field48 = new HashMap<Integer, List<InventoryTask>>();
    private int slot;
    private int slot2;
    private int slot3;
    private boolean process4;
    private int slot4;
    private int slot5;
    private InventoryTask field52;
    private int slot6;
    private int slot7;
    private int slot8;
    private TaskPhase field56;
    static final int update = 3;
    private boolean enabled;
    private boolean enabled2;
    private boolean enabled3;

    public InventoryController(EventBus eventBus) {
        eventBus.subscribe(ClientTickEvent.class, this::onClientTick);
    }

    private void onClientTick(ClientTickEvent event) {
        this.update4();
    }

    public void update() {
        this.enabled = true;
    }

    public void update2() {
        this.enabled = false;
    }

    public void setIntType(int n) {
        if (n <= 0) {
            this.update14();
            return;
        }
        this.slot4 = n;
    }

    public boolean t() {
        if (this.enabled) {
            return true;
        }
        if (this.field52 != null && this.field52.clickPolicy().hasTiming() && !this.enabled2) {
            return true;
        }
        return this.isActive3();
    }

    public boolean isActive() {
        return this.field52 != null;
    }

    public void submit(InventoryTask task) {
        if (task == null || task.action() == null) {
            return;
        }
        switch (task.conflictPolicy()) {
            case REPLACE: {
                this.setString(task.owner());
                break;
            }
            case SKIP_IF_PRESENT: {
                if (!this.process(task.owner())) break;
                return;
            }
        }
        int n2 = this.slot8 + task.delayTicks();
        this.field48.computeIfAbsent(n2, n -> new ArrayList()).add(task);
    }

    public void setString(String string) {
        if (string == null) {
            return;
        }
        for (List<InventoryTask> list : this.field48.values()) {
            list.removeIf(task -> string.equals(task.owner()));
        }
        this.field48.values().removeIf(List::isEmpty);
        if (this.field52 != null && string.equals(this.field52.owner())) {
            if (this.field52.clickPolicy().closeScreenAfterwards) {
                this.update6();
            }
            this.update5();
        }
    }

    public boolean isAvailable() {
        return this.slot6 > 0 || this.slot4 > 0;
    }

    public boolean process(String string) {
        if (string == null) {
            return false;
        }
        if (this.field52 != null && string.equals(this.field52.owner())) {
            return true;
        }
        for (List<InventoryTask> list : this.field48.values()) {
            for (InventoryTask task : list) {
                if (!string.equals(task.owner())) continue;
                return true;
            }
        }
        return false;
    }

    public void update3() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return;
        }
        mc.field_1690.field_1904.method_23481(true);
        this.slot = 1;
        this.process4 = true;
    }

    public InventoryAction process2(int n, int n2, Runnable runnable, SwapTiming timing) {
        return this.process5(n, n2, runnable, timing, n);
    }

    public InventoryAction process3(int n, int n2, Runnable runnable, SwapTiming timing, int n3, Runnable runnable2) {
        ArrayList<TimedAction> arrayList = new ArrayList<TimedAction>();
        arrayList.add(new TimedAction(timing.beforeClickDelay, new ClickSlotAction(n, n2)));
        arrayList.add(new TimedAction(timing.afterFirstClickDelay, new RunnableAction(runnable)));
        arrayList.add(new TimedAction(timing.secondClickDelay, new ClickSlotAction(n3, n2)));
        if (runnable2 != null) {
            arrayList.add(new TimedAction(timing.completionDelay, new RunnableAction(runnable2)));
        }
        arrayList.add(new TimedAction(timing.completionDelay, new RunnableAction(this::update10)));
        return new ActionSequence(arrayList);
    }

    private boolean isActive3() {
        for (List<InventoryTask> list : this.field48.values()) {
            for (InventoryTask task : list) {
                if (!task.isBlocking()) continue;
                return true;
            }
        }
        return false;
    }

    private void update4() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            this.field48.clear();
            this.update5();
            this.update12();
            ++this.slot8;
            return;
        }
        this.update8();
        if (this.slot4 > 0 && --this.slot4 == 0) {
            this.slot4 = -1;
            this.slot6 = 3;
        }
        if (this.field52 != null) {
            this.update9();
            this.update7();
            ++this.slot8;
            return;
        }
        List<InventoryTask> list = this.getList();
        if (!list.isEmpty()) {
            list.sort(Comparator.comparingInt((InventoryTask task) -> task.priority().ordinal()).reversed());
            Iterator<InventoryTask> iterator = list.iterator();
            while (iterator.hasNext()) {
                InventoryTask task2 = iterator.next();
                iterator.remove();
                if (!task2.condition().getAsBoolean()) continue;
                ClickPolicy clickPolicy = task2.clickPolicy();
                InventoryAction action = task2.action();
                if (action instanceof ActionSequence) {
                    ActionSequence sequence = (ActionSequence)action;
                    this.field52 = task2;
                    this.field56 = TaskPhase.SEQUENCE;
                    this.slot5 = 0;
                    this.advanceActionSequence(sequence);
                    if (this.field52 == null) {
                        continue;
                    }
                } else if (!clickPolicy.hasTiming()) {
                    if (InventoryController.process7(task2.action()) && this.process4) {
                        this.field48.computeIfAbsent(this.slot8 + 1, n -> new ArrayList()).add(task2);
                        continue;
                    }
                    this.executeAction(task2.action());
                    if (!InventoryController.process7(task2.action())) {
                        continue;
                    }
                } else {
                    this.field52 = task2;
                    if (clickPolicy.beforeDelay > 0) {
                        this.field56 = TaskPhase.PRE_DELAY;
                        this.slot7 = clickPolicy.beforeDelay;
                    } else {
                        this.field56 = TaskPhase.EXECUTE;
                        this.slot7 = 0;
                    }
                }
                if (!iterator.hasNext()) break;
                ArrayList deferred = new ArrayList();
                iterator.forEachRemaining(deferred::add);
                this.field48.computeIfAbsent(this.slot8 + 1, n -> new ArrayList()).addAll(deferred);
                break;
            }
        }
        ++this.slot8;
    }

    public InventoryAction process5(int n, int n2, Runnable runnable, SwapTiming timing, int n3) {
        return this.process3(n, n2, runnable, timing, n3, null);
    }

    private void executeAction(InventoryAction action) {
        Objects.requireNonNull(action, "action");
        if (action instanceof ClickSlotAction) {
            ClickSlotAction click = (ClickSlotAction)action;
            this.clickHotbarSwap(click);
        } else if (action instanceof PickupSlotAction) {
            PickupSlotAction pickup = (PickupSlotAction)action;
            this.clickSlot(pickup.slot(), pickup.button(), class_1713.field_7790);
        } else if (action instanceof SwapSlotsAction) {
            SwapSlotsAction swap = (SwapSlotsAction)action;
            this.swapSlots(swap);
        } else if (action instanceof HotbarSelectAction) {
            HotbarSelectAction select = (HotbarSelectAction)action;
            this.selectAndUse(select);
        } else if (action instanceof UseItemAction) {
            this.markUseRequested();
        } else if (action instanceof DropSlotAction) {
            DropSlotAction drop = (DropSlotAction)action;
            this.dropSlot(drop);
        } else if (action instanceof QuickMoveAction) {
            QuickMoveAction quickMove = (QuickMoveAction)action;
            this.clickSlot(quickMove.slot(), 0, class_1713.field_7794);
        } else if (action instanceof SelectSlotAction) {
            SelectSlotAction select = (SelectSlotAction)action;
            this.selectSlot(select);
        } else if (action instanceof ActionSequence) {
            ActionSequence sequence = (ActionSequence)action;
            for (int delay = 0; delay <= sequence.maxDelay(); ++delay) {
                for (TimedAction step : sequence.steps()) {
                    if (step.delay() != delay) continue;
                    this.executeAction(step.action());
                }
            }
        } else if (action instanceof RunnableAction) {
            RunnableAction runnable = (RunnableAction)action;
            runnable.runnable().run();
        } else {
            throw new IllegalArgumentException("Unsupported inventory action: " + action.getClass().getName());
        }
    }

    private void update5() {
        this.field52 = null;
        this.field56 = null;
        this.slot7 = 0;
        this.slot5 = 0;
        this.enabled2 = false;
    }

    private void clickSlot(int slot, int button, class_1713 actionType) {
        class_746 player2 = class_310.method_1551().field_1724;
        class_636 clientPlayerInteractionManager = class_310.method_1551().field_1761;
        if (player2 == null || clientPlayerInteractionManager == null) {
            return;
        }
        int syncId = player2.field_7512.field_7763;
        clientPlayerInteractionManager.method_2906(syncId, slot, button, actionType, (class_1657)player2);
    }

    private void update6() {
        class_746 player2 = class_310.method_1551().field_1724;
        if (player2 == null) {
            return;
        }
        int n = player2.field_7512.field_7763;
        if (n <= 0) {
            return;
        }
        player2.field_3944.method_52787((class_2596)new class_2815(n));
    }

    private void selectSlot(SelectSlotAction action) {
        class_746 player2 = class_310.method_1551().field_1724;
        if (player2 == null) {
            return;
        }
        if (action.slot() < 0 || action.slot() > 8) {
            return;
        }
        class_1661 inv = player2.method_31548();
        if (inv.method_67532() == action.slot()) {
            return;
        }
        inv.method_61496(action.slot());
    }

    public boolean isActive4() {
        return this.field52 == null && this.field48.isEmpty() && this.slot <= 0 && this.slot3 == -1;
    }

    private void update7() {
        List<InventoryTask> list = this.getList();
        if (!list.isEmpty()) {
            this.field48.computeIfAbsent(this.slot8 + 1, n -> new ArrayList()).addAll(list);
        }
    }

    public boolean isActive5() {
        if (this.slot6 <= 0) {
            return false;
        }
        --this.slot6;
        return true;
    }

    private void update8() {
        class_310 mc = class_310.method_1551();
        class_746 player2 = mc.field_1724;
        if (player2 == null) {
            return;
        }
        if (this.slot > 0) {
            if (--this.slot == 0) {
                mc.field_1690.field_1904.method_23481(false);
            }
        } else if (this.slot3 != -1 && --this.slot2 <= 0) {
            player2.method_31548().method_61496(this.slot3);
            this.slot3 = -1;
        }
        this.process4 = this.slot > 0 || this.slot3 != -1;
    }

    private void update9() {
        ClickPolicy clickPolicy = this.field52.clickPolicy();
        switch (this.field56) {
            case SEQUENCE: {
                this.advanceActionSequence((ActionSequence)this.field52.action());
                break;
            }
            case PRE_DELAY: {
                --this.slot7;
                if (this.slot7 > 0) break;
                this.field56 = TaskPhase.EXECUTE;
                break;
            }
            case EXECUTE: {
                this.executeAction(this.field52.action());
                if (clickPolicy.afterDelay > 0) {
                    this.field56 = TaskPhase.POST_DELAY;
                    this.slot7 = clickPolicy.afterDelay;
                    break;
                }
                if (clickPolicy.closeScreenAfterwards) {
                    this.update6();
                }
                this.update5();
                break;
            }
            case POST_DELAY: {
                if (--this.slot7 > 0) break;
                if (clickPolicy.closeScreenAfterwards) {
                    this.update6();
                }
                this.update5();
            }
        }
    }

    public void update10() {
        this.enabled2 = true;
    }

    public boolean isActive6() {
        return this.field52 != null && this.field52.clickPolicy() == ClickPolicy.SWAP;
    }

    private void update11() {
        ClickPolicy clickPolicy = this.field52.clickPolicy();
        if (clickPolicy.afterDelay > 0) {
            this.field56 = TaskPhase.POST_DELAY;
            this.slot7 = clickPolicy.afterDelay;
        } else {
            if (clickPolicy.closeScreenAfterwards) {
                this.update6();
            }
            this.update5();
        }
    }

    private void update12() {
        if (this.slot > 0) {
            class_310.method_1551().field_1690.field_1904.method_23481(false);
        }
        this.slot = 0;
        this.slot3 = -1;
        this.slot2 = 0;
        this.process4 = false;
        this.enabled3 = false;
    }

    private List<InventoryTask> getList() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (Integer clipToSpace : this.field48.keySet()) {
            if (clipToSpace > this.slot8) continue;
            arrayList.add(clipToSpace);
        }
        arrayList.sort(Comparator.naturalOrder());
        ArrayList<InventoryTask> arrayList2 = new ArrayList<InventoryTask>();
        for (Integer n : arrayList) {
            arrayList2.addAll((Collection<InventoryTask>)this.field48.remove(n));
        }
        return arrayList2;
    }

    private void selectAndUse(HotbarSelectAction action) {
        class_746 player2 = class_310.method_1551().field_1724;
        if (player2 == null) {
            return;
        }
        if (action.slot() < 0 || action.slot() > 8) {
            return;
        }
        class_1661 inv = player2.method_31548();
        int n = inv.method_67532();
        if (n != action.slot()) {
            inv.method_61496(action.slot());
            if (action.useAfterSelect()) {
                this.slot3 = n;
                this.slot2 = 2;
            }
        }
        this.update3();
    }

    private static boolean process7(InventoryAction action) {
        return action instanceof HotbarSelectAction || action instanceof UseItemAction;
    }

    private void dropSlot(DropSlotAction action) {
        this.clickSlot(action.slot(), action.entireStack() ? 1 : 0, class_1713.field_7795);
    }

    private void clickHotbarSwap(ClickSlotAction action) {
        this.clickSlot(action.slot(), action.hotbarButton(), class_1713.field_7791);
    }

    private void swapSlots(SwapSlotsAction action) {
        int sourceContainerSlot;
        class_746 player2 = class_310.method_1551().field_1724;
        if (player2 == null) {
            return;
        }
        class_1661 inv = player2.method_31548();
        int sourceSlot = action.getFromSlot();
        int n = sourceContainerSlot = sourceSlot < 9 ? sourceSlot + 36 : sourceSlot;
        if (action.getToSlot() == sourceContainerSlot) {
            return;
        }
        boolean sourceOccupied = sourceSlot >= 0 && sourceSlot < inv.method_5439() && !inv.method_5438(sourceSlot).method_7960();
        this.clickSlot(action.getToSlot(), 0, class_1713.field_7790);
        this.clickSlot(sourceContainerSlot, 0, class_1713.field_7790);
        if (sourceOccupied) {
            this.clickSlot(action.getToSlot(), 0, class_1713.field_7790);
        }
    }

    public boolean isActive7() {
        if (!this.enabled3) {
            return false;
        }
        this.enabled3 = false;
        return true;
    }

    private void advanceActionSequence(ActionSequence actionSequence) {
        for (TimedAction timedAction : actionSequence.steps()) {
            if (timedAction.delay() != this.slot5) continue;
            this.executeAction(timedAction.action());
        }
        ++this.slot5;
        if (this.slot5 > actionSequence.maxDelay()) {
            this.update11();
        }
    }

    private void markUseRequested() {
        this.enabled3 = true;
    }

    public void update14() {
        this.slot6 = 3;
    }
}

