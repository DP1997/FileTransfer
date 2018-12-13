package application;
	
import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.FileUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private double xOffset;
	private double yOffset;
	@Override
	public void start(Stage primaryStage) {
		try {
			//loading the fxml file
			Parent root = new FXMLLoader.load(getClass().getResource("FileTransfer.fxml"));
			loader.setOn
			
			AnchorPane pane = loader.load();
			
			FileTransferController controller = loader.getController();
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			/*
			//setting height and width of the window
			primaryStage.setMinHeight(400);
			primaryStage.setMinWidth(400);
			//setting title
			primaryStage.setTitle("FileTransfer");
			//window should not be resizable
			primaryStage.setResizable(false);
			*/
			//creating a new scene
			Scene scene = new Scene(pane);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			//Tests
			String chosenFile = FileUtils.chooseDownloadDirectory(primaryStage);
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
