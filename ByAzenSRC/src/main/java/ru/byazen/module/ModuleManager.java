/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module;

import java.util.ArrayList;
import java.util.List;
import ru.byazen.event.EventBus;
import ru.byazen.misc.ConfigRegistry;
import ru.byazen.misc.KeybindRegistry;
import ru.byazen.module.Module;
import ru.byazen.module.combat.AimAssistModule;
import ru.byazen.module.combat.AttackAuraModule;
import ru.byazen.module.combat.AutoArmorModule;
import ru.byazen.module.combat.AutoGAppleModule;
import ru.byazen.module.combat.AutoPotionModule;
import ru.byazen.module.combat.AutoSwapModule;
import ru.byazen.module.combat.AutoTotemModule;
import ru.byazen.module.combat.ClickPearlModule;
import ru.byazen.module.combat.ElytraHelperModule;
import ru.byazen.module.combat.HitBoxesModule;
import ru.byazen.module.combat.ItemHelperModule;
import ru.byazen.module.combat.MaceHelperModule;
import ru.byazen.module.combat.NoEntityTraceModule;
import ru.byazen.module.combat.TapeMouseModule;
import ru.byazen.module.combat.TridentClickerModule;
import ru.byazen.module.combat.TriggerBotModule;
import ru.byazen.module.combat.VelocityModule;
import ru.byazen.module.combat.WeaponSpamModule;
import ru.byazen.module.hud.AnimateModule;
import ru.byazen.module.hud.ArrowsModule;
import ru.byazen.module.hud.AuctionHelperModule;
import ru.byazen.module.hud.BetterChatModule;
import ru.byazen.module.hud.CrosshairModule;
import ru.byazen.module.hud.ExtraTabModule;
import ru.byazen.module.hud.HUDModule;
import ru.byazen.module.hud.HealthHelperModule;
import ru.byazen.module.hud.HotbarModule;
import ru.byazen.module.hud.PositionModule;
import ru.byazen.module.misc.AlternativeMenuKeyModule;
import ru.byazen.module.misc.AutoConfigSaveModule;
import ru.byazen.module.misc.CommandCharacterModule;
import ru.byazen.module.misc.CustomSoundsModule;
import ru.byazen.module.misc.FriendsModule;
import ru.byazen.module.misc.HologramOptimizerModule;
import ru.byazen.module.misc.PotionCombinerModule;
import ru.byazen.module.misc.SRPSpooferModule;
import ru.byazen.module.misc.ServerJoinerModule;
import ru.byazen.module.misc.SoundRemoverModule;
import ru.byazen.module.movement.AirStuckModule;
import ru.byazen.module.movement.AutoJumpModule;
import ru.byazen.module.movement.AutoSprintModule;
import ru.byazen.module.movement.EagleModule;
import ru.byazen.module.movement.EdgeJumpModule;
import ru.byazen.module.movement.FlightModule;
import ru.byazen.module.movement.FreeCameraModule;
import ru.byazen.module.movement.GuiMoveModule;
import ru.byazen.module.movement.NoJumpDelayModule;
import ru.byazen.module.movement.NoSlowDownModule;
import ru.byazen.module.movement.SpeedModule;
import ru.byazen.module.movement.TimerModule;
import ru.byazen.module.player.AntiAFKModule;
import ru.byazen.module.player.AutoAuthModule;
import ru.byazen.module.player.AutoEatModule;
import ru.byazen.module.player.AutoEventModule;
import ru.byazen.module.player.AutoFishModule;
import ru.byazen.module.player.AutoLeaveModule;
import ru.byazen.module.player.AutoRespawnModule;
import ru.byazen.module.player.AutoSpawnModule;
import ru.byazen.module.player.AutoTPAcceptModule;
import ru.byazen.module.player.AutoToolModule;
import ru.byazen.module.player.ChestStealerModule;
import ru.byazen.module.player.FakePlayerModule;
import ru.byazen.module.player.FastBreakModule;
import ru.byazen.module.player.FastExperienceModule;
import ru.byazen.module.player.FastPlaceModule;
import ru.byazen.module.player.ItemScrollerModule;
import ru.byazen.module.player.MineHelperModule;
import ru.byazen.module.player.NameProtectModule;
import ru.byazen.module.player.NoInteractModule;
import ru.byazen.module.player.NoPushModule;
import ru.byazen.module.player.NukerModule;
import ru.byazen.module.player.OpenWallsModule;
import ru.byazen.module.player.ServerHelperModule;
import ru.byazen.module.player.UseTrackerModule;
import ru.byazen.module.render.AmbientModule;
import ru.byazen.module.render.AspectRatioModule;
import ru.byazen.module.render.BlockESPModule;
import ru.byazen.module.render.BlockOverlayModule;
import ru.byazen.module.render.BrightnessModule;
import ru.byazen.module.render.CameraModule;
import ru.byazen.module.render.ColorCorrectionModule;
import ru.byazen.module.render.ConsumablesESPModule;
import ru.byazen.module.render.CriticalHitEffectModule;
import ru.byazen.module.render.DurationVisualiserModule;
import ru.byazen.module.render.ElytraTrailsModule;
import ru.byazen.module.render.FreeLookModule;
import ru.byazen.module.render.HandAuraModule;
import ru.byazen.module.render.HandModifyModule;
import ru.byazen.module.render.HitEffectModule;
import ru.byazen.module.render.HitParticlesModule;
import ru.byazen.module.render.ItemPhysicModule;
import ru.byazen.module.render.NoRenderModule;
import ru.byazen.module.render.SeeInvisiblesModule;
import ru.byazen.module.render.ShulkerViewerModule;
import ru.byazen.module.render.StructuresModule;
import ru.byazen.module.render.TargetESPModule;
import ru.byazen.module.render.TotemEffectsModule;
import ru.byazen.module.render.TracersModule;
import ru.byazen.module.render.TrailsModule;
import ru.byazen.module.render.WaypointsModule;
import ru.byazen.module.render.WorldParticlesModule;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.Setting;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<Module>();

    public List<Module> getModules() {
        return this.modules;
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T)((Module)clazz.cast(module));
        }
        return null;
    }

    public void registerConfigEntries(ConfigRegistry configRegistry) {
        this.modules.forEach(configRegistry::register);
    }

    public void registerDefaults(EventBus eventBus) {
        this.modules.add(new AttackAuraModule(eventBus));
        this.modules.add(new AimAssistModule(eventBus));
        this.modules.add(new TriggerBotModule(eventBus));
        this.modules.add(new TapeMouseModule(eventBus));
        this.modules.add(new TridentClickerModule(eventBus));
        this.modules.add(new NoEntityTraceModule(eventBus));
        this.modules.add(new HitBoxesModule(eventBus));
        this.modules.add(new AutoArmorModule(eventBus));
        this.modules.add(new ItemHelperModule(eventBus));
        this.modules.add(new AutoSwapModule(eventBus));
        this.modules.add(new AutoTotemModule(eventBus));
        this.modules.add(new MaceHelperModule(eventBus));
        this.modules.add(new AutoPotionModule(eventBus));
        this.modules.add(new AutoGAppleModule(eventBus));
        this.modules.add(new ElytraHelperModule(eventBus));
        this.modules.add(new ClickPearlModule(eventBus));
        this.modules.add(new VelocityModule(eventBus));
        this.modules.add(new WeaponSpamModule(eventBus));
        this.modules.add(new AutoSprintModule(eventBus));
        this.modules.add(new AutoJumpModule(eventBus));
        this.modules.add(new NoJumpDelayModule(eventBus));
        this.modules.add(new GuiMoveModule(eventBus));
        this.modules.add(new EagleModule(eventBus));
        this.modules.add(new TimerModule(eventBus));
        this.modules.add(new FreeCameraModule(eventBus));
        this.modules.add(new EdgeJumpModule(eventBus));
        this.modules.add(new AirStuckModule(eventBus));
        this.modules.add(new NoSlowDownModule(eventBus));
        this.modules.add(new SpeedModule(eventBus));
        this.modules.add(new FlightModule(eventBus));
        this.modules.add(new BrightnessModule(eventBus));
        this.modules.add(new AmbientModule(eventBus));
        this.modules.add(new HitParticlesModule(eventBus));
        this.modules.add(new BlockESPModule(eventBus));
        this.modules.add(new TracersModule(eventBus));
        this.modules.add(new HitEffectModule(eventBus));
        this.modules.add(new CriticalHitEffectModule(eventBus));
        this.modules.add(new TotemEffectsModule(eventBus));
        this.modules.add(new TargetESPModule(eventBus));
        this.modules.add(new ColorCorrectionModule(eventBus));
        this.modules.add(new ItemPhysicModule(eventBus));
        this.modules.add(new WorldParticlesModule(eventBus));
        this.modules.add(new ElytraTrailsModule(eventBus));
        this.modules.add(new HandModifyModule(eventBus));
        this.modules.add(new SeeInvisiblesModule(eventBus));
        this.modules.add(new AspectRatioModule(eventBus));
        this.modules.add(new TrailsModule(eventBus));
        this.modules.add(new NoRenderModule(eventBus));
        this.modules.add(new WaypointsModule(eventBus));
        this.modules.add(new DurationVisualiserModule(eventBus));
        this.modules.add(new StructuresModule(eventBus));
        this.modules.add(new ConsumablesESPModule(eventBus));
        this.modules.add(new ShulkerViewerModule(eventBus));
        this.modules.add(new FreeLookModule(eventBus));
        this.modules.add(new CameraModule(eventBus));
        this.modules.add(new BlockOverlayModule(eventBus));
        this.modules.add(new HandAuraModule(eventBus));
        this.modules.add(new HologramOptimizerModule(eventBus));
        this.modules.add(new FakePlayerModule(eventBus));
        this.modules.add(new AutoEventModule(eventBus));
        this.modules.add(new FriendsModule(eventBus));
        this.modules.add(new FastBreakModule(eventBus));
        this.modules.add(new FastPlaceModule(eventBus));
        this.modules.add(new NoPushModule(eventBus));
        this.modules.add(new NoInteractModule(eventBus));
        this.modules.add(new UseTrackerModule(eventBus));
        this.modules.add(new ItemScrollerModule(eventBus));
        this.modules.add(new AutoRespawnModule(eventBus));
        this.modules.add(new AutoTPAcceptModule(eventBus));
        this.modules.add(new AutoAuthModule(eventBus));
        this.modules.add(new AutoSpawnModule(eventBus));
        this.modules.add(new AutoLeaveModule(eventBus));
        this.modules.add(new OpenWallsModule(eventBus));
        this.modules.add(new FastExperienceModule(eventBus));
        this.modules.add(new ServerJoinerModule(eventBus));
        this.modules.add(new AntiAFKModule(eventBus));
        this.modules.add(new NameProtectModule(eventBus));
        this.modules.add(new MineHelperModule(eventBus));
        this.modules.add(new NukerModule(eventBus));
        this.modules.add(new SRPSpooferModule(eventBus));
        this.modules.add(new AutoEatModule(eventBus));
        this.modules.add(new AutoFishModule(eventBus));
        this.modules.add(new ChestStealerModule(eventBus));
        this.modules.add(new CrosshairModule(eventBus));
        this.modules.add(new HotbarModule(eventBus));
        this.modules.add(new PositionModule(eventBus));
        this.modules.add(new ArrowsModule(eventBus));
        this.modules.add(new HUDModule(eventBus));
        this.modules.add(new AnimateModule(eventBus));
        this.modules.add(new BetterChatModule(eventBus));
        this.modules.add(new ExtraTabModule(eventBus));
        this.modules.add(new AuctionHelperModule(eventBus));
        this.modules.add(new ServerHelperModule(eventBus));
        this.modules.add(new PotionCombinerModule(eventBus));
        this.modules.add(new SoundRemoverModule(eventBus));
        this.modules.add(new CustomSoundsModule(eventBus));
        this.modules.add(new CommandCharacterModule(eventBus));
        this.modules.add(new AlternativeMenuKeyModule(eventBus));
        this.modules.add(new AutoConfigSaveModule(eventBus));
        this.modules.add(new AutoToolModule(eventBus));
        this.modules.add(new HealthHelperModule(eventBus));
    }

    public void registerKeybindSettings(KeybindRegistry keybindRegistry) {
        for (Module module : this.modules) {
            for (Setting setting : module.getSettings()) {
                if (setting instanceof BindSetting) {
                    BindSetting bindSetting = (BindSetting)setting;
                    keybindRegistry.register(bindSetting.getKeybindBinding());
                    continue;
                }
                if (!setting.hasKeybind()) continue;
                keybindRegistry.register(setting.getKeybind());
            }
        }
    }
}

