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
import logic.businessObject.Teacher;
import logic.exceptions.DAOException;

@ExtendWith(MockitoExtension.class)
class TeacherDAOTest {

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStmt;
    @Mock private PreparedStatement mockStmt2;
    @Mock private ResultSet mockRs;
    private TeacherDAO dao;

    @BeforeEach
    void setUp() { dao = new TeacherDAO(); }

    private Teacher buildTeacher() {
        Teacher t = new Teacher();
        t.setNoPersonal(101);
        t.setName("Juan");
        t.setPaternalSurname("García");
        t.setMaternalSurname("López");
        t.setRole("Titular");
        t.setActivityStatus(true);
        t.setIdUser(5);
        return t;
    }


    // ── registerTeacher ──────────────────────────────────────────────────────

    @Test void register_AlreadyExists_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt("total")).thenReturn(1);
            assertFalse(dao.registerTeacher(buildTeacher()));
        }
    }

    @Test void register_New_() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(mockStmt, mockStmt2);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt("total")).thenReturn(0);
            when(mockStmt2.executeUpdate()).thenReturn(1);
            assertTrue(dao.registerTeacher(buildTeacher()));
        }
    }

    @Test void register_InsertFails_() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(mockStmt, mockStmt2);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt("total")).thenReturn(0);
            when(mockStmt2.executeUpdate()).thenReturn(0);
            assertFalse(dao.registerTeacher(buildTeacher()));
        }
    }

    @Test void register_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.registerTeacher(buildTeacher()));
        }
    }




    @Test void update_Success_() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.updateTeacher(buildTeacher()));
        }
    }

    @Test void update_NoRows_() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.updateTeacher(buildTeacher()));
        }
    }

    @Test void update_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.updateTeacher(buildTeacher()));
        }
    }


    // ── deleteTeacher ────────────────────────────────────────────────────────

    @Test void delete_Success_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.deleteTeacher(101));
        }
    }

    @Test void delete_NoRows_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.deleteTeacher(999));
        }
    }

    @Test void delete_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.deleteTeacher(101));
        }
    }


    // ── getAllTeachers ───────────────────────────────────────────────────────

    @Test void getAll_ReturnsList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("numeroPersonal")).thenReturn(101);
            when(mockRs.getString("nombre")).thenReturn("Juan");
            when(mockRs.getString("apellidoPaterno")).thenReturn("García");
            when(mockRs.getString("apellidoMaterno")).thenReturn("López");
            when(mockRs.getString("rol")).thenReturn("Titular");
            when(mockRs.getBoolean("estadoActividad")).thenReturn(true);

            List<Teacher> result = dao.getAllTeachers();
            assertEquals(1, result.size());
            assertEquals("Juan", result.get(0).getName());
        }
    }

    @Test void getAll_EmptyTable_ReturnsEmptyList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
            assertTrue(dao.getAllTeachers().isEmpty());
        }
    }

    @Test void getAll_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.getAllTeachers());
        }
    }

    @Test void deactivate_Success_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.deactivateTeacher(101));
        }
    }

    @Test void deactivate_NoRows_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.deactivateTeacher(999));
        }
    }

    @Test void deactivate_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.deactivateTeacher(101));
        }
    }
}