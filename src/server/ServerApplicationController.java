package server;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.TCPClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import shared_resources.datatypes.FileInformation;
import shared_resources.datatypes.ProgressStream;

public class ServerApplicationController implements Initializable{

	//topbar
    @FXML
    private AnchorPane topbar;
    @FXML
    private ImageView bt_openConView, bt_openInfoView, bt_shutdown, bt_minimize, bt_openClientCon;

    //views
    @FXML
    private AnchorPane infoView, clientConView, connectionView;
    
    //viewIndicators
    @FXML
    private ImageView imgV_conViewIndic, imgV_clientConViewIndic, imgV_infoViewIndic;
    
    //connectionView
    @FXML
    private TextField tf_port, tf_sharePath;
    @FXML
    private Label lb_offlineText, lb_onlineText;
    @FXML
    private ImageView lb_offline, lb_online, bt_hostServer, bt_openExplorer;

    //clientConnectionView
    @FXML
    private Label lb_clientKicked;
    @FXML
    private ListView<String> lv_clients;
    @FXML
    private ImageView bt_kickClient;
    
    //informationView
    @FXML
    private Label lb_serverIP, lb_serverPort, lb_sharePath;
    @FXML
    private ImageView imgV_infoChecked;


    
    private ObservableList<String> items;
    //private ImageView imageView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
    	items = FXCollections.observableArrayList();
    	lv_clients.setItems(items);
    	lv_clients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	   @FXML
	    public void topBarIconClicked(MouseEvent e) {
	    	ImageView source = (ImageView) e.getSource();
	    	if(source.getId().equals("openConView")) {
	    		if(connectionView.isVisible()) {
	    			visibilityControl(connectionView, imgV_conViewIndic, false);
	    		}
	    		else {
	    			visibilityControl(connectionView, imgV_conViewIndic, true);
	    			visibilityControl(clientConView, imgV_clientConViewIndic, false);
	    			visibilityControl(infoView, imgV_infoViewIndic, false);
	    		}
	    	}
	    	else if(source.getId().equals("openclientConView")) {
	      		if(clientConView.isVisible()) {
	    			visibilityControl(clientConView, imgV_clientConViewIndic, false);
	    		}
	    		else {
	    			visibilityControl(clientConView, imgV_clientConViewIndic, true);
	    			visibilityControl(connectionView, imgV_conViewIndic, false);
	    			visibilityControl(infoView, imgV_infoViewIndic, false);
	    		}
	    	}
	    	else if(source.getId().equals("openSettingsView")) {
	      		if(infoView.isVisible()) {
	    			visibilityControl(infoView, imgV_infoViewIndic, false);
	    		}
	    		else {
	    			visibilityControl(infoView, imgV_infoViewIndic, true);
	    			visibilityControl(clientConView, imgV_clientConViewIndic, false);
	    			visibilityControl(connectionView, imgV_conViewIndic, false);
	    		}
	    	}
	    	else if(source.getId().equals("shutdown")) {
	    		Platform.exit();
	    	}
	    	else if(source.getId().equals("minimize")) {
	    		minimizeStageOfNode((Node) e.getSource());
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
				tf_sharePath.setText(selectedDirectory.getAbsolutePath());
				// Der Pfad muss an das Betriebssystem angepasst werden
				// Bei Windows wird der Pfad mit \\ angegeben, bei Linux mit /
				String os = System.getProperty("os.name").toLowerCase();
				String textField = tf_sharePath.getText();
				String downloadPath = textField;
				// windows
				if(os.contains("win")) {
					System.out.println("windows erkannt");
					 downloadPath = textField.replace("\\","\\\\") + "\\\\";
				}
				if(os.contains("nix") || os.contains("nux")) {
					System.out.println("linux erkannt");
					downloadPath = downloadPath + "/";
				}
				try {
					TCPClient.setDownloadPath(downloadPath);
					System.out.println("Pfad gesetzt: " + downloadPath);
				} catch (AssertionError assErr) {
					showAlert("Ungültiger Pfad!", "Der angegebene Pfad darf nicht leer sein.", false);
				}
			}
			
		}
		
		private void showInExplorer() {
			try {
				TCPClient.showInExplorer();
			} catch (Exception e) {
				showAlert("Fehlerhafter Dateipfad!", "Bitte vergewissern Sie sich, dass der angegebene Pfad korrekt ist.", false);
			}
		}

	    
	    @FXML
	    public void handleMouseClick(MouseEvent e){
	    	ImageView source = (ImageView) e.getSource();
	    	
	    	//connectionView
	    	if(source.getId().equals("connect") && connect.isVisible()) {
	    		connectToServer(e);
	    	}
	    	
	    	else if(source.getId().equals("disconnect") && disconnect.isVisible()) {
	    		deleteConnection();
	    	}
	    	    	
	    	//clientConView
	    	else if(source.getId().equals("button_download")) {
	    		// background Task
	    		clickedDownload(e);

	    	}
	    	else if(source.getId().equals("button_refresh")) {
	    		//request file refresh
	    		requestFileListRefresh();
	    	}
	    	else if(source.getId().equals("button_explorer")) {
	    		showInExplorer();
	    	}
	    	
	    	//settingsView
	    	else if(source.getId().equals("button_explorer2")) {
	    		chooseDownloadDirectory(e);
	    	}
	    	else if(source.getId().equals("downloadCancel")) {
	    		cancelDownload();
	    	}
	    }
	    private void minimizeStageOfNode(Node node) {
	        ((Stage)(node).getScene().getWindow()).setIconified(true);
	    }
	    
