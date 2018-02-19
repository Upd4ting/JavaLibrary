/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketlib;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import logging.Level;
import logging.Logger;

/**
 *
 * @author Upd4ting
 */
public class SocketUDP extends RawSocket {
    
    private DatagramSocket socket;
    private DatagramPacket packet;
    private int bufSize;

    public SocketUDP(DatagramSocket socket, Logger logger) {
        super(logger);
        this.socket = socket;
        this.bufSize = 4096;
    }

    public boolean writeBytes(byte[] bytes, int size, String ip, int port) {
        try {
            DatagramPacket packet = new DatagramPacket(bytes, size, InetAddress.getByName(ip), port);
            socket.send(packet);
        } catch (IOException ex) {
            this.logger.write(Level.SEVERE, "Failed to send datagram packet...");
            return false;
        }
        return true;
    }

    @Override
    public boolean hasReceivedMessage() {
        byte[] buffer = new byte[bufSize];
        
        if (socket.getLocalPort() == -1) // A été fermé
            return false;
        
        packet = new DatagramPacket(buffer, buffer.length, socket.getLocalAddress(), socket.getLocalPort());
        try {
            socket.receive(packet);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void close(boolean eoc) {
        socket.close();
    }
    
    public void setBufSize(int val) {
        this.bufSize = val;
    }
    
    public DatagramPacket getPacket() {
        return packet;
    }

    public DatagramSocket getSocket() {
        return socket;
    }
}
