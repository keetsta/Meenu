package me.keet.meenu.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record StateUpdatePayload(BlockPos blockPos, UUID sender) implements CustomPayload {

    public static final CustomPayload.Id<StateUpdatePayload> ID = new CustomPayload.Id<>(NetworkingConstants.ENABLE_RENDERING);
    public static final PacketCodec<RegistryByteBuf, StateUpdatePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, StateUpdatePayload::blockPos,
            Uuids.PACKET_CODEC, StateUpdatePayload::sender,
            StateUpdatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(StateUpdatePayload.ID, StateUpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StateUpdatePayload.ID, StateUpdatePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            for (ServerPlayerEntity player : PlayerLookup.tracking(context.player().getServerWorld(), payload.blockPos)) {
                ServerPlayNetworking.send(player, payload);
            }
        });
    }
}
