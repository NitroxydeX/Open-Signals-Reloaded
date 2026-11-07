package com.troblecodings.signals.init;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;

public final class OSTabs {

    private OSTabs() {
    }

    public static final CreativeModeTab TAB = CreativeModeTab.builder()
            .title(Component.literal("Open Signals"))
            .icon(() -> new ItemStack(OSItems.LINKING_TOOL))
            .displayItems((parameters, output) -> {
                OSItems.registeredItems.stream()
                        .filter(item -> !(item instanceof ArmorItem))
                        .filter(item -> item != OSItems.LINKING_TOOL && item != OSItems.MULTI_LINKING_TOOL)
                        .forEach(output::accept);
                OSBlocks.BLOCKS_TO_REGISTER.stream().filter(block -> block.shouldHaveItem())
                        .forEach(output::accept);
            }).build();
}
