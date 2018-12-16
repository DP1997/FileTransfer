package utils;

import java.io.File;
import java.util.ArrayList;

import datatypes.FileInformation;

public class FileUtils {
	
	
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
	
/*
	public static ArrayList<String> getFileNames(String path) {
		ArrayList<Long> fileLengths = new ArrayList<Long>();
		
		File folder = new File(path);
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
		  if (files[i].isFile()) {
			  fileLengths.add(files[i].length());
		  } 
		}
		return fileLengths;
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
	
	
	public static String getChosenFileName(String sharePath) {
		String chosenFile = sharePath + "/xd2.txt";
		return chosenFile;
	}
}