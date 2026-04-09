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
import java.util.ArrayList;
import java.util.List;
import logic.businessObject.LinkedOrganization;
import logic.businessObject.LocationOrganization;

/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public class LinkedOrganizationDAO implements ILinkedOrganizationDAO{

    @Override
    public boolean registerLinkedOrganization(LinkedOrganization linkedOrganization) throws SQLException {
       String sql = "INSERT INTO organizaciónvinculada (nombreOrganizacion, direccion, numeroTelefono, correoElectronico, idUbicacion) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, linkedOrganization.getName());
            pstmt.setString(2, linkedOrganization.getDireccion());
            pstmt.setString(3, linkedOrganization.getPhoneNumber());
            pstmt.setString(4, linkedOrganization.getGmail());
            pstmt.setInt(5, linkedOrganization.getLocationOrganization().getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    linkedOrganization.setId(generatedKeys.getInt(1));
                }
                System.out.println("Organización vinculada registrada correctamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar organización: " + e.getMessage());
        }
        return false;  
    }


    @Override
    public List<LinkedOrganization> showAllLinkedOrganization() throws SQLException{
        List<LinkedOrganization> organizations = new ArrayList<>();
        String sql = "SELECT * FROM organizaciónvinculada ORDER BY nombreOrganizacion";
        
        try (Connection conn = ConfigDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                organizations.add(mapResultSetToLinkedOrganization(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener organizaciones: " + e.getMessage());
        }
        return organizations;
    }
    
    private LinkedOrganization mapResultSetToLinkedOrganization(ResultSet rs) throws SQLException {
        LinkedOrganization organization = new LinkedOrganization();
        organization.setId(rs.getInt("IdOrganizacionVinculada"));
        organization.setName(rs.getString("nombreOrganizacion"));
        organization.setDireccion(rs.getString("direccion"));
        organization.setPhoneNumber(rs.getString("numeroTelefono"));
        organization.setGmail(rs.getString("correoElectronico"));
        
        // Crear objeto LocationOrganization con el ID
        LocationOrganization location = new LocationOrganization();
        location.setId(rs.getInt("idUbicacion"));
        organization.setLocationOrganization(location);
        
        return organization;
    }
}
