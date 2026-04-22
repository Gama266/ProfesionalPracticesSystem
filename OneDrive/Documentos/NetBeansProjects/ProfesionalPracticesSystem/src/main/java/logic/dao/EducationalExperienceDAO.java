/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
@(#)EducationalExperienceDAO.java 1.0 04/04/2026
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
import logic.businessObject.EducationalExperience;
import logic.exceptions.DAOException;
import logic.idao.IEducationalExperienceDAO;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class EducationalExperienceDAO implements IEducationalExperienceDAO{

    @Override
    public boolean registerEducationalExperience(EducationalExperience experience) 
            throws DAOException {
        String sql = "INSERT INTO experienciaeducativa (nrc, fechaInicio, "
                + "fechaTermino, seccion) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = ConnectionDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, experience.getNrc());
            preparedStatement.setDate(2, java.sql.Date.valueOf(experience.getStartDate()));
            preparedStatement.setDate(3, java.sql.Date.valueOf(experience.getEndDate()));
            preparedStatement.setString(4, experience.getSeccion());
            
            int rows = preparedStatement.executeUpdate();
            System.out.println("Experiencia Educativa registrada correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
            throw new DAOException("Error al registrar Experiencia Educativa", e);

        }
    }

    @Override
    public List<EducationalExperience> getByDateRange(LocalDate startDate, LocalDate endDate)
            throws DAOException {
       List<EducationalExperience> experiences = new ArrayList<>();
    String sql = "SELECT * FROM experienciaeducativa WHERE fechaInicio >= ? AND fechaTermino <= ? ORDER BY fechaInicio";

    try (Connection connection = ConnectionDatabase.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setDate(1, java.sql.Date.valueOf(startDate));
        preparedStatement.setDate(2, java.sql.Date.valueOf(endDate));
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
            experiences.add(mapResultSetToEducationalExperience(resultSet));
        }
        
    } catch (SQLException e) {
        throw  new DAOException("Error al buscar Experiencia Educativa por fecha", e);
    }
    
    return experiences;
    }
    
     private EducationalExperience mapResultSetToEducationalExperience(ResultSet resultSet)
             throws SQLException {
        EducationalExperience experience = new EducationalExperience();
        experience.setNrc(resultSet.getInt("nrc"));
        experience.setStartDate(resultSet.getDate("fechaInicio").toLocalDate());
        experience.setEndDate(resultSet.getDate("fechaTermino").toLocalDate());
        experience.setSeccion(resultSet.getString("seccion"));
        return experience;
    }

}