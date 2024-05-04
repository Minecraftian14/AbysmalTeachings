package in.mcxiv.abyss.initializers;

import in.mcxiv.abyss.data.representation.Array1DPolyData;
import in.mcxiv.abyss.data.representation.PolyData;

public class Convolutive2DFCParameterInitializer implements ParameterInitializer {

    final int filter_count;
    final int filter_size;

    public Convolutive2DFCParameterInitializer(int filter_count, int filter_size) {
        this.filter_count = filter_count;
        this.filter_size = filter_size;
    }

    @Override
    public PolyData apply(int[] ints) {
        return new Array1DPolyData(filter_size, filter_size, ints[ints.length - 1], filter_count);
    }
}
