package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.LatePolyData;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.initializers.Additive2DParameterInitializer;
import in.mcxiv.abyss.initializers.Multiplicative2DParameterInitializer;
import in.mcxiv.abyss.mathematics.MoreMath;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.utilities.Cache;

import static in.mcxiv.abyss.data.representation.PolyData.*;
import static in.mcxiv.abyss.utilities.Pools.ARRAY_POOL;

public class FullyConnected extends MathematicalUnit {

    public LatePolyData weights;
    LatePolyData bias;

//    PolyData dweights = new Array1DPolyData(1);
//    PolyData dbias = new Array1DPolyData(1);

    public FullyConnected(int hiddenUnits) {
        weights = new LatePolyData(new Multiplicative2DParameterInitializer(hiddenUnits));
        bias = new LatePolyData(new Additive2DParameterInitializer(hiddenUnits));
        addInitiazables(weights, bias);
    }

    @Override
    public int[] initialize(int[] inputDims) {
        super.initialize(inputDims);
        return MoreMath.crossDimensions(inputDims, weights.shape());
    }

    @Override
    public PolyData forward(PolyData a_in, PolyData a_out, Cache cache) {
        cross(a_in, weights, a_out);
        add(a_out, bias, a_out);
        cache.put(this, "a_in", ARRAY_POOL.clone(a_in));
        return a_out;
    }

    @Override
    public PolyData backward(PolyData da_out, PolyData da_in, Cache cache) {
        var a_in = cache.remove(this, "a_in");

        var dweights = ARRAY_POOL.issue(weights);
        var dbias = ARRAY_POOL.issue(bias);

        cross(a_in.transpose(), da_out, dweights);
        sumAlong(da_out, 0, dbias);
        cross(da_out, weights.transpose(), da_in);

        cache.putParameter("weights", weights, dweights);
        cache.putParameter("bias", bias, dbias);

        ARRAY_POOL.free(a_in);

        return da_in;
    }
}