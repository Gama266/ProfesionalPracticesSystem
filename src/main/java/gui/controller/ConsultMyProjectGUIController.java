/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package gui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.businessObject.Activity;
import logic.businessObject.Report;
import logic.businessObject.ReportGeneralData;
import logic.businessObject.SessionManager;
import logic.dao.ActivityDAO;
import logic.dao.ReportDAO;
import logic.dao.StudentDAO;
import logic.exceptions.DAOException;

/**
 * FXML Controller class
 *
 * @author gamal
 */

public class ConsultMyProjectGUIController implements Initializable {

    // ── Información general ───────────────────────────────────────────────────
    @FXML private Label projectNameLabel;
    @FXML private Label organizationLabel;
    @FXML private Label technicalResponsibleLabel;
    @FXML private Label methodologyLabel;
    @FXML private Label objectiveLabel;
    @FXML private Label professorLabel;

    // ── Control de horas ──────────────────────────────────────────────────────
    @FXML private Label       accumulatedHoursLabel;
    @FXML private Label       missingHoursLabel;
    @FXML private Label       progressPercentageLabel;
    @FXML private ProgressBar hoursProgressBar;

    // ── Actividades ───────────────────────────────────────────────────────────
    @FXML private TableView<Activity>           activitiesTable;
    @FXML private TableColumn<Activity, String> columnActivityName;
    @FXML private TableColumn<Activity, String> columnActivityDescription;
    @FXML private TableColumn<Activity, String> columnActivityStart;
    @FXML private TableColumn<Activity, String> columnActivityEnd;
    @FXML private TableColumn<Activity, String> columnActivityHours;
    @FXML private TableColumn<Activity, String> columnActivityProgress;

    // ── Historial de reportes ─────────────────────────────────────────────────
    @FXML private TableView<Report>             reportsTable;
    @FXML private TableColumn<Report, String>   columnReportType;
    @FXML private TableColumn<Report, String>   columnReportDate;
    @FXML private TableColumn<Report, String>   columnReportHours;
    @FXML private TableColumn<Report, String>   columnReportStatus;

    private static final double TOTAL_HOURS_REQUIRED = 420.0;

    private final StudentDAO  studentDAO  = new StudentDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();
    private final ReportDAO   reportDAO   = new ReportDAO();

    private Map<Integer, Integer> progressMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureActivitiesTable();
        configureReportsTable();
        loadData();
    }

    // ── Configuración de tablas ───────────────────────────────────────────────

    private void configureActivitiesTable() {
        columnActivityName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnActivityDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnActivityStart.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getStartDate().toString()));
        columnActivityEnd.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getEndDate().toString()));
        columnActivityHours.setCellValueFactory(cell ->
            new SimpleStringProperty((int) cell.getValue().getPlannedHours() + " hrs"));
        columnActivityProgress.setCellValueFactory(cell -> {
            int progress = progressMap.getOrDefault(cell.getValue().getId(), 0);
            return new SimpleStringProperty(progress + "%");
        });

        // Colorear fila en verde si avance es 100%
        activitiesTable.setRowFactory(tv -> new TableRow<Activity>() {
            @Override
            protected void updateItem(Activity activity, boolean empty) {
                super.updateItem(activity, empty);
                if (empty || activity == null) {
                    setStyle("");
                } else {
                    int progress = progressMap.getOrDefault(activity.getId(), 0);
                    if (progress >= 100) {
                        setStyle("-fx-background-color: #dcfce7;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void configureReportsTable() {
        columnReportType.setCellValueFactory(new PropertyValueFactory<>("typeOfReport"));
        columnReportDate.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getDeliveryDate().toString()));
        columnReportHours.setCellValueFactory(cell ->
            new SimpleStringProperty((int) cell.getValue().getReportedHours() + " hrs"));
        columnReportStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    // ── Carga de datos ────────────────────────────────────────────────────────

    private void loadData() {
        try {
            String matricula = SessionManager.getCurrentEnrollment();

            int projectId = studentDAO.getProjectIdByEnrollment(matricula);
            if (projectId == -1) {
                showNoProjectMessage();
                return;
            }

            ReportGeneralData generalData = studentDAO.getReportGeneralData(matricula);
            projectNameLabel.setText(generalData.getProjectName());
            organizationLabel.setText(generalData.getOrganizationName());
            technicalResponsibleLabel.setText(generalData.getTechnicalResponsibleFullName());
            methodologyLabel.setText(generalData.getProjectMethodology());
            objectiveLabel.setText(generalData.getProjectObjective());
            professorLabel.setText(generalData.getProfessorFullName());

            double accumulated = reportDAO.getAccumulatedApprovedHours(matricula);
            double missing     = Math.max(0, TOTAL_HOURS_REQUIRED - accumulated);
            double progress    = Math.min(1.0, accumulated / TOTAL_HOURS_REQUIRED);
            int    percentage  = (int) (progress * 100);

            accumulatedHoursLabel.setText((int) accumulated + " hrs");
            missingHoursLabel.setText((int) missing + " hrs");
            progressPercentageLabel.setText(percentage + "%");
            hoursProgressBar.setProgress(progress);

          
            progressMap = activityDAO.getMaxProgressByProject(generalData.getProjectId());

            List<Activity> activities = activityDAO.getActivitiesByProject(generalData.getProjectId());
            activitiesTable.setItems(FXCollections.observableArrayList(activities));

            List<Report> reports = reportDAO.getReportsByStudent(matricula);
            reportsTable.setItems(FXCollections.observableArrayList(reports));

        } catch (DAOException e) {
            projectNameLabel.setText("Error: " + e.getMessage());
        }
    }

    // ── Sin proyecto asignado ─────────────────────────────────────────────────

    private void showNoProjectMessage() {
        projectNameLabel.setText("Sin proyecto asignado");
        organizationLabel.setText("—");
        technicalResponsibleLabel.setText("—");
        methodologyLabel.setText("—");
        objectiveLabel.setText("—");
        professorLabel.setText("—");

        accumulatedHoursLabel.setText("0 hrs");
        missingHoursLabel.setText("420 hrs");
        progressPercentageLabel.setText("0%");
        hoursProgressBar.setProgress(0);

        activitiesTable.setPlaceholder(new Label(
            "Aún no tienes un proyecto asignado. " +
            "Solicita uno desde la sección 'Solicitar Proyecto'."
        ));
        reportsTable.setPlaceholder(new Label(
            "No hay reportes disponibles hasta que tengas un proyecto asignado."
        ));
    }
}