package com.troblecodings.signals.core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.troblecodings.signals.tileentitys.SignalTileEntity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;

public class RenderOverlayInfo {

    public final PoseStack stack;
    public final double x;
    public final double y;
    public final double z;
    public SignalTileEntity tileEntity;
    public final Font font;
    public final MultiBufferSource source;
    public final int lightColor;

    public RenderOverlayInfo(final PoseStack stack, final double x, final double y, final double z,
            final Font font, final MultiBufferSource source, final int lightColor) {
        this.stack = stack;
        this.x = x;
        this.y = y;
        this.z = z;
        this.font = font;
        this.source = source;
        this.lightColor = lightColor;
    }

    public RenderOverlayInfo with(final SignalTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        return this;
    }
}