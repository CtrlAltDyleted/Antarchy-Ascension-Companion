package org.ctrlaltdyleted.antarchyascension.mixin.compat.immersiveengineering;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "blusunrize.immersiveengineering.common.blocks.multiblocks.logic.AdvBlastFurnaceLogic$State", remap = false)
public abstract class BlastFurnacePreheaterSpeedMixin {
    @ModifyReturnValue(method = "getProcessSpeed", at = @At("RETURN"), require = 1)
    private int antarchyascension$doubleSpeedPerPoweredPreheater(int original) {
        return original == 3 ? 4 : original;
    }
}
