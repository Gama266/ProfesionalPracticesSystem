package gui.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import logic.businessObject.Activity;
import logic.businessObject.Project;
import logic.dao.ActivityDAO;
import logic.dao.ProjectDAO;
import logic.dao.RequestDAO;
import logic.exceptions.DAOException;

public class RequestProjectGUIController implements Initializable {

    @FXML private TableView<Project>            tableProjects;
    @FXML private TableColumn<Project, String>  columnName;
    @FXML private TableColumn<Project, String>  columnDescription;
    @FXML private TableColumn<Project, Integer> columnAvailableSpaces;
    @FXML private TableColumn<Project, Void>    columnView;
    @FXML private TableColumn<Project, Void>    columnAction;
    @FXML private Label                         labelCounterRequest;

    private final ProjectDAO  projectDAO  = new ProjectDAO();
    private final RequestDAO  requestDAO  = new RequestDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();

    private String currentPractitionerEnrollment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
    }

    public void setPractitionerEnrollment(String enrollment) {
        this.currentPractitionerEnrollment = enrollment;
        loadTableData();
    }

    // ── Configuración de columnas ─────────────────────────────────────────────

    private void configureTableColumns() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnAvailableSpaces.setCellValueFactory(new PropertyValueFactory<>("availableSpaces"));
        configureViewColumn();
        configureActionColumn();
    }

    private void configureViewColumn() {
        Callback<TableColumn<Project, Void>, TableCell<Project, Void>> factory =
            col -> new TableCell<>() {
                private final Button btnView = new Button("Visualizar");

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        return;
                    }
                    Project project = getTableView().getItems().get(getIndex());
                    btnView.setOnAction(e -> showProjectDetails(project));
                    setGraphic(btnView);
                }
            };
        columnView.setCellFactory(factory);
    }

    private void configureActionColumn() {
        Callback<TableColumn<Project, Void>, TableCell<Project, Void>> factory =
            col -> new TableCell<>() {
                private final Button btn = new Button("Solicitar");

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        return;
                    }
                    Project project = getTableView().getItems().get(getIndex());
                    refreshButtonState(project);
                    setGraphic(btn);
                }

                private void refreshButtonState(Project project) {
                    try {
                        int count = requestDAO
                            .getRequestCountByPracticante(currentPractitionerEnrollment);
                        boolean alreadyRequested = requestDAO
                            .hasAlreadyRequested(currentPractitionerEnrollment, project.getId());
                        boolean limitReached = count >= 3;
                        boolean noSpaces     = project.getAvailableSpaces() <= 0;

                        if (alreadyRequested) {
                            btn.setText("Solicitado");
                            btn.setDisable(true);
                        } else if (noSpaces) {
                            btn.setText("Agotado");
                            btn.setDisable(true);
                        } else if (limitReached) {
                            btn.setText("Solicitar");
                            btn.setDisable(true);
                        } else {
                            btn.setText("Solicitar");
                            btn.setDisable(false);
                            btn.setOnAction(e -> handleRequest(project));
                        }
                    } catch (DAOException e) {
                        btn.setText("Error");
                        btn.setDisable(true);
                    }
                }
            };
        columnAction.setCellFactory(factory);
    }

    // ── Visualizar detalles del proyecto ──────────────────────────────────────

    private void showProjectDetails(Project project) {
        try {
            List<Activity> activities = activityDAO.getActivitiesByProject(project.getId());

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Detalles del Proyecto");
            dialog.setHeaderText(project.getName());
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            VBox content = new VBox(14);
            content.setPadding(new Insets(20));
            content.setPrefWidth(520);

            content.getChildren().addAll(
                buildDetailRow("Organización:",
                    project.getLinkedOrganization() != null
                        ? project.getLinkedOrganization().getName() : "—"),
                buildDetailRow("Descripción:",   project.getDescription()),
                buildDetailRow("Metodología:",   project.getMethodology()),
                buildDetailRow("Objetivo general:", project.getObjective()),
                buildDetailRow("Cupo disponible:", String.valueOf(project.getAvailableSpaces())),
                buildActivitiesSection(activities)
            );

            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(480);

            dialog.getDialogPane().setContent(scrollPane);
            dialog.showAndWait();

        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "No se pudieron cargar los detalles del proyecto: " + e.getMessage());
        }
    }

    private VBox buildDetailRow(String label, String value) {
        VBox box = new VBox(3);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        Label val = new Label(value != null && !value.isBlank() ? value : "—");
        val.setWrapText(true);
        val.setStyle("-fx-text-fill: #374151;");
        box.getChildren().addAll(lbl, val);
        return box;
    }

    private VBox buildActivitiesSection(List<Activity> activities) {
        VBox section = new VBox(8);

        Label title = new Label("Actividades del proyecto:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        section.getChildren().add(title);

        if (activities == null || activities.isEmpty()) {
            Label empty = new Label("Este proyecto aún no tiene actividades registradas.");
            empty.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic;");
            section.getChildren().add(empty);
            return section;
        }

        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            VBox card = new VBox(4);
            card.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; "
                        + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");

            Label actName = new Label((i + 1) + ". " + activity.getName());
            actName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

            Label actDesc = new Label(activity.getDescription() != null
                ? activity.getDescription() : "Sin descripción");
            actDesc.setWrapText(true);
            actDesc.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

            Label actDates = new Label(
                "Del " + activity.getStartDate() + " al " + activity.getEndDate() +
                "  |  " + (int) activity.getPlannedHours() + " horas planeadas");
            actDates.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 11px;");

            card.getChildren().addAll(actName, actDesc, actDates);
            section.getChildren().add(card);
        }

        return section;
    }

   
    private void loadTableData() {
        try {
            int count = requestDAO.getRequestCountByPracticante(currentPractitionerEnrollment);
            labelCounterRequest.setText(count + " / 3");

            List<Project> projects = projectDAO.getAllActiveProjects();
            tableProjects.setItems(FXCollections.observableArrayList(projects));

        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error de sistema",
                "No fue posible cargar los proyectos: " + e.getMessage());
        }
    }

    // ── Solicitar proyecto ────────────────────────────────────────────────────

    private void handleRequest(Project project) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar solicitud");
        confirmation.setHeaderText("Confirmación requerida");
        confirmation.setContentText(
            "¿Desea enviar su solicitud para el proyecto:\n" + project.getName() + "?");

        Optional<ButtonType> choice = confirmation.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            try {
                boolean registered = requestDAO.registerRequest(
                    currentPractitionerEnrollment, project.getId());
                if (registered) {
                    showAlert(Alert.AlertType.INFORMATION, "Solicitud exitosa",
                        "La solicitud fue enviada al coordinador correctamente.");
                    loadTableData();
                }
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Error de registro",
                    "No se pudo registrar la solicitud: " + e.getMessage());
            }
        }
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}