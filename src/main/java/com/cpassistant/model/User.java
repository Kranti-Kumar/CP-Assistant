package com.cpassistant.model;

public class User {
    private String handle;
    private String rank;
    private int rating;
    private int maxRating;
    private String maxRank;
    private int contribution;

    public User(String handle, String rank, int rating, int maxRating, String maxRank, int contribution) {
        this.handle = handle;
        this.rank = rank;
        this.rating = rating;
        this.maxRating = maxRating;
        this.maxRank = maxRank;
        this.contribution = contribution;
    }

    // Getters
    public String getHandle() {
        return handle;
    }

    public String getRank() {
        return rank;
    }

    public int getRating() {
        return rating;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public String getMaxRank() {
        return maxRank;
    }

    public int getContribution() {
        return contribution;
    }

    @Override
    public String toString() {
        return String.format("User: %s\nRank: %s\nRating: %d\nMax Rating: %d (%s)\nContribution: %d",
                handle, rank, rating, maxRating, maxRank, contribution);
    }
}