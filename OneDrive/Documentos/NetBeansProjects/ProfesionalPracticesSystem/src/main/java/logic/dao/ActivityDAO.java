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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import logic.businessObject.Activity;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class ActivityDAO implements IActivityDAO{

    @Override
    public boolean registerActivity(Activity activity) throws SQLException {
      String sql = "INSERT INTO actividad (fecha, fecha, descripcion, matricula) VALUES (?, ?, ?,?)";
        
        try (Connection conn = ConfigDatabase.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            java.sql.Date fechaSQL = java.sql.Date.valueOf(activity.getDate());
            pstmt.setDate(1,fechaSQL);
            pstmt.setDouble(2, activity.getHours());
            pstmt.setString(3, activity.getDescription());
            pstmt.setString(4, activity.getStudent().getMatricula());
            
            int rows = pstmt.executeUpdate();
            System.out.println("Registro de Actividad correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar: " + e.getMessage());
            return false;
        }  
    }

    @Override
    public List<Activity> getAll() throws SQLException {
        List<Activity> activities = new ArrayList<>();
    String sql = "SELECT * FROM actividad ORDER BY fechaActividad DESC";
    
    try (Connection conn = ConnectionDatabase.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            Activity activity = new Activity();
            
            activity.setId(rs.getInt("idActividad"));
            
            activity.setDate(rs.getDate("fechaActividad").toLocalDate());
            
            activity.setHours(rs.getFloat("horas"));
            
            activity.setDescription(rs.getString("descripcion"));
            
            Student student = new Student();
            student.setMatricula(rs.getString("matricula"));
            activity.setStudent(student);
            
            activities.add(activity);
        }
        
    } catch (SQLException e) {
        System.err.println("Error al obtener actividades: " + e.getMessage());
        throw e;
    }
    
    return activities;
    }

    @Override
    public List<Activity> getByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
         List<Activity> activities = new ArrayList<>();
    String sql = "SELECT * FROM actividad WHERE fechaActividad BETWEEN ? AND ? ORDER BY fechaActividad";
    
    try (Connection conn = ConfigDatabase.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setDate(1, java.sql.Date.valueOf(startDate));
        pstmt.setDate(2, java.sql.Date.valueOf(endDate));
        
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Activity activity = new Activity();
            activity.setId(rs.getInt("idActividad"));
            activity.setDate(rs.getDate("fechaActividad").toLocalDate());
            activity.setHours(rs.getFloat("horas"));
            activity.setDescription(rs.getString("descripcion"));
            
            Student student = new Student();
            student.setMatricula(rs.getString("matricula"));
            activity.setStudent(student);
            
            activities.add(activity);
        }
        
    } catch (SQLException e) {
        System.err.println("Error: " + e.getMessage());
        throw e;
    }
    
    return activities;
    }

    @Override
    public List<Activity> getByStudentAndDateRange(int studentId, LocalDate startDate, 
            LocalDate endDate) throws SQLException {
        List<Activity> activities = new ArrayList<>();
    String sql = "SELECT * FROM actividad WHERE matricula = ? "
            + "AND fechaActividad BETWEEN ? AND ? ORDER BY fechaActividad";
    
    try (Connection conn = ConfigDatabase.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, String.valueOf(studentId)); 
        pstmt.setDate(2, java.sql.Date.valueOf(startDate));
        pstmt.setDate(3, java.sql.Date.valueOf(endDate));
        
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Activity activity = new Activity();
            activity.setId(rs.getInt("idActividad"));
            activity.setDate(rs.getDate("fechaActividad").toLocalDate());
            activity.setHours(rs.getFloat("horas"));
            activity.setDescription(rs.getString("descripcion"));
            
            Student student = new Student();
            student.setMatricula(rs.getString("matricula"));
            activity.setStudent(student);
            
            activities.add(activity);
        }
        
    } catch (SQLException e) {
        System.err.println("Error al obtener actividades: " + e.getMessage());
        throw e;
    }
    
    return activities;
    }
    
}
