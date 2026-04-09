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
import logic.businessObject.EducationalExperience;



/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class EducationalExperienceDAO implements IEducationalExperienceDAO{

    @Override
    public boolean registerEducationalExperience(EducationalExperience experience) 
            throws SQLException {
        String sql = "INSERT INTO experienciaeducativa (nrc, fechaInicio, "
                + "fechaTermino, seccion) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, experience.getNrc());
            pstmt.setDate(2, java.sql.Date.valueOf(experience.getStartDate()));
            pstmt.setDate(3, java.sql.Date.valueOf(experience.getEndDate()));
            pstmt.setString(4, experience.getSeccion());
            
            int rows = pstmt.executeUpdate();
            System.out.println("Experiencia Educativa registrada correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<EducationalExperience> getByDateRange(LocalDate startDate, LocalDate endDate)
            throws SQLException {
       List<EducationalExperience> experiences = new ArrayList<>();
    String sql = "SELECT * FROM experienciaeducativa WHERE fechaInicio >= ? AND fechaTermino <= ? ORDER BY fechaInicio";
    
    try (Connection conn = ConfigDatabase.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setDate(1, java.sql.Date.valueOf(startDate));
        pstmt.setDate(2, java.sql.Date.valueOf(endDate));
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            experiences.add(mapResultSetToEducationalExperience(rs));
        }
        
    } catch (SQLException e) {
        System.err.println("Error al buscar por rango de fechas: " + e.getMessage());
        throw e;
    }
    
    return experiences;
    }
    
     private EducationalExperience mapResultSetToEducationalExperience(ResultSet rs)
             throws SQLException {
        EducationalExperience experience = new EducationalExperience();
        experience.setNrc(rs.getInt("nrc"));
        experience.setStartDate(rs.getDate("fechaInicio").toLocalDate());
        experience.setEndDate(rs.getDate("fechaTermino").toLocalDate());
        experience.setSeccion(rs.getString("seccion"));
        return experience;
    }
}
