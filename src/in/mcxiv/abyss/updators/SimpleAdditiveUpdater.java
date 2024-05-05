package in.mcxiv.abyss.updators;

import in.mcxiv.abyss.utilities.Cache;
import in.mcxiv.abyss.utilities.Pools;

public class SimpleAdditiveUpdater implements Updater {

    public float learning_rate;

    public SimpleAdditiveUpdater(float learning_rate) {
        this.learning_rate = learning_rate;
    }

    @Override
    public void apply(Cache cache) {
//        System.out.println("Apply called");

        cache.getParameters().forEach(key -> {
//            System.out.println("Apply performed");

            var parameter = cache.remove(key);
            var dParameter = cache.remove(parameter.hashCode());
            dParameter.mul(learning_rate);
            parameter.add(dParameter);

            Pools.ARRAY_POOL.free(dParameter);
        });
    }
}
