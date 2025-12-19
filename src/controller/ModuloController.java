package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import com.mysql.cj.util.Util;

import db.ModuloCRUD;
import db.UnidadCRUD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.util.StringConverter;
import model.AppSession;
import model.Modulo;
import model.Unidad;
import util.Basico;

public class ModuloController {

    private int id_asignatura;

    @FXML
    private Button backBtn;
    
    @FXML
    private Button colapsarBtn;
    
    @FXML
    private TreeView<Object> courseTreeView;


    @FXML
    void back(ActionEvent event) {
    	// Back to AsignaturaListView.fxml
    	Basico.back(event, "/fxml/asignaturaListaView.fxml");
    }

    @FXML
    void colapsar(ActionEvent event) {
    	 TreeItem<Object> root = courseTreeView.getRoot();
    	    if (!isCollapsed) {
    	        // colapsar → Expandir
    	        for (TreeItem<Object> item : root.getChildren()) {
    	            item.setExpanded(false);
    	        }
    	        colapsarBtn.setText("Expandir Todo");
    	        isCollapsed = true;

    	    } else {
    	        // Expandir → colapsar
    	        for (TreeItem<Object> item : root.getChildren()) {
    	            item.setExpanded(true);
    	        }
    	        colapsarBtn.setText("Colapsar Todo");
    	        isCollapsed = false;
    	    }
    	  
    	  
    }
    

    private boolean isCollapsed = false;
    
    public void initialize() {}

    public void setId_asignatura(int id) {
        this.id_asignatura = id;
        loadTreeViewData(id);
    }

    public int getId_asignatura() {
        return id_asignatura;
    }

    // =========================================================
    //  Lógica 1: Cargar datos y construir el árbol con Nodos Hijos
    // =========================================================
    
    private void loadTreeViewData(int id_asignatura) {
        // 1. Crear nodo raíz invisible
        TreeItem<Object> rootItem = new TreeItem<Object>("ROOT");
        rootItem.setExpanded(true);

        // 2. Obtener datos
        ArrayList<Unidad> unidades = UnidadCRUD.getUnidadsByIdAsignatura(id_asignatura);
        
        if (unidades != null) {
            for (Unidad u : unidades) {
                // Nodo Padre: Unidad
                TreeItem<Object> unitItem = new TreeItem<Object>(u);
                unitItem.setExpanded(true); // Expandido por defecto

                // --- Añadir nodos hijos especiales ---
                
                // A. Nodo Descripción (Solo si existe descripción)
                if (u.getDescripcion() != null && !u.getDescripcion().trim().isEmpty()) {
                    TreeItem<Object> descItem = new TreeItem<Object>(new ItemDescripcion(u.getDescripcion()));
                    unitItem.getChildren().add(descItem);
                }

                // B. Nodo Cabecera "Temario"
                TreeItem<Object> headerItem = new TreeItem<Object>(new ItemCabecera("Temario:"));
                unitItem.getChildren().add(headerItem);

                // C. Nodos Módulos (Hijos reales)
                if (u.getModulos() != null) {
                    for (Modulo m : u.getModulos()) {
                        TreeItem<Object> moduleItem = new TreeItem<Object>(m);
                        unitItem.getChildren().add(moduleItem);
                    }
                }
                
                // Añadir la unidad a la raíz
                rootItem.getChildren().add(unitItem);
            }
        }

        // 3. Configurar el TreeView
        courseTreeView.setRoot(rootItem);
        courseTreeView.setShowRoot(false); // Ocultar ROOT

        // Configurar el renderizado personalizado
        setupCustomCellFactory();
    }

    // =========================================================
    //  Lógica 2: Renderizado personalizado (CellFactory)
    // =========================================================
    
