package in.mcxiv.abyss.updators;

import in.mcxiv.abyss.utilities.Cache;
import in.mcxiv.abyss.utilities.Pools;

import static in.mcxiv.abyss.data.representation.PolyData.add;
import static in.mcxiv.abyss.data.representation.PolyData.unaryOperation;

public class SimpleAdditiveUpdater implements Updater {

    public float learning_rate = 0.1f;

    @Override
    public void apply(Cache cache) {
//        System.out.println("Apply called");

        cache.getParameters().forEach(key -> {
//            System.out.println("Apply performed");

            var parameter = cache.remove(key);
            var dParameter = cache.remove(parameter.hashCode());
            unaryOperation(dParameter, dParameter, f -> learning_rate * f);
            add(parameter, dParameter, parameter);

            Pools.ARRAY_POOL.free(dParameter);
        });
    }
}
