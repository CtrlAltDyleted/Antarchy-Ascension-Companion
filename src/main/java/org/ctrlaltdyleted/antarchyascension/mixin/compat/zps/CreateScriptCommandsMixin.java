package org.ctrlaltdyleted.antarchyascension.mixin.compat.zps;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "g_mungus.zps.compat.create.commands.CreateScriptCommands", remap = false)
public abstract class CreateScriptCommandsMixin {
    @Redirect(
        method = "registerScriptCommands",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/EntityBlock;newBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/entity/BlockEntity;"),
        require = 1
    )
    private static BlockEntity antarchyascension$onlyConstructCreateEntities(
        EntityBlock block, BlockPos pos, BlockState state
    ) {
        // Checks the Create block type before creating its entity
        if (!(block instanceof IBE<?> createBlock)
            || !SmartBlockEntity.class.isAssignableFrom(createBlock.getBlockEntityClass())) {
            return null;
        }
        // Creates the block entity after checking its type
        return block.newBlockEntity(pos, state);
    }
}
