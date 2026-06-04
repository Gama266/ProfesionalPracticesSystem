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
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Logger;
import logic.businessObject.User;
import logic.businessObject.UserType;
import logic.exceptions.DAOException;
import logic.idao.IUserDAO;
import logic.util.PasswordHasher;



public class UserDAO implements IUserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    private static final String SQL_INSERT =
        "INSERT INTO usuario (gmail, contrasena, tipoUsuario, estadoActividad) " +
        "VALUES (?, ?, ?, ?)";

    private static final String SQL_LOGIN =
        "SELECT idUsuario, gmail, contrasena, tipoUsuario, estadoActividad " +
        "FROM usuario " +
        "WHERE gmail = ? AND estadoActividad = 'activo'";

    private static final String SQL_UPDATE_PASSWORD =
        "UPDATE usuario SET contrasena = ? WHERE idUsuario = ?";

    private static final String SQL_DEACTIVATE =
        "UPDATE usuario SET estadoActividad = 'inactivo' WHERE idUsuario = ?";

    private static final String SQL_GET_ENROLLMENT =
        "SELECT matricula FROM practicante WHERE idUsuario = ?";

    @Override
    public int registerUser(User user) throws DAOException {
        validateUserForRegistration(user);

        String hashedPassword = PasswordHasher.hash(user.getPlainPassword());

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT,
                                                          Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getGmail().trim().toLowerCase());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.getUserType().getDbValue());
            preparedStatement.setString(4, user.isActive() ? "activo" : "inactivo");

            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    logger.info(() -> "Usuario registrado — id: " + generatedId +
                                     ", tipo: " + user.getUserType().getDbValue());
                    return generatedId;
                }
            }
            throw new DAOException("No se generó ID para el usuario", null);

        } catch (SQLException e) {
            if (isDuplicateEntry(e)) {
                throw new DAOException(
                    "El correo electrónico ya está registrado en el sistema.", e);
            }
            throw new DAOException("Error al registrar usuario", e);
        }
    }

    @Override
    public Optional<User> login(String gmail, String plainPassword) throws DAOException {
        if (gmail == null || gmail.isBlank() || plainPassword == null || plainPassword.isBlank()) {
            return Optional.empty();
        }

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_LOGIN)) {

            preparedStatement.setString(1, gmail.trim().toLowerCase());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("contrasena");

                    if (PasswordHasher.verify(plainPassword, storedHash)) {
                        User user = mapResultSet(resultSet);
                        logger.info(() -> "Login exitoso — gmail: " + gmail +
                                         ", tipo: " + user.getUserType().getDbValue());
                        return Optional.of(user);
                    }
                }
            }
            logger.warning(() -> "Intento de login fallido — gmail: " + gmail);
            return Optional.empty();

        } catch (SQLException e) {
            throw new DAOException("Error de base de datos durante el login", e);
        }
    }

    @Override
    public boolean updatePassword(int idUsuario, String newPlainPassword) throws DAOException {
        if (newPlainPassword == null || newPlainPassword.isBlank()) {
            throw new DAOException("La nueva contraseña no puede estar vacía", null);
        }

        String newHash = PasswordHasher.hash(newPlainPassword);

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_PASSWORD)) {

            preparedStatement.setString(1, newHash);
            preparedStatement.setInt(2, idUsuario);

            int rows = preparedStatement.executeUpdate();
            logger.info(() -> "Contraseña actualizada — idUsuario: " + idUsuario);
            return rows > 0;

        } catch (SQLException e) {
            throw new DAOException("Error al actualizar la contraseña", e);
        }
    }

    @Override
    public boolean deactivateUser(int idUsuario) throws DAOException {
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DEACTIVATE)) {

            preparedStatement.setInt(1, idUsuario);
            int rows = preparedStatement.executeUpdate();
            logger.info(() -> "Usuario desactivado — idUsuario: " + idUsuario);
            return rows > 0;

        } catch (SQLException e) {
            throw new DAOException("Error al desactivar usuario", e);
        }
    }

    @Override
    public String getPractitionerEnrollmentByUserId(int idUsuario) throws DAOException {
        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_ENROLLMENT)) {

            preparedStatement.setInt(1, idUsuario);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    logger.info(() -> "Matrícula obtenida para idUsuario: " + idUsuario);
                    return resultSet.getString("matricula");
                }
            }
            throw new DAOException("No se encontró matrícula para idUsuario: " + idUsuario, null);

        } catch (SQLException e) {
            throw new DAOException("Error al obtener la matrícula", e);
        }
    }

    private User mapResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setIdUser(resultSet.getInt("idUsuario"));
        user.setGmail(resultSet.getString("gmail"));
        user.setUserType(UserType.fromDbValue(resultSet.getString("tipoUsuario")));
        user.setActive("activo".equals(resultSet.getString("estadoActividad")));
        return user;
    }

    private void validateUserForRegistration(User user) throws DAOException {
        if (user == null) {
            throw new DAOException("El usuario no puede ser null", null);
        }
        if (user.getGmail() == null || user.getGmail().isBlank()) {
            throw new DAOException("El gmail del usuario es obligatorio", null);
        }
        if (user.getPlainPassword() == null || user.getPlainPassword().isBlank()) {
            throw new DAOException("La contraseña del usuario es obligatoria", null);
        }
        if (user.getUserType() == null) {
            throw new DAOException("El tipo de usuario es obligatorio", null);
        }
    }

    private boolean isDuplicateEntry(SQLException e) {
        return e.getErrorCode() == 1062;
    }
}