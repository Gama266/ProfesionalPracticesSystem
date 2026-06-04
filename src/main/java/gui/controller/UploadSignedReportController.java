package gui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import logic.businessObject.Report;
import logic.dao.ReportDAO;
import logic.exceptions.DAOException;

public class UploadSignedReportController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(UploadSignedReportController.class.getName());

    @FXML private Label selectedFileLabel;
    @FXML private Label fileErrorLabel;
    @FXML private Label resultLabel;

    private File selectedReportFile;
    
    private final ReportDAO reportDAO;

    public UploadSignedReportController() {
        this.reportDAO = new ReportDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hideError(fileErrorLabel);
        hideResultMessage();
    }

    @FXML
    private void handleSelectFile(ActionEvent event) {
        hideResultMessage();
        hideError(fileErrorLabel);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Reporte Firmado (PDF)");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
        );

        Stage stage = (Stage) selectedFileLabel.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            this.selectedReportFile = file;
            selectedFileLabel.setText(file.getName());
            LOGGER.info("Archivo seleccionado: " + file.getAbsolutePath());
        }
    }

    @FXML
    private void handleUpload(ActionEvent event) {
        hideResultMessage();
        hideError(fileErrorLabel);

        if (selectedReportFile == null) {
            showError(fileErrorLabel, "Por favor, seleccione un archivo PDF primero.");
            return;
        }

        try {
            Report report = new Report();
            report.setUrl(selectedReportFile.getAbsolutePath());
            report.setTypeOfReport("Signed"); 
            report.setStudent(null);          

            LOGGER.info("Intentando persistir reporte en BD: " + selectedReportFile.getName());
            
           
            boolean wasRegistered = reportDAO.registerReport(report);

            if (wasRegistered) {
                showSuccessMessage("¡El reporte \"" + selectedReportFile.getName() 
                        + "\" se guardó en la base de datos con éxito!");
                clearAllFields();
            } else {
                showErrorMessage("No se pudo registrar el reporte. Intente de nuevo.");
            }

        } catch (DAOException exception) {
            
            LOGGER.log(Level.SEVERE, "Error en la capa de datos al registrar reporte", exception);
            showErrorMessage("Error de base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error inesperado del sistema", exception);
            showErrorMessage("Ocurrió un error inesperado. Intente más tarde.");
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearAllFields();
    }

    private void clearAllFields() {
        this.selectedReportFile = null;
        selectedFileLabel.setText("Ningún archivo seleccionado");
        hideError(fileErrorLabel);
        hideResultMessage();
    }

    
    
    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void hideError(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    private void showSuccessMessage(String message) {
        resultLabel.setText(message);
        resultLabel.setStyle(
            "-fx-background-color: #E6F8F5;" +
            "-fx-border-color: #2BBFAA;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 7;" +
            "-fx-background-radius: 7;" +
            "-fx-padding: 10;" +
            "-fx-text-fill: #0D7A6B;"
        );
        resultLabel.setVisible(true);
        resultLabel.setManaged(true);
    }

    private void showErrorMessage(String message) {
        resultLabel.setText(message);
        resultLabel.setStyle(
            "-fx-background-color: #FDECEA;" +
            "-fx-border-color: #EF9A9A;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 7;" +
            "-fx-background-radius: 7;" +
            "-fx-padding: 10;" +
            "-fx-text-fill: #B71C1C;"
        );
        resultLabel.setVisible(true);
        resultLabel.setManaged(true);
    }

    private void hideResultMessage() {
        resultLabel.setVisible(false);
        resultLabel.setManaged(false);
    }
}