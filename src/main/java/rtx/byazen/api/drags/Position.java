package rtx.byazen.api.drags;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.utils.render.render2d.Render2DCoordinateSpace;

public final class Position {
    public static final float SCREEN_MARGIN = 5.0f;
    private float x;
    private float y;

    public Position(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    public float x() {
        return this.x;
    }

    public void set(float f, float f2, float f3, float f4) {
        this.x = Position.clampX(f, f3);
        this.y = Position.clampY(f2, f4);
    }

    public float y() {
        return this.y;
    }

    public static float clampX(float f, float f2) {
        float f3 = Math.max(5.0f, Position.screenWidth() - f2 - 5.0f);
        return Math.max(5.0f, Math.min(f3, f));
    }

    public static float clampY(float f, float f2) {
        float f3 = Math.max(5.0f, Position.screenHeight() - f2 - 5.0f);
        return Math.max(5.0f, Math.min(f3, f));
    }

    public void setRaw(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    public static float mouseX() {
        return Position.mapMouse(Position.rawMouseX());
    }

    public static float mouseY() {
        return Position.mapMouse(Position.rawMouseY());
    }

    /** Пересчёт координат мыши под масштаб интерфейса клиента. */
    private static float mapMouse(float f) {
        float f2 = rtx.byazen.utils.ui.UiScale.screenScale();
        if (f2 == 1.0f) {
            return f;
        }
        float f3 = Position.screenWidthRaw() * 0.5f;
        return f3 + (f - f3) / f2;
    }

    private static float rawMouseX() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) {
            return 0.0f;
        }
        double d = minecraftClient.mouse.getScaledX(minecraftClient.getWindow());
        return (float)(d / (double)Render2DCoordinateSpace.guiIndependentScale());
    }

    private static float rawMouseY() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) {
            return 0.0f;
        }
        double d = minecraftClient.mouse.getScaledY(minecraftClient.getWindow());
        return (float)(d / (double)Render2DCoordinateSpace.guiIndependentScale());
    }

    public static float screenWidthRaw() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null || minecraftClient.getWindow() == null) {
            return 960.0f;
        }
        return (float)minecraftClient.getWindow().getFramebufferWidth() / Render2DCoordinateSpace.designGuiScale();
    }

    public static float screenHeightRaw() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null || minecraftClient.getWindow() == null) {
            return 540.0f;
        }
        return (float)minecraftClient.getWindow().getFramebufferHeight() / Render2DCoordinateSpace.designGuiScale();
    }

    public static float screenWidth() {
        return Position.screenWidthRaw() / rtx.byazen.utils.ui.UiScale.screenScale();
    }

    public static float screenHeight() {
        return Position.screenHeightRaw() / rtx.byazen.utils.ui.UiScale.screenScale();
    }
}

