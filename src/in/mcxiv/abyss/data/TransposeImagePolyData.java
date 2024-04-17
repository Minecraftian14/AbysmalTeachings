package in.mcxiv.abyss.data;

import in.mcxiv.abyss.math.MoreMath;

public class TransposeImagePolyData extends PolyDataPolyData {
    public TransposeImagePolyData(PolyData polyData) {
        super(polyData);
    }

    @Override
    public int shape(int dim) {
        return super.shape(dims() - dim - 1);
    }

    @Override
    public PolyData reshape(int[] shape) {
        // TODO: Verify
        super.reshape(MoreMath.reversed(shape));
        return this;
    }

    @Override
    public float get(int... address) {
        return super.get(MoreMath.reversed(address));
    }

    @Override
    public void set(float value, int... address) {
        super.set(value, MoreMath.reversed(address));
    }

    @Override
    public PolyData clone() {
        return super.clone();
    }
}
