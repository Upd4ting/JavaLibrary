package socketlib;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import logging.Level;
import logging.Logger;

public class ClientUDP extends Client {
    
    private SocketUDP socket;
    
    private ClientUDP(Logger logger) {
        this(null, -1, logger);
    }
    
    private ClientUDP(String group, int port, Logger logger) {
        super(logger);
        this.initSocket(group, port);
    }

    private void initSocket(String group, int port) {
        try {
            DatagramSocket ds = null;
            
            if (group == null) {
                ds = new DatagramSocket();
            } else {
                InetAddress g = InetAddress.getByName(group);
                ds = new MulticastSocket(port);
                ((MulticastSocket)ds).joinGroup(g);
            }
            this.socket = new SocketUDP(ds, this.logger);
        } catch (IOException e) {
           logger.write(Level.SEVERE, "Error while initializing DatagramSocket: " + e.getMessage());
        }
    }
    
    @Override
    public void start() {
        this.socket.start(false);
    }
    
    @Override
    public void stop() {
        this.socket.close(true);
    }
    
    public static ClientUDP create(Logger logger) {
        ClientUDP client = new ClientUDP(logger);
        
        if (client.getSocket() == null) {
            return null;
        }
        
        return client;
    }
    
    public static ClientUDP create(String group, int port, Logger logger) {
        ClientUDP client = new ClientUDP(group, port, logger);
        
        if (client.getSocket() == null) {
            return null;
        }
        
        return client;
    }

    public SocketUDP getSocket() {
        return socket;
    }
} 
