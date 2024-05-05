package in.mcxiv.abyss.data.representation;

import in.mcxiv.abyss.data.representation.SlicedPolyData.Slice;
import in.mcxiv.abyss.interfaces.*;
import in.mcxiv.abyss.mathematics.AddressIterator;
import in.mcxiv.abyss.mathematics.MoreMath;
import in.mcxiv.abyss.utilities.Misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import static in.mcxiv.abyss.data.representation.SlicedPolyData.ALL;
import static in.mcxiv.abyss.utilities.Pools.ARRAY_POOL;

//default ALL;

public interface PolyData extends Serializable, Cloneable, CopyCloneable<PolyData> {

    /// BASIC Methods

    /**
     * Number of dimensions
     */
    int dims();

    /**
     * Size along (dim+1)th dimension
     */
    int shape(int dim);

    default int[] shape() {
        return MoreMath.functionalArrayToArray(dims(), this::shape);
    }

    PolyData reshape(int... shape);

    default PolyData reshape(PolyData dw) {
        return reshape(MoreMath.functionalArrayToArray(dw.dims(), dw::shape));
    }

    default PolyData reshape(int length, IntToIntFunction array) {
        // TODO: Optimize
        return reshape(MoreMath.functionalArrayToArray(length, array));
    }

    float get(int... address);

    void set(float value, int... address);

    default PolyData fill(float value) {
        AddressIterator iterator = new AddressIterator(MoreMath.functionalArrayToArray(this.dims(), this::shape));
        while (iterator.hasNext()) set(value, iterator.next());
        return this;
    }

    default PolyData fill(FloatSupplier supplier) {
        AddressIterator iterator = new AddressIterator(MoreMath.functionalArrayToArray(this.dims(), this::shape));
        while (iterator.hasNext()) set(supplier.getAsFloat(), iterator.next());
        return this;
    }

    default PolyData fill(IntToFloatBiFunction function) {
        if (dims() != 2) throw new UnsupportedOperationException("This function only works for 2D matrices.");
        for (int i = 0, is = shape(0); i < is; i++)
            for (int j = 0, js = shape(1); j < js; j++)
                set(function.get(i, j), i, j);
        return this;
    }

    default float[] export() {
        int[] shape = MoreMath.functionalArrayToArray(this.dims(), this::shape);
        float[] data = new float[MoreMath.multiplyItems(shape)];
        int i = 0;
        AddressIterator iterator = new AddressIterator(shape);
        while (iterator.hasNext()) data[i++] = get(iterator.next());
        return data;
    }

    default ArrayList<Float> exportList() {
        int[] shape = MoreMath.functionalArrayToArray(this.dims(), this::shape);
        var list = new ArrayList<Float>(MoreMath.multiplyItems(shape));
        AddressIterator iterator = new AddressIterator(shape);
        while (iterator.hasNext()) list.add(get(iterator.next()));
        return list;
    }

    default String stringify() {
        StringBuilder sb = new StringBuilder("{{");
        for (int i = 0; i < dims(); i++)
            sb.append(shape(i)).append(',');
        sb.append("},{");
        AddressIterator iterator = new AddressIterator(MoreMath.functionalArrayToArray(this.dims(), this::shape));
        while (iterator.hasNext()) sb.append(get(iterator.next())).append(',');
        return sb.append("}}").toString();
    }

    default PolyData transpose() {
        return new TransposeImagePolyData(this);
    }

    default PolyData clone() {
        return (PolyData) Misc.clone(this);
    }

    @Override
    default <R extends PolyData> void copyTo(R target) {
        int[] shape = this.shape();
        target.reshape(shape);
        var iterator = new AddressIterator(shape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            target.set(get(address), address);
        }
    }

    /// GENERAL Methods

    default boolean isShapeSame(PolyData second) {
        return isShapeSame(this, second);
    }

    default boolean isShapeSame(PolyData first, PolyData second) {
        if (first.dims() != second.dims()) return false;
        for (int i = 0, s = first.dims(); i < s; i++)
            if (first.shape(i) != second.shape(i)) return false;
        return true;
    }

