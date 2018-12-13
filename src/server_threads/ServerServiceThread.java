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
	
    public static String sharePath = "/home/donald/Schreibtisch";
	
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
				String fileName = "";
				fileName = br.readLine();
				// sende Datei zum Client
				new ServerSendThread(connection, fileName).start();
		        connection.close();
			}
    	} catch (IOException e) {
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

