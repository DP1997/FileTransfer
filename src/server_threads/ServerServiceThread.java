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
    private BufferedOutputStream bos = null;

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
												sendFileToClient(recievedFileName);
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
			bos = new BufferedOutputStream(connection.getOutputStream());
			assert(oos != null && ois != null && bos != null);
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
    
	public void sendFileToClient(String fileName) {
		String filePath = sharePath + fileName;
		// ausgew�hlte Datei des Clients
 	    File myFile = new File(filePath);
 	    
 	    byte[] mybytearray = new byte[(int) myFile.length()];
 	
 	    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile))) {
 	    	assert(bis != null);
	    	bis.read(mybytearray, 0, mybytearray.length);
	        bos.write(mybytearray, 0, mybytearray.length);
	        bos.flush(); 	
 	    } catch (FileNotFoundException e) {
 	        e.printStackTrace();
 	    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	

	}
	
	public void shareDirInformation() {
		try {
			System.out.println("sending directory information");
			fileInformation = FileUtils.getFileInformation(sharePath);
			oos.writeObject(fileInformation.size());
			for(FileInformation fi : fileInformation) {
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

