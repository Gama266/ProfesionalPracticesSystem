/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
@(#)ActividadDAO.java 1.0 04/04/2026
Copyright (c) 2026 JhonatanYerayLIS
*/
package logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dataacces.ConfigDatabase;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import logic.businessObject.Activity;
import logic.businessObject.Project;
import logic.exceptions.DAOException;
import logic.idao.IActivityDAO;
import java.util.logging.Logger;

public class ActivityDAO implements IActivityDAO {

    private static final Logger logger = Logger.getLogger(ActivityDAO.class.getName());

    @Override
    public boolean registerActivity(Activity activity) throws DAOException {
        String sql =
            "INSERT INTO actividades (idProyecto, nombre, descripcion, fechaInicio, fechaFin, horasPlaneadas) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, activity.getProject().getId());
            preparedStatement.setString(2, activity.getName());
            preparedStatement.setString(3, activity.getDescription());
            preparedStatement.setDate(4, java.sql.Date.valueOf(activity.getStartDate()));
            preparedStatement.setDate(5, java.sql.Date.valueOf(activity.getEndDate()));
            preparedStatement.setDouble(6, activity.getPlannedHours());

            int rows = preparedStatement.executeUpdate();
            logger.info("Actividad registrada correctamente");
            return rows > 0;

        } catch (SQLException e) {
            throw new DAOException("Error al registrar la actividad", e);
        }
    }

    @Override
    public boolean registerActivities(List<Activity> activities) throws DAOException {
        String sql =
            "INSERT INTO actividades (idProyecto, nombre, descripcion, fechaInicio, fechaFin, horasPlaneadas) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            for (Activity activity : activities) {
                preparedStatement.setInt(1, activity.getProject().getId());
                preparedStatement.setString(2, activity.getName());
                preparedStatement.setString(3, activity.getDescription());
                preparedStatement.setDate(4, java.sql.Date.valueOf(activity.getStartDate()));
                preparedStatement.setDate(5, java.sql.Date.valueOf(activity.getEndDate()));
                preparedStatement.setDouble(6, activity.getPlannedHours());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            logger.info("Actividades registradas correctamente — total: " + activities.size());
            return true;

        } catch (SQLException e) {
            throw new DAOException("Error al registrar las actividades", e);
        }
    }

    @Override
    public List<Activity> getActivitiesByProject(int idProyecto) throws DAOException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM actividades WHERE idProyecto = ? ORDER BY fechaInicio";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, idProyecto);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    activities.add(mapResultSetToActivity(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener actividades del proyecto", e);
        }
        return activities;
    }

    @Override
    public List<Activity> getAll() throws DAOException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM actividades ORDER BY fechaInicio DESC";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                activities.add(mapResultSetToActivity(resultSet));
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener las actividades", e);
        }
        return activities;
    }

    @Override
    public List<Activity> getByDateRange(LocalDate startDate, LocalDate endDate) throws DAOException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM actividades WHERE fechaInicio BETWEEN ? AND ? ORDER BY fechaInicio";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDate(1, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    activities.add(mapResultSetToActivity(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener las actividades por rango de fecha", e);
        }
        return activities;
    }

    @Override
    public List<Activity> getByProjectAndDateRange(int idProyecto, LocalDate startDate, LocalDate endDate) throws DAOException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM actividades WHERE idProyecto = ? " +
                     "AND fechaInicio BETWEEN ? AND ? ORDER BY fechaInicio";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, idProyecto);
            preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(3, java.sql.Date.valueOf(endDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    activities.add(mapResultSetToActivity(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener actividades del proyecto por rango de fecha", e);
        }
        return activities;
    }

    @Override
    public int countActivitiesByProject(int idProyecto) throws DAOException {
        String sql = "SELECT COUNT(*) FROM actividades WHERE idProyecto = ?";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, idProyecto);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al contar actividades del proyecto", e);
        }
        return 0;
    }

    @Override
    public boolean updateActivity(Activity activity) throws DAOException {
        String sql = "UPDATE actividades SET nombre = ?, descripcion = ?, " +
                     "fechaInicio = ?, fechaFin = ?, horasPlaneadas = ? " +
                     "WHERE idActividad = ?";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, activity.getName());
            preparedStatement.setString(2, activity.getDescription());
            preparedStatement.setDate(3, java.sql.Date.valueOf(activity.getStartDate()));
            preparedStatement.setDate(4, java.sql.Date.valueOf(activity.getEndDate()));
            preparedStatement.setDouble(5, activity.getPlannedHours());
            preparedStatement.setInt(6, activity.getId());

            int rows = preparedStatement.executeUpdate();
            logger.info("Actividad actualizada correctamente");
            return rows > 0;

        } catch (SQLException e) {
            throw new DAOException("Error al actualizar la actividad", e);
        }
    }

    @Override
    public boolean deleteActivity(int idActividad) throws DAOException {
        String sql = "DELETE FROM actividades WHERE idActividad = ?";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, idActividad);
            int rows = preparedStatement.executeUpdate();
            logger.info("Actividad eliminada correctamente");
            return rows > 0;

        } catch (SQLException e) {
            throw new DAOException("Error al eliminar la actividad", e);
        }
    }

    private Activity mapResultSetToActivity(ResultSet resultSet) throws SQLException {
        Activity activity = new Activity();
        activity.setId(resultSet.getInt("idActividad"));
        activity.setName(resultSet.getString("nombre"));
        activity.setDescription(resultSet.getString("descripcion"));
        activity.setStartDate(resultSet.getDate("fechaInicio").toLocalDate());
        activity.setEndDate(resultSet.getDate("fechaFin").toLocalDate());
        activity.setPlannedHours(resultSet.getDouble("horasPlaneadas"));

        Project project = new Project();
        project.setId(resultSet.getInt("idProyecto"));
        activity.setProject(project);

        return activity;
    }
    
@Override
public double getTotalHoursByProject(int projectId)
        throws DAOException {

    String sql =
        "SELECT SUM(horasPlaneadas) AS totalHours " +
        "FROM actividades " +
        "WHERE idProyecto = ?";

    try (
        Connection connection =
                ConfigDatabase.getConnection();

        PreparedStatement preparedStatement =
                connection.prepareStatement(sql)
    ) {

        preparedStatement.setInt(1, projectId);

        try (
            ResultSet resultSet =
                    preparedStatement.executeQuery()
        ) {

            if (resultSet.next()) {

                return resultSet.getDouble("totalHours");
            }
        }

    } catch (SQLException e) {

        throw new DAOException(
            "Error calculating total hours",
            e
        );
    }

    return 0;
}

public Map<Integer, Integer> getMaxProgressByProject(int idProyecto) throws DAOException {
    Map<Integer, Integer> progressMap = new HashMap<>();
    String sql =
        "SELECT aa.idActividad, MAX(aa.porcentajeAvance) AS maxAvance " +
        "FROM AvanceActividad aa " +
        "INNER JOIN actividades a ON aa.idActividad = a.idActividad " +
        "WHERE a.idProyecto = ? " +
        "GROUP BY aa.idActividad";

    try (Connection connection = ConfigDatabase.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setInt(1, idProyecto);
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                progressMap.put(
                    resultSet.getInt("idActividad"),
                    resultSet.getInt("maxAvance")
                );
            }
        }
    } catch (SQLException e) {
        throw new DAOException("Error al obtener el avance de actividades", e);
    }
    return progressMap;
}



}