/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.server;

import com.example.common.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServerNetworkManager server;
    private String username;

    public ClientHandler(Socket socket, ServerNetworkManager server) {
        this.socket = socket;
        this.server = server;
    }

   
    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Object input;
            while ((input = in.readObject()) != null) {
                if (input instanceof Message) {
                    MessageProcessor.process(this, (Message) input);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected.");
        } finally {
            server.removeClient(this);
        }
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public void setUsername(String username) { this.username = username; }
}