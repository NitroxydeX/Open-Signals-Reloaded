package com.troblecodings.signals.handler;

import java.util.HashMap;
import java.util.Map;

import com.troblecodings.core.ReadBuffer;
import com.troblecodings.core.interfaces.INetworkSync;
import com.troblecodings.signals.core.StateInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ClientNameHandler implements INetworkSync {

    private static final Map<StateInfo, String> CLIENT_NAMES = new HashMap<>();

    public static String getClientName(final StateInfo info) {
        synchronized (CLIENT_NAMES) {
            return CLIENT_NAMES.getOrDefault(info, "");
        }
    }

    @Override
    public void deserializeClient(final ReadBuffer buffer) {
        final Minecraft mc = Minecraft.getInstance();
        final BlockPos pos = buffer.getBlockPos();
        final ClientLevel world = mc.level;
        if (world == null)
            return;
        final StateInfo info = new StateInfo(world, pos);
        final boolean removed = buffer.getBoolean();
        if (removed) {
            setRemoved(info);
            return;
        }
        final String name = buffer.getString();
        synchronized (CLIENT_NAMES) {
            CLIENT_NAMES.put(info, name);
        }
        final BlockState state = world.getBlockState(pos);
        if (state == null)
            return;
        world.setBlocksDirty(pos, state, state);
        world.setBlockAndUpdate(pos, state);
    }

    private static void setRemoved(final StateInfo info) {
        synchronized (CLIENT_NAMES) {
            CLIENT_NAMES.remove(info);
        }
    }

}