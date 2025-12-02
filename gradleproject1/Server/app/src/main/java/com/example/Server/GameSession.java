/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Server;

import com.example.Server.Answer;
import java.util.*;
import java.util.concurrent.*;

public class GameSession {
    private String sessionId;
    private String roomName;
    private GameState currentState;
    private List<Players> players;
    private List<Question> question;
    private int currentquestionIndex;
    private Map<String, Integer> playerscores; //playerId -> total score
    private Map<String, Answer> currentRoundAnswers; // playerId -> Answer
    private long questionStartTime;
    private int timePerquestion; // in seconds
    private int maxPlayers;
    private ScheduleExecutorService scheduler;
    
    public enum GameState{
        WAITING,
        STARTING,
        QUESTION_DISPLAYED,
        WAITING_FOR_ANSWERS,
        ROUND_COMPLETE,
        GAME_ENDED
    }
    
    public GameSession(String sessionId, roomName, int maxPlayers, int timePerQuestion){
        this.sessionId = sessionId;
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.timePerquestion = timePerquestion;
        this.currentState = GameState.WAITING;
        this.players = new CopyOnWriteArraylist<>();
        this.questions = new ArrayList<>();
        this.currentquestionIndex = 0;
        this.playerScores = new ConcurrentHashMap<>();
        this.currentroundAnswers = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduleThreadPool(1);  
    }
    
    // Player management
    public synchronized boolean addPlayer(Player player){
        if(players.size() >= maxPlayers){
            return false;
        }
        if (currentState != GameState.WAITING){
            return false; // Cant join game in progress
        }
        player.add(player);
        playerscores.put(player.getid(), 0);
        return true;
    }
    
    public synchronized void removePlayer(String playerId){
        players.removeIf(p-> p.getId().equals(playerId));
        playerscores.remove(playerId);
        currentRoundAnswers.remove(playerId);
        
    }
    
    //Game Flow methods
    public void startgame(List<Question> gameQuestions){
        this.questions = gameQuestions;
        this.currentquestionIndex = 0;
        this.currentState = GameState.STARTING;
        System.out.println("Game session " +sessionId + "starting with " + players.size() + "players");
    }
    
    public Question getCurrentQuestion(){
        if (currentquestionIndex < questions.size()){
            return questions.get(currentquestionIndex);
        }
        return null;
    }
    
    public void displayNextquestion(){
        if (currentquestionIndex < questions.size()){
            currentState = Gamestate.QUESTION_DISPLAYED;
            currentroundAnswers.clear();
            questionStartTime = System.currentTimeMillis();
            
            // Schedule automatic round end after time limit
            scheduler.scheduler(() -> {
                endCurrentround();
            }, timePerquestion, TimeUnit.SECONDS);
            
            System.out.println("displaying question "+ (currentQuestionIndex + 1)+ "of" + questions.size());
        } else{
            endGame();
        }
    }
    
    public synchronized boolean submitAnswer(String playerId, String answer){
        if (currentState != Gamestate.QUESTION_DISPLAYED && currentState != Gamestae.WAITING_FOR_ANSWERS) {
            return false;
        }
        
        if (currentRoundAnswers.containsKey(playerId)){
            return false;
        }
        
        Player player = getPlayerById(playerId);
        if (player == null){
            return false;
        }
        
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion == null) {
            return false;
        }
        
        long responseTime = System.currentTimeMillis() - questionStartTime;
        
        Answer playerAnswer = new Answer(playerId, player.getUsername(), 
                                        currentQuestion.getId(), answer);
        playerAnswer.setResponseTime(responseTime);
        
        // Check if answer is correct
        boolean isCorrect = currentQuestion.getCorrectAnswer().equalsIgnoreCase(answer);
        playerAnswer.setCorrect(isCorrect);

        currentRoundAnswers.put(playerId, playerAnswer);
        
        currentState = GameState.WAITING_FOR_ANSWERS;
        
         // If all players answered, end round immediately
        if (currentRoundAnswers.size() == players.size()) {
            scheduler.execute(() -> endCurrentRound());
        }

