package in.mcxiv.abyss.core;

public class ActivationFunction {
    public static float sigmoid(float x) {
        return 1 / (1 + ((float) Math.exp(-x)));
    }

    public static float dsigmoid(float x) {
        return x * (1 - x);
    }
}
