/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;


import java.util.List;

import logic.businessObject.SelfAssessment;
import logic.exceptions.DAOException;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public interface ISelfAssessmentDAO {
    public boolean registerSelfAssessment(SelfAssessment selfAssessment) throws DAOException;

    public List<SelfAssessment> getAllSelfAssessments() throws DAOException;

}