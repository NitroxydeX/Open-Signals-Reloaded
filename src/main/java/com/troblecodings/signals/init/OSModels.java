package com.troblecodings.signals.init;

import com.troblecodings.signals.blocks.Signal;
import com.troblecodings.signals.models.SignalGeometryLoader;
import com.troblecodings.signals.models.SignalModelResources;
import com.troblecodings.signals.OpenSignalsMain;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class OSModels {

    private OSModels() {
    }

    @SubscribeEvent
    public static void registerClientExtensions(final RegisterClientExtensionsEvent event) {
        final IClientBlockExtensions extensions = new IClientBlockExtensions() {
            @Override
            public boolean areBreakingParticlesTinted(final BlockState state,
                    final ClientLevel level, final BlockPos pos) {
                return false;
            }
        };
        OSBlocks.BLOCKS_TO_REGISTER.stream().filter(block -> block instanceof Signal)
                .forEach(block -> event.registerBlock(extensions, block));
    }

    @SubscribeEvent
    public static void register(final ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(OpenSignalsMain.MODID, "signal"),
                SignalGeometryLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerResources(final AddPackFindersEvent event) {
        SignalModelResources.register(event);
    }
    @SubscribeEvent
    public static void addColor(final RegisterColorHandlersEvent.Block event) {
        final BlockColors colors = event.getBlockColors();
        OSBlocks.BLOCKS_TO_REGISTER.forEach(block -> {
            if (block instanceof Signal) {
                final Signal signal = (Signal) block;
                if (signal.hasCostumColor())
                    colors.register((_u1, _u2, _u3, index) -> signal.colorMultiplier(index), block);
            }
        });
    }
}
