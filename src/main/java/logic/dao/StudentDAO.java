/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



package logic.dao;

import dataacces.ConfigDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import logic.businessObject.Project;
import logic.businessObject.ReportGeneralData;
import logic.businessObject.Student;
import logic.exceptions.DAOException;
import logic.idao.IStudentDAO;

/**
 *
 * * @author gamal
 */

public class StudentDAO implements IStudentDAO {

    @Override
    public boolean registerStudent(Student newStudent) throws DAOException {
        String queryRegisterStudent =
            "INSERT INTO practicante (matricula, nombre, apellidoPaterno, apellidoMaterno, estadoActividad, idUsuario, idProyecto) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = ConfigDatabase.getConnection();
            try (PreparedStatement stamentRegisterStudent =
                     connection.prepareStatement(queryRegisterStudent)) {

                stamentRegisterStudent.setString(1, newStudent.getMatricula());
                stamentRegisterStudent.setString(2, newStudent.getName());
                stamentRegisterStudent.setString(3, newStudent.getPaternalSurname());
                stamentRegisterStudent.setString(4, newStudent.getMaternalSurname());

                String status = newStudent.getActivityStatus() ? "Activo" : "Inactivo";
                stamentRegisterStudent.setString(5, status);

                stamentRegisterStudent.setInt(6, newStudent.getIdUser());

                if (newStudent.getProject() != null) {
                    stamentRegisterStudent.setInt(7, newStudent.getProject().getId());
                } else {
                    stamentRegisterStudent.setNull(7, Types.INTEGER);
                }

                int rowsAffected = stamentRegisterStudent.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error registrando al estudiante: " + exceptionDB.getMessage(), exceptionDB);
        }
    }

    @Override
    public boolean updateStudent(Student student) throws DAOException {
        String queryUpdateStudent =
            "UPDATE practicante SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, estadoActividad = ?, idProyecto = ? WHERE matricula = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();
            try (PreparedStatement stamentUpdateStudent =
                         connection.prepareStatement(queryUpdateStudent)) {

                stamentUpdateStudent.setString(1, student.getName());
                stamentUpdateStudent.setString(2, student.getPaternalSurname());
                stamentUpdateStudent.setString(3, student.getMaternalSurname());

                String status = student.getActivityStatus() ? "Activo" : "Inactivo";
                stamentUpdateStudent.setString(4, status);

                if (student.getProject() != null) {
                    stamentUpdateStudent.setInt(6, student.getProject().getId());
                } else {
                    stamentUpdateStudent.setNull(6, Types.INTEGER);
                }

                stamentUpdateStudent.setString(7, student.getMatricula());

                int rowsAffected = stamentUpdateStudent.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error actualizando las modificaciones", exceptionDB);
        }
    }

    @Override
    public boolean deleteStudent(String matricula) throws DAOException {
        String queryDeleteStudent = "DELETE FROM practicante WHERE matricula = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();
            try (PreparedStatement statementDeleteStudent =
                         connection.prepareStatement(queryDeleteStudent)) {

                statementDeleteStudent.setString(1, matricula);
                int rowsAffected = statementDeleteStudent.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error eliminando al estudiante", exceptionDB);
        }
    }

