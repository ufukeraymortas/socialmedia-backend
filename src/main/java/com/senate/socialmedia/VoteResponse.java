package com.senate.socialmedia;

public class VoteResponse {
    private int score;
    private String userVote;

    public VoteResponse(int score, String userVote) {
        this.score = score;
        this.userVote = userVote;
    }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getUserVote() { return userVote; }
    public void setUserVote(String userVote) { this.userVote = userVote; }
}