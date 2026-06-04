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
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import logic.businessObject.Report;
import logic.exceptions.DAOException;
import logic.idao.IReportDAO;

public class ReportDAO implements IReportDAO {

    private static final Logger logger = Logger.getLogger(ReportDAO.class.getName());
    private final ActivityAdvanceDAO activityAdvanceDAO = new ActivityAdvanceDAO();

    @Override
    public boolean registerReport(Report newReport) throws DAOException {
        boolean isRegistered = false;
        String queryRegisterReport =
            "INSERT INTO Reporte (tipoReporte, matricula, idProyecto, horasReportadas, fechaEntrega, estado, URL) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();
            try (PreparedStatement statementRegisterReport = connection.prepareStatement(queryRegisterReport, Statement.RETURN_GENERATED_KEYS)) {

                statementRegisterReport.setString(1, newReport.getTypeOfReport());
                statementRegisterReport.setString(2, newReport.getStudent().getMatricula());
                statementRegisterReport.setInt(3, newReport.getProject().getId());
                statementRegisterReport.setDouble(4, newReport.getReportedHours());
                statementRegisterReport.setDate(5, java.sql.Date.valueOf(newReport.getDeliveryDate()));
                statementRegisterReport.setString(6, newReport.getStatus());
                statementRegisterReport.setObject(7, newReport.getUrl(), Types.VARCHAR);

                int rowsAffected = statementRegisterReport.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = statementRegisterReport.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int reportId = generatedKeys.getInt(1);
                            activityAdvanceDAO.registerAdvances(newReport.getActivityAdvances(), reportId, connection);
                            isRegistered = true;
                        }
                    }
                }
                logger.info("Reporte registrado correctamente");
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al registrar el reporte", exceptionDB);
        }

        return isRegistered;
    }

    @Override
    public boolean updateReport(Report report) throws DAOException {
        boolean isUpdated = false;
        String queryUpdateReport = "UPDATE Reporte SET estado = ?, URL = ? WHERE idReporte = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();
            try (PreparedStatement statementUpdateReport = connection.prepareStatement(queryUpdateReport)) {

                statementUpdateReport.setString(1, report.getStatus());
                statementUpdateReport.setObject(2, report.getUrl(), Types.VARCHAR);
                statementUpdateReport.setInt(3, report.getId());

                int rowsAffected = statementUpdateReport.executeUpdate();

                if (rowsAffected > 0) {
                    isUpdated = true;
                }
                logger.info("Reporte actualizado correctamente: " + report.getId());
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al actualizar el reporte", exceptionDB);
        }

        return isUpdated;
    }

    public double getAccumulatedApprovedHours(String matricula) throws DAOException {
        double accumulatedHours = 0.0;
        String queryGetAccumulatedHours =
            "SELECT SUM(horasReportadas) FROM Reporte " +
            "WHERE matricula = ? AND tipoReporte = 'MENSUAL' AND estado = 'APROBADO'";

        try {
            Connection connection = ConfigDatabase.getConnection();
            try (PreparedStatement statementGetHours = connection.prepareStatement(queryGetAccumulatedHours)) {

                statementGetHours.setString(1, matricula);

                try (ResultSet resultSet = statementGetHours.executeQuery()) {
                    if (resultSet.next()) {
                        accumulatedHours = resultSet.getDouble(1);
                    }
                }
                logger.info("Horas acumuladas obtenidas correctamente para: " + matricula);
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al calcular horas aprobadas", exceptionDB);
        }

        return accumulatedHours;
    }
    
    @Override
    public List<Report> getReportsByStudent(String matricula) throws DAOException {
    List<Report> reports = new ArrayList<>();
    String query =
        "SELECT idReporte, tipoReporte, horasReportadas, fechaEntrega, estado, URL " +
        "FROM Reporte WHERE matricula = ? ORDER BY fechaEntrega DESC";

    try (Connection connection = ConfigDatabase.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setString(1, matricula);
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Report report = new Report();
                report.setId(resultSet.getInt("idReporte"));
                report.setTypeOfReport(resultSet.getString("tipoReporte"));
                report.setReportedHours(resultSet.getDouble("horasReportadas"));
                report.setDeliveryDate(resultSet.getDate("fechaEntrega").toLocalDate());
                report.setStatus(resultSet.getString("estado"));
                report.setUrl(resultSet.getString("URL"));
                reports.add(report);
            }
        }
    } catch (SQLException e) {
        throw new DAOException("Error al obtener reportes del estudiante", e);
    }
    return reports;
}
    @Override
public boolean hasApprovedPartialReport(String matricula) throws DAOException {
    String query =
        "SELECT COUNT(*) FROM Reporte " +
        "WHERE matricula = ? AND tipoReporte = 'PARCIAL' AND estado = 'APROBADO'";

    try {
        Connection connection = ConfigDatabase.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, matricula);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                    return resultSet.getInt(1) > 0;
                }
            }
        }
    } catch (SQLException exceptionDB) {
        throw new DAOException("Error al verificar reporte parcial aprobado", exceptionDB);
    }
    return false;
}
}