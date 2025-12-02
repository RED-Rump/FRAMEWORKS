/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Server;


public class ScoreCalculator {
    private static final int BASE_POINTS = 100;
    private static final int MAX_SPEED_BONUS = 50;
    private static final double SPEED_BONUS_THRESHOLD = 0.5; // 50% of time remaining gets full bonus

    /**
     * Calculate score based on response time
     * @param responseTime Time taken to answer in milliseconds
     * @param totalTime Total time allowed in milliseconds
     * @return Points earned
     */
     public int calculateScore(long responseTime, long totalTime) {
        if (responseTime > totalTime) {
            return 0; // Answered too late
        }

        // Base points for correct answer
        int points = BASE_POINTS;

        // Calculate speed bonus
        double timeRatio = (double) responseTime / totalTime;
        
         double timeRatio = (double) responseTime / totalTime;
        
        if (timeRatio <= SPEED_BONUS_THRESHOLD) {
            // Full speed bonus for answering in first 50% of time
            points += MAX_SPEED_BONUS;
        } else {
            // Proportional speed bonus for remaining time
            double remainingRatio = (1.0 - timeRatio) / (1.0 - SPEED_BONUS_THRESHOLD);
            points += (int) (MAX_SPEED_BONUS * remainingRatio);
        }

        return points;
    }
     /**
     * Calculate score with difficulty multiplier
     */
    public int calculateScoreWithDifficulty(long responseTime, long totalTime, String difficulty) {
        int baseScore = calculateScore(responseTime, totalTime);
        
        switch (difficulty.toUpperCase()) {
            case "EASY":
                return baseScore;
            case "MEDIUM":
                return (int) (baseScore * 1.5);
            case "HARD":
                return baseScore * 2;
            default:
                return baseScore;
        }
    }
     /**
     * Calculate maximum possible score
     */
    public int getMaxScore() {
        return BASE_POINTS + MAX_SPEED_BONUS;
    }
   }

