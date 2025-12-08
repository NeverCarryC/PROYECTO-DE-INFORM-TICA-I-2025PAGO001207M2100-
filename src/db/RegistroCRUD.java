package db;

import java.sql.*;
import java.util.ArrayList;

import model.Alumno;
import model.AppSession;
import model.RegistroExamen;

public class RegistroCRUD {

    // ==========================================
    //  1. [学生用] 提交作业 (INSERT)
    // ==========================================
    public static RegistroExamen insertRegistroExamen(RegistroExamen registro) {
        String sql = "INSERT INTO modelo_gestion.registro_examen (id_examen, id_alumno, id_profesor, nota, comentario, ruta_archivo) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.conectar(); 
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // 关键：获取生成的ID
            
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
                        registro.setId(generatedKeys.getInt(1)); // 设置回生成的 ID
                        return registro;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 失败返回 null
    }

    // ==========================================
    //  2. [学生用] 查看自己的提交记录 (用于判断是否交过)
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
    //  3. [老师用] 获取某次作业的所有提交 (用于评分列表)
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
                // 老师端需要看名字
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
    //  4. [老师用] 更新分数和评语 (UPDATE)
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

    // 辅助方法：映射 ResultSet
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