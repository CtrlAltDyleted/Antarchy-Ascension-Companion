package org.ctrlaltdyleted.antarchyascension.mixin.compat.watut;

import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "com.corosus.watut.client.screen.RenderHelper", remap = false)
public abstract class WatutGuiProjectionMixin {
    @ModifyConstant(
        method = "renderWithTooltipEnd(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        constant = @Constant(floatValue = 21000.0F),
        require = 1,
        expect = 1,
        allow = 1,
        remap = false
    )
    private static float antarchyascension$useCurrentGuiFarPlane(float original) {
        // WATUT's NeoForge getFarPlane() delegates to this same helper.
        return ClientHooks.getGuiFarPlane();
    }
}
