package rtx.byazen.api.drags.components;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.MinimapModule;
import rtx.byazen.api.nav.Waypoint;
import rtx.byazen.api.nav.WaypointStore;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.lang.Lang;

/**
 * Мини-карта в HUD (идея №71 из IDEAS.md): сглаженная рамка клиента, рельеф с рудами,
 * отметки игроков, мобов и путевых точек, стрелка взгляда, компас и координаты.
 */
public final class MinimapComp
extends Draggable {

    private static final String FONT_LABEL = "montserrat-semibold";
    private static final String FONT_SMALL = "montserrat-medium";
    private static final float PAD = 4.0f;
    private static final float COORD_HEIGHT = 13.0f;

    private float currentWidth = 124.0f;
    private float currentHeight = 124.0f;
    private float alpha;
    private long lastNs;

    public MinimapComp() {
        super("minimap", 5.0f, 92.0f);
    }

    private static MinimapModule module() {
        return ModuleManager.get().get(MinimapModule.class);
    }

    @Override
    public String displayName() {
        return "Мини-карта";
    }

    @Override
    public float width() {
        return this.currentWidth;
    }

    @Override
    public float height() {
        return this.currentHeight;
    }

    @Override
    public boolean isInteractive() {
        MinimapModule module = MinimapComp.module();
        return module != null && module.isEnabled();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> settings = super.buildHudSettings();
        MinimapModule module = MinimapComp.module();
        if (module != null) {
            settings.add(new BoolSetting(module.showPlayers));
            settings.add(new BoolSetting(module.showMobs));
            settings.add(new BoolSetting(module.showWaypoints));
            settings.add(new BoolSetting(module.showCompass));
            settings.add(new BoolSetting(module.showCoords));
            settings.add(new rtx.byazen.api.ui.settings.impl.SelectSetting(module.orientation));
        }
        return settings;
    }

    @Override
    protected void render(DrawContext drawContext) {
        MinimapModule module = MinimapComp.module();
        if (module == null) {
            return;
        }
        float delta = this.deltaSeconds();
        this.alpha += ((module.isEnabled() ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        int mapSize = module.mapSize();
        boolean coords = module.showCoords.getValue();
        float height = mapSize + PAD * 2.0f + (coords ? COORD_HEIGHT : 0.0f);
        this.currentWidth = mapSize + PAD * 2.0f;
        this.currentHeight = height;
        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;
        float radius = module.rounding.getValue();
        float opacity = module.opacity.getValue() / 100.0f;
        String texture = module.textureId();

        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(x, y, this.currentWidth, height, radius + 3.0f, a);
        if (texture != null && module.snapshotSize() > 0) {
            int color = MinimapComp.color(255, 255, 255, 250.0f * opacity * a);
            Render2D.image(texture, x + PAD, y + PAD, mapSize, mapSize, radius, color);
        }
        else {
            Render2D.rect(x + PAD, y + PAD, mapSize, mapSize, radius, MinimapComp.color(14, 16, 24, 220.0f * a));
        }
        Render2D.outline(x + PAD, y + PAD, mapSize, mapSize, radius, 0.9f, ClientAccent.accentSoft(70.0f * a));
        if (module.isEnabled()) {
            this.drawMarkers(module, x + PAD, y + PAD, mapSize);
            if (module.showCompass.getValue()) {
                this.drawCompass(module, x + PAD, y + PAD, mapSize, a);
            }
        }
        if (coords) {
            MinecraftClient client = MinecraftClient.getInstance();
            String label = "X: 0  Z: 0";
            if (client != null && client.player != null) {
                label = "X: " + Math.round(client.player.getX()) + "  Z: " + Math.round(client.player.getZ())
                        + "  Y: " + Math.round(client.player.getY());
            }
            float textWidth = Render2D.msdfWidth(FONT_SMALL, label, 6.0f);
            Render2D.msdfText(FONT_SMALL, label, x + (this.currentWidth - textWidth) * 0.5f, y + mapSize + PAD + 3.0f, 6.0f,
                    ClientAccent.accentSoft(210.0f * a));
        }
        Render2D.flush();
    }

    private void drawMarkers(MinimapModule module, float originX, float originY, int mapSize) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        float centerX = originX + mapSize * 0.5f;
        float centerY = originY + mapSize * 0.5f;
        float scale = mapSize * 0.5f / Math.max(8.0f, module.radius.getValue());
        float yawRad = (float)Math.toRadians(module.mapYaw());
        float fx = (float)(-Math.sin(yawRad));
        float fz = (float)Math.cos(yawRad);
        double playerX = client.player.getX();
        double playerZ = client.player.getZ();
        float a = this.alpha;

        if (module.showWaypoints.getValue()) {
            for (Waypoint point : WaypointStore.get().all()) {
                if (!point.dimension().equals(WaypointStore.dimensionId())) {
                    continue;
                }
                this.drawDot(point.x() - playerX, point.z() - playerZ, centerX, centerY, scale, mapSize,
                        point.color() & 0xFFFFFF | 255 << 24, false, a, fx, fz);
            }
        }
        ClientWorld world = client.world;
        if (module.showMobs.getValue()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof MobEntity) || entity == client.player) {
                    continue;
                }
                this.drawDot(entity.getX() - playerX, entity.getZ() - playerZ, centerX, centerY, scale, mapSize,
                        MinimapComp.color(240, 150, 90, 230.0f * a), false, a, fx, fz);
            }
        }
        if (module.showPlayers.getValue()) {
            for (AbstractClientPlayerEntity player : world.getPlayers()) {
                if (player == client.player) {
                    continue;
                }
                int color = MinimapComp.color(255, 110, 120, 240.0f * a);
                this.drawDot(player.getX() - playerX, player.getZ() - playerZ, centerX, centerY, scale, mapSize,
                        color, true, a, fx, fz);
            }
        }
    }

    private void drawDot(double worldX, double worldZ, float centerX, float centerY, float scale, int mapSize,
                         int color, boolean bigger, float alpha, float fx, float fz) {
        float screenX = (float)(-fz * worldX + fx * worldZ);
        float screenZ = (float)(-fx * worldX - fz * worldZ);
        float pixelX = centerX + screenX * scale;
        float pixelY = centerY + screenZ * scale;
        float half = mapSize * 0.5f - 1.0f;
        if (Math.abs(pixelX - centerX) > half || Math.abs(pixelY - centerY) > half) {
            return;
        }
        float size = bigger ? 4.4f : 3.6f;
        Render2D.circle(pixelX, pixelY, size, MinimapComp.color(10, 12, 18, 200.0f * alpha));
        Render2D.circle(pixelX, pixelY, size - 1.2f, color);
    }

    private void drawCompass(MinimapModule module, float originX, float originY, int mapSize, float alpha) {
        float centerX = originX + mapSize * 0.5f;
        float centerY = originY + mapSize * 0.5f;
        float yawRad = (float)Math.toRadians(module.mapYaw());
        float fx = (float)(-Math.sin(yawRad));
        float fz = (float)Math.cos(yawRad);
        float northX = -fz * 0.0f + fx * -1.0f;
        float northZ = -fx * 0.0f - fz * -1.0f;
        float length = (float)Math.sqrt(northX * northX + northZ * northZ);
        if (length < 1.0E-4f) {
            return;
        }
        float ring = mapSize * 0.5f - 9.0f;
        float markerX = centerX + northX / length * ring;
        float markerY = centerY + northZ / length * ring;
        String north = Lang.t("С", "N");
        Render2D.msdfText(FONT_LABEL, north, markerX - Render2D.msdfWidth(FONT_LABEL, north, 7.0f) * 0.5f, markerY - 4.0f, 7.0f,
                ClientAccent.accent(230.0f * alpha));
        float lookX = -fz * fx + fx * fz;
        float lookZ = -fx * fx - fz * fz;
        float angle = (float)Math.atan2(lookZ, lookX);
        float arrow = 7.0f;
        float tipX = centerX + (float)Math.cos(angle) * arrow;
        float tipY = centerY + (float)Math.sin(angle) * arrow;
        Render2D.line(centerX, centerY, tipX, tipY, 1.4f, ClientAccent.accentOpaque(245.0f * alpha));
        float back = angle + 2.5f;
        Render2D.line(tipX, tipY, centerX + (float)Math.cos(back) * arrow * 0.7f, centerY + (float)Math.sin(back) * arrow * 0.7f, 1.4f,
                ClientAccent.accentOpaque(200.0f * alpha));
        back = angle - 2.5f;
        Render2D.line(tipX, tipY, centerX + (float)Math.cos(back) * arrow * 0.7f, centerY + (float)Math.sin(back) * arrow * 0.7f, 1.4f,
                ClientAccent.accentOpaque(200.0f * alpha));
        Render2D.circle(centerX, centerY, 1.8f, MinimapComp.color(250, 252, 255, 250.0f * alpha));
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastNs == 0L ? 0.016f : (float)(now - this.lastNs) / 1.0E9f;
        this.lastNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }

    private static int color(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
