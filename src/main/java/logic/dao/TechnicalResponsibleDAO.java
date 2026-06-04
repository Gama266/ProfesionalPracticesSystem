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
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import logic.businessObject.TechnicalResponsible;
import logic.exceptions.DAOException;
import logic.exceptions.DuplicateRecordException;
import logic.idao.ITechnicalResponsibleDAO;

/**
 * @author gamal
 */
public class TechnicalResponsibleDAO implements ITechnicalResponsibleDAO {

    @Override
    public boolean registerTechnicalResponsible(TechnicalResponsible newResponsible) throws DAOException {
        
      if (isEmailAlreadyRegistered(newResponsible.getGmail())) {
      
            throw new DuplicateRecordException("El correo electrónico ingresado ya se encuentra registrado.");
        }
        String queryRegisterResponsible =
            "INSERT INTO responsabletecnico (nombre, apellidoPaterno, apellidoMaterno, "
                + "numeroTelefono, correoElectronico, idOrganizacionVinculada) " +
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
         
            throw new DAOException("Error registrando al responsable técnico", exceptionDB);
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
           
            throw new DAOException("Error registrando al responsable técnico", exceptionDB);
        }
    }
    
@Override    
public boolean isEmailAlreadyRegistered(String email) throws DAOException {
    String query = "SELECT COUNT(*) FROM responsabletecnico WHERE correoElectronico = ?";
    try (Connection connection = ConfigDatabase.getConnection();
         PreparedStatement stament = connection.prepareStatement(query)) {
        
        stament.setString(1, email);
        ResultSet result = stament.executeQuery();
        
        if (result.next()) {
            return result.getInt(1) > 0; 
        }
    } catch (SQLException exception) {
        throw new DAOException("Error al verificar el correo", exception);
    }
    return false;
}
@Override
    public List<TechnicalResponsible> getAllTechnicalResponsibles() throws DAOException {
        List<TechnicalResponsible> responsiblesList = new java.util.ArrayList<>();
        String query = "SELECT rt.idResponsableTecnico, rt.nombre, rt.apellidoPaterno, rt.apellidoMaterno, rt.numeroTelefono, rt.correoElectronico, " +
                       "ov.nombreOrganizacion " +
                       "FROM responsabletecnico rt " +
                       "INNER JOIN organizacionvinculada ov ON rt.idOrganizacionVinculada = ov.IdOrganizacionVinculada";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                TechnicalResponsible resp = new TechnicalResponsible();
                resp.setId(resultSet.getInt("idResponsableTecnico")); 
                resp.setName(resultSet.getString("nombre")); 
                resp.setPaternalSurname(resultSet.getString("apellidoPaterno"));
                resp.setMaternalSurname(resultSet.getString("apellidoMaterno"));
                resp.setPhoneNumber(resultSet.getString("numeroTelefono"));
                resp.setGmail(resultSet.getString("correoElectronico"));
                

                logic.businessObject.LinkedOrganization org = new logic.businessObject.LinkedOrganization();
                org.setName(resultSet.getString("nombreOrganizacion"));
                resp.setLinkedOrganization(org);
                
                responsiblesList.add(resp);
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al consultar los responsables técnicos", exceptionDB);
        }
        
        return responsiblesList;
    }
    @Override
public List<TechnicalResponsible> getByOrganization(int idOrganization) throws DAOException {
    List<TechnicalResponsible> list = new ArrayList<>();
    String query =
        "SELECT idResponsableTecnico, nombre, apellidoPaterno, apellidoMaterno, " +
        "numeroTelefono, correoElectronico " +
        "FROM responsabletecnico " +
        "WHERE idOrganizacionVinculada = ?";

    try (Connection connection = ConfigDatabase.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setInt(1, idOrganization);
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                TechnicalResponsible resp = new TechnicalResponsible();
                resp.setId(resultSet.getInt("idResponsableTecnico"));
                resp.setName(resultSet.getString("nombre"));
                resp.setPaternalSurname(resultSet.getString("apellidoPaterno"));
                resp.setMaternalSurname(resultSet.getString("apellidoMaterno"));
                resp.setPhoneNumber(resultSet.getString("numeroTelefono"));
                resp.setGmail(resultSet.getString("correoElectronico"));
                list.add(resp);
            }
        }
    } catch (SQLException e) {
        throw new DAOException("Error al obtener responsables técnicos por organización", e);
    }
    return list;
}
}