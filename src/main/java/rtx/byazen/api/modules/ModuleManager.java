package rtx.byazen.api.modules;
import rtx.byazen.api.events.EventHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.input.HotBarScrollEvent;
import rtx.byazen.api.events.impl.input.KeyPressEvent;
import rtx.byazen.api.events.impl.input.KeyPressEvent.Action;
import rtx.byazen.api.events.impl.input.MouseButtonEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.Module.BindMode;
import rtx.byazen.api.modules.impl.Interface.ArmorModule;
import rtx.byazen.api.modules.impl.Interface.ArrayListModule;
import rtx.byazen.api.modules.impl.Interface.ClickGui;
import rtx.byazen.api.modules.impl.Interface.ClickGuiBackdropsModule;
import rtx.byazen.api.modules.impl.Interface.ThemeStudioModule;
import rtx.byazen.api.modules.impl.Interface.CooldownsModule;
import rtx.byazen.api.modules.impl.Interface.CustomHotbar;
import rtx.byazen.api.modules.impl.Interface.InventoryPlus;
import rtx.byazen.api.modules.impl.Interface.HudProfilesModule;
import rtx.byazen.api.modules.impl.Interface.HudStyleModule;
import rtx.byazen.api.modules.impl.Interface.PerfGraphModule;
import rtx.byazen.api.modules.impl.Interface.HPFocus;
import rtx.byazen.api.modules.impl.Interface.HotKeysModule;
import rtx.byazen.api.modules.impl.Interface.InfoModule;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.impl.Interface.InventoryModule;
import rtx.byazen.api.modules.impl.Interface.ClockModule;
import rtx.byazen.api.modules.impl.Interface.CustomTextModule;
import rtx.byazen.api.modules.impl.Interface.HudPolishModule;
import rtx.byazen.api.modules.impl.Interface.KeyStrokesModule;
import rtx.byazen.api.modules.impl.Interface.MusicPlayerModule;
import rtx.byazen.api.modules.impl.Interface.TimerAlarms;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.impl.Interface.PotionsModule;
import rtx.byazen.api.modules.impl.Interface.TargetHudModule;
import rtx.byazen.api.modules.impl.Interface.FontsModule;
import rtx.byazen.api.modules.impl.Interface.MinimapModule;
import rtx.byazen.api.modules.impl.Interface.WaypointsModule;
import rtx.byazen.api.modules.impl.Interface.LanguageModule;
import rtx.byazen.api.modules.impl.Interface.NextEventModule;
import rtx.byazen.api.modules.impl.Interface.ThemeLookModule;
import rtx.byazen.api.modules.impl.Interface.ScreenshotsModule;
import rtx.byazen.api.modules.impl.Interface.HudGroupsModule;
import rtx.byazen.api.modules.impl.Interface.UiScaleModule;
import rtx.byazen.api.modules.impl.Interface.WatermarkModule;
import rtx.byazen.api.modules.impl.Utils.GraphicsPresets;
import rtx.byazen.api.modules.impl.Utils.ServerBinds;
import rtx.byazen.api.modules.impl.Utils.ServerCheck;
import rtx.byazen.api.modules.impl.Utils.ModsManager;
import rtx.byazen.api.modules.impl.Utils.ConfigBackupModule;
import rtx.byazen.api.modules.impl.Utils.CloudConfigsModule;
import rtx.byazen.api.modules.impl.Utils.CacheLimitsModule;
import rtx.byazen.api.modules.impl.Utils.ConfigJournalModule;
import rtx.byazen.api.modules.impl.Utils.ConfigMigrationModule;
import rtx.byazen.api.modules.impl.Utils.FastStartModule;
import rtx.byazen.api.modules.impl.Utils.ModDetectModule;
import rtx.byazen.api.modules.impl.Utils.PrivateDataModule;
import rtx.byazen.api.modules.impl.Utils.RenderProfilerModule;
import rtx.byazen.api.modules.impl.Utils.StatsExportModule;
import rtx.byazen.api.modules.impl.Utils.ConfigDoctorModule;
import rtx.byazen.api.modules.impl.Utils.ConfigImportModule;
import rtx.byazen.api.modules.impl.Utils.ServerProfilesModule;
import rtx.byazen.api.modules.impl.Utils.AutoReconnect;
import rtx.byazen.api.modules.impl.Utils.HandbookModule;
import rtx.byazen.api.modules.impl.Utils.ResourcePackReview;
import rtx.byazen.api.modules.impl.Utils.ConfigTransferModule;
import rtx.byazen.api.modules.impl.Utils.AutoCommands;
import rtx.byazen.api.modules.impl.Utils.AutoDuel;
import rtx.byazen.api.modules.impl.Utils.AutoResell;
import rtx.byazen.api.modules.impl.Utils.AutoRespawn;
import rtx.byazen.api.modules.impl.Utils.DeathHistoryModule;
import rtx.byazen.api.modules.impl.Utils.AutoSprint;
import rtx.byazen.api.modules.impl.Utils.AutoSwap;
import rtx.byazen.api.modules.impl.Utils.AutoTpAccept;
import rtx.byazen.api.modules.impl.Utils.CameraSettings;
import rtx.byazen.api.modules.impl.Utils.ClickPearl;
import rtx.byazen.api.modules.impl.Utils.ClientSounds;
import rtx.byazen.api.modules.impl.Utils.CrystalOptimizer;
import rtx.byazen.api.modules.impl.Utils.ClipboardModule;
import rtx.byazen.api.modules.impl.Utils.LogsModule;
import rtx.byazen.api.modules.impl.Utils.DeathCoords;
import rtx.byazen.api.modules.impl.Utils.ElytraSwap;
import rtx.byazen.api.modules.impl.Utils.FastExp;
import rtx.byazen.api.modules.impl.Utils.Freelook;
import rtx.byazen.api.modules.impl.Utils.Globals;
import rtx.byazen.api.modules.impl.Utils.HandSwap;
import rtx.byazen.api.modules.impl.Utils.HitSound;
import rtx.byazen.api.modules.impl.Utils.HolyWorldHelper;
import rtx.byazen.api.modules.impl.Utils.Irc;
import rtx.byazen.api.modules.impl.Utils.ItemScroller;
import rtx.byazen.api.modules.impl.Utils.NotesModule;
import rtx.byazen.api.modules.impl.Utils.QuickChatModule;
import rtx.byazen.api.modules.impl.Utils.QuickHideModule;
import rtx.byazen.api.modules.impl.Utils.RemindersModule;
import rtx.byazen.api.modules.impl.Utils.Optimization;
import rtx.byazen.api.modules.impl.Utils.Party;
import rtx.byazen.api.modules.impl.Utils.Profiler;
import rtx.byazen.api.modules.impl.Utils.ShulkerPreview;
import rtx.byazen.api.modules.impl.Utils.SessionStatsModule;
import rtx.byazen.api.modules.impl.Utils.StreamerMode;
import rtx.byazen.api.modules.impl.Utils.TalTracker;
import rtx.byazen.api.modules.impl.Utils.TapeMouse;
import rtx.byazen.api.modules.impl.Utils.TestSettings;
import rtx.byazen.api.modules.impl.Visuals.Ambience;
import rtx.byazen.api.modules.impl.Visuals.AspectRatio;
import rtx.byazen.api.modules.impl.Visuals.AnimatedCapes;
import rtx.byazen.api.modules.impl.Visuals.BetterMinecraft;
import rtx.byazen.api.modules.impl.Visuals.BlockOverlay;
import rtx.byazen.api.modules.impl.Visuals.ChinaHat;
import rtx.byazen.api.modules.impl.Visuals.Crosshair;
import rtx.byazen.api.modules.impl.Visuals.CustomPet;
import rtx.byazen.api.modules.impl.Visuals.CustomSwords;
import rtx.byazen.api.modules.impl.Visuals.Customization;
import rtx.byazen.api.modules.impl.Visuals.Cosmetics3D;
import rtx.byazen.api.modules.impl.Visuals.Emotions;
import rtx.byazen.api.modules.impl.Visuals.FakePlayer;
import rtx.byazen.api.modules.impl.Visuals.Footprints;
import rtx.byazen.api.modules.impl.Visuals.FogBlur;
import rtx.byazen.api.modules.impl.Visuals.GlowEsp;
import rtx.byazen.api.modules.impl.Visuals.HitBubbles;
import rtx.byazen.api.modules.impl.Visuals.HitColor;
import rtx.byazen.api.modules.impl.Visuals.HitParticles;
import rtx.byazen.api.modules.impl.Visuals.Hitboxes;
import rtx.byazen.api.modules.impl.Visuals.HpCounter;
import rtx.byazen.api.modules.impl.Visuals.ItemHighlight;
import rtx.byazen.api.modules.impl.Visuals.ItemPhysics;
import rtx.byazen.api.modules.impl.Visuals.JumpCircle;
import rtx.byazen.api.modules.impl.Visuals.KillEffect;
import rtx.byazen.api.modules.impl.Visuals.KillEffect3D;
import rtx.byazen.api.modules.impl.Visuals.NameTags;
import rtx.byazen.api.modules.impl.Visuals.NoRender;
import rtx.byazen.api.modules.impl.Visuals.ProfileCoversModule;
import rtx.byazen.api.modules.impl.Visuals.Predictions;
import rtx.byazen.api.modules.impl.Visuals.ProjectileHelper;
import rtx.byazen.api.modules.impl.Visuals.PulseCosmetics;
import rtx.byazen.api.modules.impl.Visuals.SelfTag;
import rtx.byazen.api.modules.impl.Visuals.ShaderHands;
import rtx.byazen.api.modules.impl.Visuals.SwingAnimation;
import rtx.byazen.api.modules.impl.Visuals.TargetESP;
import rtx.byazen.api.modules.impl.Visuals.BlockShards;
import rtx.byazen.api.modules.impl.Visuals.AntiAliasing;
import rtx.byazen.api.modules.impl.Visuals.CameraPlus;
import rtx.byazen.api.modules.impl.Visuals.KillFX;
import rtx.byazen.api.modules.impl.Visuals.Trajectories;
import rtx.byazen.api.modules.impl.Visuals.Bloom;
import rtx.byazen.api.modules.impl.Visuals.CustomParticles;
import rtx.byazen.api.modules.impl.Visuals.TrailsPlus;
import rtx.byazen.api.modules.impl.Visuals.BlockHighlight;
import rtx.byazen.api.modules.impl.Visuals.Graffiti;
import rtx.byazen.api.modules.impl.Visuals.PetStyle;
import rtx.byazen.api.modules.impl.Visuals.PetPlus;
import rtx.byazen.api.modules.impl.Visuals.Wings;
import rtx.byazen.api.modules.impl.Visuals.Cinematic;
import rtx.byazen.api.modules.impl.Visuals.DeathEffects;
import rtx.byazen.api.modules.impl.Visuals.DamageEffects;
import rtx.byazen.api.modules.impl.Visuals.NightVision;
import rtx.byazen.api.modules.impl.Visuals.Shadows;
import rtx.byazen.api.modules.impl.Visuals.Trails;
import rtx.byazen.api.modules.impl.Visuals.ViewModel;
import rtx.byazen.api.modules.impl.Visuals.WorldParticles;
import rtx.byazen.api.modules.restrict.Server;
import rtx.byazen.api.modules.restrict.ServerRestrictions;
import rtx.byazen.utils.key.KeyBind;

