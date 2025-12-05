package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Registro_examen;

public class Registro_examenCRUD {
	public static void main(String[] args) {
		// System.out.println(createRegistroWithoutNota(1,2,1));
		// System.out.println(updateNotaById(1,4.95));
		// System.out.println(getRegistrosById_examen(1));
		System.out.println(getRegistrosById_examenYAlumno(1, 1));
	}

	// alumno entrega la examen
	public static boolean createRegistroWithoutNota(int id_examen, int id_alumno, int id_profesor) {
		int filasAfectadas = 0;
		String sql = "INSERT INTO registro_examen(id_examen, id_alumno, id_profesor) VALUES(?, ?, ?)";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id_examen);
			stmt.setInt(2, id_alumno);
			stmt.setInt(3, id_profesor);
			filasAfectadas = stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return filasAfectadas>0;
	}
	
	// Profesor poner nota
	public static boolean updateNotaById(int id_registro, double nota) {
		// UPDATE modelo_gestion.registro_examen
		//SET id_examen=1, id_alumno=1, id_profesor=1, nota=NULL
		//		WHERE id=1;
		int filasAfectadas = 0;
		String sql = "UPDATE registro_examen SET nota=? WHERE id=?";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setDouble(1, nota);
			stmt.setInt(2, id_registro);
			filasAfectadas = stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return filasAfectadas>0;
	}
	
	// Profesor ver la lista de registro en la exmaen
	// ej: todos los registros de examen:Analisis matematica
	public static ArrayList<Registro_examen> getRegistrosById_examen(int id_examen ){
		
//		SELECT id, id_examen, id_alumno, id_profesor, nota
//		FROM modelo_gestion.registro_examen
//		WHERE id=1;
		
		ArrayList<Registro_examen> lista = new ArrayList<>();
		String sql = "SELECT id, id_examen, id_alumno, id_profesor, nota FROM registro_examen WHERE id_examen=?";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id_examen);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				// int id, int id_examen, int id_alumno, int id_profesor, double nota
				double nota = rs.getDouble("nota");
				    if (rs.wasNull()) {
				        nota = -999; // -999 siginifica NULL
				    }
				lista.add(new Registro_examen(
						rs.getInt("id"),
						rs.getInt("id_examen"),
						rs.getInt("id_alumno"),
						rs.getInt("id_profesor"),
						nota
						));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;
	}
	
	// Alumno ver su nota en una examen
	public static ArrayList<Registro_examen> getRegistrosById_examenYAlumno(int id_examen, int id_alumno){
		ArrayList<Registro_examen> lista = new ArrayList<>();
		String sql = "SELECT id, id_examen, id_alumno, id_profesor, nota FROM registro_examen WHERE id_examen=? AND id_alumno=?";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id_examen);
			stmt.setInt(2, id_alumno);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				// int id, int id_examen, int id_alumno, int id_profesor, double nota
				double nota = rs.getDouble("nota");
				    if (rs.wasNull()) {
				        nota = -999; // -999 siginifica NULL
				    }
				lista.add(new Registro_examen(
						rs.getInt("id"),
						rs.getInt("id_examen"),
						rs.getInt("id_alumno"),
						rs.getInt("id_profesor"),
						nota
						));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;
	}
}
