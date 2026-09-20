package rtx.byazen.api.ui.events;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.events.funtime.FunTimeEvent;
import rtx.byazen.api.events.funtime.FunTimeEventsClient;
import rtx.byazen.api.events.funtime.FunTimeEventsSnapshot;
import rtx.byazen.api.events.funtime.FunTimeMine;
import rtx.byazen.api.events.funtime.FunTimeMinesSnapshot;
import rtx.byazen.api.ui.ScrollBar;
import rtx.byazen.api.ui.settings.RenderHelper;
import rtx.byazen.api.ui.theme.AccentGradient;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.animations.Decelerate;
import rtx.byazen.utils.animations.Direction;
import rtx.byazen.utils.render.fonts.Fonts;
import rtx.byazen.utils.render.others.RoundedScissor;
import rtx.byazen.utils.render.render2d.Render2D;

public final class EventsRenderer {
    private static final float ROW_H = 31.0f;
    private static final float GAP = 3.0f;
    private static final float CONTENT_Y_OFFSET = 5.0f;
    private static final float CONTENT_HEIGHT = 280.0f;
    private static final String EVENT_ICON_FONT = "event-icons";
    private static final float EVENT_ICON_SIZE = 14.5f;
    private static final String ICON_AIR_DROP = "a";
    private static final String ICON_ALTAR = "b";
    private static final String ICON_CLOVER = "c";
    private static final String ICON_COMING_SOON = "d";
    private static final String ICON_FLAME = "e";
    private static final String ICON_LIGHTHOUSE = "f";
    private static final String ICON_METEOR = "g";
    private static final String ICON_MOUNTAIN = "h";
    private static final String ICON_PICKAXE = "i";
    private static final String ICON_TREASURE = "j";
    private static final float DETAIL_ANIM_MS = 400.0f;
    private static final float FADE_OUT_DURATION = 0.15f;
    private static final int ANIM_MS = 280;
    private static final long APPEAR_STAGGER_MS = 50L;
    private static final long SHIMMER_CYCLE_MS = 4400L;
    private static final long INITIAL_WINDOW_MS = 250L;
    private static final float OPEN_FADE_MS = 240.0f;
    private static final float SLIDE_PX = 10.0f;
    private static final long LINGER_MS = 10000L;
    private static final float REORDER_RATE = 14.0f;
    private final List<EventButton> buttons = new ArrayList<EventButton>();
    private final Map<String, Long> detailAppearMs = new HashMap<String, Long>();
    private final Map<String, EventsRenderer.AnimEntry> animEntries = new LinkedHashMap<String, EventsRenderer.AnimEntry>();
    private final ScrollBar scrollBar = new ScrollBar();
    private float scroll;
    private float scrollTarget;
    private float contentH;
    private long startMs = System.currentTimeMillis();
    private boolean transitioning = false;
    private float fadeOutTime = 0.0f;
    private long openMs = System.currentTimeMillis();
    private static final float TAB_BAR_H = 18.0f;
    private static final float BOTTOM_PAD = 18.0f;
    private EventsRenderer.Tab tab = EventsRenderer.Tab.EVENTS;
    private final float[] eventsTabRect = new float[4];
    private final float[] minesTabRect = new float[4];
    private final List<EventsRenderer.MineRow> mineRows = new ArrayList<EventsRenderer.MineRow>();
    private final Map<String, EventsRenderer.MineAnim> mineAnims = new LinkedHashMap<String, EventsRenderer.MineAnim>();
    private static final float TAB_FADE_RATE = 16.0f;
    private static final float TAB_BTN_RATE = 14.0f;
    private EventsRenderer.Tab pendingTab = null;
    private float tabFade = 1.0f;
    private float eventsTabT = 1.0f;
    private float minesTabT = 0.0f;

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    private static String key(FunTimeEvent funTimeEvent) {
        return funTimeEvent.anarchy() + "|" + funTimeEvent.name();
    }

    public void open() {
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.openMs = this.startMs = System.currentTimeMillis();
        this.animEntries.clear();
        this.mineAnims.clear();
    }

    private static String lower(String string) {
        return string == null ? "" : string.toLowerCase(Locale.ROOT);
    }

    private void renderContent(DrawContext drawContext, float f, float f2, float f3, float f4, float f5) {
        float f6 = f + 117.0f;
        float f7 = f2 + 5.0f;
        float f8 = f3 - 122.0f;
        float f9 = 280.0f;
        this.updateTabTransition(f5);
        float f10 = f7;
        float f11 = f9;
        float f12 = (f4 *= this.openFadeAlpha(System.currentTimeMillis())) * EventsRenderer.smooth01(this.tabFade);
        if (this.tab == EventsRenderer.Tab.MINES) {
            this.renderMines(drawContext, f6, f10, f8, f11, f12, f5, f);
        } else {
            this.renderEvents(drawContext, f6, f10, f8, f11, f12, f5, f);
        }
    }

    public void render(DrawContext drawContext, float f, float f2, float f3, float f4, float f5) {
        float f6 = 1.0f - (float)Math.exp(-f5 * 14.0f);
        this.scroll += (this.scrollTarget - this.scroll) * f6;
        if (Math.abs(this.scrollTarget - this.scroll) < 0.05f) {
            this.scroll = this.scrollTarget;
        }
        if (this.transitioning) {
            if (this.fadeOutTime > 0.0f) {
                this.fadeOutTime -= f5;
                float f7 = f4 * Math.max(0.0f, this.fadeOutTime / 0.15f);
                this.renderContent(drawContext, f, f2, f3, f7, f5);
            }
            return;
        }
        this.renderContent(drawContext, f, f2, f3, f4, f5);
    }

    private static int rgba(int n, int n2, int n3, float f) {
        int n4 = Math.max(0, Math.min(255, Math.round(f)));
        if (n4 == 0) {
            return 0;
        }
        return new Color(n, n2, n3, n4).getRGB();
    }

    public static EventStyle classify(FunTimeEvent funTimeEvent) {
        switch (funTimeEvent.status() == null ? "" : funTimeEvent.status().toUpperCase(Locale.ROOT)) {
            case "MINE": {
                return new EventStyle(ICON_PICKAXE, 140, 120, 80, 100, 85, 55);
            }
            case "SNOWQUARRY": {
                return new EventStyle(ICON_CLOVER, 60, 160, 90, 40, 120, 60);
            }
            case "SHIP": {
                return new EventStyle(ICON_TREASURE, 70, 130, 200, 40, 90, 150);
            }
            case "JAYCOB": {
                return new EventStyle(ICON_ALTAR, 150, 110, 60, 110, 80, 40);
            }
            case "AIRDROP": {
                return new EventStyle(ICON_AIR_DROP, 80, 140, 100, 50, 100, 70);
            }
            case "METEOR": 
            case "METEOR_RAIN": {
                return new EventStyle(ICON_METEOR, 100, 100, 110, 50, 50, 60);
            }
            case "BEACON": {
                return new EventStyle(ICON_LIGHTHOUSE, 100, 40, 160, 170, 90, 230);
            }
            case "VOLCANO": {
                return new EventStyle(ICON_MOUNTAIN, 200, 50, 30, 240, 140, 40);
            }
        }
        String string = EventsRenderer.lower(funTimeEvent.name());
        if (string.contains("\u0432\u0443\u043b\u043a\u0430\u043d")) {
            return new EventStyle(ICON_MOUNTAIN, 200, 50, 30, 240, 140, 40);
        }
        if (string.contains("\u043c\u0430\u044f\u043a")) {
            return new EventStyle(ICON_LIGHTHOUSE, 100, 40, 160, 170, 90, 230);
        }
        if (string.contains("\u043c\u0435\u0442\u0435\u043e\u0440")) {
            return new EventStyle(ICON_METEOR, 100, 100, 110, 50, 50, 60);
        }
        if (string.contains("\u0430\u0434\u0441\u043a") || string.contains("\u0440\u0435\u0437\u043d")) {
            return new EventStyle(ICON_FLAME, 160, 30, 30, 100, 15, 20);
        }
        if (string.contains("\u0433\u0435\u0439\u0437\u0435\u0440")) {
            return new EventStyle(ICON_CLOVER, 40, 90, 200, 80, 180, 240);
        }
        if (string.contains("\u0441\u0443\u043d\u0434\u0443\u043a")) {
            return new EventStyle(ICON_ALTAR, 130, 130, 140, 180, 180, 190);
        }
        if (string.contains("\u0448\u0430\u0445\u0442") || string.contains("mine")) {
            return new EventStyle(ICON_PICKAXE, 140, 120, 80, 100, 85, 55);
        }
        if (string.contains("\u0430\u0438\u0440") || string.contains("air") || string.contains("\u0434\u0440\u043e\u043f")) {
            return new EventStyle(ICON_AIR_DROP, 80, 140, 100, 50, 100, 70);
        }
        if (string.contains("\u043a\u043b\u0435\u0432\u0435\u0440") || string.contains("\u0443\u0434\u0430\u0447")) {
            return new EventStyle(ICON_CLOVER, 60, 160, 80, 40, 120, 55);
        }
        if (string.contains("\u043a\u043b\u0430\u0434") || string.contains("\u043a\u0430\u0437\u043d")) {
            return new EventStyle(ICON_TREASURE, 180, 160, 60, 140, 120, 40);
        }
        if (string.contains("\u0430\u043b\u0442\u0430\u0440")) {
            return new EventStyle(ICON_ALTAR, 130, 130, 140, 180, 180, 190);
        }
        return new EventStyle(ICON_COMING_SOON, 140, 140, 150, 180, 180, 190);
    }

