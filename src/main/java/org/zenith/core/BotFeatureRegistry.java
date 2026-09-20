package org.zenith.core;

import com.google.gson.JsonObject;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.config.CosmeticEntry;
import org.zenith.config.CosmeticManager;

public final class BotFeatureRegistry {
   public final String TargetPearl;
   public final String TrapTp;
   public final Vec3d TriggerBot;
   public final float AutoAccept;
   public final String AutoAuth;
   public final String AutoCapcha;
   public final String AutoCraft;
   public final String AutoDuels;
   public final String AutoLeave;
   public final Vec3d AutoPay;
   public final float AutoRespawn;
   public final float AutoInventory;
   public final boolean AutoTool;
   public final String AutoTrap;
   public final long AutoUse;
   public final int AutoWeb;
   public final UUID BowAimBot;

   public BotFeatureRegistry(
      String var1,
      String var2,
      UUID var3,
      Vec3d var4,
      float var5,
      String var6,
      String var7,
      String var8,
      String var9,
      String var10,
      Vec3d var11,
      float var12,
      float var13,
      boolean var14,
      String var15,
      long var16,
      int var18
   ) {
      this.TargetPearl = var1;
      this.TrapTp = var2;
      this.BowAimBot = var3;
      this.TriggerBot = var4;
      this.AutoAccept = var5;
      this.AutoAuth = var6;
      this.AutoCapcha = var7 == null ? "" : var7;
      this.AutoCraft = var8 == null ? "" : var8;
      this.AutoDuels = var9 == null ? "" : var9;
      this.AutoLeave = var10 == null ? "" : var10;
      this.AutoPay = var11;
      this.AutoRespawn = var12;
      this.AutoInventory = var13;
      this.AutoTool = var14;
      this.AutoTrap = var15 == null ? "" : var15;
      this.AutoUse = Math.max(0L, var16);
      this.AutoWeb = Math.max(0, var18);
   }

   public String PacketReceiveEvent() {
      return this.TargetPearl;
   }

   public String PacketSendEvent() {
      return this.TrapTp;
   }

   public Vec3d VisualSettingsStore() {
      return this.TriggerBot;
   }

   public float Item() {
      return this.AutoAccept;
   }

   public String FriendStore() {
      return this.AutoAuth;
   }

   public String MacroManager() {
      return this.AutoCapcha;
   }

   public String UsageStatStore() {
      return this.AutoCraft;
   }

   public String StaffList() {
      return this.AutoDuels;
   }

   public String ServerConfigStore() {
      return this.AutoLeave;
   }

   public Vec3d ItemStackStore() {
      return this.AutoPay;
   }

   public float DiskStorage() {
      return this.AutoRespawn;
   }

   public float LocaleEntry() {
      return this.AutoInventory;
   }

   public boolean Translator() {
      return this.AutoTool;
   }

   public String ModuleManager() {
      return this.AutoTrap;
   }

   public long CloudApi() {
      return this.AutoUse;
   }

   public int FriendFilter() {
      return this.AutoWeb;
   }

   public UUID uuid() {
      return this.BowAimBot;
   }

   public JsonObject on23(UUID var1, long var2) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("streamId", var1.toString());
      jsonobject.addProperty("seq", var2);
      jsonobject.addProperty("gameServer", this.TargetPearl == null ? "" : this.TargetPearl);
      jsonobject.addProperty("dimension", this.AutoAuth == null ? "" : this.AutoAuth);
      jsonobject.addProperty("modelId", this.AutoCapcha);
      jsonobject.addProperty("weaponModelId", this.AutoCraft);
      jsonobject.addProperty("discordUserId", this.AutoDuels);
      jsonobject.addProperty("health", this.AutoAccept);
      JsonObject jsonobject1 = new JsonObject();
      jsonobject1.addProperty("name", this.TrapTp == null ? "" : this.TrapTp);
      if (this.BowAimBot != null) {
         jsonobject1.addProperty("uuid", this.BowAimBot.toString());
      }

      jsonobject.add("minecraftUser", jsonobject1);
      JsonObject jsonobject2 = new JsonObject();
      jsonobject2.addProperty("x", this.TriggerBot.x);
      jsonobject2.addProperty("y", this.TriggerBot.y);
      jsonobject2.addProperty("z", this.TriggerBot.z);
      jsonobject.add("position", jsonobject2);
      if (!this.AutoLeave.isBlank() || this.AutoPay != null) {
         JsonObject jsonobject3 = new JsonObject();
         jsonobject3.addProperty("modelId", this.AutoLeave);
         jsonobject3.addProperty("scale", Math.max(0.01F, Math.min(100.0F, this.AutoRespawn)));
         jsonobject3.addProperty("yaw", this.AutoInventory);
         jsonobject3.addProperty("sneaking", this.AutoTool);
         if (this.AutoPay != null) {
            JsonObject jsonobject4 = new JsonObject();
            jsonobject4.addProperty("x", this.AutoPay.x);
            jsonobject4.addProperty("y", this.AutoPay.y);
            jsonobject4.addProperty("z", this.AutoPay.z);
            jsonobject3.add("position", jsonobject4);
         }

         jsonobject.add("pet", jsonobject3);
      }

