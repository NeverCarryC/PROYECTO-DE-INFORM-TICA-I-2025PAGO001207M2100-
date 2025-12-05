package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Modulo;
import model.Unidad;

public class UnidadCRUD {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(getUnidadsByIdAsignatura(1));
	}
	
	
//	public static ArrayList<Unidad> getUnidadsYModulosByIdAsignatura(int id_asignatura){
//		
//		
//	}
	
	
	// obtener la lista de unidad y sus modulos en la asignatura
	// select DISTINCT u.id , u.nombre, u.descripcion 
	// FROM modelo_gestion.modulo m INNER  join modelo_gestion.unidad u on m.id_unidad =u.id  where m.id_curso = 1;
	public static ArrayList<Unidad> getUnidadsByIdAsignatura(int id_asignatura){
		ArrayList<Unidad> lista = new ArrayList<>();
		String sqlString = "select  u.id , u.nombre, u.descripcion FROM modelo_gestion.unidad u where u.id_asignatura = ?;";
		Connection conn = ConexionDB.conectar();
		
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setInt(1, id_asignatura);

			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				int id_unidad = rs.getInt("u.id");
				String nombre = rs.getString("u.nombre");
				String descripcion = rs.getString("u.descripcion");
				ArrayList<Modulo> modulos = ModuloCRUD.getModulosByIdUnidad(id_unidad);
				Unidad u = new Unidad(id_unidad, nombre, descripcion, modulos,id_asignatura);
				lista.add(u);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;
	}
	
	public static Unidad createUnidad(String nombre, String descripcion, int id_asignatura) {

	    String sql = "INSERT INTO modelo_gestion.unidad(nombre, descripcion,id_asignatura) VALUES (?,?,?)";

	    try (Connection conn = ConexionDB.conectar();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setString(1, nombre);
	        stmt.setString(2, descripcion);
	        stmt.setInt(3, id_asignatura);
	      

	        int filas = stmt.executeUpdate();  // ✔ INSERT 用 executeUpdate()

	        if (filas > 0) {

	            // ✔ 获取 INSERT 自动生成的 ID
	            ResultSet rs = stmt.getGeneratedKeys();
	            if (rs.next()) {
	                int idGenerado = rs.getInt(1);
	                
	                return new Unidad(
	                        idGenerado,
	                        nombre,
	                        descripcion,
	                        id_asignatura
	                );
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return null;
	}



}
