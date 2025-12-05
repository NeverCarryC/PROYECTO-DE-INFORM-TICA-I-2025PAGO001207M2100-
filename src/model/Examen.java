package model;

public class Examen {
private int id;
private String titulo;
private String contenido;
private int id_curso;


public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getTitulo() {
	return titulo;
}
public void setTitulo(String titulo) {
	this.titulo = titulo;
}
public String getContenido() {
	return contenido;
}
public void setContenido(String contenido) {
	this.contenido = contenido;
}
public int getId_curso() {
	return id_curso;
}
public void setId_curso(int id_curso) {
	this.id_curso = id_curso;
}
@Override
public String toString() {
	return "Examen [id=" + id + ", titulo=" + titulo + ", contenido=" + contenido + ", id_curso=" + id_curso + "]";
}
public Examen(int id, String titulo, String contenido, int id_curso) {
	super();
	this.id = id;
	this.titulo = titulo;
	this.contenido = contenido;
	this.id_curso = id_curso;
}


}
