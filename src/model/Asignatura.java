package model;
import java.sql.Date;


public class Asignatura {
	public Asignatura(int id, String nombre, int id_profesor, Date createTime,  String descripcion) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.id_profesor = id_profesor;
		this.createTime = createTime;
		this.descripcion= descripcion;
	}
	
	private int id;
	private String nombre;
	private int id_profesor;
	private Date createTime;
	private String descripcion;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getId_profesor() {
		return id_profesor;
	}
	public void setId_profesor(int id_profesor) {
		this.id_profesor = id_profesor;
	}
	@Override
	public String toString() {
		return nombre;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	
}
