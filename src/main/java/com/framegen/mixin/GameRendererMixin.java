package com.framegen.mixin;

import com.framegen.FrameGenMod;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "render(FJZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;reset()V",
                    shift = At.Shift.AFTER))
    private void applyFrameInterpolation(float partialTick, long nanoTime, boolean renderLevel,
                                         CallbackInfo ci) {
        FrameGenMod.frameInterpolator.applyCameraInterpolation(
                ((GameRenderer) (Object) this).getMainCamera(), partialTick);
    }
}
