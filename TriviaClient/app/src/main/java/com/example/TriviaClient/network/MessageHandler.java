package com.example.TriviaClient.network;

import com.example.common.Message;

public interface MessageHandler {
    void handleMessage(Message message);
}