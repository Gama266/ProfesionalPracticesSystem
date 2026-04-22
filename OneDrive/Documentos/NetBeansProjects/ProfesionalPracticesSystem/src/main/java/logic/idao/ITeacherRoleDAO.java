/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import logic.businessObject.TeacherRole;
import logic.exceptions.DAOException;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public interface ITeacherRoleDAO {
    public boolean registerTeacherRole(TeacherRole teacherRole) throws DAOException;
        
}