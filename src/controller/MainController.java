package controller;

import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.ListItem;


public class MainController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private ListView<ListItem> navigacionBar;
    @FXML
    public void initialize() {
    	  ObservableList<ListItem> items = FXCollections.observableArrayList(
    	            new ListItem("Courses", "/img/Courses.png"),
    	            new ListItem("Tareas", "/img/Tareas.png"),
    	            new ListItem("Perfil", "/img/Perfil.png"),
    	            new ListItem("Calendario", "/img/Calendario.png"),
    	            new ListItem("Historial", "/img/Historial.png")
    	        );
    	  navigacionBar.setItems(items);
    	  navigacionBar.setCellFactory(lv -> new ListCell<ListItem>() {

              private final ImageView imageView = new ImageView();
              private final Label label = new Label();
              private final VBox contenBox = new VBox(10, imageView, label);
             // private final HBox content = new HBox(10, imageView, label);

              {
                  imageView.setFitWidth(40);
                  imageView.setFitHeight(40);
                  imageView.setPreserveRatio(true);

                  contenBox.setAlignment(Pos.CENTER);
              }

              @Override
              protected void updateItem(ListItem item, boolean empty) {
                  super.updateItem(item, empty);

                  if (empty || item == null) {
                      setGraphic(null);
                  } else {
                      imageView.setImage(
                          new Image(getClass().getResourceAsStream(item.getImagePath()))
                      	
                      		//new Image(getClass().getResourceAsStream("/img/icon.png"))
                      );
                      label.setText(item.getText());
                      setGraphic(contenBox);
                  }
              }
          });
    	  
//        ArrayList<String> menus = new ArrayList<>();
//        menus.add("Asignaturas");
//        menus.add("Tareas");
//        menus.add("Perfil");
//        menus.add("Ajustes");
      // Lista -> ObservableList -> ListView
//        ObservableList<String> menusData = FXCollections.observableArrayList(menus);
//          
//        navigacionBar.setItems(menusData);
        
        
        // Aquí añadimos un listener al ListView
        // Cuando el usuario selecciona "Curso", se carga la página de cursos.
        // Si selecciona "Perfil", se carga la vista de perfil.
    	  
    	  navigacionBar.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ListItem>() {

			@Override
			public void changed(ObservableValue<? extends ListItem> observable, ListItem oldValue, ListItem newValue) {
				// TODO Auto-generated method stub
				
				String selectedName = newValue.getText();
				if(selectedName.equals("Courses")) {
					
				     FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/asignaturaListaView.fxml"));
			            try {
							Parent view = loader.load();
							rootPane.setCenter(view);
							// rootPane.setRight(view);
						} catch (IOException e) {
						
							e.printStackTrace();
						}

				}else if (selectedName.equals("Perfil")) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/perfilView.fxml"));
		            try {
						Parent view = loader.load();
						 rootPane.setCenter(view);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else if (selectedName.equals("Ajustes")) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ajusteView.fxml"));
		            try {
						Parent view = loader.load();
						 rootPane.setCenter(view);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else if(selectedName.equals("Tareas")) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tareaListaView.fxml"));
					try {
						Parent view =loader.load();
						rootPane.setCenter(view);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}});
    	  
    	  
    	  
    	  
//        navigacionBar.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ListItem>() {
//
//			@Override
//			public void changed(ObservableValue<? extends ListItem> observable, String oldValue, String newValue) {
//				// TODO Auto-generated method stub
//				// System.out.println(newValue);
//				if(newValue.equals("Asignaturas")) {
//					
//				     FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/asignaturaListaView.fxml"));
//			            try {
//							Parent view = loader.load();
//							rootPane.setCenter(view);
//							// rootPane.setRight(view);
//						} catch (IOException e) {
//						
//							e.printStackTrace();
//						}
//
//				}else if (newValue.equals("Perfil")) {
//					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/perfilView.fxml"));
//		            try {
//						Parent view = loader.load();
//						 rootPane.setCenter(view);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}else if (newValue.equals("Ajustes")) {
//					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ajusteView.fxml"));
//		            try {
//						Parent view = loader.load();
//						 rootPane.setCenter(view);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}else if(newValue.equals("Tareas")) {
//					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tareaListaView.fxml"));
//					try {
//						Parent view =loader.load();
//						rootPane.setCenter(view);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		});
        navigacionBar.getSelectionModel().select(0);
    }

}
