package in.mcxiv.abyss.utilities;

import in.mcxiv.abyss.data.representation.PolyData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Cache extends ConcurrentHashMap<Integer, PolyData> {

    private List<Integer> parametersBuffer = new ArrayList<>();

    public PolyData put(Object owner, String name, PolyData value) {
        return super.put(Objects.hash(owner, name), value);
    }

    public PolyData get(Object owner, String name) {
        return super.get(Objects.hash(owner, name));
    }

    public PolyData remove(Object owner, String name) {
        return super.remove(Objects.hash(owner, name));
    }

    public void putParameter(String name, PolyData parameter, PolyData dParameter) {
        put(this, name, parameter);
        put(parameter.hashCode(), dParameter);
    }

    public Stream<Integer> getParameters() {
        parametersBuffer.clear();
        for (int i : keySet())
            if (containsKey(get(i).hashCode()))
                parametersBuffer.add(i);
        return parametersBuffer.stream();
    }

}
