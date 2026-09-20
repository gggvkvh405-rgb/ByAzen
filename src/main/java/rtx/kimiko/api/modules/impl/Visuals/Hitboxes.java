package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import rtx.kimiko.api.events.impl.render.WorldRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.impl.Visuals.custompet.entity.CustomPetEntity;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.MultiSelectSetting;
import rtx.kimiko.api.modules.settings.impl.SliderSetting;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.world.WorldShapeRenderer;

public final class Hitboxes
extends Module {
    private static final String TARGET_PLAYERS = "\u0418\u0433\u0440\u043e\u043a\u0438";
    private static final String TARGET_MOBS = "\u041c\u043e\u0431\u044b";
    private static final String TARGET_SELF = "\u0421\u0435\u0431\u044f";
    private static final String MODE_OUTLINE = "\u041a\u043e\u043d\u0442\u0443\u0440";
    private static final String MODE_FILL = "\u0417\u0430\u043b\u0438\u0432\u043a\u0430";
    private static final String MODE_BOTH = "\u041e\u0431\u0430";
    private final MultiSelectSetting targets = this.register(new MultiSelectSetting("\u0426\u0435\u043b\u0438", "\u0427\u044c\u0438 \u0445\u0438\u0442\u0431\u043e\u043a\u0441\u044b \u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c.").value("\u0418\u0433\u0440\u043e\u043a\u0438", "\u041c\u043e\u0431\u044b", "\u0421\u0435\u0431\u044f").selected("\u0418\u0433\u0440\u043e\u043a\u0438", "\u041c\u043e\u0431\u044b"));
    private final ModeSetting mode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u041a\u043e\u043d\u0442\u0443\u0440, \u0437\u0430\u043b\u0438\u0432\u043a\u0430 \u0438\u043b\u0438 \u043e\u0431\u0430.", "\u041e\u0431\u0430", "\u041a\u043e\u043d\u0442\u0443\u0440", "\u0417\u0430\u043b\u0438\u0432\u043a\u0430", "\u041e\u0431\u0430"));
    private final ColorSetting color = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u0426\u0432\u0435\u0442 \u0445\u0438\u0442\u0431\u043e\u043a\u0441\u0430.", new Color(70, 170, 255, 255)));
    private final SliderSetting fillOpacity = this.register(new SliderSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0437\u0430\u043b\u0438\u0432\u043a\u0438", "\u0421\u0438\u043b\u0430 \u0437\u0430\u043b\u0438\u0432\u043a\u0438.").range(0.0f, 1.0f).increment(0.05f).setValue(0.18f).visible(() -> !this.mode.is(MODE_OUTLINE)));
    private final List<Box> boxes = new ArrayList<Box>(64);

    public Hitboxes() {
        super("Hitboxes", "\u041e\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u0435\u0442 \u0445\u0438\u0442\u0431\u043e\u043a\u0441\u044b \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439.", Category.VISUALS);
    }

    @EventHandler
    private void onWorldRender(WorldRenderEvent worldRenderEvent) {
        Vec3d vec3d;
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        Vec3d vec3d2 = worldRenderEvent.getCamera() != null ? worldRenderEvent.getCamera().getCameraPos() : this.mc.gameRenderer.getCamera().getCameraPos();
        double d = 16384.0;
        this.boxes.clear();
        for (Entity entity : this.mc.world.getEntities()) {
            if (!this.shouldRender(entity) || this.mc.player.squaredDistanceTo(entity) > d) continue;
            vec3d = entity.getLerpedPos(worldRenderEvent.getPartialTicks());
            this.boxes.add(entity.getBoundingBox().offset(vec3d.subtract(entity.getEntityPos())).expand(0.002));
        }
        if (this.boxes.isEmpty()) {
            return;
        }
        int n = this.mode.is(MODE_FILL) ? 0 : this.color.getColor();
        int n2 = this.mode.is(MODE_OUTLINE) ? 0 : ColorUtil.multAlpha(this.color.getColor(), this.fillOpacity.getFloat());
        MatrixStack matrixStack = worldRenderEvent.getStack();
        WorldShapeRenderer.boxes((VertexConsumerProvider.Immediate)this.mc.getBufferBuilders().getEntityVertexConsumers(), matrixStack, (Vec3d)vec3d2, this.boxes, (int)n2, (int)n, (float)1.0f);
    }

    private boolean shouldRender(Entity entity) {
        if (entity == null || entity instanceof CustomPetEntity || entity.isRemoved() || entity.isSpectator()) {
            return false;
        }
        if (entity == this.mc.player) {
            return this.targets.isSelected(TARGET_SELF) && !this.mc.options.getPerspective().isFirstPerson();
        }
        if (entity.isInvisible() && entity.isInvisibleTo((PlayerEntity)(Object)this.mc.player)) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            return this.targets.isSelected(TARGET_PLAYERS);
        }
        return entity instanceof MobEntity && this.targets.isSelected(TARGET_MOBS);
    }
}

