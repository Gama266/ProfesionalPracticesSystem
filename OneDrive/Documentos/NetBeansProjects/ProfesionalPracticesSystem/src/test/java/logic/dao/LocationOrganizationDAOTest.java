/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import logic.businessObject.LocationOrganization;
import logic.exceptions.DAOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author gamal
 */
public class LocationOrganizationDAOTest {

    private LocationOrganizationDAO locationDAO;
    private LocationOrganization testLocation;

    public LocationOrganizationDAOTest() {
    }

    @BeforeEach
    public void setUp() {
      
        locationDAO = new LocationOrganizationDAO();
        testLocation = new LocationOrganization();

        testLocation.setCountry("TEST_PAIS");
        testLocation.setState("TEST_ESTADO");
    }

    @AfterEach
    public void tearDown() {
      
        System.out.println("Limpiando la base de datos de ubicaciones...");
        try {
            
            locationDAO.deleteLocation("TEST_PAIS", "TEST_ESTADO");
            locationDAO.deleteLocation("TEST_PAIS", "TEST_ESTADO_MODIFICADO");
        } catch (DAOException e) {
            System.err.println("Error en limpieza de ubicacion: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterLocationExitoso() {
        System.out.println("Ejecutando: testRegisterLocation");
        try {
      
            boolean result = locationDAO.registerLocation(testLocation);

           
            assertTrue(result, "La ubicación debería registrarse correctamente");

        } catch (DAOException e) {
            fail("Falló la inserción: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateLocationExitoso() {
        System.out.println("Ejecutando: testUpdateLocation");
        try {
          
            locationDAO.registerLocation(testLocation);

           
            int generatedId = getTestLocationId();
            assertTrue(generatedId > 0, "No se pudo recuperar el ID generado para la prueba");

            
            testLocation.setId(generatedId);
            testLocation.setState("TEST_ESTADO_MODIFICADO"); // Cambiamos un dato

            
            boolean result = locationDAO.updateLocation(testLocation);

        
            assertTrue(result, "La ubicación debería actualizarse correctamente");

        } catch (DAOException | SQLException e) {
            fail("La prueba falló por una excepción: " + e.getMessage());
        }
    }

 
    private int getTestLocationId() throws SQLException {
        String query = "SELECT idUbicacion FROM ubicacion WHERE pais = ? AND estado = ?";
        try (Connection conn = ConfigDatabase.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "TEST_PAIS");
            stmt.setString(2, "TEST_ESTADO");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idUbicacion");
                }
            }
        }
        return -1;
    }
}
