/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
/*
 * @(#)RegisterActivityController.java 1.1 22/04/2026
 * Copyright (c) 2026 JhonatanYerayLIS
 */
package gui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import logic.businessObject.Activity;
import logic.businessObject.Project;
import logic.dao.ActivityDAO;
import logic.dao.ProjectDAO;
import logic.exceptions.DAOException;

public class RegisterActivityGUIController implements Initializable {

    private static final Logger logger =
            Logger.getLogger(RegisterActivityGUIController.class.getName());

    @FXML private ComboBox<Project> projectComboBox;
    @FXML private Label projectError;

    @FXML private TextField  nameField;
    @FXML private TextArea   descriptionArea;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField  plannedHoursField;

    @FXML private Label nameError;
    @FXML private Label descriptionError;
    @FXML private Label startDateError;
    @FXML private Label endDateError;
    @FXML private Label plannedHoursError;
    @FXML private Label resultLabel;

    @Override
    public void initialize(URL locationUrl, ResourceBundle resourceBundle) {
        initializePlannedHoursListener();
        loadProjectsIntoComboBox(); 
    }


    private void loadProjectsIntoComboBox() {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            List<Project> activeProjects = projectDAO.getAllActiveProjects();
            
            projectComboBox.setItems(FXCollections.observableArrayList(activeProjects));
            
            projectComboBox.setConverter(new StringConverter<Project>() {
                @Override
                public String toString(Project project) {
                    return project != null ? project.getName() : "";
                }

                @Override
                public Project fromString(String string) {
                    return null; 
                }
            });
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "No se pudieron cargar los proyectos en el ComboBox", e);
            showSystemAlert(Alert.AlertType.ERROR, "Error de conexión", 
                "No se pudieron cargar los proyectos de la base de datos.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent actionEvent) {
        if (!validateFormFieldsAreValid()) return;

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmar registro");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("¿Seguro de registro de actividad?");

        Optional<ButtonType> userChoice = confirmationAlert.showAndWait();
        if (userChoice.isEmpty() || userChoice.get() != ButtonType.OK) {
            showSystemAlert(Alert.AlertType.INFORMATION, "Cancelado",
                "Registro de actividad cancelada.");
            return;
        }

        try {
            ActivityDAO activityDataAccessObject = new ActivityDAO();
            Activity newActivity = buildActivityFromForm();

            if (activityDataAccessObject.registerActivity(newActivity)) {
                showSystemAlert(Alert.AlertType.INFORMATION, "Registro exitoso",
                    "Registro Exitoso.");
                clearFormFields();
            } else {
                showSystemAlert(Alert.AlertType.ERROR, "Error",
                    "No se pudo registrar la actividad. Intenta de nuevo.");
            }

        } catch (DAOException dataAccessException) {
            logger.log(Level.SEVERE, "Error al registrar la actividad", dataAccessException);
            showSystemAlert(Alert.AlertType.ERROR, "Error de conexión",
                "Error de conexión a la base de datos.");
        }
    }

    @FXML
    private void handleClear(ActionEvent actionEvent) {
        clearFormFields();
    }

    private boolean validateFormFieldsAreValid() {
        boolean isValid = true;

       
        if (projectComboBox.getValue() == null) {
            showFieldError(projectError, "Debes seleccionar un proyecto de la lista.");
            isValid = false;
        } else {
            hideFieldError(projectError);
        }

        if (nameField.getText().trim().isEmpty()) {
            showFieldError(nameError, "El nombre es obligatorio.");
            isValid = false;
        } else if (nameField.getText().trim().length() > 100) {
            showFieldError(nameError, "Máximo 100 caracteres.");
            isValid = false;
        } else {
            hideFieldError(nameError);
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            showFieldError(descriptionError, "La descripción es obligatoria.");
            isValid = false;
        } else if (descriptionArea.getText().trim().length() < 10) {
            showFieldError(descriptionError, "Mínimo 10 caracteres.");
            isValid = false;
        } else {
            hideFieldError(descriptionError);
        }

        if (startDatePicker.getValue() == null) {
            showFieldError(startDateError, "Selecciona la fecha de inicio.");
            isValid = false;
        } else {
            hideFieldError(startDateError);
        }

        if (endDatePicker.getValue() == null) {
            showFieldError(endDateError, "Selecciona la fecha de fin.");
            isValid = false;
        } else if (startDatePicker.getValue() != null &&
                   endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            showFieldError(endDateError, "La fecha de fin no puede ser antes de la fecha de inicio.");
            isValid = false;
        } else {
            hideFieldError(endDateError);
        }

        if (plannedHoursField.getText().trim().isEmpty()) {
            showFieldError(plannedHoursError, "Las horas planeadas son obligatorias.");
            isValid = false;
        } else {
            try {
                double hours = Double.parseDouble(plannedHoursField.getText().trim());
                if (hours <= 0) {
                    showFieldError(plannedHoursError, "Las horas deben ser mayor a 0.");
                    isValid = false;
                } else {
                    hideFieldError(plannedHoursError);
                }
            } catch (NumberFormatException numberFormatException) {
                showFieldError(plannedHoursError, "Ingresa un número válido (ej. 48.0).");
                isValid = false;
            }
        }

        return isValid;
    }

    private Activity buildActivityFromForm() {
        Project selectedProject = projectComboBox.getValue();

        Activity newActivity = new Activity();
        newActivity.setProject(selectedProject);
        newActivity.setName(nameField.getText().trim());
        newActivity.setDescription(descriptionArea.getText().trim());
        newActivity.setStartDate(startDatePicker.getValue());
        newActivity.setEndDate(endDatePicker.getValue());
        newActivity.setPlannedHours(Double.parseDouble(plannedHoursField.getText().trim()));

        return newActivity;
    }

    private void initializePlannedHoursListener() {
        plannedHoursField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                plannedHoursField.setText(oldVal);
            }
            hideFieldError(plannedHoursError);
        });
    }

    private void clearFormFields() {
        projectComboBox.setValue(null); 
        nameField.clear();
        descriptionArea.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        plannedHoursField.clear();

        hideFieldError(projectError);
        hideFieldError(nameError);
        hideFieldError(descriptionError);
        hideFieldError(startDateError);
        hideFieldError(endDateError);
        hideFieldError(plannedHoursError);

        resultLabel.setVisible(false);
        resultLabel.setManaged(false);
    }

    private void showFieldError(Label errorLabel, String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideFieldError(Label errorLabel) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void showSystemAlert(Alert.AlertType alertType, String windowTitle, String alertMessage) {
        Alert systemAlert = new Alert(alertType);
        systemAlert.setTitle(windowTitle);
        systemAlert.setHeaderText(null);
        systemAlert.setContentText(alertMessage);
        systemAlert.showAndWait();
    }
}