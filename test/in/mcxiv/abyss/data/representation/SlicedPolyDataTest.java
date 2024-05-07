package in.mcxiv.abyss.data.representation;

import in.mcxiv.abyss.mathematics.MiscMath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SlicedPolyDataTest {

    @Test
    void test() {
        var data = new Array1DPolyData(10, 4, 5, 6, 4);
        data.fill(MiscMath::random);
        System.out.println(data);

        var slice = new SlicedPolyData(data, 1, SlicedPolyData.ALL, 1);
        assertEquals(3, slice.dims);
        assertArrayEquals(new int[]{4, 6, 4}, slice.shape);
        slice.upcastAddress(2, 3, 4);
        assertArrayEquals(new int[]{1, 2, 1, 3, 4}, slice.buffer);

        slice = new SlicedPolyData(data, new int[]{3}, new int[]{6});
        assertEquals(5, slice.dims);
        assertArrayEquals(new int[]{3, 4, 5, 6, 4}, slice.shape);
        slice.upcastAddress(1, 2, 3, 4, 5);
        assertArrayEquals(new int[]{4, 2, 3, 4, 5}, slice.buffer);

        slice = new SlicedPolyData(data, 1, SlicedPolyData.ALL, 1, SlicedPolyData.ALL, 1);
        assertEquals(2, slice.dims);
        assertArrayEquals(new int[]{4, 6}, slice.shape);
        slice.upcastAddress(1, 2);
        assertArrayEquals(new int[]{1, 1, 1, 2, 1}, slice.buffer);
    }
}