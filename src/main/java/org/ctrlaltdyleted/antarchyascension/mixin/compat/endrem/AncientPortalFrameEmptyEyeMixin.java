package org.ctrlaltdyleted.antarchyascension.mixin.compat.endrem;

import org.ctrlaltdyleted.antarchyascension.compat.endrem.EndRemPortalFrameEyeCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.teamremastered.endrem.block.AncientPortalFrame", remap = false)
public abstract class AncientPortalFrameEmptyEyeMixin {
    @Inject(
        method = "newBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
        at = @At("RETURN"),
        remap = false
    )
    private void antarchyascension$clearEyeInEmptyFrame(
        BlockPos pos,
        BlockState state,
        CallbackInfoReturnable<BlockEntity> cir
    ) {
        // Clears the eye name in an empty frame
        if (!state.getValue(BlockStateProperties.EYE)) {
            EndRemPortalFrameEyeCompat.setEye(cir.getReturnValue(), "empty");
        }
    }
}
