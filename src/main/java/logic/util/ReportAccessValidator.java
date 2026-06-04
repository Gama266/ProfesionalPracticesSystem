/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.util;

import javafx.scene.control.Alert;
import logic.dao.ReportDAO;
import logic.exceptions.DAOException;
import logic.businessObject.SessionManager;


 
public class ReportAccessValidator {


    public static final int PARTIAL_REPORT_HOURS = 210;
    public static final int TOTAL_REPORT_HOURS   = 420;


    private final ReportDAO reportDAO = new ReportDAO();

    
    public boolean canAccessPartialReport() {
        try {
            String enrollment    = SessionManager.getCurrentEnrollment();
            double approvedHours = reportDAO.getAccumulatedApprovedHours(enrollment);

            if (approvedHours < PARTIAL_REPORT_HOURS) {
                showError("Reporte Parcial Bloqueado",
                    "Necesitas al menos " + PARTIAL_REPORT_HOURS +
                    " horas aprobadas para generar el reporte parcial.\n" +
                    "Horas actuales: " + (int) approvedHours + " hrs.");
                return false;
            }
            return true;

        } catch (DAOException e) {
            showError("Error de validación", e.getMessage());
            return false;
        }
    }

  
    public boolean canAccessFinalReport() {
        try {
            String enrollment    = SessionManager.getCurrentEnrollment();
            double approvedHours = reportDAO.getAccumulatedApprovedHours(enrollment);

       
            if (approvedHours < TOTAL_REPORT_HOURS) {
                showError("Reporte Final Bloqueado",
                    "Necesitas al menos " + TOTAL_REPORT_HOURS +
                    " horas aprobadas para generar el reporte final.\n" +
                    "Horas actuales: " + (int) approvedHours + " hrs.");
                return false;
            }


            boolean hasApprovedPartial = reportDAO.hasApprovedPartialReport(enrollment);
            if (!hasApprovedPartial) {
                showError("Reporte Final Bloqueado",
                    "Debes tener un reporte parcial aprobado antes de " +
                    "poder generar el reporte final.");
                return false;
            }

            return true;

        } catch (DAOException e) {
            showError("Error de validación", e.getMessage());
            return false;
        }
    }

   
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}