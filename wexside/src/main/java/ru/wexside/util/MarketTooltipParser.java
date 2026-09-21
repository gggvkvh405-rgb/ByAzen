/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package ru.wexside.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;

public final class MarketTooltipParser {
    private static final Pattern NUMBER = Pattern.compile("\\d+");
    private static final String BUY_PROMPT = "\u043d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u043a\u0443\u043f\u0438\u0442\u044c";

    public DonMarketOffer parseDonMarketOffer(class_1799 stack, class_310 client) {
        if (stack == null || stack.method_7960() || client == null) {
            return null;
        }
        Integer balance = null;
        Integer exchangeRate = null;
        Integer price = null;
        boolean purchasable = false;
        for (class_2561 line : class_437.method_25408((class_310)client, (class_1799)stack)) {
            String text = line.getString().toLowerCase();
            if (text.contains("\u0431\u0438\u0440\u0436\u0430 \u0431\u0430\u043b\u0430\u043d\u0441:")) {
                balance = MarketTooltipParser.firstNumber(text);
                continue;
            }
            if (text.contains("\u043a\u0443\u0440\u0441:")) {
                exchangeRate = MarketTooltipParser.firstNumber(text);
                continue;
            }
            if (text.contains("\u0446\u0435\u043d\u0430:")) {
                price = MarketTooltipParser.firstNumber(text);
                continue;
            }
            if (!text.contains(BUY_PROMPT)) continue;
            purchasable = true;
        }
        if (!purchasable || balance == null || exchangeRate == null || price == null || balance < price) {
            return null;
        }
        return new DonMarketOffer(balance, exchangeRate, price);
    }

    public Integer parseAuctionPrice(class_1799 stack, class_310 client) {
        if (stack == null || stack.method_7960() || client == null) {
            return null;
        }
        boolean purchasable = false;
        Integer highestDollarValue = null;
        List<class_2561> tooltip = class_437.method_25408(client, stack);
        for (class_2561 line : tooltip) {
            Integer value;
            int dollar;
            String text = line.getString();
            if (text.toLowerCase().contains(BUY_PROMPT)) {
                purchasable = true;
            }
            if ((dollar = text.indexOf(36)) < 0 || (value = MarketTooltipParser.parseDigits(text.substring(dollar + 1))) == null || highestDollarValue != null && value <= highestDollarValue) continue;
            highestDollarValue = value;
        }
        return purchasable ? highestDollarValue : null;
    }

    public Integer parseUnitAuctionPrice(class_1799 stack, class_310 client) {
        Integer total = this.parseAuctionPrice(stack, client);
        return total == null ? null : Integer.valueOf(total / Math.max(1, stack.method_7947()));
    }

    private static Integer firstNumber(String text) {
        Matcher matcher = NUMBER.matcher(text);
        return matcher.find() ? MarketTooltipParser.parseDigits(matcher.group()) : null;
    }

    private static Integer parseDigits(String text) {
        String digits = text.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(digits);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    public record DonMarketOffer(int balance, int exchangeRate, int price) {
    }
}