    public boolean click(float f, float f2) {
        if (EventsRenderer.hit(this.eventsTabRect, f, f2)) {
            this.setTab(EventsRenderer.Tab.EVENTS);
            return true;
        }
        if (EventsRenderer.hit(this.minesTabRect, f, f2)) {
            this.setTab(EventsRenderer.Tab.MINES);
            return true;
        }
        if (this.tab == EventsRenderer.Tab.MINES) {
            for (EventsRenderer.MineRow mineRow : this.mineRows) {
                if (!mineRow.contains(f, f2)) continue;
                EventsRenderer.joinAnarchy(mineRow.anarchy());
                return true;
            }
            return false;
        }
        for (EventButton eventButton : this.buttons) {
            if (!eventButton.contains(f, f2)) continue;
            EventsRenderer.joinAnarchy(eventButton.anarchy());
            return true;
        }
        return false;
    }

    public void showMines() {
        this.setTab(EventsRenderer.Tab.MINES);
    }

    public void showEvents() {
        this.setTab(EventsRenderer.Tab.EVENTS);
    }

    public void scroll(double d, float f) {
        float f2 = Math.max(0.0f, this.contentH - f + 18.0f);
        this.scrollTarget = EventsRenderer.clamp(this.scrollTarget - (float)d * 16.0f, 0.0f, f2);
    }

    public boolean isTransitioning() {
        return this.transitioning;
    }

    private static int displayRemaining(EventsRenderer.AnimEntry animEntry) {
        if (animEntry.leaving || animEntry.departedAtMs != 0L) {
            return animEntry.effectiveRemaining();
        }
        return FunTimeEventsClient.INSTANCE.remainingSeconds(animEntry.event);
    }

    private float openFadeAlpha(long l) {
        float f = EventsRenderer.clamp((float)(l - this.openMs) / 240.0f, 0.0f, 1.0f);
        return f * f * (3.0f - 2.0f * f);
    }

    private void renderMines(DrawContext drawContext, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        FunTimeMinesSnapshot funTimeMinesSnapshot = FunTimeEventsClient.INSTANCE.minesSnapshot();
        this.mineRows.clear();
        this.updateMineAnimStates(funTimeMinesSnapshot);
        Render2D.pushScissor(drawContext, f, f2, f3, f4);
        RoundedScissor.push(drawContext, f, f2, f3, f4, 0.0f, 12.0f, 12.0f, 0.0f);
        float f8 = f2 + 5.0f - this.scroll;
        float f9 = this.renderMinesList(drawContext, f, f8, f3, f5, f6);
        if (funTimeMinesSnapshot.mines().isEmpty() && this.mineAnims.isEmpty()) {
            String string = funTimeMinesSnapshot.online() ? "\u0420\u0435\u0434\u043a\u0438\u0445 \u0448\u0430\u0445\u0442 \u0441\u0435\u0439\u0447\u0430\u0441 \u043d\u0435\u0442" : EventsRenderer.offlineMessage(funTimeMinesSnapshot.error());
            float f10 = Fonts.MONTSERRAT_MEDIUM.width(string, 6.5f);
            Fonts.MONTSERRAT_MEDIUM.draw(string, f + (f3 - f10) * 0.5f, f2 + f4 * 0.5f, 6.5f, EventsRenderer.rgba(255, 255, 255, 115.0f * f5));
        }
        this.contentH = Math.max(0.0f, f9 - f8);
        float f11 = Math.max(0.0f, this.contentH - f4 + 18.0f);
        this.scrollTarget = EventsRenderer.clamp(this.scrollTarget, 0.0f, f11);
        this.scroll = EventsRenderer.clamp(this.scroll, 0.0f, f11);
        RoundedScissor.pop();
        Render2D.popScissor(drawContext);
        this.renderScrollBar(f, f2, f3, f4, f5);
    }

    private void updateAnimStates(FunTimeEventsSnapshot funTimeEventsSnapshot) {
        EventsRenderer.AnimEntry animEntry;
        Object object;
        HashSet<String> hashSet = new HashSet<String>();
        long l = System.currentTimeMillis();
        boolean bl = this.withinInitialWindow(l);
        int n = 0;
        for (FunTimeEvent object2 : funTimeEventsSnapshot.current()) {
            object = EventsRenderer.key(object2);
            hashSet.add((String)object);
            animEntry = this.animEntries.get(object);
            if (animEntry == null) {
                animEntry = new EventsRenderer.AnimEntry(object2, "current");
                this.seedAppear(animEntry, l, bl, n++);
                this.animEntries.put((String)object, animEntry);
                continue;
            }
            animEntry.markPresent(object2, "current");
        }
        for (FunTimeEvent funTimeEvent : funTimeEventsSnapshot.nearest()) {
            object = EventsRenderer.key(funTimeEvent);
            hashSet.add((String)object);
            animEntry = this.animEntries.get(object);
            if (animEntry == null) {
                animEntry = new EventsRenderer.AnimEntry(funTimeEvent, "nearest");
                this.seedAppear(animEntry, l, bl, n++);
                this.animEntries.put((String)object, animEntry);
                continue;
            }
            animEntry.markPresent(funTimeEvent, "nearest");
        }
        for (Map.Entry entry : this.animEntries.entrySet()) {
            if (hashSet.contains(entry.getKey())) continue;
            object = (EventsRenderer.AnimEntry)entry.getValue();
            if (((EventsRenderer.AnimEntry)object).leaving) continue;
            if (((EventsRenderer.AnimEntry)object).departedAtMs == 0L) {
                ((EventsRenderer.AnimEntry)object).departedAtMs = l;
                continue;
            }
            if (l - ((EventsRenderer.AnimEntry)object).departedAtMs < 10000L) continue;
            ((EventsRenderer.AnimEntry)object).markLeaving();
        }
        this.animEntries.values().removeIf(EventsRenderer.AnimEntry::isGone);
    }

    private static String offlineMessage(String string) {
        if (string == null || string.isBlank()) {
            return "\u041d\u0435\u0442 \u0441\u0432\u044f\u0437\u0438 \u0441 \u0412\u0414\u0421";
        }
        return "\u041e\u0448\u0438\u0431\u043a\u0430: " + string;
    }

    private boolean withinInitialWindow(long l) {
        return l - this.openMs < 250L;
    }

    private void updateTabTransition(float f) {
        float f2 = this.pendingTab != null ? 0.0f : 1.0f;
        this.tabFade += (f2 - this.tabFade) * (1.0f - (float)Math.exp(-f * 16.0f));
        if (this.pendingTab != null && this.tabFade <= 0.04f) {
            this.tab = this.pendingTab;
            this.pendingTab = null;
            this.scroll = 0.0f;
            this.scrollTarget = 0.0f;
            for (EventsRenderer.AnimEntry object : this.animEntries.values()) {
                object.renderYInit = false;
            }
            for (EventsRenderer.MineAnim mineAnim : this.mineAnims.values()) {
                mineAnim.renderYInit = false;
            }
            this.tabFade = 0.0f;
        }
        EventsRenderer.Tab tab = this.pendingTab != null ? this.pendingTab : this.tab;
        float f3 = 1.0f - (float)Math.exp(-f * 14.0f);
        this.eventsTabT += ((tab == EventsRenderer.Tab.EVENTS ? 1.0f : 0.0f) - this.eventsTabT) * f3;
        this.minesTabT += ((tab == EventsRenderer.Tab.MINES ? 1.0f : 0.0f) - this.minesTabT) * f3;
    }

