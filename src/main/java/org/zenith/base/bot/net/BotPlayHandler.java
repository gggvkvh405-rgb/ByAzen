package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.google.common.hash.HashCode;
import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.network.ChunkBatchSizeCalculator;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.recipebook.ClientRecipeManager;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.encryption.NetworkEncryptionUtils.SecureRandomUtil;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.LastSeenMessageList.Acknowledgment;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerLoadedC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket.Serialized;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket.Entry;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket.Reason;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket.Operation;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.display.CuttingRecipeDisplay.Grouping;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.Registry.PendingTagLoad;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.AbstractTeam.CollisionRule;
import net.minecraft.scoreboard.AbstractTeam.VisibilityRule;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.MountScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.text.Text;
import net.minecraft.storage.NbtReadView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.dynamic.HashCodeOps;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.tick.TickManager;
import org.slf4j.Logger;
import org.jspecify.annotations.Nullable;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotPhase;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotRemotePlayer;
import org.zenith.base.bot.world.BotWorld;

public final class BotPlayHandler extends BotCommonHandler implements ClientPlayPacketListener, TickablePacketListener {
   public static final Logger LOGGER = LogUtils.getLogger();
   public final GameProfile profile;
   public final Immutable registries;
   public final FeatureSet enabledFeatures;
   private final ComponentChangesHash.ComponentHasher componentHasher;
   private static final CommandTreeS2CPacket.NodeFactory<CommandSource> COMMAND_NODE_FACTORY = new CommandTreeS2CPacket.NodeFactory<>() {
      @Override
      public ArgumentBuilder<CommandSource, ?> literal(String name) {
         return LiteralArgumentBuilder.literal(name);
      }

      @Override
      public ArgumentBuilder<CommandSource, ?> argument(String name, ArgumentType<?> type, @Nullable Identifier suggestionProviderId) {
         RequiredArgumentBuilder<CommandSource, ?> argument = RequiredArgumentBuilder.argument(name, type);
         if (suggestionProviderId != null) {
            argument.suggests(SuggestionProviders.byId(suggestionProviderId));
         }
         return argument;
      }

      @Override
      public ArgumentBuilder<CommandSource, ?> modifyNode(ArgumentBuilder<CommandSource, ?> argument, boolean disableExecution, boolean requireTrusted) {
         return disableExecution ? argument.executes(context -> 0) : argument;
      }
   };
   public final ChunkBatchSizeCalculator chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
   public final Random random = Random.createThreadSafe();
   public final Map<UUID, PlayerListEntry> playerListEntries = new HashMap<>();
   public final Scoreboard scoreboard = new Scoreboard();
   public final BotCommandSource commandSource = new BotCommandSource(this);
   public volatile CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher();
   public volatile Set<RegistryKey<World>> worldKeys = Set.of();
   public final BrewingRecipeRegistry brewingRecipeRegistry;
   public FuelRegistry fuelRegistry;
   public ClientRecipeManager recipeManager = new ClientRecipeManager(Map.of(), Grouping.empty());
   public volatile BotWorld world;
   public volatile BotPlayer player;
   public volatile BotInteractionManager interactionManager;
   public Properties worldProperties;
   public volatile Text currentScreenTitle;
   public volatile int currentScreenSyncId = -1;
   public int chunkLoadDistance = 3;
   public int simulationDistance = 3;
   public boolean secureChatEnforced;
   public OptionalInt removedPlayerVehicleId = OptionalInt.empty();
   public boolean playerLoadedSent;
   public boolean initialChunksComing;

   public void onGameJoin(GameJoinS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      CommonPlayerSpawnInfo commonplayerspawninfo = packet.commonPlayerSpawnInfo();
      this.worldKeys = new LinkedHashSet<>(packet.dimensionIds());
      this.chunkLoadDistance = packet.viewDistance();
      this.simulationDistance = packet.simulationDistance();
      this.secureChatEnforced = packet.enforcesSecureChat();
      BotInteractionManager botinteractionmanager = new BotInteractionManager(this);
      this.interactionManager = botinteractionmanager;
      Properties properties = new Properties(Difficulty.NORMAL, packet.hardcore(), commonplayerspawninfo.isFlat());
      this.worldProperties = properties;
      BotWorld botworld = new BotWorld(
         this,
         properties,
         commonplayerspawninfo.dimension(),
         commonplayerspawninfo.dimensionType(),
         this.chunkLoadDistance,
         this.simulationDistance,
         commonplayerspawninfo.isDebug(),
         commonplayerspawninfo.seed(),
         commonplayerspawninfo.seaLevel()
      );
      this.world = botworld;
      BotPlayer botplayer = new BotPlayer(botworld, this, false, false);
      this.player = botplayer;
      botplayer.init();
      botplayer.setId(packet.playerEntityId());
      botplayer.setYaw(-180.0F);
      botworld.setBotPlayer(botplayer);
      botworld.addEntity(botplayer);
      botinteractionmanager.copyAbilities(botplayer);
      botplayer.setReducedDebugInfo(packet.reducedDebugInfo());
      botplayer.setLastDeathPos(commonplayerspawninfo.lastDeathLocation());
      botplayer.setPortalCooldown(commonplayerspawninfo.portalCooldown());
      botinteractionmanager.setGameModes(commonplayerspawninfo.gameMode(), commonplayerspawninfo.lastGameMode());
      this.playerLoadedSent = false;
      this.initialChunksComing = false;
      LOGGER.debug(
         "Bot {} joined dimension={} entityId={}", new Object[]{this.client.getName(), commonplayerspawninfo.dimension().getValue(), packet.playerEntityId()}
      );
      this.client.onGameJoin(botworld, botplayer);
   }

