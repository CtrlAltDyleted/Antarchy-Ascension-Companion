package org.ctrlaltdyleted.antarchyascension.mixin.compat.arsnouveau;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.ctrlaltdyleted.antarchyascension.compat.progression.ProgressionRequirements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Credits a completed apparatus recipe to its initiator, never to an item recipient. */
@Pseudo
@Mixin(targets = "com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile", remap = false)
public abstract class ApparatusProgressionMixin {
    @Unique private UUID antarchyascension$crafter;
    @Unique private ResourceLocation antarchyascension$recipe;
    @Unique private boolean antarchyascension$completed;
    @Unique private ResourceLocation antarchyascension$startingRecipe;
    @Unique private Method antarchyascension$getRecipe;

    @Unique
    private ResourceLocation antarchyascension$resolve(ItemStack reagent, Player player) {
        BlockEntity tile = (BlockEntity)(Object)this;
        try {
            if (antarchyascension$getRecipe == null) {
                antarchyascension$getRecipe = this.getClass().getMethod("getRecipe", ItemStack.class, Player.class);
            }
            Object value = antarchyascension$getRecipe.invoke(this, reagent, player);
            if (!(value instanceof Recipe<?> recipe) || tile.getLevel() == null) return null;
            return tile.getLevel().getRecipeManager().getRecipes().stream()
                    .filter(holder -> holder.value() == recipe).map(holder -> holder.id()).findFirst().orElse(null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot attribute Ars apparatus crafting", exception);
        }
    }

    @Inject(method = "attemptCraft", at = @At("HEAD"), cancellable = true)
    private void antarchyascension$check(ItemStack reagent, Player player, CallbackInfoReturnable<Boolean> ci) {
        BlockEntity tile = (BlockEntity)(Object)this;
        if (!(tile.getLevel() instanceof ServerLevel)) return;
        // Do not overwrite an offline initiator's uncredited completed ritual.
        antarchyascension$credit();
        if (antarchyascension$completed && antarchyascension$crafter != null) {
            ci.setReturnValue(false);
            return;
        }
        antarchyascension$startingRecipe = antarchyascension$resolve(reagent, player);
        if (antarchyascension$startingRecipe != null
                && !ProgressionRequirements.canStartApparatus(player, antarchyascension$startingRecipe)) {
            if (player != null) player.displayClientMessage(
                    Component.translatable("message.antarchyascension.progression_required"), true);
            ci.setReturnValue(false);
        }
    }

    @Inject(method = "attemptCraft", at = @At("RETURN"))
    private void antarchyascension$remember(ItemStack reagent, Player player, CallbackInfoReturnable<Boolean> ci) {
        BlockEntity tile = (BlockEntity)(Object)this;
        if (ci.getReturnValueZ() && tile.getLevel() instanceof ServerLevel) {
            antarchyascension$crafter = player == null ? null : player.getUUID();
            antarchyascension$recipe = antarchyascension$startingRecipe;
            antarchyascension$completed = false;
            tile.setChanged();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lcom/hollingsworth/arsnouveau/common/block/tile/EnchantingApparatusTile;clearItems()V",
            shift = At.Shift.AFTER))
    private void antarchyascension$complete(CallbackInfo ci) {
        antarchyascension$completed = true;
        ((BlockEntity)(Object)this).setChanged();
        antarchyascension$credit();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void antarchyascension$deferredCredit(CallbackInfo ci) {
        antarchyascension$credit();
    }

    @Unique
    private void antarchyascension$credit() {
        BlockEntity tile = (BlockEntity)(Object)this;
        if (!antarchyascension$completed || antarchyascension$crafter == null
                || antarchyascension$recipe == null || !(tile.getLevel() instanceof ServerLevel level)) return;
        var player = level.getServer().getPlayerList().getPlayer(antarchyascension$crafter);
        if (player == null) return;
        CriteriaTriggers.RECIPE_CRAFTED.trigger(player, antarchyascension$recipe, List.of());
        antarchyascension$crafter = null;
        antarchyascension$recipe = null;
        antarchyascension$completed = false;
        tile.setChanged();
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    private void antarchyascension$save(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (antarchyascension$crafter != null && antarchyascension$recipe != null) {
            tag.putUUID("antarchyascension_crafter", antarchyascension$crafter);
            tag.putString("antarchyascension_recipe", antarchyascension$recipe.toString());
            tag.putBoolean("antarchyascension_completed", antarchyascension$completed);
        }
    }

    @Inject(method = "loadAdditional", at = @At("RETURN"))
    private void antarchyascension$load(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (tag.hasUUID("antarchyascension_crafter")) {
            antarchyascension$crafter = tag.getUUID("antarchyascension_crafter");
            // Preserve pending ritual credit saved before the recipe-directory migration.
            String savedRecipe = tag.getString("antarchyascension_recipe");
            antarchyascension$recipe = ResourceLocation.tryParse(switch (savedRecipe) {
                case "antarchyascension:skybound_sigil" -> "antarchyascension:progression/sigils/skybound_sigil";
                case "antarchyascension:grounded_sigil" -> "antarchyascension:progression/sigils/grounded_sigil";
                case "antarchyascension:nether_crystal_seed" -> "antarchyascension:progression/nether/nether_crystal_seed";
                default -> savedRecipe;
            });
            antarchyascension$completed = tag.getBoolean("antarchyascension_completed");
        }
    }
}
