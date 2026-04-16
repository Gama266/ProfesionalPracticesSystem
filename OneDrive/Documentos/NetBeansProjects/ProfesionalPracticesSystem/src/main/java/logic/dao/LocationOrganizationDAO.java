/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import logic.businessObject.LocationOrganization;
import logic.exceptions.DAOException;
import logic.idao.ILocationOrganizationDAO;

/**
 * * @author gamal
 */
public class LocationOrganizationDAO implements ILocationOrganizationDAO {

    @Override
    public boolean registerLocation(LocationOrganization newLocation) throws DAOException {
 
        String queryRegisterLocation = "INSERT INTO ubicacion (pais, estado) VALUES (?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentRegisterLocation = 
                         connection.prepareStatement(queryRegisterLocation)) {

                stamentRegisterLocation.setString(1, newLocation.getCountry());
                stamentRegisterLocation.setString(2, newLocation.getState());

                int rowsAffected = stamentRegisterLocation.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
           
            throw new DAOException("Error registrando la ubicacion", exceptionDB);
        }
    }

    @Override
    public boolean updateLocation(LocationOrganization location) throws DAOException {
    
        String queryUpdateLocation = "UPDATE ubicacion SET pais = ?, estado = ? WHERE idUbicacion = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentUpdateLocation = 
                         connection.prepareStatement(queryUpdateLocation)) {

                stamentUpdateLocation.setString(1, location.getCountry());
                stamentUpdateLocation.setString(2, location.getState());
                stamentUpdateLocation.setInt(3, location.getId());

                int rowsAffected = stamentUpdateLocation.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al actualizar", exceptionDB);
        }
    }
}