package in.mcxiv.abyss.data;

import in.mcxiv.abyss.iface.*;
import in.mcxiv.abyss.math.AddressIterator;
import in.mcxiv.abyss.math.MoreMath;

import java.io.*;
import java.util.Arrays;

import static in.mcxiv.abyss.data.SlicedPolyData.ALL;

public interface PolyData extends Serializable, Cloneable {

    int dims();

    int shape(int dim);

    default int[] shape() {
        return MoreMath.functionalArrayToArray(dims(), this::shape);
    }

    PolyData reshape(int... shape);

    float get(int... address);

    void set(float value, int... address);

    default void fill(float value) {
        AddressIterator iterator = new AddressIterator(MoreMath.functionalArrayToArray(this.dims(), this::shape));
        while (iterator.hasNext()) set(value, iterator.next());
    }

    default void fill(FloatSupplier supplier) {
        AddressIterator iterator = new AddressIterator(MoreMath.functionalArrayToArray(this.dims(), this::shape));
        while (iterator.hasNext()) set(supplier.getAsFloat(), iterator.next());
    }

    default void fill(IntToFloatBiFunction function) {
        if (dims() != 2) throw new UnsupportedOperationException("This function only works for 2D matrices.");
        for (int i = 0, is = shape(0); i < is; i++)
            for (int j = 0, js = shape(1); j < js; j++)
                set(function.get(i, j), i, j);
    }

    default float[] export() {
        int[] shape = MoreMath.functionalArrayToArray(this.dims(), this::shape);
        float[] data = new float[MoreMath.multiplyElements(shape)];
        int i = 0;
        AddressIterator iterator = new AddressIterator(shape);
        while (iterator.hasNext()) data[i++] = get(iterator.next());
        return data;
    }

    default PolyData add(PolyData second) {
        return add(this, second, this);
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

    default PolyData reshape(PolyData dw) {
        return reshape(MoreMath.functionalArrayToArray(dw.dims(), dw::shape));
    }

    default PolyData reshape(int length, IntToIntFunction array) {
        // TODO: Optimize
        return reshape(MoreMath.functionalArrayToArray(length, array));
    }

    default PolyData clone() {
        try {
            var bos = new ByteArrayOutputStream();
            var oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.close();

            var bis = new ByteArrayInputStream(bos.toByteArray());
            var ois = new ObjectInputStream(bis);
            PolyData clone = (PolyData) ois.readObject();
            ois.close();

            return clone;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean isShapeSame(PolyData first, PolyData second) {
        if (first.dims() != second.dims()) return false;
        for (int i = 0, s = first.dims(); i < s; i++)
            if (first.shape(i) != second.shape(i)) return false;
        return true;
    }

    static PolyData scalarOperation(PolyData first, PolyData second, PolyData result, FloatOperation operation) {
        if (first.dims() == second.dims() &&
                MoreMath.functionalArrayEquals(first.dims(), first::shape, second::shape)) {
            int[] shape = MoreMath.functionalArrayToArray(first.dims(), first::shape);
            result.reshape(shape);
            AddressIterator iterator = new AddressIterator(shape);
            while (iterator.hasNext()) {
                int[] address = iterator.next();
                result.set(operation.operate(first.get(address), second.get(address)), address);
            }
        } else {
            throw new UnsupportedOperationException("Impl broadcasting");
        }
        return result;
    }

    static PolyData unaryOperation(PolyData first, PolyData result, FloatToFloatFunction function) {
        int[] shape = MoreMath.functionalArrayToArray(first.dims(), first::shape);
        result.reshape(shape);
        AddressIterator iterator = new AddressIterator(shape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            result.set(function.applyAsFloat(first.get(address)), address);
        }
        return result;
    }

    static PolyData add(PolyData first, PolyData second, PolyData result) {
        scalarOperation(first, second, result, Float::sum);
        return result;
    }

    static PolyData subtract(PolyData first, PolyData second, PolyData result) {
        return scalarOperation(first, second, result, (a, b) -> a - b);
    }

    static PolyData dot(PolyData first, PolyData second, PolyData result) {
        return scalarOperation(first, second, result, (a, b) -> a * b);
    }

    static PolyData dot(PolyData first, float second, PolyData result) {
        return unaryOperation(first, result, f -> f * second);
    }

    static PolyData square(PolyData first, PolyData result) {
        return unaryOperation(first, result, f -> f * f);
    }

    static PolyData cross(PolyData first, PolyData second, PolyData result) {
        int[] firstShape = MoreMath.functionalArrayToArray(first.dims(), first::shape);
        int[] secondShape = MoreMath.functionalArrayToArray(first.dims(), second::shape);
        int[] resultShape = MoreMath.multiplyDimensions(firstShape, secondShape);
        result.reshape(resultShape);

        int commonDim = secondShape[0];
        // Reusing firstShape and secondShape as firstAddress and secondAddress.
        AddressIterator iterator = new AddressIterator(resultShape);
        while (iterator.hasNext()) {
            int[] address = iterator.next();
            float value = 0;
            for (int i = 0; i < commonDim; i++) {
                System.arraycopy(address, 0, firstShape, 0, firstShape.length - 1);
                firstShape[firstShape.length - 1] = i;
                System.arraycopy(address, firstShape.length - 1, secondShape, 1, secondShape.length - 1);
                secondShape[0] = i;
                value += first.get(firstShape) * second.get(secondShape);
            }
            result.set(value, address);
        }
        return result;
    }

    static PolyData sum(PolyData first, int dim, PolyData result) {
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

    static float accumulate(PolyData first, FloatToFloatBiFunction accumulator) {
        int[] shape = first.shape();
        AddressIterator iterator = new AddressIterator(shape);
        float result = iterator.hasNext() ? first.get(iterator.next()) : 0;
        while (iterator.hasNext())
            result = accumulator.apply(result, first.get(iterator.next()));
        return result;
    }

    static float sum(PolyData first) {
        return accumulate(first, Float::sum);
    }

    static PolyData sumAll(PolyData first, int dim, PolyData result) {
        int[] slice = new int[first.dims()];
        Arrays.fill(slice, ALL);
        result.reshape(first.shape(dim));
        var mini = new SlicedPolyData(first, 1);
        for (int i = 0, s = first.shape(dim); i < s; i++) {
            slice[dim] = i;
            PolyData.slice(first, mini, slice);
            result.set(PolyData.sum(mini), i);
        }
        return result;
    }

    static PolyData slice(PolyData first, int... slice) {
        return new SlicedPolyData(first, slice);
    }

    static PolyData slice(PolyData first, SlicedPolyData result, int... slice) {
        return result.initialize(first, slice, slice);
    }
}
