package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import db.AsignaturaCRUD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class AsignaturaListController2 {

    @FXML
    private ListView<Asignatura> cursoLista;
    
    private void cargarDatosYRefreshView() {
    	ArrayList<Asignatura> asignaturas = AsignaturaCRUD.getAllAsignaturas();
    	
    }
    
   @FXML
   private void initialize() {
	  // System.out.println("CursoList View esta cargado");
	   // El componente ListView llamará automáticamente al método toString() de Curso para mostrar en en frontend
	   // 第一种方法 让ListView载入数据
//	    cursoLista.getItems().add(new CursoPrueba(1, "PROGRAMACIÓN CON ESTRUCTURAS LINEALES"));
	   
	   // 另一种方法让ListView载入数据
	   ArrayList<Asignatura> cursos = AsignaturaCRUD.getAllAsignaturas();
	   // 我们把数据存进AppSession中
	   AppSession.setCursos(cursos);
	   ObservableList<Asignatura> observableList = FXCollections.observableArrayList(cursos);
	   cursoLista.setItems(observableList);
	   
	   // 鼠标右键可以显示 "新增课程"，点击执行增加逻辑
	   ContextMenu emptyMenu = new ContextMenu();
	   MenuItem add = new MenuItem("nueva asignatura");

	   emptyMenu.getItems().add(add);

	// 点击新增
	   // OJO 有一个更好的方式： formAsignatura.fxml
//	   formAsignatura.fxml（界面）
//	   FormAsignaturaController.java（控制器）
//	   主界面调用一个方法 AbrirFormulario()
//	   等表单关闭后，从控制器获取结果
	   add.setOnAction(e -> {
		   
		   
		   
	       // 1. 创建自定义对话框
	       Dialog<Pair<String, String>> dialog = new Dialog<>();
	       dialog.setTitle("Nuevos asignaturas");
	       dialog.setHeaderText("Por favor, introduzca los detalles de asignatura.");

	       // 2. 设置按钮 (确定 和 取消)
	       ButtonType loginButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
	       dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

	       // 3. 创建布局和输入控件
	       GridPane grid = new GridPane();
	       grid.setHgap(10);
	       grid.setVgap(10);
	       grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

	       TextField nameField = new TextField();
	       nameField.setPromptText("Nombre");

	       // 使用 TextArea 方便输入多行描述，如果只需单行可用 TextField
	       TextArea descField = new TextArea();
	       descField.setPromptText("Descripcion");
	       descField.setPrefRowCount(3); // 设置高度为3行
	       descField.setPrefWidth(200);

	       grid.add(new Label("Nombre:"), 0, 0);
	       grid.add(nameField, 1, 0);
	       grid.add(new Label("Descripcion:"), 0, 1);
	       grid.add(descField, 1, 1);

	       dialog.getDialogPane().setContent(grid);

	       // 4. 默认聚焦在名称框
	       javafx.application.Platform.runLater(nameField::requestFocus);

	       // 5. 将结果转换为 Pair (名称, 描述)
	       dialog.setResultConverter(dialogButton -> {
	           if (dialogButton == loginButtonType) {
	               return new Pair<>(nameField.getText(), descField.getText());
	           }
	           return null;
	       });

	       // 6. 显示弹窗并处理结果
	       Optional<Pair<String, String>> result = dialog.showAndWait();

	       result.ifPresent(pair -> {
	           String nombreInput = pair.getKey();
	           String descInput = pair.getValue();

	           // 简单的验证
	           if (nombreInput == null || nombreInput.trim().isEmpty()) {
	               Alert alert = new Alert(Alert.AlertType.WARNING, "¡El nombre de asignatura no puede estar vacío!");
	               alert.show();
	               return;
	           }

	           // --- 准备数据 ---
	           
	           // A. 获取已知数据：老师 ID (你说你有办法获取，这里假设存在这个变量)
	           // int currentProfesorId = getSelectedProfesorId(); // 你的获取逻辑
	           int currentProfesorId = AppSession.getAlumno().getId(); // <--- ⚠️ 这里替换成你实际获取到的 ID
	           
	           // B. 获取已知数据：当前日期
	           LocalDate today = LocalDate.now();

	           // --- 调用后端 ---
	           Asignatura newAsignatura = AsignaturaCRUD.insertarAsignatura(
	               nombreInput, 
	               currentProfesorId, 
	               descInput
	           );

	           // --- 更新 UI ---
	           if (newAsignatura != null) {
	               // System.out.println("Guardado exitosamente！ID: " + newAsignatura.getId());
	               // 刷新列表          
	               cursoLista.getItems().add(newAsignatura);
	           } else {
	               Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar, verifique la base de datos.");
	               alert.show();
	           }
	       });
	   });

	   // 注册右键事件
	   cursoLista.setOnContextMenuRequested(event -> {
	       // 关键点：如果点在 item 上，cellFactory 已经吃掉事件了，所以这里只响应空白
	       if (cursoLista.getSelectionModel().getSelectedItem() == null) {
	           emptyMenu.show(cursoLista, event.getScreenX(), event.getScreenY());
	       }
	   });

	  
	    
	    cursoLista.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
	        if (newVal != null) {
	            int id = newVal.getId();
	            String nombre = newVal.getNombre();
	            
	            System.out.println("Clicked ID: " + id);
	            // System.out.println("Clicked Nombre: " + nombre);
	            
	            // Podemos usar id para consular los modulos correspondes en base de datos
	            // Ahora solo usa datos de prueba
	            // 我们把用户当前点击的asignatura id信息传递给ModuloController
	        	// 首先获得新loader
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/moduloListaView.fxml"));
	            try {
					Parent view = loader.load();

					// 获取新 Controller
					ModuloController controller = loader.getController();
					
					// 向新控制器传递数据
					controller.setId_asignatura(id);
					// System.out.println("当前传递给Modulo  controller的id是" + controller.getId_asignatura());
					
					// Primero tenemos que entender el concepto del "Scene Graph" en JavaFX:
					// Obtenemos el BorderPane principal desde la raíz de la escena.
					BorderPane rootPane = (BorderPane) cursoLista.getScene().getRoot();
					rootPane.setCenter(view);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	         
	        }
	    });
	    

   }
   
}
