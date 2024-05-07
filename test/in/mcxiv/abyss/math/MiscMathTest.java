package in.mcxiv.abyss.math;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.mathematics.MiscMath;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MiscMathTest {

    @Test
    void testRemoved() {
        assertArrayEquals(new int[]{1, 3, 4, 5, 6, 7, 8, 9, 10}, MiscMath.removed(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 1));
    }

    @Test
    void testSumAll() {
        var data = new Array1DPolyData(3, 3, 3);
        var ai = new AtomicInteger();
        data.fill(ai::incrementAndGet);
        System.out.println(data);
        var sum = data.reduceSum(0, new Array1DPolyData(1));
        System.out.println(sum);
        assertArrayEquals(new float[]{45.0f, 126.0f, 207.0f}, sum.export());
    }
}