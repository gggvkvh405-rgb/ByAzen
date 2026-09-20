package rtx.byazen.mixin;

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
import rtx.byazen.api.modules.impl.Utils.guishare.RemoteGuiWorld;
import rtx.byazen.api.ui.UI;
import rtx.byazen.mixin.accessor.GuiRendererDrawAccessor;
import rtx.byazen.utils.render.post.guilayerblur.GuiCapture;
import rtx.byazen.utils.render.post.guilayerblur.GuiLayerBlurRenderer;
import rtx.byazen.utils.render.post.guimotionblur.GuiMotionBlurRenderer;
import rtx.byazen.utils.render.render2d.ClientSplits;
import rtx.byazen.utils.render.render2d.arc.ArcRenderer;
import rtx.byazen.utils.render.render2d.blur.BlurFramebuffer;
import rtx.byazen.utils.render.render2d.circle.CircleRenderer;
import rtx.byazen.utils.render.render2d.glass.GlassRenderer;
import rtx.byazen.utils.render.render2d.glow.GlowRenderer;
import rtx.byazen.utils.render.render2d.image.ImageRenderer;
import rtx.byazen.utils.render.render2d.line.LineRenderer;
import rtx.byazen.utils.render.render2d.outline.outline360.Outline360Renderer;
import rtx.byazen.utils.render.render2d.outline.outlinedefault.DefaultOutlineRenderer;
import rtx.byazen.utils.render.render2d.outline.outlineglass.GlassOutlineRenderer;
import rtx.byazen.utils.render.render2d.picker.PickerRenderer;
import rtx.byazen.utils.render.render2d.radialglass.RadialGlassRenderer;
import rtx.byazen.utils.render.render2d.rectangle.rectdefault.DefaultRectangleRenderer;
import rtx.byazen.utils.render.render2d.rectangle.recthalficon.HalfIconRectangleRenderer;
import rtx.byazen.utils.render.render2d.rectangle.recthalftone.HalftoneRectangleRenderer;
import rtx.byazen.utils.render.render2d.ripple.RippleRenderer;
import rtx.byazen.utils.render.render2d.sectormask.SectorMaskRenderer;
import rtx.byazen.utils.render.render2d.shape.ShapeRenderer;
import rtx.byazen.utils.render.render2d.shimmer.ShimmerRenderer;
import rtx.byazen.utils.render.render2d.zippy.ZippyRenderer;
import rtx.byazen.utils.render.renderitem.RenderItem;

@Mixin(net.minecraft.client.gui.render.GuiRenderer.class)
public abstract class GuiRendererMixin {
    @Shadow
    @Final
    private List<?> draws;
    private RenderPass byazen_currentRenderPass;
    private boolean byazen_blurDrawActive;
    private boolean byazen_glassDrawActive;
    private boolean byazen_shapeDrawActive;
    private boolean byazen_glowDrawActive;
    private boolean byazen_glassOutlineDrawActive;
    private boolean byazen_circleDrawActive;
    private boolean byazen_arcDrawActive;
    private boolean byazen_radialGlassDrawActive;
    private boolean byazen_sectorMaskDrawActive;
    private boolean byazen_pickerDrawActive;
    private boolean byazen_rectangleDrawActive;
    private boolean byazen_halfIconRectangleDrawActive;
    private boolean byazen_halftoneRectangleDrawActive;
    private boolean byazen_zippyDrawActive;
    private boolean byazen_outlineDrawActive;
    private boolean byazen_outline360DrawActive;
    private boolean byazen_imageDrawActive;
    private boolean byazen_lineDrawActive;
    private boolean byazen_itemDrawActive;
    private boolean byazen_rippleDrawActive;
    private boolean byazen_shimmerDrawActive;

