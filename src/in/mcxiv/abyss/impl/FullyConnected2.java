package in.mcxiv.abyss.impl;

import in.mcxiv.abyss.abs.MathematicalUnit;
import in.mcxiv.abyss.core.ActivationFunction;
import in.mcxiv.abyss.data.Array1DPolyData.Pool;
import in.mcxiv.abyss.data.LatePolyData;
import in.mcxiv.abyss.data.PolyData;
import in.mcxiv.abyss.init.Additive2DParameterInitializer;
import in.mcxiv.abyss.init.Multiplicative2DParameterInitializer;
import in.mcxiv.abyss.util.Cache;

import static in.mcxiv.abyss.data.PolyData.*;

public class FullyConnected2 extends MathematicalUnit {

    LatePolyData weights;
    LatePolyData bias;

//    PolyData dweights = new Array1DPolyData(1);
//    PolyData dbias = new Array1DPolyData(1);

    public FullyConnected2(int hiddenUnits) {
        weights = new LatePolyData(new Multiplicative2DParameterInitializer(hiddenUnits));
        bias = new LatePolyData(new Additive2DParameterInitializer(hiddenUnits));
//        bias.initialize();
//        dweights.reshape(weights.shape());
        addInitiazables(weights, bias);
    }

    public void forward(PolyData a_in, PolyData a_out, Cache cache) {
        cross(a_in, weights, a_out);
        unaryOperation(a_out, a_out, ActivationFunction::sigmoid);
        cache.put(this, "a_in", a_in);
        cache.put(this, "a_out", a_out.clone());
    }

    public void backward(PolyData da_out, PolyData da_in, Cache cache) {
        var a_in = cache.get(this, "a_in");
        var a_out = cache.get(this, "a_out");

        var dweights = Pool.issue(weights.shape());

        unaryOperation(a_out, a_out, ActivationFunction::dsigmoid);
        dot(a_out, da_out, a_out);

        cross(a_in.transpose(), a_out, dweights);
        cross(a_out, weights.transpose(), da_in);

        unaryOperation(dweights, dweights, f -> 0.1f * f);

        add(weights, dweights, weights);

        Pool.free(dweights);
    }
}