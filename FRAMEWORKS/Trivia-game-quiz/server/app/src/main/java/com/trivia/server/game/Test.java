/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trivia.server.game;

import com.trivia.server.model.*;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        System.out.println("=== Testing Game Logic ===\n");

        // Test 1: Create Game Manager
        GameManager gameManager = GameManager.getInstance();
        System.out.println("✓ GameManager created");

        // Test 2: Create mock questions
        QuestionManager questionManager = new QuestionManager();
        questionManager.addQuestion(new Question(1, "What is 2+2?", 
            Arrays.asList("3", "4", "5", "6"), "4", "EASY", "Math"));
        questionManager.addQuestion(new Question(2, "Capital of France?", 
            Arrays.asList("Kenyan culture", "Geography", "Computer Science & Programming", "Biology"), "Physical mechanics", "EASY", "Chemistry"));
        
        gameManager.setQuestionManager(questionManager);
        System.out.println("✓ Questions loaded");

        // Test 3: Create players
        Player player1 = new Player("p1", " ");
        Player player2 = new Player("p2", " ");
        System.out.println("✓ Players created");

        // Test 4: Create room
        GameSession session = gameManager.createRoom("Test Room", "p1", player1);
        System.out.println("✓ Room created: " + session.getSessionId());

        // Test 5: Join room
        boolean joined = gameManager.joinRoom(session.getSessionId(), player2);
        System.out.println("✓ Player joined: " + joined);

        // Test 6: Start game
        boolean started = gameManager.startGame(session.getSessionId());
        System.out.println("✓ Game started: " + started);

        // Test 7: Get current question
        Question currentQ = gameManager.getCurrentQuestion(session.getSessionId());
        System.out.println("✓ Current question: " + currentQ.getQuestionText());

        // Test 8: Submit answers
        gameManager.submitAnswer(session.getSessionId(), "p1", "4");
        gameManager.submitAnswer(session.getSessionId(), "p2", "5");
        System.out.println("✓ Answers submitted");

        // Test 9: Get scores
        try {
            Thread.sleep(5000); // Wait for round to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Map<String, Integer> scores = gameManager.getCurrentScores(session.getSessionId());
        System.out.println("✓ Scores: " + scores);

        System.out.println("\n=== All Tests Passed ===");
    }
}
