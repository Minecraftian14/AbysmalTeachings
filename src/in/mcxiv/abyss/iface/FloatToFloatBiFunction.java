package in.mcxiv.abyss.iface;

import java.util.function.BiFunction;

@FunctionalInterface
public interface FloatToFloatBiFunction extends BiFunction<Float, Float, Float> {

    float applyAsFloat(float a, float b);

    @Override
    default Float apply(Float aFloat, Float aFloat2) {
        return applyAsFloat(aFloat, aFloat2);
    }
}
