package in.mcxiv.abyss.utilities;

import in.mcxiv.abyss.interfaces.CopyCloneable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Pool<T> {

    public static boolean RECORD_POOL_DATA = true;

    List<T> pool;
    Class<T> clazz;
    Function<T, Integer> getWeights;
    Function<Integer, T> constructor;
    Comparator<T> comparator;
    BiConsumer<T, Integer> ensureWeight;

    int liveEntities = 0;

    AtomicInteger dummy = new AtomicInteger(0);
    Comparator<T> dummyComparator;

    public Pool(Class<T> clazz) {
        this(
                clazz,
                _ -> 0,
                _ -> {
                    try {
                        return clazz.getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                },
                (_, _) -> {
                }
        );
        System.err.println("WARNING: Created a reflective pool! Targeted class name is " + clazz.getName() + ".");
    }

    public Pool(Class<T> clazz, Function<T, Integer> getWeights, Function<Integer, T> constructor, BiConsumer<T, Integer> ensureWeight) {
        this.pool = new LinkedList<>();
        this.clazz = clazz;
        this.getWeights = getWeights;
        this.constructor = constructor;
        this.comparator = Comparator.comparing(getWeights);
        this.dummyComparator = (a, _) -> getWeights.apply(a).compareTo(dummy.get());
        this.ensureWeight = ensureWeight;

        if (RECORD_POOL_DATA)
            new Recorder(() -> 1f * liveEntities, clazz.getSimpleName() + "Pool Entities");
    }

    public void free(Object source) {
        liveEntities--;

        if (!clazz.isInstance(source)) throw new IllegalStateException();
        var data = clazz.cast(source);
        int index = Collections.binarySearch(pool, data, comparator);
        if (index < 0) index = ~index;
        pool.add(index, data);
    }

    public T issueNewIfNotCompatible(T allocation, Object array) {
        if (allocation == null) return issue(array);
//        if (PolyData.isShapeSame(allocation, array)) return allocation;
        free(allocation);
        return issue(array);
    }

    public T issue(Object weight) {
        if (clazz.isInstance(weight)) return issue((int) getWeights.apply(clazz.cast(weight)));
        return issue(1);
    }

    public T clone(Object source) {
        if (!clazz.isInstance(source)) throw new IllegalStateException("Foreign class");
        if (source instanceof CopyCloneable cc) {
            T issue = issue(source);
            cc.copyTo((CopyCloneable) issue);
            return issue;
        } else throw new IllegalStateException();
    }

    public T issue() {
        return issue(1);
    }

    public T issue(int weight) {
        liveEntities++;

        if (pool.isEmpty()) return constructor.apply(weight);
        dummy.set(weight);
        int index = Collections.binarySearch(pool, null, dummyComparator);
        if (index < 0) index = Math.min(~index, pool.size() - 1);
        T source = pool.remove(index);
        if (weight > getWeights.apply(source)) ensureWeight.accept(source, weight);
        return source;
    }
}
