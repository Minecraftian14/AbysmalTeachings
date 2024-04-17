package in.mcxiv.abyss.impl;

import in.mcxiv.abyss.abs.MathematicalUnit;
import in.mcxiv.abyss.data.Array1DPolyData;
import in.mcxiv.abyss.data.ConstantPolyData;
import in.mcxiv.abyss.data.PolyData;

public class FullyConnected extends MathematicalUnit {

    PolyData weights;

    public PolyData forward(PolyData a, PolyData b) {
        return PolyData.cross(a, weights, b);
    }

    ConstantPolyData lr = new ConstantPolyData(-0.01f, 1);
    Array1DPolyData cache = new Array1DPolyData(1, 1);


    public PolyData backward(PolyData db, PolyData a) {
        var dw = PolyData.cross(a.transpose(), db, cache);
        PolyData.add(weights, PolyData.dot(dw, lr.reshape(dw), dw), weights);
        return a;
    }

}
