package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.*;
import in.mcxiv.abyss.mathematics.MiscMath;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.utilities.Cache;

import static in.mcxiv.abyss.utilities.Pools.ARRAY_POOL;

public class VeryLimitedAndPracticallyUselessConvolutionalUnit extends MathematicalUnit {

    public PolyData filter;

    public VeryLimitedAndPracticallyUselessConvolutionalUnit(int hiddenUnits) {
        filter = new Array1DPolyData(3, 3);
//        addInitiazables(filter, bias);
    }

    @Override
    public int[] initialize(int[] inputDims) {
        super.initialize(inputDims);
        return MiscMath.convolveDimensions(inputDims, filter.shape());
    }

    @Override
    public PolyData forward(PolyData a_in, PolyData a_out, Cache cache) {
        a_in.convolveOperation( filter, a_out);
        cache.put(this, "a_in", ARRAY_POOL.clone(a_in));
        return a_out;
    }

    @Override
    public PolyData backward(PolyData da_out, PolyData da_in, Cache cache) {
        var a_in = cache.remove(this, "a_in");

        var dweights = ARRAY_POOL.issue(filter);

//        convolveOperation(da_out, filter, dweights);
        a_in.convolveOperation( da_out, dweights);

        var pad_da_out = new PadImagePolyData(da_out, 0, 2,2);
        var flip_filter = new FlipImagePolyData(filter);
        pad_da_out.convolveOperation( flip_filter, da_in);

        cache.putParameter("weights", filter, dweights);

        ARRAY_POOL.free(a_in);

        return da_in;
    }
}