/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.exceptions;

/**
 *
 * @author gamal
 */
public class DuplicateRecordException extends DAOException {
    public DuplicateRecordException(String message) {
        super(message);
    }
}