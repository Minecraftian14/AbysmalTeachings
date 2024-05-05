package in.mcxiv.abyss.plot;

import java.io.*;
import java.util.List;

public class PyPlot {

    public static final int TRUNCATION_THRESHOLD = 1000;

    static void runPythonProgram(String program, boolean echo) {
        try {
            var process = new ProcessBuilder("C:\\ProgramData\\anaconda3\\python.exe")
                    .redirectErrorStream(true)
                    .start();
            var outputStream = process.getOutputStream();
            outputStream.write((program + "\n").getBytes());
            outputStream.close();
            InputStream inputStream = process.getInputStream();
            String output = new String(inputStream.readAllBytes());
            inputStream.close();
            if (echo)
                System.out.println("\nThe following program was ran:\n" + program
                        + "\nThe following output was received:\n" + output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void plotBar(String title, List<Float> array, boolean truncate) {
        try {
            int s = array.size(), k = s / TRUNCATION_THRESHOLD;
            truncate = truncate && s >= TRUNCATION_THRESHOLD;
            File file = File.createTempFile("plot_data", null);
            file.deleteOnExit();
            var writer = new OutputStreamWriter(new FileOutputStream(file));
            for (int i = 0; i < s; i++) {
                if (truncate && i % k != 0) continue;
                writer.write(String.valueOf(array.get(i)));
                writer.write(System.lineSeparator());
            }
            writer.close();
            runPythonProgram("""
                    import matplotlib.pyplot as plt
                    list = [0.0, 1.0]
                    with open(r'%s') as f:
                        list = [float(x) for x in f.readlines()]
                    plt.plot(list)
                    plt.title('%s')
                    plt.show()
                    print("Data length of plot: ", len(list))
                    """.formatted(file.getAbsolutePath(), title), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
