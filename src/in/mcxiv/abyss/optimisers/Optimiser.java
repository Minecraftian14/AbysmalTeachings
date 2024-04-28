package in.mcxiv.abyss.optimisers;

import java.util.ArrayList;
import java.util.List;

public abstract class Optimiser {

    List<TrainingEventListener> eventListeners = new ArrayList<>();

    public final void fit() {
        fitImplementation();
    }

    protected abstract void fitImplementation();

    public void addListener(TrainingEventListener listener) {
        eventListeners.add(listener);
    }

    public interface TrainingEventListener {

        default void trainingStarted() {
        }

        default void trainingStopped() {
        }

        default void nextEpochTrainingStarted() {
        }

        default void nextEpochTrainingStopped() {
        }

        default void nextBatchTrainingStarted() {
        }

        default void nextBatchTrainingStopped() {
        }

        default void recordCost(float cost) {
        }
    }

}
