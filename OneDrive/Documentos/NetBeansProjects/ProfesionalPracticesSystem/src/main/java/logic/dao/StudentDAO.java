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
import logic.businessObject.Student;
import logic.exceptions.DAOException;
import logic.idao.IStudentDAO;

/**
 * * @author gamal
 */
public class StudentDAO implements IStudentDAO {

    @Override
    public boolean registerStudent(Student newStudent) throws DAOException {
       
        String queryRegisterStudent =
            "INSERT INTO practicante (matricula, nombre, apellidoPaterno, apellidoMaterno, estadoActividad, contrasena, idProyecto) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentRegisterStudent =
                         connection.prepareStatement(queryRegisterStudent)) {

                stamentRegisterStudent.setString(1, newStudent.getMatricula());
                stamentRegisterStudent.setString(2, newStudent.getName());
                stamentRegisterStudent.setString(3, newStudent.getPaternalSurname());
                stamentRegisterStudent.setString(4, newStudent.getMaternalSurname());
                
              
                String status = newStudent.getActivityStatus() ? "Active" : "Inactive";
                stamentRegisterStudent.setString(5, status);
                
                stamentRegisterStudent.setString(6, newStudent.getPassword());

                if (newStudent.getProject() != null) {
                    stamentRegisterStudent.setInt(7, newStudent.getProject().getId());
                } else {
                    stamentRegisterStudent.setNull(7, Types.INTEGER);
                }

                int rowsAffected = stamentRegisterStudent.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error registrando al estudiante", exceptionDB);
        }
    }

    @Override
    public boolean updateStudent(Student student) throws DAOException {
        
        String queryUpdateStudent =
            "UPDATE practicante SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, estadoActividad = ?, contrasena = ?, idProyecto = ? WHERE matricula = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentUpdateStudent =
                         connection.prepareStatement(queryUpdateStudent)) {

                stamentUpdateStudent.setString(1, student.getName());
                stamentUpdateStudent.setString(2, student.getPaternalSurname());
                stamentUpdateStudent.setString(3, student.getMaternalSurname());
                
                String status = student.getActivityStatus() ? "Active" : "Inactive";
                stamentUpdateStudent.setString(4, status);
                
                stamentUpdateStudent.setString(5, student.getPassword());

                if (student.getProject() != null) {
                    stamentUpdateStudent.setInt(6, student.getProject().getId());
                } else {
                    stamentUpdateStudent.setNull(6, Types.INTEGER);
                }

                stamentUpdateStudent.setString(7, student.getMatricula());

                int rowsAffected = stamentUpdateStudent.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error actualizando las modificaciones", exceptionDB);
        }
        
    }
    
    @Override
    public boolean deleteStudent(String matricula) throws DAOException {
        
        String queryDeleteStudent = "DELETE FROM practicante WHERE matricula = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement statementDeleteStudent = 
                         connection.prepareStatement(queryDeleteStudent)) {

                statementDeleteStudent.setString(1, matricula);

                int rowsAffected = statementDeleteStudent.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error eliminando al estudiante", exceptionDB);
        }
    }
}