package socketlib;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public interface SocketFactory {
    public Socket createSocket(String ip, int port) throws Exception;
    public ServerSocket createServerSocket(int port) throws Exception;
}
