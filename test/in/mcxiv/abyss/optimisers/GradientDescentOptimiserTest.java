package in.mcxiv.abyss.optimisers;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.feeders.SingletonFeeder;
import in.mcxiv.abyss.models.implementations.FullyConnected;
import in.mcxiv.abyss.models.implementations.FullyConnectedOld;
import in.mcxiv.abyss.optimisers.events.PlotLossAfterTraining;
import in.mcxiv.abyss.utilities.Cache;
import org.junit.jupiter.api.Test;

import java.util.List;

import static in.mcxiv.abyss.core.ErrorFunction.meanSquaredError;
import static in.mcxiv.abyss.core.ScoringFunction.accuracy;
import static in.mcxiv.abyss.core.ScoringFunction.fuzzy_accuracy;
import static in.mcxiv.abyss.data.representation.Array1DPolyData.n;

class GradientDescentOptimiserTest {

    @Test
    void simpleTest() {
        var x = new Array1DPolyData(4, 2);
        x.fill(List.of(1, 1, 1, 0, 0, 1, 0, 0, 0).iterator()::next);
        var y = new Array1DPolyData(4, 1);
        y.fill(List.of(1, 0, 0, 0).iterator()::next);
        var feeder = new SingletonFeeder(x, y);

//        var model = new FullyConnectedOld(y.shape(1));
        var model = new FullyConnected(y.shape(1));
        model.weights.addPreprocessor(data -> data.fill(0.01f));

        model.initialize(x);

        System.out.println("Loss     : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));

        var optimiser = new GradientDescentOptimiser(model, feeder, null);
        optimiser.eventListeners.add(new PlotLossAfterTraining());
        optimiser.fit();

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("y_pred     : " + model.forward(x, n(), new Cache()));
    }
}