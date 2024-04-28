package in.mcxiv.abyss.utilities;

import in.mcxiv.abyss.plot.PyPlot;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Recorder {

    ArrayList<Float> list = new ArrayList<>();
    ScheduledExecutorService service;

    public Recorder(Supplier<Float> record) {
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(() -> list.add(record.get()), 100, 250, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void shutdown() {
        if (service.isShutdown()) return;
        try {
            service.shutdown();
            service.awaitTermination(1, TimeUnit.MINUTES);
            PyPlot.plotBar(list, false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
