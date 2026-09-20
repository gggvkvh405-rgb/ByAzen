package rtx.kimiko.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.kimiko.api.modules.impl.Utils.guishare.RemoteGuiWorld;
import rtx.kimiko.api.ui.UI;
import rtx.kimiko.mixin.accessor.GuiRendererDrawAccessor;
import rtx.kimiko.utils.render.post.guilayerblur.GuiCapture;
import rtx.kimiko.utils.render.post.guilayerblur.GuiLayerBlurRenderer;
import rtx.kimiko.utils.render.post.guimotionblur.GuiMotionBlurRenderer;
import rtx.kimiko.utils.render.render2d.ClientSplits;
import rtx.kimiko.utils.render.render2d.arc.ArcRenderer;
import rtx.kimiko.utils.render.render2d.blur.BlurFramebuffer;
import rtx.kimiko.utils.render.render2d.circle.CircleRenderer;
import rtx.kimiko.utils.render.render2d.glass.GlassRenderer;
import rtx.kimiko.utils.render.render2d.glow.GlowRenderer;
import rtx.kimiko.utils.render.render2d.image.ImageRenderer;
import rtx.kimiko.utils.render.render2d.line.LineRenderer;
import rtx.kimiko.utils.render.render2d.outline.outline360.Outline360Renderer;
import rtx.kimiko.utils.render.render2d.outline.outlinedefault.DefaultOutlineRenderer;
import rtx.kimiko.utils.render.render2d.outline.outlineglass.GlassOutlineRenderer;
import rtx.kimiko.utils.render.render2d.picker.PickerRenderer;
import rtx.kimiko.utils.render.render2d.radialglass.RadialGlassRenderer;
import rtx.kimiko.utils.render.render2d.rectangle.rectdefault.DefaultRectangleRenderer;
import rtx.kimiko.utils.render.render2d.rectangle.recthalficon.HalfIconRectangleRenderer;
import rtx.kimiko.utils.render.render2d.rectangle.recthalftone.HalftoneRectangleRenderer;
import rtx.kimiko.utils.render.render2d.ripple.RippleRenderer;
import rtx.kimiko.utils.render.render2d.sectormask.SectorMaskRenderer;
import rtx.kimiko.utils.render.render2d.shape.ShapeRenderer;
import rtx.kimiko.utils.render.render2d.shimmer.ShimmerRenderer;
import rtx.kimiko.utils.render.render2d.zippy.ZippyRenderer;
import rtx.kimiko.utils.render.renderitem.RenderItem;

@Mixin(net.minecraft.client.gui.render.GuiRenderer.class)
public abstract class GuiRendererMixin {
    @Shadow
    @Final
    private List<?> draws;
    private RenderPass kimiko_currentRenderPass;
    private boolean kimiko_blurDrawActive;
    private boolean kimiko_glassDrawActive;
    private boolean kimiko_shapeDrawActive;
    private boolean kimiko_glowDrawActive;
    private boolean kimiko_glassOutlineDrawActive;
    private boolean kimiko_circleDrawActive;
    private boolean kimiko_arcDrawActive;
    private boolean kimiko_radialGlassDrawActive;
    private boolean kimiko_sectorMaskDrawActive;
    private boolean kimiko_pickerDrawActive;
    private boolean kimiko_rectangleDrawActive;
    private boolean kimiko_halfIconRectangleDrawActive;
    private boolean kimiko_halftoneRectangleDrawActive;
    private boolean kimiko_zippyDrawActive;
    private boolean kimiko_outlineDrawActive;
    private boolean kimiko_outline360DrawActive;
    private boolean kimiko_imageDrawActive;
    private boolean kimiko_lineDrawActive;
    private boolean kimiko_itemDrawActive;
    private boolean kimiko_rippleDrawActive;
    private boolean kimiko_shimmerDrawActive;

