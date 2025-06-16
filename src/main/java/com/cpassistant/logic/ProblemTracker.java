package com.cpassistant.logic;

import com.cpassistant.model.Problem;
import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProblemTracker {
    private Map<String, List<Problem>> problemsByTopic;
    private static final String DATA_FILE = "data/problems.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public ProblemTracker() {
        problemsByTopic = new HashMap<>();
        loadProblems();
    }

    public void addProblem(Problem problem) {
        problemsByTopic.computeIfAbsent(problem.getTopic(), k -> new ArrayList<>())
                .add(problem);
        saveProblems();
    }

    public List<Problem> getProblemsByTopic(String topic) {
        return problemsByTopic.getOrDefault(topic, new ArrayList<>());
    }

    public Map<String, Integer> getTopicStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, List<Problem>> entry : problemsByTopic.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        return stats;
    }

    private void loadProblems() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Problem problem = new Problem(
                            parts[0],
                            parts[1],
                            parts[2],
                            LocalDate.parse(parts[3], DATE_FORMATTER));
                    addProblem(problem);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading problems: " + e.getMessage());
        }
    }

    private void saveProblems() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (List<Problem> problems : problemsByTopic.values()) {
                for (Problem problem : problems) {
                    writer.write(String.format("%s,%s,%s,%s%n",
                            problem.getName(),
                            problem.getTopic(),
                            problem.getPlatform(),
                            problem.getDateSolved().format(DATE_FORMATTER)));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving problems: " + e.getMessage());
        }
    }
}