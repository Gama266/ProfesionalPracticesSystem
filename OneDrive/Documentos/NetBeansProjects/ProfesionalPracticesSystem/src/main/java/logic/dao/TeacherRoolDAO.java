/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
@(#)TeacherRoolDAO.java 1.0 04/04/2026
Copyright (c) 2026 JhonatanYerayLIS
*/
package logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import dataacces.ConnectionDatabase;
import logic.businessObject.TeacherRool;
import logic.exceptions.DAOException;
import logic.idao.ITeacherRoolDAO;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class TeacherRoolDAO implements ITeacherRoolDAO{

    @Override
    public boolean registerSelfTeacherRool(TeacherRool teacherRool) throws DAOException {
        String sql = "INSERT INTO self_assessments (type) VALUES (?)";
        
        try (Connection connection = ConnectionDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, teacherRool.getType());

            
            int rows = preparedStatement.executeUpdate();
            System.out.println("Rool registrado correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
           throw new DAOException("Error al registrar", e);
        }
    }
    
}