    private float renderSection(DrawContext drawContext, String string, String string2, float f, float f2, float f3, float f4, float f5) {
        if (string != null && !string.isBlank()) {
            Fonts.MONTSERRAT_SEMIBOLD.draw(string, f + 6.0f, f2, 6.5f, EventsRenderer.rgba(255, 255, 255, 155.0f * f4));
            f2 += 11.0f;
        }
        ArrayList<EventsRenderer.AnimEntry> arrayList = new ArrayList<EventsRenderer.AnimEntry>();
        for (EventsRenderer.AnimEntry animEntry2 : this.animEntries.values()) {
            if (!string2.equals(animEntry2.section) || animEntry2.progress() <= 0.001f) continue;
            arrayList.add(animEntry2);
        }
        arrayList.sort(Comparator.comparingInt(EventsRenderer::displayRemaining).thenComparingInt(animEntry -> animEntry.event.anarchy()).thenComparing(animEntry -> animEntry.event.name()));
        float f6 = 1.0f - (float)Math.exp(-f5 * 14.0f);
        float f7 = f2;
        for (EventsRenderer.AnimEntry animEntry3 : arrayList) {
            boolean bl;
            float f8 = animEntry3.progress();
            float f9 = 31.0f * f8;
            float f10 = 3.0f * f8;
            boolean bl2 = bl = f8 < 0.999f;
            if (!animEntry3.renderYInit) {
                animEntry3.renderY = f7;
                animEntry3.renderYInit = true;
            } else {
                animEntry3.renderY += (f7 - animEntry3.renderY) * f6;
                if (Math.abs(f7 - animEntry3.renderY) < 0.05f) {
                    animEntry3.renderY = f7;
                }
            }
            if (bl) {
                Render2D.pushScissor(drawContext, f, animEntry3.renderY, f3, f9);
            }
            this.renderEvent(animEntry3.event, animEntry3, f + 4.0f, animEntry3.renderY, f3 - 8.0f, f4 * f8);
            if (bl) {
                Render2D.popScissor(drawContext);
            }
            f7 += f9 + f10;
        }
        if (arrayList.isEmpty()) {
            Fonts.MONTSERRAT_MEDIUM.draw("No data", f + 8.0f, f2 + 3.0f, 5.5f, EventsRenderer.rgba(255, 255, 255, 70.0f * f4));
            return f2 + 14.0f;
        }
        return f7;
    }

    private void updateMineAnimStates(FunTimeMinesSnapshot funTimeMinesSnapshot) {
        Object object;
        HashSet<String> hashSet = new HashSet<String>();
        long l = System.currentTimeMillis();
        boolean bl = this.withinInitialWindow(l);
        int n = 0;
        for (FunTimeMine object2 : funTimeMinesSnapshot.mines()) {
            object = EventsRenderer.mineKey(object2);
            hashSet.add((String)object);
            EventsRenderer.MineAnim mineAnim = this.mineAnims.get(object);
            if (mineAnim == null) {
                mineAnim = new EventsRenderer.MineAnim(object2);
                this.seedAppear(mineAnim, l, bl, n++);
                this.mineAnims.put((String)object, mineAnim);
                continue;
            }
            mineAnim.markPresent(object2);
        }
        for (Map.Entry entry : this.mineAnims.entrySet()) {
            if (hashSet.contains(entry.getKey())) continue;
            object = (EventsRenderer.MineAnim)entry.getValue();
            if (((EventsRenderer.MineAnim)object).leaving) continue;
            if (((EventsRenderer.MineAnim)object).departedAtMs == 0L) {
                ((EventsRenderer.MineAnim)object).departedAtMs = l;
                continue;
            }
            if (l - ((EventsRenderer.MineAnim)object).departedAtMs < 10000L) continue;
            ((EventsRenderer.MineAnim)object).markLeaving();
        }
        this.mineAnims.values().removeIf(EventsRenderer.MineAnim::isGone);
    }

    private void renderMineRow(FunTimeMine funTimeMine, EventsRenderer.MineAnim mineAnim, float f, float f2, float f3, float f4) {
        int[] nArray = EventsRenderer.rarityColor(funTimeMine.rarity());
        Render2D.rect(f, f2, f3, 31.0f, 5.0f, EventsRenderer.rgba(0, 0, 0, 48.0f * f4));
        float f5 = f + 4.0f;
        float f6 = f2 + 5.0f;
        float f7 = 31.0f;
        float f8 = 21.0f;
        int[] nArray2 = EventsRenderer.darker(nArray);
        int n = EventsRenderer.rgba(nArray[0], nArray[1], nArray[2], 175.0f * f4);
        int n2 = EventsRenderer.rgba(nArray2[0], nArray2[1], nArray2[2], 175.0f * f4);
        int n3 = EventsRenderer.mixColor(n, n2, 0.5f);
        Render2D.rect(f5, f6, f7, f8, 4.0f, n, n3, n2, n3);
        float[] fArray = Render2D.msdfBounds(EVENT_ICON_FONT, ICON_PICKAXE, 14.5f);
        Render2D.msdfText(EVENT_ICON_FONT, ICON_PICKAXE, f5 + f7 * 0.5f - (fArray[0] + fArray[2]) * 0.5f, f6 + f8 * 0.5f - (fArray[1] + fArray[3]) * 0.5f, 14.5f, EventsRenderer.rgba(255, 255, 255, 235.0f * f4));
        float f9 = f5 + f7 + 7.0f;
        int n4 = EventsRenderer.anarchyOf(funTimeMine);
        String string = "/an" + n4;
        float f10 = Fonts.MONTSERRAT_SEMIBOLD.width(string, 5.0f);
        Fonts.MONTSERRAT_SEMIBOLD.draw(string, f + f3 - f10 - 6.0f, f2 + 6.0f, 5.0f, ClientAccent.accentBright(200.0f * f4));
        boolean bl = mineAnim != null && (mineAnim.leaving || mineAnim.departedAtMs != 0L);
        long l = bl ? mineAnim.effectiveRemaining() : FunTimeEventsClient.INSTANCE.remainingSeconds(funTimeMine);
        String string2 = l > 0L ? EventsRenderer.formatTime((int)l) : "\u041e\u0431\u043d\u043e\u0432\u043b\u044f\u0435\u0442\u0441\u044f";
        float f11 = Fonts.MONTSERRAT_MEDIUM.width(string2, 5.0f);
        Fonts.MONTSERRAT_MEDIUM.draw(string2, f + f3 - f11 - 6.0f, f2 + 17.0f, 5.0f, EventsRenderer.rgba(255, 255, 255, 120.0f * f4));
        Object object = funTimeMine.rarity();
        if (!funTimeMine.nextRarity().isBlank() && !funTimeMine.nextRarity().equalsIgnoreCase(funTimeMine.rarity())) {
            object = (String)object + "  \u2192  " + funTimeMine.nextRarity();
        }
        Object object2 = funTimeMine.serverRuName().isBlank() ? "\u0410\u043d\u0430\u0440\u0445\u0438\u044f-" + n4 : funTimeMine.serverRuName();
        float f12 = f2 + 15.5f - 3.5f;
        float f13 = f2 + 5.0f;
        String string3 = EventsRenderer.mineKey(funTimeMine);
        long l2 = System.currentTimeMillis();
        long l3 = this.withinInitialWindow(l2) ? l2 - 400L - 1L : l2;
        this.detailAppearMs.putIfAbsent(string3, l3);
        float f14 = EventsRenderer.clamp((float)(l2 - this.detailAppearMs.get(string3)) / 400.0f, 0.0f, 1.0f);
        float f15 = f14 * f14 * (3.0f - 2.0f * f14);
        float f16 = f12 + (f13 - f12) * f15;
        Fonts.MONTSERRAT_MEDIUM.draw((String)object2, f9, f16, 6.5f, EventsRenderer.rgba(255, 255, 255, 225.0f * f4));
        Fonts.MONTSERRAT_MEDIUM.draw((String)object, f9, f2 + 17.0f, 5.0f, EventsRenderer.rgba(nArray[0], nArray[1], nArray[2], 200.0f * f4 * f15));
        if (mineAnim == null || !mineAnim.leaving) {
            this.mineRows.add(new EventsRenderer.MineRow(f, f2, f3, 31.0f, n4));
        }
    }

