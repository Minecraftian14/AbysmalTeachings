package in.mcxiv.abyss.interfaces;

import java.util.function.BiFunction;

@FunctionalInterface
public interface IntToFloatBiFunction extends BiFunction<Integer, Integer, Float> {
    float get(int x, int y);

    @Override
    default Float apply(Integer a, Integer b) {
        return get(a, b);
    }
}
