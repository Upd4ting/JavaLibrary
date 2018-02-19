package socketlib;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.net.ssl.SSLSocket;
import logging.Level;
import logging.Logger;

public abstract class ServerTCP extends Server implements Runnable {
        
    public interface ServerListener {
        public void onAccept(SocketTCP socket);
    }
    
    private ServerSocket socket;
    protected ArrayList<ServerListener> listeners;
    
    public ServerTCP(int port, SocketFactory factory, Logger logger) {
        super(port, logger);
        this.initSocket(port, factory);
        this.listeners = new ArrayList<>();
    }
    
    private void initSocket(int port, SocketFactory factory) {
        try {
            this.socket = factory.createServerSocket(port);
        } catch (Exception e) {
            logger.write(Level.SEVERE, "Error while initializing ServerSocket: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        while (this.running) {
            try {
                Socket hSocketService = this.socket.accept();
                logger.write(Level.INFO, "New client connection...");
                
                SocketTCP socket = new SocketTCP(hSocketService, this.logger);
                
                if (hSocketService instanceof SSLSocket)
                    ((SSLSocket) hSocketService).startHandshake();
                
                for (ServerListener listener : this.listeners)
                    listener.onAccept(socket);
                
                this.onClientAccept(socket);
            } catch (IOException e) {
                return;
            }
        }
    }
    
    public abstract void onClientAccept(SocketTCP socket);

    @Override
    public void onStart() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void onStop(boolean eoc) {
        try {
            this.socket.close();
        } catch (IOException e) {
            logger.write(Level.SEVERE, "Error while closing ServerSocket: " + e.getMessage());
        }
    }
    
    public void addListener(ServerListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public boolean isConnected() {
        return this.socket != null && !socket.isClosed();
    }

    public ServerSocket getSocket() {
        return socket;
    }
}
