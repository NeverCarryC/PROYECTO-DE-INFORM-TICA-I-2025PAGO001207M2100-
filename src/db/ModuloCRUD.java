package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Modulo;
import model.Unidad;

public class ModuloCRUD {
//	public static Map<Integer, List<Modulo>> getAllModulosByIdCurso(int id_curso){
//		ArrayList<Modulo> lista = new ArrayList<>();
//		String sql = "SELECT id, titulo, ruta_archivo, id_categoria, id_curso FROM modelo_gestion.modulo WHERE id_curso=?";
//		Connection conn = ConexionDB.conectar();
//		try {
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			stmt.setInt(1, id_curso);
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()) {
//				int id = rs.getInt("id");
//				String titulo = rs.getString("titulo");
//				String ruta_archivo = rs.getString("ruta_archivo");
//				int id_categoria = rs.getInt("id_unidad");
//				Modulo modulo = new Modulo(id, titulo,ruta_archivo,id_categoria, id_curso);
//				lista.add(modulo);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Map<Integer, List<Modulo>> grouped =
//				lista.stream()
//			           .collect(Collectors.groupingBy(Modulo::getId_categoria));
//
//		
//		return grouped;
//	}
	public static ArrayList<Modulo> getModulosByIdUnidad(int id_unidad){
		ArrayList<Modulo> lista = new ArrayList<>();
		String sqlString = "SELECT id, titulo, ruta_archivo, id_unidad FROM modelo_gestion.modulo WHERE id_unidad=?;";
		Connection conn = ConexionDB.conectar();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setInt(1, id_unidad);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				int id = rs.getInt("id");
				String titulo = rs.getString("titulo");
				String ruta_archivo = rs.getString("ruta_archivo");
				Modulo modulo = new Modulo(id, titulo,ruta_archivo,id_unidad);
				lista.add(modulo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return lista;
	}
	
	public static boolean updateModulo(int id_modulo, String nombre, String descrip) {
		boolean success = false;
		String sql = "UPDATE modelo_gestion.unidad SET nombre=?, descripcion=? WHERE id=?;";
		Connection conn =  ConexionDB.conectar();
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, nombre);      // 对应 SQL 中的第一个 '?' (nombre)
            pstmt.setString(2, descrip); // 对应 SQL 中的第二个 '?' (descripcion)
            pstmt.setInt(3, id_modulo);  // 对应 SQL 中的第三个 '?' (WHERE id)
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	

	
	public static void main(String[] args) {
	

		// System.out.println(ModuloCRUD.getAllModulosByIdCurso(1).get(3));
		//System.out.println(createCurso("Quimica",1));
		// System.out.println(getCursoByIdCurso(1));
		// System.out.println(getCursosByIdProfesor(11));
		
	}

}
