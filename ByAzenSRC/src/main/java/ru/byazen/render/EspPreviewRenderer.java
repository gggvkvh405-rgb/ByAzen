/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10017
 *  net.minecraft.class_10042
 *  net.minecraft.class_10055
 *  net.minecraft.class_1041
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_1937
 *  net.minecraft.class_2487
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_3730
 *  net.minecraft.class_638
 *  net.minecraft.class_745
 *  net.minecraft.class_811
 *  net.minecraft.class_9279
 *  net.minecraft.class_9334
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 */
package ru.byazen.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_10055;
import net.minecraft.class_1041;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_1937;
import net.minecraft.class_2487;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3730;
import net.minecraft.class_638;
import net.minecraft.class_745;
import net.minecraft.class_811;
import net.minecraft.class_9279;
import net.minecraft.class_9334;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.byazen.ByAzenClient;
import ru.byazen.item.ItemBadge;
import ru.byazen.misc.CaptureFramebuffer;
import ru.byazen.misc.DeferredRenderTask;
import ru.byazen.misc.EntityEspOverlayRenderer;
import ru.byazen.misc.FrameInterpolator;
import ru.byazen.misc.GlowEspSettings;
import ru.byazen.misc.ModelEspSettings;
import ru.byazen.model.esp.EspRelation;
import ru.byazen.model.esp.EspTargetType;
import ru.byazen.render.ChamsPreviewEffect;
import ru.byazen.render.ChamsSettings;
import ru.byazen.render.GlowEspPreviewEffect;
import ru.byazen.render.OffscreenRenderManager;
import ru.byazen.render.SkeletonPreviewRenderer;
import ru.byazen.ui.GuiBounds;
import ru.byazen.util.EspFeatureRegistry;
import ru.byazen.util.GuiDrawApi;

public final class EspPreviewRenderer {
    private static final int MIN_PREVIEW_SIZE = 110;
    private static final int MAX_PREVIEW_SIZE = 640;
    private static final float MAX_PITCH = 35.0f;
    private static final float ROTATION_SENSITIVITY = 1.2f;
    private static final float ROTATION_RESET_SPEED = 8.0f;
    private static final String DONATION_TAG_KEY = "don-item";
    private static final String[] DONATION_PREFIXES = new String[]{"byazen:", "byazen_", "donate:"};
    private static final class_1304[] ARMOR_SLOTS = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
    private static final class_1799 ITEM_PREVIEW_STACK = new class_1799((class_1935)class_1802.field_8288);
    private double lastMouseX;
    private double lastMouseY;
    private final SkeletonPreviewRenderer skeletonPreviewRenderer;
    private final EntityEspOverlayRenderer entityEspOverlayRenderer;
    private final class_310 client = class_310.method_1551();
    private class_1297 mobPreviewEntity;
    private float requestedSize;
    private float pitch;
    private EspTargetType espTargetType;
    private final ChamsPreviewEffect chamsPreviewEffect;
    private boolean rendered;
    private boolean rotating;
    private final GlowEspPreviewEffect glowEspPreviewEffect;
    private int previewSize = 110;
    private int framebufferWidth;
    private class_638 cachedWorld;
    private class_1297 playerPreviewEntity;
    private final CaptureFramebuffer captureFramebuffer = new CaptureFramebuffer();
    private EspRelation espRelation;
    private int previewPixelSize;
    private float yaw;
    private int chamsTextureId;
    private int framebufferHeight;
    private int skeletonTextureId;
    private int glowTextureId;

    public EspPreviewRenderer() {
        this.entityEspOverlayRenderer = new EntityEspOverlayRenderer();
        this.chamsPreviewEffect = new ChamsPreviewEffect();
        this.glowEspPreviewEffect = new GlowEspPreviewEffect();
        this.skeletonPreviewRenderer = new SkeletonPreviewRenderer();
        this.espRelation = EspRelation.DEFAULT;
    }

    private static float clampPitch(float pitch) {
        return Math.max(-35.0f, Math.min(35.0f, pitch));
    }

    private void clearPreviewMetadata(class_10017 illiliIIiI2) {
        illiliIIiI2.field_53337 = null;
        illiliIIiI2.field_53338 = null;
        illiliIIiI2.field_61821 = 0;
        illiliIIiI2.field_53333 = false;
        illiliIIiI2.field_53335 = false;
        illiliIIiI2.field_61822 = 0.0f;
        illiliIIiI2.field_61823.clear();
        illiliIIiI2.field_61820 = 0xF000F0;
        if (illiliIIiI2 instanceof class_10055) {
            class_10055 playerEntityRenderState = (class_10055)illiliIIiI2;
            playerEntityRenderState.field_53532 = false;
        }
    }

