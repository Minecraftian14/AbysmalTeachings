package in.mcxiv.abyss.data;

import java.util.stream.IntStream;

public class SlicedPolyData extends PolyDataPolyData {

    // x[1, :, 4] === x.slice(1, ALL, 4)
    public static final int ALL = -1;

    // x[1, np.newaxis, 4] === x.slice(1, NEW, 4)
    public static final int NEW = -2;

    // x[:1] === x.slice([START], [1])
    public static final int START = -3;

    // x[1:] === x.slice([1], [END])
    public static final int END = -4;

    // x[1:5] === x.slice([1], [5])
    // x[4, 1:5, :3] === x.slice([4, 1, START], [4, 5, 3])

    int[] fromMask;
    int[] toMask;
    int[] buffer;

    int dims;
    int[] shape;

    public SlicedPolyData() {
    }

    public SlicedPolyData(PolyData polyData, int... mask) {
        this(polyData, mask, mask);
    }

    public SlicedPolyData(PolyData polyData, int[] fromMask, int[] toMask) {
        super(polyData);
        initialize(polyData, fromMask, toMask);
    }

    public  SlicedPolyData initialize(PolyData polyData, int[] fromMask, int[] toMask) {
        this.polyData = polyData;
        this.fromMask = fromMask.clone();
        this.toMask = toMask.clone();
        this.buffer = new int[polyData.dims()];
        this.dims = polyData.dims() - ((int) IntStream.range(0, fromMask.length)
                .filter(i -> fromMask[i] >= 0 && fromMask[i] == toMask[i])
                .count());
        this.shape = new int[this.dims];
        int i = 0, j = 0;
        while (i < polyData.dims()) {
            if (i < fromMask.length && fromMask[i] >= 0) {
                if (fromMask[i] != toMask[i])
                    shape[j++] = toMask[i] - fromMask[i];
                i++;
            }
            else shape[j++] = polyData.shape(i++);
        }
        return this;
    }

    public void upcastAddress(int... address) {
        if (address.length != dims) throw new IllegalStateException();
        int addressIdx = 0;
        int bufferIdx = 0;

        for (int i = 0; i < fromMask.length; i++) {
            int start, end;
            if (fromMask[i] == ALL || fromMask[i] == START) start = 0;
            else start = fromMask[i];
            if (toMask[i] == ALL || toMask[i] == END) end = polyData.shape(i);
            else end = toMask[i];
            if (start == end) buffer[bufferIdx++] = start;
            else buffer[bufferIdx++] = start + address[addressIdx++];
        }

        while (addressIdx < address.length) buffer[bufferIdx++] = address[addressIdx++];
    }

    @Override
    public int dims() {
        return dims;
    }

    @Override
    public int shape(int dim) {
        return shape[dim];
    }

    @Override
    public PolyData reshape(int[] shape) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float get(int... address) {
        upcastAddress(address);
        return super.get(buffer);
    }

    @Override
    public void set(float value, int... address) {
        upcastAddress(address);
        super.set(value, buffer);
    }

    @Override
    public PolyData clone() {
        return super.clone();
    }

    @Override
    public String toString() {
        return stringify();
    }
}