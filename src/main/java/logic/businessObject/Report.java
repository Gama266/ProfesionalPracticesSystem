/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import logic.businessObject.ActivityAdvance;

/**
 *
 * @author gamal
 */
public class Report {
    
    private int id;
    private String typeOfReport; 
    private Student student;     
    private Project project;    
    private double reportedHours;
    private LocalDate deliveryDate;
    private String status;     
    private String url;
    
    private List<ActivityAdvance> activityAdvances;

    public Report() {
        this.activityAdvances = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeOfReport() {
        return typeOfReport;
    }

    public void setTypeOfReport(String typeOfReport) {
        this.typeOfReport = typeOfReport;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public double getReportedHours() {
        return reportedHours;
    }

    public void setReportedHours(double reportedHours) {
        this.reportedHours = reportedHours;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ActivityAdvance> getActivityAdvances() {
        return activityAdvances;
    }

    public void setActivityAdvances(List<ActivityAdvance> activityAdvances) {
        this.activityAdvances = activityAdvances;
    }
    
    public void addActivityAdvance(ActivityAdvance advance) {
        this.activityAdvances.add(advance);
    }
}