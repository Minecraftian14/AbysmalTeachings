package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.LatePolyData;
import in.mcxiv.abyss.data.representation.PadImagePolyData;
import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.data.representation.ReorderedDimsPolyData;
import in.mcxiv.abyss.initializers.Convolutive2DFCParameterInitializer;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.utilities.Cache;

import static in.mcxiv.abyss.data.representation.PolyData.*;
import static in.mcxiv.abyss.utilities.Pools.ARRAY_POOL;

public class ImageBatchConvolutionalUnit extends MathematicalUnit {

    public LatePolyData filter;
    public ReorderedDimsPolyData filter_reverse;
    private int filter_count;
    private int filter_size;

    public ImageBatchConvolutionalUnit(int filter_count, int filter_size) {
        this.filter = new LatePolyData(new Convolutive2DFCParameterInitializer(filter_count, filter_size));
        this.filter_reverse = new ReorderedDimsPolyData(this.filter, 1, 0, 3, 2);
        this.filter_count = filter_count;
        this.filter_size = filter_size;
        addInitiazables(filter);
    }

    @Override
    public int[] initialize(int[] inputDims) {
        assert inputDims.length == 4;
        super.initialize(inputDims);
        int[] output = inputDims.clone();
        output[1] = output[1] - filter_size + 1;
        output[2] = output[2] - filter_size + 1;
        output[3] = filter_count;
        return output;
    }

    @Override
    public PolyData forward(PolyData a_in, PolyData a_out, Cache cache) {
        a_in.imageConvolveForward(a_in, filter, a_out);
        cache.put(this, "a_in", ARRAY_POOL.clone(a_in));
        return a_out;
    }

    @Override
    public PolyData backward(PolyData da_out /*[m,rw,rh,k]*/, PolyData da_in, Cache cache) {
        var a_in /*[m,w,h,c]*/ = cache.remove(this, "a_in");

        var dweights /*[f,f,c,k]*/ = ARRAY_POOL.issue(filter);

        a_in.imageConvolveBackward(a_in, da_out, dweights);
//        convolveOperation(a_in, da_out, dweights);

        var pad_da_out /*[m,rw+2(f-1),rh+2(f-1),k]*/ = new PadImagePolyData(da_out, 0, 0, filter_size - 1, filter_size - 1, 0);
//        var flip_filter /*[f,f,k,c]*/ = new FlipImagePolyData(filter);
//        convolveOperation(pad_da_out, /*[f,f,k,c]*/ filter_reverse, da_in);
        pad_da_out.imageConvolveForward(pad_da_out, /*[f,f,k,c]*/ filter_reverse, da_in);

        cache.putParameter("weights", filter, dweights);

        ARRAY_POOL.free(a_in);

        return da_in;
    }
}