package in.mcxiv.abyss.feeders;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.data.representation.SlicedPolyData;

import java.util.AbstractMap;
import java.util.Map;

public class BatchFeeder implements Feeder {

    private int batchIndex = 0;
    private int batchSize;
    private int batchCount;
    private int[] sliceStart = new int[1];
    private int[] sliceEnd = new int[1];
    private final PolyData inputs;
    private final PolyData outputs;
    private final Map.Entry<SlicedPolyData, SlicedPolyData> data;

    public BatchFeeder(PolyData inputs, PolyData outputs, int batchSize) {
        this.inputs = inputs;
        this.outputs = outputs;
        sliceStart[0] = 0;
        sliceEnd[0] = batchSize;
        this.data = new AbstractMap.SimpleEntry<>(
                new SlicedPolyData(inputs, sliceStart, sliceEnd),
                new SlicedPolyData(outputs, sliceStart, sliceEnd)
        );
        this.batchSize = batchSize;
        this.batchCount = Math.ceilDiv(inputs.shape(0), batchSize);
    }

    @Override
    public void trainingStarted() {
        batchIndex = 0;
    }

    @Override
    public void trainingStopped() {

    }

    @Override
    public boolean hasNext() {
        return batchIndex < batchCount;
    }

    @Override
    public Map.Entry<? extends PolyData, ? extends PolyData> next() {
        sliceStart[0] = batchIndex * batchSize;
        sliceEnd[0] = Math.min(inputs.shape(0), (batchIndex + 1) * batchSize);
        data.getKey().initialize(inputs, sliceStart, sliceEnd);
        data.getValue().initialize(outputs, sliceStart, sliceEnd);
        batchIndex++;
        return data;
    }
}
