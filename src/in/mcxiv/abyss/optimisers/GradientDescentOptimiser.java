package in.mcxiv.abyss.optimisers;

import in.mcxiv.abyss.core.ErrorFunction;
import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.feeders.Feeder;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.updators.SimpleAdditiveUpdater;
import in.mcxiv.abyss.updators.Updater;
import in.mcxiv.abyss.utilities.Cache;
import in.mcxiv.abyss.utilities.Pools;

public class GradientDescentOptimiser extends Optimiser {

    MathematicalUnit model;
    Feeder trainFeeder;
    Feeder testFeeder;
    ErrorFunction error = ErrorFunction.meanSquaredError;
    ErrorFunction dError = ErrorFunction.dMeanSquaredError;
    Updater updater = new SimpleAdditiveUpdater();
    int epoch = 1000;

    public GradientDescentOptimiser(MathematicalUnit model, Feeder trainFeeder, Feeder testFeeder) {
        this.model = model;
        this.trainFeeder = trainFeeder;
        this.testFeeder = testFeeder;
        this.eventListeners.add(trainFeeder);
    }

    @Override
    protected void fitImplementation() {
        eventListeners.forEach(TrainingEventListener::trainingStarted);

        var cache = new Cache();
        Array1DPolyData yp = null, loss = null, dp = null, dx = null;

        for (int i = 0; i < epoch; i++) {

            eventListeners.forEach(TrainingEventListener::nextEpochTrainingStarted);

            while (trainFeeder.hasNext()) {

                eventListeners.forEach(TrainingEventListener::nextBatchTrainingStarted);

                var entry = trainFeeder.next();
                var trainX = entry.getKey();
                var trainY = entry.getValue();

                yp = Pools.ARRAY_POOL.issueNewIfNotCompatible(yp, trainY);
                loss = loss == null ? Pools.ARRAY_POOL.issue(yp.shape(0)) : loss;
                dp = Pools.ARRAY_POOL.issueNewIfNotCompatible(dp, trainY);
                dx = Pools.ARRAY_POOL.issueNewIfNotCompatible(dx, trainX);

                model.forward(trainX, yp, cache);

                float cost = error.netCalculate(trainY, yp, loss);
                eventListeners.forEach(trainingEventListener -> trainingEventListener.recordCost(cost));

                dError.calculate(trainY, yp, dp);
                model.backward(dp, dx, cache);
                updater.apply(cache);

                eventListeners.forEach(TrainingEventListener::nextBatchTrainingStopped);

            }

            eventListeners.forEach(TrainingEventListener::nextEpochTrainingStopped);

        }

        eventListeners.forEach(TrainingEventListener::trainingStopped);
    }
}