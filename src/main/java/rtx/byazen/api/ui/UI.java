package rtx.byazen.api.ui;
import rtx.byazen.api.events.EventHandler;
import fun.shape.profile.Profile;
import fun.shape.profile.Role;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import rtx.byazen.IMinecraft;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.impl.input.MouseButtonEvent;
import rtx.byazen.api.events.impl.input.MouseButtonEvent.Action;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.ClickGui;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.impl.Utils.guishare.GuiShareCloseState;
import rtx.byazen.api.modules.impl.Utils.guishare.GuiShareLocalSnapshot;
import rtx.byazen.api.modules.impl.Utils.guishare.GuiSharePopupRow;
import rtx.byazen.api.modules.impl.Utils.guishare.GuiShareThemeState;
import rtx.byazen.api.modules.restrict.Server;
import rtx.byazen.api.modules.restrict.ServerRestrictions;
import rtx.byazen.api.ui.BaseScreen;
import rtx.byazen.api.ui.BindPopup;
import rtx.byazen.api.ui.SettingsPopup;
import rtx.byazen.api.ui.events.EventsRenderer;
import rtx.byazen.api.ui.module.DiscordAvatar;
import rtx.byazen.api.ui.module.ModuleListRenderer;
import rtx.byazen.api.ui.module.SearchField;
import rtx.byazen.api.ui.settings.RenderHelper;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BindSetting;
import rtx.byazen.api.ui.settings.impl.TextSetting;
import rtx.byazen.api.ui.theme.AccentGradient;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.api.ui.theme.ThemeManager;
import rtx.byazen.api.ui.theme.ThemesRenderer;
import rtx.byazen.api.ui.window.GuiShatterAnimation;
import rtx.byazen.api.ui.window.WorldGuiCloseAnimation;
import rtx.byazen.mixin.accessor.GuiGraphicsExtractorAccessor;
import rtx.byazen.utils.animations.Decelerate;
import rtx.byazen.utils.animations.Direction;
import rtx.byazen.utils.animations.GuiMotionAnimation;
import rtx.byazen.utils.discord.rpc.DiscordRPCManager;
import rtx.byazen.utils.key.KeyBind;
import rtx.byazen.utils.render.fonts.Fonts;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.post.guilayerblur.GuiCapture;
import rtx.byazen.utils.render.post.guilayerblur.GuiCapture.Source;
import rtx.byazen.utils.render.post.guilayerblur.GuiLayerBlurRenderer;
import rtx.byazen.utils.render.post.guimotionblur.GuiMotionBlurRenderer;
import rtx.byazen.utils.config.ModuleFavorites;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.render.render2d.Render2DCoordinateSpace;
import rtx.byazen.utils.render.render2d.blur.BlurFramebuffer;
import rtx.byazen.utils.render.render2d.gif.GifRenderer;
import rtx.byazen.utils.sounds.Sounds;

