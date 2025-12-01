/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.common;

import java.io.Serializable;

/**
 *
 * @author ffabd
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private MessageType type;
    private Object content;
    private String senderId;

    public Message(MessageType type, Object content) {
        this.type = type;
        this.content = content;
    }

    public MessageType getType() { return type; }
    public Object getContent() { return content; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
}
