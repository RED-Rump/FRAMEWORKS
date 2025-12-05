package com.example.server;

import com.example.common.Message;
import com.example.common.MessageType;
import com.example.server.QuestionManager.Question;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket server for HTML/JavaScript clients
 * Runs on port 8081 (changed from 8080 due to conflict)
 */
public class TriviaWebSocketServer extends WebSocketServer {
    
    private static final int WS_PORT = 8081;
    private final Map<WebSocket, String> clientUsernames = new ConcurrentHashMap<>();
    private final Map<String, GameRoom> gameRooms = new ConcurrentHashMap<>();
    private final Map<WebSocket, String> playerRooms = new ConcurrentHashMap<>(); // player -> roomId
    private final Gson gson = new Gson();
    private final QuestionManager questionManager;
    
    public TriviaWebSocketServer(QuestionManager questionManager) {
        super(new InetSocketAddress(WS_PORT));
        this.questionManager = questionManager;
    }
    
    public TriviaWebSocketServer(int port, QuestionManager questionManager) {
        super(new InetSocketAddress(port));
        this.questionManager = questionManager;
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("🌐 New WebSocket client connected: " + conn.getRemoteSocketAddress());
        System.out.println("📊 Active WebSocket clients: " + (getConnections().size()));
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String username = clientUsernames.remove(conn);
        System.out.println("👋 WebSocket client disconnected: " + (username != null ? username : "Unknown"));
        System.out.println("📊 Active WebSocket clients: " + getConnections().size());
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            // Parse JSON message from HTML client
            Message msg = gson.fromJson(message, Message.class);
            
            System.out.println("📨 Received from WebSocket: " + msg.getType());
            
            // Handle different message types
            switch (msg.getType()) {
                case LOGIN_REQUEST:
                    handleLogin(conn, msg);
                    break;
                    
                case CREATE_ROOM:
                    handleCreateRoom(conn, msg);
                    break;
                    
                case JOIN_ROOM:
                    handleJoinRoom(conn, msg);
                    break;
                    
                case SUBMIT_ANSWER:
                    handleSubmitAnswer(conn, msg);
                    break;
                    
                default:
                    System.out.println("Unhandled message type: " + msg.getType());
            }
            
        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
            e.printStackTrace();
            sendError(conn, "Invalid message format");
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }
    
    @Override
    public void onStart() {
        System.out.println("🚀 WebSocket server started on port " + WS_PORT);
        System.out.println("✅ HTML clients can connect to ws://localhost:" + WS_PORT);
        System.out.println("⚠️  Update web client to use port " + WS_PORT);
        setConnectionLostTimeout(100);
    }
    
    // Message handlers
    private void handleLogin(WebSocket conn, Message msg) {
        String username = msg.getContent() != null ? msg.getContent().toString() : msg.getSenderId();
        
        if (username == null || username.trim().isEmpty()) {
            sendError(conn, "Username is required");
            return;
        }
        
        clientUsernames.put(conn, username);
        
        Message response = new Message(MessageType.LOGIN_SUCCESS, "Welcome " + username + "!");
        response.setSenderId("SERVER");
        sendMessage(conn, response);
        
        System.out.println("✅ WebSocket user logged in: " + username);
    }
    
    private void handleCreateRoom(WebSocket conn, Message msg) {
        String username = clientUsernames.get(conn);
        String roomName = msg.getContent() != null ? msg.getContent().toString() : "Game Room";
        
        // Create new room
        String roomId = UUID.randomUUID().toString().substring(0, 8);
        GameRoom room = new GameRoom(roomId, roomName, username, questionManager.getQuestions());
        room.addPlayer(conn, username);
        
        gameRooms.put(roomId, room);
        playerRooms.put(conn, roomId);
        
        System.out.println("✅ Room created: " + roomName + " by " + username);
        
        // Send success to creator
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("roomId", roomId);
        roomData.put("roomName", roomName);
        roomData.put("playerCount", 1);
        
        Message response = new Message(MessageType.ROOM_LIST_UPDATE, roomData);
        response.setSenderId("SERVER");
        sendMessage(conn, response);
        
        // Auto-start game for single player or wait for others
        if (room.canStart()) {
            scheduleGameStart(room, 5000); // Start after 5 seconds
        }
    }
    
    private void handleJoinRoom(WebSocket conn, Message msg) {
        String username = clientUsernames.get(conn);
        String roomId = msg.getContent() != null ? msg.getContent().toString() : null;
        
        if (roomId == null || !gameRooms.containsKey(roomId)) {
            sendError(conn, "Room not found");
            return;
        }
        
        GameRoom room = gameRooms.get(roomId);
        if (room.addPlayer(conn, username)) {
            playerRooms.put(conn, roomId);
            
            System.out.println("✅ " + username + " joined room: " + room.getRoomName());
            
            // Notify player
            Map<String, Object> roomData = new HashMap<>();
            roomData.put("roomId", roomId);
            roomData.put("roomName", room.getRoomName());
            roomData.put("playerCount", room.getPlayerCount());
            
            Message response = new Message(MessageType.ROOM_LIST_UPDATE, roomData);
            response.setSenderId("SERVER");
            sendMessage(conn, response);
            
            // Notify all players in room
            broadcastToRoom(room, new Message(MessageType.ROOM_LIST_UPDATE, 
                "Player joined: " + username + " (" + room.getPlayerCount() + " players)"));
        } else {
            sendError(conn, "Cannot join room (full or game started)");
        }
    }
    
