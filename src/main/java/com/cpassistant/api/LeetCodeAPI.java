package com.cpassistant.api;

import com.cpassistant.model.Problem;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class LeetCodeAPI {
    private static final String LEETCODE_API_URL = "https://leetcode.com/api/problems/all/";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static List<Problem> getProblemsByTag(String tag, int count) {
        List<Problem> problems = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LEETCODE_API_URL))
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