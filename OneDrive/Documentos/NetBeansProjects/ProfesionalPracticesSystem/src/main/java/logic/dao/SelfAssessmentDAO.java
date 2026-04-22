/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dataacces.ConnectionDatabase;
import logic.businessObject.SelfAssessment;
import logic.businessObject.Student;
import logic.exceptions.DAOException;
import logic.idao.ISelfAssessmentDAO;
import java.util.logging.Logger;
/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class SelfAssessmentDAO implements ISelfAssessmentDAO {
    private static final Logger logger = Logger.getLogger(SelfAssessmentDAO.class.getName());
    @Override
    public boolean registerSelfAssessment(SelfAssessment selfAssessment) throws DAOException {
    String sql = "INSERT INTO autoevaluacion (url, calificacion, matricula) VALUES (?, ?, ?)";
        
        try (Connection connection = ConnectionDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, selfAssessment.getUrl());
            preparedStatement.setDouble(2, selfAssessment.getGrade());
            preparedStatement.setString(3, selfAssessment.getStudent().getMatricula());
            
            int rows = preparedStatement.executeUpdate();
            logger.info("Autoevaluación insertada correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
            throw new DAOException("Error al registrar", e);
        }
    }

    @Override
    public List<SelfAssessment> getAllSelfAssessments() throws DAOException {
        List<SelfAssessment> assessments = new ArrayList<>();
        String sql = "SELECT * FROM autoevaluacion";

        try (Connection connection = ConnectionDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                assessments.add(mapResultSetToSelfAssessment(resultSet));
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener autoevaluaciones", e);
        }

        return assessments;
    }

    private SelfAssessment mapResultSetToSelfAssessment(ResultSet resultSet) throws SQLException {
        SelfAssessment assessment = new SelfAssessment();
        assessment.setId(resultSet.getInt("id"));
        assessment.setUrl(resultSet.getString("url"));
        assessment.setGrade(resultSet.getDouble("calificacion"));

        Student student = new Student();
        student.setMatricula(resultSet.getString("matricula"));
        assessment.setStudent(student);

        return assessment;
    }
}