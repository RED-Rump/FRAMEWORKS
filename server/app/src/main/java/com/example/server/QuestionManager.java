/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.server;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
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
        try (FileReader reader = new FileReader("questions.json")) {
            Gson gson = new Gson();
            Type questionListType = new TypeToken<ArrayList<Question>>(){}.getType();
            questions = gson.fromJson(reader, questionListType);
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback questions if file fails
            questions = new ArrayList<>();
        }
    }
    
    public List<Question> getQuestions() { return questions; }
    
    // Internal class for Question structure
    public class Question {
        String question;
        List<String> options;
        String correctAnswer;
    }
}