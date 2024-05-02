package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.utilities.Cache;
import in.mcxiv.abyss.utilities.Pools;
import org.junit.jupiter.api.Test;

import java.util.List;

import static in.mcxiv.abyss.core.ErrorFunction.dMeanSquaredError;
import static in.mcxiv.abyss.core.ErrorFunction.meanSquaredError;
import static in.mcxiv.abyss.data.representation.PolyData.sumAll;

class FullyConnectedUnit2Test {

    @Test
    void testBasic() {
        var x = new Array1DPolyData(4, 2);
        x.fill(List.of(1, 1, 1, -1, -1, 1, -1, -1).iterator()::next);
        var y = new Array1DPolyData(4, 1);
        y.fill(List.of(10, 1, -1, -2).iterator()::next);

        var model = new FullyConnectedUnit(1);
        model.weights.addPreprocessor(data -> data.fill(0.1f));
        model.initialize(x);

        var yp = Pools.ARRAY_POOL.issue(y.shape());
        var cache = new Cache();
        model.forward(x, yp, cache);
        System.out.println(yp);

        var loss = Pools.ARRAY_POOL.issue();
        var dp = Pools.ARRAY_POOL.issue();
        var dx = Pools.ARRAY_POOL.issue();

        for (int i = 0; i < 10000; i++) {
            model.forward(x, yp, cache);
            float lossf = sumAll(meanSquaredError.calculate(y, yp, loss)) / y.shape(0);
            System.out.println("Loss :" + lossf);
            dMeanSquaredError.calculate(y, yp, dp);
            model.backward(dp, dx, cache);
        }
    }
}