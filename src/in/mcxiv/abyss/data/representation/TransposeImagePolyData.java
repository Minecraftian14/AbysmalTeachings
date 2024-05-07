package in.mcxiv.abyss.data.representation;

import in.mcxiv.abyss.mathematics.MiscMath;

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
        super.reshape(MiscMath.reversed(shape));
        return this;
    }

    @Override
    public float get(int... address) {
        return super.get(MiscMath.reversed(address));
    }

    @Override
    public void set(float value, int... address) {
        super.set(value, MiscMath.reversed(address));
    }

    @Override
    public PolyData clone() {
        return super.clone();
    }
}
