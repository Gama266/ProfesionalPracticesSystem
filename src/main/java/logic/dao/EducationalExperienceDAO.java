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
import dataacces.ConfigDatabase;
import logic.businessObject.EducationalExperience;
import logic.exceptions.DAOException;
import logic.idao.IEducationalExperienceDAO;
import java.util.logging.Logger;
/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class EducationalExperienceDAO implements IEducationalExperienceDAO{

    private static final Logger logger = Logger.getLogger(EducationalExperienceDAO.class.getName());
    @Override
    public boolean registerEducationalExperience(EducationalExperience experience) 
            throws DAOException {
        String sql = "INSERT INTO experienciaeducativa (fechaInicio, "
                + "fechaTermino, seccion) VALUES (?, ?, ?)";
        
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            
            preparedStatement.setDate(1, java.sql.Date.valueOf(experience.getStartDate()));
            preparedStatement.setDate(2, java.sql.Date.valueOf(experience.getEndDate()));
            preparedStatement.setString(3, experience.getSection());

            
            preparedStatement.setDate(1, java.sql.Date.valueOf(experience.getStartDate()));
            preparedStatement.setDate(2, java.sql.Date.valueOf(experience.getEndDate()));
            preparedStatement.setString(3, experience.getSection());
            int rows = preparedStatement.executeUpdate();
            logger.info("Experiencia Educativa registrada correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
            throw new DAOException("Error al registrar Experiencia Educativa", e);

        }
    }
    @Override
    public List<EducationalExperience> getByDateRange(LocalDate startDate, LocalDate endDate)
            throws DAOException {
        String sql = "SELECT * FROM experienciaeducativa " +
        "WHERE fechaInicio >= ? AND fechaTermino <= ? " +
        "ORDER BY fechaInicio";

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                "La fecha de inicio no puede ser posterior a la fecha de término");
        }

        List<EducationalExperience> experiences = new ArrayList<>();

        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setDate(1, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    experiences.add(mapResultSetToEducationalExperience(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al buscar Experiencia Educativa por fecha", e);
        }
        return experiences;
    }
    
    @Override
    public EducationalExperience getNrc(int nrc) throws DAOException {
        String sql = "SELECT * FROM experienciaeducativa WHERE nrc = ?";
        EducationalExperience experience = null;

        try (Connection conn = ConfigDatabase.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, nrc);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    experience = mapResultSetToEducationalExperience(resultSet);
                }
            }   
        } catch (SQLException e) {
            throw new DAOException("Error al buscar experiencia educativa por NRC", e);
        }

        return experience;
    }

    
     private EducationalExperience mapResultSetToEducationalExperience(ResultSet resultSet)
             throws SQLException {
        EducationalExperience experience = new EducationalExperience();
        experience.setNrc(resultSet.getInt("nrc"));
        experience.setStartDate(resultSet.getDate("fechaInicio").toLocalDate());
        experience.setEndDate(resultSet.getDate("fechaTermino").toLocalDate());
        experience.setSection(resultSet.getString("seccion"));
        return experience;
    }
@Override
public List<EducationalExperience> getAllEducationalExperiences() throws DAOException {
    String sql = "SELECT * FROM experienciaeducativa";
    List<EducationalExperience> experiences = new ArrayList<>();
    try (Connection conn = ConfigDatabase.getConnection();
         PreparedStatement preparedStatement = conn.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
            experiences.add(mapResultSetToEducationalExperience(resultSet));
        }
    } catch (SQLException e) {
        throw new DAOException("Error al obtener el catálogo de experiencias educativas", e);
    }
    return experiences;
}

}
