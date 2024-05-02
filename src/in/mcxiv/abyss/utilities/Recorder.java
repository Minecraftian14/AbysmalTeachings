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
    String title;

    public Recorder(Supplier<Float> record, String title) {
        this.title = title;
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(() -> list.add(record.get()), 0, 20, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void shutdown() {
        System.out.println("qwertyuiop");
        if (service.isShutdown()) return;
        try {
            service.shutdown();
            service.awaitTermination(1, TimeUnit.MINUTES);
            PyPlot.plotBar(title, list, false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
