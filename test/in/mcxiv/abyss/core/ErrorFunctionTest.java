package in.mcxiv.abyss.core;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ErrorFunctionTest {
    @Test
    void testMSE() {
        var y = Array1DPolyData.fromArray(1, 2, 3, 4);
        var p = Array1DPolyData.fromArray(2, 4, 3, 4);

        System.out.println(ErrorFunction.meanSquaredError.calculate(y, p, new Array1DPolyData(1)));
        assertArrayEquals(new float[]{0.25f, 1f, 0f, 0f}, ErrorFunction.meanSquaredError.calculate(y, p, new Array1DPolyData(1)).export());
    }
}