    private void setupCustomCellFactory() {
        courseTreeView.setCellFactory(tv -> {
            TreeCell<Object> cell = new TreeCell<Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        // setContextMenu(null);
                        // Menú global para añadir unidad en espacio vacío
                        if(!AppSession.isAlumno()) setContextMenu(createGlobalContextMenu());
                    } else {
                        // --- Caso A: Unidad (Solo Nombre) ---
                        if (item instanceof Unidad) {
                            Unidad u = (Unidad) item;
                            
                            Label nameLbl = new Label(u.getNombre());
                            // Estilo: Grande y Negrita
                            nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                            
                            setText(null);
                            setGraphic(nameLbl);
                            if(!AppSession.isAlumno()) setContextMenu(createUnidadContextMenu(u, getTreeItem()));
                        } 
                        // --- Caso B: Descripción (Gris y Cursiva) ---
                        else if (item instanceof ItemDescripcion) {
                            ItemDescripcion desc = (ItemDescripcion) item;
                            
                            Label descLbl = new Label(desc.getTexto());
                            descLbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-font-size: 12px;");
                            descLbl.setWrapText(true);
                            descLbl.setMaxWidth(400); 
                            
                            setText(null);
                            setGraphic(descLbl);
                            if(!AppSession.isAlumno()) setContextMenu(null); // Sin menú para descripción
                        }
                        // --- Caso C: Cabecera "Temario" ---
                        else if (item instanceof ItemCabecera) {
                            ItemCabecera header = (ItemCabecera) item;
                            
                            Label headerLbl = new Label(header.getTitulo());
                            
                            headerLbl.setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-font-size: 11px; -fx-text-fill: #34495e;");
                            
                            setText(null);
                            setGraphic(headerLbl);
                            if(!AppSession.isAlumno()) setContextMenu(null); 
                        }
                        // --- Caso D: Módulo (Texto normal) ---
                        else if (item instanceof Modulo) {
                            Modulo m = (Modulo) item;
                            //System.out.println("DEBUG: 渲染 Modulo 节点 -> " + ((Modulo)item).getTitulo()); // 👈 加这行
                         // 打印一下 getTreeItem 看看是不是 null
//                            TreeItem<Object> currentItem = getTreeItem();
//                            if (currentItem == null) {
//                                System.out.println("DEBUG: 警告！getTreeItem() 是 null，菜单无法绑定！");
//                            }else {
//                            	System.out.println(currentItem);
//                            }
                            setText(m.getTitulo());
                            setGraphic(null);
                            if(!AppSession.isAlumno()) {
                                ContextMenu menu = createModuloContextMenu(m, getTreeItem());
                                setContextMenu(menu);
                            }
                       
                        }
                    }
                }
                
                
            };
            
