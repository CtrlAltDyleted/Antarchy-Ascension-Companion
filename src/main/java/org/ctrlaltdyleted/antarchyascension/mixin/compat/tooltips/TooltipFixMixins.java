package org.ctrlaltdyleted.antarchyascension.mixin.compat.tooltips;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Fixes native tooltips
public final class TooltipFixMixins {
    private TooltipFixMixins() {}

    @Mixin(targets = {
            "com.reggarf.mods.create_better_motors.content.alternator.blocks.BrassAlternatorBlock",
            "com.reggarf.mods.create_better_motors.content.alternator.blocks.AndesiteAlternatorBlock",
            "com.reggarf.mods.create_better_motors.content.alternator.blocks.CopperAlternatorBlock"
    }, remap = false)
    public abstract static class AlternatorTooltips {
        @ModifyArg(method = "appendHoverText", at = @At(value = "INVOKE", target =
                "Lnet/createmod/catnip/lang/LangBuilder;translate(Ljava/lang/String;[Ljava/lang/Object;)Lnet/createmod/catnip/lang/LangBuilder;"), index = 0)
        private String antarchyascension$unitOnly(String key) {
            // Changes the alternator unit text
            return key.equals("tooltip.create_better_motors.energy_per_tick")
                    ? "tooltip.antarchyascension.energy_per_tick_unit" : key;
        }
    }
    // Keeps the native wing attribute tooltip
    @Mixin(targets = "top.theillusivec4.curios.client.ClientEventHandler", remap = false)
    public abstract static class WingAttributeTooltips {
        @org.spongepowered.asm.mixin.injection.Inject(method = "onAttributeTooltip", at = @At("HEAD"), cancellable = true)
        private void antarchyascension$nativeWingAttributes(
                net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent event,
                org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
            // Removes duplicate wing attribute tooltips
            if (org.ctrlaltdyleted.antarchyascension.compat.curios.CurioEquipmentHelper.isAntarchyWings(event.getStack())) ci.cancel();
        }
    }
}
