package socketlib;

import logging.Level;
import logging.Logger;

public class ClientTCP extends Client {
    
    private SocketTCP socket;
    
    private ClientTCP(String ip, int port, Logger logger) {
        this(ip, port, new SocketFactoryTCP(), logger);
    }

    private ClientTCP(String ip, int port, SocketFactory factory, Logger logger) {
        super(logger);
        this.initSocket(ip, port, factory);
    }

    private void initSocket(String ip, int port, SocketFactory factory) {
        try {
            this.socket = new SocketTCP(factory.createSocket(ip, port), this.logger);
        } catch (Exception e) {
           logger.write(Level.SEVERE, "Error while initializing ClientTCP: " + e.getMessage());
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
    
    public static ClientTCP create(String ip, int port, Logger logger) {
        return create(ip, port, new SocketFactoryTCP(), logger);
    }
    
    public static ClientTCP create(String ip, int port, SocketFactory factory, Logger logger) {
        ClientTCP client = new ClientTCP(ip, port, factory, logger);
        
        if (client.getSocket() == null) {
            return null;
        }
        
        return client;
    }

    public SocketTCP getSocket() {
        return socket;
    }
}
