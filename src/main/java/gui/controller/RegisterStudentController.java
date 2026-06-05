package gui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import logic.businessObject.EducationalExperience;
import logic.businessObject.Student;
import logic.businessObject.User;
import logic.businessObject.UserType;
import logic.dao.EducationalExperienceDAO;
import logic.dao.StudentDAO;
import logic.dao.UserDAO;
import logic.exceptions.DAOException;

/**
 * Controlador de la vista de registro de estudiantes con asignación dinámica de Experiencia Educativa y Oportunidad.
 * Autores: Jhonatan Yeray & Gamaliel Cabrera
 */
public class RegisterStudentController implements Initializable {

    @FXML private TextField textFieldEnrollment;
    @FXML private TextField textFieldName;
    @FXML private TextField textFieldPaternalSurname;
    @FXML private TextField textFieldMaternalSurname;
    @FXML private TextField textFieldEmail;
    @FXML private PasswordField passwordFieldPassword;
    @FXML private PasswordField passwordFieldConfirmPassword;
    @FXML private ComboBox<EducationalExperience> comboBoxEducationalExperience;
    @FXML private ComboBox<Integer> comboBoxEnrollmentNumber; // Nuevo ComboBox
    @FXML private Label labelError;
    @FXML private Button buttonRegister;
    @FXML private Button buttonClear;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldEnrollment.textProperty().addListener((observable, oldValue, newValue) -> clearError());
        textFieldEmail.textProperty().addListener((observable, oldValue, newValue) -> clearError());
        
