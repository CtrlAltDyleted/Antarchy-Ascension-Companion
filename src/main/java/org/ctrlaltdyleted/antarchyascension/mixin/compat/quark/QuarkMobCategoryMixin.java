package org.ctrlaltdyleted.antarchyascension.mixin.compat.quark;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(
    targets = {
        "org.violetmoon.quark.content.mobs.module.StonelingsModule",
        "org.violetmoon.quark.content.mobs.module.ToretoiseModule",
        "org.violetmoon.quark.content.mobs.module.FoxhoundModule"
    },
    remap = false
)
public abstract class QuarkMobCategoryMixin {
    @ModifyArg(
        method = "register",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityType$Builder;of(Lnet/minecraft/world/entity/EntityType$EntityFactory;Lnet/minecraft/world/entity/MobCategory;)Lnet/minecraft/world/entity/EntityType$Builder;"
        ),
        index = 1,
        require = 1
    )
    private MobCategory antarchyascension$useMonsterCategory(MobCategory category) {
        return MobCategory.MONSTER;
    }
}
