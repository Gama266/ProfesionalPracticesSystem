/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;

/**
 *
 * @author gamal
 */

public class ActivityAdvance {
    
    private int id;
    private int idReport;
    private Activity activity; 
    private int progressPercentage;
    private String coveredWeeks;
    private String observations;

    public ActivityAdvance() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdReport() {
        return idReport;
    }

    public void setIdReport(int idReport) {
        this.idReport = idReport;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getCoveredWeeks() {
        return coveredWeeks;
    }

    public void setCoveredWeeks(String coveredWeeks) {
        this.coveredWeeks = coveredWeeks;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}