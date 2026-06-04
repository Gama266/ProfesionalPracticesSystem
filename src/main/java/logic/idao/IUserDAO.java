/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.idao;

import java.util.Optional;
import logic.businessObject.User;
import logic.exceptions.DAOException;

/**
 *
 * @author akyer
 */
public interface IUserDAO {
    int registerUser(User user) throws DAOException;

    Optional<User> login(String gmail, String plainPassword) throws DAOException;

    boolean updatePassword(int idUser, String newPlainPassword) throws DAOException;

    boolean deactivateUser(int idUser) throws DAOException;
    String getPractitionerEnrollmentByUserId(int idUsuario) throws DAOException;
}

