package application;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import datatypes.FileInformation;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import javafx.stage.Window;

import transfer.*;

public class FileTransferController implements Initializable{

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
    private Label labelConnection, labelNoConnection, labelErrorConnection, labelTryConnect, labelWrongInput;




    @FXML
    private ListView<String> listView;
    
    private ObservableList<String> items;
   
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
    	items = FXCollections.observableArrayList();
    	listView.setItems(items);
    	listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	System.out.println("listView successfully populated");	;	

    	/*
    	TableColumn<FileInformation, String> fileNameCol = new TableColumn<FileInformation, String>("FILENAME");
    	fileNameCol.setCellValueFactory(new PropertyValueFactory<FileInformation, String>("fileName"));

    	TableColumn<FileInformation, String> fileLengthCol = new TableColumn<FileInformation, String>("SIZE IN BYTES");
    	fileNameCol.setCellValueFactory(new PropertyValueFactory<FileInformation, String>("fileLength"));    	
    	
    	//tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	tableView.getColumns().addAll(fileNameCol, fileLengthCol);

    	tableView.setItems(items);
    	*/
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
    public void handleMouseClick(MouseEvent e){
    	ImageView source = (ImageView) e.getSource();
    	
    	//connectionView
    	if(source.getId().equals("connect") && connect.isVisible()) {
    		establishConnection();
    	}
    	
    	else if(source.getId().equals("disconnect") && disconnect.isVisible()) {
    		deleteConnection();
    	}
    	    	
    	//downloadView
    	else if(source.getId().equals("button_download")) {
    		//request file download
    		requestFileDownload();
    	}
    	else if(source.getId().equals("button_refresh")) {
    		//request file refresh
    		requestFileListRefresh();
    	}
    	else if(source.getId().equals("button_explorer")) {
    		//open file explorer view
    	}
    	
    	//settingsView
    	else if(source.getId().equals("button_explorer2")) {
    		chooseDownloadDirectory(e);
    	}
    }
    
    private void establishConnection(){
    	clearAllGUI();
    	for(int i = 0; i < 5; i ++) {
	    	try {
	    		String serverIP = textfield_ip.getText();
	    		String serverPort = textfield_port.getText();
	    		TCPClient.connectToServer(serverIP, serverPort);
	    		connectionSuc();
	    		break;
	    	} catch(SocketTimeoutException e) {
	    		System.out.println("Timeout-Error");
	    		if(i == 4) connectionTimeoutOver();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		System.out.println("Eingabe-Error");
	    		connectionIOError();
	    		break;
			}
    	}
    }
    private void connect() {
    	clearAllGUI();
    	labelTryConnect.setVisible(true);
    	noConnection.setVisible(false);
    	connect.setVisible(false);
    	textfield_ip.setEditable(false);
    	textfield_port.setEditable(false);
    }
    private void connectionSuc() {
    	clearAllGUI();
    	labelTryConnect.setVisible(false);
    	labelConnection.setVisible(true);
    	connectionEstablished.setVisible(true);
    	disconnect.setVisible(true);
    	textfield_ip.setEditable(false);
    	textfield_port.setEditable(false);
    }
    private void connectionTimeoutOver() {
    	clearAllGUI();
    	labelErrorConnection.setVisible(true);
    	noConnection.setVisible(true);
    	connect.setVisible(true);
    }
    private void connectionIOError() {
    	clearAllGUI();
    	noConnection.setVisible(true);
    	labelWrongInput.setVisible(true);
    	connect.setVisible(true);
    }
    private void clearAllGUI() {
    	labelConnection.setVisible(false);
    	labelNoConnection.setVisible(false);
    	labelErrorConnection.setVisible(false);
    	labelTryConnect.setVisible(false);
    	labelWrongInput.setVisible(false);
    	
    	connect.setVisible(false);
    	disconnect.setVisible(false);
    	noConnection.setVisible(false);
    	connectionEstablished.setVisible(false);
    	textfield_ip.setEditable(true);
    	textfield_port.setEditable(true);
    	
    }

    private void receiveDirInformation() {
		TCPClient.receiveDirInformation();	
	}

	private void deleteConnection() {
    	try {
			clearAllGUI();
	    	noConnection.setVisible(true);
	    	connect.setVisible(true);
	    	labelNoConnection.setVisible(true);
			TCPClient.deleteConnection();
			System.out.println("Verbindung getrennt");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
   
    private void requestFileDownload() {
    	//read marked list entry
    	String row = listView.getSelectionModel().getSelectedItem();
    	//aufpassen bei mehreren , im String
    	String[] fileName = row.split(",");
    	TCPClient.contactServer(fileName[0]);
    	//TCPClient.downloadFileFromServer(fileName[0]); 
    }
    
    private void requestFileListRefresh() {
    	TCPClient.contactServer("refresh");
    	TCPClient.receiveDirInformation();
    	listView.getItems().clear();
		for (FileInformation fi : TCPClient.fileInformation) {
			listView.getItems().add(fi.fileName+ ", " + fi.fileLength + " Bytes");
		}
    	
    }


}
    	

