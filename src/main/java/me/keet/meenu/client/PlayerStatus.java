package me.keet.meenu.client;

import me.keet.meenu.Meenu;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public enum PlayerStatus {
    NONE,
    ESCAPE,
    INVENTORY,
    CRAFTING,
    CHAT_SCREEN,
    EDIT_SIGN,
    EDIT_BOOK,
    CHEST,
    ENCHANTING_TABLE,
    ANVIL,
    BEACON,
    BREWING_STAND,
    DISPENSER,
    FURNACE,
    GRINDSTONE,
    HOPPER,
    HORSE,
    LOOM,
    VILLAGER,
    COMMAND_BLOCK,
    ;

    public final Identifier texturePath = Identifier.of(Meenu.MOD_ID, "textures/menus/" + this.name().toLowerCase() + ".png");

    public static final PacketCodec<PacketByteBuf, PlayerStatus> PACKET_CODEC = PacketCodec.of(
            // encoder: writing to the packet
            (value, buf) -> buf.writeEnumConstant(value),
            // decoder: reading the packet
            buf -> buf.readEnumConstant(PlayerStatus.class)
    );
}