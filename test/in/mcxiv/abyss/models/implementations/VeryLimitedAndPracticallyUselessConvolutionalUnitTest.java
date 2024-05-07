package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.feeders.SingletonFeeder;
import in.mcxiv.abyss.mathematics.MiscMath;
import in.mcxiv.abyss.optimisers.GradientDescentOptimiser;
import in.mcxiv.abyss.optimisers.events.PlotLossAfterTraining;
import in.mcxiv.abyss.updators.SimpleAdditiveUpdater;
import in.mcxiv.abyss.utilities.Cache;
import org.junit.jupiter.api.Test;

import static in.mcxiv.abyss.core.ErrorFunction.meanSquaredError;
import static in.mcxiv.abyss.core.ScoringFunction.accuracy;
import static in.mcxiv.abyss.core.ScoringFunction.fuzzy_accuracy;
import static in.mcxiv.abyss.data.representation.Array1DPolyData.n;

class VeryLimitedAndPracticallyUselessConvolutionalUnitTest {

    @Test
    void basicTest() {
//        var dataset = TestDataset.AND_DATA;
//        PolyData x = dataset.features, y = dataset.targets;

        PolyData x = new Array1DPolyData(5, 5);
        PolyData y = new Array1DPolyData(3, 3);
        x.fill(MiscMath::random);
        y.fill(MiscMath::random);

        x.unaryOperation( f -> 2 * f - 1);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
//        var feeder = new SingletonFeeder(dataset);
        var feeder = new SingletonFeeder(x, y);
        var model = new VeryLimitedAndPracticallyUselessConvolutionalUnit(97);
        model.filter.fill(0.1f * MiscMath.random());
        model.initialize(x);

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));

        var optimiser = new GradientDescentOptimiser(model, feeder, null, new SimpleAdditiveUpdater(0.01f));
        optimiser.addListener(new PlotLossAfterTraining());
        optimiser.fit();

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));

        System.out.println(model.forward(x, n(), new Cache()));
    }
}