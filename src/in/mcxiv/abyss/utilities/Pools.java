package in.mcxiv.abyss.utilities;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.data.representation.SlicedPolyData;

import java.util.HashMap;

public final class Pools {
    private Pools() {
    }

    public static final Pool<Array1DPolyData> ARRAY_POOL = new Pool<>(
            Array1DPolyData.class,
            Array1DPolyData::allocationSize,
            Array1DPolyData::new,
            Array1DPolyData::reshape
    );

    public static final Pool<SlicedPolyData> SLICE_POOL = new Pool<>(
            SlicedPolyData.class,
            _ -> 0,
            _ -> new SlicedPolyData(),
            (_, _) -> {
            }
    );

    @SuppressWarnings("rawtypes")
    private static final HashMap<Class, Pool> POOLS = new HashMap<>();

    static {
        POOLS.put(Array1DPolyData.class, ARRAY_POOL);
        POOLS.put(SlicedPolyData.class, SLICE_POOL);
    }

    public static <T> Pool<T> getPool(Class<T> clazz) {
        if (POOLS.containsKey(clazz)) return POOLS.get(clazz);
        var pool = new Pool<>(clazz);
        POOLS.put(clazz, pool);
        return pool;
    }

    public static <T> T issue(Class<T> clazz, int weight) {
        return getPool(clazz).issue(weight);
    }

    public static <T> void putPool(Class<T> clazz, Pool<T> pool) {
        if (POOLS.containsKey(clazz)) throw new IllegalStateException();
        POOLS.put(clazz, pool);
    }

    public static void free(Object source) {
        getPool(source.getClass()).free(source);
    }
}
