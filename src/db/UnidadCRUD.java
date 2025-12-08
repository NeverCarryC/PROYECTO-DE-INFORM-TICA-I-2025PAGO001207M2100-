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
	
	public static Unidad getUnidadById(int id_unidad) {
        Unidad unidad = null;
        // 注意：只查询 Unidad 表
        String sqlString = "SELECT id, nombre, descripcion, id_asignatura FROM modelo_gestion.unidad WHERE id = ?;";
        Connection conn = ConexionDB.conectar();
        
        try {
            // 1. 准备和执行查询
            PreparedStatement stmt = conn.prepareStatement(sqlString);
            stmt.setInt(1, id_unidad);
            ResultSet rs = stmt.executeQuery();
            
            // 2. 处理结果集
            if (rs.next()) { // 只期望有一个结果
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int id_asignatura = rs.getInt("id_asignatura");
                
                // 3. 获取 Modulos 列表
                // 使用 ModuloCRUD 类中已有的方法获取子模块
                ArrayList<Modulo> modulos = ModuloCRUD.getModulosByIdUnidad(id);
                
                // 4. 构建最终的 Unidad 对象
                unidad = new Unidad(id, nombre, descripcion, modulos, id_asignatura);
            }
            
        } catch (SQLException e) {
            System.err.println("发生 SQL 错误，无法获取 Unidad (ID: " + id_unidad + ")");
            e.printStackTrace();
        } finally {
            // 确保关闭连接，尽管你的代码结构中 `ConexionDB.conectar()` 返回的连接可能需要在外部或使用 try-with-resources 关闭
            // 这里假设 ConexionDB.conectar() 返回的连接会在其他地方管理关闭。
        }
        
        return unidad;
    }
	
	
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

	public static boolean deleteUnidad(int id) {
		boolean deleted = false;
		String sql = "DELETE FROM modelo_gestion.unidad WHERE id=?;";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement pstmt =conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			int rowsAffected = pstmt.executeUpdate();
			if(rowsAffected > 0) {
				deleted = true;
				System.out.println("Registro con ID " + id +" unidad eliminado correctamente");
			}else {
				System.out.println("No se encontró ningún registro con ID " + id  + " para eliminar.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deleted;
	}
	public static boolean updateUnidad(int id, String nombre, String descrip) {
		boolean success = false;
		String sql = "UPDATE modelo_gestion.unidad SET nombre=?, descripcion=? WHERE id=?;";
		Connection conn =  ConexionDB.conectar();
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, nombre);      // 对应 SQL 中的第一个 '?' (nombre)
            pstmt.setString(2, descrip); // 对应 SQL 中的第二个 '?' (descripcion)
            pstmt.setInt(3, id);  // 对应 SQL 中的第三个 '?' (WHERE id)
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
