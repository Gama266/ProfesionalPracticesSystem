/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

/**
 *
 * @author gamal
 */
import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import logic.businessObject.TechnicalResponsable;




public class TechnicalResponsibleDAO implements ITechnicalResponsibleDAO {

    @Override
    public boolean registerTechnicalResponsible(TechnicalResponsable newResponsible) throws RuntimeException {

        String queryRegisterResponsible =
            "INSERT INTO ResponsableTecnico (nombre, apellidoPaterno, apellidoMaterno, telefono, gmail, idOrganizacionVinculada) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtRegisterResponsible =
                         connection.prepareStatement(queryRegisterResponsible)) {

                stmtRegisterResponsible.setString(1, newResponsible.getName());
                stmtRegisterResponsible.setString(2, newResponsible.getPaternalSurname());
                stmtRegisterResponsible.setString(3, newResponsible.getMaternalSurname());
                stmtRegisterResponsible.setString(4, newResponsible.getPhoneNumber());
                stmtRegisterResponsible.setString(5, newResponsible.getGmail());

                if (newResponsible.getLinkedOrganization() != null) {
                    stmtRegisterResponsible.setInt(6, newResponsible.getLinkedOrganization().getId());
                } else {
                    stmtRegisterResponsible.setNull(6, Types.INTEGER);
                }

                int rowsAffected = stmtRegisterResponsible.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al registrar el responsable técnico", exceptionDB);
        }
    }

    @Override
    public boolean updateTechnicalResponsible(TechnicalResponsable responsible) throws RuntimeException {

        String queryUpdateResponsible =
            "UPDATE ResponsableTecnico SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, telefono = ?, gmail = ?, idOrganizacionVinculada = ? WHERE id = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();

            try (PreparedStatement stmtUpdateResponsible =
                         connection.prepareStatement(queryUpdateResponsible)) {

                stmtUpdateResponsible.setString(1, responsible.getName());
                stmtUpdateResponsible.setString(2, responsible.getPaternalSurname());
                stmtUpdateResponsible.setString(3, responsible.getMaternalSurname());
                stmtUpdateResponsible.setString(4, responsible.getPhoneNumber());
                stmtUpdateResponsible.setString(5, responsible.getGmail());

                if (responsible.getLinkedOrganization() != null) {
                    stmtUpdateResponsible.setInt(6, responsible.getLinkedOrganization().getId());
                } else {
                    stmtUpdateResponsible.setNull(6, Types.INTEGER);
                }

                stmtUpdateResponsible.setInt(7, responsible.getId());

                int rowsAffected = stmtUpdateResponsible.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException exceptionDB) {
            throw new RuntimeException("Error al actualizar el responsable técnico", exceptionDB);
        }
    }
}