    private Consumer<class_332> createEntityDrawTask(class_1297 member4759, float f, float f2) {
        float f3;
        float f4;
        if (member4759 == null) {
            return null;
        }
        class_10017 illiliIIiI2 = this.client.method_1561().method_72977(member4759, f);
        if (illiliIIiI2 == null) {
            return null;
        }
        this.clearPreviewMetadata(illiliIIiI2);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        if (illiliIIiI2 instanceof class_10042) {
            class_10042 livingEntityRenderState = (class_10042)illiliIIiI2;
            livingEntityRenderState.field_53446 = 180.0f + this.yaw;
            livingEntityRenderState.field_53447 = 0.0f;
            livingEntityRenderState.field_53448 = this.pitch;
            float f5 = illiliIIiI2.field_53330 <= 0.0f ? 1.8f : illiliIIiI2.field_53330;
            f4 = (float)this.previewSize * f2;
            f3 = f5 / 2.0f + 0.035f;
        } else {
            quaternionf.rotateY((float)Math.toRadians(this.yaw));
            f4 = (float)this.previewSize * 0.8f;
            f3 = 0.0f;
        }
        Vector3f translation = new Vector3f(0.0f, f3, 0.0f);
        Quaternionf quaternionf2 = new Quaternionf().rotateX((float)Math.toRadians(-this.pitch));
        return context -> context.method_70856(illiliIIiI2, f4, translation, quaternionf, quaternionf2, 0, 0, this.previewSize, this.previewSize);
    }

    private String getDonationLabel(EspTargetType espTargetType) {
        if ((espTargetType == EspTargetType.PLAYERS || espTargetType == EspTargetType.SELF) && this.client.field_1724 != null) {
            return this.readDonationItemId(this.client.field_1724.method_6079());
        }
        return null;
    }

    public void beginRotation(int n, int n2, GuiBounds bounds2) {
        if (bounds2.contains(n, n2)) {
            this.rotating = true;
            this.lastMouseX = this.getMouseX();
            this.lastMouseY = this.getMouseY();
        }
    }

    public void close() {
        this.rendered = false;
        this.captureFramebuffer.method_1238();
        this.entityEspOverlayRenderer.update();
        this.chamsPreviewEffect.releaseFramebuffers();
        this.glowEspPreviewEffect.releaseFramebuffers();
        this.skeletonPreviewRenderer.close();
        this.mobPreviewEntity = null;
        this.playerPreviewEntity = null;
        this.cachedWorld = null;
    }

    public void render(GuiDrawApi drawApi, Matrix4f matrix4f, GuiBounds bounds2) {
        int n;
        if (!this.rendered || this.captureFramebuffer.getIntType() <= 0) {
            return;
        }
        float f = Math.min(bounds2.getWidth(), bounds2.getHeight());
        float f2 = bounds2.getX() + (bounds2.getWidth() - f) / 2.0f;
        float f3 = bounds2.getY() + (bounds2.getHeight() - f) / 2.0f;
        float f4 = (float)this.previewPixelSize / (float)this.framebufferWidth;
        float f5 = 1.0f - (float)this.previewPixelSize / (float)this.framebufferHeight;
        EspTargetType espTargetType = this.espTargetType;
        if (this.glowTextureId != 0) {
            n = drawApi.bindTexture(this.glowTextureId, this.glowEspPreviewEffect.getWidth(), this.glowEspPreviewEffect.getHeight());
            drawApi.drawTexture(matrix4f, f2, f3, f, f, 0.0f, 1.0f, f4, f5, n, -1);
        }
        n = this.chamsTextureId != 0 ? this.chamsTextureId : this.captureFramebuffer.getIntType();
        int n2 = drawApi.bindTexture(n, this.framebufferWidth, this.framebufferHeight);
        drawApi.drawTexture(matrix4f, f2, f3, f, f, 0.0f, 1.0f, f4, f5, n2, -1);
        if (this.skeletonTextureId != 0) {
            int n3 = drawApi.bindTexture(this.skeletonTextureId, this.framebufferWidth, this.framebufferHeight);
            drawApi.drawTexture(matrix4f, f2, f3, f, f, 0.0f, 1.0f, f4, f5, n3, -1);
        }
        this.entityEspOverlayRenderer.process2(drawApi, matrix4f, this.calculateOverlayBounds(espTargetType, f2, f3, f), espTargetType, this.espRelation, this.getModelYaw(espTargetType), this.getDisplayName(espTargetType), this.getHealthValue(espTargetType), this.getEffectDuration(espTargetType), this.getHeldItemBadge(espTargetType), this.getDonationLabel(espTargetType));
    }

