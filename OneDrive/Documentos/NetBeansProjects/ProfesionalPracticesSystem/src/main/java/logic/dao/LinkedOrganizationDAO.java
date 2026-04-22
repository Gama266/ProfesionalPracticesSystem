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
import java.util.ArrayList;
import java.util.List;
import logic.businessObject.LinkedOrganization;
import logic.businessObject.LocationOrganization;
import logic.exceptions.DAOException;
import logic.idao.ILinkedOrganizationDAO;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class LinkedOrganizationDAO implements ILinkedOrganizationDAO{

    @Override
    public boolean registerLinkedOrganization(LinkedOrganization org) throws DAOException {
       
        String query = "INSERT INTO organizacionvinculada (nombreOrganizacion, direccion, numeroTelefono, correoElectronico, idUbicacion) " +
                       "VALUES (?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement statementRegister = connection.prepareStatement(query)) {

                statementRegister.setString(1, org.getName());
                statementRegister.setString(2, org.getDireccion());
                statementRegister.setString(3, org.getPhoneNumber());
                statementRegister.setString(4, org.getGmail());

         
                if (org.getLocationOrganization() != null) {
                    statementRegister.setInt(5, org.getLocationOrganization().getId());
                } else {
                   
                    statementRegister.setNull(5, java.sql.Types.INTEGER);
                }

            
                int rowsAffected = statementRegister.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException exceptionDB) {
           
            throw new DAOException("Error registrando la organización vinculada", exceptionDB);
        }
    }
    
    @Override
    public boolean deleteLinkedOrganization(String nombre) throws DAOException {
        String query = "DELETE FROM organizacionvinculada WHERE nombreOrganizacion = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement statementDelete = connection.prepareStatement(query)) {
                statementDelete.setString(1, nombre);
                int rowsAffected = statementDelete.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error eliminando la organización de prueba", exceptionDB);
        }
    }
    
    private LinkedOrganization mapResultSetToLinkedOrganization(ResultSet rs) throws SQLException {
        LinkedOrganization organization = new LinkedOrganization();
        organization.setId(rs.getInt("IdOrganizacionVinculada"));
        organization.setName(rs.getString("nombreOrganizacion"));
        organization.setDireccion(rs.getString("direccion"));
        organization.setPhoneNumber(rs.getString("numeroTelefono"));
        organization.setGmail(rs.getString("correoElectronico"));
        
        
        LocationOrganization location = new LocationOrganization();
        location.setId(rs.getInt("idUbicacion"));
        organization.setLocationOrganization(location);
        
        return organization;
    }
}
