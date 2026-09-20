package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.events.impl.input.MouseButtonEvent;
import rtx.kimiko.api.events.impl.input.MouseButtonEvent.Action;
import rtx.kimiko.api.events.impl.render.WorldRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.pipeline.ClientPipelines;

public final class HolyWorldHelper
extends Module {
    private static final int MAX_SIMULATION_TICKS = 160;
    private static final int MAX_ACTIVE_ZONES = 8;
    private static final int CIRCLE_SEGMENTS = 64;
    private static final int[][] CUBE_EDGES = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
    private static final double SNOW_GRAVITY = 0.03;
    private static final double SNOW_DRAG = 0.99;
    private static final double SNOW_SPEED = 1.5;
    private static final long ACTIVATION_DEBOUNCE_MS = 90L;
    private final SeparatorSetting displaySeparator = this.register(new SeparatorSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u0435"));
    private final BooleanSetting heldPreview = this.register(new BooleanSetting("\u041f\u0440\u0435\u0434\u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0431\u0443\u0434\u0443\u0449\u0443\u044e \u0437\u043e\u043d\u0443, \u043f\u043e\u043a\u0430 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 \u0440\u0443\u043a\u0435.", true));
    private final NumberSetting previewOpacity = this.register(new NumberSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u043f\u0440\u0435\u0434\u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430", "\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0437\u043e\u043d\u044b, \u043f\u043e\u043a\u0430 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 \u0440\u0443\u043a\u0435.", 0.45, 0.05, 1.0, 0.05).visibleWhen(this.heldPreview::getValue));
    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442\u0430"));
    private final ColorSetting lineColor = this.register(new ColorSetting("\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0426\u0432\u0435\u0442 \u043b\u0438\u043d\u0438\u0439 \u0437\u043e\u043d\u044b.", new Color(255, 181, 72, 235)));
    private final ColorSetting playerInZoneColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 \u043a\u0443\u043f\u043e\u043b\u0430 \u043f\u0440\u0438 \u0438\u0433\u0440\u043e\u043a\u0435", "\u0426\u0432\u0435\u0442 \u043b\u0438\u043d\u0438\u0439 \u0442\u0440\u0430\u043f\u043a\u0438, \u043a\u043e\u0433\u0434\u0430 \u0432 \u0435\u0451 \u0437\u043e\u043d\u0435 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0434\u0440\u0443\u0433\u043e\u0439 \u0438\u0433\u0440\u043e\u043a.", new Color(65, 220, 112, 255)));
    private final List<ActiveZone> activeZones = new ArrayList<ActiveZone>();
    private final List<Vec3d> simulationPoints = new ArrayList<Vec3d>(301);
    private ClientWorld trackedLevel;
    private Vec3d smoothedPreview;
    private Vec3d snowLanding;
    private long lastActivationMs;
    private long lastFrameNanos;
    private float frameDt;
    private float previewOccupiedBlend;

    public HolyWorldHelper() {
        super("HolyWorld Helper", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0437\u043e\u043d\u044b HolyWorld-\u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432 \u043f\u043e\u0441\u043b\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u044f.", Category.UTILS);
    }

    @Override
    protected void onDisable() {
        this.clearState();
        this.trackedLevel = null;
    }

    @EventHandler
    private void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.world == null) {
            this.clearState();
            this.trackedLevel = null;
            return;
        }
        if (this.trackedLevel != this.mc.world) {
            this.clearState();
            this.trackedLevel = this.mc.world;
        }
        long l = Util.getMeasuringTimeMs();
        if (HolyWorldHelper.matchHeld((PlayerEntity)(Object)this.mc.player) != HolyWorldItem.SNOW) {
            this.snowLanding = null;
            this.simulationPoints.clear();
        }
        for (int i = this.activeZones.size() - 1; i >= 0; --i) {
            ActiveZone activeZone = this.activeZones.get(i);
            if (l >= activeZone.expiresAt) {
                this.activeZones.remove(i);
                continue;
            }
            if (!activeZone.item.followsPlayer) continue;
            activeZone.moveTo(HolyWorldHelper.zoneCenter(activeZone.item, this.mc.player.getEntityPos()));
        }
    }

    @Override
    protected void onEnable() {
        this.trackedLevel = this.mc.world;
    }

    @EventHandler
    private void onMouse(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.button != 1 || mouseButtonEvent.action != MouseButtonEvent.Action.PRESS || this.mc.player == null || this.mc.world == null || this.mc.currentScreen != null) {
            return;
        }
        HolyWorldItem holyWorldItem = HolyWorldHelper.matchHeld((PlayerEntity)(Object)this.mc.player);
        if (holyWorldItem == null || holyWorldItem.durationMs <= 0L) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        if (l - this.lastActivationMs < 90L) {
            return;
        }
        this.lastActivationMs = l;
        this.activate(holyWorldItem, l);
    }

    private float approach(float f, float f2) {
        return f + (f2 - f) * Math.min(1.0f, this.frameDt * 6.0f);
    }

    private static Vec3d placedCenter(HolyWorldItem holyWorldItem, double d, double d2, double d3) {
        double d4 = holyWorldItem.floorAnchored ? d2 + (double)holyWorldItem.size * 0.5 : d2;
        return new Vec3d(d, d4, d3);
    }

    private void renderHeldPreview(WorldRenderEvent worldRenderEvent, VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d vec3d) {
        HolyWorldItem holyWorldItem = HolyWorldHelper.matchHeld((PlayerEntity)(Object)this.mc.player);
        if (holyWorldItem == null) {
            this.smoothedPreview = null;
            this.previewOccupiedBlend = 0.0f;
            return;
        }
        if (holyWorldItem == HolyWorldItem.SNOW) {
            this.smoothedPreview = null;
            this.previewOccupiedBlend = 0.0f;
            int n = ColorUtil.multAlpha(this.lineColor.getValue(), this.previewOpacity.getFloat());
            this.renderSnowPrediction(vertexConsumer, entry, vec3d, n, worldRenderEvent.getPartialTicks());
            return;
        }
        Vec3d vec3d2 = this.smoothPreview(this.playerRenderPos(worldRenderEvent.getPartialTicks()));
        double d = vec3d2.y + 0.012;
        Vec3d vec3d3 = holyWorldItem.followsPlayer ? HolyWorldHelper.zoneCenter(holyWorldItem, vec3d2) : HolyWorldHelper.placedCenter(holyWorldItem, vec3d2.x, d, vec3d2.z);
        boolean bl = this.hasOtherPlayerIn(holyWorldItem, vec3d3);
        this.previewOccupiedBlend = this.approach(this.previewOccupiedBlend, bl ? 1.0f : 0.0f);
        int n = ColorUtil.multAlpha(this.blendOccupied(this.lineColor.getValue(), this.previewOccupiedBlend), this.previewOpacity.getFloat());
        this.drawZone(vertexConsumer, entry, vec3d, holyWorldItem.shape, vec3d3, holyWorldItem.size, d, n);
    }

    private static boolean zoneContains(HolyWorldItem holyWorldItem, Vec3d vec3d, Vec3d vec3d2) {
        if (holyWorldItem.shape == Shape.CIRCLE) {
            double d = vec3d2.x - vec3d.x;
            double d2 = vec3d2.z - vec3d.z;
            return d * d + d2 * d2 <= (double)(holyWorldItem.size * holyWorldItem.size);
        }
        double d = (double)holyWorldItem.size * 0.5;
        return Math.abs(vec3d2.x - vec3d.x) <= d && Math.abs(vec3d2.y - vec3d.y) <= (double)holyWorldItem.size && Math.abs(vec3d2.z - vec3d.z) <= d;
    }

    private Vec3d simulateLanding(Vec3d vec3d, Vec3d vec3d2) {
        this.simulationPoints.clear();
        this.simulationPoints.add(vec3d);
        Vec3d vec3d3 = vec3d;
        Vec3d vec3d4 = vec3d2;
        for (int i = 0; i < 160; ++i) {
            Vec3d vec3d5 = vec3d3.add(vec3d4);
            BlockHitResult blockHitResult = this.mc.world.raycast(new RaycastContext(vec3d3, vec3d5, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)(Object)this.mc.player));
            if (blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS) {
                Vec3d vec3d6 = blockHitResult.getPos();
                this.simulationPoints.add(vec3d6);
                return vec3d6;
            }
            this.simulationPoints.add(vec3d5);
            vec3d3 = vec3d5;
            vec3d4 = vec3d4.multiply(0.99).add(0.0, -0.03, 0.0);
            if (vec3d3.y < -64.0) break;
        }
        return null;
    }

    private void emitLandingMarker(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d vec3d, Vec3d vec3d2, int n) {
        double d = 0.13;
        double d2 = vec3d.y + 0.004;
        this.quadVertex(vertexConsumer, entry, new Vec3d(vec3d.x - d, d2, vec3d.z - d).subtract(vec3d2), n);
        this.quadVertex(vertexConsumer, entry, new Vec3d(vec3d.x + d, d2, vec3d.z - d).subtract(vec3d2), n);
        this.quadVertex(vertexConsumer, entry, new Vec3d(vec3d.x + d, d2, vec3d.z + d).subtract(vec3d2), n);
        this.quadVertex(vertexConsumer, entry, new Vec3d(vec3d.x - d, d2, vec3d.z + d).subtract(vec3d2), n);
    }

    private int blendOccupied(int n, float f) {
        if (f <= 0.001f) {
            return n;
        }
        if (f >= 0.999f) {
            return this.playerInZoneColor.getColor();
        }
        return ColorUtil.lerpColor(n, this.playerInZoneColor.getColor(), f);
    }

    private void renderSnowPrediction(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d vec3d, int n, float f) {
        this.updateSnowPrediction(f);
        this.drawPath(vertexConsumer, entry, vec3d, this.simulationPoints, n);
        if (this.snowLanding != null) {
            this.drawCircle(vertexConsumer, entry, vec3d, this.snowLanding.add(0.0, 0.02, 0.0), 0.42f, n);
        }
    }

    private Vec3d smoothPreview(Vec3d vec3d) {
        if (this.smoothedPreview == null || this.smoothedPreview.squaredDistanceTo(vec3d) > 64.0) {
            this.smoothedPreview = vec3d;
            return vec3d;
        }
        this.smoothedPreview = this.smoothedPreview.add(vec3d.subtract(this.smoothedPreview).multiply(0.35));
        return this.smoothedPreview;
    }

    private double resolveFloorY(Vec3d vec3d) {
        if (this.mc.world == null || this.mc.player == null) {
            return vec3d.y;
        }
        BlockHitResult blockHitResult = this.mc.world.raycast(new RaycastContext(vec3d.add(0.0, 0.75, 0.0), vec3d.add(0.0, -8.0, 0.0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)(Object)this.mc.player));
        return blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS ? blockHitResult.getPos().y + 0.012 : vec3d.y;
    }

    private Vec3d playerRenderPos(float f) {
        ClientPlayerEntity clientPlayerEntity = this.mc.player;
        return new Vec3d(MathHelper.lerp((double)f, (double)clientPlayerEntity.lastRenderX, (double)clientPlayerEntity.getX()), MathHelper.lerp((double)f, (double)clientPlayerEntity.lastRenderY, (double)clientPlayerEntity.getY()), MathHelper.lerp((double)f, (double)clientPlayerEntity.lastRenderZ, (double)clientPlayerEntity.getZ()));
    }

    private void updateSnowPrediction(float f) {
        if (this.mc.player == null || this.mc.world == null) {
            this.snowLanding = null;
            this.simulationPoints.clear();
            return;
        }
        Vec3d vec3d = this.mc.player.getRotationVec(f);
        if (vec3d.lengthSquared() < 1.0E-6) {
            this.snowLanding = null;
            this.simulationPoints.clear();
            return;
        }
        Vec3d vec3d2 = this.mc.player.getCameraPosVec(f).add(vec3d.multiply(0.05));
        this.snowLanding = this.simulateLanding(vec3d2, vec3d.multiply(1.5));
    }

    private boolean hasOtherPlayerIn(HolyWorldItem holyWorldItem, Vec3d vec3d) {
        if (!holyWorldItem.isTrap || this.mc.world == null) {
            return false;
        }
        for (PlayerEntity playerEntity : this.mc.world.getPlayers()) {
            if (playerEntity == this.mc.player || playerEntity.isSpectator() || !HolyWorldHelper.zoneContains(holyWorldItem, vec3d, playerEntity.getEntityPos())) continue;
            return true;
        }
        return false;
    }

    private void drawPath(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d vec3d, List<Vec3d> list, int n) {
        if (list.size() < 2) {
            return;
        }
        int n2 = list.size() - 1;
        for (int i = 1; i < list.size(); ++i) {
            float f = 1.0f - (float)i / (float)n2 * 0.35f;
            int n3 = ColorUtil.multAlpha(n, f);
            if (ColorUtil.alpha(n3) <= 0) continue;
            this.lineVertex(vertexConsumer, entry, vec3d, list.get(i - 1), n3);
            this.lineVertex(vertexConsumer, entry, vec3d, list.get(i), n3);
        }
    }

    private void activate(HolyWorldItem holyWorldItem, long l) {
        Vec3d vec3d = this.mc.player.getEntityPos();
        double d = holyWorldItem.followsPlayer ? vec3d.y : this.resolveFloorY(vec3d);
        Vec3d vec3d2 = holyWorldItem.followsPlayer ? HolyWorldHelper.zoneCenter(holyWorldItem, vec3d) : HolyWorldHelper.placedCenter(holyWorldItem, vec3d.x, d, vec3d.z);
        this.activeZones.removeIf(activeZone -> activeZone.item == holyWorldItem && activeZone.center.squaredDistanceTo(vec3d2) < 2.25);
        if (this.activeZones.size() >= 8) {
            this.activeZones.remove(0);
        }
        this.activeZones.add(new ActiveZone(holyWorldItem, vec3d2, d, l + holyWorldItem.durationMs));
    }

    private void lineVertex(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d vec3d, Vec3d vec3d2, int n) {
        vertexConsumer.vertex(entry, (float)(vec3d2.x - vec3d.x), (float)(vec3d2.y - vec3d.y), (float)(vec3d2.z - vec3d.z)).color(n);
    }

    private static HolyWorldItem matchHeld(PlayerEntity playerEntity) {
        HolyWorldItem holyWorldItem = HolyWorldItem.match((ItemStack)playerEntity.getMainHandStack());
        return holyWorldItem != null ? holyWorldItem : HolyWorldItem.match((ItemStack)playerEntity.getOffHandStack());
    }

    private void quadVertex(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d vec3d, int n) {
        vertexConsumer.vertex(entry, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).color(n);
    }

    private static Vec3d zoneCenter(HolyWorldItem holyWorldItem, Vec3d vec3d) {
        return holyWorldItem.shape == Shape.CUBE ? vec3d.add(0.0, (double)holyWorldItem.size * 0.5, 0.0) : vec3d.add(0.0, 0.025, 0.0);
    }

    private void clearState() {
        this.activeZones.clear();
        this.simulationPoints.clear();
        this.smoothedPreview = null;
        this.snowLanding = null;
        this.lastActivationMs = 0L;
        this.lastFrameNanos = 0L;
        this.previewOccupiedBlend = 0.0f;
    }

    private void drawZone(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d vec3d, Shape shape, Vec3d vec3d2, float f, double d, int n) {
        if (shape == Shape.CIRCLE) {
            this.drawCircle(vertexConsumer, entry, vec3d, new Vec3d(vec3d2.x, d + 0.02, vec3d2.z), f, n);
            return;
        }
        double d2 = (double)f * 0.5;
        Vec3d[] vec3dArray = new Vec3d[]{vec3d2.add(-d2, -d2, -d2), vec3d2.add(d2, -d2, -d2), vec3d2.add(d2, -d2, d2), vec3d2.add(-d2, -d2, d2), vec3d2.add(-d2, d2, -d2), vec3d2.add(d2, d2, -d2), vec3d2.add(d2, d2, d2), vec3d2.add(-d2, d2, d2)};
        for (int[] nArray : CUBE_EDGES) {
            this.lineVertex(vertexConsumer, entry, vec3d, vec3dArray[nArray[0]], n);
            this.lineVertex(vertexConsumer, entry, vec3d, vec3dArray[nArray[1]], n);
        }
    }

    private void drawCircle(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d vec3d, Vec3d vec3d2, float f, int n) {
        for (int i = 0; i < 64; ++i) {
            double d = (float)Math.PI * 2 * (float)i / 64.0f;
            double d2 = (float)Math.PI * 2 * (float)(i + 1) / 64.0f;
            Vec3d vec3d3 = vec3d2.add(Math.cos(d) * (double)f, 0.0, Math.sin(d) * (double)f);
            Vec3d vec3d4 = vec3d2.add(Math.cos(d2) * (double)f, 0.0, Math.sin(d2) * (double)f);
            this.lineVertex(vertexConsumer, entry, vec3d, vec3d3, n);
            this.lineVertex(vertexConsumer, entry, vec3d, vec3d4, n);
        }
    }

    @EventHandler
    private void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        Camera camera = worldRenderEvent.getCamera() == null ? this.mc.gameRenderer.getCamera() : worldRenderEvent.getCamera();
        Vec3d vec3d = camera.getCameraPos();
        MatrixStack matrixStack = worldRenderEvent.getStack();
        VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer = immediate.getBuffer(ClientPipelines.TRAJECTORY_LINE);
        MatrixStack.Entry entry = matrixStack.peek();
        long l = System.nanoTime();
        this.frameDt = this.lastFrameNanos == 0L ? 0.016f : Math.min((float)(l - this.lastFrameNanos) * 1.0E-9f, 0.1f);
        this.lastFrameNanos = l;
        if (this.heldPreview.getValue()) {
            this.renderHeldPreview(worldRenderEvent, vertexConsumer, entry, vec3d);
        } else {
            this.smoothedPreview = null;
        }
        int n = this.lineColor.getValue();
        for (ActiveZone activeZone : this.activeZones) {
            boolean bl = this.hasOtherPlayerIn(activeZone.item, activeZone.center);
            activeZone.occupiedBlend = this.approach(activeZone.occupiedBlend, bl ? 1.0f : 0.0f);
            int n2 = this.blendOccupied(n, activeZone.occupiedBlend);
            Vec3d vec3d2 = activeZone.center;
            double d = activeZone.floorY;
            if (activeZone.item.followsPlayer) {
                vec3d2 = HolyWorldHelper.zoneCenter(activeZone.item, this.playerRenderPos(worldRenderEvent.getPartialTicks()));
                d = vec3d2.y - (activeZone.item.shape == Shape.CUBE ? (double)activeZone.item.size * 0.5 : 0.025);
            }
            this.drawZone(vertexConsumer, entry, vec3d, activeZone.item.shape, vec3d2, activeZone.item.size, d, n2);
        }
        immediate.draw(ClientPipelines.TRAJECTORY_LINE);
        if (this.heldPreview.getValue() && this.snowLanding != null && HolyWorldHelper.matchHeld((PlayerEntity)(Object)this.mc.player) == HolyWorldItem.SNOW) {
            int n3 = ColorUtil.multAlpha(this.lineColor.getValue(), this.previewOpacity.getFloat());
            VertexConsumer buffer = immediate.getBuffer(ClientPipelines.WORLD_PARTICLES_COLOR);
            this.emitLandingMarker(buffer, entry, this.snowLanding, vec3d, n3);
            immediate.draw(ClientPipelines.WORLD_PARTICLES_COLOR);
        }
    }


    private static class ActiveZone {
        final HolyWorldItem item;
        Vec3d center;
        final double floorY;
        final long expiresAt;
        float occupiedBlend;

        ActiveZone(HolyWorldItem item, Vec3d center, double floorY, long expiresAt) {
            this.item = item;
            this.center = center;
            this.floorY = floorY;
            this.expiresAt = expiresAt;
        }

        void moveTo(Vec3d center) {
            this.center = center;
        }
    }

    public enum Shape {
        CIRCLE, CUBE
    }

    public static final class HolyWorldItem {
        public static final HolyWorldItem SNOW = new HolyWorldItem("Snow", 0, 1, 0, Shape.CIRCLE, 0, false, false, false);
        
        public String name;
        public int price;
        public int count;
        public float size;
        public Shape shape;
        public long durationMs;
        public boolean floorAnchored;
        public boolean followsPlayer;
        public boolean isTrap;

        public HolyWorldItem(String name, int price, int count, float size, Shape shape, long durationMs, boolean floorAnchored, boolean followsPlayer, boolean isTrap) {
            this.name = name;
            this.price = price;
            this.count = count;
            this.size = size;
            this.shape = shape;
            this.durationMs = durationMs;
            this.floorAnchored = floorAnchored;
            this.followsPlayer = followsPlayer;
            this.isTrap = isTrap;
        }

        public static HolyWorldItem match(ItemStack stack) {
            if (stack == null || stack.isEmpty()) return null;
            if (stack.getItem() == net.minecraft.item.Items.SNOWBALL) {
                return SNOW;
            }
            return null;
        }
    }

}