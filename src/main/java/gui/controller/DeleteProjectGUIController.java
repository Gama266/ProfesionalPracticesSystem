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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import logic.businessObject.Project;
import logic.dao.ProjectDAO;
import logic.exceptions.DAOException;
/**
 * 
 * @author gamal
 */
public class DeleteProjectGUIController implements Initializable {

    @FXML private TableView<Project> tableProjects;
    @FXML private TableColumn<Project, Integer> columnId;
    @FXML private TableColumn<Project, String> columnName;
    @FXML private TableColumn<Project, String> columnOrg; 
    @FXML private TableColumn<Project, Void> columnActions;

    private ObservableList<Project> projectObservableList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableViewColumns();
        loadAllProjectsIntoTable();
    }

    private void configureTableViewColumns() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        columnOrg.setCellValueFactory(cellDataParameter -> {
            Project currentProject = cellDataParameter.getValue();
            boolean hasOrganizationLinked = currentProject.getLinkedOrganization() != null && currentProject.getLinkedOrganization().getName() != null;
            
            if (hasOrganizationLinked) {
                return new SimpleStringProperty(currentProject.getLinkedOrganization().getName());
            } else {
                return new SimpleStringProperty("Sin Organización Asignada");
            }
        });

        initializeDynamicActionColumnCellFactory();
    }

    private void loadAllProjectsIntoTable() {
        try {
            ProjectDAO projectDataAccessObject = new ProjectDAO();
            List<Project> activeAndInactiveProjectsList = projectDataAccessObject.retrieveAllProjectsIncludingInactive();
            
            if (activeAndInactiveProjectsList.isEmpty()) {
                displayAlertMessage(Alert.AlertType.INFORMATION, "Aviso", "No hay proyectos registrados en el sistema.");
                tableProjects.setItems(FXCollections.observableArrayList());
                return;
            }
            
            projectObservableList = FXCollections.observableArrayList(activeAndInactiveProjectsList);
            tableProjects.setItems(projectObservableList);

        } catch (DAOException daoException) {
            displayAlertMessage(Alert.AlertType.ERROR, "Error de Base de Datos", "Error de conexión con la base de datos al recuperar proyectos.");
        }
    }

    private void initializeDynamicActionColumnCellFactory() {
        Callback<TableColumn<Project, Void>, TableCell<Project, Void>> actionCellFactory = new Callback<>() {
            @Override
            public TableCell<Project, Void> call(final TableColumn<Project, Void> tableColumnParameter) {
                return new TableCell<>() {
                    private final Button toggleStatusButton = new Button();

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Project currentProjectRow = getTableView().getItems().get(getIndex());
                            
                            if (currentProjectRow.getActivityStatus()) {
                                // Lógica si el proyecto está ACTIVO (Se puede ocultar)
                                toggleStatusButton.setText("Ocultar");
                                toggleStatusButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand;"); // Rojo
                                toggleStatusButton.setOnAction(actionEvent -> processProjectHidingConfirmation(currentProjectRow));
                            } else {
                                // Lógica si el proyecto está INACTIVO (Se puede reactivar)
                                toggleStatusButton.setText("Activar");
                                toggleStatusButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-cursor: hand;"); // Verde
                                toggleStatusButton.setOnAction(actionEvent -> processProjectActivationConfirmation(currentProjectRow));
                            }
                            
                            setGraphic(toggleStatusButton);
                        }
                    }
                };
            }
        };
        columnActions.setCellFactory(actionCellFactory);
    }


    private void processProjectHidingConfirmation(Project selectedProject) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmar ocultamiento");
        confirmationAlert.setHeaderText("Proyecto seleccionado: " + selectedProject.getName());
        confirmationAlert.setContentText("¿Desea ocultar este proyecto para que ya no esté disponible para los alumnos?");

        ButtonType acceptButtonType = new ButtonType("Aceptar");
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(acceptButtonType, cancelButtonType);

        Optional<ButtonType> userConfirmationResult = confirmationAlert.showAndWait();

        if (userConfirmationResult.isPresent() && userConfirmationResult.get() == acceptButtonType) {
            executeLogicalProjectHiding(selectedProject);
        }
    }

    private void executeLogicalProjectHiding(Project projectToHide) {
        try {
            ProjectDAO projectDataAccessObject = new ProjectDAO();
            boolean isProjectSuccessfullyHidden = projectDataAccessObject.hideProject(projectToHide.getId()); 
            
            if (isProjectSuccessfullyHidden) {
                displayAlertMessage(Alert.AlertType.INFORMATION, "Éxito", "El proyecto ha sido ocultado exitosamente.");
                loadAllProjectsIntoTable(); 
            }
        } catch (DAOException daoException) {
            displayAlertMessage(Alert.AlertType.ERROR, "Error de Sistema", "Ocurrió un error al intentar ocultar el proyecto.");
        }
    }

    private void processProjectActivationConfirmation(Project selectedProject) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmar reactivación");
        confirmationAlert.setHeaderText("Proyecto seleccionado: " + selectedProject.getName());
        confirmationAlert.setContentText("¿Desea volver a hacer visible este proyecto para los alumnos?");

        ButtonType acceptButtonType = new ButtonType("Activar");
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(acceptButtonType, cancelButtonType);

        Optional<ButtonType> userConfirmationResult = confirmationAlert.showAndWait();

        if (userConfirmationResult.isPresent() && userConfirmationResult.get() == acceptButtonType) {
            executeLogicalProjectActivation(selectedProject);
        }
    }

    private void executeLogicalProjectActivation(Project projectToActivate) {
        try {
            ProjectDAO projectDataAccessObject = new ProjectDAO();
            boolean isProjectSuccessfullyReactivated = projectDataAccessObject.reactivateProject(projectToActivate.getId()); 
            
            if (isProjectSuccessfullyReactivated) {
                displayAlertMessage(Alert.AlertType.INFORMATION, "Éxito", "El proyecto está activo nuevamente.");
                loadAllProjectsIntoTable(); 
            }
        } catch (DAOException daoException) {
            displayAlertMessage(Alert.AlertType.ERROR, "Error de Sistema", "Ocurrió un error al intentar reactivar el proyecto.");
        }
    }

    private void displayAlertMessage(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}