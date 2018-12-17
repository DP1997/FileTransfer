package application;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;


public class ErrorPane extends AnchorPane {

    @FXML
    private Button exit_imgv;

    @FXML
    private ImageView error_imgV;

    @FXML
    private Label titel_label, content_label;
    
    public ErrorPane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ErrorPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
			loader.load();
		} catch (IOException e) {
            throw new RuntimeException(e);
		}
    }
    
    public void setTitle(String s) {
    	assert(s != null);
    	titel_label.setText(s);
    }
    
    public void setContent(String s) {
    	assert(s != null);
    	content_label.setText(s);
    }
    
    @FXML
    public void exit(){
    	
    }
}
