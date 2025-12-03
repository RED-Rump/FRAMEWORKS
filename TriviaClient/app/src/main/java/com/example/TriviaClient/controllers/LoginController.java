package com.example.TriviaClient.controllers;

import com.example.common.Message;
import com.example.common.MessageType;
import com.example.TriviaClient.network.ClientNetworkManager;
import com.example.TriviaClient.network.MessageHandler;
import com.example.TriviaClient.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController implements MessageHandler {
    @FXML private TextField usernameField;
    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;
    
    private SceneManager sceneManager;
    private ClientNetworkManager networkManager;
    
    private static final boolean USE_REAL_SERVER = false;
    
    @FXML
    private void initialize() {
        System.out.println("LoginController initialized");
        
        networkManager = ClientNetworkManager.getInstance();
        networkManager.setMessageHandler(this);
        
        hostField.setText("localhost");
        portField.setText("12345");
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String host = hostField.getText().trim();
        String portText = portField.getText().trim();
        
        if (username.isEmpty()) {
            showError("Please enter a username");
            return;
        }
        
        if (host.isEmpty()) {
            showError("Please enter server host");
            return;
        }
        
        int port;
        try {
            port = Integer.parseInt(portText);
            if (port < 1 || port > 65535) {
                showError("Port must be between 1-65535");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid port number");
            return;
        }
        
        statusLabel.setText("Connecting to server...");
        statusLabel.setStyle("-fx-text-fill: #f39c12;");
        loginButton.setDisable(true);
        
        if (USE_REAL_SERVER) {
            connectToRealServer(username, host, port);
        } else {
            simulateConnection(username, host, port);
        }
    }
    
    private void connectToRealServer(String username, String host, int port) {
        new Thread(() -> {
            boolean connected = networkManager.connect(host, port, username);
            
            Platform.runLater(() -> {
                if (connected) {
                    statusLabel.setText("Connected! Logging in...");
                    statusLabel.setStyle("-fx-text-fill: #27ae60;");
                } else {
                    statusLabel.setText("Connection failed");
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    loginButton.setDisable(false);
                }
            });
        }).start();
    }
    
    private void simulateConnection(String username, String host, int port) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                
                Platform.runLater(() -> {
                    statusLabel.setText("Connected to server");
                    statusLabel.setStyle("-fx-text-fill: #27ae60;");
                    
                    if (sceneManager != null) {
                        sceneManager.switchToScene("lobby");
                    }
                });
                
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Connection interrupted");
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    loginButton.setDisable(false);
                });
            }
        }).start();
    }
    
    @Override
    public void handleMessage(Message message) {
        Platform.runLater(() -> {
            System.out.println("LoginController received: " + message.getType());
            
            switch (message.getType()) {
                case LOGIN_SUCCESS:
                    handleLoginSuccess(message);
                    break;
                    
                case LOGIN_FAILURE:
                    handleLoginFailure(message);
                    break;
                    
                default:
                    System.out.println("Unhandled message: " + message.getType());
            }
        });
    }
    
    private void handleLoginSuccess(Message message) {
        String welcomeMessage = "Login successful!";
        if (message.getContent() != null) {
            welcomeMessage += " " + message.getContent();
        }
        
        statusLabel.setText(welcomeMessage);
        statusLabel.setStyle("-fx-text-fill: #2ecc71;");
        
        // Switch to lobby
        if (sceneManager != null) {
            sceneManager.switchToScene("lobby");
        }
    }
    
    private void handleLoginFailure(Message message) {
        String errorMessage = "Login failed";
        if (message.getContent() != null) {
            errorMessage += ": " + message.getContent();
        }
        
        statusLabel.setText(errorMessage);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        loginButton.setDisable(false);
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    if (statusLabel.getText().equals(message)) {
                        statusLabel.setText("");
                    }
                });
            } catch (InterruptedException e) {
                // Ignore
            }
        }).start();
    }
    
    @FXML
    private void handleEnterKey() {
        handleLogin();
    }
    
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}