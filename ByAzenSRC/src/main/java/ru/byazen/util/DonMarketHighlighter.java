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
package ru.byazen.util;

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
import ru.byazen.misc.SlotHighlight;
import ru.byazen.misc.SlotHighlightProvider;
import ru.byazen.util.MarketTooltipParser;

public final class DonMarketHighlighter
implements SlotHighlightProvider {
    private final BooleanSupplier enabled;
    private final IntSupplier highlightCount;
    private final IntSupplier highlightColor;
    private final MarketTooltipParser tooltipParser = new MarketTooltipParser();

    public DonMarketHighlighter(BooleanSupplier enabled, IntSupplier highlightCount, IntSupplier highlightColor) {
        this.enabled = enabled;
        this.highlightCount = highlightCount;
        this.highlightColor = highlightColor;
    }

    @Override
    public List<SlotHighlight> process4(class_465<?> screen) {
        if (!this.enabled.getAsBoolean() || !screen.method_25440().getString().contains("\u0414\u043e\u043d\u041c\u0430\u0440\u043a\u0435\u0442")) {
            return List.of();
        }
        class_310 client = class_310.method_1551();
        class_1703 handler = screen.method_17577();
        ArrayList<ScoredSlot> offers = new ArrayList<ScoredSlot>();
        for (int index = 0; index < handler.field_7761.size(); ++index) {
            class_1735 slot = (class_1735)handler.field_7761.get(index);
            class_1799 stack = slot.method_7677();
            MarketTooltipParser.DonMarketOffer offer = this.tooltipParser.parseDonMarketOffer(stack, client);
            if (offer == null) continue;
            offers.add(new ScoredSlot(index, offer.exchangeRate()));
        }
        offers.sort(Comparator.comparingInt(ScoredSlot::score).reversed());
        int count = Math.min(Math.max(0, this.highlightCount.getAsInt()), offers.size());
        ArrayList<SlotHighlight> highlights = new ArrayList<SlotHighlight>(count);
        for (int index = 0; index < count; ++index) {
            highlights.add(new SlotHighlight(((ScoredSlot)offers.get(index)).slotIndex(), this.highlightColor.getAsInt()));
        }
        return highlights;
    }

    private record ScoredSlot(int slotIndex, int score) {
    }
}

