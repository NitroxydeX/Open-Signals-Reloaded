package com.troblecodings.signals.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.core.Holder;

public class ItemArmorTemplate extends ArmorItem {

    public static void registerMaterials(final net.neoforged.bus.api.IEventBus eventBus) {
        EnumHelper.ARMOR_MATERIALS.register(eventBus);
    }

    public ItemArmorTemplate(final Holder<ArmorMaterial> materialIn, final EquipmentSlot equipmentSlotIn) {
        super(materialIn, armorType(equipmentSlotIn), new Properties().durability(1000));
    }

    private static ArmorItem.Type armorType(final EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ArmorItem.Type.HELMET;
            case CHEST -> ArmorItem.Type.CHESTPLATE;
            case LEGS -> ArmorItem.Type.LEGGINGS;
            case FEET -> ArmorItem.Type.BOOTS;
            default -> throw new IllegalArgumentException("Not an armor slot: " + slot);
        };
    }

    public static final Holder<ArmorMaterial> REFLECTIVE_ARMOR_MATERIAL = EnumHelper
            .addArmorMaterial("reflective", new int[] {
                    1, 1, 1, 1
            }, 30, SoundEvents.ARMOR_EQUIP_GENERIC, 0F);

    public static final Holder<ArmorMaterial> DISPATCHER_ARMOR_MATERIAL = EnumHelper
            .addArmorMaterial("dispatcher", new int[] {
                    1, 1, 1, 1
            }, 30, SoundEvents.ARMOR_EQUIP_GENERIC, 0F);

    public static final Holder<ArmorMaterial> STATIONMANAGER_ARMOR_MATERIAL = EnumHelper
            .addArmorMaterial("station_manager", new int[] {
                    1, 1, 1, 1
            }, 30, SoundEvents.ARMOR_EQUIP_GENERIC, 0F);

    public static final Holder<ArmorMaterial> TRAINDRIVER_ARMOR_MATERIAL = EnumHelper
            .addArmorMaterial("train_driver", new int[] {
                    1, 1, 1, 1
            }, 30, SoundEvents.ARMOR_EQUIP_GENERIC, 0F);

    public static final Holder<ArmorMaterial> CONDUCTOR_ARMOR_MATERIAL = EnumHelper
            .addArmorMaterial("conductor", new int[] {
                    1, 1, 1, 1
            }, 30, SoundEvents.ARMOR_EQUIP_GENERIC, 0F);
}