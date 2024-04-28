package in.mcxiv.abyss.data.representation;

import in.mcxiv.abyss.interfaces.IntToIntFunction;
import in.mcxiv.abyss.mathematics.MoreMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static PolyData fromList(List<Float> list) {
        return new Array1DPolyData(list.size()).fill(list.iterator()::next);
    }

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
