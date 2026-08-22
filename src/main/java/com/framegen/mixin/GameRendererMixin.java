package com.framegen.mixin;

import com.framegen.FrameGenMod;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderStart(float partialTick, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        // Signal that we're about to render a frame
        // This is where interpolation would happen
        FrameGenMod.frameInterpolator.tick();
    }
}
