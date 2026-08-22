package com.framegen;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class FrameGenConfigScreen extends Screen {
    private final Screen parent;

    public FrameGenConfigScreen(Screen parent) {
        super(Component.literal("FrameGen Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = width / 2;
        addRenderableWidget(Button.builder(multiplierLabel(), button -> {
            int next = FrameGenConfig.getFrameMultiplier() + 1;
            FrameGenConfig.setFrameMultiplier(next > 8 ? 2 : next);
            button.setMessage(multiplierLabel());
        }).bounds(center - 100, 70, 200, 20).build());

        double sliderValue = (FrameGenConfig.getFlowScale() - 0.25f) / 1.75f;
        addRenderableWidget(new AbstractSliderButton(
                center - 100, 105, 200, 20, flowScaleLabel(FrameGenConfig.getFlowScale()), sliderValue) {
            @Override
            protected void updateMessage() {
                setMessage(flowScaleLabel((float) (0.25 + value * 1.75)));
            }

            @Override
            protected void applyValue() {
                FrameGenConfig.setFlowScale((float) (0.25 + value * 1.75));
            }
        });

        addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
            FrameGenConfig.save();
            minecraft.setScreen(parent);
        }).bounds(center - 100, 145, 200, 20).build());
    }

    @Override
    public void onClose() {
        FrameGenConfig.save();
        minecraft.setScreen(parent);
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX,
                       int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(font, title, width / 2, 35, 0xFFFFFF);
        guiGraphics.drawCenteredString(font,
                Component.literal("Target multiplier; actual output is limited by the render loop"),
                width / 2, 180, 0xAAAAAA);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private static Component multiplierLabel() {
        return Component.literal("Frame multiplier: " + FrameGenConfig.getFrameMultiplier() + "x");
    }

    private static Component flowScaleLabel(float value) {
        return Component.literal(String.format(Locale.ROOT, "Flow scale: %.2fx", value));
    }
}