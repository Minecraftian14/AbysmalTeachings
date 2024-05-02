package in.mcxiv.abyss.plot;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static in.mcxiv.abyss.plot.PyPlot.plotBar;

@Disabled
class PyPlotTest {

    @Test
    void simpleTest() {
        plotBar("Title", IntStream.range(0, 10000).mapToDouble(v -> v + 1000 * Math.random()).mapToObj(d -> ((float) d)).toList(), true);
    }
}