package org.ctrlaltdyleted.antarchyascension.mixin.compat.watut;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.corosus.watut.client.screen.RenderHelper", remap = false)
public abstract class WatutCaptureBuffersMixin {
    @Inject(
        method = "renderWithTooltipEnd(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At(value = "INVOKE",
            target = "Lcom/corosus/watut/client/screen/RenderHelper;unbindVanillaRenderTarget()V"),
        require = 1,
        expect = 1,
        allow = 1,
        remap = false
    )
    private static void antarchyascension$finishMainGuiBeforeCapture(GuiGraphics graphics,
            int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        // Keep queued on-screen geometry out of the offscreen capture.
        graphics.flush();
    }

    @Inject(
        method = "renderWithTooltipEnd(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At(value = "INVOKE",
            target = "Lcom/corosus/watut/client/screen/ScreenParticleRenderer;unbind()V",
            ordinal = 0),
        require = 1,
        expect = 1,
        allow = 1,
        remap = false
    )
    private static void antarchyascension$finishCapturedGuiBeforeReadback(GuiGraphics graphics,
            int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        // Tooltip text/components must draw before WATUT unbinds and downsamples.
        graphics.flush();
    }
}
