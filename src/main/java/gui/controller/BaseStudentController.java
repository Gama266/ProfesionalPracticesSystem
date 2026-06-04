package gui.controller;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import logic.businessObject.Student;
import logic.dao.StudentDAO;
import logic.exceptions.DAOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 *
 * @author akyer
 */


public abstract class BaseStudentController implements Initializable {

    @FXML protected TextField searchTextField;
    @FXML protected TableView<Student> studentTableView;
    @FXML protected TableColumn<Student, String> enrollmentColumn;
    @FXML protected TableColumn<Student, String> fullNameColumn;
    @FXML protected TableColumn<Student, String> statusColumn;

    protected final ObservableList<Student> studentList =
            FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureSharedColumns();
        loadStudents();
        configureSearchField();
        initializeSpecificConfiguration();
    }

    private void configureSharedColumns() {
        configureEnrollmentColumn();
        configureFullNameColumn();
        configureStatusColumn();
    }

    private void configureEnrollmentColumn() {
        enrollmentColumn.setCellValueFactory(
                this::buildEnrollmentCellValue
        );
    }
    
    private ReadOnlyStringWrapper buildEnrollmentCellValue(
            TableColumn.CellDataFeatures<Student, String> data) {
        return new ReadOnlyStringWrapper(
                data.getValue().getMatricula()
        );
    }

    private void configureFullNameColumn() {
        fullNameColumn.setCellValueFactory(
                this::buildFullNameCellValue
        );
    }
    private ReadOnlyStringWrapper buildFullNameCellValue(
            TableColumn.CellDataFeatures<Student, String> data) {
        Student student = data.getValue();
        String fullName =
                student.getName()
                + " "
                + student.getPaternalSurname()
                + " "
                + getSafeString(student.getMaternalSurname());

        return new ReadOnlyStringWrapper(fullName.trim());
    }

    private void configureStatusColumn() {
        statusColumn.setCellValueFactory(
                this::buildStatusCellValue
        );
        statusColumn.setCellFactory(
                this::buildStatusCell
        );
    }

    private ReadOnlyStringWrapper buildStatusCellValue(
            TableColumn.CellDataFeatures<Student, String> data) {
        String statusLabel =
                Boolean.TRUE.equals(data.getValue().getActivityStatus())
                        ? "Activo"
                        : "Inactivo";
        return new ReadOnlyStringWrapper(statusLabel);
    }

    private TableCell<Student, String> buildStatusCell(
            TableColumn<Student, String> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String status,
                                      boolean isEmpty) {
                super.updateItem(status, isEmpty);
                if (isEmpty || status == null) {
                    clearCell();
                } else {
                    configureStatusCell(status);
                }
            }

            private void clearCell() {
                setText(null);
                setStyle("");
            }

            private void configureStatusCell(String status) {
                setText(status);
                setStyle(
                        "Activo".equals(status)
                                ? "-fx-text-fill: #16a34a; -fx-font-weight: bold;"
                                : "-fx-text-fill: #dc2626; -fx-font-weight: bold;"
                );
            }
        };
    }

    private void loadStudents() {

        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> students =
                    studentDAO.getAllStudents();
            studentList.setAll(students);
            studentTableView.setItems(studentList);
        } catch (DAOException exception) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo cargar la lista de estudiantes.\n"
                            + exception.getMessage()
            );
        }
    }

    private void configureSearchField() {
        searchTextField.textProperty().addListener(
                this::handleSearchTextChange
        );
    }

    private void handleSearchTextChange(
            javafx.beans.value.ObservableValue<? extends String> observable,
            String previousValue,
            String currentValue) {
        filterTable(currentValue);
    }

    private void filterTable(String filterText) {
        boolean isFilterEmpty =
                filterText == null || filterText.isBlank();
        if (isFilterEmpty) {
            studentTableView.setItems(studentList);
        } else {
            applyStudentFilter(filterText);
        }

        onFilterApplied();
    }

    private void applyStudentFilter(String filterText) {
        String normalizedFilter =
                filterText.toLowerCase();
        List<Student> filteredStudents =
                studentList.stream()
                        .filter(student ->
                                matchesFilter(student, normalizedFilter))
                        .collect(Collectors.toList());
        ObservableList<Student> filteredObservableList =
                FXCollections.observableArrayList(filteredStudents);
        studentTableView.setItems(filteredObservableList);
    }

    private boolean matchesFilter(Student student,
                                  String normalizedFilter) {
        return student.getMatricula()
                        .toLowerCase()
                        .contains(normalizedFilter)
                || student.getName()
                        .toLowerCase()
                        .contains(normalizedFilter)
                || student.getPaternalSurname()
                        .toLowerCase()
                        .contains(normalizedFilter)
                || getSafeString(student.getMaternalSurname())
                        .toLowerCase()
                        .contains(normalizedFilter);
    }

    protected String getSafeString(String value) {
        return value != null
                ? value
                : "";
    }

    protected void showAlert(Alert.AlertType alertType,
                             String title,
                             String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    protected void initializeSpecificConfiguration() {
    }

    protected void onFilterApplied() {
    }
}
