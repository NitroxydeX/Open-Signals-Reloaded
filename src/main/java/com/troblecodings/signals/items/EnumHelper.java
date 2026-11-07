package com.troblecodings.signals.items;

import java.util.List;
import java.util.Map;

import com.troblecodings.signals.OpenSignalsMain;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EnumHelper {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, OpenSignalsMain.MODID);

    private EnumHelper() {
    }

    public static Holder<ArmorMaterial> addArmorMaterial(final String name,
            final int[] defense, final int enchant, final Holder<SoundEvent> event,
            final float toughness) {
        final Map<ArmorItem.Type, Integer> protection = Map.of(
                ArmorItem.Type.BOOTS, defense[0],
                ArmorItem.Type.LEGGINGS, defense[1],
                ArmorItem.Type.CHESTPLATE, defense[2],
                ArmorItem.Type.HELMET, defense[3]);
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(
                protection, enchant, event, () -> Ingredient.EMPTY,
                List.of(new ArmorMaterial.Layer(
                        ResourceLocation.fromNamespaceAndPath(OpenSignalsMain.MODID, name))),
                toughness, 0.0F));
    }
}
