package me.keet.meenu.networking;

import me.keet.meenu.client.PlayerStatus;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record RenderStateUpdatePayload(int sender, PlayerStatus playerStatus) implements CustomPayload {

    public static final CustomPayload.Id<RenderStateUpdatePayload> ID = new CustomPayload.Id<>(NetworkingConstants.SEND_RENDERING);
    public static final PacketCodec<RegistryByteBuf, RenderStateUpdatePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RenderStateUpdatePayload::sender,
            PlayerStatus.PACKET_CODEC, RenderStateUpdatePayload::playerStatus,
            RenderStateUpdatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(RenderStateUpdatePayload.ID, RenderStateUpdatePayload.CODEC);
    }
}
