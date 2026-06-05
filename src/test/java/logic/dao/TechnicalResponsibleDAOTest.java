/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package logic.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import dataacces.ConfigDatabase;
import logic.businessObject.LinkedOrganization;
import logic.businessObject.TechnicalResponsible;
import logic.exceptions.DAOException;
import logic.exceptions.DuplicateRecordException;

@ExtendWith(MockitoExtension.class)
class TechnicalResponsibleDAOTest {

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStmt;
    @Mock private PreparedStatement mockStmt2;
    @Mock private ResultSet mockRs;
    private TechnicalResponsibleDAO dao;

    @BeforeEach
    void setUp() { dao = new TechnicalResponsibleDAO(); }

    private TechnicalResponsible buildResponsible() {
        TechnicalResponsible r = new TechnicalResponsible();
        r.setId(1);
        r.setName("Carlos");
        r.setPaternalSurname("Ramírez");
        r.setMaternalSurname("Torres");
        r.setPhoneNumber("2281234567");
        r.setGmail("carlos@mail.com");
        LinkedOrganization org = new LinkedOrganization();
        org.setId(10);
        r.setLinkedOrganization(org);
        return r;
    }

    private TechnicalResponsible buildResponsibleNoOrg() {
        TechnicalResponsible r = buildResponsible();
        r.setLinkedOrganization(null);
        return r;
    }



    @Test void isEmail_Registered_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(1);
            assertTrue(dao.isEmailAlreadyRegistered("carlos@mail.com"));
        }
    }

    @Test void isEmail_NotRegistered_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(0);
            assertFalse(dao.isEmailAlreadyRegistered("nuevo@mail.com"));
        }
    }

    @Test void isEmail_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.isEmailAlreadyRegistered("x@mail.com"));
        }
    }



    @Test void register_DuplicateEmail_ThrowsDuplicateRecordException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(1);
            assertThrows(DuplicateRecordException.class,
                    () -> dao.registerTechnicalResponsible(buildResponsible()));
        }
    }

    @Test void register_NewWithOrg_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(mockStmt, mockStmt2);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(0);
            when(mockStmt2.executeUpdate()).thenReturn(1);
            assertTrue(dao.registerTechnicalResponsible(buildResponsible()));
        }
    }

    @Test void register_NewWithoutOrg_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(mockStmt, mockStmt2);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(0);
            when(mockStmt2.executeUpdate()).thenReturn(1);
            assertTrue(dao.registerTechnicalResponsible(buildResponsibleNoOrg()));
        }
    }

    @Test void register_InsertFails_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(mockStmt, mockStmt2);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(0);
            when(mockStmt2.executeUpdate()).thenReturn(0);
            assertFalse(dao.registerTechnicalResponsible(buildResponsible()));
        }
    }

    @Test void register_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class,
                    () -> dao.registerTechnicalResponsible(buildResponsible()));
        }
    }




    @Test void update_WithOrg_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.updateTechnicalResponsible(buildResponsible()));
        }
    }

    @Test void update_WithoutOrg_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.updateTechnicalResponsible(buildResponsibleNoOrg()));
        }
    }

    @Test void update_NoRows_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.updateTechnicalResponsible(buildResponsible()));
        }
    }

    @Test void update_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class,
                    () -> dao.updateTechnicalResponsible(buildResponsible()));
        }
    }



    @Test void getAll_ReturnsList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("idResponsableTecnico")).thenReturn(1);
            when(mockRs.getString("nombre")).thenReturn("Carlos");
            when(mockRs.getString("apellidoPaterno")).thenReturn("Ramírez");
            when(mockRs.getString("apellidoMaterno")).thenReturn("Torres");
            when(mockRs.getString("numeroTelefono")).thenReturn("2281234567");
            when(mockRs.getString("correoElectronico")).thenReturn("carlos@mail.com");
            when(mockRs.getString("nombreOrganizacion")).thenReturn("UVER");

            List<TechnicalResponsible> result = dao.getAllTechnicalResponsibles();
            assertEquals(1, result.size());
            assertEquals("Carlos", result.get(0).getName());
        }
    }

    @Test void getAll_EmptyTable_ReturnsEmptyList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
            assertTrue(dao.getAllTechnicalResponsibles().isEmpty());
        }
    }

    @Test void getAll_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.getAllTechnicalResponsibles());
        }
    }


    @Test void getByOrg_ReturnsList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("idResponsableTecnico")).thenReturn(1);
            when(mockRs.getString("nombre")).thenReturn("Carlos");
            when(mockRs.getString("apellidoPaterno")).thenReturn("Ramírez");
            when(mockRs.getString("apellidoMaterno")).thenReturn("Torres");
            when(mockRs.getString("numeroTelefono")).thenReturn("2281234567");
            when(mockRs.getString("correoElectronico")).thenReturn("carlos@mail.com");

            List<TechnicalResponsible> result = dao.getByOrganization(10);
            assertEquals(1, result.size());
            assertEquals("Carlos", result.get(0).getName());
        }
    }

    @Test void getByOrg_EmptyResult_ReturnsEmptyList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
            assertTrue(dao.getByOrganization(99).isEmpty());
        }
    }

    @Test void getByOrg_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.getByOrganization(10));
        }
    }
}