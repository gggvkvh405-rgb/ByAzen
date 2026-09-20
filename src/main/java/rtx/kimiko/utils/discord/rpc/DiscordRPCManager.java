package rtx.kimiko.utils.discord.rpc;
import fun.shape.profile.Profile;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rtx.kimiko.utils.discord.rpc.DiscordNativeLoader;
import rtx.kimiko.utils.discord.rpc.utils.DiscordEventHandlers;
import rtx.kimiko.utils.discord.rpc.utils.DiscordRPC;
import rtx.kimiko.utils.discord.rpc.utils.DiscordRichPresence;
import rtx.kimiko.utils.discord.rpc.utils.DiscordUser;
import rtx.kimiko.utils.discord.rpc.utils.RPCButton;
import rtx.kimiko.utils.profile.ProfileIdentity;

public final class DiscordRPCManager {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Kimiko/DiscordRPC");
    private static final AtomicBoolean STARTED = new AtomicBoolean(false);
    private static final String CLIENT_ID = "1519057999552057436";
    private static final long CALLBACK_DELAY_MS = 15000L;
    private static final String[] DETAILS_FRAMES = new String[]{"Build \u2726 1.0 \u2726", "Build \u2726 1.0 \u2727", "Build \u2727 1.0 \u2726", "Build \u2727 1.0 \u2727"};
    private static final long DETAILS_ANIM_INTERVAL_MS = 3500L;
    private static volatile boolean running;
    private static volatile long startedAt;
    private static volatile Thread callbackThread;
    private static volatile Thread shutdownHook;
    private static final AtomicInteger detailsFrame;
    private static volatile ScheduledExecutorService detailsScheduler;
    private static volatile String avatarUrl;
    private static volatile String username;

    private DiscordRPCManager() {
    }

    static {
        detailsFrame = new AtomicInteger(0);
        avatarUrl = "";
        username = "";
    }

    public static void start() {
        if (!STARTED.compareAndSet(false, true)) {
            return;
        }
        if (DiscordRPCManager.isLinux()) {
            STARTED.set(false);
            return;
        }
        try {
            DiscordNativeLoader.prepare();
            startedAt = System.currentTimeMillis() / 1000L;
            DiscordEventHandlers discordEventHandlers = new DiscordEventHandlers.Builder().ready(DiscordRPCManager::onReady).build();
            DiscordRPC.INSTANCE.Discord_Initialize(CLIENT_ID, discordEventHandlers, true, "");
            running = true;
            callbackThread = new Thread(DiscordRPCManager::runCallbacksLoop, "Kimiko-Discord-RPC");
            callbackThread.setDaemon(true);
            callbackThread.start();
            shutdownHook = new Thread(DiscordRPCManager::stop, "Kimiko-Discord-RPC-Shutdown");
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
        catch (Throwable throwable) {
            running = false;
            STARTED.set(false);
            LOGGER.debug("Failed to start Discord RPC", throwable);
        }
    }

    private static boolean isLinux() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux");
    }

    public static void stop() {
        if (!STARTED.compareAndSet(true, false)) {
            return;
        }
        running = false;
        ScheduledExecutorService scheduledExecutorService = detailsScheduler;
        detailsScheduler = null;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
        Thread thread = callbackThread;
        callbackThread = null;
        if (thread != null) {
            thread.interrupt();
        }
        Thread thread2 = shutdownHook;
        shutdownHook = null;
        if (thread2 != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(thread2);
            }
            catch (IllegalStateException illegalStateException) {
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        try {
            DiscordRPC.INSTANCE.Discord_ClearPresence();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            DiscordRPC.INSTANCE.Discord_Shutdown();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static String username() {
        String string = username;
        return string == null || string.isEmpty() ? null : string;
    }

    private static void onReady(DiscordUser discordUser) {
        try {
            avatarUrl = "";
            if (discordUser != null && discordUser.userId != null && discordUser.avatar != null && !discordUser.userId.isBlank() && !discordUser.avatar.isBlank()) {
                avatarUrl = "https://cdn.discordapp.com/avatars/" + discordUser.userId + "/" + discordUser.avatar + ".png";
            }
            username = discordUser != null && discordUser.username != null ? discordUser.username : "";
            detailsFrame.set(0);
            DiscordRPC.INSTANCE.Discord_UpdatePresence(DiscordRPCManager.buildPresence(DETAILS_FRAMES[0]));
            DiscordRPCManager.startDetailsAnimation();
        }
        catch (Throwable throwable) {
            LOGGER.debug("Failed to update Discord presence", throwable);
        }
    }

    public static String avatarUrl() {
        String string = avatarUrl;
        return string == null || string.isEmpty() ? null : string;
    }

    private static String profileUsername() {
        String string = Profile.getUsername();
        if (string != null && !string.isBlank()) {
            return string;
        }
        return DiscordRPCManager.currentUsername();
    }

    private static String smallImageUrl() {
        String string = ProfileIdentity.avatarUrl();
        if (string != null) {
            return string;
        }
        return avatarUrl != null && !avatarUrl.isEmpty() ? avatarUrl : null;
    }

    private static void runCallbacksLoop() {
        while (running) {
            try {
                DiscordRPC.INSTANCE.Discord_RunCallbacks();
                Thread.sleep(15000L);
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                break;
            }
            catch (Throwable throwable) {
                LOGGER.debug("Discord RPC callback loop error", throwable);
                break;
            }
        }
    }

    private static DiscordRichPresence buildPresence(String string) {
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder().setStartTimestamp(startedAt).setDetails(string).setState("User: " + DiscordRPCManager.profileUsername() + " | Uid: " + Profile.getUid()).setButtons(RPCButton.create("Discord", "https://discord.gg/mCjMnPPYKA"), RPCButton.create("Telegram", "https://t.me/kimikodlc"));
        String string2 = DiscordRPCManager.smallImageUrl();
        if (string2 != null && !string2.isEmpty()) {
            builder.setSmallImage(string2, DiscordRPCManager.profileUsername());
        }
        return builder.build();
    }

    private static void tickDetails() {
        if (!running) {
            return;
        }
        try {
            int n2 = detailsFrame.updateAndGet(n -> (n + 1) % DETAILS_FRAMES.length);
            DiscordRPC.INSTANCE.Discord_UpdatePresence(DiscordRPCManager.buildPresence(DETAILS_FRAMES[n2]));
        }
        catch (Throwable throwable) {
            LOGGER.debug("Discord RPC details animation error", throwable);
        }
    }

    private static String currentUsername() {
        try {
            String string;
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient != null && minecraftClient.getSession() != null && (string = minecraftClient.getSession().getUsername()) != null && !string.isBlank()) {
                return string;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return "Player";
    }

    private static void startDetailsAnimation() {
        ScheduledExecutorService scheduledExecutorService;
        if (detailsScheduler != null) {
            return;
        }
        detailsScheduler = scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "Kimiko-Discord-RPC-Anim");
            thread.setDaemon(true);
            return thread;
        });
        scheduledExecutorService.scheduleAtFixedRate(DiscordRPCManager::tickDetails, 3500L, 3500L, TimeUnit.MILLISECONDS);
    }
}

