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
import util.TareaCell;

public class TareaListaController {
	private int id_unidad = 1;
    @FXML
    private ListView<Tarea> tareaListView;
    @FXML
    public void initialize() {

    	ArrayList<Tarea> tareaArrayList = TareasCRUD.getTareaByIdUnidad(1);
        // Convertir a ObservableList y vincular al ListView
        ObservableList<Tarea> observableList = FXCollections.observableArrayList(tareaArrayList);
       //tareaListView = new ListView<Tarea>();
       tareaListView.setItems(observableList);

       
        //  Configuración del ListView (CellFactory)
       tareaListView.setCellFactory(param -> new TareaCell());

    }
    
}
