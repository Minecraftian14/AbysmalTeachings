package in.mcxiv.abyss.data.representation;

import in.mcxiv.abyss.initializers.ParameterInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LatePolyData extends PolyDataPolyData {

    ParameterInitializer initializer;
    List<Consumer<PolyData>> preprocessors = new ArrayList<>();

    public LatePolyData(ParameterInitializer initializer) {
        this.initializer = initializer;
    }

    public void initialize(PolyData exampleInput) {
        initialize(exampleInput.shape());
    }

    public void initialize(int[] inputDims) {
        polyData = initializer.apply(inputDims);
        initializer = null;
        preprocessors.forEach(p -> p.accept(polyData));
    }

    public void addPreprocessor(Consumer<PolyData> preprocessor) {
        preprocessors.add(preprocessor);
    }

    @Override
    public PolyData clone() {
        return super.clone();
    }

}
