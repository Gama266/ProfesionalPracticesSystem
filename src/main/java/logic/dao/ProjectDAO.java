/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.businessObject.LinkedOrganization;
import logic.businessObject.Project;
import logic.exceptions.DAOException;
import logic.idao.IProjectDAO;

public class ProjectDAO implements IProjectDAO {

    private static final Logger LOG = Logger.getLogger(ProjectDAO.class.getName());
    
    @Override
    public boolean registerProject(Project newProject) throws DAOException {
        String queryRegisterProject =
            "INSERT INTO Proyecto (nombre, descripcion, metodologia, estadoActividad, objetivos, fechaRegistro, idOrganizacionVinculada, noPersonal, idResponsableTecnico, cupo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement stamentRegisterProject = connection.prepareStatement(queryRegisterProject)) {

            stamentRegisterProject.setString(1, newProject.getName());
            stamentRegisterProject.setString(2, newProject.getDescription());
            stamentRegisterProject.setString(3, newProject.getMethodology()); 
            stamentRegisterProject.setBoolean(4, newProject.getActivityStatus());
            stamentRegisterProject.setString(5, newProject.getObjective());

            if (newProject.getRegistrationDate() != null) {
                stamentRegisterProject.setDate(6, Date.valueOf(newProject.getRegistrationDate()));
            } else {
                stamentRegisterProject.setNull(6, Types.DATE);
            }

            if (newProject.getLinkedOrganization() != null) {
                stamentRegisterProject.setInt(7, newProject.getLinkedOrganization().getId());
            } else {
                stamentRegisterProject.setNull(7, Types.INTEGER);
            }

            if (newProject.getTeacher() != null) {
                stamentRegisterProject.setInt(8, newProject.getTeacher().getNoPersonal());
            } else {
                stamentRegisterProject.setNull(8, Types.INTEGER);
            }

            if (newProject.getTechnicalResponsible() != null) {
                stamentRegisterProject.setInt(9, newProject.getTechnicalResponsible().getId());
            } else {
                stamentRegisterProject.setNull(9, Types.INTEGER);
            }
            
            stamentRegisterProject.setInt(10, newProject.getAvailableSpaces());

            int rowsAffected = stamentRegisterProject.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error registrando el proyecto", exceptionDB);
        }
    }

    @Override
    public boolean updateProject(Project project) throws DAOException {
        String queryUpdateProject =
            "UPDATE Proyecto SET nombre = ?, descripcion = ?, metodologia = ?, estadoActividad = ?, objetivos = ?, fechaRegistro = ?, idOrganizacionVinculada = ?, noPersonal = ?, idResponsableTecnico = ?, cupo = ? WHERE idProyecto = ?";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement stamentUpdateProject = connection.prepareStatement(queryUpdateProject)) {

            stamentUpdateProject.setString(1, project.getName());
            stamentUpdateProject.setString(2, project.getDescription());
            stamentUpdateProject.setString(3, project.getMethodology());
            stamentUpdateProject.setBoolean(4, project.getActivityStatus());
            stamentUpdateProject.setString(5, project.getObjective());

            if (project.getRegistrationDate() != null) {
                stamentUpdateProject.setDate(6, Date.valueOf(project.getRegistrationDate()));
            } else {
                stamentUpdateProject.setNull(6, Types.DATE);
            }

            if (project.getLinkedOrganization() != null) {
                stamentUpdateProject.setInt(7, project.getLinkedOrganization().getId());
            } else {
                stamentUpdateProject.setNull(7, Types.INTEGER);
            }

            if (project.getTeacher() != null) {
                stamentUpdateProject.setInt(8, project.getTeacher().getNoPersonal());
            } else {
                stamentUpdateProject.setNull(8, Types.INTEGER);
            }

            if (project.getTechnicalResponsible() != null) {
                stamentUpdateProject.setInt(9, project.getTechnicalResponsible().getId());
            } else {
                stamentUpdateProject.setNull(9, Types.INTEGER);
            }
            
            stamentUpdateProject.setInt(10, project.getAvailableSpaces());
            stamentUpdateProject.setInt(11, project.getId());

            int rowsAffected = stamentUpdateProject.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error actualizando el proyecto", exceptionDB);
        }
    }
    
    @Override
    public boolean hideProject(int idProject) throws DAOException {
        String queryHideProject = "UPDATE Proyecto SET estadoActividad = 0 WHERE idProyecto = ?";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statementHide = connection.prepareStatement(queryHideProject)) {
            
            statementHide.setInt(1, idProject);
            int rowsAffected = statementHide.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al ocultar el proyecto en la base de datos", exceptionDB);
        }
    }

    @Override
    public List<Project> getAllActiveProjects() throws DAOException {
        List<Project> activeProjects = new ArrayList<>();

        String queryGetActive = 
            "SELECT Proyecto.idProyecto, Proyecto.nombre AS nombreProyecto, Proyecto.descripcion, " +
            "Proyecto.metodologia, Proyecto.estadoActividad, Proyecto.objetivos, Proyecto.fechaRegistro, Proyecto.cupo, " +
            "organizacionvinculada.IdOrganizacionVinculada, organizacionvinculada.nombreOrganizacion " +
            "FROM Proyecto Proyecto " +
            "INNER JOIN organizacionvinculada organizacionvinculada ON Proyecto.idOrganizacionvinculada = organizacionvinculada.IdOrganizacionVinculada " +
            "WHERE Proyecto.estadoActividad = 1"; 

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statementGet = connection.prepareStatement(queryGetActive);
             ResultSet resultSet = statementGet.executeQuery()) {

            while (resultSet.next()) {
                Project project = new Project();
                project.setId(resultSet.getInt("idProyecto"));
                project.setName(resultSet.getString("nombreProyecto")); 
                project.setDescription(resultSet.getString("descripcion"));
                project.setMethodology(resultSet.getString("metodologia"));
                project.setObjective(resultSet.getString("objetivos"));
                project.setActivityStatus(resultSet.getBoolean("estadoActividad"));
                project.setAvailableSpaces(resultSet.getInt("cupo"));
                
                Date dbDate = resultSet.getDate("fechaRegistro");
                if (dbDate != null) {
                    project.setRegistrationDate(dbDate.toLocalDate());
                }

                LinkedOrganization org = new LinkedOrganization();
                org.setId(resultSet.getInt("IdOrganizacionVinculada"));
                org.setName(resultSet.getString("nombreOrganizacion")); 
                
                project.setLinkedOrganization(org);
                activeProjects.add(project);
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al recuperar los proyectos activos", exceptionDB);
        }
        
        return activeProjects;
    }

    @Override
    public List<Project> retrieveAllProjectsIncludingInactive() throws DAOException {
        List<Project> allProjectsList = new ArrayList<>();
        String queryGetAllProjects = 
            "SELECT Proyecto.idProyecto, Proyecto.nombre AS nombreProyecto, Proyecto.descripcion, " +
            "Proyecto.metodologia, Proyecto.estadoActividad, Proyecto.objetivos, Proyecto.fechaRegistro, Proyecto.cupo, " +
            "organizacionvinculada.IdOrganizacionVinculada, organizacionvinculada.nombreOrganizacion " +
            "FROM Proyecto Proyecto " +
            "INNER JOIN organizacionvinculada organizacionvinculada ON Proyecto.idOrganizacionvinculada = organizacionvinculada.IdOrganizacionVinculada"; 

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statementGet = connection.prepareStatement(queryGetAllProjects);
             ResultSet resultSet = statementGet.executeQuery()) {

            while (resultSet.next()) {
                Project project = new Project();
                project.setId(resultSet.getInt("idProyecto"));
                project.setName(resultSet.getString("nombreProyecto")); 
                project.setDescription(resultSet.getString("descripcion"));
                project.setMethodology(resultSet.getString("metodologia"));
                project.setObjective(resultSet.getString("objetivos"));
                project.setActivityStatus(resultSet.getBoolean("estadoActividad"));
                project.setAvailableSpaces(resultSet.getInt("cupo"));
                
                java.sql.Date dbDate = resultSet.getDate("fechaRegistro");
                if (dbDate != null) {
                    project.setRegistrationDate(dbDate.toLocalDate());
                }

                LinkedOrganization org = new LinkedOrganization();
                org.setId(resultSet.getInt("IdOrganizacionVinculada"));
                org.setName(resultSet.getString("nombreOrganizacion")); 
                
                project.setLinkedOrganization(org);
                allProjectsList.add(project);
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al recuperar el histórico total de proyectos", exceptionDB);
        }
        
        return allProjectsList;
    }

    @Override
    public Project getProjectById(int projectId) throws DAOException {
        String querySelectById = 
            "SELECT idProyecto, nombre, descripcion, metodologia, " +
            "estadoActividad, objetivos, fechaRegistro " +
            "FROM Proyecto " +
            "WHERE idProyecto = ?";
            
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySelectById)) {

            preparedStatement.setInt(1, projectId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
                return null;
            }

        } catch (SQLException exceptionDB) {
            LOG.log(Level.SEVERE, "Error crítico al buscar proyecto por ID", exceptionDB);
            throw new DAOException("Error al buscar el proyecto con ID: " + projectId, exceptionDB);
        }
    }

    @Override
    public boolean reactivateProject(int projectId) throws DAOException {
        String queryReactivateProject = "UPDATE Proyecto SET estadoActividad = 1 WHERE idProyecto = ?";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statementReactivate = connection.prepareStatement(queryReactivateProject)) {
            
            statementReactivate.setInt(1, projectId);
            int rowsAffected = statementReactivate.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al reactivar el proyecto en la base de datos", exceptionDB);
        }
    }
    
    private Project mapRow(ResultSet resultSet) throws SQLException {
        Project project = new Project();
        project.setId(resultSet.getInt("idProyecto"));
        project.setName(resultSet.getString("nombre"));
        project.setDescription(resultSet.getString("descripcion"));
        project.setMethodology(resultSet.getString("metodologia"));
        project.setActivityStatus(resultSet.getBoolean("estadoActividad"));
        project.setObjective(resultSet.getString("objetivos"));

        Date registrationDate = resultSet.getDate("fechaRegistro");
        if (registrationDate != null) {
            project.setRegistrationDate(registrationDate.toLocalDate());
        }

        return project;
    }

    @Override
    public boolean assignProjectToStudent(String matricula, int idProyecto) throws DAOException {
        String queryAssignProject = "UPDATE practicante SET idProyecto = ? WHERE matricula = ?";
        String queryDecrementCupo = "UPDATE proyecto SET cupo = cupo - 1 WHERE idProyecto = ?";
        String queryDeactivateIfFull = "UPDATE proyecto SET estadoActividad = 0 WHERE idProyecto = ? AND cupo = 0";

        try (Connection connection = ConfigDatabase.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statementAssign = connection.prepareStatement(queryAssignProject);
                 PreparedStatement statementDecrement = connection.prepareStatement(queryDecrementCupo);
                 PreparedStatement statementDeactivate = connection.prepareStatement(queryDeactivateIfFull)) {

                statementAssign.setInt(1, idProyecto);
                statementAssign.setString(2, matricula);
                statementAssign.executeUpdate();

                statementDecrement.setInt(1, idProyecto);
                statementDecrement.executeUpdate();

                statementDeactivate.setInt(1, idProyecto);
                statementDeactivate.executeUpdate();

                connection.commit();
                return true;

            } catch (SQLException exceptionDB) {
                connection.rollback();
                throw new DAOException("Error al asignar el proyecto al estudiante", exceptionDB);
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error de conexión al asignar proyecto", exceptionDB);
        }
    }
}