package dev.rdh.cera.mixin.shaders;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.rdh.cera.modules.shaders.ShaderRender;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.block.entity.SignRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SignRenderer.class, priority = 1100)
public abstract class SignRendererMixin {
    @WrapMethod(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDFI)V")
    private void cera$popSignText(SignBlockEntity sign, double x, double y, double z, float tickDelta, int breakProgress, Operation<Void> original) {
        try {
            original.call(sign, x, y, z, tickDelta, breakProgress);
        } finally {
            ShaderRender.popTextBatch();
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;depthMask(Z)V", ordinal = 0, shift = At.Shift.AFTER))
    private void cera$pushSignText(SignBlockEntity sign, double x, double y, double z, float tickDelta, int breakProgress, CallbackInfo ci) {
        ShaderRender.pushTextBatch();
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;DDDFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;depthMask(Z)V", ordinal = 1, shift = At.Shift.AFTER))
    private void cera$endSignText(SignBlockEntity sign, double x, double y, double z, float tickDelta, int breakProgress, CallbackInfo ci) {
        ShaderRender.popTextBatch();
    }
}
