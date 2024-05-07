package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.utilities.AddressIterator;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.utilities.Cache;
import in.mcxiv.abyss.utilities.Pools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiLayerUnit extends MathematicalUnit {

    List<MathematicalUnit> layers = new ArrayList<>();

    public MultiLayerUnit(MathematicalUnit... layers) {
        add(layers);
    }

    public void add(MathematicalUnit... layers) {
        Collections.addAll(this.layers, layers);
    }

    @Override
    public PolyData forward(PolyData a_in, PolyData a_out, Cache cache) {
        PolyData b_in = Pools.ARRAY_POOL.issue(a_in);
        PolyData b_out = Pools.ARRAY_POOL.issue(a_in);
        PolyData temp;

        layers.get(0).forward(a_in, b_out, cache);
        for (int i = 1; i < layers.size(); i++) {
            temp = b_out;
            b_out = b_in;
            b_in = temp;
            layers.get(i).forward(b_in, b_out, cache);
        }

        int[] shape = b_out.shape();
        var iterator = new AddressIterator(shape);
        a_out.reshape(shape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            a_out.set(b_out.get(address), address);
        }

        Pools.ARRAY_POOL.free(b_in);
        Pools.ARRAY_POOL.free(b_out);

        return a_out;
    }

    @Override
    public PolyData backward(PolyData da_out, PolyData da_in, Cache cache) {
        PolyData b_out = Pools.ARRAY_POOL.issue(da_out);
        PolyData b_in = Pools.ARRAY_POOL.issue(da_in);
        PolyData temp;

        layers.get(layers.size() - 1).backward(da_out, b_in, cache);
        for (int i = layers.size() - 2; i >= 0; i--) {
            temp = b_in;
            b_in = b_out;
            b_out = temp;
            layers.get(i).backward(b_out, b_in, cache);
        }

        int[] shape = b_in.shape();
        var iterator = new AddressIterator(shape);
        da_in.reshape(shape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            da_in.set(b_in.get(address), address);
        }

        Pools.ARRAY_POOL.free(b_out);
        Pools.ARRAY_POOL.free(b_in);

        return da_in;

//        for (MathematicalUnit layer : layers.reversed())
//            da_out = layer.backward(da_out, da_in, cache);
//        return da_in;
    }

    @Override
    public int[] initialize(int[] inputDims) {
        super.initialize(inputDims);
        for (MathematicalUnit layer : layers)
            inputDims = layer.initialize(inputDims);
        return inputDims;
    }
}
