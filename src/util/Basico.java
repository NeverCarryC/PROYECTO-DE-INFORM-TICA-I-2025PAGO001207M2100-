package util;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class Basico {
	 public static  void back(ActionEvent event, String fxmlPath) {
	    	FXMLLoader loader = new FXMLLoader(Basico.class.getResource(fxmlPath));
	    	Parent newView;
			try {
				newView = loader.load();

		    	Node source = (Node) event.getSource();
		    	BorderPane rootPane = (BorderPane) source.getScene().getRoot();
		    	
		        rootPane.setCenter(newView);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    	
	    }

}
