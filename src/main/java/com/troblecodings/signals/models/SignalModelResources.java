package com.troblecodings.signals.models;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import com.troblecodings.signals.OpenSignalsMain;
import com.troblecodings.signals.blocks.Signal;
import com.troblecodings.signals.core.SignalAngel;

public final class SignalModelResources extends AbstractPackResources {
    private final Map<ResourceLocation, byte[]> resources = new HashMap<>();

    private SignalModelResources(final PackLocationInfo location) {
        super(location);
        CustomModelLoader.INSTANCE.invalidate();
        for (final String signal : Signal.SIGNALS.keySet()) {
            final JsonObject variants = new JsonObject();
            for (final SignalAngel angle : SignalAngel.values()) {
                final String path = "block/generated/" + signal + "_" + angle.ordinal();
                final JsonObject model = new JsonObject();
                model.addProperty("loader", OpenSignalsMain.MODID + ":signal");
                model.addProperty("signal", signal);
                model.addProperty("angle", angle.ordinal());
                model.addProperty("render_type", "minecraft:cutout_mipped");
                put("models/" + path + ".json", model);
                final JsonObject variant = new JsonObject();
                variant.addProperty("model", OpenSignalsMain.MODID + ":" + path);
                variants.add("angel=" + angle.getNameWrapper(), variant);
            }
            final JsonObject blockstate = new JsonObject();
            blockstate.add("variants", variants);
            put("blockstates/" + signal + ".json", blockstate);
            putEmptyModel("models/item/" + signal + ".json");
        }
        final JsonObject ghostVariant = new JsonObject();
        ghostVariant.addProperty("model", OpenSignalsMain.MODID + ":block/generated/ghostblock");
        final JsonObject ghostVariants = new JsonObject();
        ghostVariants.add("", ghostVariant);
        final JsonObject ghostState = new JsonObject();
        ghostState.add("variants", ghostVariants);
        put("blockstates/ghostblock.json", ghostState);
        putEmptyModel("models/block/generated/ghostblock.json");
        putEmptyModel("models/item/ghostblock.json");
    }

    private void putEmptyModel(final String path) {
        final JsonObject model = new JsonObject();
        model.addProperty("loader", "neoforge:empty");
        put(path, model);
    }

    private void put(final String path, final JsonObject json) {
        resources.put(ResourceLocation.fromNamespaceAndPath(OpenSignalsMain.MODID, path),
                json.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static void register(final AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
        event.addRepositorySource(consumer -> {
            final PackLocationInfo location = new PackLocationInfo(
                    OpenSignalsMain.MODID + ":signal_models", Component.literal("Open Signals models"),
                    PackSource.BUILT_IN, Optional.empty());
            final Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
                @Override
                public PackResources openPrimary(final PackLocationInfo info) {
                    return new SignalModelResources(info);
                }

                @Override
                public PackResources openFull(final PackLocationInfo info, final Pack.Metadata metadata) {
                    return new SignalModelResources(info);
                }
            };
            final Pack pack = Pack.readMetaAndCreate(location, supplier, PackType.CLIENT_RESOURCES,
                    new PackSelectionConfig(true, Pack.Position.BOTTOM, false));
            if (pack != null) consumer.accept(pack);
        });
    }

    @Override
    public IoSupplier<InputStream> getRootResource(final String... elements) {
        if (elements.length != 1 || !elements[0].equals("pack.mcmeta")) return null;
        final byte[] metadata = "{\"pack\":{\"pack_format\":34,\"description\":\"Open Signals models\"}}"
                .getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(metadata);
    }

    @Override
    public IoSupplier<InputStream> getResource(final PackType type, final ResourceLocation location) {
        final byte[] data = type == PackType.CLIENT_RESOURCES ? resources.get(location) : null;
        return data == null ? null : () -> new ByteArrayInputStream(data);
    }

    @Override
    public void listResources(final PackType type, final String namespace, final String path,
            final ResourceOutput output) {
        if (type != PackType.CLIENT_RESOURCES) return;
        resources.forEach((location, data) -> {
            if (location.getNamespace().equals(namespace) && location.getPath().startsWith(path + "/")) {
                output.accept(location, () -> new ByteArrayInputStream(data));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(final PackType type) {
        return type == PackType.CLIENT_RESOURCES ? Set.of(OpenSignalsMain.MODID) : Set.of();
    }

    @Override
    public void close() {}
}