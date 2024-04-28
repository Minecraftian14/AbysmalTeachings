package in.mcxiv.abyss.core;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorFunctionTest {

    @Test
    void testMSE() {
        var y = new Array1DPolyData(4, 1);
        var p = new Array1DPolyData(4, 1);

        y.fill(List.of(1, 2, 3, 4).iterator()::next);
        p.fill(List.of(2, 4, 3, 4).iterator()::next);
        assertArrayEquals(new float[]{1f, 4f, 0f, 0f}, ErrorFunction.meanSquaredError.calculate(y, p, new Array1DPolyData(1)).export());
    }
}