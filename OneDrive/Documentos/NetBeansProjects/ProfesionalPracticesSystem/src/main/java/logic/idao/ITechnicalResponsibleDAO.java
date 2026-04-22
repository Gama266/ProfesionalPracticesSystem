/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import logic.businessObject.TechnicalResponsible;
import logic.exceptions.DAOException;

/**
 *
 * @author gamal
 */
public interface ITechnicalResponsibleDAO {
    boolean registerTechnicalResponsible(TechnicalResponsible newResponsible) throws DAOException;

    boolean updateTechnicalResponsible(TechnicalResponsible responsible) throws DAOException;
    
}
