package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.core.ActivationFunction;
import in.mcxiv.abyss.data.presets.TestDataset;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.feeders.SingletonFeeder;
import in.mcxiv.abyss.mathematics.MoreMath;
import in.mcxiv.abyss.optimisers.GradientDescentOptimiser;
import in.mcxiv.abyss.optimisers.events.PlotLossAfterTraining;
import in.mcxiv.abyss.utilities.Cache;
import org.junit.jupiter.api.Test;

import static in.mcxiv.abyss.core.ErrorFunction.meanSquaredError;
import static in.mcxiv.abyss.core.ScoringFunction.accuracy;
import static in.mcxiv.abyss.core.ScoringFunction.fuzzy_accuracy;
import static in.mcxiv.abyss.data.representation.Array1DPolyData.n;
import static in.mcxiv.abyss.data.representation.PolyData.unaryOperation;

class MultiLayerUnitTest {
    @Test
    void basicTest() {

        var dataset = TestDataset.AND_DATA;
        PolyData x = dataset.features, y = dataset.targets;
        unaryOperation(x, x, f -> 2 * f - 1);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        var feeder = new SingletonFeeder(dataset);
        var connections = new FullyConnectedUnit[]{
                new FullyConnectedUnit(4),
                new FullyConnectedUnit(y.shape(1))
        };
        connections[0].weights.addPreprocessor(data -> data.fill(0.1f * MoreMath.random()));
        connections[1].weights.addPreprocessor(data -> data.fill(0.1f));
        var model = new MultiLayerUnit(
                connections[0],
                new ActivationUnit(ActivationFunction.SIGMOID),
                connections[1],
                new ActivationUnit(ActivationFunction.SIGMOID)
        );
//        var model = connections[1];
        model.initialize(x);

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));

        var optimiser = new GradientDescentOptimiser(model, feeder, null);
        optimiser.addListener(new PlotLossAfterTraining());
        optimiser.fit();

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));

        System.out.println(model.forward(x, n(), new Cache()));
    }
}