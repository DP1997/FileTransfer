package client;

import static client.ClientApplicationController.showAlert;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.swing.ProgressMonitor;

import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;
import shared_resources.datatypes.FileInformation;
import shared_resources.datatypes.ProgressStream;

public class ClientApplicationController implements Initializable{

    @FXML
    private ImageView conView_indic, downloadView_indic, settingsView_indic;

    @FXML
    private AnchorPane topbar, downloadView, connectionView, settingsView;

    @FXML
    private ImageView button_download, button_explorer, button_refresh, button_explorer2
    				  ,openConView, openDownloadView, openDownloadViewGrey, openSettingsView, openSettingsViewGrey, shutdown, connectToServer, conEstablished,
    				  noConnection, connectionEstablished, geprueftHaken, disconnect, connect,
    				  downloadSuc, downloadCancel;

    @FXML
    private TextField textfield_port, textfield_ip, textfield_dpath;
    
    @FXML
    private Label labelConnection, labelNoConnection, labelErrorConnection,
    labelTryConnect, labelWrongInput, labelDownload;
    
    @FXML
    private RadioButton radioSettings;
    
    @FXML
    private Button startDownloadButton, cancelDownloadButton;



    @FXML
    private ListView<String> listView;
    
    private ObservableList<String> items;
   
    @FXML
    public ProgressBar progressBar;

    // flag for checking download status
	public static boolean enableDownloading = true;
	
	// background threads for download and connecting
    public static Service<Void> connectThread;
    private Service<Void> downloadThread;
    
    // flag for checking if the download has been canceled
    public static boolean downloadCanceled = false;
    
