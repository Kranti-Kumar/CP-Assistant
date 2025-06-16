package com.cpassistant.logic;

import com.cpassistant.model.Problem;
import com.cpassistant.api.CodeforcesAPI;
import com.cpassistant.api.LeetCodeAPI;
import java.util.*;
import java.util.stream.Collectors;

public class Recommender {
    private ProblemTracker problemTracker;
    private TopicGraph topicGraph;
    private UserStats userStats;

    public Recommender(ProblemTracker problemTracker, TopicGraph topicGraph, UserStats userStats) {
        this.problemTracker = problemTracker;
        this.topicGraph = topicGraph;
        this.userStats = userStats;
    }

    public List<String> getRecommendedTopics() {
        List<String> weakTopics = userStats.getWeakTopics();
        List<String> topologicalOrder = topicGraph.getTopologicalOrder();

        // Filter out topics that have unlearned prerequisites
        return weakTopics.stream()
                .filter(topic -> hasLearnedPrerequisites(topic))
                .limit(2)
                .collect(Collectors.toList());
    }

    private boolean hasLearnedPrerequisites(String topic) {
        List<String> prerequisites = topicGraph.getPrerequisites(topic);
        if (prerequisites.isEmpty())
            return true;

        return prerequisites.stream()
                .allMatch(prereq -> userStats.getTopicScore(prereq) > 0);
    }

    public List<Problem> getRecommendedProblems(String topic) {
        List<Problem> problems = problemTracker.getProblemsByTopic(topic);
        if (problems.isEmpty()) {
            return fetchProblemsFromAPIs(topic);
        }

        // Return 1-2 problems that haven't been solved yet
        return problems.stream()
                .limit(2)
                .collect(Collectors.toList());
    }

    private List<Problem> fetchProblemsFromAPIs(String topic) {
        List<Problem> problems = new ArrayList<>();

        // Try to fetch from LeetCode first
        problems.addAll(LeetCodeAPI.getProblemsByTag(topic, 1));

        // If we don't have enough problems, try Codeforces
        if (problems.size() < 2) {
            problems.addAll(CodeforcesAPI.getProblemsByTag(topic, 2 - problems.size()));
        }

        // If still no problems, return default ones
        if (problems.isEmpty()) {
            return getDefaultProblems(topic);
        }

        return problems;
    }

    private List<Problem> getDefaultProblems(String topic) {
        List<Problem> defaultProblems = new ArrayList<>();
        defaultProblems.add(new Problem(
                "Basic " + topic + " Problem 1",
                topic,
                "LeetCode",
                java.time.LocalDate.now()));
        defaultProblems.add(new Problem(
                "Basic " + topic + " Problem 2",
                topic,
                "Codeforces",
                java.time.LocalDate.now()));
        return defaultProblems;
    }
}