package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.appliedpneumatics;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.ServerTickingBlockEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AEBaseEntityBlock.class)
public abstract class AppliedPneumaticsAmadronTickerCompatMixin {
    @Inject(
            method = "getTicker",
            at = @At("RETURN"),
            cancellable = true)
    private <T extends BlockEntity> void antarchyascensioncompanion$restoreAmadronServerTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type,
            CallbackInfoReturnable<BlockEntityTicker<T>> cir) {

        if (level.isClientSide()) {
            return;
        }

        if (!this.getClass().getName().equals(
                "com.wintercogs.appliedpneumatics.common.blocks.MEAmadronProcessStation")) {
            return;
        }

        cir.setReturnValue(
                (tickLevel, pos, tickState, blockEntity) -> {
                    if (blockEntity instanceof ServerTickingBlockEntity ticking) {
                        ticking.serverTick();
                    }
                });
    }
}
