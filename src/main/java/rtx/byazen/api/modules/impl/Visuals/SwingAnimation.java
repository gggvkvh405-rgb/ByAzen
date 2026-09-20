package rtx.byazen.api.modules.impl.Visuals;
import rtx.byazen.api.events.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.NumberSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.UI;
import rtx.byazen.mixin.accessor.LivingEntityAccessor;

public class SwingAnimation
extends Module {
    private static final long AUTO_SWING_INTERVAL_MS = 1000L;
    private static final float BASE_X = 0.56f;
    private static final float BASE_Y = -0.52f;
    private static final float BASE_Z = -0.72f;
    private static SwingAnimation instance;
    private final SeparatorSetting animationSeparator = this.register(new SeparatorSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f"));
    private final BooleanSetting autoSwing = this.register(new BooleanSetting("\u0410\u0432\u0442\u043e \u0432\u0437\u043c\u0430\u0445", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043b\u043e\u043a\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440 \u0432\u0437\u043c\u0430\u0445\u0430 \u0432 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430\u0445 \u043a\u0430\u0436\u0434\u044b\u0435 \u0434\u0432\u0435 \u0441\u0435\u043a\u0443\u043d\u0434\u044b.", false));
    private final ModeSetting swingType = this.register(new ModeSetting("\u0422\u0438\u043f", "\u041f\u0440\u0435\u0441\u0435\u0442 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0432\u0437\u043c\u0430\u0445\u0430.", "\u0412\u0437\u043c\u0430\u0445", "\u0412\u0437\u043c\u0430\u0445", "\u0412\u0437\u043c\u0430\u0445 2", "\u0421\u0434\u0432\u0438\u0433", "\u0421\u043b\u043e\u043c", "\u0412\u044b\u043f\u0430\u0434", "\u041a\u043e\u043f\u044c\u0451", "\u0412\u043d\u0438\u0437", "\u0416\u0435\u043b\u0435", "\u0428\u043b\u0435\u043f\u043e\u043a", "\u0411\u043e\u043d\u043a", "\u041a\u0440\u0443\u0433", "\u0420\u0435\u0430\u043b\u0438\u0437\u043c", "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442"));
    private final NumberSetting hitStrength = this.register(new NumberSetting("\u0421\u0438\u043b\u0430", "\u0421\u0438\u043b\u0430 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0432\u0437\u043c\u0430\u0445\u0430.", 1.0, 0.5, 3.0, 0.05));
    private final NumberSetting swingSpeed = this.register(new NumberSetting("\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c", "\u041c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c \u0434\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u0438 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0432\u0437\u043c\u0430\u0445\u0430.", 1.0, 0.5, 4.0, 0.05));
    private final BooleanSetting onlyOnTarget = this.register(new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u043d\u0430\u0432\u043e\u0434\u043a\u0435", "\u041f\u0440\u043e\u0438\u0433\u0440\u044b\u0432\u0430\u0442\u044c \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044e \u0442\u043e\u043b\u044c\u043a\u043e \u043a\u043e\u0433\u0434\u0430 \u043f\u0440\u0438\u0446\u0435\u043b \u043d\u0430\u0432\u0435\u0434\u0451\u043d \u043d\u0430 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u043e.", false));
    private int realismSlashSide = -1;
    private boolean realismSlashReady = true;
    private boolean previewWasOpen;
    private long nextPreviewAt;
    private long previewSwingUntil;

    public SwingAnimation() {
        super("Swing Animation", "\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0435 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0432\u0437\u043c\u0430\u0445\u0430 \u043e\u0442 \u043f\u0435\u0440\u0432\u043e\u0433\u043e \u043b\u0438\u0446\u0430.", Category.VISUALS);
        instance = this;
    }

    public static SwingAnimation getInstance() {
        SwingAnimation swingAnimation = ModuleManager.get().get(SwingAnimation.class);
        return swingAnimation != null ? swingAnimation : instance;
    }

    @Override
    protected void onDisable() {
        this.previewWasOpen = false;
        this.nextPreviewAt = 0L;
        this.previewSwingUntil = 0L;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        boolean bl;
        if (!tickEvent.isPost() || this.mc.player == null) {
            return;
        }
        boolean bl2 = bl = this.autoSwing.getValue() && UI.isSettingsOpenFor(this.getName());
        if (!bl) {
            this.previewWasOpen = false;
            this.nextPreviewAt = 0L;
            return;
        }
        long l = System.currentTimeMillis();
        if (!this.previewWasOpen || l >= this.nextPreviewAt) {
            this.triggerLocalPreview(l);
            this.nextPreviewAt = l + 1000L;
        }
        this.previewWasOpen = true;
    }

    private float easeOutBack(float f) {
        float f2 = 1.70158f;
        float f3 = f2 + 1.0f;
        return 1.0f + f3 * (float)Math.pow(f - 1.0f, 3.0) + f2 * (float)Math.pow(f - 1.0f, 2.0);
    }

    public static boolean applyAnimation(MatrixStack matrixStack, Hand hand, float f) {
        if (f <= 0.0f || f >= 1.0f) {
            return false;
        }
        Arm arm;
        HitResult hitResult;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        SwingAnimation swingAnimation = SwingAnimation.getInstance();
        if (swingAnimation == null || !swingAnimation.isEnabled() || minecraftClient.player == null || matrixStack == null) {
            return false;
        }
        if (swingAnimation.swingType.is("\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442")) {
            return false;
        }
        ItemStack itemStack = minecraftClient.player.getStackInHand(hand);
        if (itemStack.getItem() instanceof TridentItem || itemStack.getItem() instanceof CrossbowItem) {
            return false;
        }
        if (minecraftClient.player.isUsingItem() && minecraftClient.player.getActiveHand() == hand) {
            return false;
        }
        if (hand != Hand.MAIN_HAND) {
            return false;
        }
        if (!(!swingAnimation.onlyOnTarget.getValue() || System.currentTimeMillis() < swingAnimation.previewSwingUntil || (minecraftClient.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity))) {
            return false;
        }
        arm = minecraftClient.player != null ? minecraftClient.player.getMainArm() : (Arm)minecraftClient.options.getMainArm().getValue();
        int n = arm == Arm.RIGHT ? 1 : -1;
        float f2 = MathHelper.sin((double)(f * f * (float)Math.PI));
        float f3 = MathHelper.sin((double)(MathHelper.sqrt((float)f) * (float)Math.PI));
        float f4 = (float)Math.sin((double)f * Math.PI);
        float f5 = swingAnimation.hitStrength.getFloat();
        matrixStack.translate((float)n * 0.56f, -0.52f, -0.72f);
        switch (swingAnimation.swingType.getSelected()) {
            case "\u0412\u0437\u043c\u0430\u0445 2": {
                matrixStack.scale(1.0f, 1.0f, f4 + 1.0f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f * f4 * (f5 * 0.3f)));
                break;
            }
            case "\u0421\u0434\u0432\u0438\u0433": {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * 90.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)n * -60.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - 20.0f * f4 * f5));
                break;
            }
            case "\u0421\u043b\u043e\u043c": {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * 90.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)n * -30.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - 20.0f * f4 * f5));
                break;
            }
            case "\u0412\u044b\u043f\u0430\u0434": {
                matrixStack.translate(0.0f, 0.0f, -f4 * 0.2f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * 45.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * f2 * -20.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)n * f3 * -20.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(f3 * -Math.clamp(f5 * 55.0f, 55.0f, 80.0f)));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * 10.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-80.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * 60.0f));
                break;
            }
            case "\u041a\u043e\u043f\u044c\u0451": {
                matrixStack.translate(0.0f, 0.0f, -f4 * 0.2f);
                matrixStack.scale(1.0f, 1.0f, f4 * (f5 * 0.35f) + 1.0f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
                break;
            }
            case "\u0412\u043d\u0438\u0437": {
                matrixStack.translate(-f4 * 0.5f, f4 * 0.25f, 0.0f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * 65.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-110.0f - 80.0f * f4));
                break;
            }
            case "\u0416\u0435\u043b\u0435": {
                matrixStack.translate(0.0f, 0.0f, -f4 * 0.2f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * 35.0f));
                matrixStack.scale(1.0f, 1.0f, f4 * (f * 0.65f * f5 * 0.2f) + 1.0f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
                break;
            }
            case "\u0428\u043b\u0435\u043f\u043e\u043a": {
                matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_X.rotationDegrees(f3 * 55.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(f3 * f2 * -120.0f * f5));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * (f3 * 60.0f)));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(f3 * 55.0f));
                break;
            }
            case "\u0411\u043e\u043d\u043a": {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(15.0f * (float)n));
                float f6 = swingAnimation.easeOutElastic(f * f);
                matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_X.rotationDegrees(95.0f * f6));
                break;
            }
            case "\u041a\u0440\u0443\u0433": {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(15.0f * (float)n));
                float f7 = MathHelper.lerp((float)(1.0f - f), (float)f, (float)(f * 0.5f));
                float f8 = swingAnimation.easeOutBack(f7);
                matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_X.rotationDegrees(360.0f * f8 + 45.0f));
                break;
            }
            case "\u0420\u0435\u0430\u043b\u0438\u0437\u043c": {
                swingAnimation.applyRealismPreset(matrixStack, n, f, f4, f5);
                break;
            }
            case "\u0412\u0437\u043c\u0430\u0445": {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * (45.0f + f2 * -20.0f)));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)n * f3 * -20.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(f3 * -f5 * 22.0f));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)n * -45.0f));
                break;
            }
            default: {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(45.0f * (float)n));
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-45.0f * (float)n));
            }
        }
        return true;
    }

    private void triggerLocalPreview(long l) {
        LivingEntityAccessor livingEntityAccessor = (LivingEntityAccessor)(Object)this.mc.player;
        livingEntityAccessor.byazen_setSwingTime(-1);
        livingEntityAccessor.byazen_setSwinging(true);
        livingEntityAccessor.byazen_setSwingingArm(Hand.MAIN_HAND);
        Integer n = SwingAnimation.currentSwingDuration();
        this.previewSwingUntil = l + (n == null ? 400L : (long)n.intValue() * 50L + 100L);
    }

    private float easeOutElastic(float f) {
        if (f == 0.0f) {
            return 0.0f;
        }
        if (f == 1.0f) {
            return 1.0f;
        }
        double d = 2.0943951023931953;
        return (float)(Math.pow(2.0, -10.0f * f) * Math.sin(((double)(f * 10.0f) - 0.75) * d) + 1.0);
    }

    private void applyRealismPreset(MatrixStack matrixStack, int n, float f, float f2, float f3) {
        if (f < 0.12f && this.realismSlashReady) {
            this.realismSlashSide *= -1;
            this.realismSlashReady = false;
        } else if (f > 0.78f) {
            this.realismSlashReady = true;
        }
        int n2 = this.realismSlashSide;
        float f4 = (float)(n * n2) * 22.0f * f2 * MathHelper.clamp((float)f3, (float)0.5f, (float)2.0f);
        float f5 = -90.0f * f2 * (f3 * 0.3f);
        float f6 = 45.0f * this.easeOutElastic(f * f) * f2;
        matrixStack.scale(1.0f, 1.0f, f2 + 1.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f4));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(f5));
        matrixStack.multiply((Quaternionfc)RotationAxis.NEGATIVE_X.rotationDegrees(f6));
    }

    public static Integer currentSwingDuration() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        SwingAnimation swingAnimation = SwingAnimation.getInstance();
        if (swingAnimation == null || !swingAnimation.isEnabled() || minecraftClient.player == null || swingAnimation.swingType.is("\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442")) {
            return null;
        }
        float f = swingAnimation.swingSpeed.getFloat();
        f = StatusEffectUtil.hasHaste((LivingEntity)minecraftClient.player) ? (f *= (float)(6 - (1 + StatusEffectUtil.getHasteAmplifier((LivingEntity)minecraftClient.player)))) : (f *= minecraftClient.player.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? (float)(6 + (1 + minecraftClient.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2) : 6.0f);
        return Math.max(1, (int)f);
    }
}

