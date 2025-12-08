package model;

public class RegistroExamen {

    // --- 数据库对应字段 ---
    private int id;
    private int id_examen;
    private int id_alumno;
    private int id_profesor;
    private Double nota;         // 使用 Double 类型，允许为 null (表示未评分)
    private String comentario;   // 评语
    private String ruta_archivo; // 文件路径

    // --- 【关键新增】UI 显示专用字段 (数据库无此列) ---
    // 这个字段用于在老师评分界面的表格中直接显示学生名字
    private String nombreAlumno; 

    // --- 构造函数 ---
    public RegistroExamen(int id, int id_examen, int id_alumno, int id_profesor, Double nota, String comentario, String ruta_archivo) {
        this.id = id;
        this.id_examen = id_examen;
        this.id_alumno = id_alumno;
        this.id_profesor = id_profesor;
        this.nota = nota;
        this.comentario = comentario;
        this.ruta_archivo = ruta_archivo;
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

    // --- 【新增字段的 Getter/Setter】 ---
    public String getNombreAlumno() { return nombreAlumno; }
    public void setNombreAlumno(String nombreAlumno) { this.nombreAlumno = nombreAlumno; }

    @Override
    public String toString() {
        return "RegistroExamen [id=" + id + ", nota=" + nota + ", comentario=" + comentario + "]";
    }
}