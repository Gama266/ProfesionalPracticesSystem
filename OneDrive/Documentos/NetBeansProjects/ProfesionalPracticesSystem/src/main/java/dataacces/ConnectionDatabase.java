/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataacces;
/*
@(#)ConnectionDabase.java 1.0 04/04/2026
Copyright (c) 2026 JhonatanYerayLIS
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class ConnectionDatabase {

   static {
        try {
            if (ConfigDatabase.getDriver() == null) {
                throw new RuntimeException("Driver no configurado");
            }
            System.out.println("Configuración de BD cargada exitosamente");
        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            throw new RuntimeException("No se puede inicializar la conexión a BD", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            ConfigDatabase.getUrl(),
            ConfigDatabase.getUsername(),
            ConfigDatabase.getPassword()
        );
    }
}