    @Inject(method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("HEAD"), require = 0)
    private void byazen_beginBlurFrame(CallbackInfo ci) {
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
    private void byazen_preparePendingBlurResources(CallbackInfo ci) {
        ClientSplits.update();
        BlurFramebuffer.getInstance().preparePending();
        GlowRenderer.getInstance().preparePending();
    }

    @Inject(method = "prepare()V", at = @At("RETURN"), require = 0)
    private void byazen_prepareRenderUniforms(CallbackInfo ci) {
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
    private void byazen_prepareBlurCapture(CallbackInfo ci) {
        BlurFramebuffer.getInstance().prepareGuiDraw();
        GuiLayerBlurRenderer.beginCapture(GuiCapture.active(), RemoteGuiWorld.captureRequested());
    }

    @WrapOperation(method = "renderPreparedDraws(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Ljava/util/function/Supplier;Lnet/minecraft/client/gl/Framebuffer;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;II)V"), require = 0)
    private void byazen_routePanelRange(GuiRenderer instance, Supplier<String> label, Framebuffer target, GpuBufferSlice fog, GpuBufferSlice transforms, GpuBuffer indices, VertexFormat.IndexType indexType, int from, int to, Operation<Void> original) {
        int cursor = from;
        Framebuffer remoteTarget = GuiLayerBlurRenderer.remoteCaptureTarget();
        if (remoteTarget != null) {
            while (cursor < to) {
                if (GuiLayerBlurRenderer.isRemoteRouting()) {
                    int end = this.byazen_findRemoteMark(cursor, to, false);
                    if (end < cursor) {
                        this.byazen_drawCaptured(instance, label, remoteTarget, fog, transforms, indices, indexType, cursor, to, original);
                        return;
                    }
                    if (cursor < end) {
                        this.byazen_drawCaptured(instance, label, remoteTarget, fog, transforms, indices, indexType, cursor, end, original);
                    }
                    GuiLayerBlurRenderer.setRemoteRouting(false);
                    cursor = end + 1;
                    continue;
                }
                int begin = this.byazen_findRemoteMark(cursor, to, true);
                if (begin < cursor) break;
                if (cursor < begin) {
                    this.byazen_routeLocal(instance, label, target, fog, transforms, indices, indexType, cursor, begin, original);
                }
                GuiLayerBlurRenderer.setRemoteRouting(true);
                cursor = begin + 1;
            }
            if (cursor >= to) {
                return;
            }
        }
        this.byazen_routeLocal(instance, label, target, fog, transforms, indices, indexType, cursor, to, original);
    }

    @Unique
    private void byazen_routeLocal(GuiRenderer instance, Supplier<String> label, Framebuffer target, GpuBufferSlice fog, GpuBufferSlice transforms, GpuBuffer indices, VertexFormat.IndexType indexType, int from, int to, Operation<Void> original) {
        if (from >= to) {
            return;
        }
        Framebuffer captureTarget = GuiLayerBlurRenderer.captureTarget();
        if (captureTarget == null) {
            int popupBoundary = UI.popupLayerCapturePending() ? this.byazen_findBoundary(from, to, true) : -1;
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
        int boundary = this.byazen_findBoundary(from, to, false);
        if (boundary < from) {
            if (GuiCapture.emitPanelBoundary()) {
                original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, from, to});
            } else {
                this.byazen_drawCaptured(instance, label, captureTarget, fog, transforms, indices, indexType, from, to, original);
            }
            return;
        }
        if (from < boundary) {
            this.byazen_drawCaptured(instance, label, captureTarget, fog, transforms, indices, indexType, from, boundary, original);
        }
        if (boundary + 1 < to) {
            original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, boundary + 1, to});
        }
    }

    @Unique
    private int byazen_findRemoteMark(int from, int to, boolean begin) {
        int end = Math.min(to, this.draws.size());
        for (int i = Math.max(0, from); i < end; ++i) {
            Object draw = this.draws.get(i);
            if (!(draw instanceof GuiRendererDrawAccessor)) continue;
            GuiRendererDrawAccessor accessor = (GuiRendererDrawAccessor)draw;
            RenderPipeline pipeline = accessor.byazen_getPipeline();
            if (!(begin ? GuiLayerBlurRenderer.isRemoteBegin(pipeline) : GuiLayerBlurRenderer.isRemoteEnd(pipeline))) continue;
            return i;
        }
        return -1;
    }

    @Unique
    private void byazen_drawCaptured(GuiRenderer instance, Supplier<String> label, Framebuffer captureTarget, GpuBufferSlice fog, GpuBufferSlice transforms, GpuBuffer indices, VertexFormat.IndexType indexType, int from, int to, Operation<Void> original) {
        int cursor = from;
        boolean remote = captureTarget == GuiLayerBlurRenderer.remoteCaptureTarget();
        int popupBoundary;
        int cardBegin;
        int cardEnd;
        int boundary;
        while (cursor < to && (boundary = GuiRendererMixin.byazen_firstBoundary(popupBoundary = this.byazen_findBoundary(cursor, to, true), cardBegin = remote ? this.byazen_findRemoteCardMark(cursor, to, true) : -1, cardEnd = remote ? this.byazen_findRemoteCardMark(cursor, to, false) : -1)) >= cursor) {
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
    private int byazen_findRemoteCardMark(int from, int to, boolean begin) {
        int end = Math.min(to, this.draws.size());
        for (int i = Math.max(0, from); i < end; ++i) {
            Object draw = this.draws.get(i);
            if (!(draw instanceof GuiRendererDrawAccessor)) continue;
            GuiRendererDrawAccessor accessor = (GuiRendererDrawAccessor)draw;
            RenderPipeline pipeline = accessor.byazen_getPipeline();
            if (!(begin ? GuiLayerBlurRenderer.isRemoteCardBegin(pipeline) : GuiLayerBlurRenderer.isRemoteCardEnd(pipeline))) continue;
            return i;
        }
        return -1;
    }

    @Unique
    private static int byazen_firstBoundary(int first, int second, int third) {
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
    private int byazen_findBoundary(int from, int to, boolean popup) {
        int end = Math.min(to, this.draws.size());
        for (int i = Math.max(0, from); i < end; ++i) {
            Object draw = this.draws.get(i);
            if (!(draw instanceof GuiRendererDrawAccessor)) continue;
            GuiRendererDrawAccessor accessor = (GuiRendererDrawAccessor)draw;
            RenderPipeline pipeline = accessor.byazen_getPipeline();
            if (!(popup ? GuiLayerBlurRenderer.isPopupBoundary(pipeline) : GuiLayerBlurRenderer.isPanelBoundary(pipeline))) continue;
            return i;
        }
        return -1;
    }

    @Redirect(method = "renderPreparedDraws(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderBlur()V"), require = 0)
    private void byazen_cardLayerMidCapture(GameRenderer gameRenderer) {
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
    private void byazen_trackPipeline(RenderPass renderPass, RenderPipeline pipeline) {
        this.byazen_currentRenderPass = renderPass;
        this.byazen_blurDrawActive = BlurFramebuffer.getInstance().isBlurPipeline(pipeline);
        this.byazen_glassDrawActive = GlassRenderer.getInstance().isGlassPipeline(pipeline);
        this.byazen_shapeDrawActive = ShapeRenderer.getInstance().isShapePipeline(pipeline);
        this.byazen_glowDrawActive = GlowRenderer.getInstance().isGlowPipeline(pipeline);
        this.byazen_glassOutlineDrawActive = GlassOutlineRenderer.getInstance().isGlassOutlinePipeline(pipeline);
        this.byazen_circleDrawActive = CircleRenderer.getInstance().isCirclePipeline(pipeline);
        this.byazen_arcDrawActive = ArcRenderer.getInstance().isArcPipeline(pipeline);
        this.byazen_radialGlassDrawActive = RadialGlassRenderer.getInstance().isRadialGlassPipeline(pipeline);
        this.byazen_sectorMaskDrawActive = SectorMaskRenderer.getInstance().isSectorMaskPipeline(pipeline);
        this.byazen_pickerDrawActive = PickerRenderer.getInstance().isPickerPipeline(pipeline);
        this.byazen_rectangleDrawActive = DefaultRectangleRenderer.getInstance().isRectanglePipeline(pipeline);
        this.byazen_halfIconRectangleDrawActive = HalfIconRectangleRenderer.getInstance().isHalfIconRectanglePipeline(pipeline);
        this.byazen_halftoneRectangleDrawActive = HalftoneRectangleRenderer.getInstance().isHalftoneRectanglePipeline(pipeline);
        this.byazen_zippyDrawActive = ZippyRenderer.getInstance().isZippyPipeline(pipeline);
        this.byazen_outlineDrawActive = DefaultOutlineRenderer.getInstance().isOutlinePipeline(pipeline);
        this.byazen_outline360DrawActive = Outline360Renderer.getInstance().isOutline360Pipeline(pipeline);
        this.byazen_imageDrawActive = ImageRenderer.getInstance().isImagePipeline(pipeline);
        this.byazen_lineDrawActive = LineRenderer.getInstance().isLinePipeline(pipeline);
        this.byazen_rippleDrawActive = RippleRenderer.getInstance().isRipplePipeline(pipeline);
        this.byazen_shimmerDrawActive = ShimmerRenderer.getInstance().isShimmerPipeline(pipeline);
        this.byazen_itemDrawActive = RenderItem.isItemPipeline(pipeline);
        renderPass.setPipeline(pipeline);
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V", shift = At.Shift.BEFORE), require = 0)
    private void byazen_bindBlurParams(CallbackInfo ci) {
        if (this.byazen_blurDrawActive && this.byazen_currentRenderPass != null) {
            BlurFramebuffer.getInstance().bindBlurParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_glassDrawActive && this.byazen_currentRenderPass != null) {
            GlassRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_shapeDrawActive && this.byazen_currentRenderPass != null) {
            ShapeRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_glowDrawActive && this.byazen_currentRenderPass != null) {
            GlowRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_glassOutlineDrawActive && this.byazen_currentRenderPass != null) {
            GlassOutlineRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_circleDrawActive && this.byazen_currentRenderPass != null) {
            CircleRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_arcDrawActive && this.byazen_currentRenderPass != null) {
            ArcRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_radialGlassDrawActive && this.byazen_currentRenderPass != null) {
            RadialGlassRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_sectorMaskDrawActive && this.byazen_currentRenderPass != null) {
            SectorMaskRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_pickerDrawActive && this.byazen_currentRenderPass != null) {
            PickerRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_rectangleDrawActive && this.byazen_currentRenderPass != null) {
            DefaultRectangleRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_halfIconRectangleDrawActive && this.byazen_currentRenderPass != null) {
            HalfIconRectangleRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_halftoneRectangleDrawActive && this.byazen_currentRenderPass != null) {
            HalftoneRectangleRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_zippyDrawActive && this.byazen_currentRenderPass != null) {
            ZippyRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_outlineDrawActive && this.byazen_currentRenderPass != null) {
            DefaultOutlineRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_outline360DrawActive && this.byazen_currentRenderPass != null) {
            Outline360Renderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_imageDrawActive && this.byazen_currentRenderPass != null) {
            ImageRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_lineDrawActive && this.byazen_currentRenderPass != null) {
            LineRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_rippleDrawActive && this.byazen_currentRenderPass != null) {
            RippleRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_shimmerDrawActive && this.byazen_currentRenderPass != null) {
            ShimmerRenderer.getInstance().bindParams(this.byazen_currentRenderPass);
        }
        if (this.byazen_itemDrawActive && this.byazen_currentRenderPass != null) {
            RenderItem.bindParams(this.byazen_currentRenderPass);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At("RETURN"), require = 0)
    private void byazen_clearTrackedPipeline(CallbackInfo ci) {
        this.byazen_currentRenderPass = null;
        this.byazen_blurDrawActive = false;
        this.byazen_glassDrawActive = false;
        this.byazen_shapeDrawActive = false;
        this.byazen_glowDrawActive = false;
        this.byazen_glassOutlineDrawActive = false;
        this.byazen_circleDrawActive = false;
        this.byazen_arcDrawActive = false;
        this.byazen_radialGlassDrawActive = false;
        this.byazen_sectorMaskDrawActive = false;
        this.byazen_pickerDrawActive = false;
        this.byazen_rectangleDrawActive = false;
        this.byazen_halfIconRectangleDrawActive = false;
        this.byazen_halftoneRectangleDrawActive = false;
        this.byazen_zippyDrawActive = false;
        this.byazen_outlineDrawActive = false;
        this.byazen_outline360DrawActive = false;
        this.byazen_imageDrawActive = false;
        this.byazen_lineDrawActive = false;
        this.byazen_itemDrawActive = false;
        this.byazen_rippleDrawActive = false;
        this.byazen_shimmerDrawActive = false;
    }
}
