package databaselib;

import databaselib.DatabaseConnection.ConnectionType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionPool {
    
    public interface Listener {
        public void onConnectionCreated();
        public void onConnectionRemoved();
    }
    
    public class Credentials {
        String ip;
        int port;
        String database;
        String username;
        String password;
        
        Credentials(String ip, int port, String database, String username, String password) {
            this.ip = ip;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
        }
    }
    
    private Credentials credentials;
    private ConnectionType type;
    private LinkedList<DatabaseConnection> pools;
    private LinkedList<Timer> timers;
    private ArrayList<Listener> listeners;
    
    public ConnectionPool(ConnectionType type, String ip, int port, String database, String username, String password) {
        this.type = type;
        this.credentials = new Credentials(ip, port, database, username, password);
        this.pools = new LinkedList<>();
        this.timers = new LinkedList<>();
        this.listeners = new ArrayList<>();
    }
    
    public DatabaseConnection getConnection() {
        if (this.pools.isEmpty()) {
            DatabaseConnection con = DatabaseConnection.create(type);
            con.connect(this.credentials.ip, this.credentials.port, this.credentials.database, this.credentials.username, this.credentials.password);
            this.pools.add(con);
            
            for (Listener listener : listeners)
                listener.onConnectionCreated();
        } else {
            Timer timer = this.timers.pop();
            timer.cancel();
        }
        
        return this.pools.pop();
    }
    
    public void returnConnection(DatabaseConnection connection) {
        this.pools.push(connection);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connection.close();
                timers.remove(timer);
                pools.remove(connection);
                
                for (Listener listener : listeners)
                    listener.onConnectionRemoved();
            }
        }, 5 * 60 * 1000L); // 5 Minutes d'inactivité on ferme la connection
        this.timers.push(timer);
    }
    
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public ConnectionType getType() {
        return type;
    }
}
