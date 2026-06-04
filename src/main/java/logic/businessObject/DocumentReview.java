package logic.businessObject;

import java.time.LocalDate;

/**
 *
 * @author akyer
 */
public class DocumentReview {
    private int            id;
    private Student        student;
    private Teacher        teacher;
    private String         documentType;    // e.g., "Carta de Aceptación"
    private Integer        idDocument;      // FK a formatosiniciales (null si no entregado)
    private String         documentUrl;     // ruta del archivo, para visualización
    private DeliveryStatus deliveryStatus;
    private ReviewStatus   reviewStatus;
    private String         justification;
    private LocalDate      reviewDate;

    public DocumentReview() {
        this.deliveryStatus = DeliveryStatus.NO_ENTREGADO;
        this.reviewStatus   = ReviewStatus.PENDIENTE;
    }

    // ── Getters / Setters ─────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public Integer getIdDocument() { return idDocument; }
    public void setIdDocument(Integer idDocument) { this.idDocument = idDocument; }

    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }

    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public ReviewStatus getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(ReviewStatus reviewStatus) { this.reviewStatus = reviewStatus; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }

}
