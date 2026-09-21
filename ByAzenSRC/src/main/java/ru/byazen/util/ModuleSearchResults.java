/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.SearchQueryState;
import ru.byazen.misc.ThemeColors;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleManager;
import ru.byazen.setting.Setting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.EspFeatureRegistry;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.ScrollController;

public final class ModuleSearchResults
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    static final int slot = 3;
    static final int slot2 = 6;
    private String string4 = null;
    private final String string5;
    private final ModuleManager moduleManager;
    private final String string6;
    private final String string7;
    private final ScrollController scrollController = new ScrollController(18.0f, 30.0f);
    static final int slot3 = 9;
    private final Consumer<Module> consumer;
    private final SearchQueryState searchQueryState;
    private final Function<Module, String> function;

    public ModuleSearchResults(GuiBounds bounds2, ModuleManager moduleManager, SearchQueryState searchQueryState, Consumer<Module> consumer, Function<Module, String> function) {
        super(bounds2);
        this.string5 = "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 \u0438\u043b\u0438 \u043c\u043e\u0434\u0443\u043b\u044f \u0432 \u043f\u043e\u0438\u0441\u043a\u043e\u0432\u0443\u044e";
        this.string7 = "\u0441\u0442\u0440\u043e\u043a\u0443, \u0447\u0442\u043e-\u0431\u044b \u043d\u0430\u0439\u0442\u0438 \u043d\u0443\u0436\u043d\u044b\u0439 \u0432\u0430\u043c \u044d\u043b\u0435\u043c\u0435\u043d\u0442";
        this.string6 = "\u041c\u044b \u043e\u0431\u044b\u0441\u043a\u0430\u043b\u0438 \u0432\u0441\u0451, \u043d\u043e \u043d\u0435 \u043d\u0430\u0448\u043b\u0438 \u043d\u0438 \u043e\u0434\u043d\u043e\u0439 \u0444\u0443\u043d\u043a\u0446\u0438\u0438";
        this.moduleManager = moduleManager;
        this.searchQueryState = searchQueryState;
        this.consumer = consumer;
        this.function = function;
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
        if (this.getBounds().contains(n, n2)) {
            this.scrollController.scrollByWheel(d, this.getBounds().getHeight());
        }
    }

    @Override
    public void update() {
        for (GuiElement element2 : this.children) {
            element2.update();
        }
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        this.update3();
        return this.getBounds().contains(n, n2) && super.onMousePressed(n, n2, n3);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.update3();
        if (!this.searchQueryState.hasQuery()) {
            this.process5(matrix4f, drawApi, bounds2, "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 \u0438\u043b\u0438 \u043c\u043e\u0434\u0443\u043b\u044f \u0432 \u043f\u043e\u0438\u0441\u043a\u043e\u0432\u0443\u044e", "\u0441\u0442\u0440\u043e\u043a\u0443, \u0447\u0442\u043e-\u0431\u044b \u043d\u0430\u0439\u0442\u0438 \u043d\u0443\u0436\u043d\u044b\u0439 \u0432\u0430\u043c \u044d\u043b\u0435\u043c\u0435\u043d\u0442");
            return bounds2.getY() + bounds2.getHeight();
        }
        if (this.children.isEmpty()) {
            this.scrollController.update(bounds2.getHeight(), 0.0f);
            this.scrollController.setContentHeight(bounds2.getHeight(), 0.0f);
            String string = this.searchQueryState.getQuery();
            String string2 = "\u0438\u043b\u0438 \u043c\u043e\u0434\u0443\u043b\u044f \u043f\u043e\u0445\u043e\u0436\u0438\u0445 \u043d\u0430 \u00ab" + string + "\u00bb";
            this.process5(matrix4f, drawApi, bounds2, "\u041c\u044b \u043e\u0431\u044b\u0441\u043a\u0430\u043b\u0438 \u0432\u0441\u0451, \u043d\u043e \u043d\u0435 \u043d\u0430\u0448\u043b\u0438 \u043d\u0438 \u043e\u0434\u043d\u043e\u0439 \u0444\u0443\u043d\u043a\u0446\u0438\u0438", string2);
            return bounds2.getY() + bounds2.getHeight();
        }
        float f2 = this.getFloatType();
        this.scrollController.update(bounds2.getHeight(), f2);
        drawApi.beginStencil(1);
        drawApi.drawRoundedRectangleRadii(matrix4f, bounds2.getX() + 1.0f, bounds2.getY() + 1.0f, bounds2.getWidth() - 1.5f, bounds2.getHeight() - 1.5f, 10.5f, 0.0f, 0.0f, 0.0f, ColorUtils.rgba(0, 0, 0, 45));
        drawApi.applyStencilMask(1);
        float f3 = this.process4(f, matrix4f);
        drawApi.endStencil();
        this.scrollController.setContentHeight(bounds2.getHeight(), f3);
        return bounds2.getY() + bounds2.getHeight();
    }

    @Override
    public void update2() {
        this.scrollController.scrollToTop();
    }

    private float process4(float f, Matrix4f matrix4f) {
        float f2;
        GuiBounds bounds2 = this.getBounds();
        float f3 = this.scrollController.getOffset();
        float f4 = bounds2.getX() + 8.0f;
        float f5 = f2 = bounds2.getY() + 8.0f + f3;
        float f6 = bounds2.getY() - 1.0f;
        float f7 = bounds2.getY() + bounds2.getHeight() + 1.0f;
        for (GuiElement element2 : this.children) {
            element2.getBounds().setPosition(f4, f2);
            float f8 = element2.getBounds().getHeight();
            f5 = f2 + f8 < f6 || f2 > f7 ? f2 + f8 : element2.render(f, matrix4f);
            f2 = f5 + 4.0f;
        }
        float f9 = f5 - f3;
        return f9 <= bounds2.getY() + 8.0f ? 0.0f : f9 - bounds2.getY() + 8.0f;
    }

    private void update3() {
        String string = this.searchQueryState.getQuery();
        if (Objects.equals(string, this.string4)) {
            return;
        }
        this.string4 = string;
        this.children.clear();
        if (string == null || string.isBlank()) {
            this.scrollController.scrollToTop();
            return;
        }
        String string2 = this.process6(string);
        if (string2.isEmpty()) {
            this.scrollController.scrollToTop();
            return;
        }
        ArrayList<SearchResult> results = new ArrayList<SearchResult>();
        ArrayList<Module> arrayList2 = new ArrayList<Module>(this.moduleManager.getModules());
        EspFeatureRegistry espFeatures = ByAzenClient.getEspFeatureRegistry();
        if (espFeatures != null) {
            arrayList2.addAll(espFeatures.getDefaultModules());
        }
        for (Module module : arrayList2) {
            int n = this.process7(module, string2);
            if (n < 0) continue;
            MatchKind matchKind = n < 6 ? MatchKind.MODULE : MatchKind.SETTING;
            String string3 = this.function == null ? "" : this.function.apply(module);
            results.add(new SearchResult(n, new SearchResultRow(module, string3, matchKind, this.consumer)));
        }
        results.sort(Comparator.comparingInt(SearchResult::score));
        for (SearchResult result : results) {
            this.addChild(result.row());
        }
        this.scrollController.scrollToTop();
    }

    private void process5(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2, String string, String string2) {
        int n = ThemeColors.textPlaceholder();
        float f = bounds2.getX() + 87.5f;
        float f2 = bounds2.getY() + 119.0f;
        float f3 = FontRegistry.font2.process3(string, 6.5f);
        float f4 = FontRegistry.font2.process4(string, 6.5f);
        float f5 = f + f3 / 2.0f;
        float f6 = FontRegistry.font2.process3(string2, 6.5f);
        float f7 = f5 - f6 / 2.0f;
        float f8 = f2 + f4 + 3.0f - 1.0f;
        FontRegistry.font2.process2(matrix4f, drawApi, string, f, f2, 6.5f, n);
        FontRegistry.font2.process2(matrix4f, drawApi, string2, f7, f8, 6.5f, n);
    }

    private String process6(String string) {
        if (string == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '-' || c == '_' || Character.isWhitespace(c)) continue;
            stringBuilder.append(Character.toLowerCase(c));
        }
        return stringBuilder.toString();
    }

    private int process7(Module module, String string) {
        int n = Integer.MAX_VALUE;
        n = Math.min(n, this.process8(module.getDisplayName(), string, 0));
        for (String object : module.getAliases()) {
            n = Math.min(n, this.process8(object, string, 3));
        }
        for (Setting setting : module.getSettings()) {
            n = Math.min(n, this.process8(setting.getDisplayName(), string, 6));
            for (String string2 : setting.getAliases()) {
                n = Math.min(n, this.process8(string2, string, 9));
            }
        }
        return n == Integer.MAX_VALUE ? -1 : n;
    }

    private int process8(String string, String string2, int n) {
        if (string == null) {
            return Integer.MAX_VALUE;
        }
        String string3 = this.process6(string);
        if (string3.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        if (string3.equals(string2)) {
            return n;
        }
        if (string3.startsWith(string2)) {
            return n + 1;
        }
        if (string3.contains(string2)) {
            return n + 2;
        }
        return Integer.MAX_VALUE;
    }

    private float getFloatType() {
        float f = 8.0f;
        for (GuiElement element2 : this.children) {
            f += element2.getBounds().getHeight() + 4.0f;
        }
        return f - 4.0f + 8.0f;
    }

    public boolean isActive() {
        this.update3();
        if (!this.searchQueryState.hasQuery() || this.children.isEmpty()) {
            return false;
        }
        Object e = this.children.get(0);
        if (e instanceof SearchResultRow) {
            SearchResultRow row = (SearchResultRow)e;
            if (this.consumer != null) {
                this.consumer.accept(row.module());
                return true;
            }
        }
        return false;
    }

    private static enum MatchKind {
        MODULE,
        SETTING;

    }

    private record SearchResult(int score, SearchResultRow row) {
    }

    private static final class SearchResultRow
    extends GuiElement {
        private static final float HEIGHT = 31.0f;
        private final Module module;
        private final String context;
        private final MatchKind matchKind;
        private final Consumer<Module> onSelected;

        private SearchResultRow(Module module, String context, MatchKind matchKind, Consumer<Module> onSelected) {
            super(new GuiBounds(0.0f, 0.0f, 0.0f, 31.0f));
            this.module = module;
            this.context = context == null ? "" : context;
            this.matchKind = matchKind;
            this.onSelected = onSelected;
        }

        private Module module() {
            return this.module;
        }

        @Override
        public void onMouseScroll(int mouseX, int mouseY, double amount) {
        }

        @Override
        public void update() {
        }

        @Override
        public boolean onMousePressed(int mouseX, int mouseY, int button) {
            if (button != 0 || !this.getBounds().contains(mouseX, mouseY)) {
                return false;
            }
            if (this.onSelected != null) {
                this.onSelected.accept(this.module);
            }
            return true;
        }

        @Override
        public float render(float delta, Matrix4f matrix) {
            GuiBounds bounds = this.getBounds();
            bounds.setSize(bounds.getWidth(), 31.0f);
            GuiDrawApi renderer = ByAzenClient.getGuiRenderer();
            renderer.drawRoundedRectangle(matrix, bounds.getX(), bounds.getY(), bounds.getWidth(), 31.0f, 7.0f, ThemeColors.borderSubtle());
            FontRegistry.font5.process2(matrix, renderer, this.module.getDisplayName(), bounds.getX() + 8.0f, bounds.getY() + 6.0f, 6.5f, ThemeColors.textPrimary());
            String category = this.matchKind == MatchKind.SETTING ? "Setting" : this.module.getCategory().getName();
            String details = this.context.isBlank() ? category : category + "  \u00b7  " + this.context;
            FontRegistry.font2.process2(matrix, renderer, details, bounds.getX() + 8.0f, bounds.getY() + 17.0f, 5.75f, ThemeColors.textPlaceholder());
            return bounds.getY() + 31.0f;
        }
    }
}

