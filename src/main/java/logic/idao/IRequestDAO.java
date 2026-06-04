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
public interface IRequestDAO {
  int getRequestCountByPracticante(String matricula) throws DAOException;
    boolean hasAlreadyRequested(String matricula, int idProyecto) throws DAOException;
    boolean registerRequest(String matricula, int idProyecto) throws DAOException;
    List<Project> getProjectsRequestedByStudent(String matricula) throws DAOException;
    boolean approveRequest(String matricula, int idProyecto) throws DAOException;
    boolean rejectRemainingRequests(String matricula, int approvedIdProyecto) throws DAOException;
    boolean rejectAllRequestsForStudent(String matricula) throws DAOException;
}