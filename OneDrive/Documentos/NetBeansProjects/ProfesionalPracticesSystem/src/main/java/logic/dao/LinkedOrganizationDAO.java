/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import dataacces.ConnectionDatabase;
import logic.businessObject.LinkedOrganization;
import logic.businessObject.LocationOrganization;
import logic.exceptions.DAOException;
import logic.idao.ILinkedOrganizationDAO;
import java.util.logging.Logger;
/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class LinkedOrganizationDAO implements ILinkedOrganizationDAO{
    private static final Logger logger = Logger.getLogger(LinkedOrganizationDAO.class.getName());
    @Override
    public boolean registerLinkedOrganization(LinkedOrganization linkedOrganization) throws DAOException {
       String sql = "INSERT INTO organizaciónvinculada (nombreOrganizacion, direccion, numeroTelefono, correoElectronico," +
               " idUbicacion) VALUES (?, ?, ?, ?, ?)";
        boolean registerd = false;

        try (Connection connection = ConnectionDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, linkedOrganization.getName());
            preparedStatement.setString(2, linkedOrganization.getDireccion());
            preparedStatement.setString(3, linkedOrganization.getPhoneNumber());
            preparedStatement.setString(4, linkedOrganization.getGmail());
            preparedStatement.setInt(5, linkedOrganization.getLocationOrganization().getId());
            
            int rows = preparedStatement.executeUpdate();
            
            if (rows > 0) {
                try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){
                    if (generatedKeys.next()) {
                        linkedOrganization.setId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Organización vinculada registrada correctamente");
                registerd = true;
            }
            
        } catch (SQLException e) {
            throw new DAOException("Error al registrar organizacion ",e);
        }
        return registerd;
    }


    @Override
    public List<LinkedOrganization> showAllLinkedOrganization() throws DAOException{
        List<LinkedOrganization> organizations = new ArrayList<>();
        String sql = "SELECT * FROM organizacionvinculada ORDER BY nombreOrganizacion";
        
        try (Connection connection = ConnectionDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            while (resultSet.next()) {
                organizations.add(mapResultSetToLinkedOrganization(resultSet));
            }
            
        } catch (SQLException e) {
            throw new DAOException("Error al obtener las organizaciones", e);
        }
        return organizations;
    }
    
    private LinkedOrganization mapResultSetToLinkedOrganization(ResultSet resultSet) throws SQLException {
        LinkedOrganization organization = new LinkedOrganization();
        organization.setId(resultSet.getInt("IdOrganizacionVinculada"));
        organization.setName(resultSet.getString("nombreOrganizacion"));
        organization.setDireccion(resultSet.getString("direccion"));
        organization.setPhoneNumber(resultSet.getString("numeroTelefono"));
        organization.setGmail(resultSet.getString("correoElectronico"));

        LocationOrganization location = new LocationOrganization();
        location.setId(resultSet.getInt("idUbicacion"));
        organization.setLocationOrganization(location);
        
        return organization;
    }
}