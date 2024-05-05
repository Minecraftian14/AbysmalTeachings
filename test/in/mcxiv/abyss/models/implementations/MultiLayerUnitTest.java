package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.core.ActivationFunction;
import in.mcxiv.abyss.data.presets.TestDataset;
import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.data.representation.SlicedPolyData.Slice;
import in.mcxiv.abyss.feeders.SingletonFeeder;
import in.mcxiv.abyss.mathematics.MoreMath;
import in.mcxiv.abyss.optimisers.GradientDescentOptimiser;
import in.mcxiv.abyss.optimisers.events.EchoTrainingProgress;
import in.mcxiv.abyss.optimisers.events.PlotLossAfterTraining;
import in.mcxiv.abyss.updators.SimpleAdditiveUpdater;
import in.mcxiv.abyss.utilities.Cache;
import in.mcxiv.abyss.utilities.Pool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

import static in.mcxiv.abyss.core.ErrorFunction.meanSquaredError;
import static in.mcxiv.abyss.core.ScoringFunction.accuracy;
import static in.mcxiv.abyss.core.ScoringFunction.fuzzy_accuracy;
import static in.mcxiv.abyss.data.representation.Array1DPolyData.n;

class MultiLayerUnitTest {

    @BeforeEach
    void setUp() {
        Pool.RECORD_POOL_DATA = false;
    }

    @Test
    void basicTest() {

        var dataset = TestDataset.AND_DATA;
        PolyData x = dataset.features, y = dataset.targets;
        x.unaryOperation(x, x, f -> 2 * f - 1);
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

        var optimiser = new GradientDescentOptimiser(model, feeder, null, new SimpleAdditiveUpdater(0.1f));
        optimiser.addListener(new PlotLossAfterTraining());
        optimiser.fit();

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));

        System.out.println(model.forward(x, n(), new Cache()));
    }

    @Test
    void testWithMNIST() {
        int limlen = Math.min(100 /* Reduce or increase this value to adjust the size of data loaded from training                                                                 */, 10000);
        PolyData x = new Array1DPolyData(limlen, 28, 28, 1);
        var y = new Array1DPolyData(limlen, 10);

        try (var stream = Files.lines(Path.of("res/datasets/mnist_test.csv"))) {
            x.fill(stream.flatMap(s -> {
                String[] split = s.split(",");
                return IntStream.range(1, split.length).mapToObj(i -> 0.00390625f * Integer.parseInt(split[i]));
            }).iterator()::next);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (var stream = Files.lines(Path.of("res/datasets/mnist_test.csv"))) {
            y.fill(stream.flatMap(s -> {
                int index = Integer.parseInt(s.split(",", 2)[0]);
                return IntStream.range(0, 10).mapToObj(value -> index == value ? 1 : 0);
            }).iterator()::next);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        var s = x.slice(new Slice()
//                .all()
//                .from(12).to(16)
//                .from(12).to(16)
//                .all()
//        );
//        float[] export = s.export();
//        x = new Array1DPolyData(limlen, 4, 4, 1);
//        x.fill(IntStream.range(0, export.length).mapToObj(i -> export[i]).iterator()::next);
//        System.out.println(Arrays.toString(x.shape()));
//        x.reshape(limlen, 64);
//        if (true)
//            return;
        ////////////

        var feeder = new SingletonFeeder(x, y);
        var model = new MultiLayerUnit(
                new ImageBatchConvolutionalUnit(5, 3) {{
                    filter.addPreprocessor(pd -> pd.fill(MoreMath::randomNormal));
                }},
                new ActivationUnit(ActivationFunction.SIGMOID),
                new FlattenUnit(),
                new FullyConnectedUnit(y.shape(1)) {{
                    weights.addPreprocessor(pd -> pd.fill(MoreMath::randomNormal));
                }},
                new ActivationUnit(ActivationFunction.SIGMOID)
        );
        model.initialize(x);

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));

        var optimiser = new GradientDescentOptimiser(model, feeder, null, new SimpleAdditiveUpdater(0.01f));
        optimiser.epoch=10;
        optimiser.addListener(new PlotLossAfterTraining());
        optimiser.addListener(new EchoTrainingProgress());
        optimiser.fit();

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y.argMax(1).reshape(limlen, 1), model.forward(x, n(), new Cache()).argMax(1).reshape(limlen, 1)));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));
    }

    @Test
    void convTest() {
        PolyData x = new Array1DPolyData(10, 5, 5, 3);
        PolyData y = new Array1DPolyData(10, 3);
        x.fill(MoreMath::random);
        y.fill(MoreMath::randomBit);
        x.unaryOperation(f -> 2 * f - 1);
        var feeder = new SingletonFeeder(x, y);
        var model = new MultiLayerUnit(
                new ImageBatchConvolutionalUnit(10, 3) {{
                    filter.addPreprocessor(pd -> pd.fill(MoreMath::randomNormal));
                }},
                new ActivationUnit(ActivationFunction.SIGMOID),
                new FlattenUnit(),
                new FullyConnectedUnit(y.shape(1)) {{
                    weights.addPreprocessor(pd -> pd.fill(MoreMath::randomNormal));
                }},
                new ActivationUnit(ActivationFunction.SIGMOID)
        );
        model.initialize(x);

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));

        var updater = new SimpleAdditiveUpdater(0.0000001f);
        var optimiser = new GradientDescentOptimiser(model, feeder, null, updater);
        optimiser.addListener(new PlotLossAfterTraining());
        optimiser.epoch = 10;
        optimiser.fit();

        System.out.println("Loss       : " + meanSquaredError.netCalculate(y, model.forward(x, n(), new Cache()), n()));
        System.out.println("Accuracy   : " + accuracy.score(y, model.forward(x, n(), new Cache())));
        System.out.println("F Accuracy : " + fuzzy_accuracy.score(y, model.forward(x, n(), new Cache())));

        System.out.println(model.forward(x, n(), new Cache()));
    }
}