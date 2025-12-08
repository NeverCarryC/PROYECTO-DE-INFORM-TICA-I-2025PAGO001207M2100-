package controller;

import java.io.IOException;
import java.util.ArrayList;

import db.AsignaturaCRUD;
import db.TareasCRUD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Cell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import model.AppSession;
import model.Asignatura;
import model.Tarea;

public class TareaListaController {
	private int id_unidad = 1;
    @FXML
    private ListView<Tarea> tareaListView;
    @FXML
    public void initialize() {

    	ArrayList<Tarea> tareaArrayList = TareasCRUD.getTareaByIdUnidad(1);
        // Convertir a ObservableList y vincular al ListView
        ObservableList<Tarea> observableList = FXCollections.observableArrayList(tareaArrayList);
       System.out.println(observableList);
       //tareaListView = new ListView<Tarea>();
       tareaListView.setItems(observableList);

        //  Configuración del ListView (CellFactory)
        tareaListView.setCellFactory(cv ->{
        	ListCell<Tarea> cell = new ListCell<Tarea>() {

        		@Override
        		protected void updateItem(Tarea item, boolean empty) {
        			super.updateItem(item, empty);
        		    if (empty || item == null) {
                        setText(null);
                        // Si la fila está vacía, no mostramos el menú de eliminar/editar
                        setContextMenu(null); 
                    } else {
                        setText(item.toString());
                        // Si hay datos, asignamos el menú de acciones específicas
                        // setContextMenu(menuEditDelete);
                        setContextMenu(null); 
                    }
        			
        		}
        	};
        	
        	cell.setOnMouseClicked(event -> {
        	       if (!cell.isEmpty() && event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                       if (event.getClickCount() == 2) {
                          // abrirDetalleCurso(cell.getItem());
                    	   System.out.println("Clicked 2 veces");
                    	   Tarea tareaSeleccionadaTarea  = cell.getItem();
                    	   FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tareaDetalleVista.fxml"));
                    	   try {
							Parent view = loader.load();
							TareaDetalleController controller = loader.getController();
							// controller.CargaDatosDeTarea(tareaSeleccionadaTarea);
							controller.cargaDatosIniciarVista(tareaSeleccionadaTarea);
							BorderPane rootPane = (BorderPane) tareaListView.getScene().getRoot();
							rootPane.setCenter(view);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                       }
                   }
        	});
        	
        	return cell;
        });

    }
    
}
