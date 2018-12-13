package utils;

import java.io.File;
import java.util.ArrayList;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FileUtils {
	
	public static String[] getFileNames(String path) {
		
		File folder = new File(path);
		File[] files = folder.listFiles();
		
		String[] fileNames = new String[files.length];
		
		for (int i = 0; i < files.length; i++) {
		  if (files[i].isFile()) {
		    fileNames[i] = files[i].getName();
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