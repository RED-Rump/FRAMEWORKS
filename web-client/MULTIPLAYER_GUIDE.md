# 🎮 Trivia Game - Multiplayer Setup Guide

## Quick Start for Multiple Users

### Option 1: Using the Landing Page (Recommended)
Share this link with your friends:
```
http://localhost:8080/web-client/play.html
```

Or if the server is on a different machine:
```
http://<YOUR_SERVER_IP>:8080/web-client/play.html
```

### Option 2: Direct Game Link
Share this link to go straight to the game:
```
http://localhost:8080/web-client/index.html
```

---

## 🖥️ Server Setup

### Step 1: Start the Server
Run the following in PowerShell:
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd "c:\Users\berna\Desktop\FRAMEWORKS\server"
.\gradlew.bat run
```

Wait for this message:
```
✓ WebSocket server started on port 8081
✓ HTML clients can connect to ws://localhost:8081
```

### Step 2: Share with Players

**On the Same Network (Local):**
- Find your computer's IP: Run `ipconfig` in PowerShell, look for "IPv4 Address"
- Share link: `http://<YOUR_IP>:8080/web-client/play.html`
- Example: `http://192.168.1.100:8080/web-client/play.html`

**On the Same Computer:**
- Share link: `http://localhost:8080/web-client/play.html`
- All users open in different browser tabs or windows

---

## 📱 How Players Join

### Step 1: Open the Link
Players click on the shared link (opens in any browser):
```
http://<SERVER_IP>:8080/web-client/play.html
```

### Step 2: Enter Username
- Type their username (any name they want)
- Click "Connect"

### Step 3: Play the Game
- **Create Room**: Start a new game (other players can join)
- **Join Room**: Enter an existing room code or select from available rooms
- Game starts automatically with 5 seconds after the room is full

---

## 🔗 Connection Details

- **Server Address**: `localhost` (or your machine's IP)
- **WebSocket Port**: `8081`
- **Browser Support**: Chrome, Firefox, Safari, Edge
- **Max Players per Room**: 6
- **Questions per Game**: 10
- **Points per Question**: 10 points for correct answer

---

## 📊 Supported Scenarios

✅ **2 players**: Both on same PC, different browser windows  
✅ **3-6 players**: On same network (LAN)  
✅ **Multiple games**: Different rooms can run simultaneously  
✅ **Mobile devices**: iPhone/Android can play too (if on same network)  

---

## 🐛 Troubleshooting

### "Cannot connect to server"
- ✓ Make sure server is running (you should see "WebSocket server started on port 8081")
- ✓ Check firewall isn't blocking port 8081
- ✓ Verify you're using correct IP address

### "Can't find the game page"
- ✓ Make sure you're using correct URL with port 8080 or 8081
- ✓ Try `http://localhost:8080/web-client/play.html`

### "Players can't connect from other devices"
- ✓ Use your computer's IP instead of `localhost`
- ✓ Make sure devices are on same WiFi/network
- ✓ Check firewall settings allow port 8081

### "Server crashes after a few connections"
- ✓ Server supports up to 20 concurrent connections
- ✓ Try again or restart the server

---

## 🎯 Complete Example

**Host's PC** (running Windows):
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd "c:\Users\berna\Desktop\FRAMEWORKS\server"
.\gradlew.bat run
```

**Host's Browser**:
- Open: `http://localhost:8080/web-client/play.html`
- Click "Play Now"
- Create room "Gaming Night"
- Share this link: `http://192.168.1.100:8080/web-client/play.html`

**Friend's Phone/PC** (on same WiFi):
- Open link in browser: `http://192.168.1.100:8080/web-client/play.html`
- Enter username: "Player2"
- Click "Connect"
- Join room "Gaming Night"
- Game starts in 5 seconds
- Answer questions and compete!

---

## ✨ Features

🎮 **Real-time multiplayer** - See other players' scores instantly  
⚡ **Smooth animations** - Professional UI with responsive feedback  
🏆 **Live leaderboard** - See rankings throughout the game  
📊 **Instant feedback** - Know if your answer is correct immediately  
🎨 **Beautiful design** - Modern gradient interface  

---

## 📞 Need Help?

1. Check server is running and shows "WebSocket server started on port 8081"
2. Verify players are using correct URL with server IP
3. Make sure firewall allows port 8081
4. Try refreshing the browser page
5. Restart the server if connection issues persist

---

**Version**: 2.0 (Smooth & Responsive Edition with Multiplayer Support)  
**Last Updated**: December 2025  
**Players Supported**: 2-20 concurrent connections  
