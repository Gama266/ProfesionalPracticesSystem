/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import logic.businessObject.Report;

/**
 *
 * @author gamal
 */
public class ReportDAO implements IReportDAO {

    @Override
    public boolean registerReport(Report newReport) throws RuntimeException {

        String queryRegisterReport =
            "INSERT INTO Reporte (url, tipoReporte, matricula) VALUES (?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtRegisterReport =
                         connection.prepareStatement(queryRegisterReport)) {

                stmtRegisterReport.setString(1, newReport.getUrl());
                stmtRegisterReport.setString(2, newReport.getTypeOfReport());

                if (newReport.getStudent() != null) {
                    stmtRegisterReport.setString(3, newReport.getStudent().getMatricula());
                } else {
                    stmtRegisterReport.setNull(3, Types.VARCHAR);
                }

                int rowsAffected = stmtRegisterReport.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al registrar el reporte", exceptionDB);
        }
    }

    @Override
    public boolean updateReport(Report report) throws RuntimeException {

        String queryUpdateReport =
            "UPDATE Reporte SET url = ?, tipoReporte = ?, matricula = ? WHERE id = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtUpdateReport =
                         connection.prepareStatement(queryUpdateReport)) {

                stmtUpdateReport.setString(1, report.getUrl());
                stmtUpdateReport.setString(2, report.getTypeOfReport());

                if (report.getStudent() != null) {
                    stmtUpdateReport.setString(3, report.getStudent().getMatricula());
                } else {
                    stmtUpdateReport.setNull(3, Types.VARCHAR);
                }

                stmtUpdateReport.setInt(4, report.getId());

                int rowsAffected = stmtUpdateReport.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al actualizar el reporte", exceptionDB);
        }
    }

   
}