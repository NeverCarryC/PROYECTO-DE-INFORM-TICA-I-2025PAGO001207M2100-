package util;

import java.io.IOException;

import controller.TareaDetalleController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import model.AppSession;
import model.Modulo;
import model.Tarea;
import model.Unidad;

public class TareaCell extends ListCell<Tarea> {

    // UI 组件
    private final HBox hbox = new HBox();
    private final Label label = new Label();
    private final Pane spacer = new Pane();
    private final Button btnOpen = new Button("Open");
    private final Button btnEdit = new Button("Edit");
    private final Button btnDelete = new Button("Delete");
    private final Button btnGrade = new Button("Calificación");

    public TareaCell() {
        super();

        // --- 1. 布局设置 ---
        if(!AppSession.isAlumno()) {        	
        	hbox.getChildren().addAll(label, spacer, btnOpen, btnEdit, btnDelete, btnGrade);
        }else {
        	hbox.getChildren().addAll(label, spacer, btnOpen);
        }
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(5);
        HBox.setHgrow(spacer, Priority.ALWAYS); // 把按钮顶到右边

        // --- 2. 按钮事件 ---
        
        // "Open" 按钮：调用和双击一样的逻辑
        btnOpen.setOnAction(event -> {
            Tarea item = getItem();
            if (item != null) {
                abrirVistaDetalle(item); 
            }
        });

        btnEdit.setOnAction(event -> System.out.println("编辑: " + getItem()));
        
        btnDelete.setOnAction(event -> {
            Tarea item = getItem();
            getListView().getItems().remove(item); // 从列表中移除
            // TareasCRUD.delete(item.getId()); // 别忘了调用数据库删除
        });

        btnGrade.setOnAction(event -> {
            Tarea item = getItem();
            if (item != null) {
                try {
                    // 加载评分视图
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/calificacionVista.fxml"));
                    Parent view = loader.load();
                    
                    // 传递当前任务数据给控制器
                    controller.CalificacionController ctrl = loader.getController();
                    ctrl.initData(item);
                    
                    // 切换主界面中心内容
                    if (getScene() != null) {
                        BorderPane rootPane = (BorderPane) getScene().getRoot();
                        rootPane.setCenter(view);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // --- 3. 双击事件 (逻辑移到这里) ---
        this.setOnMouseClicked(event -> {
            // 确保不为空，且是左键，且是双击
            if (!isEmpty() && getItem() != null && event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    System.out.println("检测到双击: " + getItem());
                    abrirVistaDetalle(getItem());
                }
            }
        });
    }

    @Override
    protected void updateItem(Tarea item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            ContextMenu menuGlobal = createContextMenu();
            setContextMenu(menuGlobal);
            
            
            
        } else {
            setText(null); // 文字交给 Label 显示
            label.setText(item.toString());
            setGraphic(hbox);
        }
    }

    private ContextMenu createContextMenu() {
    	ContextMenu menuAdd = new ContextMenu();
    	MenuItem addItem = new MenuItem("Nueva Tarea");
    	addItem.setOnAction(e -> mostrarDialogAgregar());
    	menuAdd.getItems().add(addItem);
    	return menuAdd;
	}

	private void mostrarDialogAgregar() {
		System.out.println("触发，右键菜单点击事件");
		// 参考ModuloController.java
		//  private Dialog<Modulo> createModuloFormDialog(String title, Unidad unidadDefault, Modulo moduloEditar) 
	}

	// --- 4. 封装跳转逻辑 ---
    private void abrirVistaDetalle(Tarea tarea) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tareaDetalleVista.fxml"));
            Parent view = loader.load();

            // 获取 Controller 并传递数据
            TareaDetalleController controller = loader.getController();
            controller.cargaDatosIniciarVista(tarea);

            // 获取当前的 Scene Root 并进行切换
            // getScene() 只有在 Cell 被添加到界面后才有效，点击时肯定有效
            if (getScene() != null) {
                BorderPane rootPane = (BorderPane) getScene().getRoot();
                rootPane.setCenter(view);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}