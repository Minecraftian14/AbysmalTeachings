package in.mcxiv.abyss.optimisers.events;

import in.mcxiv.abyss.plot.PyPlot;

import java.util.ArrayList;
import java.util.List;

public class PlotLossAfterTraining implements in.mcxiv.abyss.optimisers.Optimiser.TrainingEventListener {

    List<Float> list = new ArrayList<>();
    List<Float> batch = new ArrayList<>();

    @Override
    public void trainingStarted() {
        list.clear();
    }

    @Override
    public void recordCost(float cost) {
        batch.add(cost);
    }

    @Override
    public void nextBatchTrainingStopped() {
        float min = Long.MAX_VALUE;
        for (Float v : batch)
            min = Math.min(min, v);
        list.add(min);
        batch.clear();
    }

    @Override
    public void trainingStopped() {
        PyPlot.plotBar("Loss Plot", list, true);
    }

}
