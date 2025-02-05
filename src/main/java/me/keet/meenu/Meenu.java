package me.keet.meenu;

import me.keet.meenu.networking.StateUpdatePayload;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Meenu implements ModInitializer {

    public static final String MOD_ID = "meenu";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        StateUpdatePayload.initialize();
    }
}
