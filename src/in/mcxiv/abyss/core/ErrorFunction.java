package in.mcxiv.abyss.core;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.mathematics.MoreMath;
import in.mcxiv.abyss.utilities.Pools;


public interface ErrorFunction {

    PolyData calculate(PolyData y, PolyData p, PolyData l);

    default float netCalculate(PolyData y, PolyData p, PolyData l) {
        return calculate(y, p, l).sumAll() / y.shape(0);
    }

    ErrorFunction meanSquaredError = (y, p, l) -> {
        if (!y.isShapeSame(p))
            throw new UnsupportedOperationException();
        var diff_sqr = y.sub(p, Pools.ARRAY_POOL.issue(y)).sqr();
        diff_sqr.reduceSum(diff_sqr, 0, l);
        Pools.ARRAY_POOL.free(diff_sqr);
        return l.div(2*l.shape(0));
    };

    ErrorFunction dMeanSquaredError = (y, p, dp) -> {
        if (!y.isShapeSame(p))
            throw new UnsupportedOperationException();
        return y.sub(p, dp);
    };
}