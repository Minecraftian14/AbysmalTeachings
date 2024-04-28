package in.mcxiv.abyss.feeders;

import in.mcxiv.abyss.data.presets.Dataset;
import in.mcxiv.abyss.data.representation.PolyData;

import java.util.AbstractMap;
import java.util.Map;

public class SingletonFeeder implements Feeder {

    private boolean isAvailable = true;
    private final Map.Entry<PolyData, PolyData> data;

    public SingletonFeeder(Dataset dataset) {
        this(dataset.features, dataset.targets);
    }

    public SingletonFeeder(PolyData inputs, PolyData outputs) {
        data = new AbstractMap.SimpleEntry<>(inputs, outputs);
    }

    @Override
    public void nextEpochTrainingStarted() {
        isAvailable = true;
    }

    @Override
    public boolean hasNext() {
        return isAvailable;
    }

    @Override
    public Map.Entry<PolyData, PolyData> next() {
        if (!isAvailable) return null;
        isAvailable = false;
        return data;
    }
}