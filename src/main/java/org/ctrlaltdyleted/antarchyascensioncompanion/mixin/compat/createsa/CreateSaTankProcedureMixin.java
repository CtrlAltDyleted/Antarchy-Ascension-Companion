package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.createsa;

import org.ctrlaltdyleted.antarchyascensioncompanion.compat.curios.CurioEquipmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Pseudo
@Mixin(
        targets = {
                "net.mcreator.createstuffadditions.procedures.SmallFillingTankItemInInventoryTickProcedure",
                "net.mcreator.createstuffadditions.procedures.SmallFuelingTankItemInInventoryTickProcedure",
                "net.mcreator.createstuffadditions.procedures.CreativeFillingTankItemInInventoryTickProcedure"
        },
        remap = false
)
public abstract class CreateSaTankProcedureMixin {
    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            ),
            require = 1
    )
    private static ItemStack antarchy$useDedicatedJetpackForTankTransfer(
            LivingEntity entity,
            EquipmentSlot slot) {

        return CurioEquipmentHelper.resolveCreateSaTankTarget(entity, slot);
    }
}
