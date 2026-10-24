package org.ctrlaltdyleted.antarchyascension.mixin.compat.endrem;

import org.ctrlaltdyleted.antarchyascension.compat.endrem.EndRemPortalFrameEyeCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.teamremastered.endrem.item.EREnderEye", remap = false)
public abstract class EndRemEyeInsertionMixin {
    @Inject(
        method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
        at = @At("RETURN"),
        remap = false
    )
    private void antarchyascension$saveInsertedEye(
        UseOnContext context,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        Level level = context.getLevel();
        // Only saves successful insertions on the server
        if (level.isClientSide || cir.getReturnValue() != InteractionResult.CONSUME) {
            return;
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.hasProperty(BlockStateProperties.EYE) || !state.getValue(BlockStateProperties.EYE)) {
            return;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        // Saves the inserted eye and updates clients
        String eye = BuiltInRegistries.ITEM.getKey((Item) (Object) this).getPath();
        if (EndRemPortalFrameEyeCompat.setEye(entity, eye)) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }
}
