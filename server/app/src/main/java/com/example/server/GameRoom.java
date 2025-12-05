package com.example.server;

import com.example.server.QuestionManager.Question;
import org.java_websocket.WebSocket;
import java.util.*;
import java.util.concurrent.*;

/**
 * Manages a game room with players, questions, and scoring
 */
public class GameRoom {
    private final String roomId;
    private final String roomName;
    private final String hostUsername;
    private final List<WebSocket> players;
    private final Map<String, Integer> scores;
    private final List<Question> questions;
    private int currentQuestionIndex;
    private final Map<WebSocket, Integer> currentAnswers;
    private Question currentQuestion;
    private boolean gameStarted;
    private final int maxPlayers;
    private final int questionsPerGame;
    
    public GameRoom(String roomId, String roomName, String hostUsername, List<Question> allQuestions) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.hostUsername = hostUsername;
        this.players = new CopyOnWriteArrayList<>();
        this.scores = new ConcurrentHashMap<>();
        this.currentAnswers = new ConcurrentHashMap<>();
        this.currentQuestionIndex = 0;
        this.gameStarted = false;
        this.maxPlayers = 6;
        this.questionsPerGame = 10;
        
        // Select random questions
        this.questions = selectRandomQuestions(allQuestions, questionsPerGame);
    }
    
    private List<Question> selectRandomQuestions(List<Question> allQuestions, int count) {
        List<Question> selected = new ArrayList<>();
        List<Question> copy = new ArrayList<>(allQuestions);
        Collections.shuffle(copy);
        
        int actualCount = Math.min(count, copy.size());
        for (int i = 0; i < actualCount; i++) {
            selected.add(copy.get(i));
        }
        return selected;
    }
    
    public boolean addPlayer(WebSocket conn, String username) {
        if (gameStarted) {
            return false; // Can't join game in progress
        }
        if (players.size() >= maxPlayers) {
            return false; // Room is full
        }
        players.add(conn);
        scores.put(username, 0);
        return true;
    }
    
    public void removePlayer(WebSocket conn) {
        players.remove(conn);
    }
    
    public boolean canStart() {
        return players.size() >= 1 && !gameStarted; // At least 1 player
    }
    
    public void startGame() {
        gameStarted = true;
        currentQuestionIndex = 0;
    }
    
    public Question getCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            currentQuestion = questions.get(currentQuestionIndex);
            return currentQuestion;
        }
        return null;
    }
    
    public void nextQuestion() {
        currentQuestionIndex++;
        currentAnswers.clear();
    }
    
    public void submitAnswer(WebSocket conn, String username, int answerIndex) {
        currentAnswers.put(conn, answerIndex);
    }
    
    public boolean allPlayersAnswered() {
        return currentAnswers.size() >= players.size();
    }
    
    public Map<String, Boolean> checkAnswers(Map<WebSocket, String> usernames) {
        Map<String, Boolean> results = new HashMap<>();
        
        if (currentQuestion == null) {
            return results;
        }
        
        String correctAnswer = currentQuestion.getCorrectAnswer();
        List<String> options = currentQuestion.getOptions();
        int correctIndex = options.indexOf(correctAnswer);
        
        for (Map.Entry<WebSocket, Integer> entry : currentAnswers.entrySet()) {
            String username = usernames.get(entry.getKey());
            boolean isCorrect = entry.getValue() == correctIndex;
            results.put(username, isCorrect);
            
            if (isCorrect) {
                scores.put(username, scores.getOrDefault(username, 0) + 10);
            }
        }
        
        return results;
    }
    
    public boolean isGameOver() {
        return currentQuestionIndex >= questions.size();
    }
    
    public Map<String, Integer> getFinalScores() {
        return new HashMap<>(scores);
    }
    
    // Getters
    public String getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public String getHostUsername() { return hostUsername; }
    public List<WebSocket> getPlayers() { return new ArrayList<>(players); }
    public int getPlayerCount() { return players.size(); }
    public boolean isGameStarted() { return gameStarted; }
    public int getCurrentQuestionNumber() { return currentQuestionIndex + 1; }
    public int getTotalQuestions() { return questions.size(); }
    public Map<String, Integer> getScores() { return new HashMap<>(scores); }
}