        setupEducationalExperienceComboBox();
        setupEnrollmentNumberComboBox();
        loadEducationalExperiences();
    }


    private void setupEducationalExperienceComboBox() {
        comboBoxEducationalExperience.setConverter(new StringConverter<EducationalExperience>() {
            @Override
            public String toString(EducationalExperience educationalExperience) {
                if (educationalExperience == null) {
                    return null;
                }
                return "NRC: " + educationalExperience.getNrc() + " - Sección: " + educationalExperience.getSection();
            }

            @Override
            public EducationalExperience fromString(String string) {
                return null;
            }
        });
        
        comboBoxEducationalExperience.valueProperty().addListener((observable, oldValue, newValue) -> clearError());
    }

  
    private void setupEnrollmentNumberComboBox() {
        ObservableList<Integer> opportunities = FXCollections.observableArrayList(1, 2, 3);
        comboBoxEnrollmentNumber.setItems(opportunities);
        comboBoxEnrollmentNumber.getSelectionModel().selectFirst(); 
        comboBoxEnrollmentNumber.valueProperty().addListener((observable, oldValue, newValue) -> clearError());
    }

   
    private void loadEducationalExperiences() {
        try {
            EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
            List<EducationalExperience> experiences = educationalExperienceDAO.getAllEducationalExperiences();
            ObservableList<EducationalExperience> observableExperiences = FXCollections.observableArrayList(experiences);
            comboBoxEducationalExperience.setItems(observableExperiences);
        } catch (DAOException e) {
            showError("Advertencia: No se pudo cargar el listado de Experiencias Educativas.");
        }
    }

 
    @FXML
    private void handleRegister() {
        clearError();

        String enrollment = textFieldEnrollment.getText().trim();
        String name = textFieldName.getText().trim();
        String paternalSurname = textFieldPaternalSurname.getText().trim();
        String maternalSurname = textFieldMaternalSurname.getText().trim();
        String email = textFieldEmail.getText().trim();
        String password = passwordFieldPassword.getText();
        String confirmPassword = passwordFieldConfirmPassword.getText();
        EducationalExperience selectedExperience = comboBoxEducationalExperience.getValue();
        Integer selectedEnrollmentNumber = comboBoxEnrollmentNumber.getValue();

        if (!validateFields(enrollment, name, paternalSurname, maternalSurname, 
                            email, password, confirmPassword, selectedExperience, selectedEnrollmentNumber)) {
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
            boolean studentRegistered = studentDao.registerStudent(newStudent);

            if (studentRegistered) {
         
                boolean experienceAssigned = studentDao.assignEducationalExperience(
                    newStudent.getMatricula(), 
                    selectedExperience.getNrc(), 
                    selectedEnrollmentNumber
                );
                
                if (experienceAssigned) {
                    showAlert(Alert.AlertType.INFORMATION, "Registro exitoso",
                        "El estudiante " + name + " " + paternalSurname + 
                        " fue registrado e inscrito en la oportunidad número [" + selectedEnrollmentNumber + 
                        "] para el NRC: " + selectedExperience.getNrc());
                    handleClear();
                } else {
                    showError("El estudiante se creó, pero falló la asignación en cursaexperienciaeducativa.");
                }
            } else {
                showError("No se pudo completar el registro del estudiante. Intenta de nuevo.");
            }

        } catch (DAOException exception) {
            String message = exception.getMessage();
            if (message != null && message.contains("correo electrónico ya está registrado")) {
                showError("El correo electrónico ya está registrado en el sistema.");
            } else {
                showError("Error al registrar en la base de datos: " + message);
            }
        } finally {
            setFormEnabled(true);
        }
    }

  
    @FXML
    private void handleClear() {
        textFieldEnrollment.clear();
        textFieldName.clear();
        textFieldPaternalSurname.clear();
        textFieldMaternalSurname.clear();
        textFieldEmail.clear();
        passwordFieldPassword.clear();
        passwordFieldConfirmPassword.clear();
        comboBoxEducationalExperience.getSelectionModel().clearSelection();
        comboBoxEnrollmentNumber.getSelectionModel().selectFirst(); 
        clearError();
        textFieldEnrollment.requestFocus();
    }

    private boolean validateFields(String enrollment, String name, String paternalSurname,
                                   String maternalSurname, String email,
                                   String password, String confirmPassword,
                                   EducationalExperience selectedExperience,
                                   Integer selectedEnrollmentNumber) {
        
        boolean isValid = validateEnrollment(enrollment);
        isValid = isValid && validateName(name, paternalSurname, maternalSurname);
        isValid = isValid && validateEmail(email);
        isValid = isValid && validatePassword(password, confirmPassword);
        
        if (isValid && selectedExperience == null) {
            showError("Debe seleccionar una Experiencia Educativa para el estudiante.");
            comboBoxEducationalExperience.requestFocus();
            isValid = false;
        }
        
        if (isValid && selectedEnrollmentNumber == null) {
            showError("Debe seleccionar el número de inscripción correspondiente.");
            comboBoxEnrollmentNumber.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }

    private boolean validateEnrollment(String enrollment) {
        if (enrollment.isEmpty()) {
            showError("La matrícula es obligatoria.");
            textFieldEnrollment.requestFocus();
            return false;
        } else if (!enrollment.matches("[Ss]\\d{8}")) {
            showError("Matrícula inválida. Formato esperado: S seguido de 8 dígitos (ej. S21013456).");
            textFieldEnrollment.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateName(String name, String paternalSurname, String maternalSurname) {
        if (name.isEmpty()) {
            showError("El nombre es obligatorio.");
            textFieldName.requestFocus();
            return false;
        } else if (paternalSurname.isEmpty()) {
            showError("El apellido paterno es obligatorio.");
            textFieldPaternalSurname.requestFocus();
            return false;
        } else if (maternalSurname.isEmpty()) {
            showError("El apellido materno es obligatorio.");
            textFieldMaternalSurname.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            showError("El correo electrónico es obligatorio.");
            textFieldEmail.requestFocus();
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            showError("Ingresa un correo electrónico válido.");
            textFieldEmail.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password, String confirmPassword) {
        if (password.isEmpty()) {
            showError("La contraseña es obligatoria.");
            passwordFieldPassword.requestFocus();
            return false;
        } else if (password.length() < 8) {
            showError("La contraseña debe tener al menos 8 caracteres.");
            passwordFieldPassword.requestFocus();
            return false;
        } else if (!password.equals(confirmPassword)) {
            showError("Las contraseñas no coinciden.");
            passwordFieldConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void showError(String message) {
        labelError.setText(message);
    }

    private void clearError() {
        labelError.setText("");
    }

    private void setFormEnabled(boolean enabled) {
        buttonRegister.setDisable(!enabled);
        buttonClear.setDisable(!enabled);
        buttonRegister.setText(enabled ? "Registrar" : "Registrando...");
        comboBoxEducationalExperience.setDisable(!enabled);
        comboBoxEnrollmentNumber.setDisable(!enabled);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}