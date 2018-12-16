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
    private String recievedFileName;
    
    private ObjectInputStream ois 		 = null;
    private ObjectOutputStream oos        = null;

    private static ArrayList<FileInformation> fileInformation = null;
	
	public ServerServiceThread(Socket sock) {
		super("FileTransferThread");
		this.connection = sock;
		System.out.println("connection with client successfully established");
		initializeStreams();
	}
	
	public void run() {
			while(!connection.isClosed()) {
				try {
					if((recievedFileName = (String) ois.readObject()) != null) {
						switch(recievedFileName) {
						case "refresh": shareDirInformation();
										break;
						default: 		if(fileInformation != null) {
											if(contains(recievedFileName)) {
												sendFileToClient();
												System.out.println("Datei: "+recievedFileName+" gesendet");
											}
										}
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					System.err.println("error occured while reading from ObjectInputStream");
					e.printStackTrace();
					System.exit(1);
				}
			}
	}
	
	private boolean contains(String fileName) {
		for(FileInformation fi : fileInformation) {
			if (fi.fileName.equals(fileName)) return true;
		}
		return false;
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
    
	public void sendFileToClient() {
		String filePath = sharePath + "/" + recievedFileName;
		System.out.println("transmitting file at "+filePath+" to client");
		// ausgew�hlte Datei des Clients
		assert(filePath != null);
	    File myFile = new File(filePath);
	    try {
			oos.writeObject(myFile);
			System.out.println("file transmitted");
		} catch (AssertionError e) {
			System.err.println("filePath is null");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("error occured while writing in ObjectOutputStream");
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	public void shareDirInformation() {
		try {
			System.out.println("sending directory information");
			ArrayList<FileInformation> fiArray = FileUtils.getFileInformation(sharePath);
			oos.writeObject(fiArray.size());
			for(FileInformation fi : fiArray) {
				oos.writeObject(fi.fileName);
				oos.writeObject(fi.fileLength);
				System.out.println(fi.fileName + ", " + fi.fileLength + " Bytes");
			}

        } catch (IOException e) {
			System.err.println("error occured while writing in ObjectOutputStream");
			e.printStackTrace();
			System.exit(1);
		}
	}
}