    public void requestPreviewRender(EspTargetType espTargetType, EspRelation espRelation, float f) {
        this.requestedSize = f;
        DeferredRenderTask.setRunnable(() -> this.renderPreview(espTargetType, espRelation));
    }

    public void updatePreviewRotation() {
        if (this.rotating && !this.isLeftMouseDown()) {
            this.rotating = false;
        }
        if (this.rotating) {
            double d = this.getMouseX();
            double d2 = this.getMouseY();
            this.yaw = EspPreviewRenderer.wrapYaw(this.yaw - (float)(d - this.lastMouseX) * 1.2f);
            this.pitch = EspPreviewRenderer.clampPitch(this.pitch + (float)(d2 - this.lastMouseY) * 1.2f);
            this.lastMouseX = d;
            this.lastMouseY = d2;
            return;
        }
        this.yaw = FrameInterpolator.lerpTowards(this.yaw, 0.0f, 8.0f);
        this.pitch = FrameInterpolator.lerpTowards(this.pitch, 0.0f, 8.0f);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderPreview(EspTargetType espTargetType, EspRelation espRelation) {
        Consumer<class_332> consumer;
        this.rendered = false;
        this.chamsTextureId = 0;
        this.glowTextureId = 0;
        this.skeletonTextureId = 0;
        this.espTargetType = espTargetType;
        EspRelation espRelation2 = this.espRelation = espRelation == null ? EspRelation.DEFAULT : espRelation;
        if (this.client.field_1687 == null || this.client.field_1724 == null) {
            return;
        }
        OffscreenRenderManager renderPipeline2 = ByAzenClient.getRenderPipeline2();
        if (renderPipeline2 == null) {
            return;
        }
        class_1041 window2 = this.client.method_22683();
        int n = window2.method_4489();
        int n2 = window2.method_4506();
        float f = window2.method_4495();
        this.updatePreviewSize(f, n, n2);
        int n3 = Math.round((float)this.previewSize * f);
        if (n <= 0 || n2 <= 0 || n3 <= 0 || n3 > n || n3 > n2) {
            return;
        }
        float f2 = this.client.method_61966().method_60637(true);
        Consumer<class_332> consumer2 = consumer = espTargetType == EspTargetType.ITEMS ? null : this.createPreviewDrawTask(espTargetType, f2);
        if (espTargetType != EspTargetType.ITEMS && consumer == null) {
            return;
        }
        int n4 = GL30.glGetInteger((int)36006);
        int[] nArray = new int[4];
        GL11.glGetIntegerv((int)2978, (int[])nArray);
        try {
            class_1297 member4759;
            ModelEspSettings modelEspSettings;
            GlowEspSettings glowEspSettings;
            ChamsSettings chamsSettings;
            if (espTargetType == EspTargetType.ITEMS) {
                renderPipeline2.process5(this.captureFramebuffer, n, n2, ITEM_PREVIEW_STACK, class_811.field_4317, (float)this.previewSize / 2.0f, (float)this.previewSize / 2.0f, this.getItemRenderScale(), -this.yaw, this.pitch);
            } else {
                renderPipeline2.process4(this.captureFramebuffer, n, n2, consumer);
            }
            this.previewPixelSize = n3;
            this.framebufferWidth = n;
            this.framebufferHeight = n2;
            this.rendered = true;
            this.entityEspOverlayRenderer.process(espTargetType, this.espRelation, this.getEquipment(espTargetType), f);
            EspFeatureRegistry espFeatures = ByAzenClient.getEspFeatureRegistry();
            ChamsSettings chamsSettings2 = chamsSettings = espFeatures != null && espTargetType == EspTargetType.PLAYERS ? espFeatures.getChamsSettings(this.espRelation) : null;
            if (chamsSettings != null && chamsSettings.isEnabled()) {
                this.chamsTextureId = this.chamsPreviewEffect.render(this.captureFramebuffer, n, n2, n3, chamsSettings);
            }
            GlowEspSettings glowEspSettings2 = glowEspSettings = espFeatures != null && espTargetType == EspTargetType.PLAYERS ? espFeatures.getGlowSettings(this.espRelation) : null;
            if (glowEspSettings != null && glowEspSettings.isEnabled()) {
                this.glowTextureId = this.glowEspPreviewEffect.render(this.captureFramebuffer, n, n2, glowEspSettings);
            }
            ModelEspSettings modelEspSettings2 = modelEspSettings = espFeatures != null && espTargetType == EspTargetType.PLAYERS ? espFeatures.getModelEspSettings(this.espRelation) : null;
            if (modelEspSettings != null && modelEspSettings.isEnabled() && (member4759 = this.getPlayerPreviewEntity()) instanceof class_1657) {
                class_1657 player2 = (class_1657)member4759;
                this.skeletonTextureId = this.skeletonPreviewRenderer.render(renderPipeline2, player2, n, n2, (float)this.previewSize / 2.0f, (float)this.previewSize / 2.0f, (float)this.previewSize * 0.4f, -0.95f, 180.0f + this.yaw, modelEspSettings);
            }
        }
        catch (RuntimeException runtimeException) {
        }
        finally {
            GL30.glBindFramebuffer((int)36160, (int)n4);
            GL11.glViewport((int)nArray[0], (int)nArray[1], (int)nArray[2], (int)nArray[3]);
        }
    }

    private ItemBadge getHeldItemBadge(EspTargetType espTargetType) {
        if ((espTargetType == EspTargetType.PLAYERS || espTargetType == EspTargetType.SELF) && this.client.field_1724 != null) {
            ItemBadge itemBadge = ItemBadge.fromStack(this.client.field_1724.method_6047());
            return itemBadge != null ? itemBadge : ItemBadge.fromStack(this.client.field_1724.method_6079());
        }
        return null;
    }

    private String getDisplayName(EspTargetType espTargetType) {
        return switch (espTargetType == null ? EspTargetType.PLAYERS : espTargetType) {
            case EspTargetType.ENTITIES -> {
                if (this.mobPreviewEntity != null) {
                    yield this.mobPreviewEntity.method_5477().getString();
                }
                yield "Entity";
            }
            case EspTargetType.ITEMS -> ITEM_PREVIEW_STACK.method_7964().getString();
            default -> this.client.field_1724 != null ? this.client.field_1724.method_5477().getString() : "Player";
        };
    }

    private class_1297 getPlayerPreviewEntity() {
        if (this.client.field_1687 == null || this.client.field_1724 == null) {
            return null;
        }
        if (this.playerPreviewEntity != null && this.cachedWorld == this.client.field_1687) {
            return this.playerPreviewEntity;
        }
        try {
            class_745 otherClientPlayerEntity = new class_745(this.client.field_1687, this.client.field_1724.method_7334());
            otherClientPlayerEntity.method_5719((class_1297)this.client.field_1724);
            otherClientPlayerEntity.method_5673(class_1304.field_6169, EspPreviewRenderer.withEnchantmentGlint(new class_1799((class_1935)class_1802.field_8805)));
            otherClientPlayerEntity.method_5673(class_1304.field_6174, EspPreviewRenderer.withEnchantmentGlint(new class_1799((class_1935)class_1802.field_8058)));
            otherClientPlayerEntity.method_5673(class_1304.field_6172, EspPreviewRenderer.withEnchantmentGlint(new class_1799((class_1935)class_1802.field_8348)));
            otherClientPlayerEntity.method_5673(class_1304.field_6166, EspPreviewRenderer.withEnchantmentGlint(new class_1799((class_1935)class_1802.field_8285)));
            otherClientPlayerEntity.method_5673(class_1304.field_6173, EspPreviewRenderer.withEnchantmentGlint(new class_1799((class_1935)class_1802.field_8802)));
            otherClientPlayerEntity.method_5673(class_1304.field_6171, EspPreviewRenderer.withEnchantmentGlint(new class_1799((class_1935)class_1802.field_8367)));
            this.playerPreviewEntity = otherClientPlayerEntity;
            this.cachedWorld = this.client.field_1687;
        }
        catch (RuntimeException runtimeException) {
            return null;
        }
        return this.playerPreviewEntity;
    }

    private Consumer<class_332> createPreviewDrawTask(EspTargetType espTargetType, float f) {
        return switch (espTargetType) {
            default -> throw new MatchException(null, null);
            case EspTargetType.PLAYERS, EspTargetType.SELF -> this.createEntityDrawTask(this.getPlayerPreviewEntity(), f, 0.4f);
            case EspTargetType.ENTITIES -> this.createEntityDrawTask(this.getMobPreviewEntity(), f, 0.42f);
            case EspTargetType.ITEMS -> null;
        };
    }

    private List<class_1799> getEquipment(EspTargetType espTargetType) {
        if (espTargetType == EspTargetType.PLAYERS || espTargetType == EspTargetType.SELF) {
            class_1297 preview = this.getPlayerPreviewEntity();
            if (!(preview instanceof class_1309)) {
                return List.of();
            }
            class_1309 entity2 = (class_1309)preview;
            ArrayList<class_1799> equipment = new ArrayList<class_1799>();
            equipment.add(entity2.method_6047());
            equipment.add(entity2.method_6079());
            for (class_1304 iliiIIiliI2 : ARMOR_SLOTS) {
                equipment.add(entity2.method_6118(iliiIIiliI2));
            }
            equipment.removeIf(class_1799::method_7960);
            return equipment;
        }
        return List.of();
    }

    private double getMouseX() {
        class_1041 window2 = this.client.method_22683();
        return this.client.field_1729.method_68879(window2);
    }

    private String readDonationItemId(class_1799 stack2) {
        class_9279 customData = (class_9279)stack2.method_58694(class_9334.field_49628);
        if (customData == null || customData.method_57458()) {
            return null;
        }
        class_2487 iiililiiII2 = customData.method_57461();
        if (!iiililiiII2.method_10545(DONATION_TAG_KEY)) {
            return null;
        }
        String string = iiililiiII2.method_68564(DONATION_TAG_KEY, "");
        for (String string2 : DONATION_PREFIXES) {
            if (!string.startsWith(string2)) continue;
            return string.substring(string2.length()).toUpperCase();
        }
        return null;
    }

    private float getModelYaw(EspTargetType espTargetType) {
        return espTargetType == EspTargetType.ITEMS ? -this.yaw : this.yaw;
    }

    private GuiBounds calculateOverlayBounds(EspTargetType espTargetType, float f, float f2, float f3) {
        float f4;
        float f5;
        float f6;
        float f7 = switch (espTargetType == null ? EspTargetType.PLAYERS : espTargetType) {
            default -> throw new MatchException(null, null);
            case EspTargetType.ENTITIES -> {
                f6 = 0.3f;
                f5 = 0.3f;
                f4 = 0.17f;
                yield 0.04f;
            }
            case EspTargetType.ITEMS -> {
                f6 = 0.24f;
                f5 = 0.24f;
                f4 = 0.2f;
                yield 0.2f;
            }
            case EspTargetType.PLAYERS, EspTargetType.SELF -> {
                f6 = 0.34f;
                f5 = 0.34f;
                f4 = 0.05f;
                yield 0.11f;
            }
        };
        float f8 = f + f6 * f3;
        float f9 = f2 + f4 * f3;
        float f10 = f3 * (1.0f - f6 - f5);
        float f11 = f3 * (1.0f - f4 - f7);
        return new GuiBounds(f8, f9, f10, f11);
    }

    private boolean isLeftMouseDown() {
        return GLFW.glfwGetMouseButton((long)this.client.method_22683().method_4490(), (int)0) == 1;
    }

    private int getEffectDuration(EspTargetType espTargetType) {
        return espTargetType == EspTargetType.ENTITIES ? 2000 : 0;
    }

    private float getItemRenderScale() {
        return (float)this.previewSize * 0.7f;
    }

    private static class_1799 withEnchantmentGlint(class_1799 stack2) {
        stack2.method_57379(class_9334.field_49641, Boolean.valueOf(true));
        return stack2;
    }

    private class_1297 getMobPreviewEntity() {
        if (this.mobPreviewEntity == null && this.client.field_1687 != null) {
            try {
                this.mobPreviewEntity = class_1299.field_6051.method_5883((class_1937)this.client.field_1687, class_3730.field_16462);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        return this.mobPreviewEntity;
    }

    private double getMouseY() {
        class_1041 window2 = this.client.method_22683();
        return this.client.field_1729.method_68883(window2);
    }

    private int getHealthValue(EspTargetType espTargetType) {
        return switch (espTargetType == null ? EspTargetType.PLAYERS : espTargetType) {
            default -> throw new MatchException(null, null);
            case EspTargetType.ENTITIES -> 20;
            case EspTargetType.ITEMS -> -1;
            case EspTargetType.PLAYERS, EspTargetType.SELF -> this.client.field_1724 != null ? Math.round(this.client.field_1724.method_6032()) : 20;
        };
    }

    private void updatePreviewSize(float f, int n, int n2) {
        int n3 = f <= 0.0f ? 110 : Math.round(this.requestedSize * 2.0f / f);
        int n4 = f <= 0.0f ? 640 : (int)Math.floor((float)Math.min(n, n2) / f);
        this.previewSize = Math.max(110, Math.min(Math.min(640, n4), n3));
    }

    private static float wrapYaw(float f) {
        float f2 = f % 360.0f;
        if (f2 > 180.0f) {
            f2 -= 360.0f;
        }
        if (f2 < -180.0f) {
            f2 += 360.0f;
        }
        return f2;
    }
}

