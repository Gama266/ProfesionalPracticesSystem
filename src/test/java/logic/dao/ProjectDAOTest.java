/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package logic.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import logic.businessObject.Project;
import logic.exceptions.DAOException;

@ExtendWith(MockitoExtension.class)
class ProjectDAOTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @Mock private Connection databaseConnection;
    @Mock private PreparedStatement preparedStatement;
    @Mock private ResultSet resultSet;

    private ProjectDAO projectDAO;

    @BeforeEach
    void setUp() throws Exception {
        projectDAO = new ProjectDAO();
    }

    private Project buildProject(int idProject, String projectName) {
        Project project = new Project();
        project.setId(idProject);
        project.setName(projectName);
        project.setDescription("Project Description");
        project.setMethodology("Agile");
        project.setActivityStatus(true);
        project.setObjective("Project Objective");
        project.setRegistrationDate(LocalDate.now());
        project.setAvailableSpaces(3);

        LinkedOrganization organization = new LinkedOrganization();
        organization.setId(1);
        project.setLinkedOrganization(organization);

        return project;
    }

    @Test
    void registerProject_successful_returnsTrue() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Project project = buildProject(1, "Sistema Institucional");

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = projectDAO.registerProject(project);

            assertTrue(result);
        }
    }

    @Test
    void registerProject_noRowsAffected_returnsFalse() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        Project project = buildProject(1, "Sistema Institucional");

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = projectDAO.registerProject(project);

            assertFalse(result);
        }
    }

    @Test
    void registerProject_sqlException_throwsDAOException() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate())
            .thenThrow(new SQLException("Failed to save project", "23000", 1));

        Project project = buildProject(1, "Sistema Institucional");

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            assertThrows(DAOException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    projectDAO.registerProject(project);
                }
            });
        }
    }

    @Test
    void updateProject_successful_returnsTrue() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Project project = buildProject(1, "Sistema Actualizado");

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = projectDAO.updateProject(project);

            assertTrue(result);
        }
    }

    @Test
    void hideProject_successful_returnsTrue() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = projectDAO.hideProject(10);

            assertTrue(result);
        }
    }

    @Test
    void getAllActiveProjects_successful_returnsListWithElements() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("idProyecto")).thenReturn(1);
        when(resultSet.getString("nombreProyecto")).thenReturn("Proyecto Base");
        when(resultSet.getDate("fechaRegistro"))
            .thenReturn(Date.valueOf(LocalDate.now()));

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            List<Project> activeProjects = projectDAO.getAllActiveProjects();

            assertNotNull(activeProjects);
            assertEquals(1, activeProjects.size());
            assertEquals("Proyecto Base", activeProjects.get(0).getName());
        }
    }

    @Test
    void getAllActiveProjects_sqlException_throwsDAOException() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery())
            .thenThrow(new SQLException("Query failed", "08001", 1));

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            assertThrows(DAOException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    projectDAO.getAllActiveProjects();
                }
            });
        }
    }

    @Test
    void getProjectById_existingId_returnsProject() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("idProyecto")).thenReturn(99);
        when(resultSet.getString("nombre")).thenReturn("Proyecto Especifico");

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            Project result = projectDAO.getProjectById(99);

            assertNotNull(result);
            assertEquals(99, result.getId());
            assertEquals("Proyecto Especifico", result.getName());
        }
    }

    @Test
    void getProjectById_nonExistingId_returnsNull() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); 

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            Project result = projectDAO.getProjectById(999);

            assertNull(result);
        }
    }

    @Test
    void assignProjectToStudent_successful_returnsTrue() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            boolean result = projectDAO.assignProjectToStudent("S12345678", 1);

            assertTrue(result);
        }
    }

    @Test
    void assignProjectToStudent_sqlException_throwsDAOException() throws Exception {
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate())
            .thenThrow(new SQLException("Constraint violation", "23000", 1062));

        try (MockedStatic<ConfigDatabase> mockedDatabase = mockStatic(ConfigDatabase.class)) {
            mockedDatabase.when(ConfigDatabase::getConnection).thenReturn(databaseConnection);

            assertThrows(DAOException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    projectDAO.assignProjectToStudent("S12345678", 1);
                }
            });
        }
    }
}