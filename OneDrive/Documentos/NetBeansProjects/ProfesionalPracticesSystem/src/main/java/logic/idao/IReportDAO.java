/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import logic.businessObject.Report;
import logic.exceptions.DAOException;

/**
 *
 * @author gamal
 */
public interface IReportDAO {
    boolean registerReport(Report newReport) throws DAOException;

 
    boolean updateReport(Report report) throws DAOException;
}
