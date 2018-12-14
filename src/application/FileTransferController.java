package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import javafx.stage.Window;

import transfer.*;

public class FileTransferController {

    @FXML
    private ImageView conView_indic, downloadView_indic, settingsView_indic;

    @FXML
    private AnchorPane topbar, downloadView, connectionView, settingsView;

    @FXML
    private ImageView button_download, button_explorer, button_refresh, button_explorer2
    				  ,openConView, openDownloadView, openSettingsView, shutdown, connectToServer, conEstablished,
    				  noConnection, connectionEstablished, geprueftHaken, disconnect, connect;

    @FXML
    private TextField textfield_port, textfield_ip, textfield_dpath;
    
    @FXML
    private Label labelConnection, labelNoConnection, labelErrorConnection;

    
    private static ObservableList<String> items = null;
    private static ListView<String> list 		= null;
    
    public FileTransferController() {
    	items = FXCollections.observableArrayList();
    	list = new ListView<>(items);
    }
    
    @FXML
    public void topBarIconClicked(MouseEvent e) {
    	ImageView source = (ImageView) e.getSource();
    	if(source.getId().equals("openConView")) {
    		if(connectionView.isVisible()) {
    			visibilityControl(connectionView, conView_indic, false);
    		}
    		else {
    			visibilityControl(connectionView, conView_indic, true);
    			visibilityControl(downloadView, downloadView_indic, false);
    			visibilityControl(settingsView, settingsView_indic, false);
    		}
    	}
    	else if(source.getId().equals("openDownloadView")) {
      		if(downloadView.isVisible()) {
    			visibilityControl(downloadView, downloadView_indic, false);
    		}
    		else {
    			visibilityControl(downloadView, downloadView_indic, true);
    			visibilityControl(connectionView, conView_indic, false);
    			visibilityControl(settingsView, settingsView_indic, false);
    		}
    	}
    	else if(source.getId().equals("openSettingsView")) {
      		if(settingsView.isVisible()) {
    			visibilityControl(settingsView, settingsView_indic, false);
    		}
    		else {
    			visibilityControl(settingsView, settingsView_indic, true);
    			visibilityControl(downloadView, downloadView_indic, false);
    			visibilityControl(connectionView, conView_indic, false);
    		}
    	}
    	else if(source.getId().equals("shutdown")) {
    		Platform.exit();
    	}
    	
    }
    
    private void visibilityControl(AnchorPane ap, ImageView iv_indic, boolean visible) {
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
        
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Download-Ordner angeben");
		File selectedDirectory = chooser.showDialog(stage);
		if(selectedDirectory != null) {
			textfield_dpath.setText(selectedDirectory.getAbsolutePath());
			geprueftHaken.setVisible(true);
		}
	}
    
    @FXML
    public void handleMouseClick(MouseEvent e) {
    	ImageView source = (ImageView) e.getSource();
    	
    	//connectionView
    	if(source.getId().equals("connect") && connect.isVisible()) {
    		establishConnection();
    	}
    	if(source.getId().equals("disconnect") && disconnect.isVisible()) {
    		deleteConnection();
    	}    	
    	//downloadView
    	if(source.getId().equals("button_download")) {
    		//request file download
    		requestFileDownload();
    	}
    	if(source.getId().equals("button_refresh")) {
    		//request file refresh
    		requestFileListRefresh();
    	}
    	if(source.getId().equals("button_explorer")) {
    		//open file explorer view
    	}
    	
    	//settingsView
    	if(source.getId().equals("button_explorer2")) {
    		chooseDownloadDirectory(e);
    	}
    }
    
    private void establishConnection() {
    	boolean connected = false;
    	String ip = textfield_ip.getText();
    	String port = textfield_port.getText();
    	try {
			TCPClient.connectToServer(ip, Integer.valueOf(port));
			connected = true;
		} catch (Exception e) {
			labelErrorConnection.setVisible(true);
			labelNoConnection.setVisible(false);
			e.printStackTrace();
		}
    	if(connected) {
		noConnection.setVisible(false);
		labelNoConnection.setVisible(false);
		
		labelConnection.setVisible(true);
		connectionEstablished.setVisible(true);
		/*
		disconnect.setVisible(true);
		connect.setVisible(false);
		*/
    	}
    }
    private void deleteConnection() {
    	TCPClient.deleteConnection();
    }
    
    
    private void requestFileDownload() {
    	//read marked list entry
    	String fileName = "";
    	TCPClient.contactServer(fileName);
    	TCPClient.downloadFileFromServer(fileName);
    }
    
    private void requestFileListRefresh() {
    	TCPClient.contactServer("refresh");
    	TCPClient.receiveDirInformation();
		for (int i = 0; i < TCPClient.fileNames.size(); i++) {
			list.getItems().add(TCPClient.fileNames.get(i) + ", " + TCPClient.fileLengths.get(i) + " Bytes");
		}
    }
}
    	

