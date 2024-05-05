package in.mcxiv.abyss.core;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.utilities.Pools;

public interface ScoringFunction {

    float score(PolyData y, PolyData p);

    ScoringFunction accuracy = (y, p) -> {
        if (!y.isShapeSame(p)) throw new IllegalStateException();
        float sum = 0;
        var ys = y.slice(0);
        var ps = p.slice(0);
        for (int i = 0, s = y.shape(0); i < s; i++) {
            sum += y.slice(ys, i).areEqual(p.slice(ps, i)) ? 1 : 0;
        }
        return sum / p.shape(0);
    };

    // Assuming that the range of p is within [0, 1]
    ScoringFunction fuzzy_accuracy = (y, p) -> {
        if (!y.isShapeSame(p)) throw new IllegalStateException();
        float sum = 0;
        var ys = y.slice(0);
        var ps = p.slice(0);
        var ms = Pools.ARRAY_POOL.issue(ys);
        for (int i = 0, s = y.shape(0); i < s; i++) {
            sum += Math.abs(y.slice(ys, i).sub(p.slice(ps, i), ms).sumAll());
        }
        Pools.ARRAY_POOL.free(ms);
        return 1 - sum / p.shape(0);
    };

}
