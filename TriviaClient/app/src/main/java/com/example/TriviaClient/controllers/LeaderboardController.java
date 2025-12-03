package com.example.TriviaClient.controllers;

import com.example.TriviaClient.network.*;
import com.example.TriviaClient.util.SceneManager;
import com.example.common.Message;
import com.example.common.MessageType;
import javafx.application.Platform;  // ADD THIS IMPORT
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LeaderboardController implements MessageHandler {
    @FXML private TableView<PlayerScore> leaderboardTable;
    @FXML private TableColumn<PlayerScore, Integer> rankColumn;
    @FXML private TableColumn<PlayerScore, String> usernameColumn;
    @FXML private TableColumn<PlayerScore, Integer> scoreColumn;
    @FXML private Button backToLobbyButton;
    
    private SceneManager sceneManager;
    private ClientNetworkManager networkManager;
    private ObservableList<PlayerScore> scores = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() {
        networkManager = ClientNetworkManager.getInstance();
        networkManager.setMessageHandler(this);
        
        // Setup table
        leaderboardTable.setItems(scores);
        
        rankColumn.setCellValueFactory(cellData -> 
            cellData.getValue().rankProperty().asObject());
        usernameColumn.setCellValueFactory(cellData -> 
            cellData.getValue().usernameProperty());
        scoreColumn.setCellValueFactory(cellData -> 
            cellData.getValue().scoreProperty().asObject());
        
        // Load sample data for now
        loadSampleData();
    }
    
    @FXML
    private void handleBackToLobby() {
        if (sceneManager != null) {
            sceneManager.switchToScene("lobby");
        }
    }
    
    @Override
    public void handleMessage(Message message) {
        Platform.runLater(() -> {
            // TODO: Handle actual leaderboard data
            // For now, just load sample data
            if (message.getType() == MessageType.GAME_OVER) {
                // Load leaderboard after game ends
                loadSampleData();
            }
        });
    }
    
    private void loadSampleData() {
        scores.clear();
        scores.addAll(
            new PlayerScore(1, "Player1", 1000),
            new PlayerScore(2, "Player2", 800),
            new PlayerScore(3, "Player3", 600),
            new PlayerScore(4, "Player4", 400),
            new PlayerScore(5, "Player5", 200)
        );
    }
    
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    public static class PlayerScore {
        private final javafx.beans.property.IntegerProperty rank;
        private final javafx.beans.property.StringProperty username;
        private final javafx.beans.property.IntegerProperty score;
        
        public PlayerScore(int rank, String username, int score) {
            this.rank = new javafx.beans.property.SimpleIntegerProperty(rank);
            this.username = new javafx.beans.property.SimpleStringProperty(username);
            this.score = new javafx.beans.property.SimpleIntegerProperty(score);
        }
        
        public javafx.beans.property.IntegerProperty rankProperty() { return rank; }
        public javafx.beans.property.StringProperty usernameProperty() { return username; }
        public javafx.beans.property.IntegerProperty scoreProperty() { return score; }
        
        public int getRank() { return rank.get(); }
        public String getUsername() { return username.get(); }
        public int getScore() { return score.get(); }
    }
}