package rtx.kimiko.utils.render.render2d.glass;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import rtx.kimiko.utils.render.render2d.blur.BlurCapture;
import rtx.kimiko.utils.render.render2d.glass.BuiltGlass;
import rtx.kimiko.utils.render.render2d.glass.GlassRenderer;

final class GlassRenderState
implements SimpleGuiElementRenderState {
    private final BuiltGlass glass;
    private final Matrix3x2f pose;
    private final ScreenRect scissorArea;
    private final ScreenRect bounds;
    private final BlurCapture capture;

    GlassRenderState(Matrix3x2f matrix3x2f, BuiltGlass builtGlass, ScreenRect screenRect, BlurCapture blurCapture) {
        this.glass = builtGlass;
        this.pose = new Matrix3x2f((Matrix3x2fc)matrix3x2f);
        this.scissorArea = screenRect;
        this.capture = blurCapture;
        ScreenRect screenRect2 = new ScreenRect(Math.round(builtGlass.x()), Math.round(builtGlass.y()), Math.round(builtGlass.width()), Math.round(builtGlass.height())).transformEachVertex((Matrix3x2fc)(Object)this.pose);
        this.bounds = screenRect == null ? screenRect2 : screenRect.intersection(screenRect2);
    }

    public ScreenRect bounds() {
        return this.bounds;
    }

    public RenderPipeline pipeline() {
        return GlassRenderer.GLASS_PIPELINE;
    }

    public ScreenRect scissorArea() {
        return this.scissorArea;
    }

    public TextureSetup textureSetup() {
        return this.capture.setup;
    }

    public void setupVertices(VertexConsumer vertices) {
        int n = GlassRenderer.getInstance().reserve(this.glass, this.capture);
        if (n < 0) {
            return;
        }
        float f = this.glass.x();
        float f2 = this.glass.y();
        float f3 = this.glass.x() + this.glass.width();
        float f4 = this.glass.y() + this.glass.height();
        this.vertex(vertices, f, f2, n);
        this.vertex(vertices, f, f4, n);
        this.vertex(vertices, f3, f4, n);
        this.vertex(vertices, f3, f2, n);
    }

    private void vertex(VertexConsumer vertexConsumer, float f, float f2, int n) {
        vertexConsumer.vertex((Matrix3x2fc)(Object)this.pose, f, f2).color(this.glass.color()).lineWidth((float)(n + 1));
    }
}

