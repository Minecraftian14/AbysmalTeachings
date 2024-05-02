package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.core.ActivationFunction;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.utilities.Cache;

import static in.mcxiv.abyss.utilities.Pools.ARRAY_POOL;

public class ActivationUnit extends CustomOperationUnit {

    public ActivationUnit(ActivationFunction function) {
        super(function::activate, function::deactivate);
    }

    @Override
    public PolyData forward(PolyData a_in, PolyData a_out, Cache cache) {
        super.forward(a_in, a_out, cache);
        cache.put(this, "outputs", ARRAY_POOL.clone(a_out));
        return a_out;
    }

    @Override
    public PolyData backward(PolyData da_out, PolyData da_in, Cache cache) {
        var outputs = cache.get(this, "outputs");
        // d activation / d inputs
        super.backward(outputs, outputs, cache);
        // d loss / d inputs
        PolyData.mul(da_out, outputs, da_in);
        ARRAY_POOL.free(outputs);
        return da_in;
    }
}
