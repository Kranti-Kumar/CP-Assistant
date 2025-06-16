package com.cpassistant.utils;

import java.io.*;
import java.util.Map;

public class CSVExporter {
    private static final String OUTPUT_DIR = "output";

    public static void exportProgress(Map<String, Integer> topicStats, String filename) {
        File outputDir = new File(OUTPUT_DIR);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_DIR + "/" + filename))) {
            // Write header
            writer.write("Topic,Total Solved\n");

            // Write data
            for (Map.Entry<String, Integer> entry : topicStats.entrySet()) {
                writer.write(String.format("%s,%d\n", entry.getKey(), entry.getValue()));
            }
        } catch (IOException e) {
            System.err.println("Error exporting progress: " + e.getMessage());
        }
    }
}