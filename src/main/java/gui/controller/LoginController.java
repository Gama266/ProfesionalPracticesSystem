
package gui.controller;

import logic.businessObject.SessionManager;
import gui.usersviews.StudentMenuGUIController;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.businessObject.User;
import logic.businessObject.UserType;
import logic.dao.UserDAO;
import logic.exceptions.DAOException;


public class LoginController implements Initializable {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private Button btnLogin;

    private static final String FXML_COORDINATOR =
            "/gui/fxml/CoordinatorMenuGUI.fxml";

    private static final String FXML_ADMIN =
            "/gui/fxml/AdminMenuGUI.fxml";

    private static final String FXML_TEACHER =
            "/gui/fxml/teacherMenu.fxml";

    private static final String FXML_STUDENT =
            "/gui/fxml/StudentMenuGUI.fxml";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        txtEmail.textProperty().addListener(
            (obs, oldVal, newVal) -> clearError()
        );

        txtPassword.textProperty().addListener(
            (obs, oldVal, newVal) -> clearError()
        );
    }

    @FXML
    private void handleLogin(ActionEvent event) {

        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (!validateFields(email, password)) {
            return;
        }

        setFormEnabled(false);

        try {

            UserDAO userDAO = new UserDAO();

            Optional<User> result =
                    userDAO.login(email, password);

            if (result.isPresent()) {

                User currentUser = result.get();

                SessionManager.setCurrentUser(currentUser);

                if (currentUser.getUserType()
                        == UserType.ESTUDIANTE) {

                    String enrollment =
                            userDAO
                            .getPractitionerEnrollmentByUserId(
                                    currentUser.getIdUser()
                            );

                    SessionManager
                            .setCurrentEnrollment(enrollment);
                }

                navigateToMenu(currentUser);

            } else {

                showError(
                    "Correo o contraseña incorrectos. "
                    + "Intenta de nuevo."
                );

                txtPassword.clear();
            }

        } catch (DAOException e) {

            showError(
                "Error de conexión con la base de datos. "
                + "Verifica tu configuración."
            );

            System.err.println(
                "[LoginController] DAOException: "
                + e.getMessage()
            );

        } finally {

            setFormEnabled(true);
        }
    }

    private void navigateToMenu(User user) {

        String fxmlPath =
                resolveFxmlPath(user.getUserType());

        String title =
                resolveWindowTitle(user.getUserType());

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(fxmlPath)
                    );

            Parent root = loader.load();

            injectUserIntoController(loader, user);

            Stage stage =
                    (Stage) btnLogin
                            .getScene()
                            .getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {

            showError(
                "No se pudo cargar el módulo. "
                + "Contacta al administrador."
            );

            System.err.println(
                "[LoginController] IOException cargando "
                + "FXML: "
                + fxmlPath
                + " — "
                + e.getMessage()
            );

        } catch (NullPointerException e) {

            showError(
                "El módulo para '"
                + user.getUserType().getDbValue()
                + "' aún no está disponible."
            );

            System.err.println(
                "[LoginController] FXML no encontrado "
                + "(null): "
                + fxmlPath
            );
        }
    }

    private void injectUserIntoController(
            FXMLLoader loader,
            User user
    ) {

        if (user.getUserType()
                == UserType.ESTUDIANTE) {

            StudentMenuGUIController controller =
                    loader.getController();

            controller.setUsuarioLogueado(user);
        }
    }

    private String resolveFxmlPath(UserType type) {

        return switch (type) {

            case COORDINADOR ->
                FXML_COORDINATOR;

            case ADMINISTRADOR ->
                FXML_ADMIN;

            case PROFESOR ->
                FXML_TEACHER;

            case ESTUDIANTE ->
                FXML_STUDENT;
        };
    }

    private String resolveWindowTitle(UserType type) {

        return switch (type) {

            case COORDINADOR ->
                "Sistema de Prácticas — Coordinador";

            case ADMINISTRADOR ->
                "Sistema de Prácticas — Administrador";

            case PROFESOR ->
                "Sistema de Prácticas — Profesor";

            case ESTUDIANTE ->
                "Sistema de Prácticas — Estudiante";
        };
    }

    private boolean validateFields(
            String email,
            String password
    ) {

        if (email.isEmpty()
                && password.isEmpty()) {

            showError(
                "Por favor ingresa tu correo "
                + "y contraseña."
            );

            return false;
        }

        if (email.isEmpty()) {

            showError(
                "El correo electrónico es obligatorio."
            );

            return false;
        }

        if (!email.contains("@")) {

            showError(
                "Ingresa un correo electrónico válido."
            );

            return false;
        }

        if (password.isEmpty()) {

            showError(
                "La contraseña es obligatoria."
            );

            return false;
        }

        return true;
    }

    private void showError(String message) {
        lblError.setText(message);
    }

    private void clearError() {
        lblError.setText("");
    }

    private void setFormEnabled(boolean enabled) {

        txtEmail.setDisable(!enabled);
        txtPassword.setDisable(!enabled);
        btnLogin.setDisable(!enabled);

        btnLogin.setText(
            enabled
                ? "Ingresar"
                : "Verificando..."
        );
    }
}
