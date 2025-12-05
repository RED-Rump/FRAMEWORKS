# Scalability Testing Guide

## Overview
This document describes how to test the Trivia Game server's ability to handle **15+ concurrent users** on different devices.

## Server Configuration

### Current Settings
- **Maximum Concurrent Connections**: 20
- **Thread Pool Type**: Fixed Thread Pool (ExecutorService)
- **Port**: 12345
- **Thread-Safe Collections**: Yes (CopyOnWriteArrayList)

### Architecture Benefits
1. **Fixed Thread Pool**: Prevents resource exhaustion by limiting threads to 20
2. **Thread-Safe Collections**: CopyOnWriteArrayList ensures safe concurrent access
3. **Graceful Shutdown**: Proper cleanup of resources when server stops
4. **Connection Monitoring**: Real-time logging of active connections

## Testing Scenarios

### Test 1: Single Machine - Multiple Clients
**Goal**: Verify 15 clients can connect simultaneously on one machine

1. Start the server:
   ```bash
   cd server
   gradlew run
   ```

2. Open 15 separate terminal windows/tabs

3. In each terminal, run:
   ```bash
   cd TriviaClient
   gradlew run
   ```

4. Connect each client:
   - Username: User1, User2, ... User15
   - Host: localhost
   - Port: 12345

**Expected Result**: Server console shows "Active clients: 15"

### Test 2: Multiple Machines - Network Testing
**Goal**: Verify clients on different devices can connect

1. Start server on one machine
2. Note the server's IP address (e.g., 192.168.1.100)
3. On 14 other devices, run the client
4. Connect using:
   - Host: [Server IP Address]
   - Port: 12345

**Expected Result**: All 15 clients connect successfully

### Test 3: Stress Test - 20 Connections
**Goal**: Test maximum capacity

1. Start the server
2. Launch 20 client instances
3. Connect all simultaneously
4. Monitor server performance

**Expected Result**: All 20 clients connect, server remains stable

### Test 4: Connection Cycling
**Goal**: Verify server handles connect/disconnect cycles

1. Connect 10 clients
2. Disconnect 5 clients
3. Connect 10 more clients (total 15 active)
4. Repeat several times

**Expected Result**: Server maintains stability, properly cleans up disconnected clients

## Monitoring Server Performance

### Console Output to Monitor
```
🚀 Server started on port 12345
✅ Ready to accept up to 20 concurrent connections
🔌 New client connected: /127.0.0.1
📊 Active clients: 1
📢 Broadcasting to 15 clients
👋 Client disconnected. Active clients: 14
```

### Key Metrics
- **Active Client Count**: Should accurately reflect connected clients
- **Thread Pool Status**: Monitor for thread exhaustion
- **Memory Usage**: Use `jconsole` or `VisualVM` to monitor
- **Response Time**: Client connections should be immediate

## Load Testing Tools (Optional)

### Custom Load Test Script
Create multiple client connections programmatically:

```java
// Example: Spawn 15 client connections
for (int i = 1; i <= 15; i++) {
    String username = "TestUser" + i;
    // Launch client instance
}
```

### Using JMeter
1. Configure JMeter to simulate TCP connections
2. Set thread count to 15
3. Target: localhost:12345
4. Run test and monitor server

## Troubleshooting

### Issue: Server refuses connection after X clients
**Solution**: Increase MAX_THREADS in ServerNetworkManager.java

### Issue: OutOfMemoryError
**Solution**: Increase JVM heap size:
```bash
gradlew run -Dorg.gradle.jvmargs="-Xmx2g"
```

### Issue: Slow performance with many clients
**Solutions**:
- Reduce message broadcast frequency
- Implement selective messaging (unicast vs broadcast)
- Use message queuing

## Performance Benchmarks

### Expected Performance (15 Clients)
- **Connection Time**: < 100ms per client
- **Message Latency**: < 50ms
- **CPU Usage**: < 30%
- **Memory**: < 512MB
- **Thread Count**: 15-20 active threads

### Expected Performance (20 Clients)
- **Connection Time**: < 150ms per client
- **Message Latency**: < 100ms
- **CPU Usage**: < 40%
- **Memory**: < 768MB
- **Thread Count**: 20 active threads

## Certification

Once testing is complete, the server is certified to support:
- ✅ **Minimum 15 concurrent users** on different devices
- ✅ **Maximum 20 concurrent users** with thread pool
- ✅ **Thread-safe concurrent operations**
- ✅ **Graceful connection/disconnection handling**
- ✅ **Stable long-running operation**

## Next Steps for Enhanced Scalability

To support even more users (50+):
1. Implement connection pooling
2. Add load balancing
3. Use NIO (non-blocking I/O)
4. Implement message queuing (RabbitMQ, Kafka)
5. Horizontal scaling with multiple server instances
