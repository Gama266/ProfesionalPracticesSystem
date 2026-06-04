/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import logic.businessObject.LinkedOrganization;
import logic.dao.LinkedOrganizationDAO;
import logic.exceptions.DAOException;
import logic.exceptions.DatabaseConnectionException;

/**
 * 
 * @author gamal
 */
public class ConsultLinkedOrganizationGUIController implements Initializable {

    @FXML private TableView<LinkedOrganization> tableOrgs;
    @FXML private TableColumn<LinkedOrganization, String> columnOrgName;
    @FXML private TableColumn<LinkedOrganization, String> columnAddress;
    @FXML private TableColumn<LinkedOrganization, String> columnPhoneNumber;
    @FXML private TableColumn<LinkedOrganization, Void> columnActions;

    private ObservableList<LinkedOrganization> organizationsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadOrganizationsData();
    }

    private void setupTableColumns() {
  
        columnOrgName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        
   
    }

    private void loadOrganizationsData() {
        try {
            LinkedOrganizationDAO dao = new LinkedOrganizationDAO();
            
          
            List<LinkedOrganization> orgs = dao.getAllOrganizations();
            
            
            organizationsList = FXCollections.observableArrayList(orgs);
            tableOrgs.setItems(organizationsList);
            
        } catch (DatabaseConnectionException e) {
            showAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar al servidor de base de datos. Verifica tu conexión.");
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error del sistema", "Ocurrió un error inesperado al intentar cargar el directorio de organizaciones.");
      
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}