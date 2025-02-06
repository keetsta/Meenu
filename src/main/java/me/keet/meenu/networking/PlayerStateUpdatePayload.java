package me.keet.meenu.networking;

import me.keet.meenu.client.PlayerStatus;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

public record PlayerStateUpdatePayload(PlayerStatus playerStatus) implements CustomPayload {

    public static final CustomPayload.Id<PlayerStateUpdatePayload> ID = new CustomPayload.Id<>(NetworkingConstants.SEND_PLAYER_STATUS);
    public static final PacketCodec<RegistryByteBuf, PlayerStateUpdatePayload> CODEC = PacketCodec.tuple(
            PlayerStatus.PACKET_CODEC, PlayerStateUpdatePayload::playerStatus,
            PlayerStateUpdatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(PlayerStateUpdatePayload.ID, PlayerStateUpdatePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            for (ServerPlayerEntity player : PlayerLookup.tracking(context.player().getServerWorld(), context.player().getBlockPos())) {
                if (player == context.player()) {
                    continue;
                }

                ServerPlayNetworking.send(player, new RenderStateUpdatePayload(context.player().getId(), payload.playerStatus));
            }
        });
    }
}
