package socketlib;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketFactoryTCP implements SocketFactory {

    @Override
    public Socket createSocket(String ip, int port) throws Exception {
        return new Socket(ip, port);
    }

    @Override
    public ServerSocket createServerSocket(int port) throws Exception {
        return new ServerSocket(port);
    }
    
}
