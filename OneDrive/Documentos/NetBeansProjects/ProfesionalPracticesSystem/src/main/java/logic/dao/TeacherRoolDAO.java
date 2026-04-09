/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import logic.businessObject.TeacherRool;

public class TeacherRoolDAO implements ITeacherRoolDAO{

    @Override
    public boolean registerSelfTeacherRool(TeacherRool teacherRool) throws SQLException {
        String sql = "INSERT INTO self_assessments (type) VALUES (?)";
        
        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, teacherRool.getType());

            
            int rows = pstmt.executeUpdate();
            System.out.println("Rool registrado correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
            return false;
        }
    }
    
}