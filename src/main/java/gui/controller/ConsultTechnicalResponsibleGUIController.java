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

import logic.businessObject.TechnicalResponsible;
import logic.businessObject.LinkedOrganization;
import logic.dao.TechnicalResponsibleDAO;
import logic.exceptions.DAOException;
import logic.exceptions.DatabaseConnectionException;

/**
 * @author gamal
 */


public class ConsultTechnicalResponsibleGUIController implements Initializable {

    @FXML private TableView<TechnicalResponsible> tableViewTechnicalResponsibles;
    @FXML private TableColumn<TechnicalResponsible, String> columnName;
    @FXML private TableColumn<TechnicalResponsible, String> columnPaternalSurname;
    @FXML private TableColumn<TechnicalResponsible, LinkedOrganization> columnLinkedOrganization;
    @FXML private TableColumn<TechnicalResponsible, String> columnEmail;
    @FXML private TableColumn<TechnicalResponsible, String> columnPhoneNumber;
    @FXML private TableColumn<TechnicalResponsible, Void> columnActions;

    private ObservableList<TechnicalResponsible> responsiblesList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadResponsiblesData();
    }

    private void setupTableColumns() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnPaternalSurname.setCellValueFactory(new PropertyValueFactory<>("paternalSurname"));
        columnLinkedOrganization.setCellValueFactory(new PropertyValueFactory<>("linkedOrganization"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("gmail"));
        columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    private void loadResponsiblesData() {
        try {
            TechnicalResponsibleDAO dao = new TechnicalResponsibleDAO();
            List<TechnicalResponsible> responsibles = dao.getAllTechnicalResponsibles();
            
            responsiblesList = FXCollections.observableArrayList(responsibles);
            tableViewTechnicalResponsibles.setItems(responsiblesList);
            
        } catch (DatabaseConnectionException exception) {
            showAlert("Error de Conexión", "No se pudo establecer conexión con la base de datos para cargar el directorio.");
        } catch (DAOException exception) {
            showAlert("Error del Sistema", "Ocurrió un error inesperado al recuperar los datos de los responsables técnicos.");
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