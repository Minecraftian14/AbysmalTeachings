package in.mcxiv.abyss.data.representation;

public class ConstantPolyData implements PolyData {

    int[] shape;
    float value;

    public ConstantPolyData(float value, int... shape) {
        this.value = value;
        this.shape = shape;
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
        this.shape = shape;
        return this;
    }

    @Override
    public float get(int... address) {
        return value;
    }

    @Override
    public void set(float value, int... address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PolyData clone() {
        return PolyData.super.clone();
    }

}
