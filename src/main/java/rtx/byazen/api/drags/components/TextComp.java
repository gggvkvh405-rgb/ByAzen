package rtx.byazen.api.drags.components;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.CustomTextModule;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.settings.impl.SliderSetting;
import rtx.byazen.api.ui.settings.impl.TextSetting;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Свой текст в HUD с плейсхолдерами (%fps%, %ping%, %coords% и т.д.).
 * Идея №27 из IDEAS.md.
 */
public final class TextComp
extends Draggable {

    private static final String FONT = "montserrat-semibold";
    private static final float PAD_X = 7.0f;
    private static final float PAD_Y = 4.0f;
    private static final float LINE_GAP = 1.5f;

    private float currentWidth = 110.0f;
    private float currentHeight = 16.0f;
    private float alpha;
    private long lastFrameNs;

    public TextComp() {
        super("custom_text", 5.0f, 120.0f);
    }

    private static CustomTextModule module() {
        return ModuleManager.get().get(CustomTextModule.class);
    }

    @Override
    public String displayName() {
        return "Свой текст";
    }

    @Override
    public float width() {
        return this.currentWidth;
    }

    @Override
    public float height() {
        return this.currentHeight;
    }

    @Override
    public boolean isInteractive() {
        CustomTextModule module = TextComp.module();
        return module != null && module.isEnabled();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        CustomTextModule module = TextComp.module();
        if (module != null) {
            list.add(new TextSetting(module.text));
            list.add(new SliderSetting(module.size));
            list.add(new BoolSetting(module.background));
            list.add(new BoolSetting(module.shadow));
            list.add(new rtx.byazen.api.ui.settings.impl.SelectSetting(module.icon));
        }
        return list;
    }

    @Override
    protected void render(DrawContext drawContext) {
        CustomTextModule module = TextComp.module();
        if (module == null) {
            return;
        }
        float delta = this.deltaSeconds();
        this.alpha += ((module.isEnabled() ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        float size = module.size.getFloat();
        List<String> lines = TextComp.lines(module);
        if (lines.isEmpty()) {
            return;
        }
        float width = 0.0f;
        for (String line : lines) {
            width = Math.max(width, Render2D.msdfWidth(FONT, line, size));
        }
        String icon = module.icon.getValue();
        float iconSize = icon == null || "Нет".equalsIgnoreCase(icon) ? 0.0f : size * 1.4f;
        float iconGap = iconSize > 0.0f ? iconSize + 4.0f : 0.0f;
        width += iconGap;
        float lineHeight = size + 2.0f;
        boolean background = module.background.getValue();
        float boxWidth = width + PAD_X * 2.0f;
        float boxHeight = (float) lines.size() * lineHeight - LINE_GAP + PAD_Y * 2.0f - 2.0f;
        this.currentWidth = background ? boxWidth : width;
        this.currentHeight = boxHeight;

        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;
        Render2D.beginFrame(drawContext);
        if (background) {
            RectUtil.drawClientRect(x, y, boxWidth, boxHeight, 6.0f, a);
        }
        for (int i = 0; i < lines.size(); ++i) {
            float lineY = y + PAD_Y + (float) i * lineHeight;
            if (module.shadow.getValue()) {
                Render2D.msdfText(FONT, lines.get(i), x + PAD_X + iconGap + 1.0f, lineY + 1.0f, size, TextComp.shadow(a));
            }
            Render2D.msdfText(FONT, lines.get(i), x + PAD_X + iconGap, lineY, size, TextComp.ink(a));
        }
        if (iconSize > 0.0f) {
            rtx.byazen.utils.render.icons.IconLibrary.draw(icon, x + PAD_X, y + PAD_Y + (size - iconSize) * 0.5f + 0.5f, iconSize,
                    rtx.byazen.utils.color.ColorUtil.rgba(232, 237, 246, Math.round(215.0f * a)));
        }
        Render2D.flush();
    }

    private static List<String> lines(CustomTextModule module) {
        ArrayList<String> lines = new ArrayList<String>();
        String value = module.text.getValue();
        if (value == null || value.isEmpty()) {
            return lines;
        }
        for (String part : CustomTextModule.resolve(value).split("\\|")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                lines.add(trimmed);
            }
        }
        return lines;
    }

    private static int ink(float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(240.0f * alpha)));
        return a << 24 | 0xF0F3FA;
    }

    private static int shadow(float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(120.0f * alpha)));
        return a << 24;
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNs == 0L ? 0.016f : (float) (now - this.lastFrameNs) / 1.0E9f;
        this.lastFrameNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }
}