      JsonObject jsonobject5 = new JsonObject();
      jsonobject5.addProperty("id", this.AutoTrap);
      jsonobject5.addProperty("seq", this.AutoUse);
      jsonobject5.addProperty("startTick", this.AutoWeb);
      jsonobject.add("emote", jsonobject5);
      return jsonobject;
   }

   public static BotFeatureRegistry Easing(JsonObject var0) {
      if (var0 == null) {
         return null;
      }

      try {
         JsonObject jsonobject = var0.has("minecraftUser") && var0.get("minecraftUser").isJsonObject()
            ? var0.getAsJsonObject("minecraftUser")
            : new JsonObject();
         JsonObject jsonobject1 = var0.getAsJsonObject("position");
         if (jsonobject1 == null) {
            return null;
         }

         String s = UiAnimation(var0, "gameServer");
         String s1 = UiAnimation(jsonobject, "name");
         UUID uuid = jsonobject.has("uuid") ? UUID.fromString(jsonobject.get("uuid").getAsString()) : new UUID(0L, 0L);
         Vec3d vec3d = ColorAnimator(jsonobject1);
         float f = var0.has("health") ? var0.get("health").getAsFloat() : -1.0F;
         String s2 = UiAnimation(var0, "dimension");
         String s3 = UiAnimation(var0, "modelId");
         String s4 = UiAnimation(var0, "weaponModelId");
         String s5 = UiAnimation(var0, "discordUserId");
         String s6 = "";
         Vec3d vec3d1 = null;
         float f1 = 0.4F;
         float f2 = 0.0F;
         boolean flag = false;
         if (var0.has("pet") && var0.get("pet").isJsonObject()) {
            JsonObject jsonobject2 = var0.getAsJsonObject("pet");
            s6 = UiAnimation(jsonobject2, "modelId");
            f1 = jsonobject2.has("scale") ? jsonobject2.get("scale").getAsFloat() : 0.4F;
            f2 = jsonobject2.has("yaw") ? jsonobject2.get("yaw").getAsFloat() : 0.0F;
            flag = jsonobject2.has("sneaking") && jsonobject2.get("sneaking").getAsBoolean();
            if (jsonobject2.has("position") && jsonobject2.get("position").isJsonObject()) {
               vec3d1 = ColorAnimator(jsonobject2.getAsJsonObject("position"));
            }
         }

         String s7 = "";
         long i = 0L;
         int j = 0;
         if (var0.has("emote") && var0.get("emote").isJsonObject()) {
            JsonObject jsonobject3 = var0.getAsJsonObject("emote");
            s7 = UiAnimation(jsonobject3, "id");
            i = jsonobject3.has("seq") ? Math.max(0L, jsonobject3.get("seq").getAsLong()) : 0L;
            j = jsonobject3.has("startTick") ? Math.max(0, jsonobject3.get("startTick").getAsInt()) : 0;
         }

         return new BotFeatureRegistry(s, s1, uuid, vec3d, f, s2, s3, s4, s5, s6, vec3d1, f1, f2, flag, s7, i, j);
      } catch (RuntimeException runtimeexception) {
         return null;
      }
   }

   public static Vec3d ColorAnimator(JsonObject var0) {
      return new Vec3d(var0.get("x").getAsDouble(), var0.get("y").getAsDouble(), var0.get("z").getAsDouble());
   }

   public static String UiAnimation(JsonObject var0, String var1) {
      return var0.has(var1) && !var0.get(var1).isJsonNull() ? var0.get(var1).getAsString() : "";
   }

   public static BotFeatureRegistry NpcCloneManager() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient != null
         && minecraftclient.player != null
         && minecraftclient.getNetworkHandler() != null
         && minecraftclient.getNetworkHandler().getBrand() != null) {
         String s = ZenithClient.on23().CloudApiClient().getServer();
         String s1 = minecraftclient.player.getGameProfile().name();
         Vec3d vec3d = minecraftclient.player.getEntityPos();
         float f = minecraftclient.player.getHealth() + minecraftclient.player.getAbsorptionAmount();
         String s2 = (
               minecraftclient.player.getEntityWorld() != null ? minecraftclient.player.getEntityWorld().getRegistryKey().getValue().toString() : ""
            )
            + minecraftclient.getNetworkHandler().getBrand().toLowerCase();
         String s3 = ZenithClient.on23().EnchantItemSpec().FireWorkESP();
         String s4 = ZenithClient.on23().EnchantItemSpec().HandFire();
         String s5 = ZenithClient.on23().PotionItemBuilder().getInfo().userId();
         String s6 = ZenithClient.on23().ItemServiceBase().Pathfinder();
         Vec3d vec3d1 = ZenithClient.on23().ItemServiceBase().AimUtils();
         float f1 = ZenithClient.on23().ItemServiceBase().TickGate();
         float f2 = ZenithClient.on23().ItemServiceBase().MovementUtils();
         boolean flag = ZenithClient.on23().ItemServiceBase().EffectEngine();
         CosmeticEntry illlillllllliili1li11i11lill_l1i1illlili = CosmeticEntry.var15Var143;
         CosmeticManager illlillllllliili1li11i11lill = ZenithClient.on23().SimpleItemBuilder();
         if (illlillllllliili1li11i11lill != null) {
            illlillllllliili1li11i11lill_l1i1illlili = illlillllllliili1li11i11lill.AutoTrap();
         }

         return new BotFeatureRegistry(
            s,
            s1,
            minecraftclient.player.getUuid(),
            vec3d,
            f,
            s2,
            s3,
            s4,
            s5,
            s6,
            vec3d1,
            f1,
            f2,
            flag,
            illlillllllliili1li11i11lill_l1i1illlili.ModuleManager(),
            illlillllllliili1li11i11lill_l1i1illlili.ElytraHelper(),
            illlillllllliili1li11i11lill_l1i1illlili.Emotes()
         );
      } else {
         return null;
      }
   }
}