    @Override
    public boolean deactivateStudent(String matricula) throws DAOException {
        String queryDesactiveStudent =
                "UPDATE practicante SET estadoActividad = 'Inactivo' WHERE matricula = ?";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryDesactiveStudent)) {

            preparedStatement.setString(1, matricula);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException("Error al inactivar al estudiante", e);
        }
    }

    @Override
    public List<Student> getAllStudents() throws DAOException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM practicante";

        try {
            Connection connection = ConfigDatabase.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Student student = new Student();
                student.setMatricula(resultSet.getString("matricula"));
                student.setName(resultSet.getString("nombre"));
                student.setPaternalSurname(resultSet.getString("apellidoPaterno"));
                student.setMaternalSurname(resultSet.getString("apellidoMaterno"));

                String statusString = resultSet.getString("estadoActividad");
                boolean isActive = "Activo".equals(statusString);
                student.setActivityStatus(isActive);

                student.setIdUser(resultSet.getInt("idUsuario"));
                students.add(student);
            }

        } catch (SQLException exceptionDB) {
            throw new DAOException("Error obteniendo estudiantes", exceptionDB);
        }

        return students;
    }

    @Override
    public List<Student> getStudentsWithPendingRequests() throws DAOException {
        String queryGetStudentsWithPending =
            "SELECT DISTINCT p.matricula, p.nombre, p.apellidoPaterno, p.apellidoMaterno, " +
            "p.estadoActividad, p.idUsuario " +
            "FROM practicante p " +
            "INNER JOIN solicitud s ON p.matricula = s.matricula " +
            "WHERE s.estado = 'Pendiente'";

        List<Student> students = new ArrayList<>();

        try {
            Connection connection = ConfigDatabase.getConnection();
            try (PreparedStatement statementGetStudents =
                         connection.prepareStatement(queryGetStudentsWithPending);
                 ResultSet resultSet = statementGetStudents.executeQuery()) {

                while (resultSet.next()) {
                    Student student = new Student();
                    student.setMatricula(resultSet.getString("matricula"));
                    student.setName(resultSet.getString("nombre"));
                    student.setPaternalSurname(resultSet.getString("apellidoPaterno"));
                    student.setMaternalSurname(resultSet.getString("apellidoMaterno"));
                    student.setActivityStatus("Activo".equals(resultSet.getString("estadoActividad")));
                    student.setIdUser(resultSet.getInt("idUsuario"));
                    students.add(student);
                }
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al obtener estudiantes con solicitudes pendientes", exceptionDB);
        }

        return students;
    }

    @Override
    public Student getStudentByIdUser(int idUser) throws DAOException {
        String queryGetStudent =
            "SELECT p.matricula, p.nombre, p.apellidoPaterno, p.apellidoMaterno, p.estadoActividad, p.idUsuario, " +
            "pr.idProyecto, pr.nombre AS nombreProyecto " +
            "FROM practicante p " +
            "LEFT JOIN Proyecto pr ON p.idProyecto = pr.idProyecto " +
            "WHERE p.idUsuario = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();
            try (PreparedStatement statementGetStudent = connection.prepareStatement(queryGetStudent)) {
                statementGetStudent.setInt(1, idUser);
                try (ResultSet resultSet = statementGetStudent.executeQuery()) {
                    if (resultSet.next()) {
                        return mapResultSetToStudent(resultSet);
                    }
                }
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al obtener el estudiante", exceptionDB);
        }
        throw new DAOException("No se encontró estudiante para idUsuario: " + idUser, null);
    }

    private Student mapResultSetToStudent(ResultSet resultSet) throws SQLException {
        Student student = new Student();
        student.setMatricula(resultSet.getString("matricula"));
        student.setName(resultSet.getString("nombre"));
        student.setPaternalSurname(resultSet.getString("apellidoPaterno"));
        student.setMaternalSurname(resultSet.getString("apellidoMaterno"));
        student.setActivityStatus("Activo".equals(resultSet.getString("estadoActividad")));
        student.setIdUser(resultSet.getInt("idUsuario"));

        int idProyecto = resultSet.getInt("idProyecto");
        if (!resultSet.wasNull()) {
            Project project = new Project();
            project.setId(idProyecto);
            project.setName(resultSet.getString("nombreProyecto"));
            student.setProject(project);
        }

        return student;
    }

    @Override
    public int getProjectIdByEnrollment(String enrollment) throws DAOException {
        String query =
            "SELECT idProyecto " +
            "FROM practicante " +
            "WHERE matricula = ?";

        try (Connection connection = ConfigDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, enrollment);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("idProyecto");
                }
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error obtaining project ID", exceptionDB);
        }

        return -1;
    }

    @Override
    public ReportGeneralData getReportGeneralData(String matricula) throws DAOException {
        String queryGetGeneralData =
            "SELECT p.nombre AS nombreAlumno, p.apellidoPaterno AS apAlumno, p.apellidoMaterno AS amAlumno, " +
            "pr.idProyecto, pr.nombre AS nombreProyecto, pr.objetivos, pr.metodologia, " +
            "prof.nombre AS nombreProfesor, prof.apellidoPaterno AS apProfesor, " +
            "rt.nombre AS nombreResponsable, rt.apellidoPaterno AS apResponsable, " +
            "ov.nombreOrganizacion, " +
            "ee.nrc, ee.seccion, ee.fechaInicio AS eeInicio, ee.fechaTermino AS eeTermino " +
            "FROM practicante p " +
            "INNER JOIN Proyecto pr ON p.idProyecto = pr.idProyecto " +
            "LEFT JOIN profesor prof ON pr.noPersonal = prof.numeroPersonal " +
            "INNER JOIN responsableTecnico rt ON pr.idResponsableTecnico = rt.idResponsableTecnico " +
            "LEFT JOIN organizacionvinculada ov ON pr.idOrganizacionVinculada = ov.IdOrganizacionVinculada " +
            "LEFT JOIN imparteexperienciaeducativa iee ON prof.numeroPersonal = iee.numeroPersonal " +
            "LEFT JOIN experienciaeducativa ee ON iee.nrc = ee.nrc " +
            "WHERE p.matricula = ?";

        try {
            Connection connection = ConfigDatabase.getConnection();
            try (PreparedStatement statementGetData = connection.prepareStatement(queryGetGeneralData)) {
                statementGetData.setString(1, matricula);
                try (ResultSet resultSet = statementGetData.executeQuery()) {
                    if (resultSet.next()) {
                        return mapResultSetToReportGeneralData(resultSet);
                    }
                }
            }
        } catch (SQLException exceptionDB) {
            throw new DAOException("Error al obtener los datos generales del reporte", exceptionDB);
        }
        throw new DAOException("No se encontraron datos para la matrícula: " + matricula, null);
    }

    private ReportGeneralData mapResultSetToReportGeneralData(ResultSet resultSet) throws SQLException {
        ReportGeneralData data = new ReportGeneralData();
        data.setStudentFullName(
            resultSet.getString("nombreAlumno") + " " +
            resultSet.getString("apAlumno") + " " +
            resultSet.getString("amAlumno"));
        data.setProjectId(resultSet.getInt("idProyecto"));
        data.setProjectName(resultSet.getString("nombreProyecto"));
        data.setProjectObjective(resultSet.getString("objetivos"));
        data.setProjectMethodology(resultSet.getString("metodologia"));

        String nombreProf = resultSet.getString("nombreProfesor");
        String apProf     = resultSet.getString("apProfesor");
        data.setProfessorFullName(
            nombreProf != null ? nombreProf + " " + apProf : "Sin asignar");

        data.setTechnicalResponsibleFullName(
            resultSet.getString("nombreResponsable") + " " +
            resultSet.getString("apResponsable"));

        data.setOrganizationName(resultSet.getString("nombreOrganizacion"));

        int nrc = resultSet.getInt("nrc");
        if (!resultSet.wasNull()) {
            data.setNrc(nrc);
            data.setSection(resultSet.getString("seccion"));
            java.sql.Date eeInicio  = resultSet.getDate("eeInicio");
            java.sql.Date eeTermino = resultSet.getDate("eeTermino");
            if (eeInicio  != null) data.setEeStartDate(eeInicio.toLocalDate());
            if (eeTermino != null) data.setEeEndDate(eeTermino.toLocalDate());
        }
        return data;
    }
}