package com.cpassistant.api;

import com.cpassistant.model.Problem;
import com.cpassistant.model.User;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class LeetCodeAPI {
    private static final String LEETCODE_API_URL = "https://leetcode.com/api/";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static User getUserInfo(String username) {
        try {
            String url = LEETCODE_API_URL + "user/" + username;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            if (jsonResponse.has("username")) {
                return new User(
                        jsonResponse.getString("username"),
                        jsonResponse.optString("ranking", "unrated"),
                        jsonResponse.optInt("rating", 0),
                        jsonResponse.optInt("maxRating", 0),
                        jsonResponse.optString("maxRank", "unrated"),
                        jsonResponse.optInt("contributionPoints", 0));
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching user info from LeetCode: " + e.getMessage());
        }
        return null;
    }

    public static List<Problem> getUserSolvedProblems(String username) {
        List<Problem> solvedProblems = new ArrayList<>();
        try {
            String url = LEETCODE_API_URL + "user/" + username + "/submissions";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            if (jsonResponse.has("submissions")) {
                JSONArray submissions = jsonResponse.getJSONArray("submissions");
                Set<String> processedProblems = new HashSet<>();

                for (int i = 0; i < submissions.length(); i++) {
                    JSONObject submission = submissions.getJSONObject(i);
                    if (submission.getString("status").equals("accepted")) {
                        String problemId = submission.getString("question_id");
                        String problemTitle = submission.getString("question_title");

                        if (!processedProblems.contains(problemId)) {
                            processedProblems.add(problemId);

                            // Get problem details including tags
                            String problemUrl = LEETCODE_API_URL + "problems/" + problemId;
                            HttpRequest problemRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(problemUrl))
                                    .GET()
                                    .build();

                            HttpResponse<String> problemResponse = client.send(problemRequest,
                                    HttpResponse.BodyHandlers.ofString());
                            JSONObject problemJson = new JSONObject(problemResponse.body());

                            String tag = "general";
                            if (problemJson.has("tags")) {
                                JSONArray tags = problemJson.getJSONArray("tags");
                                if (tags.length() > 0) {
                                    tag = tags.getString(0);
                                }
                            }

                            long submissionTime = submission.getLong("timestamp");
                            LocalDate solvedDate = Instant.ofEpochSecond(submissionTime)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            solvedProblems.add(new Problem(
                                    "LC " + problemId,
                                    tag,
                                    "LeetCode",
                                    solvedDate));
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching user submissions from LeetCode: " + e.getMessage());
        }
        return solvedProblems;
    }

    public static List<Problem> getProblemsByTag(String tag, int count) {
        List<Problem> problems = new ArrayList<>();
        try {
            String url = LEETCODE_API_URL + "problems/all/";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            if (jsonResponse.has("stat_status_pairs")) {
                JSONArray problemsArray = jsonResponse.getJSONArray("stat_status_pairs");

                for (int i = 0; i < problemsArray.length() && problems.size() < count; i++) {
                    JSONObject problem = problemsArray.getJSONObject(i);
                    JSONObject stat = problem.getJSONObject("stat");
                    JSONArray tags = problem.getJSONArray("tags");

                    // Check if problem has the requested tag
                    boolean hasTag = false;
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.getString(j).equalsIgnoreCase(tag)) {
                            hasTag = true;
                            break;
                        }
                    }

                    if (hasTag) {
                        String title = stat.getString("question__title");
                        int questionId = stat.getInt("question_id");

                        problems.add(new Problem(
                                "LC " + questionId,
                                tag,
                                "LeetCode",
                                LocalDate.now()));
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching problems from LeetCode: " + e.getMessage());
        }
        return problems;
    }
}