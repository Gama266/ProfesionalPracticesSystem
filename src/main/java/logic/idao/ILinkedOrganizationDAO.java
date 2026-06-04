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
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.1
 */
public interface ILinkedOrganizationDAO {
    boolean registerLinkedOrganization(LinkedOrganization linkedOrganization)throws DAOException;
   
 
   boolean isOrganizationAlreadyRegistered(String name) throws DAOException;
   public List<LinkedOrganization> getAllOrganizations() throws DAOException;
   
}