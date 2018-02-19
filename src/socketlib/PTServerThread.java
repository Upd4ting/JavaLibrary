package socketlib;

import logging.Logger;
import logging.Level;

public class PTServerThread implements Runnable {
    
    private PTServer server;
    private RawSocket socket;
    private int clientIsDone;
    
    public PTServerThread(PTServer server) {
        this.server = server;
        this.socket = null;
        this.clientIsDone = -1;
    }
    
    @Override
    public void run() {        
        while (this.server.running) {
            this.server.getLogger().write(Level.INFO, "Mise en attente d'un client...");
            
            this.server.lock.lock();
            
            while (this.server.current == -1) {
                try {
                    this.server.cond.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            this.clientIsDone = this.server.current;
            this.server.current = -1;
            this.socket = this.server.sPool.get(clientIsDone);
            this.server.lock.unlock();
            
            this.server.getLogger().write(Level.INFO, "Prise en charge d'un client !");
            
            this.socket.start(true);
            
            this.socket.stop(true);
            
            this.server.lock.lock();
            this.server.sPool.set(clientIsDone, null);
            this.server.lock.unlock();
            
            this.socket = null;
            this.clientIsDone = -1;
            
            this.server.getLogger().write(Level.INFO, "Le client est parti...");
        }
    }
}
