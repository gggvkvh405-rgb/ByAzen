package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.events.EventHandler;
import java.awt.Color;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.events.impl.render.WorldRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.mixin.accessor.MultiPlayerGameModeAccessor;
import rtx.kimiko.utils.animations.Easings;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.color.RainbowLut;
import rtx.kimiko.utils.render.render2d.ClientPalette;
import rtx.kimiko.utils.render.world.BlockOverlayRenderer;

public final class BlockOverlay
extends Module {
    private static final long FADE_DURATION_MS = 220L;
    private static final long MOVE_DURATION_MS = 160L;
    private static final int DARK_SECOND_COLOR = new Color(16, 16, 16, 75).getRGB();
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442"));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0426\u0432\u0435\u0442 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0440\u0430\u0437\u0440\u0443\u0448\u0435\u043d\u0438\u044f \u0431\u043b\u043e\u043a\u0430.", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u0432\u043e\u0439"));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0441\u0432\u043e\u0439 \u0446\u0432\u0435\u0442.", false));
    private final ColorSetting customColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438.", new Color(255, 255, 255, 255)).visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM)));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438.", new Color(ColorUtil.lerpColor(-1, DARK_SECOND_COLOR, 0.7f), true)).visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM) && this.useSecondColor.getValue()));
    private BlockPos targetPos;
    private Vec3d movementFrom;
    private Vec3d movementTo;
    private long movementStartedAt;
    private float damage;
    private float previousDamage;
    private float previousRawProgress;
    private boolean destroying;
    private boolean fading;
    private boolean skipFadeIn;
    private long fadeStartedAt;
    private float fadeStartAlpha;
    private float lastRenderedAlpha;
    private float lastRenderedProgress;
    private Box lastBox;
    private Box fadeFromBox;
    private Box fadeToBox;

    public BlockOverlay() {
        super("Block Overlay", "\u0420\u0438\u0441\u0443\u0435\u0442 \u043a\u0430\u0441\u0442\u043e\u043c\u043d\u0443\u044e \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044e \u0440\u0430\u0437\u0440\u0443\u0448\u0435\u043d\u0438\u044f \u0431\u043b\u043e\u043a\u0430.", Category.VISUALS);
        this.useSecondColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
    }

    private void reset() {
        this.targetPos = null;
        this.movementFrom = null;
        this.movementTo = null;
        this.movementStartedAt = 0L;
        this.damage = 0.0f;
        this.previousDamage = 0.0f;
        this.previousRawProgress = 0.0f;
        this.destroying = false;
        this.fading = false;
        this.skipFadeIn = false;
        this.fadeStartedAt = 0L;
        this.fadeStartAlpha = 0.0f;
        this.lastRenderedAlpha = 0.0f;
        this.lastRenderedProgress = 0.0f;
        this.lastBox = null;
        this.fadeFromBox = null;
        this.fadeToBox = null;
    }

    private static int fade(int n, int n2, int n3, int n4) {
        int n5 = (int)((System.currentTimeMillis() / (long)Math.max(1, n) + (long)n2) % 360L);
        n5 = n5 >= 180 ? 360 - n5 : n5;
        return ColorUtil.lerpColor(n3, n4, (float)n5 / 180.0f);
    }

    @Override
    protected void onDisable() {
        this.reset();
    }

    @EventHandler
    private void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        this.previousDamage = this.damage;
        ClientPlayerInteractionManager clientPlayerInteractionManager = this.mc.interactionManager;
        if (this.mc.player == null || this.mc.world == null || clientPlayerInteractionManager == null) {
            this.reset();
            return;
        }
        MultiPlayerGameModeAccessor multiPlayerGameModeAccessor = (MultiPlayerGameModeAccessor)clientPlayerInteractionManager;
        BlockPos blockPos = multiPlayerGameModeAccessor.kimiko_getDestroyBlockPos();
        float f = multiPlayerGameModeAccessor.kimiko_getDestroyProgress();
        boolean bl = multiPlayerGameModeAccessor.kimiko_isDestroying() || f > 0.0f && f != this.previousRawProgress;
        this.previousRawProgress = f;
        if (!bl || blockPos == null) {
            if (this.destroying && this.lastBox != null) {
                this.startFade();
            } else if (this.fading && System.currentTimeMillis() - this.fadeStartedAt >= 220L) {
                this.reset();
            } else if (!this.fading) {
                this.reset();
            }
            return;
        }
        boolean bl2 = this.fading;
        this.fading = false;
        if (this.targetPos == null) {
            this.targetPos = blockPos.toImmutable();
            this.movementTo = this.movementFrom = BlockOverlay.blockPosition(this.targetPos);
            this.movementStartedAt = System.currentTimeMillis() - 160L;
            this.skipFadeIn = false;
        } else if (!this.targetPos.equals((Object)blockPos)) {
            long l = System.currentTimeMillis();
            this.movementFrom = this.animatedPosition(l);
            this.targetPos = blockPos.toImmutable();
            this.movementTo = BlockOverlay.blockPosition(this.targetPos);
            this.movementStartedAt = l;
            this.skipFadeIn = !bl2;
        } else if (bl2) {
            this.skipFadeIn = false;
        }
        this.damage = MathHelper.clamp((float)f, (float)0.0f, (float)1.0f);
        this.destroying = true;
    }

    private static Vec3d blockPosition(BlockPos blockPos) {
        return new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
    }

    private static Box interpolateBox(Box box, Box box2, float f) {
        if (box == null || box2 == null) {
            return null;
        }
        return new Box(MathHelper.lerp((double)f, (double)box.minX, (double)box2.minX), MathHelper.lerp((double)f, (double)box.minY, (double)box2.minY), MathHelper.lerp((double)f, (double)box.minZ, (double)box2.minZ), MathHelper.lerp((double)f, (double)box.maxX, (double)box2.maxX), MathHelper.lerp((double)f, (double)box.maxY, (double)box2.maxY), MathHelper.lerp((double)f, (double)box.maxZ, (double)box2.maxZ));
    }

    private Vec3d animatedPosition(long l) {
        if (this.movementFrom == null || this.movementTo == null) {
            return this.targetPos != null ? BlockOverlay.blockPosition(this.targetPos) : Vec3d.ZERO;
        }
        float f = MathHelper.clamp((float)((float)(l - this.movementStartedAt) / 160.0f), (float)0.0f, (float)1.0f);
        double d = Easings.CUBIC_OUT.ease(f);
        return this.movementFrom.lerp(this.movementTo, d);
    }

    private static float easeInOutQuad(float f) {
        float f2 = MathHelper.clamp((float)f, (float)0.0f, (float)1.0f);
        return f2 < 0.5f ? 2.0f * f2 * f2 : 1.0f - (float)Math.pow(-2.0f * f2 + 2.0f, 2.0) * 0.5f;
    }

    private Box getAnimatedBox(float f) {
        BlockState blockState = this.mc.world.getBlockState(this.targetPos);
        VoxelShape voxelShape = blockState.getOutlineShape((BlockView)(Object)this.mc.world, this.targetPos);
        if (voxelShape.isEmpty()) {
            return null;
        }
        Box box = voxelShape.getBoundingBox();
        Vec3d vec3d = this.animatedPosition(System.currentTimeMillis());
        Box box2 = box.offset(vec3d);
        float f2 = BlockOverlay.animatedScale(f);
        double d = (box2.minX + box2.maxX) * 0.5;
        double d2 = (box2.minY + box2.maxY) * 0.5;
        double d3 = (box2.minZ + box2.maxZ) * 0.5;
        double d4 = box2.getLengthX() * (double)f2 * 0.5;
        double d5 = box2.getLengthY() * (double)f2 * 0.5;
        double d6 = box2.getLengthZ() * (double)f2 * 0.5;
        return new Box(d - d4, d2 - d5, d3 - d6, d + d4, d2 + d5, d3 + d6);
    }

    private static int[] multiplyRgb(int[] nArray, float f) {
        int[] nArray2 = new int[nArray.length];
        for (int i = 0; i < nArray.length; ++i) {
            int n = nArray[i];
            int n2 = Math.round((float)(n >>> 16 & 0xFF) * f);
            int n3 = Math.round((float)(n >>> 8 & 0xFF) * f);
            int n4 = Math.round((float)(n & 0xFF) * f);
            nArray2[i] = ColorUtil.rgba(n2, n3, n4, ColorUtil.alpha(n));
        }
        return nArray2;
    }

    private float activeAlpha(float f) {
        float f2 = 1.0f - BlockOverlay.smoothstep(0.72f, 1.0f, f);
        return this.skipFadeIn ? f2 : BlockOverlay.smoothstep(0.0f, 0.15f, f) * f2;
    }

    private static float animatedScale(float f) {
        float f2 = MathHelper.lerp((float)f, (float)0.175f, (float)Math.min(f + 0.175f, 1.0f));
        return BlockOverlay.easeInOutQuad(f2);
    }

    private int[] resolveColors(float f) {
        return new int[]{this.getColor(0, f), this.getColor(90, f), this.getColor(180, f), this.getColor(270, f)};
    }

    private int getColor(int n, float f) {
        int n2;
        int n3;
        if (this.colorMode.is(COLOR_RAINBOW)) {
            int n4 = (int)((System.currentTimeMillis() / 8L + (long)n) % 360L);
            int n5 = RainbowLut.sample((int)n4, (float)1.0f, (float)1.0f);
            return ColorUtil.multAlpha(n5 | 0xFF000000, f);
        }
        if (this.colorMode.is(COLOR_CLIENT)) {
            int[] nArray = ClientPalette.colors();
            if (nArray != null && nArray.length >= 2) {
                return ColorUtil.multAlpha(BlockOverlay.paletteFade(8, n, nArray), f);
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
            int n6 = n2 = this.useSecondColor.getValue() ? this.customSecondColor.getColor() : n3;
        }
        if (n3 == n2) {
            return ColorUtil.multAlpha(n3, f);
        }
        return ColorUtil.multAlpha(BlockOverlay.fade(8, n, n3, n2), f);
    }

    @EventHandler
    private void onWorldRender(WorldRenderEvent worldRenderEvent) {
        float f;
        Box box;
        if (!this.destroying && !this.fading || this.targetPos == null || this.movementFrom == null || this.movementTo == null || this.mc.world == null) {
            return;
        }
        float f2 = MathHelper.clamp((float)worldRenderEvent.getPartialTicks(), (float)0.0f, (float)1.0f);
        float f3 = MathHelper.clamp((float)MathHelper.lerp((float)f2, (float)this.previousDamage, (float)this.damage), (float)0.0f, (float)1.0f);
        float f4 = this.fading ? BlockOverlay.smoothstep(0.0f, 1.0f, MathHelper.clamp((float)((float)(System.currentTimeMillis() - this.fadeStartedAt) / 220.0f), (float)0.0f, (float)1.0f)) : 0.0f;
        Box box2 = box = this.destroying ? this.getAnimatedBox(f3) : BlockOverlay.interpolateBox(this.fadeFromBox, this.fadeToBox, f4);
        if (box == null) {
            if (this.lastBox == null) {
                return;
            }
            this.startFade();
            box = this.lastBox;
        } else if (this.destroying) {
            this.lastBox = box;
        }
        float f5 = f = this.fading ? this.fadeStartAlpha * (1.0f - f4) : this.activeAlpha(f3);
        if (f <= 0.0f) {
            return;
        }
        if (this.destroying) {
            this.lastRenderedAlpha = f;
            this.lastRenderedProgress = f3;
        }
        int[] nArray = this.resolveColors(f);
        int[] nArray2 = BlockOverlay.multiplyRgb(nArray, 0.2f);
        int[] nArray3 = BlockOverlay.multiplyRgb(nArray, 0.03f);
        Vec3d vec3d = worldRenderEvent.getCamera() != null ? worldRenderEvent.getCamera().getCameraPos() : this.mc.gameRenderer.getCamera().getCameraPos();
        BlockOverlayRenderer.render((VertexConsumerProvider.Immediate)this.mc.getBufferBuilders().getEntityVertexConsumers(), (MatrixStack)worldRenderEvent.getStack(), (Vec3d)vec3d, (Box)box, (int[])nArray, (int[])nArray2, (int[])nArray3);
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

    private void startFade() {
        if (this.fading) {
            return;
        }
        this.fadeStartAlpha = Math.max(this.lastRenderedAlpha, this.activeAlpha(this.damage));
        this.fadeStartedAt = System.currentTimeMillis();
        this.fadeFromBox = this.lastBox;
        this.fadeToBox = this.lastBox;
        if (this.mc.world != null && this.targetPos != null && !this.mc.world.getBlockState(this.targetPos).isAir()) {
            float f = BlockOverlay.animatedScale(this.lastRenderedProgress);
            float f2 = BlockOverlay.animatedScale(0.0f);
            this.fadeToBox = BlockOverlay.scaleBox(this.lastBox, f > 0.0f ? f2 / f : 1.0f);
        }
        this.fading = true;
        this.destroying = false;
    }

    private static Box scaleBox(Box box, float f) {
        double d = (box.minX + box.maxX) * 0.5;
        double d2 = (box.minY + box.maxY) * 0.5;
        double d3 = (box.minZ + box.maxZ) * 0.5;
        double d4 = box.getLengthX() * (double)f * 0.5;
        double d5 = box.getLengthY() * (double)f * 0.5;
        double d6 = box.getLengthZ() * (double)f * 0.5;
        return new Box(d - d4, d2 - d5, d3 - d6, d + d4, d2 + d5, d3 + d6);
    }

    private static float smoothstep(float f, float f2, float f3) {
        float f4 = MathHelper.clamp((float)((f3 - f) / (f2 - f)), (float)0.0f, (float)1.0f);
        return f4 * f4 * (3.0f - 2.0f * f4);
    }
}

