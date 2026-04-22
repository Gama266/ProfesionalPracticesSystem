/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import logic.businessObject.Teacher;
import logic.exceptions.DAOException;

/**
 * Interface for Teacher data access operations.
 * @author gamal
 */
public interface ITeacherDAO {
    
    boolean registerTeacher(Teacher newTeacher) throws DAOException;

    boolean updateTeacher(Teacher teacher) throws DAOException;
   
}