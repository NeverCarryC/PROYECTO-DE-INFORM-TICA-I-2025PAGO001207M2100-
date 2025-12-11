package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import model.Alumno;
import model.Asignatura;

public class AsignaturaCRUD {
	
	public static Asignatura getAsignaturaById(int id_asignatura){
	    String sql = "SELECT id, nombre, id_profesor, create_date, descripcion FROM curso WHERE id=?";
	    
	    try (Connection conn = ConexionDB.conectar();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, id_asignatura);
	        

	        try (ResultSet rs = stmt.executeQuery()) {
	            

	            if (rs.next()) {

	                return new Asignatura(
	                    rs.getInt("id"),
	                    rs.getString("nombre"),
	                    rs.getInt("id_profesor"),
	                    rs.getDate("create_date"),
	                    rs.getString("descripcion")
	                );
	            }
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("发生 SQL 错误，无法获取 Asignatura (ID: " + id_asignatura + ")");
	        e.printStackTrace();
	    }
	    

	    return null;
	}
	
	
	public static Asignatura insertarAsignatura(String nombre, int id_profesor,  String description ) {
		String sql = "INSERT INTO curso (nombre, id_profesor, create_date, descripcion) VALUES (?, ?, ?, ?)";
		Connection conn  = ConexionDB.conectar();
		 try {
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, nombre);
			stmt.setInt(2, id_profesor);
			LocalDate localDate = LocalDate.now();
			Date sqlDate = Date.valueOf(localDate);
			stmt.setDate(3, sqlDate);
			stmt.setString(4, description);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				ResultSet generatedKeys = stmt.getGeneratedKeys();
				if(generatedKeys.next()) {
					int newId = generatedKeys.getInt(1);
					Asignatura asignatura = new Asignatura(newId, nombre, id_profesor, sqlDate, description);
					return asignatura;
				}
						
				
			}else {
				return null;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Asignatura> getAllAsignaturas() {
		ArrayList<Asignatura> lista = new ArrayList<>();
		String sql = "SELECT id,nombre, id_profesor, create_date,descripcion from curso";
	    try (Connection conn = ConexionDB.conectar();
	            PreparedStatement stmt = conn.prepareStatement(sql)) {
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                int id = rs.getInt("id");
	                String nombre = rs.getString("nombre");
	                int idProfesor = rs.getInt("id_profesor");
	                Date createTime = rs.getDate("create_date");
	                String descripcion = rs.getString("descripcion");
	                Asignatura curso = new Asignatura(id, nombre, idProfesor,createTime,descripcion);
	                lista.add(curso);
	            }

	        
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		return lista;
	}
	
	public static Map<Integer, ArrayList<Asignatura>> getCursosPorAnio() {
	    Map<Integer, ArrayList<Asignatura>> cursosPorAnio = new HashMap<>();
	    String sql = "SELECT id, nombre, id_profesor, create_date, descripcion FROM curso";

	    try (Connection conn = ConexionDB.conectar();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String nombre = rs.getString("nombre");
	            int idProfesor = rs.getInt("id_profesor");
	            Date createDate = rs.getDate("create_date");
	            String descripcion = rs.getString("descripcion");

	            Calendar cal = Calendar.getInstance();
	            cal.setTime(createDate);
	            int year = cal.get(Calendar.YEAR);

	            Asignatura curso = new Asignatura(id, nombre, idProfesor, createDate, descripcion);
	            cursosPorAnio.computeIfAbsent(year, k -> new ArrayList<>()).add(curso);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return cursosPorAnio;
	}


	
	
	// Un profesor crea un curso
	public static boolean createCurso(String nombre, int id_profesor) {
		// INSERT INTO modelo_gestion.curso(id, nombre, id_profesor) VALUES(2, 'Física ', 1);
		int filasAfectadas=0;
		String sql = "INSERT INTO modelo_gestion.curso(nombre, id_profesor) VALUES(?, ?);";
		Connection conn = ConexionDB.conectar();
        try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, nombre);
			stmt.setInt(2, id_profesor);
			filasAfectadas  = stmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return filasAfectadas>0;
	}
	
	// Get curso ById
	public static Asignatura getCursoByIdCurso(int id_curso){
		String sql = "SELECT id,nombre, id_profesor from curso where id=?";
	    try (Connection conn = ConexionDB.conectar();
	            PreparedStatement stmt = conn.prepareStatement(sql)) {
	    	 	stmt.setInt(1, id_curso);
	            ResultSet rs = stmt.executeQuery();
	            
	            if (rs.next()) {
	                // Crear y devolver el objeto Usuario
	                return new Asignatura(
	                    rs.getInt("id"),
	                    rs.getString("nombre"),
	                    rs.getInt("id_profesor") ,
	                    rs.getDate("create_date"),
	                    rs.getString("descripcion")
	                );
	            }
	        
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		return null;
	}
	
	// La lista de cursos enseñado por un profesor
	public static ArrayList<Asignatura> getCursosByIdProfesor(int id_profesor){
		ArrayList<Asignatura> lista = new ArrayList<>();
		String sql = "SELECT id,nombre, id_profesor from curso where id_profesor=?";
	    try (Connection conn = ConexionDB.conectar();
	            PreparedStatement stmt = conn.prepareStatement(sql)) {
	    	 	stmt.setInt(1, id_profesor);
	            ResultSet rs = stmt.executeQuery();
	        
	            
	            while(rs.next()){
	            	lista.add(new Asignatura(
		                    rs.getInt("id"),
		                    rs.getString("nombre"),
		                    rs.getInt("id_profesor"),
		                    rs.getDate("create_date"),
		                    rs.getString("descripcion")
		                ));
	            }
	        
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		return lista;
	}
	
	public static boolean deleteAsignatura(int id_asignatura) {
		boolean deleted = false;
		
		String sql = "DELETE FROM modelo_gestion.curso WHERE id=?";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id_asignatura);
			int rowsAffected = stmt.executeUpdate();
			
			if (rowsAffected > 0) {
                deleted = true;
                System.out.println("Registro con ID " + id_asignatura + " eliminado correctamente.");
            } else {
                System.out.println("No se encontró ningún registro con ID " + id_asignatura + " para eliminar.");
            }
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deleted;
	}
	
	public static boolean editAsignatura(int id_asignatura, String nombre, String descrString) {
		String sql = "UPDATE modelo_gestion.curso SET nombre=?, descripcion=? WHERE id=?;" ;
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, nombre);      
            pstmt.setString(2, descrString); 
            pstmt.setInt(3, id_asignatura);  
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(getAllAsignaturas());
		 // System.out.println(getCursosPorAnio());
		// System.out.println(createCurso("Quimica",1));
		// System.out.println(getCursoByIdCurso(1));
		// System.out.println(getCursosByIdProfesor(11));
		// System.out.println(deleteAsignatura(8));
	}
}
