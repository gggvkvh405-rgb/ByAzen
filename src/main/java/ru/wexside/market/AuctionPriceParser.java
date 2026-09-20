/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package ru.wexside.market;

import java.util.List;
import java.util.Locale;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import ru.wexside.market.AuctionPrice;

public final class AuctionPriceParser {
    private static final String PURCHASE_MARKER = "\u0447\u0442\u043e\u0431\u044b \u043a\u0443\u043f\u0438\u0442\u044c";

    public AuctionPrice parse(class_1799 stack, class_310 client) {
        if (stack == null || stack.method_7960() || client == null) {
            return null;
        }
        List<class_2561> tooltip = class_437.method_25408(client, stack);
        boolean auctionItem = false;
        Integer totalPrice = null;
        Integer unitPrice = null;
        Integer dollarPrice = null;
        for (class_2561 line : tooltip) {
            String text = line.getString();
            String lowerCase = text.toLowerCase(Locale.ROOT);
            auctionItem |= lowerCase.contains(PURCHASE_MARKER);
            if (lowerCase.contains("\u0437\u0430 1 \u0435\u0434")) {
                unitPrice = AuctionPriceParser.parseAfterColon(text);
                continue;
            }
            if (lowerCase.contains("\u0446\u0435\u043d\u0430")) {
                totalPrice = AuctionPriceParser.parseAfterColon(text);
                continue;
            }
            int dollarIndex = text.indexOf(36);
            if (dollarIndex < 0) continue;
            dollarPrice = AuctionPriceParser.parseDigits(text.substring(dollarIndex + 1));
        }
        if (!auctionItem) {
            return null;
        }
        if (totalPrice == null) {
            totalPrice = dollarPrice;
        }
        int count = Math.max(1, stack.method_7947());
        if (unitPrice == null && totalPrice != null) {
            unitPrice = totalPrice / count;
        }
        if (unitPrice == null) {
            return null;
        }
        if (totalPrice == null) {
            totalPrice = (int)Math.min((long)unitPrice.intValue() * (long)count, Integer.MAX_VALUE);
        }
        return new AuctionPrice(totalPrice, unitPrice);
    }

    public boolean isAuctionItem(class_1799 stack, class_310 client) {
        if (stack == null || stack.method_7960() || client == null) {
            return false;
        }
        return class_437.method_25408((class_310)client, (class_1799)stack).stream().map(class_2561::getString).map(text -> text.toLowerCase(Locale.ROOT)).anyMatch(text -> text.contains(PURCHASE_MARKER));
    }

    private static Integer parseAfterColon(String text) {
        int colonIndex = text.lastIndexOf(58);
        return AuctionPriceParser.parseDigits(colonIndex >= 0 ? text.substring(colonIndex + 1) : text);
    }

    private static Integer parseDigits(String text) {
        String digits = text.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return null;
        }
        try {
            return (int)Math.min(Long.parseLong(digits), Integer.MAX_VALUE);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }
}

