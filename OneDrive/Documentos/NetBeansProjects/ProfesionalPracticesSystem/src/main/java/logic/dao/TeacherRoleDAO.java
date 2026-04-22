/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
@(#)TeacherRoolDAO.java 1.0 04/04/2026
Copyright (c) 2026 JhonatanYerayLIS
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
import logic.businessObject.TeacherRole;
import logic.exceptions.DAOException;
import logic.idao.ITeacherRoleDAO;
import java.util.logging.Logger;
/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class TeacherRoleDAO implements ITeacherRoleDAO{
    private static final Logger logger = Logger.getLogger(TeacherRoleDAO.class.getName());
    @Override
    public boolean registerTeacherRole(TeacherRole teacherRole) throws DAOException {
        String sql = "INSERT INTO profesorrol (type) VALUES (?)";
        
        try (Connection connection = ConnectionDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, teacherRole.getType());

            
            int rows = preparedStatement.executeUpdate();
            logger.info("Rool registrado correctamente");
            return rows > 0;
            
        } catch (SQLException e) {
           throw new DAOException("Error de registrar rol de profesor", e);
        }
    }
    
}