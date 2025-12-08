package model;

import java.sql.Date; // 如果你在模型中处理日期时间，可能需要导入

public class RegistroExamen {

    private int id;
    private int id_examen;
    private int id_alumno;
    private int id_profesor;
    private Double nota; // 成绩可能是 null，使用 Double
    private String comentario; // 备注可能是 null
    private String ruta_archivo; // 文件路径可能是 null

    // 完整的构造函数 (假设所有字段都可能从 DB 中获取)
    public RegistroExamen(int id, int id_examen, int id_alumno, int id_profesor, Double nota, String comentario, String ruta_archivo) {
        this.id = id;
        this.id_examen = id_examen;
        this.id_alumno = id_alumno;
        this.id_profesor = id_profesor;
        this.nota = nota;
        this.comentario = comentario;
        this.ruta_archivo = ruta_archivo;
    }

    // 插入时使用的构造函数 (ID 通常由数据库自动生成)
    public RegistroExamen(int id_examen, int id_alumno, int id_profesor, Double nota, String comentario, String ruta_archivo) {
        this(0, id_examen, id_alumno, id_profesor, nota, comentario, ruta_archivo);
    }

    // --- Getters and Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_examen() { return id_examen; }
    public void setId_examen(int id_examen) { this.id_examen = id_examen; }

    public int getId_alumno() { return id_alumno; }
    public void setId_alumno(int id_alumno) { this.id_alumno = id_alumno; }

    public int getId_profesor() { return id_profesor; }
    public void setId_profesor(int id_profesor) { this.id_profesor = id_profesor; }

    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getRuta_archivo() { return ruta_archivo; }
    public void setRuta_archivo(String ruta_archivo) { this.ruta_archivo = ruta_archivo; }

    @Override
    public String toString() {
        return "RegistroExamen [id=" + id + ", examen=" + id_examen + ", alumno=" + id_alumno + ", nota=" + nota + "]";
    }
}