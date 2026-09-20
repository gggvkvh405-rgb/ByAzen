package rtx.kimiko.api.modules.impl.Interface;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Arm;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.InterfaceComponentModule;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.utils.animations.Easings;
import rtx.kimiko.utils.animations.SmoothAnimation;
import rtx.kimiko.utils.render.others.RectUtil;
import rtx.kimiko.utils.render.render2d.ClientPalette;
import rtx.kimiko.utils.render.render2d.Render2D;
import rtx.kimiko.utils.render.render2d.Render2DCoordinateSpace;

public final class CustomHotbar
extends InterfaceComponentModule {
    private final SmoothAnimation selection = new SmoothAnimation();
    private boolean selectionInitialized;

    public CustomHotbar() {
        super("Custom Hotbar", "\u0417\u0430\u043c\u0435\u043d\u044f\u0435\u0442 \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0439 \u0445\u043e\u0442\u0431\u0430\u0440 \u043a\u043b\u0438\u0435\u043d\u0442\u0441\u043a\u0438\u043c \u0441\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u044b\u043c \u0434\u0438\u0437\u0430\u0439\u043d\u043e\u043c.");
    }

    public static CustomHotbar getInstance() {
        return ModuleManager.get().get(CustomHotbar.class);
    }

    public static boolean isActive() {
        CustomHotbar customHotbar = CustomHotbar.getInstance();
        return customHotbar != null && customHotbar.isEnabled();
    }

    public void render(DrawContext drawContext) {
        if (!this.isEnabled() || drawContext == null || this.mc.player == null) {
            return;
        }
        int n = this.mc.player.getInventory().getSelectedSlot();
        if (!this.selectionInitialized) {
            this.selection.set(n);
            this.selectionInitialized = true;
        }
        this.selection.run(n, 0.22, Easings.EXPO_OUT, true);
        this.selection.update();
        float f = (float)drawContext.getScaledWindowWidth() * 0.5f - 1.0f;
        float f2 = (float)drawContext.getScaledWindowHeight() - 22.0f;
        float f3 = f - 91.0f;
        float f4 = (float)Render2DCoordinateSpace.guiScale() / Render2DCoordinateSpace.designGuiScale();
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().scale(f4);
        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(f3, f2, 182.0f, 22.0f, CustomHotbar.cornerRadius(182.0f, 22.0f), 1.0f);
        float f5 = f - 90.0f + this.selection.get() * 20.0f;
        float f6 = f2 + 1.0f;
        float f7 = CustomHotbar.cornerRadius(20.0f, 20.0f);
        RectUtil.drawClientRectNoGlow(f5, f6, 20.0f, 20.0f, f7, 0.9f);
        int[] nArray = ClientPalette.cornerColors(0.74509805f);
        Render2D.outline(f5, f6, 20.0f, 20.0f, f7, 0.75f, nArray[0], nArray[1], nArray[2], nArray[3]);
        if (!this.mc.player.getOffHandStack().isEmpty()) {
            boolean bl = this.mc.player.getMainArm().getOpposite() == Arm.LEFT;
            float f8 = bl ? f - 120.0f : f + 97.0f;
            float f9 = f2 - 1.0f;
            float f10 = CustomHotbar.cornerRadius(24.0f, 24.0f);
            RectUtil.drawClientRect(f8, f9, 24.0f, 24.0f, f10, 1.0f);
            int[] nArray2 = ClientPalette.cornerColors(0.5647059f);
            Render2D.outline(f8, f9, 24.0f, 24.0f, f10, 0.5f, nArray2[0], nArray2[1], nArray2[2], nArray2[3]);
        }
        Render2D.flush();
        drawContext.getMatrices().popMatrix();
    }

    @Override
    protected void onDisable() {
        this.selectionInitialized = false;
    }

    private static float cornerRadius(float f, float f2) {
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        float f3 = interfaceModule != null ? interfaceModule.rectCornerRadius.getFloat() : 7.0f;
        return Math.min(f3, Math.min(f, f2) * 0.5f);
    }
}

