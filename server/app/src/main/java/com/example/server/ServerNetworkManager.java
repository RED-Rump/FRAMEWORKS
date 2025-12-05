/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.server;

import com.example.common.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



/**
 *
 * @author ffabd
 */



public class ServerNetworkManager {
    private static final int PORT = 12345;
    private static final int MAX_THREADS = 20; // Support up to 20 concurrent clients
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final ExecutorService clientThreadPool;
    private volatile boolean isRunning = false;
    private ServerSocket serverSocket;

    public ServerNetworkManager() {
        // Create a fixed thread pool to handle multiple clients efficiently
        this.clientThreadPool = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public void startServer() {
        isRunning = true;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("🚀 Server started on port " + PORT);
            System.out.println("✅ Ready to accept up to " + MAX_THREADS + " concurrent connections");
            
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("🔌 New client connected: " + clientSocket.getInetAddress());
                    System.out.println("📊 Active clients: " + (clients.size() + 1));
                    
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    clients.add(clientHandler);
                    
                    // Submit to thread pool instead of creating new thread
                    clientThreadPool.submit(clientHandler);
                    
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) { 
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace(); 
        } finally {
            shutdown();
        }
    }

    public void broadcast(Message message) {
        System.out.println("📢 Broadcasting to " + clients.size() + " clients");
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
    
    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("👋 Client disconnected. Active clients: " + clients.size());
    }

    public void shutdown() {
        System.out.println("🛑 Shutting down server...");
        isRunning = false;
        
        // Close server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        
        // Shutdown thread pool gracefully
        clientThreadPool.shutdown();
        try {
            if (!clientThreadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                clientThreadPool.shutdownNow();
            }
            System.out.println("✅ Server shutdown complete");
        } catch (InterruptedException e) {
            clientThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    public int getActiveClientCount() {
        return clients.size();
    }
    
}

    
   