    @FXML
    private void clickedDownload(MouseEvent e) {
    	downloadThread = new Service<Void>() {
        	@Override
        	protected Task<Void> createTask(){
        		return new Task<Void>() {
        			@Override
        			protected Void call() throws Exception{
        	    		//request file download
        	    		requestFileDownload();
        				return null;
        			}
				};
        	}
    	};

    	downloadThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    		@Override
    		public void handle(WorkerStateEvent event) {
				Platform.runLater(() -> {
					// "Ordner nach Download öffnen" radio button
					if(radioSettings.isSelected()) showInExplorer(); 
				});
				// allow the downloading again, after everything is done
	    		enableDownloading = true;
    		}
    	});
    	downloadThread.restart();
    }
     
    @FXML
    private void connectToServer(MouseEvent e) {
    	connectThread = new Service<Void>() {
        	@Override
        	protected Task<Void> createTask(){
        		return new Task<Void>() {
        			@Override
        			protected Void call() throws Exception{
        				// connecting to server
        				establishConnection();
        				return null;
        			}
        			
				};
        	}
    	};
    	connectThread.start();
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
    	items = FXCollections.observableArrayList();
    	listView.setItems(items);
    	// in order to prevent multiple selections for downloading
    	listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	// binds the progressBar
    	initializeProgressBar();
    	// listener triggers, when the connectionStatus-Property changes
    	TCPClient.connectionStatus.addListener((observable, oldValue, newValue) -> {
    		if(!newValue) {
				if(enableDownloading) {
	    			Platform.runLater(() -> {
		    				visibilityControl(downloadView, downloadView_indic, false);
		    				visibilityControl(settingsView, settingsView_indic, false);
		    				// grey out the icons and disable them
		    				enableIcons(false);
		    				// reset first inventory
		    				resetGUI();
	    			});
				}
    		} else {
    			Platform.runLater(() -> {
    				connectionSucGUI();    				
    				enableIcons(true);
    			});    			
    		}
    	});
	}
	
	// topBar
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
    // method that checks the id of the clicked button/image
    @FXML
    public void handleMouseClick(MouseEvent e){
    	ImageView source = (ImageView) e.getSource();
    	
    	//connectionView
    	if(source.getId().equals("connect") && connect.isVisible()) {
    		connectToServer(e);
    	}
    	
    	else if(source.getId().equals("disconnect") && disconnect.isVisible()) {
    		if(enableDownloading) { 
    		deleteConnection();
    		}
    		else showAlert("Fehler beim Ausloggen", "Bitte brechen Sie den laufenden Download ab, um sich auszuloggen.", false);
    	}
    	    	
    	//downloadView
    	else if(source.getId().equals("button_download")) {
    		if(enableDownloading) {
    		// background Task
    		clickedDownload(e);
    		} else {
				showAlert("Unzul�ssige Aktion", "Während eines laufenden Downloades kann kein weiterer Download gestartet werden.", false);
    		}
    		
    	}
    	else if(source.getId().equals("button_refresh")) {
    		//request file refresh
    		if(enableDownloading) {
    		requestFileListRefresh();
    		} else {
				showAlert("Unzul�ssige Aktion", "Während eines laufenden Downloades kann die Liste nicht aktualisiert werden.", false);
    		}
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
    
    // radio button checks the OS and alerts the user for linux
    @FXML
    public void rdbSettingsAction() {
    	if(!radioSettings.isSelected()) return;
		String os = System.getProperty("os.name").toLowerCase();
		//unter Linux führt das Öffnen im Explorer zu Problemen
		if(!(os.contains("nix") || os.contains("nux"))) return;
		showAlert("Nicht unterstützte Funktion!", "Auf diesem Gerät ist das Öffnen im Explorer nicht unterstützt.", false);
		radioSettings.selectedProperty().set(false);
		radioSettings.setDisable(true);
    }
    
	private void chooseDownloadDirectory(MouseEvent e) {
    	// get Stage
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        
        // chooser allows only folders
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Download-Ordner angeben");
		File selectedDirectory = chooser.showDialog(stage);
		if(selectedDirectory != null) {
			textfield_dpath.setText(selectedDirectory.getAbsolutePath());
			// Der Pfad muss an das Betriebssystem angepasst werden
			// Bei Windows wird der Pfad mit \\ angegeben, bei Linux mit /
			String os = System.getProperty("os.name").toLowerCase();
			String textField = textfield_dpath.getText();
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
				// sets the Path
				TCPClient.setDownloadPath(downloadPath);
				geprueftHaken.setVisible(true);
				System.out.println("Pfad gesetzt: " + downloadPath);
			} catch (AssertionError assErr) {
				showAlert("Ungültiger Pfad!", "Der angegebene Pfad darf nicht leer sein.", false);
			}
		}
		
	}
	
	private void showInExplorer() {
		try {
			TCPClient.showInExplorer();
		} catch(AssertionError assErr) {
			showAlert("Nicht unterstützte Funktion!", "Auf diesem Gerät ist das Öffnen im Explorer nicht unterstützt.", false);
		} catch (Exception e) {
			showAlert("Fehlerhafter Dateipfad!", "Bitte vergewissern Sie sich, dass der angegebene Pfad korrekt ist.", false);
		}
	}
	
	private void cancelDownload(){
		// streams clearen
		downloadCanceled = true;
		downloadCancel.setVisible(false);
	    labelDownload.setVisible(false);
	    
	    // bytesCounter on the InputStream resetted + gui update
		ProgressStream.resetProgressBar();
	    downloadThread.cancel();
	    System.out.println("cancelled.");
	    // refresh socket
	    deleteConnection();
	    establishConnection();
	    downloadCanceled = true;
	}
    
	// minimize
    private void minimizeStageOfNode(Node node) {
        ((Stage)(node).getScene().getWindow()).setIconified(true);
    }
    
    private void establishConnection(){
    	
    	// checks the inet-connection and eventually shows a warning
    	// the method still proceeds, because we don´t always need an inet-connection (e.g. in LAN)
    	TCPClient.checkInternetConnection(); 
    	
    	clearAllGUI();
    	connectingGUI(true);
    	String serverIP = textfield_ip.getText();
    	String serverPort = textfield_port.getText();
    	
    	// tries 5 times
    	for(int i = 0; i < 5; i ++) {
	    	try {
	    		TCPClient.connectToServer(serverIP, serverPort);
	    		connectionSucGUI();
	    		enableIcons(true);
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
    // the following methods are for
    // enable-disable
    // visiblity
    // editability
    //  GUI-Elemets
    
    
    private void enableIcons(boolean b) {
		openDownloadView.setVisible(b);
		openSettingsView.setVisible(b);
		openSettingsViewGrey.setVisible(!b);
		openDownloadViewGrey.setVisible(!b);
    }
    
    private void connectingGUI(boolean b) {
    	labelTryConnect.setVisible(b);
    	textfield_ip.setEditable(!b);
    	textfield_port.setEditable(!b);
    }
    private void connectionSucGUI() {
    	clearAllGUI();
    	labelConnection.setVisible(true);
    	connectionEstablished.setVisible(true);
    	disconnect.setVisible(true);
    	textfield_ip.setEditable(false);
    	textfield_port.setEditable(false);
    	
    }
    public void connectionTimeoutOverGUI() {
    	clearAllGUI();
    	labelErrorConnection.setVisible(true);
    	noConnection.setVisible(true);
    	connect.setVisible(true);
    }
    private void connectionIOErrorGUI() {
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
    private void resetGUI() {
    	clearAllGUI();
    	labelNoConnection.setVisible(true);
    	noConnection.setVisible(true);
    	connect.setVisible(true);
    	
    }
    
    // binding the progressBar
    public void initializeProgressBar() {
    	progressBar.setStyle("-fx-accent: green;");
    	progressBar.progressProperty().bind(ProgressStream.bytesReadProperty());
    }
    
    // formats the bytes for the GUI
    public String formatBytesRead(double bytesRead) {
    	if (bytesRead < 1000) return (int)bytesRead + " Bytes"; 
    	else if(bytesRead >= 1000 && bytesRead < 1000000) {
    		return String.format(Locale.US, "%.2f", bytesRead / (double)1000) + " KB";
    		
    	}
    	else if (bytesRead >= 1000000 && bytesRead < 1000000000) {
    		return String.format(Locale.US, "%.2f", bytesRead / (double)1000000) + " MB";
    	}
    	else if (bytesRead >= 1000000000) {
    		return String.format(Locale.US, "%.2f", bytesRead / (double)1000000000) + " GB";
    	}
    	else return (int)bytesRead + " Bytes";
    }

	private void deleteConnection() {
    	clearAllGUI();
		enableIcons(false);
		noConnection.setVisible(true);
		connect.setVisible(true);
		labelNoConnection.setVisible(true);
		// close Streams
		TCPClient.closeStreams();
		System.out.println("Verbindung getrennt");
    }
   
    private void requestFileDownload() {
	    try {
	    	//read marked list entry
	    	String row = listView.getSelectionModel().getSelectedItem();
	        assert(row != null);
	        if(row != null) {
	        // formats the listEntry
		    String fileName = formatListEntry(row);
	        Paths.get(TCPClient.sharePath);
	        
	        // request with chosen fileName
	        TCPClient.contactServer(fileName);
	    	
	    	// gui for cancel download
			labelDownload.setVisible(false);
			ProgressStream.resetProgressBar();
    		downloadSuc.setVisible(false);
			downloadCancel.setVisible(true);
			
			// download with chosen fileName
		    TCPClient.downloadFileFromServer(fileName);
		    
	    	// gui for finished and progress reset
			    Platform.runLater(()->{
					downloadCancel.setVisible(false);
					downloadSuc.setVisible(true);
					ProgressStream.resetProgressBar();
					labelDownload.setVisible(true);
					labelDownload.setText((formatBytesRead(ProgressStream.fileLength))+ " übertragen");
					if(downloadCanceled) {
						labelDownload.setText("Download abgebrochen");
						downloadSuc.setVisible(false);
						downloadCanceled = false;
					}
			    });
	        }
	        else showAlert("Ungültiger Aufruf!", "Bitte markieren Sie eine Datei aus der Liste, die Sie herunterladen möchten.", false); 
	    } catch (InvalidPathException | NullPointerException ex) {
	        ex.printStackTrace();
        	showAlert("Fehlerhafter Dateipfad!", "Bitte vergewissern Sie sich, dass der von Ihnen angegebene Pfad korrekt ist.", false);
	    } catch (AssertionError assErr) {
        	showAlert("Ungültiger Aufruf!", "Bitte markieren Sie eine Datei aus der Liste, die Sie herunterladen möchten.", false);
	    }   

    }
    
    // formats the listEntry to a string to tell the server the fileName
    private String formatListEntry(String row) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(row);
    	String rRow = sb.reverse().toString();
    	String rfileName = rRow.substring(rRow.indexOf(",") +1, rRow.length());
    	sb = new StringBuilder();
    	sb.append(rfileName);
    	return sb.reverse().toString();
    }

    // refreshing the list filled with the files of the server
    private void requestFileListRefresh() {
    	try {
	    	assert(TCPClient.clientSocket != null && !TCPClient.clientSocket.isClosed());
	    	// contact with refresh-call
	    	TCPClient.contactServer("refresh");
	    	TCPClient.receiveDirInformation();
	    	
	    	// updates on GUI
	    	Platform.runLater(() -> {
		    	listView.getItems().clear();
				for (FileInformation fi : TCPClient.fileInformation) {
					listView.getItems().add(fi.fileName+ ", " + formatBytesRead(Double.parseDouble(fi.fileLength)));
				}
				labelDownload.setText("Liste aktualisiert");
				downloadSuc.setVisible(false);
	    	});
    	} catch (AssertionError assErr) {
    		showAlert("Keine Verbindung!", "Bitte stellen Sie eine Verbindung mit einem Server her, um dessen Dateien anzeigen zu lassen.", false);
    		Platform.runLater(() -> connectionTimeoutOverGUI());
    	}    
    }
    
    // creates an alert with header, content and the option to be a fatal error or not
    // fatal errors close the whole application after clicking
    public static void showAlert(String header, String content, boolean fatal) {
    	Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(header);
			Label contenLabel = new Label(content);
			contenLabel.setWrapText(true);
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.setContent(contenLabel);
			((Stage)(dialogPane.getScene().getWindow())).initStyle(StageStyle.TRANSPARENT);
			dialogPane.getStylesheets().add(ClientApplication.class.getResource("../shared_resources/application.css").toExternalForm());
			alert.showAndWait();
			if(fatal) System.exit(1);
    	});
    }

}
    	

