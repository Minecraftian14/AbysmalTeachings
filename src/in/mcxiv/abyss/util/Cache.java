package in.mcxiv.abyss.util;

import in.mcxiv.abyss.data.PolyData;

import java.util.HashMap;
import java.util.Objects;

public class Cache extends HashMap<Integer, PolyData> {

//    public PolyData put(PolyData value) {
//        return super.put(key, value);
//    }

    public PolyData put(Object owner, String name, PolyData value) {
        return super.put(Objects.hash(owner, name), value);
    }

    public PolyData get(Object owner, String name) {
        return super.get(Objects.hash(owner, name));
    }

    record Key(int owner, String key) {

    }
}
