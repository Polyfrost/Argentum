package dev.rdh.argentum.mixin.features.framebuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.rdh.argentum.impl.ext.MinecraftExtension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.render.pipeline.RenderTarget;
import net.minecraft.text.Text;

import java.io.File;

@Mixin(Screenshot.class)
public abstract class ScreenshotMixin {
    @Inject(method = "take(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/render/pipeline/RenderTarget;)Lnet/minecraft/text/Text;", at = @At("HEAD"), cancellable = true, order = 900)
    private static void argentum$deferWhenDirect(File gameDir, String name, int width, int height, RenderTarget target, CallbackInfoReturnable<Text> cir) {
        MinecraftExtension minecraft = (MinecraftExtension) Minecraft.getInstance();
        if (!minecraft.argentum$isRenderingDirect()) return;
        minecraft.argentum$deferScreenshot(() -> Screenshot.take(gameDir, name, width, height, target));
        cir.setReturnValue(null);
    }
}
