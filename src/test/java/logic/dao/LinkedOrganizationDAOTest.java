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
import logic.businessObject.LocationOrganization;
import logic.exceptions.DAOException;
import logic.exceptions.DatabaseConnectionException;
import logic.exceptions.DuplicateRecordException;
 
@ExtendWith(MockitoExtension.class)
class LinkedOrganizationDAOTest {
 
    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStmt;
    @Mock private PreparedStatement mockStmt2;
    @Mock private ResultSet mockRs;
    private LinkedOrganizationDAO dao;
 
    @BeforeEach
    void setUp() { dao = new LinkedOrganizationDAO(); }
 
    private LinkedOrganization buildOrganization() {
        LinkedOrganization org = new LinkedOrganization();
        org.setName("UVER");
        org.setDireccion("Av. Principal 123");
        org.setPhoneNumber("2281234567");
        org.setGmail("contacto@uver.mx");
        LocationOrganization loc = new LocationOrganization();
        loc.setId(1);
        org.setLocationOrganization(loc);
        return org;
    }
 
 
    // ── isOrganizationAlreadyRegistered ──────────────────────────────────────
 
    @Test void isRegistered_Exists_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(1);
            assertTrue(dao.isOrganizationAlreadyRegistered("UVER"));
        }
    }
 
    @Test void isRegistered_NotExists_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(0);
            assertFalse(dao.isOrganizationAlreadyRegistered("Nueva"));
        }
    }
 
    @Test void isRegistered_ThrowsDatabaseConnectionException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DatabaseConnectionException.class,
                    () -> dao.isOrganizationAlreadyRegistered("UVER"));
        }
    }
 
 
    // ── registerLinkedOrganization ───────────────────────────────────────────
 
    @Test void register_DuplicateName_ThrowsDuplicateRecordException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(1);
            assertThrows(DuplicateRecordException.class,
                    () -> dao.registerLinkedOrganization(buildOrganization()));
        }
    }
 
    @Test void register_New_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            // primera llamada: isOrganizationAlreadyRegistered, segunda: INSERT
            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(mockStmt, mockStmt2);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(0);
            when(mockStmt2.executeUpdate()).thenReturn(1);
            assertTrue(dao.registerLinkedOrganization(buildOrganization()));
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
            assertFalse(dao.registerLinkedOrganization(buildOrganization()));
        }
    }
 
    @Test void register_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class,
                    () -> dao.registerLinkedOrganization(buildOrganization()));
        }
    }
 
 
    // ── getAllOrganizations ───────────────────────────────────────────────────
 
    @Test void getAll_ReturnsList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("IdOrganizacionVinculada")).thenReturn(1);
            when(mockRs.getString("nombreOrganizacion")).thenReturn("UVER");
            when(mockRs.getString("direccion")).thenReturn("Av. Principal 123");
            when(mockRs.getString("numeroTelefono")).thenReturn("2281234567");
 
            List<LinkedOrganization> result = dao.getAllOrganizations();
            assertEquals(1, result.size());
            assertEquals("UVER", result.get(0).getName());
        }
    }
 
    @Test void getAll_EmptyTable_ReturnsEmptyList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
            assertTrue(dao.getAllOrganizations().isEmpty());
        }
    }
 
    @Test void getAll_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.getAllOrganizations());
        }
    }
}