   public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      BotWorld botworld = this.world;
      BotInteractionManager botinteractionmanager = this.interactionManager;
      if (botplayer != null && botworld != null && botinteractionmanager != null && this.worldProperties != null) {
         CommonPlayerSpawnInfo commonplayerspawninfo = packet.commonPlayerSpawnInfo();
         RegistryKey registrykey = commonplayerspawninfo.dimension();
         RegistryEntry registryentry = commonplayerspawninfo.dimensionType();
         boolean flag = registrykey != botplayer.getWorld().getRegistryKey();
         BotWorld botworld1 = botworld;
         if (flag) {
            Map<MapIdComponent, MapState> map = botworld.getMapStates();
            Properties properties = new Properties(this.worldProperties.getDifficulty(), this.worldProperties.isHardcore(), commonplayerspawninfo.isFlat());
            this.worldProperties = properties;
            botworld1 = new BotWorld(
               this,
               properties,
               registrykey,
               registryentry,
               this.chunkLoadDistance,
               this.simulationDistance,
               commonplayerspawninfo.isDebug(),
               commonplayerspawninfo.seed(),
               commonplayerspawninfo.seaLevel()
            );
            botworld1.putMapStates(map);
            this.world = botworld1;
         }

         if (botplayer.shouldCloseHandledScreenOnRespawn()) {
            botplayer.closeHandledScreen();
         }

         boolean flag1 = packet.hasFlag((byte)2);
         BotPlayer botplayer1 = new BotPlayer(botworld1, this, flag1 && botplayer.isSneaking(), flag1 && botplayer.isSprinting());
         botplayer1.setId(botplayer.getId());
         this.player = botplayer1;
         if (flag1) {
            List<SerializedEntry<?>> list = botplayer.getDataTracker().getChangedEntries();
            if (list != null) {
               botplayer1.getDataTracker().writeUpdatedEntries(list);
            }

            botplayer1.setVelocity(botplayer.getVelocity());
            botplayer1.setYaw(botplayer.getYaw());
            botplayer1.setPitch(botplayer.getPitch());
         } else {
            botplayer1.init();
            botplayer1.setYaw(-180.0F);
         }

         if (packet.hasFlag((byte)1)) {
            botplayer1.getAttributes().setFrom(botplayer.getAttributes());
         } else {
            botplayer1.getAttributes().setBaseFrom(botplayer.getAttributes());
         }

         botworld1.setBotPlayer(botplayer1);
         botworld1.addEntity(botplayer1);
         botinteractionmanager.copyAbilities(botplayer1);
         botplayer1.setReducedDebugInfo(botplayer.hasReducedDebugInfo());
         botplayer1.setLastDeathPos(commonplayerspawninfo.lastDeathLocation());
         botplayer1.setPortalCooldown(commonplayerspawninfo.portalCooldown());
         botinteractionmanager.setGameModes(commonplayerspawninfo.gameMode(), commonplayerspawninfo.lastGameMode());
         this.playerLoadedSent = false;
         if (flag) {
            this.client.onGameJoin(botworld1, botplayer1);
         } else {
            this.client.onRespawn(botworld1, botplayer1);
         }
      }
   }

   public BotPlayHandler(BotClient var1, BotConnection var2, BotConnectionState var3) {
      super(var1, var2, var3);
      this.profile = var3.profile();
      this.registries = var3.receivedRegistries();
      RegistryOps<HashCode> registryOps = this.registries.getOps(HashCodeOps.INSTANCE);
      this.componentHasher = component -> component.encode(registryOps)
         .getOrThrow(error -> new IllegalArgumentException("Failed to hash " + component + ": " + error))
         .asInt();
      this.enabledFeatures = var3.enabledFeatures();
      this.brewingRecipeRegistry = BrewingRecipeRegistry.create(this.enabledFeatures);
      this.fuelRegistry = FuelRegistry.createDefault(this.registries, this.enabledFeatures);
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   public Immutable getRegistryManager() {
      return this.registries;
   }

   public FeatureSet getEnabledFeatures() {
      return this.enabledFeatures;
   }

   public ComponentChangesHash.ComponentHasher getComponentHasher() {
      return this.componentHasher;
   }

   public BotClient getClient() {
      return this.client;
   }

   public BotWorld getWorld() {
      return this.world;
   }

   public BotPlayer getPlayer() {
      return this.player;
   }

   public BotInteractionManager getInteractionManager() {
      BotInteractionManager botinteractionmanager = this.interactionManager;
      if (botinteractionmanager == null) {
         throw new IllegalStateException("Bot interaction manager is not initialized (no GameJoin yet)");
      } else {
         return botinteractionmanager;
      }
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public Text getCurrentScreenTitle() {
      BotPlayer botplayer = this.player;
      if (botplayer != null && botplayer.currentScreenHandler != null) {
         return botplayer.currentScreenHandler.syncId == this.currentScreenSyncId ? this.currentScreenTitle : null;
      } else {
         return null;
      }
   }

   public boolean hasOpenScreen() {
      return this.getCurrentScreenTitle() != null;
   }

   public void clearOpenScreen() {
      this.currentScreenTitle = null;
      this.currentScreenSyncId = -1;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public BrewingRecipeRegistry getBrewingRecipeRegistry() {
      return this.brewingRecipeRegistry;
   }

   public FuelRegistry getFuelRegistry() {
      return this.fuelRegistry;
   }

   public PlayerListEntry getPlayerListEntry(UUID var1) {
      return this.playerListEntries.get(var1);
   }

   public PlayerListEntry getPlayerListEntry(String var1) {
      for (PlayerListEntry playerlistentry : this.playerListEntries.values()) {
         if (playerlistentry.getProfile().name().equals(var1)) {
            return playerlistentry;
         }
      }

      return null;
   }

   public Iterable<PlayerListEntry> getPlayerList() {
      return this.playerListEntries.values();
   }

   public CommandDispatcher<CommandSource> getCommandDispatcher() {
      return this.commandDispatcher;
   }

   public BotCommandSource getCommandSource() {
      return this.commandSource;
   }

   public Set<RegistryKey<World>> getWorldKeys() {
      return this.worldKeys;
   }

   public void tick() {
      if (this.client.getPhase() == BotPhase.PLAY && this.connection.getPacketListener() == this) {
         BotWorld botworld = this.world;
         BotPlayer botplayer = this.player;
         BotInteractionManager botinteractionmanager = this.interactionManager;
         if (botworld != null && botplayer != null && botinteractionmanager != null) {
            botworld.runQueuedChunkUpdates();
            botworld.getChunkManager().getLightingProvider().doLightUpdates();
            botinteractionmanager.tick();
            this.client.onBotUpdate(botworld, botplayer);
            botworld.tick(() -> true);
            botworld.tickEntities();
            this.tickPlayerLoaded(botworld, botplayer);
         }

         this.sendPacket(ClientTickEndC2SPacket.INSTANCE);
      }
   }

   public void tickPlayerLoaded(BotWorld var1, BotPlayer var2) {
      if (!this.playerLoadedSent) {
         boolean flag = var1.getChunkManager().getChunk(var2.getChunkPos().x, var2.getChunkPos().z, null, false) != null;
         if (flag || var2.isLoaded()) {
            this.playerLoadedSent = true;
            var2.setLoaded(true);
            this.sendPacket(new PlayerLoadedC2SPacket());
         }
      }
   }

   public void sendChatMessage(String var1) {
      this.sendPacket(new ChatMessageC2SPacket(var1, Instant.now(), SecureRandomUtil.nextLong(), null, new Acknowledgment(0, new BitSet(), (byte)0)));
   }

   public void sendCommand(String var1) {
      this.sendPacket(new CommandExecutionC2SPacket(var1));
   }

   public void onEnterReconfiguration(EnterReconfigurationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.world = null;
      this.player = null;
      this.interactionManager = null;
      this.playerLoadedSent = false;
      this.client.setPhase(BotPhase.CONFIGURATION);
      this.client.onWorldUnload();
      BotConnectionState botconnectionstate = new BotConnectionState(
         this.profile, this.registries, this.enabledFeatures, this.brand, this.serverCookies, this.customReportDetails, this.serverLinks
      );
      BotConfigHandler botconfighandler = new BotConfigHandler(this.client, this.connection, botconnectionstate);
      this.connection.transitionInbound(ConfigurationStates.S2C, botconfighandler);
      this.connection.send(AcknowledgeReconfigurationC2SPacket.INSTANCE);
      this.connection.transitionOutbound(ConfigurationStates.C2S);
   }

   public void onBundle(BundleS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());

      for (Packet packetx : packet.getPackets()) {
         this.firePacketEvent(packetx);
         packetx.apply(this);
      }
   }

   public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer == null) {
         this.sendPacket(new TeleportConfirmC2SPacket(packet.teleportId()));
      } else {
         if (!botplayer.hasVehicle()) {
            setPosition(packet.change(), packet.relatives(), botplayer, false);
         }

         this.sendPacket(new TeleportConfirmC2SPacket(packet.teleportId()));
         this.sendPacket(
            new Full(
               botplayer.getX(), botplayer.getY(), botplayer.getZ(), botplayer.getYaw(), botplayer.getPitch(), false, false
            )
         );
      }
   }

   public static boolean setPosition(EntityPosition var0, Set<PositionFlag> var1, Entity var2, boolean var3) {
      EntityPosition playerposition = EntityPosition.fromEntity(var2);
      EntityPosition playerposition1 = EntityPosition.apply(playerposition, var0, var1);
      boolean flag = playerposition.position().squaredDistanceTo(playerposition1.position()) > 4096.0;
      if (var3 && !flag) {
         var2.updateTrackedPositionAndAngles(playerposition1.position(), playerposition1.yaw(), playerposition1.pitch());
         var2.setVelocity(playerposition1.deltaMovement());
         return true;
      } else {
         var2.setPosition(playerposition1.position());
         var2.setVelocity(playerposition1.deltaMovement());
         var2.setYaw(playerposition1.yaw());
         var2.setPitch(playerposition1.pitch());
         EntityPosition playerposition2 = new EntityPosition(var2.getLastRenderPos(), Vec3d.ZERO, var2.lastYaw, var2.lastPitch);
         EntityPosition playerposition3 = EntityPosition.apply(playerposition2, var0, var1);
         var2.setLastPositionAndAngles(playerposition3.position(), playerposition3.yaw(), playerposition3.pitch());
         return false;
      }
   }

   public void onPlayerRotation(PlayerRotationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         Set<PositionFlag> flags = PositionFlag.ofRot(packet.relativeYaw(), packet.relativePitch());
         EntityPosition current = EntityPosition.fromEntity(botplayer);
         EntityPosition rotated = EntityPosition.apply(current, current.withRotation(packet.yaw(), packet.pitch()), flags);
         botplayer.setYaw(rotated.yaw());
         botplayer.setPitch(rotated.pitch());
         botplayer.updateLastAngles();
         this.sendPacket(new LookAndOnGround(botplayer.getYaw(), botplayer.getPitch(), false, false));
      } else {
         this.sendPacket(new LookAndOnGround(packet.yaw(), packet.pitch(), false, false));
      }
   }

   public void onVehicleMove(VehicleMoveS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         Entity entity = botplayer.getRootVehicle();
         if (entity != botplayer && entity.isLogicalSideForUpdatingMovement()) {
            Vec3d vec3d = packet.position();
            Vec3d vec3d1 = entity.isInterpolating() ? entity.getInterpolator().getLerpedPos() : entity.getEntityPos();
            if (vec3d.distanceTo(vec3d1) > 1.0E-5F) {
               if (entity.isInterpolating()) {
                  entity.getInterpolator().clear();
               }
               entity.updatePositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), packet.yaw(), packet.pitch());
            }

            this.connection.send(VehicleMoveC2SPacket.fromVehicle(entity));
         }
      }
   }

   public void onStartChunkSend(StartChunkSendS2CPacket packet) {
      this.chunkBatchSizeCalculator.onStartChunkSend();
   }

   public void onChunkSent(ChunkSentS2CPacket packet) {
      this.chunkBatchSizeCalculator.onChunkSent(packet.batchSize());
      this.sendPacket(new AcknowledgeChunksC2SPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
   }

   public void onChunkData(ChunkDataS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         int i = packet.getChunkX();
         int j = packet.getChunkZ();
         ChunkData chunkdata = packet.getChunkData();
         botworld.getChunkManager().loadChunkFromPacket(i, j, chunkdata.getSectionsDataBuf(), chunkdata.getHeightmap(), chunkdata.getBlockEntities(i, j));
         LightData lightdata = packet.getLightData();
         botworld.enqueueChunkUpdate(() -> {
            this.readLightData(i, j, lightdata);
            WorldChunk worldchunk = botworld.getChunkManager().getWorldChunk(i, j, false);
            if (worldchunk != null) {
               this.markChunkSectionsReady(botworld, worldchunk);
            }
         });
      }
   }

   public void markChunkSectionsReady(BotWorld var1, WorldChunk var2) {
      LightingProvider lightingprovider = var1.getChunkManager().getLightingProvider();
      ChunkSection[] achunksection = var2.getSectionArray();
      ChunkPos chunkpos = var2.getPos();

      for (int i = 0; i < achunksection.length; i++) {
         ChunkSection chunksection = achunksection[i];
         int j = var1.sectionIndexToCoord(i);
         lightingprovider.setSectionStatus(ChunkSectionPos.from(chunkpos, j), chunksection.isEmpty());
      }
   }

   public void onChunkBiomeData(ChunkBiomeDataS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         for (Serialized serialized : packet.chunkBiomeData()) {
            botworld.getChunkManager().onChunkBiomeData(serialized.pos().x, serialized.pos().z, serialized.toReadingBuf());
         }

         for (Serialized serialized1 : packet.chunkBiomeData()) {
            botworld.resetChunkColor(new ChunkPos(serialized1.pos().x, serialized1.pos().z));
         }
      }
   }

   public void onUnloadChunk(UnloadChunkS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.getChunkManager().unload(packet.pos());
         ChunkPos chunkpos = packet.pos();
         botworld.enqueueChunkUpdate(() -> {
            LightingProvider lightingprovider = botworld.getLightingProvider();
            lightingprovider.setColumnEnabled(chunkpos, false);

            for (int i = lightingprovider.getBottomY(); i < lightingprovider.getTopY(); i++) {
               ChunkSectionPos chunksectionpos = ChunkSectionPos.from(chunkpos, i);
               lightingprovider.enqueueSectionData(LightType.BLOCK, chunksectionpos, null);
               lightingprovider.enqueueSectionData(LightType.SKY, chunksectionpos, null);
            }

            for (int j = botworld.getBottomSectionCoord(); j <= botworld.getTopSectionCoord(); j++) {
               lightingprovider.setSectionStatus(ChunkSectionPos.from(chunkpos, j), true);
            }
         });
      }
   }

   public void onLightUpdate(LightUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         int i = packet.getChunkX();
         int j = packet.getChunkZ();
         LightData lightdata = packet.getData();
         botworld.enqueueChunkUpdate(() -> this.readLightData(i, j, lightdata));
      }
   }

   public void readLightData(int var1, int var2, LightData var3) {
      BotWorld botworld = this.world;
      if (botworld != null) {
         LightingProvider lightingprovider = botworld.getChunkManager().getLightingProvider();
         BitSet bitset = var3.getInitedSky();
         BitSet bitset1 = var3.getUninitedSky();
         Iterator<byte[]> iterator = var3.getSkyNibbles().iterator();
         this.updateLighting(var1, var2, lightingprovider, LightType.SKY, bitset, bitset1, iterator);
         BitSet bitset2 = var3.getInitedBlock();
         BitSet bitset3 = var3.getUninitedBlock();
         Iterator<byte[]> iterator1 = var3.getBlockNibbles().iterator();
         this.updateLighting(var1, var2, lightingprovider, LightType.BLOCK, bitset2, bitset3, iterator1);
         lightingprovider.setColumnEnabled(new ChunkPos(var1, var2), true);
      }
   }

   public void updateLighting(int var1, int var2, LightingProvider var3, LightType var4, BitSet var5, BitSet var6, Iterator<byte[]> var7) {
      for (int i = 0; i < var3.getHeight(); i++) {
         int j = var3.getBottomY() + i;
         boolean flag = var5.get(i);
         boolean flag1 = var6.get(i);
         if (flag || flag1) {
            var3.enqueueSectionData(var4, ChunkSectionPos.from(var1, j, var2), flag ? new ChunkNibbleArray((byte[])var7.next().clone()) : new ChunkNibbleArray());
         }
      }
   }

   public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         packet.visitUpdates((var1, var2x) -> botworld.handleBlockUpdate(var1, var2x, 19));
      }
   }

   public void onBlockUpdate(BlockUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.handleBlockUpdate(packet.getPos(), packet.getState(), 19);
      }
   }

   public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.chunkLoadDistance = packet.getDistance();
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.getChunkManager().updateLoadDistance(packet.getDistance());
      }
   }

   public void onSimulationDistance(SimulationDistanceS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.simulationDistance = packet.simulationDistance();
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.setSimulationDistance(this.simulationDistance);
      }
   }

   public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
      }
   }

   public void onPlayerActionResponse(PlayerActionResponseS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.handlePlayerActionResponse(packet.sequence());
      }
   }

   public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.getBlockEntity(packet.getPos(), packet.getBlockEntityType()).ifPresent(var2x -> {
            NbtCompound nbtcompound = packet.getNbt();
            if (!nbtcompound.isEmpty()) {
               try (ErrorReporter.Logging logging = new ErrorReporter.Logging(var2x.getReporterContext(), LOGGER)) {
                  var2x.read(NbtReadView.create(logging, this.registries, nbtcompound));
               }
            }
         });
      }
   }

   public void onBlockEvent(BlockEventS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.addSyncedBlockEvent(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
      }
   }

   public void onBlockBreakingProgress(BlockBreakingProgressS2CPacket packet) {
   }

   public void onWorldEvent(WorldEventS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         if (packet.isGlobal()) {
            botworld.syncGlobalEvent(packet.getEventId(), packet.getPos(), packet.getData());
         } else {
            botworld.syncWorldEvent(packet.getEventId(), packet.getPos(), packet.getData());
         }
      }
   }

   public void onEntitySpawn(EntitySpawnS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.getEntityId()) {
            this.removedPlayerVehicleId = OptionalInt.empty();
         }

         Entity entity = this.createEntity(botworld, packet);
         if (entity != null) {
            entity.onSpawnPacket(packet);
            botworld.addEntity(entity);
         } else {
            LOGGER.warn("Bot {}: skipping entity with id {}", this.client.getName(), packet.getEntityType());
         }
      }
   }

   public Entity createEntity(BotWorld var1, EntitySpawnS2CPacket var2) {
      EntityType entitytype = var2.getEntityType();
      if (entitytype == EntityType.PLAYER) {
         PlayerListEntry playerlistentry = this.getPlayerListEntry(var2.getUuid());
         if (playerlistentry == null) {
            LOGGER.warn("Bot {}: server added player prior to sending player info (id {})", this.client.getName(), var2.getUuid());
            return null;
         } else {
            return new BotRemotePlayer(var1, this, playerlistentry.getProfile());
         }
      } else {
         return entitytype.create(var1, SpawnReason.LOAD);
      }
   }

   public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = botworld.getEntityById(packet.getEntityId());
         if (entity != null) {
            entity.setVelocityClient(packet.getVelocity());
         }
      }
   }

   public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = botworld.getEntityById(packet.id());
         if (entity != null) {
            entity.getDataTracker().writeUpdatedEntries(packet.trackedValues());
         }
      }
   }

   public void onEntityPositionSync(EntityPositionSyncS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      BotPlayer botplayer = this.player;
      if (botworld != null && botplayer != null) {
         Entity entity = botworld.getEntityById(packet.id());
         if (entity != null) {
            Vec3d vec3d = packet.values().position();
            entity.getTrackedPosition().setPos(vec3d);
            if (!entity.isLogicalSideForUpdatingMovement()) {
               float f = packet.values().yaw();
               float f1 = packet.values().pitch();
               boolean flag = entity.getEntityPos().squaredDistanceTo(vec3d) > 4096.0;
               if (botworld.hasEntity(entity) && !flag) {
                  entity.updateTrackedPositionAndAngles(vec3d, f, f1);
               } else {
                  entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f, f1);
                  if (entity.hasPassengerDeep(botplayer)) {
                     entity.updatePassengerPosition(botplayer);
                     botplayer.resetPosition();
                  }
               }

               entity.setOnGround(packet.onGround());
            }
         }
      }
   }

   public void onEntityPosition(EntityPositionS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      BotPlayer botplayer = this.player;
      if (botworld != null && botplayer != null) {
         Entity entity = botworld.getEntityById(packet.entityId());
         if (entity == null) {
            if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.entityId()) {
               LOGGER.debug("Bot {}: teleport for removed vehicle {}, applying to player", this.client.getName(), packet.entityId());
               setPosition(packet.change(), packet.relatives(), botplayer, false);
               this.connection
                  .send(
                     new Full(
                        botplayer.getX(),
                        botplayer.getY(),
                        botplayer.getZ(),
                        botplayer.getYaw(),
                        botplayer.getPitch(),
                        false,
                        false
                     )
                  );
            }
         } else {
            boolean flag = packet.relatives().contains(PositionFlag.X)
               || packet.relatives().contains(PositionFlag.Y)
               || packet.relatives().contains(PositionFlag.Z);
            boolean flag1 = botworld.hasEntity(entity) || !entity.isLogicalSideForUpdatingMovement() || flag;
            boolean flag2 = setPosition(packet.change(), packet.relatives(), entity, flag1);
            entity.setOnGround(packet.onGround());
            if (!flag2 && entity.hasPassengerDeep(botplayer)) {
               entity.updatePassengerPosition(botplayer);
               botplayer.resetPosition();
               if (entity.isLogicalSideForUpdatingMovement()) {
                  this.connection.send(VehicleMoveC2SPacket.fromVehicle(entity));
               }
            }
         }
      }
   }

   public void onEntity(EntityS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = packet.getEntity(botworld);
         if (entity != null) {
            if (entity.isLogicalSideForUpdatingMovement()) {
               TrackedPosition trackedposition = entity.getTrackedPosition();
               Vec3d vec3d = trackedposition.withDelta(packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ());
               trackedposition.setPos(vec3d);
            } else {
               if (packet.isPositionChanged()) {
                  TrackedPosition trackedposition1 = entity.getTrackedPosition();
                  Vec3d vec3d1 = trackedposition1.withDelta(packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ());
                  trackedposition1.setPos(vec3d1);
                  if (packet.hasRotation()) {
                     entity.updateTrackedPositionAndAngles(vec3d1, packet.getYaw(), packet.getPitch());
                  } else {
                     entity.updateTrackedPosition(vec3d1);
                  }
               } else if (packet.hasRotation()) {
                  entity.updateTrackedAngles(packet.getYaw(), packet.getPitch());
               }

               entity.setOnGround(packet.isOnGround());
            }
         }
      }
   }

   public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = packet.getEntity(botworld);
         if (entity != null) {
            entity.updateTrackedHeadRotation(packet.getHeadYaw(), 3);
         }
      }
   }

   public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      BotPlayer botplayer = this.player;
      if (botworld != null) {
         packet.getEntityIds().forEach(var3x -> {
            Entity entity = botworld.getEntityById(var3x);
            if (entity != null) {
               if (botplayer != null && entity.hasPassengerDeep(botplayer)) {
                  this.removedPlayerVehicleId = OptionalInt.of(var3x);
               }

               botworld.removeEntity(var3x, RemovalReason.DISCARDED);
            }
         });
      }
   }

   public void onEntityAnimation(EntityAnimationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = botworld.getEntityById(packet.getEntityId());
         if (entity != null) {
            if (packet.getAnimationId() == 0) {
               ((LivingEntity)entity).swingHand(Hand.MAIN_HAND);
            } else if (packet.getAnimationId() == 3) {
               ((LivingEntity)entity).swingHand(Hand.OFF_HAND);
            } else if (packet.getAnimationId() == 2) {
               ((PlayerEntity)entity).wakeUp(false, false);
            }
         }
      }
   }

   public void onEntityStatus(EntityStatusS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = packet.getEntity(botworld);
         if (entity != null) {
            switch (packet.getStatus()) {
               case 21:
               case 35:
               case 63:
                  break;
               default:
                  entity.handleStatus(packet.getStatus());
            }
         }
      }
   }

   public void onEntityAttach(EntityAttachS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null && botworld.getEntityById(packet.getAttachedEntityId()) instanceof Leashable leashable) {
         leashable.setUnresolvedLeashHolderId(packet.getHoldingEntityId());
      }
   }

   public void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      BotPlayer botplayer = this.player;
      if (botworld != null) {
         Entity entity = botworld.getEntityById(packet.getEntityId());
         if (entity == null) {
            LOGGER.warn("Bot {}: received passengers for unknown entity", this.client.getName());
         } else {
            entity.removeAllPassengers();

            for (int i : packet.getPassengerIds()) {
               Entity entity1 = botworld.getEntityById(i);
               if (entity1 != null) {
                  entity1.startRiding(entity, true, false);
                  if (entity1 == botplayer) {
                     this.removedPlayerVehicleId = OptionalInt.empty();
                  }
               }
            }
         }
      }
   }

   public void onEntityAttributes(EntityAttributesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = botworld.getEntityById(packet.getEntityId());
         if (entity != null) {
            if (!(entity instanceof LivingEntity livingentity)) {
               throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            }

            AttributeContainer attributecontainer = livingentity.getAttributes();

            for (Entry entry : packet.getEntries()) {
               EntityAttributeInstance entityattributeinstance = attributecontainer.getCustomInstance(entry.attribute());
               if (entityattributeinstance == null) {
                  LOGGER.warn(
                     "Bot {}: entity {} does not have attribute {}", new Object[]{this.client.getName(), entity, entry.attribute().getIdAsString()}
                  );
               } else {
                  entityattributeinstance.setBaseValue(entry.base());
                  entityattributeinstance.clearModifiers();

                  for (EntityAttributeModifier entityattributemodifier : entry.modifiers()) {
                     entityattributeinstance.addTemporaryModifier(entityattributemodifier);
                  }
               }
            }
         }
      }
   }

   public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null && botworld.getEntityById(packet.getEntityId()) instanceof LivingEntity livingentity) {
         RegistryEntry registryentry = packet.getEffectId();
         StatusEffectInstance statuseffectinstance = new StatusEffectInstance(
            registryentry, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon(), null
         );
         if (!packet.keepFading()) {
            statuseffectinstance.skipFading();
         }

         livingentity.setStatusEffect(statuseffectinstance, null);
      }
   }

   public void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null && packet.getEntity(botworld) instanceof LivingEntity livingentity) {
         livingentity.removeStatusEffectInternal(packet.effect());
      }
   }

   public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null && botworld.getEntityById(packet.getEntityId()) instanceof LivingEntity livingentity) {
         packet.getEquipmentList().forEach(var1 -> livingentity.equipStack((EquipmentSlot)var1.getFirst(), (ItemStack)var1.getSecond()));
      }
   }

   public void onEntityDamage(EntityDamageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = botworld.getEntityById(packet.entityId());
         if (entity != null) {
            entity.onDamaged(packet.createDamageSource(botworld));
         }
      }
   }

   public void onDamageTilt(DamageTiltS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = botworld.getEntityById(packet.id());
         if (entity != null) {
            entity.animateDamage(packet.yaw());
         }
      }
   }

   public void onMoveMinecartAlongTrack(MoveMinecartAlongTrackS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = packet.getEntity(botworld);
         if (entity instanceof AbstractMinecartEntity abstractminecartentity
            && !entity.isLogicalSideForUpdatingMovement()
            && abstractminecartentity.getController() instanceof ExperimentalMinecartController experimentalminecartcontroller) {
            experimentalminecartcontroller.stagingLerpSteps.addAll(packet.lerpSteps());
         }
      }
   }

   public void onProjectilePower(ProjectilePowerS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null && botworld.getEntityById(packet.getEntityId()) instanceof ExplosiveProjectileEntity explosiveprojectileentity) {
         explosiveprojectileentity.accelerationPower = packet.getAccelerationPower();
      }
   }

   public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         Entity entity = botworld.getEntityById(packet.getEntityId());
         if (entity != null) {
            if (entity instanceof ItemEntity itementity) {
               ItemStack itemstack = itementity.getStack();
               if (!itemstack.isEmpty()) {
                  itemstack.decrement(packet.getStackAmount());
               }

               if (itemstack.isEmpty()) {
                  botworld.removeEntity(packet.getEntityId(), RemovalReason.DISCARDED);
               }
            } else if (!(entity instanceof ExperienceOrbEntity)) {
               botworld.removeEntity(packet.getEntityId(), RemovalReason.DISCARDED);
            }
         }
      }
   }

   public void onExplosion(ExplosionS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         packet.playerKnockback().ifPresent(botplayer::addVelocityInternal);
      }
   }

   public void onLookAt(LookAtS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      BotPlayer botplayer = this.player;
      if (botworld != null && botplayer != null) {
         Vec3d vec3d = packet.getTargetPosition(botworld);
         if (vec3d != null) {
            botplayer.lookAt(packet.getSelfAnchor(), vec3d);
         }
      }
   }

   public void onHealthUpdate(HealthUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         botplayer.updateHealth(packet.getHealth());
         botplayer.getHungerManager().setFoodLevel(packet.getFood());
         botplayer.getHungerManager().setSaturationLevel(packet.getSaturation());
      }
   }

   public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         botplayer.setExperience(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
      }
   }

   public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null && PlayerInventory.isValidHotbarIndex(packet.slot())) {
         botplayer.getInventory().setSelectedSlot(packet.slot());
      }
   }

   public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         ItemStack itemstack = packet.getStack();
         int i = packet.getSlot();
         if (packet.getSyncId() == 0) {
            botplayer.playerScreenHandler.setStackInSlot(i, packet.getRevision(), itemstack);
         } else if (packet.getSyncId() == botplayer.currentScreenHandler.syncId) {
            botplayer.currentScreenHandler.setStackInSlot(i, packet.getRevision(), itemstack);
         }
      }
   }

   public void onSetCursorItem(SetCursorItemS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         botplayer.currentScreenHandler.setCursorStack(packet.contents());
      }
   }

   public void onSetPlayerInventory(SetPlayerInventoryS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         botplayer.getInventory().setStack(packet.slot(), packet.contents());
      }
   }

   public void onInventory(InventoryS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         if (packet.syncId() == 0) {
            botplayer.playerScreenHandler.updateSlotStacks(packet.revision(), packet.contents(), packet.cursorStack());
         } else if (packet.syncId() == botplayer.currentScreenHandler.syncId) {
            botplayer.currentScreenHandler.updateSlotStacks(packet.revision(), packet.contents(), packet.cursorStack());
         }
      }
   }

   public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null && botplayer.currentScreenHandler.syncId == packet.getSyncId()) {
         botplayer.currentScreenHandler.setProperty(packet.getPropertyId(), packet.getValue());
      }
   }

   public void onOpenScreen(OpenScreenS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         ScreenHandler screenhandler = packet.getScreenHandlerType().create(packet.getSyncId(), botplayer.getInventory());
         botplayer.currentScreenHandler = screenhandler;
         this.currentScreenTitle = packet.getName();
         this.currentScreenSyncId = packet.getSyncId();
      }
   }

   public void onOpenMountScreen(OpenMountScreenS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      BotPlayer botplayer = this.player;
      if (botworld != null && botplayer != null && botworld.getEntityById(packet.getMountId()) instanceof AbstractHorseEntity abstracthorseentity) {
         int i = packet.getSlotColumnCount();
         SimpleInventory simpleinventory = new SimpleInventory(MountScreenHandler.getSlotCount(i));
         botplayer.currentScreenHandler = new HorseScreenHandler(packet.getSyncId(), botplayer.getInventory(), simpleinventory, abstracthorseentity, i);
      }
   }

   public void onCloseScreen(CloseScreenS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         botplayer.closeScreen();
      } else {
         this.clearOpenScreen();
      }
   }

   public void onSetTradeOffers(SetTradeOffersS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         ScreenHandler screenhandler = botplayer.currentScreenHandler;
         if (packet.getSyncId() == screenhandler.syncId && screenhandler instanceof MerchantScreenHandler merchantscreenhandler) {
            merchantscreenhandler.setOffers(packet.getOffers());
            merchantscreenhandler.setExperienceFromServer(packet.getExperience());
            merchantscreenhandler.setLevelProgress(packet.getLevelProgress());
            merchantscreenhandler.setLeveled(packet.isLeveled());
            merchantscreenhandler.setCanRefreshTrades(packet.isRefreshable());
         }
      }
   }

   public void onCooldownUpdate(CooldownUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         if (packet.cooldown() == 0) {
            botplayer.getItemCooldownManager().remove(packet.cooldownGroup());
         } else {
            botplayer.getItemCooldownManager().set(packet.cooldownGroup(), packet.cooldown());
         }
      }
   }

   public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer botplayer = this.player;
      if (botplayer != null) {
         botplayer.getAbilities().flying = packet.isFlying();
         botplayer.getAbilities().creativeMode = packet.isCreativeMode();
         botplayer.getAbilities().invulnerable = packet.isInvulnerable();
         botplayer.getAbilities().allowFlying = packet.allowFlying();
         botplayer.getAbilities().setFlySpeed(packet.getFlySpeed());
         botplayer.getAbilities().setWalkSpeed(packet.getWalkSpeed());
      }
   }

   public void onDeathMessage(DeathMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      BotPlayer botplayer = this.player;
      if (botworld != null && botplayer != null) {
         Entity entity = botworld.getEntityById(packet.playerId());
         if (entity == botplayer) {
            botplayer.requestRespawn();
         }
      }
   }

   public void onGameStateChange(GameStateChangeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      Reason reason = packet.getReason();
      float f = packet.getValue();
      int i = MathHelper.floor(f + 0.5F);
      if (reason == GameStateChangeS2CPacket.RAIN_STARTED && botworld != null) {
         botworld.getLevelProperties().setRaining(true);
         botworld.setRainGradient(0.0F);
      } else if (reason == GameStateChangeS2CPacket.RAIN_STOPPED && botworld != null) {
         botworld.getLevelProperties().setRaining(false);
         botworld.setRainGradient(1.0F);
      } else if (reason == GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
         BotInteractionManager botinteractionmanager = this.interactionManager;
         if (botinteractionmanager != null) {
            botinteractionmanager.setGameMode(GameMode.byIndex(i));
         }
      } else if (reason == GameStateChangeS2CPacket.GAME_WON) {
         this.sendPacket(new ClientStatusC2SPacket(Mode.PERFORM_RESPAWN));
      } else if (reason == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED && botworld != null) {
         botworld.setRainGradient(f);
      } else if (reason == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED && botworld != null) {
         botworld.setThunderGradient(f);
      } else if (reason == GameStateChangeS2CPacket.INITIAL_CHUNKS_COMING) {
         this.initialChunksComing = true;
      }
   }

   public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.setTime(packet.time(), packet.timeOfDay(), packet.tickDayTime());
      }
   }

   public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.setSpawnPoint(packet.respawnData());
      }
   }

   public void onDifficulty(DifficultyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      if (this.worldProperties != null) {
         this.worldProperties.setDifficulty(packet.difficulty());
         this.worldProperties.setDifficultyLocked(packet.difficultyLocked());
      }
   }

   public void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         WorldBorder worldborder = botworld.getWorldBorder();
         worldborder.setCenter(packet.getCenterX(), packet.getCenterZ());
         long i = packet.getSizeLerpTime();
         if (i > 0L) {
              worldborder.interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), i, botworld.getTime());
         } else {
            worldborder.setSize(packet.getSizeLerpTarget());
         }

         worldborder.setMaxRadius(packet.getMaxRadius());
         worldborder.setWarningBlocks(packet.getWarningBlocks());
         worldborder.setWarningTime(packet.getWarningTime());
      }
   }

   public void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.getWorldBorder().setCenter(packet.getCenterX(), packet.getCenterZ());
      }
   }

   public void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
           botworld.getWorldBorder().interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), packet.getSizeLerpTime(), botworld.getTime());
      }
   }

   public void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.getWorldBorder().setSize(packet.getSizeLerpTarget());
      }
   }

   public void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.getWorldBorder().setWarningBlocks(packet.getWarningBlocks());
      }
   }

   public void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.getWorldBorder().setWarningTime(packet.getWarningTime());
      }
   }

   public void onMapUpdate(MapUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         MapIdComponent mapidcomponent = packet.mapId();
         MapState mapstate = botworld.getMapState(mapidcomponent);
         if (mapstate == null) {
            mapstate = MapState.of(packet.scale(), packet.locked(), botworld.getRegistryKey());
            botworld.putClientsideMapState(mapidcomponent, mapstate);
         }

         packet.apply(mapstate);
      }
   }

   public void onUpdateTickRate(UpdateTickRateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         TickManager tickmanager = botworld.getTickManager();
         tickmanager.setTickRate(packet.tickRate());
         tickmanager.setFrozen(packet.isFrozen());
      }
   }

   public void onTickStep(TickStepS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld botworld = this.world;
      if (botworld != null) {
         botworld.getTickManager().setStepTicks(packet.tickSteps());
      }
   }

   public void onPlayerList(PlayerListS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());

      for (net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry entry : packet.getPlayerAdditionEntries()) {
         BotPlayerListEntry botplayerlistentry = new BotPlayerListEntry(Objects.requireNonNull(entry.profile()), false);
         this.playerListEntries.putIfAbsent(entry.profileId(), botplayerlistentry);
      }

      for (net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry entry1 : packet.getEntries()) {
         PlayerListEntry playerlistentry = this.playerListEntries.get(entry1.profileId());
         if (playerlistentry instanceof BotPlayerListEntry botplayerlistentry1) {
            for (Action action : packet.getActions()) {
               this.handlePlayerListAction(action, entry1, botplayerlistentry1);
            }
         } else {
            LOGGER.warn(
               "Bot {}: ignoring player info update for unknown player {} ({})",
               new Object[]{this.client.getName(), entry1.profileId(), packet.getActions()}
            );
         }
      }
   }

   public void handlePlayerListAction(Action var1, net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry var2, BotPlayerListEntry var3) {
      BotPlayer botplayer = this.player;
      switch (var1) {
         case INITIALIZE_CHAT:
            var3.resetBotSession(false);
            break;
         case UPDATE_GAME_MODE:
            if (var3.getGameMode() != var2.gameMode() && botplayer != null && botplayer.getUuid().equals(var2.profileId())) {
               botplayer.onGameModeChanged(var2.gameMode());
            }

            var3.setBotGameMode(var2.gameMode());
            break;
         case UPDATE_LATENCY:
            var3.setBotLatency(var2.latency());
            break;
         case UPDATE_DISPLAY_NAME:
            var3.setDisplayName(var2.displayName());
            break;
         case UPDATE_HAT:
            var3.setShowHat(var2.showHat());
            break;
         case UPDATE_LIST_ORDER:
            var3.setListOrder(var2.listOrder());
         case UPDATE_LISTED:
      }
   }

   public void onPlayerRemove(PlayerRemoveS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());

      for (UUID uuid : packet.profileIds()) {
         this.playerListEntries.remove(uuid);
      }
   }

   public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      String s = packet.getName();
      if (packet.getMode() == 0) {
         this.scoreboard
            .addObjective(s, ScoreboardCriterion.DUMMY, packet.getDisplayName(), packet.getType(), false, (NumberFormat)packet.getNumberFormat().orElse(null));
      } else {
         ScoreboardObjective scoreboardobjective = this.scoreboard.getNullableObjective(s);
         if (scoreboardobjective != null) {
            if (packet.getMode() == 1) {
               this.scoreboard.removeObjective(scoreboardobjective);
            } else if (packet.getMode() == 2) {
               scoreboardobjective.setRenderType(packet.getType());
               scoreboardobjective.setDisplayName(packet.getDisplayName());
               scoreboardobjective.setNumberFormat((NumberFormat)packet.getNumberFormat().orElse(null));
            }
         }
      }
   }

   public void onScoreboardScoreUpdate(ScoreboardScoreUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      String s = packet.objectiveName();
      ScoreHolder scoreholder = ScoreHolder.fromName(packet.scoreHolderName());
      ScoreboardObjective scoreboardobjective = this.scoreboard.getNullableObjective(s);
      if (scoreboardobjective != null) {
         ScoreAccess scoreaccess = this.scoreboard.getOrCreateScore(scoreholder, scoreboardobjective, true);
         scoreaccess.setScore(packet.score());
         scoreaccess.setDisplayText((Text)packet.display().orElse(null));
         scoreaccess.setNumberFormat((NumberFormat)packet.numberFormat().orElse(null));
      } else {
         LOGGER.warn("Bot {}: received packet for unknown scoreboard objective: {}", this.client.getName(), s);
      }
   }

   public void onScoreboardScoreReset(ScoreboardScoreResetS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      String s = packet.objectiveName();
      ScoreHolder scoreholder = ScoreHolder.fromName(packet.scoreHolderName());
      if (s == null) {
         this.scoreboard.removeScores(scoreholder);
      } else {
         ScoreboardObjective scoreboardobjective = this.scoreboard.getNullableObjective(s);
         if (scoreboardobjective != null) {
            this.scoreboard.removeScore(scoreholder, scoreboardobjective);
         }
      }
   }

   public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      String s = packet.getName();
      ScoreboardObjective scoreboardobjective = s == null ? null : this.scoreboard.getNullableObjective(s);
      this.scoreboard.setObjectiveSlot(packet.getSlot(), scoreboardobjective);
   }

   public void onTeam(TeamS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      Operation operation = packet.getTeamOperation();
      Team team;
      if (operation == Operation.ADD) {
         team = this.scoreboard.addTeam(packet.getTeamName());
      } else {
         team = this.scoreboard.getTeam(packet.getTeamName());
         if (team == null) {
            LOGGER.warn("Bot {}: received packet for unknown team {}", this.client.getName(), packet.getTeamName());
            return;
         }
      }

      packet.getTeam().ifPresent(var1 -> {
         team.setDisplayName(var1.getDisplayName());
         team.setColor(var1.getColor());
         team.setFriendlyFlagsBitwise(var1.getFriendlyFlagsBitwise());
         team.setNameTagVisibilityRule(var1.getNameTagVisibilityRule());
         team.setCollisionRule(var1.getCollisionRule());

         team.setPrefix(var1.getPrefix());
         team.setSuffix(var1.getSuffix());
      });
      Operation operation1 = packet.getPlayerListOperation();
      if (operation1 == Operation.ADD) {
         for (String s : packet.getPlayerNames()) {
            this.scoreboard.addScoreHolderToTeam(s, team);
         }
      } else if (operation1 == Operation.REMOVE) {
         for (String s1 : packet.getPlayerNames()) {
            this.scoreboard.removeScoreHolderFromTeam(s1, team);
         }
      }

      if (operation == Operation.REMOVE) {
         this.scoreboard.removeTeam(team);
      }
   }

   public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.recipeManager = new ClientRecipeManager(packet.itemSets(), packet.stonecutterRecipes());
   }

   public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      List<PendingTagLoad<?>> arraylist = new ArrayList<>(packet.getGroups().size());
      packet.getGroups().forEach((var2x, var3) -> {
         if (SerializableRegistries.isSynced(var2x)) {
            arraylist.add(this.startTagReload(var2x, var3));
         }
      });
      arraylist.forEach(PendingTagLoad::apply);
      this.fuelRegistry = FuelRegistry.createDefault(this.registries, this.enabledFeatures);
   }

   public <T> PendingTagLoad<T> startTagReload(RegistryKey<? extends Registry<? extends T>> var1, net.minecraft.registry.tag.TagPacketSerializer.Serialized var2) {
      Registry registry = this.registries.getOrThrow(var1);
      return registry.startTagReload(var2.toRegistryTags(registry));
   }

   public void onGameMessage(GameMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.client.onChat(packet.content());
   }

   public void onChatMessage(ChatMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      Object object = packet.unsignedContent() != null ? packet.unsignedContent() : Text.literal(packet.body().content());
      this.client.onChat(packet.serializedParameters().applyChatDecoration((Text)object));
   }

   public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.client.onChat(packet.chatType().applyChatDecoration(packet.message()));
   }

   public void onRemoveMessage(RemoveMessageS2CPacket packet) {
   }

   public void onParticle(ParticleS2CPacket packet) {
   }

   public void onPlaySound(PlaySoundS2CPacket packet) {
   }

   public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {
   }

   public void onStopSound(StopSoundS2CPacket packet) {
   }

   public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {
   }

   public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {
   }

   public void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {
   }

   public void onStatistics(StatisticsS2CPacket packet) {
   }

   public void onRecipeBookAdd(RecipeBookAddS2CPacket packet) {
   }

   public void onRecipeBookRemove(RecipeBookRemoveS2CPacket packet) {
   }

   public void onRecipeBookSettings(RecipeBookSettingsS2CPacket packet) {
   }

   public void onAdvancements(AdvancementUpdateS2CPacket packet) {
   }

   public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet) {
   }

   public void onCommandTree(CommandTreeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
        this.commandDispatcher = new CommandDispatcher(packet.getCommandTree(CommandRegistryAccess.of(this.registries, this.enabledFeatures), COMMAND_NODE_FACTORY));
   }

   public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.commandSource.onCommandSuggestions(packet.id(), packet.getSuggestions());
   }

   public void onChatSuggestions(ChatSuggestionsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.commandSource.onChatSuggestions(packet.action(), packet.entries());
   }

   public void onNbtQueryResponse(NbtQueryResponseS2CPacket packet) {
   }

   public void onEndCombat(EndCombatS2CPacket packet) {
   }

   public void onEnterCombat(EnterCombatS2CPacket packet) {
   }

   public void onSetCameraEntity(SetCameraEntityS2CPacket packet) {
   }

   public void onTitle(TitleS2CPacket packet) {
   }

   public void onSubtitle(SubtitleS2CPacket packet) {
   }

   public void onOverlayMessage(OverlayMessageS2CPacket packet) {
   }

   public void onTitleFade(TitleFadeS2CPacket packet) {
   }

   public void onTitleClear(ClearTitleS2CPacket packet) {
   }

   public void onServerMetadata(ServerMetadataS2CPacket packet) {
   }

   public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
   }

   public void onBossBar(BossBarS2CPacket packet) {
   }

   public void onDebugSample(DebugSampleS2CPacket packet) {
   }

   @Override
   public void onGameTestHighlightPos(GameTestHighlightPosS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
   }

   @Override
   public void onTestInstanceBlockStatus(TestInstanceBlockStatusS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
   }

   @Override
   public void onWaypoint(WaypointS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
   }

   @Override
   public void onChunkValueDebug(ChunkValueDebugS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
   }

   @Override
   public void onBlockValueDebug(BlockValueDebugS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
   }

   @Override
   public void onEntityValueDebug(EntityValueDebugS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
   }

   @Override
   public void onEventDebug(EventDebugS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
   }

   public void onPingResult(PingResultS2CPacket packet) {
   }

   public NetworkPhase getPhase() {
      return NetworkPhase.PLAY;
   }
}
