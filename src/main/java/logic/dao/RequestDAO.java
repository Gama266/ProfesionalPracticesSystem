/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import logic.businessObject.LinkedOrganization;
import logic.businessObject.Project;
import logic.businessObject.TechnicalResponsible;
import logic.exceptions.DAOException;
import logic.idao.IRequestDAO;

public class RequestDAO implements IRequestDAO {

    @Override
    public int getRequestCountByPracticante(String matricula) throws DAOException {
        String queryCount = "SELECT COUNT(*) FROM solicitud WHERE matricula = ? AND estado = 'Pendiente'";
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryCount)) {

            statement.setString(1, matricula);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new DAOException("Error al contar solicitudes del practicante", exception);
        }
        return 0;
    }

    @Override
    public boolean hasAlreadyRequested(String matricula, int idProyecto) throws DAOException {
        String queryCheck = "SELECT COUNT(*) FROM solicitud WHERE matricula = ? AND idProyecto = ?";
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryCheck)) {

            statement.setString(1, matricula);
            statement.setInt(2, idProyecto);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException exception) {
            throw new DAOException("Error al verificar si ya existe la solicitud", exception);
        }
        return false;
    }

    @Override
    public boolean registerRequest(String matricula, int idProyecto) throws DAOException {
        String queryInsert = "INSERT INTO solicitud (matricula, idProyecto, fechaSolicitud, estado) VALUES (?, ?, CURDATE(), 'Pendiente')";
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryInsert)) {

            statement.setString(1, matricula);
            statement.setInt(2, idProyecto);
            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new DAOException("Error al registrar la solicitud", exception);
        }
    }

    @Override
    public List<Project> getProjectsRequestedByStudent(String matricula) throws DAOException {
        String queryGetProjects =
            "SELECT pr.idProyecto, pr.nombre AS nombreProyecto, pr.descripcion, pr.cupo, " +
            "ov.IdOrganizacionVinculada, ov.nombreOrganizacion, " +
            "rt.idResponsableTecnico, rt.nombre AS nombreRT, rt.apellidoPaterno AS apPaternoRT " +
            "FROM proyecto pr " +
            "INNER JOIN solicitud s ON pr.idProyecto = s.idProyecto " +
            "INNER JOIN organizacionvinculada ov ON pr.idOrganizacionVinculada = ov.IdOrganizacionVinculada " +
            "LEFT JOIN responsabletecnico rt ON pr.idResponsableTecnico = rt.idResponsableTecnico " +
            "WHERE s.matricula = ? AND s.estado = 'Pendiente'";

        List<Project> projects = new ArrayList<>();

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryGetProjects)) {

            statement.setString(1, matricula);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Project project = new Project();
                    project.setId(resultSet.getInt("idProyecto"));
                    project.setName(resultSet.getString("nombreProyecto"));
                    project.setDescription(resultSet.getString("descripcion"));
                    project.setAvailableSpaces(resultSet.getInt("cupo"));

                    LinkedOrganization org = new LinkedOrganization();
                    org.setId(resultSet.getInt("IdOrganizacionVinculada"));
                    org.setName(resultSet.getString("nombreOrganizacion"));
                    project.setLinkedOrganization(org);

                    TechnicalResponsible tr = new TechnicalResponsible();
                    tr.setId(resultSet.getInt("idResponsableTecnico"));
                    tr.setName(resultSet.getString("nombreRT"));
                    tr.setPaternalSurname(resultSet.getString("apPaternoRT"));
                    project.setTechnicalResponsible(tr);

                    projects.add(project);
                }
            }
        } catch (SQLException exception) {
            throw new DAOException("Error al obtener proyectos solicitados por el estudiante", exception);
        }
        return projects;
    }

    @Override
    public boolean approveRequest(String matricula, int idProyecto) throws DAOException {
        String queryApprove = "UPDATE solicitud SET estado = 'Aprobada' WHERE matricula = ? AND idProyecto = ?";
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryApprove)) {

            statement.setString(1, matricula);
            statement.setInt(2, idProyecto);
            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new DAOException("Error al aprobar la solicitud", exception);
        }
    }

    @Override
    public boolean rejectRemainingRequests(String matricula, int approvedIdProyecto) throws DAOException {
        String queryRejectRemaining = "UPDATE solicitud SET estado = 'Rechazada' WHERE matricula = ? AND idProyecto != ?";
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryRejectRemaining)) {

            statement.setString(1, matricula);
            statement.setInt(2, approvedIdProyecto);
            statement.executeUpdate();
            return true;

        } catch (SQLException exception) {
            throw new DAOException("Error al rechazar solicitudes restantes", exception);
        }
    }

  @Override
public boolean rejectAllRequestsForStudent(String matricula) throws DAOException {

    String queryDeleteAll = "DELETE FROM solicitud WHERE matricula = ? AND estado = 'Pendiente'";
    try (Connection connection = ConfigDatabase.getConnection();
         PreparedStatement statement = connection.prepareStatement(queryDeleteAll)) {

        statement.setString(1, matricula);
        statement.executeUpdate();
        return true;

    } catch (SQLException exception) {
        throw new DAOException("Error al invalidar las solicitudes del estudiante", exception);
    }
}
}