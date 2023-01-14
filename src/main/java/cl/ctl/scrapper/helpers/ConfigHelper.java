package cl.ctl.scrapper.helpers;

import org.apache.commons.lang.SystemUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 18-12-20.
 */
public class ConfigHelper {

    public static final ConfigHelper instance = new ConfigHelper();

    public Map<String, String> CONFIG = new HashMap<>();

    private static final Logger logger = Logger.getLogger(ConfigHelper.class.getName());

    /**
     * Constructor privado para el Singleton del Factory.
     */
    public ConfigHelper() {

        /*
        try (//InputStream input = new FileInputStream("config.properties")
             InputStream input = this.getClass().getResourceAsStream("/config.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            prop.keySet().forEach(x -> CONFIG.put(x.toString(), prop.getProperty(x.toString())));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */

    }

    public static ConfigHelper getInstance() {
        return instance;
    }

    public void setParameter(String name, String value) {
        CONFIG.put(name, value);
    }

    public String getParameter(String name) {
        return CONFIG.get(name);
    }

}
