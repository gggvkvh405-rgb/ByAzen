package rtx.byazen.api.drags.components;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix3x2f;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.impl.Visuals.CustomSwords;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.settings.impl.SliderSetting;
import rtx.byazen.utils.render.render2d.Render2DCoordinateSpace;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Иконка выбранного оружия в HUD (идея №65 из IDEAS.md).
 * <p>
 * Показывает модель, которая сейчас заменяет ванильные мечи: иконку предмета на мягкой подложке и
 * название под ней. Виджет таскается в редакторе интерфейса, как остальные элементы HUD.
 */
public final class SwordComp
extends Draggable {

    private static final String FONT = "montserrat-semibold";
    private static final float ICON_BASE = 16.0f;
    private static final float PAD = 4.0f;

    private float boxWidth = 30.0f;
    private float boxHeight = 26.0f;
    private float alpha;
    private long lastFrameNanos;

    public SwordComp() {
        super("swords", 5.0f, 150.0f);
    }

    private static CustomSwords module() {
        return CustomSwords.getInstance();
    }

    @Override
    public String displayName() {
        return "Оружие ByAzen";
    }

    @Override
    public float width() {
        return this.boxWidth;
    }

    @Override
    public float height() {
        return this.boxHeight;
    }

    @Override
    public boolean isInteractive() {
        CustomSwords module = SwordComp.module();
        return module != null && module.hudEnabled();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        CustomSwords module = SwordComp.module();
        if (module != null) {
            list.add(new BoolSetting(module.hudToggle()));
            list.add(new BoolSetting(module.hudNameToggle()));
            list.add(new SliderSetting(module.hudScaleSetting()));
            list.add(new BoolSetting(module.hudShadowToggle()));
        }
        return list;
    }

    @Override
    protected void render(DrawContext drawContext) {
        CustomSwords module = SwordComp.module();
        if (module == null) {
            return;
        }
        float delta = this.deltaSeconds();
        this.alpha += ((module.hudEnabled() ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.02f) {
            return;
        }
        float scale = Math.max(0.5f, module.hudScale());
        String name = module.selectedDisplay();
        boolean showName = module.hudShowName();
        float size = 6.4f;
        float textWidth = showName ? Render2D.msdfWidth(FONT, name, size) : 0.0f;
        float icon = ICON_BASE * scale;
        this.boxWidth = Math.max(icon + PAD * 2.0f, textWidth + PAD * 2.0f);
        this.boxHeight = icon + PAD * 2.0f + (showName ? size + 3.0f : 0.0f);
        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;
        Render2D.beginFrame(drawContext);
        rtx.byazen.utils.render.others.RectUtil.drawClientRect(x, y, this.boxWidth, this.boxHeight, 7.0f, a * 0.92f);
        drawContext.getMatrices().pushMatrix();
        Render2DCoordinateSpace.applyGuiScaleIndependence((Matrix3x2f)drawContext.getMatrices());
        float iconX = x + (this.boxWidth - icon) * 0.5f;
        drawContext.getMatrices().translate(iconX + icon * 0.5f, y + PAD + icon * 0.5f);
        drawContext.getMatrices().scale(scale, scale);
        drawContext.getMatrices().translate(-8.0f, -8.0f);
        drawContext.drawItem(new ItemStack(Items.DIAMOND_SWORD), 0, 0);
        drawContext.getMatrices().popMatrix();
        if (showName) {
            float textX = x + (this.boxWidth - textWidth) * 0.5f;
            float textY = y + PAD + icon + 1.5f;
            if (module.hudShowShadow()) {
                Render2D.msdfText(FONT, name, textX + 0.8f, textY + 0.8f, size,
                        rtx.byazen.utils.color.ColorUtil.rgba(6, 8, 12, Math.round(150.0f * a)));
            }
            Render2D.msdfText(FONT, name, textX, textY, size,
                    rtx.byazen.utils.color.ColorUtil.rgba(232, 237, 246, Math.round(238.0f * a)));
        }
        Render2D.flush();
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        return delta;
    }
}
