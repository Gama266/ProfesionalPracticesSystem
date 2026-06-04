
package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import logic.businessObject.Student;

/**
 *
 * @author akyer
 */

public class ConsultStudentController extends BaseStudentController {

    @FXML private Label enrollmentDetailLabel;
    @FXML private Label nameDetailLabel;
    @FXML private Label paternalSurnameDetailLabel;
    @FXML private Label maternalSurnameDetailLabel;
    @FXML private Label statusDetailLabel;

    @Override
    protected void initializeSpecificConfiguration() {
        configureStudentSelection();
    }

    private void configureStudentSelection() {
        studentTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::handleStudentSelection);
    }

    private void handleStudentSelection(
            javafx.beans.value.ObservableValue<? extends Student> observable,
            Student previousStudent,
            Student selectedStudent) {
        boolean hasSelectedStudent =
                selectedStudent != null;
        if (hasSelectedStudent) {
            showStudentDetails(selectedStudent);
        } else {

            clearStudentDetails();
        }
    }

    private void showStudentDetails(Student student) {
        enrollmentDetailLabel.setText(
                student.getMatricula()
        );
        nameDetailLabel.setText(
                student.getName()
        );
        paternalSurnameDetailLabel.setText(
                student.getPaternalSurname()
        );
        maternalSurnameDetailLabel.setText(
                getSafeString(student.getMaternalSurname())
        );
        configureStatusDetail(student);
    }

    private void configureStatusDetail(Student student) {
        boolean isActive =
                Boolean.TRUE.equals(student.getActivityStatus());
        String statusLabel =
                isActive
                        ? "Activo"
                        : "Inactivo";
        String statusStyle =
                isActive
                        ? "-fx-text-fill: #16a34a; -fx-font-weight: bold;"
                        : "-fx-text-fill: #dc2626; -fx-font-weight: bold;";
        statusDetailLabel.setText(statusLabel);
        statusDetailLabel.setStyle(statusStyle);
    }

    private void clearStudentDetails() {
        enrollmentDetailLabel.setText("—");
        nameDetailLabel.setText("—");
        paternalSurnameDetailLabel.setText("—");
        maternalSurnameDetailLabel.setText("—");
        statusDetailLabel.setText("—");
        statusDetailLabel.setStyle("");
    }

    @Override
    protected void onFilterApplied() {

        clearStudentDetails();
    }
}
