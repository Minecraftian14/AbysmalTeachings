package in.mcxiv.abyss.interfaces;

import java.util.function.Function;
import java.util.function.IntToLongFunction;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface IntToIntFunction extends Function<Integer, Integer>, IntToLongFunction, LongToIntFunction {
    int get(int value);

    @Override
    default Integer apply(Integer integer) {
        return get(integer);
    }

    @Override
    default long applyAsLong(int value) {
        return get(value);
    }

    @Override
    default int applyAsInt(long value) {
        return get((int) value);
    }
}
