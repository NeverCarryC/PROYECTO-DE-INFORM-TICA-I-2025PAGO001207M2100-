package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Alumno;
import model.Profesor;

public class ProfesorCRUD {
	public static void main(String[] args) {
		System.out.println(getProfesorByNombreYPassword("Jack", "jack"));
	}
	
	public static Profesor getProfesorByNombreYPassword(String nombre, String password) {
		String sql = "SELECT id, nombre, apellido, email, password FROM profesor WHERE nombre = ? AND password = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, nombre);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Crear y devolver el objeto Usuario
                return new Profesor(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retorna null si no encuentra el usuario
	}
}
