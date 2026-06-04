/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Fila para la tabla de entregables del REPORTE FINAL.
 * Representa la sección "Resultados en término de productos comprometidos".
 * Es exclusiva del reporte final — ni el mensual ni el parcial la usan.
 *
 * Cada fila = un producto o entregable que el estudiante comprometió entregar.
 */
public class DeliverableRow {

    // Nombre o descripción del producto entregable (ej. "Manual de usuario")
    private final SimpleStringProperty  description        = new SimpleStringProperty("");

    // Porcentaje de avance del entregable (captura manual del estudiante, 0-100)
    private final SimpleIntegerProperty progressPercentage = new SimpleIntegerProperty(0);

    // Justificación obligatoria si el avance no llegó al 100%
    private final SimpleStringProperty  observations       = new SimpleStringProperty("");

    // Constructor vacío para crear filas en blanco en la tabla
    public DeliverableRow() {}

    // Constructor con datos para cargar entregables existentes desde BD
    public DeliverableRow(String description, int progress, String observations) {
        this.description.set(description);
        this.progressPercentage.set(progress);
        this.observations.set(observations);
    }

    // ── Getters, setters y properties para JavaFX ────────────────────────────

    public String getDescription() { return description.get(); }
    public void setDescription(String v) { description.set(v); }
    public SimpleStringProperty descriptionProperty() { return description; }

    public int getProgressPercentage() { return progressPercentage.get(); }
    public void setProgressPercentage(int v) { progressPercentage.set(v); }
    public SimpleIntegerProperty progressPercentageProperty() { return progressPercentage; }

    public String getObservations() { return observations.get(); }
    public void setObservations(String v) { observations.set(v); }
    public SimpleStringProperty observationsProperty() { return observations; }
}