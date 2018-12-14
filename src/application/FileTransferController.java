package application;

import java.io.File;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import javafx.stage.Window;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import transfer.*;

public class FileTransferController {

    @FXML
    private ImageView conView_indic, downloadView_indic, settingsView_indic;

    @FXML
    private AnchorPane topbar, downloadView, connectionView, settingsView;

    @FXML
    private ImageView button_download, button_explorer, button_refresh, button_explorer2
    				  ,openConView, openDownloadView, openSettingsView, shutdown, connectToServer, conEstablished;

    @FXML
    private TextField textfield_port, textfield_ip, textfield_dpath;
    
    @FXML
    public void topBarIconClicked(MouseEvent e) {
    	ImageView source = (ImageView) e.getSource();
    	if(source.getId().equals("openConView")) {
    		if(connectionView.isVisible()) {
    			visibilityControll(connectionView, conView_indic, false);
    		}
    		else {
    			visibilityControll(connectionView, conView_indic, true);
    			visibilityControll(downloadView, downloadView_indic, false);
    			visibilityControll(settingsView, settingsView_indic, false);
    		}
    	}
    	else if(source.getId().equals("openDownloadView")) {
      		if(downloadView.isVisible()) {
    			visibilityControll(downloadView, downloadView_indic, false);
    		}
    		else {
    			visibilityControll(downloadView, downloadView_indic, true);
    			visibilityControll(connectionView, conView_indic, false);
    			visibilityControll(settingsView, settingsView_indic, false);
    		}
    	}
    	else if(source.getId().equals("openSettingsView")) {
      		if(settingsView.isVisible()) {
    			visibilityControll(settingsView, settingsView_indic, false);
    		}
    		else {
    			visibilityControll(settingsView, settingsView_indic, true);
    			visibilityControll(downloadView, downloadView_indic, false);
    			visibilityControll(connectionView, conView_indic, false);
    		}
    	}
    	else if(source.getId().equals("shutdown")) {
    		Platform.exit();
    	}
    	
    }
    
    private void visibilityControll(AnchorPane ap, ImageView iv_indic, boolean visible) {
    	if(visible) {
    		ap.setVisible(true);
    		iv_indic.setVisible(true);
    	} else {
    		ap.setVisible(false);
    		iv_indic.setVisible(false);
    	}
    }

	private void chooseDownloadDirectory(MouseEvent e) {
    	// get Stage
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        
    	String input = "Bitte Pfad angeben";
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Download-Ordner angeben");
		File selectedDirectory = chooser.showDialog(stage);
		if(selectedDirectory != null) {
			textfield_dpath.setText(selectedDirectory.getAbsolutePath());
		}
	}
    
    @FXML
    public void handleMouseClick(MouseEvent e) {
    	ImageView source = (ImageView) e.getSource();
    	
    	//connectionView
    	if(source.getId().equals("connectToServer")) {
    		establishConnection();
    	}
    	
    	//downloadView
    	if(source.getId().equals("button_download")) {
    		//download file
    	}
    	if(source.getId().equals("button_refresh")) {
    		//request file refresh
    	}
    	if(source.getId().equals("button_explorer")) {
    		//open file explorer view
    	}
    }
    
    private void establishConnection() {
    	//read textfields
    	//do socket garbage
    }
    
    private void requestFileDownload() {
    	//read marked list entry
    	String fileName = "";
    	TCPClient.contactServer(clientSocket, fileName);
    }
}
    	

