package socketlib;

import java.util.ArrayList;
import logging.Logger;
import logging.Level;

public abstract class RawSocket implements Runnable {
    
    public interface Listener {
        public void onMessage();
        public void onClose(int reason);
    }
    
    protected Logger logger;
    protected boolean running;
    protected Thread thread;
    protected ArrayList<Listener> listeners;
    
    public RawSocket(Logger logger) {
        this.logger = logger;
        this.running = false;
        this.listeners = new ArrayList<>();
        this.thread = new Thread(this);
    }
    
    public void start(boolean wait) {
        if (this.running)
            return;
        
        this.running = true;
        this.thread.start();
        
        if (wait) {
            try {
                this.thread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void run() {
        while (this.running) {
            if (this.hasReceivedMessage()) {
                for (Listener listener : this.listeners)
                    listener.onMessage();
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                
        }
    }
    
    public abstract boolean hasReceivedMessage();
    
    public boolean stop(boolean eoc) {
        byte bytes[] = Constant.END_OF_CONNECTION.getBytes();
        
        if (!this.running)
            return false;
        
        this.running = false;
        
        close(eoc);

        this.logger.write(Level.INFO, "Fermeture de la socket achevée !");

	return true;
    }
    
    public abstract void close(boolean eoc);
    
    public boolean stop(String message) {
        this.logger.write(Level.SEVERE, "Fermeture du socket du a une erreur: " + message);
        
        for (Listener listener : this.listeners)
            listener.onClose(Constant.CLOSE_ERROR_CONNECTION);

	return this.stop(false);
    }
    
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isRunning() {
        return running;
    }
}
