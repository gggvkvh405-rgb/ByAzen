package rtx.byazen.api.modules.impl.Utils;
import rtx.byazen.api.events.EventHandler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import rtx.byazen.api.events.impl.render.TextFactoryEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.MultiModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.TextSetting;
import rtx.byazen.api.party.PartyClient;
import rtx.byazen.api.party.PartyMember;
import rtx.byazen.utils.network.Network;
import rtx.byazen.utils.rank.ReallyWorldRanks;
import rtx.byazen.utils.render.ChatMaskScope;
import rtx.byazen.utils.storage.friend.FriendUtils;

public final class StreamerMode
extends Module {
    private static final Pattern IP_PATTERN = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}(?::\\d{1,5})?\\b");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(?<!\\d)\\d{4,}(?!\\d)");
    private static final String HIDE_SELF = "\u0421\u0435\u0431\u044f";
    private static final String HIDE_FRIENDS = "\u0414\u0440\u0443\u0437\u0435\u0439";
    private static final String HIDE_PARTY = "\u041f\u0430\u0442\u0438";
    private static StreamerMode instance;
    private final SeparatorSetting namesSeparator = new SeparatorSetting("\u041d\u0438\u043a");
    private final MultiModeSetting hideWho = new MultiModeSetting("\u041a\u043e\u0433\u043e \u0441\u043a\u0440\u044b\u0432\u0430\u0442\u044c", "\u041a\u0430\u043a\u0438\u0435 \u0438\u043c\u0435\u043d\u0430 \u043c\u0430\u0441\u043a\u0438\u0440\u043e\u0432\u0430\u0442\u044c", new String[]{"\u0421\u0435\u0431\u044f", "\u0414\u0440\u0443\u0437\u0435\u0439", "\u041f\u0430\u0442\u0438"}, "\u0421\u0435\u0431\u044f", "\u0414\u0440\u0443\u0437\u0435\u0439", "\u041f\u0430\u0442\u0438");
    private final TextSetting replaceName = new TextSetting("\u0417\u0430\u043c\u0435\u043d\u044f\u0442\u044c \u0438\u043c\u0435\u043d\u0430 \u043d\u0430", "\u0422\u0435\u043a\u0441\u0442 \u0434\u043b\u044f \u043f\u043e\u0434\u043c\u0435\u043d\u044b \u0441\u043a\u0440\u044b\u0432\u0430\u0435\u043c\u044b\u0445 \u0438\u043c\u0451\u043d").setPlaceholder("Protected").lengthBounds(0, 32).visible(() -> !this.hideWho.getSelected().isEmpty());
    private final SeparatorSetting coordsSeparator = new SeparatorSetting("\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b");
    private final BooleanSetting hideCoordsSetting = new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b", "\u041c\u0430\u0441\u043a\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u0432 HUD \u0438 F3 (#, #, #).", true);
    private final SeparatorSetting chatSeparator = new SeparatorSetting("\u0427\u0430\u0442 \u0438 \u0442\u0435\u043a\u0441\u0442");
    private final BooleanSetting maskIps = new BooleanSetting("\u041f\u0440\u044f\u0442\u0430\u0442\u044c IP", "\u0417\u0430\u043c\u0435\u043d\u044f\u0442\u044c \u0430\u0434\u0440\u0435\u0441\u0430 \u0432\u0438\u0434\u0430 123.45.67.89 \u043d\u0430 \u0433\u043b\u0443\u0448\u043a\u0443.", true);
    private final BooleanSetting maskAmounts = new BooleanSetting("\u041f\u0440\u044f\u0442\u0430\u0442\u044c \u0441\u0443\u043c\u043c\u044b", "\u0417\u0430\u043c\u0435\u043d\u044f\u0442\u044c \u0434\u043b\u0438\u043d\u043d\u044b\u0435 \u0447\u0438\u0441\u043b\u0430 (\u0431\u0430\u043b\u0430\u043d\u0441, \u043f\u0435\u0440\u0435\u0432\u043e\u0434\u044b) \u043d\u0430 \u0431\u043b\u043e\u043a\u0438.", false);
    private final BooleanSetting chatMask = new BooleanSetting("\u0417\u0430\u043c\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0447\u0430\u0442", "\u0421\u0442\u0440\u043e\u043a\u0438 \u0447\u0430\u0442\u0430 \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435 \u0437\u0430\u043a\u0440\u044b\u0432\u0430\u044e\u0442\u0441\u044f \u0431\u043b\u043e\u043a\u0430\u043c\u0438 - \u0434\u043b\u044f \u0441\u0442\u0440\u0438\u043c\u0430.", false);
    private final BindSetting revealChat = new BindSetting("\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u0447\u0430\u0442", "\u0423\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0439\u0442\u0435 \u043a\u043b\u0430\u0432\u0438\u0448\u0443, \u0447\u0442\u043e\u0431\u044b \u0443\u0432\u0438\u0434\u0435\u0442\u044c \u0437\u0430\u043c\u0430\u0437\u0430\u043d\u043d\u044b\u0435 \u0441\u0442\u0440\u043e\u043a\u0438.").visible(() -> this.chatMask.getValue());
    private final SeparatorSetting rankSeparator = new SeparatorSetting("\u0420\u0430\u043d\u0433 ReallyWorld");
    private final ModeSetting customRank = new ModeSetting("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u0440\u0430\u043d\u0433", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u044b\u0439 \u0434\u043e\u043d\u0430\u0442 ReallyWorld \u0432\u043c\u0435\u0441\u0442\u043e \u0441\u0432\u043e\u0435\u0433\u043e (\u0442\u043e\u043b\u044c\u043a\u043e \u0443 \u0432\u0430\u0441).", "\u0412\u044b\u043a\u043b", StreamerMode.buildRankOptions()).visibleWhen(Network::isReallyWorld);
    private Pattern cachedPattern;
    private long patternCacheAt;

    public StreamerMode() {
        super("Streamer Mode", "\u0421\u043a\u0440\u044b\u0432\u0430\u0435\u0442 \u0432\u0430\u0448 \u043d\u0438\u043a, \u043d\u0438\u043a\u0438 \u0434\u0440\u0443\u0437\u0435\u0439/\u043f\u0430\u0442\u0438 \u0438 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u0432 \u043e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0435\u043c\u043e\u043c \u0442\u0435\u043a\u0441\u0442\u0435.", Category.UTILS);
        this.register(this.namesSeparator, this.hideWho, this.replaceName, this.coordsSeparator, this.hideCoordsSetting, this.chatSeparator, this.maskIps, this.maskAmounts, this.chatMask, this.revealChat, this.rankSeparator, this.customRank);
        instance = this;
    }

    private String replacement() {
        String string = this.replaceName.getValue();
        return string == null || string.isEmpty() ? "Protected" : string;
    }

    private static void addName(List<String> list, String string, String string2) {
        if (string == null || string.isBlank()) {
            return;
        }
        if (string2 != null && string.equalsIgnoreCase(string2)) {
            return;
        }
        for (String string3 : list) {
            if (!string3.equalsIgnoreCase(string)) continue;
            return;
        }
        list.add(string);
    }

    public static boolean active() {
        return instance != null && instance.isEnabled();
    }

    /** Надо ли замазывать строки чата прямо сейчас (учитывает клавишу «показать чат»). */
    public static boolean chatMaskActive() {
        if (instance == null || !instance.isEnabled() || !instance.chatMask.getValue()) {
            return false;
        }
        if (instance.revealChat.isBound()) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient != null && instance.revealChat.getValue().isDown(minecraftClient.getWindow().getHandle())) {
                return false;
            }
        }
        return true;
    }

    /** Блоки вместо букв: замазывание строки чата с сохранением длины. */
    public static String blocks(String string) {
        StringBuilder stringBuilder = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            stringBuilder.append(c == ' ' ? ' ' : '\u2588');
        }
        return stringBuilder.toString();
    }

    /** Текущая подмена имён (по умолчанию Protected) - используется водяным знаком и тостами. */
    public static String replacementName() {
        return instance == null ? "Protected" : instance.replacement();
    }

    /** Прятать ли координаты в HUD и F3. */
    public boolean hideCoordinates() {
        return this.hideCoordsSetting.getValue();
    }

    /** Подменённое имя для показа (или исходное, если стрим-режим выключен). */
    public static String maskedName(String string) {
        if (string == null || string.isBlank() || !StreamerMode.active()) {
            return string;
        }
        return StreamerMode.replacementName();
    }

    public static Text applySelfRank(Text text) {
        if (!StreamerMode.active()) {
            return text;
        }
        String string = StreamerMode.instance.customRank.getSelected();
        if (string == null || string.isEmpty() || string.equals("\u0412\u044b\u043a\u043b")) {
            return text;
        }
        return ReallyWorldRanks.applySelfRank(text, string);
    }

    public static Text applySelfRankInChat(Text text) {
        String string;
        if (!StreamerMode.active() || text == null) {
            return text;
        }
        String string2 = StreamerMode.instance.customRank.getSelected();
        if (string2 == null || string2.isEmpty() || string2.equals("\u0412\u044b\u043a\u043b")) {
            return text;
        }
        String string3 = string = StreamerMode.instance.mc.player != null && StreamerMode.instance.mc.player.getGameProfile() != null ? StreamerMode.instance.mc.player.getGameProfile().name() : null;
        if (string == null || string.isBlank() || !ReallyWorldRanks.glyphPrecedesNick(text.getString(), string)) {
            return text;
        }
        return ReallyWorldRanks.applySelfRank(text, string2);
    }

    @EventHandler
    public void onTextFactory(TextFactoryEvent textFactoryEvent) {
        String string = textFactoryEvent.getText();
        if (string == null || string.isEmpty()) {
            return;
        }
        String string2 = string;
        if (this.maskIps.getValue()) {
            string2 = IP_PATTERN.matcher(string2).replaceAll(Matcher.quoteReplacement(this.replacement()));
        }
        if (this.maskAmounts.getValue()) {
            string2 = AMOUNT_PATTERN.matcher(string2).replaceAll(Matcher.quoteReplacement("\u2588\u2588\u2588\u2588\u2588"));
        }
        Pattern pattern = this.protectedPattern();
        if (pattern != null) {
            string2 = pattern.matcher(string2).replaceAll(Matcher.quoteReplacement(this.replacement()));
        }
        if (this.chatMask.getValue() && ChatMaskScope.active() && StreamerMode.chatMaskActive()) {
            string2 = StreamerMode.blocks(string2);
        }
        if (!string2.equals(string)) {
            textFactoryEvent.setText(string2);
        }
    }

    private static String[] buildRankOptions() {
        List<String> list = ReallyWorldRanks.orderedLabels();
        String[] stringArray = new String[list.size() + 1];
        stringArray[0] = "\u0412\u044b\u043a\u043b";
        for (int i = 0; i < list.size(); ++i) {
            stringArray[i + 1] = list.get(i);
        }
        return stringArray;
    }

    private Set<String> onlineFriends() {
        List list = FriendUtils.getFriendNames();
        if (list.isEmpty() || this.mc.player == null || this.mc.player.networkHandler == null) {
            return Set.of();
        }
        HashSet<String> hashSet = new HashSet<String>();
        for (Object object : list) {
            if (object == null || ((String)object).isBlank()) continue;
            hashSet.add(((String)object).toLowerCase(Locale.ROOT));
        }
        HashSet hashSet2 = new HashSet();
        for (PlayerListEntry playerListEntry : this.mc.player.networkHandler.getPlayerList()) {
            String string;
            if (playerListEntry.getProfile() == null || playerListEntry.getProfile().name() == null || !hashSet.contains((string = playerListEntry.getProfile().name()).toLowerCase(Locale.ROOT))) continue;
            hashSet2.add(string);
        }
        return hashSet2;
    }

    /*
     * WARNING - void declaration
     */
    private Pattern buildPattern() {
        String string = this.mc.getSession() != null ? this.mc.getSession().getUsername() : null;
        String string2 = this.mc.player != null && this.mc.player.getGameProfile() != null ? this.mc.player.getGameProfile().name() : null;
        String string3 = string != null ? string : string2;
        ArrayList<String> arrayList = new ArrayList<String>();
        if (this.hideWho.isSelected(HIDE_SELF)) {
            StreamerMode.addName(arrayList, string, null);
            StreamerMode.addName(arrayList, string2, null);
        }
        if (this.hideWho.isSelected(HIDE_FRIENDS)) {
            for (String string4 : this.onlineFriends()) {
                StreamerMode.addName(arrayList, string4, string3);
            }
        }
        if (this.hideWho.isSelected(HIDE_PARTY)) {
            for (PartyMember partyMember : PartyClient.INSTANCE.snapshot().members()) {
                StreamerMode.addName(arrayList, partyMember.name(), string3);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        arrayList.sort(Comparator.comparingInt(String::length).reversed());
        StringBuilder stringBuilder = new StringBuilder("(?i)(?<![A-Za-z0-9_])(");
        for (int i = 0; i < arrayList.size(); ++i) {
            if (i > 0) {
                stringBuilder.append('|');
            }
            stringBuilder.append(Pattern.quote(arrayList.get(i)));
        }
        stringBuilder.append(")(?![A-Za-z0-9_])");
        return Pattern.compile(stringBuilder.toString());
    }

    private Pattern protectedPattern() {
        long l = System.currentTimeMillis();
        if (l - this.patternCacheAt < 400L) {
            return this.cachedPattern;
        }
        this.patternCacheAt = l;
        this.cachedPattern = this.buildPattern();
        return this.cachedPattern;
    }

    public static boolean hideCoords() {
        return StreamerMode.active() && StreamerMode.instance.hideCoordsSetting.getValue();
    }
}

