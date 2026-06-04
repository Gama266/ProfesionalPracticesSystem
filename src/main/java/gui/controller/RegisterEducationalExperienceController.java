/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
/*
 * @(#)RegisterEducationalExperienceController.java 1.0 22/04/2026
 * Copyright (c) 2026 JhonatanYerayLIS
 */
package gui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import logic.businessObject.EducationalExperience;
import logic.dao.EducationalExperienceDAO;
import logic.exceptions.DAOException;

/**   
 * @author Jhonatan Yeray Hernández Rivera
 * 
 * @version 1.1
 */
public class RegisterEducationalExperienceController implements Initializable {

    private static final Logger logger =
            Logger.getLogger(RegisterEducationalExperienceController.class.getName());

    
    @FXML private TextField nrcField;
    @FXML private TextField sectionField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private Label nrcError;
    @FXML private Label sectionError;
    @FXML private Label startDateError;
    @FXML private Label endDateError;
    @FXML private Label resultLabel;

    private final EducationalExperienceDAO educationalExperienceDAO;

    public RegisterEducationalExperienceController() {
        this.educationalExperienceDAO = new EducationalExperienceDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeComponents();
    }

    private void initializeComponents() {
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusMonths(6));

     
        nrcField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nrcField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

       
        nrcField.textProperty().addListener((observable, oldValue, newValue) -> hideError(nrcError));
        sectionField.textProperty().addListener((observable, oldValue, newValue) -> hideError(sectionError));
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> hideError(startDateError));
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> hideError(endDateError));
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        hideResultMessage();

        if (!validateForm()) return;

        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc(Integer.parseInt(nrcField.getText().trim()));
        educationalExperience.setSection(sectionField.getText().trim());
        educationalExperience.setStartDate(startDatePicker.getValue());
        educationalExperience.setEndDate(endDatePicker.getValue());

        try {
            boolean success = educationalExperienceDAO.registerEducationalExperience(educationalExperience);

            if (success) {
                showSuccessMessage(
                        "Experiencia Educativa registrada correctamente."
                        + "  (NRC: " + educationalExperience.getNrc()
                        + "  ·  Sección: " + educationalExperience.getSection() + ")");
                clearFields();
                logger.info("Experiencia Educativa registrada — NRC: " + educationalExperience.getNrc());
            } else {
                showErrorMessage("No se pudo registrar la Experiencia Educativa."
                        + " Verifica los datos e intenta de nuevo.");
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error al registrar Experiencia Educativa", e);
            showErrorMessage("Error de base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        nrcField.clear();
        sectionField.clear();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusMonths(6));

        hideError(nrcError);
        hideError(sectionError);
        hideError(startDateError);
        hideError(endDateError);
        hideResultMessage();
    }

    private boolean validateForm() {
        boolean isValid = true;

        String nrcText = nrcField.getText().trim();
        if (nrcText.isEmpty()) {
            showFieldError(nrcError, "El NRC es obligatorio.");
            isValid = false;
        } else {
            try {
                int nrc = Integer.parseInt(nrcText);
                if (nrc <= 0) {
                    showFieldError(nrcError, "El NRC debe ser mayor a 0.");
                    isValid = false;
                } else {
                    hideError(nrcError);
                }
            } catch (NumberFormatException e) {
                showFieldError(nrcError, "El NRC debe ser un número entero.");
                isValid = false;
            }
        }

        String section = sectionField.getText().trim();
        if (section.isEmpty()) {
            showFieldError(sectionError, "La sección es obligatoria.");
            isValid = false;
        } else if (section.length() > 20) {
            showFieldError(sectionError, "Máximo 20 caracteres.");
            isValid = false;
        } else {
            hideError(sectionError);
        }

        if (startDatePicker.getValue() == null) {
            showFieldError(startDateError, "Selecciona la fecha de inicio.");
            isValid = false;
        } else {
            hideError(startDateError);
        }

        if (endDatePicker.getValue() == null) {
            showFieldError(endDateError, "Selecciona la fecha de término.");
            isValid = false;
        } else if (startDatePicker.getValue() != null
                && !endDatePicker.getValue().isAfter(startDatePicker.getValue())) {
            showFieldError(endDateError, "Debe ser posterior a la fecha de inicio.");
            isValid = false;
        } else {
            hideError(endDateError);
        }

        return isValid;
    }

    private void showFieldError(Label label, String message) {
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
            "-fx-padding: 10 14 10 14;" +
            "-fx-font-size: 12.5px;" +
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
            "-fx-padding: 10 14 10 14;" +
            "-fx-font-size: 12.5px;" +
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