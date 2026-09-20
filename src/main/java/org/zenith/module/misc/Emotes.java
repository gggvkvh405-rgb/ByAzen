package org.zenith.module.misc;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.RotationAxis;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.emote.EmoteFavoriteScreen;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.UiAnimation;
import org.zenith.event.EventMouseButton;
import org.zenith.event.EventMouseScrollHook;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.event.EventTick;
import org.zenith.event.EventTriggerKeyEvent;
import org.zenith.managers.EmoteMetadata;
import org.zenith.managers.EmoteRegistry;
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.KeySetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

@ModuleInfo(name = "Emotes", category = Category.MISC, description = "Колесо эмоций")
public final class Emotes extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Emotes emotes = new Emotes();
   public final BooleanSetting booleanSetting2;
   public final String[] val011;
   public boolean boolean13;
   public final KeySetting wheelBind2 = new KeySetting("module.emotes.wheelBind", "module.emotes.wheelBind.desc", -1);
   public boolean boolean14;
   public final NumberSetting numberSetting;
   public long long82;
   public final UiAnimation[] val091;
   public boolean boolean47;
   public int int91;
   public final UiAnimation var1433;
   public int page;

   public Emotes() {
      this.booleanSetting2 = new BooleanSetting("module.emotes.autoF5", "module.emotes.autoF5.desc", false);
      this.numberSetting = new NumberSetting("module.emotes.autoF5Duration", 5.0F, 1.0F, 30.0F, 0.5F, "module.emotes.autoF5Duration.desc", "s");
      this.val011 = new String[120];
      this.int91 = -1;
      this.var1433 = new UiAnimation(280L, 0.0F, Easing.RenderTickEvent);
      this.val091 = new UiAnimation[8];
      this.numberSetting.setVisible(this.booleanSetting2::isEnabled);

      for (int i = 0; i < this.val091.length; i++) {
         this.val091[i] = new UiAnimation(160L, 0.0F, Easing.EventClick);
      }
   }

   @Override
   public void onDisable() {
      this.ModuleStateStore(false);
      this.double27();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (this.boolean47 && (!this.booleanSetting2.isEnabled() || System.nanoTime() >= this.long82)) {
         this.double27();
      }
   }

   @EventTarget
   public void on23(EventTriggerKeyEvent var1) {
      if (var1.is(this.wheelBind2.getKeyCode()) && this.wheelBind2.getKeyCode() != -1) {
         if (var1.TridentAimbot() == 1) {
            this.call396();
         } else if (var1.TridentAimbot() == 0) {
            this.ModuleStateStore(true);
         }
      }
   }

   @EventTarget
   public void UiAnimation(EventMouseButton var1) {
      if (this.boolean13 && var1.TridentAimbot() == 1 && (var1.ContainerScanner() == 0 || var1.ContainerScanner() == 1)) {
         var1.setCancelled(true);
         int i = this.on23(this.call158(), this.call159(), this.double28());
         if (i != -1) {
            this.int91 = i;
            int k = this.BotDisconnectEvent(i);
            EmoteMetadata li1ll1i111l1l1iilli1111il = this.BotChatEvent(i);
            if (li1ll1i111l1l1iilli1111il == null) {
               this.BotWorldJoinEvent(k);
            } else if (var1.ContainerScanner() == 1) {
               this.BotPacketEvent(k);
            } else {
               this.UiAnimation(li1ll1i111l1l1iilli1111il);
               this.ModuleStateStore(false);
            }
         } else {
            int j = this.UiAnimation(this.call158(), this.call159(), this.double28());
            if (j != 0 && var1.ContainerScanner() == 0) {
               this.ItemRegistry(j, true);
            } else if (var1.ContainerScanner() == 1 && minecraftClient3.player != null) {
               EmoteRegistry.ItemSpec(minecraftClient3.player.getUuid());
            }
         }
      }
   }

   @EventTarget
   public void UiAnimation(EventMouseScrollHook var1) {
      if (this.boolean13 && var1.TapeMouse() != 0.0) {
         this.ItemRegistry(var1.TapeMouse() < 0.0 ? 1 : -1, false);
         var1.setCancelled(true);
      }
   }

   @EventTarget(4)
   public void on23(EventRenderScreenHook var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.currentScreen == null) {
         if (this.boolean13) {
            this.CloudPoller(true);
            Emotes.WheelBounds liiiiill1iililliil11illli1ll_ii1il11l111ii11iil = this.double28();
            this.int91 = this.on23(var1.WarpFarm().getMouseX(), var1.WarpFarm().getMouseY(), liiiiill1iililliil11illli1ll_ii1il11l111ii11iil);
            this.on23(var1.WarpFarm(), liiiiill1iililliil11illli1ll_ii1il11l111ii11iil);
         }
      } else if (this.boolean13) {
         this.ModuleStateStore(false);
      }
   }

   public void call396() {
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.currentScreen == null && !EmoteRegistry.set19().isEmpty()
         )
       {
         this.boolean13 = true;
         this.int91 = -1;

         for (UiAnimation l1i1illlili : this.val091) {
            l1i1illlili.setValue(0.0F);
         }

         this.var1433.setValue(0.0F);
         this.var1433.on23(1.0F);
         this.CloudPoller(true);
      }
   }

   public void call023() {
      this.ModuleStateStore(false);
   }

   public void ModuleStateStore(boolean var1) {
      if (var1 && this.boolean13) {
         EmoteMetadata li1ll1i111l1l1iilli1111il = this.BotChatEvent(this.int91);
         if (li1ll1i111l1l1iilli1111il != null) {
            this.UiAnimation(li1ll1i111l1l1iilli1111il);
         }
      }

      this.boolean13 = false;
      this.int91 = -1;
      this.CloudPoller(false);
   }

   public void UiAnimation(EmoteMetadata var1) {
      EmoteRegistry.BotChatEvent(var1.id());
      if (this.booleanSetting2.isEnabled() && minecraftClient3.options.getPerspective().isFirstPerson()) {
         this.boolean47 = true;
         minecraftClient3.options.setPerspective(Perspective.THIRD_PERSON_BACK);
         this.long82 = System.nanoTime() + (long)(this.numberSetting.getCurrent() * 1.0E9F);
      }
   }

   public void double27() {
      if (this.boolean47) {
         minecraftClient3.options.setPerspective(Perspective.FIRST_PERSON);
         this.boolean47 = false;
         this.long82 = 0L;
      }
   }

   public void on23(HudDrawContext var1, Emotes.WheelBounds var2) {
      ZenithStyle zenithstyle = val003.TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = Math.clamp(this.var1433.on23(1.0F), 0.0F, 1.0F);
         float f1 = 0.82F + 0.18F * f;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var2.float121(), var2.float122());
         var1.getMatrices().scale(f1, f1);
         var1.getMatrices().translate(-var2.float121(), -var2.float122());
         this.on23(var1, var2, f);
         this.on23(var1, var2, zenithstyle, f);
         this.UiAnimation(var1, var2, zenithstyle, f);
         this.Easing(var1, var2, zenithstyle, f);
         var1.getMatrices().popMatrix();
         this.ColorAnimator(var1, var2, zenithstyle, f);
      }
   }

   public void on23(HudDrawContext var1, Emotes.WheelBounds var2, float var3) {
      float f = val003.NbtEditor().getBlurPower();
      if (!(f <= 0.0F)) {
         float f1 = var2.float124() + 7.0F;
         ShapeRenderer.on23(
            var1.getMatrices(),
            var2.float121() - f1,
            var2.float122() - f1,
            f1 * 2.0F,
            f1 * 2.0F,
            f,
            CornerRadius.MovementInputEvent(f1),
            ArgbColor.var11934.SprintStateEvent(var3 * 0.92F),
            true,
            false
         );
      }
   }

   public void on23(HudDrawContext var1, Emotes.WheelBounds var2, ZenithStyle var3, float var4) {
      float f = 45.0F;
      float f1 = f - 2.4F;
      ArgbColor i11ii1llliilllii1i1 = var3.getSurfaceDisableBackground().getColor().SprintStateEvent(var4);
      ArgbColor i11ii1llliilllii1i11 = var3.getRightBackground().getColor().SprintStateEvent(var4 * 0.92F);
      ArgbColor i11ii1llliilllii1i12 = var3.getPrimaryColor().getColor().SprintStateEvent(var4);

      for (int i = 0; i < 8; i++) {
         float f2 = this.val091[i].on23(this.int91 == i ? 1.0F : 0.0F);
         float f3 = var2.float124() + f2 * 6.0F;
         ArgbColor i11ii1llliilllii1i13 = i11ii1llliilllii1i1.Easing(var3.getSurfaceEnableBackground().getColor().SprintStateEvent(var4), f2);
         ArgbColor i11ii1llliilllii1i14 = i11ii1llliilllii1i11.Easing(i11ii1llliilllii1i12.SprintPacketEvent(0.48F), f2 * 0.86F);
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var2.float121(), var2.float122());
         var1.getMatrices().rotate((float)Math.toRadians(i * f + 1.2F));
         var1.getMatrices().translate(-var2.float121(), -var2.float122());
         ShapeRenderer.on23(
            var1.getMatrices(),
            var2.float121() - f3,
            var2.float122() - f3,
            f3 * 2.0F,
            f3 * 2.0F,
            f3 - var2.float123(),
            f1,
            0.0F,
            i11ii1llliilllii1i13,
            i11ii1llliilllii1i14,
            i11ii1llliilllii1i14,
            i11ii1llliilllii1i13
         );
         if (f2 > 0.01F) {
            ShapeRenderer.on23(
               var1.getMatrices(),
               var2.float121() - f3,
               var2.float122() - f3,
               f3 * 2.0F,
               f3 * 2.0F,
               1.35F + f2,
               f1,
               0.0F,
               i11ii1llliilllii1i12.SprintStateEvent(f2 * 0.8F)
            );
         }

         var1.getMatrices().popMatrix();
      }
   }

   public void UiAnimation(HudDrawContext var1, Emotes.WheelBounds var2, ZenithStyle var3, float var4) {
      float f = (var2.float123() + var2.float124()) / 2.0F;

      for (int i = 0; i < 8; i++) {
         EmoteMetadata li1ll1i111l1l1iilli1111il = this.BotChatEvent(i);
         float f1 = this.val091[i].CancellableEvent();
         float f2 = BotTickEvent(i);
         float f3 = f + f1 * 4.0F;
         float f4 = var2.float121() + (float)Math.cos(f2) * f3;
         float f5 = var2.float122() + (float)Math.sin(f2) * f3;
         float f6 = 36.0F + f1 * 4.0F;
         ArgbColor i11ii1llliilllii1i1 = var3.getPanelLeftBackground().getColor().SprintStateEvent(var4 * (0.72F + f1 * 0.22F));
         var1.drawRoundedRect(f4 - f6 / 2.0F, f5 - f6 / 2.0F, f6, f6, CornerRadius.MovementInputEvent(9.0F + f1 * 2.0F), i11ii1llliilllii1i1);
         if (li1ll1i111l1l1iilli1111il != null) {
            float f7 = 31.0F + f1 * 4.0F;
            var1.drawTexture(li1ll1i111l1l1iilli1111il.icon(), f4 - f7 / 2.0F, f5 - f7 / 2.0F, f7, f7, ArgbColor.var11934.SprintStateEvent(var4));
         } else {
            float f8 = 5.0F + f1 * 1.5F;
            ArgbColor i11ii1llliilllii1i11 = var3.getTextTertiary().getColor().SprintStateEvent(var4 * (0.62F + f1 * 0.38F));
            var1.drawRoundedRect(f4 - f8, f5 - 0.75F, f8 * 2.0F, 1.5F, CornerRadius.MovementInputEvent(0.75F), i11ii1llliilllii1i11);
            var1.drawRoundedRect(f4 - 0.75F, f5 - f8, 1.5F, f8 * 2.0F, CornerRadius.MovementInputEvent(0.75F), i11ii1llliilllii1i11);
         }
      }
   }

   public void Easing(HudDrawContext var1, Emotes.WheelBounds var2, ZenithStyle var3, float var4) {
      float f = 36.0F;
      ArgbColor i11ii1llliilllii1i1 = var3.getLeftBackground().getColor().SprintStateEvent(var4 * 0.97F);
      ArgbColor i11ii1llliilllii1i11 = var3.getTextEnable().getColor().SprintStateEvent(var4);
      ArgbColor i11ii1llliilllii1i12 = var3.getTextSecondary().getColor().SprintStateEvent(var4);
      var1.drawRoundedRect(var2.float121() - f, var2.float122() - f, f * 2.0F, f * 2.0F, CornerRadius.MovementInputEvent(f), i11ii1llliilllii1i1);
      Font font = Fonts.NEW_SEMIBOLD.getFont(9.0F);
      String s = Integer.toString(this.page + 1);
      var1.drawText(font, s, var2.float121() - font.width(s) / 2.0F, var2.float122() - font.height() / 2.0F - 3.0F, i11ii1llliilllii1i11);
      Font font1 = Fonts.NEW_MEDIUM.getFont(4.8F);
      String s1 = "СТРАНИЦА";
      var1.drawText(font1, s1, var2.float121() - font1.width(s1) / 2.0F, var2.float122() + 9.0F, i11ii1llliilllii1i12);
      Font font2 = Fonts.NEW_SEMIBOLD.getFont(7.0F);
      ArgbColor i11ii1llliilllii1i13 = this.page > 0 ? i11ii1llliilllii1i11 : i11ii1llliilllii1i12.SprintStateEvent(0.45F);
      ArgbColor i11ii1llliilllii1i14 = this.page < 14 ? i11ii1llliilllii1i11 : i11ii1llliilllii1i12.SprintStateEvent(0.45F);
      var1.drawText(font2, "‹", var2.float121() - 24.0F - font2.width("‹") / 2.0F, var2.float122() - font2.height() / 2.0F, i11ii1llliilllii1i13);
      var1.drawText(font2, "›", var2.float121() + 24.0F - font2.width("›") / 2.0F, var2.float122() - font2.height() / 2.0F, i11ii1llliilllii1i14);
   }

   public void ColorAnimator(HudDrawContext var1, Emotes.WheelBounds var2, ZenithStyle var3, float var4) {
      EmoteMetadata li1ll1i111l1l1iilli1111il = this.BotChatEvent(this.int91);
      String s = this.int91 == -1 ? "Колесо эмоций" : (li1ll1i111l1l1iilli1111il == null ? "Пустой слот" : li1ll1i111l1l1iilli1111il.displayName());
      String s1 = this.int91 == -1
         ? "Колесо мыши — сменить страницу"
         : (li1ll1i111l1l1iilli1111il == null ? "ЛКМ — выбрать эмоцию" : "Отпустите бинт — воспроизвести  •  ПКМ — удалить");
      Font font = Fonts.NEW_SEMIBOLD.getFont(7.5F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      float f = Math.max(font.width(s), font1.width(s1)) + 20.0F;
      float f1 = var2.float121() - f / 2.0F;
      float f2 = var2.float122() - var2.float124() - 38.0F;
      ArgbColor i11ii1llliilllii1i1 = var3.getLeftBackground().getColor().SprintStateEvent(var4 * 0.92F);
      float f3 = val003.NbtEditor().getBlurPower();
      if (f3 > 0.0F) {
         ShapeRenderer.on23(
            var1.getMatrices(), f1, f2, f, 27.0F, f3, CornerRadius.MovementInputEvent(7.0F), ArgbColor.var11934.SprintStateEvent(var4), true, false
         );
      }

      var1.drawRoundedRect(f1, f2, f, 27.0F, CornerRadius.MovementInputEvent(7.0F), i11ii1llliilllii1i1);
      var1.drawText(font, s, var2.float121() - font.width(s) / 2.0F, f2 + 5.0F, var3.getTextEnable().getColor().SprintStateEvent(var4));
      var1.drawText(font1, s1, var2.float121() - font1.width(s1) / 2.0F, f2 + 16.0F, var3.getTextSecondary().getColor().SprintStateEvent(var4));
   }

   public int on23(float var1, float var2, Emotes.WheelBounds var3) {
      float f = var1 - var3.float121();
      float f1 = var2 - var3.float122();
      double d0 = Math.sqrt(f * f + f1 * f1);
      if (!(d0 < var3.float123()) && !(d0 > var3.float124() + 7.0F)) {
         double d1 = Math.atan2(f1, f) + (Math.PI / 2);
         if (d1 < 0.0) {
            d1 += Math.PI * 2;
         }

         return (int)Math.floor(d1 / (Math.PI * 2) * 8.0);
      } else {
         return -1;
      }
   }

   public int UiAnimation(float var1, float var2, Emotes.WheelBounds var3) {
      if (var2 < var3.float122() - 11.0F || var2 > var3.float122() + 11.0F) {
         return 0;
      } else if (var1 >= var3.float121() - 33.0F && var1 <= var3.float121() - 15.0F) {
         return -1;
      } else {
         return var1 >= var3.float121() + 15.0F && var1 <= var3.float121() + 33.0F ? 1 : 0;
      }
   }

   public void ItemRegistry(int var1, boolean var2) {
      if (var1 != 0) {
         int i = this.page + Integer.signum(var1);
         this.page = var2 ? Math.floorMod(i, 15) : Math.clamp(i, 0, 14);
         this.int91 = -1;
      }
   }

   public EmoteMetadata BotChatEvent(int var1) {
      if (var1 >= 0 && var1 < 8) {
         String s = this.val011[this.BotDisconnectEvent(var1)];
         return s == null ? null : EmoteRegistry.Event18Ext5(s).orElse(null);
      } else {
         return null;
      }
   }

   public int BotDisconnectEvent(int var1) {
      return this.page * 8 + var1;
   }

   public void BotWorldJoinEvent(int var1) {
      this.call023();
      minecraftClient3.setScreen(new EmoteFavoriteScreen(this, var1));
   }

   public void UiAnimation(int var1, String var2) {
      if (var1 >= 0 && var1 < this.val011.length && !EmoteRegistry.map56().stream().noneMatch(var2::equals)) {
         for (int i = 0; i < this.val011.length; i++) {
            if (var2.equals(this.val011[i])) {
               this.val011[i] = null;
            }
         }

         this.val011[var1] = var2;
      }
   }

   public void BotPacketEvent(int var1) {
      if (var1 >= 0 && var1 < this.val011.length) {
         this.val011[var1] = null;
      }
   }

   public String BotRespawnEvent(int var1) {
      return var1 >= 0 && var1 < this.val011.length ? this.val011[var1] : null;
   }

   @Override
   public JsonObject save() {
      JsonObject jsonobject = super.save();
      JsonArray jsonarray = new JsonArray();

      for (String s : this.val011) {
         jsonarray.add((JsonElement)(s == null ? JsonNull.INSTANCE : new JsonPrimitive(s)));
      }

      jsonobject.add("FavoriteEmotes", jsonarray);
      return jsonobject;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 != null && var1.has("FavoriteEmotes") && var1.get("FavoriteEmotes").isJsonArray()) {
         JsonArray jsonarray = var1.getAsJsonArray("FavoriteEmotes");
         Arrays.fill(this.val011, null);

         for (int i = 0; i < jsonarray.size() && i < this.val011.length; i++) {
            JsonElement jsonelement = jsonarray.get(i);
            if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isString()) {
               String s = jsonelement.getAsString();
               if (EmoteRegistry.Event05(s)) {
                  this.val011[i] = s;
               }
            }
         }
      }
   }

   public Emotes.WheelBounds double28() {
      float f = Math.min(minecraftClient3.getWindow().getScaledWidth(), minecraftClient3.getWindow().getScaledHeight());
      float f1 = Math.min(1.0F, f / 270.0F);
      float f2 = minecraftClient3.getWindow().getScaledWidth() / 2.0F;
      float f3 = minecraftClient3.getWindow().getScaledHeight() / 2.0F;
      return new Emotes.WheelBounds(f2, f3, 54.0F * f1, 104.0F * f1);
   }

   public float call158() {
      return (float)(minecraftClient3.mouse.getX() * minecraftClient3.getWindow().getScaledWidth() / minecraftClient3.getWindow().getWidth());
   }

   public float call159() {
      return (float)(minecraftClient3.mouse.getY() * minecraftClient3.getWindow().getScaledHeight() / minecraftClient3.getWindow().getHeight());
   }

   public void CloudPoller(boolean var1) {
      if (minecraftClient3.mouse != null) {
         if (var1 && !this.boolean14) {
            minecraftClient3.mouse.unlockCursor();
            this.boolean14 = true;
         } else if (!var1 && this.boolean14) {
            if (minecraftClient3.currentScreen == null) {
               minecraftClient3.mouse.lockCursor();
            }

            this.boolean14 = false;
         }
      }
   }

   public static float BotTickEvent(int var0) {
      return (float)((-Math.PI / 2) + (Math.PI * 2) * ((var0 + 0.5) / 8.0));
   }


   public record WheelBounds(float float121, float float122, float float123, float float124) {
      public float centerX() {
         return this.float121;
      }

      public float centerY() {
         return this.float122;
      }

      public float double29() {
         return this.float123;
      }

      public float double30() {
         return this.float124;
      }
   }
}
