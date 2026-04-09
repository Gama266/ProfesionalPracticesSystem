/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import logic.businessObject.LocationOrganization;


/**
 *
 * @author gamal
 */
public class LocationOrganizationDAO implements ILocationOrganizationDAO {
    @Override
    public boolean registerLocation(LocationOrganization newLocation) throws RuntimeException {

        String queryRegisterLocation =
            "INSERT INTO LocationOrganization (country, state) VALUES (?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtRegisterLocation =
                         connection.prepareStatement(queryRegisterLocation)) {

                stmtRegisterLocation.setString(1, newLocation.getCountry());
                stmtRegisterLocation.setString(2, newLocation.getState());

                int rowsAffected = stmtRegisterLocation.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al registrar la ubicación", exceptionDB);
        }
    }

    @Override
    public boolean updateLocation(LocationOrganization location) throws RuntimeException {

        String queryUpdateLocation =
            "UPDATE LocationOrganization SET country = ?, state = ? WHERE id = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtUpdateLocation =
                         connection.prepareStatement(queryUpdateLocation)) {

                stmtUpdateLocation.setString(1, location.getCountry());
                stmtUpdateLocation.setString(2, location.getState());
                stmtUpdateLocation.setInt(3, location.getId());

                int rowsAffected = stmtUpdateLocation.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al actualizar la ubicación", exceptionDB);
        }
}
}
