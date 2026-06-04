package logic.dao;

import logic.businessObject.Teacher;
import logic.exceptions.DAOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para TeacherDAO
 * @author Gamaliel
 */
public class TeacherDAOTest {

    @Test
    public void testRegisterTeacherSuccess() {
        TeacherDAO dao = new TeacherDAO();
        Teacher testTeacher = new Teacher();
        
        testTeacher.setNoPersonal(99991); 
        testTeacher.setPassword("pass123");
        testTeacher.setName("Gamaliel");
        testTeacher.setPaternalSurname("Cabrera");
        testTeacher.setMaternalSurname("Plácido");
        testTeacher.setActivityStatus(true);

        try {
            boolean result = dao.registerTeacher(testTeacher);
            assertTrue(result, "El profesor debió registrarse correctamente.");
            dao.deleteTeacher(99991);
            
        } catch (DAOException e) {
          
            fail("Falló la base de datos. Lee la consola arriba para ver el error exacto.");
        }
    }

    @Test
    public void testUpdateTeacherSuccess() {
        TeacherDAO dao = new TeacherDAO();
        Teacher testTeacher = new Teacher();
        
      
        testTeacher.setNoPersonal(99992); 
        testTeacher.setPassword("oldPass");
        testTeacher.setName("Juan");
        testTeacher.setPaternalSurname("Perez");
        testTeacher.setMaternalSurname("Gomez");
        testTeacher.setActivityStatus(true);

        try {
       
            dao.registerTeacher(testTeacher);
            
     
            testTeacher.setPassword("newPass456");
            testTeacher.setName("Juan Modificado");
            testTeacher.setActivityStatus(false);
            
          
            boolean updateResult = dao.updateTeacher(testTeacher);
            assertTrue(updateResult, "El profesor debió actualizarse correctamente.");
            
           
            dao.deleteTeacher(99992);
            
        } catch (DAOException e) {
            fail("No debió lanzar excepción al actualizar: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteTeacherSuccess() {
        TeacherDAO dao = new TeacherDAO();
        Teacher testTeacher = new Teacher();
        
        testTeacher.setNoPersonal(99993); 
        testTeacher.setPassword("tempPass");
        testTeacher.setName("Profesor");
        testTeacher.setPaternalSurname("Temporal");
        testTeacher.setMaternalSurname("Borrar");
        testTeacher.setActivityStatus(true);

        try {
        
            dao.registerTeacher(testTeacher);
           
            boolean deleteResult = dao.deleteTeacher(99993);
            assertTrue(deleteResult, "El profesor debió eliminarse correctamente.");
            
        } catch (DAOException e) {
            fail("No debió lanzar excepción al eliminar: " + e.getMessage());
        }
    }
}