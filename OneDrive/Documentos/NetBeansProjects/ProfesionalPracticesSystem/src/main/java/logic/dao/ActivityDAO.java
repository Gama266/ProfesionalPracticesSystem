/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
@(#)ActividadDAO.java 1.0 04/04/2026
Copyright (c) 2026 JhonatanYerayLIS
*/
package logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import dataacces.ConnectionDatabase;
import logic.businessObject.Activity;
import logic.businessObject.Student;
import logic.exceptions.DAOException;
import logic.idao.IActivityDAO;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class ActivityDAO implements IActivityDAO{

    @Override
    public boolean registerActivity(Activity activity) throws DAOException {
      String sql = "INSERT INTO actividad (fecha, horas, descripcion, matricula) VALUES (?, ?, ?,?)";
        
        try (Connection connection = ConnectionDatabase.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            java.sql.Date dateSQL = java.sql.Date.valueOf(activity.getDate());
            preparedStatement.setDate(1,dateSQL);
            preparedStatement.setDouble(2, activity.getHours());
            preparedStatement.setString(3, activity.getDescription());
            preparedStatement.setString(4, activity.getStudent().getMatricula());
            
            int rows = preparedStatement.executeUpdate();
            System.out.println("Registro de Actividad correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
            throw  new DAOException("Error de registro", e);
        }
    }

    @Override
    public List<Activity> getAll() throws DAOException {
        List<Activity> activities = new ArrayList<>();
    String sql = "SELECT * FROM actividad ORDER BY fechaActividad DESC";
    
    try (Connection connection = ConnectionDatabase.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery()) {
        
        while (resultSet.next()) {
            Activity activity = new Activity();
            
            activity.setId(resultSet.getInt("idActividad"));
            
            activity.setDate(resultSet.getDate("fechaActividad").toLocalDate());
            
            activity.setHours(resultSet.getFloat("horas"));
            
            activity.setDescription(resultSet.getString("descripcion"));
            
            Student student = new Student();
            student.setMatricula(resultSet.getString("matricula"));
            activity.setStudent(student);
            
            activities.add(activity);
        }
        
    } catch (SQLException e) {
        throw  new DAOException("Error al obtener las actividades", e);
    }
    return activities;
    }

    @Override
    public List<Activity> getByDateRange(LocalDate startDate, LocalDate endDate) throws DAOException {
         List<Activity> activities = new ArrayList<>();
    String sql = "SELECT * FROM actividad WHERE fechaActividad BETWEEN ? AND ? ORDER BY fechaActividad";
    
    try (Connection connection = ConnectionDatabase.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setDate(1, java.sql.Date.valueOf(startDate));
        preparedStatement.setDate(2, java.sql.Date.valueOf(endDate));
        
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
            Activity activity = new Activity();
            activity.setId(resultSet.getInt("idActividad"));
            activity.setDate(resultSet.getDate("fechaActividad").toLocalDate());
            activity.setHours(resultSet.getFloat("horas"));
            activity.setDescription(resultSet.getString("descripcion"));
            
            Student student = new Student();
            student.setMatricula(resultSet.getString("matricula"));
            activity.setStudent(student);
            
            activities.add(activity);
        }
        
    } catch (SQLException e) {
        throw new DAOException("Error al obtener las actividades por rango de fecha", e);

    }
    
    return activities;
    }

    @Override
    public List<Activity> getByStudentAndDateRange(String matricula, LocalDate startDate,
            LocalDate endDate) throws DAOException {
        List<Activity> activities = new ArrayList<>();
    String sql = "SELECT * FROM actividad WHERE matricula = ? "
            + "AND fechaActividad BETWEEN ? AND ? ORDER BY fechaActividad";
    
    try (Connection connection = ConnectionDatabase.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setString(1, String.valueOf(matricula));
        preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
        preparedStatement.setDate(3, java.sql.Date.valueOf(endDate));

        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
            Activity activity = new Activity();
            activity.setId(resultSet.getInt("idActividad"));
            activity.setDate(resultSet.getDate("fechaActividad").toLocalDate());
            activity.setHours(resultSet.getFloat("horas"));
            activity.setDescription(resultSet.getString("descripcion"));
            
            Student student = new Student();
            student.setMatricula(resultSet.getString("matricula"));
            activity.setStudent(student);
            
            activities.add(activity);
        }
        
    } catch (SQLException e) {
        throw new DAOException("Error al obtener los actividades de estudiante por fecha", e);
    }
    
    return activities;
    }
    
}