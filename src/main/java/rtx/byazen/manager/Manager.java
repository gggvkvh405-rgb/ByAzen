package rtx.byazen.manager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import rtx.byazen.ByAzen;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import rtx.byazen.api.chat.commands.CommandManager;
import rtx.byazen.api.chat.irc.IrcClient;
import rtx.byazen.api.config.ConfigManager;
import rtx.byazen.api.drags.DragSystem;
import rtx.byazen.api.events.funtime.FunTimeEventsClient;
import rtx.byazen.api.liteapi.LiteApiClient;
import rtx.byazen.api.mods.chatanim.ChatAnimationMod;
import rtx.byazen.api.mods.chathads.ChatHeads;
import rtx.byazen.api.mods.geckolib.GeckoLibClient;
import rtx.byazen.api.mods.shulkerview.ShulkerViewMod;
import rtx.byazen.api.mods.waveycapes.WaveyCapesMod;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Visuals.custompet.CustomPetWarmup;
import rtx.byazen.api.party.PartyClient;
import rtx.byazen.utils.animations.AnimationUtil;
import rtx.byazen.utils.discord.rpc.DiscordRPCManager;
import rtx.byazen.utils.input.GuiMovementHandler;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.render.render2d.gif.GifRenderer;
import rtx.byazen.utils.render.warmup.Load;
import rtx.byazen.utils.render.warmup.Render2DWarmup;
import rtx.byazen.utils.sounds.SoundManager;
import rtx.byazen.utils.startup.LazyTasks;
import rtx.byazen.utils.startup.StartTrace;
import rtx.byazen.utils.storage.macro.MacroHandler;

public final class Manager {
    private Manager() {
    }

    public static void shutdown() {
        ConfigManager.saveAll();
        FunTimeEventsClient.INSTANCE.shutdown();
        IrcClient.INSTANCE.stop();
        PartyClient.INSTANCE.stop();
        LiteApiClient.INSTANCE.stop();
        Render2D.close();
    }

    public static void init() {
        StartTrace.begin();
        boolean safe = StartTrace.safeMode();
        Render2D.init();
        StartTrace.step("1/12 · отрисовка клиента");
        if (safe) {
            ByAzen.LOGGER.warn("[ByAzen] безопасный режим: прогрев отрисовки пропущен");
        }
        else {
            Render2DWarmup.init();
        }
        AnimationUtil.init();
        StartTrace.step("2/12 · анимации и управление камерой");
        GuiMovementHandler.init();
        ModuleManager.get().init();
        StartTrace.step("3/12 · модули (" + ModuleManager.get().getAll().size() + ")");
        ConfigManager.init();
        StartTrace.step("4/12 · конфиги");
        CommandManager.get().init();
        StartTrace.step("5/12 · команды (" + CommandManager.getInstance().getCommands().size() + ")");
        MacroHandler.init();
        DragSystem.get().init();
        StartTrace.step("6/12 · макросы и HUD");
        FunTimeEventsClient.INSTANCE.start();
        new GeckoLibClient().onInitializeClient();
        StartTrace.step("7/12 · анимации мобов и события");
        SoundManager.init();
        StartTrace.step("8/12 · звуки интерфейса");
        LiteApiClient.INSTANCE.start();
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraftClient -> Manager.shutdown());
        // тяжёлые интеграции готовятся не на старте, а при входе в мир (идея №165)
        if (safe) {
            LazyTasks.setAutoRun(false);
            ByAzen.LOGGER.warn("[ByAzen] безопасный режим: отложенные интеграции (плащи, шалкеры, чат) не готовятся");
        }
        else {
            LazyTasks.submit("Косые плащи", () -> WaveyCapesMod.INSTANCE.init());
            LazyTasks.submit("Просмотр шалкеров", ShulkerViewMod::init);
            LazyTasks.submit("Головы в чате", ChatHeads::init);
            LazyTasks.submit("Анимации чата", ChatAnimationMod::init);
        }
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(LazyTasks::tick);
        // один раз при входе в мир: проверка требований и конфликтов миксинов (идеи №171, №173)
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client == null || client.player == null) {
                return;
            }
            rtx.byazen.utils.startup.EnvCheck.warnIfNeeded();
            rtx.byazen.utils.compat.MixinAudit.warnIfNeeded();
        });
        Thread thread = new Thread(DiscordRPCManager::start, "ByAzen-Discord-RPC-Init");
        thread.setDaemon(true);
        thread.start();
        StartTrace.step("9/12 · Discord RPC и фоновые службы");
        MinecraftClient minecraftClient2 = MinecraftClient.getInstance();
        Runnable runnable = () -> {
            if (StartTrace.safeMode()) {
                return;
            }
            LazyTasks.submit("Первый GIF клиента", () -> GifRenderer.preload("byazen:gif/kity.gif"));
            LazyTasks.submit("Прогрев отрисовки", Load::runStartupWarmup);
        };
        if (minecraftClient2 != null) {
            minecraftClient2.execute(runnable);
        } else {
            runnable.run();
        }
        StartTrace.step("10/12 · прогревы отрисовки поставлены в очередь");
        Identifier identifier = Identifier.of((String)"byazen", (String)"ui_font_rewarmup");
        ResourceLoader.get((ResourceType)ResourceType.CLIENT_RESOURCES).registerReloader(identifier, (ResourceReloader)((SynchronousResourceReloader)resourceManager -> {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) {
                return;
            }
            minecraftClient.execute(() -> {
                boolean bl = !Load.initialReloadSeen;
                Load.initialReloadSeen = true;
                if (bl) {
                    Load.runStartupWarmup();
                    return;
                }
                Load.warmupFonts();
                try {
                    Render2DWarmup.reset();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    CustomPetWarmup.warmup();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
        }));
        StartTrace.step("11/12 · слушатель перезагрузки ресурсов");
        StartTrace.finished();
    }
}

