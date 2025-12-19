package util;


import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

import controller.TareaDetalleController;
import db.AsignaturaCRUD;
import db.ModuloCRUD;
import db.TareasCRUD;
import db.UnidadCRUD;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import model.AppSession;
import model.Asignatura;
import model.Modulo;
import model.Tarea;
import model.Unidad;

public class TareaCell extends ListCell<Tarea> {

    // Componentes de la UI
    private final HBox hbox = new HBox();
    private final Label label = new Label();
    private final Pane spacer = new Pane();
    private final Button btnOpen = new Button("Abrir");
    private final Button btnEdit = new Button("Editar");
    private final Button btnDelete = new Button("Eliminar");
    private final Button btnGrade = new Button("Calificación");
    
    // La funcion de constructor:
    // 1. Definir layout
    // 2. Definir evento
    public TareaCell() {
        super();

        // --- 1. En caso de que el usuario sea profesor, puede abrir, editar, eliminar y calificar.
        // ---     En caso de que sea estudiante, solo puede visualizar la información.
        if(!AppSession.isAlumno()) {        	
        	hbox.getChildren().addAll(label, spacer, btnOpen, btnEdit, btnDelete, btnGrade);
        }else {
        	hbox.getChildren().addAll(label, spacer, btnOpen);
        }
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(5);
        HBox.setHgrow(spacer, Priority.ALWAYS); 
        
        // --- 2. Eventos de los botones ---
        btnOpen.setOnAction(event -> {
            Tarea item = getItem();
            if (item != null) {
                abrirVistaDetalle(item); 
            }
        });

        btnEdit.setOnAction(event -> {
        	mostrarDialogAgregar(getItem());
        });
        
       
        btnDelete.setOnAction(event -> {
            Tarea item = getItem();
            getListView().getItems().remove(item); 
            // Ojo: getListView() devolver The ListView associated with this Cell.
            // Es decir, devolver la lista de tareas -> private ListView<Tarea> tareaListView;
            TareasCRUD.deleteTarea(item.getId()); 
        });

        btnGrade.setOnAction(event -> {
            Tarea item = getItem();
            if (item != null) {
                try {
                    // Carga la vista de calificacionVista.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/calificacionVista.fxml"));
                    Parent view = loader.load();
                    
                    // Obtener el controller CalificacionController
                    controller.CalificacionController ctrl = loader.getController();
                    // Carga los datos 
                    ctrl.initData(item);
                    
                    // Cambiar el contenido central del BorderPane
                    if (getScene() != null) {
                        BorderPane rootPane = (BorderPane) getScene().getRoot();
                        rootPane.setCenter(view);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // --- 3. Evento de doble clic ---
        this.setOnMouseClicked(event -> {
            if (!isEmpty() && getItem() != null && event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    abrirVistaDetalle(getItem());
                }
            }
        });
    }

    @Override
    // La funcion de updateItem(...):
    // Actualiza el contenido de la celda(FILA) según el elemento asociado
    protected void updateItem(Tarea item, boolean empty) {
    	// item es modelo en la fila actual
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            ContextMenu menuGlobal = createContextMenu();
            setContextMenu(menuGlobal);    
        } else {
            setText(null); 
            String info = item.getTitulo();
            if (item.getFechaEntrega() != null) {
                info += " (Entrega: " + item.getFechaEntrega().toString() + ")";
            }
            info += " [Intentos: " + item.getNum_intento() + "]";
            label.setText(info);
            setGraphic(hbox);
        }
    }

    
    private ContextMenu createContextMenu() {
    	ContextMenu menuAdd = new ContextMenu();
    	MenuItem addItem = new MenuItem("Nueva Tarea");
    	addItem.setOnAction(e -> mostrarDialogAgregar(null));
    	menuAdd.getItems().add(addItem);
    	return menuAdd;
	}

	private void mostrarDialogAgregar(Tarea tareaEditar) {
		if(!AppSession.isAlumno()) {
			Dialog<Tarea> dialog = createTareaFormDialog(tareaEditar);
		    dialog.showAndWait().ifPresent(t -> {
		    	if(tareaEditar!=null) {
		    		TareasCRUD.updateTarea(t);
		    	}else {
		    		 TareasCRUD.insertTarea(t);
		    	}
	                    
	            if(tareaEditar!=null) {
	            	
	            	 int index = getListView().getItems().indexOf(tareaEditar);
	            	    if (index >= 0) {
	            	        getListView().getItems().set(index, t);
	            	    }
	            }else {
	            	getListView().getItems().add(t);
	 	            getListView().getSelectionModel().select(t);
	            }
	        });
		}
	
	}
	
	

	private Dialog<Tarea> createTareaFormDialog(Tarea tareaEditar) {
		Dialog<Tarea> dialog = new Dialog<>();
		dialog.setTitle(tareaEditar == null ? "Nueva tarea" : "Editar tarea");
		ButtonType saveBtn = new ButtonType("Guardar", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);grid.setVgap(10);grid.setPadding(new Insets(20));
		

	    /* ===========================
	       1. Campos
	    ============================ */
		// titulo
		TextField tituloField = new TextField();
		tituloField.setPromptText("Titulo de tarea");
		if(tareaEditar!=null) tituloField.setText(tareaEditar.getTitulo());
		// Contenido
	     TextField contenidoField = new TextField();
	     contenidoField.setPromptText("Contenido");
	     if(tareaEditar!=null) contenidoField.setText(tareaEditar.getContenido());
	     
	     // Num intentos
	     Spinner<Integer> intentoSpin = new Spinner<>(1, 10, 1);
	     if (tareaEditar != null) intentoSpin.getValueFactory().setValue(tareaEditar.getNum_intento());

	     // Fecha Entrega
	     DatePicker fechaPicker = new DatePicker();
	     if (tareaEditar != null) {
	    	 Date d = tareaEditar.getFechaEntrega();
	    	    if (d != null) {
	    	        fechaPicker.setValue(
	    	        		 d.toLocalDate()
	    	        );
	    	    }
	     }
	     
	     /* --------------------------
	       Compo Asignatura
	    --------------------------- */
	    
	     ComboBox<Asignatura> asigCombo = new ComboBox<>();
	     asigCombo.getItems().addAll(AsignaturaCRUD.getAllAsignaturas());
	     asigCombo.setConverter(new StringConverter<Asignatura>() {

			@Override
			public String toString(Asignatura a) {
				return a == null ? "" : a.getNombre(); 
			}

			@Override
			public Asignatura fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
	        });
	     
	     if (tareaEditar != null) {
	    	  int idUnidad = tareaEditar.getid_unidad();
	    	  Unidad unidad = UnidadCRUD.getUnidadById(idUnidad);
	    	  int selectedIdAsignatura = unidad.getId_asignatura();
	    	  Asignatura asignatura = AsignaturaCRUD.getAsignaturaById(selectedIdAsignatura);
	    	  asigCombo.getSelectionModel().select(asignatura);
	     }
	     /* --------------------------
	       Compo Unidad (depende de Asignatura)
	    --------------------------- */
	     
	     ComboBox<Unidad> unitCombo = new ComboBox<>();
	     if (tareaEditar != null) {
	    	 int selectedIdUnidad = tareaEditar.getid_unidad();
	    	 Unidad unidad = UnidadCRUD.getUnidadById(selectedIdUnidad);
	    	 int selectedIdAsignatura = unidad.getId_asignatura();
	    	 Asignatura asignatura = AsignaturaCRUD.getAsignaturaById(selectedIdAsignatura);
	    	 
	         unitCombo.getItems().addAll(
	        		 UnidadCRUD.getUnidadsByIdAsignatura(asignatura.getId())
	         );

	         unitCombo.getSelectionModel().select(
	             UnidadCRUD.getUnidadById(tareaEditar.getId_unidad())
	         );
	     }
	     
	     asigCombo.valueProperty().addListener((obs, oldV, newV) -> {
	         unitCombo.getItems().clear();
	         if (newV != null) {
	             unitCombo.getItems().addAll(
	                 UnidadCRUD.getUnidadsByIdAsignatura(newV.getId())
	             );
	         }
	     });
	     

	     unitCombo.setConverter(new StringConverter<Unidad>() {
	         @Override
	         public String toString(Unidad u) {
	             return u == null ? "" : u.getNombre();
	         }

	         @Override
	         public Unidad fromString(String s) { return null; }
	     });
	     
		// 2. Campo ruta
		 TextField pathField = new TextField();
	     pathField.setEditable(false); // 只读，只能通过按钮修改
	     if (tareaEditar != null) pathField.setText(tareaEditar.getRuta());
	     Button fileBtn = new Button("Seleccionar Archivo");
	     fileBtn.setOnAction(e -> {
	            File f = new FileChooser().showOpenDialog(dialog.getDialogPane().getScene().getWindow());
	            if(f != null) pathField.setText(f.getAbsolutePath());
	        });
	    

	     /* ===========================
	       2. Layout (GridPane)
	    ============================ */
	    grid.add(new Label("Título:"), 0, 0);
	    grid.add(tituloField, 1, 0);

	    grid.add(new Label("Contenido:"), 0, 1);
	    grid.add(contenidoField, 1, 1);

	    grid.add(new Label("Intentos:"), 0, 2);
	    grid.add(intentoSpin, 1, 2);

	    grid.add(new Label("Fecha entrega:"), 0, 3);
	    grid.add(fechaPicker, 1, 3);

	    grid.add(new Label("Asignatura:"), 0, 4);
	    grid.add(asigCombo, 1, 4);

	    grid.add(new Label("Unidad:"), 0, 5);
	    grid.add(unitCombo, 1, 5);

	    grid.add(new Label("Archivo:"), 0, 6);
	    grid.add(pathField, 1, 6);
	    grid.add(fileBtn, 2, 6);

	    dialog.getDialogPane().setContent(grid);

	    /* ===========================
	       3. Guardar → devolver Tarea
	    ============================ */
	    dialog.setResultConverter(btn -> {
	        if (btn == saveBtn) {

	            Tarea t = new Tarea();

	            if (tareaEditar != null) t.setId(tareaEditar.getId());

	            t.setTitulo(tituloField.getText());
	            t.setContenido(contenidoField.getText());
	            t.setNum_intento(intentoSpin.getValue());
	            LocalDate ld = fechaPicker.getValue();
	            if (ld != null) {
	                t.setFechaEntrega(java.sql.Date.valueOf(ld));
	            }
	         
	           
	            t.setId_unidad(unitCombo.getValue().getId());
	            t.setRuta(pathField.getText());
	            //System.err.println(t);
	            return t;
	        }
	        return null;
	    });

	    return dialog;
	}

	// --- 4. Salta a la pagina de tareaDetalleVista.fxml ---
    private void abrirVistaDetalle(Tarea tarea) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tareaDetalleVista.fxml"));
            Parent view = loader.load();

            // Obtener el controller
            TareaDetalleController controller = loader.getController();
            controller.cargaDatosIniciarVista(tarea);

            // Actualizar el contenido central del BorderPane
            if (getScene() != null) {
                BorderPane rootPane = (BorderPane) getScene().getRoot();
                rootPane.setCenter(view);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}