package server;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.TCPClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
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
    private ImageView lb_offline, lb_online, bt_hostServer, bt_turnServerOff, bt_openExplorer;

    //clientConnectionView
    @FXML
    private Label lb_clientKicked;
    @FXML
    private ListView<SocketAddress> lv_clients;
    @FXML
    private ImageView bt_kickClient;
    
    //informationView
    @FXML
    private Label lb_serverIP, lb_serverPort, lb_sharePath;
    @FXML
    private ImageView imgV_infoChecked;


    static ObservableList<SocketAddress> clients;
    public static String sharedDir = null;
    private TCPServer server = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//populate the listview
		clients = FXCollections.observableArrayList();
    	lv_clients.setItems(clients);
    	lv_clients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	//handle topbar icon clicks	
    @FXML
    public void topBarIconClicked(MouseEvent e) {
    	ImageView source = (ImageView) e.getSource();
    	
    	if(source.getId().equals("bt_openConView")) {
    		if(connectionView.isVisible()) {
    			visibilityControl(connectionView, imgV_conViewIndic, false);
    		}
    		else {
    			visibilityControl(connectionView, imgV_conViewIndic, true);
    			visibilityControl(clientConView, imgV_clientConViewIndic, false);
    			visibilityControl(infoView, imgV_infoViewIndic, false);
    		}
    	}
    	else if(source.getId().equals("bt_openClientCon")) {
      		if(clientConView.isVisible()) {
    			visibilityControl(clientConView, imgV_clientConViewIndic, false);
    		}
    		else {
    			visibilityControl(clientConView, imgV_clientConViewIndic, true);
    			visibilityControl(connectionView, imgV_conViewIndic, false);
    			visibilityControl(infoView, imgV_infoViewIndic, false);
    		}
    	}
    	else if(source.getId().equals("bt_openInfoView")) {
      		if(infoView.isVisible()) {
    			visibilityControl(infoView, imgV_infoViewIndic, false);
    		}
    		else {
    			visibilityControl(infoView, imgV_infoViewIndic, true);
    			visibilityControl(clientConView, imgV_clientConViewIndic, false);
    			visibilityControl(connectionView, imgV_conViewIndic, false);
    		}
    	}
    	else if(source.getId().equals("bt_shutdown")) {
    		System.exit(0);
    	}
    	else if(source.getId().equals("bt_minimize")) {
    		minimizeStageOfNode((Node) e.getSource());
    	}
    	
    }
	
    //help control visibility of views and their respective indicators
    private void visibilityControl(AnchorPane ap, ImageView iv_indic, boolean visible) {
    	if(visible) {
    		ap.setVisible(true);
    		iv_indic.setVisible(true);
    	} else {
    		ap.setVisible(false);
    		iv_indic.setVisible(false);
    	}
    }

	//minimize the application	
    private void minimizeStageOfNode(Node node) {
        ((Stage)(node).getScene().getWindow()).setIconified(true);
    }
	
    //handle mouse events from the connectionView
    @FXML
    public void handleMouseClick(MouseEvent e) {
    	//get source of the event -> node
    	ImageView source = (ImageView) e.getSource();
    	//get it's name
    	String sourceID = source.getId();
    	//don't handle events from invisible ImageViews
    	if(!source.isVisible()) return;
    	switch(sourceID) {
    	
    	case "bt_hostServer": 	 hostServer();
    						  	 break;
    	
    	case "bt_turnServerOff": shutDownServer();
    							 break;
    							 
    	case "bt_openExplorer":  chooseDownloadDirectory(source);
    							 break;
    	default: break;
    	}

    }

	private void hostServer() {
		Path sharedDir;
		Integer port;
		
		//check for internet connection
        try { 
            URL url = new URL("https://www.google.de/"); 
            URLConnection connection = url.openConnection(); 
            connection.connect();
            new checkInetCon(true).start();
            
        } 
        catch (Exception e) { 
            showAlert("Warnung!", "Sie haben keine Internetverbindung. Der Server ist nur lokal erreichbar.", false);
            new checkInetCon(false).start();
        } 
		
        //check for a valid path
		try {
			sharedDir = Paths.get(ServerApplicationController.sharedDir);
		} catch (InvalidPathException | NullPointerException ex) {
			ex.printStackTrace();
			showAlert("Ung�ltiger Pfad", "Bitte geben Sie den Pfad zu einem Ordner an.", false);
			return;
		}
		
		//check for a valid port
		try {
			port = Integer.parseInt(tf_port.getText());
			assert(1024 <= port  && port <= 65535);
		} catch (NumberFormatException | NullPointerException | AssertionError ex) {
				ex.printStackTrace();
				showAlert("Ung�ltiger Port", "Bitte geben Sie einen Port zwischen 1024 und 65535 an.", false);
				return;
		}

		//host server with given path and port
		this.server = new TCPServer(sharedDir.toString(), port);
		this.server.start();
		//update gui
		hostSucGUI();
		setInfoView();
	}
		
	// turn the server off
    private void shutDownServer() {
    	if(this.server == null) return;
    	System.out.println("shutting down...");
		this.server.shutDown();
		this.server.interrupt();
		try {
			this.server.welcomeSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			this.server.welcomeSocket = null;
		}
		while(this.server.isAlive()) {
			try {
				this.server.join();
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		this.server = null;
		System.out.println("TCPServer has successfully shut down");
		    defaultConViewGUI();
		    setInfoView();
   }
	    
	private void chooseDownloadDirectory(Node source) {
		try {
			//only possible if the attached server offline
			assert(this.server == null);
			if(this.server == null) {
	    	//get Stage
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
				// windows
				if(os.contains("win")) {
					System.out.println("windows erkannt");
					sharedDir = textField.replace("\\","\\\\") + "\\\\";
				}
				else if(os.contains("nix") || os.contains("nux")) {
					System.out.println("linux erkannt");
					sharedDir = textField + "/";
				}
				System.out.println("choosen directory: "+sharedDir);
			}
			}
			else showAlert("Unzul�ssige Aktion", "W�hrend dem laufenden Betrieb eines Servers, ist es nicht m�glich dessen Pfad zu �ndern", false);
		} catch (AssertionError e) {
			showAlert("Unzul�ssige Aktion", "W�hrend dem laufenden Betrieb eines Servers, ist es nicht m�glich dessen Pfad zu �ndern", false);
		}	
	}
		
	//populate the infoView with the appropriate information
	public void setInfoView() {
		if (this.server != null) {
			lb_serverPort.setText(tf_port.getText());
			lb_sharePath.setText(tf_sharePath.getText());
			try {
				lb_serverIP.setText(Inet4Address.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				lb_serverIP.setText("UNKNOWN");
				e.printStackTrace();
			}
		} else {
			lb_serverPort.setText("---");
			lb_serverIP.setText("---");
			lb_sharePath.setText("---");
		}
	}
		
	//methods for gui-control
	private void hostSucGUI() {
		clearAllGUI();
		lb_onlineText.setVisible(true);
		lb_onlineText.toFront();
		lb_online.setVisible(true);
		lb_online.toFront();
		bt_turnServerOff.setVisible(true);
		bt_turnServerOff.toFront();
		tf_port.setEditable(false);
	}
	private void defaultConViewGUI() {
		clearAllGUI();
		lb_offline.setVisible(true);
		lb_offlineText.setVisible(true);
		bt_hostServer.setVisible(true);
		lb_offline.toFront();
		lb_offlineText.toFront();
		bt_hostServer.toFront();

	}
	private void clearAllGUI() {
		lb_onlineText.setVisible(false);
		lb_offlineText.setVisible(false);
		lb_offline.setVisible(false);
		lb_online.setVisible(false);
		bt_hostServer.setVisible(false);
		bt_turnServerOff.setVisible(false);
		tf_port.setEditable(true);
	}
		
	//thread that checks for internet access every 3 seconds
    private class checkInetCon extends Thread {
    	private boolean connection;
    	
    	public checkInetCon(boolean connection) { this.connection = connection; }
    	
		@Override
		public void run() {
			boolean isConnected = connection;
			URL url;
			URLConnection urlconnection;
			while(true) {
		        try {
					//überprüfe Internetverbindung
			        try { 
			        	url = new URL("https://www.google.de/"); 
			            urlconnection = url.openConnection(); 
			            urlconnection.connect();
			            isConnected = true;
			        } 
			        catch (Exception e) { 
			             if(isConnected) showAlert("Warnung!", "Sie haben keine Internetverbindung. Der Server ist nur lokal erreichbar.", false);
			             isConnected = false;
			        }
					sleep(3000);
				} catch (InterruptedException e) {

				}
			}
		}
     }
	    
    //custom made alert that displays the header and content information
    public static void showAlert(String header, String content, boolean fatal) {
    	Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(header);
			Label contenLabel = new Label(content);
			contenLabel.setWrapText(true);
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.setContent(contenLabel);
			
			((Stage)(dialogPane.getScene().getWindow())).initStyle(StageStyle.TRANSPARENT);
			dialogPane.getStylesheets().add(ServerApplicationController.class.getResource("../shared_resources/application.css").toExternalForm());
			alert.showAndWait();
			if(fatal) System.exit(1);
    	});
    }	
}
