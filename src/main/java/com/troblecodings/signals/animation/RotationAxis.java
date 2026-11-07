package com.troblecodings.signals.animation;

import java.util.Arrays;

import org.joml.Quaternionf;
import com.troblecodings.signals.OpenSignalsMain;

public enum RotationAxis {

    X("X"), Y("Y"), Z("Z");

    private final String axis;

    private RotationAxis(final String axis) {
        this.axis = axis;
    }

    public static RotationAxis of(final String axis) {
        return Arrays.stream(values())
                .filter(rotationAxis -> rotationAxis.axis.equalsIgnoreCase(axis)).findFirst()
                .orElseGet(() -> {
                    OpenSignalsMain.exitMinecraftWithMessage("[" + axis
                            + "] is not a valid axis for a RotationAnimation! Valid are: "
                            + values());
                    return null;
                });
    }

    public Quaternionf getForAxis(final float value) {
        switch (this) {
            case X:
                return new Quaternionf().rotationX(value);
            case Y:
                return new Quaternionf().rotationY(value);
            case Z:
                return new Quaternionf().rotationZ(value);
            default:
                return new Quaternionf();
        }
    }

}