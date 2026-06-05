package gui.usersviews;

import gui.controller.ConsultMyProjectGUIController;
import gui.controller.RequestProjectGUIController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import logic.businessObject.User;
import logic.dao.UserDAO;
import logic.exceptions.DAOException;
import logic.util.SessionContext; 

public class StudentMenuGUIController implements  javafx.fxml.Initializable {

    @FXML
    private StackPane contentStackPane;

    private String practitionerEnrollment;
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setUsuarioLogueado(User user) {
        try {
            this.practitionerEnrollment = userDAO.getPractitionerEnrollmentByUserId(user.getIdUser());
            openRequestProjectView();
        } catch (DAOException e) {
            showAlert("Error de sesión", "No se pudo obtener la información del estudiante: " + e.getMessage());
        }
    }

    @FXML
    private void openMyProjectView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ConsultMyProjectGUI.fxml"));
            Node view = loader.load();

            ConsultMyProjectGUIController controller = loader.getController();

            contentStackPane.getChildren().clear();
            contentStackPane.getChildren().add(view);

        } catch (IOException e) {
            handleException(e, "/gui/fxml/ConsultMyProjectGUI.fxml");
        } catch (NullPointerException e) {
            showAlert("Vista en construcción", "El archivo MyProjectGUI.fxml aún no ha sido creado.");
        }
    }

    @FXML
    private void openRequestProjectView(ActionEvent event) {
        openRequestProjectView();
    }

    @FXML
    private void openGenerateInitialFormatsView(ActionEvent event) {
        loadView("/gui/fxml/GenerateInitialFormats.fxml");
    }

    @FXML
    private void openUploadInitialFormatsView(ActionEvent event) {
        loadView("/gui/fxml/UploadInitialFormats.fxml");
    }

    @FXML
    private void openRegisterActivityView(ActionEvent event) {
        loadView("/gui/fxml/RegisterActivityGUI.fxml");
    }

    @FXML
    private void openGenerateReportView(ActionEvent event) {
        loadView("/gui/fxml/GenerateReportGUI.fxml");
    }

    @FXML
    private void openUploadSignedReportView(ActionEvent event) {
        loadView("/gui/fxml/UploadSignedReportView.fxml");
    }

    @FXML
    private void openGenerateSelfEvaluationView(ActionEvent event) {
        loadView("/gui/fxml/GenerateSelfEvaluation.fxml");
    }

    @FXML
    private void openUploadSelfEvaluationView(ActionEvent event) {
        loadView("/gui/fxml/UploadSelfEvaluation.fxml");
    }

    @FXML
    private void openUploadOrganizationEvaluationView(ActionEvent event) {
        loadView("/gui/fxml/UploadOrganizationEvaluation.fxml");
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {

        SessionContext.getInstance().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LogingGUI.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Sistema de Prácticas — Iniciar Sesión");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            handleException(e, "/gui/fxml/LoginGUI.fxml");
        }
    }

    private void openRequestProjectView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/RequestProjectGUI.fxml"));
            Node view = loader.load();

            RequestProjectGUIController controller = loader.getController();
            controller.setPractitionerEnrollment(practitionerEnrollment);

            contentStackPane.getChildren().clear();
            contentStackPane.getChildren().add(view);

        } catch (IOException e) {
            handleException(e, "/gui/fxml/RequestProjectGUI.fxml");
        } catch (NullPointerException e) {
            showAlert("Vista en construcción", "El archivo RequestProjectGUI.fxml aún no ha sido creado.");
        }
    }

    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentStackPane.getChildren().clear();
            contentStackPane.getChildren().add(view);
        } catch (IOException e) {
            handleException(e, fxmlPath);
        } catch (NullPointerException e) {
            showAlert("Vista en construcción", "El archivo FXML " + fxmlPath + " aún no ha sido creado.");
        }
    }

    private void handleException(IOException e, String path) {
        showAlert("Error de Interfaz", "No se pudo cargar el módulo: " + path);
        System.err.println("Error details: " + e.getMessage());
        e.printStackTrace();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}