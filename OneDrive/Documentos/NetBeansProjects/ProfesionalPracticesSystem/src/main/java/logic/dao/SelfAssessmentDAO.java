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
import logic.businessObject.SelfAssessment;


/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class SelfAssessmentDAO implements ISelfAssessmentDAO {

    @Override
    public boolean registerSelfAssessment(SelfAssessment selfAssessment) throws SQLException {
    String sql = "INSERT INTO autoevaluacion (url, calificacion, matricula) VALUES (?, ?, ?)";
        
        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, selfAssessment.getUrl());
            pstmt.setDouble(2, selfAssessment.getGrade());
            pstmt.setString(3, selfAssessment.getStudent().getMatricula());
            
            int rows = pstmt.executeUpdate();
            System.out.println("Autoevaluación insertada correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
            return false;
        }
    }
    
    public void showSelfAssessments()throws SQLException{
         String sql = "SELECT * FROM autoevaluacion";
    
    try (Connection conn = ConfigDatabase.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery(sql)) {
        
        System.out.println("\n=== AUTOEVALUACIONES ===");
        
        boolean hayRegistros = false;
        
        while (rs.next()) {
            hayRegistros = true;
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("URL: " + rs.getString("url"));
            System.out.println("Calificacion: " + rs.getDouble("grade"));
            System.out.println("Matricula Estudiante: " + rs.getString("student_matricula"));
            System.out.println("--------------------");
        }
        
        if (!hayRegistros) {
            System.out.println("No hay autoevaluaciones registradas.");
        }
        
    } catch (SQLException e) {
        System.err.println("Error al mostrar: " + e.getMessage());
    }
    }
}