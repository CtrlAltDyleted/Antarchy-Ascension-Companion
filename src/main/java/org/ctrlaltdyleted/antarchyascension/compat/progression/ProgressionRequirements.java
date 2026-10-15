package org.ctrlaltdyleted.antarchyascension.compat.progression;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

/** Runtime mechanics with Companion-owned, data-driven progression requirements. */
@EventBusSubscriber(modid = AntarchyAscension.MODID)
public final class ProgressionRequirements extends SimpleJsonResourceReloadListener {
    private static volatile Map<ResourceLocation, List<ResourceLocation>> apparatus = Map.of();
    private static volatile Map<String, List<ResourceLocation>> dimensionNamespaces = Map.of();

    private ProgressionRequirements() {
        super(new Gson(), "progression");
    }

    @SubscribeEvent
    public static void reload(AddReloadListenerEvent event) {
        event.addListener(new ProgressionRequirements());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager,
            ProfilerFiller profiler) {
        var id = ResourceLocation.fromNamespaceAndPath(AntarchyAscension.MODID, "requirements");
        JsonElement resource = resources.get(id);
        if (resource == null) {
            throw new IllegalStateException("Missing Companion progression/requirements.json");
        }
        JsonObject root = resource.getAsJsonObject();
        apparatus = read(root.getAsJsonObject("apparatus"));
        Map<String, List<ResourceLocation>> dimensions = new HashMap<>();
        root.getAsJsonObject("dimension_namespaces").entrySet().forEach(entry -> dimensions.put(
                entry.getKey(), entry.getValue().getAsJsonArray().asList().stream()
                        .map(value -> ResourceLocation.parse(value.getAsString())).toList()));
        dimensionNamespaces = Map.copyOf(dimensions);
    }

    private static Map<ResourceLocation, List<ResourceLocation>> read(JsonObject object) {
        Map<ResourceLocation, List<ResourceLocation>> result = new HashMap<>();
        object.entrySet().forEach(entry -> result.put(ResourceLocation.parse(entry.getKey()),
                entry.getValue().getAsJsonArray().asList().stream()
                        .map(value -> ResourceLocation.parse(value.getAsString())).toList()));
        return Map.copyOf(result);
    }

    private static boolean satisfies(Player player, List<ResourceLocation> required) {
        if (required == null || required.isEmpty()) return true;
        if (!(player instanceof ServerPlayer serverPlayer)) return false;
        for (ResourceLocation id : required) {
            var advancement = serverPlayer.server.getAdvancements().get(id);
            if (advancement == null || !serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                return false;
            }
        }
        return true;
    }

    public static boolean canStartApparatus(Player player, ResourceLocation recipe) {
        return satisfies(player, apparatus.get(recipe));
    }

    @SubscribeEvent
    public static void dimensionTravel(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !satisfies(player,
                dimensionNamespaces.get(event.getDimension().location().getNamespace()))) {
            event.setCanceled(true);
        }
    }
}
