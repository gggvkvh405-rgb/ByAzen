package rtx.byazen.api.mods.acountswiher.ru.vidtu.ias;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rtx.byazen.api.mods.acountswiher.ru.vidtu.ias.config.IASConfig;
import rtx.byazen.api.mods.acountswiher.ru.vidtu.ias.config.IASStorage;
import rtx.byazen.api.mods.acountswiher.ru.vidtu.ias.utils.Holder;

public final class IAS {
    public static final String CLIENT_ID = "54fd49e4-2103-4044-9603-2b028c814ec3";
    public static final Duration TIMEOUT = Duration.ofSeconds(Long.getLong("ias.timeout", 15L));
    public static final String USER_AGENT = "IAS/%s (https://github.com/The-Fireplace-Minecraft-Mods/In-Game-Account-Switcher; pig@vidtu.ru)".formatted(IAS.class.getPackage().getImplementationVersion());
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"IAS");
    private static ScheduledExecutorService executor;
    private static Path gameDirectory;
    private static Path configDirectory;
    private static boolean disabled;

    private IAS() {
        throw new AssertionError((Object)"No instances.");
    }

    static {
        disabled = false;
    }

    public static void close() {
        LOGGER.info("IAS: Closing IAS...");
        try {
            ScheduledExecutorService scheduledExecutorService = executor;
            if (scheduledExecutorService != null) {
                LOGGER.info("IAS: Shutting down IAS executor...");
                scheduledExecutorService.shutdown();
                if (scheduledExecutorService.awaitTermination(30L, TimeUnit.SECONDS)) {
                    LOGGER.info("IAS: IAS executor shut down.");
                } else {
                    LOGGER.warn("IAS: Unable to shutdown IAS executor. Shutting down forcefully...");
                    scheduledExecutorService.shutdownNow();
                    if (scheduledExecutorService.awaitTermination(30L, TimeUnit.SECONDS)) {
                        LOGGER.info("IAS: IAS executor shut down forcefully.");
                    } else {
                        LOGGER.error("IAS: Unable to shutdown IAS executor forcefully.");
                    }
                }
            }
        }
        catch (InterruptedException interruptedException) {
            LOGGER.error("IAS: IAS executor interrupted while shutting down. Shutting down forcefully...", (Throwable)interruptedException);
            ScheduledExecutorService scheduledExecutorService = executor;
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdownNow();
            }
            Thread.currentThread().interrupt();
        }
        executor = null;
        if (gameDirectory != null) {
            try {
                IAS.disclaimersStorage();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        LOGGER.info("IAS: IAS has been unloaded.");
    }

    public static void init(Path path, Path path2) {
        LOGGER.info("IAS: Initializing IAS...");
        gameDirectory = path;
        configDirectory = path2;
        LOGGER.debug("IAS: Current user agent: {}", (Object)USER_AGENT);
        try {
            IAS.disclaimersStorage();
        }
        catch (Throwable throwable) {
            LOGGER.error("IAS: Unable to write disclaimers.", throwable);
        }
        try {
            IAS.loadConfig();
        }
        catch (Throwable throwable) {
            LOGGER.error("IAS: Unable to load IAS config.", throwable);
        }
        try {
            IAS.loadStorage();
        }
        catch (Throwable throwable) {
            LOGGER.error("IAS: Unable to load IAS storage.", throwable);
        }
        executor = Executors.newSingleThreadScheduledExecutor(runnable -> new Thread(runnable, "IAS"));
        if (Boolean.getBoolean("ias.skipDisableScanning")) {
            LOGGER.debug("IAS: Skipped IAS remote scanning because system property is set.");
            return;
        }
        String string = String.valueOf(IAS.class.getPackage().getImplementationVersion());
        Holder holder = new Holder();
        holder.set(executor.scheduleWithFixedDelay(() -> {
            try {
                if (disabled || Boolean.getBoolean("ias.skipDisableScanning")) {
                    LOGGER.debug("IAS: Skipped IAS remote scanning because system property is set or the mod is already disabled.");
                    return;
                }
                HttpClient httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).executor(Runnable::run).build();
                HttpResponse<Stream<String>> httpResponse = httpClient.send(HttpRequest.newBuilder().uri(new URI("https://raw.githubusercontent.com/The-Fireplace-Minecraft-Mods/In-Game-Account-Switcher/main/.ias/disabled_v1")).header("User-Agent", USER_AGENT).timeout(TIMEOUT).GET().build(), HttpResponse.BodyHandlers.ofLines());
                int n = httpResponse.statusCode();
                if (n < 200 || n > 299) {
                    return;
                }
                boolean bl = disabled = disabled || httpResponse.body().anyMatch(string2 -> "ALL".equalsIgnoreCase(string2 = string2.strip()) || string.equalsIgnoreCase((String)string2));
                if (!disabled) {
                    LOGGER.debug("IAS: Completed remote disabling check. Not disabled.");
                    return;
                }
                LOGGER.error("IAS: The In-Game Account Switcher mod has been disabled by remote due to serious issues. Please, see the mod page for more information. ({})", (Object)string);
                ScheduledFuture scheduledFuture = (ScheduledFuture)holder.get();
                if (scheduledFuture == null) {
                    return;
                }
                scheduledFuture.cancel(false);
            }
            catch (Throwable throwable) {
                LOGGER.debug("IAS: Unable to perform remote disabling check.", throwable);
            }
        }, 0L, 60L, TimeUnit.MINUTES));
        LOGGER.info("IAS: IAS has been loaded.");
    }

    public static ScheduledExecutorService executor() {
        ScheduledExecutorService scheduledExecutorService = executor;
        Objects.requireNonNull(scheduledExecutorService, "IAS executor is not available.");
        return scheduledExecutorService;
    }

    public static void loadConfig() {
        IASConfig.load(configDirectory);
    }

    public static void saveStorage() {
        IASStorage.save(gameDirectory);
    }

    public static Path configDirectory() {
        Path path = configDirectory;
        Objects.requireNonNull(path, "IAS config directory is not available.");
        return path;
    }

    public static void gameDisclaimerShownStorage() {
        IASStorage.gameDisclaimerShown(gameDirectory);
    }

    public static void saveConfig() {
        IASConfig.save(configDirectory);
    }

    public static boolean disabled() {
        return disabled;
    }

    public static void loadStorage() {
        IASStorage.load(gameDirectory);
    }

    public static void disclaimersStorage() {
        IASStorage.disclaimers(gameDirectory);
    }
}

