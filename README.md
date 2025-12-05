# FRAMEWORKS - Trivia Game

A multiplayer trivia game with JavaFX client, HTML/web client, and multi-threaded server architecture.

## 🎮 Features

- **Multiplayer Support**: Supports up to 20 concurrent players on different devices
- **Scalable Architecture**: Thread pool-based server handles multiple connections efficiently
- **Multiple Client Types**: 
  - JavaFX desktop client
  - HTML/JavaScript web client
- **Dual Server Architecture**: 
  - Socket server for Java clients (Port 12345)
  - WebSocket server for web clients (Port 8080)
- **Real-time Communication**: Socket and WebSocket-based client-server communication

## 📊 Scalability

The server is designed to handle **at least 15 concurrent users** (configurable up to 20) with:
- **Fixed Thread Pool**: ExecutorService with 20 threads for optimal resource management
- **Thread-Safe Collections**: CopyOnWriteArrayList ensures safe concurrent access
- **Efficient Connection Handling**: Each client connection is managed by the thread pool

### Server Configuration

To modify the maximum concurrent connections, edit `ServerNetworkManager.java`:
```java
private static final int MAX_THREADS = 20; // Adjust this value
```

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Gradle

### Running the Server
```bash
cd server
gradlew run
```

The server starts two servers:
- **Socket Server** (Java clients): Port **12345**
- **WebSocket Server** (Web clients): Port **8080**

### Running the Java Client
```bash
cd TriviaClient
gradlew run
```
Connect to: `localhost:12345`

### Running the Web Client
1. Open `web-client/index.html` in any browser
2. Or serve it locally:
   ```bash
   cd web-client
   python -m http.server 3000
   ```
3. Connect to: `localhost:8080` (WebSocket)

### Connecting Multiple Clients
1. Start the server first
2. Launch multiple client instances (Java or Web)
3. **Java clients**: Connect to localhost:12345
4. **Web clients**: Connect to ws://localhost:8080
5. Each client connects independently

## 🏗️ Architecture

### Server Components
- `ServerNetworkManager`: Socket server with thread pool (20 concurrent connections) - Port 12345
- `TriviaWebSocketServer`: WebSocket server for web clients (20 concurrent connections) - Port 8080
- `ClientHandler`: Handles individual Java client communication
- `MessageProcessor`: Processes incoming messages from clients

### Java Client Components
- `ClientNetworkManager`: Singleton managing server connection
- `LoginController`: Handles user authentication
- `LobbyController`: Room management and player listing
- `GameController`: Quiz gameplay interface

### Web Client Components
- `index.html`: Single-page HTML/JavaScript client
- WebSocket for real-time communication
- Responsive design for mobile and desktop

## 📝 Testing Scalability

To test with 15+ users:
1. Start the server
2. Launch 15 or more client instances on same/different machines
3. Connect all clients to the server
4. Monitor server console for connection logs

Server will display:
- Active client count after each connection
- Broadcast statistics
- Connection/disconnection events

## 🔧 Technical Details

- **Socket Server Port**: 12345 (Java clients)
- **WebSocket Server Port**: 8080 (Web/HTML clients)
- **Max Concurrent Clients**: 20 (configurable)
- **Thread Pool Type**: Fixed Thread Pool
- **Collection Type**: CopyOnWriteArrayList (thread-safe)
- **Communication**: Java ObjectInputStream/ObjectOutputStream