    @Inject(method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("HEAD"), require = 0)
    private void kimiko_beginBlurFrame(CallbackInfo ci) {
        BlurFramebuffer.getInstance().beginGuiFrame();
        GlassRenderer.getInstance().beginGuiFrame();
        ShapeRenderer.getInstance().beginGuiFrame();
        GlowRenderer.getInstance().beginGuiFrame();
        GlassOutlineRenderer.getInstance().beginGuiFrame();
        CircleRenderer.getInstance().beginGuiFrame();
        ArcRenderer.getInstance().beginGuiFrame();
        RadialGlassRenderer.getInstance().beginGuiFrame();
        SectorMaskRenderer.getInstance().beginGuiFrame();
        PickerRenderer.getInstance().beginGuiFrame();
        DefaultRectangleRenderer.getInstance().beginGuiFrame();
        HalfIconRectangleRenderer.getInstance().beginGuiFrame();
        HalftoneRectangleRenderer.getInstance().beginGuiFrame();
        ZippyRenderer.getInstance().beginGuiFrame();
        DefaultOutlineRenderer.getInstance().beginGuiFrame();
        Outline360Renderer.getInstance().beginGuiFrame();
        ImageRenderer.getInstance().beginGuiFrame();
        LineRenderer.getInstance().beginGuiFrame();
        RippleRenderer.getInstance().beginGuiFrame();
        ShimmerRenderer.getInstance().beginGuiFrame();
        RenderItem.beginGuiFrame();
    }

    @Inject(method = "prepare()V", at = @At("HEAD"), require = 0)
    private void kimiko_preparePendingBlurResources(CallbackInfo ci) {
        ClientSplits.update();
        BlurFramebuffer.getInstance().preparePending();
        GlowRenderer.getInstance().preparePending();
    }

    @Inject(method = "prepare()V", at = @At("RETURN"), require = 0)
    private void kimiko_prepareRenderUniforms(CallbackInfo ci) {
        BlurFramebuffer.getInstance().prepareBuffers();
        GlassRenderer.getInstance().prepareBuffers();
        ShapeRenderer.getInstance().prepareBuffers();
        GlowRenderer.getInstance().prepareBuffers();
        GlassOutlineRenderer.getInstance().prepareBuffers();
        CircleRenderer.getInstance().prepareBuffers();
        ArcRenderer.getInstance().prepareBuffers();
        RadialGlassRenderer.getInstance().prepareBuffers();
        SectorMaskRenderer.getInstance().prepareBuffers();
        PickerRenderer.getInstance().prepareBuffers();
        DefaultRectangleRenderer.getInstance().prepareBuffers();
        HalfIconRectangleRenderer.getInstance().prepareBuffers();
        HalftoneRectangleRenderer.getInstance().prepareBuffers();
        ZippyRenderer.getInstance().prepareBuffers();
        DefaultOutlineRenderer.getInstance().prepareBuffers();
        Outline360Renderer.getInstance().prepareBuffers();
        ImageRenderer.getInstance().prepareBuffers();
        LineRenderer.getInstance().prepareBuffers();
        RippleRenderer.getInstance().prepareBuffers();
        ShimmerRenderer.getInstance().prepareBuffers();
        RenderItem.prepareBuffers();
    }

    @Inject(method = "renderPreparedDraws(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("HEAD"), require = 0)
    private void kimiko_prepareBlurCapture(CallbackInfo ci) {
        BlurFramebuffer.getInstance().prepareGuiDraw();
        GuiLayerBlurRenderer.beginCapture(GuiCapture.active(), RemoteGuiWorld.captureRequested());
    }

    @WrapOperation(method = "renderPreparedDraws(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Ljava/util/function/Supplier;Lnet/minecraft/client/gl/Framebuffer;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;II)V"), require = 0)
    private void kimiko_routePanelRange(GuiRenderer instance, Supplier<String> label, Framebuffer target, GpuBufferSlice fog, GpuBufferSlice transforms, GpuBuffer indices, VertexFormat.IndexType indexType, int from, int to, Operation<Void> original) {
        int cursor = from;
        Framebuffer remoteTarget = GuiLayerBlurRenderer.remoteCaptureTarget();
        if (remoteTarget != null) {
            while (cursor < to) {
                if (GuiLayerBlurRenderer.isRemoteRouting()) {
                    int end = this.kimiko_findRemoteMark(cursor, to, false);
                    if (end < cursor) {
                        this.kimiko_drawCaptured(instance, label, remoteTarget, fog, transforms, indices, indexType, cursor, to, original);
                        return;
                    }
                    if (cursor < end) {
                        this.kimiko_drawCaptured(instance, label, remoteTarget, fog, transforms, indices, indexType, cursor, end, original);
                    }
                    GuiLayerBlurRenderer.setRemoteRouting(false);
                    cursor = end + 1;
                    continue;
                }
                int begin = this.kimiko_findRemoteMark(cursor, to, true);
                if (begin < cursor) break;
                if (cursor < begin) {
                    this.kimiko_routeLocal(instance, label, target, fog, transforms, indices, indexType, cursor, begin, original);
                }
                GuiLayerBlurRenderer.setRemoteRouting(true);
                cursor = begin + 1;
            }
            if (cursor >= to) {
                return;
            }
        }
        this.kimiko_routeLocal(instance, label, target, fog, transforms, indices, indexType, cursor, to, original);
    }

