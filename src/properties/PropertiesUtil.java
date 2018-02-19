package properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.function.Function;

public class PropertiesUtil {
    
    public static Properties load(String fileName, Function<Properties, Void> callback) {
        Properties prop = new Properties();
        InputStream input = null;
        OutputStream output = null;
        
        try {
            File file = new File(fileName);
        
            if (!file.exists()) {
                file.createNewFile();
                
                output = new FileOutputStream(file);
                
                callback.apply(prop);
                
                prop.save(output, null);
            } else {
                input = new FileInputStream(file);
                
                prop.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return prop;
    }
}
