/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.byazen;

import com.google.gson.Gson;
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import java.nio.file.Path;
import java.util.EnumSet;
import javax.imageio.spi.IIORegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.byazen.animation.AnimationQueryRegistry;
import ru.byazen.config.LocalConfigCatalog;
import ru.byazen.event.EventBus;
import ru.byazen.event.EventBusImpl;
import ru.byazen.event.KeyPressedEvent;
import ru.byazen.misc.BlockEspConfigStore;
import ru.byazen.misc.BlockEspStore;
import ru.byazen.misc.BlockedSoundList;
import ru.byazen.misc.BlockedSoundStore;
import ru.byazen.misc.ClasspathResource;
import ru.byazen.misc.ClientProfile;
import ru.byazen.misc.ClientRole;
import ru.byazen.misc.CommandManager;
import ru.byazen.misc.ConfigManager;
import ru.byazen.misc.ConfigRegistry;
import ru.byazen.misc.ContainerDisplaySettings;
import ru.byazen.misc.FriendList;
import ru.byazen.misc.FriendListStore;
import ru.byazen.misc.InventoryHudRegistry;
import ru.byazen.misc.KeybindRegistry;
import ru.byazen.misc.MacroConfigStore;
import ru.byazen.misc.MacroManager;
import ru.byazen.misc.PasswordConfigStore;
import ru.byazen.misc.PasswordStore;
import ru.byazen.misc.PotionPresetStore;
import ru.byazen.misc.ResourceResolver;
import ru.byazen.misc.ServerTickRate;
import ru.byazen.misc.SlotHighlightRegistry;
import ru.byazen.misc.SpookyTest;
import ru.byazen.misc.StaffNameConfigStore;
import ru.byazen.misc.StaffNameStore;
import ru.byazen.misc.TextureResource;
import ru.byazen.misc.ThemeColors;
import ru.byazen.misc.TickOverride;
import ru.byazen.misc.WaypointConfigStore;
import ru.byazen.misc.WaypointStore;
import ru.byazen.module.ModuleManager;
import ru.byazen.module.ModuleState;
import ru.byazen.module.hud.HUDModule;
import ru.byazen.notification.NotificationCenter;
import ru.byazen.render.ClientEventBridge;
import ru.byazen.render.ClientRenderPipelines;
import ru.byazen.render.GlShaderProgram;
import ru.byazen.render.GuiRenderPipeline;
import ru.byazen.render.OffscreenRenderManager;
import ru.byazen.render.ByAzenGlobals;
import ru.byazen.schedule.EventScheduleRegistry;
import ru.byazen.ui.HudLayoutManager;
import ru.byazen.ui.ByAzenScreen;
import ru.byazen.util.ClickGuiPanel;
import ru.byazen.util.Easing;
import ru.byazen.util.EspFeatureRegistry;
import ru.byazen.util.EspRenderCoordinator;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.GuiPhotoBanner;
import ru.byazen.util.GuiRenderBackend;
import ru.byazen.util.HotbarSlotLock;
import ru.byazen.util.InventoryController;
import ru.byazen.util.PotionCombiner;
import ru.byazen.util.PotionPresetController;
import ru.byazen.util.RotationController;
import ru.byazen.util.entity.NpcDetector;

