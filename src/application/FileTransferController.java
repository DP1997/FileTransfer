package application;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ResourceBundle;
import datatypes.FileInformation;
import datatypes.ProgressStream;
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
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    //private ImageView imageView;

   
    @FXML
    public ProgressBar progressBar;
    private Service<Void> downloadThread;
    
    
    @FXML
    private void clickedDownload(MouseEvent e) {
    	//String fileName = listView.getSelectionModel().getSelectedItem();
    	downloadThread = new Service<Void>() {
        	@Override
        	protected Task<Void> createTask(){
        		return new Task<Void>() {
        			@Override
        			protected Void call() throws Exception{
        	    		//request file download
        				
        				labelDownload.setVisible(false);
        				ProgressStream.resetProgress();
        	    		downloadSuc.setVisible(false);
        				downloadCancel.setVisible(true);
        	    		requestFileDownload();
        				return null;
        			}
				};
        	}
    	};
    	downloadThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    		@Override
    		public void handle(WorkerStateEvent event) {
    			downloadCancel.setVisible(false);
    			downloadSuc.setVisible(true);
				ProgressStream.resetProgress();
				labelDownload.setVisible(true);
				labelDownload.setText((int)ProgressStream.fileLength + " Bytes �bertragen");
    		}
    	});
    	downloadThread.restart();
    }
    
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
    	items = FXCollections.observableArrayList();
    	listView.setItems(items);
    	listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	//imageView = new ImageView(new Image("application/images/icons8-geprueft-96.png"));
    	initializeProgressBar();
		//listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//            @Override
//            public ListCell<String> call(ListView<String> p) {
//                return new ListCell<String>() {
//                    @Override
//                    protected void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (item != null) {
//                            setText(item);
//
//                            // decide to add a new styleClass
//                            // getStyleClass().add("costume style");
//                            // decide the new font size
//                            setFont(Font.font(24));
//                        }
//                    }
//                };
//            }
//        });

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
			if(os.contains("nix")) {
				System.out.println("linux erkannt");
				downloadPath = downloadPath + "/";
			}
			try {
				TCPClient.setDownloadPath(downloadPath);
			} catch (AssertionError assErr) {
				showAlert("Ungültiger Pfad!", "Der angegebene Pfad darf nicht leer sein.");
			}
			geprueftHaken.setVisible(true);
			System.out.println("Pfad gesetzt: " + downloadPath);
		}
		
	}
	
	private void showInExplorer() {
		try {
			TCPClient.showInExplorer();
		} catch (Exception e) {
			showAlert("Fehlerhafter Dateipfad!", "Bitte vergewissern Sie sich, dass der angegebene Pfad korrekt ist.");
		}
	}
	private void cancelDownload(){
		// streams clearen
		downloadCancel.setVisible(false);
		ProgressStream.resetProgress();
	    downloadThread.cancel();
	    System.out.println("cancelled.");		
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
    	for(int i = 0; i < 5; i ++) {
	    	try {
	    		String serverIP = textfield_ip.getText();
	    		String serverPort = textfield_port.getText();
	    		TCPClient.connectToServer(serverIP, serverPort);
	    		connectionSucGUI();
	    		break;
	    	} catch(SocketTimeoutException e) {
	    		System.out.println("Timeout-Error");
	    		if(i == 4) connectionTimeoutOverGUI();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		System.out.println("Eingabe-Error");
	    		connectionIOErrorGUI();
	    		break;
			}
    	}    	
    }
    
    private void connectGUI() {
    	clearAllGUI();
    	labelTryConnect.setVisible(true);
    	noConnection.setVisible(false);
    	connect.setVisible(false);
    	textfield_ip.setEditable(false);
    	textfield_port.setEditable(false);
    }
    private void connectionSucGUI() {
    	clearAllGUI();
    	labelTryConnect.setVisible(false);
    	labelConnection.setVisible(true);
    	connectionEstablished.setVisible(true);
    	disconnect.setVisible(true);
    	textfield_ip.setEditable(false);
    	textfield_port.setEditable(false);
    }
    private void connectionTimeoutOverGUI() {
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
    
    public void initializeProgressBar() {
    	progressBar.setStyle("-fx-accent: green;");
    	progressBar.progressProperty().bind(ProgressStream.bytesReadProperty());
    }
    
/*  
	public static void startProgressTask() {
		final double EPSILON = 0.0000005;
	final Task<Void> task = new Task<Void>() {
        final int N_ITERATIONS = 100;

        @Override
        protected Void call() throws Exception {
            for (int i = 0; i < N_ITERATIONS; i++) {
                updateProgress(i + 1, N_ITERATIONS);
                // sleep is used to simulate doing some work which takes some time....
                Thread.sleep(10);
            }

            return null;
        }
    };

    progressBar.progressProperty().bind(
            task.progressProperty()
    );
    // color the bar green when the work is complete.
    progressBar.progressProperty().addListener(observable -> {
        if (progressBar.getProgress() >= 1 - EPSILON) {
            progressBar.setStyle("-fx-accent: forestgreen;");
        }
    });

    final Thread thread = new Thread(task, "task-thread");
    thread.setDaemon(true);
    thread.start();
	}
    */

    private void receiveDirInformation() {
		TCPClient.receiveDirInformation();	
	}

	private void deleteConnection() {
    	clearAllGUI();
		noConnection.setVisible(true);
		connect.setVisible(true);
		labelNoConnection.setVisible(true);
		TCPClient.closeStreams();
		System.out.println("Verbindung getrennt");
    }
   
    private void requestFileDownload() {
    	try {
	    	//read marked list entry
	    	String row = listView.getSelectionModel().getSelectedItem();
	    	assert(row != null);
	    	//aufpassen bei mehreren , im String
	    	//von rechts lesen
	    	StringBuilder sb = new StringBuilder();
	    	sb.append(row);
	    	String rRow = sb.reverse().toString();
	    	String rfileName = rRow.substring(rRow.indexOf(",") +1, rRow.length());
	    	sb = new StringBuilder();
	    	sb.append(rfileName);
	    	String fileName = sb.reverse().toString();	    	
	    	System.out.println(fileName);
	    	assert(fileName != null);
	    	TCPClient.contactServer(fileName);
	    	TCPClient.downloadFileFromServer(fileName);
    	} catch(AssertionError assErr) {
    		showAlert("Ungültige Auswahl!", "Bitte wählen Sie einen Listeneintrag aus, um eine Datei herunterzuladen.");
    	}
    	
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
    	

