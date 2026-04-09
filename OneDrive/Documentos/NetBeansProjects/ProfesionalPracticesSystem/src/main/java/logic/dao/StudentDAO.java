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
import java.sql.Types;
import logic.businessObject.Student;




public class StudentDAO implements IStudentDAO {

    @Override
    public boolean registerStudent(Student newStudent) throws RuntimeException {

        String queryRegisterStudent =
            "INSERT INTO Estudiante (matricula, nombre, apellidoPaterno, apellidoMaterno, estadoActividad, password, idProyecto) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtRegisterStudent =
                         connection.prepareStatement(queryRegisterStudent)) {

                stmtRegisterStudent.setString(1, newStudent.getMatricula());
                stmtRegisterStudent.setString(2, newStudent.getName());
                stmtRegisterStudent.setString(3, newStudent.getPaternalSurname());
                stmtRegisterStudent.setString(4, newStudent.getMaternalSurname());
                stmtRegisterStudent.setBoolean(5, newStudent.getActivityStatus());
                stmtRegisterStudent.setString(6, newStudent.getPassword());

                if (newStudent.getProject() != null) {
                    stmtRegisterStudent.setInt(7, newStudent.getProject().getId());
                } else {
                    stmtRegisterStudent.setNull(7, Types.INTEGER);
                }

                int rowsAffected = stmtRegisterStudent.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al registrar el estudiante", exceptionDB);
        }
    }

    @Override
    public boolean updateStudent(Student student) throws RuntimeException {

        String queryUpdateStudent =
            "UPDATE Estudiante SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, estadoActividad = ?, password = ?, idProyecto = ? WHERE matricula = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtUpdateStudent =
                         connection.prepareStatement(queryUpdateStudent)) {

                stmtUpdateStudent.setString(1, student.getName());
                stmtUpdateStudent.setString(2, student.getPaternalSurname());
                stmtUpdateStudent.setString(3, student.getMaternalSurname());
                stmtUpdateStudent.setBoolean(4, student.getActivityStatus());
                stmtUpdateStudent.setString(5, student.getPassword());

                if (student.getProject() != null) {
                    stmtUpdateStudent.setInt(6, student.getProject().getId());
                } else {
                    stmtUpdateStudent.setNull(6, Types.INTEGER);
                }

                stmtUpdateStudent.setString(7, student.getMatricula());

                int rowsAffected = stmtUpdateStudent.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al actualizar el estudiante", exceptionDB);
        }
    }
}