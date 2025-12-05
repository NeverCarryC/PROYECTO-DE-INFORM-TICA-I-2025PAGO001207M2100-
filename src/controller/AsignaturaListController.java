package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import db.AsignaturaCRUD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.AppSession;
import model.Asignatura;
import model.CursoPrueba;

public class AsignaturaListController {

    @FXML
    private ListView<Asignatura> cursoLista;
    
    private void cargarDatosYRefreshView() {
    	ArrayList<Asignatura> asignaturas = AsignaturaCRUD.getAllAsignaturas();
    	
    }
    
    @FXML
	   private void initialize() {
	       // 1. 载入数据
	       ArrayList<Asignatura> cursos = AsignaturaCRUD.getAllAsignaturas();
	       AppSession.setCursos(cursos);
	       ObservableList<Asignatura> observableList = FXCollections.observableArrayList(cursos);
	       System.out.println(cursos);
	       cursoLista.setItems(observableList);
	       cursoLista.setFixedCellSize(80); // 设置item的高度

	       // =================================================================
	       // 2. 定义菜单：【新增】(用于点击空白处)
	       // =================================================================
	       ContextMenu menuAdd = new ContextMenu();
	       MenuItem addItem = new MenuItem("Nueva asignatura"); // 新增
	       menuAdd.getItems().add(addItem);

	       // "新增"的逻辑 (和你原来写的一样，我保留了你的逻辑)
	       addItem.setOnAction(e -> {
	           Dialog<Pair<String, String>> dialog = new Dialog<>();
	           dialog.setTitle("Nuevos asignaturas");
	           dialog.setHeaderText("Por favor, introduzca los detalles de asignatura.");
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
	               Asignatura newAsignatura = AsignaturaCRUD.insertarAsignatura(nombreInput, currentProfesorId, descInput);

	               if (newAsignatura != null) {
	                   cursoLista.getItems().add(newAsignatura);
	               } else {
	                   new Alert(Alert.AlertType.ERROR, "Error al guardar").show();
	               }
	           });
	       });

	       // 将【新增菜单】绑定到整个 ListView (默认右键就是新增)
	       cursoLista.setContextMenu(menuAdd);


	       // =================================================================
	       // 3. 定义菜单：【删除】(用于点击具体的 Item)
	       // =================================================================
	       // 先定义cursoLista的右键事件是 menuAdd
	       // setCellFactory定义了每行的双击事件，右击事件，作为覆盖
	       ContextMenu menuDelete = new ContextMenu();
	       MenuItem deleteItem = new MenuItem("Eliminar"); // 删除
	       MenuItem editItem = new MenuItem("Editar");
	       menuDelete.getItems().add(deleteItem);
	       menuDelete.getItems().add(editItem);
	       // "删除"的逻辑 (这里只写了框架，具体删除函数你来实现)
	       deleteItem.setOnAction(e -> {
	           // 获取当前选中的项
	           Asignatura selected = cursoLista.getSelectionModel().getSelectedItem();
	           if (selected != null) {
	               System.out.println("你要删除的是: " + selected.getNombre() + " ID: " + selected.getId());
	               
	               // TODO: 1.在这里调用你的数据库删除函数 AsignaturaCRUD.delete(selected.getId());
	               // boolean success = AsignaturaCRUD.delete(selected.getId());
	               boolean success = AsignaturaCRUD.deleteAsignatura(selected.getId()); // 假设删除成功

	               // 2. 如果数据库删除成功，从 UI 移除
	               if (success) {
	                   cursoLista.getItems().remove(selected);
	               }
	           }
	       });
	       
	       editItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				 Asignatura selected = cursoLista.getSelectionModel().getSelectedItem();
				// System.out.println("你要编辑的是: " + selected.getNombre() + " ID: " + selected.getId());
				 
				// 1. Crear el objeto Dialog. El tipo de retorno es Map<String, String> si se pulsa OK
				 Dialog<Map<String, String>> dialog = new Dialog<>();
				 dialog.setTitle("Editar");
				 dialog.setHeaderText("Introduce el nombre y la descripción:");
				 
				// 2. Configurar los botones
				    ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
				    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
				 // 3. Crear el layout (GridPane) para los campos
				    GridPane grid = new GridPane();
				    grid.setHgap(10);
				    grid.setVgap(10);
				    grid.setPadding(new Insets(20,15,10,10));
				 // 4. Crear los campos de entrada
				    TextField nameField = new TextField(selected.getNombre());
				    nameField.setPromptText("Nombre");
				    TextField descriptionField = new TextField(selected.getDescripcion());
				    descriptionField.setPromptText("Descripción");
				    
				    // Añadir etiquetas y campos al grid
				    grid.add(new Label("Nombre:"), 0, 0);
				    grid.add(nameField, 1, 0);
				    grid.add(new Label("Descripción:"), 0, 1);
				    grid.add(descriptionField, 1, 1);
				    
				    dialog.getDialogPane().setContent(grid);
				    

			           dialog.setResultConverter(dialogButton -> {
			               if (dialogButton == saveButtonType) {
			            	   Map<String, String> result = new HashMap<>();
			                   result.put("nombre", nameField.getText());
			                   result.put("descripcion", descriptionField.getText());
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
			        	    
//			        	    System.out.println("Datos Guardados:");
//			        	    System.out.println("Nombre: " + nombre);
//			        	    System.out.println("Descripción: " + descripcion);
			        	    
			        	    // Aquí puedes llamar a tu método para guardar en la base de datos o añadir al ListView
			        	    boolean success = AsignaturaCRUD.editAsignatura(selected.getId(), nombre, descripcion);
			        	    if(success) {
			        	    	selected.setNombre(nombre); 
			        	        selected.setDescripcion(descripcion);
			            	    cursoLista.refresh();
			        	    }else {
			        	    	System.err.println("Error en Base de datos, el metodo de AsignaturaCRUD.editAsignatura()");
			        	    }
			               	    
			        	} else {
			        	    // El usuario pulsó "Cancelar" o cerró el diálogo
			        	    System.out.println("Operación cancelada.");
			        	}
			}
		});

	       // =================================================================
	       // 4. 核心部分：CellFactory (控制每一行的行为)
	       // =================================================================
	       cursoLista.setCellFactory(lv -> {
	           javafx.scene.control.ListCell<Asignatura> cell = new javafx.scene.control.ListCell<Asignatura>() {
	               @Override
	               protected void updateItem(Asignatura item, boolean empty) {
	                   super.updateItem(item, empty);
	                   
	                   if (empty || item == null) {
	                       //如果是空行
	                       setText(null);
	                       // 空行使用默认的 ListView 菜单 (也就是 menuAdd)
	                       setContextMenu(null); 
	                   } else {
	                       // 如果是有数据的行
	                       setText(item.toString()); // 或者 item.getNombre()
	                       // 有数据的行，右键显示【删除菜单】
	                       setContextMenu(menuDelete); 
	                   }
	               }
	           };

	           // --- 处理鼠标点击事件 ---
	           cell.setOnMouseClicked(event -> {
	               // 必须点击的是非空行，且是鼠标左键
	               if (!cell.isEmpty() && event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
	                   
	                   // 如果是双击 (ClickCount == 2) -> 进入详情
	                   if (event.getClickCount() == 2) {
	                       Asignatura item = cell.getItem(); // 获取当前行的数据
	                       abrirDetalleCurso(item); // 调用封装好的跳转方法
	                   }
	                   
	                   // 注意：这里没有写 ClickCount == 1 的逻辑，所以单击只会有默认的“选中”效果，不会跳转
	               }
	           });

	           return cell;
	       });
	   }



   // 我把你原来的跳转逻辑封装成了一个私有方法，这样代码更整洁
   private void abrirDetalleCurso(Asignatura asignatura) {
        if (asignatura == null) return;
        
        int id = asignatura.getId();
        System.out.println("Double Clicked ID: " + id);
        // 1. Crear FXMLLoader y especificar la ruta del archivo FXML a cargar
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/moduloListaView.fxml"));
        try {
        	// 2. Cargar el FXML, devuelve el nodo raíz de la vista (Parent)
            Parent view = loader.load();
           // 3. Obtener la instancia del controlador correspondiente a este FXML
            ModuloController controller = loader.getController();
            // 4. Configurar parámetros (si el controlador necesita datos externos para inicializar)
            controller.setId_asignatura(id);
            // 5️. Obtener el nodo raíz del Scene actual (aquí se asume que es un BorderPane) Stage -> scene -> borderPane
            BorderPane rootPane = (BorderPane) cursoLista.getScene().getRoot();
            // 6. Colocar la vista cargada en la posición central del BorderPane
            rootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
   }
   
}
