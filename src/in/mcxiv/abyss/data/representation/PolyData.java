package in.mcxiv.abyss.data.representation;

import in.mcxiv.abyss.interfaces.*;
import in.mcxiv.abyss.mathematics.AddressIterator;
import in.mcxiv.abyss.mathematics.MoreMath;
import in.mcxiv.abyss.utilities.Pools;

import java.io.Serializable;
import java.util.Arrays;

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
        return (PolyData) MoreMath.clone(this);
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

    /// QOL Methods

    default PolyData add(PolyData second) {
        return add(this, second, this);
    }

    /// GENERAL Methods

    static boolean isShapeSame(PolyData first, PolyData second) {
        if (first.dims() != second.dims()) return false;
        for (int i = 0, s = first.dims(); i < s; i++)
            if (first.shape(i) != second.shape(i)) return false;
        return true;
    }

    static PolyData unaryOperation(PolyData first, PolyData result, FloatToFloatFunction function) {
        int[] shape = first.shape();
        result.reshape(shape);
        AddressIterator iterator = new AddressIterator(shape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            result.set(function.applyAsFloat(first.get(address)), address);
        }
        return result;
    }

    static PolyData scalarOperation(PolyData first, PolyData second, PolyData result, FloatOperation operation) {
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

    static PolyData vectorOperation(PolyData first, PolyData second, PolyData result,
                                    FloatOperation cast, FloatOperation reduce,
                                    float identity) {
        int[] firstShape = MoreMath.functionalArrayToArray(first.dims(), first::shape);
        int[] secondShape = MoreMath.functionalArrayToArray(second.dims(), second::shape);
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

    static float accumulatingOperation(PolyData first, FloatToFloatBiFunction accumulator) {
        int[] shape = first.shape();
        AddressIterator iterator = new AddressIterator(shape);
        float result = iterator.hasNext() ? first.get(iterator.next()) : 0;
        while (iterator.hasNext())
            result = accumulator.apply(result, first.get(iterator.next()));
        return result;
    }

    static PolyData add(PolyData first, float second) {
        return add(first, second, Pools.ARRAY_POOL.issue(first));
    }

    static PolyData add(PolyData first, float second, PolyData result) {
        return unaryOperation(first, result, f -> f + second);
    }

    static PolyData add(PolyData first, PolyData second) {
        return add(first, second, Pools.ARRAY_POOL.issue(first));
    }

    static PolyData add(PolyData first, PolyData second, PolyData result) {
        return scalarOperation(first, second, result, Float::sum);
    }

    static PolyData sub(PolyData first, float second) {
        return sub(first, Pools.ARRAY_POOL.issue(first));
    }

    static PolyData sub(PolyData first, float second, PolyData result) {
        return unaryOperation(first, result, a -> a - second);
    }

    static PolyData sub(PolyData first, PolyData second) {
        return sub(first, second, Pools.ARRAY_POOL.issue(first));
    }

    static PolyData sub(PolyData first, PolyData second, PolyData result) {
        return scalarOperation(first, second, result, MoreMath::sub);
    }

    static PolyData mul(PolyData first, float second) {
        return mul(first, Pools.ARRAY_POOL.issue(first));
    }

    static PolyData mul(PolyData first, float second, PolyData result) {
        return unaryOperation(first, result, a -> a * second);
    }

    static PolyData mul(PolyData first, PolyData second) {
        return mul(first, second, Pools.ARRAY_POOL.issue(first));
    }

    static PolyData mul(PolyData first, PolyData second, PolyData result) {
        return scalarOperation(first, second, result, MoreMath::mul);
    }

    static PolyData div(PolyData first, float second) {
        return div(first, Pools.ARRAY_POOL.issue(first));
    }

    static PolyData div(PolyData first, float second, PolyData result) {
        return unaryOperation(first, result, a -> a / second);
    }

    static PolyData div(PolyData first, PolyData second) {
        return div(first, second, Pools.ARRAY_POOL.issue(first));
    }

    static PolyData div(PolyData first, PolyData second, PolyData result) {
        return scalarOperation(first, second, result, MoreMath::div);
    }

    static PolyData sqr(PolyData first) {
        return sqr(first, Pools.ARRAY_POOL.issue(first));
    }

    static PolyData sqr(PolyData first, PolyData result) {
        return unaryOperation(first, result, MoreMath::square);
    }

    static PolyData cross(PolyData first, PolyData second, PolyData result) {
        return vectorOperation(first, second, result, MoreMath::mul, MoreMath::sum, 0);
    }

    static PolyData minmax(PolyData first, PolyData second, PolyData result) {
        return vectorOperation(first, second, result, Math::min, Math::max, -Float.MAX_VALUE);
    }

    static PolyData maxmin(PolyData first, PolyData second, PolyData result) {
        return vectorOperation(first, second, result, Math::max, Math::min, Float.MAX_VALUE);
    }

    static float sumAll(PolyData first) {
        return accumulatingOperation(first, Float::sum);
    }

    static PolyData sumAlong(PolyData first, int dim, PolyData result) {
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

    static PolyData reduceSum(PolyData first, int dim, PolyData result) {
        int[] slice = new int[first.dims()];
        Arrays.fill(slice, SlicedPolyData.ALL);
        result.reshape(first.shape(dim));
        var mini = new SlicedPolyData(first, 1);
        for (int i = 0, s = first.shape(dim); i < s; i++) {
            slice[dim] = i;
            PolyData.slice(first, mini, slice);
            result.set(PolyData.sumAll(mini), i);
        }
        return result;
    }

    static SlicedPolyData slice(PolyData first, int... slice) {
        return new SlicedPolyData(first, slice);
    }

    static SlicedPolyData slice(PolyData first, SlicedPolyData.Slice slice) {
        return slice.slice(first);
    }

    static PolyData slice(PolyData first, SlicedPolyData result, int... slice) {
        return result.initialize(first, slice, slice);
    }

    static boolean areEqual(PolyData first, PolyData second) {
        if (!isShapeSame(first, second)) throw new IllegalStateException();
        var iterator = new AddressIterator(first.shape());
        while (iterator.hasNext()) {
            int[] next = iterator.next();
            if (!MoreMath.equals(first.get(next), second.get(next)))
                return false;
        }
        return true;
    }
}