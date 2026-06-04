/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */


package gui.controller.coordinatorUsesCase;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import logic.businessObject.LinkedOrganization;
import logic.businessObject.LocationOrganization;
import logic.dao.LinkedOrganizationDAO;
import logic.dao.LocationOrganizationDAO;
import logic.exceptions.DAOException;
import logic.exceptions.DuplicateRecordException;
import logic.businessObject.Country;
import logic.util.LocationReader;

/**
 * 
 * @author gamal
 */
public class RegisterLinkedOrganizationGUIController implements Initializable {

    @FXML private TextField textFieldOrganizationName;
    @FXML private TextField textFieldPhysicalAddress;
    @FXML private ComboBox<String> comboBoxCountry;
    @FXML private ComboBox<String> comboBoxState;
    @FXML private TextField textFieldPhoneNumber;
    @FXML private TextField textFieldEmailAddress;
    @FXML private Button buttonCancel;
    @FXML private Button buttonSaveOrganization;

    private List<Country> globalCountriesList;

    @Override
    public void initialize(URL locationUrl, ResourceBundle resourceBundle) {
        globalCountriesList = LocationReader.loadLocations();
        
        for (Country country : globalCountriesList) {
            comboBoxCountry.getItems().add(country.getName());
        }

        comboBoxCountry.setOnAction(event -> {
            comboBoxState.getItems().clear(); 
            String selectedCountryName = comboBoxCountry.getValue();

            if (selectedCountryName != null) {
                for (Country country : globalCountriesList) {
                    if (country.getName().equals(selectedCountryName)) {
                        for (String stateName : country.getStates()) {
                            comboBoxState.getItems().add(stateName);
                        }
                        break; 
                    }
                }
            }
        });
    }

    @FXML
    private void buttonSaveOrganizationOnAction(ActionEvent actionEvent) {
        if (validateFormFieldsAreValid()) {
            try {
                LocationOrganizationDAO locationOrganizationDataAcessObject = new LocationOrganizationDAO();
                LinkedOrganizationDAO linkedOrganizationDataAcessObject = new LinkedOrganizationDAO();

                LocationOrganization newLocationOrganization = new LocationOrganization();
                newLocationOrganization.setCountry(comboBoxCountry.getValue().trim());
                newLocationOrganization.setState(comboBoxState.getValue().trim());
                
                if (locationOrganizationDataAcessObject.registerLocation(newLocationOrganization)) {
                    
                    LinkedOrganization newLinkedOrganization = new LinkedOrganization();
                    newLinkedOrganization.setName(textFieldOrganizationName.getText().trim());
                    newLinkedOrganization.setDireccion(textFieldPhysicalAddress.getText().trim());
                    newLinkedOrganization.setPhoneNumber(textFieldPhoneNumber.getText().trim());
                    newLinkedOrganization.setGmail(textFieldEmailAddress.getText().trim()); 
                    newLinkedOrganization.setLocationOrganization(newLocationOrganization); 

                    if (linkedOrganizationDataAcessObject.registerLinkedOrganization(newLinkedOrganization)) {
                        showSystemAlert(Alert.AlertType.INFORMATION, "Éxito", "organización vinculada registrada correctamente en el sistema.");
                        closeCurrentWindow(actionEvent);
                    } else {
                        showSystemAlert(Alert.AlertType.ERROR, "Error", "No se pudo registrar la organización.");
                    }
                }

            } catch (DuplicateRecordException duplicateException) {
                showSystemAlert(Alert.AlertType.WARNING, "Registro duplicado", duplicateException.getMessage());
            } catch (DAOException dataAcessException) {
                showSystemAlert(Alert.AlertType.ERROR, "Error del sistema", "Ocurrió un error inesperado al intentar guardar la organización.");
            }
        }
    }

    @FXML
    private void buttonCancelOnAction(ActionEvent actionEvent) {
        closeCurrentWindow(actionEvent);
    }

    private boolean validateFormFieldsAreValid() {
        if (textFieldOrganizationName.getText().trim().isEmpty() || 
            textFieldPhysicalAddress.getText().trim().isEmpty() || 
            comboBoxCountry.getValue() == null || 
            comboBoxState.getValue() == null || 
            textFieldPhoneNumber.getText().trim().isEmpty() ||
            textFieldEmailAddress.getText().trim().isEmpty()) {
            
            showSystemAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, llena todos los campos del formulario.");
            return false;
        }

        if (!validateStandardTextSyntax(textFieldOrganizationName.getText().trim()) || 
            !validateStandardTextSyntax(textFieldPhysicalAddress.getText().trim())) {
            showSystemAlert(Alert.AlertType.WARNING, "Caracteres Inválidos", "El nombre de la organización y la dirección solo pueden contener letras, números y signos de puntuación básicos.");
            return false;
        }

        if (!validateEmailSyntax(textFieldEmailAddress.getText().trim())) {
            showSystemAlert(Alert.AlertType.WARNING, "Correo Inválido", "El formato del correo electrónico es incorrecto. Ejemplo válido: contacto@empresa.com");
            return false;
        }

        if (!validatePhoneNumberSyntax(textFieldPhoneNumber.getText().trim(), comboBoxCountry.getValue())) {
            showSystemAlert(Alert.AlertType.WARNING, "Teléfono Inválido", "El número debe iniciar con el código de país correcto (Ej. +52 para México) seguido de 7 a 14 dígitos sin letras.");
            return false;
        }

        return true;
    }

    private boolean validateStandardTextSyntax(String text) {
        String standardTextRegexPattern = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑüÜ\\s.,#-]+$";
        return text.matches(standardTextRegexPattern);
    }

    private boolean validateEmailSyntax(String emailAddress) {
        String emailRegexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return emailAddress.matches(emailRegexPattern);
    }

    private boolean validatePhoneNumberSyntax(String phoneNumber, String selectedCountryName) {
        String countryPhoneCode = "";
        
        for (Country country : globalCountriesList) {
            if (country.getName().equals(selectedCountryName)) {
                countryPhoneCode = country.getPhone_code();
                break;
            }
        }
        
        if (countryPhoneCode != null && !countryPhoneCode.isEmpty()) {
            countryPhoneCode = countryPhoneCode.replace("+", "");
            String phoneRegexPattern = "^\\+" + countryPhoneCode + "\\s?[0-9]{7,14}$";
            return phoneNumber.matches(phoneRegexPattern);
        }
        
        return phoneNumber.matches("^\\+[0-9]{1,4}\\s?[0-9]{7,14}$");
    }

    private void showSystemAlert(Alert.AlertType alertType, String windowTitle, String alertMessage) {
        Alert systemAlert = new Alert(alertType);
        systemAlert.setTitle(windowTitle);
        systemAlert.setHeaderText(null);
        systemAlert.setContentText(alertMessage);
        systemAlert.showAndWait();
    }

    private void closeCurrentWindow(ActionEvent actionEvent) {
        Node eventSourceNode = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) eventSourceNode.getScene().getWindow();
        currentStage.close();
    }
}