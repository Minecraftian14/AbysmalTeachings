package in.mcxiv.abyss.core;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.utilities.Pools;

import static in.mcxiv.abyss.data.representation.PolyData.*;

public interface ScoringFunction {

    float score(PolyData y, PolyData p);

    ScoringFunction accuracy = (y, p) -> {
        if (!PolyData.isShapeSame(y, p)) throw new IllegalStateException();
        float sum = 0;
        var ys = slice(y, 0);
        var ps = slice(p, 0);
        for (int i = 0, s = y.shape(0); i < s; i++) {
            sum += areEqual(slice(y, ys, i), slice(p, ps, i)) ? 1 : 0;
        }
        return sum / p.shape(0);
    };

    // Assuming that the range of p is within [0, 1]
    ScoringFunction fuzzy_accuracy = (y, p) -> {
        if (!PolyData.isShapeSame(y, p)) throw new IllegalStateException();
        float sum = 0;
        var ys = slice(y, 0);
        var ps = slice(p, 0);
        var ms = Pools.ARRAY_POOL.issue(ys);
        for (int i = 0, s = y.shape(0); i < s; i++) {
            sum += Math.abs(PolyData.sumAll(sub(slice(y, ys, i), slice(p, ps, i), ms)));
        }
        Pools.ARRAY_POOL.free(ms);
        return 1 - sum / p.shape(0);
    };

}
