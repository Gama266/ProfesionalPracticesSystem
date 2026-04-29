/*
 * @(#)RegistroActividadController.java 1.0 22/04/2026
 * Copyright (c) 2026 JhonatanYerayLIS
 */

package gui.controller;
 

import gui.view.RegisterActivityView;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import logic.businessObject.Activity;
import logic.businessObject.Student;
import logic.dao.ActivityDAO;
import logic.exceptions.DAOException;


 
public class RegisterActivityController {
 
    private static final Logger logger =
            Logger.getLogger(RegisterActivityController.class.getName());
 
    private final RegisterActivityView view;
    private final ActivityDAO activityDAO;
    
    public RegisterActivityController(RegisterActivityView view) {
        this.view = view;
        this.activityDAO = new ActivityDAO();
 
        initializeComponents();
        bindEvents();
    }
 
    private void initializeComponents() {
        view.getDatePicker().setValue(LocalDate.now());

        view.getHoursField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                view.getHoursField().setText(oldValue);
            }
        });

        view.getStudentIdField().textProperty()
                .addListener((observable, oldValue, newValue) -> hideError(view.getStudentIdError()));
        view.getHoursField().textProperty()
                .addListener((observable, oldValue, newValue) -> hideError(view.getHoursError()));
        view.getDescriptionArea().textProperty()
                .addListener((observable, oldValue, newValue) -> hideError(view.getDescriptionError()));
        view.getDatePicker().valueProperty()
                .addListener((observable, oldValue, newValue) -> hideError(view.getDateError()));
    }
 
    private void bindEvents() {
        view.getRegisterButton().setOnAction(event -> handleRegister());
        view.getClearButton().setOnAction(event -> handleClear());
    }

    private void handleRegister() {
        hideResultMessage();
 
        if (!validateForm()) return;

        Student student = new Student();
        student.setMatricula(view.getStudentIdField().getText().trim().toUpperCase());

        Activity activity = new Activity();
        activity.setStudent(student);
        activity.setDate(view.getDatePicker().getValue());
        activity.setHours(Float.parseFloat(view.getHoursField().getText().trim()));
        activity.setDescription(view.getDescriptionArea().getText().trim());
 
        try {
            boolean success = activityDAO.registerActivity(activity);
 
            if (success) {
                showSuccessMessage(" Actividad registrada correctamente. "
                        + "(Matrícula: " + student.getMatricula()
                        + " · Horas: " + activity.getHours() + ")");
                handleClear();
                logger.info(() -> "Actividad registrada — Matrícula: " + student.getMatricula());
            } else {
                showErrorMessage("No se pudo registrar la actividad. Verifica los datos e intenta de nuevo.");
            }
 
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error al registrar la actividad", e);
            showErrorMessage("Error en la base de datos. Por favor, intenta de nuevo más tarde.");
        }
    }

    private void handleClear() {
        view.getStudentIdField().clear();
        view.getDatePicker().setValue(LocalDate.now());
        view.getHoursField().clear();
        view.getDescriptionArea().clear();
 
        hideError(view.getStudentIdError());
        hideError(view.getDateError());
        hideError(view.getHoursError());
        hideError(view.getDescriptionError());
        hideResultMessage();
    }
    
    private boolean validateForm() {
        boolean isValid = true;

        String studentId = view.getStudentIdField().getText().trim();
        if (studentId.isEmpty()) {
            showFieldError(view.getStudentIdError(), "La matrícula es obligatoria.");
            isValid = false;
        } else if (studentId.length() > 10) {
            showFieldError(view.getStudentIdError(), "Máximo 10 caracteres.");
            isValid = false;
        } else {
            hideError(view.getStudentIdError());
        }

        if (view.getDatePicker().getValue() == null) {
            showFieldError(view.getDateError(), "Selecciona la fecha de la actividad.");
            isValid = false;
        } else if (view.getDatePicker().getValue().isAfter(LocalDate.now())) {
            showFieldError(view.getDateError(), "La fecha no puede ser futura.");
            isValid = false;
        } else {
            hideError(view.getDateError());
        }

        String hoursText = view.getHoursField().getText().trim();
        if (hoursText.isEmpty()) {
            showFieldError(view.getHoursError(), "Las horas son obligatorias.");
            isValid = false;
        } else {
            try {
                float hours = Float.parseFloat(hoursText);
                if (hours <= 0 || hours > 4) {
                    showFieldError(view.getHoursError(), "Las horas deben estar entre 0 y 4.");
                    isValid = false;
                } else {
                    hideError(view.getHoursError());
                }
            } catch (NumberFormatException e) {
                showFieldError(view.getHoursError(), "Ingresa un número válido (ej. 3.5).");
                isValid = false;
            }
        }

        String description = view.getDescriptionArea().getText().trim();
        if (description.isEmpty()) {
            showFieldError(view.getDescriptionError(), "La descripción es obligatoria.");
            isValid = false;
        } else if (description.length() < 10) {
            showFieldError(view.getDescriptionError(), "Mínimo 10 caracteres.");
            isValid = false;
        } else {
            hideError(view.getDescriptionError());
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