        return true;
    }
    
    private synchronized void endCurrentRound() {
        if (currentState == GameState.ROUND_COMPLETE) {
            return; // Already ended
        }

        currentState = GameState.ROUND_COMPLETE;
        
     // Calculate scores for this round
        ScoreCalculator scoreCalculator = new ScoreCalculator();
        for (Answer answer : currentRoundAnswers.values()) {
            if (answer.isCorrect()) {
                int points = scoreCalculator.calculateScore(answer.getResponseTime(), timePerQuestion * 1000);
                answer.setPointsEarned(points);
                
                // Update player's total score
                int currentScore = playerScores.getOrDefault(answer.getPlayerId(), 0);
                playerScores.put(answer.getPlayerId(), currentScore + points);
            } else {
                answer.setPointsEarned(0);
            }
        }
     System.out.println("Round " + (currentQuestionIndex + 1) + " completed");
        
        // Move to next question after a short delay
        scheduler.schedule(() -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayNextQuestion();
            } else {
                endGame();
            }
        }, 3, TimeUnit.SECONDS); // 3 second delay to show results
    }
    
    private void endGame() {
        currentState = GameState.GAME_ENDED;
        System.out.println("Game session " + sessionId + " ended");
        scheduler.shutdown();
    }
    
     // Leaderboard Methods
    public List<Map.Entry<String, Integer>> getLeaderboard() {
        List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>(playerScores.entrySet());
        leaderboard.sort((a, b) -> b.getValue().compareTo(a.getValue())); // Sort descending
        return leaderboard;
    }
    
    public Map<String, Object> getLeaderboardWithNames() {
        List<Map<String, Object>> rankings = new ArrayList<>();
        List<Map.Entry<String, Integer>> leaderboard = getLeaderboard();
        
        int rank = 1;
        for (Map.Entry<String, Integer> entry : leaderboard) {
            Player player = getPlayerById(entry.getKey());
            if (player != null) {
                Map<String, Object> playerRanking = new HashMap<>();
                playerRanking.put("rank", rank++);
                playerRanking.put("playerId", entry.getKey());
                playerRanking.put("playerName", player.getUsername());
                playerRanking.put("score", entry.getValue());
                rankings.add(playerRanking);
            }
        }
          Map<String, Object> result = new HashMap<>();
        result.put("rankings", rankings);
        result.put("totalQuestions", questions.size());
        return result;
    }
      public boolean isGameFull() {
        return players.size() >= maxPlayers;
    }
      public boolean canStart() {
        return players.size() >= 2 && currentState == GameState.WAITING;
    }
      // Getters
    public String getSessionId() { return sessionId; }
    public String getRoomName() { return roomName; }
    public GameState getCurrentState() { return currentState; }
    public List<Player> getPlayers() { return new ArrayList<>(players); }
    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public int getTotalQuestions() { return questions.size(); }
    public Map<String, Integer> getPlayerScores() { return new HashMap<>(playerScores); }
    public Map<String, Answer> getCurrentRoundAnswers() { return new HashMap<>(currentRoundAnswers); }
    public int getTimePerQuestion() { return timePerQuestion; } 
    
     @Override
    public String toString() {
        return "GameSession{" +
                "sessionId='" + sessionId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", state=" + currentState +
                ", players=" + players.size() +
                ", currentQuestion=" + (currentQuestionIndex + 1) +
                "/" + questions.size() +
                '}';
    }
    

// Handle player disconnection
public synchronized void handlePlayerDisconnect(String playerId) {
    removePlayer(playerId);
    
    // If too few players remain, end game
    if (players.size() < 2 && 
        (currentState == GameState.QUESTION_DISPLAYED || 
         currentState == GameState.WAITING_FOR_ANSWERS)) {
        System.out.println("Too few players remaining. Ending game.");
        endGame();
    }
}

// Handle timeout scenario
public synchronized void handleTimeout() {
    if (currentState == GameState.QUESTION_DISPLAYED || 
        currentState == GameState.WAITING_FOR_ANSWERS) {
        System.out.println("Time's up for question " + (currentQuestionIndex + 1));
        endCurrentRound();
    }
}

// Pause game (if needed)
public synchronized void pauseGame() {
    if (currentState == GameState.QUESTION_DISPLAYED) {
        scheduler.shutdownNow();
        currentState = GameState.WAITING;
        System.out.println("Game paused");
    }
}

// Resume game
public synchronized void resumeGame() {
    if (currentState == GameState.WAITING) {
        scheduler = Executors.newScheduledThreadPool(1);
        displayNextQuestion();
        System.out.println("Game resumed");
    }
}
}
  



