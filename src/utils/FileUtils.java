package utils;

import java.io.File;
import java.util.ArrayList;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FileUtils {
	
	public static ArrayList<String> getFileNames(String path) {
		
		ArrayList<String> fileNames = new ArrayList<String>();
		
		File folder = new File(path);
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
		  if (files[i].isFile()) {
		    fileNames.add(files[i].getName());
		  } 
		}
		return fileNames;
	}
	    /*
		for (int i = 0; i < files.length; i++) {
			  if (files[i].isFile()) {
			    System.out.println("File " + files[i].getName() + " " + files[i].length());
			  } else if (files[i].isDirectory()) {
			    System.out.println("Directory " + files[i].getName());
			  }
			}	
		}
	*/
	public static String chooseDownloadDirectory(Stage stage) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Ordner zur Synchronisation angeben");
		File selectedDirectory = chooser.showDialog(stage);
		if(selectedDirectory != null) {
			return selectedDirectory.getAbsolutePath();
		}
		return null;
	}
}