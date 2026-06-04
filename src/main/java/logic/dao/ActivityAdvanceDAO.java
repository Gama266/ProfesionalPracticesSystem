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
import java.util.logging.Logger;
import logic.businessObject.Activity; 
import logic.businessObject.ActivityAdvance;
import logic.exceptions.DAOException;
import logic.idao.IActivityAdvanceDAO;

/**
 *
 * @author gamal
 */

public class ActivityAdvanceDAO implements IActivityAdvanceDAO {
    private static final Logger logger = Logger.getLogger(ActivityAdvanceDAO.class.getName());

    @Override
    public List<ActivityAdvance> getAdvancesByReportId(int idReport) throws DAOException {
        List<ActivityAdvance> advances = new ArrayList<>();
        String queryGetAdvances = 
            "SELECT aa.idAvance, aa.idReporte, aa.idActividad, aa.porcentajeAvance, aa.semanasCubiertas, aa.observaciones, " +
            "act.nombre, act.descripcion " +
            "FROM AvanceActividad aa " +
            "INNER JOIN actividades act ON aa.idActividad = act.idActividad " +
            "WHERE aa.idReporte = ?";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statementGetAdvances = connection.prepareStatement(queryGetAdvances)) {

            statementGetAdvances.setInt(1, idReport);

            try (ResultSet resultSet = statementGetAdvances.executeQuery()) {
                while (resultSet.next()) {
                    advances.add(mapResultSetToActivityAdvance(resultSet));
                }
            }
            logger.info("Avances obtenidos correctamente para el reporte: " + idReport);

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al obtener los avances de actividades del reporte", exceptionDB);
        }

        return advances;
    }

    @Override
    public void registerAdvances(List<ActivityAdvance> advances, int reportId, Connection connection) throws SQLException {
        String queryRegisterAdvances =
            "INSERT INTO AvanceActividad (idReporte, idActividad, porcentajeAvance, semanasCubiertas, observaciones) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statementRegisterAdvances = connection.prepareStatement(queryRegisterAdvances)) {
            for (ActivityAdvance advance : advances) {
                statementRegisterAdvances.setInt(1, reportId);
                statementRegisterAdvances.setInt(2, advance.getActivity().getId());
                statementRegisterAdvances.setInt(3, advance.getProgressPercentage());
                statementRegisterAdvances.setString(4, advance.getCoveredWeeks());
                statementRegisterAdvances.setString(5, advance.getObservations());
                statementRegisterAdvances.addBatch();
            }
            statementRegisterAdvances.executeBatch();
            logger.info("Avances registrados correctamente para el reporte: " + reportId);
        }
    }

    private ActivityAdvance mapResultSetToActivityAdvance(ResultSet resultSet) throws SQLException {
        ActivityAdvance advance = new ActivityAdvance();
        advance.setId(resultSet.getInt("idAvance"));
        advance.setIdReport(resultSet.getInt("idReporte"));
        advance.setProgressPercentage(resultSet.getInt("porcentajeAvance"));
        advance.setCoveredWeeks(resultSet.getString("semanasCubiertas"));
        advance.setObservations(resultSet.getString("observaciones"));

        Activity activity = new Activity();
        activity.setId(resultSet.getInt("idActividad"));
        activity.setName(resultSet.getString("nombre"));
        activity.setDescription(resultSet.getString("descripcion"));

        advance.setActivity(activity);

        return advance;
    }
}