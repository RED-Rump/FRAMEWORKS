# Web Client - Quick Start Guide

## 🌐 Access from HTML Frontend

### Step 1: Start the Server
```bash
cd server
gradlew run
```

You'll see:
```
🎮 Initializing Trivia Server...
📚 Questions loaded: [X]
🚀 WebSocket server started on port 8080
✅ HTML clients can connect to ws://localhost:8080
🌐 Starting servers...
   - Socket Server (Java clients): Port 12345
   - WebSocket Server (HTML clients): Port 8080
```

### Step 2: Open Web Client

#### Option A: Direct Open (Easiest)
1. Navigate to `web-client/` folder
2. Double-click `index.html`
3. Opens in your default browser

#### Option B: Local Server (Recommended)
```bash
cd web-client

# Using Python
python -m http.server 3000

# Using Node.js (if installed)
npx http-server -p 3000

# Using PHP
php -S localhost:3000
```

Then open: http://localhost:3000

### Step 3: Connect to Server
1. **Username**: Enter any username (e.g., "Player1")
2. **Server Host**: `localhost` (or IP address if server on different machine)
3. **Server Port**: `8080` (WebSocket port)
4. Click **"Connect & Login"**

### Step 4: Play!
- Wait for lobby screen
- Create or join a room
- Answer trivia questions
- See your score!

## 🌍 Access from Different Devices

### Same Network (Local)
1. Find server's IP address:
   ```bash
   # Windows
   ipconfig
   
   # Mac/Linux
   ifconfig
   ```

2. Copy the web-client folder to other device OR access via network

3. On other device:
   - Open `index.html` in browser
   - Server Host: `192.168.x.x` (server's IP)
   - Server Port: `8080`
   - Connect!

### Over Internet (Advanced)
1. Deploy server to cloud (AWS, Azure, DigitalOcean)
2. Configure firewall to allow port 8080
3. Use server's public IP or domain
4. Host web-client on web server or GitHub Pages
5. Update connection settings in HTML

## 📱 Mobile Access

### Same WiFi
1. Start server on computer
2. Find computer's IP (e.g., 192.168.1.100)
3. On phone/tablet browser:
   - Navigate to: `http://192.168.1.100:3000` (if using local server)
   - Or copy HTML file to phone and open
4. Connect:
   - Host: `192.168.1.100`
   - Port: `8080`

### Works on:
- ✅ iPhone Safari
- ✅ Android Chrome
- ✅ iPad
- ✅ Android Tablets
- ✅ Any mobile browser

## 🚀 Testing with 15+ Users

### Mix of Java and Web Clients
```
Server (Port 12345 + 8080)
    │
    ├─> Java Client 1 (Port 12345)
    ├─> Java Client 2 (Port 12345)
    ├─> Web Client 1  (Port 8080) [Browser Tab]
    ├─> Web Client 2  (Port 8080) [Browser Tab]
    ├─> Mobile Client 1 (Port 8080) [Phone]
    ├─> Mobile Client 2 (Port 8080) [Tablet]
    └─> ... up to 20 clients per server
```

### How to Test
1. **Start Server**
2. **Open 5 browser tabs** - Each with web client
3. **Launch 5 Java clients**
4. **Connect 5 mobile devices**
5. **Total: 15 clients!**

Each client type works independently!

## 🎨 Customization

### Change Colors
Edit `index.html`, find:
```css
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
```
Change to your colors!

### Change Default Server
Edit `index.html`, find:
```html
<input type="text" id="serverHost" value="localhost">
<input type="number" id="serverPort" value="8080">
```

### Add Your Logo
Add image in HTML:
```html
<img src="logo.png" alt="Logo" style="width: 100px;">
```

## 🐛 Troubleshooting

### "Failed to connect"
- ✅ Server is running
- ✅ Using correct port (8080, not 12345)
- ✅ Firewall allows port 8080
- ✅ Using `ws://` not `http://`

### "Connection refused"
- Server might not have started WebSocket server
- Check server console for "WebSocket server started on port 8080"
- Try restarting server

### Blank screen
- Check browser console (F12)
- Look for JavaScript errors
- Try different browser

### Can't connect from phone
- Both devices on same WiFi
- Using server's IP, not "localhost"
- Firewall allows connections
- Port 8080 accessible

## 📊 Connection URLs

| Client Type | Connection String | Port |
|-------------|------------------|------|
| Java Client | `localhost:12345` | 12345 |
| Web Client | `ws://localhost:8080` | 8080 |
| Remote Java | `192.168.1.100:12345` | 12345 |
| Remote Web | `ws://192.168.1.100:8080` | 8080 |

## 🎯 Production Deployment

### For Real Deployment:

1. **Server**:
   - Deploy to cloud (AWS, Azure, etc.)
   - Use domain name
   - Enable SSL/TLS
   - Use WSS (secure WebSocket)

2. **Web Client**:
   - Host on web server (Nginx, Apache)
   - Or use GitHub Pages, Netlify, Vercel
   - Update connection to production server
   - Use HTTPS

3. **Example Production URLs**:
   - Web Client: `https://trivia.example.com`
   - WebSocket: `wss://trivia.example.com:8080`

## ✅ Benefits of Web Client

- ✅ **No Installation**: Just open in browser
- ✅ **Cross-Platform**: Works on any device
- ✅ **Easy Updates**: Update HTML file, all clients get changes
- ✅ **Mobile Friendly**: Responsive design
- ✅ **Shareable**: Send link, anyone can play
- ✅ **Fast**: Lightweight, loads instantly

## 🎮 Ready to Play!

Your trivia game is now accessible from:
- Desktop Java app
- Web browsers (Chrome, Firefox, Safari, Edge)
- Mobile phones (iOS, Android)
- Tablets
- Any device with a modern browser!

Just start the server and share the web client! 🚀

---

**WebSocket Server**: Port 8080  
**Web Client**: `web-client/index.html`  
**Status**: ✅ Ready to use from any HTML frontend
