package rtx.byazen.api.modules.impl.Visuals;
import rtx.byazen.api.events.EventHandler;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.player.AttackEntityEvent;
import rtx.byazen.api.events.impl.render.DrawEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.impl.Visuals.killeffect.KillEffectDeathMemoryTracker;
import rtx.byazen.api.modules.impl.Visuals.killeffect.KillEffectEasing;
import rtx.byazen.api.modules.impl.Visuals.killeffect.KillEffectParticleSystem;
import rtx.byazen.api.modules.impl.Visuals.killeffect.KillEffectScanRenderer;
import rtx.byazen.api.modules.impl.Visuals.killeffect.KillEffectSoundQueue;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.MultiSelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.color.ColorUtil;
import rtx.byazen.utils.color.RainbowLut;
import rtx.byazen.utils.render.others.RenderCompatibility;
import rtx.byazen.utils.render.post.killdistortion.KillDistortionRenderer;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.SoundManager;
import rtx.byazen.utils.storage.friend.FriendUtils;
import rtx.byazen.utils.time.StopWatch;

public final class KillEffect
extends Module {
    private static final long BASE_EFFECT_DURATION_MS = 5200L;
    private static final long SPIKE_DURATION_MS = 600L;
    private static final Identifier VIGNETTE_TEXTURE = Identifier.of((String)"byazen", (String)"textures/effects/frag/lightarroundscreen_alpha.png");
    private static final String TARGET_PLAYERS = "\u0418\u0433\u0440\u043e\u043a\u0438";
    private static final String TARGET_FRIENDS = "\u0414\u0440\u0443\u0437\u044c\u044f";
    private static final String TARGET_MOBS = "\u041c\u043e\u0431\u044b";
    private static final String TARGET_ANIMALS = "\u0416\u0438\u0432\u043e\u0442\u043d\u044b\u0435";
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private static final int DARK_SECOND_COLOR = new Color(16, 16, 16, 75).getRGB();
    private final KillEffectSoundQueue soundQueue = new KillEffectSoundQueue();
    private final KillEffectParticleSystem particleSystem = new KillEffectParticleSystem();
    private final StopWatch effectTimer = new StopWatch();
    private static final long ATTACK_MEMORY_MS = 2500L;
    private final Map<Integer, Long> recentlyAttacked = new HashMap<Integer, Long>();
    private final KillEffectDeathMemoryTracker deathMemoryTracker = new KillEffectDeathMemoryTracker(2500L, this::handleRememberedDeath);
    private final SeparatorSetting colorsSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442\u0430"));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430 \u044d\u0444\u0444\u0435\u043a\u0442\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430.", "\u0421\u0432\u043e\u0439", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u0432\u043e\u0439"));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0441\u0432\u043e\u0439 \u0446\u0432\u0435\u0442.", false));
    private final ColorSetting customColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 \u044d\u0444\u0444\u0435\u043a\u0442\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430.", new Color(-50116, true)));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u044d\u0444\u0444\u0435\u043a\u0442\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430.", new Color(ColorUtil.lerpColor(-50116, DARK_SECOND_COLOR, 0.7f), true)));
    private final SeparatorSetting targetsSeparator = this.register(new SeparatorSetting("\u0426\u0435\u043b\u0438"));
    private final MultiSelectSetting effectTargets = this.register(new MultiSelectSetting("\u0426\u0435\u043b\u0438 \u044d\u0444\u0444\u0435\u043a\u0442\u0430", "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435, \u043a\u0430\u043a\u0438\u0435 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438 \u0437\u0430\u043f\u0443\u0441\u043a\u0430\u044e\u0442 \u044d\u0444\u0444\u0435\u043a\u0442 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430.").value("\u0418\u0433\u0440\u043e\u043a\u0438", "\u0414\u0440\u0443\u0437\u044c\u044f", "\u041c\u043e\u0431\u044b", "\u0416\u0438\u0432\u043e\u0442\u043d\u044b\u0435").selected("\u0418\u0433\u0440\u043e\u043a\u0438", "\u0414\u0440\u0443\u0437\u044c\u044f", "\u041c\u043e\u0431\u044b", "\u0416\u0438\u0432\u043e\u0442\u043d\u044b\u0435"));
    private static final float ZOOM_STRENGTH = 15.0f;
    private static final float SHAKE_STRENGTH = 250.0f;
    private final SeparatorSetting effectsSeparator = this.register(new SeparatorSetting("\u042d\u0444\u0444\u0435\u043a\u0442\u044b"));
    private final BooleanSetting spikes = this.register(new BooleanSetting("\u0428\u0438\u043f\u044b", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u043d\u0430\u043a\u043b\u0430\u0434\u043a\u0443 \u0438\u0437 \u0448\u0438\u043f\u043e\u0432, \u0432\u0440\u044b\u0432\u0430\u044e\u0449\u0443\u044e\u0441\u044f \u043e\u0442 \u043a\u0440\u0430\u0451\u0432 \u044d\u043a\u0440\u0430\u043d\u0430 \u0432\u043e \u0432\u0440\u0435\u043c\u044f \u044d\u0444\u0444\u0435\u043a\u0442\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430.", true));
    private final BooleanSetting changeSaturation = this.register(new BooleanSetting("\u041c\u0435\u043d\u044f\u0442\u044c \u043d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c", "\u0412\u0440\u0435\u043c\u0435\u043d\u043d\u043e \u0441\u043d\u0438\u0436\u0430\u0435\u0442 \u043d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u043c\u0438\u0440\u0430 \u0432\u043e \u0432\u0440\u0435\u043c\u044f \u044d\u0444\u0444\u0435\u043a\u0442\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430.", false));
    private final BooleanSetting zoomEffect = this.register(new BooleanSetting("\u041f\u0440\u0438\u0431\u043b\u0438\u0436\u0435\u043d\u0438\u0435", "\u041f\u043b\u0430\u0432\u043d\u043e \u043f\u0440\u0438\u0431\u043b\u0438\u0436\u0430\u0435\u0442 \u0438 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0435\u0442 \u043a\u0430\u043c\u0435\u0440\u0443 \u043f\u0440\u0438 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0435.", false));
    private final BooleanSetting cameraShake = this.register(new BooleanSetting("\u0422\u0440\u044f\u0441\u043a\u0430 \u043a\u0430\u043c\u0435\u0440\u044b", "\u0414\u043e\u0431\u0430\u0432\u043b\u044f\u0435\u0442 \u043a\u043e\u0440\u043e\u0442\u043a\u0443\u044e \u0442\u0440\u044f\u0441\u043a\u0443 \u043a\u0430\u043c\u0435\u0440\u044b \u043f\u0440\u0438 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0435.", false));
    private final BooleanSetting distortion = this.register(new BooleanSetting("\u0418\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u0435", "\u0420\u0430\u0441\u0448\u0438\u0440\u044f\u044e\u0449\u0430\u044f\u0441\u044f \u0441\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u0430\u044f \u0443\u0434\u0430\u0440\u043d\u0430\u044f \u0432\u043e\u043b\u043d\u0430, \u0438\u0441\u043a\u0430\u0436\u0430\u044e\u0449\u0430\u044f \u043c\u0438\u0440 \u043e\u0442 \u043c\u0435\u0441\u0442\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430.", false));
    private boolean animate;
    private float activeEffectSpeedMultiplier = 1.0f;
    private final StopWatch cameraTimer = new StopWatch();
    private boolean cameraAnimate;
    private static final long CAMERA_EFFECT_MS = 650L;
    private static final long DISTORTION_MS = 900L;
    private final StopWatch distortionTimer = new StopWatch();
    private boolean distortionAnimate;
    private Vec3d distortionCenter = Vec3d.ZERO;

    public KillEffect() {
        super("Kill Effect", "\u042d\u0444\u0444\u0435\u043a\u0442 \u0441\u043a\u0430\u043d\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f \u043f\u0440\u0438 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0435 \u0441 \u0432\u0438\u043d\u044c\u0435\u0442\u043a\u043e\u0439, \u0432\u043e\u043b\u043d\u043e\u0439 \u0438 \u0437\u0432\u0443\u043a\u0430\u043c\u0438.", Category.VISUALS);
        this.useSecondColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
        this.customColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
        this.customSecondColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM) && this.useSecondColor.getValue());
    }

    private int c2() {
        return KillEffect.lighten(this.accent(), 0.18f);
    }

    public static KillEffect getInstance() {
        return ModuleManager.get().get(KillEffect.class);
    }

    private int c1() {
        return KillEffect.lighten(this.accent(), 0.45f);
    }

    private void resetState() {
        this.animate = false;
        this.cameraAnimate = false;
        this.distortionAnimate = false;
        this.activeEffectSpeedMultiplier = this.speedMultiplier();
        this.soundQueue.clear();
        this.particleSystem.clear();
        this.recentlyAttacked.clear();
        this.deathMemoryTracker.clear();
        KillEffectScanRenderer.clear();
        KillDistortionRenderer.clear();
    }

    private static int red(int n) {
        return n >>> 16 & 0xFF;
    }

    private int c3() {
        return KillEffect.darken(this.accent(), 0.18f);
    }

    private int c4() {
        return KillEffect.lighten(this.accent(), 0.6f);
    }

    private int c5() {
        return KillEffect.lighten(this.accent(), 0.7f);
    }

    private int c6() {
        return ColorUtil.lerpColor(this.accent(), -1, 0.28f);
    }

    private int c7() {
        return KillEffect.darken(this.accent(), 0.08f);
    }

    private int c8() {
        return KillEffect.darken(this.accent(), 0.35f);
    }

    private static int alpha(int n) {
        return n >>> 24 & 0xFF;
    }

    private static int rainbow() {
        int n = (int)(System.currentTimeMillis() / 8L % 360L);
        return RainbowLut.sample((int)n, (float)1.0f, (float)1.0f);
    }

    private static int darken(int n, float f) {
        float f2 = 1.0f - Math.clamp(f, 0.0f, 1.0f);
        return ColorUtil.rgba(Math.clamp((long)Math.round((float)KillEffect.red(n) * f2), 0, 255), Math.clamp((long)Math.round((float)KillEffect.green(n) * f2), 0, 255), Math.clamp((long)Math.round((float)KillEffect.blue(n) * f2), 0, 255), KillEffect.alpha(n));
    }

    private static int fade(int n, int n2) {
        int n3 = (int)(System.currentTimeMillis() / 8L % 360L);
        n3 = n3 >= 180 ? 360 - n3 : n3;
        return ColorUtil.lerpColor(n, n2, (float)n3 / 180.0f);
    }

    @Override
    protected void onDisable() {
        this.resetState();
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost()) {
            return;
        }
        if (this.mc.player == null || this.mc.world == null) {
            this.resetState();
            return;
        }
        this.deathMemoryTracker.tick();
        this.particleSystem.tick();
        this.soundQueue.tick();
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        LivingEntity livingEntity;
        if (this.mc.player == null || attackEntityEvent.isSynthetic()) {
            return;
        }
        Entity entity = attackEntityEvent.getTarget();
        if (entity instanceof LivingEntity && (livingEntity = (LivingEntity)entity) != this.mc.player) {
            this.recentlyAttacked.put(livingEntity.getId(), System.currentTimeMillis());
            this.deathMemoryTracker.remember(livingEntity, true);
            this.pruneAttackMemory();
        }
    }

    private static int blue(int n) {
        return n & 0xFF;
    }

    private static int green(int n) {
        return n >>> 8 & 0xFF;
    }

    private int accent() {
        int n;
        int n2;
        if (this.colorMode.is(COLOR_RAINBOW)) {
            return KillEffect.opaque(KillEffect.rainbow());
        }
        if (this.colorMode.is(COLOR_CLIENT)) {
            InterfaceModule interfaceModule = InterfaceModule.getInstance();
            if (interfaceModule != null) {
                n2 = interfaceModule.clientPrimaryColorOpaque();
                n = interfaceModule.usesSecondClientColor() ? interfaceModule.clientSecondaryColorOpaque() : n2;
            } else {
                n = n2 = -50116;
            }
        } else {
            n2 = this.customColor.getColorOpaque();
            int n3 = n = this.useSecondColor.getValue() ? this.customSecondColor.getColorOpaque() : n2;
        }
        if (n2 == n) {
            return KillEffect.opaque(n2);
        }
        return KillEffect.opaque(KillEffect.fade(n2, n));
    }

    private void scheduleCharmSounds() {
        long l = this.scaleDuration(150L);
        this.soundQueue.schedule(SoundManager.FRAG_EFFECT_PULSE, 1.0f, 0L);
        this.soundQueue.schedule(SoundManager.FRAG_EFFECT_KNOCK_MAIN, 1.0f, l);
        this.soundQueue.schedule(SoundManager.FRAG_EFFECT_SPARKS_COLLISION, 0.2f, l * 2L);
        this.soundQueue.schedule(SoundManager.FRAG_EFFECT_ECHO_MAIN, 0.6f, l * 3L);
    }

    public static KillEffect getInstanceIfReady() {
        try {
            return KillEffect.getInstance();
        }
        catch (RuntimeException runtimeException) {
            return null;
        }
    }

    private void handleDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (this.mc.player == null || this.mc.world == null || livingEntity == null || livingEntity == this.mc.player) {
            return;
        }
        if (!this.localPlayerGotKill(livingEntity, damageSource)) {
            return;
        }
        if (!this.matchesEffectTarget(livingEntity)) {
            return;
        }
        this.recentlyAttacked.remove(livingEntity.getId());
        this.deathMemoryTracker.forget(livingEntity.getId());
        this.onTrackedKill(livingEntity);
    }

    private void drawVignette(DrawEvent drawEvent) {
        float f;
        if (!this.spikes.isValue()) {
            return;
        }
        long l = this.scaleDuration(600L);
        float f2 = f = this.animate ? Math.min((float)this.effectTimer.elapsedTime() / (float)l, 1.0f) : 0.0f;
        if (f <= 0.0f || f >= 1.0f) {
            return;
        }
        float f3 = (float)Math.sin((double)f * Math.PI);
        float f4 = KillEffectEasing.quintOut((float)f3);
        float f5 = 1.0f - f4;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        float f6 = Position.screenWidth();
        float f7 = Position.screenHeight();
        float f8 = f6 * 0.75f * f5;
        float f9 = f7 * 0.75f * f5;
        int n = ColorUtil.withAlpha(ColorUtil.lerpColor(KillEffect.lighten(this.accent(), 0.22f), -1, f), Math.round(150.0f * f3));
        Render2D.beginFrame(drawEvent.getGraphics());
        Render2D.image(VIGNETTE_TEXTURE.toString(), -f8, -f9, f6 + f8 * 2.0f, f7 + f9 * 2.0f, 0.0f, n);
        Render2D.flush();
    }

    private void renderDistortion(WorldRenderEvent worldRenderEvent) {
        if (!this.distortion.isValue() || !this.distortionAnimate || KillDistortionRenderer.isDisabledAfterError()) {
            return;
        }
        float f = Math.min((float)this.distortionTimer.elapsedTime() / 900.0f, 1.0f);
        if (f >= 1.0f) {
            this.distortionAnimate = false;
            return;
        }
        if (this.mc.player == null || worldRenderEvent.getCamera() == null) {
            return;
        }
        Framebuffer framebuffer = this.mc.getFramebuffer();
        if (framebuffer == null || framebuffer.textureWidth <= 0 || framebuffer.textureHeight <= 0) {
            return;
        }
        Matrix4f matrix4f = worldRenderEvent.getPositionMatrix();
        Matrix4f matrix4f2 = worldRenderEvent.getProjectionMatrix();
        if (matrix4f == null || matrix4f2 == null) {
            return;
        }
        Vec3d vec3d = worldRenderEvent.getCamera().getCameraPos();
        Vector4f vector4f = new Vector4f((float)(this.distortionCenter.x - vec3d.x), (float)(this.distortionCenter.y - vec3d.y), (float)(this.distortionCenter.z - vec3d.z), 1.0f);
        matrix4f.transform(vector4f);
        matrix4f2.transform(vector4f);
        if (vector4f.w <= 1.0E-4f) {
            return;
        }
        float f2 = vector4f.x / vector4f.w * 0.5f + 0.5f;
        float f3 = vector4f.y / vector4f.w * 0.5f + 0.5f;
        float f4 = vector4f.z / vector4f.w * 0.5f + 0.5f;
        float f5 = (float)framebuffer.textureWidth / (float)Math.max(1, framebuffer.textureHeight);
        float f6 = KillEffectEasing.sineInOut((float)f);
        float f7 = f6 * 1.4f;
        float f8 = 0.14f - 0.05f * f;
        float f9 = (float)Math.sin((double)f * Math.PI);
        float f10 = 0.055f * f9;
        float[] fArray = new float[]{f2, f3, f4, f5, f7, f8, f10, f * 6.0f};
        KillDistortionRenderer.apply((Framebuffer)framebuffer, (float[])fArray);
    }

    private float transientEnvelope(long l, long l2, long l3) {
        if (!this.animate) {
            return 0.0f;
        }
        long l4 = this.effectDurationMs();
        long l5 = Math.clamp(this.scaleDuration(l), 1L, l4);
        long l6 = Math.min(this.effectTimer.elapsedTime(), l5);
        if (l6 <= 0L) {
            return 0.0f;
        }
        long l7 = Math.clamp(this.scaleDuration(l2), 1L, l5);
        long l8 = Math.clamp(this.scaleDuration(l3), 1L, l5);
        long l9 = Math.max(l5 - l8, l7);
        if (l6 < l7) {
            return KillEffectEasing.sineOut((float)((float)l6 / (float)l7));
        }
        if (l6 >= l9) {
            float f = Math.clamp((float)(l6 - l9) / (float)l8, 0.0f, 1.0f);
            return 1.0f - KillEffectEasing.sineInOut((float)f);
        }
        return 1.0f;
    }

    private void pruneAttackMemory() {
        long l = System.currentTimeMillis() - 2500L;
        this.recentlyAttacked.values().removeIf(l2 -> l2 < l);
    }

    private float speedMultiplier() {
        return 1.0f;
    }

    public static float getKillZoomFovScale() {
        KillEffect killEffect = KillEffect.getInstanceIfReady();
        if (killEffect == null || !killEffect.isEnabled() || !killEffect.zoomEffect.isValue()) {
            return 1.0f;
        }
        float f = killEffect.cameraEnvelope();
        if (f <= 0.0f) {
            return 1.0f;
        }
        float f2 = 15.0f * f;
        float f3 = 1.0f - f2 / 70.0f;
        return Math.clamp(f3, 0.5f, 1.0f);
    }

    private boolean localPlayerGotKill(LivingEntity livingEntity, DamageSource damageSource) {
        ClientPlayerEntity clientPlayerEntity = this.mc.player;
        if (clientPlayerEntity == null) {
            return false;
        }
        Long l = this.recentlyAttacked.get(livingEntity.getId());
        if (l != null && System.currentTimeMillis() - l <= 2500L) {
            return true;
        }
        if (livingEntity.getPrimeAdversary() == clientPlayerEntity) {
            return true;
        }
        return damageSource != null && damageSource.getAttacker() == clientPlayerEntity;
    }

    private long scaleDuration(long l, float f) {
        if (l <= 0L) {
            return 0L;
        }
        return Math.max(1L, (long)Math.round((float)l / Math.clamp(f, 0.25f, 2.0f)));
    }

    private long scaleDuration(long l) {
        return this.scaleDuration(l, this.activeEffectSpeedMultiplier);
    }

    public static float getKillShakeDegrees() {
        KillEffect killEffect = KillEffect.getInstanceIfReady();
        if (killEffect == null || !killEffect.isEnabled() || !killEffect.cameraShake.isValue()) {
            return 0.0f;
        }
        float f = killEffect.cameraProgress();
        if (f <= 0.0f) {
            return 0.0f;
        }
        float f2 = 2.5f;
        float f3 = (float)killEffect.cameraTimer.elapsedTime() / 1000.0f;
        float f4 = (float)(Math.sin((double)f3 * 26.0) * 0.65 + Math.sin((double)f3 * 16.5) * 0.35);
        float f5 = (1.0f - f) * (1.0f - f);
        return f4 * 0.8f * f2 * f5;
    }

    private float effectProgress() {
        float f;
        long l = this.effectDurationMs();
        float f2 = f = this.animate ? Math.min((float)this.effectTimer.elapsedTime() / (float)l, 1.0f) : 0.0f;
        if (f >= 1.0f) {
            this.animate = false;
        }
        return f;
    }

    private float effectEnvelope(long l, long l2) {
        if (!this.animate) {
            return 0.0f;
        }
        long l3 = this.effectDurationMs();
        long l4 = Math.min(this.effectTimer.elapsedTime(), l3);
        if (l4 <= 0L) {
            return 0.0f;
        }
        long l5 = Math.clamp(this.scaleDuration(l), 1L, l3);
        long l6 = Math.clamp(this.scaleDuration(l2), 1L, l3);
        long l7 = Math.max(l3 - l6, l5);
        if (l4 < l5) {
            return KillEffectEasing.sineOut((float)((float)l4 / (float)l5));
        }
        if (l4 >= l7) {
            float f = Math.clamp((float)(l4 - l7) / (float)l6, 0.0f, 1.0f);
            return 1.0f - KillEffectEasing.sineInOut((float)f);
        }
        return 1.0f;
    }

    private float cameraEnvelope() {
        float f = this.cameraProgress();
        if (f <= 0.0f) {
            return 0.0f;
        }
        return (float)Math.sin((double)f * Math.PI);
    }

    private void restartEffect(float f) {
        this.animate = true;
        this.activeEffectSpeedMultiplier = Math.clamp(f, 0.25f, 2.0f);
        this.effectTimer.reset();
    }

    private boolean matchesEffectTarget(LivingEntity livingEntity) {
        if (livingEntity == null || livingEntity == this.mc.player) {
            return false;
        }
        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (FriendUtils.isFriend(playerEntity.getName().getString())) {
                return this.effectTargets.isSelected(TARGET_FRIENDS);
            }
            return this.effectTargets.isSelected(TARGET_PLAYERS);
        }
        if (livingEntity instanceof AnimalEntity) {
            return this.effectTargets.isSelected(TARGET_ANIMALS);
        }
        if (livingEntity instanceof MobEntity) {
            return this.effectTargets.isSelected(TARGET_MOBS);
        }
        return false;
    }

    private void handleRememberedDeath(LivingEntity livingEntity) {
        if (!this.matchesEffectTarget(livingEntity)) {
            return;
        }
        this.recentlyAttacked.remove(livingEntity.getId());
        this.onTrackedKill(livingEntity);
    }

    private float cameraProgress() {
        if (!this.cameraAnimate) {
            return 0.0f;
        }
        float f = Math.min((float)this.cameraTimer.elapsedTime() / 650.0f, 1.0f);
        if (f >= 1.0f) {
            this.cameraAnimate = false;
        }
        return f;
    }

    private long effectDurationMs() {
        return this.scaleDuration(5200L);
    }

    private void onTrackedKill(LivingEntity livingEntity) {
        if (!this.matchesEffectTarget(livingEntity)) {
            return;
        }
        if (this.mc.player == null || this.mc.player.isDead()) {
            return;
        }
        Vec3d vec3d = livingEntity.getEyePos();
        float f = this.speedMultiplier();
        this.restartEffect(f);
        KillEffectScanRenderer.ping((Vec3d)vec3d, (float)f);
        this.particleSystem.spawnBurst(vec3d, 480, 900, 1.9f, 22, 0.012f);
        this.scheduleCharmSounds();
        if (this.zoomEffect.isValue() || this.cameraShake.isValue()) {
            this.cameraAnimate = true;
            this.cameraTimer.reset();
        }
        if (this.distortion.isValue()) {
            this.distortionCenter = vec3d;
            this.distortionAnimate = true;
            this.distortionTimer.reset();
        }
    }

    public static float getWorldSaturationMultiplier() {
        KillEffect killEffect = KillEffect.getInstanceIfReady();
        if (killEffect == null || !killEffect.isEnabled() || !killEffect.changeSaturation.isValue()) {
            return 1.0f;
        }
        float f = killEffect.transientEnvelope(1950L, 260L, 760L);
        if (f <= 0.0f) {
            return 1.0f;
        }
        return Math.clamp(1.0f - f, 0.0f, 1.0f);
    }

    private static int lighten(int n, float f) {
        float f2 = Math.clamp(f, 0.0f, 1.0f);
        int n2 = KillEffect.red(n);
        int n3 = KillEffect.green(n);
        int n4 = KillEffect.blue(n);
        int n5 = KillEffect.alpha(n);
        return ColorUtil.rgba(Math.clamp((long)Math.round((float)n2 + (float)(255 - n2) * f2), 0, 255), Math.clamp((long)Math.round((float)n3 + (float)(255 - n3) * f2), 0, 255), Math.clamp((long)Math.round((float)n4 + (float)(255 - n4) * f2), 0, 255), n5);
    }

    private static int opaque(int n) {
        return 0xFF000000 | n & 0xFFFFFF;
    }

    @EventHandler
    public void onDraw(DrawEvent drawEvent) {
        this.drawVignette(drawEvent);
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || RenderCompatibility.shouldDisableFragEffectScanShader() || KillEffectScanRenderer.isDisabledAfterError()) {
            return;
        }
        KillEffectScanRenderer.render((WorldRenderEvent)worldRenderEvent, (float)this.activeEffectSpeedMultiplier, (int)this.c1(), (int)this.c2(), (int)this.c3(), (int)this.c4(), (int)this.c5(), (int)this.c6(), (int)this.c7(), (int)this.c8());
        this.renderDistortion(worldRenderEvent);
    }

    public static void notifyEntityDied(LivingEntity livingEntity, DamageSource damageSource) {
        KillEffect killEffect = KillEffect.getInstanceIfReady();
        if (killEffect == null || !killEffect.isEnabled()) {
            return;
        }
        killEffect.handleDeath(livingEntity, damageSource);
    }
}

