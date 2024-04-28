package in.mcxiv.abyss.feeders;

import in.mcxiv.abyss.data.representation.PolyData;
import in.mcxiv.abyss.optimisers.Optimiser;

import java.util.Iterator;
import java.util.Map;

public interface Feeder extends Iterator<Map.Entry<? extends PolyData, ? extends PolyData>>, Optimiser.TrainingEventListener {
}
