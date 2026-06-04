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
import logic.exceptions.DatabaseConnectionException;
import logic.exceptions.DuplicateRecordException;

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
    void setUp() throws Exception {
        linkedOrganizationDAO = new LinkedOrganizationDAO();
    }

    private LinkedOrganization buildOrganization(int id, String name) {
        LinkedOrganization organization = new LinkedOrganization();
        organization.setId(id);
        organization.setName(name);
        organization.setDireccion("Avenida Siempre Viva 123");
        organization.setPhoneNumber("2281234567");
        organization.setGmail("contacto@organizacion.com");

        LocationOrganization location = new LocationOrganization();
        location.setId(1);
        organization.setLocationOrganization(location);

        return organization;
    }

    @Test
    void registerLinkedOrganization_successful_returnsTrue() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0); 

        when(preparedStatement.executeUpdate()).thenReturn(1);

        LinkedOrganization organization = buildOrganization(1, "Organización Nueva");

        try (MockedStatic<ConfigDatabase> mockedDb = mockStatic(ConfigDatabase.class)) {
            mockedDb.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = linkedOrganizationDAO.registerLinkedOrganization(organization);

            assertTrue(result);
        }
    }

    @Test
    void registerLinkedOrganization_duplicate_throwsDuplicateRecordException() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        
        // Simulación para isOrganizationAlreadyRegistered (retorna > 0 count = verdadero)
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        LinkedOrganization organization = buildOrganization(1, "Organización Existente");

        try (MockedStatic<ConfigDatabase> mockedDb = mockStatic(ConfigDatabase.class)) {
            mockedDb.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            assertThrows(DuplicateRecordException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    linkedOrganizationDAO.registerLinkedOrganization(organization);
                }
            });
        }
    }

    @Test
    void registerLinkedOrganization_sqlException_throwsDAOException() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        
        // Evadir la excepción de duplicidad
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0); 

        // Provocar fallo al insertar
        when(preparedStatement.executeUpdate())
            .thenThrow(new SQLException("Error al insertar", "23000", 1));

        LinkedOrganization organization = buildOrganization(1, "Organización Fallida");

        try (MockedStatic<ConfigDatabase> mockedDb = mockStatic(ConfigDatabase.class)) {
            mockedDb.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            assertThrows(DAOException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    linkedOrganizationDAO.registerLinkedOrganization(organization);
                }
            });
        }
    }

    @Test
    void isOrganizationAlreadyRegistered_exists_returnsTrue() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        try (MockedStatic<ConfigDatabase> mockedDb = mockStatic(ConfigDatabase.class)) {
            mockedDb.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = linkedOrganizationDAO.isOrganizationAlreadyRegistered("Existente SA");

            assertTrue(result);
        }
    }

    @Test
    void isOrganizationAlreadyRegistered_notExists_returnsFalse() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);

        try (MockedStatic<ConfigDatabase> mockedDb = mockStatic(ConfigDatabase.class)) {
            mockedDb.when