public class ByAzenClient
implements ClientModInitializer {
    public static final String MOD_ID = "byazen";
    public static final String VERSION_LABEL = "ByAzen 1.21.11 #110826";
    static SlotHighlightRegistry slotHighlightRegistry;
    static FriendList friendList;
    static OffscreenRenderManager entityRenderPipeline;
    static RotationController rotationController;
    static GuiDrawApi legacyGuiRenderer;
    static EspRenderCoordinator espRenderCoordinator;
    static ServerTickRate serverTickRate;
    private EventScheduleRegistry eventSchedules;
    private Path configDirectory;
    private CommandManager commandManager;
    private ClickGuiPanel miscellaneous;
    private ContainerDisplaySettings containerDisplaySettings;
    private ClientProfile clientProfile;
    static BlockedSoundList blockedSoundList;
    private PotionPresetController potionPresetController;
    private ConfigRegistry configRegistry;
    private PotionCombiner potionCombiner;
    private StaffNameStore staffNameStore;
    static InventoryHudRegistry inventoryHudRegistry;
    static HotbarSlotLock hotbarSlotLock;
    static MacroManager macroManager;
    static GuiDrawApi gpuGuiRenderer;
    static NotificationCenter notificationCenter;
    static EspFeatureRegistry espFeatures;
    static EventBus eventBus;
    static InventoryController inventory;
    static TickOverride tickOverride;
    private Logger logger;
    static WaypointStore waypointStore;
    private LocalConfigCatalog localConfigCatalog;
    static NpcDetector npcDetector;
    static HudLayoutManager hudLayoutManager;
    static GuiDrawApi hudRenderer;
    private KeybindRegistry keybindRegistry;
    static PasswordStore passwordStore;
    static ConfigManager configManager;
    static BlockEspStore blockEspStore;
    private static GuiDrawApi guiRendererOverride;
    private ModuleManager moduleManager;
    static ByAzenClient instance;

    public static ServerTickRate getServerTickRate() {
        return serverTickRate;
    }

    public static InventoryController getInventoryController() {
        return inventory;
    }

    public static ByAzenClient getInstance() {
        return instance;
    }

    public void onInitializeClient() {
        this.bootstrap();
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public static RotationController getRotationController() {
        return rotationController;
    }

    public static GuiDrawApi getHudRenderer() {
        if (hudRenderer == null) {
            ByAzenClient.initializeHudRenderer();
        }
        return hudRenderer;
    }

    public static OffscreenRenderManager getRenderPipeline2() {
        if (entityRenderPipeline == null) {
            entityRenderPipeline = new OffscreenRenderManager();
        }
        return entityRenderPipeline;
    }

    public static NpcDetector getNpcDetector() {
        return npcDetector;
    }

    public static FriendList getFriends() {
        return friendList;
    }

    public static HotbarSlotLock getHotbarSlotLock() {
        return hotbarSlotLock;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public ClientProfile getClientProfile() {
        return this.clientProfile;
    }

    public static BlockEspStore getBlockEspStore() {
        return blockEspStore;
    }

    public static WaypointStore getWaypointStore() {
        return waypointStore;
    }

    public StaffNameStore getStaffNameStore() {
        return this.staffNameStore;
    }

    public static BlockedSoundList getBlockedSoundList() {
        return blockedSoundList;
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public static MacroManager getMacroManager() {
        return macroManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    private void initializeServices() {
        AnimationQueryRegistry.registerDefaults();
        eventBus = new EventBusImpl();
        this.keybindRegistry = new KeybindRegistry(eventBus);
        this.configRegistry = new ConfigRegistry();
        hudLayoutManager = new HudLayoutManager(this.configRegistry);
        tickOverride = new TickOverride(eventBus);
        serverTickRate = new ServerTickRate(eventBus);
        slotHighlightRegistry = new SlotHighlightRegistry();
        inventoryHudRegistry = new InventoryHudRegistry();
        inventory = new InventoryController(eventBus);
        hotbarSlotLock = new HotbarSlotLock(eventBus);
        SpookyTest rotationStrategies = new SpookyTest();
        rotationStrategies.update();
        rotationController = new RotationController(rotationStrategies, eventBus);
        configManager = new ConfigManager(this.configDirectory.toFile(), new Gson(), this.configRegistry, this.keybindRegistry);
        friendList = new FriendList(configManager.getStore(FriendListStore.class));
        this.staffNameStore = new StaffNameStore(configManager.getStore(StaffNameConfigStore.class));
        blockedSoundList = new BlockedSoundList(configManager.getStore(BlockedSoundStore.class));
        passwordStore = new PasswordStore(configManager.getStore(PasswordConfigStore.class));
        waypointStore = new WaypointStore(configManager.getStore(WaypointConfigStore.class));
        blockEspStore = new BlockEspStore(configManager.getStore(BlockEspConfigStore.class));
        macroManager = new MacroManager(configManager.getStore(MacroConfigStore.class), eventBus);
        npcDetector = new NpcDetector(eventBus);
        ModuleManager moduleManager = new ModuleManager();
        moduleManager.registerDefaults(eventBus);
        espFeatures = new EspFeatureRegistry(eventBus);
        moduleManager.getModules().addAll(espFeatures.getModules());
        this.moduleManager = moduleManager;
        moduleManager.registerKeybindSettings(this.keybindRegistry);
        moduleManager.registerConfigEntries(this.configRegistry);
        this.configRegistry.register(this.keybindRegistry);
        this.containerDisplaySettings = new ContainerDisplaySettings(this.configRegistry);
        this.localConfigCatalog = new LocalConfigCatalog(configManager);
        PotionPresetStore potionStore = configManager.getStore(PotionPresetStore.class);
        this.potionPresetController = new PotionPresetController(eventBus, this.keybindRegistry, potionStore);
        this.potionCombiner = new PotionCombiner(this.potionPresetController);
        espRenderCoordinator = new EspRenderCoordinator(eventBus, espFeatures);
        String cordName = System.getenv("byazen");
        if (cordName == null || cordName.isBlank()) {
            cordName = System.getProperty("byazen");
        }
        if (cordName == null || cordName.isBlank()) {
            cordName = "ByAzen";
        }
        this.clientProfile = new ClientProfile(ClientRole.USER, cordName.trim(), "offline", 0, EnumSet.of(ModuleState.DISABLED), null, new byte[0]);
        notificationCenter = new NotificationCenter(moduleManager.getModule(HUDModule.class));
        GuiPhotoBanner photoBanner = new GuiPhotoBanner(new TextureResource(new ClasspathResource("/assets/byazen/textures/gui/startup_card.jpg")), System::nanoTime, Easing.EASE_OUT_CUBIC, Easing.EASE_IN_CUBIC, ThemeColors::backgroundPrimary, ThemeColors::borderStrong);
        this.miscellaneous = new ClickGuiPanel(moduleManager, this.containerDisplaySettings, this.localConfigCatalog, photoBanner);
        eventBus.subscribe(KeyPressedEvent.class, this::handleMenuKey);
        configManager.initialize();
        this.localConfigCatalog.refresh();
        ClientEventBridge.register(eventBus);
    }

    private void handleMenuKey(KeyPressedEvent event) {
        if (event.key() != 344) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1755 instanceof ByAzenScreen) {
            client.method_1507(null);
        } else if (client.field_1755 == null && this.miscellaneous != null) {
            client.method_1507((class_437)new ByAzenScreen(this.miscellaneous));
        }
    }

    private void bootstrap() {
        this.configDirectory = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
        this.logger = LoggerFactory.getLogger((String)MOD_ID);
        instance = this;
        this.initializeServices();
        this.initializeCommands();
        this.registerWebPReader();
    }

    public static NotificationCenter getNotificationCenter() {
        return notificationCenter;
    }

    public static InventoryHudRegistry getInventoryHudRegistry() {
        return inventoryHudRegistry;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public static GuiDrawApi getGpuGuiRenderer() {
        if (gpuGuiRenderer == null) {
            ByAzenClient.initializeGpuGuiRenderer();
        }
        return gpuGuiRenderer;
    }

    private static void initializeLegacyGuiRenderer() {
        ResourceResolver shaderResources = new ResourceResolver("/assets/byazen/shaders/", ClasspathResource::new);
        legacyGuiRenderer = new GuiDrawApi(new GuiRenderBackend(new GlShaderProgram(shaderResources.resolve("gl/core_gl.frag"), shaderResources.resolve("gl/core_gl.vert"))));
    }

    public LocalConfigCatalog getLocalConfigCatalog() {
        return this.localConfigCatalog;
    }

    public PotionCombiner getPotionCombiner() {
        return this.potionCombiner;
    }

    public static PasswordStore getPasswordStore() {
        return passwordStore;
    }

    public ConfigRegistry getConfigRegistry() {
        return this.configRegistry;
    }

    public ClickGuiPanel getMiscellaneous() {
        return this.miscellaneous;
    }

    private static void initializeHudRenderer() {
        if (legacyGuiRenderer == null) {
            ByAzenClient.initializeLegacyGuiRenderer();
        }
        GuiRenderBackend guiRenderBackend = (GuiRenderBackend)legacyGuiRenderer.getRenderDriver();
        ByAzenGlobals byAzenGlobals = (ByAzenGlobals)ByAzenClient.getGpuGuiRenderer().getRenderDriver();
        hudRenderer = new GuiDrawApi(new GuiRenderPipeline(guiRenderBackend, byAzenGlobals));
    }

    public PotionPresetController getPotionPresetController() {
        return this.potionPresetController;
    }

    public static TickOverride getTickOverride() {
        return tickOverride;
    }

    public EventScheduleRegistry getEventSchedules() {
        return this.eventSchedules;
    }

    public static HudLayoutManager getHudLayoutManager() {
        return hudLayoutManager;
    }

    public ContainerDisplaySettings getContainerDisplaySettings() {
        return this.containerDisplaySettings;
    }

    private void registerWebPReader() {
        try {
            IIORegistry.getDefaultInstance().registerServiceProvider(new WebPImageReaderSpi());
        }
        catch (Throwable error) {
            this.logger.warn("WebP reader unavailable: {}", (Object)error.toString());
        }
    }

    private static void initializeGpuGuiRenderer() {
        gpuGuiRenderer = new GuiDrawApi(new ByAzenGlobals(ClientRenderPipelines.GUI_BATCH));
    }

    public static SlotHighlightRegistry getSlotHighlightRegistry() {
        return slotHighlightRegistry;
    }

    private void initializeCommands() {
        this.eventSchedules = new EventScheduleRegistry();
        CommandManager commandManager = new CommandManager();
        commandManager.initializeCommands();
        this.commandManager = commandManager;
    }

    public static EspFeatureRegistry getEspFeatureRegistry() {
        return espFeatures;
    }

    public static EspRenderCoordinator getEspRenderCoordinator() {
        return espRenderCoordinator;
    }

    public Path getConfigDirectory() {
        return this.configDirectory;
    }

    public static GuiDrawApi getGuiRenderer() {
        if (guiRendererOverride != null) {
            return guiRendererOverride;
        }
        return ByAzenClient.getHudRenderer();
    }

    public static void setGuiRendererOverride(GuiDrawApi renderer) {
        guiRendererOverride = renderer;
    }

    public KeybindRegistry getKeybindRegistry() {
        return this.keybindRegistry;
    }
}

