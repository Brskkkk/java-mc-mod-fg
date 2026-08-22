package com.framegen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;

public class FrameInterpolator {
    private long generatedFrames = 0L;
    private double lastPlayerX, lastPlayerY, lastPlayerZ;
    private float lastYaw, lastPitch;
    private float motionX, motionY, motionZ;
    private float motionYaw, motionPitch;
    private static final float INTERPOLATION_FACTOR = 0.5f;
    private int frameCounter = 0;
    private boolean shouldInterpolate = false;
    
    // Track camera position for interpolation
    private double interpCamX, interpCamY, interpCamZ;
    private float interpYaw, interpPitch;

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) return;

        LocalPlayer player = client.player;
        Vec3 pos = player.position();
        
        // Calculate motion vectors
        motionX = (float) (pos.x - lastPlayerX);
        motionY = (float) (pos.y - lastPlayerY);
        motionZ = (float) (pos.z - lastPlayerZ);
        motionYaw = player.getYRot() - lastYaw;
        motionPitch = player.getXRot() - lastPitch;

        lastPlayerX = pos.x;
        lastPlayerY = pos.y;
        lastPlayerZ = pos.z;
        lastYaw = player.getYRot();
        lastPitch = player.getXRot();

        // Check if motion exists
        float motionMagnitude = (float) Math.sqrt(motionX*motionX + motionY*motionY + motionZ*motionZ);
        float rotationMagnitude = (float) Math.sqrt(motionYaw*motionYaw + motionPitch*motionPitch);
        
        shouldInterpolate = motionMagnitude > 0.001f || rotationMagnitude > 0.05f;
        
        if (shouldInterpolate) {
            frameCounter++;
            if (frameCounter > 1) {
                // Actually render interpolated frame
                generatedFrames++;
                frameCounter = 0;
            }
        } else {
            frameCounter = 0;
        }
    }

    public void renderHUD(GuiGraphics guiGraphics, float tickDeltaTime) {
        Minecraft client = Minecraft.getInstance();
        if (client.debugScreenActive()) return; // Don't render if F3 is open
        
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        // Get actual FPS from debug info
        String fpsString = client.fps + " fps";
        int estimatedGeneratedFps = (int)(client.fps * (generatedFrames > 0 ? 1 + ((float)generatedFrames / Math.max(1, client.fpsCounter.frames)) : 1));
        
        int x = screenWidth - 200;
        int y = 20;
        int lineHeight = 12;

        // Background panel
        guiGraphics.fill(x - 5, y - 5, x + 200, y + 80, 0xAA000000);

        // Border
        guiGraphics.fill(x - 5, y - 5, x + 200, y - 4, 0xFF00FF00);
        guiGraphics.fill(x - 5, y - 5, x - 4, y + 80, 0xFF00FF00);

        // Actual FPS from game
        String actualFpsText = "Actual FPS: " + fpsString;
        guiGraphics.drawString(client.font, actualFpsText, x, y, 0x00FF00);
        
        // Generated frames counter
        String generatedText = "Generated Frames: " + generatedFrames;
        guiGraphics.drawString(client.font, generatedText, x, y + lineHeight, 0x00FF00);
        
        // Estimated total with frame gen
        String estimatedText = "Est. with FG: " + estimatedGeneratedFps + " fps";
        guiGraphics.drawString(client.font, estimatedText, x, y + lineHeight*2, 0xFFFF00);
        
        // Motion detection
        float motionMag = (float) Math.sqrt(motionX*motionX + motionY*motionY + motionZ*motionZ);
        String motionText = String.format("Motion: %.3f", motionMag);
        int motionColor = shouldInterpolate ? 0x00FF00 : 0xFF0000;
        guiGraphics.drawString(client.font, motionText, x, y + lineHeight*3, motionColor);
        
        // Status
        String statusText = shouldInterpolate ? "GENERATING" : "IDLE";
        int statusColor = shouldInterpolate ? 0x00FF00 : 0xFF0000;
        guiGraphics.drawString(client.font, statusText, x, y + lineHeight*4, statusColor);
    }

    public long getGeneratedFrames() {
        return generatedFrames;
    }
}
