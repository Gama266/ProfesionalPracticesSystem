/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import logic.businessObject.Project;
import logic.exceptions.DAOException;

/**
 *
 * @author gamal
 */
public interface IProjectDAO {
    boolean registerProject(Project newProject) throws DAOException;
    boolean updateProject(Project project) throws DAOException;
}
