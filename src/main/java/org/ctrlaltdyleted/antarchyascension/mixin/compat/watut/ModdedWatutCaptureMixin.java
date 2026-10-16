package org.ctrlaltdyleted.antarchyascension.mixin.compat.watut;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.ctrlaltdyleted.antarchyascension.client.compat.watut.WatutCaptureState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.neoforged.neoforge.client.ClientHooks", remap = false)
public abstract class ModdedWatutCaptureMixin {
    @WrapOperation(
        method = "drawScreenInternal(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"),
        require = 1,
        expect = 1,
        allow = 1,
        remap = false
    )
    private static void antarchyascension$captureScreensMissingWatutHook(Screen screen,
            GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
            Operation<Void> original) {
        long hookCallsBefore = WatutCaptureState.hookCalls();
        original.call(screen, graphics, mouseX, mouseY, partialTick);

        // Preserve WATUT's normal hook; supply it only for overrides that bypass it.
        if (WatutCaptureState.hookCalls() == hookCallsBefore
                && Minecraft.getInstance().screen == screen
                && !WatutRenderHelperAccessor.antarchyascension$isCapturing()) {
            WatutRenderHelperAccessor.antarchyascension$capture(
                    graphics, mouseX, mouseY, partialTick);
        }
    }
}
