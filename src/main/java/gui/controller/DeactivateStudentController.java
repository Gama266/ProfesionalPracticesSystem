package gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import logic.businessObject.Student;
import logic.dao.StudentDAO;
import logic.exceptions.DAOException;
 
/**
 * @author akyer
 */
public class DeactivateStudentController extends BaseStudentController {
    @FXML private TableColumn<Student, Void> actionColumn;

    @Override
    protected void initializeSpecificConfiguration() {
        configureActionColumn();
    }
    
    private void configureActionColumn() {
        actionColumn.setCellFactory(
                this::buildActionCell
        );
    }

    private TableCell<Student, Void> buildActionCell(
            TableColumn<Student, Void> column) {
        return new TableCell<>() {
            private final Button deactivateButton =
                    createDeactivateButton();
            @Override
            protected void updateItem(Void item,
                                      boolean isEmpty) {
                super.updateItem(item, isEmpty);
                if (isEmpty) {
                    clearCell();
                } else {
                    configureCellButton();
                }
            }

            private void clearCell() {

                setGraphic(null);
            }

            private void configureCellButton() {

                Student student =
                        getCurrentStudent();

                boolean isStudentActive =
                        Boolean.TRUE.equals(
                                student.getActivityStatus()
                        );

                deactivateButton.setDisable(
                        !isStudentActive
                );

                setGraphic(deactivateButton);
            }

            private Student getCurrentStudent() {

                return getTableView()
                        .getItems()
                        .get(getIndex());
            }

            private Button createDeactivateButton() {

                Button button =
                        new Button("Inactivar");

                configureDeactivateButtonStyle(button);

                button.setOnAction(
                        this::handleDeactivateButtonAction
                );

                return button;
            }

            private void configureDeactivateButtonStyle(
                    Button button) {

                button.setStyle(
                        "-fx-background-color: #dc2626;"
                        + "-fx-text-fill: white;"
                        + "-fx-cursor: hand;"
                        + "-fx-font-size: 11px;"
                );
            }

            private void handleDeactivateButtonAction(
                    ActionEvent event) {

                Student selectedStudent =
                        getCurrentStudent();

                confirmStudentDeactivation(
                        selectedStudent
                );
            }
        };
    }  

    private void confirmStudentDeactivation(
            Student student) {

        String fullName =
                buildStudentFullName(student);

        Alert confirmationAlert =
                createConfirmationAlert(
                        fullName,
                        student.getMatricula()
                );

        ButtonType selectedButton =
        confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);

        handleConfirmationResult(selectedButton, student);
    }

    private void handleConfirmationResult(
        ButtonType selectedButton,
        Student student) {
        boolean isConfirmed =
            selectedButton == ButtonType.OK;
        if (isConfirmed) {
            executeStudentDeactivation(student);
    }
}

    private Alert createConfirmationAlert(
            String fullName,
            String enrollment) {

        Alert confirmationAlert =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmationAlert.setTitle(
                "Confirmar inactivación"
        );

        confirmationAlert.setHeaderText(
                "¿Inactivar al estudiante?"
        );

        confirmationAlert.setContentText(
                "Esta acción marcará como inactivo a:\n"
                + fullName
                + " ("
                + enrollment
                + ")"
                + "\n\nNo podrá iniciar sesión."
        );

        return confirmationAlert;
    }

    private String buildStudentFullName(
            Student student) {

        return student.getName()
                + " "
                + student.getPaternalSurname();
    }

    private void executeStudentDeactivation(
            Student student) {

        try {

            StudentDAO studentDAO =
                    new StudentDAO();

            boolean wasDeactivated =
                    studentDAO.deactivateStudent(
                            student.getMatricula()
                    );

            handleDeactivationResult(
                    wasDeactivated,
                    student
            );

        } catch (DAOException exception) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo inactivar.\n"
                            + exception.getMessage()
            );
        }
    }

    private void handleDeactivationResult(
            boolean wasDeactivated,
            Student student) {

        if (wasDeactivated) {

            updateStudentStatus(student);

            refreshStudentTable();

            showSuccessfulDeactivationAlert();

        } else {

            showStudentNotFoundAlert();
        }
    }

    private void updateStudentStatus(
            Student student) {

        student.setActivityStatus(false);
    }

    private void refreshStudentTable() {

        studentTableView.refresh();
    }

    private void showSuccessfulDeactivationAlert() {

        showAlert(
                Alert.AlertType.INFORMATION,
                "Éxito",
                "Estudiante inactivado correctamente."
        );
    }

    private void showStudentNotFoundAlert() {

        showAlert(
                Alert.AlertType.WARNING,
                "Sin cambios",
                "No se encontró al estudiante."
        );
    }
}