package org.zenith.hud;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector2f;
import org.zenith.ZenithClient;
import org.zenith.core.ClientProvider;
import org.zenith.event.EventMouseButton;
import org.zenith.module.render.Interface;
import org.zenith.setting.SettingGroup;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.Setting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.HudDrawContext;
import org.zenith.utility.render.display.base.RenderMathUtils;

public abstract class HudElement implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final String string90;
   protected float x;
   protected float y;
   public float width;
   public float height;
   protected float float217;
   protected float float218;
   protected float float219 = Float.NaN;
   protected float float220 = Float.NaN;
   protected float float221 = -1.0F;
   protected float float222 = -1.0F;
   protected final NumberSetting scale4 = new NumberSetting(
      "module.interface.hudElement.scale", 100.0F, 90.0F, 250.0F, 1.0F, "module.interface.hudElement.scale.desc", "%"
   );
   public static final float float223 = 8.0F;
   public boolean boolean154 = true;
   public HudElement.Anchor var129Var159 = HudElement.Anchor.val015;
   public float float224 = 0.0F;
   public float float225 = 0.0F;

   public void load(JsonObject var1) {
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      if (var1.has("enable")) {
         this.boolean154 = var1.get("enable").getAsBoolean();
      } else if (var1.has("enabled")) {
         this.boolean154 = var1.get("enabled").getAsBoolean();
      }

      if (var1.has("x")) {
         this.x = var1.get("x").getAsFloat();
      }

      if (var1.has("y")) {
         this.y = var1.get("y").getAsFloat();
      }

      if (var1.has("width")) {
         this.width = var1.get("width").getAsFloat();
      }

      if (var1.has("height")) {
         this.height = var1.get("height").getAsFloat();
      }

      if (var1.has("windowWidth")) {
         this.float217 = var1.get("windowWidth").getAsFloat();
      }

      if (var1.has("windowHeight")) {
         this.float218 = var1.get("windowHeight").getAsFloat();
      }

      if (var1.has("offsetX")) {
         this.float224 = var1.get("offsetX").getAsFloat();
         flag1 = true;
      }

      if (var1.has("offsetY")) {
         this.float225 = var1.get("offsetY").getAsFloat();
         flag2 = true;
      }

      if (var1.has("align")) {
         try {
            this.var129Var159 = HudElement.Anchor.valueOf(var1.get("align").getAsString());
            flag = true;
         } catch (IllegalArgumentException illegalargumentexception) {
            this.var129Var159 = HudElement.Anchor.val015;
         }
      }

      if (var1.has("relativeX")) {
         this.float219 = var1.get("relativeX").getAsFloat();
      }

      if (var1.has("relativeY")) {
         this.float220 = var1.get("relativeY").getAsFloat();
      }

      if (var1.has("Settings") && var1.get("Settings").isJsonObject()) {
         JsonObject jsonobject = var1.getAsJsonObject("Settings");

         for (Setting l1illl1lllllll1l1l1l1ili11l1 : this.getSettings()) {
            if (jsonobject.has(l1illl1lllllll1l1l1l1ili11l1.getKey()) || l1illl1lllllll1l1l1l1ili11l1 instanceof SettingGroup) {
               l1illl1lllllll1l1l1l1ili11l1.load(jsonobject);
            }
         }
      }

      if (!flag || !flag1 || !flag2) {
         float f = this.EmotePlayback(this.float217);
         float f1 = this.CancellableEvent(this.float218);
         this.TradeGuardService(f, f1);
      }

      if (!this.priorityBlockingQueue()) {
         this.CloudRouter(this.EmotePlayback(this.float217), this.CancellableEvent(this.float218));
      }

      if (minecraftClient3 != null && minecraftClient3.getWindow() != null) {
         this.ServiceException(minecraftClient3.getWindow().getScaledWidth(), minecraftClient3.getWindow().getScaledHeight());
      }
   }

   public void tick() {
   }

   public boolean on23(EventMouseButton var1) {
      return false;
   }

   public HudElement(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      this.string90 = var1;
      this.x = var2;
      this.y = var3;
      this.float217 = var4;
      this.float218 = var5;
      this.float224 = var6;
      this.float225 = var7;
      this.var129Var159 = var8 != null ? var8 : HudElement.Anchor.val015;
   }

   public float double110() {
      return Math.max(0.01F, this.scale4.getCurrent() / 100.0F);
   }

   public float zClass06744() {
      return this.width * this.double110();
   }

   public float int437() {
      return this.height * this.double110();
   }

   public float blockPos30() {
      return this.x - this.zClass06739();
   }

   public float blockPos31() {
      return this.y - this.int483();
   }

   public float zClass06739() {
      return (this.zClass06744() - this.width) / 2.0F;
   }

   protected float int483() {
      return (this.int437() - this.height) / 2.0F;
   }

   public void on23(HudDrawContext var1) {
      float f = this.double110();
      var1.pushMatrix();
      var1.getMatrices().translate(this.x + this.width / 2.0F, this.y + this.height / 2.0F);
      var1.getMatrices().scale(f, f);
      var1.getMatrices().translate(-this.x - this.width / 2.0F, -this.y - this.height / 2.0F);
      this.on23((CustomDrawContext)var1);
      var1.popMatrix();
   }

   public abstract void on23(CustomDrawContext var1);

   public boolean isEnabled() {
      return this.boolean154;
   }

   public void setEnabled(boolean var1) {
      this.boolean154 = var1;
   }

   public void toggle() {
      this.boolean154 = !this.boolean154;
   }

   public List<Setting> getSettings() {
      List<Setting> arraylist = new ArrayList<>();

      for (Class oclass = this.getClass(); oclass != null && HudElement.class.isAssignableFrom(oclass); oclass = oclass.getSuperclass()) {
         Arrays.stream(oclass.getDeclaredFields()).forEach(var2x -> {
            try {
               var2x.setAccessible(true);
               if (var2x.get(this) instanceof Setting l1illl1lllllll1l1l1l1ili11l1) {
                  arraylist.add(l1illl1lllllll1l1l1l1ili11l1);
               }
            } catch (IllegalAccessException var5) {
            }
         });
         if (oclass == HudElement.class) {
            break;
         }
      }

      return arraylist;
   }

   public boolean ColorAnimator(double var1, double var3) {
      float f = this.blockPos30();
      float f1 = this.blockPos31();
      return var1 >= f && var1 <= f + this.zClass06744() && var3 >= f1 && var3 <= f1 + this.int437();
   }

   public boolean ItemRegistry(double var1, double var3) {
      float f = this.blockPos30();
      float f1 = this.blockPos31();
      float f2 = f + this.zClass06744();
      float f3 = f1 + this.int437();
      return var1 >= f2 - 8.0F && var1 <= f2 + 2.0F && var3 >= f3 - 8.0F && var3 <= f3 + 2.0F;
   }

   protected void UiAnimation(CustomDrawContext var1) {
      float f = 5.5F;
      float f1 = 6.0F;
      ArgbColor i11ii1llliilllii1i1 = new ArgbColor(179, 145, 255, 255);
      var1.drawRoundedBorder(this.x, this.y, this.width, this.height, f, CornerRadius.MovementInputEvent(f1), i11ii1llliilllii1i1);
   }

   public void on23(CustomDrawContext var1, float var2, float var3, Interface var4, float var5, float var6) {
      float f = this.ConfigJsonUtil(var2, var5);
      float f1 = this.CloudResponse(var3, var6);
      float f2 = this.zClass06744();
      float f3 = this.int437();
      Vector2f vector2f = var4.CommandManager(f, f1);
      HudElement.Service i1i1l111li_l1i1illlili = new HudElement.Service(this, vector2f.x, 0.0F);
      HudElement.Service i1i1l111li_l1i1illlili1 = new HudElement.Service(this, vector2f.y, 0.0F);
      Vector2f vector2f1 = var4.CommandManager(f + f2, f1 + f3);
      HudElement.Service i1i1l111li_l1i1illlili2 = new HudElement.Service(this, vector2f1.x, -f2);
      HudElement.Service i1i1l111li_l1i1illlili3 = new HudElement.Service(this, vector2f1.y, -f3);
      Vector2f vector2f2 = var4.CommandManager(f + f2 / 2.0F, f1 + f3 / 2.0F);
      HudElement.Service i1i1l111li_l1i1illlili4 = new HudElement.Service(this, vector2f2.x, -f2 / 2.0F);
      HudElement.Service i1i1l111li_l1i1illlili5 = new HudElement.Service(this, vector2f2.y, -f3 / 2.0F);
      this.x = f + this.zClass06739();
      this.y = f1 + this.int483();
      this.float217 = var5;
      this.float218 = var6;
      this.CloudRouter(var5, var6);
      HudElement.Service i1i1l111li_l1i1illlili6 = this.on23(i1i1l111li_l1i1illlili, i1i1l111li_l1i1illlili2, i1i1l111li_l1i1illlili4);
      HudElement.Service i1i1l111li_l1i1illlili7 = this.on23(i1i1l111li_l1i1illlili1, i1i1l111li_l1i1illlili3, i1i1l111li_l1i1illlili5);
      this.on23(var1, i1i1l111li_l1i1illlili6);
      this.UiAnimation(var1, i1i1l111li_l1i1illlili7);
   }

   public HudElement.Service on23(HudElement.Service var1, HudElement.Service var2, HudElement.Service var3) {
      if (var1.float26 != -1.0F) {
         return var1;
      } else {
         return var2.float26 != -1.0F ? var2 : var3;
      }
   }

   protected void UiAnimation(CustomDrawContext var1, HudElement.Service var2) {
      if (var2.float26 == -1.0F) {
         this.float222 = var2.float26;
      } else {
         float f = this.float217 > 0.0F ? this.float217 : var1.getScaledWindowWidth();
         var1.drawRoundedRect(
            this.TradeGuardService(f),
            var2.float26,
            this.CommandManager(f),
            1.0F,
            CornerRadius.var159,
            ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor()
         );
         this.float222 = var2.float26 + var2.float27;
      }
   }

   protected void on23(CustomDrawContext var1, HudElement.Service var2) {
      if (var2.float26 == -1.0F) {
         this.float221 = var2.float26;
      } else {
         float f = this.float218 > 0.0F ? this.float218 : var1.getScaledWindowHeight();
         var1.drawRoundedRect(
            var2.float26,
            this.ModuleStateStore(f),
            1.0F,
            this.EmoteMetadata(f),
            CornerRadius.var159,
            ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor()
         );
         this.float221 = var2.float26 + var2.float27;
      }
   }

   public void on23(float var1, float var2) {
      this.x = var1;
      this.y = var2;
      float f = this.EmotePlayback(this.float217);
      float f1 = this.CancellableEvent(this.float218);
      if (f > 0.0F && f1 > 0.0F) {
         this.ProtocolMessage(f, f1);
         this.TradeGuardService(f, f1);
      }

      this.CloudRouter(f, f1);
   }

   public void ServiceException(float var1, float var2) {
      if (!(var2 <= 0.0F) && !(var1 <= 0.0F)) {
         if (!this.priorityBlockingQueue()) {
            float f = this.float217 > 0.0F ? this.float217 : var1;
            float f1 = this.float218 > 0.0F ? this.float218 : var2;
            this.CloudRouter(f, f1);
         }

         this.float217 = var1;
         this.float218 = var2;
         float f4;
         float f5;
         if (this.priorityBlockingQueue()) {
            f4 = this.TradeGuardService(var1) + this.float219 * this.CommandManager(var1);
            f5 = this.ModuleStateStore(var2) + this.float220 * this.EmoteMetadata(var2);
         } else if (this.var129Var159 != null) {
            float f2 = this.on23(this.var129Var159, var1) + this.float224;
            float f3 = this.UiAnimation(this.var129Var159, var2) + this.float225;
            f4 = f2 - this.zClass06739();
            f5 = f3 - this.int483();
         } else {
            f4 = this.blockPos30();
            f5 = this.blockPos31();
         }

         this.x = this.ConfigJsonUtil(f4, var1) + this.zClass06739();
         this.y = this.CloudResponse(f5, var2) + this.int483();
      }
   }

   public void ProtocolMessage(float var1, float var2) {
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         float f = this.ConfigJsonUtil(this.blockPos30(), var1);
         float f1 = this.CloudResponse(this.blockPos31(), var2);
         this.x = f + this.zClass06739();
         this.y = f1 + this.int483();
      }
   }

   public void double109() {
      if (this.float221 != -1.0F) {
         this.x = this.float221 + this.zClass06739();
      }

      if (this.float222 != -1.0F) {
         this.y = this.float222 + this.int483();
      }

      float f = this.EmotePlayback(this.float217);
      float f1 = this.CancellableEvent(this.float218);
      if (f > 0.0F && f1 > 0.0F) {
         this.ProtocolMessage(f, f1);
         this.TradeGuardService(f, f1);
      }

      this.float221 = -1.0F;
      this.float222 = -1.0F;
      this.CloudRouter(f, f1);
   }

   public HudElement.Anchor TradeGuardService(float var1, float var2, float var3, float var4) {
      float f = this.TradeGuardService(var3);
      float f1 = this.ModuleStateStore(var4);
      float f2 = this.CommandManager(var3);
      float f3 = this.EmoteMetadata(var4);
      float f4 = var1 - this.zClass06739();
      float f5 = var2 - this.int483();
      float f6 = f4 + this.zClass06744() / 2.0F;
      float f7 = f5 + this.int437() / 2.0F;
      boolean flag = f6 < f + f2 / 3.0F;
      boolean flag1 = f6 > f + f2 * 2.0F / 3.0F;
      boolean flag2 = !flag && !flag1;
      boolean flag3 = f7 < f1 + f3 / 3.0F;
      boolean flag4 = f7 > f1 + f3 * 2.0F / 3.0F;
      boolean flag5 = !flag3 && !flag4;
      if (flag3) {
         if (flag) {
            return HudElement.Anchor.val015;
         } else {
            return flag2 ? HudElement.Anchor.val292 : HudElement.Anchor.val093;
         }
      } else if (flag5) {
         if (flag) {
            return HudElement.Anchor.val293;
         } else {
            return flag2 ? HudElement.Anchor.val125 : HudElement.Anchor.val126;
         }
      } else if (flag) {
         return HudElement.Anchor.val294;
      } else {
         return flag2 ? HudElement.Anchor.val127 : HudElement.Anchor.val128;
      }
   }

   public float on23(HudElement.Anchor var1, float var2) {
      float f = this.TradeGuardService(var2);
      float f1 = this.BotFeaturesDto(var2);
      float f2 = f + this.CommandManager(var2) / 2.0F;
      float f3 = this.zClass06744();

      float f4 = switch (var1) {
         case val015, val293, val294 -> f;
         case val292, val125, val127 -> f2 - f3 / 2.0F;
         case val093, val126, val128 -> f1 - f3;
      };
      return f4 + this.zClass06739();
   }

   public float UiAnimation(HudElement.Anchor var1, float var2) {
      float f = this.ModuleStateStore(var2);
      float f1 = this.CloudPoller(var2);
      float f2 = f + this.EmoteMetadata(var2) / 2.0F;
      float f3 = this.int437();

      float f4 = switch (var1) {
         case val015, val292, val093 -> f;
         case val293, val125, val126 -> f2 - f3 / 2.0F;
         case val294, val127, val128 -> f1 - f3;
      };
      return f4 + this.int483();
   }

   protected float TradeGuardService(float var1) {
      return 0.0F;
   }

   protected float BotFeaturesDto(float var1) {
      return this.TradeGuardService(var1) + this.CommandManager(var1);
   }

   protected float CommandManager(float var1) {
      return var1;
   }

   protected float ModuleStateStore(float var1) {
      return 0.0F;
   }

   protected float CloudPoller(float var1) {
      return this.ModuleStateStore(var1) + this.EmoteMetadata(var1);
   }

   protected float EmoteMetadata(float var1) {
      return var1;
   }

   public JsonObject save() {
      if (!this.priorityBlockingQueue()) {
         this.CloudRouter(this.EmotePlayback(this.float217), this.CancellableEvent(this.float218));
      }

      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("enable", this.boolean154);
      jsonobject.addProperty("enabled", this.boolean154);
      jsonobject.addProperty("x", this.x);
      jsonobject.addProperty("y", this.y);
      jsonobject.addProperty("width", this.width);
      jsonobject.addProperty("height", this.height);
      jsonobject.addProperty("windowWidth", this.float217);
      jsonobject.addProperty("windowHeight", this.float218);
      jsonobject.addProperty("offsetX", this.float224);
      jsonobject.addProperty("offsetY", this.float225);
      jsonobject.addProperty("align", this.var129Var159.name());
      if (Float.isFinite(this.float219)) {
         jsonobject.addProperty("relativeX", this.float219);
      }

      if (Float.isFinite(this.float220)) {
         jsonobject.addProperty("relativeY", this.float220);
      }

      JsonObject jsonobject1 = new JsonObject();

      for (Setting l1illl1lllllll1l1l1l1ili11l1 : this.getSettings()) {
         l1illl1lllllll1l1l1l1ili11l1.safe(jsonobject1);
      }

      jsonobject.add("Settings", jsonobject1);
      return jsonobject;
   }

   public void AnalyticsTracker(float var1, float var2) {
      this.x = var1;
      this.y = var2;
      float f = this.EmotePlayback(this.float217);
      float f1 = this.CancellableEvent(this.float218);
      if (f > 0.0F && f1 > 0.0F) {
         this.ProtocolMessage(f, f1);
         this.TradeGuardService(f, f1);
      }

      this.CloudRouter(f, f1);
   }

   protected float ConfigJsonUtil(float var1, float var2) {
      float f = this.TradeGuardService(var2);
      float f1 = this.BotFeaturesDto(var2) - this.zClass06744();
      if (f1 < f) {
         f1 = f;
      }

      return Math.max(f, Math.min(var1, f1));
   }

   protected float CloudResponse(float var1, float var2) {
      float f = this.ModuleStateStore(var2);
      float f1 = this.CloudPoller(var2) - this.int437();
      if (f1 < f) {
         f1 = f;
      }

      return Math.max(f, Math.min(var1, f1));
   }

   protected boolean priorityBlockingQueue() {
      return Float.isFinite(this.float219) && Float.isFinite(this.float220);
   }

   protected void CloudRouter(float var1, float var2) {
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         float f = this.CommandManager(var1);
         float f1 = this.EmoteMetadata(var2);
         if (!(f <= 0.0F) && !(f1 <= 0.0F)) {
            float f2 = (this.ConfigJsonUtil(this.blockPos30(), var1) - this.TradeGuardService(var1)) / f;
            float f3 = (this.CloudResponse(this.blockPos31(), var2) - this.ModuleStateStore(var2)) / f1;
            this.float219 = Math.max(0.0F, Math.min(1.0F, f2));
            this.float220 = Math.max(0.0F, Math.min(1.0F, f3));
         }
      }
   }

   protected void TradeGuardService(float var1, float var2) {
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         HudElement.Anchor i1i1l111li_ii1il11l111ii11iil = this.TradeGuardService(this.x, this.y, var1, var2);
         float f = this.on23(i1i1l111li_ii1il11l111ii11iil, var1);
         float f1 = this.UiAnimation(i1i1l111li_ii1il11l111ii11iil, var2);
         this.var129Var159 = i1i1l111li_ii1il11l111ii11iil;
         this.float224 = this.x - f;
         this.float225 = this.y - f1;
      }
   }

   protected float EmoteManager(float var1) {
      return Math.max(0.0F, this.CommandManager(var1) - this.zClass06744());
   }

   protected float CosmeticManager(float var1) {
      return Math.max(0.0F, this.EmoteMetadata(var1) - this.int437());
   }

   protected float EmotePlayback(float var1) {
      if (var1 > 0.0F) {
         return var1;
      } else if (this.float217 > 0.0F) {
         return this.float217;
      } else {
         return minecraftClient3 != null && minecraftClient3.getWindow() != null ? minecraftClient3.getWindow().getScaledWidth() : var1;
      }
   }

   protected float CancellableEvent(float var1) {
      if (var1 > 0.0F) {
         return var1;
      } else if (this.float218 > 0.0F) {
         return this.float218;
      } else {
         return minecraftClient3 != null && minecraftClient3.getWindow() != null ? minecraftClient3.getWindow().getScaledHeight() : var1;
      }
   }

   protected net.minecraft.client.util.math.Vector2f int213() {
      net.minecraft.client.util.math.Vector2f vector2f = RenderMathUtils.CloudResponse((float)minecraftClient3.getWindow().getScaleFactor());
      float f = this.double110();
      float f1 = this.x + this.width / 2.0F;
      float f2 = this.y + this.height / 2.0F;
      float f3 = f1 + (vector2f.x() - f1) / f;
      float f4 = f2 + (vector2f.y() - f2) / f;
      return new net.minecraft.client.util.math.Vector2f(f3, f4);
   }

   public String getName() {
      return this.string90;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getWidth() {
      return this.width;
   }

   public float getHeight() {
      return this.height;
   }

   public NumberSetting int171() {
      return this.scale4;
   }


   public static class Service {
      public float float26;
      public float float27;

      public Service(HudElement var1, float var2, float var3) {
         this.float26 = var2;
         this.float27 = var3;
      }

      public float call263() {
         return this.float26;
      }

      public float logger() {
         return this.float27;
      }

      public void Event08(float var1) {
         this.float26 = var1;
      }

      public void BotChatEvent(float var1) {
         this.float27 = var1;
      }
   }

   public enum Anchor {
      val015,
      val292,
      val093,
      val293,
      val125,
      val126,
      val294,
      val127,
      val128;
   }
}
