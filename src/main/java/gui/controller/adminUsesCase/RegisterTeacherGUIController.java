package gui.controller.adminUsesCase;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import logic.businessObject.Teacher;
import logic.businessObject.User;
import logic.businessObject.UserType;
import logic.dao.TeacherDAO;
import logic.dao.UserDAO;
import logic.exceptions.DAOException;

/**
 * Autor: gamal
 */
public class RegisterTeacherGUIController implements Initializable {

    @FXML private TextField     personalNumberField;
    @FXML private TextField     nameField;
    @FXML private TextField     paternalSurnameField;
    @FXML private TextField     maternalSurnameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton   coordinatorRadioButton;
    @FXML private RadioButton   teacherRadioButton;

    private ToggleGroup roleToggleGroup;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleToggleGroup = new ToggleGroup();
        coordinatorRadioButton.setToggleGroup(roleToggleGroup);
        teacherRadioButton.setToggleGroup(roleToggleGroup);
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        try {
            int personalNumber = Integer.parseInt(personalNumberField.getText().trim());
            boolean isCoordinator = coordinatorRadioButton.isSelected();

            User newUser = new User();
            newUser.setGmail(emailField.getText().trim());
            newUser.setPlainPassword(passwordField.getText());
            newUser.setUserType(isCoordinator ? UserType.COORDINADOR : UserType.PROFESOR);
            newUser.setActive(true);

            UserDAO userDAO = new UserDAO();
            int userId = userDAO.registerUser(newUser);

            Teacher newTeacher = new Teacher();
            newTeacher.setNoPersonal(personalNumber);
            newTeacher.setName(nameField.getText().trim());
            newTeacher.setPaternalSurname(paternalSurnameField.getText().trim());
            newTeacher.setMaternalSurname(maternalSurnameField.getText().trim());
            newTeacher.setRole(isCoordinator ? "Coordinador" : "Profesor");
            newTeacher.setActivityStatus(true);
            newTeacher.setIdUser(userId);

            TeacherDAO teacherDAO = new TeacherDAO();
            boolean success = teacherDAO.registerTeacher(newTeacher);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Confirmación", "Registro exitoso.");
                clearFields();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato",
                "El número de personal debe contener solo números.");
        } catch (DAOException e) {
            handleDAOException(e);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Aviso", "Registro cancelado.");
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {
        if (personalNumberField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos",
                "El número de personal es obligatorio.");
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos",
                "El nombre es obligatorio.");
            return false;
        }
        if (paternalSurnameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos",
                "El apellido paterno es obligatorio.");
            return false;
        }
        if (emailField.getText().trim().isEmpty()
                || !emailField.getText().contains("@")) {
            showAlert(Alert.AlertType.WARNING, "Correo inválido",
                "Ingresa un correo electrónico válido.");
            return false;
        }
        if (passwordField.getText().isEmpty()
                || passwordField.getText().length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Contraseña inválida",
                "La contraseña debe tener al menos 8 caracteres.");
            return false;
        }
        if (!coordinatorRadioButton.isSelected() && !teacherRadioButton.isSelected()) {
            showAlert(Alert.AlertType.WARNING, "Falta el rol",
                "Por favor selecciona si es Coordinador o Profesor.");
            return false;
        }
        return true;
    }

    private void handleDAOException(DAOException e) {
        String message = e.getMessage();
        if (message != null && message.contains("correo electrónico ya está registrado")) {
            showAlert(Alert.AlertType.WARNING, "Aviso",
                "El correo electrónico ya está registrado en el sistema.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error",
                "Error en la conexión a la base de datos.");
        }
    }

    private void clearFields() {
        personalNumberField.clear();
        nameField.clear();
        paternalSurnameField.clear();
        maternalSurnameField.clear();
        emailField.clear();
        passwordField.clear();
        if (roleToggleGroup.getSelectedToggle() != null) {
            roleToggleGroup.getSelectedToggle().setSelected(false);
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