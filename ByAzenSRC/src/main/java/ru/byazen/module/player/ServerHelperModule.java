/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package ru.byazen.module.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.HudRenderEvent;
import ru.byazen.event.OutgoingChatEvent;
import ru.byazen.misc.AuctionMathExpander;
import ru.byazen.misc.ItemBindBox;
import ru.byazen.misc.ServerHelperShulker;
import ru.byazen.misc.ServerKind;
import ru.byazen.misc.SwapTiming;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.server.LeastPopulatedServerSelector;
import ru.byazen.server.ServerHelperAction;
import ru.byazen.server.ServerHelperActions;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BindSettingBuilder;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.ColorSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.CheapestAuctionHighlighter;
import ru.byazen.util.DonMarketHighlighter;
import ru.byazen.util.FTSnap;
import ru.byazen.util.ItemStatusHudElement;

public class ServerHelperModule
extends Module
implements ConfigSerializable {
    private static final String[] ACTION_NAMES = new String[]{"\u0411\u043e\u0436\u044c\u044f \u0430\u0443\u0440\u0430", "\u0422\u0440\u0430\u043f\u043a\u0430", "\u041f\u043b\u0430\u0441\u0442", "\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", "\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", "\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0438", "\u041e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0441\u043c\u0435\u0440\u0447", "Wind Charge", "\u0417\u0435\u043b\u044c\u0435 \u0410\u0441\u0441\u0430\u0441\u0438\u043d\u0430", "\u0421\u0432\u044f\u0442\u0430\u044f \u0412\u043e\u0434\u0430", "\u0417\u0435\u043b\u044c\u0435 \u0413\u043d\u0435\u0432\u0430", "\u0417\u0435\u043b\u044c\u0435 \u041f\u0430\u043b\u043b\u0430\u0434\u0438\u043d\u0430", "\u0425\u043b\u043e\u043f\u0443\u0448\u043a\u0430", "\u0417\u0435\u043b\u044c\u0435 \u0420\u0430\u0434\u0438\u0430\u0446\u0438\u0438", "\u0417\u0435\u043b\u044c\u0435 \u0421\u043d\u043e\u0442\u0432\u043e\u0440\u043d\u043e\u0433\u043e"};
    static volatile ServerHelperModule serverHelperModule2;
    private final BooleanSetting enabledSetting;
    private final ModeSetting serverMode;
    private final ModeSetting swapMode;
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private final BooleanSetting noLeftHandPlace;
    private final BindSetting openShulker;
    private final BindSetting radialSelector;
    private final MultiSelectSetting selectorItems;
    private final ModeSetting windChargeMode;
    private final BooleanSetting windChargeAutoJump;
    private final ModeSetting buffThrowMode;
    private final BooleanSetting auctionCalculator;
    private final BooleanSetting visualizer;
    private final BooleanSetting visualizerAll;
    private final MultiSelectSetting visualizerItems;
    private final BooleanSetting highlightEmpty;
    private final BooleanSetting visualizePotions;
    private final BooleanSetting highlightCheapest;
    private final ColorSetting cheapestColor;
    private final NumberSetting cheapestCount;
    private final BooleanSetting highlightBestOffer;
    private final ColorSetting bestOfferColor;
    private final NumberSetting bestOfferCount;
    private final Map<String, BindSetting> actionBinds = new LinkedHashMap<String, BindSetting>();
    private final LeastPopulatedServerSelector serverSelector = new LeastPopulatedServerSelector();
    private ItemStatusHudElement inventoryHUD2Impl;
    private ServerHelperShulker serverHelperShulker;
    private AuctionMathExpander auctionMathExpander;
    private FTSnap fTSnap;

    public ServerHelperModule(EventBus eventBus) {
        super(eventBus, "server_helper", "Server Helper", "\u041f\u043e\u043c\u043e\u0449\u043d\u0438\u043a \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u0432 FT/Others: \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b, \u0448\u0430\u043b\u043a\u0435\u0440, \u043a\u0430\u043b\u044c\u043a\u0443\u043b\u044f\u0442\u043e\u0440, \u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440", ModuleCategory.valueOf("PLAYER"), new String[0]);
        serverHelperModule2 = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.serverMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("FT", "Others").defaultOption("FT").name("Server Mode").id("server_mode").description("\u0420\u0435\u0436\u0438\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u0430")).build();
        this.registerSetting(this.serverMode);
        this.swapMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Default", "Legit", "FS").defaultOption("Default").name("Mode").id("swap_mode").description("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0441\u0432\u0430\u043f\u0430: Default - \u043c\u0433\u043d\u043e\u0432\u0435\u043d\u043d\u043e, Legit - \u0441 \u0437\u0430\u0434\u0435\u0440\u0436\u043a\u043e\u0439")).build();
        this.registerSetting(this.swapMode);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0414\u043e\u0441\u0442\u0430\u0432\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0438\u0437 \u043c\u0435\u0448\u043a\u0430 \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
        this.noLeftHandPlace = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("No Left Hand Place").id("no_left_hand_place").description("\u041d\u0435 \u0441\u0442\u0430\u0432\u0438\u0442\u044c \u0441\u0444\u0435\u0440\u044b (\u0433\u043e\u043b\u043e\u0432\u0443) \u0438\u0437 \u043e\u0444\u0444-\u0445\u0435\u043d\u0434\u0430 (\u043b\u0435\u0432\u043e\u0439 \u0440\u0443\u043a\u0438)")).build();
        this.registerSetting(this.noLeftHandPlace);
        this.openShulker = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(ignored -> this.openShulker()).name("Open Shulker").id("open_shulker").description("\u041a\u043d\u043e\u043f\u043a\u0430 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u0448\u0430\u043b\u043a\u0435\u0440\u0430 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.openShulker);
        this.radialSelector = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(ignored -> this.openRadialSelector()).name("Radial Selector").id("radial_selector").description("\u041a\u0440\u0443\u0433\u043e\u0432\u043e\u0439 \u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432")).build();
        this.registerSetting(this.radialSelector);
        MultiSelectSetting selectorSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options(ACTION_NAMES).selectAll(false).optionListEnabled(false).name("Selector Items").id("selector_items").description("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b, \u043e\u0442\u043e\u0431\u0440\u0430\u0436\u0430-\n\u0435\u043c\u044b\u0435 \u0432 \u043a\u0440\u0443\u0433\u043e\u0432\u043e\u043c\n\u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440\u0435")).build();
        selectorSetting.setOptions(new String[0]);
        this.selectorItems = selectorSetting;
        this.registerSetting(selectorSetting);
        this.registerAction("GodsAura", "action_gods_aura", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0411\u043e\u0436\u044c\u044f \u0430\u0443\u0440\u0430", 0);
        this.registerAction("Trap", "action_trap", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0422\u0440\u0430\u043f\u043a\u0430", 1);
        this.registerAction("Plast", "action_plast", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u041f\u043b\u0430\u0441\u0442", 2);
        this.registerAction("Disorientation", "action_disorientation", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", 3);
        this.registerAction("VisibleDust", "action_visible_dust", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", 4);
        this.registerAction("FreezeBall", "action_freezeball", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0438", 5);
        this.registerAction("FieryTornado", "action_fiery_tornado", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u041e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0441\u043c\u0435\u0440\u0447", 6);
        this.registerAction("WindCharge", "action_wind_charge", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: Wind Charge", 7);
        this.windChargeMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("\u041f\u043e\u0434 \u0441\u0435\u0431\u044f", "\u041f\u043e \u043f\u0440\u0438\u0446\u0435\u043b\u0443").defaultOption("\u041f\u043e\u0434 \u0441\u0435\u0431\u044f").name("Wind Charge").id("wind_charge_mode").description("\u041d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0431\u0440\u043e\u0441\u043a\u0430 \u0441\u043d\u0430\u0440\u044f\u0434\u0430 \u0432\u0435\u0442\u0440\u0430")).build();
        this.registerSetting(this.windChargeMode);
        this.windChargeAutoJump = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Auto Jump").id("wind_charge_auto_jump").description("\u041f\u043e\u0434\u043f\u0440\u044b\u0433\u0438\u0432\u0430\u043d\u0438\u0435 \u0432 \u043c\u043e\u043c\u0435\u043d\u0442 \u0431\u0440\u043e\u0441\u043a\u0430 \u043f\u043e\u0434 \u0441\u0435\u0431\u044f \u0434\u043b\u044f \u0431\u043e\u043b\u044c\u0448\u0435\u0439 \u0432\u044b\u0441\u043e\u0442\u044b").visibleWhen(() -> "\u041f\u043e\u0434 \u0441\u0435\u0431\u044f".equals(this.windChargeMode.getSelectedOption()))).build();
        this.registerSetting(this.windChargeAutoJump);
        this.buffThrowMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("\u041f\u043e \u043f\u0440\u0438\u0446\u0435\u043b\u0443", "\u041f\u043e\u0434 \u0441\u0435\u0431\u044f").defaultOption("\u041f\u043e \u043f\u0440\u0438\u0446\u0435\u043b\u0443").name("\u041a\u0443\u0434\u0430 \u0431\u0440\u043e\u0441\u0430\u0442\u044c \u0431\u0430\u0444\u044b").id("buff_throw_mode").description("\u041d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0431\u0440\u043e\u0441\u043a\u0430 \u0431\u0430\u0444\u0444-\u0437\u0435\u043b\u0438\u0439 (\u0434\u0435\u0431\u0430\u0444\u0444\u044b \u0432\u0441\u0435\u0433\u0434\u0430 \u043f\u043e \u043f\u0440\u0438\u0446\u0435\u043b\u0443)")).build();
        this.registerSetting(this.buffThrowMode);
        this.registerAction("PotionAssassin", "action_potion_assassin", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0417\u0435\u043b\u044c\u0435 \u0410\u0441\u0441\u0430\u0441\u0438\u043d\u0430", 8);
        this.registerAction("PotionHolyWater", "action_potion_holy_water", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0421\u0432\u044f\u0442\u0430\u044f \u0412\u043e\u0434\u0430", 9);
        this.registerAction("PotionRage", "action_potion_rage", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0417\u0435\u043b\u044c\u0435 \u0413\u043d\u0435\u0432\u0430", 10);
        this.registerAction("PotionPaladin", "action_potion_paladin", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0417\u0435\u043b\u044c\u0435 \u041f\u0430\u043b\u043b\u0430\u0434\u0438\u043d\u0430", 11);
        this.registerAction("PotionPopper", "action_potion_popper", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0425\u043b\u043e\u043f\u0443\u0448\u043a\u0430", 12);
        this.registerAction("PotionRadiation", "action_potion_radiation", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0417\u0435\u043b\u044c\u0435 \u0420\u0430\u0434\u0438\u0430\u0446\u0438\u0438", 13);
        this.registerAction("PotionDrowsiness", "action_potion_drowsiness", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c: \u0417\u0435\u043b\u044c\u0435 \u0421\u043d\u043e\u0442\u0432\u043e\u0440\u043d\u043e\u0433\u043e", 14);
        this.auctionCalculator = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Auction Calculator").id("auction_calculator").description("\u0412 /ah \u0437\u0430\u043c\u0435\u043d\u044f\u0435\u0442 N*M \u043d\u0430 \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442")).build();
        this.registerSetting(this.auctionCalculator);
        this.visualizer = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Visualizer").id("visualizer").description("\u0421\u0435\u0442\u043a\u0430 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432 \u0441 \u0431\u0438\u043d\u0434\u0430\u043c\u0438")).build();
        this.registerSetting(this.visualizer);
        this.visualizerAll = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0412\u0441\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b").id("visualizer_all").description("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0432\u0441\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0432 \u0441\u0435\u0442\u043a\u0435").visibleWhen(this.visualizer::isEnabled)).build();
        this.registerSetting(this.visualizerAll);
        MultiSelectSetting visualizerItemsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options(ACTION_NAMES).selectAll(true).optionListEnabled(false).name("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b").id("visualizer_items").description("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0432 \u0441\u0435\u0442\u043a\u0435").visibleWhen(() -> this.visualizer.isEnabled() && !this.visualizerAll.isEnabled())).build();
        visualizerItemsSetting.setOptions(ACTION_NAMES);
        this.visualizerItems = visualizerItemsSetting;
        this.registerSetting(visualizerItemsSetting);
        this.highlightEmpty = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Highlight Empty").id("highlight_empty").description("\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u044e\u0449\u0438\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432").visibleWhen(this.visualizer::isEnabled)).build();
        this.registerSetting(this.highlightEmpty);
        this.visualizePotions = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Highlight Potions").id("visualize_potions").description("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0434\u043e\u043d-\u0437\u0435\u043b\u044c\u044f \u0434\u0430\u0436\u0435 \u0431\u0435\u0437 \u0431\u0438\u043d\u0434\u0430").visibleWhen(this.visualizer::isEnabled)).build();
        this.registerSetting(this.visualizePotions);
        this.highlightCheapest = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Highlight Cheapest").id("highlight_cheapest").description("\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430 \u0441\u0430\u043c\u044b\u0445 \u0434\u0435\u0448\u0451\u0432\u044b\u0445 \u043b\u043e\u0442\u043e\u0432 \u0432 /ah")).build();
        this.registerSetting(this.highlightCheapest);
        ColorSetting cheapestColorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Cheapest Color").id("cheapest_color").description("\u0426\u0432\u0435\u0442 \u043f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0438 \u0434\u0435\u0448\u0451\u0432\u044b\u0445 \u043b\u043e\u0442\u043e\u0432").visibleWhen(this.highlightCheapest::isEnabled)).build();
        cheapestColorSetting.setPrimaryColor(0, -11753627);
        cheapestColorSetting.setPrimaryColor(1, -1543135);
        cheapestColorSetting.setPrimaryColor(2, -9279489);
        cheapestColorSetting.setPrimaryColor(3, -46001);
        cheapestColorSetting.setPrimaryColor(4, -13218);
        cheapestColorSetting.setPrimaryColor(5, -10582785);
        cheapestColorSetting.setPrimaryColor(6, -2732032);
        this.cheapestColor = cheapestColorSetting;
        this.registerSetting(cheapestColorSetting);
        this.cheapestCount = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 5.0).defaultValue(1.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Cheapest Count").id("cheapest_count").description("\u0421\u043a\u043e\u043b\u044c\u043a\u043e \u043b\u043e\u0442\u043e\u0432 \u043f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0442\u044c").visibleWhen(this.highlightCheapest::isEnabled)).build();
        this.registerSetting(this.cheapestCount);
        this.highlightBestOffer = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Highlight Best Offer").id("highlight_best_offer").description("\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430 \u043b\u0443\u0447\u0448\u0438\u0445 \u043f\u0440\u0435\u0434\u043b\u043e\u0436\u0435\u043d\u0438\u0439 \u0432 \u0414\u043e\u043d\u041c\u0430\u0440\u043a\u0435\u0442\u0435")).build();
        this.registerSetting(this.highlightBestOffer);
        ColorSetting bestOfferColorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Best Offer Color").id("best_offer_color").description("\u0426\u0432\u0435\u0442 \u043f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0438 \u043b\u0443\u0447\u0448\u0438\u0445 \u043f\u0440\u0435\u0434\u043b\u043e\u0436\u0435\u043d\u0438\u0439").visibleWhen(this.highlightBestOffer::isEnabled)).build();
        bestOfferColorSetting.setPrimaryColor(0, -11753627);
        bestOfferColorSetting.setPrimaryColor(1, -1543135);
        bestOfferColorSetting.setPrimaryColor(2, -9279489);
        bestOfferColorSetting.setPrimaryColor(3, -46001);
        bestOfferColorSetting.setPrimaryColor(4, -13218);
        bestOfferColorSetting.setPrimaryColor(5, -10582785);
        bestOfferColorSetting.setPrimaryColor(6, -2732032);
        this.bestOfferColor = bestOfferColorSetting;
        this.registerSetting(bestOfferColorSetting);
        this.bestOfferCount = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 5.0).defaultValue(1.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Best Offer Count").id("best_offer_count").description("\u0421\u043a\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0435\u0434\u043b\u043e\u0436\u0435\u043d\u0438\u0439 \u043f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0442\u044c").visibleWhen(this.highlightBestOffer::isEnabled)).build();
        this.registerSetting(this.bestOfferCount);
        this.buildVisualizer();
        this.fTSnap = new FTSnap(this.getEventBus(), () -> ServerKind.parse(this.serverMode.getSelectedOption()), this::swapTiming, () -> "\u041f\u043e\u0434 \u0441\u0435\u0431\u044f".equals(this.buffThrowMode.getSelectedOption()), this::throwAtFeet, () -> this.windChargeAutoJump.isEnabled(), () -> this.fromBundle.isEnabled(), () -> this.ftMode.isEnabled());
        this.serverHelperShulker = new ServerHelperShulker();
        this.auctionMathExpander = new AuctionMathExpander(() -> this.auctionCalculator.isEnabled());
        ByAzenClient.getSlotHighlightRegistry().setCallback56(new CheapestAuctionHighlighter(() -> this.isEnabled() && this.highlightCheapest.isEnabled(), () -> this.cheapestCount.getIntValue(), () -> this.cheapestColor.getColor()));
        ByAzenClient.getSlotHighlightRegistry().setCallback56(new DonMarketHighlighter(() -> this.isEnabled() && this.highlightBestOffer.isEnabled(), () -> this.bestOfferCount.getIntValue(), () -> this.bestOfferColor.getColor()));
    }

    @Override
    protected void initialize() {
        this.listen(HudRenderEvent.class, event -> {
            if (this.inventoryHUD2Impl != null) {
                this.inventoryHUD2Impl.renderFrame();
            }
        });
        this.listen(ClientTickEvent.class, event -> this.serverSelector.tick());
        this.listen(OutgoingChatEvent.class, event -> {
            if (this.auctionMathExpander != null) {
                this.auctionMathExpander.onOutgoingChat((OutgoingChatEvent)event);
            }
        });
    }

    public boolean isEnabled() {
        return this.enabledSetting.isEnabled();
    }

    public BooleanSetting getBooleanSetting() {
        return this.enabledSetting;
    }

    public ColorSetting getColorSetting() {
        return this.cheapestColor;
    }

    public ItemStatusHudElement getInventoryHUD2Impl() {
        return this.inventoryHUD2Impl;
    }

    public MultiSelectSetting getMultiSelectSetting() {
        return this.visualizerItems;
    }

    public BooleanSetting getBooleanSetting2() {
        return this.visualizerAll;
    }

    public FTSnap getFTSnap() {
        return this.fTSnap;
    }

    public Map<String, BindSetting> getMap() {
        return this.actionBinds;
    }

    public BooleanSetting getBooleanSetting3() {
        return this.windChargeAutoJump;
    }

    public BooleanSetting getBooleanSetting4() {
        return this.highlightEmpty;
    }

    public ModeSetting getModeSetting() {
        return this.serverMode;
    }

    public BooleanSetting getBooleanSetting5() {
        return this.highlightCheapest;
    }

    public BindSetting getBindSetting() {
        return this.radialSelector;
    }

    public ServerHelperShulker getServerHelperShulker() {
        return this.serverHelperShulker;
    }

    public static void tick2() {
        ServerHelperModule module = serverHelperModule2;
        if (module != null) {
            module.serverSelector.start();
        }
    }

    public BooleanSetting getBooleanSetting6() {
        return this.highlightBestOffer;
    }

    public BooleanSetting getBooleanSetting7() {
        return this.noLeftHandPlace;
    }

    public BooleanSetting getBooleanSetting8() {
        return this.fromBundle;
    }

    public BindSetting compute(String name) {
        return this.actionBinds.get(name);
    }

    public NumberSetting getNumberSetting() {
        return this.bestOfferCount;
    }

    public void queueAction(ServerHelperAction action) {
        if (this.fTSnap != null) {
            this.fTSnap.queueAction(action);
        }
    }

    public BooleanSetting getBooleanSetting9() {
        return this.visualizer;
    }

    public BindSetting getBindSetting2() {
        return this.openShulker;
    }

    public static String getString2() {
        ServerHelperModule module = serverHelperModule2;
        if (module == null || !module.isEnabled() || module.openShulker.getBindInput().isUnbound()) {
            return null;
        }
        return module.openShulker.getKeyDisplayName();
    }

    public ColorSetting getColorSetting2() {
        return this.bestOfferColor;
    }

    public BooleanSetting getBooleanSetting10() {
        return this.auctionCalculator;
    }

    public BooleanSetting getBooleanSetting11() {
        return this.ftMode;
    }

    public BooleanSetting getBooleanSetting12() {
        return this.visualizePotions;
    }

    public LeastPopulatedServerSelector getServerSelector() {
        return this.serverSelector;
    }

    public ServerKind getServerKind() {
        return ServerKind.parse(this.serverMode.getSelectedOption());
    }

    public ModeSetting getModeSetting2() {
        return this.buffThrowMode;
    }

    public static boolean isEnabled3() {
        ServerHelperModule module = serverHelperModule2;
        return module != null && module.isEnabled() && module.noLeftHandPlace.isEnabled();
    }

    public ModeSetting getModeSetting3() {
        return this.swapMode;
    }

    public AuctionMathExpander getAuctionMathExpander() {
        return this.auctionMathExpander;
    }

    public List<ServerHelperAction> getList() {
        ArrayList<ServerHelperAction> selected = new ArrayList<ServerHelperAction>();
        for (ServerHelperAction action : ServerHelperActions.ALL) {
            if (!this.selectorItems.getSelectedOptions().contains(action.selectorLabel())) continue;
            selected.add(action);
        }
        return selected;
    }

    public ModeSetting getModeSetting4() {
        return this.windChargeMode;
    }

    public NumberSetting getNumberSetting2() {
        return this.cheapestCount;
    }

    public MultiSelectSetting getMultiSelectSetting2() {
        return this.selectorItems;
    }

    private SwapTiming swapTiming() {
        String mode = this.swapMode.getSelectedOption();
        if (mode != null && mode.equalsIgnoreCase("Legit")) {
            return SwapTiming.LEGIT;
        }
        if (mode != null && mode.equalsIgnoreCase("FS")) {
            return SwapTiming.FUNTIME;
        }
        return SwapTiming.DEFAULT;
    }

    private boolean throwAtFeet() {
        return "\u041f\u043e\u0434 \u0441\u0435\u0431\u044f".equals(this.windChargeMode.getSelectedOption());
    }

    private boolean isVisualizerItemEnabled(ServerHelperAction action) {
        return this.visualizerAll.isEnabled() || this.visualizerItems.getSelectedOptions().contains(action.selectorLabel());
    }

    private boolean isVisualizerEnabled() {
        return this.enabledSetting.isEnabled() && this.visualizer.isEnabled();
    }

    private void openShulker() {
        if (!this.isEnabled() || this.serverHelperShulker == null) {
            return;
        }
        this.serverHelperShulker.update();
    }

    private void openRadialSelector() {
        if (!this.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || this.getList().isEmpty()) {
            return;
        }
        client.method_1507((class_437)new RadialSelectorScreen(this.radialSelector, this::getList, this::getServerKind, this::queueAction));
    }

    private boolean matchesAction(ServerHelperAction action, class_1799 stack) {
        if (stack == null || stack.method_7960() || action == null) {
            return false;
        }
        if (action.matchByItem()) {
            return stack.method_31574(action.icon());
        }
        String name = stack.method_7964().getString();
        if (this.getServerKind() == ServerKind.OTHERS) {
            for (String tag : action.alternateServerTags()) {
                if (!name.contains(tag)) continue;
                return true;
            }
            return false;
        }
        String ftTag = action.generalServerTag();
        return ftTag != null && !ftTag.isEmpty() && name.contains(ftTag);
    }

    private void useAction(ServerHelperAction action) {
        if (!this.isEnabled() || action == null) {
            return;
        }
        this.queueAction(action);
    }

    private ServerHelperAction actionAt(int index) {
        List<ServerHelperAction> actions = ServerHelperActions.ALL;
        if (index < 0 || index >= actions.size()) {
            return null;
        }
        return actions.get(index);
    }

    private void registerAction(String name, String id, String description, int index) {
        ServerHelperAction action = this.actionAt(index);
        BindSetting bind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(ignored -> this.useAction(action)).name(name).id(id).description(description)).build();
        this.registerSetting(bind);
        if (action != null) {
            this.actionBinds.put(action.selectorLabel(), bind);
        } else if (index >= 0 && index < ACTION_NAMES.length) {
            this.actionBinds.put(ACTION_NAMES[index], bind);
        }
    }

    private void buildVisualizer() {
        ArrayList<ItemBindBox> boxes = new ArrayList<ItemBindBox>();
        List<ServerHelperAction> actions = ServerHelperActions.ALL;
        for (ServerHelperAction action : actions) {
            BindSetting bind = this.actionBinds.get(action.selectorLabel());
            if (bind == null) continue;
            boxes.add(new ItemBindBox(action.icon(), bind, stack -> this.matchesAction(action, (class_1799)stack), () -> this.isVisualizerItemEnabled(action)));
        }
        this.inventoryHUD2Impl = new ItemStatusHudElement("Server Helper", this::isVisualizerEnabled, boxes, this.highlightEmpty::isEnabled);
    }

    public static class RadialSelectorScreen
    extends class_437 {
        private final BindSetting bind;
        private final Supplier<List<ServerHelperAction>> actions;
        private final Supplier<ServerKind> serverKind;
        private final Consumer<ServerHelperAction> onSelect;

        public RadialSelectorScreen(BindSetting bind, Supplier<List<ServerHelperAction>> actions, Supplier<ServerKind> serverKind, Consumer<ServerHelperAction> onSelect) {
            super((class_2561)class_2561.method_43473());
            this.bind = bind;
            this.actions = actions;
            this.serverKind = serverKind;
            this.onSelect = onSelect;
        }

        public BindSetting bind() {
            return this.bind;
        }

        public List<ServerHelperAction> actions() {
            return this.actions.get();
        }

        public ServerKind serverKind() {
            return this.serverKind.get();
        }

        public void method_25393() {
            if (this.bind == null || !this.bind.isPressed()) {
                this.method_25419();
            }
        }

        public boolean method_25421() {
            return false;
        }

        public void select(ServerHelperAction action) {
            if (action != null && this.onSelect != null) {
                this.onSelect.accept(action);
            }
            this.method_25419();
        }
    }
}

