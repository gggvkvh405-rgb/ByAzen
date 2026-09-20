package rtx.byazen.api.mods.waveycapes;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.PlayerLikeEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector4f;
import rtx.byazen.api.mods.waveycapes.CapeRenderer;
import rtx.byazen.api.mods.waveycapes.NMSUtil;
import rtx.byazen.api.mods.waveycapes.VanillaCapeRenderer;
import rtx.byazen.api.mods.waveycapes.WaveyCapesBase;
import rtx.byazen.api.mods.waveycapes.compat.MathUtil;
import rtx.byazen.api.mods.waveycapes.compat.PlayerWrapper;
import rtx.byazen.api.mods.waveycapes.compat.VertexConsumerUtil;
import rtx.byazen.api.mods.waveycapes.support.ModSupport;
import rtx.byazen.api.mods.waveycapes.support.SupportManager;
import rtx.byazen.api.mods.waveycapes.versionless.CapeHolder;
import rtx.byazen.api.mods.waveycapes.versionless.CapeMovement;
import rtx.byazen.api.mods.waveycapes.versionless.CapeStyle;
import rtx.byazen.api.mods.waveycapes.versionless.ModBase;
import rtx.byazen.api.mods.waveycapes.versionless.sim.BasicSimulation;
import rtx.byazen.api.mods.waveycapes.versionless.util.Vector3;
import rtx.byazen.api.mods.waveycapes.versionless.util.Vector4;
import rtx.byazen.api.modules.impl.Visuals.BetterMinecraft;

public class CustomCapeRenderer {
    private static final float CAPE_WAVE_SPEED = 0.5f;
    private static final float CAPE_WAVE_STRENGTH = 3.5f;
    private static final int PART_COUNT = 16;
    private final ModelPart[] customCape = NMSUtil.buildCape(64, 64, n -> 0, n -> n);
    private static final float CAPE_WIDTH = 0.625f;
    private static final float CAPE_HEIGHT = 1.0f;
    private static final float CAPE_DEPTH = 0.0625f;
    private static VanillaCapeRenderer vanillaCape = new VanillaCapeRenderer();

