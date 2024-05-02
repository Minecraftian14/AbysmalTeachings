package in.mcxiv.abyss.models.implementations;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.models.abstractions.MathematicalUnit;
import in.mcxiv.abyss.utilities.Cache;

public class CustomOperationUnit extends MathematicalUnit {

    private ForwardUnit forwardUnit;
    private BackwardUnit backwardUnit;

    public CustomOperationUnit(ForwardUnit forwardUnit, BackwardUnit backwardUnit) {
        this.forwardUnit = forwardUnit;
        this.backwardUnit = backwardUnit;
    }

    @Override
    public PolyData forward(PolyData a_in, PolyData a_out, Cache cache) {
        return forwardUnit.forward(a_in, a_out);
    }

    @Override
    public PolyData backward(PolyData da_out, PolyData da_in, Cache cache) {
        return backwardUnit.backward(da_out, da_in);
    }

    public interface ForwardUnit {
        PolyData forward(PolyData a_in, PolyData a_out);
    }

    public interface BackwardUnit {
        PolyData backward(PolyData da_out, PolyData da_in);
    }

}
