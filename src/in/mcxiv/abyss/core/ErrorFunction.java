package in.mcxiv.abyss.core;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.mathematics.MoreMath;
import in.mcxiv.abyss.utilities.Pools;

import static in.mcxiv.abyss.data.representation.PolyData.sumAll;

public interface ErrorFunction {

    PolyData calculate(PolyData y, PolyData p, PolyData l);

    default float netCalculate(PolyData y, PolyData p, PolyData l) {
        return sumAll(calculate(y, p, l)) / y.shape(0);
    }

    ErrorFunction meanSquaredError = (y, p, l) -> {
        if (!PolyData.isShapeSame(y, p))
            throw new UnsupportedOperationException();
        var buffer = Pools.ARRAY_POOL.issue(y);
        PolyData.sub(y, p, buffer);
        PolyData.sqr(buffer, buffer);
        PolyData.reduceSum(buffer, 0, l);
        float order = 1f / (MoreMath.sumItems(y.dims(), y::shape) * 2);
        PolyData.mul(l, order, l);
        Pools.ARRAY_POOL.free(buffer);
        return l;
    };

    ErrorFunction dMeanSquaredError = (y, p, dp) -> {
        if (!PolyData.isShapeSame(y, p))
            throw new UnsupportedOperationException();
        return PolyData.sub(y, p, dp);
    };

}
