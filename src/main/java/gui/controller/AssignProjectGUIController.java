/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gui.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.businessObject.Project;
import logic.businessObject.Student;
import logic.dao.ProjectDAO;
import logic.dao.RequestDAO;
import logic.dao.StudentDAO;
import logic.exceptions.DAOException;

public class AssignProjectGUIController implements Initializable {

    @FXML private TableView<Student>            tableStudents;
    @FXML private TableColumn<Student, String>  columnStudentName;
    @FXML private TableColumn<Student, String>  columnStudentMatricula;

    @FXML private TableView<Project>            tableRequestedProjects;
    @FXML private TableColumn<Project, String>  columnProjectName;
    @FXML private TableColumn<Project, String>  columnOrganization;
    @FXML private TableColumn<Project, String>  columnTechnicalResponsible;
    @FXML private TableColumn<Project, Integer> columnAvailability;

    @FXML private Label  labelSelectedStudent;
    @FXML private Button btnAssignProject;
    @FXML private Button btnNotAssignProject;

    private final StudentDAO studentDAO = new StudentDAO();
    private final RequestDAO requestDAO = new RequestDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    private Student selectedStudent;
    private Project selectedProject;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        setupSelectionListeners();
        loadStudentsWithPendingRequests();
        updateButtonStates();
    }

    private void configureTableColumns() {
        columnStudentName.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getName() + " " +
                cellData.getValue().getPaternalSurname()
            )
        );
        columnStudentMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        columnProjectName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnOrganization.setCellValueFactory(cellData -> {
            Project p = cellData.getValue();
            String org = p.getLinkedOrganization() != null ? p.getLinkedOrganization().getName() : "N/A";
            return new SimpleStringProperty(org);
        });
        columnTechnicalResponsible.setCellValueFactory(cellData -> {
            Project p = cellData.getValue();
            String tr = p.getTechnicalResponsible() != null ?
                p.getTechnicalResponsible().getName() + " " +
                p.getTechnicalResponsible().getPaternalSurname() : "N/A";
            return new SimpleStringProperty(tr);
        });
        columnAvailability.setCellValueFactory(new PropertyValueFactory<>("availableSpaces"));
    }

    private void setupSelectionListeners() {
        tableStudents.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedStudent = newVal;
            selectedProject = null;
            tableRequestedProjects.getSelectionModel().clearSelection();
            if (newVal != null) {
                labelSelectedStudent.setText(
                    newVal.getName() + " " + newVal.getPaternalSurname() +
                    "  |  " + newVal.getMatricula()
                );
                loadRequestedProjects(newVal.getMatricula());
            } else {
                labelSelectedStudent.setText("Ningún estudiante seleccionado");
                tableRequestedProjects.setItems(FXCollections.observableArrayList());
            }
            updateButtonStates();
        });

        tableRequestedProjects.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedProject = newVal;
            updateButtonStates();
        });
    }

    private void loadStudentsWithPendingRequests() {
        try {
            List<Student> students = studentDAO.getStudentsWithPendingRequests();
            tableStudents.setItems(FXCollections.observableArrayList(students));
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error de conexión con la base de datos", e.getMessage());
        }
    }

    private void loadRequestedProjects(String matricula) {
        try {
            List<Project> projects = requestDAO.getProjectsRequestedByStudent(matricula);
            tableRequestedProjects.setItems(FXCollections.observableArrayList(projects));
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error de conexión con la base de datos", e.getMessage());
        }
    }

    private void updateButtonStates() {
        boolean studentSelected  = selectedStudent != null;
        boolean projectAvailable = selectedProject != null && selectedProject.getAvailableSpaces() > 0;

        btnAssignProject.setDisable(!studentSelected || !projectAvailable);
        btnNotAssignProject.setDisable(!studentSelected);
    }

    @FXML
    private void handleAssignProject() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar asignación");
        confirmation.setHeaderText("Confirmación requerida");
        confirmation.setContentText("¿Deseas asignarle este proyecto?");
        confirmation.getButtonTypes().setAll(
            new ButtonType("Asignar", ButtonBar.ButtonData.OK_DONE),
            new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE)
        );

        Optional<ButtonType> choice = confirmation.showAndWait();
        if (choice.isPresent() && choice.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            try {
                projectDAO.assignProjectToStudent(selectedStudent.getMatricula(), selectedProject.getId());
                requestDAO.approveRequest(selectedStudent.getMatricula(), selectedProject.getId());
                requestDAO.rejectRemainingRequests(selectedStudent.getMatricula(), selectedProject.getId());

                showAlert(Alert.AlertType.INFORMATION, "Asignación exitosa",
                    "Asignación de proyecto exitosa.");
                resetView();

            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Error de conexión con la base de datos", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Cancelado", "Se canceló la asignación.");
        }
    }

    @FXML
    private void handleNotAssignProject() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar");
        confirmation.setHeaderText("Confirmación requerida");
        confirmation.setContentText("¿Deseas no asignarle ningún proyecto a este estudiante?");

        Optional<ButtonType> choice = confirmation.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            try {
                requestDAO.rejectAllRequestsForStudent(selectedStudent.getMatricula());
                showAlert(Alert.AlertType.INFORMATION, "Sin asignación",
                    "No se le asignó algún proyecto a estudiante.");
                resetView();

            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Error de conexión con la base de datos", e.getMessage());
            }
        }
    }

    private void resetView() {
        loadStudentsWithPendingRequests();
        tableRequestedProjects.setItems(FXCollections.observableArrayList());
        labelSelectedStudent.setText("Ningún estudiante seleccionado");
        selectedStudent = null;
        selectedProject = null;
        updateButtonStates();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}