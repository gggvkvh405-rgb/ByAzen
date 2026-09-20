package org.zenith.core;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.event.EventMouseButton;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.util.CooldownTimer;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudMediaPanel extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final ExecutorService executorService3 = Executors.newSingleThreadExecutor();
   public final UiAnimation var14349;
   public final CooldownTimer zClass06740;
   public volatile IMediaSession val339;
   public CornerRadiusF val105;
   public static final float float264 = 0.0F;
   public final Identifier identifier10;
   public final UiAnimation var14350;
   public MediaInfo mediaInfo = new MediaInfo("Track Name", "Artist", new byte[0], 43L, 150L, false);
   public CornerRadiusF val106;
   public final UiAnimation var14351;
   public CornerRadiusF val107;

   @Override
   public void on23(CustomDrawContext var1) {
      try {
         this.var14351
            .on23(
               !this.zClass06740.EventModifyMouseRotationInput(2000L)
                  || minecraftClient3.currentScreen instanceof ChatScreen
                  || ZenithClient.on23().NbtEditor().isRenderHud()
            );
         if (this.var14351.CancellableEvent() == 0.0F) {
            return;
         }

         Vector2f vector2f = this.int213();
         this.var14350.on23(this.Easing(vector2f.x(), vector2f.y()));
         float f = this.var14350.CancellableEvent();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.REGULAR.getFont(5.5F);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
         float f1 = 17.0F;
         float f2 = f1 + GuiStyle.PADDING.intValue() * 2.0F;
         float f3 = GuiStyle.PADDING.intValue();
         float f4 = Interface.float212();
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getHudBackground().getColor();
         ArgbColor i11ii1llliilllii1i11 = zenithstyle.getHeaderHudBackground().getColor();
         ArgbColor i11ii1llliilllii1i12 = zenithstyle.getTextEnable().getColor();
         ArgbColor i11ii1llliilllii1i13 = zenithstyle.getTextSecondary().getColor();
         ArgbColor i11ii1llliilllii1i14 = zenithstyle.getFieldBorder().getColor();
         ArgbColor i11ii1llliilllii1i15 = zenithstyle.getPrimaryColor().getColor();
         this.width = 109.0F;
         this.height = 25.0F + 0.0F * f;
         var1.pushMatrix();
         float f5 = this.x + this.width / 2.0F;
         float f6 = this.y + this.height / 2.0F;
         var1.getMatrices().translate(f5, f6);
         var1.getMatrices().scale(this.var14351.CancellableEvent(), this.var14351.CancellableEvent());
         var1.getMatrices().translate(-f5, -f6);
         var1.drawBlurHud(this.x, this.y, this.width, this.height, 21.0F, CornerRadius.MovementInputEvent(f4), ArgbColor.var11934);
         var1.drawRoundedRect(this.x, this.y, this.width, this.height, CornerRadius.MovementInputEvent(f4), i11ii1llliilllii1i1);
         var1.drawRoundedRect(this.x, this.y, f2, 25.0F, CornerRadius.MovementInputEvent(f4), i11ii1llliilllii1i11);
         var1.drawRoundedTexture(this.identifier10, this.x + f3, this.y + f3, f1, f1, CornerRadius.MovementInputEvent(2.0F));
         float f7 = this.x + f2 + f3;
         float f8 = this.x + this.width - f3 - f1;
         float f9 = this.y + (25.0F - f1) / 2.0F;
         float f10 = this.mediaInfo.getDuration() > 0L ? (float)this.mediaInfo.getPosition() / (float)this.mediaInfo.getDuration() : 0.0F;
         this.var14349.on23(f10);
         float f11 = this.var14349.CancellableEvent();
         float f12 = this.y + f3 + f3 / 2.0F;
         float f13 = f12 + font.height() + 0.5F;
         String s = this.ItemRegistry(this.mediaInfo.getPosition());
         float f14 = f8 + (f1 - font2.width(s)) / 2.0F;
         float f15 = Math.max(0.0F, f8 - f7 - GuiStyle.PADDING.intValue());
         this.on23(var1, font, this.mediaInfo.getTitle(), f7, f12, i11ii1llliilllii1i12, f15);
         if (!this.mediaInfo.getArtist().isEmpty()) {
            this.on23(var1, font1, this.mediaInfo.getArtist(), f7, f13 + 1.5F, i11ii1llliilllii1i13, f15);
         }

         var1.drawText(font2, s, f14, f9 + (f1 - font2.height()) / 2.0F, i11ii1llliilllii1i12);
         float f16 = Math.max(0.0F, Math.min(f11, 1.0F));
         var1.drawArcBorder(f8, f9, f1, f1, 1.0F, 360.0F, 0.5F, i11ii1llliilllii1i14);
         var1.drawArcBorder(f8, f9, f1, f1, 1.0F, 360.0F * f16, 0.5F, i11ii1llliilllii1i15);
         this.on23(var1, zenithstyle, vector2f, f);
         var1.popMatrix();
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public HudMediaPanel(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.identifier10 = ZenithClient.on23("icons/avatarmusic.png");
      this.zClass06740 = new CooldownTimer();
      this.var14351 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
      this.var14350 = new UiAnimation(220L, Easing.PreventActionEvent);
      this.var14349 = new UiAnimation(200L, Easing.PreventActionEvent);
   }

   @Override
   public void tick() {
      if (minecraftClient3.player != null && minecraftClient3.player.age % 5 == 0) {
         this.executorService3
            .execute(
               () -> {
                  IMediaSession imediasession;
                  try {
                     imediasession = MediaPlayerInfo.Instance
                        .getMediaSessions()
                        .stream()
                        .max(Comparator.comparing(var0 -> var0.getMedia().getPlaying()))
                        .orElse(null);
                  } catch (Throwable throwable) {
                     imediasession = null;
                  }

                  if (imediasession != null) {
                     MediaInfo mediainfo = imediasession.getMedia();
                     if (!mediainfo.getTitle().isEmpty() || !mediainfo.getArtist().isEmpty()) {
                        byte[] abyte = mediainfo.getArtworkPng();
                        IMediaSession imediasession1 = imediasession;
                        minecraftClient3.execute(() -> {
                           if (this.mediaInfo.getTitle().equals("Track Name") || !Arrays.equals(this.mediaInfo.getArtworkPng(), abyte)) {
                              AvatarRenderer.on23(new TextureIdFactory(this.identifier10), abyte);
                           }

                           this.val339 = imediasession1;
                           this.mediaInfo = mediainfo;
                           this.zClass06740.reset();
                        });
                        return;
                     }
                  }

                  this.val339 = null;
               }
            );
      }
   }

   @Override
   public boolean on23(EventMouseButton var1) {
      if (var1.TridentAimbot() == 1 && var1.ContainerScanner() == 0) {
         Vector2f vector2f = this.int213();
         double d0 = vector2f.x();
         double d1 = vector2f.y();
         if (this.val105 != null && this.val105.PotionItemBuilder(d0, d1)) {
            this.on23(HudMediaMode.val414);
            return true;
         } else if (this.val107 != null && this.val107.PotionItemBuilder(d0, d1)) {
            this.on23(HudMediaMode.val415);
            return true;
         } else if (this.val106 != null && this.val106.PotionItemBuilder(d0, d1)) {
            this.on23(HudMediaMode.val416);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void on23(CustomDrawContext var1, ZenithStyle var2, Vector2f var3, float var4) {
      if (var4 <= 0.001F) {
         this.val105 = null;
         this.val107 = null;
         this.val106 = null;
      } else {
         Font font = Fonts.NEW_ICONS.getFont(5.0F);
         float f = GuiStyle.PADDING.intValue();
         float f1 = GuiStyle.PADDING * 2;
         float f2 = this.width - f1 * 2.0F;
         float f3 = (f2 - f * 2.0F) / 3.0F;
         float f4 = font.height() + f;
         float f5 = this.y + 25.0F + (0.0F - f4) / 2.0F - (1.0F - var4) * 4.0F;
         float f6 = this.x + f1;
         this.val105 = new CornerRadiusF(f6, f5, f3, f4);
         this.val107 = new CornerRadiusF(f6 + f3 + f, f5, f3, f4);
         this.val106 = new CornerRadiusF(f6 + (f3 + f) * 2.0F, f5, f3, f4);
         boolean flag = this.val105.PotionItemBuilder(var3.x(), var3.y());
         boolean flag1 = this.val107.PotionItemBuilder(var3.x(), var3.y());
         boolean flag2 = this.val106.PotionItemBuilder(var3.x(), var3.y());
         ArgbColor i11ii1llliilllii1i1 = var2.getTextEnable().getColor().SprintStateEvent(var4);
         ArgbColor i11ii1llliilllii1i11 = var2.getHeaderHudBackground().getColor().EventTick(0.06F).SprintStateEvent(var4);
         ArgbColor i11ii1llliilllii1i12 = var2.getPrimaryColor().getColor().SprintStateEvent(var4);
         this.on23(var1, font, "y", this.val105, flag, i11ii1llliilllii1i11, i11ii1llliilllii1i12, i11ii1llliilllii1i1);
         this.on23(var1, font, "z", this.val106, flag2, i11ii1llliilllii1i11, i11ii1llliilllii1i12, i11ii1llliilllii1i1);
         this.on23(
            var1,
            Fonts.NEW_ICONS.getFont(5.5F),
            this.mediaInfo.getPlaying() ? "|" : "}",
            this.val107,
            flag1,
            i11ii1llliilllii1i11,
            i11ii1llliilllii1i12,
            i11ii1llliilllii1i1
         );
      }
   }

   public void on23(CustomDrawContext var1, Font var2, String var3, CornerRadiusF var4, boolean var5, ArgbColor var6, ArgbColor var7, ArgbColor var8) {
      float f = var4.x() + (var4.width() - var2.width(var3)) / 2.0F;
      float f1 = var4.y() + (var4.height() - var2.height()) / 2.0F;
      var1.drawText(var2, var3, f, f1, var5 ? var7 : var8);
   }

   public boolean Easing(double var1, double var3) {
      return this.on23(var1, var3, this.x, this.y, this.width, 25.0F)
         ? true
         : this.var14350.CancellableEvent() > 0.01F && this.on23(var1, var3, this.x, this.y, this.width, 25.0F);
   }

   public boolean on23(double var1, double var3, float var5, float var6, float var7, float var8) {
      return var1 >= var5 && var1 <= var5 + var7 && var3 >= var6 && var3 <= var6 + var8;
   }

   public void on23(HudMediaMode var1) {
      IMediaSession imediasession = this.val339;
      if (imediasession != null) {
         this.executorService3.execute(() -> {
            try {
               switch (var1) {
                  case val414:
                     imediasession.previous();
                     break;
                  case val415:
                     imediasession.playPause();
                     break;
                  case val416:
                     imediasession.next();
               }

               this.zClass06740.reset();
            } catch (Throwable var4) {
            }
         });
      }
   }

   public void on23(CustomDrawContext var1, Font var2, String var3, float var4, float var5, ArgbColor var6, float var7) {
      float f = var2.width(var3);
      float f1 = 0.0F;
      if (f > var7) {
         float f2 = f - var7;
         if (f2 < 0.0F) {
            f2 = 0.0F;
         }

         float f3 = 1000.0F;
         float f4 = 4000.0F;
         float f5 = f3 + f4 + f3 + f4;
         long i = System.currentTimeMillis();
         float f6 = (float)(i % (long)f5);
         if (f6 < f3) {
            f1 = 0.0F;
         } else if (f6 < f3 + f4) {
            float f7 = (f6 - f3) / f4;
            f1 = f7 * f2;
         } else if (f6 < f3 + f4 + f3) {
            f1 = f2;
         } else {
            float f8 = (f6 - f3 - f4 - f3) / f4;
            f1 = f2 * (1.0F - f8);
         }
      }

      var1.enableScissor(
         (int)Math.ceil(var4 - 1.0F),
         (int)Math.ceil(var5 - 1.0F),
         (int)Math.ceil(var4 - 1.0F + var7 + 2.0F),
         (int)Math.ceil(var5 - 1.0F + var2.height() + 5.0F)
      );
      var1.drawText(var2, var3.toLowerCase(), var4 - f1, var5, var6);
      var1.disableScissor();
   }

   public String ItemRegistry(long var1) {
      long i = var1 / 60L;
      long j = var1 % 60L;
      return String.format("%d:%02d", i, j);
   }
}
