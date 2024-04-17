package in.mcxiv.abyss.abs;

import in.mcxiv.abyss.data.LatePolyData;
import in.mcxiv.abyss.data.PolyData;

import java.util.ArrayList;
import java.util.Arrays;

public class MathematicalUnit extends Unit {

    ArrayList<LatePolyData> initializableParameters = new ArrayList<>();

    protected void addInitiazables(LatePolyData... d) {
        initializableParameters.addAll(Arrays.asList(d));
    }

    public void initialize(PolyData exampleInput) {
        initialize(exampleInput.shape());
    }

    public void initialize(int[] inputDims) {
        initializableParameters.forEach(parameter -> parameter.initialize(inputDims));
    }

}
