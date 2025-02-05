package me.keet.meenu.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class PlayerStatusManager {
    static PlayerStatus playerStatus;

    public static void onScreenOpen(Object client, Screen screen, int scaledWidth, int scaledHeight) {
        if (screen instanceof InventoryScreen) {
            playerStatus = PlayerStatus.INVENTORY;
        } else if (screen instanceof CraftingScreen) {
            playerStatus = PlayerStatus.CRAFTING;
        }
    }
}
