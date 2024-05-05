package in.mcxiv.cuda;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.mathematics.MoreMath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CudaArrayPolyDataTest {

    @Test
    void testCuda() {
        var m1a = new Array1DPolyData(10, 8);
        var m1c = new CudaArrayPolyData(10, 8);
        var m2 = new CudaArrayPolyData(8, 6);
        var m3a = new Array1DPolyData();
        var m3c = new Array1DPolyData();

        m1a.fill(() -> MoreMath.randomInt(10));
        System.arraycopy(m1a.data, 0, m1c.data, 0, m1a.data.length);
        m2.fill(() -> MoreMath.randomInt(10));

        System.out.println("m1a = " + m1a);
        System.out.println("m1c = " + m1c);
        System.out.println("m2  = " + m2);

        m1a.cross(m2, m3a);
        m1c.cross(m2, m3c);

        System.out.println("m3a = " + m3a);
        System.out.println("m3c = " + m3c);

        Assertions.assertArrayEquals(m3a.data, m3c.data);
    }

    @Test
    void testSpeed() {
        var m1a = new Array1DPolyData(10000, 100);
        var m1c = new CudaArrayPolyData(m1a.shape());
        var m2 = new Array1DPolyData(m1a.shape(1), 50);
        var m3 = new Array1DPolyData();
        m1a.fill(MoreMath::random10);
        m1c.fill(MoreMath::random10);
        m2.fill(MoreMath::random10);

        float time_a = 0;
        float time_c = 0;
        int iterations = 5;
        for (int i = 0; i < iterations; i++) {
//            if (i % (iterations / 10) == 0)
            System.out.println(i + " iters compl");

            long mark = System.nanoTime();
            m1a.cross(m2, m3);
            long delta = System.nanoTime() - mark;
            time_a += delta * 1f / iterations;

            mark = System.nanoTime();
            m1c.cross(m2, m3);
            delta = System.nanoTime() - mark;
            time_c += delta * 1f / iterations;
        }

        System.out.println("Array " + time_a + " ns");
        System.out.println("Cuda  " + time_c + " ns");
    }
}