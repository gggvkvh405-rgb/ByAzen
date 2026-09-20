package rtx.kimiko.api.ui.module;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import rtx.kimiko.api.drags.Position;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.restrict.Server;
import rtx.kimiko.api.modules.restrict.ServerRestrictions;
import rtx.kimiko.api.ui.ScrollBar;
import rtx.kimiko.api.ui.UI;
import rtx.kimiko.api.ui.settings.RenderHelper;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.mixin.accessor.GuiGraphicsExtractorAccessor;
import rtx.kimiko.utils.animations.Decelerate;
import rtx.kimiko.utils.animations.Direction;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.key.KeyBind;
import rtx.kimiko.utils.render.fonts.Fonts;
import rtx.kimiko.utils.render.others.RoundedScissor;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class ModuleListRenderer {
    private static final float FADE_OUT_DURATION = 0.15f;
    private static final int ROW_FADE_MS = 380;
    private static final int QUICK_ROW_FADE_MS = 320;
    private static final float SLIDE_PX = 6.0f;
    private static final float CONTENT_Y_OFFSET = 5.0f;
    public static final float HEADER_OFFSET = 30.0f;
    private static final float CARD_RADIUS = 6.0f;
    private static final float CARD_GAP = 5.0f;
    private static final float CARD_PAD = 5.0f;
    private static final float PAD_X = 8.0f;
    private static final float NAME_SIZE = 7.5f;
    private static final float NAME_TOP = 7.0f;
    private static final float TITLE_CY = 10.75f;
    private static final float DESC_SIZE = 6.0f;
    private static final float DESC_LINE_H = 7.0f;
    private static final float DESC_TOP = 19.0f;
    private static final float DESC_RIGHT_PAD = 4.0f;
    private static final float DESC_BOTTOM_PAD = 7.0f;
    private static final float NO_DESC_H = 22.0f;
    private static final float TOGGLE_RIGHT_PAD = 8.0f;
    private static final float GEAR_GAP = 7.0f;
    private static final float GEAR_SIZE = 7.0f;
    private static final String GEAR_GLYPH = "f";
    private static final float CHECK_SIZE = 8.5f;
    private static final float CHECK_SCALE = 0.425f;
    private static final float CHECK_RADIUS = 2.9750001f;
    private static final float CHECK_RING1_INSET = 1.2750001f;
    private static final float CHECK_RING1_RADIUS = 2.5500002f;
    private static final float CHECK_RING2_INSET = 2.5500002f;
    private static final float CHECK_RING2_RADIUS = 2.125f;
    private static final float CHECK_DOT_INSET = 2.125f;
    private static final float CHECK_RING_THICKNESS = Math.max(0.5f, 0.425f);
    private static final float BADGE_H = 10.0f;
    private static final EnumMap<Category, String> ICON_CACHE = new EnumMap(Category.class);
    private final EnumMap<Category, List<Module>> moduleCache = new EnumMap(Category.class);
    private final Map<String, Decelerate> enableAnims = new HashMap<String, Decelerate>();
    private final Map<String, Float> hoverAnims = new HashMap<String, Float>();
    private final Map<String, Float> gearHoverAnims = new HashMap<String, Float>();
    private final Map<String, Float> gearOpenAnims = new HashMap<String, Float>();
    private final Map<String, List<String>> descLinesCache = new HashMap<String, List<String>>();
    private final Map<String, Float> baseHeightCache = new HashMap<String, Float>();
    private float scroll = 0.0f;
    private float scrollTarget = 0.0f;
    private float contentH = 0.0f;
    private Category lastRendered = null;
    private final Map<Integer, Decelerate> rowAppearAnims = new HashMap<Integer, Decelerate>();
    private int appearFadeMs = 320;
    private long appearBaseMs;
    private boolean appearInitialFrame;
    private final ScrollBar scrollBar = new ScrollBar();
    private boolean transitioning = false;
    private boolean quickAppear = false;
    private List<Module> fadingOut = Collections.emptyList();
    private float fadeOutTime = 0.0f;
    private String focusName;
    private long focusUntilMs;
    private boolean focusScrollPending;
    private float focusDimT;
    public static final int MAX_BLUR_CARDS = 64;
    private final float[] cardBlurRects = new float[384];
    private int cardBlurCount;
    private float cardBlurMaxPhase;
    private boolean appearComposite;
    private int lastModulesSig;

    static {
        for (Category category : Category.values()) {
            ICON_CACHE.put(category, String.valueOf(ModuleListRenderer.iconChar(category)));
        }
    }

    public void render(DrawContext drawContext, float f, float f2, float f3, float f4, float f5, float f6, Category category, List<Module> list) {
        float f7;
        float f8;
        boolean bl;
        float f9 = f + 117.0f;
        float f10 = f2 + 5.0f + 30.0f;
        float f11 = f3 - 122.0f;
        float f12 = ModuleListRenderer.colW(f11);
        float f13 = f9 + 5.0f;
        float f14 = f13 + f12 + 5.0f;
        float f15 = 250.0f;
        float f16 = 1.0f - (float)Math.exp(-f6 * 14.0f);
        this.scroll += (this.scrollTarget - this.scroll) * f16;
        if (Math.abs(this.scrollTarget - this.scroll) < 0.05f) {
            this.scroll = this.scrollTarget;
        }
        if (this.transitioning) {
            if (this.fadeOutTime > 0.0f) {
                this.fadeOutTime -= f6;
                float f17 = f4 * Math.max(0.0f, this.fadeOutTime / 0.15f);
                this.renderCards(drawContext, f9, f10, f11, f12, f13, f14, f15, f17, f5, f6, this.fadingOut, false);
            }
            return;
        }
        int n = ModuleListRenderer.modulesSignature(list);
        boolean bl2 = this.appearComposite && (category != this.lastRendered || n != this.lastModulesSig || this.scrollBar.isDragging() || Math.abs(this.scrollTarget - this.scroll) > 0.05f || this.hasUnfinishedAppear());
        this.lastModulesSig = n;
        if (category != this.lastRendered) {
            this.lastRendered = category;
            this.startRowAnims(ModuleListRenderer.rowCount(list.size()));
        }
        this.contentH = this.totalContentH(list, f12);
        float f18 = Math.max(0.0f, this.contentH - f15);
        this.scrollTarget = Math.max(0.0f, Math.min(this.scrollTarget, f18));
        int n2 = this.focusName != null ? ModuleListRenderer.indexOfModule(list, this.focusName) : -1;
        boolean bl3 = bl = n2 >= 0 && System.currentTimeMillis() < this.focusUntilMs;
        if (this.focusScrollPending && n2 >= 0) {
            f8 = 5.0f + this.cardTopInColumn(list, n2, f12);
            f7 = this.baseCardHeight(list.get(n2), f12);
            this.scrollTarget = Math.max(0.0f, Math.min(f18, f8 - (f15 - f7) * 0.5f));
            this.focusScrollPending = false;
        }
        this.focusDimT += ((bl ? 1.0f : 0.0f) - this.focusDimT) * (1.0f - (float)Math.exp(-f6 * 8.0f));
        if (!bl && this.focusDimT < 0.01f && !this.focusScrollPending) {
            this.focusName = null;
        }
        f8 = f + f3 - 7.5f;
        f7 = RenderHelper.effectiveCornerRadius(12.0f, f11, 280.0f);
        float f19 = Math.max(0.0f, RenderHelper.cornerEdgeInset(f7, 1.0f) + 1.5f - 3.0f);
        float f20 = f10 + 3.0f;
        float f21 = f15 - 6.0f - f19;
        float f22 = this.scrollBar.render(f8, f20, f21, f15, this.contentH, this.scroll, f4);
        if (this.scrollBar.isDragging()) {
            this.scroll = f22;
            this.scrollTarget = f22;
        }
        if (bl2) {
            GuiRenderState guiRenderState = ((GuiGraphicsExtractorAccessor)drawContext).kimiko_getGuiRenderState();
            guiRenderState.createNewRootLayer();
            guiRenderState.applyBlur();
            UI.markCardStratum();
        }
        this.renderCards(drawContext, f9, f10, f11, f12, f13, f14, f15, f4, f5, f6, list, true);
        this.appearInitialFrame = false;
    }

    public void render(DrawContext drawContext, float f, float f2, float f3, float f4, float f5, Category category, List<Module> list) {
        this.render(drawContext, f, f2, f3, f4, f4, f5, category, list);
    }

    private static int rgba(int n, int n2, int n3, float f) {
        int n4 = ModuleListRenderer.clamp255(f);
        if (n4 <= 0) {
            return 0;
        }
        return new Color(n, n2, n3, n4).getRGB();
    }

    public void warmup() {
        int n = 0x1FFFFFF;
        float f = -500.0f;
        float f2 = -500.0f;
        String string2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 .,:;!?-_()[]{}<>/\\|@#$%^&*+=\"'`~\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f\u0430\u0431\u0432\u0433\u0434\u0435\u0451\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f";
        Fonts.MONTSERRAT_MEDIUM.draw(string2, -500.0f, -500.0f, 6.0f, n);
        for (Category category : Category.values()) {
            for (Module module : this.getModules(category)) {
                this.enableAnims.computeIfAbsent(module.getName(), string -> ModuleListRenderer.createAnim(220));
                Fonts.KIMIKO.msdf(ICON_CACHE.get((Object)category), -500.0f, -500.0f, 6.5f, n);
                Fonts.MONTSERRAT_MEDIUM.draw(module.getDisplayName(), -500.0f, -500.0f, 7.5f, n);
                Fonts.MONTSERRAT_MEDIUM.shimmer(module.getDisplayName(), -500.0f, -500.0f, 7.5f, n, 0.5f, 0.35f, 0.5f, 0.5f);
                String string3 = module.getDescription() != null ? module.getDescription() : "";
                Fonts.MONTSERRAT_MEDIUM.draw(string3, -500.0f, -500.0f, 6.0f, n);
                Fonts.MONTSERRAT_MEDIUM.width(module.getDisplayName(), 7.5f);
                Fonts.MONTSERRAT_MEDIUM.width(string3, 6.0f);
            }
        }
        Fonts.KIMIKO.msdf(GEAR_GLYPH, -500.0f, -500.0f, 7.0f, n);
        Render2D.rect(-500.0f, -500.0f, 30.0f, 30.0f, 4.0f, 4, 4, 4, n);
        Render2D.outline(-500.0f, -500.0f, 30.0f, 30.0f, 4.0f, 4.0f, 4.0f, 4.0f, 0.7f, 0, 26447859, 0, 26447859);
    }

    private static Decelerate createAnim(int n) {
        Decelerate decelerate = (Decelerate)new Decelerate().setMs(n).setValue(1.0);
        decelerate.setDirection(Direction.BACKWARDS);
        decelerate.counter.setTime(System.currentTimeMillis() - 10000L);
        return decelerate;
    }

    private static char iconChar(Category category) {
        return switch (category) {
            default -> throw new MatchException(null, null);
            case Category.VISUALS -> 'p';
            case Category.DISPLAY -> 'j';
            case Category.UTILS -> 'r';
            case Category.EVENTS -> 'i';
            case Category.THEMES -> 'B';
        };
    }

    public void scroll(double d, float f) {
        float f2 = Math.max(0.0f, this.contentH - f);
        this.scrollTarget = Math.max(0.0f, Math.min(f2, this.scrollTarget - (float)d * 16.0f));
    }

    private static float colW(float f) {
        return (f - 5.0f - 10.0f) * 0.5f;
    }

    public float[] cardRect(List<Module> list, int n, float f, float f2, float f3) {
        if (list == null || n < 0 || n >= list.size()) {
            return null;
        }
        float f4 = ModuleListRenderer.colW(f3);
        float f5 = f + 5.0f;
        float f6 = f5 + f4 + 5.0f;
        float f7 = n % 2 == 0 ? f5 : f6;
        float f8 = f2 + 5.0f + this.cardTopInColumn(list, n, f4) - this.scroll;
        float f9 = this.baseCardHeight(list.get(n), f4);
        return new float[]{f7, f8, f4, f9};
    }

    private static int clamp255(float f) {
        return Math.max(0, Math.min(255, (int)f));
    }

    private List<String> descLines(Module module, float f) {
        String string = ModuleListRenderer.descKey(module, f);
        List<String> list = this.descLinesCache.get(string);
        if (list != null) {
            return list;
        }
        String string2 = module.getDescription() != null ? module.getDescription() : "";
        ArrayList<String> arrayList = new ArrayList<String>();
        if (!string2.isEmpty()) {
            float f2 = f - 16.0f - 4.0f;
            StringBuilder stringBuilder = new StringBuilder();
            for (String string3 : string2.split(" ")) {
                String string4;
                String string5 = string4 = stringBuilder.length() > 0 ? String.valueOf(stringBuilder) + " " + string3 : string3;
                if (Fonts.MONTSERRAT_MEDIUM.width(string4, 6.0f) > f2 && stringBuilder.length() > 0) {
                    arrayList.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder(string3);
                    continue;
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(' ');
                }
                stringBuilder.append(string3);
            }
            if (stringBuilder.length() > 0) {
                arrayList.add(stringBuilder.toString());
            }
        }
        this.descLinesCache.put(string, arrayList);
        return arrayList;
    }

    public List<Module> getModules(Category category2) {
        if (category2 == null) {
            return Collections.emptyList();
        }
        List<Module> list = this.moduleCache.computeIfAbsent(category2, category -> ModuleManager.get().getByCategory(category));
        EnumSet<Server> enumSet = ServerRestrictions.current();
        ArrayList<Module> arrayList = new ArrayList<Module>(list.size());
        for (Module module : list) {
            if (ServerRestrictions.isHiddenBy(module, enumSet)) continue;
            arrayList.add(module);
        }
        return arrayList;
    }

    public boolean isTransitioning() {
        return this.transitioning;
    }

    public float cardBlurMaxPhase() {
        return this.cardBlurMaxPhase;
    }

    public float[] cardBlurRects() {
        return this.cardBlurRects;
    }

    public void scrollbarRelease() {
        this.scrollBar.release();
    }

    public int cardBlurCount() {
        return this.cardBlurCount;
    }

    public float currentScroll() {
        return this.scroll;
    }

    public void resetCardBlur() {
        this.cardBlurCount = 0;
        this.cardBlurMaxPhase = 0.0f;
    }

    public void setAppearComposite(boolean bl) {
        this.appearComposite = bl;
    }

    public void finishTransition() {
        this.transitioning = false;
        this.fadingOut = Collections.emptyList();
        this.fadeOutTime = 0.0f;
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.lastRendered = null;
    }

    public void focusModule(String string) {
        this.focusName = string;
        this.focusUntilMs = System.currentTimeMillis() + 5000L;
        this.focusScrollPending = true;
    }

    public boolean hitSettingsIcon(float f, float f2, float f3, float f4, float f5, Module module) {
        if (module.getSettings().all().isEmpty()) {
            return false;
        }
        float f6 = f3 + f5 - 8.5f - 8.0f;
        float f7 = Fonts.KIMIKO.msdfWidth(GEAR_GLYPH, 7.0f);
        float f8 = f6 - 7.0f - f7;
        return f >= f8 - 3.5f && f <= f8 + f7 + 3.5f && f2 >= f4 + 3.5f && f2 <= f4 + 18.0f;
    }

    public boolean scrollbarGrab(float f, float f2) {
        return this.scrollBar.tryGrab(f, f2);
    }

    public void replayAppear() {
        this.lastRendered = null;
    }

    public boolean isFadeOutDone() {
        return this.transitioning && this.fadeOutTime <= 0.0f;
    }

    private float cardTopInColumn(List<Module> list, int n, float f) {
        float f2 = 0.0f;
        for (int i = n % 2; i < n; i += 2) {
            f2 += this.baseCardHeight(list.get(i), f) + 5.0f;
        }
        return f2;
    }

    private static int modulesSignature(List<Module> list) {
        int n = list.size();
        if (!list.isEmpty()) {
            n = n * 31 + list.get(0).getName().hashCode();
            n = n * 31 + list.get(list.size() - 1).getName().hashCode();
        }
        return n;
    }

    public float baseCardHeight(Module module, float f) {
        String string = ModuleListRenderer.descKey(module, f);
        Float f2 = this.baseHeightCache.get(string);
        if (f2 != null) {
            return f2.floatValue();
        }
        List<String> list = this.descLines(module, f);
        float f3 = list.isEmpty() ? 22.0f : 19.0f + (float)list.size() * 7.0f + 7.0f;
        this.baseHeightCache.put(string, Float.valueOf(f3));
        return f3;
    }

    public void prepareQuickAppear() {
        this.quickAppear = true;
    }

    private void startRowAnims(int n) {
        this.rowAppearAnims.clear();
        this.appearFadeMs = this.quickAppear ? 320 : 380;
        this.quickAppear = false;
        this.appearBaseMs = System.currentTimeMillis();
        this.appearInitialFrame = true;
    }

    public boolean scrollbarDragging() {
        return this.scrollBar.isDragging();
    }

    private static int indexOfModule(List<Module> list, String string) {
        for (int i = 0; i < list.size(); ++i) {
            if (!list.get(i).getName().equals(string)) continue;
            return i;
        }
        return -1;
    }

    private void renderCards(DrawContext drawContext, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, List<Module> list, boolean bl) {
        Render2D.pushScissor(drawContext, f, f2, f3, f7);
        RoundedScissor.push(drawContext, f, f2, f3, f7, 0.0f, 0.0f, 12.0f, 0.0f);
        float f11 = (1.0f - f9) * 8.0f;
        float f12 = Position.mouseX();
        float f13 = Position.mouseY();
        boolean bl2 = UI.isOpen();
        boolean bl3 = bl2 && f12 >= f && f12 <= f + f3 && f13 >= f2 && f13 <= f2 + f7 && !UI.isSettingsPopupVisible();
        float f14 = bl2 ? 1.0f - (float)Math.exp(-f10 * 16.0f) : 0.0f;
        EnumSet<Server> enumSet = ServerRestrictions.current();
        for (int i = 0; i < list.size(); ++i) {
            float f15;
            float f16;
            int n;
            float f17;
            float f18;
            float f19;
            float f20;
            float f21;
            float f22;
            Module module = list.get(i);
            boolean bl4 = ServerRestrictions.isBlockedBy(module, enumSet);
            int n2 = i % 2;
            int n3 = i / 2;
            float f23 = n2 == 0 ? f5 : f6;
            float f24 = this.baseCardHeight(module, f4);
            float f25 = f2 + 5.0f + this.cardTopInColumn(list, i, f4) - this.scroll + f11;
            boolean bl5 = bl3 && f12 >= f23 && f12 <= f23 + f4 && f13 >= f25 && f13 <= f25 + f24;
            float f26 = this.hoverAnims.getOrDefault(module.getName(), Float.valueOf(0.0f)).floatValue();
            f26 += ((bl5 ? 1.0f : 0.0f) - f26) * f14;
            this.hoverAnims.put(module.getName(), Float.valueOf(f26));
            if (f25 + f24 < f2 - 5.0f || f25 > f2 + f7 + 5.0f) continue;
            float f27 = f22 = bl ? this.rowAppear(n3) : 1.0f;
            if (f22 < 0.001f) continue;
            float f28 = f8;
            if (this.focusDimT > 0.01f && !module.getName().equals(this.focusName)) {
                f28 *= 1.0f - 0.75f * this.focusDimT;
            }
            if (bl4) {
                f28 *= 0.35f;
            }
            float f29 = Math.min(1.0f, f22 / 0.6f);
            float f30 = f29 * f29;
            float f31 = f22 < 0.6f ? 0.0f : (f22 - 0.6f) / 0.4f;
            float f32 = 1.0f - f31 * f31 * (3.0f - 2.0f * f31);
            f25 += (1.0f - f29) * 6.0f;
            boolean bl6 = f22 < 0.999f;
            boolean bl7 = this.appearComposite && bl6;
            float f33 = f21 = f28 * (!bl6 || bl7 ? 1.0f : f30);
            if (bl6) {
                f20 = 0.85f + 0.15f * f29;
                if (bl7 && this.cardBlurCount < 64) {
                    f19 = Math.max(f25, f2);
                    f18 = Math.min(f25 + f24, f2 + f7);
                    if (f18 - f19 > 0.5f) {
                        int n4 = this.cardBlurCount * 6;
                        this.cardBlurRects[n4] = f23;
                        this.cardBlurRects[n4 + 1] = f19;
                        this.cardBlurRects[n4 + 2] = f4;
                        this.cardBlurRects[n4 + 3] = f18 - f19;
                        this.cardBlurRects[n4 + 4] = f30;
                        this.cardBlurRects[n4 + 5] = f32;
                        ++this.cardBlurCount;
                        this.cardBlurMaxPhase = Math.max(this.cardBlurMaxPhase, f32);
                    }
                }
                f19 = f23 + f4 * 0.5f;
                f18 = f25 + f24 * 0.5f;
                drawContext.getMatrices().pushMatrix();
                drawContext.getMatrices().translate(f19, f18);
                drawContext.getMatrices().scale(f20, f20);
                drawContext.getMatrices().translate(-f19, -f18);
            }
            f20 = 6.0f;
            f19 = 6.0f;
            if (n2 == 1 && (f18 = f25 + f24) > (f17 = f2 + f7) - 16.0f) {
                float f34 = Math.max(0.0f, Math.min(1.0f, (f18 - (f17 - 16.0f)) / 16.0f));
                f20 = 6.0f + 6.0f * f34;
            }
            Decelerate decelerate = this.enableAnims.computeIfAbsent(module.getName(), string -> ModuleListRenderer.createAnim(220));
            decelerate.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
            float f35 = decelerate.getOutput().floatValue();
            Render2D.rect(f23, f25, f4, f24, 6.0f, f19, f20, 6.0f, ModuleListRenderer.rgba(10, 12, 16, (72.0f + 26.0f * f26) * f33));
            if (f35 > 0.01f) {
                int n5 = ClientAccent.gradientA(30.0f * f35 * f33);
                n = ClientAccent.gradientB(30.0f * f35 * f33);
                Render2D.rect(f23, f25, f4, f24, 6.0f, f19, f20, 6.0f, n5, n, n, n5);
            }
            int n6 = ModuleListRenderer.rgba(255, 255, 255, (14.0f + 12.0f * f26) * f33);
            n = ColorUtil.lerpColor(n6, ClientAccent.gradientA(65.0f * f33), f35);
            int n7 = ColorUtil.lerpColor(n6, ClientAccent.gradientB(65.0f * f33), f35);
            Render2D.outline(f23, f25, f4, f24, 6.0f, f19, f20, 6.0f, 0.6f, n, n7, n7, n);
            KeyBind keyBind = module.getBind();
            float f36 = 0.0f;
            if (keyBind.isBound()) {
                String string2 = ModuleListRenderer.badgeLabel(keyBind);
                f16 = Math.max(10.0f, Fonts.MONTSERRAT_MEDIUM.width(string2, 5.5f) + 6.0f);
                f15 = f23 + 8.0f;
                float f37 = f25 + 10.75f - 5.0f;
                Render2D.rect(f15, f37, f16, 10.0f, 3.0f, ModuleListRenderer.rgba(255, 255, 255, 16.0f * f21));
                Render2D.outline(f15, f37, f16, 10.0f, 3.0f, 0.5f, ModuleListRenderer.rgba(255, 255, 255, 30.0f * f21));
                float f38 = Fonts.MONTSERRAT_MEDIUM.width(string2, 5.5f);
                Fonts.MONTSERRAT_MEDIUM.draw(string2, f15 + (f16 - f38) * 0.5f, f37 + 1.75f, 5.5f, ModuleListRenderer.rgba(255, 255, 255, 205.0f * f21));
                f36 = f16 + 5.0f;
            }
            float f39 = f23 + 8.0f + f36;
            Fonts.MONTSERRAT_MEDIUM.draw(module.getDisplayName(), f39, f25 + 7.0f, 7.5f, ModuleListRenderer.rgba(255, 255, 255, (160.0f + 95.0f * f35) * f21));
            this.renderDesc(module, f23, f25, f4, f21, f35);
            f16 = f23 + f4 - 8.5f - 8.0f;
            f15 = f25 + 10.75f - 4.25f;
            boolean bl8 = bl5 && f12 >= f16 - 2.0f && f12 <= f16 + 8.5f + 2.0f && f13 >= f15 - 2.0f && f13 <= f15 + 8.5f + 2.0f;
            ModuleListRenderer.drawModuleCheck(f16, f15, f35, f21, bl8);
            if (!module.getSettings().all().isEmpty()) {
                boolean bl9 = UI.isSettingsOpenFor(module.getName());
                float f40 = Fonts.KIMIKO.msdfWidth(GEAR_GLYPH, 7.0f);
                float f41 = f16 - 7.0f - f40;
                float f42 = f25 + 10.75f - 3.5f;
                boolean bl10 = bl5 && f12 >= f41 - 3.0f && f12 <= f41 + f40 + 3.0f && f13 >= f25 + 4.0f && f13 <= f25 + 17.5f;
                float f43 = this.gearHoverAnims.getOrDefault(module.getName(), Float.valueOf(0.0f)).floatValue();
                f43 += ((bl10 ? 1.0f : 0.0f) - f43) * f14;
                this.gearHoverAnims.put(module.getName(), Float.valueOf(f43));
                float f44 = this.gearOpenAnims.getOrDefault(module.getName(), Float.valueOf(0.0f)).floatValue();
                if (bl2) {
                    f44 += ((bl9 ? 1.0f : 0.0f) - f44) * (1.0f - (float)Math.exp(-f10 * 12.0f));
                }
                this.gearOpenAnims.put(module.getName(), Float.valueOf(f44));
                int n8 = ColorUtil.lerpColor(ModuleListRenderer.rgba(255, 255, 255, (120.0f + 105.0f * f43) * f21), ClientAccent.accentSoft(235.0f * f21), f44);
                Fonts.KIMIKO.msdf(GEAR_GLYPH, f41, f42, 7.0f, n8);
            }
            if (!bl6) continue;
            drawContext.getMatrices().popMatrix();
        }
        RoundedScissor.pop();
        Render2D.popScissor(drawContext);
    }

    private float totalContentH(List<Module> list, float f) {
        float f2 = 0.0f;
        float f3 = 0.0f;
        for (int i = 0; i < list.size(); ++i) {
            float f4 = this.baseCardHeight(list.get(i), f) + 5.0f;
            if ((i & 1) == 0) {
                f2 += f4;
                continue;
            }
            f3 += f4;
        }
        float f5 = Math.max(f2, f3);
        if (f5 > 0.0f) {
            f5 -= 5.0f;
        }
        return 10.0f + f5;
    }

    public float getContentH() {
        return this.contentH;
    }

    private static void drawModuleCheck(float f, float f2, float f3, float f4, boolean bl) {
        float f5;
        Render2D.rect(f, f2, 8.5f, 8.5f, 2.9750001f, ModuleListRenderer.rgba(17, 17, 18, 91.0f * f4));
        if (f3 < 0.996f) {
            f5 = f4 * (1.0f - f3);
            float f6 = 1.2750001f;
            Render2D.outline(f + f6, f2 + f6, 8.5f - f6 * 2.0f, 8.5f - f6 * 2.0f, 2.5500002f, CHECK_RING_THICKNESS, ClientAccent.accent((bl ? 80.0f : 35.0f) * f5));
            float f7 = 2.5500002f;
            Render2D.outline(f + f7, f2 + f7, 8.5f - f7 * 2.0f, 8.5f - f7 * 2.0f, 2.125f, CHECK_RING_THICKNESS, ClientAccent.accent((bl ? 45.0f : 20.0f) * f5));
        }
        if (f3 > 0.004f) {
            f5 = 255.0f * f3 * f4;
            int n = ClientAccent.gradientA(f5);
            int n2 = ModuleListRenderer.checkDarker(ClientAccent.gradientB(f5), 100);
            Render2D.rect(f, f2, 8.5f, 8.5f, 2.9750001f, n, n, n2, n2);
            float f8 = 2.125f;
            float f9 = 8.5f - f8 * 2.0f;
            Render2D.rect(f + f8, f2 + f8, f9, f9, f9 * 0.5f, ModuleListRenderer.rgba(255, 255, 255, 255.0f * f3 * f4));
        }
    }

    private static int checkDarker(int n, int n2) {
        int n3 = n >>> 24 & 0xFF;
        int n4 = Math.max(0, (n >> 16 & 0xFF) - n2);
        int n5 = Math.max(0, (n >> 8 & 0xFF) - n2);
        int n6 = Math.max(0, (n & 0xFF) - n2);
        return n3 << 24 | n4 << 16 | n5 << 8 | n6;
    }

    public void beginFadeOut(Category category) {
        if (category == null) {
            return;
        }
        this.fadingOut = this.getModules(category);
        this.fadeOutTime = 0.15f;
        this.transitioning = true;
    }

    private boolean hasUnfinishedAppear() {
        for (Decelerate decelerate : this.rowAppearAnims.values()) {
            if (!(decelerate.getOutput().floatValue() < 0.999f)) continue;
            return true;
        }
        return false;
    }

    private float rowAppear(int n) {
        Decelerate decelerate = this.rowAppearAnims.get(n);
        if (decelerate == null) {
            long l = this.appearInitialFrame ? this.appearBaseMs : System.currentTimeMillis();
            decelerate = (Decelerate)new Decelerate().setMs(this.appearFadeMs).setValue(1.0);
            decelerate.counter.setTime(l);
            this.rowAppearAnims.put(n, decelerate);
        }
        float f = decelerate.getOutput().floatValue();
        return Math.max(0.0f, Math.min(1.0f, f));
    }

    public float getScroll() {
        return this.scroll;
    }

    private static String descKey(Module module, float f) {
        return module.getName() + "|" + (int)f;
    }

    private void renderDesc(Module module, float f, float f2, float f3, float f4, float f5) {
        List<String> list = this.descLines(module, f3);
        if (list.isEmpty()) {
            return;
        }
        int n = ModuleListRenderer.rgba(255, 255, 255, (78.0f + 27.0f * f5) * f4);
        float f6 = f2 + 19.0f;
        for (String string : list) {
            Fonts.MONTSERRAT_MEDIUM.draw(string, f + 8.0f, f6, 6.0f, n);
            f6 += 7.0f;
        }
    }

    private static int rowCount(int n) {
        return (n + 1) / 2;
    }

    private static String badgeLabel(KeyBind keyBind) {
        String string = keyBind.getDisplayName();
        return string == null || string.isEmpty() ? "?" : string;
    }
}

