package org.ctrlaltdyleted.antarchyascension.compat.curios;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public final class CurioEquipmentHelper {
    // Keeps the existing Elytra slot ID
    public static final String ELYTRA_SLOT = "hdpe_elytra";
    public static final String OXYGEN_SLOT = "oxygen_tank";
    private static final TagKey<Item> ELYTRAS = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath("curios", ELYTRA_SLOT));
    private static final TagKey<Item> GENERIC = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath("curios", "curio"));
    private static final TagKey<Item> GENERIC_EXCLUSIONS = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath("curios", "generic_exclusions"));

    public static final String JETPACK_SLOT = "jetpack";
    public static final String TANK_SLOT = "filling_fueling_tank";
    public static final String GOGGLES_SLOT = "engineers_goggles";

    public static final TagKey<Item> CREATE_SA_JETPACKS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("create_sa", "jetpack"));

    public static final TagKey<Item> CREATE_SA_FILLABLE =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("create_sa", "fillable"));

    public static final TagKey<Item> CREATE_SA_FUELABLE =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("create_sa", "fuelable"));

    private CurioEquipmentHelper() {
    }

    public static ItemStack findFirstInSlot(LivingEntity entity, String slotId,
            Predicate<ItemStack> predicate) {
        return findFirstInSlot(entity, slotId, predicate, false);
    }

    public static ItemStack findFirstRenderedInSlot(LivingEntity entity, String slotId,
            Predicate<ItemStack> predicate) {
        return findFirstInSlot(entity, slotId, predicate, true);
    }

    private static ItemStack findFirstInSlot(LivingEntity entity, String slotId,
            Predicate<ItemStack> predicate, boolean renderedOnly) {
        // Finds the first matching stack
        ICurioStacksHandler handler = slotHandler(entity, slotId);
        if (handler == null) return ItemStack.EMPTY;
        for (int index = 0; index < handler.getSlots(); index++) {
            ItemStack stack = handler.getStacks().getStackInSlot(index);
            if ((!renderedOnly || isRendered(handler, index)) && !stack.isEmpty() && predicate.test(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean isRendered(ICurioStacksHandler handler, int index) {
        return handler.getRenders().size() > index && handler.getRenders().get(index);
    }

    public static boolean isExactStackInSlot(
            LivingEntity entity,
            String slotId,
            ItemStack wanted) {

        if (wanted.isEmpty()) {
            return false;
        }

        ICurioStacksHandler handler = slotHandler(entity, slotId);

        if (handler == null) {
            return false;
        }

        for (int index = 0; index < handler.getSlots(); index++) {
            if (handler.getStacks().getStackInSlot(index) == wanted) {
                return true;
            }
        }

        return false;
    }

    public static void forEachStackInSlot(
            LivingEntity entity,
            String slotId,
            Consumer<ItemStack> action) {

        ICurioStacksHandler handler = slotHandler(entity, slotId);

        if (handler == null) {
            return;
        }

        for (int index = 0; index < handler.getSlots(); index++) {
            ItemStack stack = handler.getStacks().getStackInSlot(index);

            if (!stack.isEmpty()) {
                action.accept(stack);
            }
        }
    }

    public static ItemStack resolveCreateSaJetpack(LivingEntity entity, EquipmentSlot requestedSlot) {
        return resolveChestEquipment(entity, requestedSlot, stack -> stack.is(CREATE_SA_JETPACKS));
    }

    public static ItemStack resolveCreateSaTankTarget(LivingEntity entity, EquipmentSlot requestedSlot) {
        return resolveChestEquipment(entity, requestedSlot, CurioEquipmentHelper::isCreateSaTankTarget);
    }

    private static ItemStack resolveChestEquipment(LivingEntity entity, EquipmentSlot requestedSlot,
            Predicate<ItemStack> predicate) {
        ItemStack normal = entity.getItemBySlot(requestedSlot);
        // Checks chest equipment before Curios for Create S&A
        if (requestedSlot != EquipmentSlot.CHEST || predicate.test(normal)) return normal;
        ItemStack curio = findFirstInSlot(entity, JETPACK_SLOT, predicate);
        return curio.isEmpty() ? normal : curio;
    }

    private static boolean isCreateSaTankTarget(ItemStack stack) {
        return stack.is(CREATE_SA_FILLABLE) || stack.is(CREATE_SA_FUELABLE);
    }

    private static ICurioStacksHandler slotHandler(LivingEntity entity, String slotId) {
        var inventory = entity.getCapability(CuriosCapability.INVENTORY);
        return inventory == null ? null : inventory.getStacksHandler(slotId).orElse(null);
    }

    // Equipment checks
    public static boolean isOrnithopter(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals("createornithopterglider")
                && stack.getItem() instanceof ElytraItem;
    }

    public static boolean isEternalOrnithopter(ItemStack stack) {
        if (!isOrnithopter(stack)) return false;
        var type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(
                ResourceLocation.fromNamespaceAndPath("forbidden_arcanus", "modifier"));
        if (type == null) throw new IllegalStateException("Missing Forbidden & Arcanus modifier component");
        // Checks for the Eternal modifier
        Object modifier = stack.get(type);
        return modifier instanceof net.minecraft.core.Holder<?> holder
                && holder.unwrapKey().map(key -> key.location().equals(
                        ResourceLocation.fromNamespaceAndPath("forbidden_arcanus", "eternal"))).orElse(false);
    }

    public static boolean isSupportedElytra(ItemStack stack) {
        return !BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("gobber2:dragon_elytra")
                && stack.is(ELYTRAS);
    }

    public static boolean isAntarchyWings(ItemStack stack) {
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return id.equals("antarchy:brutalfly_elytra") || isManticoreWings(stack);
    }

    public static boolean isManticoreWings(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("antarchy:manticore_wings");
    }

    public static ItemStack activeElytraStack(LivingEntity entity) {
        var inventory = entity.getCapability(CuriosCapability.INVENTORY);
        // Skips disabled Elytra slots
        if (inventory == null || !inventory.isSlotActive(ELYTRA_SLOT, 0)) return ItemStack.EMPTY;
        var handler = inventory.getStacksHandler(ELYTRA_SLOT).orElse(null);
        return handler == null || handler.getSlots() == 0
                ? ItemStack.EMPTY : handler.getStacks().getStackInSlot(0);
    }

    public static ItemStack equippedElytra(LivingEntity entity) {
        ItemStack stack = activeElytraStack(entity);
        return isSupportedElytra(stack) ? stack : ItemStack.EMPTY;
    }

    public static boolean isOxygenTank(ItemStack stack) {
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return id.equals("ad_astra_giselle_addon:oxygen_can")
                || id.equals("ad_astra_giselle_addon:netherite_oxygen_can")
                || id.equals("ad_astra_giselle_addon:creative_oxygen_can");
    }

    public static ItemStack ornithopter(LivingEntity entity) {
        ItemStack stack = activeElytraStack(entity);
        return isOrnithopter(stack) ? stack : ItemStack.EMPTY;
    }

    public static ItemStack renderedElytra(LivingEntity entity) {
        ItemStack curio = equippedElytra(entity);
        if (!curio.isEmpty()) {
            var inventory = entity.getCapability(CuriosCapability.INVENTORY);
            var handler = inventory == null ? null : inventory.getStacksHandler(ELYTRA_SLOT).orElse(null);
            // Hidden Curios wings keep chest wings hidden
            return handler != null && isRendered(handler, 0) ? curio : ItemStack.EMPTY;
        }
        return entity.getItemBySlot(EquipmentSlot.CHEST);
    }

    public static boolean reject(String slot, ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (isSupportedElytra(stack)) return !slot.equals(ELYTRA_SLOT);
        if (isOxygenTank(stack)) return !slot.equals(OXYGEN_SLOT);
        // Keeps other items out of dedicated slots
        if (slot.equals(OXYGEN_SLOT) || slot.equals(ELYTRA_SLOT)) return true;
        if (slot.equals("curio") && stack.is(GENERIC_EXCLUSIONS)) return true;
        if (!slot.equals("curio") && stack.is(GENERIC)) {
            return !stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", slot)));
        }
        // Keeps dedicated equipment out of the generic slot
        if (slot.equals("curio") && !stack.is(GENERIC)) {
            return stack.getTags().anyMatch(tag -> tag.location().getNamespace().equals("curios")
                    && !tag.location().getPath().equals("generic_exclusions"));
        }
        return false;
    }

    // Selects equipment for flight and abilities
    public static ItemStack abilityStack(ItemStack chest, LivingEntity entity) {
        ItemStack curio = activeElytraStack(entity);
        return isSupportedElytra(curio) ? (isOrnithopter(curio) ? ItemStack.EMPTY : curio) : chest;
    }

    public static ItemStack flightStack(ItemStack chest, LivingEntity entity) {
        return flightStack(chest, entity, activeElytraStack(entity));
    }

    private static ItemStack flightStack(ItemStack chest, LivingEntity entity, ItemStack curio) {
        // Only changes player flight
        if (!(entity instanceof Player)) return chest;
        // Leaves Ornithopter flight to its native handler
        if (isOrnithopter(curio)) return chest;
        return isSupportedElytra(curio) && curio.canElytraFly(entity)
                ? curio : chest;
    }

    // Adds sound support to equipment without a native Curios handler
    public static void registerEquipSounds(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        for (String id : java.util.List.of(
                "create_sa:andesite_jetpack_chestplate", "create_sa:copper_jetpack_chestplate",
                "create_sa:brass_jetpack_chestplate", "create_sa:netherite_jetpack_chestplate")) {
            registerEquipSound(event, id, JETPACK_SLOT);
        }
        for (String id : java.util.List.of("minecraft:elytra", "antarchy:brutalfly_elytra", "antarchy:manticore_wings")) {
            registerEquipSound(event, id, ELYTRA_SLOT);
        }
        for (String color : java.util.List.of("", "_black", "_blue", "_brown", "_cyan", "_gray", "_green",
                "_light_blue", "_light_gray", "_lime", "_magenta", "_orange", "_pink", "_purple", "_red", "_yellow")) {
            registerEquipSound(event, "createornithopterglider:ornithopterglider" + color, ELYTRA_SLOT);
        }
        for (String id : java.util.List.of("create:goggles", "actuallyadditions:engineers_goggles",
                "actuallyadditions:engineers_goggles_advanced", "occultengineering:combined_goggles")) {
            registerEquipSound(event, id, GOGGLES_SLOT);
        }
    }

    private static void registerEquipSound(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event,
            String id, String slot) {
        Item item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(id))
                .orElseThrow(() -> new IllegalStateException("Missing equipment item: " + id));
        // Native providers run first
        event.registerItem(CuriosCapability.ITEM, (stack, context) -> new EquipSoundCurio(stack, slot), item);
    }

    private record EquipSoundCurio(ItemStack stack, String slot)
            implements top.theillusivec4.curios.api.type.capability.ICurio {
        @Override
        public ItemStack getStack() {
            return stack;
        }

        @Override
        public SoundInfo getEquipSound(top.theillusivec4.curios.api.SlotContext context) {
            net.minecraft.world.item.Equipable equipment = net.minecraft.world.item.Equipable.get(stack);
            var sound = equipment == null ? net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC : equipment.getEquipSound();
            return new SoundInfo(sound.value(), 1.0F, 1.0F);
        }

        @Override
        public void onEquipFromUse(top.theillusivec4.curios.api.SlotContext context) {
            // Plays once on the server for an actual slot insertion
            if (slot.equals(context.identifier()) && context.entity().level() instanceof net.minecraft.server.level.ServerLevel) {
                top.theillusivec4.curios.api.type.capability.ICurio.super.onEquipFromUse(context);
            }
        }
    }
}
