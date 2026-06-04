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
import logic.businessObject.ReportReview;
import logic.businessObject.ReviewStatus;
import logic.businessObject.Student;
import logic.businessObject.Teacher;
import logic.exceptions.DAOException;
import logic.idao.IReportReviewDAO;

/**
 * Implementación JDBC del repositorio de revisiones de reportes firmados.
 *
 * Referencias a tablas reales de BD:
 *   - revision_reporte  (nueva, creada por migration_reviews.sql)
 *   - reportes          (tabla real: idReporte, URL, tipoReporte, matricula)
 *   - practicante       (tabla real de alumnos)
 *   - profesor          (tabla real de profesores)
 */
public class ReportReviewDAO implements IReportReviewDAO {

    private static final Logger LOG = Logger.getLogger(ReportReviewDAO.class.getName());

    // ── SQL constants ─────────────────────────────────────────────────────────

    private static final String SQL_UPSERT =
        "INSERT INTO revision_reporte " +
        "  (matricula, noPersonal, tipoReporte, idReporte, " +
        "   estadoEntrega, estadoRevision, justificacion, fechaRevision) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "  idReporte      = VALUES(idReporte),     " +
        "  estadoEntrega  = VALUES(estadoEntrega),  " +
        "  estadoRevision = VALUES(estadoRevision), " +
        "  justificacion  = VALUES(justificacion),  " +
        "  fechaRevision  = VALUES(fechaRevision)";

    /**
     * Vista del profesor: lista los reportes de un alumno con sus revisiones.
     * JOIN a reportes (no a 'Reporte') — columna URL en mayúsculas.
     */
    private static final String SQL_BY_STUDENT_AND_TEACHER =
        "SELECT rr.idRevision, rr.matricula, rr.noPersonal, rr.tipoReporte, " +
        "       rr.idReporte, rr.estadoEntrega, rr.estadoRevision, " +
        "       rr.justificacion, rr.fechaRevision, " +
        "       rep.URL AS reportUrl, " +
        "       p.nombre, p.apellidoPaterno, p.apellidoMaterno " +
        "FROM revision_reporte rr " +
        "LEFT JOIN reportes rep ON rr.idReporte = rep.idReporte " +   // tabla: reportes, PK: idReporte
        "LEFT JOIN practicante p ON rr.matricula = p.matricula " +    // tabla: practicante (no students)
        "WHERE rr.matricula = ? AND rr.noPersonal = ? " +
        "ORDER BY rr.tipoReporte";

    /**
     * Vista del alumno: lista todas sus revisiones de reportes (todos los profesores).
     */
    private static final String SQL_BY_STUDENT =
        "SELECT rr.idRevision, rr.matricula, rr.noPersonal, rr.tipoReporte, " +
        "       rr.idReporte, rr.estadoEntrega, rr.estadoRevision, " +
        "       rr.justificacion, rr.fechaRevision, " +
        "       rep.URL AS reportUrl, " +
        "       pr.nombre AS profNombre, pr.apellidoPaterno AS profPaterno " +
        "FROM revision_reporte rr " +
        "LEFT JOIN reportes rep ON rr.idReporte = rep.idReporte " +
        "LEFT JOIN profesor pr  ON rr.noPersonal = pr.numeroPersonal " +
        "WHERE rr.matricula = ? " +
        "ORDER BY rr.tipoReporte, rr.noPersonal";

    // ── Public methods ────────────────────────────────────────────────────────

    @Override
    public boolean upsertReview(ReportReview review) throws DAOException {
        validateReview(review);

        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPSERT)) {

            ps.setString(1, review.getStudent().getMatricula());
            ps.setInt   (2, review.getTeacher().getNoPersonal());
            ps.setString(3, review.getReportType());

            if (review.getIdReport() != null) {
                ps.setInt(4, review.getIdReport());
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
            LOG.info("upsertReview (reporte): rows=" + rows
                    + ", matricula=" + review.getStudent().getMatricula()
                    + ", tipo=" + review.getReportType());
            return rows > 0;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al guardar revisión de reporte", e);
            throw new DAOException("Error al guardar la revisión del reporte", e);
        }
    }

    @Override
    public List<ReportReview> getReviewsByStudentAndTeacher(
            String matricula, int noPersonal) throws DAOException {

        List<ReportReview> reviews = new ArrayList<>();

        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_BY_STUDENT_AND_TEACHER)) {

            ps.setString(1, matricula);
            ps.setInt   (2, noPersonal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) reviews.add(mapRow(rs, false));
            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al consultar revisiones de reporte", e);
            throw new DAOException("Error al consultar revisiones de reportes", e);
        }
        return reviews;
    }

    @Override
    public List<ReportReview> getReviewsByStudent(String matricula) throws DAOException {
        List<ReportReview> reviews = new ArrayList<>();

        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_BY_STUDENT)) {

            ps.setString(1, matricula);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) reviews.add(mapRow(rs, true));
            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al consultar revisiones del alumno", e);
            throw new DAOException("Error al consultar tus revisiones de reportes", e);
        }
        return reviews;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ReportReview mapRow(ResultSet rs, boolean includeTeacher) throws SQLException {
        ReportReview review = new ReportReview();
        review.setId(rs.getInt("idRevision"));
        review.setReportType(rs.getString("tipoReporte"));
        review.setDeliveryStatus(DeliveryStatus.fromDb(rs.getString("estadoEntrega")));
        review.setReviewStatus(ReviewStatus.fromDb(rs.getString("estadoRevision")));
        review.setJustification(rs.getString("justificacion"));

        Date fechaRs = rs.getDate("fechaRevision");
        if (fechaRs != null) review.setReviewDate(fechaRs.toLocalDate());

        int idRep = rs.getInt("idReporte");
        if (!rs.wasNull()) review.setIdReport(idRep);
        review.setReportUrl(rs.getString("reportUrl"));   // alias de URL

        Student student = new Student();
        student.setMatricula(rs.getString("matricula"));
        if (!includeTeacher) {
            try {
                student.setName(rs.getString("nombre"));
                student.setPaternalSurname(rs.getString("apellidoPaterno"));
                student.setMaternalSurname(rs.getString("apellidoMaterno"));
            } catch (SQLException ignored) {}
        }
        review.setStudent(student);

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

    private void validateReview(ReportReview review) throws DAOException {
        if (review == null)
            throw new DAOException("La revisión no puede ser nula", null);
        if (review.getStudent() == null || review.getStudent().getMatricula() == null)
            throw new DAOException("La revisión debe tener un estudiante válido", null);
        if (review.getTeacher() == null)
            throw new DAOException("La revisión debe tener un profesor válido", null);
        if (review.getReportType() == null || review.getReportType().isBlank())
            throw new DAOException("El tipo de reporte no puede estar vacío", null);
    }
}
