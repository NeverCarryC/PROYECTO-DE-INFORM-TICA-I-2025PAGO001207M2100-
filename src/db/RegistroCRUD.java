package db;

import java.sql.*;
import java.util.ArrayList;

import model.Alumno;
import model.AppSession;
import model.RegistroExamen;

public class RegistroCRUD {

    // ==========================================
    //  1. [Para estudiantes] Enviar tarea (INSERTAR)
    // ==========================================
    public static RegistroExamen insertRegistroExamen(RegistroExamen registro) {
        String sql = "INSERT INTO modelo_gestion.registro_examen (id_examen, id_alumno, id_profesor, nota, comentario, ruta_archivo) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.conectar(); 
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, registro.getId_examen());
            pstmt.setInt(2, registro.getId_alumno());
            pstmt.setInt(3, registro.getId_profesor());
            
            if (registro.getNota() == null) pstmt.setNull(4, Types.DOUBLE);
            else pstmt.setDouble(4, registro.getNota());
            
            pstmt.setString(5, registro.getComentario());
            pstmt.setString(6, registro.getRuta_archivo());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        registro.setId(generatedKeys.getInt(1)); 
                        return registro;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    // ==========================================
    //  2. 2. [Para estudiantes] Ver su historial de entrega (para determinar si ha entregado).
    // ==========================================
    public static ArrayList<RegistroExamen> getRegistrosByAlumnoAndExamen(int idAlumno, int idExamen) {
        ArrayList<RegistroExamen> lista = new ArrayList<>();
        String sql = "SELECT id, id_examen, id_alumno, id_profesor, nota, comentario, ruta_archivo FROM modelo_gestion.registro_examen WHERE id_alumno = ? AND id_examen = ?";
        try (Connection conn = ConexionDB.conectar(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAlumno);
            pstmt.setInt(2, idExamen);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapResultSetToRegistro(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ==========================================
    // 3. [Para uso del profesor] (para la lista de calificación)
    // ==========================================
    public static ArrayList<RegistroExamen> getRegistrosPorExamen(int idExamen) {
        ArrayList<RegistroExamen> lista = new ArrayList<>();
        String sql = "SELECT id, id_examen, id_alumno, id_profesor, nota, comentario, ruta_archivo FROM modelo_gestion.registro_examen WHERE id_examen = ?";
        try (Connection conn = ConexionDB.conectar(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idExamen);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                RegistroExamen reg = mapResultSetToRegistro(rs);
               //  String nombre = AppSession.getAlumno().getNombre();
                Alumno alumno = AlumnoCRUD.getAlumnoById(reg.getId_alumno());
                String nombre = alumno.getNombre();
                reg.setNombreAlumno(nombre); // 记得这里换成你真实的查名字函数
                lista.add(reg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ==========================================
    //  4. [Para profesores] Actualizar calificaciones y comentarios  (UPDATE)
    // ==========================================
    public static void updateNotaYComentario(int id, Double nota, String comentario) {
        String sql = "UPDATE modelo_gestion.registro_examen SET nota = ?, comentario = ? WHERE id = ?";
        try (Connection conn = ConexionDB.conectar(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (nota == null) pstmt.setNull(1, Types.DOUBLE);
            else pstmt.setDouble(1, nota);
            pstmt.setString(2, comentario);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static RegistroExamen mapResultSetToRegistro(ResultSet rs) throws SQLException {
        return new RegistroExamen(
            rs.getInt("id"),
            rs.getInt("id_examen"),
            rs.getInt("id_alumno"),
            rs.getInt("id_profesor"),
            (Double) rs.getObject("nota"),
            rs.getString("comentario"),
            rs.getString("ruta_archivo")
        );
    }
}