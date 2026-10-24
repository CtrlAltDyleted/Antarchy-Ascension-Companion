package org.ctrlaltdyleted.antarchyascension.compat.progression;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

// Saves recipe credit until the player can receive it
@EventBusSubscriber(modid = AntarchyAscension.MODID)
public final class PendingApparatusCredits extends SavedData {
    private static final Factory<PendingApparatusCredits> FACTORY =
            new Factory<>(PendingApparatusCredits::new, PendingApparatusCredits::load);
    private static final String DATA_NAME = "antarchyascension_apparatus_credits";
    private final Map<UUID, Set<ResourceLocation>> pending = new HashMap<>();

    private static PendingApparatusCredits get(MinecraftServer server) {
        // Stores pending credit in the Overworld save
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static void queue(ServerLevel level, UUID player, ResourceLocation recipe) {
        get(level.getServer()).add(player, recipe);
    }

    public static void deliverOnline(MinecraftServer server, UUID playerId) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        if (player != null) {
            get(server).deliver(playerId,
                    recipe -> CriteriaTriggers.RECIPE_CRAFTED.trigger(player, recipe, List.of()));
        }
    }

    @SubscribeEvent
    public static void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            deliverOnline(player.server, player.getUUID());
        }
    }

    void add(UUID player, ResourceLocation recipe) {
        // Keeps one pending credit per recipe
        if (pending.computeIfAbsent(player, ignored -> new LinkedHashSet<>()).add(recipe)) {
            setDirty();
        }
    }

    void deliver(UUID player, Consumer<ResourceLocation> grant) {
        Set<ResourceLocation> recipes = pending.get(player);
        if (recipes == null) return;
        // Grants and removes pending recipe credit
        for (ResourceLocation recipe : List.copyOf(recipes)) {
            grant.accept(recipe);
            recipes.remove(recipe);
            setDirty();
        }
        if (recipes.isEmpty()) pending.remove(player);
    }

    static PendingApparatusCredits load(CompoundTag tag, HolderLookup.Provider registries) {
        PendingApparatusCredits data = new PendingApparatusCredits();
        ListTag entries = tag.getList("credits", Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            ResourceLocation recipe = ResourceLocation.tryParse(entry.getString("recipe"));
            // Checks the saved player and recipe
            if (!entry.hasUUID("player") || recipe == null) {
                throw new IllegalStateException("Invalid pending apparatus credit at index " + i);
            }
            data.pending.computeIfAbsent(entry.getUUID("player"), ignored -> new LinkedHashSet<>())
                    .add(recipe);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag entries = new ListTag();
        pending.forEach((player, recipes) -> recipes.forEach(recipe -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("player", player);
            entry.putString("recipe", recipe.toString());
            entries.add(entry);
        }));
        tag.put("credits", entries);
        return tag;
    }
}
