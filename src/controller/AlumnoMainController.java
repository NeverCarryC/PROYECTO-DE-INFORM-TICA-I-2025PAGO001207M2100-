package controller;

import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

public class AlumnoMainController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private ListView<String> navigacionBar;
    @FXML
    public void initialize() {
 
        ArrayList<String> menus = new ArrayList<>();
        menus.add("Asignaturas");
        menus.add("Tareas");
        menus.add("Perfil");
        menus.add("Ajustes");
      // Lista -> ObservableList -> ListView
        ObservableList<String> menusData = FXCollections.observableArrayList(menus);
          
        navigacionBar.setItems(menusData);
        // Aquí añadimos un listener al ListView
        // Cuando el usuario selecciona "Curso", se carga la página de cursos.
        // Si selecciona "Perfil", se carga la vista de perfil.
        navigacionBar.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				// System.out.println(newValue);
				if(newValue.equals("Asignaturas")) {
					
				     FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/asignaturaListaView.fxml"));
			            try {
							Parent view = loader.load();
							rootPane.setCenter(view);
							// rootPane.setRight(view);
						} catch (IOException e) {
						
							e.printStackTrace();
						}

				}else if (newValue.equals("Perfil")) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/perfilView.fxml"));
		            try {
						Parent view = loader.load();
						 rootPane.setCenter(view);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else if (newValue.equals("Ajustes")) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ajusteView.fxml"));
		            try {
						Parent view = loader.load();
						 rootPane.setCenter(view);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else if(newValue.equals("Tareas")) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tareaListaView.fxml"));
					try {
						Parent view =loader.load();
						rootPane.setCenter(view);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
        
    }

}
