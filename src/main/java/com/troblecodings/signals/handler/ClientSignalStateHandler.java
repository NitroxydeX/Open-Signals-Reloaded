package com.troblecodings.signals.handler;


import java.util.HashMap;
import java.util.Map;



import com.google.common.collect.ImmutableMap;
import com.troblecodings.core.ReadBuffer;
import com.troblecodings.core.interfaces.INetworkSync;
import com.troblecodings.signals.SEProperty;
import com.troblecodings.signals.blocks.Signal;
import com.troblecodings.signals.core.NetworkBufferWrappers;
import com.troblecodings.signals.core.StateInfo;
import com.troblecodings.signals.enums.ChangedState;
import com.troblecodings.signals.tileentitys.SignalTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientSignalStateHandler implements INetworkSync {

    private static final Map<StateInfo, Map<SEProperty, String>> CURRENTLY_LOADED_STATES =
            new HashMap<>();

    public static final Map<SEProperty, String> getClientStates(final StateInfo info) {
        synchronized (CURRENTLY_LOADED_STATES) {
            return ImmutableMap
                    .copyOf(CURRENTLY_LOADED_STATES.computeIfAbsent(info, _u -> new HashMap<>()));
        }
    }


    @Override
    public void deserializeClient(final ReadBuffer buffer) {
        final Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            final ClientLevel level = mc.level;
            if (level == null)
                return;
            final BlockPos signalPos = buffer.getBlockPos();
            final StateInfo stateInfo = new StateInfo(level, signalPos);
            final int signalID = buffer.getInt();
            final ChangedState changedState = buffer.getEnumValue(ChangedState.class);
            if (changedState.equals(ChangedState.REMOVED_FROM_CACHE)
                    || changedState.equals(ChangedState.REMOVED_FROM_FILE)) {
                setRemoved(stateInfo);
                return;
            }
            final Signal signal = Signal.getSignalByID(signalID);
            final Map<SEProperty, String> newProperties = buffer.getMapWithCombinedValueFunc(
                    NetworkBufferWrappers.getSEPropertyFunc(signal),
                    (buf, prop) -> prop.getObjFromID(buf.getByteToUnsignedInt()));
            synchronized (CURRENTLY_LOADED_STATES) {
                final Map<SEProperty, String> properties =
                        CURRENTLY_LOADED_STATES.computeIfAbsent(stateInfo, _u -> new HashMap<>());
                properties.putAll(newProperties);
                CURRENTLY_LOADED_STATES.put(stateInfo, properties);
            }
            final BlockEntity entity = level.getBlockEntity(signalPos);
            if (entity instanceof SignalTileEntity) {
                entity.requestModelDataUpdate();
            }

        });
    }

    private static void setRemoved(final StateInfo info) {
        synchronized (CURRENTLY_LOADED_STATES) {
            CURRENTLY_LOADED_STATES.remove(info);
        }
    }

}