    public static int[] rarityColor(String string) {
        String string2;
        String string3 = string2 = string == null ? "" : string.toLowerCase(Locale.ROOT);
        if (string2.contains("\u043c\u0438\u0444\u0438\u0447\u0435\u0441\u043a")) {
            return new int[]{170, 90, 230};
        }
        if (string2.contains("\u043b\u0435\u0433\u0435\u043d\u0434\u0430\u0440\u043d")) {
            return new int[]{235, 190, 70};
        }
        if (string2.contains("\u044d\u043f\u0438\u0447\u0435\u0441\u043a")) {
            return new int[]{150, 90, 220};
        }
        if (string2.contains("\u0440\u0435\u0434\u043a")) {
            return new int[]{70, 130, 235};
        }
        return new int[]{150, 150, 165};
    }

    public static String eventRarityLabel(String string) {
        if (string == null) {
            return "";
        }
        return switch (string.toUpperCase(Locale.ROOT)) {
            case "MYTHICAL" -> "\u041c\u0438\u0444\u0438\u0447\u0435\u0441\u043a\u0438\u0439";
            case "LEGENDARY" -> "\u041b\u0435\u0433\u0435\u043d\u0434\u0430\u0440\u043d\u044b\u0439";
            case "EPIC" -> "\u042d\u043f\u0438\u0447\u0435\u0441\u043a\u0438\u0439";
            case "RARE" -> "\u0420\u0435\u0434\u043a\u0438\u0439";
            case "NORMAL" -> "\u041e\u0431\u044b\u0447\u043d\u044b\u0439";
            default -> "";
        };
    }

    private float renderMinesList(DrawContext drawContext, float f, float f2, float f3, float f4, float f5) {
        ArrayList<EventsRenderer.MineAnim> arrayList = new ArrayList<EventsRenderer.MineAnim>();
        for (EventsRenderer.MineAnim mineAnim2 : this.mineAnims.values()) {
            if (mineAnim2.progress() <= 0.001f) continue;
            arrayList.add(mineAnim2);
        }
        arrayList.sort(Comparator.comparingInt((EventsRenderer.MineAnim mineAnim) -> EventsRenderer.anarchyOf(mineAnim.mine)).thenComparing(mineAnim -> mineAnim.mine.mineName()));
        float f6 = 1.0f - (float)Math.exp(-f5 * 14.0f);
        float f7 = f2;
        for (EventsRenderer.MineAnim mineAnim3 : arrayList) {
            boolean bl;
            float f8 = mineAnim3.progress();
            float f9 = 31.0f * f8;
            float f10 = 3.0f * f8;
            boolean bl2 = bl = f8 < 0.999f;
            if (!mineAnim3.renderYInit) {
                mineAnim3.renderY = f7;
                mineAnim3.renderYInit = true;
            } else {
                mineAnim3.renderY += (f7 - mineAnim3.renderY) * f6;
                if (Math.abs(f7 - mineAnim3.renderY) < 0.05f) {
                    mineAnim3.renderY = f7;
                }
            }
            if (bl) {
                Render2D.pushScissor(drawContext, f, mineAnim3.renderY, f3, f9);
            }
            this.renderMineRow(mineAnim3.mine, mineAnim3, f + 4.0f, mineAnim3.renderY, f3 - 8.0f, f4 * f8);
            if (bl) {
                Render2D.popScissor(drawContext);
            }
            f7 += f9 + f10;
        }
        return f7;
    }

    private void renderEvents(DrawContext drawContext, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        FunTimeEventsSnapshot funTimeEventsSnapshot = FunTimeEventsClient.INSTANCE.snapshot();
        this.buttons.clear();
        this.updateAnimStates(funTimeEventsSnapshot);
        Render2D.pushScissor(drawContext, f, f2, f3, f4);
        RoundedScissor.push(drawContext, f, f2, f3, f4, 0.0f, 12.0f, 12.0f, 0.0f);
        float f8 = f2 + 5.0f - this.scroll;
        f8 = this.renderSection(drawContext, "", "current", f, f8, f3, f5, f6);
        if (funTimeEventsSnapshot.current().isEmpty() && this.animEntries.isEmpty()) {
            String string = funTimeEventsSnapshot.online() ? "\u0421\u043e\u0431\u044b\u0442\u0438\u0439 \u043f\u043e\u043a\u0430 \u043d\u0435\u0442" : EventsRenderer.offlineMessage(funTimeEventsSnapshot.error());
            float f9 = Fonts.MONTSERRAT_MEDIUM.width(string, 6.5f);
            Fonts.MONTSERRAT_MEDIUM.draw(string, f + (f3 - f9) * 0.5f, f2 + f4 * 0.5f, 6.5f, EventsRenderer.rgba(255, 255, 255, 115.0f * f5));
        }
        this.contentH = Math.max(0.0f, f8 - (f2 + 5.0f - this.scroll));
        float f10 = Math.max(0.0f, this.contentH - f4 + 18.0f);
        this.scrollTarget = EventsRenderer.clamp(this.scrollTarget, 0.0f, f10);
        this.scroll = EventsRenderer.clamp(this.scroll, 0.0f, f10);
        RoundedScissor.pop();
        Render2D.popScissor(drawContext);
        this.renderScrollBar(f, f2, f3, f4, f5);
    }

    private static void joinAnarchy(int n) {
        if (n <= 0) {
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player == null || minecraftClient.player.networkHandler == null) {
            return;
        }
        minecraftClient.player.networkHandler.sendChatCommand("an" + n);
    }

    private void renderEvent(FunTimeEvent funTimeEvent, EventsRenderer.AnimEntry animEntry, float f, float f2, float f3, float f4) {
        EventStyle eventStyle = EventsRenderer.classify(funTimeEvent);
        Render2D.rect(f, f2, f3, 31.0f, 5.0f, EventsRenderer.rgba(0, 0, 0, 48.0f * f4));
        float f5 = f + 4.0f;
        float f6 = f2 + 5.0f;
        float f7 = 31.0f;
        float f8 = 21.0f;
        int n = EventsRenderer.rgba(eventStyle.r1, eventStyle.g1, eventStyle.b1, 175.0f * f4);
        int n2 = EventsRenderer.rgba(eventStyle.r2, eventStyle.g2, eventStyle.b2, 175.0f * f4);
        int n3 = EventsRenderer.mixColor(n, n2, 0.5f);
        Render2D.rect(f5, f6, f7, f8, 4.0f, n, n3, n2, n3);
        float[] fArray = Render2D.msdfBounds(EVENT_ICON_FONT, eventStyle.icon, 14.5f);
        Render2D.msdfText(EVENT_ICON_FONT, eventStyle.icon, f5 + f7 * 0.5f - (fArray[0] + fArray[2]) * 0.5f, f6 + f8 * 0.5f - (fArray[1] + fArray[3]) * 0.5f, 14.5f, EventsRenderer.rgba(255, 255, 255, 235.0f * f4));
        float f9 = f5 + f7 + 7.0f;
        String string = "/an" + funTimeEvent.anarchy();
        float f10 = Fonts.MONTSERRAT_SEMIBOLD.width(string, 5.0f);
        Fonts.MONTSERRAT_SEMIBOLD.draw(string, f + f3 - f10 - 6.0f, f2 + 6.0f, 5.0f, ClientAccent.accentBright(200.0f * f4));
        String string2 = "\u0418\u0434\u0451\u0442";
        float f11 = Fonts.MONTSERRAT_MEDIUM.width(string2, 5.0f);
        Fonts.MONTSERRAT_MEDIUM.draw(string2, f + f3 - f11 - 6.0f, f2 + 17.0f, 5.0f, EventsRenderer.rgba(255, 255, 255, 120.0f * f4));
        String string3 = EventsRenderer.eventRarityLabel(funTimeEvent.rarity());
        boolean bl = !string3.isBlank();
        float f12 = f2 + 15.5f - 3.5f;
        float f13 = f2 + 5.0f;
        if (bl) {
            String string4 = EventsRenderer.key(funTimeEvent);
            long l = System.currentTimeMillis();
            long l2 = this.withinInitialWindow(l) ? l - 400L - 1L : l;
            this.detailAppearMs.putIfAbsent(string4, l2);
            float f14 = EventsRenderer.clamp((float)(l - this.detailAppearMs.get(string4)) / 400.0f, 0.0f, 1.0f);
            float f15 = f14 * f14 * (3.0f - 2.0f * f14);
            float f16 = f12 + (f13 - f12) * f15;
            Fonts.MONTSERRAT_MEDIUM.draw(funTimeEvent.name(), f9, f16, 6.5f, EventsRenderer.rgba(255, 255, 255, 225.0f * f4));
            float f17 = 120.0f * f4 * f15;
            Fonts.MONTSERRAT_MEDIUM.draw(string3, f9, f2 + 17.0f, 5.0f, EventsRenderer.rgba(255, 255, 255, f17));
            float f18 = (float)((l - this.startMs) % 4400L) / 4400.0f;
            float f19 = -0.5f + f18 * 2.0f;
            Fonts.MONTSERRAT_MEDIUM.shimmer(string3, f9, f2 + 17.0f, 5.0f, EventsRenderer.rgba(255, 255, 255, Math.round(200.0f * f4 * f15)), f19, 0.52f, 0.9f, f4 * f15);
        } else {
            this.detailAppearMs.remove(EventsRenderer.key(funTimeEvent));
            Fonts.MONTSERRAT_MEDIUM.draw(funTimeEvent.name(), f9, f12, 6.5f, EventsRenderer.rgba(255, 255, 255, 225.0f * f4));
        }
        if (animEntry == null || !animEntry.leaving) {
            this.buttons.add(new EventButton(f, f2, f3, 31.0f, funTimeEvent.anarchy()));
        }
    }

