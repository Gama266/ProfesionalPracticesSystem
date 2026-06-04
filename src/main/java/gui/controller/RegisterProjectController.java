package gui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import logic.businessObject.LinkedOrganization;
import logic.businessObject.Project;
import logic.businessObject.TechnicalResponsible;
import logic.dao.LinkedOrganizationDAO;
import logic.dao.ProjectDAO;
import logic.dao.TechnicalResponsibleDAO;
import logic.exceptions.DAOException;
import logic.exceptions.DuplicateEntryException;
import logic.exceptions.EntityNotFoundException;

public class RegisterProjectController implements Initializable {

    @FXML private TextField                        projectNameTextField;
    @FXML private TextArea                         projectDescriptionTextArea;
    @FXML private TextField                        methodologyTextField;
    @FXML private TextArea                         objectivesTextArea;
    @FXML private DatePicker                       registrationDatePicker;
    @FXML private ComboBox<LinkedOrganization>     linkedOrganizationComboBox;
    @FXML private ComboBox<TechnicalResponsible>   technicalResponsibleComboBox;
    @FXML private TextField                        capacityTextField;
    @FXML private CheckBox                         activeStatusCheckBox;
    @FXML private Label                            activityCounterLabel;

    private Consumer<Project> onProjectRegistered;

    public void setOnProjectRegistered(Consumer<Project> callback) {
        this.onProjectRegistered = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureLinkedOrganizationComboBox();
        configureTechnicalResponsibleComboBox();
        setupOrganizationSelectionListener();
        initializeDefaultValues();
    }

    // ── Configuración de combos ──────────────────────────────────────────────

