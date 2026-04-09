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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import logic.businessObject.InitialFormats;
import logic.businessObject.Student;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class InitialFormatsDAO implements IInitialFormatsDAO{

    @Override
    public boolean registerInitialFormats(InitialFormats initialFormats) throws SQLException {
        String sql = "INSERT INTO formatosiniciales (tipoDocumentos, URL, matricula) VALUES (?, ?, ?)";
        
        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, initialFormats.getTypeOfDocument());
            pstmt.setString(2, initialFormats.getUrl());
            pstmt.setString(3, initialFormats.getStudent().getMatricula());
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    initialFormats.setId(generatedKeys.getInt(1));
                }
                System.out.println("Formato inicial registrado correctamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar formato: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<InitialFormats> getByStudentMatricula(String matricula) throws SQLException {
    List<InitialFormats> initialFormats = new ArrayList<>();
    
    String sql = "SELECT f.*, s.nombre as student_name, s.email as student_email " +  // ← Espacio al final
                 "FROM formatosiniciales f " +
                 "LEFT JOIN students s ON f.matricula = s.matricula " +
                 "WHERE f.matricula = ? " +
                 "ORDER BY f.idDocumentos";
    
    try (Connection conn = ConfigDatabase.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {  // ← Todo en un solo try
        
        pstmt.setString(1, matricula);
        
        while (rs.next()) {
            initialFormats.add(mapResultSetToInitialFormats(rs));
        }
        
    } catch (SQLException e) {
        System.err.println("Error al obtener formatos por matricula: " + e.getMessage());
        throw e;
    }
    
    return initialFormats;
}

    @Override
    public List<InitialFormats> getByTypeOfDocument(String typeOfDocument) throws SQLException {
        List<InitialFormats> formats = new ArrayList<>();
        String sql = "SELECT f.*, s.nombre as student_name, s.email as student_email " +
                     "FROM formatosiniciales f " +
                     "LEFT JOIN students s ON f.matricula = s.matricula " +
                     "WHERE f.tipoDocumentos = ? " +
                     "ORDER BY f.idDocumentos";
        
        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, typeOfDocument);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                formats.add(mapResultSetToInitialFormats(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
        return formats;
    }
    private InitialFormats mapResultSetToInitialFormats(ResultSet rs) throws SQLException {
        InitialFormats format = new InitialFormats();
        format.setId(rs.getInt("idDocumentos"));
        format.setTypeOfDocument(rs.getString("tipoDocumentos"));
        format.setUrl(rs.getString("URL"));
        
        Student student = new Student();
        student.setMatricula(rs.getString("matricula"));
        

        try {
            String studentName = rs.getString("student_name");
            if (studentName != null) {
                student.setName(studentName);
            }
        } catch (SQLException e) {
        }
        
        format.setStudent(student);
        return format;
    }
}