    private static boolean hit(float[] fArray, float f, float f2) {
        return f >= fArray[0] && f <= fArray[0] + fArray[2] && f2 >= fArray[1] && f2 <= fArray[1] + fArray[3];
    }

    public void scrollbarRelease() {
        this.scrollBar.release();
    }

    public float currentScroll() {
        return this.scroll;
    }

    public void finishTransition() {
        this.transitioning = false;
        this.fadeOutTime = 0.0f;
    }

    public boolean scrollbarGrab(float f, float f2) {
        return this.scrollBar.tryGrab(f, f2);
    }

    private void renderScrollBar(float f, float f2, float f3, float f4, float f5) {
        float f6 = f + f3 - 2.5f;
        float f7 = RenderHelper.effectiveCornerRadius(12.0f, f3, 280.0f);
        float f8 = Math.max(0.0f, RenderHelper.cornerEdgeInset(f7, 1.0f) + 1.5f - 3.0f);
        float f9 = f2 + 3.0f + f8;
        float f10 = f4 - 6.0f - f8 * 2.0f;
        float f11 = this.scrollBar.render(f6, f9, f10, f4, this.contentH + 18.0f, this.scroll, f5);
        if (this.scrollBar.isDragging()) {
            this.scroll = f11;
            this.scrollTarget = f11;
        }
    }

    public boolean isFadeOutDone() {
        return this.transitioning && this.fadeOutTime <= 0.0f;
    }

    public boolean scrollbarDragging() {
        return this.scrollBar.isDragging();
    }

    public void beginFadeOut() {
        this.fadeOutTime = 0.15f;
        this.transitioning = true;
    }

