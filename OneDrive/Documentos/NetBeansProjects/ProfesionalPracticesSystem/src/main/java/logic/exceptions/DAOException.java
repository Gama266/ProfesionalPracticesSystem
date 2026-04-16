/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.exceptions;

import java.sql.SQLException;

/**
 *
 * @author gamal
 */


 
public class DAOException extends RuntimeException {
    public DAOException(String message, SQLException exceptionDB) {
        super(message);
    }

    
}