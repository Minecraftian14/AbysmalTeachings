package in.mcxiv.abyss.interfaces;

import java.util.function.BiFunction;

@FunctionalInterface
public
interface FloatOperation extends BiFunction<Float, Float, Float> {
    float operate(float a, float b);

    @Override
    default Float apply(Float aFloat, Float aFloat2) {
        return operate(aFloat, aFloat2);
    }
}
