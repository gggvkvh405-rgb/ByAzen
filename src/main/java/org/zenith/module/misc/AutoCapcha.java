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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.EntityPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState.UpdateData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import org.zenith.ZenithClient;
import org.zenith.core.CloudApiClient;
import org.zenith.core.FileLogger;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.PacketEvent;
import org.zenith.utility.mixin.accessors.ItemFrameEntityAccessor;

@ModuleInfo(name = "AutoCapcha", category = Category.MISC, description = "Автоматически решает капчу с карт через Zenith Cloud")
public final class AutoCapcha extends Module {
   public static final AutoCapcha autoCapcha = new AutoCapcha();
   public static final int int15 = 128;
   public static final int int16 = 250;
   public static final int int17 = 150;
   public static final int int18 = 3;
   public static final long long7 = 15000L;
   public static final long long8 = 10000L;
   public static final long long9 = 700L;
   public static final long long10 = 5000L;
   public static final long long11 = 6000L;
   public static final String[] val486 = new String[0];
   public static final String[] val487 = new String[0];
   public static final ExecutorService executorService = Executors.newSingleThreadExecutor();
   public final Map<Integer, int[]> map6 = new ConcurrentHashMap<>();
   public final Map<Integer, double[]> map7 = new ConcurrentHashMap<>();
   public final Map<Integer, Integer> map8 = new ConcurrentHashMap<>();
   public final Map<Integer, Integer> map9 = new ConcurrentHashMap<>();
   public final AtomicBoolean atomicBoolean = new AtomicBoolean();
   public final AtomicBoolean atomicBoolean2 = new AtomicBoolean();
   public final AtomicLong atomicLong = new AtomicLong();
   public final Object object = new Object();
   public volatile double[] look;
   public volatile long joinTime;
   public volatile long keywordTime;
   public volatile long lastTileTime;
   public volatile long lastAttemptTime;
   public volatile int lastMapId = -1;
   public volatile int lastAttemptHash;

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows()) {
         Packet packet = var1.ItemScroller();
         AutoCapcha.RecognitionTask l1i111illi1i1_ii1il11l111ii11iil = this.int347();
         if (packet instanceof GameJoinS2CPacket || packet instanceof PlayerRespawnS2CPacket) {
            this.resetState(true);
            this.log("join detected, armed");
         } else if (packet instanceof PlayerPositionLookS2CPacket playerpositionlooks2cpacket) {
            EntityPosition playerposition = playerpositionlooks2cpacket.change();
            this.look = new double[]{
               playerposition.position().x, playerposition.position().y, playerposition.position().z, playerposition.yaw()
            };
         } else {
            if (packet instanceof EntitySpawnS2CPacket entityspawns2cpacket
               && (entityspawns2cpacket.getEntityType() == EntityType.ITEM_FRAME || entityspawns2cpacket.getEntityType() == EntityType.GLOW_ITEM_FRAME)) {
               this.map7
                  .put(
                     entityspawns2cpacket.getEntityId(),
                     new double[]{
                        entityspawns2cpacket.getX(),
                        entityspawns2cpacket.getY(),
                        entityspawns2cpacket.getZ(),
                        entityspawns2cpacket.getEntityData()
                     }
                  );
               return;
            }

            if (packet instanceof EntityTrackerUpdateS2CPacket entitytrackerupdates2cpacket) {
               this.on23(entitytrackerupdates2cpacket, l1i111illi1i1_ii1il11l111ii11iil);
               return;
            }

            if (packet instanceof MapUpdateS2CPacket mapupdates2cpacket) {
               this.on23(mapupdates2cpacket.mapId().id(), (UpdateData)mapupdates2cpacket.updateData().orElse(null), l1i111illi1i1_ii1il11l111ii11iil);
               return;
            }

            String s = packetText(packet);
            if (s != null) {
               this.on23(s, l1i111illi1i1_ii1il11l111ii11iil);
            }
         }
      }
   }

   public void on23(int var1, UpdateData var2, AutoCapcha.RecognitionTask var3) {
      if (var2 != null && var2.width() > 0 && var2.height() > 0) {
         byte[] abyte = var2.colors();
         int i = var2.width();
         int j = var2.height();
         if (abyte.length >= i * j) {
            synchronized (this.object) {
               int[] aint = this.map6.computeIfAbsent(var1, var0 -> new int[16384]);

               for (int k = 0; k < j; k++) {
                  int l = var2.startZ() + k;
                  if (l >= 0 && l < 128) {
                     for (int i1 = 0; i1 < i; i1++) {
                        int j1 = var2.startX() + i1;
                        if (j1 >= 0 && j1 < 128) {
                           aint[j1 + l * 128] = abyte[i1 + k * i] & 255;
                        }
                     }
                  }
               }
            }

            this.lastMapId = var1;
            this.lastTileTime = System.currentTimeMillis();
            this.on23(var3);
         }
      }
   }

   public void on23(BufferedImage var1, AutoCapcha.RecognitionTask var2, long var3) {
      String s = null;

      try {
         int i = 1;

         while (true) {
            label194: {
               label159:
               if (i <= 3) {
                  if (var3 != this.atomicLong.get() || !this.isEnabledRaw() || !var2.isConnected()) {
                     return;
                  }

                  CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
                  if (l1i1iil111il1l1l == null || !l1i1iil111il1l1l.isConnected()) {
                     s = "Cloud is offline";
                     UiAnimation(var2, s);
                     Easing(var2.label() + ": облако недоступно для решения капчи", true);
                     return;
                  }

                  try {
                     UiAnimation(var2, "Cloud captcha attempt " + i + "/3");
                     int j = this.UiAnimation(var2);
                     byte[] abyte = encodePng(var1);
                     String s1 = l1i1iil111il1l1l.on23(abyte, j, j).join();
                     if (isSafeAnswer(s1)) {
                        String s2 = s1.trim();
                        if (j > 0 && s2.length() != j) {
                           s = "answer length " + s2.length() + " != " + j;
                           UiAnimation(var2, s + ": " + FileLogger.trim(s2));
                           Thread.sleep(1000L * i);
                           break label194;
                        }

                        if (this.on23(var2, s2)) {
                           UiAnimation(var2, "answer sent: " + s2);
                           Easing(var2.label() + ": капча решена -> " + s2, false);
                           this.resetState(false);
                        } else {
                           Easing(var2.label() + ": не удалось отправить ответ капчи", true);
                        }
                        break;
                     }

                     if (s1 != null && !s1.isBlank()) {
                        s = "Cloud returned an unsafe answer";
                        UiAnimation(var2, s + ": " + FileLogger.trim(s1));
                        break label159;
                     }

                     UiAnimation(var2, "Cloud returned no answer on attempt " + i);
                  } catch (InterruptedException interruptedexception) {
                     Thread.currentThread().interrupt();
                     return;
                  } catch (Exception exception) {
                     s = rootMessage(exception);
                     UiAnimation(var2, "Cloud attempt " + i + " failed: " + s);
                  }

                  Thread.sleep(1000L * i);
                  break label194;
               }

               Easing(var2.label() + ": капча не решена" + (s == null ? "" : " (" + s + ")"), true);
               return;
            }

            i++;
         }
      } catch (InterruptedException interruptedexception1) {
         Thread.currentThread().interrupt();
         return;
      } finally {
         this.lastAttemptTime = System.currentTimeMillis();
         this.atomicBoolean2.set(false);
      }
   }

   public AutoCapcha.ImageRegion int346() {
      Map<Long, List<int[]>> hashmap = new HashMap<>();

      for (Entry<Integer, Integer> entry : this.map8.entrySet()) {
         double[] adouble = this.map7.get(entry.getKey());
         if (adouble != null && this.map6.containsKey(entry.getValue())) {
            int i = (int)adouble[3];
            if (i >= 2 && i <= 5) {
               double d0 = i >= 4 ? adouble[0] : adouble[2];
               long j = (long)i << 40 ^ Math.round(d0 * 4.0) & 1099511627775L;
               hashmap.computeIfAbsent(j, var0 -> new ArrayList<>()).add(new int[]{entry.getKey(), entry.getValue()});
            }
         }
      }

      List<int[]> list = this.choosePlane(hashmap);
      if (list != null && !list.isEmpty()) {
         double[] adouble3 = this.map7.get(list.getFirst()[0]);
         if (adouble3 == null) {
            return null;
         }

         int l1 = (int)adouble3[3];
         boolean flag = l1 >= 4;
         double d5 = Double.MAX_VALUE;
         double d6 = -Double.MAX_VALUE;
         double d1 = Double.MAX_VALUE;
         double d2 = -Double.MAX_VALUE;

         for (int[] aint : list) {
            double[] adouble1 = this.map7.get(aint[0]);
            if (adouble1 != null) {
               double d3 = flag ? adouble1[2] : adouble1[0];
               d5 = Math.min(d5, d3);
               d6 = Math.max(d6, d3);
               d1 = Math.min(d1, adouble1[1]);
               d2 = Math.max(d2, adouble1[1]);
            }
         }

         int i2 = (int)Math.round(d6 - d5) + 1;
         int j2 = (int)Math.round(d2 - d1) + 1;
         if (i2 >= 1 && j2 >= 1 && i2 <= 8 && j2 <= 8 && i2 * j2 >= 2) {
            BufferedImage bufferedimage = new BufferedImage(i2 * 128, j2 * 128, 1);
            Graphics2D graphics2d = bufferedimage.createGraphics();
            graphics2d.setColor(Color.WHITE);
            graphics2d.fillRect(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight());
            int k = 1;
            int l = 0;

            for (int[] aint1 : list) {
               int[] aint2 = this.cloneBuffer(aint1[1]);
               double[] adouble2 = this.map7.get(aint1[0]);
               if (aint2 != null && adouble2 != null) {
                  int i1 = this.map9.getOrDefault(aint1[0], 0) & 3;
                  aint2 = rotateClockwise(aint2, i1);
                  double d4 = flag ? adouble2[2] : adouble2[0];

                  int j1 = switch (l1) {
                     case 3, 4 -> (int)Math.round(d4 - d5);
                     default -> (int)Math.round(d6 - d4);
                  };
                  int k1 = (int)Math.round(d2 - adouble2[1]);
                  if (j1 >= 0 && j1 < i2 && k1 >= 0 && k1 < j2) {
                     graphics2d.drawImage(render(aint2), j1 * 128, k1 * 128, null);
                     k = k * 31 + aint1[1];
                     k = k * 31 + Arrays.hashCode(aint2);
                     k = k * 31 + j1 * 17 + k1;
                     l++;
                  }
               }
            }

            graphics2d.dispose();
            return l == 0 ? null : new AutoCapcha.ImageRegion(bufferedimage, k, i2, j2, l);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public List<int[]> choosePlane(Map<Long, List<int[]>> var1) {
      if (var1.isEmpty()) {
         return null;
      }

      List<int[]> list = null;
      double d0 = -1.0;
      double[] adouble = this.look;
      if (adouble != null) {
         double d1 = Math.toRadians(adouble[3]);
         double d2 = -Math.sin(d1);
         double d3 = Math.cos(d1);

         for (List<int[]> list2 : var1.values()) {
            double d4 = 0.0;
            double d5 = 0.0;
            int i = 0;

            for (int[] aint : list2) {
               double[] adouble1 = this.map7.get(aint[0]);
               if (adouble1 != null) {
                  d4 += adouble1[0];
                  d5 += adouble1[2];
                  i++;
               }
            }

            if (i != 0) {
               d4 /= i;
               d5 /= i;
               double d9 = d4 - adouble[0];
               double d10 = d5 - adouble[2];
               double d6 = Math.sqrt(d9 * d9 + d10 * d10);
               if (!(d6 < 0.01) && !(d6 > 32.0)) {
                  double d7 = d9 / d6 * d2 + d10 / d6 * d3;
                  double d8 = d7 < 0.2 ? -1.0 : d7 * 100.0 / (1.0 + d6);
                  if (d8 > d0) {
                     d0 = d8;
                     list = list2;
                  }
               }
            }
         }
      }

      if (list == null) {
         for (List<int[]> list1 : var1.values()) {
            if (list == null || list1.size() > list.size()) {
               list = list1;
            }
         }
      }

      return list;
   }

   public void resetState(boolean var1) {
      this.atomicLong.incrementAndGet();
      synchronized (this.object) {
         this.map6.clear();
      }

      this.map7.clear();
      this.map8.clear();
      this.map9.clear();
      this.atomicBoolean.set(false);
      this.atomicBoolean2.set(false);
      this.look = null;
      this.lastMapId = -1;
      this.lastAttemptHash = 0;
      this.lastAttemptTime = 0L;
      this.lastTileTime = System.currentTimeMillis();
      this.keywordTime = 0L;
      this.joinTime = var1 ? System.currentTimeMillis() : 0L;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetState(true);
      this.log("enabled and armed");
   }

   @Override
   public void onDisable() {
      this.resetState(false);
      super.onDisable();
   }

   public void on23(EntityTrackerUpdateS2CPacket var1, AutoCapcha.RecognitionTask var2) {
      for (SerializedEntry serializedentry : var1.trackedValues()) {
         if (serializedentry.value() instanceof ItemStack itemstack) {
            Object var8 = itemstack.get(DataComponentTypes.MAP_ID);
            if (var8 != null) {
               this.map8.put(var1.id(), ((MapIdComponent)var8).id());
               this.lastTileTime = System.currentTimeMillis();
               this.on23(var2);
            }
         } else if (serializedentry.id() == ItemFrameEntityAccessor.getRotationData().id()
            && serializedentry.value() instanceof Integer integer) {
            this.map9.put(var1.id(), integer);
            this.lastTileTime = System.currentTimeMillis();
         }
      }
   }

   public void on23(String var1, AutoCapcha.RecognitionTask var2) {
      if (!var1.isBlank()) {
         String s = var1.toLowerCase(Locale.ROOT);

         for (String s1 : val486) {
            if (s.contains(s1)) {
               this.keywordTime = System.currentTimeMillis();
               this.log("captcha prompt: " + FileLogger.trim(var1));
               this.on23(var2);
               return;
            }
         }

         for (String s2 : val487) {
            if (s.contains(s2)) {
               this.log("captcha accepted: " + FileLogger.trim(var1));
               this.resetState(false);
               return;
            }
         }
      }
   }

   public void on23(AutoCapcha.RecognitionTask var1) {
      if (var1 != null && (this.lastMapId >= 0 || !this.map8.isEmpty())) {
         long i = System.currentTimeMillis();
         boolean flag = this.joinTime != 0L && i - this.joinTime <= 15000L;
         boolean flag1 = this.keywordTime != 0L && i - this.keywordTime <= 10000L;
         if ((flag || flag1) && this.atomicBoolean.compareAndSet(false, true)) {
            long j = this.atomicLong.get();
            executorService.submit(() -> {
               try {
                  long ix;
                  while (j == this.atomicLong.get() && (ix = System.currentTimeMillis() - this.lastTileTime) < 700L) {
                     Thread.sleep(700L - ix);
                  }

                  if (j == this.atomicLong.get()) {
                     this.awaitFullWall(j);
                     this.on23(var1, j);
                  }
               } catch (InterruptedException interruptedexception) {
                  Thread.currentThread().interrupt();
               } finally {
                  this.atomicBoolean.set(false);
               }
            });
         }
      }
   }

   public void awaitFullWall(long var1) throws InterruptedException {
      long i = System.currentTimeMillis() + 5000L;

      while (var1 == this.atomicLong.get() && System.currentTimeMillis() < i) {
         AutoCapcha.ImageRegion l1i111illi1i1_l1i1illlili = this.int346();
         if (l1i111illi1i1_l1i1illlili == null || l1i111illi1i1_l1i1illlili.tiles() >= l1i111illi1i1_l1i1illlili.cols() * l1i111illi1i1_l1i1illlili.rows()) {
            return;
         }

         Thread.sleep(250L);
      }
   }

   public void on23(AutoCapcha.RecognitionTask var1, long var2) {
      if (var2 == this.atomicLong.get() && this.isEnabledRaw() && this.atomicBoolean2.compareAndSet(false, true)) {
         AutoCapcha.ImageRegion l1i111illi1i1_l1i1illlili = this.int346();
         BufferedImage bufferedimage;
         int i;
         if (l1i111illi1i1_l1i1illlili != null) {
            bufferedimage = l1i111illi1i1_l1i1illlili.image();
            i = l1i111illi1i1_l1i1illlili.hash();
            UiAnimation(
               var1,
               "prepared wall " + l1i111illi1i1_l1i1illlili.cols() + "x" + l1i111illi1i1_l1i1illlili.rows() + " tiles=" + l1i111illi1i1_l1i1illlili.tiles()
            );
         } else {
            int[] aint = this.cloneBuffer(this.lastMapId);
            if (aint == null || isUniform(aint)) {
               this.atomicBoolean2.set(false);
               return;
            }

            bufferedimage = render(aint);
            i = Arrays.hashCode(aint);
            UiAnimation(var1, "prepared map " + this.lastMapId);
         }

         long j = System.currentTimeMillis();
         if (i == this.lastAttemptHash && j - this.lastAttemptTime < 6000L) {
            this.atomicBoolean2.set(false);
         } else {
            this.lastAttemptHash = i;
            BufferedImage bufferedimage1 = resizeForApi(bufferedimage);
            executorService.submit(() -> this.on23(bufferedimage1, var1, var2));
         }
      }
   }

   public int UiAnimation(AutoCapcha.RecognitionTask var1) {
      ClientPlayNetworkHandler clientplaynetworkhandler = var1.int348();
      return clientplaynetworkhandler != null
            && clientplaynetworkhandler.getServerInfo() != null
            && clientplaynetworkhandler.getServerInfo().address.toLowerCase(Locale.ROOT).contains("funtime.su")
         ? 5
         : 0;
   }

   public boolean on23(AutoCapcha.RecognitionTask var1, String var2) {
      ClientPlayNetworkHandler clientplaynetworkhandler = var1.int348();
      if (clientplaynetworkhandler == null) {
         return false;
      }

      MinecraftClient.getInstance().execute(() -> {
         if (var2.startsWith("/")) {
            clientplaynetworkhandler.sendChatCommand(var2.substring(1));
         } else {
            clientplaynetworkhandler.sendChatMessage(var2);
         }

         System.out.println(var2);
      });
      return true;
   }

   public int[] cloneBuffer(int var1) {
      synchronized (this.object) {
         int[] aint = this.map6.get(var1);
         return aint == null ? null : (int[])aint.clone();
      }
   }

   public AutoCapcha.RecognitionTask int347() {
      ClientPlayNetworkHandler clientplaynetworkhandler = MinecraftClient.getInstance().getNetworkHandler();
      return clientplaynetworkhandler == null ? null : new AutoCapcha.RecognitionTask(clientplaynetworkhandler, "main");
   }

   public void log(String var1) {
      FileLogger.log("[main] " + var1);
   }

   public static void UiAnimation(AutoCapcha.RecognitionTask var0, String var1) {
      FileLogger.log("[" + var0.label() + "] " + var1);
   }

   public static String packetText(Packet<?> var0) {
      if (var0 instanceof GameMessageS2CPacket gamemessages2cpacket) {
         return gamemessages2cpacket.content().getString();
      } else if (var0 instanceof ProfilelessChatMessageS2CPacket profilelesschatmessages2cpacket) {
         return profilelesschatmessages2cpacket.message().getString();
      } else if (var0 instanceof ChatMessageS2CPacket chatmessages2cpacket) {
         return chatmessages2cpacket.unsignedContent() == null ? chatmessages2cpacket.body().content() : chatmessages2cpacket.unsignedContent().getString();
      } else if (var0 instanceof OverlayMessageS2CPacket overlaymessages2cpacket) {
         return overlaymessages2cpacket.text().getString();
      } else if (var0 instanceof SubtitleS2CPacket subtitles2cpacket) {
         return subtitles2cpacket.text().getString();
      } else {
         return var0 instanceof TitleS2CPacket titles2cpacket ? titles2cpacket.text().getString() : null;
      }
   }

   public static boolean isSafeAnswer(String var0) {
      if (var0 == null) {
         return false;
      }

      String s = var0.trim();
      return !s.isEmpty() && s.length() <= 64 ? s.codePoints().allMatch(var0x -> Character.isLetterOrDigit(var0x) || var0x == 95 || var0x == 45) : false;
   }

   public static int[] rotateClockwise(int[] var0, int var1) {
      int[] aint = var0;

      for (int i = 0; i < var1; i++) {
         int[] aint1 = new int[16384];

         for (int j = 0; j < 128; j++) {
            for (int k = 0; k < 128; k++) {
               aint1[k + j * 128] = aint[j + (127 - k) * 128];
            }
         }

         aint = aint1;
      }

      return aint;
   }

   public static BufferedImage resizeForApi(BufferedImage var0) {
      if (var0.getWidth() == 250 && var0.getHeight() == 150) {
         return var0;
      }

      BufferedImage bufferedimage = new BufferedImage(250, 150, 1);
      Graphics2D graphics2d = bufferedimage.createGraphics();
      graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      graphics2d.drawImage(var0, 0, 0, 250, 150, null);
      graphics2d.dispose();
      return bufferedimage;
   }

   public static byte[] encodePng(BufferedImage var0) throws Exception {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      if (!ImageIO.write(var0, "png", bytearrayoutputstream)) {
         throw new IllegalStateException("PNG encoder is unavailable");
      } else {
         return bytearrayoutputstream.toByteArray();
      }
   }

   public static String rootMessage(Throwable var0) {
      Throwable throwable = var0;

      while (throwable.getCause() != null && throwable.getCause() != throwable) {
         throwable = throwable.getCause();
      }

      String s = throwable.getMessage();
      return s != null && !s.isBlank() ? s : throwable.getClass().getSimpleName();
   }

   public static BufferedImage render(int[] var0) {
      BufferedImage bufferedimage = new BufferedImage(128, 128, 1);

      for (int i = 0; i < 128; i++) {
         for (int j = 0; j < 128; j++) {
            int k = MapColor.getRenderColor(var0[j + i * 128]);
            if (k >>> 24 == 0) {
               k = 16777215;
            }

            bufferedimage.setRGB(j, i, k & 16777215);
         }
      }

      return bufferedimage;
   }

   public static boolean isUniform(int[] var0) {
      int i = var0[0];

      for (int j : var0) {
         if (j != i) {
            return false;
         }
      }

      return true;
   }

   public static void Easing(String var0, boolean var1) {
      MinecraftClient.getInstance().execute(() -> {
         if (var1) {
            StyledTextBuilder.RotationLegitStrategy(var0);
         } else {
            StyledTextBuilder.RefreshCacheEvent(var0);
         }
      });
   }


   public record ImageRegion(BufferedImage bufferedImage, int int145, int int146, int int147, int int148) {
      public BufferedImage image() {
         return this.bufferedImage;
      }

      public int hash() {
         return this.int145;
      }

      public int cols() {
         return this.int146;
      }

      public int rows() {
         return this.int147;
      }

      public int tiles() {
         return this.int148;
      }
   }

   public record RecognitionTask(ClientPlayNetworkHandler clientPlayNetworkHandler, String string42) {
      public boolean isConnected() {
         return this.clientPlayNetworkHandler != null && this.clientPlayNetworkHandler.getConnection().isOpen();
      }

      public ClientPlayNetworkHandler int348() {
         return this.clientPlayNetworkHandler;
      }

      public String label() {
         return this.string42;
      }
   }
}
