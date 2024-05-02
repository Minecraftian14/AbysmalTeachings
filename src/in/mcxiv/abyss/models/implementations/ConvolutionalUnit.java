package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.*;
import in.mcxiv.abyss.initializers.Additive2DParameterInitializer;
import in.mcxiv.abyss.initializers.Multiplicative2DParameterInitializer;
import in.mcxiv.abyss.mathematics.MoreMath;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.utilities.Cache;

import static in.mcxiv.abyss.data.representation.PolyData.*;
import static in.mcxiv.abyss.utilities.Pools.ARRAY_POOL;

public class ConvolutionalUnit extends MathematicalUnit {

    public PolyData filter;

    public ConvolutionalUnit(int hiddenUnits) {
        filter = new Array1DPolyData(3, 3);
//        addInitiazables(filter, bias);
    }

    @Override
    public int[] initialize(int[] inputDims) {
        super.initialize(inputDims);
        return MoreMath.convolveDimensions(inputDims, filter.shape());
    }

    @Override
    public PolyData forward(PolyData a_in, PolyData a_out, Cache cache) {
        convolveOperation(a_in, filter, a_out);
        cache.put(this, "a_in", ARRAY_POOL.clone(a_in));
        return a_out;
    }

    @Override
    public PolyData backward(PolyData da_out, PolyData da_in, Cache cache) {
        var a_in = cache.remove(this, "a_in");

        var dweights = ARRAY_POOL.issue(filter);

//        convolveOperation(da_out, filter, dweights);
        convolveOperation(a_in, da_out, dweights);

        var pad_da_out = new PadImagePolyData(da_out, 2, 0);
        var flip_filter = new FlipImagePolyData(filter);
        convolveOperation(pad_da_out, flip_filter, da_in);

        cache.putParameter("weights", filter, dweights);

        ARRAY_POOL.free(a_in);

        return da_in;
    }
}