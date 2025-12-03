package com.example.TriviaClient.controllers;

import com.example.TriviaClient.models.Room;
import com.example.TriviaClient.network.*;
import com.example.TriviaClient.util.SceneManager;
import com.example.common.Message;
import com.example.common.MessageType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;

public class LobbyController implements MessageHandler {
    @FXML private Label usernameLabel;
    @FXML private Button refreshButton;
    @FXML private Button createButton;
    @FXML private Button joinButton;
    
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomIdColumn;
    @FXML private TableColumn<Room, String> roomNameColumn;
    @FXML private TableColumn<Room, Integer> playersColumn;
    @FXML private TableColumn<Room, String> statusColumn;
    
    private ObservableList<Room> roomsList = FXCollections.observableArrayList();
    private SceneManager sceneManager;
    private ClientNetworkManager networkManager;
    
    @FXML
    private void initialize() {
        System.out.println("✓ LobbyController.initialize() called!");
        
        networkManager = ClientNetworkManager.getInstance();
        networkManager.setMessageHandler(this);
        
        System.out.println("✓ Rooms Table: " + (roomsTable != null ? "✓ Found" : "✗ NULL"));
        
        setupTableView();
        
        if (networkManager.getUsername() != null) {
            usernameLabel.setText("Player: " + networkManager.getUsername());
        }
        
        loadDummyData();
    }
    
    private void setupTableView() {
        if (roomIdColumn != null && roomNameColumn != null && 
            playersColumn != null && statusColumn != null) {
            
            roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
            roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("roomName"));
            playersColumn.setCellValueFactory(new PropertyValueFactory<>("players"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            
            roomsTable.setItems(roomsList);
            
            System.out.println("✓ TableView setup complete!");
        } else {
            System.out.println("✗ ERROR: TableColumns are null!");
        }
    }
    
    @FXML
    private void handleRefresh() {
        System.out.println("🔄 Refresh button clicked!");
        loadDummyData();
        
        // Note: MessageType doesn't have LIST_ROOMS
        // You need to add ROOM_LIST_UPDATE to MessageType
        // For now, just use dummy data
        // networkManager.sendMessage(MessageType.ROOM_LIST_UPDATE, null);
    }
    
    @FXML
    private void handleCreate() {
        TextInputDialog dialog = new TextInputDialog("My Game Room");
        dialog.setTitle("Create Room");
        dialog.setHeaderText("Enter room name:");
        dialog.setContentText("Room Name:");
        
        dialog.showAndWait().ifPresent(roomName -> {
            if (!roomName.trim().isEmpty()) {
                System.out.println("Creating room: " + roomName);
                
                String newRoomId = String.format("%03d", roomsList.size() + 1);
                roomsList.add(new Room(newRoomId, roomName, 1, "Waiting"));
                
                // Send CREATE_ROOM message to server
                networkManager.sendMessage(MessageType.CREATE_ROOM, roomName);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Room Created");
                alert.setHeaderText(null);
                alert.setContentText("Room '" + roomName + "' has been created!");
                alert.showAndWait();
            }
        });
    }
    
    @FXML
    private void handleJoin() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        
        if (selectedRoom != null) {
            System.out.println("🚪 Joining room: " + selectedRoom.getRoomName() + 
                             " (ID: " + selectedRoom.getRoomId() + ")");
            
            if ("In Game".equals(selectedRoom.getStatus())) {
                showAlert("Cannot Join", "This room is currently in a game.", Alert.AlertType.WARNING);
                return;
            }
            
            if (selectedRoom.getPlayers() >= 4) {
                showAlert("Room Full", "This room is full (4/4 players).", Alert.AlertType.WARNING);
                return;
            }
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Join Room");
            confirmAlert.setHeaderText("Join '" + selectedRoom.getRoomName() + "'?");
            confirmAlert.setContentText("Click OK to join the room.");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("✓ Joining room: " + selectedRoom.getRoomId());
                    
                    // Send JOIN_ROOM message to server
                    networkManager.sendMessage(MessageType.JOIN_ROOM, selectedRoom.getRoomId());
                }
            });
            
        } else {
            showAlert("No Room Selected", "Please select a room from the list first!", Alert.AlertType.WARNING);
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void loadDummyData() {
        System.out.println("📋 Loading dummy room data...");
        
        roomsList.clear();
        roomsList.addAll(
            new Room("001", "Trivia Masters", 2, "Waiting"),
            new Room("002", "Brain Busters", 1, "Waiting"),
            new Room("003", "Quiz Champions", 4, "In Game"),
            new Room("004", "Knowledge Seekers", 3, "Waiting"),
            new Room("005", "Quick Thinkers", 0, "Waiting")
        );
        
        System.out.println("✓ Loaded " + roomsList.size() + " rooms");
    }
    
    @Override
    public void handleMessage(Message message) {
        Platform.runLater(() -> {
            System.out.println("📨 Lobby received message: " + message.getType());
            
            switch (message.getType()) {
                case ROOM_LIST_UPDATE:
                    System.out.println("Room list update received: " + message.getContent());
                    // TODO: Update rooms list with actual data
                    break;
                    
                case GAME_START:  // When room is ready to start game
                    System.out.println("Game starting, switching to game...");
                    if (sceneManager != null) {
                        sceneManager.switchToScene("game");
                    }
                    break;
                    
                case ERROR:
                    showAlert("Error", message.getContent().toString(), Alert.AlertType.ERROR);
                    break;
                    
                default:
                    System.out.println("Unhandled message type: " + message.getType());
            }
        });
    }
    
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}