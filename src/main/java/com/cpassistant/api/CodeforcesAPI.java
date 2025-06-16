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

public class CodeforcesAPI {
    private static final String CODEFORCES_API_URL = "https://codeforces.com/api/problemset.problems";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static List<Problem> getProblemsByTag(String tag, int count) {
        List<Problem> problems = new ArrayList<>();
        try {
            String url = CODEFORCES_API_URL + "?tags=" + tag;
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
}