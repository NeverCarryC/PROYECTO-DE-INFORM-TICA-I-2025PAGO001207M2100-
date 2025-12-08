package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.RegistroExamen;


public class RegistroExamenCRUD {
	public static void main(String[] args) {
		// System.out.println(createRegistroWithoutNota(1,2,1));
		// System.out.println(updateNotaById(1,4.95));
		// System.out.println(getRegistrosById_examen(1));
		// System.out.println(getRegistrosById_examenYAlumno(1, 1));
	}
	public static ArrayList<RegistroExamen> getRegistrosByAlumnoAndExamen(int id_alumno, int id_examen) {
        ArrayList<RegistroExamen> lista = new ArrayList<>();
        
        String sql = "SELECT id, id_examen, id_alumno, id_profesor, nota, comentario, ruta_archivo " +
                     "FROM modelo_gestion.registro_examen " +
                     "WHERE id_alumno = ? AND id_examen = ?";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // 1. 绑定参数
            stmt.setInt(1, id_alumno);
            stmt.setInt(2, id_examen);
            
            // 2. 执行查询
            try (ResultSet rs = stmt.executeQuery()) {
                // 3. 处理结果集
                while (rs.next()) {
                    // 注意：getDouble/getString 会返回 null
                    Double nota = rs.getObject("nota") != null ? rs.getDouble("nota") : null; 
                    
                    RegistroExamen registro = new RegistroExamen(
                        rs.getInt("id"),
                        rs.getInt("id_examen"),
                        rs.getInt("id_alumno"),
                        rs.getInt("id_profesor"),
                        nota,
                        rs.getString("comentario"),
                        rs.getString("ruta_archivo")
                    );
                    lista.add(registro);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ SQL 错误：查询学生考试记录失败 (Alumno ID: " + id_alumno + ", Examen ID: " + id_examen + ")");
            e.printStackTrace();
        }
        
        return lista;
    }
	
	public static RegistroExamen insertRegistroExamen(RegistroExamen registro) {
        String sql = "INSERT INTO modelo_gestion.registro_examen " +
                     "(id_examen, id_alumno, id_profesor, nota, comentario, ruta_archivo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1. 绑定参数
            stmt.setInt(1, registro.getId_examen());
            stmt.setInt(2, registro.getId_alumno());
            stmt.setInt(3, registro.getId_profesor());
            
            // 处理 Double 类型，如果为 null，使用 setNull
            if (registro.getNota() != null) {
                stmt.setDouble(4, registro.getNota());
            } else {
                stmt.setNull(4, java.sql.Types.DOUBLE);
            }
            
            // 处理 String 类型，如果为 null，使用 setNull
            stmt.setString(5, registro.getComentario()); // setString 可以处理 Java 的 null
            stmt.setString(6, registro.getRuta_archivo());

            int rowsAffected = stmt.executeUpdate(); 

            if (rowsAffected > 0) {
                // 2. 获取自动生成的 ID
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        registro.setId(idGenerado); // 更新对象 ID
                        return registro;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
	

	// alumno entrega la examen
//	public static boolean createRegistroWithoutNota(int id_examen, int id_alumno, int id_profesor) {
//		int filasAfectadas = 0;
//		String sql = "INSERT INTO registro_examen(id_examen, id_alumno, id_profesor) VALUES(?, ?, ?)";
//		Connection conn = ConexionDB.conectar();
//		try {
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			stmt.setInt(1, id_examen);
//			stmt.setInt(2, id_alumno);
//			stmt.setInt(3, id_profesor);
//			filasAfectadas = stmt.executeUpdate();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return filasAfectadas>0;
//	}
	
	// Profesor poner nota
//	public static boolean updateNotaById(int id_registro, double nota) {
//		// UPDATE modelo_gestion.registro_examen
//		//SET id_examen=1, id_alumno=1, id_profesor=1, nota=NULL
//		//		WHERE id=1;
//		int filasAfectadas = 0;
//		String sql = "UPDATE registro_examen SET nota=? WHERE id=?";
//		Connection conn = ConexionDB.conectar();
//		try {
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			stmt.setDouble(1, nota);
//			stmt.setInt(2, id_registro);
//			filasAfectadas = stmt.executeUpdate();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return filasAfectadas>0;
//	}
	
	// Profesor ver la lista de registro en la exmaen
	// ej: todos los registros de examen:Analisis matematica
//	public static ArrayList<RegistroExamen> getRegistrosById_examen(int id_examen ){
//		
////		SELECT id, id_examen, id_alumno, id_profesor, nota
////		FROM modelo_gestion.registro_examen
////		WHERE id=1;
//		
//		ArrayList<RegistroExamen> lista = new ArrayList<>();
//		String sql = "SELECT id, id_examen, id_alumno, id_profesor, nota FROM registro_examen WHERE id_examen=?";
//		Connection conn = ConexionDB.conectar();
//		try {
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			stmt.setInt(1, id_examen);
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()) {
//				// int id, int id_examen, int id_alumno, int id_profesor, double nota
//				double nota = rs.getDouble("nota");
//				    if (rs.wasNull()) {
//				        nota = -999; // -999 siginifica NULL
//				    }
//				lista.add(new RegistroExamen(
//						rs.getInt("id"),
//						rs.getInt("id_examen"),
//						rs.getInt("id_alumno"),
//						rs.getInt("id_profesor"),
//						nota
//						));
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return lista;
//	}
	
	// Alumno ver su nota en una examen
//	public static ArrayList<Registro_examen> getRegistrosById_examenYAlumno(int id_examen, int id_alumno){
//		ArrayList<Registro_examen> lista = new ArrayList<>();
//		String sql = "SELECT id, id_examen, id_alumno, id_profesor, nota FROM registro_examen WHERE id_examen=? AND id_alumno=?";
//		Connection conn = ConexionDB.conectar();
//		try {
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			stmt.setInt(1, id_examen);
//			stmt.setInt(2, id_alumno);
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()) {
//				// int id, int id_examen, int id_alumno, int id_profesor, double nota
//				double nota = rs.getDouble("nota");
//				    if (rs.wasNull()) {
//				        nota = -999; // -999 siginifica NULL
//				    }
//				lista.add(new Registro_examen(
//						rs.getInt("id"),
//						rs.getInt("id_examen"),
//						rs.getInt("id_alumno"),
//						rs.getInt("id_profesor"),
//						nota
//						));
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return lista;
//	}
}
