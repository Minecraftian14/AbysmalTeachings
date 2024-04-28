package in.mcxiv.abyss.data.presets;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.data.representation.PolyData;

import java.util.List;

public class TestDataset extends Dataset {
    private TestDataset(PolyData features, PolyData targets) {
        this.features = features;
        this.targets = targets;
    }

    public static final List<Float> BINARY_FEATURES = List.of(1f, 1f, 1f, 0f, 0f, 1f, 0f, 0f);

    public static final TestDataset OR_DATA = new TestDataset(
            new Array1DPolyData(4, 2).fill(BINARY_FEATURES.iterator()::next),
            new Array1DPolyData(4, 1).fill(List.of(1, 1, 1, 0).iterator()::next)
    );

    public static final TestDataset AND_DATA = new TestDataset(
            new Array1DPolyData(4, 2).fill(BINARY_FEATURES.iterator()::next),
            new Array1DPolyData(4, 1).fill(List.of(1, 0, 0, 0).iterator()::next)
    );

    public static final TestDataset XOR_DATA = new TestDataset(
            new Array1DPolyData(4, 2).fill(BINARY_FEATURES.iterator()::next),
            new Array1DPolyData(4, 1).fill(List.of(0, 1, 1, 0).iterator()::next)
    );
}