    private void scheduleGameStart(GameRoom room, long delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                startGame(room);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private void startGame(GameRoom room) {
        room.startGame();
        System.out.println("🎮 Starting game in room: " + room.getRoomName());
        
        // Notify all players
        Message startMsg = new Message(MessageType.GAME_START, "Game starting!");
        startMsg.setSenderId("SERVER");
        broadcastToRoom(room, startMsg);
        
        // Send first question after a delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                sendNextQuestion(room);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private void sendNextQuestion(GameRoom room) {
        QuestionManager.Question question = room.getCurrentQuestion();
        
        if (question == null) {
            endGame(room);
            return;
        }
        
        System.out.println("📝 Sending question " + room.getCurrentQuestionNumber() + "/" + room.getTotalQuestions());
        
        // Prepare question data (without correct answer!)
        Map<String, Object> questionData = new HashMap<>();
        questionData.put("questionNumber", room.getCurrentQuestionNumber());
        questionData.put("totalQuestions", room.getTotalQuestions());
        questionData.put("question", question.getQuestion());
        questionData.put("options", question.getOptions());
        questionData.put("category", question.getCategory());
        
        Message questionMsg = new Message(MessageType.NEW_QUESTION, questionData);
        questionMsg.setSenderId("SERVER");
        broadcastToRoom(room, questionMsg);
    }
    
    private void handleSubmitAnswer(WebSocket conn, Message msg) {
        String username = clientUsernames.get(conn);
        String roomId = playerRooms.get(conn);
        
        if (roomId == null || !gameRooms.containsKey(roomId)) {
            sendError(conn, "Not in a game room");
            return;
        }
        
        GameRoom room = gameRooms.get(roomId);
        
        try {
            int answerIndex = Integer.parseInt(msg.getContent().toString());
            room.submitAnswer(conn, username, answerIndex);
            
            System.out.println("✅ Answer from " + username + ": " + answerIndex);
            
            // Send confirmation
            Message confirm = new Message(MessageType.ROUND_RESULT, "Answer submitted!");
            confirm.setSenderId("SERVER");
            sendMessage(conn, confirm);
            
            // Check if all players answered
            if (room.allPlayersAnswered()) {
                processRoundResults(room);
            }
            
        } catch (Exception e) {
            sendError(conn, "Invalid answer format");
        }
    }
    
    private void processRoundResults(GameRoom room) {
        System.out.println("⏱️ Processing round results...");
        
        // Check answers
        Map<String, Boolean> results = room.checkAnswers(clientUsernames);
        
        // Get correct answer index
        Question currentQuestion = room.getCurrentQuestion();
        int correctAnswerIndex = 0;
        if (currentQuestion != null) {
            String correctAnswer = currentQuestion.getCorrectAnswer();
            List<String> options = currentQuestion.getOptions();
            correctAnswerIndex = options.indexOf(correctAnswer);
        }
        
        // Prepare results data
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("results", results);
        resultData.put("scores", room.getScores());
        resultData.put("correctAnswer", correctAnswerIndex);
        resultData.put("questionNumber", room.getCurrentQuestionNumber());
        resultData.put("totalQuestions", 10);
        
        Message resultMsg = new Message(MessageType.ROUND_RESULT, resultData);
        resultMsg.setSenderId("SERVER");
        broadcastToRoom(room, resultMsg);
        
        // Move to next question after delay
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                room.nextQuestion();
                
                if (room.isGameOver()) {
                    endGame(room);
                } else {
                    sendNextQuestion(room);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private void endGame(GameRoom room) {
        System.out.println("🏁 Game over in room: " + room.getRoomName());
        
        Map<String, Object> gameOverData = new HashMap<>();
        gameOverData.put("finalScores", room.getFinalScores());
        gameOverData.put("winner", getWinner(room.getFinalScores()));
        
        Message gameOverMsg = new Message(MessageType.GAME_OVER, gameOverData);
        gameOverMsg.setSenderId("SERVER");
        broadcastToRoom(room, gameOverMsg);
        
        // Clean up room after delay
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                gameRooms.remove(room.getRoomId());
                System.out.println("🗑️ Removed room: " + room.getRoomName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private String getWinner(Map<String, Integer> scores) {
        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No winner");
    }
    
    private void broadcastToRoom(GameRoom room, Message message) {
        for (WebSocket player : room.getPlayers()) {
            sendMessage(player, message);
        }
    }
    
    // Send message to specific client
    public void sendMessage(WebSocket conn, Message message) {
        try {
            String json = gson.toJson(message);
            conn.send(json);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    // Broadcast to all WebSocket clients
    public void broadcastMessage(Message message) {
        String json = gson.toJson(message);
        broadcast(json);
        System.out.println("📢 Broadcast to " + getConnections().size() + " WebSocket clients");
    }
    
    // Send error message
    private void sendError(WebSocket conn, String errorMessage) {
        Message error = new Message(MessageType.ERROR, errorMessage);
        error.setSenderId("SERVER");
        sendMessage(conn, error);
    }
    
    public int getClientCount() {
        return getConnections().size();
    }
}
