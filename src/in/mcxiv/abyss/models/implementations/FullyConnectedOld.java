package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.core.ActivationFunction;
import in.mcxiv.abyss.data.representation.LatePolyData;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.initializers.Additive2DParameterInitializer;
import in.mcxiv.abyss.initializers.Multiplicative2DParameterInitializer;
import in.mcxiv.abyss.mathematics.MoreMath;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.utilities.Cache;
import in.mcxiv.abyss.utilities.Pools;

import static in.mcxiv.abyss.data.representation.PolyData.*;

public class FullyConnectedOld extends MathematicalUnit {

    public LatePolyData weights;
    LatePolyData bias;

//    PolyData dweights = new Array1DPolyData(1);
//    PolyData dbias = new Array1DPolyData(1);

    public FullyConnectedOld(int hiddenUnits) {
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
        a_in.cross( weights, a_out);
        a_out.add( bias, a_out);
        a_out.unaryOperation(a_out, a_out, ActivationFunction::sigmoid);
        cache.put(this, "a_in", a_in);
        cache.put(this, "a_out", a_out.clone());
        return a_out;
    }

    @Override
    public PolyData backward(PolyData da_out, PolyData da_in, Cache cache) {
        var a_in = cache.remove(this, "a_in");
        var a_out = cache.remove(this, "a_out");

        var dweights = Pools.ARRAY_POOL.issue(weights);
        var dbias = Pools.ARRAY_POOL.issue(bias);

        a_out.unaryOperation(a_out, a_out, ActivationFunction::dsigmoid);
        a_out.mul( da_out, a_out);

        a_in.transpose().cross( a_out, dweights);
        a_out.sumAlong(a_out, 0, dbias);
        a_out.cross( weights.transpose(), da_in);

        cache.putParameter("weights", weights, dweights);
        cache.putParameter("bias", bias, dbias);

        return da_in;
    }
}