package in.mcxiv.abyss.plot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class PyPlot {

    public static void plot() {

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Process process =
                new ProcessBuilder("C:\\ProgramData\\anaconda3\\python.exe")
//                new ProcessBuilder("C:\\Windows\\System32\\cmd.exe", "/K", "C:\\ProgramData\\anaconda3\\python.exe")
//                new ProcessBuilder("C:\\Windows\\System32\\notepad.exe")
                        .start();
        BufferedReader reader = process.inputReader();
        BufferedWriter writer = process.outputWriter();
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        new Thread(() -> {
            try {
                while (true)
                    System.out.println("| " + reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        Thread.sleep(1000);
        while (process.isAlive()) {
            System.out.print("|> ");
            writer.write(scanner.nextLine());
            writer.flush();
        }
//                .directory(new File(""));
//        System.out.println(new File("Hello.txt").getAbsolutePath());
    }

}
