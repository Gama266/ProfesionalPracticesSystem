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
public class ReportReview {
    private int            id;
    private Student        student;
    private Teacher        teacher;
    private String         reportType;      
    private Integer        idReport;        
    private String         reportUrl;       
    private DeliveryStatus deliveryStatus;
    private ReviewStatus   reviewStatus;
    private String         justification;
    private LocalDate      reviewDate;

    public ReportReview() {
        this.deliveryStatus = DeliveryStatus.NO_ENTREGADO;
        this.reviewStatus   = ReviewStatus.PENDIENTE;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public Integer getIdReport() { return idReport; }
    public void setIdReport(Integer idReport) { this.idReport = idReport; }

    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }

    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public ReviewStatus getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(ReviewStatus reviewStatus) { this.reviewStatus = reviewStatus; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }

}
