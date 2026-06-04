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
    private String methodology; 
    private boolean activityStatus;
    private String objective;
    private LocalDate registrationDate;
    
    private LinkedOrganization linkedOrganization;
    private Teacher teacher;
    private TechnicalResponsible technicalResponsible;
    private int availableSpaces;

public int getAvailableSpaces() {
    return availableSpaces;
}

public void setAvailableSpaces(int availableSpaces) {
    this.availableSpaces = availableSpaces;
}

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

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public boolean getActivityStatus() {
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

    public LinkedOrganization getLinkedOrganization() {
        return linkedOrganization;
    }

    public void setLinkedOrganization(LinkedOrganization linkedOrganization) { 
        this.linkedOrganization = linkedOrganization;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

  
    public TechnicalResponsible getTechnicalResponsible() {
        return technicalResponsible;
    }

    public void setTechnicalResponsible(TechnicalResponsible technicalResponsible) {
        this.technicalResponsible = technicalResponsible;
    }
}