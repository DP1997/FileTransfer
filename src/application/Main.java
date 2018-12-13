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


public class Main extends Application {
	
	private double xOffset;
	private double yOffset;
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
					primaryStage.setX(event.getSceneX() - xOffset);
					primaryStage.setY(event.getSceneY() - yOffset);
				}
			});
			
			//creating a new scene
			Scene scene = new Scene(root);
			scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
			scene.fillProperty();
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
