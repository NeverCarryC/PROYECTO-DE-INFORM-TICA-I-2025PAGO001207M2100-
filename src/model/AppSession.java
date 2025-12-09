package model;

import java.util.ArrayList;

import db.AsignaturaCRUD;

// Guarda los datos compartido. ej: usuario logged, 
public class AppSession {

    private static Alumno alumnoLogeado;
  
    private static Profesor profesorLogeado;
    private static ArrayList<Asignatura> cursos;
    private static boolean isAlumno; 
    


    public static ArrayList<Asignatura> getCursos() {
		return cursos;
	}

	public static void setCursos(ArrayList<Asignatura> cursos) {
		AppSession.cursos = cursos;
	}
	
	
	
    public static void setAlumno(Alumno alumno) {
    	
        alumnoLogeado = alumno;
        setIsAlumno(true);
    }
	public static Alumno getAlumno() {
        return alumnoLogeado;
    }

	public static Profesor getProfesor() {
		return profesorLogeado;
	
	}

	public static void setProfesor(Profesor profesorLogeado) {
		setIsAlumno(false);
		AppSession.profesorLogeado = profesorLogeado;
	}

	public static boolean isAlumno() {
		return isAlumno;
	}

	public static void setIsAlumno(boolean isAlumno) {
		AppSession.isAlumno = isAlumno;
	}




}