public class UI
extends BaseScreen
implements GuiCapture.Source {
    private static final String[] EVENT_SUBS = new String[]{"Events", "Mines"};
    private static final String[] EVENT_SUB_ICONS = new String[]{"b", "m"};
    public static final UI INSTANCE = new UI();
    public static final float PANEL_W = 430.0f;
    public static final float PANEL_H = 290.0f;
    public static final float SIDEBAR_W = 110.0f;
    public static final float CONTENT_X_OFF = 117.0f;
    public static final float CONTENT_INSET = 122.0f;
    public static final float CONTENT_HEIGHT = 280.0f;
    private static final float CONTENT_Y_OFFSET = 5.0f;
    private static final Category[] MAIN_CATEGORIES = new Category[]{Category.VISUALS, Category.DISPLAY, Category.UTILS};
    private static final Category[] OTHER_CATEGORIES = new Category[]{Category.THEMES};
    private static final float CAT_COL_TOP = 34.0f;
    private static final float CAT_HEADER_H = 20.0f;
    private static final float CAT_SUB_GAP = 4.0f;
    private static final float CAT_SUB_ROW_H = 18.0f;
    private static final float CAT_OTHERS_GAP = 12.0f;
    private static final float CAT_EVENTS_GAP = 3.0f;
    private static final float CAT_OTHER_ROW_H = 19.0f;
    private Category targetCategory = null;
    private Category contentCategory = null;
    private String oldSubText = "\u041d\u0435 \u0432\u044b\u0431\u0440\u0430\u043d\u043e";
    private String newSubText = "\u041d\u0435 \u0432\u044b\u0431\u0440\u0430\u043d\u043e";
    private final Map<Category, Decelerate> categoryAnims = new EnumMap<Category, Decelerate>(Category.class);
    private final Decelerate subTextAnim = UI.createAnim(300);
    private final Decelerate modulesHeaderAnim = UI.createAnim(200);
    private final Decelerate eventsHeaderAnim = UI.createAnim(200);
    private int eventsSub = 0;
    private final float[] eventsSubT = new float[EVENT_SUBS.length];
    private boolean subTextAnimDone = true;
    private static final float CATEGORY_FADE_SEC = 0.15f;
    private float categoryT = 1.0f;
    private float themesRowT = 1.0f;
    private final ModuleListRenderer moduleList = new ModuleListRenderer();
    private final SearchField search = new SearchField();
    private final BindPopup bindPopup = new BindPopup();
    private final SettingsPopup settingsPopup = new SettingsPopup();
    private final SettingsSearchPalette settingsPalette = new SettingsSearchPalette();
    private final ThemesRenderer themesRenderer = new ThemesRenderer();
    private final EventsRenderer eventsRenderer = new EventsRenderer();
    private final GuiMotionAnimation screenAnim = new GuiMotionAnimation();
    private long lastNs = System.nanoTime();
    private static Screen pendingAfterClose;
    private static boolean motionBlurPending;
    private static float motionBlurOpacity;
    private static float motionBlurRadius;
    private static float motionBlurScale;
    private static float motionBlurOriginX;
    private static float motionBlurOriginY;
    private static int motionBlurX;
    private static int motionBlurY;
    private static int motionBlurW;
    private static int motionBlurH;
    private static int motionBlurSrcX;
    private static int motionBlurSrcY;
    private static int motionBlurSrcW;
    private static int motionBlurSrcH;
    private static final float[] motionBlurMask;
    private static final float CARD_BLUR_MAX_RADIUS = 22.0f;
    private static boolean cardStratumMarked;
    private static boolean cardBlurPending;
    private static final float[] cardBlurMask;
    private static int cardBlurMaskCount;
    private static boolean cardCaptureStaged;
    private static boolean cardBlurCaptured;
    private static boolean panelSplitMarked;
    private static boolean vanillaBlurRequested;
    private static boolean popupStratumMarked;
    private static final float POPUP_BLUR_GUI_RADIUS = 9.0f;
    private static final float[] popupBlurMask;
    private static int popupBlurMaskCount;
    private static boolean popupLayerBlurWanted;
    private static boolean popupBlurWanted;
    private static boolean popupBlurStaged;
    private static boolean popupBlurCaptured;
    private static boolean popupBlurPending;
    private static float popupBlurRadius;
    private static int popupBlurX;
    private static int popupBlurY;
    private static int popupBlurW;
    private static int popupBlurH;
    private static float popupBlurOriginX;
    private static float popupBlurOriginY;
    private static float guiCaptureScale;
    private static float guiCaptureBlurMainPx;
    private final Decelerate placeholderAnim = UI.createAnim(200);
    private float parallaxX;
    private float parallaxY;
    private float lastCameraYaw = Float.NaN;
    private float lastCameraPitch;
    private static final String RU_LAYOUT = "\u0439\u0446\u0443\u043a\u0435\u043d\u0433\u0448\u0449\u0437\u0445\u044a\u0444\u044b\u0432\u0430\u043f\u0440\u043e\u043b\u0434\u0436\u044d\u044f\u0447\u0441\u043c\u0438\u0442\u044c\u0431\u044e\u0451";
    private static final String EN_LAYOUT = "qwertyuiop[]asdfghjkl;'zxcvbnm,.`";

    private UI() {
        super((Text)Text.literal((String)"UI"));
        this.placeholderAnim.setDirection(Direction.FORWARDS);
        EventBus.get().subscribe(this);
    }

    static {
        motionBlurMask = new float[12];
        cardBlurMask = new float[396];
        popupBlurMask = new float[18];
        guiCaptureScale = 1.0f;
    }

    public static boolean isOpen() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        return minecraftClient != null && minecraftClient.currentScreen == INSTANCE;
    }

    private static int color(int n, int n2, int n3, int n4, float f) {
        int n5 = Math.max(0, Math.min(255, Math.round((float)n4 * f)));
        if (n5 <= 0) {
            return 0;
        }
        return new Color(n, n2, n3, n5).getRGB();
    }

    private void renderNoCategoryPlaceholder(DrawContext drawContext, float f, float f2, float f3, float f4) {
        float f5 = f + 117.0f;
        float f6 = f2 + 5.0f;
        float f7 = f3 - 122.0f;
        float f8 = 280.0f;
        float f9 = 60.0f;
        float f10 = f5 + (f7 - f9) * 0.5f;
        float f11 = f6 + (f8 - f9) * 0.5f - 10.0f;
        GifRenderer.draw(drawContext, f10, f11, f9, f9, 8.0f, "byazen:gif/kity.gif", f4);
        String string = "\u041e\u0442\u043a\u0440\u043e\u0439\u0442\u0435 \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044e \u0447\u0442\u043e\u0431\u044b \u043d\u0430\u0447\u0430\u0442\u044c";
        float f12 = 6.0f;
        float f13 = Fonts.MONTSERRAT_MEDIUM.width(string, f12);
        Fonts.MONTSERRAT_MEDIUM.draw(string, f5 + (f7 - f13) * 0.5f, f11 + f9 + 8.0f, f12, UI.color(255, 255, 255, 100, f4));
    }

    public void warmupRender() {
        this.moduleList.warmup();
    }

    public static void closeInto(Screen screen) {
        pendingAfterClose = screen;
        UI uI = INSTANCE;
        WorldGuiCloseAnimation.cancel();
        GuiShatterAnimation.cancel();
        uI.screenAnim.snapClosed();
        uI.releaseAllDrags();
        uI.settingsPopup.close();
        uI.search.blur();
        if (MinecraftClient.getInstance().currentScreen == uI) {
            MinecraftClient.getInstance().setScreen(screen);
        }
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

    private static boolean ctrlHeld() {
        long l = IMinecraft.mc.getWindow().getHandle();
        return GLFW.glfwGetKey((long)l, (int)341) == 1 || GLFW.glfwGetKey((long)l, (int)345) == 1;
    }

    @EventHandler
    private void onRawMouse(MouseButtonEvent mouseButtonEvent) {
        if (MinecraftClient.getInstance().currentScreen != this) {
            return;
        }
        if (mouseButtonEvent.action != MouseButtonEvent.Action.PRESS || mouseButtonEvent.button != 2) {
            return;
        }
        if (this.settingsPopup.isOpen() && this.settingsPopup.middleClick(Position.mouseX(), Position.mouseY())) {
            mouseButtonEvent.cancel();
            return;
        }
        Module module = this.moduleAtCursor();
        if (module != null) {
            this.bindPopup.open(module, Position.mouseX(), Position.mouseY());
            mouseButtonEvent.cancel();
        }
    }

    public void close() {
        WorldGuiCloseAnimation.cancel();
        GuiShatterAnimation.cancel();
        this.screenAnim.snapClosed();
        this.releaseAllDrags();
        this.settingsPopup.close();
        this.search.blur();
        Sounds.play("gui_close");
        if (MinecraftClient.getInstance().currentScreen == this) {
            MinecraftClient.getInstance().setScreen(null);
        }
    }

    public boolean keyPressed(KeyInput input) {
        int n;
        Setting setting;
        if (this.settingsPalette.isOpen() && this.settingsPalette.keyPressed(input)) {
            return true;
        }
        if (UI.ctrlHeld() && input.key() == 70 && this.screenAnim.canInteract()) {
            this.settingsPalette.openPalette();
            Sounds.play("settings_open");
            return true;
        }
        if (this.screenAnim.isClosing()) {
            int n2;
            ClickGui clickGui = ModuleManager.get().get(ClickGui.class);
            int n3 = n2 = clickGui != null ? clickGui.getBind().getCode() : 344;
            if (input.key() == n2) {
                pendingAfterClose = null;
                WorldGuiCloseAnimation.reverse();
                GuiShatterAnimation.gather(WorldGuiCloseAnimation.isReversing() ? WorldGuiCloseAnimation.remainingNanos() : 0L);
                this.screenAnim.resumeOpening();
                Sounds.play("gui_open");
            }
            return true;
        }
        if (this.bindPopup.isOpen()) {
            if (this.bindPopup.keyPressed(input.key())) {
                return true;
            }
            if (input.key() == 256) {
                this.bindPopup.close();
            }
            return true;
        }
        for (Setting setting2 : this.settingsPopup.widgets()) {
            if (!(setting2 instanceof BindSetting) || !((BindSetting)(setting = (BindSetting)setting2)).isListening()) continue;
            int n4 = input.key();
            boolean bl = n4 == 256 || n4 == 261;
            ((BindSetting)setting).setKey(bl ? -1 : n4);
            ((BindSetting)setting).setListening(false);
            return true;
        }
        for (Setting setting2 : this.settingsPopup.widgets()) {
            if (!(setting2 instanceof TextSetting) || !((TextSetting)(setting = (TextSetting)setting2)).isFocused() || !((TextSetting)setting).typeKey(input.key())) continue;
            return true;
        }
        if (input.key() == 256 && this.settingsPopup.hasOpenOverlay()) {
            this.settingsPopup.closeOverlays();
            return true;
        }
        if (input.key() == 256 && this.settingsPopup.isOpen()) {
            this.settingsPopup.close();
            return true;
        }
        if (this.search.isTyping() && this.search.keyPressed(input)) {
            return true;
        }
        ClickGui clickGui = ModuleManager.get().get(ClickGui.class);
        int n5 = n = clickGui != null ? clickGui.getBind().getCode() : 344;
        if (input.key() == 256 || input.key() == n) {
            this.close();
            return true;
        }
        return super.keyPressed(input);
    }

    public void removed() {
        if (!this.screenAnim.isClosing()) {
            this.screenAnim.snapClosed();
            GuiShatterAnimation.cancel();
            this.settingsPopup.close();
            this.search.blur();
        }
        this.releaseAllDrags();
        super.removed();
    }

    protected void init() {
        pendingAfterClose = null;
        BaseScreen.dropClosingOverlay();
        GuiCapture.bind(this);
        if (this.screenAnim.isClosing()) {
            WorldGuiCloseAnimation.reverse();
            GuiShatterAnimation.gather(WorldGuiCloseAnimation.isReversing() ? WorldGuiCloseAnimation.remainingNanos() : 0L);
            this.screenAnim.resumeOpening();
        } else {
            WorldGuiCloseAnimation.cancel();
            GuiShatterAnimation.cancel();
            this.screenAnim.startOpening();
        }
        this.lastNs = System.nanoTime();
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (!this.screenAnim.canInteract()) {
            return true;
        }
        if (this.settingsPalette.isOpen() && this.settingsPalette.mouseClicked(click)) {
            return true;
        }
        float f = 430.0f;
        float f2 = 290.0f;
        float f3 = Position.screenWidth() / 2.0f - f / 2.0f;
        float f4 = Position.screenHeight() / 2.0f - f2 / 2.0f;
        float f5 = Position.mouseX();
        float f6 = Position.mouseY();
        for (Setting setting : this.settingsPopup.widgets()) {
            BindSetting bindSetting;
            if (!(setting instanceof BindSetting) || !(bindSetting = (BindSetting)setting).isListening()) continue;
            bindSetting.setKey(KeyBind.mouse(click.button()).getCode());
            bindSetting.setListening(false);
            return true;
        }
        if (this.bindPopup.isOpen() && this.bindPopup.mouseBind(click.button())) {
            return true;
        }
        if (this.bindPopup.isOpen() && click.button() == 0) {
            this.bindPopup.click(f5, f6);
            return true;
        }
        if (this.settingsPopup.isOpen() && click.button() == 0) {
            this.settingsPopup.click(f5, f6);
            return true;
        }
        if (this.settingsPopup.isOpen() && click.button() == 1 && this.settingsPopup.contains(f5, f6)) {
            return true;
        }
        if (this.isModuleView() && this.search.mouseClicked(f5, f6, click.button())) {
            return true;
        }
        if (this.contentCategory == Category.EVENTS && click.button() == 0) {
            if (this.eventsRenderer.scrollbarGrab(f5, f6)) {
                return true;
            }
            if (this.eventsRenderer.click(f5, f6)) {
                return true;
            }
        }
        if (this.contentCategory == Category.THEMES && click.button() == 0 && this.themesRenderer.click(f3, f4, f, f5, f6)) {
            return true;
        }
        if (click.button() == 0 || click.button() == 1) {
            boolean bl = this.isModuleView();
            if (bl && this.moduleList.scrollbarGrab(f5, f6)) {
                return true;
            }
            float f7 = f3 + 117.0f;
            float f8 = f4 + 5.0f + 30.0f;
            float f9 = f - 122.0f;
            float f10 = 250.0f;
            if (bl) {
                List<Module> list = this.filteredModules(this.contentCategory);
                if (f5 >= f7 && f5 <= f7 + f9 && f6 >= f8 && f6 <= f8 + f10) {
                    for (int i = 0; i < list.size(); ++i) {
                        Module module = list.get(i);
                        float[] fArray = this.moduleList.cardRect(list, i, f7, f8, f9);
                        if (fArray == null) continue;
                        float f11 = fArray[0];
                        float f12 = fArray[1];
                        float f13 = fArray[2];
                        float f14 = fArray[3];
                        if (!(f5 >= f11) || !(f5 <= f11 + f13) || !(f6 >= f12) || !(f6 <= f12 + f14)) continue;
                        float[] fArray2 = this.moduleList.favoriteRect(module, f11, f12, f13);
                        if (click.button() == 0 && f5 >= fArray2[0] - 2.0f && f5 <= fArray2[0] + fArray2[2] + 2.0f
                                && f6 >= fArray2[1] - 2.0f && f6 <= fArray2[1] + fArray2[3] + 2.0f) {
                            boolean bl4 = ModuleFavorites.toggle(module);
                            Sounds.play(bl4 ? "gui_open" : "gui_close");
                            NotificationsModule.notify((bl4 ? "В избранном: " : "Убрано из избранного: ") + module.getDisplayName(), 1600L);
                            return true;
                        }
                        if (click.button() == 1) {
                            if (!module.getSettings().all().isEmpty()) {
                                boolean bl2 = this.settingsPopup.toggle(module, f5, f6, f7, f8, f9, f10);
                                Sounds.play(bl2 ? "module_settings_open" : "module_settings_close");
                            }
                            return true;
                        }
                        if (this.search.hasText() && UI.ctrlHeld()) {
                            Category category = module.getCategory();
                            this.moduleList.focusModule(module.getName());
                            if (this.targetCategory != category) {
                                Sounds.play("select_category");
                                this.selectCategory(category);
                            } else {
                                this.search.setText("");
                                this.search.blur();
                            }
                            return true;
                        }
                        if (this.moduleList.hitSettingsIcon(f5, f6, f11, f12, f13, module)) {
                            boolean bl3 = this.settingsPopup.toggle(module, f5, f6, f7, f8, f9, f10);
                            Sounds.play(bl3 ? "module_settings_open" : "module_settings_close");
                            return true;
                        }
                        if (!(module instanceof ClickGui) && !(module instanceof InterfaceModule)) {
                            module.toggle();
                        }
                        return true;
                    }
                }
            }
            if (click.button() == 1) {
                return true;
            }
        }
        if (click.button() != 0) {
            return super.mouseClicked(click, doubled);
        }
        int n = this.eventsSubAt(f3, f4, f5, f6);
        if (n >= 0) {
            if (this.targetCategory != Category.EVENTS || this.eventsSub != n) {
                Sounds.play("select_category");
            }
            this.selectEventsSub(n);
            return true;
        }
        Category category = this.categoryButtonAt(f3, f4, f5, f6);
        if (category != null) {
            if (category != this.targetCategory) {
                Sounds.play("select_category");
            }
            this.selectCategory(category);
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.screenAnim.canInteract()) {
            return true;
        }
        if (this.settingsPalette.mouseScrolled(verticalAmount)) {
            return true;
        }
        if (this.bindPopup.isOpen()) {
            this.bindPopup.close();
            return true;
        }
        float f = 430.0f;
        float f2 = Position.screenWidth() / 2.0f - f / 2.0f;
        float f3 = Position.screenHeight() / 2.0f - 145.0f;
        float f4 = f2 + 117.0f;
        float f5 = f3 + 5.0f;
        float f6 = f - 122.0f;
        float f7 = 280.0f;
        double d = Position.mouseX();
        double d2 = Position.mouseY();
        if (this.settingsPopup.isOpen() && this.settingsPopup.scroll((float)d, (float)d2, verticalAmount)) {
            return true;
        }
        if (d >= (double)f4 && d <= (double)(f4 + f6) && d2 >= (double)f5 && d2 <= (double)(f5 + f7)) {
            if (this.contentCategory == Category.EVENTS) {
                this.eventsRenderer.scroll(verticalAmount, f7);
            } else if (this.contentCategory == Category.THEMES) {
                this.themesRenderer.scroll(verticalAmount, f7);
            } else {
                this.settingsPopup.close();
                this.moduleList.scroll(verticalAmount, f7 - 30.0f);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public boolean mouseReleased(Click click) {
        if (this.screenAnim.isClosing()) {
            return true;
        }
        this.settingsPalette.mouseReleased(click.button());
        if (click.button() == 0) {
            this.settingsPopup.releaseDrags();
            this.bindPopup.releaseDrag();
        }
        this.moduleList.scrollbarRelease();
        this.eventsRenderer.scrollbarRelease();
        this.search.mouseReleased(click.button());
        return super.mouseReleased(click);
    }

    public boolean charTyped(CharInput input) {
        if (this.screenAnim.isClosing()) {
            return true;
        }
        if (this.settingsPalette.charTyped(input)) {
            return true;
        }
        if (this.bindPopup.isOpen()) {
            return true;
        }
        for (Setting setting : this.settingsPopup.widgets()) {
            TextSetting textSetting;
            if (!(setting instanceof TextSetting) || !(textSetting = (TextSetting)setting).isFocused()) continue;
            textSetting.typeChar(input.codepoint());
            return true;
        }
        if (this.search.isTyping() && this.search.charTyped(input)) {
            return true;
        }
        return super.charTyped(input);
    }

    public static boolean isSearchTyping() {
        return UI.INSTANCE.search.isTyping();
    }

    public static boolean guiCaptureActive() {
        boolean bl;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (!GuiCapture.isBound(INSTANCE)) {
            return false;
        }
        boolean bl2 = bl = WorldGuiCloseAnimation.isActive() && !WorldGuiCloseAnimation.isFinished() && (minecraftClient == null || minecraftClient.currentScreen == null || WorldGuiCloseAnimation.isReversing());
        boolean bl3 = UI.INSTANCE.screenAnim.isClosing() && (WorldGuiCloseAnimation.isActive() ? bl : !UI.INSTANCE.screenAnim.isCloseFinished());
        return !(!UI.isOpen() && !bl3 || !UI.INSTANCE.screenAnim.isAnimating() && !bl);
    }

    public static GuiShareLocalSnapshot shareSnapshot() {
        UI uI = INSTANCE;
        if (!UI.isOpen()) {
            return null;
        }
        Category category = uI.targetCategory;
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Module module : uI.filteredModules(category)) {
            if (!module.isEnabled()) continue;
            arrayList.add(module.getName());
        }
        Object object = null;
        try {
            object = DiscordAvatar.currentUrl();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        float f = Position.screenWidth() / 2.0f - 215.0f;
        float f2 = Position.screenHeight() / 2.0f - 145.0f;
        return new GuiShareLocalSnapshot(category == null ? "" : category.name(), uI.eventsSub, uI.eventsRenderer.currentScroll(), uI.moduleList.currentScroll(), (Position.mouseX() - f) / 430.0f, (Position.mouseY() - f2) / 290.0f, uI.settingsPopup.shareModuleName(), uI.settingsPopup.shareScroll(), (uI.settingsPopup.shareX() - f) / 430.0f, (uI.settingsPopup.shareY() - f2) / 290.0f, GuiSharePopupRow.capture(uI.settingsPopup.shareModule(), uI.settingsPopup.widgets()), uI.search.getText(), arrayList, UI.profileRole(), (String)(object == null ? "" : object), GuiShareThemeState.capture(), ThemeManager.current().name(), uI.themesRenderer.currentScroll());
    }

    public static GuiShareCloseState shareCloseState() {
        if (!WorldGuiCloseAnimation.isActive() || WorldGuiCloseAnimation.isFinished()) {
            return GuiShareCloseState.IDLE;
        }
        float[] fArray = GuiShatterAnimation.beginRect();
        return new GuiShareCloseState(true, 1.0f - WorldGuiCloseAnimation.progress(), GuiShatterAnimation.local().lastProgress(), WorldGuiCloseAnimation.screenScale(), GuiShatterAnimation.seed(), fArray[0], fArray[1], fArray[2], fArray[3], fArray[4], fArray[5], fArray[6]);
    }

    private void releaseAllDrags() {
        this.settingsPopup.releaseDrags();
        this.moduleList.scrollbarRelease();
        this.eventsRenderer.scrollbarRelease();
    }

    public static float guiCaptureScale() {
        return guiCaptureScale;
    }

    public static boolean isSettingsPopupVisible() {
        return UI.isOpen() && UI.INSTANCE.settingsPopup.isVisible();
    }

    private List<Module> filteredModules(Category category) {
        String string = this.search.getText().trim().toLowerCase(Locale.ROOT);
        if (string.isEmpty()) {
            return this.moduleList.getModules(category);
        }
        String string2 = UI.layoutNormalize(string);
        String string3 = string.replace(" ", "");
        String string4 = string2.replace(" ", "");
        ArrayList<Module> arrayList = new ArrayList<Module>();
        EnumSet<Server> enumSet = ServerRestrictions.current();
        for (Module module : ModuleManager.get().getAll()) {
            if (ServerRestrictions.isHiddenBy(module, enumSet)) continue;
            String string5 = module.getName().toLowerCase(Locale.ROOT);
            String string6 = string5.replace(" ", "");
            if (!string5.contains(string) && !string5.contains(string2) && !string6.contains(string3) && !string6.contains(string4)) continue;
            arrayList.add(module);
        }
        return arrayList;
    }

    public static boolean isSettingsOpenFor(String string) {
        return UI.isOpen() && UI.INSTANCE.settingsPopup.isOpenFor(string);
    }

    public static boolean consumePopupStratumMark() {
        boolean bl = popupStratumMarked;
        popupStratumMarked = false;
        return bl;
    }

    public static boolean consumePopupBlurCapture() {
        if (!popupBlurWanted) {
            return false;
        }
        popupBlurWanted = false;
        popupBlurCaptured = true;
        return true;
    }

    public static boolean consumePopupLayerCapture() {
        if (!popupLayerBlurWanted) {
            return false;
        }
        popupLayerBlurWanted = false;
        popupBlurCaptured = true;
        return true;
    }

    public static void requestVanillaBlurAtSplit() {
        vanillaBlurRequested = true;
    }

    public static boolean consumeVanillaBlurRequest() {
        boolean bl = vanillaBlurRequested;
        vanillaBlurRequested = false;
        return bl;
    }

    private static boolean isMainCategory(Category category) {
        if (category == null) {
            return false;
        }
        for (Category category2 : MAIN_CATEGORIES) {
            if (category2 != category) continue;
            return true;
        }
        return false;
    }

    public static void applyMainCompositeAtSplit() {
        if (!motionBlurPending) {
            return;
        }
        motionBlurPending = false;
        cardBlurPending = false;
        GuiMotionBlurRenderer.applyWithCopy(motionBlurOpacity, motionBlurRadius, motionBlurX, motionBlurY, motionBlurW, motionBlurH, motionBlurMask, 2, motionBlurSrcX, motionBlurSrcY, motionBlurSrcW, motionBlurSrcH, motionBlurScale, motionBlurOriginX, motionBlurOriginY);
    }

    private void stagePopupBlur() {
        if (!popupBlurStaged) {
            return;
        }
        popupBlurStaged = false;
        int n = 1;
        if (this.settingsPopup.writeBlurRect(popupBlurMask, n * 6)) {
            ++n;
        }
        if (this.bindPopup.writeBlurRect(popupBlurMask, n * 6)) {
            ++n;
        }
        if (n <= 1) {
            return;
        }
        float f = 0.0f;
        for (int i = 1; i < n; ++i) {
            f = Math.max(f, popupBlurMask[i * 6 + 5]);
        }
        if (f <= 0.004f) {
            return;
        }
        float f2 = Render2DCoordinateSpace.designGuiScale();
        float f3 = Math.max(0.5f, 9.0f * f2 * f);
        float f4 = Float.MAX_VALUE;
        float f5 = Float.MAX_VALUE;
        float f6 = -3.4028235E38f;
        float f7 = -3.4028235E38f;
        for (int i = 1; i < n; ++i) {
            int n2 = i * 6;
            float f8 = popupBlurMask[n2 + 5] > 0.004f ? f3 : 0.0f;
            float f9 = (popupBlurMask[n2] + this.parallaxX) * f2 - f8;
            float f10 = (popupBlurMask[n2 + 1] + this.parallaxY) * f2 - f8;
            float f11 = popupBlurMask[n2 + 2] * f2 + f8 * 2.0f;
            float f12 = popupBlurMask[n2 + 3] * f2 + f8 * 2.0f;
            UI.popupBlurMask[n2] = f9;
            UI.popupBlurMask[n2 + 1] = f10;
            UI.popupBlurMask[n2 + 2] = f11;
            UI.popupBlurMask[n2 + 3] = f12;
            f4 = Math.min(f4, f9);
            f5 = Math.min(f5, f10);
            f6 = Math.max(f6, f9 + f11);
            f7 = Math.max(f7, f10 + f12);
        }
        float f13 = 24.0f + f3;
        UI.popupBlurMask[0] = f4 - f13;
        UI.popupBlurMask[1] = f5 - f13;
        UI.popupBlurMask[2] = f6 - f4 + f13 * 2.0f;
        UI.popupBlurMask[3] = f7 - f5 + f13 * 2.0f;
        UI.popupBlurMask[4] = -1.0f;
        UI.popupBlurMask[5] = 0.0f;
        popupBlurMaskCount = n;
        float f14 = f13 + f3 * 2.0f + 8.0f;
        popupBlurRadius = f3;
        popupBlurX = Math.round(f4 - f14);
        popupBlurY = Math.round(f5 - f14);
        popupBlurW = Math.round(f6 - f4 + f14 * 2.0f);
        popupBlurH = Math.round(f7 - f5 + f14 * 2.0f);
        popupBlurOriginX = (f4 + f6) * 0.5f;
        popupBlurOriginY = (f5 + f7) * 0.5f;
        popupBlurPending = true;
    }

    private void stageCardBlur(float f, float f2, float f3) {
        float f4;
        float f5;
        float[] fArray;
        float f6;
        int n;
        if (motionBlurPending || !cardCaptureStaged) {
            return;
        }
        float f7 = f + 117.0f;
        float f8 = f3 - 122.0f;
        if (this.moduleList.cardBlurCount() > 0) {
            n = this.moduleList.cardBlurCount();
            f6 = this.moduleList.cardBlurMaxPhase();
            fArray = this.moduleList.cardBlurRects();
            f5 = f2 + 5.0f + 30.0f;
            f4 = 250.0f;
        } else {
            n = this.themesRenderer.cardBlurCount();
            f6 = this.themesRenderer.cardBlurMaxPhase();
            fArray = this.themesRenderer.cardBlurRects();
            f5 = f2 + 5.0f;
            f4 = 280.0f;
        }
        if (n <= 0 || f6 <= 0.003f) {
            return;
        }
        float f9 = Render2DCoordinateSpace.designGuiScale();
        float f10 = f7 * f9;
        float f11 = f5 * f9;
        float f12 = f8 * f9;
        float f13 = f4 * f9;
        float f14 = 48.0f;
        UI.cardBlurMask[0] = f10 - f14;
        UI.cardBlurMask[1] = f11 - f14;
        UI.cardBlurMask[2] = f12 + f14 * 2.0f;
        UI.cardBlurMask[3] = f13 + f14 * 2.0f;
        UI.cardBlurMask[4] = -1.0f;
        UI.cardBlurMask[5] = 0.0f;
        int n2 = 1;
        for (int i = 0; i < n; ++i) {
            int n3 = i * 6;
            int n4 = n2 * 6;
            UI.cardBlurMask[n4] = fArray[n3] * f9;
            UI.cardBlurMask[n4 + 1] = fArray[n3 + 1] * f9;
            UI.cardBlurMask[n4 + 2] = fArray[n3 + 2] * f9;
            UI.cardBlurMask[n4 + 3] = fArray[n3 + 3] * f9;
            UI.cardBlurMask[n4 + 4] = fArray[n3 + 4];
            UI.cardBlurMask[n4 + 5] = fArray[n3 + 5] / f6;
            ++n2;
        }
        cardBlurMaskCount = n2;
        motionBlurOpacity = 1.0f;
        motionBlurRadius = Math.max(0.5f, 22.0f * f6);
        float f15 = f14 + motionBlurRadius * 2.0f + 8.0f;
        motionBlurX = Math.round(f10 - f15);
        motionBlurY = Math.round(f11 - f15);
        motionBlurW = Math.round(f12 + f15 * 2.0f);
        motionBlurH = Math.round(f13 + f15 * 2.0f);
        motionBlurScale = 1.0f;
        motionBlurOriginX = (f7 + f8 * 0.5f) * f9;
        motionBlurOriginY = (f5 + f4 * 0.5f) * f9;
        cardBlurPending = true;
    }

    public static boolean motionBlurCapturePending() {
        return motionBlurPending;
    }

    private static void beginShatter() {
        if (GuiShatterAnimation.resume()) {
            return;
        }
        float f = Render2DCoordinateSpace.designGuiScale();
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        float f2 = interfaceModule != null && interfaceModule.rectGlow.getValue() ? interfaceModule.rectGlowRadius.getFloat() : 0.0f;
        float f3 = (f2 + 14.0f) * f;
        float f4 = 430.0f * f;
        float f5 = 290.0f * f;
        float f6 = Position.screenWidth() * f;
        float f7 = Position.screenHeight() * f;
        GuiShatterAnimation.begin((f6 - f4) * 0.5f, (f7 - f5) * 0.5f, f4, f5, f3, f6, f7);
    }

    public static boolean consumeCardStratumMark() {
        boolean bl = cardStratumMarked;
        cardStratumMarked = false;
        if (bl) {
            cardBlurCaptured = true;
        }
        return bl;
    }

    public static boolean popupLayerCapturePending() {
        return popupLayerBlurWanted;
    }

    public static float motionBlurCaptureRadius() {
        return motionBlurRadius;
    }

    public static void markPopupLayerCapture() {
        popupLayerBlurWanted = true;
    }

    public static boolean consumePanelSplitMark() {
        boolean bl = panelSplitMarked;
        panelSplitMarked = false;
        return bl;
    }

    public static void markPopupStratum(boolean bl, boolean bl2) {
        popupStratumMarked = true;
        popupBlurWanted = bl;
        popupBlurStaged = bl2;
        popupBlurCaptured = false;
    }

    private static String profileRole() {
        Role role = Profile.getRole();
        return role == null ? "" : role.name();
    }

    public static void markCardStratum() {
        cardStratumMarked = true;
        cardCaptureStaged = true;
    }

    public static void dropPendingBlurs() {
        cardStratumMarked = false;
        panelSplitMarked = false;
        vanillaBlurRequested = false;
        popupStratumMarked = false;
        popupLayerBlurWanted = false;
        popupBlurWanted = false;
        popupBlurStaged = false;
        popupBlurCaptured = false;
        popupBlurPending = false;
        cardBlurPending = false;
        cardCaptureStaged = false;
        cardBlurCaptured = false;
    }

    public static float guiShatterProgress() {
        float f = WorldGuiCloseAnimation.isActive() ? WorldGuiCloseAnimation.progress() : UI.INSTANCE.screenAnim.closeProgress();
        return GuiShatterAnimation.progress(f);
    }

    @Override
    public float shatterProgress() {
        return UI.guiShatterProgress();
    }

    private void swapContentCategory(Category category) {
        this.moduleList.finishTransition();
        this.themesRenderer.finishTransition();
        this.eventsRenderer.finishTransition();
        this.contentCategory = category;
        if (category == Category.THEMES) {
            this.themesRenderer.open(false);
        }
        if (category == Category.EVENTS) {
            this.eventsRenderer.open();
            if (this.eventsSub == 1) {
                this.eventsRenderer.showMines();
            } else {
                this.eventsRenderer.showEvents();
            }
        }
        if (category == null) {
            this.placeholderAnim.setDirection(Direction.FORWARDS);
            this.placeholderAnim.counter.resetCounter();
        }
    }

    private void renderModuleHeader(DrawContext drawContext, float f, float f2, float f3, float f4, Category category, float f5, float f6) {
        float f7 = f + 117.0f;
        float f8 = f2 + 5.0f + f6;
        float f9 = f3 - 122.0f;
        float f10 = 6.0f;
        float f11 = 14.0f;
        float f12 = 26.0f;
        float f13 = f8 + (f12 - f11) * 0.5f;
        float f14 = 18.0f;
        float f15 = f7 + f9 - f10 - f14;
        float f16 = f8 + (f12 - f14) * 0.5f;
        this.renderDiscordAvatar(drawContext, f15, f16, f14, f4);
        String string = UI.profileName();
        String string2 = UI.profileRole();
        float f17 = 6.5f;
        float f18 = 5.0f;
        float f19 = Fonts.MONTSERRAT_MEDIUM.width(string, f17);
        float f20 = Fonts.MONTSERRAT_MEDIUM.width(string2, f18);
        float f21 = f15 - 5.0f;
        float f22 = f16 + (f14 - (f17 + 1.0f + f18)) * 0.5f;
        Fonts.MONTSERRAT_MEDIUM.draw(string, f21 - f19, f22, f17, UI.color(255, 255, 255, 230, f4));
        Fonts.MONTSERRAT_MEDIUM.draw(string2, f21 - f20, f22 + f17 + 1.0f, f18, ClientAccent.accentSoft(195.0f * f4));
        float f23 = f21 - Math.max(f19, f20);
        float f24 = f7 + f10;
        float f25 = Math.max(60.0f, f23 - f10 - f24);
        this.search.render(drawContext, f24, f13, f25, f11, f4, Position.mouseX(), Position.mouseY(), f5);
    }

    @Override
    public float captureScale() {
        return guiCaptureScale;
    }

    private void renderCategoryPanel(float f, float f2, float f3, float f4) {
        float f5;
        float f6;
        float f7;
        float f8;
        float f9 = f;
        float f10 = 110.0f;
        float f11 = f9 + 9.0f;
        int n = MAIN_CATEGORIES.length + OTHER_CATEGORIES.length;
        float f12 = f2 + 3.0f;
        float f13 = 26.0f;
        RenderHelper.drawPanelBg(f9, f12, f10, f13, 12.0f, 0.0f, 0.0f, 0.0f, f4);
        float f14 = f12 + f13 * 0.5f;
        String string = "x";
        float f15 = 11.7f;
        float f16 = 11.0f;
        float f17 = 7.7999997f;
        float f18 = Fonts.BYAZEN.msdfWidth(string, f15);
        float f19 = Fonts.MONTSERRAT_EXTRABOLD.msdfWidth("ByAzen", f16);
        float f20 = f9 + (f10 - (f18 + f17 + f19)) * 0.5f;
        AccentGradient.msdfIcon("byazen", string, f20, f14 - f15 * 0.5f + 0.5f, f15, 225.0f * f4, 0.1f);
        Fonts.MONTSERRAT_EXTRABOLD.msdf("ByAzen", f20 + f18 + f17, f14 - f16 * 0.6f, f16, UI.color(255, 255, 255, 255, f4));
        float f21 = f2 + 34.0f;
        float f22 = f21 + 10.0f;
        float f23 = this.modulesHeaderAnim.getOutput().floatValue();
        if (f23 > 0.004f) {
            Render2D.rect(f9 + 4.0f, f21 + 4.5f, f10 - 8.0f, 13.333333f, 5.0f, UI.color(255, 255, 255, Math.round(16.0f * f23), f4));
        }
        String string2 = "h";
        float f24 = Fonts.BYAZEN.msdfWidth(string2, 8.0f);
        AccentGradient.msdfIcon("byazen", string2, f11, f22 - 4.0f + 1.5f, 8.0f, (200.0f + 55.0f * f23) * f4, 0.15f);
        Fonts.MONTSERRAT_MEDIUM.draw("Modules", f11 + f24 + 6.0f, f22 - 4.0f + 0.5f, 8.0f, UI.color(255, 255, 255, Math.round(215.0f + 40.0f * f23), f4));
        float f25 = f21 + 20.0f + 4.0f;
        float f26 = f11 + 3.0f;
        float f27 = f26 + 9.0f;
        float f28 = f25 + 9.0f;
        float f29 = f25 + (float)(MAIN_CATEGORIES.length - 1) * 18.0f + 9.0f;
        Render2D.rect(f26, f28, 1.0f, f29 - f28, 0.0f, UI.color(255, 255, 255, 36, f4));
        float f30 = 2.0f;
        for (int i = 0; i < MAIN_CATEGORIES.length; ++i) {
            Category category = MAIN_CATEGORIES[i];
            f8 = f25 + (float)i * 18.0f;
            float f31 = f8 + 9.0f;
            f7 = this.getCategoryAnim(category).getOutput().floatValue();
            int n2 = Math.min(255, 140 + Math.round(f7 * 115.0f));
            int n3 = UI.color(255, 255, 255, n2, f4);
            String string3 = String.valueOf(UI.iconChar(category));
            float f32 = Fonts.BYAZEN.msdfWidth(string3, 7.0f);
            if (f7 > 0.01f) {
                f6 = (f32 + 5.0f + Fonts.MONTSERRAT_MEDIUM.width(category.getDisplayName(), 7.0f)) * f7;
                AccentGradient.fillHorizontal(f27, f31 + 8.5f - f30, f6, 0.75f, 0.625f, 88.0f * f7 * f4);
            }
            f6 = n > 1 ? (float)i / (float)(n - 1) : 0.5f;
            AccentGradient.msdfIcon("byazen", string3, f27, f31 - 3.5f + 1.5f - f30, 7.0f, (float)n2 * f4, f6);
            Fonts.MONTSERRAT_MEDIUM.draw(category.getDisplayName(), f27 + f32 + 5.0f, f31 - 3.5f + 0.5f - f30, 7.0f, n3);
            String string4 = String.valueOf(this.moduleList.getModules(category).size());
            f5 = Fonts.MONTSERRAT_MEDIUM.width(string4, 5.5f);
            Fonts.MONTSERRAT_MEDIUM.draw(string4, f9 + f10 - 10.0f - f5, f31 - 2.75f + 0.5f - f30, 5.5f, UI.color(255, 255, 255, Math.round(80.0f + 80.0f * f7), f4));
        }
        float f33 = f25 + (float)MAIN_CATEGORIES.length * 18.0f + 3.0f;
        float f34 = f33 + 10.0f;
        f8 = this.eventsHeaderAnim.getOutput().floatValue();
        if (f8 > 0.004f) {
            Render2D.rect(f9 + 4.0f, f33 + 4.5f, f10 - 8.0f, 13.333333f, 5.0f, UI.color(255, 255, 255, Math.round(16.0f * f8), f4));
        }
        String string5 = "e";
        f7 = Fonts.BYAZEN.msdfWidth(string5, 8.0f);
        AccentGradient.msdfIcon("byazen", string5, f11, f34 - 4.0f + 1.5f, 8.0f, (200.0f + 55.0f * f8) * f4, 0.6f);
        Fonts.MONTSERRAT_MEDIUM.draw("Server", f11 + f7 + 6.0f, f34 - 4.0f + 0.5f, 8.0f, UI.color(255, 255, 255, Math.round(215.0f + 40.0f * f8), f4));
        float f35 = f33 + 20.0f + 4.0f;
        float f36 = f35 + 9.0f;
        float f37 = f35 + (float)(EVENT_SUBS.length - 1) * 18.0f + 9.0f;
        Render2D.rect(f26, f36, 1.0f, f37 - f36, 0.0f, UI.color(255, 255, 255, 36, f4));
        for (int i = 0; i < EVENT_SUBS.length; ++i) {
            f6 = f35 + (float)i * 18.0f;
            float f38 = f6 + 9.0f;
            f5 = this.eventsSubT[i];
            int n4 = Math.min(255, 140 + Math.round(f5 * 115.0f));
            int n5 = UI.color(255, 255, 255, n4, f4);
            String string6 = EVENT_SUB_ICONS[i];
            float f39 = Fonts.BYAZEN.msdfWidth(string6, 7.0f);
            if (f5 > 0.01f) {
                float f40 = (f39 + 5.0f + Fonts.MONTSERRAT_MEDIUM.width(EVENT_SUBS[i], 7.0f)) * f5;
                AccentGradient.fillHorizontal(f27, f38 + 8.5f - f30, f40, 0.75f, 0.625f, 88.0f * f5 * f4);
            }
            AccentGradient.msdfIcon("byazen", string6, f27, f38 - 3.5f + 1.5f - f30, 7.0f, (float)n4 * f4, 0.6f);
            Fonts.MONTSERRAT_MEDIUM.draw(EVENT_SUBS[i], f27 + f39 + 5.0f, f38 - 3.5f + 0.5f - f30, 7.0f, n5);
        }
        float f41 = f35 + (float)EVENT_SUBS.length * 18.0f + 12.0f;
        for (int i = 0; i < OTHER_CATEGORIES.length; ++i) {
            Category category = OTHER_CATEGORIES[i];
            float f42 = f5 = category == Category.THEMES ? this.themesRowT : 1.0f;
            if (f5 <= 0.01f) continue;
            float f43 = f41 + (float)i * 19.0f + 9.5f;
            float f44 = this.getCategoryAnim(category).getOutput().floatValue();
            int n6 = Math.round((float)Math.min(255, 140 + Math.round(f44 * 115.0f)) * f5);
            int n7 = UI.color(255, 255, 255, n6, f4);
            String string7 = String.valueOf(UI.iconChar(category));
            float f45 = Fonts.BYAZEN.msdfWidth(string7, 7.5f);
            AccentGradient.msdfIcon("byazen", string7, f11, f43 - 3.75f + 1.5f - f30, 7.5f, (float)n6 * f4, 0.85f);
            Fonts.MONTSERRAT_MEDIUM.draw(category.getDisplayName(), f11 + f45 + 6.0f, f43 - 3.5f + 0.5f - f30, 7.0f, n7);
        }
    }

    private void updateCameraParallax() {
        ClientPlayerEntity clientPlayerEntity = IMinecraft.mc.player;
        if (clientPlayerEntity == null) {
            this.lastCameraYaw = Float.NaN;
            this.parallaxX = 0.0f;
            this.parallaxY = 0.0f;
            return;
        }
        float f = clientPlayerEntity.getYaw();
        float f2 = clientPlayerEntity.getPitch();
        if (Float.isNaN(this.lastCameraYaw)) {
            this.lastCameraYaw = f;
            this.lastCameraPitch = f2;
        }
        float f3 = MathHelper.wrapDegrees((float)(f - this.lastCameraYaw));
        float f4 = f2 - this.lastCameraPitch;
        this.lastCameraYaw = f;
        this.lastCameraPitch = f2;
        if (!this.screenAnim.isClosing()) {
            this.parallaxX = 0.0f;
            this.parallaxY = 0.0f;
            return;
        }
        double d = Math.max(30.0, (double)((Integer)IMinecraft.mc.options.getFov().getValue()).intValue());
        float f5 = (float)((double)Position.screenHeight() / d);
        this.parallaxX -= f3 * f5;
        this.parallaxY -= f4 * f5;
    }

    private void selectEventsSub(int n) {
        if (this.targetCategory != Category.EVENTS) {
            this.selectCategory(Category.EVENTS);
        }
        this.eventsSub = n;
        if (n == 1) {
            this.eventsRenderer.showMines();
        } else {
            this.eventsRenderer.showEvents();
        }
    }

    @Override
    public boolean captureActive() {
        return UI.guiCaptureActive();
    }

    public static float guiCaptureBlurRadius() {
        return guiCaptureBlurMainPx;
    }

    private void renderNoResults(float f, float f2, float f3, float f4) {
        float f5 = f + 117.0f;
        float f6 = f3 - 122.0f;
        float f7 = f2 + 5.0f + 30.0f;
        float f8 = 250.0f;
        String string = "\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e";
        float f9 = 6.5f;
        float f10 = Fonts.MONTSERRAT_MEDIUM.width(string, f9);
        Fonts.MONTSERRAT_MEDIUM.draw(string, f5 + (f6 - f10) * 0.5f, f7 + f8 * 0.5f - 3.0f, f9, UI.color(255, 255, 255, 110, f4));
    }

    private void updateEventsSubAnim(float f) {
        float f2 = 1.0f - (float)Math.exp(-f * 12.0f);
        for (int i = 0; i < this.eventsSubT.length; ++i) {
            float f3 = this.contentCategory == Category.EVENTS && this.eventsSub == i ? 1.0f : 0.0f;
            int n = i;
            this.eventsSubT[n] = this.eventsSubT[n] + (f3 - this.eventsSubT[i]) * f2;
        }
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int n, int n2, float f) {
        this.renderPanel(drawContext);
        this.settingsPalette.render(drawContext, this.screenAnim.alpha());
    }

    @Override
    public void tick() {
        super.tick();
        this.settingsPalette.tick();
    }

    /** Открывает окно настроек модуля (при необходимости с подсветкой конкретной настройки). */
    public static void openModuleSettings(Module module, String settingName) {
        if (module == null) {
            return;
        }
        UI uI = UI.INSTANCE;
        if (!uI.settingsPopup.isOpenFor(module.getName())) {
            float f = Position.mouseX();
            float f2 = Position.mouseY();
            uI.settingsPopup.toggle(module, f, f2, f, f2, 300.0f, 250.0f);
        }
        if (settingName != null) {
            uI.settingsPopup.focusSetting(settingName);
        }
    }

    /** Открывает ClickGui и подсвечивает нужный модуль (идея №189: «где это найти»). */
    public static void focusModuleInGui(Module module) {
        if (module == null) {
            return;
        }
        UI uI = UI.INSTANCE;
        Category category = module.getCategory();
        if (uI.contentCategory != category) {
            if (category == Category.THEMES) {
                uI.selectCategory(category);
            }
            else {
                uI.selectCategory(category);
            }
        }
        uI.moduleList.focusModule(module.getName());
    }

    private void renderModuleHeaderPanel(float f, float f2, float f3, float f4, float f5) {
        RenderHelper.drawPanelBg(f, f2, f3, f4, 0.0f, 12.0f, 0.0f, 0.0f, f5);
    }

    private boolean isModuleView() {
        return this.contentCategory != null && this.contentCategory != Category.THEMES && this.contentCategory != Category.EVENTS;
    }

    private void selectCategory(Category category) {
        Category category2;
        Category category3 = category2 = category == this.targetCategory ? null : category;
        if (category2 == this.targetCategory) {
            return;
        }
        this.search.setText("");
        this.search.blur();
        this.settingsPopup.close();
        this.targetCategory = category2;
        this.oldSubText = this.newSubText;
        this.newSubText = category2 == null ? "\u041d\u0435 \u0432\u044b\u0431\u0440\u0430\u043d\u043e" : category2.getDisplayName();
        this.subTextAnim.setDirection(Direction.FORWARDS);
        this.subTextAnim.counter.resetCounter();
        this.subTextAnimDone = false;
        for (Category category4 : Category.values()) {
            this.getCategoryAnim(category4).setDirection(category4 == category2 ? Direction.FORWARDS : Direction.BACKWARDS);
        }
        this.modulesHeaderAnim.setDirection(UI.isMainCategory(category2) ? Direction.FORWARDS : Direction.BACKWARDS);
        this.eventsHeaderAnim.setDirection(category2 == Category.EVENTS ? Direction.FORWARDS : Direction.BACKWARDS);
        if (category2 == null) {
            this.placeholderAnim.setDirection(Direction.FORWARDS);
            this.placeholderAnim.counter.resetCounter();
        } else {
            this.placeholderAnim.setDirection(Direction.BACKWARDS);
        }
    }

    private void updateCategoryCrossfade(float f) {
        if (this.targetCategory == this.contentCategory) {
            if (this.contentCategory != null && this.categoryT < 1.0f) {
                this.categoryT = Math.min(1.0f, this.categoryT + f / 0.15f);
            }
            return;
        }
        if (this.contentCategory == null) {
            this.swapContentCategory(this.targetCategory);
            this.categoryT = 0.0f;
        } else {
            this.categoryT = Math.max(0.0f, this.categoryT - f / 0.15f);
            if (this.categoryT <= 0.0f) {
                this.categoryT = 0.0f;
                this.swapContentCategory(this.targetCategory);
            }
        }
    }

    public static void flushMotionBlur() {
        cardStratumMarked = false;
        panelSplitMarked = false;
        vanillaBlurRequested = false;
        popupStratumMarked = false;
        popupLayerBlurWanted = false;
        popupBlurWanted = false;
        popupBlurStaged = false;
        cardCaptureStaged = false;
        boolean bl = popupBlurCaptured;
        popupBlurCaptured = false;
        boolean bl2 = cardBlurCaptured;
        cardBlurCaptured = false;
        if (motionBlurPending) {
            motionBlurPending = false;
            cardBlurPending = false;
            popupBlurPending = false;
            GuiMotionBlurRenderer.applyWithCopy(motionBlurOpacity, motionBlurRadius, motionBlurX, motionBlurY, motionBlurW, motionBlurH, motionBlurMask, 2, motionBlurSrcX, motionBlurSrcY, motionBlurSrcW, motionBlurSrcH, motionBlurScale, motionBlurOriginX, motionBlurOriginY);
            return;
        }
        if (popupBlurPending && bl) {
            popupBlurPending = false;
            cardBlurPending = false;
            GuiMotionBlurRenderer.applyWithCopy(1.0f, popupBlurRadius, popupBlurX, popupBlurY, popupBlurW, popupBlurH, popupBlurMask, popupBlurMaskCount, popupBlurX, popupBlurY, popupBlurW, popupBlurH, 1.0f, popupBlurOriginX, popupBlurOriginY, true);
            return;
        }
        popupBlurPending = false;
        if (cardBlurPending && bl2) {
            cardBlurPending = false;
            GuiMotionBlurRenderer.applyWithCopy(motionBlurOpacity, motionBlurRadius, motionBlurX, motionBlurY, motionBlurW, motionBlurH, cardBlurMask, cardBlurMaskCount, motionBlurX, motionBlurY, motionBlurW, motionBlurH, 1.0f, motionBlurOriginX, motionBlurOriginY);
        }
    }

    @Override
    public float captureBlurRadius() {
        return guiCaptureBlurMainPx;
    }

    private Decelerate getCategoryAnim(Category category2) {
        return this.categoryAnims.computeIfAbsent(category2, category -> UI.createAnim(200));
    }

    private void renderPanel(DrawContext drawContext) {
        float f;
        boolean bl;
        float f2;
        this.screenAnim.updateFrame();
        this.updateCameraParallax();
        boolean bl2 = WorldGuiCloseAnimation.isDetachedRender();
        if (bl2) {
            this.parallaxX = 0.0f;
            this.parallaxY = 0.0f;
        }
        if (this.screenAnim.isCloseFinished() && !bl2) {
            if (MinecraftClient.getInstance().currentScreen == this) {
                this.screenAnim.snapClosed();
                MinecraftClient.getInstance().setScreen(null);
            }
            return;
        }
        float f3 = this.screenAnim.scale();
        float f4 = Math.max(Math.abs(this.parallaxX), Math.abs(this.parallaxY));
        if (f4 > 0.5f) {
            f2 = Math.min(1.0f, f4 / 20.0f);
            f3 += (1.0f - f3) * f2;
        }
        guiCaptureScale = f3;
        f2 = bl2 ? WorldGuiCloseAnimation.blurRadius() : this.screenAnim.blurRadius();
        guiCaptureBlurMainPx = Math.max(f2, GuiShatterAnimation.blurRadius()) * Render2DCoordinateSpace.designGuiScale();
        float f5 = this.screenAnim.alpha();
        float f6 = bl2 ? 1.0f : f5;
        float f7 = 430.0f;
        float f8 = 290.0f;
        float f9 = Position.screenWidth() / 2.0f - f7 / 2.0f;
        float f10 = Position.screenHeight() / 2.0f - f8 / 2.0f;
        Render2D.rect(-5.0f, -5.0f, IMinecraft.mc.getWindow().getFramebufferWidth(), IMinecraft.mc.getWindow().getFramebufferHeight(), 0.0f, UI.color(0, 0, 0, 60, f5));
        if (UI.guiCaptureActive()) {
            Render2D.flush();
            Render2D.beginFrame(drawContext);
            GuiRenderState guiRenderState = ((GuiGraphicsExtractorAccessor)drawContext).byazen_getGuiRenderState();
            guiRenderState.createNewRootLayer();
            guiRenderState.applyBlur();
        }
        this.moduleList.resetCardBlur();
        this.themesRenderer.resetCardBlur();
        drawContext.getMatrices().pushMatrix();
        if (this.parallaxX != 0.0f || this.parallaxY != 0.0f) {
            drawContext.getMatrices().translate(this.parallaxX, this.parallaxY);
        }
        if (rtx.byazen.utils.render.backdrops.ClickGuiBackdrops.active()) {
            rtx.byazen.utils.render.backdrops.ClickGuiBackdrops.render(drawContext, f6, f9, f10, f7, f8);
        }
        RectUtil.drawClientRect(f9, f10, f7, f8, 12.0f, f6, 6.0f);
        float f11 = 12.0f;
        RenderHelper.drawPanelBg(f9 + 5.0f, f10 + 5.0f, 110.0f, f8 - 10.0f, f11, 0.0f, 0.0f, f11, f6);
        long l = System.nanoTime();
        float f12 = Math.min(0.1f, (float)(l - this.lastNs) / 1.0E9f);
        this.lastNs = l;
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        float f13 = interfaceModule == null || interfaceModule.isThemeClientColor() ? 1.0f : 0.0f;
        this.themesRowT += (f13 - this.themesRowT) * (1.0f - (float)Math.exp(-f12 * 10.0f));
        this.renderCategoryPanel(f9 + 5.0f, f10 + 2.0f, f8, f6);
        RenderHelper.drawPanelBg(f9 + 117.0f, f10 + 5.0f, f7 - 122.0f, 280.0f, 0.0f, f11, f11, 0.0f, f6);
        this.updateCategoryCrossfade(f12);
        this.updateEventsSubAnim(f12);
        float f14 = f6 * this.categoryT;
        float f15 = this.screenAnim.isClosing() ? 1.0f : this.categoryT;
        boolean bl3 = !UI.guiCaptureActive() && !this.bindPopup.isVisible() && !this.settingsPopup.isVisible();
        this.moduleList.setAppearComposite(bl3);
        this.themesRenderer.setAppearComposite(bl3);
        if (f14 > 0.01f && this.contentCategory != null) {
            if (this.contentCategory == Category.EVENTS) {
                this.eventsRenderer.render(drawContext, f9, f10, f7, f14, f12);
            } else if (this.contentCategory == Category.THEMES) {
                this.themesRenderer.render(drawContext, f9, f10, f7, f14, f15, f12);
            } else {
                List<Module> list = this.filteredModules(this.contentCategory);
                this.moduleList.render(drawContext, f9, f10, f7, f14, f15, f12, this.contentCategory, list);
                if (list.isEmpty() && this.search.hasText()) {
                    this.renderNoResults(f9, f10, f7, f14);
                }
            }
        }
        float f16 = this.modulesHeaderAnim.getOutput().floatValue();
        boolean bl4 = bl = this.contentCategory != null && this.contentCategory != Category.THEMES && this.contentCategory != Category.EVENTS;
        if (bl && f6 > 0.01f && f16 > 0.004f) {
            f = f9 + 117.0f;
            float f17 = f10 + 5.0f;
            float f18 = f7 - 122.0f;
            float f19 = 26.0f;
            float f20 = f6 * f16;
            this.renderModuleHeaderPanel(f, f17, f18, f19, f20);
            Render2D.pushScissor(drawContext, f, f17, f18, f19);
            this.renderModuleHeader(drawContext, f9, f10, f7, f20, this.contentCategory, f12, 0.0f);
            Render2D.popScissor(drawContext);
        }
        f = this.placeholderAnim.getOutput().floatValue();
        if (this.contentCategory == null && this.targetCategory == null && f > 0.01f) {
            this.renderNoCategoryPlaceholder(drawContext, f9, f10, f7, f6 * f);
        }
        boolean bl5 = this.settingsPopup.isVisible();
        boolean bl6 = this.bindPopup.isVisible();
        if (bl5 || bl6) {
            boolean bl7;
            boolean bl8 = this.settingsPopup.blurPhase() > 0.004f;
            boolean bl9 = this.bindPopup.blurPhase() > 0.004f;
            boolean bl10 = bl7 = !UI.guiCaptureActive() && bl5 && bl6 && bl9 && !bl8;
            if (!UI.guiCaptureActive()) {
                GuiRenderState guiRenderState = ((GuiGraphicsExtractorAccessor)drawContext).byazen_getGuiRenderState();
                guiRenderState.createNewRootLayer();
                guiRenderState.applyBlur();
                UI.markPopupStratum((bl8 || bl9) && !bl7, bl8 || bl9);
            }
            if (bl5) {
                this.settingsPopup.render(drawContext, f6);
            }
            if (bl7) {
                UI.markPopupLayerCapture();
                GuiLayerBlurRenderer.markPopupBoundary(drawContext);
            }
            if (bl6) {
                this.bindPopup.render(drawContext, f6);
            }
        }
        drawContext.getMatrices().popMatrix();
        if (!UI.guiCaptureActive()) {
            this.stagePopupBlur();
            this.stageCardBlur(f9, f10, f7);
        }
    }

    private static String layoutNormalize(String string) {
        StringBuilder stringBuilder = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            int n = RU_LAYOUT.indexOf(c);
            stringBuilder.append(n >= 0 ? EN_LAYOUT.charAt(n) : c);
        }
        return stringBuilder.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void renderClosingPanelOverHud(DrawContext drawContext) {
        boolean bl;
        UI uI = INSTANCE;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        WorldGuiCloseAnimation.updateFrame();
        if (GuiCapture.isBound(uI) && WorldGuiCloseAnimation.isReversing() && WorldGuiCloseAnimation.isFinished()) {
            WorldGuiCloseAnimation.cancel();
            GuiShatterAnimation.cancel();
            if (!uI.screenAnim.isClosing()) {
                uI.screenAnim.snapOpen();
            }
        }
        boolean bl2 = GuiCapture.isBound(uI);
        if (minecraftClient.currentScreen != null) {
            if (minecraftClient.currentScreen != uI) {
                if (bl2 && WorldGuiCloseAnimation.isActive()) {
                    WorldGuiCloseAnimation.cancel();
                }
                if (uI.screenAnim.isClosing()) {
                    uI.screenAnim.snapClosed();
                }
                pendingAfterClose = null;
            }
            return;
        }
        if (bl2 && WorldGuiCloseAnimation.isActive() && (minecraftClient.world == null || minecraftClient.options.hudHidden || WorldGuiCloseAnimation.isFinished() || WorldGuiCloseAnimation.surfaceChanged() || !GuiLayerBlurRenderer.available())) {
            WorldGuiCloseAnimation.cancel();
            if (uI.screenAnim.isClosing()) {
                uI.screenAnim.snapClosed();
                if (pendingAfterClose != null) {
                    Screen screen = pendingAfterClose;
                    pendingAfterClose = null;
                    minecraftClient.setScreen(screen);
                }
            }
            return;
        }
        boolean bl3 = bl2 && WorldGuiCloseAnimation.isActive();
        boolean bl4 = bl = uI.screenAnim.isClosing() && (bl3 || !uI.screenAnim.isCloseFinished());
        if (!bl) {
            if (uI.screenAnim.isClosing() && pendingAfterClose != null) {
                Screen screen = pendingAfterClose;
                pendingAfterClose = null;
                uI.screenAnim.snapClosed();
                minecraftClient.setScreen(screen);
            }
            return;
        }
        Render2D.beginFrame(drawContext);
        if (bl3) {
            BlurFramebuffer.beginWorldScope();
        }
        try {
            uI.renderPanel(drawContext);
        }
        finally {
            BlurFramebuffer.endWorldScope();
        }
        Render2D.flush();
        GuiLayerBlurRenderer.markPanelEnd(drawContext);
    }

    private int eventsSubAt(float f, float f2, float f3, float f4) {
        float f5 = f + 5.0f;
        float f6 = 110.0f;
        float f7 = f5 + 4.0f;
        float f8 = f5 + f6 - 4.0f;
        float f9 = f2 + 2.0f;
        float f10 = f9 + 34.0f + 20.0f + 4.0f;
        float f11 = f10 + (float)MAIN_CATEGORIES.length * 18.0f + 3.0f;
        float f12 = f11 + 20.0f + 4.0f;
        for (int i = 0; i < EVENT_SUBS.length; ++i) {
            float f13 = f12 + (float)i * 18.0f;
            if (!(f3 >= f7) || !(f3 <= f8) || !(f4 >= f13) || !(f4 <= f13 + 18.0f)) continue;
            return i;
        }
        return -1;
    }

    private void renderDiscordAvatar(DrawContext drawContext, float f, float f2, float f3, float f4) {
        float f5 = f3 * 0.5f;
        int n = Math.max(0, Math.min(255, Math.round(f4 * 255.0f))) << 24 | 0xFFFFFF;
        String string = DiscordAvatar.texture();
        if (string != null && Render2D.imageReady(string)) {
            Render2D.image(string, f, f2, f3, f3, f5, n);
            return;
        }
        Render2D.rect(f, f2, f3, f3, f5, UI.color(0, 0, 0, 90, f4));
        Render2D.outline(f, f2, f3, f3, f5, 0.8f, UI.color(255, 255, 255, 30, f4));
    }

    private Module moduleAtCursor() {
        if (!this.isModuleView()) {
            return null;
        }
        float f = 430.0f;
        float f2 = Position.screenWidth() / 2.0f - f / 2.0f;
        float f3 = Position.screenHeight() / 2.0f - 145.0f;
        float f4 = Position.mouseX();
        float f5 = Position.mouseY();
        float f6 = f2 + 117.0f;
        float f7 = f3 + 5.0f + 30.0f;
        float f8 = f - 122.0f;
        float f9 = 250.0f;
        if (f4 < f6 || f4 > f6 + f8 || f5 < f7 || f5 > f7 + f9) {
            return null;
        }
        List<Module> list = this.filteredModules(this.contentCategory);
        for (int i = 0; i < list.size(); ++i) {
            float[] fArray = this.moduleList.cardRect(list, i, f6, f7, f8);
            if (fArray == null || !(f4 >= fArray[0]) || !(f4 <= fArray[0] + fArray[2]) || !(f5 >= fArray[1]) || !(f5 <= fArray[1] + fArray[3])) continue;
            return list.get(i);
        }
        return null;
    }

    private Category categoryButtonAt(float f, float f2, float f3, float f4) {
        float f5;
        float f6 = f + 5.0f;
        float f7 = 110.0f;
        float f8 = f6 + 4.0f;
        float f9 = f6 + f7 - 4.0f;
        float f10 = f2 + 2.0f;
        float f11 = f10 + 34.0f + 20.0f + 4.0f;
        for (int i = 0; i < MAIN_CATEGORIES.length; ++i) {
            f5 = f11 + (float)i * 18.0f;
            if (!(f3 >= f8) || !(f3 <= f9) || !(f4 >= f5) || !(f4 <= f5 + 18.0f)) continue;
            return MAIN_CATEGORIES[i];
        }
        float f12 = f11 + (float)MAIN_CATEGORIES.length * 18.0f + 3.0f;
        f5 = f12 + 20.0f + 4.0f;
        float f13 = f5 + (float)EVENT_SUBS.length * 18.0f + 12.0f;
        for (int i = 0; i < OTHER_CATEGORIES.length; ++i) {
            float f14 = f13 + (float)i * 19.0f;
            if (!(f3 >= f8) || !(f3 <= f9) || !(f4 >= f14) || !(f4 <= f14 + 19.0f)) continue;
            Category category = OTHER_CATEGORIES[i];
            if (category == Category.THEMES && this.themesRowT < 0.5f) {
                return null;
            }
            return category;
        }
        return null;
    }

    private static String profileName() {
        String string = Profile.getUsername();
        if (string != null && !string.isBlank()) {
            return string;
        }
        String string2 = DiscordRPCManager.username();
        if (string2 != null && !string2.isBlank()) {
            return string2;
        }
        try {
            if (IMinecraft.mc.getSession() != null && IMinecraft.mc.getSession().getUsername() != null && !IMinecraft.mc.getSession().getUsername().isBlank()) {
                return IMinecraft.mc.getSession().getUsername();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return "Player";
    }
}

