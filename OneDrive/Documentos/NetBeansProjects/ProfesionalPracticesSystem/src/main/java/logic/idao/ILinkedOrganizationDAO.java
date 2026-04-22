/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import java.util.List;
import logic.businessObject.LinkedOrganization;
import logic.exceptions.DAOException;

/**
 *
 * @author jonatanYeray
 */
public interface ILinkedOrganizationDAO {
     boolean registerLinkedOrganization(LinkedOrganization linkedOrganization)throws DAOException;
     boolean deleteLinkedOrganization(String nombre) throws DAOException; 
     
}
