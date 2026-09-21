package rtx.byazen.api.music;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
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
 * Cover art loader of the music player: downloads artwork in the background, decodes it and uploads
 * it as a GPU texture. Textures are cached by url and evicted when the cache grows too large.
 * <p>
 * {@link #textureFor(String)} is meant to be called from the render thread - it never blocks.
 */
public final class MusicCovers {

    private static final int CACHE_LIMIT = 48;
    private static final AtomicInteger ID = new AtomicInteger();
    private static final Map<String, MusicCovers.Cover> CACHE = new LinkedHashMap<String, MusicCovers.Cover>();

    private MusicCovers() {
    }

    /** Texture id usable with Render2D, or null while it is still downloading/uploading. */
    public static String textureFor(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        MusicCovers.Cover cover;
        synchronized (CACHE) {
            cover = CACHE.get(url);
            if (cover == null) {
                cover = new MusicCovers.Cover();
                CACHE.put(url, cover);
                MusicCovers.evict();
                MusicCovers.download(url, cover);
            }
        }
        synchronized (cover) {
            if (cover.decoded != null) {
                BufferedImage image = cover.decoded;
                cover.decoded = null;
                cover.state = MusicCovers.State.UPLOADING;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) {
                    client.execute(() -> MusicCovers.upload(url, cover, image));
                }
            }
            return cover.state == MusicCovers.State.READY ? cover.textureId : null;
        }
    }

    public static void clear() {
        synchronized (CACHE) {
            for (MusicCovers.Cover cover : CACHE.values()) {
                MusicCovers.destroy(cover);
            }
            CACHE.clear();
        }
    }

    private static void evict() {
        while (CACHE.size() > CACHE_LIMIT) {
            String oldest = CACHE.keySet().iterator().next();
            MusicCovers.destroy(CACHE.remove(oldest));
        }
    }

    private static void destroy(MusicCovers.Cover cover) {
        if (cover == null || cover.identifier == null) {
            return;
        }
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.getTextureManager().destroyTexture(cover.identifier);
            }
        }
        catch (Throwable ignored) {
        }
        cover.identifier = null;
        cover.textureId = null;
        cover.state = MusicCovers.State.IDLE;
    }

    private static void download(String url, MusicCovers.Cover cover) {
        cover.state = MusicCovers.State.DOWNLOADING;
        Thread thread = new Thread(() -> {
            try {
                URLConnection connection = URI.create(url).toURL().openConnection();
                connection.setRequestProperty("User-Agent", "ByAzen/1.3.0");
                connection.setConnectTimeout(7000);
                connection.setReadTimeout(9000);
                BufferedImage image;
                try (InputStream stream = connection.getInputStream()) {
                    image = ImageIO.read(stream);
                }
                if (image == null) {
                    synchronized (cover) {
                        cover.state = MusicCovers.State.FAILED;
                    }
                    return;
                }
                if (image.getWidth() > 320 || image.getHeight() > 320) {
                    image = MusicCovers.scale(image, Math.min(320, Math.max(image.getWidth(), image.getHeight())));
                }
                synchronized (cover) {
                    cover.decoded = image;
                    cover.state = MusicCovers.State.DECODED;
                }
            }
            catch (Throwable throwable) {
                synchronized (cover) {
                    cover.state = MusicCovers.State.FAILED;
                }
                ByAzen.LOGGER.debug("[ByAzen] Cover art failed ({}): {}", url, throwable.toString());
            }
        }, "byazen-music-cover");
        thread.setDaemon(true);
        thread.start();
    }

    private static BufferedImage scale(BufferedImage source, int size) {
        BufferedImage target = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D graphics = target.createGraphics();
        graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        double scale = Math.max((double) size / source.getWidth(), (double) size / source.getHeight());
        int width = (int) Math.round(source.getWidth() * scale);
        int height = (int) Math.round(source.getHeight() * scale);
        graphics.drawImage(source, (size - width) / 2, (size - height) / 2, width, height, null);
        graphics.dispose();
        return target;
    }

    private static void upload(String url, MusicCovers.Cover cover, BufferedImage image) {
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
            GpuTexture texture = RenderSystem.getDevice().createTexture(() -> "byazen_music_cover_" + id, 5, TextureFormat.RGBA8, width, height, 1, 1);
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, nativeImage);
            GpuTextureView view = RenderSystem.getDevice().createTextureView(texture);
            nativeImage.close();
            Identifier identifier = Identifier.of("byazen", "music/cover_" + id);
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) {
                synchronized (cover) {
                    cover.state = MusicCovers.State.FAILED;
                }
                return;
            }
            client.getTextureManager().registerTexture(identifier, new MusicCovers.CoverTexture(texture, view));
            synchronized (cover) {
                cover.identifier = identifier;
                cover.textureId = identifier.toString();
                cover.state = MusicCovers.State.READY;
            }
        }
        catch (Throwable throwable) {
            synchronized (cover) {
                cover.state = MusicCovers.State.FAILED;
            }
            ByAzen.LOGGER.warn("[ByAzen] Cover art could not be uploaded ({}): {}", url, throwable.toString());
        }
    }

    private enum State {
        IDLE,
        DOWNLOADING,
        DECODED,
        UPLOADING,
        READY,
        FAILED
    }

    private static final class Cover {
        private State state = MusicCovers.State.IDLE;
        private Identifier identifier;
        private String textureId;
        private BufferedImage decoded;
    }

    public static final class CoverTexture
    extends AbstractTexture {
        private final GpuTexture gpuTexture;
        private final GpuTextureView gpuTextureView;

        public CoverTexture(GpuTexture gpuTexture, GpuTextureView gpuTextureView) {
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
