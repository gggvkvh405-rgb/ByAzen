package rtx.byazen.api.modules.impl.Visuals;
import rtx.byazen.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.MultiSelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.color.ColorUtil;
import rtx.byazen.utils.color.RainbowLut;
import rtx.byazen.utils.render.post.glowesp.GlowEspRenderer;
import rtx.byazen.utils.render.render2d.ClientPalette;
import rtx.byazen.utils.storage.friend.FriendUtils;

public class GlowEsp
extends Module {
    private static final String TARGET_PLAYERS = "\u0418\u0433\u0440\u043e\u043a\u0438";
    private static final String TARGET_MOBS = "\u041c\u043e\u0431\u044b";
    private static final String TARGET_SELF = "\u0421\u0435\u0431\u044f";
    private static final String TARGET_FRIENDS = "\u0414\u0440\u0443\u0437\u044c\u044f";
    private static final String TARGET_ITEMS = "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b";
    private static final String TARGET_CONTAINERS = "\u041a\u043e\u043d\u0442\u0435\u0439\u043d\u0435\u0440\u044b";
    private static final String MODE_BOTH = "\u041e\u0431\u0430";
    private static final String MODE_GAUSSIAN = "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435";
    private static final String MODE_OUTLINE = "\u041a\u043e\u043d\u0442\u0443\u0440";
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private static final int DARK_SECOND = new Color(16, 16, 16, 75).getRGB();
    private static GlowEsp instance;
    private final MultiSelectSetting targets = this.register(new MultiSelectSetting("\u0426\u0435\u043b\u0438", "\u041a\u043e\u0433\u043e \u043f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0442\u044c \u0447\u0435\u0440\u0435\u0437 GlowEsp.").value("\u0418\u0433\u0440\u043e\u043a\u0438", "\u041c\u043e\u0431\u044b", "\u0421\u0435\u0431\u044f", "\u0414\u0440\u0443\u0437\u044c\u044f", "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", "\u041a\u043e\u043d\u0442\u0435\u0439\u043d\u0435\u0440\u044b").selected("\u0418\u0433\u0440\u043e\u043a\u0438", "\u041c\u043e\u0431\u044b", "\u0414\u0440\u0443\u0437\u044c\u044f"));
    private final ModeSetting renderMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u041a\u0430\u043a\u043e\u0439 \u044d\u0444\u0444\u0435\u043a\u0442 \u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c.", "\u041e\u0431\u0430", "\u041e\u0431\u0430", "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", "\u041a\u043e\u043d\u0442\u0443\u0440"));
    private final SeparatorSetting glowSeparator = this.register(new SeparatorSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435"));
    private final SliderSetting glowRadius = this.register(new SliderSetting("\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u0420\u0430\u0434\u0438\u0443\u0441 \u0433\u0430\u0443\u0441\u0441-\u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f.").range(1.0f, 20.0f).increment(0.5f).setValue(7.0f).visible(() -> !this.renderMode.is(MODE_OUTLINE)));
    private final SliderSetting glowStrength = this.register(new SliderSetting("\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0432\u043d\u0435\u0448\u043d\u0435\u0433\u043e \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f.").range(0.1f, 3.0f).increment(0.05f).setValue(1.15f).visible(() -> !this.renderMode.is(MODE_OUTLINE)));
    private final BooleanSetting blending = this.register(new BooleanSetting("\u0421\u043c\u0435\u0448\u0438\u0432\u0430\u043d\u0438\u0435", "\u0410\u0434\u0434\u0438\u0442\u0438\u0432\u043d\u043e \u043f\u043e\u0434\u043c\u0435\u0448\u0438\u0432\u0430\u0442\u044c \u0446\u0432\u0435\u0442 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f \u043a \u0441\u0446\u0435\u043d\u0435 (\u043a\u0430\u043a \u0441\u0432\u0435\u0442/bloom).", false).visibleWhen(() -> !this.renderMode.is(MODE_OUTLINE)));
    private final SeparatorSetting outlineSeparator = this.register(new SeparatorSetting("\u041a\u043e\u043d\u0442\u0443\u0440"));
    private final SliderSetting outlineWidth = this.register(new SliderSetting("\u0428\u0438\u0440\u0438\u043d\u0430 \u043a\u043e\u043d\u0442\u0443\u0440\u0430", "\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u043e\u0431\u0432\u043e\u0434\u043a\u0438 \u0432\u043e\u043a\u0440\u0443\u0433 \u043c\u043e\u0434\u0435\u043b\u0438.").range(0.1f, 0.5f).increment(0.05f).setValue(0.3f).visible(() -> !this.renderMode.is(MODE_GAUSSIAN)));
    private final SliderSetting outlineBright = this.register(new SliderSetting("\u0412\u044b\u0441\u0432\u0435\u0442\u043b\u0435\u043d\u0438\u0435 \u043a\u043e\u043d\u0442\u0443\u0440\u0430", "\u041d\u0430\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u043e\u0431\u0432\u043e\u0434\u043a\u0430 \u0441\u043c\u0435\u0448\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0441 \u0431\u0435\u043b\u044b\u043c.").range(0.0f, 1.0f).increment(0.01f).setValue(0.35f).visible(() -> !this.renderMode.is(MODE_GAUSSIAN)));
    private final SeparatorSetting categorySeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442\u0430 \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0439"));
    private final BooleanSetting categoryColors = this.register(new BooleanSetting("\u0421\u0432\u043e\u0438 \u0446\u0432\u0435\u0442\u0430", "\u041e\u0442\u0434\u0435\u043b\u044c\u043d\u044b\u0439 \u0446\u0432\u0435\u0442 \u0434\u043b\u044f \u0438\u0433\u0440\u043e\u043a\u043e\u0432, \u043c\u043e\u0431\u043e\u0432, \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432 \u0438 \u043a\u043e\u043d\u0442\u0435\u0439\u043d\u0435\u0440\u043e\u0432.", true));
    private final ColorSetting playerColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", "\u041e\u0440\u0435\u043e\u043b \u0432\u043e\u043a\u0440\u0443\u0433 \u0438\u0433\u0440\u043e\u043a\u043e\u0432.", new Color(255, 96, 120, 210)).visible(this.categoryColors::getValue));
    private final ColorSetting mobColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 \u043c\u043e\u0431\u043e\u0432", "\u041e\u0440\u0435\u043e\u043b \u0432\u043e\u043a\u0440\u0443\u0433 \u043c\u043e\u0431\u043e\u0432.", new Color(255, 176, 72, 200)).visible(this.categoryColors::getValue));
    private final ColorSetting itemColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432", "\u041e\u0440\u0435\u043e\u043b \u0432\u043e\u043a\u0440\u0443\u0433 \u0432\u044b\u043f\u0430\u0432\u0448\u0438\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432.", new Color(120, 235, 170, 200)).visible(this.categoryColors::getValue));
    private final ColorSetting containerColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 \u043a\u043e\u043d\u0442\u0435\u0439\u043d\u0435\u0440\u043e\u0432", "\u041e\u0440\u0435\u043e\u043b \u0432\u043e\u043a\u0440\u0443\u0433 \u0441\u0443\u043d\u0434\u0443\u043a\u043e\u0432, \u0431\u043e\u0447\u0435\u043a \u0438 \u0448\u0430\u043b\u043a\u0435\u0440\u043e\u0432.", new Color(150, 190, 255, 200)).visible(this.categoryColors::getValue));
    private final SliderSetting containerRadius = this.register(new SliderSetting("\u0420\u0430\u0434\u0438\u0443\u0441 \u043f\u043e\u0438\u0441\u043a\u0430 \u043a\u043e\u043d\u0442\u0435\u0439\u043d\u0435\u0440\u043e\u0432", "\u041a\u0430\u043a \u0434\u0430\u043b\u0435\u043a\u043e \u0438\u0441\u043a\u0430\u0442\u044c \u0441\u0443\u043d\u0434\u0443\u043a\u0438 \u0438 \u0431\u043e\u0447\u043a\u0438.").range(8.0f, 40.0f).increment(2.0f).setValue(20.0f).visible(() -> this.targets.isSelected(TARGET_CONTAINERS)));
    private final SliderSetting containerLimit = this.register(new SliderSetting("\u041c\u0430\u043a\u0441\u0438\u043c\u0443\u043c \u043a\u043e\u043d\u0442\u0435\u0439\u043d\u0435\u0440\u043e\u0432", "\u0421\u043a\u043e\u043b\u044c\u043a\u043e \u043a\u043e\u043d\u0442\u0435\u0439\u043d\u0435\u0440\u043e\u0432 \u043f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0442\u044c \u043e\u0434\u043d\u043e\u0432\u0440\u0435\u043c\u0435\u043d\u043d\u043e.").range(1.0f, 64.0f).increment(1.0f).setValue(24.0f).visible(() -> this.targets.isSelected(TARGET_CONTAINERS)));
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

    private final List<Box> containerBoxes = new ArrayList<Box>();
    private final Map<Integer, List<Box>> containerLayers = new HashMap<Integer, List<Box>>();
    private int containerScanLayer;

    /** Контейнер ли это: сундук, бочка, шалкер или эндер-сундук. */
    private static boolean isContainer(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        if (state.isOf(Blocks.CHEST) || state.isOf(Blocks.TRAPPED_CHEST) || state.isOf(Blocks.BARREL) || state.isOf(Blocks.ENDER_CHEST)) {
            return true;
        }
        return state.getBlock().getTranslationKey().contains("shulker_box");
    }

    /** Выпавшие предметы рядом: категория «Предметы». */
    private List<ItemEntity> collectItems() {
        ArrayList<ItemEntity> items = new ArrayList<ItemEntity>();
        if (this.mc.world == null || this.mc.player == null) {
            return items;
        }
        for (Entity entity : this.mc.world.getEntities()) {
            if (!(entity instanceof ItemEntity)) {
                continue;
            }
            ItemEntity item = (ItemEntity)entity;
            if (item.isRemoved() || item.getStack().isEmpty()) {
                continue;
            }
            if (this.mc.player.squaredDistanceTo(item) > 4096.0) {
                continue;
            }
            items.add(item);
        }
        return items;
    }

    /** Слой контейнеров пересчитывается за тик: скан идёт волной, без просадок кадра. */
    @EventHandler
    private void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || !this.isEnabled() || !this.targets.isSelected(TARGET_CONTAINERS)) {
            this.containerBoxes.clear();
            this.containerLayers.clear();
            return;
        }
        if (this.mc.world == null || this.mc.player == null) {
            return;
        }
        int radius = (int)this.containerRadius.getFloat();
        int bottom = (int)Math.floor(this.mc.player.getY()) - 12;
        int layers = 25;
        if (this.containerScanLayer >= layers) {
            this.containerScanLayer = 0;
        }
        int y = bottom + this.containerScanLayer;
        int centerX = (int)Math.floor(this.mc.player.getX());
        int centerZ = (int)Math.floor(this.mc.player.getZ());
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        ArrayList<Box> found = new ArrayList<Box>();
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }
                mutable.set(centerX + dx, y, centerZ + dz);
                if (!GlowEsp.isContainer(this.mc.world.getBlockState(mutable))) {
                    continue;
                }
                BlockPos immutable = mutable.toImmutable();
                found.add(new Box(immutable.getX(), immutable.getY(), immutable.getZ(),
                        immutable.getX() + 1.0, immutable.getY() + 1.0, immutable.getZ() + 1.0));
            }
        }
        this.containerLayers.put(y, found);
        this.containerBoxes.clear();
        int limit = (int)this.containerLimit.getFloat();
        for (List<Box> layer : this.containerLayers.values()) {
            for (Box box : layer) {
                if (this.containerBoxes.size() >= limit) {
                    break;
                }
                this.containerBoxes.add(box);
            }
        }
        ++this.containerScanLayer;
    }

    private void renderGroup(List<? extends LivingEntity> entities, float tickDelta, MatrixStack matrixStack, Vec3d cameraPos,
                             boolean glow, boolean outline, float radius, float strength, float width, int color) {
        if (entities.isEmpty()) {
            return;
        }
        int[] body = new int[]{color};
        GlowEspRenderer.render(entities, tickDelta, matrixStack, cameraPos, glow, outline, radius, strength, width,
                this.blending.getValue(), body, body);
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
        boolean glow = !this.renderMode.is(MODE_OUTLINE);
        boolean outline = !this.renderMode.is(MODE_GAUSSIAN);
        float radius = this.glowRadius.getFloat();
        float strength = this.glowStrength.getFloat();
        float width = this.outlineWidth.getFloat();
        float ticks = (float)worldRenderEvent.getPartialTicks();
        MatrixStack stack = worldRenderEvent.getStack();
        int[] palette = this.glowGradientColors();
        int[] outlinePalette = this.outlineGradientColors(palette);
        if (this.categoryColors.getValue()) {
            ArrayList<LivingEntity> players = new ArrayList<LivingEntity>();
            ArrayList<LivingEntity> mobs = new ArrayList<LivingEntity>();
            for (LivingEntity entity : list) {
                if (entity instanceof AbstractClientPlayerEntity) {
                    players.add(entity);
                }
                else {
                    mobs.add(entity);
                }
            }
            this.renderGroup(players, ticks, stack, vec3d, glow, outline, radius, strength, width, this.playerColor.getColor());
            this.renderGroup(mobs, ticks, stack, vec3d, glow, outline, radius, strength, width, this.mobColor.getColor());
        }
        else {
            GlowEspRenderer.render(list, ticks, stack, vec3d, glow, outline, radius, strength, width, this.blending.getValue(), palette, outlinePalette);
        }
        if (this.targets.isSelected(TARGET_ITEMS)) {
            List<ItemEntity> items = this.collectItems();
            if (!items.isEmpty()) {
                int itemTint = this.categoryColors.getValue() ? this.itemColor.getColor() : palette[0];
                GlowEspRenderer.render(items, ticks, stack, vec3d, glow, outline, radius, strength, width,
                        this.blending.getValue(), new int[]{itemTint}, new int[]{itemTint});
            }
        }
        if (this.targets.isSelected(TARGET_CONTAINERS) && !this.containerBoxes.isEmpty()) {
            int containerTint = this.categoryColors.getValue() ? this.containerColor.getColor() : palette[0];
            GlowEspRenderer.renderBoxes(this.containerBoxes, stack, vec3d, glow, outline, radius, strength, width, containerTint);
        }
    }
}

