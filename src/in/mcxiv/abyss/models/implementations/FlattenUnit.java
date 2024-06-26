package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.mathematics.MiscMath;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.utilities.Cache;

public class FlattenUnit extends MathematicalUnit {

    @Override
    public int[] initialize(int[] inputDims) {
        super.initialize(inputDims);
        inputDims = inputDims.clone();
        int[] result = new int[2];
        result[0] = inputDims[0];
        inputDims[0] = 1;
        result[1] = MiscMath.multiplyItems(inputDims);
        return result;
    }

    @Override
    public PolyData forward(PolyData a_in, PolyData a_out, Cache cache) {
        int[] shape = a_in.shape();
        shape[0] = 1;
        a_out.reshape(a_in).fill(0);
        a_out.add( a_in, a_out);
        cache.put(this, "a_in", a_in.clone());
        return a_out.reshape(a_in.shape(0), MiscMath.multiplyItems(shape));
    }

    @Override
    public PolyData backward(PolyData da_out, PolyData da_in, Cache cache) {
        PolyData a_in = cache.get(this, "a_in");
        da_in.reshape(da_out).fill(0);
        da_in.add( da_out, da_in);
        return da_in.reshape(a_in.shape());
    }
}
