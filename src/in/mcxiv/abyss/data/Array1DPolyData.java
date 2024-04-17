package in.mcxiv.abyss.data;

import in.mcxiv.abyss.iface.IntToIntFunction;
import in.mcxiv.abyss.math.MoreMath;

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
        this.data = new float[MoreMath.multiplyElements(shape)];
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
    public PolyData reshape(int[] shape) {
        int newDataLength = MoreMath.multiplyElements(shape);
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


    public final class Pool {
        private Pool() {
        }

        private static List<Array1DPolyData> pool = new ArrayList<>();

        public static void free(Array1DPolyData data) {
            if (pool.isEmpty()) {
                pool.add(data);
                return;
            }
            if (data.data.length <= pool.getFirst().data.length) {
                pool.addFirst(data);
                return;
            }
            if (data.data.length >= pool.getLast().data.length) {
                pool.addLast(data);
                return;
            }
            for (int i = 0, s = pool.size(); i < s; i++)
                if (pool.get(i).data.length > data.data.length) {
                    pool.add(i, data);
                    return;
                }
            throw new IllegalStateException();
        }

        public static Array1DPolyData issue() {
            return pool.isEmpty()? new Array1DPolyData(): pool.removeFirst();
        }

        public static Array1DPolyData issue(int weight) {
            for (int i = 0, s = pool.size(); i < s; i++)
                if (pool.get(i).data.length > weight)
                    return pool.remove(i);
            return new Array1DPolyData(1, weight);
        }

        public static Array1DPolyData issue(int[] shape) {
            return (Array1DPolyData) issue(MoreMath.multiplyElements(shape)).reshape(shape);
        }

        public static Array1DPolyData issue(PolyData copyFrom) {
            return issue(copyFrom.dims(), copyFrom::shape);
        }

        public static Array1DPolyData issue(int length, IntToIntFunction array) {
            return (Array1DPolyData) issue(MoreMath.multiplyElements(length, array)).reshape(length, array);
        }
    }

    @Override
    public PolyData clone() {
        return PolyData.super.clone();
    }
}
