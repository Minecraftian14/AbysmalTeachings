package in.mcxiv.abyss.mathematics.backend;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.data.representation.SlicedPolyData;
import in.mcxiv.abyss.data.representation.TransposeImagePolyData;
import in.mcxiv.abyss.interfaces.FloatOperation;
import in.mcxiv.abyss.interfaces.FloatSupplier;
import in.mcxiv.abyss.interfaces.FloatToFloatFunction;
import in.mcxiv.abyss.mathematics.MiscMath;
import in.mcxiv.abyss.utilities.AddressIterator;
import in.mcxiv.abyss.utilities.Misc;
import in.mcxiv.abyss.utilities.Pool;
import in.mcxiv.abyss.utilities.Pools;

import java.util.Arrays;

import static in.mcxiv.abyss.data.representation.PolyData.isShapeSame;
import static in.mcxiv.abyss.data.representation.SlicedPolyData.ALL;

public interface Backend {

    Backend GENERIC_SUPPORT_BACKEND = new Backend() {
    };

    default Pool<? extends PolyData> pool() {
        return Pools.ARRAY_POOL;
    }

    default PolyData fill(PolyData first, float value) {
        AddressIterator iterator = new AddressIterator(first.shape());
        while (iterator.hasNext()) first.set(value, iterator.next());
        return first;
    }

    default PolyData fill(PolyData first, FloatSupplier supplier) {
        AddressIterator iterator = new AddressIterator(first.shape());
        while (iterator.hasNext()) first.set(supplier.getAsFloat(), iterator.next());
        return first;
    }

    default float[] export(PolyData first) {
        int[] shape = first.shape();
        float[] data = new float[MiscMath.multiplyItems(shape)];
        int i = 0;
        AddressIterator iterator = new AddressIterator(shape);
        while (iterator.hasNext()) data[i++] = first.get(iterator.next());
        return data;
    }

    default String stringify(PolyData first) {
        StringBuilder sb = new StringBuilder("{{");
        for (int i = 0; i < first.dims(); i++)
            sb.append(first.shape(i)).append(',');
        sb.append("},{");
        AddressIterator iterator = new AddressIterator(first.shape());
        while (iterator.hasNext()) sb.append(first.get(iterator.next())).append(',');
        return sb.append("}}").toString();
    }

    default PolyData transpose(PolyData first) {
        return new TransposeImagePolyData(first);
    }

    default SlicedPolyData slice(PolyData first, int... slice) {
        return new SlicedPolyData(first, slice);
    }

    default boolean areEqual(PolyData first, PolyData second) {
        if (first == second) return true;
        if (!isShapeSame(first, second)) throw new IllegalStateException();
        var iterator = new AddressIterator(first.shape());
        while (iterator.hasNext()) {
            int[] next = iterator.next();
            if (!MiscMath.equals(first.get(next), second.get(next)))
                return false;
        }
        return true;
    }

    default PolyData clone(PolyData first) {
        return (PolyData) Misc.clone(first);
    }


    default PolyData unaryOperation(PolyData first, PolyData result, FloatToFloatFunction function) {
        int[] shape = first.shape();
        result.reshape(shape);
        AddressIterator iterator = new AddressIterator(shape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            result.set(function.applyAsFloat(first.get(address)), address);
        }
        return result;
    }

    default PolyData scalarOperation(PolyData first, PolyData second, PolyData result, FloatOperation operation) {
        if (isShapeSame(first, second)) {
            int[] shape = MiscMath.functionalArrayToArray(first.dims(), first::shape);
            result.reshape(shape);
            AddressIterator iterator = new AddressIterator(shape);
            while (iterator.hasNext()) {
                int[] address = iterator.next();
                result.set(operation.operate(first.get(address), second.get(address)), address);
            }
        } else {
            int[] firstShape = first.shape();
            result.reshape(firstShape);
            AddressIterator iterator = new AddressIterator(firstShape);
            int[] broadcastingMask = MiscMath.broadcastingMask(second.shape());
            int[] secondAddress = broadcastingMask.clone();
            while (iterator.hasNext()) {
                int[] firstAddress = iterator.next();
                MiscMath.mul(firstAddress, broadcastingMask, secondAddress);
                result.set(operation.operate(
                        first.get(firstAddress),
                        second.get(secondAddress)
                ), firstAddress);
            }
        }
        return result;
    }

