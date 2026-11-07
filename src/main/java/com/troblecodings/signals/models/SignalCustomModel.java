package com.troblecodings.signals.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import com.troblecodings.signals.OpenSignalsMain;
import com.troblecodings.signals.core.SignalAngel;

public final class SignalCustomModel implements IUnbakedGeometry<SignalCustomModel> {
    private static final Map<ResourceLocation, BakedModel> LOCATION_TO_MODEL = new ConcurrentHashMap<>();
    public static final RandomSource RANDOM = RandomSource.create();
    private final SignalAngel angle;
    private final List<SignalModelLoaderInfo> definitions;
    private final List<BlockModel> parts = new ArrayList<>();

    public SignalCustomModel(final SignalAngel angle, final List<SignalModelLoaderInfo> definitions) {
        this.angle = angle;
        this.definitions = definitions;
        for (final SignalModelLoaderInfo info : definitions) {
            final JsonObject json = new JsonObject();
            json.addProperty("parent", OpenSignalsMain.MODID + ":block/" + info.name);
            final JsonObject textures = new JsonObject();
            info.retexture.forEach(textures::addProperty);
            json.add("textures", textures);
            parts.add(BlockModel.fromString(json.toString()));
        }
    }

    @Override
    public void resolveParents(final Function<ResourceLocation, UnbakedModel> modelGetter,
            final IGeometryBakingContext context) {
        parts.forEach(part -> part.resolveParents(modelGetter));
    }

    @Override
    public BakedModel bake(final IGeometryBakingContext context, final ModelBaker baker,
            final Function<Material, TextureAtlasSprite> spriteGetter, final ModelState modelState,
            final ItemOverrides overrides) {
        final List<BakedModelPair> baked = new ArrayList<>();
        for (int index = 0; index < definitions.size(); index++) {
            final SignalModelLoaderInfo info = definitions.get(index);
            final Transformation translation = new Transformation(new Vector3f(info.x, info.y, info.z),
                    null, null, null);
            final ModelState partState = new SimpleModelState(
                    modelState.getRotation().compose(translation), modelState.isUvLocked());
            final BakedModel part = parts.get(index).bake(baker, spriteGetter, partState);
            final Matrix4f rotation = new Matrix4f().translation(0.5f, 0, 0.5f)
                    .rotateY((float) angle.getRadians()).translate(-0.5f, 0, -0.5f);
            final BakedModel transformed = new TransformedPart(part, rotation);
            baked.add(new BakedModelPair(info.state, transformed));
            if (angle == SignalAngel.ANGEL0 && info.isAnimation) {
                LOCATION_TO_MODEL.put(ResourceLocation.fromNamespaceAndPath(OpenSignalsMain.MODID,
                        info.name), transformed);
            }
        }
        if (baked.isEmpty()) {
            throw new IllegalArgumentException("Signal model has no parts: " + context.getModelName());
        }
        return new SignalBakedModel(baked, context.getTransforms(), overrides);
    }

    static void clearAnimationModels() {
        LOCATION_TO_MODEL.clear();
    }

    public static BakedModel getModelFromLocation(final ResourceLocation location) {
        return LOCATION_TO_MODEL.getOrDefault(location,
                Minecraft.getInstance().getModelManager().getMissingModel());
    }

    private static final class TransformedPart extends BakedModelWrapper<BakedModel> {
        private final Matrix4f rotation;
        private final List<BakedQuad> quads;

        private TransformedPart(final BakedModel original, final Matrix4f rotation) {
            super(original);
            this.rotation = rotation;
            this.quads = bakeQuads();
        }

        private List<BakedQuad> bakeQuads() {
            final RandomSource random = RandomSource.create(0);
            final List<BakedQuad> source = new ArrayList<>(originalModel.getQuads(null, null,
                    random, ModelData.EMPTY, null));
            for (final Direction direction : Direction.values()) {
                source.addAll(originalModel.getQuads(null, direction, random, ModelData.EMPTY, null));
            }
            final List<BakedQuad> transformed = new ArrayList<>(source.size());
            for (final BakedQuad quad : source) {
                final Vector3f normal = new Vector3f(quad.getDirection().getStepX(),
                        quad.getDirection().getStepY(), quad.getDirection().getStepZ());
                rotation.transformDirection(normal);
                final BakedQuad copy = new BakedQuad(quad.getVertices().clone(), quad.getTintIndex(),
                        Direction.getNearest(normal.x, normal.y, normal.z), quad.getSprite(),
                        quad.isShade(), quad.hasAmbientOcclusion());
                QuadTransformers.applying(new Transformation(rotation)).processInPlace(copy);
                final String texture = quad.getSprite().contents().name().toString();
                final int light = texture.contains("lamp_") ? 0x00F000F0
                        : texture.contains("reflection_") ? 0x00780078 : -1;
                if (light >= 0) {
                    final int[] vertices = copy.getVertices();
                    final int stride = vertices.length / 4;
                    for (int vertex = 0; vertex < 4; vertex++) vertices[vertex * stride + 6] = light;
                }
                transformed.add(copy);
            }
            return List.copyOf(transformed);
        }

        @Override
        public List<BakedQuad> getQuads(final BlockState state, final Direction side,
                final RandomSource random, final ModelData data, final RenderType renderType) {
            return side == null ? quads : List.of();
        }
        @Override
        public List<BakedQuad> getQuads(final BlockState state, final Direction side,
                final RandomSource random) {
            return getQuads(state, side, random, ModelData.EMPTY, null);
        }
    }
}