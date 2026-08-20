package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.createsa;

import org.ctrlaltdyleted.antarchyascensioncompanion.compat.curios.CurioEquipmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.Iterables;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Pseudo
@Mixin(
        targets = {
                "net.mcreator.createstuffadditions.item.AndesiteJetpackItem$Chestplate",
                "net.mcreator.createstuffadditions.item.CopperJetpackItem$Chestplate",
                "net.mcreator.createstuffadditions.item.BrassJetpackItem$Chestplate",
                "net.mcreator.createstuffadditions.item.NetheriteJetpackItem$Chestplate"
        },
        remap = false
)
public abstract class CreateSaJetpackInventoryTickMixin {
    @Redirect(
            method = "inventoryTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Iterables;contains(Ljava/lang/Iterable;Ljava/lang/Object;)Z"
            ),
            require = 1
    )
    private boolean antarchy$treatDedicatedJetpackAsWorn(
            Iterable<?> armorSlots,
            Object wanted,
            ItemStack stack,
            Level level,
            Entity entity,
            int slot,
            boolean selected) {

        if (Iterables.contains(armorSlots, wanted)) {
            return true;
        }

        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }

        return CurioEquipmentHelper.isExactStackInSlot(
                livingEntity,
                CurioEquipmentHelper.JETPACK_SLOT,
                stack);
    }
}