    private static Vector4 transform(Matrix4f matrix4f, Vector4 vector4) {
        Vector4f vector4f = matrix4f.transform(new Vector4f(vector4.x, vector4.y, vector4.z, vector4.w));
        return new Vector4(vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    private double getAngle(Vector3 vector3, Vector3 vector32) {
        Vector3 vector33 = vector32.subtract(vector3);
        return Math.toDegrees(Math.atan2(vector33.x, vector33.y)) + 180.0;
    }

    private boolean hasSimulation(PlayerWrapper playerWrapper) {
        if (ModBase.simulationBroken) {
            return false;
        }
        BasicSimulation basicSimulation = ((CapeHolder)playerWrapper.getAvatar()).getSimulation();
        return basicSimulation != null && !basicSimulation.empty();
    }

    private CapeRenderer getCapeRenderer(PlayerWrapper playerWrapper) {
        for (ModSupport modSupport : SupportManager.getSupportedMods()) {
            if (!modSupport.shouldBeUsed(playerWrapper)) continue;
            return modSupport.getRenderer();
        }
        if (playerWrapper.getCapeTexture() == null || !playerWrapper.isCapeVisible()) {
            return null;
        }
        return vanillaCape;
    }

    private void renderSmoothCape(MatrixStack matrixStack, VertexConsumer vertexConsumer, PlayerWrapper playerWrapper, float f, int n) {
        int n2;
        float f2 = SupportManager.getAlphaSupplier().get().floatValue();
        boolean bl = playerWrapper.isLocalPlayer();
        Matrix4f[] matrix4fArray = new Matrix4f[16];
        Vector3[] vector3Array = new Vector3[16];
        Vector3[] vector3Array2 = new Vector3[16];
        for (n2 = 0; n2 < 16; ++n2) {
            this.modifyPoseStack(matrixStack, playerWrapper, f, n2);
            matrix4fArray[n2] = new Matrix4f((Matrix4fc)matrixStack.peek().getPositionMatrix());
            vector3Array[n2] = CustomCapeRenderer.getNormalVec(matrix4fArray[Math.max(n2 - 1, 0)], matrix4fArray[Math.max(n2 - 1, 0)], matrix4fArray[n2], new Vector3(0.3125f, (float)n2 * 0.0625f, -0.0625f), new Vector3(-0.3125f, (float)n2 * 0.0625f, -0.0625f), new Vector3(0.3125f, (float)(n2 + 1) * 0.0625f, -0.0625f), n == 0xF000F0);
            vector3Array2[n2] = CustomCapeRenderer.getNormalVec(matrix4fArray[Math.max(n2 - 1, 0)], matrix4fArray[Math.max(n2 - 1, 0)], matrix4fArray[n2], new Vector3(0.3125f, (float)(n2 + 1) * 0.0625f, 0.0f), new Vector3(-0.3125f, (float)(n2 + 1) * 0.0625f, 0.0f), new Vector3(0.3125f, (float)n2 * 0.0625f, 0.0f), n == 0xF000F0);
            matrixStack.pop();
        }
        for (n2 = 0; n2 < 16; ++n2) {
            Vector3 vector3;
            float f3;
            float f4;
            float f5;
            float f6;
            if (n2 == 0) {
                f6 = 0.015625f;
                f5 = 0.171875f;
                f4 = 0.0f;
                f3 = 0.03125f;
                vector3 = CustomCapeRenderer.getNormalVec(matrix4fArray[0], matrix4fArray[0], matrix4fArray[0], new Vector3(0.3125f, 0.0f, 0.0f), new Vector3(-0.3125f, 0.0f, 0.0f), new Vector3(0.3125f, 0.0f, 0.0625f), n == 0xF000F0);
                VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[0], (float)0.3125f, (float)0.0f, (float)0.0f, (float)f5, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
                VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[0], (float)-0.3125f, (float)0.0f, (float)0.0f, (float)f6, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
                VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[0], (float)-0.3125f, (float)0.0f, (float)-0.0625f, (float)f6, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
                VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[0], (float)0.3125f, (float)0.0f, (float)-0.0625f, (float)f5, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            }
            if (n2 == 15) {
                f6 = 0.171875f;
                f5 = 0.328125f;
                f4 = 0.0f;
                f3 = 0.03125f;
                vector3 = CustomCapeRenderer.getNormalVec(matrix4fArray[n2], matrix4fArray[n2], matrix4fArray[n2], new Vector3(0.3125f, 1.0f, -0.0625f), new Vector3(-0.3125f, 1.0f, -0.0625f), new Vector3(0.3125f, 1.0f, 0.0f), n == 0xF000F0);
                VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)0.3125f, (float)1.0f, (float)-0.0625f, (float)f5, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
                VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)-0.3125f, (float)1.0f, (float)-0.0625f, (float)f6, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
                VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)-0.3125f, (float)1.0f, (float)0.0f, (float)f6, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
                VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)0.3125f, (float)1.0f, (float)0.0f, (float)f5, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            }
            f6 = 0.0f;
            f5 = 0.015625f;
            f4 = 0.03125f * (float)(n2 + 1);
            f3 = f4 + 0.03125f;
            vector3 = CustomCapeRenderer.getNormalVec(matrix4fArray[n2], matrix4fArray[n2], matrix4fArray[Math.max(n2 - 1, 0)], new Vector3(-0.3125f, (float)(n2 + 1) * 0.0625f, 0.0f), new Vector3(-0.3125f, (float)(n2 + 1) * 0.0625f, -0.0625f), new Vector3(-0.3125f, (float)n2 * 0.0625f, 0.0f), n == 0xF000F0);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)-0.3125f, (float)((float)(n2 + 1) * 0.0625f), (float)0.0f, (float)f6, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)-0.3125f, (float)((float)(n2 + 1) * 0.0625f), (float)-0.0625f, (float)f5, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[Math.max(n2 - 1, 0)], (float)-0.3125f, (float)((float)n2 * 0.0625f), (float)-0.0625f, (float)f5, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[Math.max(n2 - 1, 0)], (float)-0.3125f, (float)((float)n2 * 0.0625f), (float)0.0f, (float)f6, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            f6 = 0.171875f;
            f5 = 0.1875f;
            vector3 = CustomCapeRenderer.getNormalVec(matrix4fArray[n2], matrix4fArray[n2], matrix4fArray[Math.max(n2 - 1, 0)], new Vector3(0.3125f, (float)(n2 + 1) * 0.0625f, -0.0625f), new Vector3(0.3125f, (float)(n2 + 1) * 0.0625f, 0.0f), new Vector3(0.3125f, (float)n2 * 0.0625f, -0.0625f), n == 0xF000F0);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)0.3125f, (float)((float)(n2 + 1) * 0.0625f), (float)-0.0625f, (float)f6, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)0.3125f, (float)((float)(n2 + 1) * 0.0625f), (float)0.0f, (float)f5, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[Math.max(n2 - 1, 0)], (float)0.3125f, (float)((float)n2 * 0.0625f), (float)0.0f, (float)f5, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[Math.max(n2 - 1, 0)], (float)0.3125f, (float)((float)n2 * 0.0625f), (float)-0.0625f, (float)f6, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector3.x, (float)vector3.y, (float)vector3.z, (float)f2, (boolean)bl);
            f6 = 0.015625f;
            f5 = 0.171875f;
            Vector3 vector32 = ((Vector3)vector3Array[n2].clone()).add(vector3Array[Math.max(n2 - 1, 0)]).div(2.0f);
            Vector3 vector33 = ((Vector3)vector3Array[n2].clone()).add(vector3Array[Math.min(n2 + 1, 15)]).div(2.0f);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[Math.max(n2 - 1, 0)], (float)0.3125f, (float)((float)n2 * 0.0625f), (float)-0.0625f, (float)f5, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector32.x, (float)vector32.y, (float)vector32.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[Math.max(n2 - 1, 0)], (float)-0.3125f, (float)((float)n2 * 0.0625f), (float)-0.0625f, (float)f6, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector32.x, (float)vector32.y, (float)vector32.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)-0.3125f, (float)((float)(n2 + 1) * 0.0625f), (float)-0.0625f, (float)f6, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector33.x, (float)vector33.y, (float)vector33.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)0.3125f, (float)((float)(n2 + 1) * 0.0625f), (float)-0.0625f, (float)f5, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector33.x, (float)vector33.y, (float)vector33.z, (float)f2, (boolean)bl);
            f6 = 0.1875f;
            f5 = 0.34375f;
            vector32 = ((Vector3)vector3Array2[n2].clone()).add(vector3Array2[Math.max(n2 - 1, 0)]).div(2.0f);
            vector33 = ((Vector3)vector3Array2[n2].clone()).add(vector3Array2[Math.min(n2 + 1, 15)]).div(2.0f);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[Math.max(n2 - 1, 0)], (float)0.3125f, (float)((float)n2 * 0.0625f), (float)0.0f, (float)f6, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector32.x, (float)vector32.y, (float)vector32.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[Math.max(n2 - 1, 0)], (float)-0.3125f, (float)((float)n2 * 0.0625f), (float)0.0f, (float)f5, (float)f4, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector32.x, (float)vector32.y, (float)vector32.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)-0.3125f, (float)((float)(n2 + 1) * 0.0625f), (float)0.0f, (float)f5, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector33.x, (float)vector33.y, (float)vector33.z, (float)f2, (boolean)bl);
            VertexConsumerUtil.addVertex((VertexConsumer)vertexConsumer, (Matrix4f)matrix4fArray[n2], (float)0.3125f, (float)((float)(n2 + 1) * 0.0625f), (float)0.0f, (float)f6, (float)f3, (int)OverlayTexture.DEFAULT_UV, (int)n, (float)vector33.x, (float)vector33.y, (float)vector33.z, (float)f2, (boolean)bl);
        }
    }

    private float getNatrualWindSwing(int n, boolean bl) {
        if (!BetterMinecraft.capeWavesEnabled()) {
            return 0.0f;
        }
        long l = (long)((double)System.currentTimeMillis() / (bl ? 9.0 : 3.0) * 0.5) % 360L;
        float f = (float)(n + 1) / 16.0f;
        return (float)(Math.sin(Math.toRadians(f * 360.0f - (float)l)) * 3.5);
    }

    private static MatrixStack poseStackFrom(MatrixStack.Entry entry) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().copy(entry);
        return matrixStack;
    }

    private void modifyPoseStack(MatrixStack matrixStack, PlayerWrapper playerWrapper, float f, int n) {
        if (WaveyCapesBase.config.capeMovement != CapeMovement.VANILLA && this.hasSimulation(playerWrapper)) {
            this.modifyPoseStackSimulation(matrixStack, playerWrapper, f, n);
            return;
        }
        PlayerEntityRenderState playerEntityRenderState = playerWrapper.getRenderState();
        matrixStack.push();
        matrixStack.translate(0.0, 0.0, 0.125);
        PlayerLikeEntity playerLikeEntity = playerWrapper.getAvatar();
        matrixStack.multiply((Quaternionfc)MathUtil.XP.rotationDegrees(6.0f + playerEntityRenderState.field_53537 / 2.0f + playerEntityRenderState.field_53536 + this.getNatrualWindSwing(n, playerLikeEntity.isSubmergedInWater())));
        matrixStack.multiply((Quaternionfc)MathUtil.ZP.rotationDegrees(playerEntityRenderState.field_53538 / 2.0f));
        matrixStack.multiply((Quaternionfc)MathUtil.YP.rotationDegrees(180.0f - playerEntityRenderState.field_53538 / 2.0f));
    }

    private static Vector3 getNormalVec(Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3, Vector3 vector3, Vector3 vector32, Vector3 vector33, boolean bl) {
        Vector3 vector34 = CustomCapeRenderer.transform(matrix4f, new Vector4(vector3.x, vector3.y, vector3.z, 1.0f)).toVec3();
        Vector3 vector35 = CustomCapeRenderer.transform(matrix4f2, new Vector4(vector32.x, vector32.y, vector32.z, 1.0f)).toVec3();
        Vector3 vector36 = CustomCapeRenderer.transform(matrix4f3, new Vector4(vector33.x, vector33.y, vector33.z, 1.0f)).toVec3();
        vector35.subtract(vector34);
        vector36.subtract(vector34);
        vector35.cross(vector36);
        vector35.normalize();
        return bl ? vector35.mul(-1.0f) : vector35;
    }

    private void modifyPoseStackSimulation(MatrixStack matrixStack, PlayerWrapper playerWrapper, float f, int n) {
        PlayerLikeEntity playerLikeEntity = playerWrapper.getAvatar();
        BasicSimulation basicSimulation = ((CapeHolder)playerLikeEntity).getSimulation();
        matrixStack.push();
        matrixStack.translate(0.0, 0.0, 0.125);
        float f2 = basicSimulation.getPoints().get(n).getLerpX(f) - basicSimulation.getPoints().get(0).getLerpX(f);
        if (f2 > 0.0f) {
            f2 = 0.0f;
        }
        float f3 = basicSimulation.getPoints().get(0).getLerpY(f) - (float)n - basicSimulation.getPoints().get(n).getLerpY(f);
        float f4 = basicSimulation.getPoints().get(0).getLerpZ(f) - basicSimulation.getPoints().get(n).getLerpZ(f);
        float f5 = 0.0f;
        float f6 = this.getRotation(f, n, basicSimulation);
        float f7 = 0.0f;
        float f8 = this.getNatrualWindSwing(n, playerLikeEntity.isSubmergedInWater());
        matrixStack.multiply((Quaternionfc)MathUtil.XP.rotationDegrees(6.0f + f7 + f8));
        matrixStack.multiply((Quaternionfc)MathUtil.ZP.rotationDegrees(f5 / 2.0f));
        matrixStack.multiply((Quaternionfc)MathUtil.YP.rotationDegrees(180.0f - f5 / 2.0f));
        matrixStack.translate(-f4 / 16.0f, f3 / 16.0f, f2 / 16.0f);
        matrixStack.translate(0.0, 0.03, -0.03);
        matrixStack.translate(0.0f, (float)n * 1.0f / 16.0f, (float)(n * 0 / 16));
        matrixStack.multiply((Quaternionfc)MathUtil.XP.rotationDegrees(-f6));
        matrixStack.translate(0.0f, (float)(-n) * 1.0f / 16.0f, (float)(-n * 0 / 16));
        matrixStack.translate(0.0, -0.03, 0.03);
    }

    private boolean prepareCape(PlayerWrapper playerWrapper) {
        CapeHolder capeHolder = (CapeHolder)playerWrapper.getAvatar();
        if (capeHolder == null) {
            return false;
        }
        if (ModBase.simulationBroken) {
            return true;
        }
        try {
            capeHolder.updateSimulation(16);
        }
        catch (Throwable throwable) {
            ModBase.simulationBroken = true;
            ModBase.LOGGER.error("[WaveyCapes] Cape simulation failed and was disabled for this session", throwable);
        }
        return true;
    }

    public void render(PlayerWrapper playerWrapper, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int n, float f) {
        CapeRenderer capeRenderer = this.getCapeRenderer(playerWrapper);
        if (capeRenderer == null) {
            return;
        }
        if (!this.prepareCape(playerWrapper)) {
            return;
        }
        RenderLayer renderLayer = capeRenderer.getRenderType(playerWrapper);
        if (renderLayer == null) {
            return;
        }
        boolean bl = ModBase.config.capeStyle == CapeStyle.SMOOTH && capeRenderer.vanillaUvValues();
        orderedRenderCommandQueue.submitCustom(matrixStack, renderLayer, (entry, vertexConsumer) -> {
            MatrixStack renderPoseStack = CustomCapeRenderer.poseStackFrom(entry);
            if (bl) {
                this.renderSmoothCape(renderPoseStack, vertexConsumer, playerWrapper, f, n);
            } else {
                ModelPart[] modelPartArray = this.customCape;
                for (int i = 0; i < 16; ++i) {
                    ModelPart modelPart = modelPartArray[i];
                    this.modifyPoseStack(renderPoseStack, playerWrapper, f, i);
                    capeRenderer.render(playerWrapper, i, modelPart, renderPoseStack, vertexConsumer, n, OverlayTexture.DEFAULT_UV);
                    renderPoseStack.pop();
                }
            }
        });
    }

    private float getRotation(float f, int n, BasicSimulation basicSimulation) {
        if (n == 15) {
            return this.getRotation(f, n - 1, basicSimulation);
        }
        return (float)this.getAngle(basicSimulation.getPoints().get(n).getLerpedPos(f), basicSimulation.getPoints().get(n + 1).getLerpedPos(f));
    }
}

