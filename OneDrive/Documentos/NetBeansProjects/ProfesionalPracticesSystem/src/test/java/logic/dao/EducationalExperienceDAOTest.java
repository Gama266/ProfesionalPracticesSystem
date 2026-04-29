
package logic.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import logic.businessObject.EducationalExperience;

/**
 * Pruebas nativas para EducationalExperienceDAO
 * @author akyer
 */
public class EducationalExperienceDAOTest {
    
    private EducationalExperienceDAO dao;
    private Connection realConnection; // Conexión real (no mock)
    
    @BeforeAll
    public static void setUpClass() {
        System.out.println("=== Iniciando pruebas de EducationalExperienceDAO ===");
    }
    
    @AfterAll
    public static void tearDownClass() {
        System.out.println("=== Finalizando pruebas ===");
    }
    
    @BeforeEach
    public void setUp() throws Exception {
        dao = new EducationalExperienceDAO();
        
        // Conexión a base de datos de prueba REAL
        realConnection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/profesional_practices_test",
            "test_user",
            "test_password"
        );
        
        // Limpiar tablas antes de cada prueba
        cleanTables();
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        if (realConnection != null && !realConnection.isClosed()) {
            realConnection.close();
        }
    }
    
    private void cleanTables() throws SQLException {
        try (Statement stmt = realConnection.createStatement()) {
            stmt.execute("DELETE FROM educational_experience");
            // Ajusta según el nombre real de tu tabla
        }
    }
    
    /**
     * Test of registerEducationalExperience method.
     */
    @Test
    public void testRegisterEducationalExperience_Exitoso() throws Exception {
        System.out.println("registerEducationalExperience - caso exitoso");
        
        // ARRANGE
        EducationalExperience exp = new EducationalExperience();
        exp.setNrc(12345);
        exp.setStartDate(LocalDate.of(2026, 1, 15));
        exp.setEndDate(LocalDate.of(2026, 6, 15));
        exp.setSection("A");

        boolean result = dao.registerEducationalExperience(exp);

        assertTrue(result, "Debe registrar exitosamente");
        
        EducationalExperience saved = dao.getNrc(12345);
        assertNotNull(saved);
        assertEquals("A", saved.getSection());
    }
    
    @Test
    public void testRegisterEducationalExperience_NRC_Duplicado() throws Exception {
        System.out.println("registerEducationalExperience - NRC duplicado");
        
        EducationalExperience exp1 = buildExperience(11111);
        EducationalExperience exp2 = buildExperience(11111);

        assertTrue(dao.registerEducationalExperience(exp1));
        assertFalse(dao.registerEducationalExperience(exp2), 
                   "No debe permitir duplicados");
    }
    
    @Test
    public void testGetByDateRange_ConDatos() throws Exception {
        System.out.println("getByDateRange - con resultados");

        EducationalExperience exp1 = buildExperience(1001);
        exp1.setStartDate(LocalDate.of(2026, 1, 10));
        exp1.setEndDate(LocalDate.of(2026, 6, 20));
        
        EducationalExperience exp2 = buildExperience(1002);
        exp2.setStartDate(LocalDate.of(2026, 2, 15));
        exp2.setEndDate(LocalDate.of(2026, 7, 25));
        
        EducationalExperience exp3 = buildExperience(1003);
        exp3.setStartDate(LocalDate.of(2025, 12, 1));
        exp3.setEndDate(LocalDate.of(2026, 1, 5));
        
        dao.registerEducationalExperience(exp1);
        dao.registerEducationalExperience(exp2);
        dao.registerEducationalExperience(exp3);

        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 30);
        List<EducationalExperience> results = dao.getByDateRange(start, end);

        assertEquals(2, results.size(), "Solo dos experiencias en el rango");
        assertTrue(results.stream().anyMatch(e -> e.getNrc() == 1001));
        assertTrue(results.stream().anyMatch(e -> e.getNrc() == 1002));
    }
    
    @Test
    public void testGetByDateRange_SinFechas() throws Exception {
        System.out.println("getByDateRange - sin resultados");

        List<EducationalExperience> results = dao.getByDateRange(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 12, 31)
        );

        assertTrue(results.isEmpty(), "Lista debe estar vacía");
    }
    
    @Test
    public void testGetByDateRange_FechasInvalidas() throws Exception {
        System.out.println("getByDateRange - fechas inválidas");
        
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            dao.getByDateRange(LocalDate.of(2026, 6, 1), 
                              LocalDate.of(2026, 1, 1));
        });
    }
    
    // Helper method
    private EducationalExperience buildExperience(int nrc) {
        EducationalExperience e = new EducationalExperience();
        e.setNrc(nrc);
        e.setStartDate(LocalDate.of(2026, 1, 15));
        e.setEndDate(LocalDate.of(2026, 6, 15));
        e.setSection("A");
        return e;
    }
}
