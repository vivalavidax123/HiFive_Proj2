package hifive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    public static Properties loadPropertiesFile(String propertiesFile) {
        try(InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(propertiesFile)) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            return prop;
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
