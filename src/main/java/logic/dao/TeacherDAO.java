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
import logic.businessObject.Teacher;
import logic.exceptions.DAOException;
import logic.idao.ITeacherDAO;

/**
 * Se debe modificar por el cambio
 * @author gamal
 */

public class TeacherDAO implements ITeacherDAO {

 
    @Override
    public boolean registerTeacher(Teacher newTeacher) throws DAOException {
        
        String queryRegisterTeacher = 
            "INSERT INTO profesor " +
            "(numeroPersonal, nombre, apellidoPaterno, apellidoMaterno, " +
            "rol, estadoActividad, idUsuario) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement stamentRegisterTeacher = 
                     connection.prepareStatement(queryRegisterTeacher)) {

            stamentRegisterTeacher.setInt(1, newTeacher.getNoPersonal());
            stamentRegisterTeacher.setString(2, newTeacher.getName());
            stamentRegisterTeacher.setString(3, newTeacher.getPaternalSurname());
            stamentRegisterTeacher.setString(4, newTeacher.getMaternalSurname());          
            stamentRegisterTeacher.setString(5, newTeacher.getRole());
            stamentRegisterTeacher.setBoolean(6, newTeacher.getActivityStatus());
            stamentRegisterTeacher.setInt(7, newTeacher.getIdUser());

            int rowsAffected = stamentRegisterTeacher.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error registrando al profesor", exceptionDB);
        }
    }

    
    @Override
    public boolean updateTeacher(Teacher teacher) throws DAOException {
        String queryUpdateTeacher = 
            "UPDATE profesor " +
            "SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, " +
            "rol = ?, estadoActividad = ? " +
            "WHERE numeroPersonal = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentUpdateTeacher = 
                         connection.prepareStatement(queryUpdateTeacher)) {
                stamentUpdateTeacher.setString(1, teacher.getName());
                stamentUpdateTeacher.setString(2, teacher.getPaternalSurname());
                stamentUpdateTeacher.setString(3, teacher.getMaternalSurname());
                stamentUpdateTeacher.setString(4, teacher.getRole());                              
                stamentUpdateTeacher.setBoolean(5, teacher.getActivityStatus());
                stamentUpdateTeacher.setInt(6, teacher.getNoPersonal());

                int rowsAffected = stamentUpdateTeacher.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error actualizando al profesor", exceptionDB);
        }
    }
        
   
    @Override
    public boolean deleteTeacher(int noPersonal) throws DAOException {
        
        String queryDeleteTeacher = 
            "DELETE FROM profesor WHERE numeroPersonal = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentDeleteTeacher = 
                         connection.prepareStatement(queryDeleteTeacher)) {

                stamentDeleteTeacher.setInt(1, noPersonal);

                int rowsAffected = stamentDeleteTeacher.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error eliminando al profesor", exceptionDB);
        }
    }
    
    /**
     * Retrieves a list of all teachers registered in the database.
     * @return List of Teacher objects.
     * @throws DAOException if a database error occurs.
     */
    @Override
    public List<Teacher> getAllTeachers() throws DAOException {
        String queryGetAll =         
            "SELECT numeroPersonal, nombre, apellidoPaterno, apellidoMaterno, " +
            "rol, estadoActividad " +
            "FROM profesor ORDER BY apellidoPaterno, nombre";
            
        List<Teacher> list = new ArrayList<>();
        
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = 
                     connection.prepareStatement(queryGetAll);
             ResultSet resultSet = preparedStatement.executeQuery()) {
 
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setNoPersonal(resultSet.getInt("numeroPersonal"));
                teacher.setName(resultSet.getString("nombre"));
                teacher.setPaternalSurname(resultSet.getString("apellidoPaterno"));
                teacher.setMaternalSurname(resultSet.getString("apellidoMaterno"));
                teacher.setRole(resultSet.getString("rol"));

                teacher.setActivityStatus(resultSet.getBoolean("estadoActividad"));
                
                list.add(teacher);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener lista de profesores", e);
        }
        return list;
    }
    
 
    @Override
    public boolean deactivateTeacher(int noPersonal) throws DAOException {
        
        String queryDeactivate = 
            "UPDATE profesor SET estadoActividad = false WHERE numeroPersonal = ?";
            
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = 
                     connection.prepareStatement(queryDeactivate)) {
 
                preparedStatement.setInt(1, noPersonal);
                return preparedStatement.executeUpdate() > 0;
 
        } catch (SQLException e) {
            throw new DAOException("Error al inactivar al profesor", e);
        }
    
    }
}