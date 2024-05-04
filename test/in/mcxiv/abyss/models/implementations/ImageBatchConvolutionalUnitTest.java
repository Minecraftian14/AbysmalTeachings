package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
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

class ImageBatchConvolutionalUnitTest {

    @Test
    void basicTest() {
        PolyData x = new Array1DPolyData(10, 5, 5, 3);
        PolyData y = new Array1DPolyData(10, 3, 3, 5);
        x.fill(MoreMath::random);
        y.fill(MoreMath::random);
        unaryOperation(x, x, f -> 2 * f - 1);
        var feeder = new SingletonFeeder(x, y);
        var model = new ImageBatchConvolutionalUnit(5, 3);
        model.filter.addPreprocessor(pd -> pd.fill(0.1f));
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