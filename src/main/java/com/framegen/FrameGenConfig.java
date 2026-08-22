package com.framegen;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class FrameGenConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("framegen.properties");

    private static int frameMultiplier = 2;
    private static float flowScale = 1.0f;

    private FrameGenConfig() {
    }

    public static void load() {
        Properties properties = new Properties();
        if (Files.exists(CONFIG_PATH)) {
            try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
                properties.load(input);
            } catch (IOException ignored) {
                return;
            }
        }

        try {
            frameMultiplier = clampMultiplier(Integer.parseInt(
                    properties.getProperty("frameMultiplier", "2")));
            flowScale = clampFlowScale(Float.parseFloat(
                    properties.getProperty("flowScale", "1.0")));
        } catch (NumberFormatException ignored) {
            frameMultiplier = 2;
            flowScale = 1.0f;
        }
    }

    public static void save() {
        Properties properties = new Properties();
        properties.setProperty("frameMultiplier", Integer.toString(frameMultiplier));
        properties.setProperty("flowScale", Float.toString(flowScale));
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (OutputStream output = Files.newOutputStream(CONFIG_PATH)) {
                properties.store(output, "FrameGen settings");
            }
        } catch (IOException ignored) {
            FrameGenMod.LOGGER.warn("Unable to save FrameGen settings");
        }
    }

    public static int getFrameMultiplier() {
        return frameMultiplier;
    }

    public static void setFrameMultiplier(int value) {
        frameMultiplier = clampMultiplier(value);
    }

    public static float getFlowScale() {
        return flowScale;
    }

    public static void setFlowScale(float value) {
        flowScale = clampFlowScale(value);
    }

    private static int clampMultiplier(int value) {
        return Math.max(2, Math.min(8, value));
    }

    private static float clampFlowScale(float value) {
        return Math.max(0.25f, Math.min(2.0f, value));
    }
}