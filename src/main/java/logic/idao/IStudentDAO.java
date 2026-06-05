/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;

import java.util.List;
import logic.businessObject.ReportGeneralData;
import logic.businessObject.Student;
import logic.exceptions.DAOException;

/**
 *
 * @author gamal
 */
public interface IStudentDAO {
    boolean registerStudent(Student newStudent) throws DAOException;
    boolean updateStudent(Student student) throws DAOException;
    boolean deleteStudent(String matricula) throws DAOException;
    boolean deactivateStudent(String matricula) throws DAOException;
    List<Student> getAllStudents() throws DAOException;
    List<Student> getStudentsWithPendingRequests() throws DAOException;
Student getStudentByIdUser(int idUser) throws DAOException;
 int getProjectIdByEnrollment(String enrollment)throws DAOException ;
 ReportGeneralData getReportGeneralData(String matricula) throws DAOException;
 boolean assignEducationalExperience(String matricula, int nrc, int numeroInscripcion) throws DAOException;

}
