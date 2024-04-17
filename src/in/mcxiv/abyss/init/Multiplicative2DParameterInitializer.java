package in.mcxiv.abyss.init;

import in.mcxiv.abyss.data.Array1DPolyData;
import in.mcxiv.abyss.data.PolyData;

public class Multiplicative2DParameterInitializer implements ParameterInitializer {

    final int order;

    public Multiplicative2DParameterInitializer(int order) {
        this.order = order;
    }

    @Override
    public PolyData apply(int[] ints) {
        return new Array1DPolyData(ints[ints.length - 1], order);
    }
}
