/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import logic.businessObject.TechnicalResponsible;
import logic.exceptions.DAOException;
import logic.idao.ITechnicalResponsibleDAO;

/**
 * @author gamal
 */
public class TechnicalResponsibleDAO implements ITechnicalResponsibleDAO {

    @Override
    public boolean registerTechnicalResponsible(TechnicalResponsible newResponsible) throws DAOException {
      
        String queryRegisterResponsible =
            "INSERT INTO responsabletecnico (nombre, apellidoPaterno, apellidoMaterno, numeroTelefono, correoElectronico, idOrganizacionVinculada) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentRegisterResponsible =
                         connection.prepareStatement(queryRegisterResponsible)) {

                stamentRegisterResponsible.setString(1, newResponsible.getName());
                stamentRegisterResponsible.setString(2, newResponsible.getPaternalSurname());
                stamentRegisterResponsible.setString(3, newResponsible.getMaternalSurname());
                stamentRegisterResponsible.setString(4, newResponsible.getPhoneNumber());
                stamentRegisterResponsible.setString(5, newResponsible.getGmail()); 

                if (newResponsible.getLinkedOrganization() != null) {
                    stamentRegisterResponsible.setInt(6, newResponsible.getLinkedOrganization().getId());
                } else {
                    stamentRegisterResponsible.setNull(6, Types.INTEGER);
                }

                int rowsAffected = stamentRegisterResponsible.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error registering the technical responsible", exceptionDB);
        }
    }

    @Override
    public boolean updateTechnicalResponsible(TechnicalResponsible responsible) throws DAOException {
      
        String queryUpdateResponsible =
            "UPDATE responsabletecnico SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, numeroTelefono = ?, correoElectronico = ?, idOrganizacionVinculada = ? " +
            "WHERE idResponsableTecnico = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stamentUpdateResponsible =
                         connection.prepareStatement(queryUpdateResponsible)) {

                stamentUpdateResponsible.setString(1, responsible.getName());
                stamentUpdateResponsible.setString(2, responsible.getPaternalSurname());
                stamentUpdateResponsible.setString(3, responsible.getMaternalSurname());
                stamentUpdateResponsible.setString(4, responsible.getPhoneNumber());
                stamentUpdateResponsible.setString(5, responsible.getGmail());

                if (responsible.getLinkedOrganization() != null) {
                    stamentUpdateResponsible.setInt(6, responsible.getLinkedOrganization().getId());
                } else {
                    stamentUpdateResponsible.setNull(6, Types.INTEGER);
                }

                stamentUpdateResponsible.setInt(7, responsible.getId());

                int rowsAffected = stamentUpdateResponsible.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error updating the technical responsible", exceptionDB);
        }
    }
}