package org.zenith.utility.mixin.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.ZenithClient;
import org.zenith.module.misc.NameProtect;
import org.zenith.module.misc.StreamerMode;
import org.zenith.util.TextUtils;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud {
   @Shadow
   public Text header;
   @Shadow
   public Text footer;
   @Unique
   public Text zenith_savedHeader;
   @Unique
   public Text zenith_savedFooter;

   @Shadow
   protected abstract Text applyGameModeFormatting(PlayerListEntry var1, MutableText var2);

   @Inject(method = "render", at = @At("HEAD"))
   public void zenith_streamerTabStart(CallbackInfo var1) {
      if (TextUtils.isActive()) {
         this.zenith_savedHeader = this.header;
         this.zenith_savedFooter = this.footer;
         this.header = TextUtils.ItemSpec(this.header);
         this.footer = TextUtils.ItemSpec(this.footer);
      }
   }

   @Inject(method = "render", at = @At("RETURN"))
   public void zenith_streamerTabEnd(CallbackInfo var1) {
      if (this.zenith_savedHeader != null || this.zenith_savedFooter != null) {
         this.header = this.zenith_savedHeader;
         this.footer = this.zenith_savedFooter;
         this.zenith_savedHeader = null;
         this.zenith_savedFooter = null;
      }
   }

   @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
   public void getPlayerName(PlayerListEntry var1, CallbackInfoReturnable<Text> var2) {
      if (!NameProtect.nameProtect.isEnabled()
         || !var1.getProfile().equals(MinecraftClient.getInstance().getGameProfile())
            && (!NameProtect.nameProtect.call057() || !ZenithClient.on23().MediaTrackInfo().isFriend(var1.getProfile().name()))) {
         Text text = StreamerMode.streamerMode.ColorAnimator(var1);
         if (text != null) {
            var2.setReturnValue(text);
         }
      } else {
         var2.setReturnValue(
            NameProtect.nameProtect
               .ColorAnimator(
                  var1.getDisplayName() != null
                     ? this.applyGameModeFormatting(var1, var1.getDisplayName().copy())
                     : this.applyGameModeFormatting(var1, Team.decorateName(var1.getScoreboardTeam(), Text.literal(var1.getProfile().name())))
               )
         );
      }
   }

   @Inject(method = "collectPlayerEntries", at = @At("RETURN"), cancellable = true)
   public void collectPlayerEntries(CallbackInfoReturnable<List<PlayerListEntry>> var1) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.player != null) {
         List<PlayerListEntry> list = (List<PlayerListEntry>)var1.getReturnValue();
         if (list != null && !list.isEmpty()) {
            if (StreamerMode.streamerMode.isEnabled()) {
               ArrayList arraylist1 = new ArrayList();

               for (PlayerListEntry playerlistentry1 : list) {
                  if (!playerlistentry1.getProfile().id().equals(minecraftclient.player.getUuid())) {
                     arraylist1.add(playerlistentry1);
                  }
               }

               StreamerMode.streamerMode.ItemSpec(arraylist1);
               var1.setReturnValue(arraylist1);
            } else if (NameProtect.nameProtect.isEnabled()) {
               int i = NameProtect.nameProtect.call209();
               if (i != Integer.MIN_VALUE && list.size() >= 2) {
                  ArrayList arraylist = new ArrayList(list);
                  int j = -1;

                  for (int k = 0; k < arraylist.size(); k++) {
                     if (((PlayerListEntry)arraylist.get(k)).getProfile().id().equals(minecraftclient.player.getUuid())) {
                        j = k;
                        break;
                     }
                  }

                  if (j != -1) {
                     int l = Math.max(0, Math.min(arraylist.size() - 1, i));
                     if (l != j) {
                        PlayerListEntry playerlistentry = (PlayerListEntry)arraylist.remove(j);
                        if (l > j) {
                           l--;
                        }

                        arraylist.add(Math.min(arraylist.size(), l), playerlistentry);
                        var1.setReturnValue(arraylist);
                     }
                  }
               }
            }
         }
      }
   }
}
