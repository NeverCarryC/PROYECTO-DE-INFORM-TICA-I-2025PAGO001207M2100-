package model;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.mysql.cj.conf.StringProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Unidad {
    private int id;
    private SimpleStringProperty nombre;
    private SimpleStringProperty descripcion;
    private int id_asignatura;
    private ObservableList<Modulo> modulos;

    public Unidad(int id, String nombre, String descripcion, int id_asignatura) {
        this.id = id;
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.setId_asignatura(id_asignatura);
       
    }
    
    
    public Unidad(int id, String nombre, String descripcion, ArrayList<Modulo> modulos,int id_asignatura) {
        this.id = id;
        this.setId_asignatura(id_asignatura);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.modulos = FXCollections.observableArrayList(modulos);
    }

    // Getters y Setters con Property
    public int getId() { return id; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public SimpleStringProperty nombreProperty() { return nombre; }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    public SimpleStringProperty descripcionProperty() { return descripcion; }

    public ObservableList<Modulo> getModulos() { return modulos; }


	public int getId_asignatura() {
		return id_asignatura;
	}


	public void setId_asignatura(int id_asignatura) {
		this.id_asignatura = id_asignatura;
	}
}
