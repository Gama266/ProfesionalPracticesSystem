/*
 * @(#)RegistroExperienciaEducativaController.java 1.0 22/04/2026
 * Copyright (c) 2026 JhonatanYerayLIS
 */
package gui.controller;
 

import gui.view.RegisterEducationalExperienceView;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import logic.businessObject.EducationalExperience;
import logic.dao.EducationalExperienceDAO;
import logic.exceptions.DAOException;

 
/**
 * Controller of the Educational Experience Registration form.
 * Receives the view, links the button events, and orchestrates
 * the validation and persistence through {@link EducationalExperienceDAO}.
 *
 * @author Jhonatan Yeray Hernández Rivera
 * @version 1.0
 */
public class RegisterEducationalExperienceController {
 
    private static final Logger logger =
            Logger.getLogger(RegisterEducationalExperienceController.class.getName());
 
    private final RegisterEducationalExperienceView view;
    private final EducationalExperienceDAO educationalExperienceDAO;
 
    public RegisterEducationalExperienceController(RegisterEducationalExperienceView view) {
        this.view = view;
        this.educationalExperienceDAO = new EducationalExperienceDAO();
 
        initializeComponents();
        bindEvents();
    }

    private void initializeComponents() {
        view.getStartDatePicker().setValue(LocalDate.now());
        view.getEndDatePicker().setValue(LocalDate.now().plusMonths(6));

        view.getNrcField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                view.getNrcField().setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        view.getNrcField().textProperty()
                .addListener((observable, oldValue, newValue) -> hideError(view.getNrcError()));
        view.getSectionField().textProperty()
                .addListener((observable, oldValue, newValue) -> hideError(view.getSectionError()));
        view.getStartDatePicker().valueProperty()
                .addListener((observable, oldValue, newValue) -> hideError(view.getStartDateError()));
        view.getEndDatePicker().valueProperty()
                .addListener((observable, oldValue, newValue) -> hideError(view.getEndDateError()));
    }

    private void bindEvents() {
        view.getRegisterButton().setOnAction(event -> handleRegister());
        view.getClearButton().setOnAction(event -> handleClear());
    }

    private void handleRegister() {
        hideResultMessage();
 
        if (!validateForm()) return;

        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc(Integer.parseInt(view.getNrcField().getText().trim()));
        educationalExperience.setSection(view.getSectionField().getText().trim());
        educationalExperience.setStartDate(view.getStartDatePicker().getValue());
        educationalExperience.setEndDate(view.getEndDatePicker().getValue());
 
        try {
            boolean success = educationalExperienceDAO
                    .registerEducationalExperience(educationalExperience);
 
            if (success) {
                showSuccessMessage(
                        "Experiencia Educativa registrada correctamente."
                        + "  (NRC: " + educationalExperience.getNrc()
                        + "  ·  Sección: " + educationalExperience.getSection() + ")");
                handleClear();
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

    private void handleClear() {
        view.getNrcField().clear();
        view.getSectionField().clear();
        view.getStartDatePicker().setValue(LocalDate.now());
        view.getEndDatePicker().setValue(LocalDate.now().plusMonths(6));
 
        hideError(view.getNrcError());
        hideError(view.getSectionError());
        hideError(view.getStartDateError());
        hideError(view.getEndDateError());
        hideResultMessage();
    }

    private boolean validateForm() {
        boolean isValid = true;

        String nrcText = view.getNrcField().getText().trim();
        if (nrcText.isEmpty()) {
            showFieldError(view.getNrcError(), "El NRC es obligatorio.");
            isValid = false;
        } else {
            try {
                int nrc = Integer.parseInt(nrcText);
                if (nrc <= 0) {
                    showFieldError(view.getNrcError(), "El NRC debe ser mayor a 0.");
                    isValid = false;
                } else {
                    hideError(view.getNrcError());
                }
            } catch (NumberFormatException e) {
                showFieldError(view.getNrcError(), "El NRC debe ser un número entero.");
                isValid = false;
            }
        }

        String section = view.getSectionField().getText().trim();
        if (section.isEmpty()) {
            showFieldError(view.getSectionError(), "La sección es obligatoria.");
            isValid = false;
        } else if (section.length() > 20) {
            showFieldError(view.getSectionError(), "Máximo 20 caracteres.");
            isValid = false;
        } else {
            hideError(view.getSectionError());
        }

        if (view.getStartDatePicker().getValue() == null) {
            showFieldError(view.getStartDateError(),
                    "Selecciona la fecha de inicio.");
            isValid = false;
        } else {
            hideError(view.getStartDateError());
        }

        if (view.getEndDatePicker().getValue() == null) {
            showFieldError(view.getEndDateError(),
                    "Selecciona la fecha de término.");
            isValid = false;
        } else if (view.getStartDatePicker().getValue() != null
                && !view.getEndDatePicker().getValue()
                         .isAfter(view.getStartDatePicker().getValue())) {
            showFieldError(view.getEndDateError(),
                    "Debe ser posterior a la fecha de inicio.");
            isValid = false;
        } else {
            hideError(view.getEndDateError());
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
        Label resultLabel = view.getResultLabel();
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
        Label resultLabel = view.getResultLabel();
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
        view.getResultLabel().setVisible(false);
        view.getResultLabel().setManaged(false);
    }
}
