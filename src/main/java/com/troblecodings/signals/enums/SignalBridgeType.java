package com.troblecodings.signals.enums;

import com.troblecodings.signals.OpenSignalsMain;
import com.troblecodings.signals.init.OSBlocks;
import com.troblecodings.signals.signalbridge.SignalBridgeBasicBlock;
import com.troblecodings.signals.signalbridge.SignalBridgeBlockProperties;


public enum SignalBridgeType {

    BASE, MAST, MAST_HEAD, CANTILEVER, CANTILEVER_END;

    public SignalBridgeBasicBlock createNewBlock(final String name,
            final SignalBridgeBlockProperties properties) {
        final SignalBridgeBasicBlock block = new SignalBridgeBasicBlock(properties);
        if (OSBlocks.BLOCKS_TO_REGISTER.contains(block)) {
            OpenSignalsMain.exitMinecraftWithMessage(
                    "Block for [" + name.toLowerCase() + "] alredy exists!");
        }
        OSBlocks.loadBlock(block, name);
        return block;
    }

}