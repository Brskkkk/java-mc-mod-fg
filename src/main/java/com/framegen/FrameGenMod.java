package com.framegen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameGenMod implements ClientModInitializer {
    public static final String MOD_ID = "framegen";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static final FrameInterpolator frameInterpolator = new FrameInterpolator();

    @Override
    public void onInitializeClient() {
        FrameGenConfig.load();
        LOGGER.info("FrameGen mod initialized");
        
        // Hook into render tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                frameInterpolator.tick();
            }
        });
        
        // Render HUD overlay with stats
        HudRenderCallback.EVENT.register((guiGraphics, tickDeltaTime) -> {
            frameInterpolator.renderHUD(guiGraphics, tickDeltaTime);
        });
    }
}
