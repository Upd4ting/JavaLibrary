package socketlib;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import logging.Level;
import logging.Logger;

public class ServerUDP extends Server {
    
    private SocketUDP socket;

    public ServerUDP(int port, Logger logger) {
        super(port, logger);
        this.initSocket(null, port);
    }
    
    public ServerUDP(InetAddress group, int port, Logger logger) { // Multi cast constructor
        super(port, logger);
        this.initSocket(group, port);
    }
    
    private void initSocket(InetAddress group, int port) {
        try {
            this.socket = group == null ? new SocketUDP(new DatagramSocket(port), this.logger) : new SocketUDP(new MulticastSocket(port), this.logger);
            
            if (group != null)
                ((MulticastSocket)this.socket.getSocket()).joinGroup(group);
        } catch (IOException e) {
            logger.write(Level.SEVERE, "Error while initializing ServerSocket: " + e.getMessage());
        }
    }

    @Override
    public void onStart() {
        this.socket.start(false);
    }

    @Override
    public void onStop(boolean eoc) {
        this.socket.close(eoc);
    }

    @Override
    public boolean isConnected() {
        return this.socket != null;
    }

    public SocketUDP getSocket() {
        return socket;
    }
    
    
}
