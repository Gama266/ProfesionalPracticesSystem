/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

/**
 *
 * @author gamal
 */

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import logic.businessObject.Teacher;




public class TeacherDAO implements ITeacherDAO {

    @Override
    public boolean registerTeacher(Teacher newTeacher) throws RuntimeException {

        String queryRegisterTeacher =
            "INSERT INTO Teacher (noPersonal, password, paternaSurname, maternalSurname, activityStatus) " +
            "VALUES (?, ?, ?, ?, ?)";

        try {
           Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtRegisterTeacher =
                         connection.prepareStatement(queryRegisterTeacher)) {

                stmtRegisterTeacher.setInt(1, newTeacher.getNoPersonal());
                stmtRegisterTeacher.setString(2, newTeacher.getPassword());
                stmtRegisterTeacher.setString(3, newTeacher.getPaternaSurname());
                stmtRegisterTeacher.setString(4, newTeacher.getMaternalSurname());
                stmtRegisterTeacher.setBoolean(5, newTeacher.getActivityStatus());

                int rowsAffected = stmtRegisterTeacher.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al registrar el profesor", exceptionDB);
        }
    }

    @Override
    public boolean updateTeacher(Teacher teacher) throws RuntimeException {

        String queryUpdateTeacher =
            "UPDATE Teacher SET password = ?, paternaSurname = ?, maternalSurname = ?, activityStatus = ? " +
            "WHERE noPersonal = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtUpdateTeacher =
                         connection.prepareStatement(queryUpdateTeacher)) {

                stmtUpdateTeacher.setString(1, teacher.getPassword());
                stmtUpdateTeacher.setString(2, teacher.getPaternaSurname());
                stmtUpdateTeacher.setString(3, teacher.getMaternalSurname());
                stmtUpdateTeacher.setBoolean(4, teacher.getActivityStatus());
                stmtUpdateTeacher.setInt(5, teacher.getNoPersonal());

                int rowsAffected = stmtUpdateTeacher.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al actualizar el profesor", exceptionDB);
        }
    }
}