public final class ModuleManager {
    private static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new ArrayList<Module>();
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private ModuleManager() {
    }

    public <T extends Module> T get(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T)module;
        }
        return null;
    }

    public static ModuleManager get() {
        return INSTANCE;
    }

    private void register(Module ... moduleArray) {
        for (Module module : moduleArray) {
            this.modules.add(module);
        }
    }

    public void init() {
        this.register(new HitSound(), new AutoSprint(), new Ambience(), new BetterMinecraft(), new AspectRatio(), new ClickGui(), new CustomHotbar(), new CustomPet(), new Customization(), new FogBlur(), new Footprints(), new HitBubbles(), new HpCounter(), new HitColor(), new Hitboxes(), new SelfTag(), new InterfaceModule(), new NotificationsModule(), new WatermarkModule(), new HotKeysModule(), new Profiler(), new TargetHudModule(), new PotionsModule(), new CooldownsModule(), new InfoModule(), new ArrayListModule(), new ArmorModule(), new InventoryModule(), new InventoryPlus(), new HPFocus(), new KeyStrokesModule(), new MusicPlayerModule(), new TimerAlarms(), new ScreenshotsModule(), new ClockModule(), new CustomTextModule(), new HudPolishModule(), new HudStyleModule(), new HudGroupsModule(), new UiScaleModule(), new NextEventModule(), new ThemeLookModule(), new LanguageModule(), new FontsModule(), new MinimapModule(), new WaypointsModule(), new HudProfilesModule(), new PerfGraphModule(), new ViewModel(), new Freelook(), new HandSwap(), new CameraSettings(), new ItemPhysics(), new CustomSwords(), new AnimatedCapes(), new JumpCircle(), new Crosshair(), new KillEffect(), new KillEffect3D(), new PulseCosmetics(), new NameTags(), new GlowEsp(), new ChinaHat(), new Emotions(), new BlockOverlay(), new FakePlayer(), new ShaderHands(), new ItemHighlight(), new NoRender(), new WorldParticles(), new HitParticles(), new Predictions(), new ProjectileHelper(), new SwingAnimation(), new TargetESP(), new Trails(), new NightVision(), new DamageEffects(), new Cinematic(), new Shadows(), new DeathEffects(), new BlockShards(), new Bloom(), new CustomParticles(), new TrailsPlus(), new BlockHighlight(), new AntiAliasing(), new CameraPlus(), new KillFX(), new Trajectories(), new Wings(), new Graffiti(), new Cosmetics3D(), new PetPlus(), new PetStyle(), new ProfileCoversModule(), new ShulkerPreview(), new AutoDuel(), new AutoResell(), new AutoTpAccept(), new ClientSounds(), new DeathCoords(), new AutoRespawn(), new SessionStatsModule(), new DeathHistoryModule(), new QuickHideModule(), new RemindersModule(), new NotesModule(), new QuickChatModule(), new ClipboardModule(), new LogsModule(), new ClickPearl(), new HolyWorldHelper(), new FastExp(), new TalTracker(), new ItemScroller(), new StreamerMode(), new ElytraSwap(), new AutoSwap(), new TestSettings(), new AutoCommands(), new TapeMouse(), new CrystalOptimizer(), new Optimization(), new GraphicsPresets(), new ServerBinds(), new ServerCheck(), new ModsManager(), new ConfigBackupModule(), new AutoReconnect(), new HandbookModule(), new ResourcePackReview(), new ConfigTransferModule(), new Irc(), new Party(), new Globals(), new CloudConfigsModule(), new ServerProfilesModule(), new ConfigImportModule(), new ConfigDoctorModule(), new ClickGuiBackdropsModule(), new ThemeStudioModule(), new ConfigJournalModule(), new ConfigMigrationModule(), new StatsExportModule(), new PrivateDataModule(), new RenderProfilerModule(), new CacheLimitsModule(), new FastStartModule(), new ModDetectModule());
        EventBus.get().subscribe(this);
    }

    public List<Module> getAll() {
        return Collections.unmodifiableList(this.modules);
    }

    @EventHandler
    public void onKey(KeyPressEvent keyPressEvent) {
        if (this.shouldIgnoreBinds()) {
            return;
        }
        if (keyPressEvent.action == KeyPressEvent.Action.PRESS) {
            this.applyKeyBound(keyPressEvent.keyCode, true);
        } else if (keyPressEvent.action == KeyPressEvent.Action.RELEASE) {
            this.applyKeyBound(keyPressEvent.keyCode, false);
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        EnumSet<Server> enumSet = ServerRestrictions.current();
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            if (ServerRestrictions.isHiddenBy(module, enumSet)) {
                module.disable();
                continue;
            }
            String string = ServerRestrictions.blockReason(module, enumSet);
            if (string == null) continue;
            module.disable();
            ServerRestrictions.notify(string);
        }
    }

    private boolean applyBind(Module module, boolean bl) {
        if (module.getBindMode() == Module.BindMode.HOLD) {
            module.setEnabled(bl);
            return true;
        }
        if (bl) {
            module.toggle();
            return true;
        }
        return false;
    }

    @EventHandler
    public void onMouse(MouseButtonEvent mouseButtonEvent) {
        if (this.shouldIgnoreBinds()) {
            return;
        }
        if (mouseButtonEvent.action == MouseButtonEvent.Action.PRESS) {
            this.applyMouseBound(mouseButtonEvent.button, true);
        } else if (mouseButtonEvent.action == MouseButtonEvent.Action.RELEASE) {
            this.applyMouseBound(mouseButtonEvent.button, false);
        }
    }

    public List<Module> getEnabled() {
        return this.modules.stream().filter(Module::isEnabled).toList();
    }

    @EventHandler
    public void onScroll(HotBarScrollEvent hotBarScrollEvent) {
        if (this.shouldIgnoreBinds()) {
            return;
        }
        int n = hotBarScrollEvent.getVertical() > 0.0 ? 1000 : (hotBarScrollEvent.getVertical() < 0.0 ? 1001 : -1);
        if (n != -1 && this.toggleBound(n)) {
            hotBarScrollEvent.cancel();
        }
    }

    public List<Module> getByCategory(Category category) {
        return this.modules.stream().filter(module -> module.getCategory() == category).toList();
    }

    private boolean toggleBound(int n) {
        boolean bl = false;
        for (Module module : this.modules) {
            KeyBind keyBind;
            if (module instanceof ClickGui || !(keyBind = module.getBind()).isBound() || keyBind.getCode() != n) continue;
            module.toggle();
            bl = true;
        }
        return bl;
    }

    private boolean applyMouseBound(int n, boolean bl) {
        boolean bl2 = false;
        for (Module module : this.modules) {
            KeyBind keyBind = module.getBind();
            if (!keyBind.isBound() || !keyBind.matchesMouseButton(n)) continue;
            bl2 |= this.applyBind(module, bl);
        }
        return bl2;
    }

    private boolean applyKeyBound(int n, boolean bl) {
        boolean bl2 = false;
        for (Module module : this.modules) {
            KeyBind keyBind;
            if (module instanceof ClickGui || !(keyBind = module.getBind()).isBound() || keyBind.getCode() != n) continue;
            bl2 |= this.applyBind(module, bl);
        }
        return bl2;
    }

    private boolean shouldIgnoreBinds() {
        return this.mc == null || this.mc.currentScreen != null;
    }

    public Module findByName(String string) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(string)) continue;
            return module;
        }
        return null;
    }
}