            cell.setOnMouseClicked(event -> {
            	if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    Object item = cell.getItem();
                    if (item instanceof Modulo) {
                        Modulo m = (Modulo) item;
                        abrirArchivoLocal(m.getRuta_archivo()); // 调用打开文件的方法
                    }
                }
            });
            return cell;
        });
    }

    // =========================================================
    //  Context Menus (Menús contextuales)
    // =========================================================

    private void abrirArchivoLocal(String ruta) {
    	try {
            if (ruta == null || ruta.isEmpty()) {
            	mostrarAlerta("Error", "Este módulo no tiene un archivo asignado.");
                return;
            }

            File file = new File(ruta);
            if (file.exists()) {
                // Abrir archivo
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(file);
                } else {
                    mostrarAlerta("Error", "El sistema no soporta abrir archivos automáticamente.");
                }
            } else {
                mostrarAlerta("Error", "El archivo no existe:\n" + ruta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el archivo: " + e.getMessage());
        }
	}


     // Crea un diálogo para la gestión de módulos (Genérico)
    private Dialog<Modulo> createModuloFormDialog(String title, Unidad unidadDefault, Modulo moduloEditar) {
        Dialog<Modulo> dialog = new Dialog<>();
        dialog.setTitle(title);
        ButtonType saveBtn = new ButtonType("Guardar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        // 1. Campo nombre
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del módulo");
        if (moduloEditar != null) nameField.setText(moduloEditar.getTitulo());

        // 2. Campo archivo
        TextField pathField = new TextField();
        pathField.setEditable(false); // 只读，只能通过按钮修改
        if (moduloEditar != null) pathField.setText(moduloEditar.getRuta_archivo());

        Button fileBtn = new Button("Seleccionar Archivo");
        fileBtn.setOnAction(e -> {
            File f = new FileChooser().showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if(f != null) pathField.setText(f.getAbsolutePath());
        });

        // 3. combobox unidad
        ComboBox<Unidad> unitCombo = new ComboBox<>();
        unitCombo.getItems().addAll(UnidadCRUD.getUnidadsByIdAsignatura(this.id_asignatura));
        
        // configurar el texto
        unitCombo.setConverter(new StringConverter<Unidad>() {
            public String toString(Unidad u) { return u == null ? "" : u.getNombre(); }
            public Unidad fromString(String s) { return null; }
        });

        // selecionado por defecto
        int targetUnidadId = (moduloEditar != null) ? moduloEditar.getId_unidad() : unidadDefault.getId();
        for(Unidad u : unitCombo.getItems()) {
            if(u.getId() == targetUnidadId) { 
                unitCombo.getSelectionModel().select(u); 
                break; 
            }
        }

        grid.add(new Label("Nombre:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Archivo:"), 0, 1); grid.add(pathField, 1, 1); grid.add(fileBtn, 2, 1);
        grid.add(new Label("Unidad:"), 0, 2); grid.add(unitCombo, 1, 2);
        
        dialog.getDialogPane().setContent(grid);

        // resultado
        dialog.setResultConverter(b -> {
            if (b == saveBtn && !nameField.getText().isEmpty() && unitCombo.getValue() != null) {
                int id = (moduloEditar != null) ? moduloEditar.getId() : 0;
                return new Modulo(id, nameField.getText(), pathField.getText(), unitCombo.getValue().getId());
            }
            return null;
        });

        return dialog;
    }
    
    
    /**
     * Guardar archivo
     */
    private String guardarArchivoEnProyecto(String rutaOriginal) {
        if (rutaOriginal == null || rutaOriginal.isEmpty()) return null;
        
        File sourceFile = new File(rutaOriginal);
        File destDir = new File("archivos_curso"); 
        if (!destDir.exists()) destDir.mkdir();

        File destFile = new File(destDir, sourceFile.getName());

        try {
            java.nio.file.Files.copy(
                sourceFile.toPath(), 
                destFile.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Fallo al copiar el archivo: " + e.getMessage());
            return null;
        }
    }
    
    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
    
    
	private ContextMenu createGlobalContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem addUnit = new MenuItem("Añadir Nueva Unidad");
        addUnit.setOnAction(e -> handleAddUnidad());
        menu.getItems().add(addUnit);
        return menu;
    }
	
	private ContextMenu createLabelTemarioContextMenu(Unidad unidad) {
		ContextMenu menu = new ContextMenu();
		 MenuItem addModule = new MenuItem("Añadir Módulo");
	        addModule.setOnAction(e -> handleAddModulo(unidad)); 
	        menu.getItems().addAll(addModule);
	        return menu;
	}

    private ContextMenu createUnidadContextMenu(Unidad unidad, TreeItem<Object> item) {
        ContextMenu menu = new ContextMenu();

        MenuItem addModule = new MenuItem("Añadir Módulo");
        addModule.setOnAction(e -> handleAddModulo(unidad)); 

        MenuItem editUnit = new MenuItem("Editar Unidad");
        editUnit.setOnAction(e -> handleEditUnidad(unidad, item)); 

        MenuItem delUnit = new MenuItem("Eliminar Unidad");
        delUnit.setOnAction(e -> handleDeleteUnidad(unidad, item));
        
        MenuItem addUnit = new MenuItem("Añadir Nueva Unidad");
        addUnit.setOnAction(e -> handleAddUnidad());

        menu.getItems().addAll(addModule, new SeparatorMenuItem(), editUnit, delUnit,addUnit);
        return menu;
    }

    private ContextMenu createModuloContextMenu(Modulo modulo, TreeItem<Object> item) {
        ContextMenu menu = new ContextMenu();

        MenuItem editMod = new MenuItem("Editar Módulo");
        editMod.setOnAction(e -> handleEditModulo(modulo, item));

        MenuItem delMod = new MenuItem("Eliminar Módulo");
        delMod.setOnAction(e -> handleDeleteModulo(modulo, item));
        menu.getItems().addAll(editMod,delMod);
        return menu;
    }

    // =========================================================
    //  Lógica CRUD
    // =========================================================

    // --- Añadir Unidad ---
    private void handleAddUnidad() {
        Dialog<Pair<String, String>> dialog = createUnidadDialog("Nueva Unidad", "", "");
        dialog.showAndWait().ifPresent(pair -> {
            Unidad newUnidad = UnidadCRUD.createUnidad(pair.getKey(), pair.getValue(), this.id_asignatura);
            if (newUnidad != null) {
                // Crear nodo Unidad
                TreeItem<Object> newItem = new TreeItem<Object>(newUnidad);
                newItem.setExpanded(true);
                
                // Añadir hijos visuales (Descripción y Cabecera)
                if (!newUnidad.getDescripcion().isEmpty()) {
                    newItem.getChildren().add(new TreeItem<>(new ItemDescripcion(newUnidad.getDescripcion())));
                }
                newItem.getChildren().add(new TreeItem<>(new ItemCabecera("Temario:")));

                courseTreeView.getRoot().getChildren().add(newItem);
            }
        });
    }

    // --- Eliminar Unidad ---
    private void handleDeleteUnidad(Unidad unidad, TreeItem<Object> item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar unidad " + unidad.getNombre() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES && UnidadCRUD.deleteUnidad(unidad.getId())) {
                item.getParent().getChildren().remove(item);
            }
        });
    }
    
    // --- Editar Unidad (Complejo: actualiza nodos hijos) ---
    private void handleEditUnidad(Unidad unidad, TreeItem<Object> item) {
        Dialog<Pair<String, String>> dialog = createUnidadDialog("Editar", unidad.getNombre(), unidad.getDescripcion());
        dialog.showAndWait().ifPresent(pair -> {
            if (UnidadCRUD.updateUnidad(unidad.getId(), pair.getKey(), pair.getValue())) {
                String nuevoNombre = pair.getKey();
                String nuevaDesc = pair.getValue();

                // 1. Actualizar objeto y vista del padre (Nombre)
                unidad.setNombre(nuevoNombre);
                unidad.setDescripcion(nuevaDesc);
                
                // Forzar refresco del nodo padre (hack para disparar updateItem)
                TreeItem<Object> parent = item.getParent();
                int index = parent.getChildren().indexOf(item);
                parent.getChildren().set(index, item); 

                // 2. Actualizar el nodo hijo de "Descripción"
                TreeItem<Object> descItemNode = null;
                for (TreeItem<Object> child : item.getChildren()) {
                    if (child.getValue() instanceof ItemDescripcion) {
                        descItemNode = child;
                        break;
                    }
                }

                if (descItemNode != null) {
                    // Si ya existía un nodo descripción...
                    if (nuevaDesc.trim().isEmpty()) {
                        // Si la nueva descripción está vacía, eliminamos el nodo
                        item.getChildren().remove(descItemNode);
                    } else {
                        // Si no, actualizamos su texto
                        descItemNode.setValue(new ItemDescripcion(nuevaDesc));
                    }
                } else {
                    // Si NO existía nodo descripción y ahora hay texto...
                    if (!nuevaDesc.trim().isEmpty()) {
                        // Lo añadimos al principio (índice 0)
                        item.getChildren().add(0, new TreeItem<>(new ItemDescripcion(nuevaDesc)));
                    }
                }
            }
        });
    }

   
    private void handleAddModulo(Unidad currentUnidad) {
        // null -> añadir modulo
        Dialog<Modulo> dialog = createModuloFormDialog("Nuevo Módulo", currentUnidad, null);
        
        dialog.showAndWait().ifPresent(tempMod -> {
            // 1. guardar el archivo
            String finalPath = guardarArchivoEnProyecto(tempMod.getRuta_archivo());
            
            // 2. Guardar en la tabla modulo
            // Modulo addModulo(String titulo, String ruta_archivo, int id_unidad)
            // System.out.println(finalPath);
            Modulo newMod = ModuloCRUD.addModulo(tempMod.getTitulo(), finalPath, tempMod.getId_unidad());
            
            // 3. Actualizar UI
            if (newMod != null) {
                TreeItem<Object> targetParentItem = findTreeItemByUnidadId(newMod.getId_unidad());
                if (targetParentItem != null) {
                    targetParentItem.getChildren().add(new TreeItem<Object>(newMod));
                    targetParentItem.setExpanded(true);
                }
            }
        });
    }
    // --- Eliminar Módulo ---
    private void handleDeleteModulo(Modulo modulo, TreeItem<Object> item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar módulo?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES && ModuloCRUD.deleteModulo(modulo.getId())) {
                item.getParent().getChildren().remove(item);
            }
        });
    }

    
    private void handleEditModulo(Modulo modulo, TreeItem<Object> item) {
    

        Unidad currentUnidad = (Unidad) item.getParent().getValue(); 
        Dialog<Modulo> dialog = createModuloFormDialog("Editar Módulo", currentUnidad, modulo);

        dialog.showAndWait().ifPresent(resultMod -> {
            String finalPath = modulo.getRuta_archivo(); 

            // --- A. Guardar archivo
            // Si la nueva ruta no está vacía y es diferente de la ruta anterior, significa que el usuario ha seleccionado un nuevo archivo.
            if (resultMod.getRuta_archivo() != null && !resultMod.getRuta_archivo().equals(modulo.getRuta_archivo())) {
                String newStoredPath = guardarArchivoEnProyecto(resultMod.getRuta_archivo());
                if (newStoredPath != null) {
                    finalPath = newStoredPath;
                }
            }

            // --- B. guardar en la tabla modulo ---
            System.out.println(finalPath);
            boolean success = ModuloCRUD.editModulo(
                modulo.getId(), 
                resultMod.getTitulo(), 
                finalPath, 
                resultMod.getId_unidad() // Esta es la nueva ID seleccionada por el usuario en el menú desplegable.
            );

            if (success) {
                // --- C. Actualizar el model clase ---
                modulo.setTitulo(resultMod.getTitulo());
                modulo.setRuta_archivo(finalPath);
                int oldUnidadId = modulo.getId_unidad();
                int newUnidadId = resultMod.getId_unidad();
                modulo.setId_unidad(newUnidadId); // 更新 ID

                // --- D. actualizar TreeView UI ---
                
                // Caso 1: La unidad en sí no ha cambiado; solo se ha modificado el texto o el nombre del archivo.
                if (oldUnidadId == newUnidadId) {
                    // Forzar la actualización del nodo actual (activa la actualización de CellFactory)
                    // Consejos: Establézcalo primero en nulo y luego restablézcalo, o use fireEvent directamente. La forma más sencilla es restablecer el valor.
                    item.setValue(null); 
                    item.setValue(modulo); 
                    // 或者更优雅的：courseTreeView.refresh();
                } 
                // Caso 2: El usuario movió el módulo a otra unidad (una situación problemática).
                else {
                    // 1. Remove
                    item.getParent().getChildren().remove(item);

                    // 2. add
                    TreeItem<Object> newParentItem = findTreeItemByUnidadId(newUnidadId);
                    if (newParentItem != null) {
                        newParentItem.getChildren().add(item);
                        newParentItem.setExpanded(true);
                    }
                }
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el módulo en la base de datos.");
            }
        });
    }

    // =========================================================
    //  Métodos Auxiliares
    // =========================================================

    private TreeItem<Object> findTreeItemByUnidadId(int unidadId) {
        for (TreeItem<Object> unitItem : courseTreeView.getRoot().getChildren()) {
            Object value = unitItem.getValue();
            if (value instanceof Unidad) {
                if (((Unidad) value).getId() == unidadId) {
                    return unitItem;
                }
            }
        }
        return null;
    }

    private Dialog<Pair<String, String>> createUnidadDialog(String title, String name, String desc) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        ButtonType saveBtn = new ButtonType("Guardar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        
        TextField nameF = new TextField(name);
        TextArea descF = new TextArea(desc); descF.setPrefRowCount(3);
        
        grid.add(new Label("Nombre:"), 0, 0); grid.add(nameF, 1, 0);
        grid.add(new Label("Desc:"), 0, 1); grid.add(descF, 1, 1);
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(b -> (b == saveBtn) ? new Pair<>(nameF.getText(), descF.getText()) : null);
        return dialog;
    }

    private Dialog<Modulo> createAddModuloDialog(Unidad currentUnidad) {
        Dialog<Modulo> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Módulo");
        ButtonType saveBtn = new ButtonType("Guardar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Nombre");
        
        TextField pathField = new TextField();
        pathField.setEditable(false);
        Button fileBtn = new Button("File");
        fileBtn.setOnAction(e -> {
            File f = new FileChooser().showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if(f!=null) pathField.setText(f.getAbsolutePath());
        });

        ComboBox<Unidad> unitCombo = new ComboBox<>();
        unitCombo.getItems().addAll(UnidadCRUD.getUnidadsByIdAsignatura(this.id_asignatura));
        unitCombo.setConverter(new StringConverter<Unidad>() {
            public String toString(Unidad u) { return u==null?"":u.getNombre(); }
            public Unidad fromString(String s) { return null; }
        });
        
        for(Unidad u : unitCombo.getItems()) {
            if(u.getId() == currentUnidad.getId()) { unitCombo.getSelectionModel().select(u); break; }
        }

        grid.add(new Label("Nombre:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Archivo:"), 0, 1); grid.add(pathField, 1, 1); grid.add(fileBtn, 2, 1);
        grid.add(new Label("Unidad:"), 0, 2); grid.add(unitCombo, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(b -> {
            if (b == saveBtn && !nameField.getText().isEmpty() && unitCombo.getValue() != null) {
                return new Modulo(0, nameField.getText(), pathField.getText(), unitCombo.getValue().getId());
            }
            return null;
        });
        
        return dialog;
    }
}



/**
 * Clase auxiliar para representar el nodo de descripción en el TreeView.
 */
class ItemDescripcion {
    private String texto;
    public ItemDescripcion(String texto) { this.texto = texto; }
    public String getTexto() { return texto; }
    @Override public String toString() { return texto; }
}

/**
 * Clase auxiliar para representar la cabecera fija "Temario".
 */
class ItemCabecera {
    private String titulo;
    public ItemCabecera(String titulo) { this.titulo = titulo; }
    public String getTitulo() { return titulo; }
}


