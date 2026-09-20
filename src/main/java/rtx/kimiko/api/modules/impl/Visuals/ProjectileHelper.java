package rtx.kimiko.api.modules.impl.Visuals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import rtx.kimiko.api.drags.Position;
import rtx.kimiko.api.events.EventHandler;
import rtx.kimiko.api.events.impl.render.HudRenderEvent;
import rtx.kimiko.api.events.impl.render.WorldRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.MultiSelectSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.api.modules.settings.impl.SliderSetting;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.pipeline.ClientPipelines;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class ProjectileHelper extends Module {
    private static final int MAX_STEPS = 200;
    private static final String FONT = "montserrat-semibold";
    private static final float FONT_SIZE = 6.5f;
    private static final int CROSS_SIDES = 10;
    private static final float FADE_STEPS = 40.0f;
    private static final float[] CROSS_COS = new float[10];
    private static final float[] CROSS_SIN = new float[10];
    private static final String PEARL = "Жемчуг эндера";
    private static final String ARROW = "Стрелы";
    private static final String TRIDENT = "Трезубец";
    private static final String POTION = "Зелья";
    private static final String ITEM = "Предметы";
    private final SeparatorSetting visualSeparator = this.register(new SeparatorSetting("Настройка визуала"));
    private final MultiSelectSetting itemsToPredict = this.register(new MultiSelectSetting("Предметы для предсказания", "Какие снаряды предсказывать.").value(PEARL, ARROW, TRIDENT, POTION, ITEM).selected(PEARL, ARROW, TRIDENT, POTION));
    private final BooleanSetting fromHand = this.register(new BooleanSetting("Предсказание из рук", "Предсказывать снаряд в руке.", false));
    private final SliderSetting lineWidth = this.register(new SliderSetting("Толщина линии", "Толщина траектории.").range(0.1f, 0.3f).increment(0.05f).setValue(0.2f));
    private final SliderSetting indicatorSize = this.register(new SliderSetting("Размер индикатора", "Радиус круга приземления.").range(0.3f, 0.5f).increment(0.05f).setValue(0.4f));
    private final List<LineData> linesToRender = new ArrayList<>();
    private final List<LandingData> landingData = new ArrayList<>();
    private final List<Label> labels = new ArrayList<>();
    private final List<LivingEntity> livingScratch = new ArrayList<>();
    private final Vector4f projectionScratch = new Vector4f();

    public ProjectileHelper() {
        super("ProjectileHelper", "Показывает траекторию полёта снарядов", Category.VISUALS);
    }

    static {
        for (int i = 0; i < 10; ++i) {
            double d = Math.PI * 2 * (double)i / 10.0;
            CROSS_COS[i] = (float)Math.cos(d);
            CROSS_SIN[i] = (float)Math.sin(d);
        }
    }

    private Vec3d step(Vec3d pos, Vec3d velocity, Profile profile) {
        boolean inWater = this.mc.world != null && this.mc.world.getFluidState(BlockPos.ofFloored(pos)).isIn(FluidTags.WATER);
        double d = inWater ? profile.waterDrag() : profile.drag();
        return velocity.multiply(d).subtract(0.0, profile.gravity(), 0.0);
    }

    @EventHandler
    public void onHud(HudRenderEvent hudRenderEvent) {
        if (this.labels.isEmpty()) {
            return;
        }
        DrawContext drawContext = hudRenderEvent.getGraphics();
        Render2D.beginFrame(drawContext);
        for (Label label : this.labels) {
            double d = (double)(label.ticks() * 50) / 1000.0;
            String string = String.format(Locale.US, "%.1f", d) + "s";
            float f = Render2D.msdfWidth(FONT, string, 6.5f);
            float f2 = f + 8.0f;
            float f3 = label.x() - f2 / 2.0f;
            float f4 = label.y() - 7.25f;
            Render2D.rect(f3, f4 + 3.0f, f2, 8.5f, ColorUtil.rgba(0, 0, 0, 128));
            Render2D.msdfText(FONT, string, f3 + (f2 - f) / 2.0f, f4 + 4.0f, 6.5f, -1);
        }
        Render2D.flush();
    }

    @Override
    protected void onDisable() {
        this.linesToRender.clear();
        this.landingData.clear();
        this.labels.clear();
        this.livingScratch.clear();
    }

    private static int alpha255(float f) {
        return Math.clamp((long)Math.round(f * 255.0f), 0, 255);
    }

    private Profile profileFor(Entity entity) {
        if (entity instanceof EnderPearlEntity) {
            return Profile.PEARL;
        }
        if (entity instanceof TridentEntity) {
            return Profile.TRIDENT;
        }
        if (entity instanceof PersistentProjectileEntity) {
            return Profile.ARROW;
        }
        if (entity instanceof PotionEntity) {
            return Profile.POTION;
        }
        if (entity instanceof ItemEntity) {
            return Profile.ITEM;
        }
        return null;
    }

    private void predict(Entity entity, Vec3d pos, Vec3d velocity, boolean entitySource, Profile profile) {
        ArrayList<Vec3d> points = new ArrayList<>();
        Vec3d currPos = pos;
        Vec3d currVel = velocity;
        int ticks = 0;
        Vec3d finalPos = currPos;
        Vec3d normal = new Vec3d(0.0, 1.0, 0.0);
        boolean isEntity = false;
        boolean finished = false;
        for (int i = 0; i < 200; ++i) {
            points.add(currPos);
            Vec3d prevPos = currPos;
            currPos = currPos.add(currVel);
            ++ticks;
            EntityHit entityHit = this.firstEntityInPath(prevPos, currPos, entity);
            if (entityHit != null) {
                finalPos = entityHit.hitPos();
                isEntity = true;
                normal = currVel.lengthSquared() > 1.0E-9 ? currVel.normalize().multiply(-1.0) : normal;
                finished = true;
                break;
            }
            BlockHitResult blockHitResult = this.mc.world != null ? this.mc.world.raycast(new RaycastContext(prevPos, currPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)) : null;
            if (blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS) {
                finalPos = blockHitResult.getPos();
                normal = ProjectileHelper.faceToNormal(blockHitResult.getSide());
                finished = true;
                break;
            }
            if (currPos.y < -128.0) {
                finalPos = currPos;
                finished = true;
                break;
            }
            currVel = this.step(prevPos, currVel, profile);
        }
        if (!finished) {
            finalPos = currPos;
        }
        this.linesToRender.add(new LineData(points, finalPos));
        this.landingData.add(new LandingData(finalPos, ticks, isEntity, normal, false));
    }

    private HandShot handShot(PlayerEntity playerEntity, ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }
        Item item = itemStack.getItem();
        if (item == Items.ENDER_PEARL) {
            return new HandShot(Profile.PEARL, 1.5);
        }
        if (item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) {
            return new HandShot(Profile.POTION, 0.5);
        }
        if (item instanceof CrossbowItem) {
            return new HandShot(Profile.ARROW, 3.15);
        }
        if (item instanceof BowItem) {
            if (!this.isUsing(playerEntity, itemStack)) {
                return null;
            }
            float f = Math.max(0.05f, BowItem.getPullProgress(playerEntity.getItemUseTime()));
            return new HandShot(Profile.ARROW, 3.0 * (double)f);
        }
        if (item instanceof TridentItem) {
            if (!this.isUsing(playerEntity, itemStack)) {
                return null;
            }
            float f = MathHelper.clamp((float)playerEntity.getItemUseTime() / 10.0f, 0.1f, 1.0f);
            return new HandShot(Profile.TRIDENT, 2.5 * (double)f);
        }
        return null;
    }

    private void renderDisc(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d camPos, LandingData landingData) {
        Vec3d pos = landingData.pos();
        Vec3d normal = landingData.normal();
        double d = this.indicatorSize.getFloat();
        float f = (float)(System.currentTimeMillis() % 4000L) / 4000.0f;
        int n = ColorUtil.multAlpha(ClientAccent.gradientColor(ProjectileHelper.pingPong(f), 255.0f), 0.55f);
        Vec3d perp1 = ProjectileHelper.anyPerpendicular(normal).normalize();
        Vec3d perp2 = normal.crossProduct(perp1).normalize();
        int n2 = 48;
        for (int i = 0; i < n2; ++i) {
            double d2 = Math.PI * 2 * (double)i / (double)n2;
            double d3 = Math.PI * 2 * (double)(i + 1) / (double)n2;
            int n3 = ColorUtil.multAlpha(ClientAccent.gradientColor(ProjectileHelper.pingPong(f + (float)i / (float)n2), 255.0f), 0.22f);
            int n4 = ColorUtil.multAlpha(ClientAccent.gradientColor(ProjectileHelper.pingPong(f + (float)(i + 1) / (float)n2), 255.0f), 0.22f);
            Vec3d r1 = pos.add(ProjectileHelper.ringOffset(perp1, perp2, d2, d));
            Vec3d r2 = pos.add(ProjectileHelper.ringOffset(perp1, perp2, d3, d));
            this.vertex(vertexConsumer, entry, pos, camPos, n);
            this.vertex(vertexConsumer, entry, r1, camPos, n3);
            this.vertex(vertexConsumer, entry, r2, camPos, n4);
        }
    }

    private static Vec3d ringOffset(Vec3d v1, Vec3d v2, int n, double d) {
        return v1.multiply((double)CROSS_COS[n] * d).add(v2.multiply((double)CROSS_SIN[n] * d));
    }

    private static Vec3d ringOffset(Vec3d v1, Vec3d v2, double d, double d2) {
        double d3 = Math.cos(d);
        double d4 = Math.sin(d);
        return v1.multiply(d2 * d3).add(v2.multiply(d2 * d4));
    }

    private static float pingPong(float f) {
        float f2 = MathHelper.fractionalPart(f);
        return f2 < 0.5f ? f2 * 2.0f : (1.0f - f2) * 2.0f;
    }

    private boolean isUsing(PlayerEntity playerEntity, ItemStack itemStack) {
        return playerEntity.isUsingItem() && !playerEntity.getActiveItem().isEmpty() && playerEntity.getActiveItem().getItem() == itemStack.getItem();
    }

    private void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d pos, Vec3d camPos, int color) {
        vertexConsumer.vertex(entry, (float)(pos.x - camPos.x), (float)(pos.y - camPos.y), (float)(pos.z - camPos.z)).color(color);
    }

    private Vector4f project(Matrix4f viewMatrix, Matrix4f projMatrix, Vec3d camPos, Vec3d targetPos) {
        Vector4f vector4f = this.projectionScratch.set((float)(targetPos.x - camPos.x), (float)(targetPos.y - camPos.y), (float)(targetPos.z - camPos.z), 1.0f);
        viewMatrix.transform(vector4f);
        projMatrix.transform(vector4f);
        if (vector4f.w <= 1.0E-4f) {
            return null;
        }
        return vector4f;
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        this.linesToRender.clear();
        this.landingData.clear();
        this.labels.clear();
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        float f = MathHelper.clamp(worldRenderEvent.getPartialTicks(), 0.0f, 1.0f);
        this.gatherLiving();
        for (Entity entityLoop : this.mc.world.getEntities()) {
            Profile profile = this.profileFor(entityLoop);
            if (profile == null || !this.itemsToPredict.isSelected(profile.setting()) || entityLoop.getVelocity().lengthSquared() <= 1.0E-6) continue;
            this.predict(entityLoop, entityLoop.getLerpedPos(f), entityLoop.getVelocity(), true, profile);
        }
        if (this.fromHand.getValue()) {
            this.predictFromHand(f);
        }
        Camera camera = worldRenderEvent.getCamera() == null ? this.mc.gameRenderer.getCamera() : worldRenderEvent.getCamera();
        Vec3d cameraPos = camera.getCameraPos();
        VertexConsumerProvider.Immediate consumers = this.mc.getBufferBuilders().getEntityVertexConsumers();
        MatrixStack.Entry entry = worldRenderEvent.getStack().peek();
        double d = (double)this.lineWidth.getFloat() * 0.05;
        VertexConsumer vertexConsumer = consumers.getBuffer(ClientPipelines.PROJECTILE_TRIS);
        for (LandingData object : this.landingData) {
            this.renderDisc(vertexConsumer, entry, cameraPos, object);
        }
        VertexConsumer vertexConsumer2 = consumers.getBuffer(ClientPipelines.TARGET_CIRCLE_NODEPTH);
        for (LineData lineData : this.linesToRender) {
            this.emitLineTube(vertexConsumer2, entry, cameraPos, lineData, d);
        }
        for (LandingData landing : this.landingData) {
            this.emitCrossTube(vertexConsumer2, entry, cameraPos, landing, d * 0.7);
        }
        consumers.draw(ClientPipelines.PROJECTILE_TRIS);
        consumers.draw(ClientPipelines.TARGET_CIRCLE_NODEPTH);
        this.collectLabels(worldRenderEvent, cameraPos);
    }

    private Vec3d directionFromRotation(float pitch, float yaw) {
        float f3 = pitch * ((float)Math.PI / 180.0f);
        float f4 = -yaw * ((float)Math.PI / 180.0f);
        float f5 = MathHelper.cos(f3);
        return new Vec3d((double)(MathHelper.sin(f4) * f5), (double)(-MathHelper.sin(f3)), (double)(MathHelper.cos(f4) * f5)).normalize();
    }

    private void gatherLiving() {
        this.livingScratch.clear();
        if (this.mc.world == null) {
            return;
        }
        for (Entity entity : this.mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living) || entity == this.mc.player) continue;
            this.livingScratch.add(living);
        }
    }

    private void straightTube(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d camPos, Vec3d start, Vec3d end, int color, double radius) {
        Vec3d dir = end.subtract(start);
        if (dir.lengthSquared() < 1.0E-9) {
            return;
        }
        Vec3d normDir = dir.normalize();
        Vec3d perp1 = ProjectileHelper.anyPerpendicular(normDir).normalize();
        Vec3d perp2 = normDir.crossProduct(perp1).normalize();
        for (int i = 0; i < 10; ++i) {
            int next = (i + 1) % 10;
            Vec3d off1 = ProjectileHelper.ringOffset(perp1, perp2, i, radius);
            Vec3d off2 = ProjectileHelper.ringOffset(perp1, perp2, next, radius);
            Vec3d p1 = start.add(off1);
            Vec3d p2 = start.add(off2);
            Vec3d p3 = end.add(off2);
            Vec3d p4 = end.add(off1);
            this.vertex(vertexConsumer, entry, p1, camPos, color);
            this.vertex(vertexConsumer, entry, p2, camPos, color);
            this.vertex(vertexConsumer, entry, p3, camPos, color);
            this.vertex(vertexConsumer, entry, p4, camPos, color);
        }
    }

    private void collectLabels(WorldRenderEvent worldRenderEvent, Vec3d camPos) {
        Matrix4f projMatrix = worldRenderEvent.getProjectionMatrix();
        Matrix4f viewMatrix = worldRenderEvent.getStack().peek().getPositionMatrix();
        float screenW = Position.screenWidth();
        float screenH = Position.screenHeight();
        for (LandingData landing : this.landingData) {
            Vector4f vector4f;
            if (!landing.isEntity() || (vector4f = this.project(viewMatrix, projMatrix, camPos, landing.pos())) == null) continue;
            float f3 = (vector4f.x / vector4f.w * 0.5f + 0.5f) * screenW;
            float f4 = (1.0f - (vector4f.y / vector4f.w * 0.5f + 0.5f)) * screenH;
            if (Float.isNaN(f3) || Float.isNaN(f4)) continue;
            this.labels.add(new Label(landing.ticks(), f3, f4));
        }
    }

    private static Vec3d faceToNormal(Direction direction) {
        if (direction == null) {
            return new Vec3d(0.0, 1.0, 0.0);
        }
        return new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    private void emitCrossTube(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d camPos, LandingData landingData, double radius) {
        Vec3d pos = landingData.pos();
        Vec3d normal = landingData.normal();
        int color = ColorUtil.multAlpha(ClientAccent.accentOpaque(), 0.6f);
        Vec3d p1 = ProjectileHelper.anyPerpendicular(normal).normalize().multiply(0.15);
        Vec3d p2 = normal.crossProduct(p1).normalize().multiply(0.15);
        this.straightTube(vertexConsumer, entry, camPos, pos.add(p1), pos.subtract(p1), color, radius);
        this.straightTube(vertexConsumer, entry, camPos, pos.add(p2), pos.subtract(p2), color, radius);
    }

    private static Vec3d anyPerpendicular(Vec3d vec3d) {
        Vec3d perp = Math.abs(vec3d.y) < 0.9 ? new Vec3d(0.0, 1.0, 0.0) : new Vec3d(1.0, 0.0, 0.0);
        return perp.crossProduct(vec3d);
    }

    private EntityHit firstEntityInPath(Vec3d start, Vec3d end, Entity sourceEntity) {
        double closestDist = Double.MAX_VALUE;
        LivingEntity hitEntity = null;
        Vec3d hitPoint = null;
        for (LivingEntity target : this.livingScratch) {
            Optional<Vec3d> rayHit;
            if (target == sourceEntity || (rayHit = target.getBoundingBox().expand(0.1).raycast(start, end)).isEmpty()) continue;
            double dist = start.squaredDistanceTo(rayHit.get());
            if (dist < closestDist) {
                closestDist = dist;
                hitEntity = target;
                hitPoint = rayHit.get();
            }
        }
        return hitEntity != null ? new EntityHit(hitEntity, hitPoint) : null;
    }

    private static float relakeAlpha(int step) {
        return MathHelper.clamp((float)step / 40.0f, 0.0f, 1.0f);
    }

    private void emitLineTube(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d camPos, LineData lineData, double radius) {
        List<Vec3d> points = lineData.points();
        int count = points.size();
        if (count == 0) {
            return;
        }
        int total = count + 1;
        if (total < 2) {
            return;
        }
        Vec3d[] fullPoints = new Vec3d[total];
        for (int i = 0; i < count; ++i) {
            fullPoints[i] = points.get(i);
        }
        fullPoints[count] = lineData.finalPos();
        Vec3d[] tangents = new Vec3d[total];
        for (int i = 0; i < total; ++i) {
            Vec3d d1 = i > 0 ? fullPoints[i].subtract(fullPoints[i - 1]) : fullPoints[1].subtract(fullPoints[0]);
            Vec3d d2 = i < total - 1 ? fullPoints[i + 1].subtract(fullPoints[i]) : fullPoints[i].subtract(fullPoints[i - 1]);
            Vec3d avg = d1.add(d2);
            tangents[i] = avg.lengthSquared() > 1.0E-9 ? avg.normalize() : (d2.lengthSquared() > 1.0E-9 ? d2.normalize() : new Vec3d(0.0, 0.0, 1.0));
        }
        Vec3d currentPerp = ProjectileHelper.anyPerpendicular(tangents[0]).normalize();
        Vec3d[] prevRing = null;
        int[] prevColors = null;
        for (int i = 0; i < total; ++i) {
            if (i > 0) {
                Vec3d nextPerp = currentPerp.subtract(tangents[i].multiply(currentPerp.dotProduct(tangents[i])));
                currentPerp = nextPerp.lengthSquared() > 1.0E-9 ? nextPerp.normalize() : ProjectileHelper.anyPerpendicular(tangents[i]).normalize();
            }
            Vec3d binormal = tangents[i].crossProduct(currentPerp).normalize();
            Vec3d toCam = camPos.subtract(fullPoints[i]);
            double camDist = toCam.length();
            Vec3d normCam = camDist > 1.0E-6 ? toCam.multiply(1.0 / camDist) : new Vec3d(0.0, 1.0, 0.0);
            float alphaProgress = ProjectileHelper.relakeAlpha(i);
            float colorT = (float)i / (float)(total - 1);
            int baseColor = ClientAccent.gradientColor(colorT, 255.0f) & 0xFFFFFF;
            Vec3d[] ring = new Vec3d[10];
            int[] ringColors = new int[10];
            for (int s = 0; s < 10; ++s) {
                Vec3d ringVec = ProjectileHelper.ringOffset(currentPerp, binormal, s, radius);
                ring[s] = fullPoints[i].add(ringVec);
                double facing = radius > 1.0E-9 ? MathHelper.clamp(ringVec.dotProduct(normCam) / radius, 0.0, 1.0) : 0.0;
                float shade = 1.0f - 0.5f * (float)facing;
                ringColors[s] = baseColor | ProjectileHelper.alpha255(alphaProgress * shade) << 24;
            }
            if (prevRing != null) {
                for (int s = 0; s < 10; ++s) {
                    int nextS = (s + 1) % 10;
                    this.vertex(vertexConsumer, entry, prevRing[s], camPos, prevColors[s]);
                    this.vertex(vertexConsumer, entry, prevRing[nextS], camPos, prevColors[nextS]);
                    this.vertex(vertexConsumer, entry, ring[nextS], camPos, ringColors[nextS]);
                    this.vertex(vertexConsumer, entry, ring[s], camPos, ringColors[s]);
                }
            }
            prevRing = ring;
            prevColors = ringColors;
        }
    }

    private void predictFromHand(float f) {
        ClientPlayerEntity clientPlayerEntity = this.mc.player;
        if (clientPlayerEntity == null) {
            return;
        }
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();
        HandShot handShot = this.handShot(clientPlayerEntity, itemStack);
        if (handShot == null) {
            return;
        }
        Vec3d dir = this.directionFromRotation(clientPlayerEntity.getPitch(), clientPlayerEntity.getYaw());
        Vec3d startPos = clientPlayerEntity.getCameraPosVec(f).add(dir.multiply(0.2));
        Vec3d velocity = dir.multiply(handShot.speed()).add(0.0, clientPlayerEntity.getVelocity().y * 0.5, 0.0);
        this.predict(clientPlayerEntity, startPos, velocity, false, handShot.profile());
    }

    public static record LineData(List<Vec3d> points, Vec3d finalPos) {}
    public static record LandingData(Vec3d pos, int ticks, boolean isEntity, Vec3d normal, boolean bl2) {}
    public static record Label(int ticks, float x, float y) {}
    public static record HandShot(Profile profile, double speed) {}
    public static record EntityHit(LivingEntity entity, Vec3d hitPos) {}

    public static enum Profile {
        ARROW(0.05, 0.99, 0.6, ProjectileHelper.ARROW),
        PEARL(0.03, 0.99, 0.8, ProjectileHelper.PEARL),
        TRIDENT(0.05, 0.99, 0.6, ProjectileHelper.TRIDENT),
        POTION(0.05, 0.99, 0.8, ProjectileHelper.POTION),
        ITEM(0.04, 0.98, 0.8, ProjectileHelper.ITEM);

        private final double gravity, drag, waterDrag;
        private final String setting;

        Profile(double gravity, double drag, double waterDrag, String setting) {
            this.gravity = gravity;
            this.drag = drag;
            this.waterDrag = waterDrag;
            this.setting = setting;
        }

        public double gravity() { return gravity; }
        public double drag() { return drag; }
        public double waterDrag() { return waterDrag; }
        public String setting() { return setting; }
    }
}
