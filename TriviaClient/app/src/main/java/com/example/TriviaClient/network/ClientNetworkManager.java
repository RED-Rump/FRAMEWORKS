package com.example.TriviaClient.network;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import com.example.common.Message;
import com.example.common.MessageType;
import java.net.SocketException;
public class ClientNetworkManager {
    private static ClientNetworkManager instance;
    
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private volatile boolean isConnected = false;
    private String serverHost = "localhost";
    private int serverPort = 12345;  // CHANGED: Default to 12345 to match server
    private String username;
    
    private ExecutorService listenerService;
    private MessageHandler messageHandler;
    
    private final Object connectionLock = new Object();
    
    private ClientNetworkManager() {}
    
    public static synchronized ClientNetworkManager getInstance() {
        if (instance == null) {
            instance = new ClientNetworkManager();
        }
        return instance;
    }
    
    public boolean connect(String host, int port, String username) {
        synchronized (connectionLock) {
            if (isConnected) {
                disconnect();
            }
            
            this.serverHost = host;
            this.serverPort = port;
            this.username = username;
            
            try {
                System.out.println("Connecting to " + host + ":" + port);
                socket = new Socket(host, port);
                
                // IMPORTANT: Create ObjectOutputStream BEFORE ObjectInputStream
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                
                isConnected = true;
                
                startMessageListener();
                
                // Send login request - FIXED: Use senderId
                Message loginMsg = new Message(MessageType.LOGIN_REQUEST, username);
                loginMsg.setSenderId(username);
                sendMessage(loginMsg);
                
                return true;
                
            } catch (IOException e) {
                System.err.println("Connection failed: " + e.getMessage());
                e.printStackTrace();
                disconnect();
                return false;
            }
        }
    }
    
    private void startMessageListener() {
        if (listenerService != null && !listenerService.isShutdown()) {
            listenerService.shutdownNow();
        }
        
        listenerService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "MessageListener-Thread");
            thread.setDaemon(true);
            return thread;
        });
        
        listenerService.submit(() -> {
            while (isConnected && !Thread.currentThread().isInterrupted()) {
                try {
                    Object obj = inputStream.readObject();
                    if (obj instanceof Message) {
                        Message message = (Message) obj;
                        if (messageHandler != null) {
                            try {
                                javafx.application.Platform.runLater(() -> {
                                    messageHandler.handleMessage(message);
                                });
                            } catch (IllegalStateException e) {
                                // Not in JavaFX thread, handle directly
                                messageHandler.handleMessage(message);
                            }
                        }
                    }
                } catch (EOFException e) {
                    System.out.println("Connection closed by server");
                    break;
                } catch (SocketException e) {
                    if (isConnected) {
                        System.out.println("Socket error: " + e.getMessage());
                    }
                    break;
                } catch (IOException e) {
                    if (isConnected) {
                        System.err.println("Error reading from server: " + e.getMessage());
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Invalid message received: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Unexpected error in listener: " + e.getMessage());
                    break;
                }
            }
            
            // Clean up if still connected when listener stops
            if (isConnected) {
                disconnect();
            }
        });
    }
    
    public void sendMessage(Message message) {
        if (!isConnected || outputStream == null) return;
        
        if (message.getSenderId() == null) {
            message.setSenderId(username);
        }
        
        synchronized (this) {
            try {
                outputStream.writeObject(message);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                System.err.println("Failed to send message: " + e.getMessage());
                disconnect();
            }
        }
    }
    
    public void sendMessage(MessageType type, Object data) {
        Message message = new Message(type, data);
        message.setSenderId(username);
        sendMessage(message);
    }
    
    public void disconnect() {
        synchronized (connectionLock) {
            if (!isConnected) return;
            
            isConnected = false;
            
            if (socket != null && !socket.isClosed() && outputStream != null) {
                try {
                    Message logoutMsg = new Message(MessageType.LOGIN_FAILURE, null);
                    logoutMsg.setSenderId(username);
                    sendMessage(logoutMsg);
                } catch (Exception e) {
                    // Ignore
                }
            }
            
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                // Ignore
            }
            
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                // Ignore
            }
            
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                // Ignore
            }
            
            if (listenerService != null) {
                listenerService.shutdownNow();
                try {
                    if (!listenerService.awaitTermination(2, TimeUnit.SECONDS)) {
                        listenerService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    listenerService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            
            inputStream = null;
            outputStream = null;
            socket = null;
            
            System.out.println("Disconnected from server");
        }
    }
    
    public void setServerDetails(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }
    
    public boolean reconnect() {
        return connect(serverHost, serverPort, username);
    }
    
    public boolean isConnected() { 
        return isConnected && socket != null && !socket.isClosed() && socket.isConnected(); 
    }
    
    public String getUsername() { return username; }
    
    public void setMessageHandler(MessageHandler handler) { 
        this.messageHandler = handler; 
    }
}