package gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import logic.businessObject.Teacher;
import logic.dao.TeacherDAO;
import logic.exceptions.DAOException;

/**
 * 
 * Autor: gamal
 */
public class TeacherGUIController implements Initializable {

   
    @FXML private TextField txtnNoPersonal;
    @FXML private TextField txtcontrasenia;
    @FXML private TextField txtNombre;
    @FXML private TextField txtpaterno;
    @FXML private TextField txtMaterno;
    @FXML private RadioButton rbCoordinador;
    @FXML private RadioButton rbProfesor;

 
    private ToggleGroup roleGroup;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
        roleGroup = new ToggleGroup();
        rbCoordinador.setToggleGroup(roleGroup);
        rbProfesor.setToggleGroup(roleGroup);
    }    

    @FXML
    private void btnRegistrarClicked(ActionEvent event) {
      
        if (isValidInput()) {
            try {
              
                int personalNumber = Integer.parseInt(txtnNoPersonal.getText().trim());
                
                
                Teacher newTeacher = new Teacher();
                newTeacher.setNoPersonal(personalNumber);
                newTeacher.setName(txtNombre.getText().trim());
                newTeacher.setPaternalSurname(txtpaterno.getText().trim());
                newTeacher.setMaternalSurname(txtMaterno.getText().trim());
                
             
                String role = rbCoordinador.isSelected() ? "Coordinador" : "Profesor";
                newTeacher.setRole(role);
                
              
                newTeacher.setActivityStatus(true); 

              
                TeacherDAO dao = new TeacherDAO();
                boolean isRegistered = dao.registerTeacher(newTeacher);

                if (isRegistered) {
                
                    showAlert("Confirmación", "Registro exitoso");
                    clearFields();
                }

            } catch (NumberFormatException e) {
                showAlert("Error de formato", "El número de personal debe contener solo números.");
            } catch (DAOException e) {
                handleException(e);
            }
        }
    }

    @FXML
    private void btnCancelarClicked(ActionEvent event) {
     
        showAlert("Aviso", "Registro cancelado");
        
   
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

   
    private boolean isValidInput() {
        if (txtnNoPersonal.getText().trim().isEmpty() || 
            txtcontrasenia.getText().trim().isEmpty() ||
            txtNombre.getText().trim().isEmpty() || 
            txtpaterno.getText().trim().isEmpty() ||
            txtMaterno.getText().trim().isEmpty()) {
            
            showAlert("Datos incompletos", "Por favor llena todos los campos de texto.");
            return false;
        }
        
        if (!rbCoordinador.isSelected() && !rbProfesor.isSelected()) {
            showAlert("Falta el Rol", "Por favor selecciona si es Coordinador o Profesor.");
            return false;
        }
        
        return true;
    }

   
    private void handleException(DAOException e) {
       
        if (e.getMessage().contains("Duplicate") || (e.getCause() != null && e.getCause().getMessage().contains("Duplicate"))) {
            showAlert("Aviso", "El Profesor ya se encuentra registrado, revisa la información");
        } else {
    
            showAlert("Error", "Error en la conexión a la base de datos");
          
        }
    }

    
    private void clearFields() {
        txtnNoPersonal.clear();
        txtcontrasenia.clear();
        txtNombre.clear();
        txtpaterno.clear();
        txtMaterno.clear();
        
        if (roleGroup.getSelectedToggle() != null) {
            roleGroup.getSelectedToggle().setSelected(false);
        }
    }

    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); 
    }
}