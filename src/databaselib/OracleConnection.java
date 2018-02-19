package databaselib;

import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnection extends BaseConnection {

    @Override
    public boolean connect(String ip, int port, String database, String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:oracle:" + database + ":@" + ip + ":" + Integer.toString(port) + "/" + "orcl",username,password);
            connection.setAutoCommit(true);
            //connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
