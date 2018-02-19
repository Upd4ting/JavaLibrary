package databaselib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

// Pour une meilleure représentation
// Map<String, Parameter> = un tuple SQL, -> clé = colonne, valeur = valeur de la colonne sous forme d'un object

public interface DatabaseConnection {
    
    public enum ConnectionType {
        SQL, ORACLE;
    }

    public boolean connect(String ip, int port, String database, String username, String password);
    public ArrayList<Map<String, Object>> executeQuery(String query, ArrayList<Object> parameters);
    public boolean executeUpdate(String query, ArrayList<Object> parameters);
    public long count(String table);
    public long count(String table, String where);
    public long maxId(String table);
    public boolean close();
    public boolean isConnected();
    public ArrayList<String> getColumns(String table);
    public ArrayList<String> getTables();
    
    public static DatabaseConnection create(ConnectionType type) {
        DatabaseConnection connection = null;
        
        if (type == ConnectionType.ORACLE)
            connection = new OracleConnection();
        else if (type == ConnectionType.SQL)
            connection = new SqlConnection();
        
        return connection;
    }
    
    // JPA like utils
    public static <T> T map(Map<String, Object> result, Class<T> clazz) {
        try {
            T object = clazz.newInstance();
            
            for (String f : result.keySet()) {
                Field field = clazz.getDeclaredField(f);
                field.setAccessible(true);
                field.set(object, result.get(f));
            }
            
            return object;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