    default PolyData unaryOperation(FloatToFloatFunction function) {
        return unaryOperation(this, this, function);
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

    default PolyData scalarOperation(PolyData second, FloatOperation operation) {
        return scalarOperation(this, second, this, operation);
    }

    default PolyData scalarOperation(PolyData first, PolyData second, PolyData result, FloatOperation operation) {
        if (isShapeSame(first, second)) {
            int[] shape = MoreMath.functionalArrayToArray(first.dims(), first::shape);
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
            int[] broadcastingMask = MoreMath.broadcastingMask(second.shape());
            int[] secondAddress = broadcastingMask.clone();
            while (iterator.hasNext()) {
                int[] firstAddress = iterator.next();
                MoreMath.mul(firstAddress, broadcastingMask, secondAddress);
                result.set(operation.operate(
                        first.get(firstAddress),
                        second.get(secondAddress)
                ), firstAddress);
            }
        }
        return result;
    }


    default PolyData vectorOperation(PolyData second,
                                     FloatOperation cast, FloatOperation reduce,
                                     float identity) {
        return vectorOperation(this, second, ARRAY_POOL.issue(MoreMath.crossDimensions(this.shape(), second.shape())), cast, reduce, identity);
    }

    default PolyData vectorOperation(PolyData first, PolyData second, PolyData result,
                                     FloatOperation cast, FloatOperation reduce,
                                     float identity) {
        int[] firstShape = first.shape();
        int[] secondShape = second.shape();
        int[] resultShape = MoreMath.crossDimensions(firstShape, secondShape);
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

    default PolyData convolveOperation(PolyData second) {
        return convolveOperation(this, second, ARRAY_POOL.issue(MoreMath.convolveDimensions(this.shape(), second.shape())));
    }

    default PolyData convolveOperation(PolyData first, PolyData second, PolyData result) {
        int[] firstShape = first.shape();
        int[] secondShape = second.shape();
        if (first.dims() != second.dims()) throw new UnsupportedOperationException();
        int[] resultShape = MoreMath.convolveDimensions(firstShape, secondShape);
        result.reshape(resultShape);

        var sliceFrom = new int[secondShape.length];
        var sliceTo = Arrays.copyOf(secondShape, secondShape.length);
        var slice = new SlicedPolyData(first, sliceFrom, sliceTo);
        var buffer = ARRAY_POOL.issue(second);

        AddressIterator resultIterator = new AddressIterator(resultShape);
        while (resultIterator.hasNext()) {
            int[] resultAddress = resultIterator.next();

            MoreMath.sum(resultAddress, secondShape, sliceTo);
            sliceFrom = resultAddress;

            slice.initialize(first, sliceFrom, sliceTo);

            result.set(slice.mul(second, buffer).sumAll(), resultAddress);
        }

        ARRAY_POOL.free(buffer);

        return result;
    }

    default PolyData imageConvolveForward(PolyData second) {
        return imageConvolveForward(this, second, ARRAY_POOL.issue(MoreMath.convolveDimensions(this.shape(), second.shape())));
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

    default PolyData imageConvolveBackward(PolyData second) {
        return imageConvolveBackward(this, second, ARRAY_POOL.issue(MoreMath.convolveDimensions(this.shape(), second.shape())));
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

    default float accumulatingOperation(FloatToFloatBiFunction accumulator) {
        return accumulatingOperation(this, accumulator);
    }

    default float accumulatingOperation(PolyData first, FloatToFloatBiFunction accumulator) {
        int[] shape = first.shape();
        AddressIterator iterator = new AddressIterator(shape);
        float result = iterator.hasNext() ? first.get(iterator.next()) : 0;
        while (iterator.hasNext())
            result = accumulator.apply(result, first.get(iterator.next()));
        return result;
    }

    default PolyData reduceOperation(int dim, FloatOperation operation) {
        return reduceOperation(this, ARRAY_POOL.issue(MoreMath.multiplyItems(this.shape()) / shape(dim)), dim, operation);
    }

    default PolyData reduceOperation(PolyData first, PolyData result, int dim, FloatOperation operation) {
        int[] resultShape = new int[first.dims() - 1];
        for (int i = 0, j = 0, s = first.dims(); i < s; i++)
            if (i != dim)
                resultShape[j++] = first.shape(i);
        result.reshape(resultShape);
        var iterator = new AddressIterator(resultShape);
        int[] firstAddress = first.shape();
        int chosenDimLength = first.shape(resultShape.length);
        while (iterator.hasNext()) {
            int[] secondAddress = iterator.next();
            System.arraycopy(secondAddress, 0, firstAddress, 0, dim);
            System.arraycopy(secondAddress, dim, firstAddress, dim + 1, secondAddress.length - dim);
            firstAddress[dim] = 0;
            float root = first.get(firstAddress);
            for (int i = 1; i < chosenDimLength; i++) {
                firstAddress[dim] = i;
                root = operation.operate(root, first.get(firstAddress));
            }
            result.set(root, secondAddress);
        }
        return result;
    }

    default PolyData indexOperation(int dim, FloatOperation operation) {
        return indexOperation(this, ARRAY_POOL.issue(MoreMath.multiplyItems(this.shape()) / shape(dim)), dim, operation);
    }

    default PolyData indexOperation(PolyData first, PolyData result, int dim, FloatOperation operation) {
        int[] resultShape = new int[first.dims() - 1];
        for (int i = 0, j = 0, s = first.dims(); i < s; i++)
            if (i != dim)
                resultShape[j++] = first.shape(i);
        result.reshape(resultShape);
        var iterator = new AddressIterator(resultShape);
        int[] firstAddress = first.shape();
        int chosenDimLength = first.shape(resultShape.length);
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

    default PolyData add(float second) {
        return unaryOperation(f -> f + second);
    }

    default PolyData add(float second, PolyData result) {
        return unaryOperation(this, result, f -> f + second);
    }

    default PolyData add(PolyData second) {
        return scalarOperation(second, Float::sum);
    }

    default PolyData add(PolyData second, PolyData result) {
        return scalarOperation(this, second, result, Float::sum);
    }

    default PolyData sub(float second) {
        return unaryOperation(f -> f - second);
    }

    default PolyData sub(float second, PolyData result) {
        return unaryOperation(this, result, a -> a - second);
    }

    default PolyData sub(PolyData second) {
        return scalarOperation(second, MoreMath::sub);
    }

    default PolyData sub(PolyData second, PolyData result) {
        return scalarOperation(this, second, result, MoreMath::sub);
    }

    default PolyData mul(float second) {
        return unaryOperation(f -> f * second);
    }

    default PolyData mul(float second, PolyData result) {
        return unaryOperation(this, result, a -> a * second);
    }

    default PolyData mul(PolyData second) {
        return scalarOperation(second, MoreMath::mul);
    }

    default PolyData mul(PolyData second, PolyData result) {
        return scalarOperation(this, second, result, MoreMath::mul);
    }

    default PolyData div(float second) {
        return unaryOperation(f -> f / second);
    }

    default PolyData div(float second, PolyData result) {
        return unaryOperation(this, result, a -> a / second);
    }

    default PolyData div(PolyData second) {
        return scalarOperation(second, MoreMath::div);
    }

    default PolyData div(PolyData second, PolyData result) {
        return scalarOperation(this, second, result, MoreMath::div);
    }

    default PolyData sqr() {
        return unaryOperation(MoreMath::square);
    }

    default PolyData sqr(PolyData result) {
        return unaryOperation(this, result, MoreMath::square);
    }

    default PolyData cross(PolyData second) {
        return vectorOperation(second, MoreMath::mul, MoreMath::sum, 0);
    }

    default PolyData cross(PolyData second, PolyData result) {
        return vectorOperation(this, second, result, MoreMath::mul, MoreMath::sum, 0);
    }

    default PolyData minmax(PolyData second) {
        return vectorOperation(second, Math::min, Math::max, -Float.MAX_VALUE);
    }

    default PolyData minmax(PolyData second, PolyData result) {
        return vectorOperation(this, second, result, Math::min, Math::max, -Float.MAX_VALUE);
    }

    default PolyData maxmin(PolyData second) {
        return vectorOperation(second, Math::max, Math::min, Float.MAX_VALUE);
    }

    default PolyData maxmin(PolyData second, PolyData result) {
        return vectorOperation(this, second, result, Math::max, Math::min, Float.MAX_VALUE);
    }

    default float sumAll() {
        return accumulatingOperation(Float::sum);
    }

    default PolyData argMax(int dim) {
        return indexOperation(dim, Float::max);
    }

    default PolyData argMax(PolyData result, int dim) {
        return indexOperation(this, result, dim, Math::max);
    }

    default PolyData sumAlong(PolyData first, int dim, PolyData result) {
        int[] shape = MoreMath.functionalArrayToArray(first.dims(), first::shape);
        int size = shape[dim];
        shape[dim] = 1;
        result.reshape(shape);
        AddressIterator iterator = new AddressIterator(shape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            float sum = 0;
            for (int i = size - 1; i >= 0; i--) {
                address[dim] = i;
                sum += first.get(address);
            }
            result.set(sum, address);
        }
        return result;
    }

    default PolyData reduceSum(PolyData first, int dim, PolyData result) {
        int[] slice = new int[first.dims()];
        Arrays.fill(slice, ALL);
        result.reshape(first.shape(dim));
        var mini = new SlicedPolyData(first, 1);
        for (int i = 0, s = first.shape(dim); i < s; i++) {
            slice[dim] = i;
            first.slice(mini, slice);
            result.set(mini.sumAll(), i);
        }
        return result;
    }

    default SlicedPolyData slice(int... slice) {
        return new SlicedPolyData(this, slice);
    }

    default SlicedPolyData slice(Slice slice) {
        return slice.slice(this);
    }

    default SlicedPolyData slice(SlicedPolyData result, int... slice) {
        return result.initialize(this, slice, slice);
    }

    default boolean areEqual(PolyData second) {
        if (!isShapeSame(this, second)) throw new IllegalStateException();
        var iterator = new AddressIterator(this.shape());
        while (iterator.hasNext()) {
            int[] next = iterator.next();
            if (!MoreMath.equals(this.get(next), second.get(next)))
                return false;
        }
        return true;
    }
}