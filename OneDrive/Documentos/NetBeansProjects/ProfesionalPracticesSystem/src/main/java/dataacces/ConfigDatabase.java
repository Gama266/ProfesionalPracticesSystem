/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataacces;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author gamal
 */
public class ConfigDatabase {
   
    private static final String CONFIG_FILE = "databasesProperties.properties";
    private static Properties properties = new Properties();
    
    static {
        try (InputStream input = ConfigDatabase.class.getResourceAsStream(CONFIG_FILE)) {
            
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo: " + CONFIG_FILE 
                    + " en el paquete " + ConfigDatabase.class.getPackage().getName());
            }
            
            properties.load(input);
            System.out.println("Configuración cargada desde " + CONFIG_FILE);
            
        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            throw new RuntimeException("No se pudo cargar la configuración de BD", e);
        }
    }
    
    public static String getDriver() {
        return properties.getProperty("database.driver");
    }
    
    public static String getUrl() {
        return properties.getProperty("database.url");
    }
    
    public static String getUsername() {
        return properties.getProperty("database.username");
    }
    
    public static String getPassword() {
        return properties.getProperty("database.password");
    }
    
    
}

