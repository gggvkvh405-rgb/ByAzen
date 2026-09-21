package rtx.byazen.api.modules.impl.Interface;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.HudRenderEvent;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.world.DynamicTextures;
import rtx.byazen.utils.world.TerrainBuilder;

/**
 * Мини-карта (идея №71 из IDEAS.md): рельеф сверху, руды, отметки игроков и путевых точек,
 * стрелка направления и координаты. Карта строится порциями по нескольку строк за тик,
 * поэтому даже большой радиус не роняет FPS.
 */
public final class MinimapModule
extends InterfaceComponentModule {

    public static final String ORIENT_NORTH = "Север сверху";
    public static final String ORIENT_VIEW = "По взгляду";
    private static final String TEXTURE_KEY = "minimap";

    public final SeparatorSetting main = this.register(new SeparatorSetting("Мини-карта"));
    public final SliderSetting size = this.register(new SliderSetting("Размер, px", "Сторона карты на экране.").range(64.0f, 200.0f).increment(4.0f).setValue(116.0f));
    public final SliderSetting radius = this.register(new SliderSetting("Радиус, блоки", "Сколько блоков вокруг игрока попадает на карту.").range(16.0f, 192.0f).increment(8.0f).setValue(56.0f));
    public final ModeSetting orientation = this.register(new ModeSetting("Поворот", "Как ориентирована карта: север всегда сверху или карта крутится за взглядом.", ORIENT_NORTH, ORIENT_NORTH, ORIENT_VIEW));
    public final SliderSetting rounding = this.register(new SliderSetting("Скругление", "Радиус углов карты.").range(0.0f, 20.0f).increment(1.0f).setValue(7.0f));
    public final SliderSetting opacity = this.register(new SliderSetting("Прозрачность, %", "Насколько плотная карта поверх мира.").range(35.0f, 100.0f).increment(5.0f).setValue(95.0f));
    public final SliderSetting updateTicks = this.register(new SliderSetting("Обновление, тик", "Как часто пересчитывать рельеф. Меньше — живее, больше — экономнее.").range(1.0f, 20.0f).increment(1.0f).setValue(5.0f));

    public final SeparatorSetting marksSeparator = this.register(new SeparatorSetting("Отметки"));
    public final BooleanSetting showPlayers = this.register(new BooleanSetting("Игроки", "Точки других игроков рядом.", true));
    public final BooleanSetting showMobs = this.register(new BooleanSetting("Мобы", "Точки мобов рядом (без враждебных меток поверх).", false));
    public final BooleanSetting showWaypoints = this.register(new BooleanSetting("Путевые точки", "Ваши точки из модуля Waypoints.", true));
    public final BooleanSetting showCompass = this.register(new BooleanSetting("Стрелка и компас", "Стрелка взгляда и буква севера.", true));
    public final BooleanSetting showCoords = this.register(new BooleanSetting("Координаты", "Подпись с X/Z и измерением под картой.", true));

    private final TerrainBuilder builder = new TerrainBuilder();
    private int[] snapshot = new int[0];
    private int snapshotSize;
    private String textureId;
    private int updateCounter;
    private boolean pending;

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public MinimapModule() {
        super("Minimap", "Мини-карта: рельеф, руды, игроки, путевые точки, координаты и компас.");
    }

    public int mapSize() {
        return (int)this.size.getValue();
    }

    public int[] snapshot() {
        return this.snapshot;
    }

    public int snapshotSize() {
        return this.snapshotSize;
    }

    public String textureId() {
        return this.textureId;
    }

    public float mapYaw() {
        if (ORIENT_VIEW.equals(this.orientation.getValue())) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                return client.player.getYaw();
            }
        }
        return 180.0f;
    }

    @Override
    protected void onEnable() {
        this.pending = true;
        this.updateCounter = 0;
    }

    @Override
    protected void onDisable() {
        this.builder.cancel();
        DynamicTextures.release("minimap");
        this.textureId = null;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        if (!this.isEnabled()) {
            return;
        }
        if (!this.builder.running()) {
            if (++this.updateCounter < (int)this.updateTicks.getValue()) {
                return;
            }
            this.updateCounter = 0;
            this.builder.begin(this.mapSize(), (int)this.radius.getValue(), this.mapYaw());
        }
        boolean done = this.builder.step(client.world, client.player.getX(), client.player.getY(), client.player.getZ());
        if (this.builder.changed()) {
            this.snapshot = this.builder.pixels();
            this.snapshotSize = this.builder.size();
            this.pending = true;
        }
        if (done) {
            this.pending = true;
        }
    }

    @EventHandler
    public void onHudRender(HudRenderEvent hudRenderEvent) {
        if (!this.isEnabled() || !this.pending || this.snapshot.length == 0) {
            return;
        }
        this.pending = false;
        String id = DynamicTextures.acquire("minimap", this.snapshotSize, this.snapshotSize);
        if (id == null) {
            return;
        }
        this.textureId = id;
        DynamicTextures.upload("minimap", this.snapshot, this.snapshotSize, this.snapshotSize);
    }
}
