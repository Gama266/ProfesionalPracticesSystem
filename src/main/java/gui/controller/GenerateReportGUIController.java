package gui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import logic.businessObject.Activity;
import logic.businessObject.ActivityAdvance;
import logic.businessObject.ActivityAdvanceRow;
import logic.businessObject.DeliverableRow;
import logic.businessObject.PerformanceCriterionRow;
import logic.businessObject.Project;
import logic.businessObject.Report;
import logic.businessObject.ReportGeneralData;
import logic.businessObject.SessionManager;
import logic.businessObject.Student;
import logic.dao.ActivityDAO;
import logic.dao.ReportDAO;
import logic.dao.StudentDAO;
import logic.exceptions.DAOException;
import logic.util.ReportAccessValidator;

public class GenerateReportGUIController implements Initializable {

    @FXML private Label studentNameLabel;
    @FXML private Label studentMatriculaLabel;
    @FXML private Label professorNameLabel;
    @FXML private Label responsibleNameLabel;
    @FXML private Label organizationNameLabel;
    @FXML private Label nrcSectionLabel;
    @FXML private Label projectNameLabel;
    @FXML private Label methodologyLabel;
    @FXML private Label objectiveLabel;
    @FXML private Label reportNumberLabel;
    @FXML private Label schoolPeriodLabel;
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private TextField periodTextField;
    @FXML private VBox monthlySection;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private TextField monthlyHoursField;
    @FXML private TextField directResponsibleField;
    @FXML private Label accumulatedHoursLabel;
    @FXML private Label missingHoursLabel;
    @FXML private VBox activitiesCheckboxContainer;
    @FXML private TextArea monthlyObservationsArea;
    @FXML private VBox partialSection;
    @FXML private Label partialCoveredHoursLabel;
    @FXML private TableView<ActivityAdvanceRow> partialActivitiesTable;
    @FXML private TableColumn<ActivityAdvanceRow, String> pActNameColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> pPlannedStartColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> pPlannedEndColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> pPlannedHoursColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> pCoveredWeeksColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> pProgressColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> pAdvanceObsColumn;
    @FXML private TableView<PerformanceCriterionRow> criteriaTable;
    @FXML private TableColumn<PerformanceCriterionRow, String> criterionColumn;
    @FXML private TableColumn<PerformanceCriterionRow, String> criterionLevelColumn;
    @FXML private VBox finalSection;
    @FXML private TableView<ActivityAdvanceRow> finalActivitiesTable;
    @FXML private TableColumn<ActivityAdvanceRow, String> fActNameColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> fPlannedStartColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> fPlannedEndColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> fPlannedHoursColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> fCoveredWeeksColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> fProgressColumn;
    @FXML private TableColumn<ActivityAdvanceRow, String> fAdvanceObsColumn;
    @FXML private TableView<DeliverableRow> deliverablesTable;
    @FXML private TableColumn<DeliverableRow, String> deliverableDescColumn;
    @FXML private TableColumn<DeliverableRow, String> deliverableProgressColumn;
    @FXML private TableColumn<DeliverableRow, String> deliverableObsColumn;
    @FXML private Label systemMessageLabel;

    private static final int TOTAL_HOURS = ReportAccessValidator.TOTAL_REPORT_HOURS;
    private static final int PARTIAL_HOURS = ReportAccessValidator.PARTIAL_REPORT_HOURS;

    private final ReportDAO reportDAO = new ReportDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ReportAccessValidator validator = new ReportAccessValidator();

    private ReportGeneralData generalData;
    private List<Activity> projectActivities;
    private double accumulatedHours = 0.0;
    private ObservableList<MonthlyActivityRow> monthlyRows = FXCollections.observableArrayList();

    private static class MonthlyActivityRow {
        final Activity activity;
        final CheckBox checkBox;
        final TextField weeksField;
        final TextArea observationsArea;
        final VBox detailBox;

