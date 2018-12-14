package application;
	
import java.io.File;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.FileUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
			Parent root = FXMLLoader.load(Main.class.getResource("FileTransfer.fxml"));
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			
			
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
			
			//creating a new scene
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			primaryStage.setScene(scene);
			primaryStage.show();
			
<<<<<<< HEAD
			
			
			
=======
			//Tests
			//String chosenFile = FileUtils.chooseDownloadDirectory(primaryStage);
>>>>>>> branch 'master' of https://github.com/roninshowdown/eva_ws18.git
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
