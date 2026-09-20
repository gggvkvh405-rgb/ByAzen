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
import java.util.function.Supplier;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_465;
import ru.wexside.market.AuctionPrice;
import ru.wexside.market.AuctionPriceParser;
import ru.wexside.misc.AuctionHighlightSettings;
import ru.wexside.misc.SlotHighlight;
import ru.wexside.misc.SlotHighlightProvider;
import ru.wexside.util.ContainerScreenHelper;

public final class AuctionSlotHighlighter
implements SlotHighlightProvider {
    private final Supplier<AuctionHighlightSettings> configSupplier;
    private final AuctionPriceParser priceParser = new AuctionPriceParser();
    private final BooleanSupplier enabledSupplier;

    public AuctionSlotHighlighter(BooleanSupplier enabledSupplier, Supplier<AuctionHighlightSettings> configSupplier) {
        this.enabledSupplier = enabledSupplier;
        this.configSupplier = configSupplier;
    }

    private static int withAlpha(int rgb, int alpha) {
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    private static int pulseAlpha(int speed) {
        double period = Math.max(1, speed);
        float wave = 0.5f + 0.5f * (float)Math.sin((double)System.currentTimeMillis() / 1000.0 * period * Math.PI);
        return Math.round(50.0f + 180.0f * wave);
    }

    @Override
    public List<SlotHighlight> process4(class_465<?> screen) {
        if (!this.enabledSupplier.getAsBoolean()) {
            return List.of();
        }
        class_310 client = class_310.method_1551();
        class_1703 handler = screen.method_17577();
        if (handler == null || !ContainerScreenHelper.isAuctionContainer(handler, screen)) {
            return List.of();
        }
        AuctionHighlightSettings config = this.configSupplier.get();
        ArrayList<PricedSlot> pricedSlots = new ArrayList<PricedSlot>();
        for (int i = 0; i < handler.field_7761.size(); ++i) {
            AuctionPrice priceInfo;
            class_1735 slot = (class_1735)handler.field_7761.get(i);
            class_1799 stack = slot.method_7677();
            if (stack.method_7960() || config.enabled3 && stack.method_7919() > 0 || (priceInfo = this.priceParser.parse(stack, client)) == null || config.enabled && config.slot2 >= 0 && priceInfo.unitPrice() > config.slot2) continue;
            pricedSlots.add(new PricedSlot(i, priceInfo.totalPrice()));
        }
        if (pricedSlots.isEmpty()) {
            return List.of();
        }
        pricedSlots.sort(Comparator.comparingInt(PricedSlot::price));
        int count = Math.min(Math.max(1, config.slot4), pricedSlots.size());
        int color = config.enabled2 ? AuctionSlotHighlighter.withAlpha(config.slot, AuctionSlotHighlighter.pulseAlpha(config.slot3)) : config.slot;
        ArrayList<SlotHighlight> highlights = new ArrayList<SlotHighlight>(count);
        for (int i = 0; i < count; ++i) {
            PricedSlot pricedSlot = (PricedSlot)pricedSlots.get(i);
            highlights.add(new SlotHighlight(pricedSlot.slotIndex(), color));
        }
        return highlights;
    }

    private record PricedSlot(int slotIndex, int price) {
    }
}

