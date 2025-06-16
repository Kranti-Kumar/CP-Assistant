package com.cpassistant;

import com.cpassistant.api.CodeforcesAPI;
import com.cpassistant.api.LeetCodeAPI;
import com.cpassistant.logic.*;
import com.cpassistant.model.Problem;
import com.cpassistant.model.User;
import com.cpassistant.utils.CSVExporter;
import java.time.LocalDate;
import java.util.*;

public class Main {
    private static ProblemTracker problemTracker;
    private static TopicGraph topicGraph;
    private static UserStats userStats;
    private static Recommender recommender;
    private static Scanner scanner;

    public static void main(String[] args) {
        initializeComponents();
        scanner = new Scanner(System.in);

        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addSolvedProblem();
                    break;
                case 2:
                    viewProblemsByTopic();
                    break;
                case 3:
                    viewWeakTopics();
                    break;
                case 4:
                    getRecommendations();
                    break;
                case 5:
                    exportProgress();
                    break;
                case 6:
                    fetchCodeforcesUser(scanner);
                    break;
                case 7:
                    fetchLeetCodeUser(scanner);
                    break;
                case 8:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void initializeComponents() {
        problemTracker = new ProblemTracker();
        topicGraph = new TopicGraph();
        userStats = new UserStats(problemTracker);
        recommender = new Recommender(problemTracker, topicGraph, userStats);
    }

    private static void displayMenu() {
        System.out.println("\n=== CP Assistant ===");
        System.out.println("1. Add a Solved Problem");
        System.out.println("2. View Solved Problems by Topic");
        System.out.println("3. View Weak Topics");
        System.out.println("4. Get Today's Practice Recommendations");
        System.out.println("5. Export Progress as CSV");
        System.out.println("6. Fetch Codeforces User Info");
        System.out.println("7. Fetch LeetCode User Info");
        System.out.println("8. Exit");
    }

    private static void addSolvedProblem() {
        System.out.println("\nEnter problem details:");
        String name = getStringInput("Problem name: ");
        String topic = getStringInput("Topic: ");
        String platform = getStringInput("Platform: ");
        LocalDate date = LocalDate.parse(getStringInput("Date solved (YYYY-MM-DD): "));

        Problem problem = new Problem(name, topic, platform, date);
        problemTracker.addProblem(problem);
        userStats.updateScores();
        System.out.println("Problem added successfully!");
    }

    private static void viewProblemsByTopic() {
        String topic = getStringInput("Enter topic to view: ");
        List<Problem> problems = problemTracker.getProblemsByTopic(topic);

        if (problems.isEmpty()) {
            System.out.println("No problems found for topic: " + topic);
            return;
        }

        System.out.println("\nProblems in " + topic + ":");
        for (Problem problem : problems) {
            System.out.println(problem);
        }
    }

    private static void viewWeakTopics() {
        List<String> weakTopics = userStats.getWeakTopics();
        if (weakTopics.isEmpty()) {
            System.out.println("No weak topics found!");
            return;
        }

        System.out.println("\nWeak Topics (from weakest to strongest):");
        for (String topic : weakTopics) {
            System.out.printf("%s: %d problems solved%n",
                    topic, userStats.getTopicScore(topic));
        }
    }

    private static void getRecommendations() {
        List<String> recommendedTopics = recommender.getRecommendedTopics();
        if (recommendedTopics.isEmpty()) {
            System.out.println("No recommendations available at this time.");
            return;
        }

        System.out.println("\nRecommended Topics:");
        for (String topic : recommendedTopics) {
            System.out.println("\nTopic: " + topic);
            List<Problem> problems = recommender.getRecommendedProblems(topic);
            System.out.println("Recommended Problems:");
            for (Problem problem : problems) {
                System.out.println("- " + problem.getName() + " (" + problem.getPlatform() + ")");
            }
        }
    }

    private static void exportProgress() {
        String filename = getStringInput("Enter filename for export (e.g., progress.csv): ");
        CSVExporter.exportProgress(userStats.getAllTopicScores(), filename);
        System.out.println("Progress exported successfully to " + filename);
    }

    private static void fetchCodeforcesUser(Scanner scanner) {
        System.out.print("Enter Codeforces handle: ");
        String handle = scanner.nextLine().trim();

        User user = CodeforcesAPI.getUserInfo(handle);
        if (user != null) {
            System.out.println("\nUser Information:");
            System.out.println(user);

            // Switch to user's problem file
            problemTracker.setUser(handle);

            List<Problem> solvedProblems = CodeforcesAPI.getUserSolvedProblems(handle);

            if (!solvedProblems.isEmpty()) {
                // Add problems to ProblemTracker
                for (Problem problem : solvedProblems) {
                    problemTracker.addProblem(problem);
                }

                // Show statistics
                Map<String, Integer> topicStats = problemTracker.getTopicStats();
                System.out.println("\nTopic-wise distribution:");
                topicStats.forEach((topic, count) -> System.out.printf("%s: %d problems%n", topic, count));
            } else {
                System.out.println("No solved problems found.");
            }
        } else {
            System.out.println("Failed to fetch user information. Please check the handle and try again.");
        }
    }

    private static void fetchLeetCodeUser(Scanner scanner) {
        System.out.print("Enter LeetCode username: ");
        String username = scanner.nextLine().trim();

        User user = LeetCodeAPI.getUserInfo(username);
        if (user != null) {
            System.out.println("\nUser Information:");
            System.out.println(user);

            // Switch to user's problem file
            problemTracker.setUser("LC_" + username);

            List<Problem> solvedProblems = LeetCodeAPI.getUserSolvedProblems(username);

            if (!solvedProblems.isEmpty()) {
                // Add problems to ProblemTracker
                for (Problem problem : solvedProblems) {
                    problemTracker.addProblem(problem);
                }

                // Show statistics
                Map<String, Integer> topicStats = problemTracker.getTopicStats();
                System.out.println("\nTopic-wise distribution:");
                topicStats.forEach((topic, count) -> System.out.printf("%s: %d problems%n", topic, count));
            } else {
                System.out.println("No solved problems found.");
            }
        } else {
            System.out.println("Failed to fetch user information. Please check the username and try again.");
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void viewProblemHistory(ProblemTracker problemTracker) {
        System.out.println("\nProblem History");
        System.out.println("--------------");

        // Show current file being viewed
        System.out.println("Current file: " + problemTracker.getCurrentUserFile());

        // Print topic-wise statistics
        userStats.printTopicStats();

        // Print all problems
        System.out.println("\nAll Solved Problems:");
        System.out.println("-------------------");
        Map<String, List<Problem>> problemsByTopic = new HashMap<>();

        // Group problems by topic
        for (String topic : problemTracker.getTopicStats().keySet()) {
            problemsByTopic.put(topic, problemTracker.getProblemsByTopic(topic));
        }

        // Print problems for each topic
        problemsByTopic.forEach((topic, problems) -> {
            System.out.println("\n" + topic + ":");
            problems.forEach(problem -> System.out.println("  " + problem));
        });
    }
}