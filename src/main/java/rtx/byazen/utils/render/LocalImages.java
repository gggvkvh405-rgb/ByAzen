package rtx.byazen.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import rtx.byazen.ByAzen;

/**
 * Кэш текстур для локальных картинок (скриншоты в галерее, свои граффити и т.п.).
 * <p>
 * Файл читается и декодируется в фоновом потоке, отправка в GPU - в клиентском, вызов
 * {@link #textureFor(Path)} безопасен из отрисовки и никогда не блокирует кадр.
 * Текстуры выгружаются, когда кэш перерастает лимит (идея №164: лимит памяти и корректная выгрузка).
 */
public final class LocalImages {

    private static int cacheLimit = 72;
    private static final AtomicInteger ID = new AtomicInteger();
    private static final Map<String, LocalImages.Entry> CACHE = new LinkedHashMap<String, LocalImages.Entry>();

    private LocalImages() {
    }

    public static String textureFor(Path path) {
        if (path == null) {
            return null;
        }
        String key = path.toAbsolutePath().toString();
        LocalImages.Entry entry;
        synchronized (CACHE) {
            entry = CACHE.get(key);
            if (entry == null) {
                entry = new LocalImages.Entry();
                CACHE.put(key, entry);
                LocalImages.evict();
                LocalImages.load(path, entry);
            }
        }
        LocalImages.Entry current = entry;
        synchronized (current) {
            if (current.decoded != null) {
                BufferedImage image = current.decoded;
                current.decoded = null;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) {
                    client.execute(() -> LocalImages.upload(key, current, image));
                }
            }
            return current.state == LocalImages.State.READY ? current.textureId : null;
        }
    }

    public static void clear() {
        synchronized (CACHE) {
            for (LocalImages.Entry entry : CACHE.values()) {
                LocalImages.destroy(entry);
            }
            CACHE.clear();
        }
    }

    /** Предел кэша настраивается из окна «Диагностика» (идея №164). */
    public static void setCacheLimit(int limit) {
        cacheLimit = Math.max(4, Math.min(512, limit));
        synchronized (CACHE) {
            evict();
        }
    }

    public static int cacheLimit() {
        return cacheLimit;
    }

    public static int cacheSize() {
        synchronized (CACHE) {
            return CACHE.size();
        }
    }

    private static void evict() {
        while (CACHE.size() > cacheLimit) {
            String oldest = CACHE.keySet().iterator().next();
            LocalImages.destroy(CACHE.remove(oldest));
        }
    }

    private static void destroy(LocalImages.Entry entry) {
        if (entry == null || entry.identifier == null) {
            return;
        }
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.getTextureManager().destroyTexture(entry.identifier);
            }
        }
        catch (Throwable ignored) {
        }
        entry.identifier = null;
        entry.textureId = null;
        entry.state = LocalImages.State.IDLE;
    }

    private static void load(Path path, LocalImages.Entry entry) {
        entry.state = LocalImages.State.LOADING;
        Thread thread = new Thread(() -> {
            try {
                BufferedImage image;
                try (InputStream stream = Files.newInputStream(path)) {
                    image = ImageIO.read(stream);
                }
                if (image == null) {
                    synchronized (entry) {
                        entry.state = LocalImages.State.FAILED;
                    }
                    return;
                }
                synchronized (entry) {
                    entry.decoded = image;
                    entry.state = LocalImages.State.DECODED;
                }
            }
            catch (Throwable throwable) {
                synchronized (entry) {
                    entry.state = LocalImages.State.FAILED;
                }
                ByAzen.LOGGER.debug("[ByAzen] Не удалось прочитать картинку {}: {}", path, throwable.toString());
            }
        }, "byazen-local-image");
        thread.setDaemon(true);
        thread.start();
    }

    private static void upload(String key, LocalImages.Entry entry, BufferedImage image) {
        try {
            int width = image.getWidth();
            int height = image.getHeight();
            NativeImage nativeImage = new NativeImage(width, height, false);
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    nativeImage.setColorArgb(x, y, image.getRGB(x, y));
                }
            }
            int id = ID.getAndIncrement();
            GpuTexture texture = RenderSystem.getDevice().createTexture(() -> "byazen_local_image_" + id, 5, TextureFormat.RGBA8, width, height, 1, 1);
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, nativeImage);
            GpuTextureView view = RenderSystem.getDevice().createTextureView(texture);
            nativeImage.close();
            Identifier identifier = Identifier.of("byazen", "local/image_" + id);
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.getTextureManager().registerTexture(identifier, new LocalImages.LocalTexture(texture, view));
            }
            synchronized (entry) {
                entry.identifier = identifier;
                entry.textureId = identifier.toString();
                entry.state = LocalImages.State.READY;
            }
        }
        catch (Throwable throwable) {
            synchronized (entry) {
                entry.state = LocalImages.State.FAILED;
            }
            ByAzen.LOGGER.warn("[ByAzen] Картинка не загрузилась в GPU ({}): {}", key, throwable.toString());
        }
    }

    private enum State {
        IDLE,
        LOADING,
        DECODED,
        READY,
        FAILED
    }

    private static final class Entry {
        private LocalImages.State state = LocalImages.State.IDLE;
        private Identifier identifier;
        private String textureId;
        private BufferedImage decoded;
    }

    public static final class LocalTexture
    extends AbstractTexture {
        private final GpuTexture gpuTexture;
        private final GpuTextureView gpuTextureView;

        public LocalTexture(GpuTexture gpuTexture, GpuTextureView gpuTextureView) {
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
