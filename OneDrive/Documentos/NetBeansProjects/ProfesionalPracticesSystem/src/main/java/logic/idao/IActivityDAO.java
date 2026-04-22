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
    
    List<Activity> getAll()throws DAOException;
    
    List<Activity> getByDateRange(LocalDate startDate, LocalDate endDate)throws DAOException;
    
    List<Activity> getByStudentAndDateRange(String matricula, LocalDate startDate, LocalDate endDate)throws DAOException;
}