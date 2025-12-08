package controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import db.AsignaturaCRUD;
import db.RegistroExamenCRUD;
import db.UnidadCRUD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import model.AppSession;
import model.Asignatura;
import model.RegistroExamen;
import model.Tarea;
import model.Unidad;

public class TareaDetalleController {
	private Tarea tarea; 
	private Unidad unidad;
	private Asignatura asignatura;
	
	private Button descargar;
	
	@FXML
    private HBox mainContentHBox;
    @FXML
    private Label nombreAsignatura;
    
    @FXML
    private Label contenido;

    @FXML
    private Button empezar;

    @FXML
    private Hyperlink enunciado;

    @FXML
    private Label fechaEntrega;

    @FXML
    private Label name;

    @FXML
    private Label nombre_unidada;

    @FXML
    private Label num_intento;

    @FXML
    void empezar(ActionEvent event) {
    	Dialog<Map<String, String>> dialog = createFileUploadDialog("Subir Archivo y Comentario");
    	// 显示弹窗并等待结果
        dialog.showAndWait().ifPresent(resultMap -> {
            
        	
            String absolutePath = resultMap.get("filePath");
            String comentario = resultMap.get("comentario");
            
            // 成功获取到数据
            System.out.println("--- Subida Confirmada ---");
            System.out.println("Ruta absoluta del archivo: " + absolutePath);
            System.out.println("Comentario: " + comentario);
            
            // TODO: 在这里执行文件处理和数据库存储逻辑...
            RegistroExamen nuevoRegistro = new RegistroExamen(
                    this.tarea.getId(),
                    AppSession.getAlumno().getId(),
                    this.asignatura.getId_profesor(),
                    null,
                    comentario,
                    absolutePath
                );
            
            RegistroExamen registroInsertado = RegistroExamenCRUD.insertRegistroExamen(nuevoRegistro);
        
            if (registroInsertado != null) {
                // System.out.println("✅ 数据库插入成功！新记录 ID: " + registroInsertado.getId());
                
                // 提示用户成功
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Éxito");
                successAlert.setHeaderText(null);
                successAlert.setContentText("El archivo y el comentario han sido subidos correctamente.");
                successAlert.showAndWait();
                
                // TODO: 更新UI列表或状态
                this.empezar.setText("Entregado!");
                empezar.setDisable(true);
                createAndSetupDownloadButton(registroInsertado.getRuta_archivo());
                
            } else {
                System.err.println("❌ 数据库插入失败。");
                
                // 提示用户失败
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error de Base de Datos");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("No se pudo guardar el registro en la base de datos.");
                errorAlert.showAndWait();
            }
        });
    }
    
