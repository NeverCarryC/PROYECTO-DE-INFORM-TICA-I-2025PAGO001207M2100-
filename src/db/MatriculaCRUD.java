//package db;
//
//import java.sql.Connection;
//import java.sql.Date;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//
//import model.Curso;
//import model.Examen;
//import model.Matricula;
//
//public class MatriculaCRUD {
//	 public static void main(String[] args) {
//	        // Obtener el tiempo actual(unidad milisegundo)
//	        // long millis = System.currentTimeMillis();
//
//	        // Convertir al java.sql.Date
//	        // Date today = new Date(millis);
//
//	        // System.out.println(today); // Resultado：2025-10-25
//		 
//		 System.out.println(getCursosMatriculadoPorIDAlumno(1));
//	    }
//	
//	public static boolean createMatricula(int id_alumno, int id_curso) {
//		// Obtener el tiempo actual(unidad milisegundo)
//        long millis = System.currentTimeMillis();
//        // Convertir al java.sql.Date
//        Date hoy = new Date(millis);
//
//		
////        INSERT INTO modelo_gestion.matricula
////        (id, id_alumno, id_curso, fecha_matricula)
////        VALUES(0, 0, 0, '');
//		int filasAfectadas=0;
//		String sql = "INSERT INTO modelo_gestion.matricula(id_alumno, id_curso, fecha_matricula)  VALUES(?, ?, ?);";
//		Connection conn = ConexionDB.conectar();
//        try {
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			stmt.setInt(1, id_alumno);
//			stmt.setInt(2, id_curso);
//			stmt.setDate(3, hoy);
//			filasAfectadas  = stmt.executeUpdate();
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		return filasAfectadas>0;
//
//	}
//	
//	// FUNCION 2: La lista de cursos matriculado por un alumno
//	public static ArrayList<Curso> getCursosMatriculadoPorIDAlumno(int id_alumno){
////		SELECT 
////	    m.id_alumno,
////	    c.nombre AS nombre_curso,
////	    c.id_profesor,
////	    m.fecha_matricula
////	FROM modelo_gestion.matricula m
////	INNER JOIN modelo_gestion.curso c
////	    ON m.id_curso = c.id
////	WHERE m.id_alumno = 1;
//		
//		ArrayList<Curso> lista = new ArrayList<>();
//		String sql = "SELECT c.id,c.nombre, c.id_profesor "
//				+ "FROM modelo_gestion.matricula m INNER JOIN modelo_gestion.curso c "
//				+ " ON m.id_curso = c.id WHERE m.id_alumno = ?;";
//		Connection conn = ConexionDB.conectar();
//		try {
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			stmt.setInt(1, id_alumno);
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//            	lista.add(new Curso(
//	                    rs.getInt("id"),
//	                    rs.getString("nombre"),
//	                    rs.getInt("id_profesor")  
//	                ));
//            }
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//				
//		
//		return lista;
//	}
//	
//	
//
//}
