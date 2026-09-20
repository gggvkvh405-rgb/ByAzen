package org.zenith.core;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.zenith.config.ProtoAuthLogin;
import org.zenith.config.ProtoCaptchaSolve;
import org.zenith.config.ProtoChatDirect;
import org.zenith.config.ProtoChatGlobal;
import org.zenith.config.ProtoChatHistory;
import org.zenith.config.ProtoCodeRedeem;
import org.zenith.config.ProtoCodesListGet;
import org.zenith.config.ProtoConfigCatalogGet;
import org.zenith.config.ProtoConfigCodeRevoke;
import org.zenith.config.ProtoConfigCodesCreate;
import org.zenith.config.ProtoConfigDelete;
import org.zenith.config.ProtoConfigListGet;
import org.zenith.config.ProtoConfigPublish;
import org.zenith.config.ProtoContentUpdateComplete;
import org.zenith.config.ProtoContentUpdateInit;
import org.zenith.config.ProtoDownloadGet;
import org.zenith.config.ProtoFriendAccept;
import org.zenith.config.ProtoFriendDecline;
import org.zenith.config.ProtoFriendRemove;
import org.zenith.config.ProtoFriendRequest;
import org.zenith.config.ProtoFriendsSnapshot;
import org.zenith.config.ProtoHello;
import org.zenith.config.ProtoInventoryUpdate;
import org.zenith.config.ProtoLikeToggle;
import org.zenith.config.ProtoMetadataUpdate;
import org.zenith.config.ProtoPreviewDelete;
import org.zenith.config.ProtoPreviewInit;
import org.zenith.config.ProtoStateUpdate;
import org.zenith.config.ProtoUploadComplete;
import org.zenith.config.ProtoWatchSet;
import org.zenith.config.ProtocolMessage;
import org.zenith.event.EventTick;

public final class CloudApiClient {
   public static final String PetManager = "wss://cloud.zenithdlc.org/ws/v1";
   public static final String HolyWorldClient = "5W9nLU1l3V6SqJMVI4mSViItwLsLOPZMwhMJbdZE2ZhIjNKVf1g3OUtL8mOKBgA6";
   public static final int RotationQueue = 262144;
   public static final int TaskQueue = 307200;
   public static final int RotationEasing = 262144;
   public static final int RotationManager = 512;
   public static final SecureRandom RotationTask = new SecureRandom();
   public static final int MotorPolicyNet = 1024;
   public static final int NeuralProvider = 256;
   public static final int ConfigLoader = 500;
   public static final long NoiseGenerator = 5000L;
   public static final long PermissionListsStore = 15000L;
   public static final long GmmModel = 15L;
   public static final long MotorIntentModel = 130L;
   public static final long PermissionListCodec = 900L;
   public final FriendStore InventoryCodec;
   public final URI GameCoordinator;
   public final Supplier<String> RotationPredictiveStrategy;
   public final HttpClient RotationSmoothStrategy;
   public final ThreadPoolExecutor RotationBurstStrategy;
   public final CloudRouter RotationSnapStrategy;
   public final List<ModuleSnapshotDto> RotationLegitStrategy = new CopyOnWriteArrayList<>();
   public final Map<String, UUID> AimPolicyRotationStrategy = new ConcurrentHashMap<>();
   public final Map<UUID, CompletableFuture<BotFeaturesDto>> MotorIntentRotationStrategy = new ConcurrentHashMap<>();
   public final Map<String, Long> RotationBotStrategy = new ConcurrentHashMap<>();
   public final List<CloudListener> RotationStrategyBase = new CopyOnWriteArrayList<>();
   public final ArrayDeque<BotFeaturesDto> MenuEaseB = new ArrayDeque<>();
   public final AtomicBoolean RoundedRectEasing = new AtomicBoolean();
   public volatile Set<String> MenuEaseD = Set.of();
   public volatile Set<String> MenuEaseA = Set.of();
   public volatile CloudApiClient.ItemSpec MenuEaseE;
   public volatile boolean closed;
   public volatile boolean MenuEaseC;
   public volatile boolean MenuEaseF;
   public volatile boolean RotationEasingBase;
   public volatile boolean BotActivity;
   public volatile long TargetInterpolator;
   public volatile int TrajectoryDataset = 262144;
   public volatile long MovementSimulator;
   public volatile long MotionSampleStore;
   public volatile BotFeatureRegistry RemoteEventsPoller;
   public volatile long ChatTagParser;
   public volatile long ProfileCacheStore;
   public volatile int SoundManager;
   public volatile long ServerTheme;
   public UUID ThemeColorCycler = UUID.randomUUID();
   public long EmoteRegistry;
   public long UserdataManager;
   public boolean ArmorHud;
   public boolean HudHotbarPanel;
   public long HudInventoryPanel;
   public ReconnectBackoff HudElementMessage;

