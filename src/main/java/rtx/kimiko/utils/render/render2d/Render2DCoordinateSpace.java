package rtx.kimiko.utils.render.render2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public final class Render2DCoordinateSpace {
    private static final float DESIGN_GUI_SCALE = 2.0f;

    private Render2DCoordinateSpace() {
    }

    public static Matrix3x2f pose(DrawContext drawContext) {
        float f = Render2DCoordinateSpace.guiIndependentScale();
        Matrix3x2f matrix3x2f = new Matrix3x2f((Matrix3x2fc)drawContext.getMatrices());
        if (f == 1.0f) {
            return matrix3x2f;
        }
        return new Matrix3x2f().scale(f).mul((Matrix3x2fc)matrix3x2f);
    }

    public static int guiScale() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) {
            return 1;
        }
        Window window = minecraftClient.getWindow();
        if (window == null) {
            return 1;
        }
        return Math.max(1, window.getScaleFactor());
    }

    public static float guiIndependentScale() {
        return 2.0f / (float)Render2DCoordinateSpace.guiScale();
    }

    public static float designGuiScale() {
        return 2.0f;
    }

    public static void applyGuiScaleIndependence(Matrix3x2f matrix3x2f) {
        float f = Render2DCoordinateSpace.guiIndependentScale();
        if (f != 1.0f) {
            matrix3x2f.set((Matrix3x2fc)new Matrix3x2f().scale(f).mul((Matrix3x2fc)matrix3x2f));
        }
    }

    public static float toGui(float f) {
        return f * Render2DCoordinateSpace.guiIndependentScale();
    }

    public static int toGuiInt(float f) {
        return Math.round(Render2DCoordinateSpace.toGui(f));
    }
}

