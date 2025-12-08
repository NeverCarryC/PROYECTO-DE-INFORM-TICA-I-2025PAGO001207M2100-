package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Alumno;

public class AlumnoCRUD {
	public static Alumno getAlumnoById(int id) {
	    // SQL语句：只根据ID进行查询
	    String sql = "SELECT id, nombre, apellido, email, password FROM alumno WHERE id = ?";
	    
	    // 假设你的数据库连接类是 ConexionDB
	    try (Connection conn = ConexionDB.conectar();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	         
	        // 绑定参数：将传入的ID设置到SQL语句的第一个问号(?)处
	        stmt.setInt(1, id);
	        
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            // 如果找到记录，则创建并返回 Alumno 对象
	            return new Alumno(
	                rs.getInt("id"),
	                rs.getString("nombre"),
	                rs.getString("apellido"),
	                rs.getString("email"),
	                rs.getString("password")
	            );
	        }
	    } catch (SQLException e) {
	        // 打印异常信息（在生产环境中应记录日志而不是直接打印）
	        e.printStackTrace();
	    }
	    // 如果没有找到匹配的ID或发生异常，则返回 null
	    return null; 
	}
	
	public static Alumno login(String nombre, String password) {
		  String sql = "SELECT id, nombre, apellido, email, password FROM alumno WHERE nombre = ? AND password = ?";
	        try (Connection conn = ConexionDB.conectar();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {
	             
	            stmt.setString(1, nombre);
	            stmt.setString(2, password);
	            ResultSet rs = stmt.executeQuery();
	            
	            if (rs.next()) {
	                // Crear y devolver el objeto Usuario
	                return new Alumno(
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Alumno alumno =  AlumnoCRUD.login("nico", "nico");
		
		System.out.print(login("simon2", "simon"));
	}

}
