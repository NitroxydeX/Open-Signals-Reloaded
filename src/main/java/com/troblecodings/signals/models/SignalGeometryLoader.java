package com.troblecodings.signals.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import com.troblecodings.signals.core.SignalAngel;

public final class SignalGeometryLoader implements IGeometryLoader<SignalCustomModel> {
    public static final SignalGeometryLoader INSTANCE = new SignalGeometryLoader();

    private SignalGeometryLoader() {}

    @Override
    public SignalCustomModel read(final JsonObject json,
            final JsonDeserializationContext context) {
        final String signal = GsonHelper.getAsString(json, "signal");
        final int angle = GsonHelper.getAsInt(json, "angle", 0);
        if (angle < 0 || angle >= SignalAngel.values().length) {
            throw new JsonParseException("Invalid signal angle: " + angle);
        }
        return new SignalCustomModel(SignalAngel.values()[angle],
                CustomModelLoader.INSTANCE.getDefinition(signal));
    }
}