package in.mcxiv.abyss.data.representation;

public class PolyDataPolyData implements PolyData {

    PolyData polyData;

    public PolyDataPolyData(PolyData polyData) {
        this.polyData = polyData;
    }

    PolyDataPolyData() {
    }

    @Override
    public int dims() {
        return polyData.dims();
    }

    @Override
    public int shape(int dim) {
        return polyData.shape(dim);
    }

    @Override
    public PolyData reshape(int[] shape) {
        polyData.reshape(shape);
        return this;
    }

    @Override
    public float get(int... address) {
        return polyData.get(address);
    }

    @Override
    public void set(float value, int... address) {
        polyData.set(value, address);
    }

    @Override
    public PolyData clone() {
        return PolyData.super.clone();
    }
}