    @Unique
    private void kimiko_routeLocal(GuiRenderer instance, Supplier<String> label, Framebuffer target, GpuBufferSlice fog, GpuBufferSlice transforms, GpuBuffer indices, VertexFormat.IndexType indexType, int from, int to, Operation<Void> original) {
        if (from >= to) {
            return;
        }
        Framebuffer captureTarget = GuiLayerBlurRenderer.captureTarget();
        if (captureTarget == null) {
            int popupBoundary = UI.popupLayerCapturePending() ? this.kimiko_findBoundary(from, to, true) : -1;
            if (popupBoundary < from) {
                original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, from, to});
                return;
            }
            if (from < popupBoundary) {
                original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, from, popupBoundary});
            }
            if (UI.consumePopupLayerCapture()) {
                GuiMotionBlurRenderer.captureBackground(0.0f);
            }
            if (popupBoundary + 1 < to) {
                original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, popupBoundary + 1, to});
            }
            return;
        }
        int boundary = this.kimiko_findBoundary(from, to, false);
        if (boundary < from) {
            if (GuiCapture.emitPanelBoundary()) {
                original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, from, to});
            } else {
                this.kimiko_drawCaptured(instance, label, captureTarget, fog, transforms, indices, indexType, from, to, original);
            }
            return;
        }
        if (from < boundary) {
            this.kimiko_drawCaptured(instance, label, captureTarget, fog, transforms, indices, indexType, from, boundary, original);
        }
        if (boundary + 1 < to) {
            original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, boundary + 1, to});
        }
    }

    @Unique
    private int kimiko_findRemoteMark(int from, int to, boolean begin) {
        int end = Math.min(to, this.draws.size());
        for (int i = Math.max(0, from); i < end; ++i) {
            Object draw = this.draws.get(i);
            if (!(draw instanceof GuiRendererDrawAccessor)) continue;
            GuiRendererDrawAccessor accessor = (GuiRendererDrawAccessor)draw;
            RenderPipeline pipeline = accessor.kimiko_getPipeline();
            if (!(begin ? GuiLayerBlurRenderer.isRemoteBegin(pipeline) : GuiLayerBlurRenderer.isRemoteEnd(pipeline))) continue;
            return i;
        }
        return -1;
    }

    @Unique
    private void kimiko_drawCaptured(GuiRenderer instance, Supplier<String> label, Framebuffer captureTarget, GpuBufferSlice fog, GpuBufferSlice transforms, GpuBuffer indices, VertexFormat.IndexType indexType, int from, int to, Operation<Void> original) {
        int cursor = from;
        boolean remote = captureTarget == GuiLayerBlurRenderer.remoteCaptureTarget();
        int popupBoundary;
        int cardBegin;
        int cardEnd;
        int boundary;
        while (cursor < to && (boundary = GuiRendererMixin.kimiko_firstBoundary(popupBoundary = this.kimiko_findBoundary(cursor, to, true), cardBegin = remote ? this.kimiko_findRemoteCardMark(cursor, to, true) : -1, cardEnd = remote ? this.kimiko_findRemoteCardMark(cursor, to, false) : -1)) >= cursor) {
            if (cursor < boundary) {
                original.call(new Object[]{instance, label, captureTarget, fog, transforms, indices, indexType, cursor, boundary});
            }
            if (boundary == cardBegin) {
                RemoteGuiWorld.beginCardBlurDraw(captureTarget);
            } else if (boundary == cardEnd) {
                RemoteGuiWorld.endCardBlurDraw(captureTarget);
            } else {
                BlurFramebuffer.getInstance().recaptureWorldBackdrop();
            }
            cursor = boundary + 1;
        }
        if (cursor < to) {
            original.call(new Object[]{instance, label, captureTarget, fog, transforms, indices, indexType, cursor, to});
        }
    }

    @Unique
    private int kimiko_findRemoteCardMark(int from, int to, boolean begin) {
        int end = Math.min(to, this.draws.size());
        for (int i = Math.max(0, from); i < end; ++i) {
            Object draw = this.draws.get(i);
            if (!(draw instanceof GuiRendererDrawAccessor)) continue;
            GuiRendererDrawAccessor accessor = (GuiRendererDrawAccessor)draw;
            RenderPipeline pipeline = accessor.kimiko_getPipeline();
            if (!(begin ? GuiLayerBlurRenderer.isRemoteCardBegin(pipeline) : GuiLayerBlurRenderer.isRemoteCardEnd(pipeline))) continue;
            return i;
        }
        return -1;
    }

    @Unique
    private static int kimiko_firstBoundary(int first, int second, int third) {
        int result = -1;
        if (first >= 0) {
            result = first;
        }
        if (second >= 0 && (result < 0 || second < result)) {
            result = second;
        }
        if (third >= 0 && (result < 0 || third < result)) {
            result = third;
        }
        return result;
    }

    @Unique
    private int kimiko_findBoundary(int from, int to, boolean popup) {
        int end = Math.min(to, this.draws.size());
        for (int i = Math.max(0, from); i < end; ++i) {
            Object draw = this.draws.get(i);
            if (!(draw instanceof GuiRendererDrawAccessor)) continue;
            GuiRendererDrawAccessor accessor = (GuiRendererDrawAccessor)draw;
            RenderPipeline pipeline = accessor.kimiko_getPipeline();
            if (!(popup ? GuiLayerBlurRenderer.isPopupBoundary(pipeline) : GuiLayerBlurRenderer.isPanelBoundary(pipeline))) continue;
            return i;
        }
        return -1;
    }

    @Redirect(method = "renderPreparedDraws(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderBlur()V"), require = 0)
    private void kimiko_cardLayerMidCapture(GameRenderer gameRenderer) {
        if (GuiCapture.active()) {
            GuiLayerBlurRenderer.markPanelRange();
            UI.consumePanelSplitMark();
            UI.consumeCardStratumMark();
            UI.consumePopupStratumMark();
            UI.consumeVanillaBlurRequest();
            return;
        }
        if (UI.consumePanelSplitMark()) {
            UI.applyMainCompositeAtSplit();
            if (UI.consumeVanillaBlurRequest() && !UI.isOpen()) {
                gameRenderer.renderBlur();
            }
            return;
        }
        if (UI.consumePopupStratumMark()) {
            BlurFramebuffer.getInstance().recaptureBackdrop();
            if (UI.consumePopupBlurCapture()) {
                GuiMotionBlurRenderer.captureBackground(0.0f);
            }
            return;
        }
        if (UI.consumeCardStratumMark()) {
            GuiMotionBlurRenderer.captureBackground(0.0f);
            return;
        }
        if (UI.isOpen()) {
            return;
        }
        gameRenderer.renderBlur();
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V"), require = 0)
    private void kimiko_trackPipeline(RenderPass renderPass, RenderPipeline pipeline) {
        this.kimiko_currentRenderPass = renderPass;
        this.kimiko_blurDrawActive = BlurFramebuffer.getInstance().isBlurPipeline(pipeline);
        this.kimiko_glassDrawActive = GlassRenderer.getInstance().isGlassPipeline(pipeline);
        this.kimiko_shapeDrawActive = ShapeRenderer.getInstance().isShapePipeline(pipeline);
        this.kimiko_glowDrawActive = GlowRenderer.getInstance().isGlowPipeline(pipeline);
        this.kimiko_glassOutlineDrawActive = GlassOutlineRenderer.getInstance().isGlassOutlinePipeline(pipeline);
        this.kimiko_circleDrawActive = CircleRenderer.getInstance().isCirclePipeline(pipeline);
        this.kimiko_arcDrawActive = ArcRenderer.getInstance().isArcPipeline(pipeline);
        this.kimiko_radialGlassDrawActive = RadialGlassRenderer.getInstance().isRadialGlassPipeline(pipeline);
        this.kimiko_sectorMaskDrawActive = SectorMaskRenderer.getInstance().isSectorMaskPipeline(pipeline);
        this.kimiko_pickerDrawActive = PickerRenderer.getInstance().isPickerPipeline(pipeline);
        this.kimiko_rectangleDrawActive = DefaultRectangleRenderer.getInstance().isRectanglePipeline(pipeline);
        this.kimiko_halfIconRectangleDrawActive = HalfIconRectangleRenderer.getInstance().isHalfIconRectanglePipeline(pipeline);
        this.kimiko_halftoneRectangleDrawActive = HalftoneRectangleRenderer.getInstance().isHalftoneRectanglePipeline(pipeline);
        this.kimiko_zippyDrawActive = ZippyRenderer.getInstance().isZippyPipeline(pipeline);
        this.kimiko_outlineDrawActive = DefaultOutlineRenderer.getInstance().isOutlinePipeline(pipeline);
        this.kimiko_outline360DrawActive = Outline360Renderer.getInstance().isOutline360Pipeline(pipeline);
        this.kimiko_imageDrawActive = ImageRenderer.getInstance().isImagePipeline(pipeline);
        this.kimiko_lineDrawActive = LineRenderer.getInstance().isLinePipeline(pipeline);
        this.kimiko_rippleDrawActive = RippleRenderer.getInstance().isRipplePipeline(pipeline);
        this.kimiko_shimmerDrawActive = ShimmerRenderer.getInstance().isShimmerPipeline(pipeline);
        this.kimiko_itemDrawActive = RenderItem.isItemPipeline(pipeline);
        renderPass.setPipeline(pipeline);
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V", shift = At.Shift.BEFORE), require = 0)
    private void kimiko_bindBlurParams(CallbackInfo ci) {
        if (this.kimiko_blurDrawActive && this.kimiko_currentRenderPass != null) {
            BlurFramebuffer.getInstance().bindBlurParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_glassDrawActive && this.kimiko_currentRenderPass != null) {
            GlassRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_shapeDrawActive && this.kimiko_currentRenderPass != null) {
            ShapeRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_glowDrawActive && this.kimiko_currentRenderPass != null) {
            GlowRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_glassOutlineDrawActive && this.kimiko_currentRenderPass != null) {
            GlassOutlineRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_circleDrawActive && this.kimiko_currentRenderPass != null) {
            CircleRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_arcDrawActive && this.kimiko_currentRenderPass != null) {
            ArcRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_radialGlassDrawActive && this.kimiko_currentRenderPass != null) {
            RadialGlassRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_sectorMaskDrawActive && this.kimiko_currentRenderPass != null) {
            SectorMaskRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_pickerDrawActive && this.kimiko_currentRenderPass != null) {
            PickerRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_rectangleDrawActive && this.kimiko_currentRenderPass != null) {
            DefaultRectangleRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_halfIconRectangleDrawActive && this.kimiko_currentRenderPass != null) {
            HalfIconRectangleRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_halftoneRectangleDrawActive && this.kimiko_currentRenderPass != null) {
            HalftoneRectangleRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_zippyDrawActive && this.kimiko_currentRenderPass != null) {
            ZippyRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_outlineDrawActive && this.kimiko_currentRenderPass != null) {
            DefaultOutlineRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_outline360DrawActive && this.kimiko_currentRenderPass != null) {
            Outline360Renderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_imageDrawActive && this.kimiko_currentRenderPass != null) {
            ImageRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_lineDrawActive && this.kimiko_currentRenderPass != null) {
            LineRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_rippleDrawActive && this.kimiko_currentRenderPass != null) {
            RippleRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_shimmerDrawActive && this.kimiko_currentRenderPass != null) {
            ShimmerRenderer.getInstance().bindParams(this.kimiko_currentRenderPass);
        }
        if (this.kimiko_itemDrawActive && this.kimiko_currentRenderPass != null) {
            RenderItem.bindParams(this.kimiko_currentRenderPass);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At("RETURN"), require = 0)
    private void kimiko_clearTrackedPipeline(CallbackInfo ci) {
        this.kimiko_currentRenderPass = null;
        this.kimiko_blurDrawActive = false;
        this.kimiko_glassDrawActive = false;
        this.kimiko_shapeDrawActive = false;
        this.kimiko_glowDrawActive = false;
        this.kimiko_glassOutlineDrawActive = false;
        this.kimiko_circleDrawActive = false;
        this.kimiko_arcDrawActive = false;
        this.kimiko_radialGlassDrawActive = false;
        this.kimiko_sectorMaskDrawActive = false;
        this.kimiko_pickerDrawActive = false;
        this.kimiko_rectangleDrawActive = false;
        this.kimiko_halfIconRectangleDrawActive = false;
        this.kimiko_halftoneRectangleDrawActive = false;
        this.kimiko_zippyDrawActive = false;
        this.kimiko_outlineDrawActive = false;
        this.kimiko_outline360DrawActive = false;
        this.kimiko_imageDrawActive = false;
        this.kimiko_lineDrawActive = false;
        this.kimiko_itemDrawActive = false;
        this.kimiko_rippleDrawActive = false;
        this.kimiko_shimmerDrawActive = false;
    }
}
