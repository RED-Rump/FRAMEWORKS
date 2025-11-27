/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trivia.server.model;

import java.time.LocalDateTime;

public class Answer {
    private String playerId;
    private String playerName;
    private int questionId;
    private String submittedAnswer;
    private boolean isCorrect;
    private long responseTime; // in milliseconds
    private LocalDateTime timestamp;
    private int pointsEarned;

    public Answer(String playerId, String playerName, int questionId, String submittedAnswer) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.questionId = questionId;
        this.submittedAnswer = submittedAnswer;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getSubmittedAnswer() { return submittedAnswer; }
    public void setSubmittedAnswer(String submittedAnswer) { this.submittedAnswer = submittedAnswer; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public long getResponseTime() { return responseTime; }
    public void setResponseTime(long responseTime) { this.responseTime = responseTime; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }

    @Override
    public String toString() {
        return "Answer{" +
                "playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", questionId=" + questionId +
                ", submittedAnswer='" + submittedAnswer + '\'' +
                ", isCorrect=" + isCorrect +
                ", responseTime=" + responseTime +
                ", pointsEarned=" + pointsEarned +
                '}';
    }
}


   

   
          