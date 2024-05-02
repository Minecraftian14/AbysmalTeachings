package in.mcxiv.abyss.data.representation;

import in.mcxiv.abyss.mathematics.MoreMath;

public class FlipImagePolyData extends PolyDataPolyData {

    int[] shapeCache;

    public FlipImagePolyData(PolyData polyData) {
        super(polyData);
        shapeCache = polyData.shape();
    }

    @Override
    public PolyData reshape(int[] shape) {
        shapeCache =shape.clone();
        return  super.reshape(shapeCache);
    }

    @Override
    public float get(int... address) {
        for (int i = 0; i < address.length; i++)
            address[i] = shapeCache[i] - address[i] - 1;
        return super.get(address);
    }

    @Override
    public void set(float value, int... address) {
        for (int i = 0; i < address.length; i++)
            address[i] = shapeCache[i] - address[i] - 1;
        super.set(value, address);
    }

    @Override
    public PolyData clone() {
        return super.clone();
    }
}