    default PolyData vectorOperation(PolyData first, PolyData second, PolyData result,
                                     FloatOperation cast, FloatOperation reduce,
                                     float identity) {
        int[] firstShape = first.shape();
        int[] secondShape = second.shape();
        int[] resultShape = MiscMath.crossDimensions(firstShape, secondShape);
        result.reshape(resultShape);

        int commonDim = secondShape[0];
        // Reusing firstShape and secondShape as firstAddress and secondAddress.
        AddressIterator iterator = new AddressIterator(resultShape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            float value = identity;
            for (int i = 0; i < commonDim; i++) {
                System.arraycopy(address, 0, firstShape, 0, firstShape.length - 1);
                firstShape[firstShape.length - 1] = i;
                System.arraycopy(address, firstShape.length - 1, secondShape, 1, secondShape.length - 1);
                secondShape[0] = i;
                value = reduce.operate(value, cast.operate(first.get(firstShape), second.get(secondShape)));
            }
            result.set(value, address);
        }
        return result;
    }

    default PolyData convolveOperationNew(PolyData first, PolyData second, PolyData result, int channels) {
        // TODO
        //  Say, the args are like,
        //      first    [=] [1000, 1000, 100, 100, 100, 3]
        //      second   [=] [5, 5, 128, 64]
        //      channels  =  2
        //  Then, we assume,
        //      The last 'channels' values in second are channels
        //      The remains is the filter
        //      The last 'len(filter)+channels' values of first after skipping 'channels' values are the ones where we apply convolution
        //  Therefore,
        //      #samples  =  [1000, 1000] = 1000000
        //      sample   [=] [100,100,100,3]
        //      filter   [=] [5,5]
        //      channels  =  [128, 64] = 8192
        //      result   [=] [1000, 1000, 96, 96, 128, 64]
        throw new UnsupportedOperationException();
    }

    default PolyData convolveOperation(PolyData first, PolyData second, PolyData result) {
        int[] firstShape = first.shape();
        int[] secondShape = second.shape();
        if (first.dims() != second.dims()) throw new UnsupportedOperationException();
        int[] resultShape = MiscMath.convolveDimensions(firstShape, secondShape);
        result.reshape(resultShape);

        var sliceFrom = new int[secondShape.length];
        var sliceTo = Arrays.copyOf(secondShape, secondShape.length);
        var slice = new SlicedPolyData(first, sliceFrom, sliceTo);
        var buffer = pool().issue(second);

        AddressIterator resultIterator = new AddressIterator(resultShape);
        while (resultIterator.hasNext()) {
            int[] resultAddress = resultIterator.next();

            MiscMath.sum(resultAddress, secondShape, sliceTo);
            sliceFrom = resultAddress;

            slice.initialize(first, sliceFrom, sliceTo);

            result.set(slice.mul(second, buffer).sumAll(), resultAddress);
        }

        pool().free(buffer);

        return result;
    }

    default PolyData imageConvolveForward(PolyData first, PolyData second, PolyData result) {
        assert first.dims() == 4 : "We only expect a batch of 2D images with color channels as first argument. Received %s.".formatted(Arrays.toString(first.shape()));
        assert second.dims() == 4 : "Only 2D convolutions with channel contraction and filters implemented. Received %s.".formatted(Arrays.toString(second.shape()));

        int samples = first.shape(0);
        int width = first.shape(1);
        int height = first.shape(2);
        int channels = first.shape(3);
        int f_width = second.shape(0);
        int f_height = second.shape(1);
        int contract = second.shape(2);
        int filters = second.shape(3);
        int r_width = width - f_width + 1;
        int r_height = height - f_height + 1;

        assert width >= f_width : "Insufficient width %d for convolution with %d.".formatted(width, f_width);
        assert height >= f_height : "Insufficient height %d for convolution with %d.".formatted(height, f_height);
        assert channels == contract : "Incompatible channels contraction for %d channels and %d contraction.".formatted(channels, contract);

        result.reshape(samples, r_width, r_height, 1, filters).fill(0);

        var sampleSlice = first.slice(0, ALL, ALL, ALL);
        var filterSlice = second.slice(ALL, ALL, ALL, 0);
        var resultSlice = result.slice(0, ALL, ALL, 0);

        for (int sampleIdx = 0; sampleIdx < samples; sampleIdx++) {
            for (int filterIdx = 0; filterIdx < filters; filterIdx++) {

                // width, height, channels
                first.slice(sampleSlice, sampleIdx, ALL, ALL, ALL);
                // f_width, f_height, contract
                second.slice(filterSlice, ALL, ALL, ALL, filterIdx);
                // red_width, red_height, 1
                result.slice(resultSlice, sampleIdx, ALL, ALL, ALL, filterIdx);

                convolveOperation(sampleSlice, filterSlice, resultSlice);
            }
        }
        return result.reshape(samples, r_width, r_height, filters);
    }

