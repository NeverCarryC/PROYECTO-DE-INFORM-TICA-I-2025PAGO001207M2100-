package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Asignatura;
import model.Examen;

public class ExamenCRUD {
		
	// Crear una examen en el curso
	public static Boolean createExamen(String titulo, String contenido, int id_curso) {
		int filasAfectadas=0;
//		INSERT INTO modelo_gestion.examen
//		(id, titulo, contenido, id_curso)
//		VALUES(0, '', '', 0);
		String sql = "INSERT INTO modelo_gestion.examen(titulo, contenido, id_curso) VALUES(?, ?, ?)";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, titulo);
			stmt.setString(2, contenido);
			stmt.setInt(3, id_curso);
			filasAfectadas  = stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filasAfectadas>0;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// System.out.println(getCursos());
		//System.out.println(createCurso("Quimica",1));
		// System.out.println(getCursoByIdCurso(1));
		// System.out.println(createExamen("2º Examen de matematica", "El límite de (1/x^2 ) + 2 = ? ",1));
		
		// getExamenByIdCurso(int id_curso)
		System.out.println(getExamenByIdCurso(1));
	}
	
	
	
	public static ArrayList<Examen> getExamenByIdCurso(int id_curso){
		ArrayList<Examen> lista = new ArrayList<>();

		String sql = "SELECT id, titulo, contenido, id_curso FROM modelo_gestion.examen WHERE id_curso=?";
	    try (Connection conn = ConexionDB.conectar();
	            PreparedStatement stmt = conn.prepareStatement(sql)) {
	    	 	stmt.setInt(1, id_curso);
	            ResultSet rs = stmt.executeQuery();
	        
	            // public Examen(int id, String titulo, String contenido, int id_curso) 
	            while(rs.next()){
	            	lista.add(new Examen(
		                    rs.getInt("id"),
		                    rs.getString("titulo"),
		                    rs.getString("contenido"),
		                    rs.getInt("id_curso")
		                ));
	            }
	        
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		return lista;
	}
}
