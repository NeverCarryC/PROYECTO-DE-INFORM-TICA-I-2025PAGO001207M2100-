package controller;

import java.io.IOException;

import db.AlumnoCRUD;
import db.ProfesorCRUD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Alumno;
import model.AppSession;
import model.Profesor;

public class loginController {

    @FXML
    private Button RegistrarBtn;

    @FXML
    private ComboBox<String> cargoCombo;

    @FXML
    private Button loginBtn;

    @FXML
    private ImageView logo;

    @FXML
    private PasswordField password;

    @FXML
    private Hyperlink resetLink;

    @FXML
    private TextField user;

    @FXML
    void login(ActionEvent event) {
    	String usuario = user.getText();
    	String passwordStr = password.getText();
    	String cargo = cargoCombo.getValue();
    	
 
   	 	
   	 	if (usuario.isEmpty() || passwordStr.isEmpty() || cargo == null) {
   	 		showErrorAlerta("Campos incompletos", "Por favor, completa todos los campos.","Usuario, contraseña y cargo son obligatorios." );
	        return;
	    }else {
	    	// Login by alumno

	    	if(cargo.equals("ALUMNO")) {
	    		Alumno alumno = AlumnoCRUD.login(usuario, passwordStr);
		    	
				if(alumno!=null ) {
					  	AppSession.setAlumno(alumno);// Guardar las informacion compartido en todas las paginas 
				        // abre la ventana de /fxml/alumnoMainView.fxml
					  	
				        toMainView();
				        
				}else {
					showErrorAlerta("Error","Por favor, introduce cuenta y contraseña otra vez.","Usuario, contraseña y cargo no son correctos." );
				}
				// Login with profesor
	    	}else {
	    		Profesor profesor = ProfesorCRUD.getProfesorByNombreYPassword(usuario,passwordStr);
	    		if(profesor!=null) {
	    			AppSession.setProfesor(profesor);
	    			toMainView();
	    			
	    		}else {
	    			showErrorAlerta("Error","Por favor, introduce cuenta y contraseña otra vez.","Usuario, contraseña y cargo no son correctos." );
				}
	    	}
	    	
		}
    	
    	
    }

    private void toMainView() {
    	Stage currentStage = (Stage) loginBtn.getScene().getWindow();
        currentStage.close();
        
        try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/mainView.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(root,1000,600));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showErrorAlerta(String title, String headerText, String contentText) {
    	Alert alerta;
		// TODO Auto-generated method stub
    	alerta = new Alert(Alert.AlertType.WARNING);
	    alerta.setTitle(title);
	    alerta.setHeaderText(headerText);
	    alerta.setContentText(contentText);
	    alerta.showAndWait();
	}

	@FXML
    void registrar(ActionEvent event) {

    }

    @FXML
    void resetPassword(ActionEvent event) {

    }
    
    @FXML
    public void initialize() {
    	 cargoCombo.getItems().addAll("PROFESOR", "ALUMNO"); 
    }
    
    

}
