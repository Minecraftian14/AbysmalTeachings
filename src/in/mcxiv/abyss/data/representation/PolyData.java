package in.mcxiv.abyss.data.representation;

import in.mcxiv.abyss.data.representation.SlicedPolyData.Slice;
import in.mcxiv.abyss.interfaces.CopyCloneable;
import in.mcxiv.abyss.interfaces.FloatOperation;
import in.mcxiv.abyss.interfaces.FloatSupplier;
import in.mcxiv.abyss.interfaces.FloatToFloatFunction;
import in.mcxiv.abyss.mathematics.MiscMath;
import in.mcxiv.abyss.utilities.AddressIterator;

import java.io.Serializable;

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
        return MiscMath.functionalArrayToArray(dims(), this::shape);
    }

    PolyData reshape(int... shape);

    default PolyData reshape(PolyData dw) {
        return reshape(MiscMath.functionalArrayToArray(dw.dims(), dw::shape));
    }

    float get(int... address);

    void set(float value, int... address);

    default PolyData fill(float value) {
        return MiscMath.BACKEND.fill(this, value);
    }

    default PolyData fill(FloatSupplier supplier) {
        return MiscMath.BACKEND.fill(this, supplier);
    }

    default float[] export() {
        return MiscMath.BACKEND.export(this);
    }

    default String stringify() {
        return MiscMath.BACKEND.stringify(this);
    }

    default PolyData transpose() {
        return MiscMath.BACKEND.transpose(this);
    }

    default PolyData clone() {
        return MiscMath.BACKEND.clone(this);
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

    static boolean isShapeSame(PolyData first, PolyData second) {
        if (first.dims() != second.dims()) return false;
        for (int i = 0, s = first.dims(); i < s; i++)
            if (first.shape(i) != second.shape(i)) return false;
        return true;
    }

    default PolyData unaryOperation(FloatToFloatFunction function) {
        return unaryOperation(this, function);
    }

    default PolyData unaryOperation(PolyData result, FloatToFloatFunction function) {
        return MiscMath.BACKEND.unaryOperation(this, result, function);
    }

    default PolyData scalarOperation(PolyData second, FloatOperation operation) {
        return scalarOperation(second, this, operation);
    }

    default PolyData scalarOperation(PolyData second, PolyData result, FloatOperation operation) {
        return MiscMath.BACKEND.scalarOperation(this, second, result, operation);
    }

    default PolyData vectorOperation(PolyData second,
                                     FloatOperation cast, FloatOperation reduce,
                                     float identity) {
        return vectorOperation(second, MiscMath.BACKEND.pool().issue(MiscMath.crossDimensions(this.shape(), second.shape())), cast, reduce, identity);
    }

    default PolyData vectorOperation(PolyData second, PolyData result,
                                     FloatOperation cast, FloatOperation reduce,
                                     float identity) {
        return MiscMath.BACKEND.vectorOperation(this, second, result, cast, reduce, identity);
    }

    default PolyData convolveOperation(PolyData second) {
        return convolveOperation(second, MiscMath.BACKEND.pool().issue(MiscMath.convolveDimensions(this.shape(), second.shape())));
    }

    default PolyData convolveOperation(PolyData second, PolyData result) {
        return MiscMath.BACKEND.convolveOperation(this, second, result);
    }

    default PolyData imageConvolveForward(PolyData second) {
        return imageConvolveForward(second, MiscMath.BACKEND.pool().issue(MiscMath.convolveDimensions(this.shape(), second.shape())));
    }

    default PolyData imageConvolveForward(PolyData second, PolyData result) {
        return MiscMath.BACKEND.imageConvolveForward(this, second, result);
    }

    default PolyData imageConvolveBackward(PolyData second) {
        return imageConvolveBackward(second, MiscMath.BACKEND.pool().issue(MiscMath.convolveDimensions(this.shape(), second.shape())));
    }

    default PolyData imageConvolveBackward(PolyData second, PolyData result) {
        return MiscMath.BACKEND.imageConvolveBackward(this, second, result);
    }

    default float accumulatingOperation(FloatOperation accumulator) {
        return MiscMath.BACKEND.accumulatingOperation(this, accumulator);
    }

    default PolyData reduceOperation(int dim, boolean keepDims, FloatOperation operation) {
        return reduceOperation(MiscMath.BACKEND.pool().issue(MiscMath.multiplyItems(this.shape()) / shape(dim)), dim, keepDims, operation);
    }

    default PolyData reduceOperation(PolyData result, int dim, boolean keepDims, FloatOperation operation) {
        return MiscMath.BACKEND.reduceOperation(this, result, dim, keepDims, operation);
    }

    default PolyData reduceAllOperation(int dim, FloatOperation operation) {
        return reduceAllOperation(MiscMath.BACKEND.pool().issue(this.shape(dim)), dim, operation);
    }

    default PolyData reduceAllOperation(PolyData result, int dim, FloatOperation operation) {
        return MiscMath.BACKEND.reduceAllOperation(this, result, dim, operation);
    }

    default PolyData indexOperation(int dim, FloatOperation operation) {
        return indexOperation(MiscMath.BACKEND.pool().issue(MiscMath.multiplyItems(this.shape()) / shape(dim)), dim, operation);
    }

    default PolyData indexOperation(PolyData result, int dim, FloatOperation operation) {
        return MiscMath.BACKEND.indexOperation(this, result, dim, operation);
    }

    // INTERNALS

    default PolyData add(float second) {
        return unaryOperation(f -> f + second);
    }

    default PolyData add(float second, PolyData result) {
        return unaryOperation(result, f -> f + second);
    }

    default PolyData add(PolyData second) {
        return scalarOperation(second, Float::sum);
    }

    default PolyData add(PolyData second, PolyData result) {
        return scalarOperation(second, result, Float::sum);
    }

    default PolyData sub(float second) {
        return unaryOperation(f -> f - second);
    }

    default PolyData sub(float second, PolyData result) {
        return unaryOperation(result, a -> a - second);
    }

    default PolyData sub(PolyData second) {
        return scalarOperation(second, MiscMath::sub);
    }

    default PolyData sub(PolyData second, PolyData result) {
        return scalarOperation(second, result, MiscMath::sub);
    }

    default PolyData mul(float second) {
        return unaryOperation(f -> f * second);
    }

    default PolyData mul(float second, PolyData result) {
        return unaryOperation(result, a -> a * second);
    }

    default PolyData mul(PolyData second) {
        return scalarOperation(second, MiscMath::mul);
    }

    default PolyData mul(PolyData second, PolyData result) {
        return scalarOperation(second, result, MiscMath::mul);
    }

    default PolyData div(float second) {
        return unaryOperation(f -> f / second);
    }

    default PolyData div(float second, PolyData result) {
        return unaryOperation(result, a -> a / second);
    }

    default PolyData div(PolyData second) {
        return scalarOperation(second, MiscMath::div);
    }

    default PolyData div(PolyData second, PolyData result) {
        return scalarOperation(second, result, MiscMath::div);
    }

    default PolyData sqr() {
        return unaryOperation(MiscMath::square);
    }

    default PolyData sqr(PolyData result) {
        return unaryOperation(result, MiscMath::square);
    }

    default PolyData cross(PolyData second) {
        return vectorOperation(second, MiscMath::mul, MiscMath::sum, 0);
    }

    default PolyData cross(PolyData second, PolyData result) {
        return vectorOperation(second, result, MiscMath::mul, MiscMath::sum, 0);
    }

    default PolyData minmax(PolyData second) {
        return vectorOperation(second, Math::min, Math::max, -Float.MAX_VALUE);
    }

    default PolyData minmax(PolyData second, PolyData result) {
        return vectorOperation(second, result, Math::min, Math::max, -Float.MAX_VALUE);
    }

    default PolyData maxmin(PolyData second) {
        return vectorOperation(second, Math::max, Math::min, Float.MAX_VALUE);
    }

    default PolyData maxmin(PolyData second, PolyData result) {
        return vectorOperation(second, result, Math::max, Math::min, Float.MAX_VALUE);
    }

    default float sumAll() {
        return accumulatingOperation(Float::sum);
    }

    default PolyData argMax(int dim) {
        return indexOperation(dim, Float::max);
    }

    default PolyData argMax(PolyData result, int dim) {
        return indexOperation(result, dim, Math::max);
    }

    default PolyData sumAlong(int dim, PolyData result) {
        return reduceOperation(result, dim, true, Float::sum);
    }

    default PolyData reduceSum(int dim, PolyData result) {
        return reduceAllOperation(result, dim, Float::sum);
    }

    default SlicedPolyData slice(int... slice) {
        return MiscMath.BACKEND.slice(this, slice);
    }

    default SlicedPolyData slice(Slice slice) {
        return slice.slice(this);
    }

    default SlicedPolyData slice(SlicedPolyData result, int... slice) {
        return result.initialize(this, slice, slice);
    }

    default SlicedPolyData slice(SlicedPolyData result, Slice slice) {
        return slice.slice(this, result);
    }

    default boolean areEqual(PolyData second) {
        return MiscMath.BACKEND.areEqual(this, second);
    }
}