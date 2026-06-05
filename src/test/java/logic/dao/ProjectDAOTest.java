/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package logic.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import dataacces.ConfigDatabase;
import logic.businessObject.LinkedOrganization;
import logic.businessObject.Project;
import logic.businessObject.Teacher;
import logic.businessObject.TechnicalResponsible;
import logic.exceptions.DAOException;

@ExtendWith(MockitoExtension.class)
class ProjectDAOTest {

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStmt;
    @Mock private PreparedStatement mockStmt2;
    @Mock private PreparedStatement mockStmt3;
    @Mock private ResultSet mockRs;
    private ProjectDAO dao;

    @BeforeEach
    void setUp() { dao = new ProjectDAO(); }

    private Project buildProject() {
        Project project = new Project();
        project.setId(1);
        project.setName("Sistema de inventario");
        project.setDescription("Descripción");
        project.setMethodology("Scrum");
        project.setActivityStatus(true);
        project.setObjective("Automatizar inventario");
        project.setRegistrationDate(LocalDate.of(2024, 1, 15));
        project.setAvailableSpaces(3);
        LinkedOrganization org = new LinkedOrganization();
        org.setId(10);
        project.setLinkedOrganization(org);
        Teacher teacher = new Teacher();
        teacher.setNoPersonal(101);
        project.setTeacher(teacher);
        TechnicalResponsible resp = new TechnicalResponsible();
        resp.setId(5);
        project.setTechnicalResponsible(resp);
        return project;
    }

    private Project buildProjectNullables() {
        Project p = new Project();
        p.setId(1);
        p.setName("Proyecto mínimo");
        p.setDescription("Desc");
        p.setMethodology("Kanban");
        p.setActivityStatus(false);
        p.setObjective("Objetivo");
        p.setRegistrationDate(null);
        p.setAvailableSpaces(0);
        p.setLinkedOrganization(null);
        p.setTeacher(null);
        p.setTechnicalResponsible(null);
        return p;
    }

    private void stubActiveResultSet() throws SQLException {
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getInt("idProyecto")).thenReturn(1);
        when(mockRs.getString("nombreProyecto")).thenReturn("Sistema de inventario");
        when(mockRs.getString("descripcion")).thenReturn("Descripción");
        when(mockRs.getString("metodologia")).thenReturn("Scrum");
        when(mockRs.getString("objetivos")).thenReturn("Automatizar inventario");
        when(mockRs.getBoolean("estadoActividad")).thenReturn(true);
        when(mockRs.getInt("cupo")).thenReturn(3);
        when(mockRs.getDate("fechaRegistro")).thenReturn(Date.valueOf("2024-01-15"));
        when(mockRs.getInt("IdOrganizacionVinculada")).thenReturn(10);
        when(mockRs.getString("nombreOrganizacion")).thenReturn("UVER");
    }



    @Test void register_WithAllFields_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.registerProject(buildProject()));
        }
    }

    @Test void register_WithNullableFields_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.registerProject(buildProjectNullables()));
        }
    }

    @Test void register_NoRows_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.registerProject(buildProject()));
        }
    }

    @Test void register_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.registerProject(buildProject()));
        }
    }



    @Test void update_WithAllFields_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.updateProject(buildProject()));
        }
    }

    @Test void update_WithNullableFields_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.updateProject(buildProjectNullables()));
        }
    }

    @Test void update_NoRows_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.updateProject(buildProject()));
        }
    }

    @Test void update_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.updateProject(buildProject()));
        }
    }



    @Test void hide_Success_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.hideProject(1));
        }
    }

    @Test void hide_NoRows_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.hideProject(99));
        }
    }

    @Test void hide_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.hideProject(1));
        }
    }



    @Test void reactivate_Success_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);
            assertTrue(dao.reactivateProject(1));
        }
    }

    @Test void reactivate_NoRows_ReturnsFalse() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(0);
            assertFalse(dao.reactivateProject(99));
        }
    }

    @Test void reactivate_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.reactivateProject(1));
        }
    }



    @Test void getActive_ReturnsList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            stubActiveResultSet();

            List<Project> result = dao.getAllActiveProjects();
            assertEquals(1, result.size());
            assertEquals("Sistema de inventario", result.get(0).getName());
        }
    }

    @Test void getActive_NullDate_MapsCorrectly() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            stubActiveResultSet();
            when(mockRs.getDate("fechaRegistro")).thenReturn(null);

            List<Project> result = dao.getAllActiveProjects();
            assertNull(result.get(0).getRegistrationDate());
        }
    }

    @Test void getActive_EmptyTable_ReturnsEmptyList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
            assertTrue(dao.getAllActiveProjects().isEmpty());
        }
    }

    @Test void getActive_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.getAllActiveProjects());
        }
    }



    @Test void getAll_ReturnsList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            stubActiveResultSet();

            List<Project> result = dao.retrieveAllProjectsIncludingInactive();
            assertEquals(1, result.size());
        }
    }

    @Test void getAll_EmptyTable_ReturnsEmptyList() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
            assertTrue(dao.retrieveAllProjectsIncludingInactive().isEmpty());
        }
    }

    @Test void getAll_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.retrieveAllProjectsIncludingInactive());
        }
    }




    @Test void getById_Found_ReturnsProject() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt("idProyecto")).thenReturn(1);
            when(mockRs.getString("nombre")).thenReturn("Sistema de inventario");
            when(mockRs.getString("descripcion")).thenReturn("Descripción");
            when(mockRs.getString("metodologia")).thenReturn("Scrum");
            when(mockRs.getBoolean("estadoActividad")).thenReturn(true);
            when(mockRs.getString("objetivos")).thenReturn("Automatizar inventario");
            when(mockRs.getDate("fechaRegistro")).thenReturn(Date.valueOf("2024-01-15"));

            assertNotNull(dao.getProjectById(1));
        }
    }

    @Test void getById_NotFound_ReturnsNull() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);
            assertNull(dao.getProjectById(99));
        }
    }

    @Test void getById_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
            assertThrows(DAOException.class, () -> dao.getProjectById(1));
        }
    }


    // ── assignProjectToStudent ───────────────────────────────────────────────

    @Test void assign_Success_ReturnsTrue() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            // tres prepareStatement en orden: assign, decrement, deactivate
            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(mockStmt, mockStmt2, mockStmt3);

            assertTrue(dao.assignProjectToStudent("zS21012345", 1));
            verify(mockConnection).commit();
        }
    }

    @Test void assign_InnerException_RollsBackAndThrows() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(mockStmt, mockStmt2, mockStmt3);
            when(mockStmt.executeUpdate()).thenThrow(new SQLException("fallo"));

            assertThrows(DAOException.class,
                    () -> dao.assignProjectToStudent("zS21012345", 1));
            verify(mockConnection).rollback();
        }
    }

    @Test void assign_ConnectionFails_ThrowsException() throws Exception {
        try (MockedStatic<ConfigDatabase> db = mockStatic(ConfigDatabase.class)) {
            db.when(ConfigDatabase::getConnection).thenThrow(SQLException.class);
            assertThrows(DAOException.class,
                    () -> dao.assignProjectToStudent("zS21012345", 1));
        }
    }
}