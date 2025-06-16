package com.cpassistant.logic;

import java.util.*;
import java.io.*;

public class TopicGraph {
    private Map<String, List<String>> adjacencyList;
    private static final String GRAPH_FILE = "src/main/resources/topic_graph.txt";

    public TopicGraph() {
        adjacencyList = new HashMap<>();
        loadGraph();
    }

    public void addDependency(String topic, String prerequisite) {
        adjacencyList.computeIfAbsent(topic, k -> new ArrayList<>())
                .add(prerequisite);
        saveGraph();
    }

    public List<String> getPrerequisites(String topic) {
        return adjacencyList.getOrDefault(topic, new ArrayList<>());
    }

    public List<String> getTopologicalOrder() {
        Set<String> visited = new HashSet<>();
        Set<String> temp = new HashSet<>();
        List<String> order = new ArrayList<>();

        for (String topic : adjacencyList.keySet()) {
            if (!visited.contains(topic)) {
                if (!topologicalSort(topic, visited, temp, order)) {
                    throw new IllegalStateException("Circular dependency detected!");
                }
            }
        }

        Collections.reverse(order);
        return order;
    }

    private boolean topologicalSort(String topic, Set<String> visited, Set<String> temp, List<String> order) {
        if (temp.contains(topic))
            return false;
        if (visited.contains(topic))
            return true;

        temp.add(topic);
        for (String prerequisite : getPrerequisites(topic)) {
            if (!topologicalSort(prerequisite, visited, temp, order)) {
                return false;
            }
        }
        temp.remove(topic);
        visited.add(topic);
        order.add(topic);
        return true;
    }

    private void loadGraph() {
        File file = new File(GRAPH_FILE);
        if (!file.exists()) {
            System.err.println("Topic graph file not found: " + GRAPH_FILE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split("->");
                if (parts.length == 2) {
                    addDependency(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading topic graph: " + e.getMessage());
        }
    }

    private void saveGraph() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GRAPH_FILE))) {
            for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
                for (String prerequisite : entry.getValue()) {
                    writer.write(String.format("%s -> %s%n", entry.getKey(), prerequisite));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving topic graph: " + e.getMessage());
        }
    }
}