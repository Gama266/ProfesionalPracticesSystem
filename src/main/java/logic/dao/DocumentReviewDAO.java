/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.dao;

/**
 *
 * @author akyer
 */
import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.businessObject.DeliveryStatus;
import logic.businessObject.DocumentReview;
import logic.businessObject.ReviewStatus;
import logic.businessObject.Student;
import logic.businessObject.Teacher;
import logic.exceptions.DAOException;
import logic.idao.IDocumentReviewDAO;

/**
 * Implementación JDBC del repositorio de revisiones de documentos iniciales.
 *
 * Usa INSERT ... ON DUPLICATE KEY UPDATE para garantizar que cada combinación
 * (matricula, noPersonal, tipoDocumento) tenga exactamente una revisión.
 */
public class DocumentReviewDAO implements IDocumentReviewDAO {

    private static final Logger LOG = Logger.getLogger(DocumentReviewDAO.class.getName());

    // ── SQL constants ─────────────────────────────────────────────────────────

    private static final String SQL_UPSERT =
        "INSERT INTO revision_documento " +
        "  (matricula, noPersonal, tipoDocumento, idDocumento, " +
        "   estadoEntrega, estadoRevision, justificacion, fechaRevision) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "  idDocumento    = VALUES(idDocumento),  " +
        "  estadoEntrega  = VALUES(estadoEntrega), " +
        "  estadoRevision = VALUES(estadoRevision), " +
        "  justificacion  = VALUES(justificacion), " +
        "  fechaRevision  = VALUES(fechaRevision)";

    private static final String SQL_BY_STUDENT_AND_TEACHER =
        "SELECT rd.idRevision, rd.matricula, rd.noPersonal, rd.tipoDocumento, " +
        "       rd.idDocumento, rd.estadoEntrega, rd.estadoRevision, " +
        "       rd.justificacion, rd.fechaRevision, " +
        "       fi.URL AS documentUrl, " +
        "       p.nombre, p.apellidoPaterno, p.apellidoMaterno " +
        "FROM revision_documento rd " +
        "LEFT JOIN formatosiniciales fi ON rd.idDocumento = fi.idDocumentos " +
        "LEFT JOIN practicante p ON rd.matricula = p.matricula " +
        "WHERE rd.matricula = ? AND rd.noPersonal = ? " +
        "ORDER BY rd.tipoDocumento";

    private static final String SQL_BY_STUDENT =
        "SELECT rd.idRevision, rd.matricula, rd.noPersonal, rd.tipoDocumento, " +
        "       rd.idDocumento, rd.estadoEntrega, rd.estadoRevision, " +
        "       rd.justificacion, rd.fechaRevision, " +
        "       fi.URL AS documentUrl, " +
        "       pr.nombre AS profNombre, pr.apellidoPaterno AS profPaterno " +
        "FROM revision_documento rd " +
        "LEFT JOIN formatosiniciales fi ON rd.idDocumento = fi.idDocumentos " +
        "LEFT JOIN profesor pr ON rd.noPersonal = pr.numeroPersonal " +
        "WHERE rd.matricula = ? " +
        "ORDER BY rd.tipoDocumento, rd.noPersonal";

    // ── Public methods ────────────────────────────────────────────────────────

    @Override
    public boolean upsertReview(DocumentReview review) throws DAOException {
        validateReview(review);

        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPSERT)) {

            ps.setString(1, review.getStudent().getMatricula());
            ps.setInt   (2, review.getTeacher().getNoPersonal());
            ps.setString(3, review.getDocumentType());

            if (review.getIdDocument() != null) {
                ps.setInt(4, review.getIdDocument());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, review.getDeliveryStatus().toDb());
            ps.setString(6, review.getReviewStatus().toDb());
            ps.setString(7, review.getJustification());

            if (review.getReviewDate() != null) {
                ps.setDate(8, Date.valueOf(review.getReviewDate()));
            } else {
                ps.setNull(8, Types.DATE);
            }

            int rows = ps.executeUpdate();
            LOG.info("upsertReview (documento): filas afectadas=" + rows +
                     ", matricula=" + review.getStudent().getMatricula() +
                     ", tipo=" + review.getDocumentType());
            return rows > 0;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al guardar revisión de documento", e);
            throw new DAOException("Error al guardar la revisión del documento", e);
        }
    }

    @Override
    public List<DocumentReview> getReviewsByStudentAndTeacher(
            String matricula, int noPersonal) throws DAOException {

        List<DocumentReview> reviews = new ArrayList<>();

        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_BY_STUDENT_AND_TEACHER)) {

            ps.setString(1, matricula);
            ps.setInt   (2, noPersonal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapRowFull(rs, false));
                }
            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al consultar revisiones de documento", e);
            throw new DAOException("Error al consultar revisiones de documentos", e);
        }
        return reviews;
    }

    @Override
    public List<DocumentReview> getReviewsByStudent(String matricula) throws DAOException {
        List<DocumentReview> reviews = new ArrayList<>();

        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_BY_STUDENT)) {

            ps.setString(1, matricula);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapRowFull(rs, true));
                }
            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al consultar revisiones del estudiante", e);
            throw new DAOException("Error al consultar tus revisiones de documentos", e);
        }
        return reviews;
    }


    private DocumentReview mapRowFull(ResultSet rs, boolean includeTeacher) throws SQLException {
        DocumentReview review = new DocumentReview();
        review.setId(rs.getInt("idRevision"));
        review.setDocumentType(rs.getString("tipoDocumento"));
        review.setDeliveryStatus(DeliveryStatus.fromDb(rs.getString("estadoEntrega")));
        review.setReviewStatus(ReviewStatus.fromDb(rs.getString("estadoRevision")));
        review.setJustification(rs.getString("justificacion"));

        Date fechaRs = rs.getDate("fechaRevision");
        if (fechaRs != null) {
            review.setReviewDate(fechaRs.toLocalDate());
        }

        int idDoc = rs.getInt("idDocumento");
        if (!rs.wasNull()) {
            review.setIdDocument(idDoc);
        }
        review.setDocumentUrl(rs.getString("documentUrl"));

        // Estudiante
        Student student = new Student();
        student.setMatricula(rs.getString("matricula"));
        if (!includeTeacher) {
            // en vista profesor ya se conoce el alumno por la selección
            try {
                student.setName(rs.getString("nombre"));
                student.setPaternalSurname(rs.getString("apellidoPaterno"));
                student.setMaternalSurname(rs.getString("apellidoMaterno"));
            } catch (SQLException ignored) { /* columnas opcionales */ }
        }
        review.setStudent(student);

        // Profesor
        Teacher teacher = new Teacher();
        teacher.setNoPersonal(rs.getInt("noPersonal"));
        if (includeTeacher) {
            try {
                teacher.setName(rs.getString("profNombre"));
                teacher.setPaternalSurname(rs.getString("profPaterno"));
            } catch (SQLException ignored) {}
        }
        review.setTeacher(teacher);

        return review;
    }

    private void validateReview(DocumentReview review) throws DAOException {
        if (review == null)
            throw new DAOException("La revisión no puede ser nula", null);
        if (review.getStudent() == null || review.getStudent().getMatricula() == null)
            throw new DAOException("La revisión debe tener un estudiante válido", null);
        if (review.getTeacher() == null)
            throw new DAOException("La revisión debe tener un profesor válido", null);
        if (review.getDocumentType() == null || review.getDocumentType().isBlank())
            throw new DAOException("El tipo de documento no puede estar vacío", null);
    }
}

