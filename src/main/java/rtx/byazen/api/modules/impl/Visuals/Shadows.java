package rtx.byazen.api.modules.impl.Visuals;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.MultiSelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.render.world.WorldShapeRenderer;

/**
 * Мягкие тени (идея №54 из IDEAS.md).
 * <p>
 * Под игроками, мобами и предметами появляется аккуратная контактная тень: чем ближе к земле,
 * тем плотнее. Тень — это несколько плоских слоёв с прозрачностью, поэтому край мягкий,
 * без пиксельных краёв.
 */
public final class Shadows
extends Module {

    private static final String WHO_PLAYERS = "Игроки";
    private static final String WHO_MOBS = "Мобы";
    private static final String WHO_ITEMS = "Предметы";

    public final SeparatorSetting main = this.register(new SeparatorSetting("Тени"));
    public final SliderSetting size = this.register(new SliderSetting("Размер, %", "Насколько тень шире самой модели.").range(60.0f, 180.0f).increment(5.0f).setValue(110.0f));
    public final SliderSetting density = this.register(new SliderSetting("Плотность, %", "Насколько тёмная тень под моделью.").range(5.0f, 70.0f).increment(1.0f).setValue(26.0f));
    public final SliderSetting softness = this.register(new SliderSetting("Мягкость края", "Сколько слоёв размытия у тени.").range(1.0f, 5.0f).increment(1.0f).setValue(3.0f));
    public final SeparatorSetting whoSeparator = this.register(new SeparatorSetting("Кому рисовать"));
    public final MultiSelectSetting targets = this.register(new MultiSelectSetting("Цели", "Для кого рисовать контактные тени.").value(WHO_PLAYERS, WHO_MOBS, WHO_ITEMS).selected(WHO_PLAYERS, WHO_MOBS));
    public final SliderSetting falloff = this.register(new SliderSetting("Затухание, блоков", "На какой высоте над землёй тень полностью исчезает.").range(1.0f, 10.0f).increment(0.5f).setValue(4.0f));
    public final ColorSetting color = this.register(new ColorSetting("Цвет", "Цвет тени. По умолчанию почти чёрный.", new java.awt.Color(8, 10, 14, 255)));

    public Shadows() {
        super("Shadows", "Мягкие контактные тени под игроками, мобами и предметами.", Category.VISUALS);
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        Vec3d camera = worldRenderEvent.getCamera() != null ? worldRenderEvent.getCamera().getCameraPos() : client.gameRenderer.getCamera().getCameraPos();
        float ticks = (float)worldRenderEvent.getPartialTicks();
        float sizeFactor = this.size.getValue() / 100.0f;
        float alpha = this.density.getValue() / 100.0f * 210.0f;
        int layers = Math.max(1, (int)this.softness.getValue());
        float falloff = Math.max(0.5f, this.falloff.getValue());
        int rgb = this.color.getValue() & 0xFFFFFF;
        List<Vec3d> positions = new ArrayList<Vec3d>();
        List<Float> widths = new ArrayList<Float>();
        List<Float> alphas = new ArrayList<Float>();
        for (Entity entity : client.world.getEntities()) {
            if (!Shadows.isTarget(entity, this.targets)) {
                continue;
            }
            Vec3d pos = entity.getLerpedPos(ticks);
            if (client.player.squaredDistanceTo(pos) > 4900.0) {
                continue;
            }
            int groundY = client.world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                    (int)Math.floor(pos.x), (int)Math.floor(pos.z)) - 1;
            double clearance = pos.y - (double)groundY;
            if (clearance < -0.5 || clearance > (double)falloff) {
                continue;
            }
            float fade = (float)Math.max(0.0, 1.0 - clearance / (double)falloff);
            if (fade <= 0.02f) {
                continue;
            }
            float width = Math.max(0.35f, entity.getWidth() * sizeFactor);
            positions.add(new Vec3d(pos.x, (double)groundY + 0.03, pos.z));
            widths.add(width);
            alphas.add(alpha * fade * fade);
        }
        if (positions.isEmpty()) {
            return;
        }
        for (int i = 0; i < positions.size(); ++i) {
            Vec3d pos = positions.get(i);
            float width = widths.get(i);
            float base = alphas.get(i);
            for (int layer = layers; layer >= 1; --layer) {
                float spread = (float)layer / (float)layers;
                float half = width * (0.5f + spread * 0.35f);
                float layerAlpha = base * (layer == 1 ? 1.0f : 0.45f / (float)layer);
                int fill = Shadows.argb(Math.round(layerAlpha), rgb);
                WorldShapeRenderer.boxes(client.getBufferBuilders().getEntityVertexConsumers(), worldRenderEvent.getStack(), camera,
                        List.of(new Box(pos.x - (double)half, pos.y, pos.z - (double)half, pos.x + (double)half, pos.y + 0.012, pos.z + (double)half)),
                        fill, 0, 1.0f);
            }
        }
    }

    private static boolean isTarget(Entity entity, MultiSelectSetting targets) {
        if (entity == null || entity.isRemoved() || entity.isSpectator()) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            return targets.isSelected(WHO_PLAYERS);
        }
        if (entity instanceof PassiveEntity) {
            return targets.isSelected(WHO_MOBS);
        }
        if (entity instanceof MobEntity) {
            return targets.isSelected(WHO_MOBS);
        }
        return entity instanceof net.minecraft.entity.ItemEntity && targets.isSelected(WHO_ITEMS);
    }

    private static int argb(int alpha, int rgb) {
        int a = Math.max(0, Math.min(255, alpha));
        return a << 24 | rgb & 0xFFFFFF;
    }
}
