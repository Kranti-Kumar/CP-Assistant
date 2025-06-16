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
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public int getTopicScore(String topic) {
        return topicScores.getOrDefault(topic, 0);
    }

    public Map<String, Integer> getAllTopicScores() {
        return new HashMap<>(topicScores);
    }

    public boolean isTopicWeak(String topic) {
        int avgScore = (int) topicScores.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
        return getTopicScore(topic) < avgScore;
    }
}