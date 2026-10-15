package org.ctrlaltdyleted.antarchyascension.mixin.compat.appliedpneumatics;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.ServerTickingBlockEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AEBaseEntityBlock.class)
public abstract class AE2ServerTickerCompatMixin {
    @Shadow private Class<?> blockEntityClass;

    @Inject(method = "getTicker", at = @At("RETURN"), cancellable = true)
    private <T extends BlockEntity> void antarchyascension$restoreServerTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type,
            CallbackInfoReturnable<BlockEntityTicker<T>> cir) {

        // This is a missing-ticker fallback; never bypass upstream ticker behavior.
        if (level.isClientSide() || cir.getReturnValue() != null) {
            return;
        }

        AEBaseEntityBlock<?> block = (AEBaseEntityBlock<?>)(Object)this;
        if (type != block.getBlockEntityType() || blockEntityClass == null
                || !ServerTickingBlockEntity.class.isAssignableFrom(blockEntityClass)) {
            return;
        }

        cir.setReturnValue((tickLevel, pos, tickState, blockEntity) -> {
            if (blockEntity instanceof ServerTickingBlockEntity ticking) {
                ticking.serverTick();
            }
        });
    }
}
