package socketlib;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SocketFactorySSL implements SocketFactory {
    
    private KeyStore key;
    private String pass;
    
    public SocketFactorySSL(KeyStore key, String pass) {
        this.key = key;
        this.pass = pass;
    }

    @Override
    public Socket createSocket(String ip, int port) throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(key);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(key, pass.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("SSLv3");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLSocketFactory sf = sslContext.getSocketFactory();
        SSLSocket s = (SSLSocket)sf.createSocket(ip, port);
        s.startHandshake();
        
        return s;
    }

    @Override
    public ServerSocket createServerSocket(int port) throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(key);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(key, pass.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("SSLv3");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
        SSLServerSocket s = (SSLServerSocket) sf.createServerSocket(port);
        return s;
    }
    
}
