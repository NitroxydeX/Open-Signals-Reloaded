package com.troblecodings.signals.models;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

public class SignalBakedModel implements IDynamicBakedModel {
    private final BakedModel baseGetter;
    private final List<BakedModelPair> bakedCache;
    private final ItemTransforms transforms;
    private final ItemOverrides overrides;

    public SignalBakedModel(final List<BakedModelPair> bakedCache,
            final ItemTransforms transforms, final ItemOverrides overrides) {
        this.bakedCache = List.copyOf(bakedCache);
        this.baseGetter = bakedCache.getFirst().model;
        this.transforms = transforms;
        this.overrides = overrides;
    }

    @Override public boolean useAmbientOcclusion() { return baseGetter.useAmbientOcclusion(); }
    @Override public boolean isGui3d() { return baseGetter.isGui3d(); }
    @Override public boolean usesBlockLight() { return baseGetter.usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public TextureAtlasSprite getParticleIcon() { return findParticleIcon(null); }
    @Override public ItemOverrides getOverrides() { return overrides; }
    @Override public ItemTransforms getTransforms() { return transforms; }

    @Override
    public TextureAtlasSprite getParticleIcon(final ModelData data) {
        return findParticleIcon(data);
    }

    private TextureAtlasSprite findParticleIcon(final ModelData data) {
        final ModelInfoWrapper wrapper = data == null ? null : new ModelInfoWrapper(data);
        final ModelData modelData = data == null ? ModelData.EMPTY : data;
        for (final BakedModelPair pair : bakedCache) {
            if (wrapper != null && !pair.predicate.test(wrapper)) continue;
            final TextureAtlasSprite sprite = pair.model.getParticleIcon(modelData);
            if (!sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) return sprite;
            for (final BakedQuad quad : pair.model.getQuads(null, null, RandomSource.create(42), modelData, null)) {
                if (!quad.getSprite().contents().name().equals(MissingTextureAtlasSprite.getLocation()))
                    return quad.getSprite();
            }
        }
        return data == null ? baseGetter.getParticleIcon() : findParticleIcon(null);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(final BlockState state, final RandomSource random,
            final ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.cutoutMipped());
    }

    @Override
    public List<BakedQuad> getQuads(final BlockState state, final Direction side,
            final RandomSource random, final ModelData data, final RenderType renderType) {
        if (renderType != null && renderType != RenderType.cutoutMipped()) return List.of();
        final List<BakedQuad> quads = new ArrayList<>();
        final ModelInfoWrapper wrapper = new ModelInfoWrapper(data);
        for (final BakedModelPair pair : bakedCache) {
            if (pair.predicate.test(wrapper)) {
                quads.addAll(pair.model.getQuads(state, side, random, data, renderType));
            }
        }
        return quads;
    }
}
