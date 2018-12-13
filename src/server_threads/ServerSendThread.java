package server_threads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerSendThread extends Thread {
	
    private String fileName = "";
    private String sharePath = "/home/donald/Schreibtisch";
	private Socket connection = null;
	
	public ServerSendThread(Socket sock, String fileName) {
		this.connection = sock;
		this.fileName = fileName;
	}
	
	public void run() {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
			sendFileToClient(bos);
		} catch (IOException ex) {
			ex.printStackTrace();
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
		System.out.println("Datei: " + fileName + " gesendet");
	}
}
