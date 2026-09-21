package rtx.byazen.utils.world;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import rtx.byazen.ByAzen;

/**
 * Текстуры, которые клиент рисует сам и обновляет каждый кадр или тик: мини-карта, радар,
 * предпросмотры (идеи №71, №68). Один и тот же ключ переиспользует свою текстуру, поэтому
 * частые обновления не создают мусор в GPU.
 */
public final class DynamicTextures {

    private static final Map<String, DynamicTextures.Slot> SLOTS = new LinkedHashMap<String, DynamicTextures.Slot>();

    private DynamicTextures() {
    }

    /** Отдаёт строковый id текстуры, готовой к отрисовке через Render2D.image. */
    public static String acquire(String key, int width, int height) {
        if (key == null || width <= 0 || height <= 0) {
            return null;
        }
        synchronized (SLOTS) {
            DynamicTextures.Slot slot = SLOTS.get(key);
            if (slot == null) {
                slot = new DynamicTextures.Slot();
                SLOTS.put(key, slot);
            }
            if (slot.identifier != null && slot.width == width && slot.height == height) {
                return slot.identifier.toString();
            }
            DynamicTextures.dispose(slot);
            try {
                NativeImage image = new NativeImage(width, height, false);
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        image.setColorArgb(x, y, 0);
                    }
                }
                slot.texture = RenderSystem.getDevice().createTexture(() -> "byazen_dyn_" + key, 5, TextureFormat.RGBA8, width, height, 1, 1);
                RenderSystem.getDevice().createCommandEncoder().writeToTexture(slot.texture, image);
                slot.view = RenderSystem.getDevice().createTextureView(slot.texture);
                image.close();
                slot.identifier = Identifier.of("byazen", "dynamic/" + key.replaceAll("[^a-zA-Z0-9_]", "_"));
                slot.width = width;
                slot.height = height;
                slot.image = new NativeImage(width, height, false);
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) {
                    client.getTextureManager().registerTexture(slot.identifier, new DynamicTextures.BoundTexture(slot.texture, slot.view));
                }
                return slot.identifier.toString();
            }
            catch (Throwable throwable) {
                ByAzen.LOGGER.warn("[ByAzen] Не удалось создать текстуру {}: {}", key, throwable.toString());
                return null;
            }
        }
    }

    /** Заливает текстуру пикселями (ARGB), размер обязан совпадать с {@link #acquire}. */
    public static void upload(String key, int[] argb, int width, int height) {
        synchronized (SLOTS) {
            DynamicTextures.Slot slot = SLOTS.get(key);
            if (slot == null || slot.texture == null || slot.image == null || slot.width != width || slot.height != height || argb == null) {
                return;
            }
            try {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        slot.image.setColorArgb(x, y, argb[y * width + x]);
                    }
                }
                RenderSystem.getDevice().createCommandEncoder().writeToTexture(slot.texture, slot.image);
            }
            catch (Throwable throwable) {
                ByAzen.LOGGER.debug("[ByAzen] Не удалось обновить текстуру {}: {}", key, throwable.toString());
            }
        }
    }

    public static void release(String key) {
        synchronized (SLOTS) {
            DynamicTextures.Slot slot = SLOTS.remove(key);
            DynamicTextures.dispose(slot);
        }
    }

    private static void dispose(DynamicTextures.Slot slot) {
        if (slot == null) {
            return;
        }
        try {
            if (slot.image != null) {
                slot.image.close();
            }
        }
        catch (Throwable ignored) {
        }
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && slot.identifier != null) {
                client.getTextureManager().destroyTexture(slot.identifier);
            }
        }
        catch (Throwable ignored) {
        }
        slot.image = null;
        slot.identifier = null;
        slot.texture = null;
        slot.view = null;
    }

    private static final class Slot {
        private Identifier identifier;
        private GpuTexture texture;
        private GpuTextureView view;
        private NativeImage image;
        private int width;
        private int height;
    }

    private static final class BoundTexture
    extends AbstractTexture {
        private final GpuTexture gpuTexture;
        private final GpuTextureView gpuTextureView;

        BoundTexture(GpuTexture gpuTexture, GpuTextureView gpuTextureView) {
            this.gpuTexture = gpuTexture;
            this.gpuTextureView = gpuTextureView;
        }

        public void load(ResourceManager resourceManager) {
        }

        public GpuTextureView getGlTextureView() {
            return this.gpuTextureView;
        }

        public GpuTexture getGlTexture() {
            return this.gpuTexture;
        }
    }
}