   public CloudApiClient(FriendStore var1, String var2, Supplier<String> var3, long var4) {
      this.InventoryCodec = Objects.requireNonNull(var1, "friendManager");
      this.GameCoordinator = ServiceException(var2);
      this.RotationPredictiveStrategy = Objects.requireNonNull(var3, "accessTokenProvider");
      this.TargetInterpolator = var4;
      this.RotationSmoothStrategy = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).followRedirects(Redirect.NEVER).build();
      this.RotationBurstStrategy = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1024), var0 -> {
         Thread thread = new Thread(var0, "zenith-cloud-protocol");
         thread.setDaemon(true);
         return thread;
      }, new AbortPolicy());
      this.RotationSnapStrategy = this.EventPushOutOfBlocks();
      EventManager.register(this);
      this.EventClick();
   }

   @EventTarget
   public void on23(EventTick var1) {
      if (!this.closed) {
         long i = System.currentTimeMillis();
         if (!this.GuiWalkEvent()) {
            if (i - this.MovementSimulator >= 5000L) {
               this.EventClick();
            }
         } else {
            if (this.ArmorHud && i - this.HudInventoryPanel > 15000L) {
               this.UiAnimation(() -> this.MediaTrackInfo("Friends snapshot timed out"));
            }

            if (this.RotationEasingBase) {
               if (i - this.MotionSampleStore >= 100L) {
                  this.MotionSampleStore = i;
                  BotFeatureRegistry ili1ll11li1ili11l1i1l11l1 = BotFeatureRegistry.NpcCloneManager();
                  if (ili1ll11li1ili11l1i1l11l1 != null) {
                     this.on23(ili1ll11li1ili11l1i1l11l1);
                  }
               }

               if (i - this.ChatTagParser >= 1000L) {
                  this.ChatTagParser = i;
                  InventoryUtils l11illi1i11 = InventoryUtils.GameMessageEvent();
                  if (l11illi1i11 != null) {
                     int j = l11illi1i11.PacketEvent();
                     if (j != this.SoundManager || i - this.ProfileCacheStore >= 5000L) {
                        this.SoundManager = j;
                        this.ProfileCacheStore = i;
                        this.on23(l11illi1i11);
                     }
                  }
               }
            }
         }
      }
   }

   public void on23(CloudApiClient.ItemSpec var1, String var2) {
      this.UiAnimation(() -> {
         if (this.MenuEaseE == var1 && !this.closed) {
            try {
               if (var2.getBytes(StandardCharsets.UTF_8).length > this.TrajectoryDataset) {
                  throw new ServiceException("PAYLOAD_TOO_LARGE", "Incoming Cloud frame is too large", false);
               }

               this.RotationSnapStrategy.InventoryUtils(TradeGuardService.TradeGuardService(var2));
            } catch (RuntimeException runtimeexception) {
               this.on23("BAD_SERVER_PACKET", rootMessage(runtimeexception), false);
               var1.close(1002, "Invalid protocol packet");
            }
         }
      });
   }

   public void on23(CloudApiClient.ItemSpec var1, int var2, String var3) {
      this.UiAnimation(() -> {
         if (this.MenuEaseE == var1) {
            this.MenuEaseE = null;
            this.MenuEaseC = false;
            this.MenuEaseF = false;
            boolean flag = this.RotationEasingBase;
            this.RotationEasingBase = false;
            this.EventHookTickEvent();
            this.RotationBotStrategy.clear();
            this.InventoryCodec.ShaderHand().forEach(CloudUserProfile::EventPosHook);
            this.on23(new CloudApiClient.ClientException("Cloud connection closed: " + var2 + " " + var3));
            if (flag) {
               this.ColorAnimator(false);
            }
         }
      });
   }

   public void on23(CloudApiClient.ItemSpec var1, Exception var2) {
      if (this.MenuEaseE == var1 && !this.closed) {
         if (on23(var2)) {
            this.BotActivity = true;
         }

         this.on23("TRANSPORT_ERROR", rootMessage(var2), true);
      }
   }

   public static boolean on23(Throwable var0) {
      for (Throwable throwable = var0; throwable != null; throwable = throwable.getCause()) {
         if (throwable instanceof ConnectException) {
            return true;
         }

         String s = throwable.getMessage();
         if (s != null && s.toLowerCase(Locale.ROOT).contains("connection refused")) {
            return true;
         }
      }

      return false;
   }

   public CloudRouter EventPushOutOfBlocks() {
      return new CloudRouter(this::on23)
         .on23(CloudServerStatsDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudCodeDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudPlayerInfoDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudPermissionsDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudErrorDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudFriendsDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudRelationWrapDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudAccountDto.class, (var1, var2) -> {
            if (!var1.MenuEaseF()) {
               this.PreventActionEvent();
            }
         })
         .on23(CloudBadgesDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudFeatureDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudMediaEntryDto.class, (var1, var2) -> this.on23(var2.SoundManager()))
         .on23(CloudLogsDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudStatsDto.class, (var1, var2) -> this.on23(var2))
         .on23(CloudAuthAckDto.class, (var1, var2) -> this.on23(var2.userId(), var2.OffHandManager(), true))
         .on23(CloudSessionAckDto.class, (var1, var2) -> this.on23(var2.userId(), var2.OffHandManager(), false))
         .on23(CloudMessageDto.class, (var1, var2) -> {
            if (!var1.MenuEaseF()) {
               this.on23(var2);
            }
         });
   }

   public boolean on23(BotFeaturesDto var1) {
      if (var1.MenuEaseF()) {
         boolean flag = this.UiAnimation(var1);
         if (!flag && var1.BotActivity() instanceof CloudMessageDto l1i1li1i11_li11ii1li11lli1i1liil) {
            this.on23(l1i1li1i11_li11ii1li11lli1i1liil);
         }
      }

      if (this.ArmorHud && Easing(var1)) {
         if (this.MenuEaseB.size() >= 256) {
            throw new ServiceException("SNAPSHOT_OVERFLOW", "Too many friend events during snapshot", false);
         }

         this.MenuEaseB.addLast(var1);
         return false;
      } else {
         return true;
      }
   }

   public CompletableFuture<CloudPairDto> on23(UUID var1, UUID var2) {
      Objects.requireNonNull(var1, "configId");
      Objects.requireNonNull(var2, "codeId");
      return this.on23(new ProtoConfigCodeRevoke(var1, var2)).thenApply(var0 -> on23(var0, CloudPairDto.class));
   }

   public CompletableFuture<CloudFlagsDto> ItemRegistry(UUID var1) {
      Objects.requireNonNull(var1, "configId");
      return this.on23(new ProtoConfigDelete(var1)).thenApply(var0 -> on23(var0, CloudFlagsDto.class));
   }

   public void on23(String var1, boolean var2) {
      Long olong = InventoryUtils(var1);
      if (olong != null) {
         UUID uuid = this.AimPolicyRotationStrategy.get(Long.toString(olong));
         if (uuid == null) {
            this.on23("REQUEST_NOT_FOUND", "Refresh the friends snapshot and try again", false);
            this.PreventActionEvent();
         } else {
            Object object = var2 ? new ProtoFriendAccept(uuid) : new ProtoFriendDecline(uuid);
            this.on23((ProtocolMessage)object).thenAccept(var2x -> {
               this.CloudUserProfile(Long.toString(olong));
               this.PreventActionEvent();
            }).exceptionally(this::UiAnimation);
         }
      }
   }

   public void on23(BotFeatureRegistry var1) {
      this.RemoteEventsPoller = var1;
      this.InventoryCodec.ShaderHand().forEach(var1xx -> var1xx.ItemRegistry(var1));
      UUID uuid = this.ThemeColorCycler;
      long i = this.EmoteRegistry++;
      this.UiAnimation(new ProtoStateUpdate(var1.on23(uuid, i)));
   }

   public void on23(InventoryUtils var1) {
      UUID uuid = this.ThemeColorCycler;
      long i = this.UserdataManager++;
      this.UiAnimation(new ProtoInventoryUpdate(var1.on23(uuid, i)));
   }

   public void EventMotion() {
      if (this.RotationEasingBase) {
         this.on23(new ProtoWatchSet()).exceptionally(this::UiAnimation);
      }
   }

   public CompletableFuture<BotFeaturesDto> on23(ProtocolMessage var1) {
      return this.on23(var1, 15L);
   }

   public CompletableFuture<BotFeaturesDto> on23(ProtocolMessage var1, long var2) {
      if (!this.RotationEasingBase) {
         return CompletableFuture.failedFuture(new CloudApiClient.ClientException("Cloud session is not authenticated"));
      }

      UUID uuid = UUID.randomUUID();
      CompletableFuture completablefuture = new CompletableFuture();
      this.MotorIntentRotationStrategy.put(uuid, completablefuture);
      completablefuture.orTimeout(var2, TimeUnit.SECONDS).whenComplete((var3, var4x) -> this.MotorIntentRotationStrategy.remove(uuid, completablefuture));
      if (!this.on23(var1, uuid)) {
         this.MotorIntentRotationStrategy.remove(uuid, completablefuture);
         completablefuture.completeExceptionally(new CloudApiClient.ClientException("Cloud WebSocket is not open"));
      }

      return completablefuture;
   }

   public void UiAnimation(ProtocolMessage var1) {
      if (this.RotationEasingBase) {
         this.on23(var1, UUID.randomUUID());
      }
   }

   public boolean on23(ProtocolMessage var1, UUID var2) {
      String s = AnalyticsTracker.on23(var2, var1);
      if (s.getBytes(StandardCharsets.UTF_8).length > this.TrajectoryDataset) {
         this.on23("PAYLOAD_TOO_LARGE", "Outgoing Cloud packet exceeds frame limit", false);
         return false;
      }

      CloudApiClient.ItemSpec l1i1iil111il1l1l_l1iil11li = this.MenuEaseE;
      if (l1i1iil111il1l1l_l1iil11li != null && l1i1iil111il1l1l_l1iil11li.isOpen()) {
         try {
            l1i1iil111il1l1l_l1iil11li.send(s);
            return true;
         } catch (RuntimeException runtimeexception) {
            this.on23("SEND_FAILED", rootMessage(runtimeexception), true);
            l1i1iil111il1l1l_l1iil11li.close();
            return false;
         }
      } else {
         return false;
      }
   }

   public void EventClick() {
      if (!this.closed && this.RoundedRectEasing.compareAndSet(false, true)) {
         this.MovementSimulator = System.currentTimeMillis();

         try {
            this.RotationBurstStrategy.execute(this::EventEntityCollision);
         } catch (RejectedExecutionException rejectedexecutionexception) {
            this.RoundedRectEasing.set(false);
         }
      }
   }

   public void on23(CloudApiClient.ItemSpec var1) {
      this.UiAnimation(
         () -> {
            if (!this.closed && this.MenuEaseE == var1) {
               this.MenuEaseC = false;
               this.MenuEaseF = false;
               this.RotationEasingBase = false;
               this.BotActivity = false;
               this.TrajectoryDataset = 262144;
               ProtoHello il11i1ii1iili1lllllili1ll11i_ili1ll11li1ili11l1i1l11l1 = new ProtoHello(
                  "ZenithDLC/3", List.of("config-http-v1", "cosmetics-access-v1", "state-batch-v1", "inventory-batch-v1")
               );
               if (!this.on23(il11i1ii1iili1lllllili1ll11i_ili1ll11li1ili11l1i1l11l1, UUID.randomUUID())) {
                  var1.close();
               }
            } else {
               var1.close();
            }
         }
      );
   }

   public CompletableFuture<CloudUserDto> on23(String var1, String var2, String var3, String var4, String var5, int var6, byte[] var7) {
      String s;
      try {
         s = CloudRouter(var4);
         on23(var1, var2, var7);
         if (var3 == null || var3.isBlank()) {
            throw new IllegalArgumentException("Config server address is required");
         }

         if (var6 < 0 || var6 > 25 || !"CODE".equals(s) && var6 != 0) {
            throw new IllegalArgumentException("Initial code count must be 0..25 and is only valid for CODE configs");
         }

         ProtocolMessage(var5);
      } catch (IllegalArgumentException illegalargumentexception) {
         return CompletableFuture.failedFuture(illegalargumentexception);
      }

      byte[] abyte = (byte[])var7.clone();
      ProtoConfigPublish il11i1ii1iili1lllllili1ll11i_l11illi1i11 = new ProtoConfigPublish(
         var1.trim(), var2.trim(), var3.trim(), abyte.length, sha256Hex(abyte), s, var5, var6
      );
      return this.on23(il11i1ii1iili1lllllili1ll11i_l11illi1i11)
         .thenCompose(
            var2x -> {
               CloudFullUserDto l1i1li1i11_i1ii1liii = on23(var2x, CloudFullUserDto.class);
               if (l1i1li1i11_i1ii1liii.HudElement()) {
                  if (l1i1li1i11_i1ii1liii.HudHotbarPanel() == null) {
                     throw new ServiceException("BAD_PACKET", "Upload ticket has no config metadata", false);
                  } else {
                     return CompletableFuture.completedFuture(l1i1li1i11_i1ii1liii.HudHotbarPanel());
                  }
               } else {
                  CloudSessionDto l1i1li1i11_i1lil1liil1l111llliliiliili1 = l1i1li1i11_i1ii1liii.HudTabList();
                  if (l1i1li1i11_i1lil1liil1l111llliliiliili1 == null) {
                     throw new ServiceException("BAD_PACKET", "Upload ticket has no HTTP credentials", false);
                  }

                  Builder builder = HttpRequest.newBuilder(URI.create(l1i1li1i11_i1lil1liil1l111llliliiliili1.url()))
                     .timeout(Duration.ofSeconds(30L))
                     .PUT(BodyPublishers.ofByteArray(abyte));
                  on23(builder, l1i1li1i11_i1lil1liil1l111llliliiliili1.Blink());
                  return this.RotationSmoothStrategy.sendAsync(builder.build(), BodyHandlers.discarding()).thenCompose(var2xx -> {
                     on23(var2xx.statusCode(), "Config upload");
                     return this.on23(new ProtoUploadComplete(l1i1li1i11_i1ii1liii.GameCoordinator().toString()));
                  }).thenApply(var0 -> on23(var0, CloudUserResultDto.class).HudHotbarPanel());
               }
            }
         );
   }

   public CompletableFuture<CloudUserDto> on23(UUID var1, long var2, String var4, byte[] var5) {
      Objects.requireNonNull(var1, "configId");

      try {
         on23("update", var4, var5);
         if (var2 < 1L) {
            throw new IllegalArgumentException("expectedVersion must be positive");
         }
      } catch (IllegalArgumentException illegalargumentexception) {
         return CompletableFuture.failedFuture(illegalargumentexception);
      }

      byte[] abyte = (byte[])var5.clone();
      return this.on23(new ProtoContentUpdateInit(var1, var2, var4.trim(), abyte.length, sha256Hex(abyte)))
         .thenCompose(
            var2x -> {
               CloudFullUserDto l1i1li1i11_i1ii1liii = on23(var2x, CloudFullUserDto.class);
               CloudSessionDto l1i1li1i11_i1lil1liil1l111llliliiliili1 = l1i1li1i11_i1ii1liii.HudTabList();
               if (l1i1li1i11_i1lil1liil1l111llliliiliili1 == null) {
                  throw new ServiceException("BAD_PACKET", "Content update ticket is missing", false);
               }

               Builder builder = HttpRequest.newBuilder(URI.create(l1i1li1i11_i1lil1liil1l111llliliiliili1.url()))
                  .timeout(Duration.ofSeconds(30L))
                  .PUT(BodyPublishers.ofByteArray(abyte));
               on23(builder, l1i1li1i11_i1lil1liil1l111llliliiliili1.Blink());
               return this.RotationSmoothStrategy.sendAsync(builder.build(), BodyHandlers.discarding()).thenCompose(var2xx -> {
                  on23(var2xx.statusCode(), "Config content update");
                  return this.on23(new ProtoContentUpdateComplete(l1i1li1i11_i1ii1liii.GameCoordinator()));
               }).thenApply(var0 -> on23(var0, CloudUserRefDto.class).HudHotbarPanel());
            }
         );
   }

   public CompletableFuture<Void> on23(UUID var1, byte[] var2) {
      Objects.requireNonNull(var1, "configId");

      try {
         if (var2 == null || var2.length == 0) {
            throw new IllegalArgumentException("Preview content is required");
         }

         if (var2.length > 262144) {
            throw new IllegalArgumentException("Preview exceeds the 256 KiB limit");
         }
      } catch (IllegalArgumentException illegalargumentexception) {
         return CompletableFuture.failedFuture(illegalargumentexception);
      }

      byte[] abyte = (byte[])var2.clone();
      return this.on23(new ProtoPreviewInit(var1, abyte.length, sha256Hex(abyte)))
         .thenCompose(
            var2x -> {
               CloudLoginDto l1i1li1i11_ili1ll11li1ili11l1i1l11l1 = on23(var2x, CloudLoginDto.class);
               Builder builder = HttpRequest.newBuilder(URI.create(l1i1li1i11_ili1ll11li1ili11l1i1l11l1.HudTabList().url()))
                  .timeout(Duration.ofSeconds(30L))
                  .PUT(BodyPublishers.ofByteArray(abyte));
               on23(builder, l1i1li1i11_ili1ll11li1ili11l1i1l11l1.HudTabList().Blink());
               return this.RotationSmoothStrategy
                  .sendAsync(builder.build(), BodyHandlers.discarding())
                  .thenAccept(var0 -> on23(var0.statusCode(), "Preview upload"));
            }
         );
   }

   public void EventEntityCollision() {
      try {
         if (this.closed || this.GuiWalkEvent()) {
            return;
         }

         CloudApiClient.ItemSpec l1i1iil111il1l1l_l1iil11lix = this.MenuEaseE;
         if (l1i1iil111il1l1l_l1iil11lix != null) {
            l1i1iil111il1l1l_l1iil11lix.close();
         }

         l1i1iil111il1l1l_l1iil11lix = new CloudApiClient.ItemSpec(this.GameCoordinator);
         l1i1iil111il1l1l_l1iil11lix.setDaemon(true);
         l1i1iil111il1l1l_l1iil11lix.setConnectionLostTimeout(30);
         this.MenuEaseE = l1i1iil111il1l1l_l1iil11lix;
         if (!l1i1iil111il1l1l_l1iil11lix.connectBlocking(5L, TimeUnit.SECONDS) && this.MenuEaseE == l1i1iil111il1l1l_l1iil11lix) {
            this.MenuEaseE = null;
            l1i1iil111il1l1l_l1iil11lix.close();
         }
      } catch (InterruptedException interruptedexception) {
         Thread.currentThread().interrupt();
         return;
      } catch (RuntimeException runtimeexception) {
         this.on23("CONNECT_FAILED", rootMessage(runtimeexception), true);
         return;
      } finally {
         this.RoundedRectEasing.set(false);
      }
   }

   public void on23(CloudFriendsDto var1) {
      String s = var1.AimAssist();
      int i = var1.Aura();
      if (i < 0) {
         throw new ServiceException("BAD_SNAPSHOT", "Invalid friends snapshot chunk", false);
      }

      if (this.HudElementMessage == null || !this.HudElementMessage.HudElementValue.equals(s)) {
         if (i != 0) {
            throw new ServiceException("BAD_SNAPSHOT", "Friends snapshot started mid-stream", false);
         }

         this.HudElementMessage = new ReconnectBackoff(s);
         this.ArmorHud = true;
         this.HudInventoryPanel = System.currentTimeMillis();
      }

      if (i != this.HudElementMessage.HudArmorPanel) {
         throw new ServiceException("BAD_SNAPSHOT", "Friends snapshot chunk order is broken", false);
      }

      this.HudElementMessage.HudArmorPanel++;
      this.on23(this.HudElementMessage, var1.friends());
      this.UiAnimation(this.HudElementMessage, var1.AutoSwap());
      if (var1.AutoExplosion()) {
         this.on23(this.HudElementMessage);
         this.HudElementMessage = null;
         this.ArmorHud = false;
         this.HudInventoryPanel = 0L;
         if (this.MenuEaseF && !this.RotationEasingBase) {
            this.RotationEasingBase = true;
            this.ThemeColorCycler = UUID.randomUUID();
            this.EmoteRegistry = 0L;
            this.UserdataManager = 0L;
            this.ColorAnimator(true);
            this.EventMotion();
         }

         this.EventHookPacketProcess();
         if (this.HudHotbarPanel) {
            this.HudHotbarPanel = false;
            this.ModuleToggleEvent();
         }
      }
   }

   public static String on23(String var0, long var1, String var3, String var4) {
      if (var0.length() < 32) {
         throw new IllegalArgumentException("Client auth secret must contain at least 32 characters");
      }

      try {
         byte[] abyte = "ZenithCloud/AuthTicket/v4".getBytes(StandardCharsets.US_ASCII);
         byte[] abyte1 = MessageDigest.getInstance("SHA-256").digest(var0.getBytes(StandardCharsets.UTF_8));
         ByteBuffer bytebuffer = ByteBuffer.wrap(abyte1);
         ByteBuffer bytebuffer1 = ByteBuffer.allocate(abyte1.length);

         for (int i = 0; i < 4; i++) {
            bytebuffer1.putLong(on23(bytebuffer.getLong(), i));
         }

         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(new SecretKeySpec(bytebuffer1.array(), "HmacSHA256"));
         mac.update(abyte);
         mac.update((byte)0);
         mac.update("zenith-auth".getBytes(StandardCharsets.UTF_8));
         mac.update((byte)0);
         byte[] abyte2 = mac.doFinal("zenith-cloud".getBytes(StandardCharsets.UTF_8));
         long j = System.currentTimeMillis() / 1000L;
         UUID uuid = UUID.randomUUID();
         ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(256);

         try (DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream)) {
            dataoutputstream.writeByte(1);
            dataoutputstream.writeLong(j);
            dataoutputstream.writeLong(j + 900L);
            dataoutputstream.writeLong(var1);
            dataoutputstream.writeLong(uuid.getMostSignificantBits());
            dataoutputstream.writeLong(uuid.getLeastSignificantBits());
            dataoutputstream.writeUTF("zenith-auth");
            dataoutputstream.writeUTF("zenith-cloud");
            dataoutputstream.writeUTF(var3);
            dataoutputstream.writeUTF(var4);
         }

         byte[] var23 = new byte[12];
         RotationTask.nextBytes(var23);
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         cipher.init(1, new SecretKeySpec(abyte2, "AES"), new GCMParameterSpec(128, var23));
         cipher.updateAAD(abyte);
         byte[] abyte3 = cipher.doFinal(bytearrayoutputstream.toByteArray());
         byte[] abyte4 = new byte[var23.length + abyte3.length];
         System.arraycopy(var23, 0, abyte4, 0, var23.length);
         System.arraycopy(abyte3, 0, abyte4, var23.length, abyte3.length);
         return "zct4." + Base64.getUrlEncoder().withoutPadding().encodeToString(abyte4);
      } catch (Exception exception) {
         throw new IllegalStateException("Could not issue development access ticket", exception);
      }
   }

   public Set<String> CloseScreenEvent() {
      return this.MenuEaseD;
   }

   public Set<String> EventDead() {
      return this.MenuEaseA;
   }

   public boolean NbtItemSpec(String var1) {
      return var1 != null && !var1.isBlank() && (this.BotActivity || this.MenuEaseD.contains(var1));
   }

   public boolean EnchantItemSpec(String var1) {
      return var1 != null && !var1.isBlank() && (this.BotActivity || this.MenuEaseA.contains(var1));
   }

   public CloudApiClient(FriendStore var1, String var2, String var3, String var4, String var5, String var6) {
      this(var1, var2, on23(var3, var4, var5, var6), BotFeatureRegistry(var4));
   }

   public CloudApiClient(FriendStore var1, String var2, Supplier<String> var3) {
      this(var1, var2, var3, 0L);
   }

   public void shutdown() {
      if (!this.closed) {
         this.closed = true;
         EventManager.unregister(this);
         CloudApiClient.ItemSpec l1i1iil111il1l1l_l1iil11li = this.MenuEaseE;
         this.MenuEaseE = null;
         if (l1i1iil111il1l1l_l1iil11li != null) {
            l1i1iil111il1l1l_l1iil11li.close(1000, "Client shutdown");
         }

         this.RotationEasingBase = false;
         this.on23(new CloudApiClient.ClientException("Cloud client was shut down"));
         this.RotationLegitStrategy.clear();
         this.AimPolicyRotationStrategy.clear();
         this.RotationBurstStrategy.shutdownNow();
      }
   }

   public boolean isConnected() {
      return this.RotationEasingBase && this.GuiWalkEvent();
   }

   public List<CloudUserProfile> HotbarInputEvent() {
      return List.copyOf(this.InventoryCodec.ShaderHand());
   }

   public List<ModuleSnapshotDto> StopUsingItemEvent() {
      return List.copyOf(this.RotationLegitStrategy);
   }

   public long RefreshCacheEvent() {
      return this.TargetInterpolator;
   }

   public void on23(CloudListener var1) {
      this.RotationStrategyBase.add(Objects.requireNonNull(var1, "listener"));
   }

   public void UiAnimation(CloudListener var1) {
      this.RotationStrategyBase.remove(var1);
   }

   public void SimpleItemBuilder(String var1) {
      Long olong = InventoryUtils(var1);
      if (olong != null && olong != this.TargetInterpolator) {
         this.on23(new ProtoFriendRequest(Long.toString(olong))).thenRun(this::PreventActionEvent).exceptionally(this::UiAnimation);
      } else {
         this.on23("BAD_USER_ID", "Cloud friend must be specified by UID", false);
      }
   }

   public void ItemServiceBase(String var1) {
      Long olong = InventoryUtils(var1);
      if (olong != null && olong != this.TargetInterpolator) {
         this.on23(new ProtoFriendRemove(Long.toString(olong))).thenAccept(var2x -> {
            this.InventoryCodec.BotWorldJoinEvent(Long.toString(olong));
            this.PreventActionEvent();
         }).exceptionally(this::UiAnimation);
      }
   }

   public void PreventActionEvent() {
      this.UiAnimation(this::ModuleToggleEvent);
   }

   public void ModuleToggleEvent() {
      if (this.RotationEasingBase) {
         if (this.ArmorHud) {
            this.HudHotbarPanel = true;
         } else {
            this.EventInjectAddEntity();
            this.on23(new ProtoFriendsSnapshot()).exceptionally(var1 -> {
               this.UiAnimation(() -> this.MediaTrackInfo(rootMessage(var1)));
               return null;
            });
         }
      }
   }

   public void NbtEditor(String var1) {
      this.on23(var1, true);
   }

   public void PotionItemBuilder(String var1) {
      this.on23(var1, false);
   }

   public CompletableFuture<MediaTrackInfo> ProfileItemBuilder(String var1) {
      return this.on23(new ProtoChatGlobal(var1)).thenApply(var0 -> on23(var0, CloudMediaDto.class).SoundManager());
   }

   public CompletableFuture<MediaTrackInfo> Easing(String var1, String var2) {
      Long olong = this.ModuleSnapshotDto(var1);
      return olong != null && olong != this.TargetInterpolator
         ? this.on23(new ProtoChatDirect(Long.toString(olong), var2)).thenApply(var0 -> on23(var0, CloudMediaDto.class).SoundManager())
         : CompletableFuture.failedFuture(new IllegalArgumentException("Direct message recipient must be a friend UID or nickname"));
   }

   public void StringCodec(String var1) {
      this.ProfileItemBuilder(var1).exceptionally(this::UiAnimation);
   }

   public void FileLogger(String var1) {
      for (CloudUserProfile li1ilil1i11ii111l11l : this.InventoryCodec.ShaderHand()) {
         this.Easing(li1ilil1i11ii111l11l.id(), var1).exceptionally(this::UiAnimation);
      }
   }

   public CompletableFuture<CloudMediaPageDto> on23(String var1, String var2, JsonObject var3, int var4) {
      String s = var1 == null ? "GLOBAL" : var1.trim().toUpperCase(Locale.ROOT);
      if (!Set.of("GLOBAL", "DIRECT").contains(s)) {
         return CompletableFuture.failedFuture(new IllegalArgumentException("Unknown chat channel"));
      }

      if (var4 >= 1 && var4 <= 50) {
         String s1 = null;
         if ("DIRECT".equals(s)) {
            Long olong = this.ModuleSnapshotDto(var2);
            if (olong == null) {
               return CompletableFuture.failedFuture(new IllegalArgumentException("Direct history requires peer UID"));
            }

            s1 = Long.toString(olong);
         }

         return this.on23(new ProtoChatHistory(s, s1, var3, var4)).thenApply(var0 -> on23(var0, CloudMediaPageDto.class));
      } else {
         return CompletableFuture.failedFuture(new IllegalArgumentException("History limit must be 1..50"));
      }
   }

   public CompletableFuture<CloudUserDto> on23(String var1, byte[] var2, String var3) {
      String s = var1 != null && !var1.toLowerCase(Locale.ROOT).endsWith(".zenith") ? var1 + ".zenith" : var1;
      return this.on23(var1, s, "unknown", var3, 0, var2);
   }

   public CompletableFuture<CloudUserDto> on23(String var1, String var2, String var3, String var4, int var5, byte[] var6) {
      return this.on23(var1, var2, var3, var4, null, var5, var6);
   }

   public CompletableFuture<byte[]> on23(UUID var1) {
      Objects.requireNonNull(var1, "configId");
      return this.on23(new ProtoDownloadGet(var1)).thenCompose(var1x -> {
         CloudWhoAmIDto l1i1li1i11_l1i1iil111il1l1l = on23(var1x, CloudWhoAmIDto.class);
         Builder builder = HttpRequest.newBuilder(URI.create(l1i1li1i11_l1i1iil111il1l1l.HudTabList().url())).timeout(Duration.ofSeconds(30L)).GET();
         on23(builder, l1i1li1i11_l1i1iil111il1l1l.HudTabList().Blink());
         return this.RotationSmoothStrategy.sendAsync(builder.build(), BodyHandlers.ofByteArray()).thenApply(var0 -> {
            on23(var0.statusCode(), "Config download");
            return var0.body();
         });
      });
   }

   public CompletableFuture<CloudUserDto> UiAnimation(UUID var1) {
      Objects.requireNonNull(var1, "configId");
      return this.on23(new ProtoPreviewDelete(var1)).thenApply(var0 -> on23(var0, CloudUserRefDto.class).HudHotbarPanel());
   }

   public CompletableFuture<byte[]> on23(CloudSessionExtDto var1) {
      Objects.requireNonNull(var1, "preview");
      Builder builder = HttpRequest.newBuilder(URI.create(var1.HudTabList().url())).timeout(Duration.ofSeconds(30L)).GET();
      on23(builder, var1.HudTabList().Blink());
      return this.RotationSmoothStrategy.sendAsync(builder.build(), BodyHandlers.ofByteArray()).thenApply(var0 -> {
         on23(var0.statusCode(), "Preview download");
         return var0.body();
      });
   }

   public CompletableFuture<CloudUsersPageDto> on23(int var1, int var2) {
      return this.on23(new ProtoConfigListGet(var1, var2)).thenApply(var0 -> on23(var0, CloudUsersPageDto.class));
   }

   public CompletableFuture<CloudConfigsPageDto> on23(String var1, int var2, int var3) {
      String s = var1 != null && !var1.isBlank() ? var1.trim().toUpperCase(Locale.ROOT) : "LIBRARY";
      if (!Set.of("LIBRARY", "PUBLIC").contains(s)) {
         return CompletableFuture.failedFuture(new IllegalArgumentException("Catalog scope must be LIBRARY or PUBLIC"));
      } else {
         return var2 >= 0 && var3 >= 1 && var3 <= 25
            ? this.on23(new ProtoConfigCatalogGet(s, var2, var3)).thenApply(var0 -> on23(var0, CloudConfigsPageDto.class))
            : CompletableFuture.failedFuture(new IllegalArgumentException("Invalid config catalog page"));
      }
   }

   public CompletableFuture<CloudLikeDto> Easing(UUID var1) {
      Objects.requireNonNull(var1, "configId");
      return this.on23(new ProtoLikeToggle(var1)).thenApply(var0 -> on23(var0, CloudLikeDto.class));
   }

   public CompletableFuture<String> on23(byte[] var1, int var2, int var3) {
      Objects.requireNonNull(var1, "png");
      if (var1.length == 0) {
         return CompletableFuture.failedFuture(new IllegalArgumentException("Captcha image is empty"));
      }

      String s = Base64.getEncoder().encodeToString(var1);
      return this.on23(new ProtoCaptchaSolve(s, var2, var3), 130L).thenApply(var0 -> on23(var0, CloudNoticeDto.class).RemoteEventsPoller());
   }

   public CompletableFuture<CloudUserDto> on23(UUID var1, long var2, String var4, String var5, String var6) {
      return this.on23(var1, var2, var4, var5, null, var6);
   }

   public CompletableFuture<CloudUserDto> on23(UUID var1, long var2, String var4, String var5, String var6, String var7) {
      Objects.requireNonNull(var1, "configId");

      String s;
      try {
         s = var7 != null && !var7.isBlank() ? CloudRouter(var7) : null;
         ProtocolMessage(var6);
      } catch (IllegalArgumentException illegalargumentexception) {
         return CompletableFuture.failedFuture(illegalargumentexception);
      }

      return this.on23(new ProtoMetadataUpdate(var1, var2, var4, var5, var6, s)).thenApply(var0 -> on23(var0, CloudUserRefDto.class).HudHotbarPanel());
   }

   public CompletableFuture<CloudSessionsDto> on23(UUID var1, int var2) {
      Objects.requireNonNull(var1, "configId");
      return var2 >= 1 && var2 <= 25
         ? this.on23(new ProtoConfigCodesCreate(var1, var2)).thenApply(var0 -> on23(var0, CloudSessionsDto.class))
         : CompletableFuture.failedFuture(new IllegalArgumentException("Code count must be 1..25"));
   }

   public CompletableFuture<CloudSessionsDto> ColorAnimator(UUID var1) {
      Objects.requireNonNull(var1, "configId");
      return this.on23(new ProtoCodesListGet(var1)).thenApply(var0 -> on23(var0, CloudSessionsDto.class));
   }

   public CompletableFuture<CloudUserDto> CloudApiClient(String var1) {
      return var1 != null && !var1.isBlank()
         ? this.on23(new ProtoCodeRedeem(var1.trim())).thenApply(var0 -> on23(var0, CloudUserWrapDto.class).HudHotbarPanel())
         : CompletableFuture.failedFuture(new IllegalArgumentException("Config access code is required"));
   }

   public boolean UiAnimation(BotFeaturesDto var1) {
      CompletableFuture completablefuture = this.MotorIntentRotationStrategy.remove(var1.RotationEasingBase());
      if (completablefuture == null) {
         return false;
      }

      if (var1.BotActivity() instanceof CloudMessageDto l1i1li1i11_li11ii1li11lli1i1liil) {
         this.UiAnimation(l1i1li1i11_li11ii1li11lli1i1liil);
         completablefuture.completeExceptionally(
            new ServiceException(
               l1i1li1i11_li11ii1li11lli1i1liil.PlayerStateService(), Easing(l1i1li1i11_li11ii1li11lli1i1liil), l1i1li1i11_li11ii1li11lli1i1liil.PetManager()
            )
         );
      } else {
         completablefuture.complete(var1);
      }

      return true;
   }

   public void on23(CloudMessageDto var1) {
      this.on23(var1.PlayerStateService(), var1.message(), var1.PetManager());
      this.UiAnimation(var1);
   }

   public void UiAnimation(CloudMessageDto var1) {
      if ("AUTH_EXPIRED".equals(var1.PlayerStateService())) {
         CloudApiClient.ItemSpec l1i1iil111il1l1l_l1iil11li = this.MenuEaseE;
         if (l1i1iil111il1l1l_l1iil11li != null) {
            l1i1iil111il1l1l_l1iil11li.close(1008, "Authentication expired");
         }
      }
   }

   public void on23(CloudServerStatsDto var1) {
      if (this.MenuEaseC) {
         throw new ServiceException("BAD_PHASE", "Duplicate connection.welcome", false);
      }

      this.MenuEaseC = true;
      int i = var1.BooleanSetting();
      if (i >= 4096 && i <= 1048576) {
         this.TrajectoryDataset = i;
      }

      String s;
      try {
         s = this.RotationPredictiveStrategy.get();
      } catch (RuntimeException runtimeexception) {
         throw new ServiceException("TOKEN_FAILED", rootMessage(runtimeexception), false);
      }

      if (s != null && !s.isBlank()) {
         if (!this.on23(new ProtoAuthLogin(s), UUID.randomUUID())) {
            throw new CloudApiClient.ClientException("Could not send auth.login");
         }
      } else {
         throw new ServiceException("TOKEN_FAILED", "Access token provider returned an empty ticket", false);
      }
   }

   public void on23(CloudPlayerInfoDto var1) {
      if (this.MenuEaseC && !this.MenuEaseF) {
         Long olong = InventoryUtils(var1.TargetInterpolator().id());
         if (olong == null) {
            throw new ServiceException("BAD_PACKET", "auth.success has no user id", false);
         }

         this.TargetInterpolator = olong;
         this.BotActivity = false;
         this.on23(var1.MotionSampleStore());
         this.MenuEaseF = true;
         this.EventInjectAddEntity();
      } else {
         throw new ServiceException("BAD_PHASE", "Unexpected auth.success", false);
      }
   }

   public void on23(CloudPermissionsDto var1) {
      this.MenuEaseD = Set.copyOf(var1.ColorSetting());
      this.MenuEaseA = Set.copyOf(var1.StringListSetting());
   }

   public void on23(CloudErrorDto var1) {
      this.on23(var1.PlayerStateService(), var1.message(), false);
      CloudApiClient.ItemSpec l1i1iil111il1l1l_l1iil11li = this.MenuEaseE;
      if (l1i1iil111il1l1l_l1iil11li != null) {
         l1i1iil111il1l1l_l1iil11li.close(1008, "Authentication failed");
      }
   }

   public void on23(CloudCodeDto var1) {
      CloudApiClient.ItemSpec l1i1iil111il1l1l_l1iil11li = this.MenuEaseE;
      if (l1i1iil111il1l1l_l1iil11li != null) {
         l1i1iil111il1l1l_l1iil11li.close(var1.code(), var1.Category());
      }
   }

   public void on23(ReconnectBackoff var1, List<CloudFriendDto> var2) {
      for (CloudFriendDto l1i1li1i11_llll111illll111ll : var2) {
         CloudBadgeDto l1i1li1i11_l1l1i1i1ii1il1l1li = l1i1li1i11_llll111illll111ll.TargetInterpolator();
         String s = l1i1li1i11_l1l1i1i1ii1il1l1li.id();
         if (InventoryUtils(s) != null) {
            var1.HudInfoBoxSecondary
               .add(
                  new CloudUserStatus(
                     new CloudUserProfile(s, on23(l1i1li1i11_l1l1i1i1ii1il1l1li), l1i1li1i11_l1l1i1i1ii1il1l1li.Event29()),
                     l1i1li1i11_llll111illll111ll.Event18Ext5()
                  )
               );
         }
      }
   }

   public void UiAnimation(ReconnectBackoff var1, List<CloudRelationDto> var2) {
      for (CloudRelationDto l1i1li1i11_l111llliilll1iii1ii : var2) {
         if ("PENDING".equalsIgnoreCase(l1i1li1i11_l111llliilll1iii1ii.ThemeColorCycler())) {
            var1.HudSelectedItemPanel
               .add(
                  new CloudApiClient.ItemRegistry(
                     l1i1li1i11_l111llliilll1iii1ii.Event05(),
                     l1i1li1i11_l111llliilll1iii1ii.ModeSetting().id(),
                     l1i1li1i11_l111llliilll1iii1ii.ModeSetting().Event29()
                  )
               );
         }
      }
   }

   public void on23(ReconnectBackoff var1) {
      List<CloudUserProfile> list = var1.HudInfoBoxSecondary.stream().map(CloudUserStatus::AttackEntityEvent).toList();
      this.InventoryCodec.UiAnimation(list);

      for (CloudUserStatus l1i1iil111il1l1l_Var160 : var1.HudInfoBoxSecondary) {
         CloudUserProfile li1ilil1i11ii111l11l = this.InventoryCodec.BotPacketEvent(l1i1iil111il1l1l_Var160.AttackEntityEvent().id());
         if (li1ilil1i11ii111l11l != null) {
            if (l1i1iil111il1l1l_Var160.Event18Ext5()) {
               li1ilil1i11ii111l11l.SprintEvent();
            } else {
               li1ilil1i11ii111l11l.EventPosHook();
            }
         }
      }

      this.RotationLegitStrategy.clear();
      this.AimPolicyRotationStrategy.clear();

      for (CloudApiClient.ItemRegistry l1i1iil111il1l1l_liil11l111liil1ll : var1.HudSelectedItemPanel) {
         this.on23(
            l1i1iil111il1l1l_liil11l111liil1ll.Event05(), l1i1iil111il1l1l_liil11l111liil1ll.Event37(), l1i1iil111il1l1l_liil11l111liil1ll.EventUpdateHealth()
         );
      }
   }

   public void on23(CloudRelationWrapDto var1) {
      CloudRelationDto l1i1li1i11_l111llliilll1iii1ii = var1.TextSetting();
      this.on23(
         l1i1li1i11_l111llliilll1iii1ii.Event05(), l1i1li1i11_l111llliilll1iii1ii.ModeSetting().id(), l1i1li1i11_l111llliilll1iii1ii.ModeSetting().Event29()
      );
   }

   public void on23(CloudBadgesDto var1) {
      for (CloudBadgeDto l1i1li1i11_l1l1i1i1ii1il1l1li : var1.SettingGroup()) {
         Long olong = InventoryUtils(l1i1li1i11_l1l1i1i1ii1il1l1li.id());
         if (olong != null && olong != this.TargetInterpolator) {
            String s = Long.toString(olong);
            this.InventoryCodec.on23(s, on23(l1i1li1i11_l1l1i1i1ii1il1l1li), l1i1li1i11_l1l1i1i1ii1il1l1li.Event29());
            CloudUserProfile li1ilil1i11ii111l11l = this.InventoryCodec.BotPacketEvent(s);
            if (li1ilil1i11ii111l11l != null) {
               li1ilil1i11ii111l11l.SprintEvent();
            }

            this.CloudUserProfile(s);
         }
      }

      this.EventMotion();
   }

   public void on23(CloudFeatureDto var1) {
      this.InventoryCodec.BotWorldJoinEvent(var1.RoundedRectEasing());
      this.CloudUserProfile(var1.RoundedRectEasing());
   }

   public void on23(CloudLogsDto var1) {
      for (CloudLogEntryDto l1i1li1i11_ii1il11i11lilii1i1l11liliil1l : var1.Reach()) {
         CloudUserProfile li1ilil1i11ii111l11l = this.InventoryCodec.BotPacketEvent(l1i1li1i11_ii1il11i11lilii1i1l11liliil1l.userId());
         if (li1ilil1i11ii111l11l != null && li1ilil1i11ii111l11l.EventTickEnd()) {
            BotFeatureRegistry ili1ll11li1ili11l1i1l11l1 = BotFeatureRegistry.Easing(l1i1li1i11_ii1il11i11lilii1i1l11liliil1l.MenuEaseE());
            if (ili1ll11li1ili11l1i1l11l1 != null) {
               li1ilil1i11ii111l11l.UiAnimation(ili1ll11li1ili11l1i1l11l1);
               li1ilil1i11ii111l11l.ItemRegistry(this.RemoteEventsPoller);
               this.InventoryCodec.on23(li1ilil1i11ii111l11l, ili1ll11li1ili11l1i1l11l1);
            }
         }
      }
   }

   public void on23(CloudStatsDto var1) {
      for (CloudStatEntryDto l1i1li1i11_iililll11i1iii1iilil111111ll : var1.FakeLag()) {
         CloudUserProfile li1ilil1i11ii111l11l = this.InventoryCodec.BotPacketEvent(l1i1li1i11_iililll11i1iii1iilil111111ll.userId());
         if (li1ilil1i11ii111l11l != null && li1ilil1i11ii111l11l.EventTickEnd()) {
            InventoryUtils l11illi1i11 = InventoryUtils.UiAnimation(l1i1li1i11_iililll11i1iii1iilil111111ll.MenuEaseA());
            if (l11illi1i11 != null) {
               li1ilil1i11ii111l11l.UiAnimation(l11illi1i11);
            }
         }
      }
   }

   public void on23(String var1, long var2, boolean var4) {
      CloudUserProfile li1ilil1i11ii111l11l = this.InventoryCodec.BotPacketEvent(var1);
      if (li1ilil1i11ii111l11l != null && var2 > 0L) {
         Long olong = this.RotationBotStrategy.get(var1);
         if (olong == null || var2 >= olong) {
            this.RotationBotStrategy.put(var1, var2);
            if (var4) {
               li1ilil1i11ii111l11l.SprintEvent();
            } else {
               li1ilil1i11ii111l11l.EventPosHook();
            }
         }
      }
   }

   public void EventInjectAddEntity() {
      this.ArmorHud = true;
      this.HudInventoryPanel = System.currentTimeMillis();
   }

   public void MediaTrackInfo(String var1) {
      if (this.ArmorHud) {
         this.EventHookTickEvent();
         this.on23("SNAPSHOT_FAILED", var1, true);
         CloudApiClient.ItemSpec l1i1iil111il1l1l_l1iil11li = this.MenuEaseE;
         if (l1i1iil111il1l1l_l1iil11li != null) {
            l1i1iil111il1l1l_l1iil11li.close(1011, "Friends snapshot failed");
         }
      }
   }

   public void EventHookTickEvent() {
      this.ArmorHud = false;
      this.HudHotbarPanel = false;
      this.HudInventoryPanel = 0L;
      this.HudElementMessage = null;
      this.MenuEaseB.clear();
   }

   public void EventHookPacketProcess() {
      while (!this.ArmorHud && !this.MenuEaseB.isEmpty()) {
         this.RotationSnapStrategy.InventoryUtils(this.MenuEaseB.removeFirst());
      }
   }

   public void on23(UUID var1, String var2, String var3) {
      Long olong = InventoryUtils(var2);
      if (olong != null) {
         String s = Long.toString(olong);
         this.AimPolicyRotationStrategy.put(s, var1);
         this.RotationLegitStrategy.removeIf(var1x -> s.equals(var1x.Event37()));

         while (this.RotationLegitStrategy.size() >= 500) {
            ModuleSnapshotDto l1iiiil1lii1iliiill1 = this.RotationLegitStrategy
               .stream()
               .min(Comparator.comparingLong(ModuleSnapshotDto::EventRender))
               .orElseThrow();
            this.RotationLegitStrategy.remove(l1iiiil1lii1iliiill1);
            this.AimPolicyRotationStrategy.remove(l1iiiil1lii1iliiill1.Event37());
         }

         this.RotationLegitStrategy.add(new ModuleSnapshotDto(s, var3 == null ? "" : var3, System.currentTimeMillis()));
      }
   }

   public void CloudUserProfile(String var1) {
      Long olong = InventoryUtils(var1);
      if (olong != null) {
         String s = Long.toString(olong);
         this.AimPolicyRotationStrategy.remove(s);
         this.RotationLegitStrategy.removeIf(var1x -> s.equals(var1x.Event37()));
      }
   }

   public void UiAnimation(Runnable var1) {
      if (!this.closed) {
         try {
            this.RotationBurstStrategy.execute(var1);
         } catch (RejectedExecutionException rejectedexecutionexception) {
            CloudApiClient.ItemSpec l1i1iil111il1l1l_l1iil11li = this.MenuEaseE;
            if (l1i1iil111il1l1l_l1iil11li != null) {
               l1i1iil111il1l1l_l1iil11li.close(1013, "Client protocol queue is full");
            }
         }
      }
   }

   public boolean GuiWalkEvent() {
      CloudApiClient.ItemSpec l1i1iil111il1l1l_l1iil11li = this.MenuEaseE;
      return l1i1iil111il1l1l_l1iil11li != null && l1i1iil111il1l1l_l1iil11li.isOpen();
   }

   public void on23(RuntimeException var1) {
      this.MotorIntentRotationStrategy.forEach((var1xx, var2) -> var2.completeExceptionally(var1));
      this.MotorIntentRotationStrategy.clear();
   }

   public <T> T UiAnimation(Throwable var1) {
      Throwable throwable = Easing(var1);
      if (throwable instanceof ServiceException illll1l1l1il) {
         this.on23(illll1l1l1il.PlayerStateService(), illll1l1l1il.getMessage(), illll1l1l1il.PetManager());
      } else {
         this.on23("CLIENT_ERROR", rootMessage(throwable), true);
      }

      return null;
   }

   public void on23(String var1, String var2, boolean var3) {
      String s = var1 != null && !var1.isBlank() ? var1 : "UNKNOWN";
      String s1 = var2 != null && !var2.isBlank() ? var2 : "Unknown Cloud error";
      long i = System.currentTimeMillis();
      if (i - this.ServerTheme >= 10000L) {
         this.ServerTheme = i;
         System.err.println("[Zenith Cloud] " + s + ": " + s1);
      }

      for (CloudListener l1i1iil111il1l1l_l1i1illlili : this.RotationStrategyBase) {
         try {
            l1i1iil111il1l1l_l1i1illlili.UiAnimation(s, s1, var3);
         } catch (RuntimeException var11) {
         }
      }
   }

   public void ColorAnimator(boolean var1) {
      for (CloudListener l1i1iil111il1l1l_l1i1illlili : this.RotationStrategyBase) {
         try {
            l1i1iil111il1l1l_l1i1illlili.ItemRegistry(var1);
         } catch (RuntimeException var5) {
         }
      }
   }

   public void on23(MediaTrackInfo var1) {
      for (CloudListener l1i1iil111il1l1l_l1i1illlili : this.RotationStrategyBase) {
         try {
            l1i1iil111il1l1l_l1i1illlili.UiAnimation(var1);
         } catch (RuntimeException var5) {
         }
      }
   }

   public static boolean Easing(BotFeaturesDto var0) {
      return !var0.MenuEaseF() && var0.type().startsWith("friends.") && !(var0.BotActivity() instanceof CloudFriendsDto);
   }

   public Long ModuleSnapshotDto(String var1) {
      Long olong = InventoryUtils(var1);
      if (olong != null) {
         return olong;
      }

      if (var1 == null) {
         return null;
      }

      for (CloudUserProfile li1ilil1i11ii111l11l : this.InventoryCodec.ShaderHand()) {
         if (li1ilil1i11ii111l11l.username().equalsIgnoreCase(var1.trim())) {
            return InventoryUtils(li1ilil1i11ii111l11l.id());
         }
      }

      return null;
   }

   public static Long InventoryUtils(String var0) {
      if (var0 != null && !var0.isBlank()) {
         try {
            long i = Long.parseLong(var0.trim());
            return i > 0L ? i : null;
         } catch (NumberFormatException numberformatexception) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static long BotFeatureRegistry(String var0) {
      Long olong = InventoryUtils(var0);
      if (olong != null) {
         return olong;
      }

      String s = var0 == null ? "local" : var0.trim().toLowerCase(Locale.ROOT);
      return Integer.toUnsignedLong(s.hashCode()) + 1L;
   }

   public static URI ServiceException(String var0) {
      String s = var0 != null && !var0.isBlank() ? var0.trim() : "wss://cloud.zenithdlc.org/ws/v1";
      if (s.startsWith("http://")) {
         s = "ws://" + s.substring("http://".length());
      } else if (s.startsWith("https://")) {
         s = "wss://" + s.substring("https://".length());
      } else if (!s.startsWith("ws://") && !s.startsWith("wss://")) {
         s = "ws://" + s;
      }

      URI uri = URI.create(s);
      if (uri.getHost() == null) {
         throw new IllegalArgumentException("Cloud URL must contain a host");
      }

      String s1 = uri.getPath();
      if (s1 == null || s1.isBlank() || "/".equals(s1)) {
         String s2 = s.replaceAll("/+$", "");
         uri = URI.create(s2 + "/ws/v1");
      }

      return uri;
   }

   public static Supplier<String> on23(String var0, String var1, String var2, String var3) {
      String s = var0 != null && !var0.isBlank() ? var0.trim() : "5W9nLU1l3V6SqJMVI4mSViItwLsLOPZMwhMJbdZE2ZhIjNKVf1g3OUtL8mOKBgA6";
      long i = BotFeatureRegistry(var1);
      String s1 = var2 != null && !var2.isBlank() ? var2.trim() : "UID " + i;
      String s2 = var3 != null && !var3.isBlank() ? var3.trim().toUpperCase(Locale.ROOT) : "USER";
      return () -> on23(s, i, s1, s2);
   }

   public static long on23(long var0, int var2) {
      long i = var0;

      for (int j = 0; j < 12; j++) {
         long k = -7046029254386353131L * (j + 1L + var2);
         long l = Long.rotateLeft(k, j * 11 + var2 * 7 & 63);
         i = on23(i, l);
         i = UiAnimation(i, Long.rotateRight(i, (j * 5 + var2 * 3) % 63 + 1));
         i = ColorAnimator(i);
         i = Easing(i, UiAnimation(k));
         long i1 = Long.rotateLeft(i, (j * 13 + 17) % 63 + 1);
         i = on23(i, Easing(i1));
         i = Long.rotateRight(i, (j * 7 + var2 + 9) % 63 + 1);
      }

      return i;
   }

   public static long on23(long var0, long var2) {
      return (var0 | var2) - (var0 & var2);
   }

   public static long UiAnimation(long var0, long var2) {
      return (var0 ^ var2) + ((var0 & var2) << 1);
   }

   public static long Easing(long var0, long var2) {
      return UiAnimation(var0, Easing(var2));
   }

   public static long UiAnimation(long var0) {
      return Easing(-1L, var0);
   }

   public static long Easing(long var0) {
      return UiAnimation(on23(var0, -1L), 1L);
   }

   public static long ColorAnimator(long var0) {
      return (var0 & 255L) << 56
         | (var0 & 65280L) << 40
         | (var0 & 16711680L) << 24
         | (var0 & 4278190080L) << 8
         | (var0 & 1095216660480L) >>> 8
         | (var0 & 280375465082880L) >>> 24
         | (var0 & 71776119061217280L) >>> 40
         | (var0 & -72057594037927936L) >>> 56;
   }

   public static String sha256Hex(byte[] var0) {
      try {
         return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(var0));
      } catch (Exception exception) {
         throw new IllegalStateException("SHA-256 is unavailable", exception);
      }
   }

   public static String CloudRouter(String var0) {
      String s = var0 != null && !var0.isBlank() ? var0.trim().toUpperCase(Locale.ROOT) : "PRIVATE";
      if (!Set.of("PRIVATE", "FRIENDS", "PUBLIC", "CODE").contains(s)) {
         throw new IllegalArgumentException("Unknown config visibility");
      } else {
         return s;
      }
   }

   public static void on23(String var0, String var1, byte[] var2) {
      if (var0 != null && !var0.isBlank() && var1 != null && !var1.isBlank() && var2 != null && var2.length != 0) {
         if (!var1.toLowerCase(Locale.ROOT).endsWith(".zenith")) {
            throw new IllegalArgumentException("Config file must use the .zenith extension");
         }

         if (var2.length > 307200) {
            throw new IllegalArgumentException("Config exceeds the 300 KiB limit");
         }
      } else {
         throw new IllegalArgumentException("Config name, file name and content are required");
      }
   }

   public static void ProtocolMessage(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String s = var0.strip();
         if (s.codePointCount(0, s.length()) > 512) {
            throw new IllegalArgumentException("Description must be at most 512 characters");
         }
      }
   }

   public static void on23(Builder var0, Map<String, String> var1) {
      for (Entry<String, String> entry : var1.entrySet()) {
         if (!"content-length".equalsIgnoreCase(entry.getKey())) {
            var0.header(entry.getKey(), entry.getValue());
         }
      }
   }

   public static void on23(int var0, String var1) {
      if (var0 < 200 || var0 >= 300) {
         throw new CloudApiClient.ClientException(var1 + " returned HTTP " + var0);
      }
   }

   public static String Easing(CloudMessageDto var0) {
      String s = var0.message();
      if (s == null || s.isBlank()) {
         s = "Cloud protocol error";
      }

      if (var0.TrapTp() != null && var0.TrapTp() > 0L && !s.toLowerCase(Locale.ROOT).contains("retry")) {
         long i = Math.max(1L, (var0.TrapTp() + 999L) / 1000L);
         return s + "; retry in " + i + "s";
      } else {
         return s;
      }
   }

   public static <T extends CloudResponse> T on23(BotFeaturesDto var0, Class<T> var1) {
      if (!var1.isInstance(var0.BotActivity())) {
         throw new ServiceException("UNEXPECTED_REPLY", "Expected " + var1.getSimpleName() + " but received " + var0.type(), false);
      } else {
         return var1.cast(var0.BotActivity());
      }
   }

   public static String on23(CloudBadgeDto var0) {
      return var0.HudInventoryPanel().isBlank() ? "UID " + var0.id() : var0.HudInventoryPanel();
   }

   public static Throwable Easing(Throwable var0) {
      Throwable throwable = var0;

      while ((throwable instanceof CompletionException || throwable instanceof ExecutionException) && throwable.getCause() != null) {
         throwable = throwable.getCause();
      }

      return throwable;
   }

   public static String rootMessage(Throwable var0) {
      Throwable throwable = Easing(var0);
      String s = throwable.getMessage();
      return s != null && !s.isBlank() ? s : throwable.getClass().getSimpleName();
   }

   public static final class ClientException extends RuntimeException {
      public ClientException(String var1) {
         super(var1);
      }
   }

   public record ItemRegistry(UUID Event05, String Event37, String EventUpdateHealth) {
   }

   public final class ItemSpec extends WebSocketClient {
      public ItemSpec(URI var2) {
         super(var2);
      }

      @Override
      public void onOpen(ServerHandshake var1) {
         CloudApiClient.this.on23(this);
      }

      @Override
      public void onMessage(String var1) {
         CloudApiClient.this.on23(this, var1);
      }

      @Override
      public void onClose(int var1, String var2, boolean var3) {
         CloudApiClient.this.on23(this, var1, var2);
      }

      @Override
      public void onError(Exception var1) {
         CloudApiClient.this.on23(this, var1);
      }
   }
}
