package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Asignatura;
import model.Tarea;

public class TareasCRUD {
	
	public static ArrayList<Tarea> getTareaByIdUnidad(int id_unidad) {
		ArrayList<Tarea> tareas = new ArrayList<Tarea>();
				
		String sql = "SELECT id, titulo, contenido, num_intento, fecha_entrega, id_unidad, ruta FROM modelo_gestion.tarea WHERE id_unidad=?;";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement  stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id_unidad);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				int id = rs.getInt("id");
				String titulo = rs.getString("titulo");
				String contenido = rs.getString("contenido");
				int num_intento = rs.getInt("num_intento");
				Date fechaEntrga = rs.getDate("fecha_entrega");
				// int id_unidad = rs.getInt("id_unidad");
				String ruta = rs.getString("ruta");
				tareas.add(new Tarea(id,titulo,contenido,num_intento,fechaEntrga,id_unidad,ruta));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tareas;
		
	}
	
	public static ArrayList<Tarea> getAllTareas() {
		ArrayList<Tarea> tareas = new ArrayList<Tarea>();
				
		String sql = "SELECT id, titulo, contenido, num_intento, fecha_entrega, id_unidad, ruta FROM modelo_gestion.tarea;";
		Connection conn = ConexionDB.conectar();
		try {
			PreparedStatement  stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				int id = rs.getInt("id");
				String titulo = rs.getString("titulo");
				String contenido = rs.getString("contenido");
				int num_intento = rs.getInt("num_intento");
				Date fechaEntrga = rs.getDate("fecha_entrega");
				int id_unidad = rs.getInt("id_unidad");
				String ruta = rs.getString("ruta");
				tareas.add(new Tarea(id,titulo,contenido,num_intento,fechaEntrga,id_unidad,ruta));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tareas;
		
	}
	
	
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
	
	public static boolean editExamen(int id, String titulo, String contenido, int id_curso) {
	    String sql = "UPDATE modelo_gestion.examen "
	               + "SET titulo = ?, contenido = ?, id_curso = ? "
	               + "WHERE id = ?";

	    int filasAfectadas = 0;

	    try (Connection conn = ConexionDB.conectar();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, titulo);
	        stmt.setString(2, contenido);
	        stmt.setInt(3, id_curso);
	        stmt.setInt(4, id);  // ← WHERE id = ?

	        filasAfectadas = stmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return filasAfectadas > 0;
	}

	public static void main(String[] args) {
	
		System.out.println(getTareaByIdUnidad(1));
	}
	
	
	
	public static ArrayList<Tarea> getExamenByIdCurso(int id_curso){
		ArrayList<Tarea> lista = new ArrayList<>();

		String sql = "SELECT id, titulo, contenido, id_curso FROM modelo_gestion.examen WHERE id_curso=?";
	    try (Connection conn = ConexionDB.conectar();
	            PreparedStatement stmt = conn.prepareStatement(sql)) {
	    	 	stmt.setInt(1, id_curso);
	            ResultSet rs = stmt.executeQuery();
	        
	            // public Examen(int id, String titulo, String contenido, int id_curso) 
	            while(rs.next()){
	            	lista.add(new Tarea(
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
	
	
	
	// ---------- MAPPER ----------
    private static Tarea mapResultSetToTarea(ResultSet rs) throws SQLException {
        return new Tarea(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("contenido"),
            rs.getInt("num_intento"),
            rs.getDate("fecha_entrega"),
            rs.getInt("id_unidad"),
            rs.getString("ruta")
        );
    }
    
	
	 // ---------- CREATE ----------
    public static boolean insertTarea(Tarea t) {
        String sql = "INSERT INTO modelo_gestion.tarea " +
                     "(id, titulo, contenido, num_intento, fecha_entrega, id_unidad, ruta) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, t.getId());
            pstmt.setString(2, t.getTitulo());
            pstmt.setString(3, t.getContenido());
            pstmt.setInt(4, t.getNum_intento());
            pstmt.setDate(5, new java.sql.Date(t.getFechaEntrega().getTime()));
            pstmt.setInt(6, t.getId_unidad());
            pstmt.setString(7, t.getRuta());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ---------- READ (GET BY ID) ----------
    public static Tarea getTareaById(int id) {
        Tarea tarea = null;
        String sql = "SELECT * FROM modelo_gestion.tarea WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                tarea = mapResultSetToTarea(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tarea;
    }
    
 // ---------- UPDATE ----------
    public static boolean updateTarea(Tarea t) {
        String sql = "UPDATE modelo_gestion.tarea SET " +
                     "titulo=?, contenido=?, num_intento=?, fecha_entrega=?, id_unidad=?, ruta=? " +
                     "WHERE id=?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, t.getTitulo());
            pstmt.setString(2, t.getContenido());
            pstmt.setInt(3, t.getNum_intento());
            pstmt.setDate(4, new java.sql.Date(t.getFechaEntrega().getTime()));
            pstmt.setInt(5, t.getId_unidad());
            pstmt.setString(6, t.getRuta());
            pstmt.setInt(7, t.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ---------- DELETE ----------
    public static boolean deleteTarea(int id) {
        String sql = "DELETE FROM modelo_gestion.tarea WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
