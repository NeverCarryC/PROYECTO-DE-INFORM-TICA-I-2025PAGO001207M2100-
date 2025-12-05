package model;

public class Modulo {
	private int id;
	private String titulo;
	private String ruta_archivo;
	private int id_categoria;
	@Override
	public String toString() {
		return titulo;
	}
	public Modulo(int id, String titulo, String ruta_archivo, int id_categoria) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.ruta_archivo = ruta_archivo;
		this.id_categoria = id_categoria;
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
	public String getRuta_archivo() {
		return ruta_archivo;
	}
	public void setRuta_archivo(String ruta_archivo) {
		this.ruta_archivo = ruta_archivo;
	}
	public int getId_categoria() {
		return id_categoria;
	}
	public void setId_categoria(int id_categoria) {
		this.id_categoria = id_categoria;
	}
	
	
	

}
