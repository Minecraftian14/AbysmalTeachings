package in.mcxiv.abyss.initializers;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.data.representation.PolyData;

public class Additive2DParameterInitializer implements ParameterInitializer {

    final int order;

    public Additive2DParameterInitializer(int order) {
        this.order = order;
    }

    @Override
    public PolyData apply(int[] ints) {
        return new Array1DPolyData(1, order);
    }
}