        MonthlyActivityRow(Activity activity) {
            this.activity = activity;
            checkBox = new CheckBox(activity.getName());
            checkBox.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            weeksField = new TextField();
            weeksField.setPromptText("Ej. Semana 1 al 4 de Febrero");
            weeksField.setDisable(true);
            observationsArea = new TextArea();
            observationsArea.setPromptText("Observaciones sobre esta actividad...");
            observationsArea.setPrefRowCount(2);
            observationsArea.setWrapText(true);
            observationsArea.setDisable(true);
            Label weeksLabel = new Label("Semanas cubiertas:");
            weeksLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
            Label obsLabel = new Label("Observaciones:");
            obsLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
            detailBox = new VBox(6, weeksLabel, weeksField, obsLabel, observationsArea);
            detailBox.setPadding(new Insets(6, 0, 0, 20));
            detailBox.setVisible(false);
            detailBox.setManaged(false);
            checkBox.selectedProperty().addListener((obs, oldVal, selected) -> {
                detailBox.setVisible(selected);
                detailBox.setManaged(selected);
                weeksField.setDisable(!selected);
                observationsArea.setDisable(!selected);
            });
        }

        boolean isSelected() { return checkBox.isSelected(); }
        String getWeeks() { return weeksField.getText().trim(); }
        String getObs() { return observationsArea.getText().trim(); }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
            "MENSUAL", "PARCIAL", "FINAL"
        ));
        monthComboBox.setItems(FXCollections.observableArrayList(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        ));
        loadGeneralData();
        setupReportTypeListener();
    }

    private void loadGeneralData() {
        try {
            String matricula = SessionManager.getCurrentEnrollment();
            generalData = studentDAO.getReportGeneralData(matricula);
            accumulatedHours = reportDAO.getAccumulatedApprovedHours(matricula);
            projectActivities = activityDAO.getActivitiesByProject(generalData.getProjectId());

            int totalPreviousReports = reportDAO.getReportsByStudent(matricula).size();
            generalData.setReportNumber(totalPreviousReports + 1);

            studentNameLabel.setText(generalData.getStudentFullName());
            studentMatriculaLabel.setText(generalData.getStudentMatricula());
            professorNameLabel.setText(generalData.getProfessorFullName());
            responsibleNameLabel.setText(generalData.getTechnicalResponsibleFullName());
            organizationNameLabel.setText(generalData.getOrganizationName());
            nrcSectionLabel.setText(generalData.getNrc() + " / " + generalData.getSection());
            projectNameLabel.setText(generalData.getProjectName());
            methodologyLabel.setText(generalData.getProjectMethodology());
            objectiveLabel.setText(generalData.getProjectObjective());
            reportNumberLabel.setText("Informe No. " + generalData.getReportNumber());

            if (generalData.getEeStartDate() != null && generalData.getEeEndDate() != null) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");
                String period = generalData.getEeStartDate().format(fmt) 
                              + " — " 
                              + generalData.getEeEndDate().format(fmt);
                schoolPeriodLabel.setText(period);
            } else {
                schoolPeriodLabel.setText("Periodo no asignado o pendiente");
            }

            if (projectActivities == null || projectActivities.isEmpty()) {
                reportTypeComboBox.setDisable(true);
                showError("No hay actividades registradas en tu proyecto aún.");
            }

        } catch (DAOException e) {
            reportTypeComboBox.setDisable(true);
            showError("Error al cargar los datos del proyecto: " + e.getMessage());
        }
    }

    private void setupReportTypeListener() {
        reportTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            hideAllSections();
            switch (newVal) {
                case "MENSUAL" -> showMonthlySection();
                case "PARCIAL" -> {
                    if (validator.canAccessPartialReport()) {
                        showPartialSection();
                    } else {
                        reportTypeComboBox.setValue(null);
                    }
                }
                case "FINAL" -> {
                    if (validator.canAccessFinalReport()) {
                        showFinalSection();
                    } else {
                        reportTypeComboBox.setValue(null);
                    }
                }
            }
        });
    }

    private void hideAllSections() {
        monthlySection.setVisible(false);
        monthlySection.setManaged(false);
        partialSection.setVisible(false);
        partialSection.setManaged(false);
        finalSection.setVisible(false);
        finalSection.setManaged(false);
    }

    private void showMonthlySection() {
        monthlySection.setVisible(true);
        monthlySection.setManaged(true);
        accumulatedHoursLabel.setText((int) accumulatedHours + " hrs");
        double missing = Math.max(0, TOTAL_HOURS - accumulatedHours);
        missingHoursLabel.setText((int) missing + " hrs");
        buildMonthlyCheckboxList();
    }

    private void showPartialSection() {
        partialSection.setVisible(true);
        partialSection.setManaged(true);
        partialCoveredHoursLabel.setText((int) accumulatedHours + " hrs");
        setupPartialActivitiesTable();
        setupCriteriaTable();
    }

    private void showFinalSection() {
        finalSection.setVisible(true);
        finalSection.setManaged(true);
        setupFinalActivitiesTable();
        setupDeliverablesTable();
    }

    private void buildMonthlyCheckboxList() {
        activitiesCheckboxContainer.getChildren().clear();
        monthlyRows.clear();
        for (Activity activity : projectActivities) {
            MonthlyActivityRow row = new MonthlyActivityRow(activity);
            monthlyRows.add(row);
            VBox card = new VBox(4, row.checkBox, row.detailBox);
            card.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; "
                        + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");
            activitiesCheckboxContainer.getChildren().add(card);
        }
    }

    private void setupPartialActivitiesTable() {
        partialActivitiesTable.setEditable(true);
        ObservableList<ActivityAdvanceRow> rows = FXCollections.observableArrayList();
        for (Activity activity : projectActivities) {
            rows.add(new ActivityAdvanceRow(activity));
        }
        partialActivitiesTable.setItems(rows);
        bindActivityAdvanceColumns(
            pActNameColumn, pPlannedStartColumn, pPlannedEndColumn,
            pPlannedHoursColumn, pCoveredWeeksColumn, pProgressColumn,
            pAdvanceObsColumn, partialActivitiesTable
        );
    }

    private void setupFinalActivitiesTable() {
        finalActivitiesTable.setEditable(true);
        ObservableList<ActivityAdvanceRow> rows = FXCollections.observableArrayList();
        for (Activity activity : projectActivities) {
            rows.add(new ActivityAdvanceRow(activity));
        }
        finalActivitiesTable.setItems(rows);
        bindActivityAdvanceColumns(
            fActNameColumn, fPlannedStartColumn, fPlannedEndColumn,
            fPlannedHoursColumn, fCoveredWeeksColumn, fProgressColumn,
            fAdvanceObsColumn, finalActivitiesTable
        );
    }

    private void bindActivityAdvanceColumns(
            TableColumn<ActivityAdvanceRow, String> nameCol,
            TableColumn<ActivityAdvanceRow, String> startCol,
            TableColumn<ActivityAdvanceRow, String> endCol,
            TableColumn<ActivityAdvanceRow, String> hoursCol,
            TableColumn<ActivityAdvanceRow, String> weeksCol,
            TableColumn<ActivityAdvanceRow, String> progressCol,
            TableColumn<ActivityAdvanceRow, String> obsCol,
            TableView<ActivityAdvanceRow> table) {

        nameCol.setCellValueFactory(cell  -> cell.getValue().activityNameProperty());
        startCol.setCellValueFactory(cell -> cell.getValue().plannedStartProperty());
        endCol.setCellValueFactory(cell   -> cell.getValue().plannedEndProperty());
        hoursCol.setCellValueFactory(cell -> cell.getValue().plannedHoursProperty());

        weeksCol.setCellValueFactory(cell -> cell.getValue().coveredWeeksProperty());
        weeksCol.setCellFactory(TextFieldTableCell.forTableColumn());
        weeksCol.setOnEditCommit(e -> e.getRowValue().setCoveredWeeks(e.getNewValue()));

        progressCol.setCellValueFactory(cell -> cell.getValue().progressPercentageProperty().asString());
        progressCol.setCellFactory(TextFieldTableCell.forTableColumn());
        progressCol.setOnEditCommit(e -> {
            try {
                int value = Integer.parseInt(e.getNewValue());
                if (value >= 0 && value <= 100) {
                    e.getRowValue().setProgressPercentage(value);
                } else {
                    showError("El porcentaje debe estar entre 0 y 100.");
                    table.refresh();
                }
            } catch (NumberFormatException ex) {
                showError("Ingrese un número válido para el porcentaje.");
                table.refresh();
            }
        });

        obsCol.setCellValueFactory(cell -> cell.getValue().observationsProperty());
        obsCol.setCellFactory(TextFieldTableCell.forTableColumn());
        obsCol.setOnEditCommit(e -> e.getRowValue().setObservations(e.getNewValue()));
    }

    private void setupCriteriaTable() {
        criteriaTable.setEditable(false);
        criteriaTable.setItems(PerformanceCriterionRow.getDefaultCriteria());
        criterionColumn.setCellValueFactory(cell -> cell.getValue().criterionProperty());
        criterionLevelColumn.setCellValueFactory(cell -> cell.getValue().levelProperty());
    }

    private void setupDeliverablesTable() {
        deliverablesTable.setEditable(true);
        deliverablesTable.setItems(FXCollections.observableArrayList());

        deliverableDescColumn.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        deliverableDescColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        deliverableDescColumn.setOnEditCommit(e -> e.getRowValue().setDescription(e.getNewValue()));

        deliverableProgressColumn.setCellValueFactory(cell -> cell.getValue().progressPercentageProperty().asString());
        deliverableProgressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        deliverableProgressColumn.setOnEditCommit(e -> {
            try {
                int value = Integer.parseInt(e.getNewValue());
                if (value >= 0 && value <= 100) {
                    e.getRowValue().setProgressPercentage(value);
                } else {
                    showError("El porcentaje del entregable debe estar entre 0 y 100.");
                    deliverablesTable.refresh();
                }
            } catch (NumberFormatException ex) {
                showError("Ingrese un número válido para el porcentaje del entregable.");
                deliverablesTable.refresh();
            }
        });

        deliverableObsColumn.setCellValueFactory(cell -> cell.getValue().observationsProperty());
        deliverableObsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        deliverableObsColumn.setOnEditCommit(e -> e.getRowValue().setObservations(e.getNewValue()));
    }

    @FXML
    private void handleAddDeliverable() {
        if (deliverablesTable.getItems() != null) {
            deliverablesTable.getItems().add(new DeliverableRow());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        String reportType = reportTypeComboBox.getValue();
        try {
            Report newReport = buildReport(reportType);
            boolean isRegistered = reportDAO.registerReport(newReport);

            if (isRegistered) {
                showSuccess("Reporte guardado correctamente con estado PENDIENTE.");
                handleClear();
            } else {
                showError("No se pudo guardar el reporte. Intente de nuevo.");
            }
        } catch (DAOException e) {
            showError("Error al guardar el reporte: " + e.getMessage());
        }
    }

    @FXML
    private void handleGeneratePDF() {
        showError("Funcionalidad de generación de PDF en construcción.");
    }

    private boolean validateFields() {
        if (reportTypeComboBox.getValue() == null) {
            showError("Seleccione el tipo de reporte.");
            return false;
        }
        if (periodTextField.getText().trim().isEmpty()) {
            showError("Ingrese el período del reporte.");
            return false;
        }

        String type = reportTypeComboBox.getValue();

        if (type.equals("MENSUAL")) {
            return validateMonthlyFields();
        }
        if (type.equals("FINAL")) {
            return validateFinalFields();
        }
        return true;
    }

    private boolean validateMonthlyFields() {
        if (monthComboBox.getValue() == null) {
            showError("Seleccione el mes reportado.");
            return false;
        }
        if (monthlyHoursField.getText().trim().isEmpty()) {
            showError("Ingrese las horas elaboradas este mes.");
            return false;
        }
        if (directResponsibleField.getText().trim().isEmpty()) {
            showError("Ingrese el nombre del responsable directo.");
            return false;
        }
        try {
            double hours = Double.parseDouble(monthlyHoursField.getText().trim());
            if (hours <= 0) {
                showError("Las horas deben ser un valor positivo.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Las horas deben ser un número válido.");
            return false;
        }
        boolean anySelected = monthlyRows.stream().anyMatch(MonthlyActivityRow::isSelected);
        if (!anySelected) {
            showError("Selecciona al menos una actividad realizada este mes.");
            return false;
        }
        return true;
    }

    private boolean validateFinalFields() {
        for (ActivityAdvanceRow row : finalActivitiesTable.getItems()) {
            if (row.getProgressPercentage() < 100 && row.getObservations().trim().isEmpty()) {
                showError("La actividad \"" + row.getActivityName() + "\" no llegó al 100%. Las observaciones son obligatorias.");
                return false;
            }
        }
        if (deliverablesTable.getItems().isEmpty()) {
            showError("Agrega al menos un entregable en la tabla de productos comprometidos.");
            return false;
        }
        return true;
    }

    private Report buildReport(String reportType) {
        Student student = new Student();
        student.setMatricula(SessionManager.getCurrentEnrollment());

        Project project = new Project();
        project.setId(generalData.getProjectId());

        Report newReport = new Report();
        newReport.setTypeOfReport(reportType);
        newReport.setStudent(student);
        newReport.setProject(project);
        newReport.setDeliveryDate(LocalDate.now());
        newReport.setStatus("PENDIENTE");
        newReport.setUrl(null);

        if (reportType.equals("MENSUAL")) {
            newReport.setReportedHours(Double.parseDouble(monthlyHoursField.getText().trim()));
            for (MonthlyActivityRow row : monthlyRows) {
                if (!row.isSelected()) continue;
                ActivityAdvance advance = new ActivityAdvance();
                advance.setActivity(row.activity);
                advance.setProgressPercentage(0);
                advance.setCoveredWeeks(row.getWeeks());
                advance.setObservations(row.getObs());
                newReport.addActivityAdvance(advance);
            }
        } else {
            newReport.setReportedHours(accumulatedHours);
            ObservableList<ActivityAdvanceRow> tableItems = reportType.equals("PARCIAL")
                ? partialActivitiesTable.getItems()
                : finalActivitiesTable.getItems();

            for (ActivityAdvanceRow row : tableItems) {
                ActivityAdvance advance = new ActivityAdvance();
                advance.setActivity(row.getActivity());
                advance.setProgressPercentage(row.getProgressPercentage());
                advance.setCoveredWeeks(row.getCoveredWeeks());
                advance.setObservations(row.getObservations());
                newReport.addActivityAdvance(advance);
            }
        }
        return newReport;
    }

    @FXML
    private void handleClear() {
        reportTypeComboBox.setValue(null);
        periodTextField.clear();
        monthComboBox.setValue(null);
        monthlyHoursField.clear();
        directResponsibleField.clear();
        monthlyObservationsArea.clear();
        activitiesCheckboxContainer.getChildren().clear();
        monthlyRows.clear();
        hideAllSections();
        systemMessageLabel.setVisible(false);
    }

    private void showError(String message) {
        systemMessageLabel.setTextFill(Color.web("#d32f2f"));
        systemMessageLabel.setText(message);
        systemMessageLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        systemMessageLabel.setTextFill(Color.web("#2bbfaa"));
        systemMessageLabel.setText(message);
        systemMessageLabel.setVisible(true);
    }
}