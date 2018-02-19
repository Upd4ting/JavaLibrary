package socketlib;

import java.util.ArrayList;
import logging.Logger;
import logging.Level;

public abstract class Server {
    
    protected Logger logger;
    protected int port;
    protected boolean running;
    
    protected Server(int port, Logger logger) {
        this.port = port;
        this.logger = logger;
    }
    
    public abstract void onStart();
    public abstract void onStop(boolean eoc);
    public abstract boolean isConnected();
    
    public void start() {
        if (!this.isConnected()) {
            logger.write(Level.SEVERE, "La socket est déconnectée et ne peut être relancée !");
            return;
        }
        
        this.running = true;
        
        this.onStart();
    }
    
    public boolean stop(boolean eoc) {
        this.running = false;
        this.onStop(eoc);
        return true;
    }

    public Logger getLogger() {
        return logger;
    }
    
    public int getPort() {
        return this.port;
    }
}
