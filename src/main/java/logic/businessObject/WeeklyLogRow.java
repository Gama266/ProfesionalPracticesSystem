/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author gamal
 */



public class WeeklyLogRow {
    private final SimpleStringProperty         weekRange    = new SimpleStringProperty("");
    private final SimpleObjectProperty<Activity> activity   = new SimpleObjectProperty<>(null);
    private final SimpleStringProperty         observations = new SimpleStringProperty("");

    public String getWeekRange() { return weekRange.get(); }
    public void setWeekRange(String v) { weekRange.set(v); }
    public SimpleStringProperty weekRangeProperty() { return weekRange; }

    public Activity getActivity() { return activity.get(); }
    public void setActivity(Activity v) { activity.set(v); }
    public SimpleObjectProperty<Activity> activityProperty() { return activity; }

    public String getObservations() { return observations.get(); }
    public void setObservations(String v) { observations.set(v); }
    public SimpleStringProperty observationsProperty() { return observations; }
}