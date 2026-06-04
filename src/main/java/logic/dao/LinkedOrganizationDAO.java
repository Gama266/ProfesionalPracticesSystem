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
import dataacces.ConfigDatabase;
import logic.businessObject.LinkedOrganization;
import logic.businessObject.LocationOrganization;
import logic.exceptions.DAOException;
import logic.idao.ILinkedOrganizationDAO;
import java.util.logging.Logger;
import logic.exceptions.DatabaseConnectionException;
import logic.exceptions.DuplicateRecordException;
/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class LinkedOrganizationDAO implements ILinkedOrganizationDAO{

@Override
    public boolean registerLinkedOrganization(LinkedOrganization linkedOrganization) throws DAOException {
        
      
        if (isOrganizationAlreadyRegistered(linkedOrganization.getName())) {
            throw new DuplicateRecordException("Esta organización ya se encuentra registrada en el sistema.");
        }

        String sql = "INSERT INTO organizacionvinculada (nombreOrganizacion, direccion, numeroTelefono,correoElectronico, idUbicacion) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
     
                               preparedStatement.setString(1, linkedOrganization.getName());
                               preparedStatement.setString(2, linkedOrganization.getDireccion());
                               preparedStatement.setString(3, linkedOrganization.getPhoneNumber());
                               preparedStatement.setString(4, linkedOrganization.getGmail()); 
                               preparedStatement.setInt(5, linkedOrganization.getLocationOrganization().getId()); 

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al registrar la organización vinculada", exceptionDB);
        }
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
    
    @Override
    public boolean isOrganizationAlreadyRegistered(String name) throws DAOException {
        String query = "SELECT COUNT(*) FROM organizacionvinculada WHERE nombreOrganizacion = ?";
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, name);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1) > 0;
                }
            }
        } catch (SQLException exception) {
            throw new DatabaseConnectionException("Error al verificar duplicados", exception);
        }
        return false;
    }
    
  @Override
    public List<LinkedOrganization> getAllOrganizations() throws DAOException {
        List<LinkedOrganization> organizationsList = new java.util.ArrayList<>();
        
     
        String query = "SELECT IdOrganizacionVinculada, nombreOrganizacion, direccion, numeroTelefono FROM organizacionvinculada";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                LinkedOrganization linkedOrganization = new LinkedOrganization();
                
          
                linkedOrganization.setId(resultSet.getInt("IdOrganizacionVinculada")); 
                
                linkedOrganization.setName(resultSet.getString("nombreOrganizacion")); 
                linkedOrganization.setDireccion(resultSet.getString("direccion"));
                linkedOrganization.setPhoneNumber(resultSet.getString("numeroTelefono"));
                
                organizationsList.add(linkedOrganization);
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al consultar las organizaciones vinculadas", exceptionDB);
        }
        
        return organizationsList;
    }
}