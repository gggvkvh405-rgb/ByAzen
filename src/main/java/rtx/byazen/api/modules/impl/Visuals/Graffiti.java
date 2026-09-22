package rtx.byazen.api.modules.impl.Visuals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.modules.settings.impl.StringSetting;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleRenderer;
import rtx.byazen.utils.graffiti.GraffitiStore;
import rtx.byazen.utils.render.LocalImages;

/**
 * Граффити 2.0 (идея №68 из IDEAS.md): свои рисунки в мире, сделанные встроенным редактором.
 * <p>
 * Редактор собран из векторных слоёв: фон и штрихи, всё со сглаживанием. При сохранении слои
 * собираются в PNG и попадают в папку {@code byazen/graffiti}, а в мире рисунок вешается на грань
 * блока, куда смотрит игрок: ровно по стене, с поворотом, свечением и плавным появлением.
 */
public final class Graffiti
extends Module {

    private final SeparatorSetting placeSeparator = this.register(new SeparatorSetting("Размещение"));
    public final StringSetting file = this.register(new StringSetting("Файл рисунка", "Имя PNG из папки byazen/graffiti.", GraffitiStore.nextName(), 64));
    public final BindSetting placeKey = this.register(new BindSetting("Клавиша установки", "Нажмите клавишу, чтобы повесить рисунок на стену перед собой."));
    public final ButtonSetting nextPicture = this.register(new ButtonSetting("Сменить рисунок", "Взять следующий PNG из папки.").label("Сменить").onClick(Graffiti::cyclePicture));
    public final ButtonSetting placeNow = this.register(new ButtonSetting("Поставить по прицелу", "Повесить рисунок на блок, на который вы смотрите.").label("Поставить").onClick(Graffiti::placeAtCrosshair));
    public final ButtonSetting editor = this.register(new ButtonSetting("Редактор граффити", "Открыть редактор: слои, кисть, цвета и сохранение в PNG.").label("Открыть").onClick(Graffiti::openEditor));
    public final ButtonSetting clearAll = this.register(new ButtonSetting("Снять все наклейки", "Убрать все рисунки из мира (файлы PNG остаются).").label("Снять").onClick(Graffiti::clearAll));

    private final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Вид наклейки"));
    public final SliderSetting size = this.register(new SliderSetting("Размер, блоки", "Насколько крупный рисунок на стене.", 1.4f, 0.35f, 4.0f, 0.05f));
    public final SliderSetting rotation = this.register(new SliderSetting("Поворот, °", "Поворот рисунка вокруг оси стены.", 0.0f, -180.0f, 180.0f, 5.0f));
    public final SliderSetting opacity = this.register(new SliderSetting("Прозрачность, %", "Насколько плотный рисунок.", 100.0f, 20.0f, 100.0f, 5.0f));
    public final BooleanSetting emissive = this.register(new BooleanSetting("Свечение", "Рисунок не гаснет в темноте, как светящаяся краска.", true));
    public final BooleanSetting fadeIn = this.register(new BooleanSetting("Плавное появление", "Наклейка мягко проявляется после установки.", true));
    public final SliderSetting range = this.register(new SliderSetting("Дальность, блоков", "С какого расстояния показывать наклейки.", 64.0f, 16.0f, 160.0f, 8.0f));

    private final ParticleRenderer renderer = new ParticleRenderer();
    private final List<GraffitiStore.Decal> decals = new ArrayList<GraffitiStore.Decal>();
    private boolean loaded;
    private boolean keyWasDown;

    public Graffiti() {
        super("Graffiti", "Свои граффити в мире: векторный редактор, PNG-слои, свечение и плавное появление.", Category.VISUALS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        this.loaded = false;
    }

    @Override
    protected void onDisable() {
        this.decals.clear();
        this.loaded = false;
    }

    private void ensureLoaded() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        this.decals.clear();
        this.decals.addAll(GraffitiStore.load());
    }

    /** Открывает редактор граффити. */
    public static void openEditor() {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new rtx.byazen.api.ui.GraffitiEditorScreen());
        }
    }

    /** Выбирает следующий рисунок из папки. */
    public static void cyclePicture() {
        Graffiti module = rtx.byazen.api.modules.ModuleManager.get().get(Graffiti.class);
        List<Path> files = GraffitiStore.saved();
        if (module == null || files.isEmpty()) {
            return;
        }
        String current = module.file.getText();
        int index = 0;
        for (int i = 0; i < files.size(); ++i) {
            if (files.get(i).getFileName().toString().equalsIgnoreCase(current)) {
                index = (i + 1) % files.size();
                break;
            }
        }
        module.file.setText(files.get(index).getFileName().toString());
    }

    /** Вешает текущий рисунок на блок под прицелом. */
    public static void placeAtCrosshair() {
        Graffiti module = rtx.byazen.api.modules.ModuleManager.get().get(Graffiti.class);
        if (module == null || module.mc.player == null || module.mc.world == null) {
            return;
        }
        module.place();
    }

    /** Убирает все наклейки. */
    public static void clearAll() {
        Graffiti module = rtx.byazen.api.modules.ModuleManager.get().get(Graffiti.class);
        if (module == null) {
            return;
        }
        module.ensureLoaded();
        module.decals.clear();
        GraffitiStore.save(module.decals);
    }

    private void place() {
        this.ensureLoaded();
        HitResult hitResult = this.mc.crosshairTarget;
        if (!(hitResult instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult blockHitResult = (BlockHitResult)hitResult;
        String picture = this.file.getText();
        if (picture == null || picture.isBlank()) {
            picture = GraffitiStore.nextName();
            this.file.setText(picture);
        }
        GraffitiStore.Decal decal = new GraffitiStore.Decal();
        decal.world = this.mc.world.getRegistryKey().getValue().toString();
        decal.file = picture.trim();
        Vec3d position = blockHitResult.getPos();
        // грань определяем по геометрии попадания: так не зависим от имён методов Direction
        double fx = position.x - Math.floor(position.x);
        double fy = position.y - Math.floor(position.y);
        double fz = position.z - Math.floor(position.z);
        double ax = Math.min(fx, 1.0 - fx);
        double ay = Math.min(fy, 1.0 - fy);
        double az = Math.min(fz, 1.0 - fz);
        double planeX = position.x;
        double planeY = position.y;
        double planeZ = position.z;
        if (ax <= ay && ax <= az) {
            decal.side = fx < 0.5 ? "west" : "east";
            planeX = fx < 0.5 ? Math.floor(position.x) : Math.floor(position.x) + 1.0;
        }
        else if (ay <= az) {
            decal.side = fy < 0.5 ? "down" : "up";
            planeY = fy < 0.5 ? Math.floor(position.y) : Math.floor(position.y) + 1.0;
        }
        else {
            decal.side = fz < 0.5 ? "north" : "south";
            planeZ = fz < 0.5 ? Math.floor(position.z) : Math.floor(position.z) + 1.0;
        }
        decal.x = planeX;
        decal.y = planeY;
        decal.z = planeZ;
        decal.size = this.size.getValue();
        decal.rotation = this.rotation.getValue();
        decal.createdAt = System.currentTimeMillis();
        this.decals.add(decal);
        GraffitiStore.save(this.decals);
        rtx.byazen.utils.sounds.Sounds.play("select_category");
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || !this.isEnabled()) {
            return;
        }
        this.ensureLoaded();
        boolean down = false;
        if (this.placeKey.isBound() && this.mc.getWindow() != null) {
            down = this.placeKey.getValue().isDown(this.mc.getWindow().getHandle());
        }
        if (down && !this.keyWasDown) {
            this.place();
        }
        this.keyWasDown = down;
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.mc.world == null || this.decals.isEmpty()) {
            return;
        }
        Vec3d camera = worldRenderEvent.getCamera() != null
                ? worldRenderEvent.getCamera().getCameraPos()
                : (this.mc.gameRenderer.getCamera() != null ? this.mc.gameRenderer.getCamera().getCameraPos() : null);
        if (camera == null) {
            return;
        }
        String world = this.mc.world.getRegistryKey().getValue().toString();
        double maxDistance = (double)this.range.getValue();
        double maxSquared = maxDistance * maxDistance;
        float alpha = this.opacity.getValue() / 100.0f;
        VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        this.renderer.clear();
        boolean drawn = false;
        for (GraffitiStore.Decal decal : this.decals) {
            if (decal == null || !world.equals(decal.world)) {
                continue;
            }
            Vec3d center = new Vec3d(decal.x, decal.y, decal.z);
            if (center.squaredDistanceTo(camera) > maxSquared) {
                continue;
            }
            Path path = GraffitiStore.fileOf(decal);
            if (path == null) {
                continue;
            }
            String textureId = LocalImages.textureFor(path);
            if (textureId == null) {
                continue;
            }
            Identifier texture = Graffiti.identifier(textureId);
            if (texture == null) {
                continue;
            }
            Vec3d normal = Graffiti.normalOf(decal.side);
            Vec3d at = center.add(normal.multiply(0.012));
            Quaternionf orientation = Graffiti.orientationOf(decal.side);
            float localAlpha = alpha * (this.fadeIn.getValue() ? Graffiti.fadeAlpha(decal.createdAt) : 1.0f);
            int color = Graffiti.withAlpha(0xFFFFFF, localAlpha);
            this.renderer.drawTexture(worldRenderEvent.getStack(), immediate, texture, at, camera, orientation,
                    decal.size, decal.rotation + Graffiti.inPlaneTilt(decal.side), color, this.emissive.getValue());
            drawn = true;
        }
        if (drawn) {
            this.renderer.flush(immediate);
        }
    }

    private static float fadeAlpha(long createdAt) {
        if (createdAt <= 0L) {
            return 1.0f;
        }
        long elapsed = System.currentTimeMillis() - createdAt;
        if (elapsed <= 0L) {
            return 1.0f;
        }
        float progress = Math.min(1.0f, (float)elapsed / 520.0f);
        return progress * progress * (3.0f - 2.0f * progress);
    }

    private static Identifier identifier(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            int index = value.indexOf(':');
            return index < 0 ? Identifier.of("byazen", value) : Identifier.of(value.substring(0, index), value.substring(index + 1));
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    private static Vec3d normalOf(String side) {
        if (side == null) {
            return new Vec3d(0.0, 0.0, 1.0);
        }
        switch (side) {
            case "north": {
                return new Vec3d(0.0, 0.0, -1.0);
            }
            case "east": {
                return new Vec3d(1.0, 0.0, 0.0);
            }
            case "west": {
                return new Vec3d(-1.0, 0.0, 0.0);
            }
            case "up": {
                return new Vec3d(0.0, 1.0, 0.0);
            }
            case "down": {
                return new Vec3d(0.0, -1.0, 0.0);
            }
            default: {
                return new Vec3d(0.0, 0.0, 1.0);
            }
        }
    }

    /** Ориентация плоскости рисунка: по умолчанию она смотрит в +Z. */
    private static Quaternionf orientationOf(String side) {
        float half = (float)Math.PI * 0.5f;
        if (side == null) {
            return new Quaternionf();
        }
        switch (side) {
            case "north": {
                return new Quaternionf().rotateY((float)Math.PI);
            }
            case "east": {
                return new Quaternionf().rotateY(half);
            }
            case "west": {
                return new Quaternionf().rotateY(-half);
            }
            case "up": {
                return new Quaternionf().rotateX(-half);
            }
            case "down": {
                return new Quaternionf().rotateX(half);
            }
            default: {
                return new Quaternionf();
            }
        }
    }

    /** Для пола и потолка рисунок удобнее развернуть на 90°, чтобы он читался сбоку. */
    private static float inPlaneTilt(String side) {
        if ("up".equals(side)) {
            return 0.0f;
        }
        if ("down".equals(side)) {
            return 180.0f;
        }
        return 0.0f;
    }

    private static int withAlpha(int rgb, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(255.0f * alpha)));
        return a << 24 | rgb & 0xFFFFFF;
    }
}