    public static int anarchyOf(FunTimeMine funTimeMine) {
        String string = funTimeMine.serverId() == null ? "" : funTimeMine.serverId();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c < '0' || c > '9') continue;
            stringBuilder.append(c);
        }
        try {
            return stringBuilder.isEmpty() ? 0 : Integer.parseInt(stringBuilder.toString());
        }
        catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    public static int[] darker(int[] nArray) {
        return new int[]{Math.round((float)nArray[0] * 0.42f), Math.round((float)nArray[1] * 0.42f), Math.round((float)nArray[2] * 0.42f)};
    }

    private void drawTab(float f, float f2, float f3, float f4, String string, float f5, float f6, float[] fArray) {
        float f7 = EventsRenderer.smooth01(f5);
        Render2D.rect(f, f2, f3, f4, 4.0f, EventsRenderer.rgba(255, 255, 255, 16.0f * f6 * (1.0f - f7)));
        if (f7 > 0.001f) {
            AccentGradient.fillVertical(f, f2, f3, f4, 4.0f, 210.0f * f6 * f7);
        }
        float f8 = 5.5f;
        float f9 = Fonts.MONTSERRAT_SEMIBOLD.width(string, f8);
        float f10 = (130.0f + 125.0f * f7) * f6;
        Fonts.MONTSERRAT_SEMIBOLD.draw(string, f + (f3 - f9) * 0.5f, f2 + f4 * 0.5f - f8 * 0.5f, f8, EventsRenderer.rgba(255, 255, 255, f10));
        fArray[0] = f;
        fArray[1] = f2;
        fArray[2] = f3;
        fArray[3] = f4;
    }

    private void seedAppear(EventsRenderer.AnimEntry animEntry, long l, boolean bl, int n) {
        if (bl) {
            animEntry.anim.counter.setTime(l - 280L - 1L);
        } else {
            animEntry.anim.counter.setTime(l + (long)n * 50L);
        }
    }

    private void seedAppear(EventsRenderer.MineAnim mineAnim, long l, boolean bl, int n) {
        if (bl) {
            mineAnim.anim.counter.setTime(l - 280L - 1L);
        } else {
            long l2 = (long)Math.min(n, 12) * 50L;
            mineAnim.anim.counter.setTime(l + l2);
        }
    }

    public boolean isMines() {
        return (this.pendingTab != null ? this.pendingTab : this.tab) == EventsRenderer.Tab.MINES;
    }

    private static int mixColor(int n, int n2, float f) {
        int n3 = n >> 24 & 0xFF;
        int n4 = n >> 16 & 0xFF;
        int n5 = n >> 8 & 0xFF;
        int n6 = n & 0xFF;
        int n7 = n2 >> 24 & 0xFF;
        int n8 = n2 >> 16 & 0xFF;
        int n9 = n2 >> 8 & 0xFF;
        int n10 = n2 & 0xFF;
        return (int)((float)n3 + (float)(n7 - n3) * f) << 24 | (int)((float)n4 + (float)(n8 - n4) * f) << 16 | (int)((float)n5 + (float)(n9 - n5) * f) << 8 | (int)((float)n6 + (float)(n10 - n6) * f);
    }

    private void setTab(EventsRenderer.Tab tab) {
        EventsRenderer.Tab tab2 = this.pendingTab != null ? this.pendingTab : this.tab;
        if (tab == tab2) {
            return;
        }
        this.pendingTab = tab;
    }

    private void renderTabs(DrawContext drawContext, float f, float f2, float f3) {
        float f4 = 5.5f;
        float f5 = 7.0f;
        float f6 = 12.0f;
        float f7 = 4.0f;
        float f8 = f2 + 4.0f;
        float f9 = Fonts.MONTSERRAT_SEMIBOLD.width("\u0421\u043e\u0431\u044b\u0442\u0438\u044f", f4) + f5 * 2.0f;
        float f10 = Fonts.MONTSERRAT_SEMIBOLD.width("\u0428\u0430\u0445\u0442\u044b", f4) + f5 * 2.0f;
        float f11 = f + 4.0f;
        float f12 = f11 + f9 + f7;
        this.drawTab(f11, f8, f9, f6, "\u0421\u043e\u0431\u044b\u0442\u0438\u044f", this.eventsTabT, f3, this.eventsTabRect);
        this.drawTab(f12, f8, f10, f6, "\u0428\u0430\u0445\u0442\u044b", this.minesTabT, f3, this.minesTabRect);
    }

    private static float smooth01(float f) {
        f = EventsRenderer.clamp(f, 0.0f, 1.0f);
        return f * f * (3.0f - 2.0f * f);
    }

    private static String mineKey(FunTimeMine funTimeMine) {
        return funTimeMine.serverId() + "|" + funTimeMine.mineName();
    }

    public static String formatTime(int n) {
        if (n <= 0) {
            return "\u0418\u0434\u0451\u0442";
        }
        int n2 = n / 60;
        int n3 = n % 60;
        return n2 + "\u043c\u0438\u043d. " + n3 + "\u0441\u0435\u043a.";
    }


    public static final class AnimEntry {
        FunTimeEvent event;
        String section;
        final Decelerate anim;
        boolean leaving;
        long departedAtMs;
        int lastRemaining;
        long lastSeenMs;
        float renderY;
        boolean renderYInit;
    
        AnimEntry(FunTimeEvent funTimeEvent, String string) {
            this.event = funTimeEvent;
            this.section = string;
            this.anim = (Decelerate)new Decelerate().setMs(31595).setValue(1.0);
            this.anim.setDirection(Direction.FORWARDS);
            this.lastRemaining = FunTimeEventsClient.INSTANCE.remainingSeconds(funTimeEvent);
            this.lastSeenMs = System.currentTimeMillis();
        }
    
        int effectiveRemaining() {
            long l = (System.currentTimeMillis() - this.lastSeenMs) / 1000L;
            int n = (int)l;
            int n2 = this.lastRemaining;
            int n3 = n2 + (n2 * n2 << -67);
            n2 = n3 - (n3 * n3 << -67);
            n3 = n + (n * n << -67);
            n = n3 - (n3 * n3 << -67);
            return Math.max(0, n2 + -n);
        }
    
        void markPresent(FunTimeEvent funTimeEvent, String string) {
            long l;
            long l2;
            long l3 = 0L;
            AnimEntry animEntry = null;
            long l4 = 0L;
            FunTimeEvent funTimeEvent2 = null;
            long l5 = 0L;
            String string2 = null;
            long l6 = 0L;
            Object var18_10 = null;
            long l7 = 0L;
            Object var21_12 = null;
            long l8 = 0L;
            Object var24_14 = null;
            long l9 = 0L;
            Object var27_16 = null;
            animEntry = this;
            funTimeEvent2 = funTimeEvent;
            string2 = string;
            l8 = 381937633;
            long l10 = 300916221;
            long l11 = l8;
            l9 = (int)l11 * (int)l10;
            l10 = l8;
            l11 = l9;
            l8 = (int)l11 ^ (int)l10;
            l8 = 597752409;
            long l12 = -2109761829;
            l10 = l8;
            l9 = (int)l10 * (int)l12;
            l12 = l8;
            l10 = l9;
            l8 = (int)l10 ^ (int)l12;
            animEntry.event = funTimeEvent2;
            long l13 = -6887029872148940909L - -6887029872148940909L;
            if ((int)((long)(l13 == 0L ? 0 : (l13 < 0L ? -1 : 1))) != 0) {
                l8 = -3870113160643251163L;
                l11 = 63;
                l2 = -2258306111597334959L;
                long l14 = (int)l2 & (int)l11;
                l11 = 16;
                l2 = l14;
                l9 = (int)l2 + (int)l11;
                while ((int)l9 != 0) {
                    l2 = l8;
                    l11 = 1;
                    if ((int)((long)((int)l2 & (int)l11)) != 0) {
                        l11 = 3;
                        l2 = l8;
                        long l15 = (int)l2 * (int)l11;
                        l11 = 1;
                        l2 = l15;
                        l8 = (int)l2 + (int)l11;
                    } else {
                        l11 = 1;
                        l2 = l8;
                        l8 = (int)l2 >>> (int)l11;
                    }
                    l11 = 1;
                    l2 = l9;
                    l9 = (int)l2 - (int)l11;
                }
            }
            if ((int)((long)((l = -3870113160643251163L - -3870113160643251163L) == 0L ? 0 : (l < 0L ? -1 : 1))) != 0) {
                l8 = -6809657229649788499L;
                l10 = 63;
                l11 = 6609140407737253288L;
                long l16 = (int)l11 & (int)l10;
                l10 = 16;
                l11 = l16;
                l9 = (int)l11 + (int)l10;
                while ((int)l9 != 0) {
                    l11 = l8;
                    l10 = 1;
                    if ((int)((long)((int)l11 & (int)l10)) != 0) {
                        l10 = 3;
                        l11 = l8;
                        long l17 = (int)l11 * (int)l10;
                        l10 = 1;
                        l11 = l17;
                        l8 = (int)l11 + (int)l10;
                    } else {
                        l10 = 1;
                        l11 = l8;
                        l8 = (int)l11 >>> (int)l10;
                    }
                    l10 = 1;
                    l11 = l9;
                    l9 = (int)l11 - (int)l10;
                }
            }
            animEntry.section = string2;
            long l18 = -8504282180203975530L - -8504282180203975530L;
            if ((int)((long)(l18 == 0L ? 0 : (l18 < 0L ? -1 : 1))) != 0) {
                l8 = 6609140407737253288L;
                l10 = 63;
                l11 = -6887029872148940909L;
                long l19 = (int)l11 & (int)l10;
                l10 = 16;
                l11 = l19;
                l9 = (int)l11 + (int)l10;
                while ((int)l9 != 0) {
                    l11 = l8;
                    l10 = 1;
                    if ((int)((long)((int)l11 & (int)l10)) != 0) {
                        l10 = 3;
                        l11 = l8;
                        long l20 = (int)l11 * (int)l10;
                        l10 = 1;
                        l11 = l20;
                        l8 = (int)l11 + (int)l10;
                    } else {
                        l10 = 1;
                        l11 = l8;
                        l8 = (int)l11 >>> (int)l10;
                    }
                    l10 = 1;
                    l11 = l9;
                    l9 = (int)l11 - (int)l10;
                }
            }
            FunTimeEvent funTimeEvent3 = funTimeEvent2;
            FunTimeEventsClient funTimeEventsClient = FunTimeEventsClient.INSTANCE;
            animEntry.lastRemaining = (int)((long)funTimeEventsClient.remainingSeconds(funTimeEvent3));
            l8 = 1539920581;
            l12 = 991517607;
            long l21 = l8;
            l9 = (int)l21 * (int)l12;
            l12 = l8;
            l21 = l9;
            l8 = (int)l21 ^ (int)l12;
            animEntry.lastSeenMs = System.currentTimeMillis();
            long l22 = -3870113160643251163L - -3870113160643251163L;
            if ((int)((long)(l22 == 0L ? 0 : (l22 < 0L ? -1 : 1))) != 0) {
                l8 = -3557601562890675566L;
                l21 = 63;
                long l23 = -3557601562890675566L;
                long l24 = (int)l23 & (int)l21;
                l21 = 16;
                l23 = l24;
                l9 = (int)l23 + (int)l21;
                while ((int)l9 != 0) {
                    l23 = l8;
                    l21 = 1;
                    if ((int)((long)((int)l23 & (int)l21)) != 0) {
                        l21 = 3;
                        l23 = l8;
                        long l25 = (int)l23 * (int)l21;
                        l21 = 1;
                        l23 = l25;
                        l8 = (int)l23 + (int)l21;
                    } else {
                        l21 = 1;
                        l23 = l8;
                        l8 = (int)l23 >>> (int)l21;
                    }
                    l21 = 1;
                    l23 = l9;
                    l9 = (int)l23 - (int)l21;
                }
            }
            animEntry.departedAtMs = 0L;
            l8 = 234786822;
            l21 = 212784963;
            long l26 = l8;
            l9 = (int)l26 * (int)l21;
            l21 = l8;
            l26 = l9;
            l8 = (int)l26 ^ (int)l21;
            l8 = 1837894016;
            l21 = -1215050815;
            l26 = l8;
            l9 = (int)l26 * (int)l21;
            l21 = l8;
            l26 = l9;
            l8 = (int)l26 ^ (int)l21;
            if ((int)(animEntry.leaving ? 1 : 0) != 0) {
                l8 = 351463633;
                l21 = 1503081193;
                l26 = l8;
                l9 = (int)l26 * (int)l21;
                l21 = l8;
                l26 = l9;
                l8 = (int)l26 ^ (int)l21;
                l8 = 1270790329;
                l12 = -1338057611;
                l21 = l8;
                l9 = (int)l21 * (int)l12;
                l12 = l8;
                l21 = l9;
                l8 = (int)l21 ^ (int)l12;
                animEntry.leaving = false;
                l8 = 1816928403;
                l26 = 1258435989;
                l2 = l8;
                l9 = (int)l2 * (int)l26;
                l26 = l8;
                l2 = l9;
                l8 = (int)l2 ^ (int)l26;
                Direction direction = Direction.FORWARDS;
                Decelerate decelerate = animEntry.anim;
                decelerate.setDirection(direction);
            }
        }
    
        void markLeaving() {
            long l = 0L;
            AnimEntry animEntry = null;
            long l2 = 0L;
            Object var10_4 = null;
            long l3 = 0L;
            Object var13_6 = null;
            long l4 = 0L;
            Object var16_8 = null;
            long l5 = 0L;
            Object var19_10 = null;
            animEntry = this;
            l4 = 381937633;
            long l6 = 300916221;
            long l7 = l4;
            l5 = (int)l7 * (int)l6;
            l6 = l4;
            l7 = l5;
            l4 = (int)l7 ^ (int)l6;
            l4 = 597752409;
            l6 = -2109761829;
            l7 = l4;
            l5 = (int)l7 * (int)l6;
            l6 = l4;
            l7 = l5;
            l4 = (int)l7 ^ (int)l6;
            if ((int)(animEntry.leaving ? 1 : 0) == 0) {
                long l8;
                long l9 = -6887029872148940909L - -6887029872148940909L;
                if ((int)((long)(l9 == 0L ? 0 : (l9 < 0L ? -1 : 1))) != 0) {
                    l4 = -3870113160643251163L;
                    l7 = 63;
                    long l10 = -2258306111597334959L;
                    long l11 = (int)l10 & (int)l7;
                    l7 = 16;
                    l10 = l11;
                    l5 = (int)l10 + (int)l7;
                    while ((int)l5 != 0) {
                        l10 = l4;
                        l7 = 1;
                        if ((int)((long)((int)l10 & (int)l7)) != 0) {
                            l7 = 3;
                            l10 = l4;
                            long l12 = (int)l10 * (int)l7;
                            l7 = 1;
                            l10 = l12;
                            l4 = (int)l10 + (int)l7;
                        } else {
                            l7 = 1;
                            l10 = l4;
                            l4 = (int)l10 >>> (int)l7;
                        }
                        l7 = 1;
                        l10 = l5;
                        l5 = (int)l10 - (int)l7;
                    }
                }
                if ((int)((long)((l8 = -3870113160643251163L - -3870113160643251163L) == 0L ? 0 : (l8 < 0L ? -1 : 1))) != 0) {
                    l4 = -6809657229649788499L;
                    l6 = 63;
                    l7 = 6609140407737253288L;
                    long l13 = (int)l7 & (int)l6;
                    l6 = 16;
                    l7 = l13;
                    l5 = (int)l7 + (int)l6;
                    while ((int)l5 != 0) {
                        l7 = l4;
                        l6 = 1;
                        if ((int)((long)((int)l7 & (int)l6)) != 0) {
                            l6 = 3;
                            l7 = l4;
                            long l14 = (int)l7 * (int)l6;
                            l6 = 1;
                            l7 = l14;
                            l4 = (int)l7 + (int)l6;
                        } else {
                            l6 = 1;
                            l7 = l4;
                            l4 = (int)l7 >>> (int)l6;
                        }
                        l6 = 1;
                        l7 = l5;
                        l5 = (int)l7 - (int)l6;
                    }
                }
                animEntry.leaving = true;
                long l15 = -8504282180203975530L - -8504282180203975530L;
                if ((int)((long)(l15 == 0L ? 0 : (l15 < 0L ? -1 : 1))) != 0) {
                    l4 = 6609140407737253288L;
                    l6 = 63;
                    l7 = -6887029872148940909L;
                    long l16 = (int)l7 & (int)l6;
                    l6 = 16;
                    l7 = l16;
                    l5 = (int)l7 + (int)l6;
                    while ((int)l5 != 0) {
                        l7 = l4;
                        l6 = 1;
                        if ((int)((long)((int)l7 & (int)l6)) != 0) {
                            l6 = 3;
                            l7 = l4;
                            long l17 = (int)l7 * (int)l6;
                            l6 = 1;
                            l7 = l17;
                            l4 = (int)l7 + (int)l6;
                        } else {
                            l6 = 1;
                            l7 = l4;
                            l4 = (int)l7 >>> (int)l6;
                        }
                        l6 = 1;
                        l7 = l5;
                        l5 = (int)l7 - (int)l6;
                    }
                }
                Direction direction = Direction.BACKWARDS;
                Decelerate decelerate = animEntry.anim;
                decelerate.setDirection(direction);
            }
        }
    
        float progress() {
            return (float)Math.max(0.0, Math.min(1.0, this.anim.getOutput()));
        }
    
        boolean isGone() {
            return this.leaving && this.anim.isFinished(Direction.BACKWARDS);
        }
    }
    
        public static final class MineAnim {
        FunTimeMine mine;
        final Decelerate anim;
        boolean leaving;
        long departedAtMs;
        long lastRemaining;
        long lastSeenMs;
        float renderY;
        boolean renderYInit;
    
        MineAnim(FunTimeMine funTimeMine) {
            this.mine = funTimeMine;
            this.anim = (Decelerate)new Decelerate().setMs(31595).setValue(1.0);
            this.anim.setDirection(Direction.FORWARDS);
            this.lastRemaining = FunTimeEventsClient.INSTANCE.remainingSeconds(funTimeMine);
            this.lastSeenMs = System.currentTimeMillis();
        }
    
        long effectiveRemaining() {
            long l = (System.currentTimeMillis() - this.lastSeenMs) / 1000L;
            return Math.max(0L, this.lastRemaining - l);
        }
    
        void markPresent(FunTimeMine funTimeMine) {
            long l;
            long l2 = 0L;
            MineAnim mineAnim = null;
            long l3 = 0L;
            FunTimeMine funTimeMine2 = null;
            long l4 = 0L;
            Object var14_7 = null;
            long l5 = 0L;
            Object var17_9 = null;
            long l6 = 0L;
            Object var20_11 = null;
            long l7 = 0L;
            Object var23_13 = null;
            mineAnim = this;
            funTimeMine2 = funTimeMine;
            l6 = 381937633;
            long l8 = 300916221;
            long l9 = l6;
            l7 = (int)l9 * (int)l8;
            l8 = l6;
            l9 = l7;
            l6 = (int)l9 ^ (int)l8;
            l6 = 597752409;
            long l10 = -2109761829;
            l8 = l6;
            l7 = (int)l8 * (int)l10;
            l10 = l6;
            l8 = l7;
            l6 = (int)l8 ^ (int)l10;
            mineAnim.mine = funTimeMine2;
            long l11 = -6887029872148940909L - -6887029872148940909L;
            if ((int)((long)(l11 == 0L ? 0 : (l11 < 0L ? -1 : 1))) != 0) {
                l6 = -3870113160643251163L;
                l9 = 63;
                long l12 = -2258306111597334959L;
                long l13 = (int)l12 & (int)l9;
                l9 = 16;
                l12 = l13;
                l7 = (int)l12 + (int)l9;
                while ((int)l7 != 0) {
                    l12 = l6;
                    l9 = 1;
                    if ((int)((long)((int)l12 & (int)l9)) != 0) {
                        l9 = 3;
                        l12 = l6;
                        long l14 = (int)l12 * (int)l9;
                        l9 = 1;
                        l12 = l14;
                        l6 = (int)l12 + (int)l9;
                    } else {
                        l9 = 1;
                        l12 = l6;
                        l6 = (int)l12 >>> (int)l9;
                    }
                    l9 = 1;
                    l12 = l7;
                    l7 = (int)l12 - (int)l9;
                }
            }
            if ((int)((long)((l = -3870113160643251163L - -3870113160643251163L) == 0L ? 0 : (l < 0L ? -1 : 1))) != 0) {
                l6 = -6809657229649788499L;
                l8 = 63;
                l9 = 6609140407737253288L;
                long l15 = (int)l9 & (int)l8;
                l8 = 16;
                l9 = l15;
                l7 = (int)l9 + (int)l8;
                while ((int)l7 != 0) {
                    l9 = l6;
                    l8 = 1;
                    if ((int)((long)((int)l9 & (int)l8)) != 0) {
                        l8 = 3;
                        l9 = l6;
                        long l16 = (int)l9 * (int)l8;
                        l8 = 1;
                        l9 = l16;
                        l6 = (int)l9 + (int)l8;
                    } else {
                        l8 = 1;
                        l9 = l6;
                        l6 = (int)l9 >>> (int)l8;
                    }
                    l8 = 1;
                    l9 = l7;
                    l7 = (int)l9 - (int)l8;
                }
            }
            FunTimeMine funTimeMine3 = funTimeMine2;
            FunTimeEventsClient funTimeEventsClient = FunTimeEventsClient.INSTANCE;
            long l17 = funTimeEventsClient.remainingSeconds(funTimeMine3);
            long l18 = -8504282180203975530L - -8504282180203975530L;
            if ((int)((long)(l18 == 0L ? 0 : (l18 < 0L ? -1 : 1))) != 0) {
                l6 = 6609140407737253288L;
                l10 = 63;
                long l19 = -6887029872148940909L;
                long l20 = (int)l19 & (int)l10;
                l10 = 16;
                l19 = l20;
                l7 = (int)l19 + (int)l10;
                while ((int)l7 != 0) {
                    l19 = l6;
                    l10 = 1;
                    if ((int)((long)((int)l19 & (int)l10)) != 0) {
                        l10 = 3;
                        l19 = l6;
                        long l21 = (int)l19 * (int)l10;
                        l10 = 1;
                        l19 = l21;
                        l6 = (int)l19 + (int)l10;
                    } else {
                        l10 = 1;
                        l19 = l6;
                        l6 = (int)l19 >>> (int)l10;
                    }
                    l10 = 1;
                    l19 = l7;
                    l7 = (int)l19 - (int)l10;
                }
            }
            mineAnim.lastRemaining = l17;
            mineAnim.lastSeenMs = System.currentTimeMillis();
            l6 = 1539920581;
            l10 = 991517607;
            long l22 = l6;
            l7 = (int)l22 * (int)l10;
            l10 = l6;
            l22 = l7;
            l6 = (int)l22 ^ (int)l10;
            mineAnim.departedAtMs = 0L;
            long l23 = -3870113160643251163L - -3870113160643251163L;
            if ((int)((long)(l23 == 0L ? 0 : (l23 < 0L ? -1 : 1))) != 0) {
                l6 = -3557601562890675566L;
                l22 = 63;
                long l24 = -3557601562890675566L;
                long l25 = (int)l24 & (int)l22;
                l22 = 16;
                l24 = l25;
                l7 = (int)l24 + (int)l22;
                while ((int)l7 != 0) {
                    l24 = l6;
                    l22 = 1;
                    if ((int)((long)((int)l24 & (int)l22)) != 0) {
                        l22 = 3;
                        l24 = l6;
                        long l26 = (int)l24 * (int)l22;
                        l22 = 1;
                        l24 = l26;
                        l6 = (int)l24 + (int)l22;
                    } else {
                        l22 = 1;
                        l24 = l6;
                        l6 = (int)l24 >>> (int)l22;
                    }
                    l22 = 1;
                    l24 = l7;
                    l7 = (int)l24 - (int)l22;
                }
            }
            if ((int)(mineAnim.leaving ? 1 : 0) != 0) {
                l6 = 234786822;
                l22 = 212784963;
                long l27 = l6;
                l7 = (int)l27 * (int)l22;
                l22 = l6;
                l27 = l7;
                l6 = (int)l27 ^ (int)l22;
                l6 = 1837894016;
                l10 = -1215050815;
                l22 = l6;
                l7 = (int)l22 * (int)l10;
                l10 = l6;
                l22 = l7;
                l6 = (int)l22 ^ (int)l10;
                mineAnim.leaving = false;
                l6 = 351463633;
                l22 = 1503081193;
                l27 = l6;
                l7 = (int)l27 * (int)l22;
                l22 = l6;
                l27 = l7;
                l6 = (int)l27 ^ (int)l22;
                l6 = 1270790329;
                l22 = -1338057611;
                l27 = l6;
                l7 = (int)l27 * (int)l22;
                l22 = l6;
                l27 = l7;
                l6 = (int)l27 ^ (int)l22;
                l6 = 1816928403;
                l10 = 1258435989;
                l22 = l6;
                l7 = (int)l22 * (int)l10;
                l10 = l6;
                l22 = l7;
                l6 = (int)l22 ^ (int)l10;
                Direction direction = Direction.FORWARDS;
                Decelerate decelerate = mineAnim.anim;
                decelerate.setDirection(direction);
            }
        }
    
        void markLeaving() {
            long l = 0L;
            MineAnim mineAnim = null;
            long l2 = 0L;
            Object var10_4 = null;
            long l3 = 0L;
            Object var13_6 = null;
            long l4 = 0L;
            Object var16_8 = null;
            long l5 = 0L;
            Object var19_10 = null;
            mineAnim = this;
            l4 = 381937633;
            long l6 = 300916221;
            long l7 = l4;
            l5 = (int)l7 * (int)l6;
            l6 = l4;
            l7 = l5;
            l4 = (int)l7 ^ (int)l6;
            l4 = 597752409;
            l6 = -2109761829;
            l7 = l4;
            l5 = (int)l7 * (int)l6;
            l6 = l4;
            l7 = l5;
            l4 = (int)l7 ^ (int)l6;
            if ((int)(mineAnim.leaving ? 1 : 0) == 0) {
                long l8;
                long l9 = -6887029872148940909L - -6887029872148940909L;
                if ((int)((long)(l9 == 0L ? 0 : (l9 < 0L ? -1 : 1))) != 0) {
                    l4 = -3870113160643251163L;
                    l7 = 63;
                    long l10 = -2258306111597334959L;
                    long l11 = (int)l10 & (int)l7;
                    l7 = 16;
                    l10 = l11;
                    l5 = (int)l10 + (int)l7;
                    while ((int)l5 != 0) {
                        l10 = l4;
                        l7 = 1;
                        if ((int)((long)((int)l10 & (int)l7)) != 0) {
                            l7 = 3;
                            l10 = l4;
                            long l12 = (int)l10 * (int)l7;
                            l7 = 1;
                            l10 = l12;
                            l4 = (int)l10 + (int)l7;
                        } else {
                            l7 = 1;
                            l10 = l4;
                            l4 = (int)l10 >>> (int)l7;
                        }
                        l7 = 1;
                        l10 = l5;
                        l5 = (int)l10 - (int)l7;
                    }
                }
                if ((int)((long)((l8 = -3870113160643251163L - -3870113160643251163L) == 0L ? 0 : (l8 < 0L ? -1 : 1))) != 0) {
                    l4 = -6809657229649788499L;
                    l6 = 63;
                    l7 = 6609140407737253288L;
                    long l13 = (int)l7 & (int)l6;
                    l6 = 16;
                    l7 = l13;
                    l5 = (int)l7 + (int)l6;
                    while ((int)l5 != 0) {
                        l7 = l4;
                        l6 = 1;
                        if ((int)((long)((int)l7 & (int)l6)) != 0) {
                            l6 = 3;
                            l7 = l4;
                            long l14 = (int)l7 * (int)l6;
                            l6 = 1;
                            l7 = l14;
                            l4 = (int)l7 + (int)l6;
                        } else {
                            l6 = 1;
                            l7 = l4;
                            l4 = (int)l7 >>> (int)l6;
                        }
                        l6 = 1;
                        l7 = l5;
                        l5 = (int)l7 - (int)l6;
                    }
                }
                mineAnim.leaving = true;
                long l15 = -8504282180203975530L - -8504282180203975530L;
                if ((int)((long)(l15 == 0L ? 0 : (l15 < 0L ? -1 : 1))) != 0) {
                    l4 = 6609140407737253288L;
                    l6 = 63;
                    l7 = -6887029872148940909L;
                    long l16 = (int)l7 & (int)l6;
                    l6 = 16;
                    l7 = l16;
                    l5 = (int)l7 + (int)l6;
                    while ((int)l5 != 0) {
                        l7 = l4;
                        l6 = 1;
                        if ((int)((long)((int)l7 & (int)l6)) != 0) {
                            l6 = 3;
                            l7 = l4;
                            long l17 = (int)l7 * (int)l6;
                            l6 = 1;
                            l7 = l17;
                            l4 = (int)l7 + (int)l6;
                        } else {
                            l6 = 1;
                            l7 = l4;
                            l4 = (int)l7 >>> (int)l6;
                        }
                        l6 = 1;
                        l7 = l5;
                        l5 = (int)l7 - (int)l6;
                    }
                }
                Direction direction = Direction.BACKWARDS;
                Decelerate decelerate = mineAnim.anim;
                decelerate.setDirection(direction);
            }
        }
    
        float progress() {
            return (float)Math.max(0.0, Math.min(1.0, this.anim.getOutput()));
        }
    
        boolean isGone() {
            return this.leaving && this.anim.isFinished(Direction.BACKWARDS);
        }
    }
    
        public static record MineRow(float x, float y, float w, float h, int anarchy) {
        boolean contains(float f, float f2) {
            return f >= this.x && f <= this.x + this.w && f2 >= this.y && f2 <= this.y + this.h;
        }
    }

    public static record EventButton(float x, float y, float w, float h, int anarchy) {
        public boolean contains(float mx, float my) {
            return mx >= this.x && mx <= this.x + this.w && my >= this.y && my <= this.y + this.h;
        }
    }

    public static record EventStyle(String icon, int r1, int g1, int b1, int r2, int g2, int b2) {}

    public static enum Tab {
        EVENTS,
        MINES
    }
}

