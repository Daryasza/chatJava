# Client-Server Chat Application

---
## Features

### **Server**
- **Configuration**:
    - Loads settings from a configuration file, specifying:
        - Server port
        - Server name
        - List of banned phrases
- **Message Broadcasting**:
    - Sends messages to all connected clients by default.
    - Blocks messages containing banned phrases and notifies the sender.
- **Client Management**:
    - Tracks connected clients (name and port).
    - Notifies all clients when someone disconnects.

### **Client**
- **Messaging Capabilities**:
    - Send messages to:
        - All connected clients (default).
        - Specific clients by username.
        - Multiple specified clients.
        - Everyone except certain specified clients.
- **Server Queries**:
    - Retrieve the list of banned phrases.
- **User Notifications**:
    - Receives updates about newly connected or disconnected users.

---

## Key Concepts
- **I/O Streams**:
    - Text and binary file handling.
    - Socket-based communication.
- **Client-Server Architecture**:
    - Multi-client connections with centralized message routing.
- **Dynamic Configuration**:
  - Adjustable server settings via configuration files.

---





