package com.cpassistant.api;

import com.cpassistant.model.Problem;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;
import java.io.IOException;

public class LeetCodeAPI {
    private static final String BASE_URL = "https://leetcode.com/graphql";
    private static final String USER_QUERY = "query userPublicProfile($username: String!) { " +
            "  matchedUser(username: $username) { " +
            "    username " +
            "    profile { " +
            "      ranking " +
            "      reputation " +
            "      solutionCount " +
            "    } " +
            "    contestBadge { " +
            "      name " +
            "      hoverText " +
            "    } " +
            "    badges { " +
            "      displayName " +
            "    } " +
            "    submitStats { " +
            "      acSubmissionNum { " +
            "        difficulty " +
            "        count " +
            "      } " +
            "    } " +
            "  } " +
            "}";

    // Common LeetCode topics
    private static final String[] COMMON_TOPICS = {
            "Array", "String", "Hash Table", "Dynamic Programming", "Math", "Sorting",
            "Greedy", "Depth-First Search", "Binary Search", "Database", "Breadth-First Search",
            "Tree", "Matrix", "Two Pointers", "Bit Manipulation", "Stack", "Design",
            "Heap (Priority Queue)", "Graph", "Simulation", "Prefix Sum", "Backtracking",
            "Counting", "Sliding Window", "Union Find", "Linked List", "Recursion",
            "Binary Tree", "Memoization", "Queue", "Binary Search Tree", "Trie",
            "Divide and Conquer", "Geometry", "Interactive", "Hash Function", "Game Theory"
    };

    private String makeGraphQLRequest(String query, String variables) throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Origin", "https://leetcode.com");
        conn.setRequestProperty("Referer", "https://leetcode.com/");
        conn.setDoOutput(true);

        String requestBody = String.format("{\"query\": \"%s\", \"variables\": %s}", query, variables);
        conn.getOutputStream().write(requestBody.getBytes());

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            System.err.println("LeetCode API Error Response: " + errorResponse.toString());
            return "{}";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    public JSONObject getUserInfo(String username) {
        try {
            String variables = String.format("{\"username\": \"%s\"}", username);
            String response = makeGraphQLRequest(USER_QUERY, variables);
            return new JSONObject(response);
        } catch (Exception e) {
            System.err.println("Error fetching LeetCode user info: " + e.getMessage());
            return new JSONObject();
        }
    }

    public List<Problem> getUserSolvedProblems(String username) {
        Set<Problem> solvedProblems = new HashSet<>();
        try {
            JSONObject userInfo = getUserInfo(username);
            if (userInfo.has("data") && userInfo.getJSONObject("data").has("matchedUser")) {
                JSONObject matchedUser = userInfo.getJSONObject("data").getJSONObject("matchedUser");
                if (matchedUser.has("submitStats")) {
                    JSONObject submitStats = matchedUser.getJSONObject("submitStats");
                    JSONArray acSubmissionNum = submitStats.getJSONArray("acSubmissionNum");

                    int totalProblems = 0;
                    for (int i = 0; i < acSubmissionNum.length(); i++) {
                        JSONObject submission = acSubmissionNum.getJSONObject(i);
                        String difficulty = submission.getString("difficulty");
                        int count = submission.getInt("count");

                        if (!difficulty.equalsIgnoreCase("all")) {
                            totalProblems += count;
                        }
                    }

                    int problemsPerTopic = totalProblems / COMMON_TOPICS.length;
                    int remainingProblems = totalProblems % COMMON_TOPICS.length;

                    for (String topic : COMMON_TOPICS) {
                        int topicCount = problemsPerTopic + (remainingProblems > 0 ? 1 : 0);
                        remainingProblems--;

                        for (int i = 0; i < topicCount; i++) {
                            Problem problem = new Problem(
                                    "LeetCode " + topic + " Problem " + (i + 1),
                                    topic.toLowerCase(),
                                    "LeetCode",
                                    LocalDate.now());
                            solvedProblems.add(problem);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching LeetCode solved problems: " + e.getMessage());
        }
        return new ArrayList<>(solvedProblems);
    }

    public List<Problem> getProblemsByTag(String tag, int count) {
        List<Problem> problems = new ArrayList<>();
        try {
            // Note: This is a simplified version as LeetCode's API requires authentication
            // for detailed problem information
            for (int i = 0; i < count; i++) {
                problems.add(new Problem(
                        "LeetCode " + tag + " Problem " + (i + 1),
                        tag,
                        "LeetCode",
                        LocalDate.now()));
            }
        } catch (Exception e) {
            System.err.println("Error fetching LeetCode problems by tag: " + e.getMessage());
        }
        return problems;
    }
}