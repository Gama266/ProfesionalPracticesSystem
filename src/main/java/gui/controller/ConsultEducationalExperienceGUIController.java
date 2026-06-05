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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import logic.businessObject.EducationalExperience;
import logic.dao.EducationalExperienceDAO;
import logic.exceptions.DAOException;

/**
 * 
 * Autor gamal
 */
public class ConsultEducationalExperienceGUIController implements Initializable {

    @FXML 
    private TableView<EducationalExperience> tableViewEducationalExperiences;
    
    @FXML 
    private TableColumn<EducationalExperience, Integer> tableColumnNrc;
    
    @FXML 
    private TableColumn<EducationalExperience, String> tableColumnEducationalExperienceName;
    
    @FXML 
    private TextField textFieldSearchNrc;

    private final EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
    private final ObservableList<EducationalExperience> educationalExperiencesObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        loadAllEducationalExperiences();
    }

  
    private void configureTableColumns() {
        tableColumnNrc.setCellValueFactory(new PropertyValueFactory<>("nrc"));
        tableColumnEducationalExperienceName.setCellValueFactory(new PropertyValueFactory<>("section"));
    }


    private void loadAllEducationalExperiences() {
        try {
            List<EducationalExperience> structuralList = educationalExperienceDAO.getAllEducationalExperiences();
            educationalExperiencesObservableList.setAll(structuralList);
            tableViewEducationalExperiences.setItems(educationalExperiencesObservableList);
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo recuperar el catálogo de experiencias educativas.");
        }
    }


    @FXML
    private void handleSearch(ActionEvent event) {
        String searchInput = textFieldSearchNrc.getText().trim();

        if (searchInput.isEmpty()) {
            loadAllEducationalExperiences();
            return;
        }

        try {
            int nrc = Integer.parseInt(searchInput);
            EducationalExperience experienceResult = educationalExperienceDAO.getNrc(nrc);

            educationalExperiencesObservableList.clear();
            if (experienceResult != null) {
                educationalExperiencesObservableList.add(experienceResult);
                tableViewEducationalExperiences.setItems(educationalExperiencesObservableList);
            } else {
                tableViewEducationalExperiences.setItems(FXCollections.emptyObservableList());
                showAlert(Alert.AlertType.INFORMATION, "Sin resultados", "No se encontró ninguna experiencia educativa con el NRC: " + nrc);
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Formato incorrecto", "El NRC debe ser un número entero válido (Ej. 18432).");
        } catch (DAOException e) {
          
            showAlert(Alert.AlertType.ERROR, "Error de Conexión", "Ocurrió un problema al conectar con la base de datos.");
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