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
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

import datatypes.FileInformation;
import utils.FileUtils;

public class ServerServiceThread extends Thread{
	
	private Socket connection = null;
	
    private String sharePath = "/home/donald/Schreibtisch";
    private String fileName;
    
    private static ArrayList<String> fileNames = null;
    private static ArrayList<Long> fileLengths = null;
    
    private ObjectInputStream ois 		 = null;
    private ObjectOutputStream oos        = null;

	
	public ServerServiceThread(Socket sock) {
		super("FileTransferThread");
		this.connection = sock;
		System.out.println("connection with client successfully established");
		initializeStreams();
	}
	
	public void run() {
			while(!connection.isClosed()) {
				try {
					if((fileName = (String) ois.readObject()) != null) {
						switch(fileName) {
						case "refresh": shareDirInformation();
										break;
						default: 		if(fileNames != null) if(fileNames.contains(fileName)) sendFileToClient();
										System.out.println("Datei: " + fileName + " gesendet");								
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					System.err.println("error occured while reading from ObjectInputStream");
					e.printStackTrace();
					System.exit(1);
				}
			}
	}
	
    //versucht die alle benötigten Streams zu initialisieren
    private  void initializeStreams() {
    	
    	try {
    		assert(connection != null && !connection.isClosed());
    	} catch (AssertionError ae) {
    		System.err.println("clientSocket is null or closed");
    	}

    	try {
			oos = new ObjectOutputStream(connection.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(connection.getInputStream());
			assert(oos != null && ois != null);
			System.out.println("ObjectStreams have been successfully initialized");
		} catch (IOException e) {
			System.err.println("ObjectStreams could not be initialized");
			e.printStackTrace();
			System.exit(1);
		} catch (AssertionError ae) {
			System.err.println("ObjectStreams are null");
			ae.printStackTrace();
			System.exit(1);
		}
    }
    
	public void sendFileToClient() throws IOException {
		String filePath = sharePath + "/" + fileName;
		// ausgew�hlte Datei des Clients
	    File myFile = new File(filePath);
	    
	    byte[] mybytearray = new byte[(int) myFile.length()];
	
	    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile))) {
	    	BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
	    	if (bis != null && bos != null) {
		    	bis.read(mybytearray, 0, mybytearray.length);
		        bos.write(mybytearray, 0, mybytearray.length);
		        bos.flush();
	    	}
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }	
	}
	
	public void shareDirInformation() {
		try {
			System.out.println("sending directory information");
			fileNames = FileUtils.getFileNames(sharePath);
			fileLengths = FileUtils.getFileLengths(sharePath);
			oos.writeObject(fileNames);
			oos.writeObject(fileLengths);
			System.out.println("following directory information transmitted:");
			for (int i = 0; i < fileNames.size(); i++) {
				System.out.println(fileNames.get(i) + " " + fileLengths.get(i) + " Bytes");
			}
        } catch (IOException e) {
			System.err.println("error occured while writing in ObjectOutputStream");
			e.printStackTrace();
			System.exit(1);
		}
	}
}

