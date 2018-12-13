package thread;

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

public class FileTransferThread extends Thread{
	
	private Socket connection = null;
	
    private String sharePath = "C:\\Users\\Mirco\\Desktop\\testordner";
    private String fileName = "";

	
	public FileTransferThread(Socket sock) {
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
				
				while((fileName = br.readLine()) != null) {
				// sende Datei zum Client
				sendFileToClient(bos);
				System.out.println("Datei: " + fileName + " gesendet");
				}
		        connection.close();
			}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}
	
	public void sendFileToClient(BufferedOutputStream bos) throws IOException {
		fileName = FileUtils.getChosenFileName(sharePath);
		// ausgew�hlte Datei des Clients
	    File myFile = new File(fileName);
	    
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
					System.out.print(fileNames.get(i) + " " + fileLengths.get(i) + " Bytes");
				}
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Share-Ordner mitgeteilt.");
	}
}

