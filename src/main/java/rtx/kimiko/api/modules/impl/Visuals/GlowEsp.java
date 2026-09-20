package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import rtx.kimiko.api.events.impl.render.WorldRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.MultiSelectSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.api.modules.settings.impl.SliderSetting;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.color.RainbowLut;
import rtx.kimiko.utils.render.post.glowesp.GlowEspRenderer;
import rtx.kimiko.utils.render.render2d.ClientPalette;
import rtx.kimiko.utils.storage.friend.FriendUtils;

public class GlowEsp
extends Module {
    private static final String TARGET_PLAYERS = "\u0418\u0433\u0440\u043e\u043a\u0438";
    private static final String TARGET_MOBS = "\u041c\u043e\u0431\u044b";
    private static final String TARGET_SELF = "\u0421\u0435\u0431\u044f";
    private static final String TARGET_FRIENDS = "\u0414\u0440\u0443\u0437\u044c\u044f";
    private static final String MODE_BOTH = "\u041e\u0431\u0430";
    private static final String MODE_GAUSSIAN = "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435";
    private static final String MODE_OUTLINE = "\u041a\u043e\u043d\u0442\u0443\u0440";
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private static final int DARK_SECOND = new Color(16, 16, 16, 75).getRGB();
    private static GlowEsp instance;
    private final MultiSelectSetting targets = this.register(new MultiSelectSetting("\u0426\u0435\u043b\u0438", "\u041a\u043e\u0433\u043e \u043f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0442\u044c \u0447\u0435\u0440\u0435\u0437 GlowEsp.").value("\u0418\u0433\u0440\u043e\u043a\u0438", "\u041c\u043e\u0431\u044b", "\u0421\u0435\u0431\u044f", "\u0414\u0440\u0443\u0437\u044c\u044f").selected("\u0418\u0433\u0440\u043e\u043a\u0438", "\u041c\u043e\u0431\u044b", "\u0414\u0440\u0443\u0437\u044c\u044f"));
    private final ModeSetting renderMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u041a\u0430\u043a\u043e\u0439 \u044d\u0444\u0444\u0435\u043a\u0442 \u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c.", "\u041e\u0431\u0430", "\u041e\u0431\u0430", "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", "\u041a\u043e\u043d\u0442\u0443\u0440"));
    private final SeparatorSetting glowSeparator = this.register(new SeparatorSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435"));
    private final SliderSetting glowRadius = this.register(new SliderSetting("\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u0420\u0430\u0434\u0438\u0443\u0441 \u0433\u0430\u0443\u0441\u0441-\u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f.").range(1.0f, 20.0f).increment(0.5f).setValue(7.0f).visible(() -> !this.renderMode.is(MODE_OUTLINE)));
    private final SliderSetting glowStrength = this.register(new SliderSetting("\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0432\u043d\u0435\u0448\u043d\u0435\u0433\u043e \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f.").range(0.1f, 3.0f).increment(0.05f).setValue(1.15f).visible(() -> !this.renderMode.is(MODE_OUTLINE)));
    private final BooleanSetting blending = this.register(new BooleanSetting("\u0421\u043c\u0435\u0448\u0438\u0432\u0430\u043d\u0438\u0435", "\u0410\u0434\u0434\u0438\u0442\u0438\u0432\u043d\u043e \u043f\u043e\u0434\u043c\u0435\u0448\u0438\u0432\u0430\u0442\u044c \u0446\u0432\u0435\u0442 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f \u043a \u0441\u0446\u0435\u043d\u0435 (\u043a\u0430\u043a \u0441\u0432\u0435\u0442/bloom).", false).visibleWhen(() -> !this.renderMode.is(MODE_OUTLINE)));
    private final SeparatorSetting outlineSeparator = this.register(new SeparatorSetting("\u041a\u043e\u043d\u0442\u0443\u0440"));
    private final SliderSetting outlineWidth = this.register(new SliderSetting("\u0428\u0438\u0440\u0438\u043d\u0430 \u043a\u043e\u043d\u0442\u0443\u0440\u0430", "\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u043e\u0431\u0432\u043e\u0434\u043a\u0438 \u0432\u043e\u043a\u0440\u0443\u0433 \u043c\u043e\u0434\u0435\u043b\u0438.").range(0.1f, 0.5f).increment(0.05f).setValue(0.3f).visible(() -> !this.renderMode.is(MODE_GAUSSIAN)));
    private final SliderSetting outlineBright = this.register(new SliderSetting("\u0412\u044b\u0441\u0432\u0435\u0442\u043b\u0435\u043d\u0438\u0435 \u043a\u043e\u043d\u0442\u0443\u0440\u0430", "\u041d\u0430\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u043e\u0431\u0432\u043e\u0434\u043a\u0430 \u0441\u043c\u0435\u0448\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0441 \u0431\u0435\u043b\u044b\u043c.").range(0.0f, 1.0f).increment(0.01f).setValue(0.35f).visible(() -> !this.renderMode.is(MODE_GAUSSIAN)));
    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442"));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0420\u0435\u0436\u0438\u043c \u043e\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u043d\u0438\u044f GlowEsp.", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u0432\u043e\u0439"));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0432 \u0440\u0435\u0436\u0438\u043c\u0435 \u00ab\u0421\u0432\u043e\u0439\u00bb.", false));
    private final ColorSetting customColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 GlowEsp.", new Color(120, 170, 255, 220)));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 GlowEsp.", new Color(ColorUtil.lerpColor(new Color(120, 170, 255, 220).getRGB(), DARK_SECOND, 0.7f), true)));

    public GlowEsp() {
        super("Shader ESP", "\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438 \u0448\u0435\u0439\u0434\u0435\u0440\u043d\u044b\u043c \u043a\u043e\u043d\u0442\u0443\u0440\u043e\u043c \u0438 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435\u043c.", Category.VISUALS);
        instance = this;
        this.useSecondColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
        this.customColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
        this.customSecondColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM) && this.useSecondColor.getValue());
    }

    public static GlowEsp getInstance() {
        GlowEsp glowEsp = ModuleManager.get().get(GlowEsp.class);
        return glowEsp != null ? glowEsp : instance;
    }

    private static int interpolate(int n, int n2, int n3) {
        if (n == n2) {
            return n;
        }
        int n4 = (int)((System.currentTimeMillis() / 8L + (long)n3) % 360L);
        float f = n4 > 180 ? (float)(360 - n4) / 180.0f : (float)n4 / 180.0f;
        return ColorUtil.lerpColor(n, n2, f);
    }

    private static int rainbow(long l) {
        int n = (int)(l % 360L);
        int n2 = RainbowLut.sample((int)n, (float)1.0f, (float)1.0f);
        return n2 | 0xFF000000;
    }

    @Override
    protected void onDisable() {
        GlowEspRenderer.clear();
    }

    private int[] glowGradientColors() {
        if (this.colorMode.is(COLOR_RAINBOW)) {
            long l = System.currentTimeMillis() / 8L;
            return new int[]{GlowEsp.rainbow(l), GlowEsp.rainbow(l + 90L), GlowEsp.rainbow(l + 180L), GlowEsp.rainbow(l + 270L)};
        }
        if (this.colorMode.is(COLOR_CLIENT)) {
            InterfaceModule interfaceModule = InterfaceModule.getInstance();
            int n = interfaceModule != null ? interfaceModule.gradientStyleId() : 0;
            float f = ClientPalette.phase() * (n == 2 ? 20.0f : 1.0f);
            return new int[]{ClientPalette.loopColor(f) | 0xFF000000, ClientPalette.loopColor(f + 0.25f) | 0xFF000000, ClientPalette.loopColor(f + 0.5f) | 0xFF000000, ClientPalette.loopColor(f + 0.75f) | 0xFF000000};
        }
        int n = this.customColor.getColor();
        int n2 = this.useSecondColor.getValue() ? this.customSecondColor.getColor() : n;
        return new int[]{GlowEsp.interpolate(n, n2, 0), GlowEsp.interpolate(n, n2, 90), GlowEsp.interpolate(n, n2, 180), GlowEsp.interpolate(n, n2, 270)};
    }

    private boolean shouldRenderPlayer(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (abstractClientPlayerEntity.isSpectator()) {
            return false;
        }
        if (abstractClientPlayerEntity == this.mc.player) {
            return this.targets.isSelected(TARGET_SELF) && this.mc.options.getPerspective() != Perspective.FIRST_PERSON;
        }
        if (!this.targets.isSelected(TARGET_PLAYERS)) {
            return false;
        }
        return this.targets.isSelected(TARGET_FRIENDS) || !FriendUtils.isFriend(abstractClientPlayerEntity.getName().getString());
    }

    private boolean shouldRenderEntity(LivingEntity livingEntity) {
        if (livingEntity == null || !livingEntity.isAlive() || livingEntity.isRemoved()) {
            return false;
        }
        if (livingEntity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
            return this.shouldRenderPlayer(abstractClientPlayerEntity);
        }
        if (livingEntity instanceof MobEntity) {
            return this.targets.isSelected(TARGET_MOBS) && !livingEntity.isSpectator();
        }
        return false;
    }

    private int[] outlineGradientColors(int[] nArray) {
        int[] nArray2 = new int[nArray.length];
        float f = this.outlineBright.getFloat();
        for (int i = 0; i < nArray.length; ++i) {
            int n = ColorUtil.rgba(255, 255, 255, ColorUtil.alpha(nArray[i]));
            nArray2[i] = ColorUtil.lerpColor(nArray[i], n, f);
        }
        return nArray2;
    }

    private List<LivingEntity> collectTargets(Frustum frustum, Vec3d vec3d) {
        EntityRenderManager entityRenderManager = this.mc.getEntityRenderDispatcher();
        ArrayList<LivingEntity> arrayList = new ArrayList<LivingEntity>();
        for (Entity entity : this.mc.world.getEntities()) {
            LivingEntity livingEntity;
            if (!(entity instanceof LivingEntity) || !this.shouldRenderEntity(livingEntity = (LivingEntity)entity) || !entityRenderManager.shouldRender((Entity)livingEntity, frustum, vec3d.x, vec3d.y, vec3d.z)) continue;
            arrayList.add(livingEntity);
        }
        return arrayList;
    }

    @EventHandler
    private void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        Vec3d vec3d = worldRenderEvent.getCamera() != null ? worldRenderEvent.getCamera().getCameraPos() : this.mc.gameRenderer.getCamera().getCameraPos();
        Frustum frustum = new Frustum(worldRenderEvent.getPositionMatrix(), worldRenderEvent.getProjectionMatrix());
        frustum.setPosition(vec3d.x, vec3d.y, vec3d.z);
        List<LivingEntity> list = this.collectTargets(frustum, vec3d);
        if (list.isEmpty()) {
            return;
        }
        int[] nArray = this.glowGradientColors();
        int[] nArray2 = this.outlineGradientColors(nArray);
        GlowEspRenderer.render(list, (float)worldRenderEvent.getPartialTicks(), (MatrixStack)worldRenderEvent.getStack(), (Vec3d)vec3d, (!this.renderMode.is(MODE_OUTLINE) ? 1 : 0) != 0, (!this.renderMode.is(MODE_GAUSSIAN) ? 1 : 0) != 0, (float)this.glowRadius.getFloat(), (float)this.glowStrength.getFloat(), (float)this.outlineWidth.getFloat(), (boolean)this.blending.getValue(), (int[])nArray, (int[])nArray2);
    }
}

