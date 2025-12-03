package com.example.TriviaClient.controllers;

import com.example.TriviaClient.network.*;
import com.example.TriviaClient.util.SceneManager;
import com.example.common.Message;
import com.example.common.MessageType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

public class GameController implements MessageHandler {
    @FXML private Label questionLabel;
    @FXML private Label timerLabel;
    @FXML private ProgressBar timerProgressBar;
    @FXML private Button submitButton;
    @FXML private VBox optionsContainer;
    
    private SceneManager sceneManager;
    private ClientNetworkManager networkManager;
    private ToggleGroup answerGroup = new ToggleGroup();
    private List<RadioButton> optionButtons = new ArrayList<>();
    
    @FXML
    private void initialize() {
        networkManager = ClientNetworkManager.getInstance();
        networkManager.setMessageHandler(this);
        
        // Setup radio buttons
        for (int i = 0; i < 4; i++) {
            RadioButton rb = new RadioButton();
            rb.setToggleGroup(answerGroup);
            rb.setUserData(i);
            optionButtons.add(rb);
            optionsContainer.getChildren().add(rb);
        }
        
        // Set sample question for testing
        setQuestion("What is the capital of France?", 
                   new String[]{"London", "Berlin", "Paris", "Madrid"});
    }
    
    private void setQuestion(String question, String[] options) {
        questionLabel.setText(question);
        for (int i = 0; i < 4 && i < options.length; i++) {
            optionButtons.get(i).setText(options[i]);
            optionButtons.get(i).setSelected(false);
        }
        answerGroup.selectToggle(null);
    }
    
    @FXML
    private void handleSubmitAnswer() {
        RadioButton selected = (RadioButton) answerGroup.getSelectedToggle();
        if (selected != null) {
            int answerIndex = (int) selected.getUserData();
            System.out.println("Submitted answer: " + answerIndex);
            
            // Send answer to server
            networkManager.sendMessage(MessageType.SUBMIT_ANSWER, answerIndex);
            
            // For testing - simulate moving to leaderboard
            if (sceneManager != null) {
                sceneManager.switchToScene("leaderboard");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Answer Selected");
            alert.setHeaderText("Please select an answer first!");
            alert.showAndWait();
        }
    }
    
    @Override
    public void handleMessage(Message message) {
        Platform.runLater(() -> {
            switch (message.getType()) {
                case NEW_QUESTION:  // CHANGED from QUESTION to NEW_QUESTION
                    System.out.println("New question received: " + message.getContent());
                    // TODO: Parse question data and update UI
                    break;
                case ROUND_RESULT:  // CHANGED from ANSWER_RESULT to ROUND_RESULT
                    System.out.println("Round result: " + message.getContent());
                    break;
                case GAME_OVER:
                    System.out.println("Game over!");
                    if (sceneManager != null) {
                        sceneManager.switchToScene("leaderboard");
                    }
                    break;
            }
        });
    }
    
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}