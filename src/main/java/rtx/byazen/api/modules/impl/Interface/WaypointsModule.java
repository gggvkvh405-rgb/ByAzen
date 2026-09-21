package rtx.byazen.api.modules.impl.Interface;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.modules.settings.impl.TextSetting;
import rtx.byazen.api.nav.Waypoint;
import rtx.byazen.api.nav.WaypointStore;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.render.world.WorldShapeRenderer;

/**
 * Путевые точки (идея №72 из IDEAS.md): столб света над точкой, метка у земли, список в HUD,
 * сохранение с клавиши и команды {@code waypoint}. Точки хранятся в конфиге клиента.
 */
public final class WaypointsModule
extends InterfaceComponentModule {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Путевые точки"));
    public final BooleanSetting beams = this.register(new BooleanSetting("Столб света", "Вертикальный луч над каждой точкой в текущем измерении.", true));
    public final SliderSetting beamHeight = this.register(new SliderSetting("Высота луча", "Насколько высоко поднимается луч над точкой.").range(8.0f, 96.0f).increment(4.0f).setValue(32.0f).visible(this.beams::getValue));
    public final SliderSetting beamAlpha = this.register(new SliderSetting("Яркость луча, %", "Плотность луча у земли.").range(10.0f, 100.0f).increment(5.0f).setValue(65.0f).visible(this.beams::getValue));
    public final BooleanSetting marker = this.register(new BooleanSetting("Метка на земле", "Аккуратный контур у самой точки.", true));
    public final BooleanSetting listInHud = this.register(new BooleanSetting("Список в HUD", "Виджет со списком точек и расстоянием.", true));
    public final SliderSetting listSize = this.register(new SliderSetting("Точек в списке", "Сколько точек показывать в виджете.").range(1.0f, 12.0f).increment(1.0f).setValue(5.0f).visible(this.listInHud::getValue));
    public final BindSetting saveHere = this.register(new BindSetting("Сохранить точку", "Клавиша создаёт точку с координатами и текущим измерением."));
    public final BooleanSetting announce = this.register(new BooleanSetting("Отмечать в чате", "Писать в чат, когда точка создана, удалена или выбрана.", true));
    public final TextSetting travelCommand = this.register(new TextSetting("Команда перехода", "Необязательная команда для «дойти»: {x}, {y}, {z} и {name} подставятся сами. Пусто — команда не отправляется.")
            .setPlaceholder("/tpa {name}").lengthBounds(0, 64));

    private boolean keyHeld;

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public WaypointsModule() {
        super("Waypoints", "Путевые точки: столб света, метка на земле, список с расстоянием и команды.");
    }

    @Override
    protected void onDisable() {
        this.keyHeld = false;
    }

    /** Ближайшие точки в текущем измерении для HUD-виджета и подсказок. */
    public List<Waypoint> visiblePoints() {
        return WaypointStore.get().nearby((int)this.listSize.getValue());
    }

    public boolean listEnabled() {
        return this.isEnabled() && this.listInHud.getValue();
    }

    /** Выбирает точку целью и, если задана команда перехода, отправляет её. */
    public boolean travelTo(Waypoint waypoint) {
        if (waypoint == null) {
            return false;
        }
        WaypointStore.get().setActive(waypoint.name());
        String template = this.travelCommand.getValue();
        if (template != null && !template.isBlank()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                String command = template
                        .replace("{x}", Integer.toString((int)Math.floor(waypoint.x())))
                        .replace("{y}", Integer.toString((int)Math.floor(waypoint.y())))
                        .replace("{z}", Integer.toString((int)Math.floor(waypoint.z())))
                        .replace("{name}", waypoint.name());
                if (command.startsWith("/")) {
                    command = command.substring(1);
                }
                client.player.networkHandler.sendChatCommand(command);
            }
        }
        if (this.announce.getValue()) {
            ChatMessage.brandmessage("Цель: «" + waypoint.name() + "», " + waypoint.shortDimension()
                    + " • " + Math.round(waypoint.distanceTo(WaypointStore.playerPos())) + " м");
        }
        return true;
    }

    @EventHandler
    public void onWorldRender(rtx.byazen.api.events.impl.render.WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        List<Waypoint> points = WaypointStore.get().all();
        if (points.isEmpty()) {
            return;
        }
        String dimension = WaypointStore.dimensionId();
        net.minecraft.client.render.VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        net.minecraft.util.math.Vec3d camera = worldRenderEvent.getCamera().getCameraPos();
        float height = this.beamHeight.getValue();
        float alpha = this.beamAlpha.getValue() / 100.0f;
        int segments = 6;
        String activeName = WaypointStore.get().activeName();
        for (Waypoint point : points) {
            if (dimension != null && !point.dimension().equals(dimension)) {
                continue;
            }
            if (client.player.squaredDistanceTo(point.pos()) > 16384.0) {
                continue;
            }
            boolean active = point.name().equalsIgnoreCase(activeName);
            int tint = active ? ClientAccent.accentOpaque() & 0xFFFFFF : point.color() & 0xFFFFFF;
            if (this.beams.getValue()) {
                for (int i = 0; i < segments; ++i) {
                    double bottom = point.y() + (double)(height / (float)segments * (float)i);
                    double top = point.y() + (double)(height / (float)segments * (float)(i + 1));
                    int alphaStep = Math.round(alpha * (active ? 190.0f : 150.0f) * (1.0f - (float)i / (float)segments));
                    int color = WaypointsModule.argb(alphaStep, tint);
                    List<net.minecraft.util.math.Box> single = new ArrayList<net.minecraft.util.math.Box>(1);
                    single.add(new net.minecraft.util.math.Box(point.x() - 0.07, bottom, point.z() - 0.07, point.x() + 0.07, top, point.z() + 0.07));
                    WorldShapeRenderer.boxes(immediate, worldRenderEvent.getStack(), camera, single, color, color, 1.0f);
                }
            }
            if (this.marker.getValue()) {
                List<net.minecraft.util.math.Box> single = new ArrayList<net.minecraft.util.math.Box>(1);
                single.add(new net.minecraft.util.math.Box(point.x() - 0.5, point.y() + 0.02, point.z() - 0.5, point.x() + 0.5, point.y() + 0.6, point.z() + 0.5));
                int fill = WaypointsModule.argb(Math.round(alpha * 60.0f), tint);
                int outline = WaypointsModule.argb(Math.round(Math.min(255.0f, alpha * 220.0f)), tint);
                WorldShapeRenderer.boxes(immediate, worldRenderEvent.getStack(), camera, single, fill, outline, 1.4f);
            }
        }
    }

    @EventHandler
    public void onTick(rtx.byazen.api.events.impl.game.TickEvent tickEvent) {
        if (!tickEvent.isPre() || !this.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.currentScreen != null) {
            return;
        }
        boolean down = this.saveHere.isBound() && this.saveHere.getValue().isDown(client.getWindow().getHandle());
        if (down && !this.keyHeld) {
            this.keyHeld = true;
            this.saveCurrent(WaypointsModule.nextName());
            return;
        }
        if (!down) {
            this.keyHeld = false;
        }
    }

    private static String nextName() {
        return "Точка " + (WaypointStore.get().size() + 1);
    }

    /** Создаёт точку по текущим координатам игрока. */
    public Waypoint saveCurrent(String name) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return null;
        }
        net.minecraft.util.math.Vec3d pos = new net.minecraft.util.math.Vec3d(client.player.getX(), client.player.getY(), client.player.getZ());
        String dimension = WaypointStore.dimensionId();
        int color = ClientAccent.accentOpaque() & 0xFFFFFF;
        Waypoint waypoint = new Waypoint(name, Math.floor(pos.x) + 0.5, Math.floor(pos.y), Math.floor(pos.z) + 0.5, dimension, color, "");
        WaypointStore.get().put(waypoint);
        if (this.announce.getValue()) {
            ChatMessage.brandmessage("Точка «" + name + "» сохранена: " + Math.round(pos.x) + " / " + Math.round(pos.y) + " / " + Math.round(pos.z));
        }
        return waypoint;
    }

    /** Собирает цвет с нужной прозрачностью: сначала прозрачность, потом чистый RGB. */
    private static int argb(int alpha, int rgb) {
        int a = Math.max(0, Math.min(255, alpha));
        return a << 24 | rgb & 0xFFFFFF;
    }
}
