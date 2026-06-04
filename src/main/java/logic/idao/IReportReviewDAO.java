package logic.idao;

import java.util.List;
import logic.businessObject.ReportReview;
import logic.exceptions.DAOException;

/**
 *
 * @author akyer
 */

public interface IReportReviewDAO {

    /**
     * Inserts or updates the revision of a report.
     *
     * @param review Object with the revision data.
     * @return true if the operation affected at least one row.
     * @throws DAOException if a database error occurs.
     */
    boolean upsertReview(ReportReview review) throws DAOException;

    List<ReportReview> getReviewsByStudentAndTeacher(String matricula, int noPersonal)
            throws DAOException;

    List<ReportReview> getReviewsByStudent(String matricula) throws DAOException;
}

