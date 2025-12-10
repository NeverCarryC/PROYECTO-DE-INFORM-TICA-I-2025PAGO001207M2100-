package controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import db.AsignaturaCRUD;
import db.RegistroCRUD; // ✅ 确保只引用这个，删掉 RegistroExamenCRUD
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
import util.Basico;

public class TareaDetalleController {
    
    private Tarea tarea; 
    private Unidad unidad;
    private Asignatura asignatura;
    private Button descargar;
    
    @FXML private HBox mainContentHBox;
    @FXML private Label nombreAsignatura;
    @FXML private Label contenido;
    @FXML private Button empezar; // 提交按钮
    @FXML private Hyperlink enunciado;
    @FXML private Label fechaEntrega;
    @FXML private Label name;
    @FXML private Label nombre_unidada;
    @FXML private Label num_intento;
    @FXML private HBox resultadoBox;
    @FXML
    private Label resultado;
    @FXML
    void empezar(ActionEvent event) {
        Dialog<Map<String, String>> dialog = createFileUploadDialog("Subir Archivo y Comentario");
        
        dialog.showAndWait().ifPresent(resultMap -> {
            
            String absolutePath = resultMap.get("filePath");
            String comentario = resultMap.get("comentario");
            
            System.out.println("--- Subida Confirmada ---");
            
            // 1. 创建对象：注意这里使用适合插入的构造函数（没有ID，或者ID为0）
            RegistroExamen nuevoRegistro = new RegistroExamen(
                0, // ID 占位符，数据库自动生成
                this.tarea.getId(),
                AppSession.getAlumno().getId(),
                this.asignatura.getId_profesor(),
                null, // 还没有打分，所以是 null
                comentario,
                absolutePath
            );
            
            // 2. 调用修正后的 CRUD 进行插入
            RegistroExamen registroInsertado = RegistroCRUD.insertRegistroExamen(nuevoRegistro);
        
            if (registroInsertado != null) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Éxito");
                successAlert.setHeaderText(null);
                successAlert.setContentText("El archivo y el comentario han sido subidos correctamente.");
                successAlert.showAndWait();
                
                // 更新 UI：Button Empezar 变成已提交状态
                this.empezar.setText("Entregado!");
                this.empezar.setDisable(true);
                createAndSetupDownloadButton(registroInsertado.getRuta_archivo());
                // 更新UI： label Resultado
                ArrayList<RegistroExamen> intentos = RegistroCRUD.getRegistrosByAlumnoAndExamen(AppSession.getAlumno().getId(),  this.tarea.getId());
               
                
                loadEstado(intentos.size(), intentos);
                
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error de Base de Datos");
                errorAlert.setContentText("No se pudo guardar el registro.");
                errorAlert.showAndWait();
            }
        });
    }
    
    @FXML
    void back(ActionEvent event) {
    	Basico.back(event, "/fxml/tareaListaView.fxml");
    }
    
    // --- 其他方法保持不变 ---

    private Dialog<Map<String, String>> createFileUploadDialog(String title){
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        ButtonType saveBtn = new ButtonType("Guardar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10); 
        grid.setVgap(10); 
        grid.setPadding(new Insets(20));
        
        TextField pathField = new TextField();
        pathField.setEditable(false); 
        pathField.setPromptText("Ruta del archivo seleccionado");
        
        Button fileBtn = new Button("Seleccionar Archivo");
        fileBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Archivo");
            File selectedFile = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if(selectedFile != null) {
                pathField.setText(selectedFile.getAbsolutePath());
            }
        });
        
        HBox fileBox = new HBox(5, pathField, fileBtn);
        HBox.setHgrow(pathField, Priority.ALWAYS);
        
        TextArea commentArea = new TextArea();
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(4); 
        commentArea.setPromptText("Escriba su comentario aquí (Opcional)");

        grid.add(new Label("Archivo:"), 0, 0); 
        grid.add(fileBox, 1, 0);
        grid.add(new Label("Comentario:"), 0, 1); 
        grid.add(commentArea, 1, 1);

        dialog.getDialogPane().setContent(grid);
        
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.disableProperty().bind(pathField.textProperty().isEmpty());

        dialog.setResultConverter(button -> {
            if (button == saveBtn && !pathField.getText().isEmpty()) {
                Map<String, String> result = new HashMap<>();
                result.put("filePath", pathField.getText());
                result.put("comentario", commentArea.getText());
                return result;
            }
            return null;
        });

        return dialog;
    }

    // 两个名字一样的方法，为了兼容保留，建议统一用 cargaDatosIniciarVista
    void CargaDatosDeTarea(Tarea tarea){
        cargaDatosIniciarVista(tarea);
    }
    
    public void cargaDatosIniciarVista(Tarea tarea) {
        this.tarea = tarea;
        this.unidad = UnidadCRUD.getUnidadById(tarea.getId_unidad());
        this.asignatura = AsignaturaCRUD.getAsignaturaById(this.unidad.getId_asignatura());
        
        // 使用 unified CRUD 查询是否已经提交过
    	ArrayList<RegistroExamen> intentos;
        if(!AppSession.isAlumno()) {
        	
        	intentos = RegistroCRUD.getRegistrosByAlumnoAndExamen(AppSession.getProfesor().getId(),  this.tarea.getId());
        	System.out.println(intentos);
        	
        	
        }else {
        	  intentos = RegistroCRUD.getRegistrosByAlumnoAndExamen(AppSession.getAlumno().getId(),  this.tarea.getId());
        }

        int numeroIntentos = intentos.size();
        
        if(numeroIntentos > 0) {
        	// 这里要写逻辑，如果是老师打开，Resultado 就要写几个学生已经交作业了
            RegistroExamen primeraEntrega = intentos.get(0);
            empezar.setText("Entregado!!!");
            empezar.setDisable(true);
            createAndSetupDownloadButton(primeraEntrega.getRuta_archivo());
        }
        
        // 读取考试的成绩
        // 1. 没提交
        // 2. 提交了没批改
        // 3. 批改了
  
        loadEstado(numeroIntentos, intentos);

        
        this.name.setText(tarea.getTitulo());
        this.fechaEntrega.setText("Fecha de entrega: " + tarea.getFechaEntrega().toString());
        this.nombre_unidada.setText(this.unidad.getNombre());
        this.contenido.setText(tarea.getContenido());
        this.num_intento.setText("Intentos: " + tarea.getNum_intento());
        this.nombreAsignatura.setText(asignatura.getNombre());
        
        
        if (tarea.getRuta() != null) {
             File file= new File(tarea.getRuta());
             enunciado.setText(file.getName());
        }
    }
    
    
    
    private void loadEstado(int numeroIntentos, ArrayList<RegistroExamen> intentos) {
		// TODO Auto-generated method stub
    	String resultadoString ="";
    	  if(numeroIntentos == 0) {
          	resultadoString = "Resultado: No entregado";
          }
          else if(intentos.get(0).getNota() == null ) {
        	resultadoString = "Resultado: Entregado";
          }else if(intentos.get(0).getNota()!= null) {
          	resultadoString = "Resultado: Calificado" + "\n Punto: " + intentos.get(0).getNota() ;
          }
    	  resultado.setText( resultadoString);
	}

	private void createAndSetupDownloadButton(String ruta_archivo) {
        if (descargar == null) {
            descargar = new Button("Descargar Mi Entrega");
            mainContentHBox.getChildren().add(descargar);
            HBox.setMargin(descargar, new Insets(0, 0, 0, 10)); 
            descargar.setOnAction(e -> handleOpenFile(ruta_archivo));
        }
    }

    private void handleOpenFile(String rutaAbsoluta) {
        if (rutaAbsoluta == null || rutaAbsoluta.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No hay archivo.").showAndWait();
            return;
        }
        File file = new File(rutaAbsoluta);
        if (file.exists() && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    void descargar(ActionEvent event) {
        if (this.tarea != null && this.tarea.getRuta() != null) {
            handleOpenFile(this.tarea.getRuta());
        }
    }
}