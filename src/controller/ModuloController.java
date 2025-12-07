package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import db.ModuloCRUD;
import db.UnidadCRUD;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.util.StringConverter;
import model.Modulo;
import model.Unidad;

public class ModuloController {

    private int id_asignatura;

    @FXML
    private TreeView<Object> courseTreeView;

    public void initialize() {
        // Esperar a que se llame a setId_asignatura
    }

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
                        setContextMenu(null);
                        // Menú global para añadir unidad en espacio vacío
                        setContextMenu(createGlobalContextMenu());
                    } else {
                        // --- Caso A: Unidad (Solo Nombre) ---
                        if (item instanceof Unidad) {
                            Unidad u = (Unidad) item;
                            
                            Label nameLbl = new Label(u.getNombre());
                            // Estilo: Grande y Negrita
                            nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                            
                            setText(null);
                            setGraphic(nameLbl);
                            setContextMenu(createUnidadContextMenu(u, getTreeItem()));
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
                            setContextMenu(null); // Sin menú para descripción
                        }
                        // --- Caso C: Cabecera "Temario" ---
                        else if (item instanceof ItemCabecera) {
                            ItemCabecera header = (ItemCabecera) item;
                            
                            Label headerLbl = new Label(header.getTitulo());
                            headerLbl.setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-font-size: 11px; -fx-text-fill: #34495e;");
                            
                            setText(null);
                            setGraphic(headerLbl);
                            setContextMenu(null); 
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
                            ContextMenu menu = createModuloContextMenu(m, getTreeItem());
                            setContextMenu(menu);
                            //setContextMenu(createModuloContextMenu(m, getTreeItem()));
                        }
                    }
                }
                
                
            };
            
            cell.setOnMouseClicked(event -> {
            	if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    Object item = cell.getItem();
                    
                    // 3. 只有当点击的是 Modulo 时才触发
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
                // 使用 Java AWT Desktop 类打开文件
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

    /**
     * 创建模块对话框 (通用)
     * @param unidadDefault 默认选中的单元
     * @param moduloEditar 如果是编辑模式，传入旧模块对象；如果是新建，传入 null
     */
    private Dialog<Modulo> createModuloFormDialog(String title, Unidad unidadDefault, Modulo moduloEditar) {
        Dialog<Modulo> dialog = new Dialog<>();
        dialog.setTitle(title);
        ButtonType saveBtn = new ButtonType("Guardar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        // 1. 名字字段
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del módulo");
        if (moduloEditar != null) nameField.setText(moduloEditar.getTitulo());

        // 2. 文件字段
        TextField pathField = new TextField();
        pathField.setEditable(false); // 只读，只能通过按钮修改
        if (moduloEditar != null) pathField.setText(moduloEditar.getRuta_archivo());

        Button fileBtn = new Button("Seleccionar Archivo");
        fileBtn.setOnAction(e -> {
            File f = new FileChooser().showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if(f != null) pathField.setText(f.getAbsolutePath());
        });

        // 3. 单元下拉框
        ComboBox<Unidad> unitCombo = new ComboBox<>();
        unitCombo.getItems().addAll(UnidadCRUD.getUnidadsByIdAsignatura(this.id_asignatura));
        
        // 设置下拉框显示的文字
        unitCombo.setConverter(new StringConverter<Unidad>() {
            public String toString(Unidad u) { return u == null ? "" : u.getNombre(); }
            public Unidad fromString(String s) { return null; }
        });

        // 选中默认单元
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

        // 转换结果
        dialog.setResultConverter(b -> {
            if (b == saveBtn && !nameField.getText().isEmpty() && unitCombo.getValue() != null) {
                // 返回一个临时的 Modulo 对象，ID 设为 0 或者保留原 ID
                int id = (moduloEditar != null) ? moduloEditar.getId() : 0;
                return new Modulo(id, nameField.getText(), pathField.getText(), unitCombo.getValue().getId());
            }
            return null;
        });

        return dialog;
    }
    
    
    /**
     * 辅助方法：将源文件复制到项目目录 "archivos_curso"
     * @param rutaOriginal 用户选择的源文件路径
     * @return 复制后的新绝对路径 (如果出错返回 null)
     */
    private String guardarArchivoEnProyecto(String rutaOriginal) {
        if (rutaOriginal == null || rutaOriginal.isEmpty()) return null;
        
        File sourceFile = new File(rutaOriginal);
        File destDir = new File("archivos_curso"); 
        if (!destDir.exists()) destDir.mkdir();

        // 简单起见用原文件名，实际项目建议加 UUID 防止重名
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
    
 // 简单的弹窗辅助方法
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

    // --- Añadir Módulo ---
 // 修改原本的 handleAddModulo 方法
//    private void handleAddModulo(Unidad currentUnidad) {
//        // 1. 弹出对话框获取用户输入（包含源文件路径）
//        Dialog<Modulo> dialog = createAddModuloDialog(currentUnidad);
//
//        dialog.showAndWait().ifPresent(tempMod -> {
//            String rutaOriginal = tempMod.getRuta_archivo();
//            String rutaFinal = rutaOriginal; // 默认等于原路径
//
//            // 2. 如果用户选了文件，执行“上传”（复制）逻辑
//            if (rutaOriginal != null && !rutaOriginal.isEmpty()) {
//                File sourceFile = new File(rutaOriginal);
//                
//                // 定义你的存储目录，比如项目根目录下的 "archivos_curso"
//                File destDir = new File("archivos_curso"); 
//                if (!destDir.exists()) {
//                    destDir.mkdir(); // 如果目录不存在，创建它
//                }
//
//                // 为了防止文件名冲突，最好加个时间戳或者UUID，这里简单演示用原名
//                // 比如: archivos_curso/documento.pdf
//                File destFile = new File(destDir, sourceFile.getName());
//
//                try {
//                    // 【核心代码】复制文件 (StandardCopyOption.REPLACE_EXISTING 表示如果存在则覆盖)
//                    java.nio.file.Files.copy(
//                        sourceFile.toPath(), 
//                        destFile.toPath(), 
//                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
//                    );
//
//                    // 3. 更新路径为新的相对路径或绝对路径
//                    // 建议存绝对路径方便打开，或者存相对路径但在打开时拼接
//                    rutaFinal = destFile.getAbsolutePath(); 
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    mostrarAlerta("Error al subir archivo", "No se pudo copiar el archivo.");
//                    return; // 如果复制失败，中断保存
//                }
//            }
//
//            // 4. 保存到数据库 (注意这里用的是 rutaFinal)
//            Modulo newMod = ModuloCRUD.addModulo(
//                tempMod.getTitulo(), 
//                rutaFinal, 
//                tempMod.getId_unidad()
//            );
//
//            // 5. 更新 UI
//            if (newMod != null) {
//                TreeItem<Object> targetParentItem = findTreeItemByUnidadId(newMod.getId_unidad());
//                if (targetParentItem != null) {
//                    targetParentItem.getChildren().add(new TreeItem<>(newMod)); // 记得用 TreeItem<Object>
//                    targetParentItem.setExpanded(true);
//                }
//            }
//        });
//    }
    private void handleAddModulo(Unidad currentUnidad) {
        // 传入 null 表示是“新建模式”
        Dialog<Modulo> dialog = createModuloFormDialog("Nuevo Módulo", currentUnidad, null);
        
        dialog.showAndWait().ifPresent(tempMod -> {
            // 1. 处理文件
            String finalPath = guardarArchivoEnProyecto(tempMod.getRuta_archivo());
            
            // 2. 存库
            // Modulo addModulo(String titulo, String ruta_archivo, int id_unidad)
            System.out.println(finalPath);
            Modulo newMod = ModuloCRUD.addModulo(tempMod.getTitulo(), finalPath, tempMod.getId_unidad());
            
            // 3. 更新 UI
            if (newMod != null) {
                TreeItem<Object> targetParentItem = findTreeItemByUnidadId(newMod.getId_unidad());
                if (targetParentItem != null) {
                    // 记得用 Object 泛型
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

    // --- Editar Módulo ---
//    private void handleEditModulo(Modulo modulo, TreeItem<Object> item) {
//        TextInputDialog dialog = new TextInputDialog(modulo.getTitulo());
//        dialog.setHeaderText("Editar nombre");
//        dialog.showAndWait().ifPresent(newName -> {
//            if (ModuloCRUD.editModulo(modulo.getId(), newName, modulo.getRuta_archivo(), modulo.getId_unidad())) {
//                modulo.setTitulo(newName);
//                
//                // Refrescar item
//                TreeItem<Object> parent = item.getParent();
//                int index = parent.getChildren().indexOf(item);
//                parent.getChildren().set(index, item); 
//            }
//        });
//    }
    
    private void handleEditModulo(Modulo modulo, TreeItem<Object> item) {
        // 1. 调用上面的通用对话框，传入当前模块作为初始值
        // 注意：这里 findUnidadById 是个假设的方法，你可以直接从 item.getParent().getValue() 获取当前单元
        Unidad currentUnidad = (Unidad) item.getParent().getValue(); 
        Dialog<Modulo> dialog = createModuloFormDialog("Editar Módulo", currentUnidad, modulo);

        dialog.showAndWait().ifPresent(resultMod -> {
            String finalPath = modulo.getRuta_archivo(); // 默认保持旧路径

            // --- A. 检查文件是否改变 ---
            // 如果新路径不为空，且和旧路径不一样 -> 说明用户选了新文件
            if (resultMod.getRuta_archivo() != null && !resultMod.getRuta_archivo().equals(modulo.getRuta_archivo())) {
                String newStoredPath = guardarArchivoEnProyecto(resultMod.getRuta_archivo());
                if (newStoredPath != null) {
                    finalPath = newStoredPath;
                }
            }

            // --- B. 更新数据库 ---
            System.out.println(finalPath);
            boolean success = ModuloCRUD.editModulo(
                modulo.getId(), 
                resultMod.getTitulo(), 
                finalPath, 
                resultMod.getId_unidad() // 这里是用户在下拉框选的新ID
            );

            if (success) {
                // --- C. 更新内存对象 ---
                modulo.setTitulo(resultMod.getTitulo());
                modulo.setRuta_archivo(finalPath);
                int oldUnidadId = modulo.getId_unidad();
                int newUnidadId = resultMod.getId_unidad();
                modulo.setId_unidad(newUnidadId); // 更新 ID

                // --- D. 更新 TreeView UI ---
                
                // 情况 1: 单元没变，只是改了字或文件
                if (oldUnidadId == newUnidadId) {
                    // 强制刷新当前节点（触发 CellFactory 更新）
                    // 技巧：先设为 null 再设回来，或者直接用 fireEvent，最简单是重置一下 value
                    item.setValue(null); 
                    item.setValue(modulo); 
                    // 或者更优雅的：courseTreeView.refresh();
                } 
                // 情况 2: 用户把模块移动到了另一个单元 (麻烦的情况)
                else {
                    // 1. 从旧爸爸那里移除自己
                    item.getParent().getChildren().remove(item);

                    // 2. 找新爸爸
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

// =========================================================
//  Clases auxiliares (Helper Classes)
// =========================================================

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


