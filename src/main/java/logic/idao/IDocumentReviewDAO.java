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


    boolean upsertReview(DocumentReview review) throws DAOException;

   
    List<DocumentReview> getReviewsByStudentAndTeacher(String matricula, int noPersonal)
            throws DAOException;

    List<DocumentReview> getReviewsByStudent(String matricula) throws DAOException;
}
