package org.ctrlaltdyleted.antarchyascension.mixin.compat.watut;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.ClientHooks;
import org.ctrlaltdyleted.antarchyascension.client.compat.watut.WatutCaptureState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        // Finishes drawing before capture
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
        // Finishes the tooltip before reading the capture
        graphics.flush();
    }

    @Inject(
        method = "renderWithTooltipEnd(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At("HEAD"),
        require = 1,
        expect = 1,
        allow = 1,
        remap = false
    )
    private static void antarchyascension$recordCaptureHook(GuiGraphics graphics,
            int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        // Records that the capture hook ran
        WatutCaptureState.markHookCall();
    }

    @ModifyConstant(
        method = "renderWithTooltipEnd(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        constant = @Constant(floatValue = 21000.0F),
        require = 1,
        expect = 1,
        allow = 1,
        remap = false
    )
    private static float antarchyascension$useCurrentGuiFarPlane(float original) {
        // Uses the current GUI draw distance
        return ClientHooks.getGuiFarPlane();
    }
}
