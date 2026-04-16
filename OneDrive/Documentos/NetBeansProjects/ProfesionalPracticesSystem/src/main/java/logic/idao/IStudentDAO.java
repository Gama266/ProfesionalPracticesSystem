/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import logic.businessObject.Student;
import logic.exceptions.DAOException;

/**
 *
 * @author gamal
 */
public interface IStudentDAO {
     boolean registerStudent(Student newStudent) throws DAOException;
    boolean updateStudent(Student student) throws DAOException;
   
}
