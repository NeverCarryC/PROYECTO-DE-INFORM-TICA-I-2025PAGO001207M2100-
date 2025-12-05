package model;

public class Registro_examen {
private int id;
private int id_examen;
private int id_alumno;
private int id_profesor;
private double nota;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getId_examen() {
	return id_examen;
}
public void setId_examen(int id_examen) {
	this.id_examen = id_examen;
}
public int getId_alumno() {
	return id_alumno;
}
public void setId_alumno(int id_alumno) {
	this.id_alumno = id_alumno;
}
public int getId_profesor() {
	return id_profesor;
}
public void setId_profesor(int id_profesor) {
	this.id_profesor = id_profesor;
}
public double getNota() {
	return nota;
}
public void setNota(double nota) {
	this.nota = nota;
}
@Override
public String toString() {
	return "Registro_examen [id=" + id + ", id_examen=" + id_examen + ", id_alumno=" + id_alumno + ", id_profesor="
			+ id_profesor + ", nota=" + nota + "]";
}
public Registro_examen(int id, int id_examen, int id_alumno, int id_profesor, double nota) {
	super();
	this.id = id;
	this.id_examen = id_examen;
	this.id_alumno = id_alumno;
	this.id_profesor = id_profesor;
	this.nota = nota;
}


}
