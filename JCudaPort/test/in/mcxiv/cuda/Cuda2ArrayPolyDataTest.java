package in.mcxiv.cuda;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.data.representation.PolyData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class Cuda2ArrayPolyDataTest {

    @Test
    void simpleValueGetSet() {
        var m = new Cuda2ArrayPolyData(10, 10);

        m.fill(1f);
        m.set(100.45f, 2, 3);
        m.set(45.1f, 6, 9);

        Assertions.assertEquals(100.45f, m.get(2, 3));
        Assertions.assertEquals(45.1f, m.get(6, 9));
        Assertions.assertEquals(1f, m.get(1, 1));
    }

    @Test
    void timeItTest() {
        PolyData m;
        long mark, time;

        m = new Cuda2ArrayPolyData(1000, 1000);
        m.fill(3.5f);
        mark = System.nanoTime();
        m.fill(4.5f);
        time = System.nanoTime() - mark;
        System.out.println(time);
        Assertions.assertEquals(4.5f, m.get(1, 1));

        m = new Array1DPolyData(1000, 1000);
        m.fill(3.5f);
        mark = System.nanoTime();
        m.fill(4.5f);
        time = System.nanoTime() - mark;
        System.out.println(time);
        Assertions.assertEquals(4.5f, m.get(1, 1));
    }

    @Test
    void testScalarOperation() {
        var m1 = new Cuda2ArrayPolyData(10, 4);
        m1.fill(0.1f);
        var m2 = new Cuda2ArrayPolyData(10, 4);
        m2.fill(0.2f);
        var m3 = m1.scalarOperation(m2, new Cuda2ArrayPolyData(), Float::sum);
        System.out.println(m3.get(0, 0));
        System.out.println(Arrays.toString(m3.shape()));
        System.out.println(m3);
    }

    @Test
    void testVectorOperation() {
        var m1 = new Cuda2ArrayPolyData(10, 4);
        m1.fill(0.1f);
        var m2 = new Cuda2ArrayPolyData(4, 6);
        m2.fill(0.2f);
        var m3 = m1.cross(m2);
        System.out.println(m3.get(0, 0));
        System.out.println(Arrays.toString(m3.shape()));
        System.out.println(m3);
    }
}