    private void configureLinkedOrganizationComboBox() {
        linkedOrganizationComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(LinkedOrganization org) {
                return org == null ? "" : org.getName();
            }
            @Override public LinkedOrganization fromString(String s) { return null; }
        });
        linkedOrganizationComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(LinkedOrganization org, boolean empty) {
                super.updateItem(org, empty);
                setText(!empty && org != null ? org.getName() : null);
            }
        });
        loadLinkedOrganizations();
    }

    private void configureTechnicalResponsibleComboBox() {
        technicalResponsibleComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(TechnicalResponsible tr) {
                return tr == null ? "" : tr.getName() + " " + tr.getPaternalSurname();
            }
            @Override public TechnicalResponsible fromString(String s) { return null; }
        });
        technicalResponsibleComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(TechnicalResponsible tr, boolean empty) {
                super.updateItem(tr, empty);
                setText(!empty && tr != null ? tr.getName() + " " + tr.getPaternalSurname() : null);
            }
        });
        technicalResponsibleComboBox.setDisable(true); // deshabilitado hasta elegir organización
    }

    private void setupOrganizationSelectionListener() {
        linkedOrganizationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            technicalResponsibleComboBox.getItems().clear();
            technicalResponsibleComboBox.setValue(null);
            if (newVal == null) {
                technicalResponsibleComboBox.setDisable(true);
                return;
            }
            loadTechnicalResponsiblesByOrganization(newVal.getId());
        });
    }

    private void loadLinkedOrganizations() {
        try {
            LinkedOrganizationDAO dao = new LinkedOrganizationDAO();
            List<LinkedOrganization> orgs = dao.getAllOrganizations();
            linkedOrganizationComboBox.setItems(FXCollections.observableArrayList(orgs));
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "No se pudieron cargar las organizaciones vinculadas.\n" + e.getMessage());
        }
    }

    private void loadTechnicalResponsiblesByOrganization(int idOrganization) {
        try {
            TechnicalResponsibleDAO dao = new TechnicalResponsibleDAO();
            List<TechnicalResponsible> responsibles = dao.getByOrganization(idOrganization);

            if (responsibles.isEmpty()) {
                technicalResponsibleComboBox.setDisable(true);
                showAlert(Alert.AlertType.WARNING, "Sin responsables",
                    "La organización seleccionada no tiene responsables técnicos registrados.\n" +
                    "Debe registrar al menos uno antes de poder crear el proyecto.");
            } else {
                technicalResponsibleComboBox.setItems(FXCollections.observableArrayList(responsibles));
                technicalResponsibleComboBox.setDisable(false);
            }
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "No se pudieron cargar los responsables técnicos.\n" + e.getMessage());
        }
    }

    // ── Acciones ─────────────────────────────────────────────────────────────

    @FXML
    private void RegisterClicked(ActionEvent event) {
        if (validateFields()) {
            Project project = buildProject();
            registerProject(project);
        }
    }

    @FXML
    private void ClearClicked(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void handleAddActivity(ActionEvent event) {
        System.out.println("Botón 'Agregar Actividad' presionado.");
    }

    // ── Registro ─────────────────────────────────────────────────────────────

    private void registerProject(Project project) {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            boolean wasRegistered = projectDAO.registerProject(project);
            if (wasRegistered) {
                showAlert(Alert.AlertType.INFORMATION, "Éxito al registrar el proyecto",
                    "El proyecto \"" + project.getName() + "\" fue registrado exitosamente.");
                if (onProjectRegistered != null) onProjectRegistered.accept(project);
                clearFields();
            } else {
                showAlert(Alert.AlertType.WARNING, "Sin cambios",
                    "No se pudo registrar el proyecto. Intente de nuevo.");
            }
        } catch (DuplicateEntryException e) {
            showAlert(Alert.AlertType.WARNING, "Proyecto duplicado", e.getMessage());
        } catch (EntityNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Entidad no encontrada", e.getMessage());
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error de base de datos",
                "Ocurrió un error inesperado al registrar el proyecto.");
        }
    }

    // ── Construcción del objeto ───────────────────────────────────────────────

    private Project buildProject() {
        Project project = new Project();
        project.setName(projectNameTextField.getText().trim());
        project.setDescription(projectDescriptionTextArea.getText().trim());
        project.setMethodology(methodologyTextField.getText().trim());
        project.setObjective(objectivesTextArea.getText().trim());
        project.setAvailableSpaces(Integer.parseInt(capacityTextField.getText().trim()));
        project.setRegistrationDate(registrationDatePicker.getValue());
        project.setActivityStatus(activeStatusCheckBox.isSelected());
        project.setLinkedOrganization(linkedOrganizationComboBox.getValue());
        project.setTechnicalResponsible(technicalResponsibleComboBox.getValue());
        return project;
    }

    // ── Validaciones ──────────────────────────────────────────────────────────

    private boolean validateFields() {
        return validateProjectName()
            && validateProjectDescription()
            && validateMethodology()
            && validateObjectives()
            && validateRegistrationDate()
            && validateLinkedOrganization()
            && validateTechnicalResponsible()
            && validateCapacity();
    }

    private boolean validateProjectName() {
        if (projectNameTextField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo requerido", "El nombre del proyecto es obligatorio.");
            projectNameTextField.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateProjectDescription() {
        if (projectDescriptionTextArea.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo requerido", "La descripción del proyecto es obligatoria.");
            projectDescriptionTextArea.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateMethodology() {
        if (methodologyTextField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo requerido", "La metodología es obligatoria.");
            methodologyTextField.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateObjectives() {
        if (objectivesTextArea.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo requerido", "Los objetivos del proyecto son obligatorios.");
            objectivesTextArea.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateRegistrationDate() {
        if (registrationDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Campo requerido", "Seleccione una fecha de registro.");
            return false;
        }
        return true;
    }

    private boolean validateLinkedOrganization() {
        if (linkedOrganizationComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Campo requerido",
                "Debe seleccionar una organización vinculada.\n" +
                "Si no hay organizaciones disponibles, registre una primero.");
            linkedOrganizationComboBox.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateTechnicalResponsible() {
        if (technicalResponsibleComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Campo requerido",
                "Debe seleccionar un responsable técnico.\n" +
                "Si la organización no tiene responsables, registre uno primero.");
            technicalResponsibleComboBox.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateCapacity() {
        String text = capacityTextField.getText().trim();
        if (text.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo requerido", "El cupo de estudiantes es obligatorio.");
            capacityTextField.requestFocus();
            return false;
        }
        try {
            int capacity = Integer.parseInt(text);
            if (capacity <= 0) {
                showAlert(Alert.AlertType.WARNING, "Valor inválido", "El cupo debe ser mayor a 0.");
                capacityTextField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "El cupo debe ser un número entero.");
            capacityTextField.requestFocus();
            return false;
        }
        return true;
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private void initializeDefaultValues() {
        registrationDatePicker.setValue(LocalDate.now());
        activeStatusCheckBox.setSelected(true);
        activityCounterLabel.setText("Actividades registradas: 0 / --");
    }

    private void clearFields() {
        projectNameTextField.clear();
        projectDescriptionTextArea.clear();
        methodologyTextField.clear();
        objectivesTextArea.clear();
        capacityTextField.clear();
        linkedOrganizationComboBox.getSelectionModel().clearSelection();
        technicalResponsibleComboBox.getItems().clear();
        technicalResponsibleComboBox.setDisable(true);
        initializeDefaultValues();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}