/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.businessObject.ActivityAdvance;
import logic.exceptions.DAOException;

/**
 *
 * @author gamal
 */
public interface IActivityAdvanceDAO {
     List<ActivityAdvance> getAdvancesByReportId(int idReport) throws DAOException;
    void registerAdvances(List<ActivityAdvance> advances, int reportId, Connection connection) throws SQLException;
}
