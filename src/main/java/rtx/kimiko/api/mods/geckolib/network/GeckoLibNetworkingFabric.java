package rtx.kimiko.api.mods.geckolib.network;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import rtx.kimiko.api.mods.geckolib.GeckoLibClient;
import rtx.kimiko.api.mods.geckolib.GeckoLibServices;
import rtx.kimiko.api.mods.geckolib.network.packet.MultiloaderPacket;
import rtx.kimiko.api.mods.geckolib.service.GeckoLibNetworking;

public final class GeckoLibNetworkingFabric
implements GeckoLibNetworking {
    @Override
    public void sendToAllPlayersTrackingBlock(MultiloaderPacket multiloaderPacket, ServerWorld serverWorld, BlockPos blockPos) {
        for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.tracking((ServerWorld)serverWorld, (BlockPos)blockPos)) {
            this.sendToPlayer(multiloaderPacket, serverPlayerEntity);
        }
    }

    @Override
    public void sendToAllPlayersTrackingEntity(MultiloaderPacket multiloaderPacket, Entity entity) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            this.sendToPlayer(multiloaderPacket, serverPlayerEntity);
        }
        for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.tracking((Entity)entity)) {
            this.sendToPlayer(multiloaderPacket, serverPlayerEntity);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <B extends PacketByteBuf, P extends MultiloaderPacket> void registerPacketInternal(CustomPayload.Id<P> id, PacketCodec<B, P> packetCodec, boolean bl) {
        if (bl) {
            PayloadTypeRegistry.playS2C().register(id, (PacketCodec) packetCodec);
            if (GeckoLibServices.PLATFORM.isPhysicalClient()) {
                GeckoLibClient.registerPacket(id);
            }
        } else {
            PayloadTypeRegistry.playC2S().register(id, (PacketCodec) packetCodec);
            ServerPlayNetworking.registerGlobalReceiver(id, (multiloaderPacket, context) -> multiloaderPacket.receiveMessage((PlayerEntity)context.player(), arg_0 -> ((MinecraftServer)context.player().getEntityWorld().getServer()).execute(arg_0)));
        }
    }

    @Override
    public void sendToPlayer(MultiloaderPacket multiloaderPacket, ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send((ServerPlayerEntity)serverPlayerEntity, (CustomPayload)multiloaderPacket);
    }
}

