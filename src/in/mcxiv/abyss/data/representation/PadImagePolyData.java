package in.mcxiv.abyss.data.representation;

public class PadImagePolyData extends PolyDataPolyData {

    private int[] padSize;
    private float padValue;

    public PadImagePolyData(PolyData polyData, float padValue, int...padSize) {
        super(polyData);
        assert padSize.length == polyData.dims();
        this.padSize = padSize;
        this.padValue = padValue;
    }

    @Override
    public int shape(int dim) {
        return super.shape(dim) + 2 * padSize[dim];
    }

    @Override
    public PolyData reshape(int[] shape) {
        for (int i = 0; i < shape.length; i++)
            shape[i] -= 2 * padSize[i];
        super.reshape(shape);
        return this;
    }

    @Override
    public float get(int... address) {
        for (int i = 0; i < address.length; i++) {
            if (address[i] < padSize[i] || address[i] >= super.shape(i) + padValue)
                return padValue;
            else address[i] -= padSize[i];
        }
        return super.get(address);
    }

    @Override
    public void set(float value, int... address) {
        for (int i = 0; i < address.length; i++) {
            if (address[i] < padSize[i] || address[i] >= super.shape(i) + padValue) {
                System.err.println("Setting value to pad space; was it intentional?");
                padValue = value;
                return;
            } else address[i] -= padSize[i];
        }
        super.set(value, address);
    }

    @Override
    public PolyData clone() {
        return super.clone();
    }
}
