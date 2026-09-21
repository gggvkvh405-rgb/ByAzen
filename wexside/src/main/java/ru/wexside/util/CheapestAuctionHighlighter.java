/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1703
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_465
 */
package ru.wexside.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_465;
import ru.wexside.misc.SlotHighlight;
import ru.wexside.misc.SlotHighlightProvider;
import ru.wexside.util.MarketTooltipParser;

public final class CheapestAuctionHighlighter
implements SlotHighlightProvider {
    private final BooleanSupplier enabled;
    private final IntSupplier highlightCount;
    private final IntSupplier highlightColor;
    private final MarketTooltipParser tooltipParser = new MarketTooltipParser();

    public CheapestAuctionHighlighter(BooleanSupplier enabled, IntSupplier highlightCount, IntSupplier highlightColor) {
        this.enabled = enabled;
        this.highlightCount = highlightCount;
        this.highlightColor = highlightColor;
    }

    @Override
    public List<SlotHighlight> process4(class_465<?> screen) {
        String title = screen.method_25440().getString();
        if (!this.enabled.getAsBoolean() || !title.contains("/") || title.contains("\u0414\u043e\u043d\u041c\u0430\u0440\u043a\u0435\u0442")) {
            return List.of();
        }
        class_310 client = class_310.method_1551();
        class_1703 handler = screen.method_17577();
        ArrayList<PricedSlot> offers = new ArrayList<PricedSlot>();
        for (int index = 0; index < handler.field_7761.size(); ++index) {
            class_1735 slot = (class_1735)handler.field_7761.get(index);
            class_1799 stack = slot.method_7677();
            Integer unitPrice = this.tooltipParser.parseUnitAuctionPrice(stack, client);
            if (unitPrice == null) continue;
            offers.add(new PricedSlot(index, unitPrice));
        }
        offers.sort(Comparator.comparingInt(PricedSlot::unitPrice));
        int count = Math.min(Math.max(0, this.highlightCount.getAsInt()), offers.size());
        ArrayList<SlotHighlight> highlights = new ArrayList<SlotHighlight>(count);
        for (int index = 0; index < count; ++index) {
            highlights.add(new SlotHighlight(((PricedSlot)offers.get(index)).slotIndex(), this.highlightColor.getAsInt()));
        }
        return highlights;
    }

    private record PricedSlot(int slotIndex, int unitPrice) {
    }
}

