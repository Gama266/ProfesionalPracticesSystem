package gui.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import logic.businessObject.Teacher;
import logic.dao.TeacherDAO;
import logic.exceptions.DAOException;
 
/**
 * @author akYer
 */
public class DeactivateTeacherController implements Initializable {
 
    @FXML private TextField searchField;
    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, String> personalNumberColumn;
    @FXML private TableColumn<Teacher, String> fullNameColumn;
    @FXML private TableColumn<Teacher, String> roleColumn;
    @FXML private TableColumn<Teacher, String> statusColumn;
    @FXML private TableColumn<Teacher, Void> actionsColumn;

    private final ObservableList<Teacher> allTeachers = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureColumns();
        loadTeachers();
        configureSearch();
    }

    private void configureColumns() {
        configurePersonalNumberColumn();
        configureFullNameColumn();
        configureRoleColumn();
        configureStatusColumn();
        configureActionsColumn();
    }

    private void configurePersonalNumberColumn() {
        personalNumberColumn.setCellValueFactory(data ->
            new ReadOnlyStringWrapper(String.valueOf(data.getValue().getNoPersonal())));
    }

    private void configureFullNameColumn() {
        fullNameColumn.setCellValueFactory(data -> {
            Teacher teacher = data.getValue();
            String fullName = teacher.getName() + " " + 
                             teacher.getPaternalSurname() + " " + 
                             teacher.getMaternalSurname();
            return new ReadOnlyStringWrapper(fullName);
        });
    }

    private void configureRoleColumn() {
        roleColumn.setCellValueFactory(data ->
            new ReadOnlyStringWrapper(data.getValue().getRole()));
    }

    private void configureStatusColumn() {
        statusColumn.setCellValueFactory(data ->
            new ReadOnlyStringWrapper(data.getValue().getActivityStatus() ? "Activo" : "Inactivo"));

        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    boolean isActive = "Activo".equals(item);
                    String style = isActive 
                        ? "-fx-text-fill: #16a34a; -fx-font-weight: bold;"
                        : "-fx-text-fill: #dc2626; -fx-font-weight: bold;";
                    setStyle(style);
                }
            }
        });
    }

    private void configureActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deactivateButton = new Button("Inactivar");

            {
                deactivateButton.setStyle(
                    "-fx-background-color: #dc2626; -fx-text-fill: white; " +
                    "-fx-cursor: hand; -fx-font-size: 11px;");
                deactivateButton.setOnAction(event -> {
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    confirmDeactivation(teacher);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    deactivateButton.setDisable(!teacher.getActivityStatus());
                    setGraphic(deactivateButton);
                }
            }
        });
    }


    private void loadTeachers() {
        try {
            TeacherDAO teacherDao = new TeacherDAO();
            List<Teacher> teachers = teacherDao.getAllTeachers();
            allTeachers.setAll(teachers);
            teacherTable.setItems(allTeachers);
        } catch (DAOException exception) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "No se pudo cargar la lista de profesores.\n" + exception.getMessage());
        }
    }


    private void configureSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> 
            filterTable(newValue));
    }

    private void filterTable(String filterText) {
        if (filterText == null || filterText.isBlank()) {
            teacherTable.setItems(allTeachers);
            return;
        }

        String lowerCaseFilter = filterText.toLowerCase();
        List<Teacher> filteredTeachers = allTeachers.stream()
            .filter(teacher ->
                teacher.getName().toLowerCase().contains(lowerCaseFilter) ||
                teacher.getPaternalSurname().toLowerCase().contains(lowerCaseFilter) ||
                teacher.getMaternalSurname().toLowerCase().contains(lowerCaseFilter) ||
                String.valueOf(teacher.getNoPersonal()).contains(lowerCaseFilter))
            .collect(Collectors.toList());

        teacherTable.setItems(FXCollections.observableArrayList(filteredTeachers));
    }


    private void confirmDeactivation(Teacher teacher) {
        String teacherName = teacher.getName() + " " + teacher.getPaternalSurname();

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmar inactivación");
        confirmationAlert.setHeaderText("¿Inactivar al profesor?");
        confirmationAlert.setContentText("Esta acción marcará como inactivo a:\n" + teacherName +
                                       "\n\nNo podrá iniciar sesión en el sistema.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            executeDeactivation(teacher);
        }
    }

    private void executeDeactivation(Teacher teacher) {
        try {
            TeacherDAO teacherDao = new TeacherDAO();
            boolean deactivationSuccess = teacherDao.deactivateTeacher(teacher.getNoPersonal());

            if (deactivationSuccess) {
                teacher.setActivityStatus(false);
                teacherTable.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Éxito",
                    "El profesor ha sido inactivado correctamente.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Sin cambios",
                    "No se encontró el profesor en la base de datos.");
            }
        } catch (DAOException exception) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "No se pudo inactivar al profesor.\n" + exception.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}