package socketlib;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import logging.Level;
import logging.Logger;

// J'ai pas mis de private ou public car par défaut la visibilitée est pour les classes du même package
// Ce qui permet au PTServerThread d'y avoir accès
// Y a pas de système de friend comme en c++ :c 

public class PTServer extends ServerTCP {
    
    int maxThreads;
    ArrayList<SocketTCP> sPool;
    ArrayList<Thread> tPool;
    int current;
    Lock lock;
    Condition cond;
    
    private PTServer(int port, int maxThreads, SocketFactory factory, Logger logger) {
        super(port, factory, logger);
        this.maxThreads = maxThreads;
        this.sPool = new ArrayList<>();
        this.tPool = new ArrayList<>();
        this.current = -1;
        this.lock = lock = new ReentrantLock();
        this.cond = this.lock.newCondition();
    }

    @Override
    public void onStart() {
        this.current = -1;
        this.sPool.clear();
        this.tPool.clear();
        
        this.lock.lock();
        
        for (int i = 0; i < this.maxThreads; i++) {
            Thread thread = new Thread(new PTServerThread(this));
            thread.start();
            
            tPool.add(thread);
            sPool.add(null);
        }
        
        this.lock.unlock();
        
        super.onStart();
    }

    @Override
    public void onClientAccept(SocketTCP socket) {
        int j;
        
        this.lock.lock();

	for (j = 0; j < this.maxThreads && this.sPool.get(j) != null; j++);

	// Plus de client
	if (j == this.maxThreads)
	{
            byte bytes[] = Constant.DENY_OF_CONNECTION.getBytes();
            socket.writeBytesWithSize(bytes, bytes.length);
            socket.stop(false);
	}
	else
	{
            this.sPool.set(j, socket);
            this.current = j;
	}
        
        this.cond.signal();
        this.lock.unlock();
    }

    @Override
    public void onStop(boolean eoc) {
        this.lock.lock();
        
        for (RawSocket socket : sPool) {
            if (socket == null) continue;
            
            socket.stop(eoc);
            
            logger.write(Level.INFO, "On a clear un client!");
        }
        
        this.lock.unlock();
        
        super.onStop(eoc);
    }
    
    public int getMaxThreads() {
        return maxThreads;
    }

    public ArrayList<SocketTCP> getsPool() {
        return sPool;
    }
    
    public static PTServer create(int port, int maxThreads, Logger logger) {
        return create(port, maxThreads, new SocketFactoryTCP(), logger);
    }
    
    public static PTServer create(int port, int maxThreads, SocketFactory factory, Logger logger) {
        PTServer server = new PTServer(port, maxThreads, factory, logger);
        
        if (server.getSocket() == null) {
            return null;
        }
        
        return server;
    }
}
