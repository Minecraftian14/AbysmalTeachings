package in.mcxiv.abyss.data.representation;

public class ReorderedDimsPolyData extends PolyDataPolyData {

    int[] shapeIndices;
    int[] buffer;

    public ReorderedDimsPolyData(PolyData polyData, int...shapeIndices) {
        super(polyData);
        this.shapeIndices = shapeIndices;
        this.buffer = new int[shapeIndices.length];
    }

    @Override
    public int shape(int dim) {
        return super.shape(shapeIndices[dim]);
    }

    @Override
    public PolyData reshape(int[] shape) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float get(int... address) {
        for (int i = 0; i < address.length; i++)
            buffer[i] = address[shapeIndices[i]];
        return super.get(buffer);
    }

    @Override
    public void set(float value, int... address) {
        for (int i = 0; i < address.length; i++)
            buffer[i] = address[shapeIndices[i]];
        super.set(value, buffer);
    }

    @Override
    public PolyData clone() {
        return super.clone();
    }
}
