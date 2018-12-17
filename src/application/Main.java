package application;
	
import java.io.File;

import datatypes.ProgressUpdate2;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import transfer.TCPClient;
import utils.FileUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


public class Main extends Application {
	
	private double xOffset = 0;
	private double yOffset = 0;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			//loading the fxml file
			FXMLLoader fxmlLoader = new FXMLLoader();
			Parent root = FXMLLoader.load(Main.class.getResource("FileTransfer.fxml"));
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			FileTransferController ftc = (FileTransferController) fxmlLoader.getController();
			
			root.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					xOffset = event.getSceneX();
					yOffset = event.getSceneY();
				}
			});
			root.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					primaryStage.setX(event.getScreenX() - xOffset);
					primaryStage.setY(event.getScreenY() - yOffset);
				}
			
			});
			
			
			// Progress initialize
			/*
			//setting height and width of the window
			primaryStage.setMinHeight(638.0);
			primaryStage.setMinWidth(600.0);
			//setting title
			primaryStage.setTitle("FileTransfer");
			//window should not be resizable
			primaryStage.setResizable(false);
			
			AnchorPane pane = loader.load();
			*/
			/*
	    	Task task = new Task<Void>() {
	    	    @Override public Void call() throws InterruptedException {
	    	    	System.out.println("Task alive");
	    	        for (int i=1; i<=1000000; i++) {
	    	        	Thread.sleep(10);
	    	        	//pb.setProgress(i / 1000000);
	    	            updateProgress(i, 1000000);
	    	        }
	    	        return null;
	    	    }
	    	};
	    	ftc.progressBar.progressProperty().bind(task.progressProperty());
	    	new Thread(task).start();
	    	*/
			//creating a new scene
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
