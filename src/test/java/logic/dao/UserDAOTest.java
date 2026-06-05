package logic.dao;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
import java.sql.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import dataacces.ConfigDatabase;
import logic.businessObject.User;
import logic.businessObject.UserType;
import logic.exceptions.DAOException;
import logic.util.PasswordHasher;
 
@ExtendWith(MockitoExtension.class)
class UserDAOTest {
 
    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStmt;
    @Mock private ResultSet mockRs;
    private UserDAO dao;
 
    @BeforeEach
    void setUp() { dao = new UserDAO(); }
 
    private User buildUser() {
        User u = new User();
        u.setGmail("juan@mail.com");
        u.setPlainPassword("password123");
        u.setUserType(UserType.PROFESOR);
        u.setActive(true);
        return u;
    }
 
 
    // ── registerUser ─────────────────────────────────────────────────────────
 
    @Test void register_Success_ReturnsGeneratedId() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class);
             MockedStatic<PasswordHasher> ph = mockStatic(PasswordHasher.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            ph.when(() -> PasswordHasher.hash(anyString())).thenReturn("hashedPass");
            when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStmt);
            when(mockStmt.getGeneratedKeys()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt(1)).thenReturn(42);
 
            assertEquals(42, dao.registerUser(buildUser()));
        }
    }
 
    @Test void register_NoKeyGenerated_ThrowsDAOException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class);
             MockedStatic<PasswordHasher> ph = mockStatic(PasswordHasher.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            ph.when(() -> PasswordHasher.hash(anyString())).thenReturn("hashedPass");
            when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStmt);
            when(mockStmt.getGeneratedKeys()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
 
            assertThrows(DAOException.class, () -> dao.registerUser(buildUser()));
        }
    }
 
    @Test void register_DuplicateEmail_ThrowsDAOException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class);
             MockedStatic<PasswordHasher> ph = mockStatic(PasswordHasher.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            ph.when(() -> PasswordHasher.hash(anyString())).thenReturn("hashedPass");
            SQLException duplicateEx = mock(SQLException.class);
            when(duplicateEx.getErrorCode()).thenReturn(1062);
            when(mockConnection.prepareStatement(anyString(), anyInt())).thenThrow(duplicateEx);
 
            DAOException ex = assertThrows(DAOException.class, () -> dao.registerUser(buildUser()));
            assertTrue(ex.getMessage().contains("ya está registrado"));
        }
    }
 
    @Test void register_NullUser_ThrowsDAOException() throws Exception {
        assertThrows(DAOException.class, () -> dao.registerUser(null));
    }
 
    @Test void register_BlankGmail_ThrowsDAOException() throws Exception {
        User u = buildUser();
        u.setGmail("  ");
        assertThrows(DAOException.class, () -> dao.registerUser(u));
    }
 
    @Test void register_BlankPassword_ThrowsDAOException() throws Exception {
        User u = buildUser();
        u.setPlainPassword("");
        assertThrows(DAOException.class, () -> dao.registerUser(u));
    }
 
    @Test void register_NullUserType_ThrowsDAOException() throws Exception {
        User u = buildUser();
        u.setUserType(null);
        assertThrows(DAOException.class, () -> dao.registerUser(u));
    }
 
 
    // ── login ────────────────────────────────────────────────────────────────
 
    @Test void login_ValidCredentials_ReturnsUser() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class);
             MockedStatic<PasswordHasher> ph = mockStatic(PasswordHasher.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getString("contrasena")).thenReturn("hashedPass");
            ph.when(() -> PasswordHasher.verify(anyString(), anyString())).thenReturn(true);
            when(mockRs.getInt("idUsuario")).thenReturn(1);
            when(mockRs.getString("gmail")).thenReturn("juan@mail.com");
            when(mockRs.getString("tipoUsuario")).thenReturn("profesor");
            when(mockRs.getString("estadoActividad")).thenReturn("activo");
 
            Optional<User> result = dao.login("juan@mail.com", "password123");
            assertTrue(result.isPresent());
        }
    }
 
    @Test void login_WrongPassword_ReturnsEmpty() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class);
             MockedStatic<PasswordHasher> ph = mockStatic(PasswordHasher.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getString("contrasena")).thenReturn("hashedPass");
            ph.when(() -> PasswordHasher.verify(anyString(), anyString())).thenReturn(false);
 
            assertTrue(dao.login("juan@mail.com", "wrong").isEmpty());
        }
    }
 
    @Test void login_UserNotFound_ReturnsEmpty() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
 
            assertTrue(dao.login("noexiste@mail.com", "pass").isEmpty());
        }
    }
 
    @Test void login_BlankGmail_ReturnsEmpty() throws Exception {
        assertTrue(dao.login("  ", "pass").isEmpty());
    }
 
    @Test void login_NullPassword_ReturnsEmpty() throws Exception {
        assertTrue(dao.login("juan@mail.com", null).isEmpty());
    }
 
    @Test void login_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.login("juan@mail.com", "pass"));
        }
    }
 
 
    // ── updatePassword ───────────────────────────────────────────────────────
 
    @Test void updatePassword_Success_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class);
             MockedStatic<PasswordHasher> ph = mockStatic(PasswordHasher.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            ph.when(() -> PasswordHasher.hash(anyString())).thenReturn("newHash");
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
 
            assertTrue(dao.updatePassword(1, "nuevaPass"));
        }
    }
 
    @Test void updatePassword_NoRows_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class);
             MockedStatic<PasswordHasher> ph = mockStatic(PasswordHasher.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            ph.when(() -> PasswordHasher.hash(anyString())).thenReturn("newHash");
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
 
            assertFalse(dao.updatePassword(99, "nuevaPass"));
        }
    }
 
    @Test void updatePassword_BlankPassword_ThrowsDAOException() throws Exception {
        assertThrows(DAOException.class, () -> dao.updatePassword(1, "  "));
    }
 
    @Test void updatePassword_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class);
             MockedStatic<PasswordHasher> ph = mockStatic(PasswordHasher.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            ph.when(() -> PasswordHasher.hash(anyString())).thenReturn("newHash");
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
 
            assertThrows(DAOException.class, () -> dao.updatePassword(1, "nuevaPass"));
        }
    }
 
 
    // ── deactivateUser ───────────────────────────────────────────────────────
 
    @Test void deactivate_Success_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.deactivateUser(1));
        }
    }
 
    @Test void deactivate_NoRows_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.deactivateUser(99));
        }
    }
 
    @Test void deactivate_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.deactivateUser(1));
        }
    }
 
 
    // ── getPractitionerEnrollmentByUserId ────────────────────────────────────
 
    @Test void getEnrollment_Found_ReturnsMatricula() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getString("matricula")).thenReturn("zS21012345");
 
            assertEquals("zS21012345", dao.getPractitionerEnrollmentByUserId(1));
        }
    }
 
    @Test void getEnrollment_NotFound_ThrowsDAOException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
 
            assertThrows(DAOException.class, () -> dao.getPractitionerEnrollmentByUserId(99));
        }
    }
 
    @Test void getEnrollment_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.getPractitionerEnrollmentByUserId(1));
        }
    }
}
 