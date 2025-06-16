package com.cpassistant.logic;

import com.cpassistant.model.Problem;
import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProblemTracker {
    private Map<String, List<Problem>> problemsByTopic;
    private Set<String> existingProblems;
    private static final String DATA_DIR = "data";
    private static final String LOCAL_FILE = "local_problems.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private String currentUserFile;

    public ProblemTracker() {
        this(LOCAL_FILE);
    }

    public ProblemTracker(String filename) {
        problemsByTopic = new HashMap<>();
        existingProblems = new HashSet<>();
        currentUserFile = filename;
        loadProblems();
    }

    public void setUser(String username) {
        if (username != null && !username.isEmpty()) {
            currentUserFile = username + "_problems.txt";
            // Clear current data and load user's problems
            problemsByTopic.clear();
            existingProblems.clear();
            loadProblems();
        } else {
            currentUserFile = LOCAL_FILE;
            // Clear current data and load local problems
            problemsByTopic.clear();
            existingProblems.clear();
            loadProblems();
        }
    }

    public void addProblem(Problem problem) {
        String problemId = problem.getName() + "|" + problem.getTopic() + "|" + problem.getPlatform();

        if (!existingProblems.contains(problemId)) {
            existingProblems.add(problemId);
            problemsByTopic.computeIfAbsent(problem.getTopic(), k -> new ArrayList<>())
                    .add(problem);
            saveProblems();
        }
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
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = new File(DATA_DIR, currentUserFile);
        if (!file.exists()) {
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

                    String problemId = problem.getName() + "|" + problem.getTopic() + "|" + problem.getPlatform();
                    existingProblems.add(problemId);

                    problemsByTopic.computeIfAbsent(problem.getTopic(), k -> new ArrayList<>())
                            .add(problem);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading problems: " + e.getMessage());
        }
    }

    private void saveProblems() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = new File(DATA_DIR, currentUserFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
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

    public String getCurrentUserFile() {
        return currentUserFile;
    }
}