/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.exceptions;

/**
 *
 * @author gamal
 */
public class DatabaseConnectionException extends DAOException {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}