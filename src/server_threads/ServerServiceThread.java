package server_threads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

import datatypes.FileInformation;
import utils.FileUtils;

public class ServerServiceThread extends Thread{
	
	private Socket connection = null;
	
    private String sharePath = "/home/donald/Schreibtisch";
    private String fileName = "";

	
	public ServerServiceThread(Socket sock) {
		super("FileTransferThread");
		this.connection = sock; 	
	}
	
	public void run() {
		shareDirInformation();
		//...
		try (BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream())))
		{
			
			if (bos != null && br != null) {
				System.out.println("Connection established");
				
				// fileName wird gelesen
				fileName = br.readLine();
				// sende Datei zum Client
				sendFileToClient(bos);
				//
				System.out.println("Datei: " + fileName + " gesendet");
				
			}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}
	
	public void sendFileToClient(BufferedOutputStream bos) throws IOException {
		String filePath = sharePath + "/" + fileName;
		// ausgew�hlte Datei des Clients
	    File myFile = new File(filePath);
	    
	    byte[] mybytearray = new byte[(int) myFile.length()];
	
	    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile))) {
	    	bis.read(mybytearray, 0, mybytearray.length);
	        bos.write(mybytearray, 0, mybytearray.length);
	        bos.flush();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }	
	}
	
	public void shareDirInformation() {
		
		try{
			ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
			if (oos != null) {
				ArrayList<String> fileNames = FileUtils.getFileNames(sharePath);
				ArrayList<Long> fileLengths = FileUtils.getFileLengths(sharePath);
				oos.writeObject(fileNames);
				oos.writeObject(fileLengths);
				System.out.println("Server-Ordner:");
				for (int i = 0; i < fileNames.size(); i++) {
					System.out.println(fileNames.get(i) + " " + fileLengths.get(i) + " Bytes");
				}
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Share-Ordner mitgeteilt.");
	}
}

