package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	// codigos por defecto 
	//@Override
//	public void start(Stage primaryStage) {
//		try {
//			BorderPane root = new BorderPane();
//			Scene scene = new Scene(root,1200,800);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			primaryStage.setScene(scene);
//			primaryStage.show();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
    @Override
    public void start(Stage stage) throws Exception {

    	// 1. Cargar el archivo FXML que contiene el diseño de la interfaz de usuario.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        
        // 2. Cargar y procesar el archivo FXML.
        // Es decir, crea todos los controles de la interfaz y devuelve el nodo raíz (root).
        Parent root = loader.load();
        // 3. Crear una escena utilizando el nodo raíz y establecer el tamaño de la ventana.
        Scene scene = new Scene(root, 1000,600);
        stage.setTitle("JavaFX camupus");
        Image icon = new Image(getClass().getResourceAsStream("/img/icon.png"));
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
