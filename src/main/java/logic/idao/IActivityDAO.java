/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import logic.businessObject.Activity;
import logic.exceptions.DAOException;


/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public interface IActivityDAO {
      boolean registerActivity(Activity activity) throws DAOException;
    boolean registerActivities(List<Activity> activities) throws DAOException;
    List<Activity> getAll() throws DAOException;
    List<Activity> getActivitiesByProject(int idProyecto) throws DAOException;
    List<Activity> getByDateRange(LocalDate startDate, LocalDate endDate) throws DAOException;
    List<Activity> getByProjectAndDateRange(int idProyecto, LocalDate startDate, LocalDate endDate) throws DAOException;
    int countActivitiesByProject(int idProyecto) throws DAOException;
    boolean updateActivity(Activity activity) throws DAOException;
    boolean deleteActivity(int idActividad) throws DAOException;
    double getTotalHoursByProject(int projectId) throws DAOException;
    java.util.Map<Integer, Integer> getMaxProgressByProject(int idProyecto) throws DAOException;
}