package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.LatePolyData;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.initializers.Additive2DParameterInitializer;
import in.mcxiv.abyss.initializers.Multiplicative2DParameterInitializer;
import in.mcxiv.abyss.mathematics.MiscMath;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.utilities.Cache;

import static in.mcxiv.abyss.utilities.Pools.ARRAY_POOL;

public class FullyConnectedUnit extends MathematicalUnit {

    public LatePolyData weights;
    LatePolyData bias;

    public FullyConnectedUnit(int hiddenUnits) {
        weights = new LatePolyData(new Multiplicative2DParameterInitializer(hiddenUnits));
        bias = new LatePolyData(new Additive2DParameterInitializer(hiddenUnits));
        addInitiazables(weights, bias);
    }

    @Override
    public int[] initialize(int[] inputDims) {
        super.initialize(inputDims);
        return MiscMath.crossDimensions(inputDims, weights.shape());
    }

    @Override
    public PolyData forward(PolyData a_in, PolyData a_out, Cache cache) {
        a_in.cross(weights, a_out);
        a_out.add(bias, a_out);
        cache.put(this, "a_in", ARRAY_POOL.clone(a_in));
        return a_out;
    }

    @Override
    public PolyData backward(PolyData da_out, PolyData da_in, Cache cache) {
        var a_in = cache.remove(this, "a_in");

        var dweights = ARRAY_POOL.issue(weights);
        var dbias = ARRAY_POOL.issue(bias);

        a_in.transpose().cross(da_out, dweights);
        da_out.sumAlong(0, dbias);
        da_out.cross(weights.transpose(), da_in);

        cache.putParameter("weights", weights, dweights);
        cache.putParameter("bias", bias, dbias);

        ARRAY_POOL.free(a_in);

        return da_in;
    }
}