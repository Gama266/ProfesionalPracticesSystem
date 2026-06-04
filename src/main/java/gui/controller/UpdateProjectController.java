package gui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import logic.businessObject.Project;
import logic.dao.ProjectDAO;
import logic.exceptions.DAOException;

public class UpdateProjectController implements Initializable {

    private static final Logger LOGGER =
            Logger.getLogger(UpdateProjectController.class.getName());

    private static final int MAX_NAME_LENGTH        = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_METHODOLOGY_LENGTH = 100;
    private static final int MAX_OBJECTIVES_LENGTH  = 500;
    private static final int MIN_TEXT_LENGTH        = 3;

    @FXML private TextField projectIdTextField;
    @FXML private Label     searchErrorLabel;

    @FXML private TextField  projectNameTextField;
    @FXML private TextArea   projectDescriptionTextArea;
    @FXML private TextField  methodologyTextField;
    @FXML private TextArea   objectivesTextArea;
    @FXML private DatePicker registrationDatePicker;
    @FXML private CheckBox   activeStatusCheckBox;

    @FXML private Label nameErrorLabel;
    @FXML private Label descriptionErrorLabel;
    @FXML private Label methodologyErrorLabel;
    @FXML private Label objectivesErrorLabel;
    @FXML private Label dateErrorLabel;

    @FXML private Button updateProjectButton;
    @FXML private Label  resultLabel;

    private final ProjectDAO projectDAO;

  
    private Project loadedProject;

    public UpdateProjectController() {
        this.projectDAO = new ProjectDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        attachInputListeners();
    }

    
    private void attachInputListeners() {
        projectIdTextField.textProperty().addListener(
                (obs, oldVal, newVal) -> hideError(searchErrorLabel));
        projectNameTextField.textProperty().addListener(
                (obs, oldVal, newVal) -> hideError(nameErrorLabel));
        projectDescriptionTextArea.textProperty().addListener(
                (obs, oldVal, newVal) -> hideError(descriptionErrorLabel));
        methodologyTextField.textProperty().addListener(
                (obs, oldVal, newVal) -> hideError(methodologyErrorLabel));
        objectivesTextArea.textProperty().addListener(
                (obs, oldVal, newVal) -> hideError(objectivesErrorLabel));
        registrationDatePicker.valueProperty().addListener(
                (obs, oldVal, newVal) -> hideError(dateErrorLabel));
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        hideResultMessage();
        hideError(searchErrorLabel);

        String idText = projectIdTextField.getText().trim();

        if (!isValidProjectIdInput(idText)) {
            return;
        }

        int projectId = Integer.parseInt(idText);

        try {
            Project foundProject = projectDAO.getProjectById(projectId);

            if (foundProject == null) {
                /* No row returned – expected outcome, not a crash. */
                LOGGER.warning("Búsqueda sin resultado — ID: " + projectId);
                showError(searchErrorLabel,
                        "No se encontró ningún proyecto con el ID "
                        + projectId + ".");
                disableForm();
                return;
            }

            populateForm(foundProject);
            loadedProject = foundProject;
            updateProjectButton.setDisable(false);
            LOGGER.info("Proyecto cargado para edición — ID: " + projectId);

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar proyecto por ID", e);
            showError(searchErrorLabel,
                    "Error de base de datos al buscar el proyecto. "
                    + "Intenta de nuevo más tarde.");
        }
    }