    private Dialog<Map<String, String>> createFileUploadDialog(String title){
    	Dialog<Map<String, String>> dialog = new Dialog<>();
    	dialog.setTitle(title);
    	
    	ButtonType saveBtn = new ButtonType("Guardar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        
     // 布局
        GridPane grid = new GridPane();
        grid.setHgap(10); 
        grid.setVgap(10); 
        grid.setPadding(new Insets(20));
        
     // --- 1. 文件上传字段 ---
        TextField pathField = new TextField();
        pathField.setEditable(false); 
        pathField.setPromptText("Ruta del archivo seleccionado"); // 选中的文件路径
        
        Button fileBtn = new Button("Seleccionar Archivo");
     // 文件选择按钮的点击事件
        fileBtn.setOnAction(e -> {
            // 使用 FileChooser 弹出文件选择框
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Archivo para Subir");
            
            // 注意：必须传入 Stage 才能显示对话框
            File selectedFile = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if(selectedFile != null) {
                // 将绝对路径显示在文本字段中
                pathField.setText(selectedFile.getAbsolutePath());
            }
        });
        
     // 包装文件选择按钮和路径显示
        HBox fileBox = new HBox(5, pathField, fileBtn);
        fileBox.setSpacing(5);
        HBox.setHgrow(pathField, Priority.ALWAYS); // 确保路径字段占据剩余空间
     // --- 2. 备注字段 (Comentario) ---
        TextArea commentArea = new TextArea();
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(4); 
        commentArea.setPromptText("Escriba su comentario aquí (Opcional)"); // 在此写下您的备注（可选）

        // 布局组件添加到 Grid
        grid.add(new Label("Archivo:"), 0, 0); 
        grid.add(fileBox, 1, 0); // 文件路径和按钮放在同一行
        
        grid.add(new Label("Comentario:"), 0, 1); 
        grid.add(commentArea, 1, 1); // 备注区域

        dialog.getDialogPane().setContent(grid);
        
        // **验证输入并禁用保存按钮 (推荐)**
        // 只有当文件路径不为空时，才允许保存
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.disableProperty().bind(pathField.textProperty().isEmpty());

        // **转换结果 (Result Converter)**
        dialog.setResultConverter(button -> {
            if (button == saveBtn && !pathField.getText().isEmpty()) {
                Map<String, String> result = new HashMap<>();
                // 确保文件路径是绝对路径
                result.put("filePath", pathField.getText());
                // 获取备注内容
                result.put("comentario", commentArea.getText());
                return result;
            }
            return null;
        });

        return dialog;
        
    }

    void CargaDatosDeTarea(Tarea tarea){
    	this.tarea = tarea;
    }
    
    void cargaDatosIniciarVista(Tarea tarea) {
    	this.tarea = tarea;
    	this.unidad = UnidadCRUD.getUnidadById(tarea.getId_unidad());
    	this.asignatura = AsignaturaCRUD.getAsignaturaById(this.unidad.getId_asignatura());
    	ArrayList<RegistroExamen> intentos = RegistroExamenCRUD.getRegistrosByAlumnoAndExamen(AppSession.getAlumno().getId(),  this.tarea.getId());
    	int numeroIntentos = intentos.size();
    	if(numeroIntentos>0) {
    		RegistroExamen primeraEntrega = intentos.get(0);
    		empezar.setText("Entregado!!!");
            empezar.setDisable(true);
            createAndSetupDownloadButton(primeraEntrega.getRuta_archivo());
    	}
    	this.name.setText(tarea.getTitulo());
    	this.fechaEntrega.setText("Fecha de entrega   " + tarea.getFechaEntrega().toString());
    	this.nombre_unidada.setText(this.unidad.getNombre());
    	this.contenido.setText(tarea.getContenido());
    	this.num_intento.setText("numeros intentos  " + String.valueOf(tarea.getNum_intento()));
    	this.nombreAsignatura.setText(asignatura.getNombre());
    	File file= new File(tarea.getRuta());
    	enunciado.setText(file.getName());
    }
    
    private void createAndSetupDownloadButton(String ruta_archivo) {
		// TODO Auto-generated method stub
    	if (descargar == null) {
            descargar = new Button("Descargar");
            
            // 插入下载按钮到 HBox 的末尾（即 empezar 按钮的右侧）
            mainContentHBox.getChildren().add(descargar);

            // 设置边距，使其与 empezar 按钮有所分隔 (可选)
            HBox.setMargin(descargar, new Insets(0, 0, 0, 10)); 
            descargar.setOnAction(e -> {
                handleOpenFile(ruta_archivo);
            });
        }
	}

	private void handleOpenFile(String rutaAbsoluta) {
		// TODO Auto-generated method stub
		if (rutaAbsoluta == null || rutaAbsoluta.isEmpty()) {
	        new Alert(Alert.AlertType.WARNING, "No hay archivo adjunto para descargar.").showAndWait();
	        return;
	    }
	    
	    File file = new File(rutaAbsoluta);

	    if (file.exists() && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
	        try {
	            // 使用操作系统的默认程序打开文件
	            Desktop.getDesktop().open(file);
	        } catch (IOException ex) {
	            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el archivo. Verifique si tiene la aplicación adecuada instalada.").showAndWait();
	            ex.printStackTrace();
	        }
	    } else {
	        new Alert(Alert.AlertType.ERROR, "El archivo no existe o su sistema no soporta la acción.").showAndWait();
	    }
	}

	@FXML
    void descargar(ActionEvent event) {
    	if (this.tarea == null) {
            System.err.println("tarea es null");
            return;
        }

        String rutaAbsoluta = this.tarea.getRuta();
        File file = new File(rutaAbsoluta);

        // 检查文件是否存在，并且 Desktop 类是否支持打开操作
        if (file.exists() && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            try {
                // 使用操作系统的默认程序打开文件（例如：PDF 阅读器、浏览器等）
                Desktop.getDesktop().open(file);
                System.out.println("✅ 文件已尝试打开: " + rutaAbsoluta);
            } catch (IOException e) {
                // 如果文件无法打开（例如，文件被占用，或没有关联的程序）
                System.err.println("❌ 打开文件时发生 I/O 错误: " + rutaAbsoluta);
                e.printStackTrace();
            }
        } else {
            // 如果文件不存在或系统不支持此操作
            System.err.println("❌ 无法打开文件。文件不存在或系统不支持操作: " + rutaAbsoluta);
        }
    }
}
