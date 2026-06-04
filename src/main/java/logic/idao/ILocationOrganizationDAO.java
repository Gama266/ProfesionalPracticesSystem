/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import logic.businessObject.LocationOrganization;
import logic.exceptions.DAOException;

/**
 *
 * @author gamal
 */
public interface ILocationOrganizationDAO {
    boolean updateLocation(LocationOrganization location) throws DAOException;
    boolean registerLocation(LocationOrganization newLocation) throws DAOException;
    boolean deleteLocation(String country, String state) throws DAOException;
    int getExistingLocationId(String country, String state) throws DAOException;
}
