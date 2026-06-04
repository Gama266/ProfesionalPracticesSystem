/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.idao;

import java.util.List;
import logic.businessObject.DocumentReview;
import logic.exceptions.DAOException;

/**
 *
 * @author akyer
 */
public interface IDocumentReviewDAO {

    /**
     *  Inserts or updates a document's revision.
     *  Uses INSERT ... ON DUPLICATE KEY UPDATE to be idempotent.
     *
     * @param review Object containing the revision data.
     * @return true if the operation affected at least one row.
     * @throws DAOException if a database error occurs.
     */
    boolean upsertReview(DocumentReview review) throws DAOException;

    /**
     * Gets all revisions for a student under a specific professor.
     *
     * @param matricula Student's student ID.
     * @param noPersonal Professor's employee number.
     * @return List of revisions (may include unsubmitted types).
     * @throws DAOException if a database error occurs.
     */
    List<DocumentReview> getReviewsByStudentAndTeacher(String matricula, int noPersonal)
            throws DAOException;

    /**
     * Gets all reviews for a student (student's own view).
     *
     * @param matricula Student's registration number.
     * @return List of reviews from all teachers.
     * @throws DAOException if a database error occurs.
     */
    List<DocumentReview> getReviewsByStudent(String matricula) throws DAOException;
}
