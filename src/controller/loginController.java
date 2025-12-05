package controller;

import java.io.IOException;

import db.AlumnoCRUD;
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
    	System.out.println("login");
    	String usuario = user.getText();
    	String passwordStr = password.getText();
    	String cargo = cargoCombo.getValue();
   	 	Alert alerta;

       
       
       
       
   	 	if (usuario.isEmpty() || passwordStr.isEmpty() || cargo == null) {
	        alerta = new Alert(Alert.AlertType.WARNING);
	        alerta.setTitle("Campos incompletos");
	        alerta.setHeaderText("Por favor, completa todos los campos.");
	        alerta.setContentText("Usuario, contraseña y cargo son obligatorios.");
	        alerta.showAndWait();
	        return;
	    }else {
	    	Alumno alumno = AlumnoCRUD.login(usuario, passwordStr);
	    	
			if(alumno!=null) {
				// 方法1:用static 变量来存储当前登录用户信息
				  AppSession.setAlumno(alumno);
				  
				  // 方法1： controller 之间传递信息
				  // 还有一个方法可以获取新页面的控制器
				  // FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/alumnoMainView.fxml"));
				  // Parent root = loader.load();
				// 获取新 Controller
				// AlumnoMainController controller = loader.getController();
				// 向新控制器传递数据
				// controller.initData(alumno, cargo)
			        // Cerrar la ventana actual
			        Stage currentStage = (Stage) loginBtn.getScene().getWindow();
			        currentStage.close();
			        
			        // abre la ventana de /fxml/alumnoMainView.fxml
			        try {
						Parent root = FXMLLoader.load(getClass().getResource("/fxml/alumnoMainView.fxml"));
						Stage stage = new Stage();
						stage.setScene(new Scene(root,1000,600));
						stage.show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        
			}else {
			    alerta = new Alert(Alert.AlertType.WARNING);
		        alerta.setTitle("Error");
		        alerta.setHeaderText("Por favor, introduce cuenta y contraseña otra vez.");
		        alerta.setContentText("Usuario, contraseña y cargo no son correctos.");
		        alerta.showAndWait();
			}
		}
    	
    	
    }

    @FXML
    void registrar(ActionEvent event) {

    }

    @FXML
    void resetPassword(ActionEvent event) {

    }
    
    @FXML
    public void initialize() {
//    	 Image image = new Image(getClass().getResource("/img/logo.png").toExternalForm());
//    	 logo.setImage(image);
    	 cargoCombo.getItems().addAll("PROFESOR", "ALUMNO");
//    
    }

}
