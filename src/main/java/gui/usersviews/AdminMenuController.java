package gui.usersviews;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 *
 * Author: gamal
 */
public class AdminMenuController implements Initializable {

    
    @FXML private BorderPane BorderPanePrincipal;
    
    
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
  
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}