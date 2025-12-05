package model;

public class CursoPrueba {
    private int id;
    private String nombre;

    public CursoPrueba(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre; // ListView puede llamar la funcion de toString para mostrar en la vista
    }
}
