package org.zenith.core;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentChanges.Builder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

public class ProfileItemBuilder extends ItemServiceBase {
   public final String ItemStackStore;
   public final String DiskStorage;

   public ProfileItemBuilder(String var1, SoundCueKind var2, String var3) {
      this(var3, var1, var1, var2);
   }

   public ProfileItemBuilder(String var1, String var2, String var3, SoundCueKind var4) {
      super(Items.PLAYER_HEAD.getDefaultStack(), var2, var3, var4);
      this.ItemStackStore = var1;
      this.DiskStorage = this.ItemRegistry(var1);
      Builder builder = ComponentChanges.builder();
      PropertyMap propertymap = new PropertyMap(ImmutableMultimap.of("textures", new Property("textures", var1)));
      ProfileComponent profilecomponent = ProfileComponent.ofStatic(new GameProfile(UUID.randomUUID(), "", propertymap));
      builder.add(DataComponentTypes.PROFILE, profilecomponent);
      ItemStackArgument itemstackargument = new ItemStackArgument(this.PacketReceiveEvent.getRegistryEntry(), builder.build());

      try {
         this.PacketReceiveEvent = itemstackargument.createStack(1, false);
      } catch (CommandSyntaxException var12) {
      }
   }

   public String ItemRegistry(String var1) {
      try {
         String s = new String(Base64.getDecoder().decode(var1));
         int i = s.indexOf("\"url\"");
         if (i != -1) {
            int j = s.indexOf("\"", i + 5);
            if (j != -1) {
               int k = s.indexOf("\"", j + 1);
               if (k != -1) {
                  return s.substring(j + 1, k);
               }
            }
         }
      } catch (Exception var6) {
      }

      return null;
   }

   @Override
   public boolean UiAnimation(ItemStack var1) {
      if (!super.UiAnimation(var1)) {
         return false;
      }

      ProfileComponent profilecomponent = (ProfileComponent)var1.get(DataComponentTypes.PROFILE);
      if (profilecomponent != null) {
         PropertyMap propertymap = profilecomponent.getGameProfile().properties();
         if (propertymap != null && propertymap.containsKey("textures")) {
            for (Property property : propertymap.get("textures")) {
               if (this.DiskStorage != null) {
                  String s = this.ItemRegistry(property.value());
                  if (s != null && s.equals(this.DiskStorage)) {
                     return true;
                  }
               }

               if (property.value().contains(this.ItemStackStore)) {
                  return true;
               }
            }
         }
      }

      NbtComponent nbtcomponent = (NbtComponent)var1.get(DataComponentTypes.CUSTOM_DATA);
      NbtCompound customData = nbtcomponent == null ? null : nbtcomponent.copyNbt();
      if (customData != null && customData.contains("SkullOwner")) {
         String s1 = customData.get("SkullOwner").toString();
         if (this.DiskStorage != null && s1.contains(this.DiskStorage)) {
            return true;
         }

         if (s1.contains(this.ItemStackStore)) {
            return true;
         }
      }

      return false;
   }

   public String BlockInteractEvent() {
      return this.ItemStackStore;
   }
}
