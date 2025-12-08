package model;

import java.sql.Date;

public class Tarea {
private int id;
private String titulo;
private String contenido;
private int num_intento;
private Date fechaEntrega;
private int id_unidad;
private String ruta;


public Tarea(int id, String titulo, String contenido, int num_intento, Date fechaEntrega, int id_unidad, String ruta) {
	super();
	this.id = id;
	this.titulo = titulo;
	this.contenido = contenido;
	this.num_intento = num_intento;
	this.fechaEntrega = fechaEntrega;
	this.id_unidad = id_unidad;
	this.ruta = ruta;
}
public int getNum_intento() {
	return num_intento;
}
public void setNum_intento(int num_intento) {
	this.num_intento = num_intento;
}
public Date getFechaEntrega() {
	return fechaEntrega;
}
public void setFechaEntrega(Date fechaEntrega) {
	this.fechaEntrega = fechaEntrega;
}
public int getId_unidad() {
	return id_unidad;
}
public void setId_unidad(int id_unidad) {
	this.id_unidad = id_unidad;
}
public Tarea(int id, String titulo, String contenido, int id_unidad, String ruta) {
	super();
	this.id = id;
	this.titulo = titulo;
	this.contenido = contenido;
	this.id_unidad = id_unidad;
	this.ruta = ruta;
}
public String getRuta() {
	return ruta;
}
public void setRuta(String ruta) {
	this.ruta = ruta;
}
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
public int getid_unidad() {
	return id_unidad;
}
public void setid_unidad(int id_unidad) {
	this.id_unidad = id_unidad;
}
@Override
public String toString() {
	return "Tarea [id=" + id + ", titulo=" + titulo + ", contenido=" + contenido + ", num_intento=" + num_intento
			+ ", fechaEntrega=" + fechaEntrega + ", id_unidad=" + id_unidad + ", ruta=" + ruta + "]";
}
public Tarea(int id, String titulo, String contenido, int id_unidad) {
	super();
	this.id = id;
	this.titulo = titulo;
	this.contenido = contenido;
	this.id_unidad = id_unidad;
}


}
