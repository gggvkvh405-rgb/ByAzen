package rtx.byazen.api.modules.impl.Visuals;

import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.player.AttackEntityEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;

/**
 * Кастомная камера (идея №59 из IDEAS.md).
 * <p>
 * Три вещи в одном модуле: плавное следование (взгляд мягко догоняет резкие движения мышью),
 * отдача при ударе (камеру коротко подбрасывает и подкидывает по крену) и кинематографический
 * режим (медленное «дыхание» зума, покачивание и крен). Всё считается по времени кадра и
 * подмешивается в проекцию, поэтому работает с любой частотой монитора.
 */
public final class CameraPlus
extends Module {

    private static CameraPlus instance;

    private final SeparatorSetting followSeparator = this.register(new SeparatorSetting("Плавное следование"));
    public final BooleanSetting smooth = this.register(new BooleanSetting("Плавное следование", "Взгляд мягко догоняет поворот: при резких движениях мыши камера слегка отстаёт и догоняет.", true));
    public final SliderSetting smoothStrength = this.register(new SliderSetting("Сила сглаживания, %", "Насколько заметно отставание камеры.").range(5.0f, 100.0f).increment(5.0f).setValue(35.0f)).visible(this.smooth::getValue);
    public final SliderSetting smoothSpeed = this.register(new SliderSetting("Скорость догона", "Как быстро камера догоняет поворот.").range(10.0f, 100.0f).increment(5.0f).setValue(45.0f)).visible(this.smooth::getValue);
    public final BooleanSetting lean = this.register(new BooleanSetting("Крен в движении", "Камера наклоняется в сторону при быстром стрейфе — как в кинематографе.", true));
    public final SliderSetting leanStrength = this.register(new SliderSetting("Сила крена, %", "Насколько сильно камера наклоняется в движении.").range(10.0f, 150.0f).increment(5.0f).setValue(60.0f)).visible(this.lean::getValue);

    private final SeparatorSetting recoilSeparator = this.register(new SeparatorSetting("Отдача"));
    public final BooleanSetting recoil = this.register(new BooleanSetting("Отдача при ударе", "При вашем ударе камеру коротко подбрасывает: кик по высоте, крен и мягкое сжатие зума.", true));
    public final SliderSetting recoilStrength = this.register(new SliderSetting("Сила отдачи, %", "Насколько заметен кик камеры.").range(20.0f, 200.0f).increment(10.0f).setValue(90.0f)).visible(this.recoil::getValue);
    public final SliderSetting recoilSpeed = this.register(new SliderSetting("Затухание отдачи", "Как быстро камера возвращается на место.").range(10.0f, 100.0f).increment(5.0f).setValue(55.0f)).visible(this.recoil::getValue);

    private final SeparatorSetting cinemaSeparator = this.register(new SeparatorSetting("Кинематографический режим"));
    public final BooleanSetting cinematic = this.register(new BooleanSetting("Кинематограф", "Плавное «дыхание» зума, лёгкое покачивание и крен кадра.", false));
    public final SliderSetting cinemaSpeed = this.register(new SliderSetting("Скорость кадра", "Как быстро движется кинематографическое покачивание.").range(10.0f, 100.0f).increment(5.0f).setValue(30.0f)).visible(this.cinematic::getValue);
    public final SliderSetting cinemaZoom = this.register(new SliderSetting("Дыхание зума, %", "Насколько заметно плавное изменение зума.").range(0.0f, 100.0f).increment(5.0f).setValue(40.0f)).visible(this.cinematic::getValue);

    private boolean primed;
    private float laggedYaw;
    private float laggedPitch;
    private float roll;
    private float kickPitch;
    private float kickYaw;
    private float kickRoll;
    private float kickFov;
    private float swayTime;
    private long lastFrameNanos;

    public CameraPlus() {
        super("Camera Plus", "Плавное следование взгляда, отдача при ударе и кинематографический режим камеры.", Category.VISUALS);
        instance = this;
    }

    public static CameraPlus getInstance() {
        if (instance == null) {
            instance = ModuleManager.get().get(CameraPlus.class);
        }
        return instance;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onDisable() {
        this.primed = false;
        this.roll = 0.0f;
        this.kickPitch = 0.0f;
        this.kickYaw = 0.0f;
        this.kickRoll = 0.0f;
        this.kickFov = 0.0f;
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        if (!this.isEnabled() || !this.recoil.getValue() || attackEntityEvent.isSynthetic()) {
            return;
        }
        float strength = this.recoilStrength.getValue() / 100.0f;
        this.kickPitch = Math.min(6.0f, this.kickPitch + 2.4f * strength);
        this.kickYaw = this.kickYaw + (float)(Math.random() - 0.5) * 1.1f * strength;
        this.kickRoll = this.kickRoll + (float)(Math.random() - 0.5) * 2.0f * strength;
        this.kickFov = Math.min(0.22f, this.kickFov + 0.045f * strength);
    }

    /** Кадровое обновление: интерполяция взгляда, затухание отдачи и кинематографическое покачивание. */
    private void frame(MinecraftClient client) {
        long now = System.nanoTime();
        float dt = this.lastFrameNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        float yaw = client.player.getYaw();
        float pitch = client.player.getPitch();
        if (!this.primed) {
            this.laggedYaw = yaw;
            this.laggedPitch = pitch;
            this.primed = true;
            return;
        }
        float yawDelta = CameraPlus.wrap(yaw - this.laggedYaw);
        float pitchDelta = pitch - this.laggedPitch;
        float catchUp = Math.max(0.05f, Math.min(1.0f, dt * (0.8f + this.smoothSpeed.getValue() / 100.0f * 12.0f)));
        this.laggedYaw += yawDelta * catchUp;
        this.laggedPitch += pitchDelta * catchUp;
        float decay = (float)Math.exp(-(double)dt * (2.0 + this.recoilSpeed.getValue() / 100.0f * 12.0f));
        this.kickPitch *= decay;
        this.kickYaw *= decay;
        this.kickRoll *= decay;
        this.kickFov *= decay;
        float targetRoll = 0.0f;
        if (this.lean.getValue()) {
            double side = CameraPlus.sideSpeed(client);
            targetRoll += (float)Math.max(-3.2, Math.min(3.2, -side * 1.9)) * this.leanStrength.getValue() / 100.0f;
        }
        this.roll += (targetRoll - this.roll) * Math.max(0.02f, Math.min(1.0f, dt * 7.0f));
    }

    /** Боковая скорость игрока относительно взгляда — по ней считается крен. */
    private static double sideSpeed(MinecraftClient client) {
        if (client.player == null) {
            return 0.0;
        }
        double yawRad = Math.toRadians((double)client.player.getYaw());
        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);
        return client.player.getVelocity().x * rightX + client.player.getVelocity().z * rightZ;
    }

    private static float wrap(float value) {
        float v = value % 360.0f;
        if (v > 180.0f) {
            v -= 360.0f;
        }
        if (v < -180.0f) {
            v += 360.0f;
        }
        return v;
    }

    /** Итоговая поправка проекции: крен, отдача и плавное следование. */
    public static Matrix4f applyProjection(Matrix4f original) {
        CameraPlus module = instance;
        if (module == null || !module.isEnabled() || original == null) {
            return original;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            module.primed = false;
            return original;
        }
        module.frame(client);
        float rollDeg = 0.0f;
        float pitchDeg = 0.0f;
        float yawDeg = 0.0f;
        if (module.smooth.getValue()) {
            float strength = module.smoothStrength.getValue() / 100.0f;
            yawDeg += CameraPlus.wrap(module.laggedYaw - client.player.getYaw()) * strength;
            pitchDeg += (module.laggedPitch - client.player.getPitch()) * strength;
        }
        if (module.lean.getValue()) {
            rollDeg += module.roll;
        }
        if (module.cinematic.getValue()) {
            float speed = module.cinemaSpeed.getValue() / 100.0f;
            module.swayTime += (float)(0.016 * (double)(0.4f + speed * 2.2f));
            rollDeg += (float)Math.sin((double)module.swayTime * 0.53) * 0.85f * speed;
            pitchDeg += (float)Math.sin((double)module.swayTime * 0.31) * 0.45f * speed;
            yawDeg += (float)Math.sin((double)module.swayTime * 0.24) * 0.4f * speed;
        }
        rollDeg += module.kickRoll;
        pitchDeg += module.kickPitch;
        yawDeg += module.kickYaw;
        if (Math.abs(rollDeg) < 0.002f && Math.abs(pitchDeg) < 0.002f && Math.abs(yawDeg) < 0.002f) {
            return original;
        }
        Matrix4f result = new Matrix4f(original);
        Matrix4f rotation = new Matrix4f().identity();
        rotation.rotateX((float)Math.toRadians((double)pitchDeg));
        rotation.rotateY((float)Math.toRadians((double)yawDeg));
        rotation.rotateZ((float)Math.toRadians((double)rollDeg));
        result.mul(rotation);
        return result;
    }

    /** Поправка зума: отдача и «дыхание» кинематографического режима. */
    public static float applyFov(float original) {
        CameraPlus module = instance;
        if (module == null || !module.isEnabled()) {
            return original;
        }
        float scale = 1.0f - module.kickFov;
        if (module.cinematic.getValue()) {
            float zoom = module.cinemaZoom.getValue() / 100.0f;
            scale *= 1.0f + (float)Math.sin((double)module.swayTime * 0.42) * 0.05f * zoom;
        }
        return original * Math.max(0.6f, Math.min(1.2f, scale));
    }
}
