package com.cpassistant.logic;

import java.util.*;
import java.util.stream.Collectors;

public class UserStats {
    private Map<String, Integer> topicScores;
    private ProblemTracker problemTracker;

    public UserStats(ProblemTracker problemTracker) {
        this.problemTracker = problemTracker;
        this.topicScores = new HashMap<>();
        updateScores();
    }

    public void updateScores() {
        topicScores = problemTracker.getTopicStats();
    }

    public List<String> getWeakTopics() {
        return topicScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(5) // Limit to 5 weakest topics
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public int getTopicScore(String topic) {
        updateScores(); // Update scores before getting topic score
        return topicScores.getOrDefault(topic, 0);
    }

    public Map<String, Integer> getAllTopicScores() {
        updateScores(); // Update scores before getting all scores
        return new HashMap<>(topicScores);
    }

    public boolean isTopicWeak(String topic) {
        updateScores(); // Update scores before checking if topic is weak
        if (topicScores.isEmpty()) {
            return false;
        }

        double avgScore = topicScores.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        return getTopicScore(topic) < avgScore;
    }

    public void printTopicStats() {
        updateScores();
        if (topicScores.isEmpty()) {
            System.out.println("No problems solved yet.");
            return;
        }

        System.out.println("\nTopic-wise Statistics:");
        System.out.println("---------------------");

        // Calculate average
        double avgScore = topicScores.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        // Sort topics by score
        topicScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> {
                    String status = entry.getValue() < avgScore ? " (Weak)" : " (Strong)";
                    System.out.printf("%s: %d problems%s%n",
                            entry.getKey(),
                            entry.getValue(),
                            status);
                });

        System.out.printf("%nAverage problems per topic: %.1f%n", avgScore);
    }
}