package socketlib;

import logging.Logger;

public abstract class Client {
    
    protected Logger logger;
    
    protected Client(Logger logger) {
        this.logger = logger;
    }
    
    public abstract void start();
    public abstract void stop();

    public Logger getLogger() {
        return logger;
    }
}
