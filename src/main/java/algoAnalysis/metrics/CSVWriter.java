package algoAnalysis.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {
    public static void writeToCSV(String filename, List<String[]> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
        System.out.println("Data written to: " + filename);
    }

    public static void writeMetricsToCSV(String filename, String algorithm, int arraySize,
                                         AlgorithmMetrics metrics) throws IOException {

        File dir = new File("analysis_results");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, filename);
        boolean writeHeader = !file.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (writeHeader) {
                writer.write("Algorithm,ArraySize,Comparisons,Swaps,Assignments,MaxDepth,TimeNanos,TimeMillis");
                writer.newLine();
            }

            writer.write(String.format("%s,%d,%d,%d,%d,%d,%d,%.3f",
                    algorithm, arraySize, metrics.getComparisons(), metrics.getSwaps(),
                    metrics.getAssignments(), metrics.getMaxDepth(),
                    metrics.getExecutionTimeNanos(), metrics.getExecutionTimeMillis()));
            writer.newLine();
        }
    }
}
