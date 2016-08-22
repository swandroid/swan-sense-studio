package swanlake.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogCompiler {

    public static final Pattern LOG_PAT = Pattern.compile("(\\d+),(\\d+),(\\d+),(\\d+),(\\d+),(\\d+)");
    public static final Pattern LOG_BAT_PAT = Pattern.compile("(\\d+),(\\d+)");

    public static void main(String[] args) {
        System.out.println(args[0]);
        compileFiles(new File(args[0]));
    }

    public static void compileFiles(File dir) {
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                compileFiles(file); // Calls same method again.
            } else {
                if(file.getName().startsWith("Trepn")) {
                    compileFile(file);
                }
            }
        }
    }

    public static void compileFile(File file) {
        StringBuffer sb = new StringBuffer();
        System.out.println("Processing file: " + file.getName());

        try {
            Scanner sc = new Scanner(file);
            PrintWriter pw = new PrintWriter(file.getParent() + File.separator + "res_stats.txt");
            double avgCpu = 0;
            double avgMem = 0;
            double logCount = 0;
            long deltaBat = -1;
            long prevBat = 101;

            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                Matcher mat = LOG_PAT.matcher(line);
                Matcher matBat = LOG_BAT_PAT.matcher(line);

                if(mat.find()) {
                    long time = Long.parseLong(mat.group(1));
                    long cpu = Long.parseLong(mat.group(2));
                    long mem = Long.parseLong(mat.group(4));
                    long bat = Long.parseLong(mat.group(6));

                    avgCpu += cpu;
                    avgMem += mem;
                    logCount++;

                    if(deltaBat < 0) {
                        deltaBat = 100 - bat;
                    }

                    if(bat + deltaBat < prevBat) {
                        sb.append(time + "\t" + cpu + "\t" + mem + "\t" + (bat + deltaBat) + "\n");
                        prevBat = bat + deltaBat;
                    }
                } else if(matBat.find()) {
                    long time = Long.parseLong(matBat.group(1));
                    long bat = Long.parseLong(matBat.group(2));

                    if(deltaBat < 0) {
                        deltaBat = 100 - bat;
                    }

                    if(bat + deltaBat < prevBat) {
                        sb.append(time + "\t" + (bat + deltaBat) + "\n");
                        prevBat = bat + deltaBat;
                    }
                }
            }

            if(logCount > 0) {
                avgCpu /= logCount;
                avgMem /= logCount;

                pw.print("\navg CPU(%) = " + avgCpu);
                pw.print("\navg memory(MB) = " + avgMem / 1000);
                pw.print("\n\n# Time(ms)\tCPU(%)\tMemory(KB)\tBattery(%)");
            } else {
                pw.print("\n\n# Time(ms)\tBattery(%)");
            }

            pw.print("\n\n" + sb.toString());

            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
