package org.ctrlaltdyleted.antarchyascension.mixin.compat.arsnouveau;

import java.lang.reflect.Method;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.ctrlaltdyleted.antarchyascension.compat.progression.PendingApparatusCredits;
import org.ctrlaltdyleted.antarchyascension.compat.progression.ProgressionRequirements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Gives recipe credit to the player who started the craft
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
            // Caches the recipe lookup method
            if (antarchyascension$getRecipe == null) {
                antarchyascension$getRecipe = this.getClass().getMethod("getRecipe", ItemStack.class, Player.class);
            }
            Object value = antarchyascension$getRecipe.invoke(this, reagent, player);
            if (!(value instanceof Recipe<?> recipe) || tile.getLevel() == null) return null;
            // Finds the selected recipe ID
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
        // Saves completed credit before another craft starts
        antarchyascension$credit();
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
        // Remembers who started a successful craft
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
        // Only grants completed crafts on the server
        if (!antarchyascension$completed || !(tile.getLevel() instanceof ServerLevel level)) return;
        UUID crafter = antarchyascension$crafter;
        if (crafter != null && antarchyascension$recipe != null) {
            PendingApparatusCredits.queue(level, crafter, antarchyascension$recipe);
        }
        // Clears the credit to prevent duplicate grants
        antarchyascension$crafter = null;
        antarchyascension$recipe = null;
        antarchyascension$completed = false;
        tile.setChanged();
        if (crafter != null) PendingApparatusCredits.deliverOnline(level.getServer(), crafter);
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
            // Accepts the old saved recipe names
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
