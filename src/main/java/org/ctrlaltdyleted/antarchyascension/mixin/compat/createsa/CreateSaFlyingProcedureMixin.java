package org.ctrlaltdyleted.antarchyascension.mixin.compat.createsa;

import org.ctrlaltdyleted.antarchyascension.compat.curios.CurioEquipmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(
        targets = {
                "net.mcreator.createstuffadditions.procedures.FlyingOnKeyPressedProcedure",
                "net.mcreator.createstuffadditions.procedures.FlyingOnKeyReleasedProcedure"
        },
        remap = false
)
public abstract class CreateSaFlyingProcedureMixin {
    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            ),
            require = 1
    )
    private static ItemStack antarchyascension$useDedicatedJetpackSlot(
            LivingEntity entity,
            EquipmentSlot slot) {

        // Finds the jetpack for the flight key procedure
        return CurioEquipmentHelper.resolveCreateSaJetpack(entity, slot);
    }
}
