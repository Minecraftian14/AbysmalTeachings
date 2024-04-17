package in.mcxiv.abyss.impl;

import in.mcxiv.abyss.data.Array1DPolyData;
import in.mcxiv.abyss.data.Array1DPolyData.Pool;
import in.mcxiv.abyss.util.Cache;
import org.junit.jupiter.api.Test;

import java.util.List;

import static in.mcxiv.abyss.core.LossFunction.dMeanSquaredError;
import static in.mcxiv.abyss.core.LossFunction.meanSquaredError;
import static in.mcxiv.abyss.data.PolyData.sum;

class FullyConnected2Test {

    @Test
    void testBasic() {
        var x = new Array1DPolyData(4, 2);
        x.fill(List.of(1, 1, 1, -1, -1, 1, -1, -1).iterator()::next);
        var y = new Array1DPolyData(4, 1);
        y.fill(List.of(10, 1, -1, -2).iterator()::next);

        var model = new FullyConnected2(1);
        model.weights.addPreprocessor(data -> data.fill(0.1f));
        model.initialize(x);

        var yp = Pool.issue(y.shape());
        var cache = new Cache();
        model.forward(x, yp, cache);
        System.out.println(yp);

        var loss = Pool.issue();
        var dp = Pool.issue();
        var dx = Pool.issue();

        for (int i = 0; i < 10000; i++) {
            model.forward(x, yp, cache);
            float lossf = sum(meanSquaredError(y, yp, loss)) / y.shape(0);
            System.out.println("Loss :" + lossf);
            dMeanSquaredError(y, yp, dp);
            model.backward(dp, dx, cache);
        }
    }
}