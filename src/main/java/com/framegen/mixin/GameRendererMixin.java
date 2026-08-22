package com.framegen.mixin;

import com.framegen.FrameGenMod;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class GameRendererMixin {
    @Inject(method = "setup", at = @At("RETURN"))
    private void applyFrameInterpolation(BlockGetter level, Entity entity, boolean detached,
                                         boolean thirdPerson, float partialTick, CallbackInfo ci) {
        FrameGenMod.frameInterpolator.applyCameraInterpolation(
                (Camera) (Object) this, partialTick);
    }
}
