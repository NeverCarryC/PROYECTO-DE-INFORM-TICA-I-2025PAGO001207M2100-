package model;

import java.sql.Date;

public class Matricula {
private int id;
private int id_alumno;
private int id_curso;
 private Date fecha_matricula;
 public int getId() {
	return id;
 }
 public void setId(int id) {
	this.id = id;
 }
 public int getId_alumno() {
	return id_alumno;
 }
 public void setId_alumno(int id_alumno) {
	this.id_alumno = id_alumno;
 }
 public int getId_curso() {
	return id_curso;
 }
 public void setId_curso(int id_curso) {
	this.id_curso = id_curso;
 }
 public Date getFecha_matricula() {
	return fecha_matricula;
 }
 public void setFecha_matricula(Date fecha_matricula) {
	this.fecha_matricula = fecha_matricula;
 }
 @Override
 public String toString() {
	return "Matricula [id=" + id + ", id_alumno=" + id_alumno + ", id_curso=" + id_curso + ", fecha_matricula="
			+ fecha_matricula + "]";
 }
 public Matricula(int id, int id_alumno, int id_curso, Date fecha_matricula) {
	super();
	this.id = id;
	this.id_alumno = id_alumno;
	this.id_curso = id_curso;
	this.fecha_matricula = fecha_matricula;
 }
 
 
}
