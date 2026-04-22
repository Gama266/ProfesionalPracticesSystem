/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package logic.dao;
import logic.idao.IStudentDAO;
import logic.businessObject.Student;
import logic.exceptions.DAOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author gamal
 */
public class StudentDAOTest {
    
    private StudentDAO studentDAO;
    private Student testStudent;
    
    public StudentDAOTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
      
        studentDAO = new StudentDAO();
        testStudent = new Student();
        
       
        testStudent.setMatricula("TEST00001"); 
        testStudent.setName("Juan");
        testStudent.setPaternalSurname("Perez");
        testStudent.setMaternalSurname("Gomez");
        testStudent.setActivityStatus(true);
        testStudent.setPassword("pass123");
        testStudent.setProject(null); 
    }
    
    @AfterEach
    public void tearDown() throws DAOException {
   
        System.out.println("Limpiando la base de datos...");
        boolean eliminado = studentDAO.deleteStudent("TEST00001");
        if (eliminado) {
            System.out.println("Éxito: El estudiante de prueba fue eliminado de la BD.");
        } else {
            System.out.println("Aviso: No se encontró al estudiante para eliminar (es normal si la prueba de registro falló).");
        }
    }

    @Test
    public void testRegisterStudentExitoso() {
        System.out.println("Ejecutando prueba: testRegisterStudentExitoso");
        try {
      
            boolean result = studentDAO.registerStudent(testStudent);
            
   
            assertTrue(result, "El estudiante de prueba debería registrarse correctamente en la BD");
            
        } catch (DAOException e) {
            fail("El método lanzó una excepción inesperada: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateStudentExitoso() {
        System.out.println("Ejecutando prueba: testUpdateStudentExitoso");
        try {
          
            studentDAO.registerStudent(testStudent);
            
           
            testStudent.setName("Juan Modificado");
            testStudent.setActivityStatus(false);
            
    
            boolean result = studentDAO.updateStudent(testStudent);
            
           
            assertTrue(result, "El estudiante debería actualizarse correctamente");
            
        } catch (DAOException e) {
            fail("El método lanzó una excepción inesperada: " + e.getMessage());
        }
    }
}