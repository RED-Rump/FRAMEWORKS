package com.example.TriviaClient.models;

public class Room {
    private String roomId;
    private String roomName;
    private int players;
    private String status;
    
    // Default constructor
    public Room() {}
    
    // Constructor with parameters
    public Room(String roomId, String roomName, int players, String status) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.players = players;
        this.status = status;
    }
    
    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    public int getPlayers() { return players; }
    public void setPlayers(int players) { this.players = players; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}