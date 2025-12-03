package com.example.TriviaClient;

import com.example.TriviaClient.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    
    private SceneManager sceneManager;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🎮 Trivia Game Client");
        primaryStage.setWidth(900);
        primaryStage.setHeight(650);
        
        // Initialize SceneManager
        sceneManager = new SceneManager(primaryStage);
        
        // Load all scenes from ui/ folder
        sceneManager.loadScene("login", "/ui/login.fxml");
        sceneManager.loadScene("lobby", "/ui/lobby.fxml");
        sceneManager.loadScene("game", "/ui/game.fxml");
        sceneManager.loadScene("leaderboard", "/ui/leaderboard.fxml");
        
        // Start with login screen
        sceneManager.switchToScene("login");
        
        // Handle window close
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("👋 Application closing...");
            // Clean up network connections
            System.exit(0);
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}