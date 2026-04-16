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
import java.sql.Types;
import logic.businessObject.Project;
import logic.exceptions.DAOException;
import logic.idao.IProjectDAO;

/**
 *
 * @author gamal
 * @version 1.0
 */
public class ProjectDAO implements IProjectDAO {

    @Override
    public boolean registerProject(Project newProject) throws DAOException {

        String queryRegisterProject =
            "INSERT INTO Proyecto (nombre, descripcion, metodologia, estadoActividad, objetivos, `fecha registro`, idOrganizacionVinculada, noPersonal, idResponsableTecnico) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentRegisterProject =
                         connection.prepareStatement(queryRegisterProject)) {

                stamentRegisterProject.setString(1, newProject.getName());
                stamentRegisterProject.setString(2, newProject.getDescription());
                stamentRegisterProject.setString(3, newProject.getMethodology()); 
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

        
                if (newProject.getTechnicalResponsible() != null) {
                    stamentRegisterProject.setInt(9, newProject.getTechnicalResponsible().getId());
                } else {
                    stamentRegisterProject.setNull(9, Types.INTEGER);
                }

                int rowsAffected = stamentRegisterProject.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error registrando el proyecto", exceptionDB);
        }
    }

    @Override
    public boolean updateProject(Project project) throws DAOException {

        String queryUpdateProject =
            "UPDATE Proyecto SET nombre = ?, descripcion = ?, metodologia = ?, estadoActividad = ?, objetivos = ?, `fecha registro` = ?, idOrganizacionVinculada = ?, noPersonal = ?, idResponsableTecnico = ? WHERE idProyecto = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentUpdateProject =
                         connection.prepareStatement(queryUpdateProject)) {

                stamentUpdateProject.setString(1, project.getName());
                stamentUpdateProject.setString(2, project.getDescription());
                stamentUpdateProject.setString(3, project.getMethodology());
                stamentUpdateProject.setBoolean(4, project.isActivityStatus());
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

                stamentUpdateProject.setInt(10, project.getId());

                int rowsAffected = stamentUpdateProject.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error actualizando el proyecto", exceptionDB);
        }
    }
}