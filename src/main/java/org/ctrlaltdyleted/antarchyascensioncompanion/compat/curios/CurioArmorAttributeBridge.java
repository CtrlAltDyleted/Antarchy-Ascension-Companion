package org.ctrlaltdyleted.antarchyascensioncompanion.compat.curios;

import com.google.common.collect.Multimap;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.ctrlaltdyleted.antarchyascensioncompanion.AntarchyAscensionCompanion;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@EventBusSubscriber(modid = AntarchyAscensionCompanion.MODID)
public final class CurioArmorAttributeBridge {
    private CurioArmorAttributeBridge() {
    }

    @SubscribeEvent
    public static void onCurioAttributes(CurioAttributeModifierEvent event) {
        String slotId = event.getSlotContext().identifier();

        if (CurioEquipmentHelper.JETPACK_SLOT.equals(slotId)) {
            addDefensiveEquipmentModifiers(
                    event,
                    event.getItemStack(),
                    EquipmentSlotGroup.CHEST);
            return;
        }

        if (CurioEquipmentHelper.GOGGLES_SLOT.equals(slotId)) {
            addDefensiveEquipmentModifiers(
                    event,
                    event.getItemStack(),
                    EquipmentSlotGroup.HEAD);
        }
    }

    private static void addDefensiveEquipmentModifiers(
            CurioAttributeModifierEvent event,
            ItemStack stack,
            EquipmentSlotGroup equipmentGroup) {

        stack.forEachModifier(
                equipmentGroup,
                (attribute, modifier) -> {
                    if (!isDefensiveAttribute(attribute)) {
                        return;
                    }

                    if (!containsEquivalentModifier(
                            event.getModifiers(),
                            attribute,
                            modifier)) {

                        event.addModifier(attribute, modifier);
                    }
                });
    }

    private static boolean isDefensiveAttribute(Holder<Attribute> attribute) {
        return attribute.equals(Attributes.ARMOR)
                || attribute.equals(Attributes.ARMOR_TOUGHNESS)
                || attribute.equals(Attributes.KNOCKBACK_RESISTANCE);
    }

    private static boolean containsEquivalentModifier(
            Multimap<Holder<Attribute>, AttributeModifier> existing,
            Holder<Attribute> attribute,
            AttributeModifier wanted) {

        for (AttributeModifier modifier : existing.get(attribute)) {
            if (modifier.equals(wanted)) {
                return true;
            }

            if (modifier.is(wanted.id())) {
                return true;
            }
        }

        return false;
    }
}
