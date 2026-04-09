/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;

import java.time.LocalDate;

/**
 *
 * @author gamal
 */
public class Project {
        private int id;
    private String name;
    private String description;
    private String metodology;
    private boolean activityStatus;
    private String objective;
    private LocalDate registrationDate;
    
    private LinkedOrganization linkedOrganization;
    private Teacher teacher;

    public Project() {
    }

            
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetodology() {
        return metodology;
    }

    public void setMetodology(String metodology) {
        this.metodology = metodology;
    }

    public boolean isActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(boolean activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LinkedOrganization getLinjedOrganization() {
        return linkedOrganization;
    }

    public void setLinjedOrganization(LinkedOrganization linjedOrganization) {
        this.linkedOrganization = linjedOrganization;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    
}


