/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author gamal
 */

public class ActivityAdvanceRow {
    private final Activity             activity;
    private final SimpleStringProperty activityName;
    private final SimpleStringProperty plannedStart;
    private final SimpleStringProperty plannedEnd;
    private final SimpleStringProperty plannedHours;
    private final SimpleStringProperty coveredWeeks    = new SimpleStringProperty("");
    private final SimpleIntegerProperty progressPercentage = new SimpleIntegerProperty(0);
    private final SimpleStringProperty observations    = new SimpleStringProperty("");

    public ActivityAdvanceRow(Activity activity) {
        this.activity     = activity;
        this.activityName = new SimpleStringProperty(activity.getName());
        this.plannedStart = new SimpleStringProperty(
            activity.getStartDate() != null ? activity.getStartDate().toString() : "—");
        this.plannedEnd   = new SimpleStringProperty(
            activity.getEndDate() != null ? activity.getEndDate().toString() : "—");
        this.plannedHours = new SimpleStringProperty(
            String.valueOf(activity.getPlannedHours()));
    }

    public Activity getActivity() { return activity; }

    public String getActivityName() { return activityName.get(); }
    public SimpleStringProperty activityNameProperty() { return activityName; }

    public String getPlannedStart() { return plannedStart.get(); }
    public SimpleStringProperty plannedStartProperty() { return plannedStart; }

    public String getPlannedEnd() { return plannedEnd.get(); }
    public SimpleStringProperty plannedEndProperty() { return plannedEnd; }

    public String getPlannedHours() { return plannedHours.get(); }
    public SimpleStringProperty plannedHoursProperty() { return plannedHours; }

    public String getCoveredWeeks() { return coveredWeeks.get(); }
    public void setCoveredWeeks(String v) { coveredWeeks.set(v); }
    public SimpleStringProperty coveredWeeksProperty() { return coveredWeeks; }

    public int getProgressPercentage() { return progressPercentage.get(); }
    public void setProgressPercentage(int v) { progressPercentage.set(v); }
    public SimpleIntegerProperty progressPercentageProperty() { return progressPercentage; }

    public String getObservations() { return observations.get(); }
    public void setObservations(String v) { observations.set(v); }
    public SimpleStringProperty observationsProperty() { return observations; }
}