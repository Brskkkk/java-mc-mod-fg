package com.framegen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class FrameInterpolator {
    private long interpolatedFrames = 0L;
    private boolean hasSnapshot;
    private double previousX, previousY, previousZ;
    private double currentX, currentY, currentZ;
    private float previousYaw, previousPitch;
    private float currentYaw, currentPitch;
    private float motionX, motionY, motionZ;
    private float motionYaw, motionPitch;
    private boolean shouldInterpolate = false;

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) return;

        LocalPlayer player = client.player;
        Vec3 pos = player.position();

        if (!hasSnapshot) {
            previousX = currentX = pos.x;
            previousY = currentY = pos.y;
            previousZ = currentZ = pos.z;
            previousYaw = currentYaw = player.getYRot();
            previousPitch = currentPitch = player.getXRot();
            hasSnapshot = true;
            return;
        }

        previousX = currentX;
        previousY = currentY;
        previousZ = currentZ;
        previousYaw = currentYaw;
        previousPitch = currentPitch;
        currentX = pos.x;
        currentY = pos.y;
        currentZ = pos.z;
        currentYaw = player.getYRot();
        currentPitch = player.getXRot();

        double distanceSquared = squaredDistance(previousX, previousY, previousZ,
            currentX, currentY, currentZ);
        if (distanceSquared > 64.0) {
            previousX = currentX;
            previousY = currentY;
            previousZ = currentZ;
            previousYaw = currentYaw;
            previousPitch = currentPitch;
        }

        motionX = (float) (currentX - previousX);
        motionY = (float) (currentY - previousY);
        motionZ = (float) (currentZ - previousZ);
        motionYaw = angleDifference(currentYaw, previousYaw);
        motionPitch = currentPitch - previousPitch;
        shouldInterpolate = Math.abs(motionX) > 0.001f
                || Math.abs(motionY) > 0.001f
                || Math.abs(motionZ) > 0.001f
                || Math.abs(motionYaw) > 0.05f
                || Math.abs(motionPitch) > 0.05f;
    }

    public void applyCameraInterpolation(Camera camera, float partialTick, boolean thirdPerson) {
        if (!hasSnapshot || !shouldInterpolate || camera.isDetached()
            || camera.getEntity() != Minecraft.getInstance().player) return;

        float flowScale = FrameGenConfig.getFlowScale();
        float alpha = 0.5f + (partialTick - 0.5f) * flowScale;
        alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        Vec3 cameraOffset = camera.getPosition().subtract(currentX, currentY, currentZ);
        float cameraYawOffset = angleDifference(camera.getYRot(), currentYaw);
        float cameraPitchOffset = camera.getXRot() - currentPitch;
        camera.setPosition(
            lerp(previousX, currentX, alpha) + cameraOffset.x,
            lerp(previousY, currentY, alpha) + cameraOffset.y,
            lerp(previousZ, currentZ, alpha) + cameraOffset.z);
        camera.setRotation(
            interpolateAngle(previousYaw, currentYaw, alpha) + cameraYawOffset,
            lerp(previousPitch, currentPitch, alpha) + cameraPitchOffset);
        interpolatedFrames++;
    }

    public void renderHUD(GuiGraphics guiGraphics, float tickDeltaTime) {
        Minecraft client = Minecraft.getInstance();

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        // Get actual FPS from debug info
        int currentFps = client.getFps();
        String fpsString = currentFps + " fps";
        
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
        String generatedText = "Interpolated Frames: " + interpolatedFrames;
        guiGraphics.drawString(client.font, generatedText, x, y + lineHeight, 0x00FF00);
        
        // Estimated total with frame gen
        guiGraphics.drawString(client.font, "Camera interpolation: " + (shouldInterpolate ? "ON" : "IDLE"), x, y + lineHeight*2, 0xFFFF00);
        
        // Motion detection
        float motionMag = (float) Math.sqrt(motionX*motionX + motionY*motionY + motionZ*motionZ);
        String motionText = String.format("Motion: %.3f", motionMag);
        int motionColor = shouldInterpolate ? 0x00FF00 : 0xFF0000;
        guiGraphics.drawString(client.font, motionText, x, y + lineHeight*3, motionColor);
        
        // Status
        String statusText = shouldInterpolate ? "INTERPOLATING" : "IDLE";
        int statusColor = shouldInterpolate ? 0x00FF00 : 0xFF0000;
        guiGraphics.drawString(client.font, statusText, x, y + lineHeight*4, statusColor);
    }

    private static double lerp(double start, double end, float alpha) {
        return start + (end - start) * alpha;
    }

    private static float lerp(float start, float end, float alpha) {
        return start + (end - start) * alpha;
    }

    private static float interpolateAngle(float start, float end, float alpha) {
        return start + angleDifference(end, start) * alpha;
    }

    private static double squaredDistance(double x1, double y1, double z1,
                                          double x2, double y2, double z2) {
        double x = x2 - x1;
        double y = y2 - y1;
        double z = z2 - z1;
        return x * x + y * y + z * z;
    }

    private static float angleDifference(float end, float start) {
        return (end - start + 540.0f) % 360.0f - 180.0f;
    }
}
