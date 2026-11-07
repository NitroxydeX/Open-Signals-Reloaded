package com.troblecodings.signals.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Map;
import java.util.IdentityHashMap;

import com.google.gson.Gson;
import com.troblecodings.core.NBTWrapper;
import com.troblecodings.linkableapi.Linkingtool;
import com.troblecodings.linkableapi.MultiLinkingTool;
import com.troblecodings.signals.OpenSignalsMain;
import com.troblecodings.signals.blocks.Signal;
import com.troblecodings.signals.items.ItemArmorTemplate;
import com.troblecodings.signals.items.Placementtool;
import com.troblecodings.signals.items.SignalBridgeItem;
import com.troblecodings.signals.items.ToolParser;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class OSItems {

    private OSItems() {
    }

    public static final net.minecraft.core.component.DataComponentType<net.minecraft.nbt.CompoundTag>
            LINKING_DATA = net.minecraft.core.component.DataComponentType
                    .<net.minecraft.nbt.CompoundTag>builder()
                    .persistent(net.minecraft.nbt.CompoundTag.CODEC)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.COMPOUND_TAG)
                    .build();

    public static final Linkingtool LINKING_TOOL = new Linkingtool(OSTabs.TAB, (world, pos) -> {
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();
        final boolean isRedstoneBlock = block == OSBlocks.REDSTONE_IN
                || block == OSBlocks.REDSTONE_OUT || block == OSBlocks.COMBI_REDSTONE_INPUT;
        return isRedstoneBlock || (block instanceof Signal && ((Signal) block).canBeLinked())
                || block == OSBlocks.SIGNAL_BOX;
    }, _u -> true, (level, pos, tag) -> {
        final BlockState state = level.getBlockState(pos);
        final NBTWrapper wrapper = new NBTWrapper((net.minecraft.nbt.CompoundTag) tag);
        wrapper.putString(pos.toShortString(), BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath());
    }, LINKING_DATA);
    public static final MultiLinkingTool MULTI_LINKING_TOOL = new MultiLinkingTool(OSTabs.TAB,
            (world, pos) -> {
                final BlockState state = world.getBlockState(pos);
                final Block block = state.getBlock();
                final boolean isRedstoneBlock = block == OSBlocks.REDSTONE_IN
                        || block == OSBlocks.REDSTONE_OUT || block == OSBlocks.COMBI_REDSTONE_INPUT;
                return isRedstoneBlock
                        || (block instanceof Signal && ((Signal) block).canBeLinked())
                        || block == OSBlocks.SIGNAL_BOX;
            }, _u -> true, (level, pos, tag) -> {
                final BlockState state = level.getBlockState(pos);
                final NBTWrapper wrapper = new NBTWrapper((net.minecraft.nbt.CompoundTag) tag);
                wrapper.putString(pos.toShortString(),
                        BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath());
            }, LINKING_DATA);
    public static final Item CONDUCTOR_TROWEL_GREEN = new Item(
            new Properties());
    public static final Item CONDUCTOR_TROWEL_RED = new Item(
            new Properties());
    public static final Item WARNING_FLAG = new Item(
            new Properties());
    public static final Item K_BOARD = new Item(new Properties());
    public static final Item L_BOARD = new Item(new Properties());
    public static final ItemArmorTemplate REFLECTIVE_HEAD = new ItemArmorTemplate(
            ItemArmorTemplate.REFLECTIVE_ARMOR_MATERIAL, EquipmentSlot.HEAD);
    public static final ItemArmorTemplate REFLECTIVE_CHESTPLATE = new ItemArmorTemplate(
            ItemArmorTemplate.REFLECTIVE_ARMOR_MATERIAL, EquipmentSlot.CHEST);
    public static final ItemArmorTemplate REFLECTIVE_PANTS = new ItemArmorTemplate(
            ItemArmorTemplate.REFLECTIVE_ARMOR_MATERIAL, EquipmentSlot.LEGS);
    public static final ItemArmorTemplate REFLECTIVE_SHOES = new ItemArmorTemplate(
            ItemArmorTemplate.REFLECTIVE_ARMOR_MATERIAL, EquipmentSlot.FEET);
    public static final ItemArmorTemplate DISPATCHER_HEAD = new ItemArmorTemplate(
            ItemArmorTemplate.DISPATCHER_ARMOR_MATERIAL, EquipmentSlot.HEAD);
    public static final ItemArmorTemplate DISPATCHER_CHESTPLATE = new ItemArmorTemplate(
            ItemArmorTemplate.DISPATCHER_ARMOR_MATERIAL, EquipmentSlot.CHEST);
    public static final ItemArmorTemplate DISPATCHER_PANTS = new ItemArmorTemplate(
            ItemArmorTemplate.DISPATCHER_ARMOR_MATERIAL, EquipmentSlot.LEGS);
    public static final ItemArmorTemplate DISPATCHER_SHOES = new ItemArmorTemplate(
            ItemArmorTemplate.DISPATCHER_ARMOR_MATERIAL, EquipmentSlot.FEET);
    public static final ItemArmorTemplate STATION_MANAGER_HEAD = new ItemArmorTemplate(
            ItemArmorTemplate.STATIONMANAGER_ARMOR_MATERIAL, EquipmentSlot.HEAD);
    public static final ItemArmorTemplate STATION_MANAGER_CHESTPLATE = new ItemArmorTemplate(
            ItemArmorTemplate.STATIONMANAGER_ARMOR_MATERIAL, EquipmentSlot.CHEST);
    public static final ItemArmorTemplate STATION_MANAGER_PANTS = new ItemArmorTemplate(
            ItemArmorTemplate.STATIONMANAGER_ARMOR_MATERIAL, EquipmentSlot.LEGS);
    public static final ItemArmorTemplate STATION_MANAGER_SHOES = new ItemArmorTemplate(
            ItemArmorTemplate.STATIONMANAGER_ARMOR_MATERIAL, EquipmentSlot.FEET);
    public static final ItemArmorTemplate TRAIN_DRIVER_HEAD = new ItemArmorTemplate(
            ItemArmorTemplate.TRAINDRIVER_ARMOR_MATERIAL, EquipmentSlot.HEAD);
    public static final ItemArmorTemplate TRAIN_DRIVER_CHESTPLATE = new ItemArmorTemplate(
            ItemArmorTemplate.TRAINDRIVER_ARMOR_MATERIAL, EquipmentSlot.CHEST);
    public static final ItemArmorTemplate TRAIN_DRIVER_PANTS = new ItemArmorTemplate(
            ItemArmorTemplate.TRAINDRIVER_ARMOR_MATERIAL, EquipmentSlot.LEGS);
    public static final ItemArmorTemplate TRAIN_DRIVER_SHOES = new ItemArmorTemplate(
            ItemArmorTemplate.TRAINDRIVER_ARMOR_MATERIAL, EquipmentSlot.FEET);
    public static final ItemArmorTemplate CONDUCTOR_HEAD = new ItemArmorTemplate(
            ItemArmorTemplate.CONDUCTOR_ARMOR_MATERIAL, EquipmentSlot.HEAD);
    public static final ItemArmorTemplate CONDUCTOR_CHESTPLATE = new ItemArmorTemplate(
            ItemArmorTemplate.CONDUCTOR_ARMOR_MATERIAL, EquipmentSlot.CHEST);
    public static final ItemArmorTemplate CONDUCTOR_PANTS = new ItemArmorTemplate(
            ItemArmorTemplate.CONDUCTOR_ARMOR_MATERIAL, EquipmentSlot.LEGS);
    public static final ItemArmorTemplate CONDUCTOR_SHOES = new ItemArmorTemplate(
            ItemArmorTemplate.CONDUCTOR_ARMOR_MATERIAL, EquipmentSlot.FEET);
    public static final Item SIGNAL_PLATE = new Item(
            new Properties());
    public static final Item SIGNAL_SHIELD = new Item(
            new Properties());
    public static final Item LAMPS = new Item(new Properties());
    public static final Item ELECTRIC_PARTS = new Item(
            new Properties());
    public static final Item MANIPULATOR = new Item(new Properties());
    public static final SignalBridgeItem SIGNAL_BRIDGE_ITEM = new SignalBridgeItem();

    public static ArrayList<Item> registeredItems = new ArrayList<>();
    public static final Map<Item, ResourceLocation> ITEM_IDS = new IdentityHashMap<>();
    private static boolean initialized;

    public static ArrayList<Placementtool> placementtools = new ArrayList<>();

    public static void init() {
        synchronized (LINKING_TOOL) {
            if (initialized)
                return;
            final Field[] fields = OSItems.class.getFields();
            for (final Field field : fields) {
                final int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                        && Modifier.isPublic(modifiers)) {
                    final String name = field.getName().toLowerCase().replace("_", "");
                    try {
                        if (!(field.get(null) instanceof Item item))
                            continue;
                        ITEM_IDS.put(item, ResourceLocation.fromNamespaceAndPath(OpenSignalsMain.MODID, name));
                        registeredItems.add(item);
                    } catch (final IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            loadTools();
            initialized = true;
        }
    }

    private static final Gson GSON = new Gson();

    private static void loadTools() {
        OpenSignalsMain.contentPacks.getFiles("tools").forEach(entry -> {
            final ToolParser tools = GSON.fromJson(entry.getValue(), ToolParser.class);
            tools.getPlacementTools().forEach(placementtool -> {
                final Placementtool tool = new Placementtool();
                final String name = placementtool.toLowerCase().replace("_", "").trim();
                ITEM_IDS.put(tool, ResourceLocation.fromNamespaceAndPath(OpenSignalsMain.MODID, name));
                placementtools.add(tool);
                registeredItems.add(tool);
            });
        });
    }

    @SubscribeEvent
    public static void registerItem(final RegisterEvent event) {
        event.register(Registries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(OpenSignalsMain.MODID, "linking_data"),
                () -> LINKING_DATA);
        event.register(Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath(OpenSignalsMain.MODID, "main"), () -> OSTabs.TAB);
        event.register(Registries.ITEM, registry -> {
            OSItems.init();
            registeredItems.forEach(item -> registry.register(ITEM_IDS.get(item), item));
        });
    }

    @SubscribeEvent
    public static void populateVanillaTabs(
            final net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(net.minecraft.world.item.CreativeModeTabs.COMBAT)) {
            java.util.List.of(CONDUCTOR_TROWEL_GREEN, CONDUCTOR_TROWEL_RED, WARNING_FLAG,
                    K_BOARD, L_BOARD, REFLECTIVE_HEAD, REFLECTIVE_CHESTPLATE, REFLECTIVE_PANTS,
                    REFLECTIVE_SHOES, DISPATCHER_HEAD, DISPATCHER_CHESTPLATE, DISPATCHER_PANTS,
                    DISPATCHER_SHOES, STATION_MANAGER_HEAD, STATION_MANAGER_CHESTPLATE,
                    STATION_MANAGER_PANTS, STATION_MANAGER_SHOES, TRAIN_DRIVER_HEAD,
                    TRAIN_DRIVER_CHESTPLATE, TRAIN_DRIVER_PANTS, TRAIN_DRIVER_SHOES,
                    CONDUCTOR_HEAD, CONDUCTOR_CHESTPLATE, CONDUCTOR_PANTS, CONDUCTOR_SHOES)
                    .forEach(event::accept);
        } else if (event.getTabKey().equals(net.minecraft.world.item.CreativeModeTabs.INGREDIENTS)) {
            java.util.List.of(SIGNAL_PLATE, SIGNAL_SHIELD, LAMPS, ELECTRIC_PARTS)
                    .forEach(event::accept);
        }
    }
}