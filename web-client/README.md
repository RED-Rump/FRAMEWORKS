# Trivia Game - Web Client

A modern HTML/JavaScript client for the Trivia Game that connects via WebSocket.

## 🌐 Features

- **Pure HTML/CSS/JavaScript** - No frameworks required
- **WebSocket Connection** - Real-time communication with server
- **Responsive Design** - Works on desktop and mobile
- **Modern UI** - Beautiful gradient design
- **Easy to Use** - Just open in any browser

## 🚀 Quick Start

### Prerequisites
1. Server must be running with WebSocket support
2. Any modern web browser (Chrome, Firefox, Edge, Safari)

### Running the Web Client

#### Option 1: Open Directly
Simply double-click `index.html` to open in your default browser.

#### Option 2: Local Server (Recommended)
```bash
# Using Python
python -m http.server 3000

# Using Node.js
npx http-server -p 3000

# Using PHP
php -S localhost:3000
```

Then open: `http://localhost:3000`

### Connecting to Server

1. **Start the Trivia Server**:
   ```bash
   cd server
   gradlew run
   ```
   Server will start on:
   - Socket Server (Java clients): Port **12345**
   - WebSocket Server (Web clients): Port **8080**

2. **Open the Web Client** in your browser

3. **Enter Connection Details**:
   - Username: Your display name
   - Server Host: `localhost` (or server IP address)
   - Server Port: `8080` (WebSocket port)

4. **Click "Connect & Login"**

## 📱 Using from Different Devices

### Same Network
1. Find server's IP address:
   ```bash
   ipconfig  # Windows
   ifconfig  # Mac/Linux
   ```

2. On other device, open web client

3. Connect using:
   - Server Host: `192.168.x.x` (server's IP)
   - Server Port: `8080`

### Example
If server is at `192.168.1.100`:
- Java clients connect to: `192.168.1.100:12345`
- Web clients connect to: `ws://192.168.1.100:8080`

## 🎮 How to Play

1. **Login**: Enter username and connect
2. **Lobby**: Create or join a game room
3. **Game**: Answer trivia questions
4. **Results**: See your score and rankings

## 🔧 Configuration

### Change Server Connection
Edit the HTML file, find these lines:
```javascript
<input type="text" id="serverHost" value="localhost" placeholder="localhost">
<input type="number" id="serverPort" value="8080" placeholder="8080">
```

### Customize Styling
All CSS is embedded in the `<style>` section. Modify colors, fonts, etc.

## 🌐 WebSocket Protocol

The client communicates using JSON messages:

### Login Request
```json
{
  "type": "LOGIN_REQUEST",
  "content": "username",
  "senderId": "username"
}
```

### Login Response
```json
{
  "type": "LOGIN_SUCCESS",
  "content": "Welcome username!",
  "senderId": "SERVER"
}
```

### Create Room
```json
{
  "type": "CREATE_ROOM",
  "content": "Room Name",
  "senderId": "username"
}
```

### Submit Answer
```json
{
  "type": "SUBMIT_ANSWER",
  "content": 2,
  "senderId": "username"
}
```

## 📊 Supported Message Types

- `LOGIN_REQUEST` / `LOGIN_SUCCESS` / `LOGIN_FAILURE`
- `CREATE_ROOM` / `JOIN_ROOM`
- `ROOM_LIST_UPDATE`
- `GAME_START`
- `NEW_QUESTION`
- `SUBMIT_ANSWER`
- `ROUND_RESULT`
- `GAME_OVER`
- `ERROR`

## 🐛 Troubleshooting

### Cannot Connect
- ✅ Server is running
- ✅ WebSocket port 8080 is not blocked
- ✅ Correct IP address and port
- ✅ Browser supports WebSocket (all modern browsers do)

### Connection Refused
- Check if server shows "WebSocket server started on port 8080"
- Try using `ws://localhost:8080` explicitly
- Check firewall settings

### Mixed Content Error (HTTPS sites)
- If hosting on HTTPS, you'll need WSS (secure WebSocket)
- For local testing, use HTTP or localhost

## 🚀 Testing with Multiple Clients

### Same Computer
1. Open multiple browser tabs/windows
2. Connect each with different username
3. All connect to same server

### Different Devices
1. Ensure all devices on same network
2. Use server's IP address
3. Each device opens web client in browser

## 🎨 Customization Ideas

- Change color scheme (edit gradient in CSS)
- Add sound effects
- Implement chat functionality
- Add animations
- Create different themes
- Add mobile-specific UI

## 📈 Performance

- **Lightweight**: ~10KB HTML file
- **Fast**: WebSocket for real-time communication
- **Responsive**: Works on all screen sizes
- **Compatible**: Works in all modern browsers

## 🔒 Security Notes

For production use:
- Use WSS (WebSocket Secure) instead of WS
- Implement authentication tokens
- Validate all user input
- Use HTTPS for hosting
- Implement rate limiting

## 📝 Browser Compatibility

- ✅ Chrome 16+
- ✅ Firefox 11+
- ✅ Safari 7+
- ✅ Edge (all versions)
- ✅ Opera 12.1+
- ✅ Mobile browsers (iOS Safari, Chrome Mobile)

## 🎯 Next Steps

- [ ] Add chat functionality
- [ ] Implement leaderboard display
- [ ] Add sound effects
- [ ] Create mobile app wrapper (Cordova/Capacitor)
- [ ] Add animations
- [ ] Implement reconnection logic
- [ ] Add profile pictures

---

**WebSocket Server**: Port 8080  
**Status**: ✅ Ready for use  
**Clients Supported**: Unlimited (browser-based)
