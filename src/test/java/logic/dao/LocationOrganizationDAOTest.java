/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package logic.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import dataacces.ConfigDatabase;
import logic.businessObject.LinkedOrganization;
import logic.businessObject.LocationOrganization;
import logic.exceptions.DAOException;
import logic.exceptions.DuplicateRecordException;

/**
 * Pruebas unitarias con Mockito para LinkedOrganizationDAO.
 * @author Gamaliel Cabrera Plácido
 */
@ExtendWith(MockitoExtension.class)
class LinkedOrganizationDAOTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @Mock private Connection databaseConnection;
    @Mock private PreparedStatement preparedStatement;
    @Mock private ResultSet resultSet;

    private LinkedOrganizationDAO linkedOrganizationDAO;

    @BeforeEach
    void setUp() {
        linkedOrganizationDAO = new LinkedOrganizationDAO();
    }

    /**
     * Objeto simulado para las pruebas.
     */
    private LinkedOrganization buildOrganization(int id, String name) {
        LinkedOrganization org = new LinkedOrganization();
        org.setId(id);
        org.setName(name);
        org.setDireccion("Av. Luis Castelazo s/n, Xalapa"); 
        org.setPhoneNumber("2288421700");
        org.setGmail("vinculacion.fei@uv.mx");

        LocationOrganization location = new LocationOrganization();
        location.setId(1);
        org.setLocationOrganization(location);

        return org;
    }

    @Test
    void registerLinkedOrganization_successful_returnsTrue() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        
        // 1. Simular la verificación de duplicados (isOrganizationAlreadyRegistered = false)
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0); 

        // 2. Simular la inserción de la organización
        when(preparedStatement.executeUpdate()).thenReturn(1);

        LinkedOrganization organization = buildOrganization(1, "Organizacion Base");

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = linkedOrganizationDAO.registerLinkedOrganization(organization);

            assertTrue(result, "La organización debería registrarse correctamente y devolver true");
        }
    }

    @Test
    void registerLinkedOrganization_duplicateOrganization_throwsDuplicateRecordException() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        
        // Simular que ya existe 1 registro con ese nombre en la base de datos
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1); 

        LinkedOrganization organization = buildOrganization(1, "Organizacion Duplicada");

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            assertThrows(DuplicateRecordException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    linkedOrganizationDAO.registerLinkedOrganization(organization);
                }
            }, "Debería lanzar DuplicateRecordException al detectar un duplicado");
        }
    }

    @Test
    void isOrganizationAlreadyRegistered_existingName_returnsTrue() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1); // Conteo > 0

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = linkedOrganizationDAO.isOrganizationAlreadyRegistered("Empresa Tech");

            assertTrue(result);
        }
    }

    @Test
    void isOrganizationAlreadyRegistered_newName_returnsFalse() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0); // Conteo == 0

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = linkedOrganizationDAO.isOrganizationAlreadyRegistered("Empresa Nueva");

            assertFalse(result);
        }
    }

    @Test
    void getAllOrganizations_successful_returnsListWithElements() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        
        // Simular que el ResultSet tiene un registro y luego se detiene
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        
        // Simular los datos devueltos por la BD
        when(resultSet.getInt("IdOrganizacionVinculada")).thenReturn(10);
        when(resultSet.getString("nombreOrganizacion")).thenReturn("Instituto de Prácticas");
        when(resultSet.getString("direccion")).thenReturn("Calle Central");
        when(resultSet.getString("numeroTelefono")).thenReturn("2281234567");

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            List<LinkedOrganization> organizations = linkedOrganizationDAO.getAllOrganizations();

            assertNotNull(organizations);
            assertEquals(1, organizations.size());
            assertEquals(10, organizations.get(0).getId());
            assertEquals("Instituto de Prácticas", organizations.get(0).getName());
        }
    }

    @Test
    void getAllOrganizations_sqlException_throwsDAOException() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        
        // Forzar un fallo de SQL
        when(preparedStatement.executeQuery())
            .thenThrow(new SQLException("Error de conexión con la tabla", "08001", 1));

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            assertThrows(DAOException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    linkedOrganizationDAO.getAllOrganizations();
                }
            });
        }
    }
}