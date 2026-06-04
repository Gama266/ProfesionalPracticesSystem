/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import java.util.List;
import logic.businessObject.Project;
import logic.exceptions.DAOException;

/**
 *
 * @author gamal
 */
public interface IProjectDAO {
    boolean registerProject(Project newProject) throws DAOException;
    boolean updateProject(Project project) throws DAOException;
    boolean hideProject(int idProject) throws DAOException;
    List<Project> getAllActiveProjects() throws DAOException;
    Project getProjectById(int projectId) throws DAOException;
    List<Project> retrieveAllProjectsIncludingInactive() throws DAOException;
     boolean reactivateProject(int projectId) throws DAOException;
     boolean assignProjectToStudent(String matricula, int idProyecto) throws DAOException;
}
