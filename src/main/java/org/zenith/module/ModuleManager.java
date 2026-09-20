package org.zenith.module;

import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import org.zenith.ZenithClient;
import org.zenith.core.ClientProvider;
import org.zenith.event.EventTriggerKeyEvent;

public final class ModuleManager implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final List<Module> list103 = new ArrayList<>();

   public void ScoreboardHelper() {
      this.on23(Interface.interfaceField);
      this.on23(BetterMinecraft.betterMinecraft);
      this.on23(AntiInvisible.antiInvisible);
      this.on23(Arrows.arrows);
      this.on23(Menu.menu);
      this.on23(NoRender.noRender);
      this.on23(Predictions.predictions);
      this.on23(BlockESP.blockESP);
      this.on23(BlockOverLay.blockOverLay);
      this.on23(SwingAnimation.swingAnimation);
      this.on23(Crosshair.crosshair);
      this.on23(ViewModel.viewModel);
      this.on23(WorldTweaks.worldTweaks);
      this.on23(ShaderFog.shaderFog);
      this.on23(Chams.chams);
      this.on23(EntityESP.entityESP);
      this.on23(TargetESP.targetESP);
      this.on23(ShaderHand.shaderHand);
      this.on23(HandFire.handFire);
      this.on23(AutoExplosion.autoExplosion);
      this.on23(Cape.cape);
      this.on23(JumpCircle.jumpCircle);
      this.on23(WorldParticles.worldParticles);
      this.on23(HitParticles.hitParticles);
      this.on23(TotemParticles.totemParticles);
      this.on23(Trails.trails);
      this.on23(KillEffect.killEffect);
      this.on23(Particles.particles);
      this.on23(ViewArmorDurability.viewArmorDurability);
      this.on23(FireWorkESP.fireWorkESP);
      this.on23(TotemPop.totemPop);
   }

   public void StyledTextBuilder() {
      this.on23(Emotes.emotes);
      this.on23(BowAimBot.bowAimBot);
      this.on23(TridentAimbot.tridentAimbot);
      this.on23(ItemDebug.itemDebug);
      if (ZenithClient.on23().CommandManager().getUsername().equals("Bogdan")) {
         this.on23(FakePlayer.fakePlayer);
         this.on23(PathTeleport.pathTeleport);
      }

      this.on23(ShaderESP.shaderESP);
      this.on23(ShulkerPreview.shulkerPreview);
      this.on23(ServerHelper.serverHelper);
      this.on23(ElytraHelper.elytraHelper);
      this.on23(ItemScroller.itemScroller);
      this.on23(AutoTrap.autoTrap);
      this.on23(ClickAction.clickAction);
      this.on23(FreeCam.freeCam);
      this.on23(PvpSafe.pvpSafe);
      this.on23(CameraTweaks.cameraTweaks);
      this.on23(AutoAuth.autoAuth);
      this.on23(AutoCapcha.autoCapcha);
      this.on23(AutoPay.autoPay);
      this.on23(AutoDuels.autoDuels);
      this.on23(AutoLeave.autoLeave);
      this.on23(AHHelper.aHHelper);
      this.on23(AutoInventory.autoInventory);
      this.on23(AutoCraft.autoCraft);
      this.on23(ContainerHelper.containerHelper);
      this.on23(NoInteract.noInteract);
      this.on23(NoFriendDamage.noFriendDamage);
      this.on23(ChestStealer.chestStealer);
      this.on23(AutoAccept.autoAccept);
      this.on23(EventTracker.eventTracker);
      this.on23(AutoUse.autoUse);
      this.on23(TapeMouse.tapeMouse);
      this.on23(AutoRespawn.autoRespawn);
      this.on23(NameProtect.nameProtect);
      this.on23(AutoWeb.autoWeb);
      this.on23(XrayBypass.xrayBypass);
      this.on23(StreamerMode.streamerMode);
   }

   public ModuleManager() {
      this.init();
      EventManager.register(this);
   }

   public void init() {
      this.BlockFinder();
      this.I1Type();
      this.ScoreboardHelper();
      this.HotbarSwapper();
      this.StyledTextBuilder();
   }

   public void BlockFinder() {
      if (ZenithClient.on23().CommandManager().getUsername().equals("Bogdan")) {
         this.on23(RotationRecorder.rotationRecorder);
      }

      this.on23(AimAssist.aimAssist);
      this.on23(Aura.aura);
      this.on23(Reach.reach2);
      this.on23(AutoSwap.autoSwap);
      this.on23(TriggerBot.triggerBot);
      this.on23(OffHandManager.offHandManager);
      this.on23(ElytraTarget.elytraTarget);
      this.on23(Criticals.criticals);
      this.on23(TargetPearl.targetPearl);
      this.on23(AutoTotem.autoTotem);
      this.on23(AntiBot.antiBot);
      this.on23(InventorySetting.inventorySetting);
      this.on23(Blink.blink);
      this.on23(TrapTp.trapTp);
      this.on23(FakeLag.fakeLag);
      this.on23(Backtrack.reachV3);
   }

   public void I1Type() {
      this.on23(AutoSprint.autoSprint);
      this.on23(ElytraBooster.elytraBooster);
      this.on23(CastleFly.castleFly);
      this.on23(GrimGlide.grimGlide);
      this.on23(SlimeFlight.slimeFlight);
      this.on23(ElytraFly.elytraFly);
      this.on23(Velocity.velocity);
      this.on23(NoWeb.noWeb);
      this.on23(GuiWalk.guiWalk);
      this.on23(NoSlow.noSlow);
      this.on23(Spider.spider);
      this.on23(Speed.speed11);
      this.on23(Timer.timer);
      this.on23(ElytraMotion.elytraMotion);
      this.on23(BoatHighJump.boatHighJump);
      this.on23(BoatLongJump.boatLongJump);
      this.on23(ShulkerJump.shulkerJump);
      this.on23(Strafe.strafe);
      this.on23(AirStuck.airStuck);
      this.on23(WallBypass.wallBypass);
   }

   public void HotbarSwapper() {
      this.on23(AutoTool.autoTool);
      this.on23(NoDelay.noDelay);
      this.on23(NoSweetSlow.noSweetSlow);
      this.on23(NoPush.noPush);
      this.on23(OpenWals.openWals);
      this.on23(BaseFinder.baseFinder);
      this.on23(AppleFarm.appleFarm);
      this.on23(FastBreak.fastBreak);
      this.on23(AutoLoot.autoLoot);
      this.on23(AutoMine.autoMine);
      this.on23(CropFarmer.cropFarmer);
      this.on23(AutoZamok.autoZamok);
      this.on23(AutoBrewing.autoBrewing);
      this.on23(Bot.bot);
      this.on23(AutoWarden.autoWarden);
      this.on23(WarpFarm.warpFarm);
   }

   public void on23(Module var1) {
      this.UiAnimation(var1);
   }

   public void UiAnimation(Module var1) {
      Objects.requireNonNull(var1, "module");
      if (this.list103.stream().anyMatch(var1xx -> var1xx.getId().equals(var1.getId()))) {
         throw new IllegalArgumentException("Duplicate module id: " + var1.getId());
      }

      this.list103.add(var1);
   }

   public Module HotbarInputEvent(String var1) {
      return this.list103.stream().filter(var1xx -> var1xx.getName().equalsIgnoreCase(var1)).findFirst().orElse(null);
   }

   public Set<Module> MenuScreenId() {
      Set<Module> linkedhashset = new LinkedHashSet<>();

      for (Module lii1lll1l1li1ii1iiillii : this.list103) {
         if (lii1lll1l1li1ii1iiillii.isEnabled()) {
            linkedhashset.add(lii1lll1l1li1ii1iiillii);
         }
      }

      return linkedhashset;
   }

   @EventTarget
   public void on23(EventTriggerKeyEvent var1) {
      if (var1.TridentAimbot() == 1) {
         if (Menu.menu.getKeyCode() == var1.getKeyCode() && Menu.menu.getKeyCode() != -1) {
            Menu.menu.toggle();
         } else if (minecraftClient3.currentScreen == null) {
            for (Module lii1lll1l1li1ii1iiillii : this.list103) {
               if (lii1lll1l1li1ii1iiillii.getKeyCode() == var1.getKeyCode() && lii1lll1l1li1ii1iiillii.getKeyCode() != -1) {
                  lii1lll1l1li1ii1iiillii.toggle();
               }
            }
         }
      }
   }

   public List<Module> PacketDispatcher() {
      return this.list103;
   }
}
