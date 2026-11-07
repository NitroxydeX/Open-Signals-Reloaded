package com.troblecodings.signals.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.IdentityHashMap;

import com.troblecodings.signals.OpenSignalsMain;
import com.troblecodings.signals.blocks.BasicBlock;
import com.troblecodings.signals.blocks.CombinedRedstoneInput;
import com.troblecodings.signals.blocks.GhostBlock;
import com.troblecodings.signals.blocks.PathwayRequester;
import com.troblecodings.signals.blocks.Post;
import com.troblecodings.signals.blocks.PostConnectable;
import com.troblecodings.signals.blocks.RedstoneIO;
import com.troblecodings.signals.blocks.RedstoneInput;
import com.troblecodings.signals.blocks.Signal;
import com.troblecodings.signals.blocks.SignalBox;
import com.troblecodings.signals.blocks.SignalController;
import com.troblecodings.signals.blocks.TrainNumberBlock;
import com.troblecodings.signals.contentpacks.SignalBridgeBlockParser;
import com.troblecodings.signals.core.SignalLoader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class OSBlocks {

    private OSBlocks() {
    }

    public static final SignalController HV_SIGNAL_CONTROLLER = new SignalController();
    public static final Post POST = new Post();
    public static final GhostBlock GHOST_BLOCK = new GhostBlock();
    public static final SignalBox SIGNAL_BOX = new SignalBox();
    public static final RedstoneInput REDSTONE_IN = new RedstoneInput();
    public static final RedstoneIO REDSTONE_OUT = new RedstoneIO();
    public static final CombinedRedstoneInput COMBI_REDSTONE_INPUT = new CombinedRedstoneInput();
    public static final PathwayRequester PATHWAY_REQUESTER = new PathwayRequester();
    public static final TrainNumberBlock TRAIN_NUMBER_BLOCK = new TrainNumberBlock();
    public static final PostConnectable POST_CONNECTABLE = new PostConnectable();

    public static final List<BasicBlock> BLOCKS_TO_REGISTER = new ArrayList<>();
    public static final Map<BasicBlock, ResourceLocation> BLOCK_IDS = new IdentityHashMap<>();

    public static void init() {
        final Field[] fields = OSBlocks.class.getFields();
        for (final Field field : fields) {
            final int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                    && Modifier.isPublic(modifiers)) {
                final String name = field.getName().toLowerCase().replace("_", "");
                try {
                    final Object object = field.get(null);
                    if (object instanceof BasicBlock) {
                        loadBlock((BasicBlock) object, name);
                    }
                } catch (final IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        OSItems.init();
        SignalLoader.loadAllSignals();
        BasicBlock.prepare();
        SignalBridgeBlockParser.loadSignalBridgeBlocks();
    }

    public static void loadBlock(final BasicBlock block, final String pName) {
        if (BLOCKS_TO_REGISTER.contains(block))
            return;
        final String name = pName.toLowerCase().trim();
        BLOCK_IDS.put(block, ResourceLocation.fromNamespaceAndPath(OpenSignalsMain.MODID, name));
        BLOCKS_TO_REGISTER.add(block);
        if (block instanceof Signal) {
            if (Signal.SIGNALS.containsKey(name))
                throw new IllegalArgumentException(
                        "A Signal with the name '" + name + "' alredy exists!");
            Signal.SIGNALS.put(name, (Signal) block);
        }
    }

    @SubscribeEvent
    public static void registerBlock(final RegisterEvent event) {
        event.register(Registries.BLOCK, registry -> {
            if (!BLOCK_IDS.containsKey(POST))
                OSBlocks.init();
            BLOCKS_TO_REGISTER.forEach(block -> registry.register(BLOCK_IDS.get(block), block));
        });
    }

    @SubscribeEvent
    public static void registerBlockEntitys(
            final RegisterEvent event) {
        event.register(Registries.BLOCK_ENTITY_TYPE, registry -> {
            if (!BLOCK_IDS.containsKey(POST))
                OSBlocks.init();
            BasicBlock.BLOCK_ENTITYS.forEach((supplier, type) ->
                    registry.register(BasicBlock.getBlockEntityId(supplier), type));
        });
    }

    @SubscribeEvent
    public static void registerItem(final RegisterEvent event) {
        event.register(Registries.ITEM, registry -> {
            if (!BLOCK_IDS.containsKey(POST))
                OSBlocks.init();
            BLOCKS_TO_REGISTER.forEach(block -> {
                if (block.shouldHaveItem())
                    registry.register(BLOCK_IDS.get(block), new BlockItem(block, new Properties()));
            });
        });
    }
}