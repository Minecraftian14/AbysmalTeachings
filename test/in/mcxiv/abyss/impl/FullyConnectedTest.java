package in.mcxiv.abyss.impl;

import in.mcxiv.abyss.data.Array1DPolyData;
import in.mcxiv.abyss.math.MoreMath;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static in.mcxiv.abyss.data.PolyData.*;

class FullyConnectedTest {

    @Test
    void basicTest() {
        var model = new FullyConnected();
        model.weights = new Array1DPolyData(2, 1);
        model.weights.fill(MoreMath::random);

        var x = new Array1DPolyData(4, 2);
        x.fill(List.of(1, 1, 1, -1, -1, 1, -1, -1).iterator()::next);
        var y = new Array1DPolyData(4, 1);
        y.fill(List.of(10, 1, -1, -2).iterator()::next);

        var yp = new Array1DPolyData(4, 1);
        model.forward(x, yp);
        System.out.println(yp);

        var cache = new Array1DPolyData(1);
        var loss = new Array1DPolyData(1);

        subtract(y, yp, cache);
        dot(cache, cache, cache);
        sum(cache, 0, loss);
        System.out.println("yp = " + yp);
        System.out.println("loss = " + loss);

        var list = new ArrayList<Float>();

        for (int i = 0; i < 100; i++) {
            model.forward(x, yp);
            scalarOperation(y, yp, cache, (a, b) -> -Math.abs(a - b) / 2);
            model.backward(cache, x);
            subtract(y, yp, cache);
            dot(cache, cache, cache);
            sum(cache, 0, loss);
            list.add(loss.get(0, 0));
        }

//        list.forEach(System.out::println);

        subtract(y, yp, cache);
        dot(cache, cache, cache);
        sum(cache, 0, loss);
        System.out.println("yp = " + yp);
        System.out.println("loss = " + loss);

    }
}