/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;

import javafx.beans.property.SimpleStringProperty;

/**
 * Fila para la tabla de evaluación de desempeño del REPORTE PARCIAL.
 * Es exclusiva del reporte parcial — el formato PRAIS-P-02 la requiere.
 *
 * Cada fila = un criterio cualitativo que evalúa el desempeño del estudiante.
 * Los 4 criterios son fijos (definidos por la institución), no los captura el alumno.
 * Lo que el alumno llena es únicamente la columna de observaciones.
 */
public class PerformanceCriterionRow {

    // Descripción del criterio (viene predefinida, no la edita el alumno)
    private final SimpleStringProperty criterion    = new SimpleStringProperty("");

    // Nivel de cumplimiento: "Excelente", "Bueno", "Regular", "Deficiente"
    private final SimpleStringProperty level        = new SimpleStringProperty("");

    // Observaciones opcionales sobre ese criterio
    private final SimpleStringProperty observations = new SimpleStringProperty("");

    /**
     * Constructor que recibe el criterio predefinido.
     * Se llama al inicializar la tabla con los 4 criterios institucionales.
     */
    public PerformanceCriterionRow(String criterion) {
        this.criterion.set(criterion);
    }

    // ── Getters, setters y properties para JavaFX ────────────────────────────

    public String getCriterion() { return criterion.get(); }
    public SimpleStringProperty criterionProperty() { return criterion; }
    // El criterio no tiene setter porque es de solo lectura (viene predefinido)

    public String getLevel() { return level.get(); }
    public void setLevel(String v) { level.set(v); }
    public SimpleStringProperty levelProperty() { return level; }

    public String getObservations() { return observations.get(); }
    public void setObservations(String v) { observations.set(v); }
    public SimpleStringProperty observationsProperty() { return observations; }

    /**
     * Devuelve los 4 criterios institucionales predefinidos listos para
     * cargar en la tabla. Llama esto desde el controller al mostrar el parcial:
     *
     *   activitiesTable.setItems(PerformanceCriterionRow.getDefaultCriteria());
     */
    public static javafx.collections.ObservableList<PerformanceCriterionRow> getDefaultCriteria() {
        return javafx.collections.FXCollections.observableArrayList(
            new PerformanceCriterionRow("Aporte de ideas para la toma de decisiones en la solución"),
            new PerformanceCriterionRow("Organización en el desarrollo del trabajo"),
            new PerformanceCriterionRow("Aplicación de conocimientos teórico-prácticos"),
            new PerformanceCriterionRow("Realizó las actividades encomendadas correctamente")
        );
    }
}