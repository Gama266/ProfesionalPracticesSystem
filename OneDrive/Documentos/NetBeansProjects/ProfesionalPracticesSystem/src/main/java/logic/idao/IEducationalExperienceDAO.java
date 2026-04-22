/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import java.time.LocalDate;
import java.util.List;
import logic.businessObject.EducationalExperience;
import logic.exceptions.DAOException;


public interface IEducationalExperienceDAO {
       boolean registerEducationalExperience(EducationalExperience experience)throws DAOException;
   
   List<EducationalExperience> getByDateRange(LocalDate startDate, LocalDate endDate)
           throws DAOException;
}
