package com.troblecodings.signals.models;

import java.util.Map;

import com.troblecodings.core.interfaces.BlockModelDataWrapper;
import com.troblecodings.signals.SEProperty;

import net.neoforged.neoforge.client.model.data.ModelData;

import net.neoforged.neoforge.client.model.data.ModelData.Builder;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class ModelInfoWrapper implements BlockModelDataWrapper {

    private ModelData data;

    public ModelInfoWrapper(final Map<SEProperty, String> states) {
        final Builder builder = ModelData.builder();
        states.forEach((property, value) -> builder.with(property, value));
        this.data = builder.build();
    }

    public ModelInfoWrapper(final ModelData data) {
        this.data = data;
    }

    @Override
    public boolean hasProperty(final ModelProperty<?> prop) {
        return data.has(prop);
    }

    @Override
    public <T> T getData(final ModelProperty<T> prop) {
        return data.get(prop);
    }

    @Override
    public <T> T setData(final ModelProperty<T> prop, final T value) {
        final T previous = data.get(prop);
        data = data.derive().with(prop, value).build();
        return previous;
    }

    @Override
    public ModelData toModelData() {
        return data;
    }

    public boolean has(final SEProperty property) {
        return hasProperty(property);
    }

    public String get(final SEProperty property) {
        return getData(property);
    }

    public void set(final SEProperty property, final String value) {
        setData(property, value);
    }
}