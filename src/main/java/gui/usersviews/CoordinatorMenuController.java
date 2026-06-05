/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gui.usersviews;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import logic.util.SessionContext; 

/**
 * * Autor: gamal
 */
public class CoordinatorMenuController implements Initializable {

    @FXML private StackPane spContenido;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
    }
    
    @FXML private void RegisterLinkedOrganization(ActionEvent event) {
        loadview("/gui/fxml/RegisterLinkedOrganizationGUI.fxml"); 
    }
    
    @FXML private void ConsultLinkedOrganization(ActionEvent event) {
        loadview("/gui/fxml/ConsultLinkedOrganizationGUI.fxml");
    }
    
    @FXML private void RegisterTechnicalResponsible(ActionEvent event) {
        loadview("/gui/fxml/RegisterTechnicalResponsibleGUI.fxml"); 
    }
    
    @FXML private void ConsultTechnicalResponsible(ActionEvent event) {
        loadview("/gui/fxml/ConsultTechnicalResponsibleGUI.fxml");
    }

    @FXML private void RegisterProject(ActionEvent event) {
        loadview("/gui/fxml/RegisterProjectView.fxml");
    }
    
    @FXML private void AssignProject(ActionEvent event) {
        loadview("/gui/fxml/AssignProjectGUI.fxml");
    }
    
    @FXML private void DeleteProject(ActionEvent event) {
        loadview("/gui/fxml/DeleteProjectGUI.fxml");
    }
    
    @FXML private void UpdateProject(ActionEvent event) {
        loadview("/gui/fxml/UpdateProjectView.fxml");
    }
    
    @FXML private void openRegisterActivityView(ActionEvent event) {
        loadview("/gui/fxml/RegisterActivityGUI.fxml");
    }

    @FXML private void RegisterEducationalExperience(ActionEvent event) {
        loadview("/gui/fxml/RegisterEducationalExperienceGUI.fxml");
    }
    
    @FXML private void ConsultEducationalExperience(ActionEvent event) {
        loadview("/gui/fxml/ConsultEducationalExperienceGUI.fxml");
    }

    @FXML private void RegisterStudent(ActionEvent event) {
        loadview("/gui/fxml/RegisterStudentView.fxml");
    }
    
    @FXML private void ConsultStudent(ActionEvent event) {
        loadview("/gui/fxml/ConsultStudentView.fxml");
    }
    
    @FXML private void DesactiveStudent(ActionEvent event) {
        loadview("/gui/fxml/DeactivateStudentView.fxml");
    }

    @FXML private void btnConsultarProfesor(ActionEvent event) {
        loadview("/gui/fxml/ConsultTeacherGUI.fxml");
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {

        SessionContext.getInstance().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LoginGUI.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Prácticas — Iniciar Sesión");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException exception) {
            handleException(exception, "/gui/fxml/LoginGUI.fxml");
        }
    }

    private void loadview(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            spContenido.getChildren().clear();
            spContenido.getChildren().add(view);

        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Excepción completa");

            String errorMessage = exception.toString();

            if (exception.getCause() != null) {
                errorMessage += "\n\nCAUSE:\n" + exception.getCause().toString();
            }

            alert.setContentText(errorMessage);
            alert.showAndWait();
        }
    }

    private void handleException(IOException exception, String path) {
        exception.printStackTrace();
        showAlert(
                "Error de Interfaz",
                "No se pudo cargar el módulo: " + path
                        + "\n\n"
                        + exception.getMessage()
        );
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}