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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import logic.util.SessionContext; 

/**
 *
 * Author: gamal
 */
public class AdminMenuController implements Initializable {

    @FXML private BorderPane bpPrincipal; 
    @FXML private StackPane stackpaneContenido;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadView("/gui/fxml/TeacherGUI.fxml");
    }

    @FXML
    private void Registrationteacher(ActionEvent event) {
        loadView("/gui/fxml/TeacherGUI.fxml");
    }

    @FXML
    private void Managementteacher(ActionEvent event) {
        loadView("/gui/fxml/DeactivateTeacherView.fxml");
    }

    @FXML
    private void handleCloseSesion(ActionEvent event) {
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

    private void loadView(String fxmlPath) {
        try {
            Node vista = FXMLLoader.load(getClass().getResource(fxmlPath));
            stackpaneContenido.getChildren().clear();
            stackpaneContenido.getChildren().add(vista);
        } catch (IOException e) {
            handleException(e, fxmlPath);
        }
    }

    private void handleException(IOException e, String path) {
        showAlert("Error de Interfaz", "No se pudo cargar el módulo: " + path);
        System.err.println("[AdminMenu] Error cargando vista: " + path + " — " + e.getMessage());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}