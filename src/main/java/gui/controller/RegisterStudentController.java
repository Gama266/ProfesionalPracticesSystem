package gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import logic.businessObject.Student;
import logic.businessObject.User;
import logic.businessObject.UserType;
import logic.dao.StudentDAO;
import logic.dao.UserDAO;
import logic.exceptions.DAOException;

/**
 * @author akyer
 */
public class RegisterStudentController implements Initializable {

    @FXML private TextField enrollmentField;
    @FXML private TextField nameField;
    @FXML private TextField paternalSurnameField;
    @FXML private TextField maternalSurnameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Button registerButton;
    @FXML private Button clearButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        enrollmentField.textProperty().addListener((observable, oldValue, newValue) -> clearError());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> clearError());
    }

    @FXML
    private void handleRegister() {
        clearError();

        String enrollment = enrollmentField.getText().trim();
        String name = nameField.getText().trim();
        String paternalSurname = paternalSurnameField.getText().trim();
        String maternalSurname = maternalSurnameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!validateFields(enrollment, name, paternalSurname, maternalSurname, 
                           email, password, confirmPassword)) {
            return;
        }

        setFormEnabled(false);

        try {
            User newUser = new User();
            newUser.setGmail(email);
            newUser.setPlainPassword(password);
            newUser.setUserType(UserType.ESTUDIANTE);
            newUser.setActive(true);

            UserDAO userDao = new UserDAO();
            int userId = userDao.registerUser(newUser);

            Student newStudent = new Student();
            newStudent.setMatricula(enrollment);
            newStudent.setName(name);
            newStudent.setPaternalSurname(paternalSurname);
            newStudent.setMaternalSurname(maternalSurname);
            newStudent.setActivityStatus(true);
            newStudent.setIdUser(userId);
            newStudent.setProject(null);

            StudentDAO studentDao = new StudentDAO();
            boolean success = studentDao.registerStudent(newStudent);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Registro exitoso",
                    "El estudiante " + name + " " + paternalSurname + " fue registrado correctamente.");
                handleClear();
            } else {
                showError("No se pudo completar el registro. Intenta de nuevo.");
            }

        } catch (DAOException exception) {
            String message = exception.getMessage();
            if (message != null && message.contains("correo electrónico ya está registrado")) {
                showError("El correo electrónico ya está registrado en el sistema.");
            } else {
                showError("Error al registrar: " + message);
                
            }
        } finally {
            setFormEnabled(true);
        }
    }

    @FXML
    private void handleClear() {
        enrollmentField.clear();
        nameField.clear();
        paternalSurnameField.clear();
        maternalSurnameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        clearError();
        enrollmentField.requestFocus();
    }

    private boolean validateFields(String enrollment, String name, String paternalSurname,
                                   String maternalSurname, String email,
                                   String password, String confirmPassword) {
        
        boolean isValid = validateEnrollment(enrollment);
        isValid = isValid && validateName(name, paternalSurname, maternalSurname);
        isValid = isValid && validateEmail(email);
        isValid = isValid && validatePassword(password, confirmPassword);
        
        return isValid;
    }

    private boolean validateEnrollment(String enrollment) {
        boolean isValid = true;
        
        if (enrollment.isEmpty()) {
            showError("La matrícula es obligatoria.");
            enrollmentField.requestFocus();
            isValid = false;
        } else if (!enrollment.matches("[Ss]\\d{8}")) {
            showError("Matrícula inválida. Formato esperado: S seguido de 8 dígitos (ej. S21013456).");
            enrollmentField.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }

    private boolean validateName(String name, String paternalSurname, String maternalSurname) {
        boolean isValid = true;
        
        if (name.isEmpty()) {
            showError("El nombre es obligatorio.");
            nameField.requestFocus();
            isValid = false;
        } else if (paternalSurname.isEmpty()) {
            showError("El apellido paterno es obligatorio.");
            paternalSurnameField.requestFocus();
            isValid = false;
        } else if (maternalSurname.isEmpty()) {
            showError("El apellido materno es obligatorio.");
            maternalSurnameField.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }

    private boolean validateEmail(String email) {
        boolean isValid = true;
        
        if (email.isEmpty()) {
            showError("El correo electrónico es obligatorio.");
            emailField.requestFocus();
            isValid = false;
        } else if (!email.contains("@") || !email.contains(".")) {
            showError("Ingresa un correo electrónico válido.");
            emailField.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }

    private boolean validatePassword(String password, String confirmPassword) {
        boolean isValid = true;
        
        if (password.isEmpty()) {
            showError("La contraseña es obligatoria.");
            passwordField.requestFocus();
            isValid = false;
        } else if (password.length() < 8) {
            showError("La contraseña debe tener al menos 8 caracteres.");
            passwordField.requestFocus();
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            showError("Las contraseñas no coinciden.");
            confirmPasswordField.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText("");
    }

    private void setFormEnabled(boolean enabled) {
        registerButton.setDisable(!enabled);
        clearButton.setDisable(!enabled);
        registerButton.setText(enabled ? "Registrar" : "Registrando...");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
