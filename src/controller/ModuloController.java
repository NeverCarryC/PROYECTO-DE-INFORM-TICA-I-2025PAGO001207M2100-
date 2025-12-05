package controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import db.AsignaturaCRUD;
import db.ModuloCRUD;
import db.UnidadCRUD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import model.AppSession;
import model.Asignatura;
import model.Modulo;
import model.Unidad;
import model.UnidadPrueba;

public class ModuloController {

	private int id_asignatura;
    @FXML
    private Accordion accordion;
    
    private ContextMenu currentContextMenu;// 不然就会出现，右键点击出现多个ContextMenu
 
    @FXML
 // CONOCIMIENTO: CICLO DE VIDA DE FXML VIEW
 // No se debe poner en initialize() ninguna operación que dependa
 // de parámetros externos (por ejemplo, id_asignatura).
 //
 // Esto se debe al orden del ciclo de vida de JavaFX.
 //
 // Razón principal: el orden temporal.
 //
 // El flujo es el siguiente:
 //
 // 1. Se ejecuta loader.load()
 //
 // 2. JavaFX crea una nueva instancia del controlador (new ModuloController())
 //
 // 3. JavaFX inyecta los elementos @FXML (por ejemplo, el accordion)
 //
 // 4. JavaFX ejecuta inmediatamente initialize()
 //
//     🚨 En este momento, todavía NO se ha llamado a setId_asignatura(),
//     por lo que this.id_asignatura sigue con el valor por defecto: 0.
 //
//     Si hiciéramos aquí la consulta a la base de datos, se buscarían
//     los datos con ID = 0 (lógicamente vacíos).
 //
 // 5. loader.load() termina y devuelve la vista
 //
 // 6. Se obtiene el controlador con loader.getController()
 //
 // 7. Finalmente, se llama manualmente a controller.setId_asignatura(id)
 //
//     ✅ Solo en este momento el ID llega correctamente.
 //
    public void initialize() {
    }
    public void cargarUnidadesYAsignatura(int id_asignatura) {

    	// Obtener la lista de unidades y modulos desde la base de datos según el id_asignatura
    	ArrayList<Unidad> unidades = UnidadCRUD.getUnidadsByIdAsignatura(id_asignatura);
    	System.out.println(unidades);
    	// Comprobar si no hay unidades disponibles
        if (unidades == null || unidades.isEmpty()) {
            // Crear un VBox como contenedor de un mensaje de "sin contenido"
            VBox emptyBox = new VBox();
            emptyBox.setPadding(new Insets(20));
            // Crear una etiqueta con el mensaje
            Label emptyLabel = new Label("(No hay contenido disponible)");
            emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            emptyBox.getChildren().add(emptyLabel);
            
            // Crear un TitledPane para mostrar el mensaje en el Accordion
            // Un accordion tiene muchos titledPan(1:N)
            TitledPane emptyPane = new TitledPane("Info： No hay contenido disponible", emptyBox);
            accordion.getPanes().add(emptyPane);
            return; // Terminar el método si no hay unidades
        }
        
        //  Iterar sobre cada unidad y crear su correspondiente sección en el Accordion
    	for(Unidad u :unidades) {
    		 // Crear un VBox contenedor para cada ítem del Accordion
	        VBox box = new VBox(10);
	        box.setPadding(new Insets(10));
	        
	        // Etiqueta para la descripción de la unidad (con ajuste de texto automático)
	        Label descripcionLabel = new Label();
	        descripcionLabel.textProperty().bind(u.descripcionProperty());
	        descripcionLabel.setWrapText(true);
	        
	        // Etiqueta para el título "Temario"
	        Label temarioLabel = new Label("Temario");
	        temarioLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
	        
	        // ListView para mostrar los módulos de la unidad
	        // 1. Crear un ListView vacío de tipo Modulo
	        ListView<Modulo> moduloListView = new ListView<>();
	        // ListView es un control que muestra una lista vertical desplazable.
	        // El tipo <Modulo> indica que cada elemento de la lista es un objeto Modulo.
	        
	        // 2. Convertir la lista de módulos de la unidad en un ObservableList
	        ObservableList<Modulo> observableList = FXCollections.observableArrayList(u.getModulos());
	        // u.getModulos() devuelve un List<Modulo> normal de Java.
	        // FXCollections.observableArrayList(...) lo convierte en ObservableList, que es "observable":
	        // cuando se agregan, eliminan o modifican elementos, la UI se actualiza automáticamente.
	        
	        // 3. Establecer la lista observable como fuente de datos del ListView
	        moduloListView.setItems(observableList);
	        // A partir de este momento, el ListView muestra todos los Modulos del ObservableList.
	        // Si se modifica observableList (añadir, eliminar, cambiar), ListView se actualiza automáticamente.
	        // Nota: ListView no almacena los datos por sí mismo, solo los muestra.
	        
	        // Agregar la descripción, el título y la lista de módulos al VBox
	        box.getChildren().addAll(descripcionLabel, temarioLabel, moduloListView);
	        
	        // Crear un TitledPane para la unidad y añadirlo al Accordion
	        TitledPane pane = new TitledPane();
	        pane.textProperty().bind(u.nombreProperty());
	        pane.setContent(box);
	        
	        // 用户右键点击Unidad, 编辑，删除
	        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->{
	        	if(event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
	        		if (currentContextMenu != null) {
	        			currentContextMenu.hide();
	        		}
	        		
	        		//System.out.println("右键点击了单元: " + u.getNombre());
	        		// System.out.println("ID de Unidad: " + u.getId());
	        		ContextMenu contextMenu = createContextMenuForUnidad(u);
	        		contextMenu.show(pane, event.getScreenX(), event.getScreenY());
	        		// 更新 currentContextMenu 字段为当前显示的菜单
	        		currentContextMenu = contextMenu;
	        		
	        		// event.consume();
	        	}else {
					//System.out.println("点到了别的地方");

	        		if (currentContextMenu != null) {
	        			currentContextMenu.hide();
	        		}
				}
	        });
	        
	        
	        accordion.getPanes().add(pane);
	        
    	}
    }
    
   
    private ContextMenu createContextMenuForUnidad(Unidad unidad) {
    	ContextMenu contextMenu = new ContextMenu();
    	
    	// --- 1. 编辑菜单项 ---
    	MenuItem editItem = new MenuItem("Editar");
    	editItem.setOnAction(e -> {
    		// 🚨 TODO: 替换为实际的编辑逻辑，例如打开编辑对话框
    		// System.out.println("Clic en Editar para Unidad: " + unidad.getNombre());
    		 Dialog<Map<String, String>> dialog = new Dialog<>();
			 dialog.setTitle("Editar");
			 dialog.setHeaderText("Introduce el nombre y la descripción:");
			 
			 ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
			 dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
			 
			 GridPane grid = new GridPane();
			 grid.setHgap(10);
			 grid.setVgap(10);
			 grid.setPadding(new Insets(20,15,10,10));
			 
			 TextField nameField = new TextField(unidad.getNombre());
			 nameField.setPromptText("Nombre");
			 
			 TextArea descripTextField = new TextArea(unidad.getDescripcion());
			 descripTextField.setWrapText(true);// 自动换行
			 descripTextField.setPrefSize(400, 300);
			 descripTextField.setPromptText("Descripción");
			 
			 grid.add(new Label("Nombre:"), 0, 0);
			 grid.add(nameField, 1, 0);
			 grid.add(new Label("Descripción:"), 0, 1);
			 grid.add(descripTextField, 1, 1);
			    
			 dialog.getDialogPane().setContent(grid);
			 
		      dialog.setResultConverter(dialogButton -> {
	               if (dialogButton == saveButtonType) {
	            	   Map<String, String> result = new HashMap<>();
	                   result.put("nombre", nameField.getText());
	                   result.put("descripcion", descripTextField.getText());
	                   return result;
	               }else {
					System.out.println("Not into dialogButton == saveButtonType");
				}
	               return null;
	           });
		      
		      Optional<Map<String, String>> result = dialog.showAndWait();
		      if (result.isPresent()) {
	        	    // El usuario pulsó "Guardar". Obtenemos el Map con los datos.
	        	    Map<String, String> data = result.get();
	        	    
	        	    String nombre = data.get("nombre");
	        	    String descripcion = data.get("descripcion");
	        	    
	        	    System.out.println("Datos Guardados:");
	        	    System.out.println("Nombre: " + nombre);
	        	    System.out.println("Descripción: " + descripcion);
	        	    
	        	    // Aquí puedes llamar a tu método para guardar en la base de datos o añadir al ListView
	        	   //  boolean success = AsignaturaCRUD.editAsignatura(selected.getId(), nombre, descripcion);
	        	    boolean success = ModuloCRUD.updateModulo(unidad.getId(), nombre, descripcion);
	        	    if(success) {
	        	    	unidad.setNombre(nombre); 
	        	    	unidad.setDescripcion(descripcion);
	        	    	System.out.println("修改成功");
	            	    // cursoLista.refresh();
	        	    }else {
	        	    	System.err.println("Error en Base de datos, el metodo de AsignaturaCRUD.editAsignatura()");
	        	    }
	               	    
	        	} else {
	        	    // El usuario pulsó "Cancelar" o cerró el diálogo
	        	    System.out.println("Operación cancelada.");
	        	}
		      
    	});
    	
    	// --- 2. 删除菜单项 ---
    	MenuItem deleteItem = new MenuItem("Eliminar");
    	deleteItem.setOnAction(e -> {
    		// 🚨 TODO: 替换为实际的删除逻辑，例如弹出确认对话框并调用 DAO
    		System.out.println("Clic en Eliminar para Unidad: " + unidad.getNombre());
    	});
    	
    	
    	//--- 3. 增加菜单项 ---
    	MenuItem addItem = new MenuItem("Añadir");
    	addItem.setOnAction(e->{
    		 Dialog<Pair<String, String>> dialog = new Dialog<>();
	           dialog.setTitle("Nuevos Unidades");
	           dialog.setHeaderText("Por favor, introduzca los detalles de Unidades.");
	           ButtonType loginButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
	           dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

	           GridPane grid = new GridPane();
	           grid.setHgap(10);
	           grid.setVgap(10);
	           grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

	           TextField nameField = new TextField();
	           nameField.setPromptText("Nombre");
	           TextArea descField = new TextArea();
	           descField.setPromptText("Descripcion");
	           descField.setPrefRowCount(3);
	           descField.setPrefWidth(200);

	           grid.add(new Label("Nombre:"), 0, 0);
	           grid.add(nameField, 1, 0);
	           grid.add(new Label("Descripcion:"), 0, 1);
	           grid.add(descField, 1, 1);

	           dialog.getDialogPane().setContent(grid);
	           javafx.application.Platform.runLater(nameField::requestFocus);

	           dialog.setResultConverter(dialogButton -> {
	               if (dialogButton == loginButtonType) {
	                   return new Pair<>(nameField.getText(), descField.getText());
	               }
	               return null;
	           });

	           Optional<Pair<String, String>> result = dialog.showAndWait();
	           result.ifPresent(pair -> {
	               String nombreInput = pair.getKey();
	               String descInput = pair.getValue();
	               if (nombreInput == null || nombreInput.trim().isEmpty()) {
	                   new Alert(Alert.AlertType.WARNING, "¡El nombre no puede estar vacío!").show();
	                   return;
	               }
	               
	               // 注意：这里需要根据实际情况修改
	               int currentProfesorId = AppSession.getAlumno().getId(); 
	               
	               // 调用后端插入
	               // Asignatura newAsignatura = AsignaturaCRUD.insertarAsignatura(nombreInput, currentProfesorId, descInput);
	               Unidad newUnidad = UnidadCRUD.createUnidad(nombreInput, descInput,this.id_asignatura);
	               if (newUnidad != null) {
	            	   addUnidadToAccordion(newUnidad);

	               } else {
	                   new Alert(Alert.AlertType.ERROR, "Error al guardar").show();
	               }
	           });
    	});
    	
    	// 将菜单项添加到 ContextMenu
    	contextMenu.getItems().addAll(editItem, deleteItem,addItem);
    	
    	return contextMenu;
    }


    
    private void addUnidadToAccordion(Unidad u) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label descripcionLabel = new Label();
        descripcionLabel.textProperty().bind(u.descripcionProperty());
        descripcionLabel.setWrapText(true);

        Label temarioLabel = new Label("Temario");
        temarioLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<Modulo> moduloListView = new ListView<>();
        if(u.getModulos()!=null) {
        	   ObservableList<Modulo> observableList =
            FXCollections.observableArrayList(u.getModulos()); moduloListView.setItems(observableList);
            box.getChildren().addAll(descripcionLabel, temarioLabel, moduloListView);
            
        }else {
        	  box.getChildren().addAll(descripcionLabel, temarioLabel);
        }
     
        TitledPane pane = new TitledPane();
        pane.textProperty().bind(u.nombreProperty());
        pane.setContent(box);

        accordion.getPanes().add(pane);
    }

    
    public void setId_asignatura(int id) {
    	this.id_asignatura = id;
    	cargarUnidadesYAsignatura(id);
    }
    
    public int getId_asignatura() {
    	return id_asignatura;
    }
}
