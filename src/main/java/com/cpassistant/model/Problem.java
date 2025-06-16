package com.cpassistant.model;

import java.time.LocalDate;

public class Problem {
    private String name;
    private String topic;
    private String platform;
    private LocalDate dateSolved;

    public Problem(String name, String topic, String platform, LocalDate dateSolved) {
        this.name = name;
        this.topic = topic;
        this.platform = platform;
        this.dateSolved = dateSolved;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getTopic() {
        return topic;
    }

    public String getPlatform() {
        return platform;
    }

    public LocalDate getDateSolved() {
        return dateSolved;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setDateSolved(LocalDate dateSolved) {
        this.dateSolved = dateSolved;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s - Solved on: %s",
                name, platform, topic, dateSolved.toString());
    }
}