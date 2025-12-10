package controller;

import java.awt.Desktop;
import java.io.File;
import db.RegistroCRUD;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import model.RegistroExamen;
import model.Tarea;
import util.Basico;

public class CalificacionController {

    @FXML private Label lblTitulo;
    @FXML private TableView<RegistroExamen> tabla;
    @FXML private TableColumn<RegistroExamen, String> colAlumno;
    @FXML private TableColumn<RegistroExamen, Double> colNota;
    @FXML private TableColumn<RegistroExamen, String> colComentario;
    @FXML private TableColumn<RegistroExamen, String> colArchivo;

    @FXML
    void back(ActionEvent event) {
    	Basico.back(event, "/fxml/tareaListaView.fxml");
    }
    
    private Tarea tareaActual;

    @FXML
    public void initialize() {
        tabla.setEditable(true);

        // 1. 学生列
        colAlumno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreAlumno()));

        // 2. 分数列 (Double + Editable)
        colNota.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNota()));
        colNota.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colNota.setOnEditCommit(e -> {
            RegistroExamen reg = e.getRowValue();
            reg.setNota(e.getNewValue());
            RegistroCRUD.updateNotaYComentario(reg.getId(), reg.getNota(), reg.getComentario());
        });

        // 3. 评语列 (String + Editable)
        colComentario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getComentario()));
        colComentario.setCellFactory(TextFieldTableCell.forTableColumn());
        colComentario.setOnEditCommit(e -> {
            RegistroExamen reg = e.getRowValue();
            reg.setComentario(e.getNewValue());
            RegistroCRUD.updateNotaYComentario(reg.getId(), reg.getNota(), reg.getComentario());
        });

        // 4. 文件按钮列
        colArchivo.setCellFactory(col -> new TableCell<RegistroExamen, String>() {
            private final Button btn = new Button("Abrir");
            {
                btn.setOnAction(e -> {
                    RegistroExamen reg = (RegistroExamen) getTableRow().getItem();
                    if(reg != null) abrirArchivo(reg.getRuta_archivo());
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic((empty || getTableRow().getItem() == null || ((RegistroExamen) getTableRow().getItem()).getRuta_archivo() == null) ? null : btn);
            }
        });
    }

    public void initData(Tarea tarea) {
        this.tareaActual = tarea;
        lblTitulo.setText("Calificar: " + tarea.getTitulo());
        ObservableList<RegistroExamen> lista = FXCollections.observableArrayList(RegistroCRUD.getRegistrosPorExamen(tarea.getId()));
        tabla.setItems(lista);
    }

    private void abrirArchivo(String ruta) {
        try {
            File file = new File(ruta);
            if (file.exists()) Desktop.getDesktop().open(file);
        } catch (Exception e) { e.printStackTrace(); }
    }
}