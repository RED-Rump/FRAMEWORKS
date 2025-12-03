/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.server;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ffabd
 */


public class QuestionManager {
   


    private List<Question> questions;

    public QuestionManager() {
        loadQuestions();
    }

    private void loadQuestions() {
        // This is the FIX: Load from the "Resources" folder (Classpath)
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("questions.json")) {
            
            if (inputStream == null) {
                System.out.println("ERROR: Could not find questions.json in Resources!");
                questions = new ArrayList<>();
                return;
            }

            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type questionListType = new TypeToken<ArrayList<Question>>(){}.getType();
            questions = gson.fromJson(reader, questionListType);
            
        } catch (IOException e) {
            e.printStackTrace();
            questions = new ArrayList<>();
        }
    }
    
    public List<Question> getQuestions() { return questions; }
    
    // Internal class for Question structure
    public class Question {
        String category;
        String question;
        List<String> options;
        String correctAnswer;
        
        public String getCategory() { return category; }
        public String getQuestion() { return question; }
        public List<String> getOptions() { return options; }
        public String getCorrectAnswer() { return correctAnswer; }
    }
}
