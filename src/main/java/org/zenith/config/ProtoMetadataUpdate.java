package org.zenith.config;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public class ProtoMetadataUpdate implements ProtocolMessage {
   public final UUID BoatHighJump;
   public final long BoatLongJump;
   public final String ElytraBooster;
   public final String ElytraFly;
   public final String ElytraMotion;
   public final String ElytraTarget;

   public ProtoMetadataUpdate(UUID var1, long var2, String var4, String var5, String var6, String var7) {
      Objects.requireNonNull(var1, "configId");
      if (var2 < 1L) {
         throw new IllegalArgumentException("expectedVersion must be positive");
      }

      this.BoatHighJump = var1;
      this.BoatLongJump = var2;
      this.ElytraBooster = var4;
      this.ElytraFly = var5;
      this.ElytraMotion = var6;
      this.ElytraTarget = var7;
   }

   public ProtoMetadataUpdate(UUID var1, long var2, String var4, String var5, String var6) {
      this(var1, var2, var4, var5, null, var6);
   }

   @Override
   public String type() {
      return "config.metadata.update";
   }

   @Override
   public JsonObject TaskQueue() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("configId", this.BoatHighJump.toString());
      jsonobject.addProperty("expectedVersion", this.BoatLongJump);
      ConfigJsonUtil.on23(jsonobject, "name", this.ElytraBooster);
      ConfigJsonUtil.on23(jsonobject, "serverAddress", this.ElytraFly);
      ConfigJsonUtil.on23(jsonobject, "visibility", this.ElytraTarget);
      if (this.ElytraMotion != null) {
         if (this.ElytraMotion.isBlank()) {
            jsonobject.add("description", JsonNull.INSTANCE);
         } else {
            jsonobject.addProperty("description", this.ElytraMotion.strip());
         }
      }

      return jsonobject;
   }

   public UUID PermissionListCodec() {
      return this.BoatHighJump;
   }

   public long RotationPredictiveStrategy() {
      return this.BoatLongJump;
   }

   public String name() {
      return this.ElytraBooster;
   }

   public String RotationLegitStrategy() {
      return this.ElytraFly;
   }

   public String description() {
      return this.ElytraMotion;
   }

   public String AimPolicyRotationStrategy() {
      return this.ElytraTarget;
   }
}
