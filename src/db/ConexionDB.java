package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

	 private static final String URL = "jdbc:mysql://localhost:3306/modelo_gestion";
	    private static final String USUARIO = "root";
	    private static final String PASSWORD = "";

	    public static Connection conectar() {
	        try {
	        	
	            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
	           
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConexionDB.conectar();
	}


}
