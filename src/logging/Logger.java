package logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public final class Logger {
    
    public interface Listener {
        public void onWriteLog(String message);
    }

    private String fileName;
    private ArrayList<Listener> listeners;

    public Logger(String fileName) {
        this.fileName = fileName;
        this.listeners = new ArrayList<>();
    }

    public synchronized void write(Level level, String log) {
        try {
            Date date = new Date();
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            String line = "[" + date + "] " + level + " : " + log;
            bw.write(line);
            bw.newLine();
            bw.close();
            
            for (Listener listener : this.listeners)
                listener.onWriteLog(line);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public String getFileName() {
        return fileName;
    }
}