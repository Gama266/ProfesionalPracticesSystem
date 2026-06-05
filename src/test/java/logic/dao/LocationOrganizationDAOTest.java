package logic.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import dataacces.ConfigDatabase;
import logic.businessObject.LocationOrganization;
import logic.exceptions.DAOException;

@ExtendWith(MockitoExtension.class)
class LocationOrganizationDAOTest {

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStmt;
    @Mock private ResultSet mockRs;
    private LocationOrganizationDAO dao;

    @BeforeEach
     void setUp() { dao = new LocationOrganizationDAO(); }


    @Test void register_Exists_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(anyString())).thenReturn(1);
            assertTrue(dao.registerLocation(new LocationOrganization("MX", "VER")));
        }
    }

    @Test void register_New_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            when(mockStmt.getGeneratedKeys()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(10);
            assertTrue(dao.registerLocation(new LocationOrganization("MX", "JAL")));
        }
    }

    @Test void register_Fails_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.registerLocation(new LocationOrganization("MX", "PUE")));
        }
    }

    @Test void register_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString(), anyInt())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.registerLocation(new LocationOrganization("MX", "DF")));
        }
    }

   
    @Test void update_Success() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.updateLocation(new LocationOrganization(1, "MX", "VER")));
        }
    }

    @Test void update_FailRowsZero() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.updateLocation(new LocationOrganization(1, "MX", "VER")));
        }
    }

    @Test void update_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.updateLocation(new LocationOrganization(1, "MX", "VER")));
        }
    }

   
    @Test void delete_Success() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.deleteLocation("MX", "VER"));
        }
    }

    @Test void delete_FailRowsZero() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.deleteLocation("MX", "VER"));
        }
    }

    @Test void delete_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.deleteLocation("MX", "VER"));
        }
    }


    @Test void getId_Found() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(anyString())).thenReturn(5);
            assertEquals(5, dao.getExistingLocationId("MX", "VER"));
        }
    }

    @Test void getId_NotFound() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
            assertEquals(0, dao.getExistingLocationId("MX", "VER"));
        }
    }

    @Test void getId_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.getExistingLocationId("MX", "VER"));
        }
    }

    @Test void register_NullParameters() throws Exception {

        assertThrows(Exception.class, () -> dao.getExistingLocationId(null, null));
    }
}