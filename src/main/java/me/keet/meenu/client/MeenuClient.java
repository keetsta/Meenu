package me.keet.meenu.client;

import me.keet.meenu.client.render.ReferenceRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class MeenuClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register(PlayerStatusManager::onScreenOpen);

        ReferenceRenderer.init();
    }
}
