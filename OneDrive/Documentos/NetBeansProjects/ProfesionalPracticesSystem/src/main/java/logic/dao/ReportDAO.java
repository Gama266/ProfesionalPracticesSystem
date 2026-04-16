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
import logic.exceptions.DAOException; 
import logic.idao.IReportDAO;

/**
 *
 * @author gamal
 */
public class ReportDAO implements IReportDAO {

    @Override
    public boolean registerReport(Report newReport) throws DAOException {

        String queryRegisterReport =
            "INSERT INTO Reporte (url, tipoReporte, matricula) VALUES (?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentRegisterReport =
                         connection.prepareStatement(queryRegisterReport)) {

                stamentRegisterReport.setString(1, newReport.getUrl());
                stamentRegisterReport.setString(2, newReport.getTypeOfReport());

                if (newReport.getStudent() != null) {
                    stamentRegisterReport.setString(3, newReport.getStudent().getMatricula());
                } else {
                    stamentRegisterReport.setNull(3, Types.VARCHAR); 
                }

                int rowsAffected = stamentRegisterReport.executeUpdate(); 
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error registrando el reporte", exceptionDB);
        }
    }

    @Override
    public boolean updateReport(Report report) throws DAOException {

        String queryUpdateReport =
            "UPDATE Reporte SET url = ?, tipoReporte = ?, matricula = ? WHERE id = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentUpdateReport =
                         connection.prepareStatement(queryUpdateReport)) {

                stamentUpdateReport.setString(1, report.getUrl());
                stamentUpdateReport.setString(2, report.getTypeOfReport());

                if (report.getStudent() != null) {
                    stamentUpdateReport.setString(3, report.getStudent().getMatricula());
                } else {
                    stamentUpdateReport.setNull(3, Types.VARCHAR); 
                }

                stamentUpdateReport.setInt(4, report.getId());

                int rowsAffected = stamentUpdateReport.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error actualizando el proyecto", exceptionDB);
        }
    }
}