    default PolyData imageConvolveBackward(PolyData first, PolyData second, PolyData result) {
        assert first.dims() == 4 : "We only expect a batch of 2D images with color channels as first argument. Received %s.".formatted(Arrays.toString(first.shape()));
        assert second.dims() == 4 : "We only expect a batch of 2D images with filters as second argument. Received %s.".formatted(Arrays.toString(second.shape()));

        int samples = first.shape(0);
        int width = first.shape(1);
        int height = first.shape(2);
        int channels = first.shape(3);
        int r_width = second.shape(1);
        int r_height = second.shape(2);
        int filters = second.shape(3);
        int f_width = width - r_width + 1;
        int f_height = height - r_height + 1;

//        assert width >= r_width;
//        assert height >= r_height;

        result.reshape(1, f_width, f_height, channels, filters).fill(0);

        var sampleSlice = first.slice(ALL, ALL, ALL, 0);
        var derivativeSlice = second.slice(ALL, ALL, ALL, 0);
        var filerSlice = result.slice(ALL, ALL, ALL, 0, 0);

        for (int channelIdx = 0; channelIdx < channels; channelIdx++) {
            for (int filterIdx = 0; filterIdx < filters; filterIdx++) {
                first.slice(sampleSlice, ALL, ALL, ALL, channelIdx);
                second.slice(derivativeSlice, ALL, ALL, ALL, filterIdx);
                result.slice(filerSlice, ALL, ALL, ALL, channelIdx, filterIdx);

                convolveOperation(sampleSlice, derivativeSlice, filerSlice);
            }
        }

        return result.reshape(f_width, f_height, channels, filters);
    }

    default float accumulatingOperation(PolyData first, FloatOperation accumulator) {
        int[] shape = first.shape();
        AddressIterator iterator = new AddressIterator(shape);
        float result = iterator.hasNext() ? first.get(iterator.next()) : 0;
        while (iterator.hasNext())
            result = accumulator.operate(result, first.get(iterator.next()));
        return result;
    }

    default PolyData reduceOperation(PolyData first, PolyData result, int dim, boolean keepDims, FloatOperation operation) {
        int[] resultShape = first.shape();
        resultShape[dim] = 1;
        result.reshape(resultShape);
        int dimLength = first.shape(dim);
        var iterator = new AddressIterator(resultShape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            float root = first.get(address);
            for (int i = 1; i < dimLength; i++) {
                address[dim] = i;
                root = operation.operate(root, first.get(address));
            }
            address[dim] = 0;
            result.set(root, address);
        }
        if (keepDims) return result;
        int[] newShape = new int[resultShape.length - 1];
        for (int i = 0, j = 0; i < resultShape.length; i++)
            if (i != dim) newShape[j++] = resultShape[i];
        return result.reshape(newShape);
    }

    default PolyData reduceAllOperation(PolyData first, PolyData result, int dim, FloatOperation operation) {
        int[] resultShape = new int[]{first.shape(dim)};
        result.reshape(resultShape);
        int[] slice = new int[first.dims()];
        Arrays.fill(slice, ALL);
        slice[dim] = 0;
        var firstSlice = first.slice(slice);
        result.set(firstSlice.accumulatingOperation(operation), 0);
        for (int i = 0, s = first.shape(dim); i < s; i++) {
            slice[dim] = i;
            result.set(first.slice(firstSlice, slice).accumulatingOperation(operation), i);
        }
        return result;
    }

    default PolyData indexOperation(PolyData first, PolyData result, int dim, FloatOperation operation) {
        int[] resultShape = new int[first.dims() - 1];
        for (int i = 0, j = 0, s = first.dims(); i < s; i++)
            if (i != dim)
                resultShape[j++] = first.shape(i);
        result.reshape(resultShape);
        var iterator = new AddressIterator(resultShape);
        int[] firstAddress = first.shape();
        int chosenDimLength = first.shape(dim);
        while (iterator.hasNext()) {
            int[] secondAddress = iterator.next();
            System.arraycopy(secondAddress, 0, firstAddress, 0, dim);
            System.arraycopy(secondAddress, dim, firstAddress, dim + 1, secondAddress.length - dim);
            firstAddress[dim] = 0;
            float root = first.get(firstAddress);
            int rootIndex = 0;
            for (int i = 1; i < chosenDimLength; i++) {
                firstAddress[dim] = i;
                float compare = operation.operate(root, first.get(firstAddress));
                if (root != compare) {
                    root = compare;
                    rootIndex = i;
                }
            }
            result.set(rootIndex, secondAddress);
        }
        return result;
    }

}
