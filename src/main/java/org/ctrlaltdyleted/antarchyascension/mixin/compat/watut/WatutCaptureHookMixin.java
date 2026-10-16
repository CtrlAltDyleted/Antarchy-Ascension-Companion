package org.ctrlaltdyleted.antarchyascension.mixin.compat.watut;

import net.minecraft.client.gui.GuiGraphics;
import org.ctrlaltdyleted.antarchyascension.client.compat.watut.WatutCaptureState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.corosus.watut.client.screen.RenderHelper", remap = false)
public abstract class WatutCaptureHookMixin {
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
        WatutCaptureState.markHookCall();
    }
}
