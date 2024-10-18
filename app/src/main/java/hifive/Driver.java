package hifive;

import java.util.Properties;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "properties/game2.properties";

    public static void main(String[] args) {
        final Properties properties = PropertiesLoader.loadPropertiesFile(DEFAULT_PROPERTIES_PATH);
        assert properties != null;
        String logResult = new HiFive(properties).runApp();
        System.out.println("logResult = " + logResult);
    }
}
