package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import db.AsignaturaCRUD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import model.AppSession;
import model.Asignatura;

public class AsignaturaListController {

    @FXML
    private ListView<Asignatura> cursoLista;

    private ContextMenu menuAdd;
    private ContextMenu menuEditDelete;

    @FXML
    private void initialize() {
        cargarDatos();  // 1. Cargar datos desde la base de datos
        inicializarMenus(); // 2. Inicializar los menús contextuales (clic derecho)
        configurarListView(); // 3. Configurar el comportamiento visual y la interacción del ListView (CellFactory)
    }

    // =================================================================
    // 1. Lógica de carga de datos
    // =================================================================
    private void cargarDatos() {
        ArrayList<Asignatura> cursos = AsignaturaCRUD.getAllAsignaturas();
        AppSession.setCursos(cursos); // Guardar en la sesión global
        
        // Convertir a ObservableList y vincular al ListView
        ObservableList<Asignatura> observableList = FXCollections.observableArrayList(cursos);
        cursoLista.setItems(observableList);
    }

    // =================================================================
    // 2. Lógica de inicialización de menús
    // =================================================================
    private void inicializarMenus() {
    	// --- Menú A: Añadir nueva asignatura (para clic en espacio vacío) ---
        menuAdd = new ContextMenu();
        MenuItem addItem = new MenuItem("Nueva asignatura");
        addItem.setOnAction(e -> mostrarDialogoAgregar());
        menuAdd.getItems().add(addItem);

        // Establecer este menú como predeterminado para la lista
        cursoLista.setContextMenu(menuAdd);

        // --- Menú B: Editar/Eliminar (para clic en un elemento existente) ---
        menuEditDelete = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Eliminar");
        MenuItem editItem = new MenuItem("Editar");

        deleteItem.setOnAction(e -> eliminarAsignaturaSeleccionada());
        editItem.setOnAction(e -> mostrarDialogoEditarSeleccionada());

        menuEditDelete.getItems().addAll(deleteItem, editItem);
    }

    // =================================================================
    // 3. Configuración del ListView (CellFactory)
    // =================================================================
    private void configurarListView() {
        cursoLista.setFixedCellSize(80);

        cursoLista.setCellFactory(lv -> {
            ListCell<Asignatura> cell = new ListCell<Asignatura>() {
                @Override
                protected void updateItem(Asignatura item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        // Si la fila está vacía, no mostramos el menú de eliminar/editar
                        setContextMenu(null); 
                    } else {
                        setText(item.toString());
                        // Si hay datos, asignamos el menú de acciones específicas
                        setContextMenu(menuEditDelete);
                    }
                }
            };

         // Manejar eventos de ratón (ej. doble clic)
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    if (event.getClickCount() == 2) {
                        abrirDetalleCurso(cell.getItem());
                    }
                }
            });

            return cell;
        });
    }

    // =================================================================
    // 4. Lógica de Negocio (CRUD: Agregar, Editar, Eliminar)
    // =================================================================

    // Muestra el diálogo para añadir una nueva asignatura.
    private void mostrarDialogoAgregar() {
        Dialog<Pair<String, String>> dialog = crearDialogoBase("Nueva asignatura", "", "");
        
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            String nombre = pair.getKey();
            String desc = pair.getValue();

            if (nombre == null || nombre.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "¡El nombre no puede estar vacío!").show();
                return;
            }

            int currentProfesorId = AppSession.getAlumno().getId();
            Asignatura newAsignatura = AsignaturaCRUD.insertarAsignatura(nombre, currentProfesorId, desc);

            if (newAsignatura != null) {
                cursoLista.getItems().add(newAsignatura);
            } else {
                new Alert(Alert.AlertType.ERROR, "Error al guardar").show();
            }
        });
    }

    // Muestra el diálogo para editar la asignatura seleccionada actualmente.
    private void mostrarDialogoEditarSeleccionada() {
        Asignatura selected = cursoLista.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Dialog<Pair<String, String>> dialog = crearDialogoBase("Editar asignatura", selected.getNombre(), selected.getDescripcion());
        
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            String nombre = pair.getKey();
            String descripcion = pair.getValue();

            boolean success = AsignaturaCRUD.editAsignatura(selected.getId(), nombre, descripcion);
            if (success) {
                selected.setNombre(nombre);
                selected.setDescripcion(descripcion);
                cursoLista.refresh();
            } else {
                System.err.println("Error al actualizar en BD");
            }
        });
    }

    // Elimina la asignatura seleccionada de la BD y de la lista.
    private void eliminarAsignaturaSeleccionada() {
        Asignatura selected = cursoLista.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean success = AsignaturaCRUD.deleteAsignatura(selected.getId());
            if (success) {
                cursoLista.getItems().remove(selected);
            }
        }
    }

    /**
     * Método auxiliar para crear un diálogo genérico con campos de Nombre y Descripción.
     * Evita la duplicación de código entre "Agregar" y "Editar".
     */
    private Dialog<Pair<String, String>> crearDialogoBase(String titulo, String nombreDefault, String descDefault) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText("Por favor, introduzca los detalles.");

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(nombreDefault);
        nameField.setPromptText("Nombre");

        TextArea descField = new TextArea(descDefault); 
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
            if (dialogButton == saveButtonType) {
                return new Pair<>(nameField.getText(), descField.getText());
            }
            return null;
        });

        return dialog;
    }

    // =================================================================
    // 5. Navegación
    // =================================================================
    private void abrirDetalleCurso(Asignatura asignatura) {
        if (asignatura == null) return;
        
        int id = asignatura.getId();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/moduloListaView.fxml"));
        try {
            Parent view = loader.load();
            ModuloController controller = loader.getController();
            controller.setId_asignatura(id);
            BorderPane rootPane = (BorderPane) cursoLista.getScene().getRoot();
            rootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}