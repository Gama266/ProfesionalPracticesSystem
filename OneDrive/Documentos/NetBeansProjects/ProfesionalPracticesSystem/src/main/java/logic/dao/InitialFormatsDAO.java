/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;
/*
@(#)InitialFormatsDAO.java 1.0 04/04/2026
Copyright (c) 2026 JhonatanYerayLIS
*/


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import dataacces.ConnectionDatabase;
import logic.businessObject.InitialFormats;
import logic.businessObject.Student;
import logic.exceptions.DAOException;
import logic.idao.IInitialFormatsDAO;
import java.util.logging.Logger;
/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class InitialFormatsDAO implements IInitialFormatsDAO{
    private static final Logger logger = Logger.getLogger(InitialFormatsDAO.class.getName());
    @Override
    public boolean registerInitialFormats(InitialFormats initialFormats) throws DAOException {
        String sql = "INSERT INTO formatosiniciales (tipoDocumentos, URL, matricula) VALUES (?, ?, ?)";
        boolean registered = false;
        try (Connection connection = ConnectionDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, initialFormats.getTypeOfDocument());
            preparedStatement.setString(2, initialFormats.getUrl());
            preparedStatement.setString(3, initialFormats.getStudent().getMatricula());
            
            int rows = preparedStatement.executeUpdate();
            
            if (rows > 0) {
                try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){
                    if (generatedKeys.next()) {
                        initialFormats.setId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Formato inicial registrado correctamente");
                registered = true;
            }
            
        } catch (SQLException e) {
            throw new DAOException("Error al registrar formato", e);
        }
        return registered;
    }

    @Override
    public List<InitialFormats> getByStudentMatricula(String matricula) throws DAOException {
    List<InitialFormats> initialFormats = new ArrayList<>();
    
    String sql = "SELECT f.*, s.nombre as student_name, s.email as student_email " +
                 "FROM formatosiniciales f " +
                 "LEFT JOIN students s ON f.matricula = s.matricula " +
                 "WHERE f.matricula = ? " +
                 "ORDER BY f.idDocumentos";
    
    try (Connection connection = ConnectionDatabase.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)){

         preparedStatement.setString(1, matricula);

        try(ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                initialFormats.add(mapResultSetToInitialFormats(resultSet));
            }
        }
        
    } catch (SQLException e) {
        throw new DAOException("Error al obtener formatos por matricula", e);
    }
    
    return initialFormats;
}

    @Override
    public List<InitialFormats> getByTypeOfDocument(String typeOfDocument) throws DAOException {
        List<InitialFormats> formats = new ArrayList<>();
        String sql = "SELECT f.*, s.nombre as student_name, s.email as student_email " +
                     "FROM formatosiniciales f " +
                     "LEFT JOIN students s ON f.matricula = s.matricula " +
                     "WHERE f.tipoDocumentos = ? " +
                     "ORDER BY f.idDocumentos";
        
        try (Connection connection = ConnectionDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, typeOfDocument);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
            
                while (resultSet.next()) {
                    formats.add(mapResultSetToInitialFormats(resultSet));
                }
            }
            
        } catch (SQLException e) {
            throw new DAOException("Error de obtencion de los tipos de documentos", e);
        }
        return formats;
    }
    private InitialFormats mapResultSetToInitialFormats(ResultSet resultSet) throws SQLException {
        InitialFormats format = new InitialFormats();
        format.setId(resultSet.getInt("idDocumentos"));
        format.setTypeOfDocument(resultSet.getString("tipoDocumentos"));
        format.setUrl(resultSet.getString("URL"));
        
        Student student = new Student();
        student.setMatricula(resultSet.getString("matricula"));
        


        String studentName = resultSet.getString("student_name");
        if (studentName != null) {
            student.setName(studentName);
        }

        format.setStudent(student);
        return format;
    }
}