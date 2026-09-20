package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import rtx.kimiko.api.events.impl.player.AttackEntityEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.color.RainbowLut;
import rtx.kimiko.utils.render.post.hitbubbles.HitBubblesRenderer;
import rtx.kimiko.utils.render.render2d.ClientPalette;

public final class HitBubbles
extends Module {
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private static final int DARK_SECOND_COLOR = new Color(16, 16, 16, 75).getRGB();
    private static HitBubbles instance;
    private final SeparatorSetting rippleSeparator = this.register(new SeparatorSetting("\u0412\u043e\u043b\u043d\u0430"));
    private final NumberSetting strength = this.register(new NumberSetting("\u0421\u0438\u043b\u0430", "\u041d\u0430\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u0438\u043b\u044c\u043d\u043e \u0432\u043e\u043b\u043d\u0430 \u0438\u0441\u043a\u0440\u0438\u0432\u043b\u044f\u0435\u0442 \u043c\u0438\u0440.", 1.0, 0.2, 3.0, 0.1));
    private final NumberSetting size = this.register(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440", "\u0420\u0430\u0434\u0438\u0443\u0441 \u0432\u043e\u043b\u043d\u044b \u043a\u0430\u043a \u0434\u043e\u043b\u044f \u044d\u043a\u0440\u0430\u043d\u0430.", 0.35, 0.1, 0.6, 0.05));
    private final NumberSetting duration = this.register(new NumberSetting("\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c", "\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0432\u043e\u043b\u043d\u044b \u0432 \u043c\u0438\u043b\u043b\u0438\u0441\u0435\u043a\u0443\u043d\u0434\u0430\u0445.", 800.0, 300.0, 2000.0, 50.0));
    private final SeparatorSetting warpSeparator = this.register(new SeparatorSetting("\u0418\u0441\u043a\u0440\u0438\u0432\u043b\u0435\u043d\u0438\u0435"));
    private final BooleanSetting warp = this.register(new BooleanSetting("\u0418\u0441\u043a\u0440\u0438\u0432\u043b\u0435\u043d\u0438\u0435", "\u0414\u043e\u0431\u0430\u0432\u043b\u044f\u0435\u0442 \u0432\u043e\u043b\u043d\u0438\u0441\u0442\u0443\u044e \u0442\u0443\u0440\u0431\u0443\u043b\u0435\u043d\u0442\u043d\u043e\u0441\u0442\u044c, \u0438\u0441\u043a\u0440\u0438\u0432\u043b\u044f\u044e\u0449\u0443\u044e \u043c\u0438\u0440 \u0432\u043d\u0443\u0442\u0440\u0438 \u043f\u0443\u0437\u044b\u0440\u044f.", false));
    private final NumberSetting warpStrength = this.register(new NumberSetting("\u0421\u0438\u043b\u0430 \u0438\u0441\u043a\u0440\u0438\u0432\u043b\u0435\u043d\u0438\u044f", "\u0421\u0438\u043b\u0430 \u0432\u043d\u0443\u0442\u0440\u0435\u043d\u043d\u0435\u0433\u043e \u0432\u043e\u043b\u043d\u0438\u0441\u0442\u043e\u0433\u043e \u0438\u0441\u043a\u0440\u0438\u0432\u043b\u0435\u043d\u0438\u044f.", 0.5, 0.1, 2.0, 0.1).visibleWhen(this.warp::getValue));
    private final SeparatorSetting effectSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442"));
    private final NumberSetting saturation = this.register(new NumberSetting("\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c", "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u0446\u0432\u0435\u0442\u0430 \u0432\u043d\u0443\u0442\u0440\u0438 \u043f\u0443\u0437\u044b\u0440\u044f: -1 \u0441\u0435\u0440\u044b\u0439, 0 \u043e\u0431\u044b\u0447\u043d\u044b\u0439, +1 \u044f\u0440\u043a\u0438\u0439.", 0.0, -1.0, 1.0, 0.05));
    private final SeparatorSetting tintSeparator = this.register(new SeparatorSetting("\u041f\u043e\u0434\u043a\u0440\u0430\u0441\u043a\u0430"));
    private final BooleanSetting tint = this.register(new BooleanSetting("\u041f\u043e\u0434\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u0442\u044c \u0446\u0432\u0435\u0442\u043e\u043c", "\u041f\u043e\u0434\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u0435\u0442 \u043e\u0431\u043b\u0430\u0441\u0442\u044c \u0432\u043e\u043b\u043d\u044b \u0445\u0438\u0442\u0431\u0430\u0431\u043b\u0430 \u0446\u0432\u0435\u0442\u043e\u043c \u043d\u0438\u0436\u0435.", false));
    private final NumberSetting tintStrength = this.register(new NumberSetting("\u0421\u0438\u043b\u0430 \u0446\u0432\u0435\u0442\u0430", "\u041d\u0430\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u0438\u043b\u044c\u043d\u043e \u043e\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0432\u043e\u043b\u043d\u0430: 0% \u043d\u0435\u0442, 100% \u043f\u043e\u043b\u043d\u044b\u0439 \u0446\u0432\u0435\u0442.", 55.0, 0.0, 100.0, 1.0).visibleWhen(this.tint::getValue));
    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442 \u0432\u043e\u043b\u043d\u044b").visible(this::colorsApply));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430 \u043f\u043e\u0434\u043a\u0440\u0430\u0441\u043a\u0438.", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u0432\u043e\u0439"));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0441\u0432\u043e\u0439 \u0446\u0432\u0435\u0442.", false));
    private final ColorSetting customColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 \u043f\u043e\u0434\u043a\u0440\u0430\u0441\u043a\u0438.", new Color(255, 255, 255, 255)));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u043f\u043e\u0434\u043a\u0440\u0430\u0441\u043a\u0438.", new Color(ColorUtil.lerpColor(-1, DARK_SECOND_COLOR, 0.7f), true)));
    private final List<Ripple> ripples = new ArrayList<Ripple>();

    public HitBubbles() {
        super("Hit Bubbles", "\u0418\u0441\u043a\u0430\u0436\u0430\u0435\u0442 \u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u043e \u0432\u043e\u043b\u043d\u043e\u0439 \u0432 \u043c\u0435\u0441\u0442\u0435 \u0443\u0434\u0430\u0440\u0430.", Category.VISUALS);
        instance = this;
        this.colorMode.visibleWhen(this::colorsApply);
        this.useSecondColor.visibleWhen(() -> this.colorsApply() && this.colorMode.is(COLOR_CUSTOM));
        this.customColor.visibleWhen(() -> this.colorsApply() && this.colorMode.is(COLOR_CUSTOM));
        this.customSecondColor.visibleWhen(() -> this.colorsApply() && this.colorMode.is(COLOR_CUSTOM) && this.useSecondColor.getValue());
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public static HitBubbles getInstance() {
        HitBubbles hitBubbles = ModuleManager.get().get(HitBubbles.class);
        return hitBubbles != null ? hitBubbles : instance;
    }

    private static int rainbow(int n, int n2, float f, float f2, float f3) {
        int n3 = (int)((System.currentTimeMillis() / (long)Math.max(1, n) + (long)n2) % 360L);
        int n4 = RainbowLut.sample((int)n3, (float)f, (float)f2);
        return ColorUtil.rgba(n4 >>> 16 & 0xFF, n4 >>> 8 & 0xFF, n4 & 0xFF, Math.round(MathHelper.clamp((float)f3, (float)0.0f, (float)1.0f) * 255.0f));
    }

    private static void putColor(float[] fArray, int n, int n2) {
        fArray[n] = (float)(n2 >> 16 & 0xFF) / 255.0f;
        fArray[n + 1] = (float)(n2 >> 8 & 0xFF) / 255.0f;
        fArray[n + 2] = (float)(n2 & 0xFF) / 255.0f;
    }

    private static int fade(int n, int n2, int n3, int n4) {
        int n5 = (int)((System.currentTimeMillis() / (long)Math.max(1, n) + (long)n2) % 360L);
        n5 = n5 >= 180 ? 360 - n5 : n5;
        return ColorUtil.lerpColor(n3, n4, (float)n5 / 180.0f);
    }

    @Override
    protected void onDisable() {
        this.ripples.clear();
    }

    @EventHandler
    private void onAttack(AttackEntityEvent attackEntityEvent) {
        Entity entity = attackEntityEvent.getTarget();
        if (entity == null) {
            return;
        }
        Vec3d vec3d = entity.getEntityPos().add(0.0, (double)entity.getHeight() * 0.5, 0.0);
        this.ripples.add(new Ripple(vec3d, System.currentTimeMillis()));
        while (this.ripples.size() > 16) {
            this.ripples.remove(0);
        }
    }

    private int getColor(int n, float f) {
        int n2;
        int n3;
        if (this.colorMode.is(COLOR_RAINBOW)) {
            return HitBubbles.rainbow(8, n, 1.0f, 1.0f, f);
        }
        if (this.colorMode.is(COLOR_CLIENT)) {
            int[] nArray = ClientPalette.colors();
            if (nArray != null && nArray.length >= 2) {
                return ColorUtil.multAlpha(HitBubbles.paletteFade(8, n, nArray), f);
            }
            InterfaceModule interfaceModule = InterfaceModule.getInstance();
            n3 = interfaceModule != null ? interfaceModule.clientPrimaryColorOpaque() : -1;
            n2 = interfaceModule != null && interfaceModule.usesSecondClientColor() ? interfaceModule.clientSecondaryColorOpaque() : n3;
        } else {
            n3 = this.customColor.getColor();
            int n4 = n2 = this.useSecondColor.getValue() ? this.customSecondColor.getColor() : this.customColor.getColor();
        }
        if (n3 == n2) {
            return ColorUtil.multAlpha(n3, f);
        }
        return ColorUtil.multAlpha(HitBubbles.fade(8, n, n3, n2), f);
    }

    public void onAfterWorld(Framebuffer framebuffer, Matrix4f matrix4f, Matrix4f matrix4f2, Camera camera) {
        if (framebuffer == null || camera == null || this.ripples.isEmpty()) {
            return;
        }
        long l = System.currentTimeMillis();
        float f = Math.max(1.0f, this.duration.getFloat());
        float f2 = this.size.getFloat();
        float f3 = HitBubbles.clamp(f2 * 0.4f, 0.06f, 0.22f);
        float f4 = 0.018f * this.strength.getFloat();
        float f5 = (float)framebuffer.textureWidth / (float)Math.max(1, framebuffer.textureHeight);
        Vec3d vec3d = camera.getCameraPos();
        float[] fArray = new float[152];
        int n = 0;
        Iterator<Ripple> iterator = this.ripples.iterator();
        while (iterator.hasNext()) {
            Ripple ripple = iterator.next();
            float f6 = (float)(l - ripple.spawnMs) / f;
            if (f6 >= 1.0f) {
                iterator.remove();
                continue;
            }
            if (n >= 16) continue;
            Vector4f vector4f = new Vector4f((float)(ripple.pos.x - vec3d.x), (float)(ripple.pos.y - vec3d.y), (float)(ripple.pos.z - vec3d.z), 1.0f);
            matrix4f.transform(vector4f);
            matrix4f2.transform(vector4f);
            if (vector4f.w <= 1.0E-4f) continue;
            float f7 = vector4f.x / vector4f.w * 0.5f + 0.5f;
            float f8 = vector4f.y / vector4f.w * 0.5f + 0.5f;
            if (f7 < -0.5f || f7 > 1.5f || f8 < -0.5f || f8 > 1.5f) continue;
            float f9 = HitBubbles.clamp(f6 / 0.1f, 0.0f, 1.0f);
            float f10 = HitBubbles.clamp((1.0f - f6) / 0.3f, 0.0f, 1.0f);
            float f11 = f9 * f10;
            float f12 = f4 * f11;
            int n2 = 24 + n * 8;
            fArray[n2] = f7;
            fArray[n2 + 1] = f8;
            fArray[n2 + 2] = f6 * f2;
            fArray[n2 + 3] = f3;
            fArray[n2 + 4] = f12;
            fArray[n2 + 5] = f11;
            fArray[n2 + 6] = f2;
            ++n;
        }
        if (n == 0) {
            return;
        }
        fArray[0] = n;
        fArray[1] = f5;
        fArray[2] = (float)(l % 100000L) / 1000.0f;
        fArray[3] = this.warp.getValue() ? 0.01f * this.warpStrength.getFloat() : 0.0f;
        fArray[4] = HitBubbles.clamp(1.0f + this.saturation.getFloat(), 0.0f, 2.0f);
        if (this.tint.getValue()) {
            fArray[5] = HitBubbles.clamp(this.tintStrength.getFloat() / 100.0f, 0.0f, 1.0f);
            HitBubbles.putColor(fArray, 8, this.getColor(0, 1.0f));
            HitBubbles.putColor(fArray, 12, this.getColor(90, 1.0f));
            HitBubbles.putColor(fArray, 16, this.getColor(180, 1.0f));
            HitBubbles.putColor(fArray, 20, this.getColor(270, 1.0f));
        }
        HitBubblesRenderer.apply((Framebuffer)framebuffer, (float[])fArray);
    }

    private boolean colorsApply() {
        return this.tint.getValue();
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

    public static record Ripple(Vec3d pos, long spawnMs) {}
}

