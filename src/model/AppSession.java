package model;

import java.util.ArrayList;

import db.AsignaturaCRUD;

// Guarda los datos compartido. ej: usuario logged, 
public class AppSession {

    private static Alumno alumnoLogeado;
    private static ArrayList<Asignatura> cursos;
    //  ArrayList<Curso> cursos = CursoCRUD.getCursos();

    

    
    public static void setAlumno(Alumno alumno) {
        alumnoLogeado = alumno;
    }

    public static ArrayList<Asignatura> getCursos() {
		return cursos;
	}

	public static void setCursos(ArrayList<Asignatura> cursos) {
		AppSession.cursos = cursos;
	}

	public static Alumno getAlumno() {
        return alumnoLogeado;
    }
}