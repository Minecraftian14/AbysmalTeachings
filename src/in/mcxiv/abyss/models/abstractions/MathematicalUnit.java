package in.mcxiv.abyss.models.abstractions;

import in.mcxiv.abyss.data.representation.LatePolyData;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.utilities.Cache;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class MathematicalUnit extends Unit {

    ArrayList<LatePolyData> initializableParameters = new ArrayList<>();

    protected void addInitiazables(LatePolyData... d) {
        initializableParameters.addAll(Arrays.asList(d));
    }

    public final int[] initialize(PolyData exampleInput) {
        return initialize(exampleInput.shape());
    }

    public int[] initialize(int[] inputDims) {
        initializableParameters.forEach(parameter -> parameter.initialize(inputDims));
        return inputDims;
    }

    public abstract PolyData forward(PolyData a_in, PolyData a_out, Cache cache);

    public abstract PolyData backward(PolyData da_out, PolyData da_in, Cache cache);

}
