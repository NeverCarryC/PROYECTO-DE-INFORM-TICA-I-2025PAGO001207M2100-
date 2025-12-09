package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Modulo;
import model.Unidad;

public class ModuloCRUD {

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
	
	
	public static boolean editModulo(int id, String titulo, String ruta_archivo, int id_unidad) {
		String sql = "UPDATE modelo_gestion.modulo SET titulo=?, ruta_archivo=?, id_unidad=? WHERE id=?;";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, titulo);
			pstmt.setString(2, ruta_archivo);
			pstmt.setInt(3, id_unidad);
			pstmt.setInt(4, id);
			 int rowsAffected = pstmt.executeUpdate();
			 return rowsAffected>0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean deleteModulo(int id) {
        String sql = "DELETE FROM modelo_gestion.modulo WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar módulo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

	public static Modulo addModulo(String titulo, String ruta_archivo, int id_unidad) {
        String sql = "INSERT INTO modelo_gestion.modulo (titulo, ruta_archivo, id_unidad) VALUES (?, ?, ?)";

        // Statement.RETURN_GENERATED_KEYS 是为了获取数据库自动生成的 ID (Auto Increment)
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, titulo);
            pstmt.setString(2, ruta_archivo);
            pstmt.setInt(3, id_unidad);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // 获取生成的 ID
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        // 返回完整的对象，供 UI 使用
                        return new Modulo(generatedId, titulo, ruta_archivo, id_unidad);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al crear módulo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // 如果失败返回 null
    }
	public static Modulo getModuloById(int idModulo) {
	    String sql = "SELECT id, titulo, ruta_archivo, id_unidad FROM modelo_gestion.modulo WHERE id = ?";

	    try (Connection conn = ConexionDB.conectar();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, idModulo);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            return new Modulo(
	                    rs.getInt("id"),
	                    rs.getString("titulo"),
	                    rs.getString("ruta_archivo"),
	                    rs.getInt("id_unidad")
	            );
	        }

	    } catch (SQLException e) {
	        System.err.println("Error obteniendo módulo por ID: " + e.getMessage());
	    }

	    return null;
	}

	public static void main(String[] args) {
		// addModulo("Test modulo", "",1);
		// deleteModulo(7);
		
		
		// param: int id, String titulo, String ruta_archivo, int id_unidad)
		// editModulo(9,"edit","",3);
	}

}
