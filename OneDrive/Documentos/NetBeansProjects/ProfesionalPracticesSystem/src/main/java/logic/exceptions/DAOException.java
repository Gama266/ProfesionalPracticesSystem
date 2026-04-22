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

//completar lo faltante, el uso de ia, revisar comentarios, hacer las pruebas
 
public class DAOException extends Exception {
    public DAOException(String message, SQLException exceptionDB) {
        super(message);
    }

    
}