/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

/**
 *
 * @author gamal
 * @version1.0
 */

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Types;
import logic.businessObject.Project;



public class ProjectDAO implements IProjectDAO {

    @Override
    public boolean registerProject(Project newProject) throws RuntimeException {

        String queryRegisterProject =
            "INSERT INTO Proyecto (nombre, descripcion, metodologia, estadoActividad, objetivos, `fecha registro`, idOrganizacionVinculada, noPersonal, idResponsableTecnico) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentRegisterProject =
                         connection.prepareStatement(queryRegisterProject)) {

                stamentRegisterProject.setString(1, newProject.getName());
                stamentRegisterProject.setString(2, newProject.getDescription());
                stamentRegisterProject.setString(3, newProject.getMetodology());
                stamentRegisterProject.setBoolean(4, newProject.isActivityStatus());
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

                if (newProject.getIdTechnicalResponsable()) {
                    stamentRegisterProject.setInt(9, newProject.getIdTechnicalResponsable());
                } else {
                    stamentRegisterProject.setNull(9, Types.INTEGER);
                }

                int rowsAffected = stmtRegisterProject.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al registrar el proyecto", exceptionDB);
        }
    }

    @Override
    public boolean updateProject(Project project) throws RuntimeException {

        String queryUpdateProject =
            "UPDATE Proyecto SET nombre = ?, descripcion = ?, metodologia = ?, estadoActividad = ?, objetivos = ?, `fecha registro` = ?, idOrganizacionVinculada = ?, noPersonal = ?, idResponsableTecnico = ? WHERE idProyecto = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtUpdateProject =
                         connection.prepareStatement(queryUpdateProject)) {

                stmtUpdateProject.setString(1, project.getName());
                stmtUpdateProject.setString(2, project.getDescription());
                stmtUpdateProject.setString(3, project.getMetodology());
                stmtUpdateProject.setBoolean(4, project.isActivityStatus());
                stmtUpdateProject.setString(5, project.getObjective());

                if (project.getRegistrationDate() != null) {
                    stmtUpdateProject.setDate(6, Date.valueOf(project.getRegistrationDate()));
                } else {
                    stmtUpdateProject.setNull(6, Types.DATE);
                }

                if (project.getLinkedOrganization() != null) {
                    stmtUpdateProject.setInt(7, project.getLinkedOrganization().getId());
                } else {
                    stmtUpdateProject.setNull(7, Types.INTEGER);
                }

                if (project.getTeacher() != null) {
                    stmtUpdateProject.setInt(8, project.getTeacher().getNoPersonal());
                } else {
                    stmtUpdateProject.setNull(8, Types.INTEGER);
                }

                if (project.getIdResponsableTecnico()) {
                    stmtUpdateProject.setInt(9, project.getIdResponsableTecnico());
                } else {
                    stmtUpdateProject.setNull(9, Types.INTEGER);
                }

                stmtUpdateProject.setInt(10, project.getId());

                int rowsAffected = stmtUpdateProject.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al actualizar el proyecto", exceptionDB);
        }
    }
}