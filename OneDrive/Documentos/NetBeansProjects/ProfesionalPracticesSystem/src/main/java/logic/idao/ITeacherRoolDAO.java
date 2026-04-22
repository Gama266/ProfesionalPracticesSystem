/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import logic.businessObject.TeacherRool;
import logic.exceptions.DAOException;

/**
 *
 * @author Yeray
 */
public interface ITeacherRoolDAO {
     public boolean registerSelfTeacherRool(TeacherRool teacherRool) throws DAOException;
}
