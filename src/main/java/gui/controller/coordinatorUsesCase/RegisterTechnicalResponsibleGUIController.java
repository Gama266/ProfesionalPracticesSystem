package gui.controller.coordinatorUsesCase;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import logic.businessObject.TechnicalResponsible;
import logic.businessObject.LinkedOrganization; 
import logic.dao.LinkedOrganizationDAO;
import logic.dao.TechnicalResponsibleDAO;
import logic.exceptions.DAOException;
import logic.exceptions.DatabaseConnectionException;
import logic.exceptions.DuplicateRecordException;

public class RegisterTechnicalResponsibleGUIController implements Initializable {

    @FXML private TextField textFieldName;
    @FXML private TextField textFieldPaternalSurname;
    @FXML private TextField textFieldMaternalSurname;
    @FXML private TextField textFieldPhoneNumber;
    @FXML private TextField textFieldEmail;
    @FXML private ComboBox<LinkedOrganization> comboBoxLinkedOrganization;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadOrganizationsList();
    }    

    private void loadOrganizationsList() {
        try {
            LinkedOrganizationDAO orgDao = new LinkedOrganizationDAO();
            List<LinkedOrganization> organizations = orgDao.getAllOrganizations();
            ObservableList<LinkedOrganization> observableList = FXCollections.observableArrayList(organizations);
            comboBoxLinkedOrganization.setItems(observableList);
            
        } catch (DatabaseConnectionException exception) {
            showAlert("Error de Conexión", "No se pudo conectar a la base de datos para cargar las organizaciones.");
        } catch (DAOException exception) {
            showAlert("Error", "Ocurrió un error al cargar las organizaciones vinculadas.");
        }
    }

    @FXML
    private void actionButtonSave(ActionEvent event) {
        if (isValidInput()) {
            try {
                TechnicalResponsible newResponsible = new TechnicalResponsible();
                newResponsible.setName(textFieldName.getText().trim());
                newResponsible.setPaternalSurname(textFieldPaternalSurname.getText().trim());
                newResponsible.setMaternalSurname(textFieldMaternalSurname.getText().trim());
                newResponsible.setPhoneNumber(textFieldPhoneNumber.getText().trim());
                newResponsible.setGmail(textFieldEmail.getText().trim());
                
                newResponsible.setLinkedOrganization(comboBoxLinkedOrganization.getValue());

                TechnicalResponsibleDAO dao = new TechnicalResponsibleDAO();
                boolean isRegistered = dao.registerTechnicalResponsible(newResponsible);

                if (isRegistered) {
                    showAlert("Éxito", "Responsable Técnico registrado correctamente.");
                    clearFields();
                }

            } catch (DatabaseConnectionException e) {
                showAlert("Error de Conexión", "No se pudo conectar a la base de datos. Verifica tu red.");
            } catch (DuplicateRecordException e) {
                showAlert("Registro Duplicado", "Este correo electrónico ya se encuentra registrado.");
            } catch (DAOException e) {
                showAlert("Error de Base de Datos", "Ocurrió un error al intentar guardar el registro.");
            }
        }
    }
    private boolean isValidInput() {
        boolean isValid = true; 
        
        if (textFieldName.getText().trim().isEmpty()){
            showAlert("Campos incompletos", "El nombre es obligatorio.");
            isValid = false;
        } else if(textFieldPaternalSurname.getText().trim().isEmpty()){
            showAlert("Campos incompletos", "El apellido paterno es obligatorio."); 
            isValid = false;
        } else if(textFieldEmail.getText().trim().isEmpty()){
            showAlert("Campos incompletos", "El correo es obligatorio.");
            isValid = false; 
        } else if(comboBoxLinkedOrganization.getValue() == null) {
            showAlert("Campos incompletos", "Debe seleccionar una Organización Vinculada.");
            isValid = false;
        }
        
        return isValid; 
    }

    @FXML
    private void actionButtonCancel(ActionEvent event) {
        Stage stage = (Stage) textFieldName.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        textFieldName.clear();
        textFieldPaternalSurname.clear();
        textFieldMaternalSurname.clear();
        textFieldPhoneNumber.clear();
        textFieldEmail.clear();
        comboBoxLinkedOrganization.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}