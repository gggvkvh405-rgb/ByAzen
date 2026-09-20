package org.zenith.module.render;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4d;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.AvatarRenderer;
import org.zenith.core.BotFeatureRegistry;
import org.zenith.core.CloudUserProfile;
import org.zenith.core.EffectEngine;
import org.zenith.core.InventoryUtils;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.HudRenderEvent;
import org.zenith.managers.FriendFilter;
import org.zenith.render.HudPreviewRenderQueue;
import org.zenith.render.ScreenProjection;
import org.zenith.render.WorldRender;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.MathUtils;
import org.zenith.util.TextReplaceUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.QueuedCustomDrawContext;

@ModuleInfo(name = "EntityESP", category = Category.RENDER, description = "ESP")
public final class EntityESP extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final EntityESP entityESP = new EntityESP();
   public final NumberSetting scaleSetting = new NumberSetting(
      "module.entityESP.scaleSetting", 0.7F, 0.5F, 1.0F, 0.1F, "module.entityESP.scaleSetting.desc", "x"
   );
   public final MultiSelectSetting modeSetting6 = MultiSelectSetting.on23(
      "module.entityESP.elements",
      "module.entityESP.elements.desc",
      Arrays.asList(
         "module.entityESP.names",
         "module.entityESP.items",
         "module.entityESP.armor",
         "module.entityESP.triangles",
         "module.entityESP.boxes",
         "module.entityESP.hands",
         "Tracers"
      )
   );
   public final MultiSelectSetting modeSetting7 = MultiSelectSetting.on23(
      "module.entityESP.targets",
      "module.entityESP.targets.desc",
      Arrays.asList(
         "module.entityESP.players", "module.entityESP.noArmor", "module.entityESP.friends", "module.entityESP.droppedItems", "module.entityESP.mobs", "TNT"
      )
   );
   public final NumberSetting removeHiddenPlayerDelay = new NumberSetting(
      "module.entityESP.removeHiddenPlayerDelay",
      1.0F,
      0.0F,
      40.0F,
      1.0F,
      "module.entityESP.removeHiddenPlayerDelay.desc",
      "t",
      () -> this.modeSetting7.ConfigJsonUtil(0),
      null
   );
   public final BooleanSetting blur2 = new BooleanSetting("module.entityESP.blur", "module.entityESP.blurDesc", false, Interface.interfaceField::string88);
   public final BooleanSetting glow = new BooleanSetting("module.entityESP.glow", "module.entityESP.glowDesc", false, Interface.interfaceField::string129);
   public final BooleanSetting gradient = new BooleanSetting(
      "module.entityESP.gradient", "module.entityESP.gradientDesc", true, () -> this.modeSetting6.ConfigJsonUtil(4)
   );
   public final Map<String, EntityESP.CachedRenderData> map16 = new HashMap<>();
   public final HudPreviewRenderQueue hudPreviewRenderQueue = new HudPreviewRenderQueue();

   @EventTarget
   public void PotionItemBuilder(EventHookWorldRender var1) {
      if (this.modeSetting6.ConfigJsonUtil(4) || this.modeSetting6.ConfigJsonUtil(6)) {
         try {
            for (EntityESP.CachedRenderData i1l1i11i1111l11iil1ii_l1i1illlili : this.int464()) {
               EntityESP.RenderData i1l1i11i1111l11iil1ii_ii1il11l111ii11iil = i1l1i11i1111l11iil1ii_l1i1illlili.double154();
               if ((i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.float370() || i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.float371())
                  && (!i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.float370() || !i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.float359())) {
                  try {
                     if (i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.boolean198()) {
                        WorldRender.on23(
                           i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.int449(),
                           ZenithClient.on23().TextScanner().getCurrentStyle().getFriendColor().getColor().call001(),
                           1.0F
                        );
                     } else {
                        WorldRender.on23(
                           i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.int449(),
                           ZenithClient.on23().TextScanner().getClientColor(0).SprintStateEvent(0.7F).call001(),
                           ZenithClient.on23().TextScanner().getClientColor(180).call001(),
                           1.0F
                        );
                     }

                     if (this.modeSetting6.ConfigJsonUtil(6)) {
                        Vec3d vec3d = new Vec3d(0.0, 0.0, 75.0)
                           .rotateX(-((float)Math.toRadians(minecraftClient3.gameRenderer.getCamera().getPitch())))
                           .rotateY(-((float)Math.toRadians(minecraftClient3.gameRenderer.getCamera().getYaw())))
                           .add(FreeCam.freeCam.var1357());
                        WorldRender.on23(
                           vec3d,
                           i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.int449().getCenter(),
                           ZenithClient.on23().TextScanner().getClientColor(0).call001(),
                           1.5F,
                           false
                        );
                     }
                  } catch (Exception exception) {
                     System.out.println("ESP 3D render error: " + exception.getMessage());
                  }
               }
            }
         } catch (Exception exception1) {
            System.out.println("ESP 3D loop error: " + exception1.getMessage());
         }
      }
   }

   @EventTarget
   public void on23(HudRenderEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.getEntityRenderDispatcher().camera != null) {
         CustomDrawContext customdrawcontext = var1.Bot();
         if (customdrawcontext != null) {
            try {
               AvatarRenderer.UiAnimation(customdrawcontext);
               QueuedCustomDrawContext queuedcustomdrawcontext = QueuedCustomDrawContext.of(customdrawcontext);
               HudPreviewRenderQueue.on23(this.hudPreviewRenderQueue);

               try {
                  for (EntityESP.CachedRenderData i1l1i11i1111l11iil1ii_l1i1illlili : this.int464()) {
                     try {
                        EntityESP.RenderData i1l1i11i1111l11iil1ii_ii1il11l111ii11iil = i1l1i11i1111l11iil1ii_l1i1illlili.double154();
                        if (!i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.float370() && !i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.float371()) {
                           if (i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.vec3d44()) {
                              if (this.modeSetting6.ConfigJsonUtil(1) && this.modeSetting7.RotationUpdateStartEvent("module.entityESP.droppedItems")) {
                                 this.Easing(queuedcustomdrawcontext, i1l1i11i1111l11iil1ii_ii1il11l111ii11iil);
                              }
                           } else if (i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.box10() && this.modeSetting6.ConfigJsonUtil(0)) {
                              this.on23(queuedcustomdrawcontext, i1l1i11i1111l11iil1ii_ii1il11l111ii11iil);
                           }
                        } else {
                           if (this.modeSetting6.ConfigJsonUtil(5)) {
                              Vector4d vector4d = this.TextScanner(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.int449());
                              if (vector4d != null) {
                                 this.on23(queuedcustomdrawcontext, i1l1i11i1111l11iil1ii_ii1il11l111ii11iil, vector4d);
                              }
                           }

                           if (this.modeSetting6.ConfigJsonUtil(0)) {
                              this.on23(queuedcustomdrawcontext, i1l1i11i1111l11iil1ii_ii1il11l111ii11iil);
                           }

                           if (this.modeSetting6.ConfigJsonUtil(3)) {
                              this.UiAnimation(queuedcustomdrawcontext, i1l1i11i1111l11iil1ii_ii1il11l111ii11iil);
                           }
                        }
                     } catch (Exception exception) {
                        System.out.println("ESP 2D render error: " + exception.getMessage());
                     }
                  }

                  this.hudPreviewRenderQueue.flush();
               } finally {
                  HudPreviewRenderQueue.UiAnimation(this.hudPreviewRenderQueue);
               }
            } catch (Exception exception1) {
               System.out.println("Строка  145 " + exception1.getMessage());
            }
         }
      }
   }

   public List<EntityESP.CachedRenderData> int464() {
      long i = System.currentTimeMillis();
      if (minecraftClient3.world != null) {
         for (Entity entity : minecraftClient3.world.getEntities()) {
            if (entity instanceof PlayerEntity playerentity) {
               if (!FriendFilter.PotionItemBuilder(playerentity.getId())) {
                  EntityESP.RenderData i1l1i11i1111l11iil1ii_ii1il11l111ii11iil = this.SimpleItemBuilder(playerentity);
                  if (i1l1i11i1111l11iil1ii_ii1il11l111ii11iil != null) {
                     this.map16.put(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.key(), new EntityESP.CachedRenderData(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil, i));
                  }
               }
            } else if (entity instanceof MobEntity mobentity) {
               EntityESP.RenderData i1l1i11i1111l11iil1ii_ii1il11l111ii11iil1 = this.on23(mobentity);
               if (i1l1i11i1111l11iil1ii_ii1il11l111ii11iil1 != null) {
                  this.map16.put(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil1.key(), new EntityESP.CachedRenderData(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil1, i));
               }
            } else if (entity instanceof TntEntity tntentity) {
               EntityESP.RenderData i1l1i11i1111l11iil1ii_ii1il11l111ii11iil2 = this.on23(tntentity);
               if (i1l1i11i1111l11iil1ii_ii1il11l111ii11iil2 != null) {
                  this.map16.put(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil2.key(), new EntityESP.CachedRenderData(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil2, i));
               }
            } else if (entity instanceof ItemEntity itementity) {
               EntityESP.RenderData i1l1i11i1111l11iil1ii_ii1il11l111ii11iil3 = this.UiAnimation(itementity);
               if (i1l1i11i1111l11iil1ii_ii1il11l111ii11iil3 != null) {
                  this.map16.put(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil3.key(), new EntityESP.CachedRenderData(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil3, i));
               }
            }
         }
      }

      this.EmotePlayback(i);
      long j = (long)(this.removeHiddenPlayerDelay.getCurrent() * 50.0F);
      this.map16.values().removeIf(var4x -> i - var4x.double155() > (var4x.entityESPVar159().boolean72() ? j : 200L));
      return new ArrayList<>(this.map16.values());
   }

   public void EmotePlayback(long var1) {
      this.map16.entrySet().removeIf(var0 -> var0.getValue().double154().int448());
      if (this.modeSetting7.RotationUpdateStartEvent("module.entityESP.friends") && minecraftClient3.world != null && minecraftClient3.getNetworkHandler() != null) {
         for (CloudUserProfile li1ilil1i11ii111l11l : ZenithClient.on23().MediaTrackInfo().ShaderHand()) {
            if (li1ilil1i11ii111l11l != null && li1ilil1i11ii111l11l.EventTickEnd() && li1ilil1i11ii111l11l.EventGetBasicProjectionMatrixHook()) {
               BotFeatureRegistry ili1ll11li1ili11l1i1l11l1 = li1ilil1i11ii111l11l.RotationUpdateStartEvent();
               if (ili1ll11li1ili11l1i1l11l1 != null && ili1ll11li1ili11l1i1l11l1.VisualSettingsStore() != null) {
                  String s = ili1ll11li1ili11l1i1l11l1.PacketSendEvent();
                  if (s != null && !s.isBlank() && minecraftClient3.getNetworkHandler().getPlayerListEntry(s) != null) {
                     boolean flag = minecraftClient3.world.getPlayers().stream().anyMatch(var1x -> s.equalsIgnoreCase(var1x.getGameProfile().name()));
                     if (!flag) {
                        Vec3d vec3d = li1ilil1i11ii111l11l.SprintPacketEvent();
                        if (vec3d != null) {
                           Box box = new Box(
                              vec3d.x - 0.3,
                              vec3d.y,
                              vec3d.z - 0.3,
                              vec3d.x + 0.3,
                              vec3d.y + 1.8,
                              vec3d.z + 0.3
                           );
                           Object object = List.of();
                           InventoryUtils l11illi1i11 = li1ilil1i11ii111l11l.TargetAcquireEvent();
                           if (l11illi1i11 != null && li1ilil1i11ii111l11l.FovEvent().isEnabled()) {
                              ArrayList arraylist = new ArrayList();
                              arraylist.add(l11illi1i11.EventHookWorldRender());
                              arraylist.addAll(l11illi1i11.HudRenderEvent());
                              object = arraylist;
                           }

                           String s1 = ili1ll11li1ili11l1i1l11l1.uuid() == null ? li1ilil1i11ii111l11l.id() : ili1ll11li1ili11l1i1l11l1.uuid().toString();
                           EntityESP.RenderData i1l1i11i1111l11iil1ii_ii1il11l111ii11iil = new EntityESP.RenderData(
                              "cloud-player:" + s1,
                              Text.literal(s),
                              true,
                              true,
                              false,
                              false,
                              false,
                              true,
                              false,
                              box.getCenter(),
                              box,
                              ili1ll11li1ili11l1i1l11l1.Item(),
                              0,
                              List.of(),
                              (List<ItemStack>)object,
                              null
                           );
                           this.map16.put(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil.key(), new EntityESP.CachedRenderData(i1l1i11i1111l11iil1ii_ii1il11l111ii11iil, var1));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void on23(CustomDrawContext var1, EntityESP.RenderData var2, Vector4d var3) {
      if (var2 != null
         && var1 != null
         && var3 != null
         && (!var2.float359() || !minecraftClient3.options.getPerspective().isFirstPerson())
         && !ScreenProjection.on23(var3)
         && !var2.double151().isEmpty()) {
         try {
            double d0 = var3.w - 10.0;
            float f = (float)ScreenProjection.UiAnimation(var3);
            ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
            Font font = Fonts.NEW_MEDIUM.getFont(8.0F);

            for (ItemStack itemstack : var2.double151()) {
               if (itemstack != null && !itemstack.isEmpty()) {
                  try {
                     MutableText mutabletext = itemstack.getName().copy();
                     if (!mutabletext.getString().isEmpty() && mutabletext.getString().endsWith(" ")) {
                        mutabletext = TextReplaceUtils.ItemRegistry(mutabletext, "");
                     }

                     NbtComponent nbtcomponent = (NbtComponent)itemstack.get(DataComponentTypes.CUSTOM_DATA);
                     if (nbtcomponent != null && nbtcomponent.copyNbt().getKeys().contains("itemServiceId")) {
                        String s = nbtcomponent.copyNbt()
                           .getCompound("itemServiceId")
                           .flatMap(component -> component.getString("name"))
                           .orElse("unknown")
                           .replace("_", " ");
                        if (itemstack.getItem() == Items.TOTEM_OF_UNDYING) {
                           s = "Талисман Eternity";
                        }

                        s = Character.toUpperCase(s.toCharArray()[0]) + s.substring(1);
                        s = s.replace("2", " 2").replace("1", " 1");
                        if (s.equals("Мифическая сфера 1")) {
                           s = "Мифка у3 б2";
                        } else if (s.equals("Мифическая сфера 2")) {
                           s = "Мифка б3 у2";
                        }

                        mutabletext = Text.literal(s)
                           .setStyle(Style.EMPTY.withColor(ZenithClient.on23().TextScanner().getClientColor(90).call001()));
                     }

                     if ((!ZenithClient.on23().CloudApiClient().call003() || var2.boolean77()) && itemstack.getCount() > 1) {
                        mutabletext.append(
                           Text.of(" x" + itemstack.getCount())
                              .copy()
                              .setStyle(Style.EMPTY.withColor(zenithstyle.getPrimaryColor().getColor().call001()))
                        );
                     }

                     d0 += (font.height() / 2.0F + 12.0F + 2.0F) * this.scaleSetting.getCurrent();
                     float f7 = f - font.width(mutabletext) / 2.0F;
                     ArgbColor i11ii1llliilllii1i1 = zenithstyle.getHudBackground().getColor();
                     float f1 = font.width(mutabletext) + 8.0F;
                     float f2 = font.height() + GuiStyle.PADDING.intValue() + 1.0F;
                     float f3 = f7 - 4.0F;
                     float f4 = (float)d0;
                     float f5 = f3 + f1 / 2.0F;
                     float f6 = f4 + f2 / 2.0F;
                     this.pushCenteredScale(var1, f5, f6, 1.0F, 1.0F);
                     CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(Interface.float212() * 0.6F);
                     var1.drawBlurHudBooleanCheck(
                        f3,
                        f4,
                        f1,
                        f2,
                        22.0F,
                        ii1il11l111ii11iil,
                        ArgbColor.var11934,
                        this.blur2.isEnabled() && this.blur2.isVisible(),
                        this.glow.isEnabled() && this.glow.isVisible()
                     );
                     var1.drawRoundedRect(f3, f4, f1, f2, ii1il11l111ii11iil, i11ii1llliilllii1i1);
                     var1.pushMatrix();
                     var1.getMatrices().translate(f7, f4 + (f2 - 1.0F - font.height()) / 2.0F);
                     var1.drawText(font, mutabletext, 0.0F, 0.0F, zenithstyle.getTextEnable().getColor().call001());
                     var1.popMatrix();
                     this.pop(var1);
                  } catch (Exception exception) {
                     System.out.println("drawHands err: " + exception.getMessage());
                  }
               }
            }
         } catch (Exception exception1) {
            System.out.println("drawHands outer err: " + exception1.getMessage());
         }
      }
   }

   public void on23(CustomDrawContext var1, EntityESP.RenderData var2) {
      if (var2 != null && var1 != null && (!var2.float359() || !minecraftClient3.options.getPerspective().isFirstPerson())) {
         try {
            Box box = var2.int449();
            Vec3d vec3d = new Vec3d(box.getCenter().x, box.maxY + 0.15F, box.getCenter().z);
            if (!this.NbtEditor(vec3d)) {
               return;
            }

            Vec3d vec3d1 = ScreenProjection.BotDisconnectEvent(vec3d);
            if (vec3d1 == null || vec3d1.z <= 0.0) {
               return;
            }

            ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
            boolean flag = var2.boolean198();
            ArgbColor i11ii1llliilllii1i1 = flag ? zenithstyle.getFriendColor().getColor().SprintPacketEvent(0.5F) : zenithstyle.getHudBackground().getColor();
            Text text = var2.name() != null ? var2.name() : Text.of("unknown");
            MutableText mutabletext = Text.literal(NameProtect.call029());
            Object object = (!flag || !NameProtect.nameProtect.isEnabled() || !NameProtect.nameProtect.call057()) && !var2.boolean77() ? text : mutabletext;
            float f = var2.Item();
            String s = var2.box10() ? " " + this.EventMotion(var2.int450()) : (f >= 0.0F ? " " + EffectEngine.RefreshCacheEvent(f) : " --");
            Font font = Fonts.NEW_MEDIUM.getFont(7.0F);
            float f1 = font.width((Text)object) + GuiStyle.PADDING.intValue() / 2.0F + font.width(s);
            float f2 = (float)vec3d1.x - f1 / 2.0F;
            float f3 = (float)vec3d1.y;
            this.pushCenteredScale(var1, f2 + f1 / 2.0F, f3 + (font.height() + GuiStyle.PADDING.intValue() + 1.0F) / 2.0F, 1.0F, 1.0F);
            CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(Interface.float212() * 0.6F);
            var1.drawBlurHudBooleanCheck(
               f2 - 4.0F,
               f3 - 13.0F,
               f1 + 8.0F,
               font.height() + GuiStyle.PADDING.intValue() + 1.0F,
               22.0F,
               ii1il11l111ii11iil,
               ArgbColor.var11934,
               this.blur2.isEnabled() && this.blur2.isVisible(),
               this.glow.isEnabled() && this.glow.isVisible()
            );
            var1.drawRoundedRect(f2 - 4.0F, f3 - 13.0F, f1 + 8.0F, font.height() + GuiStyle.PADDING.intValue() + 1.0F, ii1il11l111ii11iil, i11ii1llliilllii1i1);
            var1.pushMatrix();
            var1.getMatrices().translate(f2, f3 - 13.0F + GuiStyle.PADDING.intValue() / 2.0F);
            ArgbColor i11ii1llliilllii1i11 = var2.box10()
               ? ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor()
               : (f >= 0.0F ? this.BotTickEvent(f) : zenithstyle.getTextEnable().getColor());
            var1.drawText(font, (Text)object, 0.0F, 0.0F, zenithstyle.getTextEnable().getColor().call001());
            var1.drawText(font, s, font.width((Text)object) + 1.5F, 0.0F, i11ii1llliilllii1i11);
            var1.popMatrix();
            this.pop(var1);
            if (this.modeSetting6.ConfigJsonUtil(2) && !var2.double152().isEmpty()) {
               List<ItemStack> arraylist = new ArrayList<>(var2.double152());
               long i = arraylist.stream().filter(var0 -> var0 != null && !var0.isEmpty()).count();
               if (i > 0L) {
                  float f4 = (float)(i * 18L);
                  float f5 = f2 + f1 / 2.0F;
                  float f6 = f3 - 13.0F - GuiStyle.PADDING.intValue() / 2.0F - 18.0F * this.scaleSetting.getCurrent();
                  float f7 = f5 - f4 / 2.0F - 1.0F;
                  float f8 = 18.0F;
                  float f9 = f7 + f4 / 2.0F;
                  float f10 = f6 + f8;
                  this.pushCenteredScale(var1, f9, f10, 1.0F, 1.0F);
                  var1.drawBlurHudBooleanCheck(
                     f7,
                     f6,
                     f4,
                     f8,
                     22.0F,
                     ii1il11l111ii11iil,
                     ArgbColor.var11934,
                     this.blur2.isEnabled() && this.blur2.isVisible(),
                     this.glow.isEnabled() && this.glow.isVisible()
                  );
                  var1.drawRoundedRect(f7, f6, f4, f8, ii1il11l111ii11iil, i11ii1llliilllii1i1);
                  float f11 = 0.0F;

                  for (ItemStack itemstack : arraylist) {
                     if (itemstack != null && !itemstack.isEmpty()) {
                        var1.pushMatrix();
                        var1.getMatrices().translate(f5 - f4 / 2.0F + f11, f6);
                        var1.getMatrices().scale(0.8F, 0.8F);
                        var1.getMatrices().translate(2.0F, 2.0F);
                        minecraftClient3.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
                        var1.drawItem(itemstack, 0, 1);
                        var1.popMatrix();
                        f11 += 18.0F;
                     }
                  }

                  this.pop(var1);
               }
            }
         } catch (Exception exception) {
            System.out.println("renderNameTag err: " + exception.getMessage());
         }
      }
   }

   public void UiAnimation(CustomDrawContext var1, EntityESP.RenderData var2) {
      if (var2 != null && var1 != null && (!var2.float359() || !minecraftClient3.options.getPerspective().isFirstPerson())) {
         try {
            Vector4d vector4d = this.TextScanner(var2.int449());
            if (vector4d == null) {
               return;
            }

            this.on23(var1, var2.boolean198(), vector4d);
         } catch (Exception exception) {
            System.out.println("renderEntityBox err: " + exception.getMessage());
         }
      }
   }

   public void Easing(CustomDrawContext var1, EntityESP.RenderData var2) {
      if (var2 != null && var1 != null && var2.double153() != null && !var2.double153().isEmpty()) {
         try {
            Vec3d[] avec3d = this.NbtItemSpec(var2.int449());
            Vector4d vector4d = null;

            for (Vec3d vec3d : avec3d) {
               try {
                  if (this.NbtEditor(vec3d)) {
                     Vec3d vec3d1 = ScreenProjection.BotDisconnectEvent(vec3d);
                     if (vec3d1 != null && vec3d1.z > 0.0) {
                        if (vector4d == null) {
                           vector4d = new Vector4d(vec3d1.x, vec3d1.y, vec3d1.x, vec3d1.y);
                        }

                        vector4d.x = Math.min(vector4d.x, vec3d1.x);
                        vector4d.y = Math.min(vector4d.y, vec3d1.y);
                        vector4d.z = Math.max(vector4d.z, vec3d1.x);
                        vector4d.w = Math.max(vector4d.w, vec3d1.y);
                     }
                  }
               } catch (Exception var18) {
               }
            }

            if (vector4d == null) {
               return;
            }

            MutableText mutabletext = var2.double153().getName().copy();
            if (mutabletext.getString().endsWith(" ")) {
               mutabletext = TextReplaceUtils.ItemRegistry(mutabletext, "");
            }

            if (var2.double153().getCount() > 1) {
               mutabletext = mutabletext.copy()
                  .append(
                     Text.of(" x" + var2.double153().getCount())
                        .copy()
                        .setStyle(
                           Style.EMPTY.withColor(ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor().call001())
                        )
                  );
            }

            ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
            Font font = Fonts.NEW_MEDIUM.getFont(8.0F);
            float f7 = GuiStyle.PADDING.intValue();
            float f8 = GuiStyle.PADDING.intValue() + font.height() + 1.0F;
            float f = font.width(mutabletext);
            float f1 = f7 + 10.4F + f7 + f + f7;
            float f2 = (float)vector4d.x;
            float f3 = (float)vector4d.z;
            float f4 = (float)vector4d.y;
            float f5 = f2 + (f3 - f2) / 2.0F - f1 / 2.0F;
            float f6 = f4 - 13.0F * this.scaleSetting.getCurrent();
            this.pushCenteredScale(var1, f5 + f1 / 2.0F, f6 + 6.5F, 1.0F, 1.0F);
            CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(Interface.float212() / 2.0F);
            var1.drawBlurHudBooleanCheck(
               f5,
               f6,
               f1,
               f8,
               22.0F,
               ii1il11l111ii11iil,
               ArgbColor.var11934,
               this.blur2.isEnabled() && this.blur2.isVisible(),
               this.glow.isEnabled() && this.glow.isVisible()
            );
            var1.drawRoundedRect(f5, f6, f1, f8, ii1il11l111ii11iil, zenithstyle.getHudBackground().getColor());
            var1.drawText(font, mutabletext, f5 + f7 + 10.4F + f7, f6 + (f8 - 1.0F - font.height()) / 2.0F, zenithstyle.getTextEnable().getColor().call001());
            var1.pushMatrix();
            var1.getMatrices().translate(f5 + f7, f6 + (f8 - 1.0F - 10.4F));
            var1.getMatrices().scale(0.65F, 0.65F);
            var1.drawItem(var2.double153(), 0, 0);
            var1.popMatrix();
            this.pop(var1);
         } catch (Exception exception1) {
            System.out.println("renderItemTarget err: " + exception1.getMessage());
         }
      }
   }

   public void on23(CustomDrawContext var1, boolean var2, Vector4d var3) {
      if (var3 != null) {
         try {
            int[] aint = var2
               ? new int[]{
                  ZenithClient.on23().TextScanner().getCurrentStyle().getFriendColor().getColor().call001(),
                  ZenithClient.on23().TextScanner().getCurrentStyle().getFriendColor().getColor().call001(),
                  ZenithClient.on23().TextScanner().getCurrentStyle().getFriendColor().getColor().call001(),
                  ZenithClient.on23().TextScanner().getCurrentStyle().getFriendColor().getColor().call001()
               }
               : new int[]{
                  ZenithClient.on23().TextScanner().getClientColor(0).call001(),
                  ZenithClient.on23().TextScanner().getClientColor(90).call001(),
                  ZenithClient.on23().TextScanner().getClientColor(180).call001(),
                  ZenithClient.on23().TextScanner().getClientColor(270).call001()
               };
            float f = (float)var3.x;
            float f1 = (float)var3.y;
            float f2 = (float)var3.z;
            float f3 = (float)var3.w;
            float f4 = (f2 - f) / 3.0F;
            var1.drawRect(f - 1.0F, f1 - 1.0F, f4, 1.0F, ArgbColor.HudRenderEvent(aint[0]));
            var1.drawRect(f - 1.0F, f1, 1.0F, f4 + 1.0F, ArgbColor.HudRenderEvent(aint[0]));
            var1.drawRect(f - 1.0F, f3 - f4 - 1.0F, 1.0F, f4, ArgbColor.HudRenderEvent(aint[1]));
            var1.drawRect(f - 1.0F, f3 - 1.0F, f4, 1.0F, ArgbColor.HudRenderEvent(aint[1]));
            var1.drawRect(f2 - f4 + 2.0F, f1 - 1.0F, f4, 1.0F, ArgbColor.HudRenderEvent(aint[2]));
            var1.drawRect(f2 + 1.0F, f1, 1.0F, f4 + 1.0F, ArgbColor.HudRenderEvent(aint[2]));
            var1.drawRect(f2 + 1.0F, f3 - f4 - 1.0F, 1.0F, f4, ArgbColor.HudRenderEvent(aint[3]));
            var1.drawRect(f2 - f4 + 2.0F, f3 - 1.0F, f4, 1.0F, ArgbColor.HudRenderEvent(aint[3]));
         } catch (Exception exception) {
            System.out.println("drawFlatBox err: " + exception.getMessage());
         }
      }
   }

   public Vector4d TextScanner(Box var1) {
      Vec3d[] avec3d = this.NbtItemSpec(var1);
      Vector4d vector4d = null;

      for (Vec3d vec3d : avec3d) {
         try {
            if (this.NbtEditor(vec3d)) {
               Vec3d vec3d1 = ScreenProjection.BotDisconnectEvent(vec3d);
               if (vec3d1 != null && vec3d1.z > 0.0) {
                  if (vector4d == null) {
                     vector4d = new Vector4d(vec3d1.x, vec3d1.y, vec3d1.x, vec3d1.y);
                  }

                  vector4d.x = Math.min(vector4d.x, vec3d1.x);
                  vector4d.y = Math.min(vector4d.y, vec3d1.y);
                  vector4d.z = Math.max(vector4d.z, vec3d1.x);
                  vector4d.w = Math.max(vector4d.w, vec3d1.y);
               }
            }
         } catch (Exception var9) {
         }
      }

      return vector4d;
   }

   public ArgbColor BotTickEvent(float var1) {
      try {
         if (var1 <= 7.0F) {
            return new ArgbColor(255, 0, 0, 255);
         } else {
            return var1 <= 15.0F ? new ArgbColor(255, 255, 0, 255) : new ArgbColor(0, 255, 0, 255);
         }
      } catch (Exception exception) {
         return ArgbColor.var11934;
      }
   }

   public float getSize() {
      return this.scaleSetting.getCurrent();
   }

   public boolean box4() {
      return this.gradient.isEnabled();
   }

   public boolean float55() {
      return this.isEnabled() && this.modeSetting6.ConfigJsonUtil(0);
   }

   public void pushCenteredScale(CustomDrawContext var1, float var2, float var3, float var4, float var5) {
      if (var1 != null) {
         try {
            var1.pushMatrix();
            var1.getMatrices().translate(var2, var3);
            var1.getMatrices().scale(var4 * this.scaleSetting.getCurrent(), var5 * this.scaleSetting.getCurrent());
            var1.getMatrices().translate(-var2, -var3);
         } catch (Exception exception) {
            System.out.println("Строка  82 " + exception.getMessage());
         }
      }
   }

   public void pop(CustomDrawContext var1) {
      if (var1 != null) {
         try {
            var1.popMatrix();
         } catch (Exception exception) {
            System.out.println("Строка  91 " + exception.getMessage());
         }
      }
   }

   public EntityESP.RenderData SimpleItemBuilder(PlayerEntity var1) {
      boolean flag = ZenithClient.on23().MediaTrackInfo().isFriend(var1.getName().getString());
      if (flag && !this.modeSetting7.RotationUpdateStartEvent("module.entityESP.friends")) {
         return null;
      }

      if (!flag) {
         boolean flag1 = EffectEngine.ItemServiceBase(var1) == 0.0F;
         if (flag1 && !this.modeSetting7.RotationUpdateStartEvent("module.entityESP.noArmor")) {
            return null;
         }

         if (!flag1 && !this.modeSetting7.RotationUpdateStartEvent("module.entityESP.players")) {
            return null;
         }
      }

      Vec3d vec3d = MathUtils.CloudResponse(var1);
      Box box = var1.getBoundingBox().offset(vec3d.subtract(var1.getEntityPos()));
      float f = EffectEngine.SimpleItemBuilder(var1);
      ArrayList arraylist = new ArrayList();
      arraylist.add(var1.getMainHandStack());
      arraylist.add(var1.getOffHandStack());
      ArrayList arraylist1 = new ArrayList();
      arraylist1.add(var1.getOffHandStack());
      arraylist1.add(var1.getEquippedStack(EquipmentSlot.FEET));
      arraylist1.add(var1.getEquippedStack(EquipmentSlot.LEGS));
      arraylist1.add(var1.getEquippedStack(EquipmentSlot.CHEST));
      arraylist1.add(var1.getEquippedStack(EquipmentSlot.HEAD));
      arraylist1.add(var1.getMainHandStack());
      return new EntityESP.RenderData(
         "player:" + var1.getUuidAsString(),
         StreamerMode.streamerMode.UiAnimation(var1.getDisplayName() != null ? var1.getDisplayName() : var1.getName(), var1.getGameProfile().name()),
         flag,
         true,
         false,
         false,
         false,
         false,
         var1 == minecraftClient3.player,
         box.getCenter(),
         box,
         f,
         0,
         arraylist,
         arraylist1,
         null
      );
   }

   public EntityESP.RenderData on23(MobEntity var1) {
      if (!this.modeSetting7.RotationUpdateStartEvent("module.entityESP.mobs")) {
         return null;
      }

      Vec3d vec3d = MathUtils.CloudResponse(var1);
      Box box = var1.getBoundingBox().offset(vec3d.subtract(var1.getEntityPos()));
      float f = EffectEngine.SimpleItemBuilder(var1);
      return new EntityESP.RenderData(
         "mob:" + var1.getUuidAsString(),
         var1.getDisplayName() != null ? var1.getDisplayName() : var1.getName(),
         false,
         false,
         true,
         false,
         false,
         false,
         false,
         box.getCenter(),
         box,
         f,
         0,
         List.of(),
         List.of(),
         null
      );
   }

   public EntityESP.RenderData on23(TntEntity var1) {
      if (!this.modeSetting7.RotationUpdateStartEvent("TNT")) {
         return null;
      }

      Vec3d vec3d = MathUtils.CloudResponse(var1);
      Box box = var1.getBoundingBox().offset(vec3d.subtract(var1.getEntityPos()));
      return new EntityESP.RenderData(
         "tnt:" + var1.getUuidAsString(),
         var1.getDisplayName(),
         false,
         false,
         false,
         false,
         true,
         false,
         false,
         box.getCenter(),
         box,
         -1.0F,
         var1.getFuse(),
         List.of(),
         List.of(),
         null
      );
   }

   public EntityESP.RenderData UiAnimation(ItemEntity var1) {
      if (var1.getStack().isEmpty()) {
         return null;
      }

      if (!this.modeSetting7.RotationUpdateStartEvent("module.entityESP.droppedItems")) {
         return null;
      }

      Vec3d vec3d = MathUtils.CloudResponse(var1);
      Box box = var1.getBoundingBox().offset(vec3d.subtract(var1.getEntityPos()));
      return new EntityESP.RenderData(
         "item:" + var1.getUuidAsString(),
         var1.getStack().getCustomName() != null ? var1.getStack().getCustomName() : var1.getName(),
         false,
         false,
         false,
         true,
         false,
         false,
         false,
         box.getCenter(),
         box,
         -1.0F,
         0,
         List.of(),
         List.of(),
         var1.getStack().copy()
      );
   }

   public Vec3d[] NbtItemSpec(Box var1) {
      try {
         return new Vec3d[]{
            new Vec3d(var1.minX, var1.minY, var1.minZ),
            new Vec3d(var1.minX, var1.maxY, var1.minZ),
            new Vec3d(var1.maxX, var1.minY, var1.minZ),
            new Vec3d(var1.maxX, var1.maxY, var1.minZ),
            new Vec3d(var1.minX, var1.minY, var1.maxZ),
            new Vec3d(var1.minX, var1.maxY, var1.maxZ),
            new Vec3d(var1.maxX, var1.minY, var1.maxZ),
            new Vec3d(var1.maxX, var1.maxY, var1.maxZ)
         };
      } catch (Exception exception) {
         System.out.println("getPoints err: " + exception.getMessage());
         return new Vec3d[8];
      }
   }

   public float EnchantItemSpec(Box var1) {
      return (float)var1.getLengthY();
   }

   public boolean NbtEditor(Vec3d var1) {
      if (minecraftClient3.gameRenderer != null && minecraftClient3.gameRenderer.getCamera() != null) {
         Vec3d vec3d = minecraftClient3.gameRenderer.getCamera().getCameraPos();
         Vec3d vec3d1 = var1.subtract(vec3d);
         float f = minecraftClient3.gameRenderer.getCamera().getPitch();
         float f1 = minecraftClient3.gameRenderer.getCamera().getYaw();
         double d0 = Math.toRadians(f);
         double d1 = Math.toRadians(f1);
         Vec3d vec3d2 = new Vec3d(-Math.sin(d1) * Math.cos(d0), -Math.sin(d0), Math.cos(d1) * Math.cos(d0));
         return vec3d1.dotProduct(vec3d2) > 0.0;
      } else {
         return false;
      }
   }

   public String EventMotion(int var1) {
      return String.format("%.1fs", Math.max(var1, 0) / 20.0F);
   }

   public BooleanSetting float369() {
      return this.blur2;
   }

   public BooleanSetting zClass026() {
      return this.glow;
   }


   public record CachedRenderData(RenderData entityESPVar159, long long87) {
      public RenderData double154() {
         return this.entityESPVar159;
      }

      public long double155() {
         return this.long87;
      }
   }

   public record RenderData(
      String string20,
      Text text3,
      boolean boolean71,
      boolean boolean72,
      boolean boolean73,
      boolean boolean74,
      boolean boolean75,
      boolean boolean76,
      boolean boolean77,
      Vec3d vec3d9,
      Box box3,
      float float53,
      int int113,
      List<ItemStack> list35,
      List<ItemStack> list36,
      ItemStack itemStack6
   ) {
      public String key() {
         return this.string20;
      }

      public Text name() {
         return this.text3;
      }

      public boolean boolean198() {
         return this.boolean71;
      }

      public boolean float370() {
         return this.boolean72;
      }

      public boolean float371() {
         return this.boolean73;
      }

      public boolean vec3d44() {
         return this.boolean74;
      }

      public boolean box10() {
         return this.boolean75;
      }

      public boolean int448() {
         return this.boolean76;
      }

      public boolean float359() {
         return this.boolean77;
      }

      public Vec3d VisualSettingsStore() {
         return this.vec3d9;
      }

      public Box int449() {
         return this.box3;
      }

      public float Item() {
         return this.float53;
      }

      public int int450() {
         return this.int113;
      }

      public List<ItemStack> double151() {
         return this.list35;
      }

      public List<ItemStack> double152() {
         return this.list36;
      }

      public ItemStack double153() {
         return this.itemStack6;
      }
   }
}
