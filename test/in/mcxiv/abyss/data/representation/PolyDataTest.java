package in.mcxiv.abyss.data.representation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class PolyDataTest {
    @Test
    void testAdd() {
        PolyData m1 = new Array1DPolyData(2, 3, 5);
        m1.fill(3);
        PolyData m2 = new Array1DPolyData(2, 3, 5);
        AtomicInteger integer = new AtomicInteger();
        m2.fill(integer::incrementAndGet);
        PolyData m3 = new Array1DPolyData(5, 5);
        PolyData.add(m1, m2, m3);
        Assertions.assertArrayEquals(new float[]{4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f, 16.0f, 17.0f, 18.0f, 19.0f, 20.0f, 21.0f, 22.0f, 23.0f, 24.0f, 25.0f, 26.0f, 27.0f, 28.0f, 29.0f, 30.0f, 31.0f, 32.0f, 33.0f}, m3.export());
    }

    @Test
    void testCross() {
        PolyData m1 = new Array1DPolyData(3, 2);
        m1.fill(List.of(0, 1, 2, 3, 4, 5).iterator()::next);
        PolyData m2 = new Array1DPolyData(2, 4);
        m2.fill(List.of(0, 1, 2, 3, 4, 5, 6, 7).iterator()::next);
        PolyData m3 = new Array1DPolyData(5, 5);
        PolyData.cross(m1, m2, m3);
        Assertions.assertArrayEquals(new float[]{4.0f, 5.0f, 6.0f, 7.0f, 12.0f, 17.0f, 22.0f, 27.0f, 20.0f, 29.0f, 38.0f, 47.0f}, m3.export());
    }

    @Test
    void testReduceSumAlong() {
        PolyData m1 = new Array1DPolyData(4, 3, 2);
        AtomicInteger integer = new AtomicInteger();
        m1.fill(integer::getAndIncrement);
        PolyData m2 = new Array1DPolyData(1);
        PolyData.sumAlong(m1, 0, m2);
        Assertions.assertArrayEquals(new float[]{36.0f, 40.0f, 44.0f, 48.0f, 52.0f, 56.0f}, m2.export());
        PolyData.sumAlong(m1, 1, m2);
        Assertions.assertArrayEquals(new float[]{6.0f, 9.0f, 24.0f, 27.0f, 42.0f, 45.0f, 60.0f, 63.0f}, m2.export());
    }

    @Test
    void testSlice() {
        var data = new Array1DPolyData(3, 3, 3);
        var ai = new AtomicInteger();
        data.fill(ai::incrementAndGet);
        System.out.println(data);
        System.out.println(PolyData.slice(data, 0));
    }
}