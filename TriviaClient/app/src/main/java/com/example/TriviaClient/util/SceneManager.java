package com.example.TriviaClient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private Stage primaryStage;
    private Map<String, Parent> scenes = new HashMap<>();
    private Map<String, Object> controllers = new HashMap<>();
    
    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void loadScene(String sceneName, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();
            
            scenes.put(sceneName, root);
            controllers.put(sceneName, controller);
            
            // Pass SceneManager to controller
            try {
                controller.getClass().getMethod("setSceneManager", SceneManager.class)
                          .invoke(controller, this);
            } catch (Exception e) {
                // Controller doesn't have setSceneManager - that's OK
            }
            
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
    
    public void switchToScene(String sceneName) {
        Parent root = scenes.get(sceneName);
        if (root != null) {
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
            primaryStage.show();
        }
    }
    
    public Object getController(String sceneName) {
        return controllers.get(sceneName);
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}