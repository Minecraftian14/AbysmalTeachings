package in.mcxiv.abyss.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PoolTest {

    @Test
    void simpleTest() {
        var pool = new Pool<>(
                StringBuilder.class,
                StringBuilder::capacity,
                StringBuilder::new,
                StringBuilder::ensureCapacity
        );

        assertEquals(0, pool.pool.size());
        StringBuilder first = pool.issue(100);
        assertEquals(0, pool.pool.size());
        assertEquals(100, first.capacity());
        pool.free(first);
        assertEquals(1, pool.pool.size());
        pool.free(new StringBuilder(50));
        pool.free(new StringBuilder(150));
        assertEquals(3, pool.pool.size());
        assertEquals(100, pool.issue(69).capacity());
        assertEquals(150, pool.issue(69).capacity());
        assertEquals(102, pool.issue(69).capacity());
        assertEquals(69, pool.issue(69).capacity());
    }
}