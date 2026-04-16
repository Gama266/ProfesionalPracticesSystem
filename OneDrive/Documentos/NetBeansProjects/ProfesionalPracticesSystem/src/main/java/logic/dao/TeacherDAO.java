/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import logic.businessObject.Teacher;
import logic.exceptions.DAOException;
import logic.idao.ITeacherDAO;

/**
 * Data Access Object for Teacher.
 * @author gamal
 */
public class TeacherDAO implements ITeacherDAO {

    @Override
    public boolean registerTeacher(Teacher newTeacher) throws DAOException {
        String queryRegisterTeacher = 
            "INSERT INTO profesor (numeroPersonal, contrasena, nombre, apellidoPaterno, apellidoMaterno, EstadoActividad) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentRegisterTeacher = 
                         connection.prepareStatement(queryRegisterTeacher)) {

                stamentRegisterTeacher.setInt(1, newTeacher.getNoPersonal());
                stamentRegisterTeacher.setString(2, newTeacher.getPassword());
                stamentRegisterTeacher.setString(3, newTeacher.getName());
                stamentRegisterTeacher.setString(4, newTeacher.getPaternalSurname());
                stamentRegisterTeacher.setString(5, newTeacher.getMaternalSurname());
                
                String status = newTeacher.isActivityStatus() ? "Active" : "Inactive";
                stamentRegisterTeacher.setString(6, status);

                int rowsAffected = stamentRegisterTeacher.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error registrando al profesor ", exceptionDB);
        }
    }

    @Override
    public boolean updateTeacher(Teacher teacher) throws DAOException {
        String queryUpdateTeacher = 
            "UPDATE profesor SET contrasena = ?, nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, EstadoActividad = ? " +
            "WHERE numeroPersonal = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentUpdateTeacher = 
                         connection.prepareStatement(queryUpdateTeacher)) {

                stamentUpdateTeacher.setString(1, teacher.getPassword());
                stamentUpdateTeacher.setString(2, teacher.getName());
                stamentUpdateTeacher.setString(3, teacher.getPaternalSurname());
                stamentUpdateTeacher.setString(4, teacher.getMaternalSurname());
                
                String status = teacher.isActivityStatus() ? "Active" : "Inactive";
                stamentUpdateTeacher.setString(5, status);
                
                stamentUpdateTeacher.setInt(6, teacher.getNoPersonal());

                int rowsAffected = stamentUpdateTeacher.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error actualizando al profesor", exceptionDB);
        }
    }
}