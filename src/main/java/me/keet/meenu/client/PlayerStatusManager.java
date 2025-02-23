package me.keet.meenu.client;

import me.keet.meenu.networking.PlayerStateUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.option.OptionsScreen;

public class PlayerStatusManager {
    static PlayerStatus playerStatus = PlayerStatus.NONE;

    public static void onScreenOpen(Object client, Screen screen, int scaledWidth, int scaledHeight) {
        if (MinecraftClient.getInstance().getNetworkHandler() == null) {
            return;
        }

        ScreenEvents.remove(screen).register(PlayerStatusManager::onScreenClosed);

        if (screen instanceof GameMenuScreen || screen instanceof OptionsScreen || screen instanceof AdvancementsScreen) {
            playerStatus = PlayerStatus.ESCAPE;
        } else if (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen) {
            playerStatus = PlayerStatus.INVENTORY;
        } else if (screen instanceof CraftingScreen || screen instanceof CrafterScreen) {
            playerStatus = PlayerStatus.CRAFTING;
        } else if (screen instanceof ChatScreen) {
            playerStatus = PlayerStatus.CHAT_SCREEN;
        } else if (screen instanceof SignEditScreen) {
            playerStatus = PlayerStatus.EDIT_SIGN;
        } else if (screen instanceof BookEditScreen) {
            playerStatus = PlayerStatus.EDIT_BOOK;
        } else if (screen instanceof GenericContainerScreen || screen instanceof ShulkerBoxScreen) {
            playerStatus = PlayerStatus.CHEST;
        } else if (screen instanceof EnchantmentScreen) {
            playerStatus = PlayerStatus.ENCHANTING_TABLE;
        } else if (screen instanceof AnvilScreen) {
            playerStatus = PlayerStatus.ANVIL;
        } else if (screen instanceof BeaconScreen) {
            playerStatus = PlayerStatus.BEACON;
        } else if (screen instanceof BrewingStandScreen) {
            playerStatus = PlayerStatus.BREWING_STAND;
        } else if (screen instanceof Generic3x3ContainerScreen) {
            playerStatus = PlayerStatus.DISPENSER;
        } else if (screen instanceof FurnaceScreen || screen instanceof SmokerScreen || screen instanceof BlastFurnaceScreen) {
            playerStatus = PlayerStatus.FURNACE;
        } else if (screen instanceof GrindstoneScreen) {
            playerStatus = PlayerStatus.GRINDSTONE;
        } else if (screen instanceof HopperScreen) {
            playerStatus = PlayerStatus.HOPPER;
        } else if (screen instanceof HorseScreen) {
            playerStatus = PlayerStatus.HORSE;
        } else if (screen instanceof LoomScreen) {
            playerStatus = PlayerStatus.LOOM;
        } else if (screen instanceof MerchantScreen) {
            playerStatus = PlayerStatus.VILLAGER;
        } else if (screen instanceof CommandBlockScreen) {
            playerStatus = PlayerStatus.COMMAND_BLOCK;
        } else {
            System.out.println(screen.getClass());
        }

        ClientPlayNetworking.send(new PlayerStateUpdatePayload(playerStatus));
    }

    private static void onScreenClosed(Screen screen) {
        if (MinecraftClient.getInstance().getNetworkHandler() == null) {
            return;
        }

        playerStatus = PlayerStatus.NONE;

        ClientPlayNetworking.send(new PlayerStateUpdatePayload(playerStatus));
    }
}
