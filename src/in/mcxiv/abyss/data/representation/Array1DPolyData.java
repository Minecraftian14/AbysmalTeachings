package in.mcxiv.abyss.data.representation;

import in.mcxiv.abyss.mathematics.MoreMath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Array1DPolyData implements PolyData {

    int[] shape;
    float[] data;

    public Array1DPolyData() {
        this(1);
    }

    public Array1DPolyData(int... shape) {
        this.shape = shape;
        this.data = new float[MoreMath.multiplyItems(shape)];
    }

    public static Array1DPolyData n() {
        return new Array1DPolyData();
    }

    public static PolyData fromStream(Supplier<Float> supplier, int... shape) {
        return new Array1DPolyData(shape).fill(supplier::get);
    }

    public static PolyData fromList(List<Float> list) {
        return fromStream(list.iterator()::next, list.size(), 1);
    }

//    public static PolyData fromPath(Path path, int... shape) {
//        try (var stream = Files.lines(path)) {
//            return fromStream(stream.flatMap(s -> {
//                String[] split = s.split(",");
//                return IntStream.range(1, split.length).mapToObj(i -> Float.parseFloat(split[i]));
//            }).iterator()::next, shape);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public int dims() {
        return shape.length;
    }

    @Override
    public int shape(int dim) {
        return shape[dim];
    }

    @Override
    public PolyData reshape(int... shape) {
        int newDataLength = MoreMath.multiplyItems(shape);
        if (newDataLength > data.length)
            data = Arrays.copyOf(data, newDataLength);
        this.shape = shape;
        return this;
    }

    @Override
    public float get(int... address) {
        return data[MoreMath.collapseAddress(shape, address)];
    }

    @Override
    public void set(float value, int... address) {
        data[MoreMath.collapseAddress(shape, address)] = value;
    }

    @Override
    public String toString() {
        return stringify();
    }

    public int allocationSize() {
        return data.length;
    }

    @Override
    public PolyData clone() {
        return PolyData.super.clone();
    }
}
