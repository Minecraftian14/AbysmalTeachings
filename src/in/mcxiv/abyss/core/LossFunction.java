package in.mcxiv.abyss.core;

import in.mcxiv.abyss.data.Array1DPolyData.Pool;
import in.mcxiv.abyss.data.PolyData;
import in.mcxiv.abyss.math.MoreMath;

public class LossFunction {

    public static PolyData meanSquaredError(PolyData y, PolyData p, PolyData l) {
        if (!PolyData.isShapeSame(y, p))
            throw new UnsupportedOperationException();
        var buffer = Pool.issue(y);
        PolyData.subtract(y, p, buffer);
        PolyData.square(buffer, buffer);
        PolyData.sumAll(buffer, 0, l);
        float order = 1f / (MoreMath.sumElements(y.dims(), y::shape) * 2);
        PolyData.dot(l, order, l);
        Pool.free(buffer);
        return l;
    }

    public static PolyData dMeanSquaredError(PolyData y, PolyData p, PolyData dp) {
        if (!PolyData.isShapeSame(y, p))
            throw new UnsupportedOperationException();
        return PolyData.subtract(y, p, dp);
    }

}
