package in.mcxiv.abyss.util;

import in.mcxiv.abyss.data.Array1DPolyData;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheTest {
    @Test
    void testDepth() {
        Cache cache = new Cache();
        Array1DPolyData data = new Array1DPolyData(3, 3);
        cache.put(this, "something", data);
        Integer key = cache.keySet().stream().findFirst().get();
        assertEquals(Objects.hash(this.hashCode(), "something"), key);
        assertEquals(data, cache.get(this, "something"));
    }
}