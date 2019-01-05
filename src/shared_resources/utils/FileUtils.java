package shared_resources.utils;

import java.io.File;
import java.util.ArrayList;

import shared_resources.datatypes.FileInformation;

public class FileUtils {
	
	//read files from the specified path (sharedDir from server)
	public static ArrayList<FileInformation> getFileInformation(String path) {
		ArrayList<FileInformation> fileInformation = new ArrayList<FileInformation>();
				
		File folder = new File(path);
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
		  if (files[i].isFile()) {
			  fileInformation.add(new FileInformation(files[i].getName(), Long.toString(files[i].length())));
		  } 
		}
		return fileInformation;
	}
}