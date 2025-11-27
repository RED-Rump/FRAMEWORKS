/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trivia.server.game;

import com.trivia.server.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private static GameManager instance;
    private Map<String, GameSession> activeSessions;
    private Map<String, GameSession> waitingRooms;
    private QuestionManager questionManager;
    private int defaultQuestionsPerGame = 10;
    private int defaultTimePerQuestion = 30; // seconds
    private int defaultMaxPlayers = 6;

    private GameManager() {
        this.activeSessions = new ConcurrentHashMap<>();
        this.waitingRooms = new ConcurrentHashMap<>();
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void setQuestionManager(QuestionManager questionManager) {
        this.questionManager = questionManager;
    }

    // Room Management
    public synchronized GameSession createRoom(String roomName, String creatorId, Player creator) {
        String sessionId = UUID.randomUUID().toString();
        GameSession session = new GameSession(sessionId, roomName, 
                                             defaultMaxPlayers, defaultTimePerQuestion);
        session.addPlayer(creator);
        waitingRooms.put(sessionId, session);
        
        System.out.println("Room created: " + roomName + " (ID: " + sessionId + ")");
        return session;
    }

    public synchronized boolean joinRoom(String sessionId, Player player) {
        GameSession session = waitingRooms.get(sessionId);
        if (session == null) {
            System.out.println("Room not found: " + sessionId);
            return false;
        }

        if (session.isGameFull()) {
            System.out.println("Room is full: " + sessionId);
            return false;
        }

        boolean added = session.addPlayer(player);
        if (added) {
            System.out.println("Player " + player.getUsername() + " joined room " + sessionId);
        }
        return added;
    }

    public synchronized void leaveRoom(String sessionId, String playerId) {
        GameSession session = waitingRooms.get(sessionId);
        if (session != null) {
            session.removePlayer(playerId);
            
            // If room is empty, remove it
            if (session.getPlayers().isEmpty()) {
                waitingRooms.remove(sessionId);
                System.out.println("Room " + sessionId + " removed (empty)");
            }
        }
    }

    public List<Map<String, Object>> getAvailableRooms() {
        List<Map<String, Object>> rooms = new ArrayList<>();
        
        for (GameSession session : waitingRooms.values()) {
            if (session.getCurrentState() == GameSession.GameState.WAITING) {
                Map<String, Object> roomInfo = new HashMap<>();
                roomInfo.put("sessionId", session.getSessionId());
                roomInfo.put("roomName", session.getRoomName());
                roomInfo.put("players", session.getPlayers().size());
                roomInfo.put("maxPlayers", defaultMaxPlayers);
                roomInfo.put("isFull", session.isGameFull());
                rooms.add(roomInfo);
            }
        }
        
        return rooms;
    }

    // Game Flow
    public synchronized boolean startGame(String sessionId) {
        GameSession session = waitingRooms.get(sessionId);
        
        if (session == null) {
            System.out.println("Session not found: " + sessionId);
            return false;
        }

        if (!session.canStart()) {
            System.out.println("Cannot start game - need at least 2 players");
            return false;
        }

        // Get random questions
        List<Question> questions = questionManager.getRandomQuestions(defaultQuestionsPerGame);
        
        if (questions.isEmpty()) {
            System.out.println("No questions available");
            return false;
        }

        // Move from waiting to active
        waitingRooms.remove(sessionId);
        activeSessions.put(sessionId, session);

        // Start the game
        session.startGame(questions);
        
        // Display first question
        session.displayNextQuestion();

        System.out.println("Game started: " + sessionId);
        return true;
    }

    public boolean submitAnswer(String sessionId, String playerId, String answer) {
        GameSession session = activeSessions.get(sessionId);
        if (session == null) {
            return false;
        }

        return session.submitAnswer(playerId, answer);
    }

    public Question getCurrentQuestion(String sessionId) {
        GameSession session = activeSessions.get(sessionId);
        if (session == null) {
            return null;
        }
        return session.getCurrentQuestion();
    }

    public Map<String, Integer> getCurrentScores(String sessionId) {
        GameSession session = activeSessions.get(sessionId);
        if (session == null) {
            return new HashMap<>();
        }
        return session.getPlayerScores();
    }

    public Map<String, Object> getFinalLeaderboard(String sessionId) {
        GameSession session = activeSessions.get(sessionId);
        if (session == null) {
            return new HashMap<>();
        }
        return session.getLeaderboardWithNames();
    }

    public void endGame(String sessionId) {
        GameSession session = activeSessions.remove(sessionId);
        if (session != null) {
            System.out.println("Game ended: " + sessionId);
        }
    }

    // Session Queries
    public GameSession getSession(String sessionId) {
        GameSession session = activeSessions.get(sessionId);
        if (session == null) {
            session = waitingRooms.get(sessionId);
        }
        return session;
    }

    public boolean isGameActive(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }

    public boolean isRoomWaiting(String sessionId) {
        return waitingRooms.containsKey(sessionId);
    }

    // Configuration
    public void setDefaultQuestionsPerGame(int count) {
        this.defaultQuestionsPerGame = count;
    }

    public void setDefaultTimePerQuestion(int seconds) {
        this.defaultTimePerQuestion = seconds;
    }

    public void setDefaultMaxPlayers(int max) {
        this.defaultMaxPlayers = max;
    }

    // Statistics
    public int getActiveGameCount() {
        return activeSessions.size();
    }

    public int getWaitingRoomCount() {
        return waitingRooms.size();
    }

    @Override
    public String toString() {
        return "GameManager{" +
                "activeGames=" + activeSessions.size() +
                ", waitingRooms=" + waitingRooms.size() +
                '}';
    }
}
