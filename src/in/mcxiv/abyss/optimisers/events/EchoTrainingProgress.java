package in.mcxiv.abyss.optimisers.events;

import in.mcxiv.abyss.optimisers.Optimiser;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class EchoTrainingProgress implements Optimiser.TrainingEventListener {

    PrintStream out;
    DateTimeFormatter dtf;

    public EchoTrainingProgress() {
        this(System.out, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public EchoTrainingProgress(PrintStream out, DateTimeFormatter dtf) {
        this.out = out;
        this.dtf = dtf;
    }

    @Override
    public void trainingStarted() {
        out.println("Training started at " + LocalDateTime.now().format(dtf));
        epoch = 0;
    }

    int epoch;
    LocalDateTime startTime;

    @Override
    public void nextEpochTrainingStarted() {
        epoch++;
        startTime = LocalDateTime.now();
    }

    @Override
    public void nextEpochTrainingStopped() {
        out.printf("Epoch %d completed in %.2f seconds.%n", epoch, 0.001f*ChronoUnit.MILLIS.between(startTime, LocalDateTime.now()));
    }

    @Override
    public void trainingStopped() {
        out.println("Training stopped at " + LocalDateTime.now().format(dtf));
    }
}
