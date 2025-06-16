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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class CodeforcesAPI {
    private static final String CODEFORCES_API_URL = "https://codeforces.com/api/";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static List<Problem> getProblemsByTag(String tag, int count) {
        List<Problem> problems = new ArrayList<>();
        try {
            String url = CODEFORCES_API_URL + "problemset.problems?tags=" + tag;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            if (jsonResponse.getString("status").equals("OK")) {
                JSONObject result = jsonResponse.getJSONObject("result");
                JSONArray problemsArray = result.getJSONArray("problems");

                for (int i = 0; i < Math.min(count, problemsArray.length()); i++) {
                    JSONObject problem = problemsArray.getJSONObject(i);
                    String name = problem.getString("name");
                    String problemId = problem.getInt("contestId") + problem.getString("index");

                    problems.add(new Problem(
                            "CF " + problemId,
                            tag,
                            "Codeforces",
                            LocalDate.now()));
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching problems from Codeforces: " + e.getMessage());
        }
        return problems;
    }

    public static User getUserInfo(String handle) {
        try {
            String url = CODEFORCES_API_URL + "user.info?handles=" + handle;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            if (jsonResponse.getString("status").equals("OK")) {
                JSONArray result = jsonResponse.getJSONArray("result");
                if (result.length() > 0) {
                    JSONObject userData = result.getJSONObject(0);
                    return new User(
                            userData.getString("handle"),
                            userData.optString("rank", "unrated"),
                            userData.optInt("rating", 0),
                            userData.optInt("maxRating", 0),
                            userData.optString("maxRank", "unrated"),
                            userData.optInt("contribution", 0));
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching user info from Codeforces: " + e.getMessage());
        }
        return null;
    }

    public static List<Problem> getUserSolvedProblems(String handle) {
        List<Problem> solvedProblems = new ArrayList<>();
        try {
            // First get user submissions with increased limit
            String submissionsUrl = CODEFORCES_API_URL + "user.status?handle=" + handle + "&count=5000";
            HttpRequest submissionsRequest = HttpRequest.newBuilder()
                    .uri(URI.create(submissionsUrl))
                    .GET()
                    .build();

            HttpResponse<String> submissionsResponse = client.send(submissionsRequest,
                    HttpResponse.BodyHandlers.ofString());
            JSONObject submissionsJson = new JSONObject(submissionsResponse.body());

            if (submissionsJson.getString("status").equals("OK")) {
                JSONArray submissions = submissionsJson.getJSONArray("result");
                Set<String> processedProblems = new HashSet<>();
                Map<String, JSONObject> problemDetails = new HashMap<>();

                // First pass: collect all unique problem IDs
                for (int i = 0; i < submissions.length(); i++) {
                    JSONObject submission = submissions.getJSONObject(i);
                    if (submission.getString("verdict").equals("OK")) {
                        JSONObject problem = submission.getJSONObject("problem");
                        String problemId = problem.getInt("contestId") + problem.getString("index");
                        if (!processedProblems.contains(problemId)) {
                            processedProblems.add(problemId);
                            problemDetails.put(problemId, problem);
                        }
                    }
                }

                // Second pass: create Problem objects
                for (Map.Entry<String, JSONObject> entry : problemDetails.entrySet()) {
                    String problemId = entry.getKey();
                    JSONObject problem = entry.getValue();

                    // Get tags from the problem object
                    JSONArray tags = problem.getJSONArray("tags");
                    String tag = tags.length() > 0 ? tags.getString(0) : "general";

                    // Find the submission time for this problem
                    for (int i = 0; i < submissions.length(); i++) {
                        JSONObject submission = submissions.getJSONObject(i);
                        if (submission.getString("verdict").equals("OK")) {
                            JSONObject subProblem = submission.getJSONObject("problem");
                            String subProblemId = subProblem.getInt("contestId") + subProblem.getString("index");
                            if (subProblemId.equals(problemId)) {
                                long submissionTime = submission.getLong("creationTimeSeconds");
                                LocalDate solvedDate = Instant.ofEpochSecond(submissionTime)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();

                                solvedProblems.add(new Problem(
                                        "CF " + problemId,
                                        tag,
                                        "Codeforces",
                                        solvedDate));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching user submissions from Codeforces: " + e.getMessage());
        }
        return solvedProblems;
    }
}