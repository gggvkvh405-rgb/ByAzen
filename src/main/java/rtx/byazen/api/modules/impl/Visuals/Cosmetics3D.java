package rtx.byazen.api.modules.impl.Visuals;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.CosmeticCatalogScreen;
import rtx.byazen.utils.cosmetics.CosmeticRenderer;
import rtx.byazen.utils.cosmetics.CosmeticSounds;
import rtx.byazen.utils.cosmetics.Cosmetics;

/**
 * Косметика ByAzen (идеи №121, №122 и №124 из IDEAS.md).
 * <p>
 * Модуль отвечает за всё, что игрок надел: шляпы, короны, рога, нимбы, ушки, маски, очки, шарфы,
 * хвосты и ауру. Здесь же настройка посадки под свой скин — сдвиг по трём осям и размер, — а сам
 * каталог с 3D-превью открывается кнопкой.
 */
public final class Cosmetics3D
extends Module {

    private static Cosmetics3D instance;

    private final SliderSetting offsetX = this.register(new SliderSetting("Сдвиг по X", "Сдвигает аксессуар вправо и влево, чтобы он сел ровно на голову.").range(-1.5f, 1.5f).increment(0.05f).setValue(0.0f));
    private final SliderSetting offsetY = this.register(new SliderSetting("Сдвиг по Y", "Поднимает и опускает аксессуар.").range(-1.5f, 1.5f).increment(0.05f).setValue(0.0f));
    private final SliderSetting offsetZ = this.register(new SliderSetting("Сдвиг по Z", "Двигает аксессуар вперёд и назад.").range(-1.5f, 1.5f).increment(0.05f).setValue(0.0f));
    private final SliderSetting scale = this.register(new SliderSetting("Размер", "Общий размер моделей косметики.").range(0.5f, 2.0f).increment(0.05f).setValue(1.0f));
    private final BooleanSetting showOthers = this.register(new BooleanSetting("Видно на других", "Показывать косметику других игроков, у которых включён ByAzen.", true));
    private final BooleanSetting particles = this.register(new BooleanSetting("Частицы", "Искры и снежинки вокруг аксессуаров по стилю набора.", true));
    private final SliderSetting particleAmount = this.register(new SliderSetting("Плотность частиц", "Сколько искр появляется за кадр.").range(0.2f, 2.5f).increment(0.1f).setValue(1.0f)).visible(this.particles::getValue);
    private final SliderSetting soundVolume = this.register(new SliderSetting("Громкость звуков, %", "Громкость звуков косметики (они уже нормализованы).").range(0.0f, 100.0f).increment(5.0f).setValue(70.0f));
    private final ButtonSetting catalog = this.register(new ButtonSetting("Каталог косметики", "Открыть каталог с 3D-превью, наборами и выдачей.").label("Открыть").onClick(Cosmetics3D::openCatalog));

    public Cosmetics3D() {
        super("Cosmetics 3D", "Каталог косметики ByAzen: наборы «Зима», «Неон» и «Космос», 3D-превью и настройка посадки аксессуаров.", Category.VISUALS);
        instance = this;
    }

    public static Cosmetics3D getInstance() {
        return instance;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        Cosmetics.load();
    }

    @Override
    protected void onDisable() {
        CosmeticRenderer.clear();
    }

    /** Открывает каталог косметики — используется и кнопкой, и командой. */
    public static void openCatalog() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        Cosmetics3D module = Cosmetics3D.getInstance();
        if (module != null && !module.isEnabled()) {
            module.enable();
        }
        Cosmetics.load();
        client.setScreen(new CosmeticCatalogScreen());
    }

    /** Громкость звуков косметики: 0…1. */
    public static float volume() {
        Cosmetics3D module = Cosmetics3D.getInstance();
        return module == null ? 0.7f : module.soundVolume.getFloat() / 100.0f;
    }

    /** Играет звук надетого предмета с учётом пользовательской громкости. */
    public static void playEquipSound(boolean equipped) {
        CosmeticSounds.playEquipped(equipped, Cosmetics3D.volume());
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        CosmeticRenderer.Layout layout = new CosmeticRenderer.Layout();
        layout.offsetX = this.offsetX.getFloat();
        layout.offsetY = this.offsetY.getFloat();
        layout.offsetZ = this.offsetZ.getFloat();
        layout.scale = this.scale.getFloat();
        layout.particles = this.particles.getValue();
        layout.particleAmount = this.particleAmount.getFloat();
        layout.otherPlayers = this.showOthers.getValue();
        CosmeticRenderer.setLayout(layout);
        CosmeticRenderer.render(event, MinecraftClient.getInstance());
    }
}
