/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;

import java.time.LocalDate;

/**
 * Contiene todos los datos generales que se muestran en la cabecera
 * de los tres tipos de reporte (Mensual, Parcial y Final).
 * El sistema jala esta información automáticamente del proyecto
 * y la inscripción del alumno — el estudiante no captura nada aquí.
 */
public class ReportGeneralData {

    // ── Datos del proyecto ────────────────────────────────────────────────────
    private int       projectId;
    private String    projectName;
    private String    projectObjective;
    private String    projectMethodology;

    // ── Datos de identificación ───────────────────────────────────────────────
    private String    organizationName;             // Nombre de la organización vinculada
    private String    studentFullName;              // Nombre completo del alumno
    private String    studentMatricula;             // Matrícula — para cabecera del PDF y firmas
    private String    professorFullName;            // Nombre del profesor de la EE
    private String    technicalResponsibleFullName; // Nombre del responsable técnico en la empresa

    // ── Datos de la Experiencia Educativa (EE) ────────────────────────────────
    private int       nrc;                          // NRC de la sección
    private String    section;                      // Sección (ej. "D01")
    private LocalDate eeStartDate;                  // Inicio del periodo escolar
    private LocalDate eeEndDate;                    // Fin del periodo escolar

    // ── Datos del documento ───────────────────────────────────────────────────
    // El sistema calcula reportNumber contando los reportes previos del alumno + 1
    private int       reportNumber;                 // Número de informe secuencial

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectObjective() { return projectObjective; }
    public void setProjectObjective(String projectObjective) { this.projectObjective = projectObjective; }

    public String getProjectMethodology() { return projectMethodology; }
    public void setProjectMethodology(String projectMethodology) { this.projectMethodology = projectMethodology; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getStudentFullName() { return studentFullName; }
    public void setStudentFullName(String studentFullName) { this.studentFullName = studentFullName; }

    public String getStudentMatricula() { return studentMatricula; }
    public void setStudentMatricula(String studentMatricula) { this.studentMatricula = studentMatricula; }

    public String getProfessorFullName() { return professorFullName; }
    public void setProfessorFullName(String professorFullName) { this.professorFullName = professorFullName; }

    public String getTechnicalResponsibleFullName() { return technicalResponsibleFullName; }
    public void setTechnicalResponsibleFullName(String technicalResponsibleFullName) {
        this.technicalResponsibleFullName = technicalResponsibleFullName;
    }

    public int getNrc() { return nrc; }
    public void setNrc(int nrc) { this.nrc = nrc; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public LocalDate getEeStartDate() { return eeStartDate; }
    public void setEeStartDate(LocalDate eeStartDate) { this.eeStartDate = eeStartDate; }

    public LocalDate getEeEndDate() { return eeEndDate; }
    public void setEeEndDate(LocalDate eeEndDate) { this.eeEndDate = eeEndDate; }

    public int getReportNumber() { return reportNumber; }
    public void setReportNumber(int reportNumber) { this.reportNumber = reportNumber; }
}