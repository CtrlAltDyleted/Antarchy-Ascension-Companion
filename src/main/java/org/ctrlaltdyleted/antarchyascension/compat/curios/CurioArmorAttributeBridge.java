package org.ctrlaltdyleted.antarchyascension.compat.curios;

import com.google.common.collect.Multimap;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@EventBusSubscriber(modid = AntarchyAscension.MODID)
public final class CurioArmorAttributeBridge {
    private CurioArmorAttributeBridge() {
    }

    @SubscribeEvent
    public static void onCurioAttributes(CurioAttributeModifierEvent event) {
        String slotId = event.getSlotContext().identifier();

        if (CurioEquipmentHelper.JETPACK_SLOT.equals(slotId)) {
            addNonzeroEquipmentModifiers(
                    event,
                    event.getItemStack(),
                    EquipmentSlotGroup.CHEST);
            return;
        }

        if (CurioEquipmentHelper.GOGGLES_SLOT.equals(slotId)) {
            addNonzeroEquipmentModifiers(
                    event,
                    event.getItemStack(),
                    EquipmentSlotGroup.HEAD);
        }
    }

    private static void addNonzeroEquipmentModifiers(
            CurioAttributeModifierEvent event,
            ItemStack stack,
            EquipmentSlotGroup equipmentGroup) {

        stack.forEachModifier(
                equipmentGroup,
                (attribute, modifier) -> {
                    if (modifier.amount() == 0.0D) {
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
