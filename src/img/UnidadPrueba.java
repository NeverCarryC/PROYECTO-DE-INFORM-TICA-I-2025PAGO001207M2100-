package img;

import java.util.ArrayList;

public class UnidadPrueba {
    private int id;
    private String nombre;
    private ArrayList<String> materiales;

    public UnidadPrueba(int id, String nombre, ArrayList<String> materiales) {
        this.id = id;
        this.nombre = nombre;
        this.materiales = materiales;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public ArrayList<String> getMateriales() { return materiales; }

    @Override
    public String toString() {
        return nombre;
    }
}
