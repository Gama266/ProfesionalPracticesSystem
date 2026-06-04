/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;

import java.time.LocalDate;

/**
 *
 * @author akyer
 */
public class EducationalExperience {
    private int nrc;
    private LocalDate startDate;
    private LocalDate endDate;
    private String section;

    public EducationalExperience() {
    }

    public int getNrc() {
        return nrc;
    }

    public void setNrc(int nrc) {
        this.nrc = nrc;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

   
    public String getSection() {
        return section;
    }

 
    public void setSection(String section) {
        this.section = section;
    }
}