	    private void establishConnection(){
	    	clearAllGUI();
	    	connectingGUI(true);
	    	String serverIP = textfield_ip.getText();
	    	String serverPort = textfield_port.getText();
	    	
	    	for(int i = 0; i < 5; i ++) {
		    	try {
		    		TCPClient.connectToServer(serverIP, serverPort);
		    		connectionSucGUI();
		    		break;
		    	} catch(SocketTimeoutException e) {
		    		System.out.println("Timeout-Error");
		    		if(i == 4) connectionTimeoutOverGUI();
		    	} catch (Exception e) {
		    		System.out.println("Eingabe-Error");
		    		connectionIOErrorGUI();
		    		break;
				}
	    	}
	    	connectingGUI(false);
	    }
	    
	    private void connectingGUI(boolean b) {
	    	labelTryConnect.setVisible(b);
	    	textfield_ip.setEditable(!b);
	    	textfield_port.setEditable(!b);
	    }
	    private void connectionSucGUI() {
	    	clearAllGUI();
	    	lb_onlineText.setVisible(true);
	    	lb_online.setVisible(true);
	    	disconnect.setVisible(true);
	    	textfield_ip.setEditable(false);
	    	textfield_port.setEditable(false);
	    }
	    private void connectionTimeoutOverGUI() {
	    	clearAllGUI();
	    	labelErrorConnection.setVisible(true);
	    	lb_offline.setVisible(true);
	    	connect.setVisible(true);
	    }
	    private void connectionIOErrorGUI() {
	    	clearAllGUI();
	    	lb_offline.setVisible(true);
	    	labelWrongInput.setVisible(true);
	    	connect.setVisible(true);
	    }
	    private void clearAllGUI() {
	    	lb_onlineText.setVisible(false);
	    	lb_offlineText.setVisible(false);
	    	labelErrorConnection.setVisible(false);
	    	labelTryConnect.setVisible(false);
	    	labelWrongInput.setVisible(false);
	    	
	    	connect.setVisible(false);
	    	disconnect.setVisible(false);
	    	lb_offline.setVisible(false);
	    	lb_online.setVisible(false);
	    	textfield_ip.setEditable(true);
	    	textfield_port.setEditable(true);
	    	
	    }
	    

	    private void receiveDirInformation() {
			TCPClient.receiveDirInformation();	
		}

		private void deleteConnection() {
	    	clearAllGUI();
			lb_offline.setVisible(true);
			connect.setVisible(true);
			lb_offlineText.setVisible(true);
			TCPClient.closeStreams();
			System.out.println("Verbindung getrennt");
	    }
	   
	    	/*
	    	listView.setCellFactory(param -> new ListCell<String>() {
	    		
	            @Override
	            public void updateItem(String name, boolean empty) {
	                super.updateItem(name, empty);
	                if (empty) {
	                    setText(null);
	                    setGraphic(null);
	                } else if (name.equals(fileName[0])) {
	                    setText(name);
	                    setGraphic(imageView);
	                }
	            }
	        });
	        */
	    
	    private void requestFileListRefresh() {
	    	
	    	TCPClient.contactServer("refresh");
	    	TCPClient.receiveDirInformation();
	    	listView.getItems().clear();
			for (FileInformation fi : TCPClient.fileInformation) {
				listView.getItems().add(fi.fileName+ ", " + fi.fileLength + " Bytes");
			}
	    	
	    }
	    
	    public static void showAlert(String header, String content, boolean fatal) {
	    	Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText(header);
				Label contenLabel = new Label(content);
				contenLabel.setWrapText(true);
				DialogPane dialogPane = alert.getDialogPane();
				dialogPane.setContent(contenLabel);
				((Stage)(dialogPane.getScene().getWindow())).initStyle(StageStyle.TRANSPARENT);
				dialogPane.getStylesheets().add(ClientApplication.class.getResource("shared_resources/application.css").toExternalForm());
				alert.showAndWait();
				if(fatal) System.exit(1);
	    	});
	    }
	   
	    // ProgressBar
	    /*
	    @FXML
	    public void handle(ActionEvent event) {
	        startDownloadButton.setDisable(true);
	        progressBar.setProgress(0);
	        //cancelDownloadButton.setDisable(false);
	        
	        progressWorker = createWorker();

	        progressBar.progressProperty().unbind();
	        progressBar.progressProperty().bind(progressWorker.progressProperty());
	    
	    public static void showAlert(String header, String content) {
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setHeaderText(header);
	        Label contenLabel = new Label(content);
	        contenLabel.setWrapText(true);
	        DialogPane dialogPane = alert.getDialogPane();
	        dialogPane.setContent(contenLabel);
	        ((Stage)(dialogPane.getScene().getWindow())).initStyle(StageStyle.TRANSPARENT);
	        dialogPane.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
	        alert.showAndWait();
	    }

	        new Thread(progressWorker).start();
	    }
	    
	    public void initializeProgress() {
	        
	        cancelDownloadButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent event) {
	                startDownloadButton.setDisable(false);
	                cancelDownloadButton.setDisable(true);
	                progressWorker.cancel(true);
	                progressBar.progressProperty().unbind();
	                progressBar.setProgress(0);
	                System.out.println("cancelled.");
	            }
	        });
	    }

	    public Task createWorker() {
	        return new Task() {
	            @Override
	            protected Object call() throws Exception {
	                for (int i = 0; i < 1631385; i++) {
	                    updateProgress(0, 1631385);
	                }
	                return true;
	            }
	        };
	    } 
	       */
	
}
