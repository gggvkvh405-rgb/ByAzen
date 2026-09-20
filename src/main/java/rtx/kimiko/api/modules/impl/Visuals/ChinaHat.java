package rtx.kimiko.api.modules.impl.Visuals;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import rtx.kimiko.api.events.impl.render.WorldRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.color.RainbowLut;
import rtx.kimiko.utils.render.pipeline.ClientPipelines;
import rtx.kimiko.utils.render.render2d.ClientPalette;
import rtx.kimiko.utils.storage.friend.FriendUtils;

public class ChinaHat
extends Module {
    private static final int SEGMENTS = 144;
    private static final int OUTLINE_CROSS_SIDES = 16;
    private static final float TAU = (float)Math.PI * 2;
    private static final int DARK_SECOND_COLOR = new Color(16, 16, 16, 75).getRGB();
    private static final long TRANSFORM_TTL_MS = 250L;
    private static final float HAT_SCALE = 1.0f;
    private static final float OUTLINE_WIDTH = 2.5f;
    private static final String MODE_SELF = "\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u0431\u044f";
    private static final String MODE_FRIENDS = "\u0421\u0435\u0431\u044f \u0438 \u0434\u0440\u0443\u0437\u0435\u0439";
    private static final String MODE_ALL = "\u0412\u0441\u0435\u0445";
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private static ChinaHat instance;
    private final ModeSetting renderMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u043e\u0442\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u044f", "\u041d\u0430 \u043a\u043e\u043c \u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c \u0448\u043b\u044f\u043f\u0443.", "\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u0431\u044f", "\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u0431\u044f", "\u0421\u0435\u0431\u044f \u0438 \u0434\u0440\u0443\u0437\u0435\u0439", "\u0412\u0441\u0435\u0445"));
    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442"));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430 \u0448\u043b\u044f\u043f\u044b.", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u0432\u043e\u0439"));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0432 \u0440\u0435\u0436\u0438\u043c\u0435 \u00ab\u0421\u0432\u043e\u0439\u00bb.", false));
    private final ColorSetting customColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 \u0448\u043b\u044f\u043f\u044b.", new Color(255, 255, 255, 255)));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0448\u043b\u044f\u043f\u044b.", new Color(ColorUtil.lerpColor(-1, DARK_SECOND_COLOR, 0.7f), true)));
    private final Map<Integer, HatTransform> hatTransforms = new HashMap<Integer, HatTransform>();

    public ChinaHat() {
        super("China Hat", "\u0420\u0438\u0441\u0443\u0435\u0442 \u043a\u0438\u0442\u0430\u0439\u0441\u043a\u0443\u044e \u0448\u043b\u044f\u043f\u0443-\u043a\u043e\u043d\u0443\u0441 \u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u0430\u0445.", Category.VISUALS);
        instance = this;
        this.useSecondColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
        this.customColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
        this.customSecondColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM) && this.useSecondColor.getValue());
    }

    public static ChinaHat getInstance() {
        ChinaHat chinaHat = ModuleManager.get().get(ChinaHat.class);
        return chinaHat != null ? chinaHat : instance;
    }

    private static int rainbow(int n, int n2, float f, float f2, float f3) {
        int n3 = (int)((System.currentTimeMillis() / (long)Math.max(1, n) + (long)n2) % 360L);
        int n4 = RainbowLut.sample((int)n3, (float)f, (float)f2);
        return ColorUtil.rgba(n4 >>> 16 & 0xFF, n4 >>> 8 & 0xFF, n4 & 0xFF, Math.round(MathHelper.clamp((float)f3, (float)0.0f, (float)1.0f) * 255.0f));
    }

    private static int fade(int n, int n2, int n3, int n4) {
        int n5 = (int)((System.currentTimeMillis() / (long)Math.max(1, n) + (long)n2) % 360L);
        n5 = n5 >= 180 ? 360 - n5 : n5;
        return ColorUtil.lerpColor(n3, n4, (float)n5 / 180.0f);
    }

    @Override
    protected void onDisable() {
        this.hatTransforms.clear();
    }

    private boolean hasVisibleHelmet(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void renderAfterPostEffects(WorldRenderEvent worldRenderEvent) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        float f = worldRenderEvent.getPartialTicks();
        Vec3d vec3d = worldRenderEvent.getCamera() != null ? worldRenderEvent.getCamera().getCameraPos() : this.mc.gameRenderer.getCamera().getCameraPos();
        MatrixStack matrixStack = worldRenderEvent.getStack();
        VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer = immediate.getBuffer(ClientPipelines.CHINA_HAT);
        long l = System.currentTimeMillis();
        boolean bl = false;
        for (PlayerEntity playerEntity : this.mc.world.getPlayers()) {
            HatTransform hatTransform;
            AbstractClientPlayerEntity abstractClientPlayerEntity;
            if (!(playerEntity instanceof AbstractClientPlayerEntity) || !this.shouldRender(abstractClientPlayerEntity = (AbstractClientPlayerEntity)playerEntity) || abstractClientPlayerEntity.isInvisible() || abstractClientPlayerEntity.isSpectator() || abstractClientPlayerEntity.isBaby() || abstractClientPlayerEntity == this.mc.player && this.mc.options.getPerspective() == Perspective.FIRST_PERSON || (hatTransform = this.hatTransforms.get(abstractClientPlayerEntity.getId())) == null || l - hatTransform.time > 250L) continue;
            Vec3d vec3d2 = abstractClientPlayerEntity.getLerpedPos(f);
            matrixStack.push();
            matrixStack.translate(vec3d2.x - vec3d.x, vec3d2.y - vec3d.y, vec3d2.z - vec3d.z);
            matrixStack.multiplyPositionMatrix((Matrix4fc)hatTransform.matrix);
            this.renderHat(matrixStack, vertexConsumer, abstractClientPlayerEntity);
            matrixStack.pop();
            bl = true;
        }
        if (bl) {
            // RenderPipeline owns GL state in current Minecraft versions.  Direct
            // legacy GL11 state changes cause failures with core-profile drivers.
            immediate.draw(ClientPipelines.CHINA_HAT);
        }
    }

    private void emitOutlineTube(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float f2, float f3) {
        float[] fArray = new float[16];
        float[] fArray2 = new float[16];
        float[] fArray3 = new float[16];
        float[] fArray4 = new float[16];
        float[] fArray5 = new float[16];
        float[] fArray6 = new float[16];
        boolean bl = false;
        for (int i = 0; i <= 144; ++i) {
            int n;
            float f4 = (float)i / 144.0f;
            float f5 = f4 * ((float)Math.PI * 2);
            float f6 = MathHelper.sin((double)f5);
            float f7 = -MathHelper.cos((double)f5);
            float f8 = f6 * f;
            float f9 = f7 * f;
            int n2 = this.modeColor((int)(f4 * 720.0f), 1.0f);
            for (n = 0; n < 16; ++n) {
                float f10 = (float)Math.PI * 2 * (float)n / 16.0f;
                float f11 = MathHelper.cos((double)f10);
                float f12 = MathHelper.sin((double)f10);
                fArray4[n] = f8 + f3 * f11 * f6;
                fArray5[n] = f2 + f3 * f12;
                fArray6[n] = f9 + f3 * f11 * f7;
            }
            if (bl) {
                for (n = 0; n < 16; ++n) {
                    int n3 = (n + 1) % 16;
                    vertexConsumer.vertex(entry, fArray[n], fArray2[n], fArray3[n]).color(n2);
                    vertexConsumer.vertex(entry, fArray[n3], fArray2[n3], fArray3[n3]).color(n2);
                    vertexConsumer.vertex(entry, fArray4[n3], fArray5[n3], fArray6[n3]).color(n2);
                    vertexConsumer.vertex(entry, fArray4[n], fArray5[n], fArray6[n]).color(n2);
                }
            }
            System.arraycopy(fArray4, 0, fArray, 0, 16);
            System.arraycopy(fArray5, 0, fArray2, 0, 16);
            System.arraycopy(fArray6, 0, fArray3, 0, 16);
            bl = true;
        }
    }

    private void renderHat(MatrixStack matrixStack, VertexConsumer vertexConsumer, AbstractClientPlayerEntity abstractClientPlayerEntity) {
        matrixStack.push();
        matrixStack.translate(0.0f, this.getYOffset(abstractClientPlayerEntity), 0.0f);
        MatrixStack.Entry entry = matrixStack.peek();
        float f = 1.0f;
        float f2 = Math.max(0.3f, abstractClientPlayerEntity.getWidth()) * f;
        float f3 = 0.0f;
        float f4 = 0.3f * f;
        int n = this.modeColor(0, 1.0f);
        for (int i = 0; i < 144; ++i) {
            float f5 = (float)i / 144.0f;
            float f6 = (float)(i + 1) / 144.0f;
            float f7 = f5 * ((float)Math.PI * 2);
            float f8 = f6 * ((float)Math.PI * 2);
            float f9 = MathHelper.sin((double)f7) * f2;
            float f10 = -MathHelper.cos((double)f7) * f2;
            float f11 = MathHelper.sin((double)f8) * f2;
            float f12 = -MathHelper.cos((double)f8) * f2;
            int n2 = this.modeColor((int)(f5 * 720.0f), 0.5f);
            int n3 = this.modeColor((int)(f6 * 720.0f), 0.5f);
            vertexConsumer.vertex(entry, f9, f3, f10).color(n2);
            vertexConsumer.vertex(entry, f11, f3, f12).color(n3);
            vertexConsumer.vertex(entry, 0.0f, f4, 0.0f).color(n);
            vertexConsumer.vertex(entry, 0.0f, f4, 0.0f).color(n);
        }
        float f13 = 0.010000001f;
        this.emitOutlineTube(entry, vertexConsumer, f2, f3, f13);
        matrixStack.pop();
    }

    private int modeColor(int n, float f) {
        int n2;
        int n3;
        if (this.colorMode.is(COLOR_RAINBOW)) {
            return ChinaHat.rainbow(8, n, 1.0f, 1.0f, f);
        }
        if (this.colorMode.is(COLOR_CLIENT)) {
            int[] nArray = ClientPalette.colors();
            if (nArray != null && nArray.length >= 2) {
                return ColorUtil.multAlpha(ChinaHat.paletteFade(8, n, nArray), f);
            }
            InterfaceModule interfaceModule = InterfaceModule.getInstance();
            if (interfaceModule != null) {
                n3 = interfaceModule.clientPrimaryColorOpaque();
                n2 = interfaceModule.usesSecondClientColor() ? interfaceModule.clientSecondaryColorOpaque() : n3;
            } else {
                n3 = -1;
                n2 = ColorUtil.lerpColor(n3, DARK_SECOND_COLOR, 0.7f);
            }
        } else {
            n3 = this.customColor.getColor();
            int n4 = n2 = this.useSecondColor.getValue() ? this.customSecondColor.getColor() : this.customColor.getColor();
        }
        if (n3 == n2) {
            return ColorUtil.multAlpha(n3, f);
        }
        return ColorUtil.multAlpha(ChinaHat.fade(8, n, n3, n2), f);
    }

    private float getYOffset(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        boolean bl = abstractClientPlayerEntity.getAbilities().flying;
        boolean bl2 = abstractClientPlayerEntity.isSwimming();
        boolean bl3 = this.hasVisibleHelmet(abstractClientPlayerEntity);
        if (abstractClientPlayerEntity.isInSneakingPose() && !bl && !bl2) {
            return bl3 ? 0.39f : 0.28f;
        }
        return bl3 ? 0.49f : 0.38f;
    }

    private static int paletteFade(int n, int n2, int[] nArray) {
        int n3 = nArray.length;
        int n4 = (int)((System.currentTimeMillis() / (long)Math.max(1, n) + (long)n2) % 360L);
        float f = (float)n4 / 360.0f * (float)n3;
        int n5 = (int)f % n3;
        int n6 = (n5 + 1) % n3;
        int n7 = nArray[n5] | 0xFF000000;
        int n8 = nArray[n6] | 0xFF000000;
        return ColorUtil.lerpColor(n7, n8, f - (float)Math.floor(f)) | 0xFF000000;
    }

    public boolean shouldRender(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (this.mc.player == null) {
            return false;
        }
        if (this.renderMode.is(MODE_ALL)) {
            return true;
        }
        if (this.renderMode.is(MODE_FRIENDS)) {
            return abstractClientPlayerEntity == this.mc.player || FriendUtils.isFriend((String)abstractClientPlayerEntity.getName().getString());
        }
        return abstractClientPlayerEntity == this.mc.player;
    }

    public void captureTransform(int n, Matrix4f matrix4f) {
        this.hatTransforms.put(n, new HatTransform(matrix4f, System.currentTimeMillis()));
    }

    public static record HatTransform(Matrix4f matrix, long time) {}
}

