/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.module.hud;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.OutgoingChatEvent;
import ru.wexside.misc.AuctionHighlightSettings;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.server.FunTimeServerContext;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.AuctionSlotHighlighter;

public final class AuctionHelperModule
extends Module
implements ConfigSerializable {
    private final NumberSetting tintSpeed;
    private final BooleanSetting affordable;
    private final BooleanSetting tint;
    private final BooleanSetting onlyWhole;
    private final BooleanSetting calculator;
    private final ColorSetting color;
    private final Pattern pattern;
    private final NumberSetting slotsCount;
    private final BooleanSetting enabledSetting;

    public boolean isEnabled() {
        return this.enabledSetting.isEnabled();
    }

    @Override
    protected void initialize() {
        WexSideClient.getSlotHighlightRegistry().setCallback56(new AuctionSlotHighlighter(this::isEnabled, this::getAuctionHighlightSettings));
        this.listen(OutgoingChatEvent.class, this::onOutgoingChatEvent);
    }

    public BooleanSetting getBooleanSetting() {
        return this.enabledSetting;
    }

    public ColorSetting getColorSetting() {
        return this.color;
    }

    public NumberSetting getNumberSetting() {
        return this.slotsCount;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public BooleanSetting getBooleanSetting2() {
        return this.affordable;
    }

    private AuctionHighlightSettings getAuctionHighlightSettings() {
        int n = this.affordable.isEnabled() ? FunTimeServerContext.getBalance() : -1;
        return new AuctionHighlightSettings(this.onlyWhole.isEnabled(), this.affordable.isEnabled(), n, this.slotsCount.getIntValue(), this.color.getColor(), this.tint.isEnabled(), this.tintSpeed.getIntValue());
    }

    private static Long compute(String string) {
        try {
            if (string.contains("*")) {
                String[] stringArray = string.split("\\*");
                return Long.parseLong(stringArray[0].trim()) * Long.parseLong(stringArray[1].trim());
            }
            if (string.contains("/")) {
                String[] stringArray = string.split("/");
                long l = Long.parseLong(stringArray[1].trim());
                if (l == 0L) {
                    l = 1L;
                }
                return Long.parseLong(stringArray[0].trim()) / l;
            }
            if (string.contains("+")) {
                String[] stringArray = string.split("\\+");
                return Long.parseLong(stringArray[0].trim()) + Long.parseLong(stringArray[1].trim());
            }
            if (string.contains("-")) {
                String[] stringArray = string.split("-");
                return Long.parseLong(stringArray[0].trim()) - Long.parseLong(stringArray[1].trim());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public BooleanSetting getBooleanSetting3() {
        return this.calculator;
    }

    public BooleanSetting getBooleanSetting4() {
        return this.onlyWhole;
    }

    private String compute2(String string) {
        Matcher matcher = this.pattern.matcher(string);
        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find()) {
            double d = Double.parseDouble(matcher.group(1).replace(',', '.'));
            long l = 1L;
            for (int i = 0; i < matcher.group(2).length(); ++i) {
                l *= 1000L;
            }
            long l2 = (long)Math.floor(d * (double)l);
            matcher.appendReplacement(stringBuilder, Long.toString(l2));
        }
        matcher.appendTail(stringBuilder);
        return stringBuilder.toString();
    }

    private String compute3(String string) {
        String string2 = this.compute2(string);
        Long l = AuctionHelperModule.compute(string2);
        if (l != null) {
            return Long.toString(l);
        }
        return string2.matches("-?\\d+") ? string2 : null;
    }

    public BooleanSetting getBooleanSetting5() {
        return this.tint;
    }

    public NumberSetting getNumberSetting2() {
        return this.tintSpeed;
    }

    private void onOutgoingChatEvent(OutgoingChatEvent gameEvent15) {
        if (!this.isEnabled() || !this.calculator.isEnabled()) {
            return;
        }
        String string = gameEvent15.getString();
        if (string == null || !string.startsWith("/ah sell ")) {
            return;
        }
        String string2 = string.substring("/ah sell ".length()).trim();
        String string3 = this.compute3(string2);
        if (string3 != null && !string3.equals(string2)) {
            String string4 = string3;
            gameEvent15.setString("/ah sell " + string4);
        }
    }

    public AuctionHelperModule(EventBus eventBus) {
        super(eventBus, "auction_helper", "Auction Helper", "\u041f\u043e\u043c\u043e\u0449\u043d\u0438\u043a \u043d\u0430 \u0430\u0443\u043a\u0446\u0438\u043e\u043d\u0435", ModuleCategory.valueOf("DISPLAY"), new String[0]);
        BooleanSetting booleanSetting;
        NumberSetting numberSetting;
        BooleanSetting toggle;
        BooleanSetting toggle2;
        BooleanSetting toggle3;
        NumberSetting number;
        BooleanSetting toggle4;
        this.enabledSetting = toggle4 = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(toggle4);
        this.slotsCount = number = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 5.0).defaultValue(1.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Slots Count").id("slots_count").description("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u043c\u044b\u0445 \u0441\u0430\u043c\u044b\u0445 \u0434\u0435\u0448\u0451\u0432\u044b\u0445 \u0441\u043b\u043e\u0442\u043e\u0432")).build();
        this.registerSetting(number);
        this.onlyWhole = toggle3 = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Whole Items Only").id("only_whole").description("\u0422\u043e\u043b\u044c\u043a\u043e \u0446\u0435\u043b\u044b\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b")).build();
        this.registerSetting(toggle3);
        this.affordable = toggle2 = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Balance Filter").id("affordable").description("\u0423\u0447\u0438\u0442\u044b\u0432\u0430\u0442\u044c \u0431\u0430\u043b\u0430\u043d\u0441")).build();
        this.registerSetting(toggle2);
        ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Cheapest Color").id("color").description("\u0426\u0432\u0435\u0442 \u043f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0438 \u0441\u0430\u043c\u044b\u0445 \u0434\u0435\u0448\u0451\u0432\u044b\u0445 \u043b\u043e\u0442\u043e\u0432")).build();
        colorSetting.setPrimaryColor(0, -11753627);
        colorSetting.setPrimaryColor(1, -1543135);
        colorSetting.setPrimaryColor(2, -9279489);
        colorSetting.setPrimaryColor(3, -46001);
        colorSetting.setPrimaryColor(4, -13218);
        colorSetting.setPrimaryColor(5, -10582785);
        colorSetting.setPrimaryColor(6, -2732032);
        this.color = colorSetting;
        this.registerSetting(colorSetting);
        this.tint = toggle = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Tint").id("tint").description("\u041c\u0438\u0433\u0430\u043d\u0438\u0435 \u043f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0438")).build();
        this.registerSetting(toggle);
        this.tintSpeed = numberSetting = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 10.0).defaultValue(5.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Tint Speed").id("tint_speed").description("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043c\u0438\u0433\u0430\u043d\u0438\u044f").visibleWhen(toggle::isEnabled)).build();
        this.registerSetting(numberSetting);
        this.calculator = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Calculator").id("calculator").description("\u0412 /ah sell \u0440\u0430\u0437\u0432\u043e\u0440\u0430\u0447\u0438\u0432\u0430\u0435\u0442 1kk \u2192 1000000 \u0438 \u0441\u0447\u0438\u0442\u0430\u0435\u0442 * / + -")).build();
        this.registerSetting(booleanSetting);
        this.pattern = Pattern.compile("(\\d+(?:[.,]\\d+)?)([kK\u043a\u041a]+)");
    }
}

