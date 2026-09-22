package rtx.byazen.api.modules.impl.Visuals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.DeathScreenEvent;
import rtx.byazen.api.events.impl.player.AttackEntityEvent;
import rtx.byazen.api.events.impl.render.HudRenderEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.SelectSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.modules.settings.impl.StringSetting;
import rtx.byazen.api.ui.PetPlayScreen;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.pets.PetAccessories;
import rtx.byazen.utils.cosmetics.CosmeticSounds;
import rtx.byazen.utils.pets.PetStyleData;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Питомцы 2.0 — оформление (идея №127 из IDEAS.md).
 * <p>
 * Цвет, акцент, имя, размер и аксессуар питомца настраиваются здесь и сразу видны на модели: бант,
 * шляпа, корона, шарф или очки. Рядом кнопка мини-игр, где питомца можно покормить, погладить и
 * обыграть игрушкой — за это он получает опыт и поднимает настроение.
 */
public final class PetStyle
extends Module {

    private static PetStyle instance;

    private final ColorSetting color = this.register(new ColorSetting("Цвет", "Основной цвет аксессуаров и ауры питомца.").value(0x8FA8FF));
    private final ColorSetting accent = this.register(new ColorSetting("Акцент", "Второй цвет: подкладка, блики и свечение.").value(0xC7D6FF));
    private final SliderSetting scale = this.register(new SliderSetting("Размер", "Насколько крупным становится питомец и его аксессуары.").range(0.5f, 1.8f).increment(0.05f).setValue(1.0f));
    private final SelectSetting accessory = this.register(new SelectSetting("Аксессуар", "Что надеть на питомца.")
            .value(PetStyleData.ACCESSORIES).selected(PetStyleData.ACCESSORY_BOW));
    private final StringSetting name = this.register(new StringSetting("Имя", "Подпись над питомцем.", "Питомец", 16));
    private final BooleanSetting showName = this.register(new BooleanSetting("Показывать имя", "Подпись с именем и уровнем над питомцем.", true));
    private final SliderSetting nameSize = this.register(new SliderSetting("Размер имени", "Размер подписи над питомцем.").range(5.0f, 14.0f).increment(0.5f).setValue(8.0f)).visible(this.showName::getValue);
    private final BooleanSetting aura = this.register(new BooleanSetting("Аура настроения", "Кольцо у лап и мягкое свечение по настроению.", true));
    private final ButtonSetting games = this.register(new ButtonSetting("Игры с питомцем", "Покормить, погладить, обыграть игрушкой, позвать к себе.").label("Открыть").onClick(PetStyle::openGames));

    private final List<NameTag> tags = new ArrayList<NameTag>();
    private long lastAttackMs;

    public PetStyle() {
        super("Pet Style", "Оформление питомца: цвет, размер, имя и аксессуары, а также мини-игры с опытом и настроением.", Category.VISUALS);
        instance = this;
        PetStyleData.ensureLoaded();
    }

    public static PetStyle getInstance() {
        return instance;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        PetStyleData.ensureLoaded();
    }

    /** Открывает мини-игры с питомцем. */
    public static void openGames() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        PetStyle module = PetStyle.getInstance();
        if (module != null && !module.isEnabled()) {
            module.enable();
        }
        client.setScreen(new PetPlayScreen());
    }

    private void syncSettings() {
        PetStyleData.setColor(this.color.getValue());
        PetStyleData.setAccent(this.accent.getValue());
        PetStyleData.setScale(this.scale.getFloat());
        PetStyleData.setAccessory(this.accessory.getSelected());
        PetStyleData.setName(this.name.getValue());
        PetStyleData.setShowName(this.showName.getValue());
    }

    /** Размер питомца для рендера: используется миксином масштаба модели. */
    public static float petScale() {
        PetStyle module = PetStyle.instance;
        if (module == null || !module.isEnabled()) {
            return 1.0f;
        }
        return PetStyleData.getScale();
    }

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastAttackMs < 1500L) {
            return;
        }
        this.lastAttackMs = now;
        boolean leveled = PetStyleData.addXp(2);
        if (leveled) {
            CosmeticSounds.play("pet_level", 0.85f);
        }
    }

    @EventHandler
    public void onDeath(DeathScreenEvent event) {
        if (this.isEnabled()) {
            PetStyleData.changeMood(-14);
        }
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        this.syncSettings();
        this.tags.clear();
        if (this.aura.getValue()) {
            PetAccessories.render(event, this.mc);
        }
        if (this.showName.getValue()) {
            this.collectTag(event);
        }
    }

    private void collectTag(WorldRenderEvent event) {
        if (this.mc.world == null || this.mc.gameRenderer == null || this.mc.gameRenderer.getCamera() == null) {
            return;
        }
        Vec3d anchor = PetAccessories.nameAnchor(this.mc, (float)event.getPartialTicks());
        if (anchor == null) {
            return;
        }
        Vec3d camera = this.mc.gameRenderer.getCamera().getCameraPos();
        Matrix4f position = event.getPositionMatrix();
        Matrix4f projection = event.getProjectionMatrix();
        if (position == null || projection == null) {
            return;
        }
        Vector4f vector = new Vector4f((float)(anchor.x - camera.x), (float)(anchor.y - camera.y), (float)(anchor.z - camera.z), 1.0f);
        position.transform(vector);
        projection.transform(vector);
        if (vector.w <= 1.0E-4f) {
            return;
        }
        float screenX = (vector.x / vector.w * 0.5f + 0.5f) * Position.screenWidth();
        float screenY = (1.0f - (vector.y / vector.w * 0.5f + 0.5f)) * Position.screenHeight();
        if (Float.isNaN(screenX) || Float.isNaN(screenY)) {
            return;
        }
        NameTag tag = new NameTag();
        tag.x = screenX;
        tag.y = screenY;
        tag.depth = vector.w;
        this.tags.add(tag);
    }

    @EventHandler
    public void onHud(HudRenderEvent event) {
        if (!this.isEnabled() || this.tags.isEmpty()) {
            return;
        }
        DrawContext drawContext = event.getGraphics();
        this.tags.sort(Comparator.comparingDouble(tag -> -tag.depth));
        float size = this.nameSize.getFloat();
        String title = PetStyleData.getName() + " · ур. " + PetStyleData.getLevel();
        String mood = "Настроение " + PetStyleData.getMood() + "%";
        for (NameTag tag : this.tags) {
            float width = Render2D.msdfWidth("montserrat-bold", title, size);
            float moodWidth = Render2D.msdfWidth("montserrat-medium", mood, size * 0.72f);
            float boxWidth = Math.max(width, moodWidth) + 14.0f;
            float boxX = tag.x - boxWidth * 0.5f;
            float boxY = tag.y - 20.0f;
            Render2D.beginFrame(drawContext);
            Render2D.rect(boxX, boxY, boxWidth, 20.0f, 6.0f, PetStyle.rgba(10, 12, 18, 170));
            Render2D.outline(boxX, boxY, boxWidth, 20.0f, 6.0f, 1.0f, ClientAccent.accentSoft(60.0f));
            Render2D.rect(boxX, boxY + 18.5f, boxWidth * ((float)PetStyleData.getMood() / 100.0f), 1.5f, 0.75f,
                    ClientAccent.accent(230));
            Render2D.msdfText("montserrat-bold", title, tag.x - width * 0.5f, boxY + 3.0f, size, PetStyle.rgba(233, 238, 248, 245));
            Render2D.msdfText("montserrat-medium", mood, tag.x - moodWidth * 0.5f, boxY + 11.5f, size * 0.72f,
                    PetStyle.rgba(168, 176, 194, 220));
            Render2D.flush();
        }
    }

    @Override
    protected void onDisable() {
        this.tags.clear();
    }

    private static int rgba(int red, int green, int blue, int alpha) {
        int a = Math.max(0, Math.min(255, alpha));
        return a << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
    }

    /** Подпись питомца: экранная позиция, посчитанная в кадре мира. */
    private static final class NameTag {

        float x;
        float y;
        float depth;
    }
}
