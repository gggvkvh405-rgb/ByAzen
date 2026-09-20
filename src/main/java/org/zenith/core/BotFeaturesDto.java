package org.zenith.core;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public record BotFeaturesDto(int BaseFinder, UUID Bot, UUID ClanUpgrade, CloudResponse CropFarmer, JsonObject WarpFarm) {
   public BotFeaturesDto {
      Objects.requireNonNull(Bot, "id");
      Objects.requireNonNull(CropFarmer, "packet");
      WarpFarm = Objects.requireNonNull(WarpFarm, "payload").deepCopy();
   }

   public String type() {
      return this.CropFarmer.type();
   }

   public boolean MenuEaseF() {
      return this.ClanUpgrade != null;
   }

   public int version() {
      return this.BaseFinder;
   }

   public UUID id() {
      return this.Bot;
   }

   public UUID RotationEasingBase() {
      return this.ClanUpgrade;
   }

   public CloudResponse BotActivity() {
      return this.CropFarmer;
   }

   public JsonObject TaskQueue() {
      return this.WarpFarm;
   }
}
