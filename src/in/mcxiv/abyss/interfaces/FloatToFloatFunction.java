package in.mcxiv.abyss.interfaces;

import java.util.function.Function;

public interface FloatToFloatFunction extends Function<Float, Float> {

    float applyAsFloat(float f);

    @Override
    default Float apply(Float f) {
        return applyAsFloat(f);
    }
}