    private boolean isValidProjectIdInput(String idText) {
        if (idText.isEmpty()) {
            showError(searchErrorLabel, "Ingresa el ID del proyecto.");
            return false;
        }

        try {
            int id = Integer.parseInt(idText);
            if (id <= 0) {
                showError(searchErrorLabel,
                        "El ID debe ser un número entero mayor a 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(searchErrorLabel,
                    "El ID debe ser un número entero (ej: 12).");
            return false;
        }

        return true;
    }

    private void populateForm(Project project) {
        projectNameTextField.setText(
                project.getName() != null ? project.getName() : "");
        projectDescriptionTextArea.setText(
                project.getDescription() != null
                        ? project.getDescription() : "");
        methodologyTextField.setText(
                project.getMethodology() != null
                        ? project.getMethodology() : "");
        objectivesTextArea.setText(
                project.getObjective() != null
                        ? project.getObjective() : "");

        registrationDatePicker.setValue(
                project.getRegistrationDate() != null
                        ? project.getRegistrationDate()
                        : LocalDate.now());

        activeStatusCheckBox.setSelected(project.getActivityStatus());
    }


    @FXML
    private void handleUpdate(ActionEvent event) {
        hideResultMessage();

        if (!validateForm()) {
            return;
        }
        
        applyFormToProject(loadedProject);

        try {
            boolean updated = projectDAO.updateProject(loadedProject);

            if (updated) {
                showSuccessMessage(
                        "Proyecto actualizado correctamente. "
                        + "(ID: " + loadedProject.getId()
                        + " · Nombre: " + loadedProject.getName() + ")");
                LOGGER.info("Proyecto actualizado — ID: "
                        + loadedProject.getId());
            } else {
                showErrorMessage(
                        "No se realizaron cambios. Verifica que el "
                        + "proyecto aún exista en la base de datos.");
            }

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error crítico al actualizar proyecto", e);
            showErrorMessage(
                    "Error de base de datos al actualizar. "
                    + "Intenta de nuevo más tarde.");
        }
    }

    private void applyFormToProject(Project project) {
        project.setName(projectNameTextField.getText().trim());
        project.setDescription(projectDescriptionTextArea.getText().trim());
        project.setMethodology(methodologyTextField.getText().trim());
        project.setObjective(objectivesTextArea.getText().trim());
        project.setRegistrationDate(registrationDatePicker.getValue());
        project.setActivityStatus(activeStatusCheckBox.isSelected());
    }

   
    private boolean validateForm() {
        boolean isValid = true;

        isValid &= validateName();
        isValid &= validateDescription();
        isValid &= validateMethodology();
        isValid &= validateObjectives();
        isValid &= validateRegistrationDate();

        return isValid;
    }
    private boolean validateName() {
        return validateTextField(
                projectNameTextField.getText().trim(),
                nameErrorLabel,
                "El nombre es obligatorio.",
                MAX_NAME_LENGTH
        );
    }

    private boolean validateDescription() {
        return validateTextField(
                projectDescriptionTextArea.getText().trim(),
                descriptionErrorLabel,
                "La descripción es obligatoria.",
                MAX_DESCRIPTION_LENGTH
        );
    }

    private boolean validateMethodology() {
        String methodology = methodologyTextField.getText().trim();

        if (methodology.isEmpty()) {
            showError(methodologyErrorLabel, "La metodología es obligatoria.");
            return false;
        }

        if (methodology.length() > MAX_METHODOLOGY_LENGTH) {
            showError(methodologyErrorLabel,
                    "Máximo " + MAX_METHODOLOGY_LENGTH + " caracteres.");
            return false;
        }

        hideError(methodologyErrorLabel);
        return true;
    }

    private boolean validateObjectives() {
        return validateTextField(
                objectivesTextArea.getText().trim(),
                objectivesErrorLabel,
                "Los objetivos son obligatorios.",
                MAX_OBJECTIVES_LENGTH
        );
    }

    private boolean validateRegistrationDate() {
        LocalDate date = registrationDatePicker.getValue();

        if (date == null) {
            showError(dateErrorLabel,
                    "Selecciona la fecha de registro del proyecto.");
            return false;
        }

        if (date.isAfter(LocalDate.now())) {
            showError(dateErrorLabel, "La fecha no puede ser futura.");
            return false;
        }

        hideError(dateErrorLabel);
        return true;
    }
    private boolean validateTextField(
        String text,
        Label errorLabel,
        String emptyMessage,
        int maxLength
) {

    if (text.isEmpty()) {
        showError(errorLabel, emptyMessage);
        return false;
    }

    if (text.length() < MIN_TEXT_LENGTH) {
        showError(errorLabel,
                "Mínimo " + MIN_TEXT_LENGTH + " caracteres.");
        return false;
    }

    if (text.length() > maxLength) {
        showError(errorLabel,
                "Máximo " + maxLength + " caracteres.");
        return false;
    }

    hideError(errorLabel);
    return true;
}
    
    @FXML
    private void handleClear(ActionEvent event) {
        clearAllFields();
    }

    private void clearAllFields() {
        projectIdTextField.clear();
        projectNameTextField.clear();
        projectDescriptionTextArea.clear();
        methodologyTextField.clear();
        objectivesTextArea.clear();
        registrationDatePicker.setValue(null);
        activeStatusCheckBox.setSelected(false);

        hideError(searchErrorLabel);
        hideError(nameErrorLabel);
        hideError(descriptionErrorLabel);
        hideError(methodologyErrorLabel);
        hideError(objectivesErrorLabel);
        hideError(dateErrorLabel);
        hideResultMessage();

        disableForm();
        loadedProject = null;
    }

    private void disableForm() {
        updateProjectButton.setDisable(true);
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

