package socketlib;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import logging.Level;
import logging.Logger;

public class SocketTCP extends RawSocket {
    
    private Socket socket;
    private byte[] message;
    
    public SocketTCP(Socket socket, Logger logger) {
        super(logger);
        this.socket = socket;
    }
    
    private boolean isSpecialFrame(byte bytes[]) {
        String s = new String(bytes);
                
        if (s.equals(Constant.DENY_OF_CONNECTION) || s.equals(Constant.END_OF_CONNECTION)) {
            
            for (Listener listener : this.listeners)
                listener.onClose(s.equals(Constant.DENY_OF_CONNECTION) ? Constant.CLOSE_DENY_CONNECTION : Constant.CLOSE_END_CONNECTION);
            
            this.stop(false);
            
            return true;
        }
        
        return false;
    }

    private byte[] readBytes(int size) {
        byte[] bytes = null;
        DataInputStream dIn = null;
        
        try {
            dIn = new DataInputStream(this.socket.getInputStream());
            bytes = new byte[size];
            dIn.readFully(bytes, 0, size);
        } catch (IOException e) {
            this.stop("Erreur sur le read InputStream: " + e.getMessage());
            return null;
        } 
        
        if (bytes != null && isSpecialFrame(bytes)) {
            return null;
        }
        
        return bytes;
    }
    
    private byte[] readBytes() {
        byte[] message = null;
        byte[] bytes = null;
        int size = -1;
        
        // Read size
        bytes = readBytes(4);
        
        if (bytes == null)
            return null;
        
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        size = bb.getInt();
        
        message = readBytes(size);
        
        return message;
    }

    private boolean writeBytes(byte[] bytes, int size) {
        boolean success = false;
        
        DataOutputStream dOn = null;
        
        try {
            dOn = new DataOutputStream(this.socket.getOutputStream());
            dOn.write(bytes, 0, size);
            success = true;
        } catch (IOException e) {
            this.stop("Erreur sur le write OutputStream: " + e.getMessage());
            return false;
        } 
        
        return success;
    }
    
    public boolean writeBytesWithSize(byte[] bytes, int size) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(size);
        
        boolean success = writeBytes(b.array(), 4);
        
        if (!success)
            return false;
        
        return writeBytes(bytes, size);
    }

    @Override
    public boolean hasReceivedMessage() {
        message = readBytes();
        
        return message != null;
    }

    @Override
    public void close(boolean eoc) {
        byte bytes[] = Constant.END_OF_CONNECTION.getBytes();
        
        if (eoc)
	{
            this.writeBytesWithSize(bytes, bytes.length);
            this.logger.write(Level.INFO, "Fermeture et envoi de fin de connection!");
	}
                
        try {
            this.socket.close();
        } catch (IOException e) {
            this.logger.write(Level.SEVERE, "Erreur de fermeture de la socket: " + e.getMessage());
        }
    }

    public byte[] getMessage() {
        return message;
    }
    
}
