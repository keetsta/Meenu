package me.keet.meenu.client;

import com.google.gson.internal.GsonBuildConfig;
import me.keet.meenu.networking.PlayerStateUpdatePayload;
import me.keet.meenu.networking.RenderStateUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class PlayerStatusManager {
    static PlayerStatus playerStatus = PlayerStatus.NONE;

    public static void onScreenOpen(Object client, Screen screen, int scaledWidth, int scaledHeight) {
        if (MinecraftClient.getInstance().getNetworkHandler() == null) {
            return;
        }

        ScreenEvents.remove(screen).register(PlayerStatusManager::onScreenClosed);

        if (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen) {
            playerStatus = PlayerStatus.INVENTORY;
        } else if (screen instanceof CraftingScreen) {
            playerStatus = PlayerStatus.CRAFTING;
        } else if (screen instanceof GameMenuScreen) {
            playerStatus = PlayerStatus.ESCAPE;
        } else {
            System.err.println("Unknown screen: " + screen.getClass().getName());
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
