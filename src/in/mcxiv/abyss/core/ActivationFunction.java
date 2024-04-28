package in.mcxiv.abyss.core;

import in.mcxiv.abyss.data.representation.PolyData;

public interface ActivationFunction {

    // y = f(x)
    float activate(float x);

    default PolyData activate(PolyData x, PolyData y) {
        return PolyData.unaryOperation(x, y, this::activate);
    }

    // TODO derivative of activate, is it right to call it deactivate?
    // dy/dx = f'(x) = g(x)
    float deactivate(float x);

    default PolyData deactivate(PolyData x, PolyData dx) {
        return PolyData.unaryOperation(x, dx, this::deactivate);
    }

    ActivationFunction SIGMOID = new ActivationFunction() {
        @Override
        public float activate(float x) {
            return 1 / (1 + ((float) Math.exp(-x)));
        }

        @Override
        public float deactivate(float x) {
            return x * (1 - x);
        }
    };

    static float sigmoid(float x) {
        return 1 / (1 + ((float) Math.exp(-x)));
    }

    static float dsigmoid(float x) {
        return x * (1 - x);
    }
}
