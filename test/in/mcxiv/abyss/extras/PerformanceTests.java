package in.mcxiv.abyss.extras;

import in.mcxiv.abyss.plot.PyPlot;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

@Disabled
public class PerformanceTests {

    @Test
    void test() {
        List<Double> l = new ArrayList<>();

//        LL add index O n at 125000
//        AL add index O n at   8000
//        LL rem index O n at  80000
//        AL rem index O 1 at   7000
//        LL rem value O n at 175000
//        AL rem value O 1 at 100000

        for (int i = 0; i < 100000; i++) l.add(Math.random());
        ArrayList<Float> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {

            int index = (int) (Math.random() * l.size());
            double value = l.get(index);

            long mark = System.nanoTime();

//            l.add(index, value);
//            l.remove(index);
            l.remove(value);

            long time = System.nanoTime() - mark;
            list.add((float) time);
        }
        float t = 3*(float) list.stream().mapToDouble(value -> value).average().getAsDouble();
        PyPlot.plotBar("Title", list.stream().filter(f -> f<t).toList(), false);
    }
}
