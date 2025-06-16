package src.main.java.com.cpassistant;

import src.main.java.com.cpassistant.logic.*;
import src.main.java.com.cpassistant.model.Problem;
import src.main.java.com.cpassistant.utils.CSVExporter;
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
        System.out.println("6. Exit");
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
}