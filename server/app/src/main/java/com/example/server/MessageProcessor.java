/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.server;

import com.example.common.Message;
import com.example.common.MessageType;


/**
 *
 * @author ffabd
 */


public class MessageProcessor {
    public static void process(ClientHandler client, Message msg) {
        switch (msg.getType()) {
            case LOGIN_REQUEST:
                client.setUsername((String) msg.getContent());
                System.out.println("User logged in: " + msg.getContent());
                client.sendMessage(new Message(MessageType.LOGIN_SUCCESS, "Welcome!"));
                break;
            // Member 2 will add GAME_START, SUBMIT_ANSWER logic here later
            default:
                System.out.println("Unknown command");
        }
    }

 }