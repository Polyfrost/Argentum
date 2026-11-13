package dev.rdh.argentum.mixin.features.framebuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.rdh.argentum.impl.Argentum;
import dev.rdh.argentum.impl.ext.MinecraftExtension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.platform.GLX;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.text.Text;

import java.util.function.Supplier;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftExtension {
    @Shadow
    public GameRenderer gameRenderer;

    @Shadow
    public WorldRenderer worldRenderer;

    @Shadow
    public Screen screen;

    @Shadow
    public GameGui gui;

    @Unique
    private boolean argentum$renderingDirect;

    @Unique
    private boolean argentum$framebufferRequested;

    @Unique
    private Supplier<Text> argentum$pendingScreenshot;

    @Override
    public boolean argentum$isRenderingDirect() {
        return this.argentum$renderingDirect;
    }

    @Override
    public void argentum$requestFramebuffer() {
        this.argentum$framebufferRequested = true;
    }

    @Inject(method = "runGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/pipeline/RenderTarget;bindWrite(Z)V"))
    private void argentum$chooseFramebuffer(CallbackInfo ci) {
        this.argentum$renderingDirect = Argentum.CONFIG.directRendering() && GLX.useFbo()
                && this.screen == null
                && !this.gameRenderer.hasShader()
                && !((WorldRendererInvoker) this.worldRenderer).invokeShouldRenderEntityOutlines()
                && !this.argentum$framebufferRequested;
        this.argentum$framebufferRequested = false;
    }

    @Override
    public void argentum$deferScreenshot(Supplier<Text> screenshot) {
        this.argentum$pendingScreenshot = screenshot;
        this.argentum$framebufferRequested = true;
    }

    @Inject(method = "runGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;updateDisplay()V"))
    private void argentum$takeScreenshot(CallbackInfo ci) {
        Supplier<Text> screenshot = this.argentum$pendingScreenshot;
        if (screenshot == null) return;
        this.argentum$pendingScreenshot = null;
        this.gui.getChat().addMessage(screenshot.get());
    }
}
