package server;

import static shared_resources.utils.MarshallingUtils.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import javafx.application.Platform;
import shared_resources.datatypes.FileInformation;
import shared_resources.utils.FileUtils;
import static server.TCPServer.*;
import static server.ServerApplicationController.clients;
import static server.ServerApplicationController.sharedDir;

public class ServerServiceThread extends Thread{
	
	public Socket connection = null;
	private String recievedFileName;
   
    private BufferedOutputStream bos = null;
    private BufferedInputStream bis = null;

    private static ArrayList<FileInformation> fileInformation = null;
    private SocketAddress sockAddr = null;
	
	public ServerServiceThread(Socket sock) {
		super("FileTransferThread");
		this.connection = sock;
		sockAddr = connection.getRemoteSocketAddress();
		Platform.runLater(() -> clients.add(sockAddr));
		System.out.println("connection with client successfully established");
		initializeStreams();
	}
	
	public void run() {
		//length of data in bytes
		byte[] lod_byte = new byte[4];
		//length of data as Integer
		int lengthOfData;
		//data in bytes
		byte[] data;
			while(!connection.isClosed() && !isInterrupted()) {
				try {
		            //retrieve header information
		            checkConnection(bis.read(lod_byte));
		            lengthOfData = unmarshalling(lod_byte);
		            System.out.println("incoming msg expected to be "+lengthOfData+" bytes long");
		            //retrieve data
		            data = new byte[lengthOfData];
		            checkConnection(bis.read(data));
					recievedFileName = new String(data);
					System.out.println("action: "+recievedFileName+" recieved");
					switch(recievedFileName) {
					case "refresh": shareDirInformation();
									break;
					default: 		if(fileInformation != null) {
										if(contains(recievedFileName)) {
											System.out.println("file transfer requested for "+ recievedFileName);
											sendFileToClient(recievedFileName);
										}
									}
					}
	
				} catch (IOException e) {
					System.err.println("error occured while reading from stream - terminating connection");
					e.printStackTrace();
					closeStreams();	
				}
			}
			System.out.println("		ServerServiceThread "+this.getId()+" exiting...");   
			closeStreams();
	}
	
	private boolean contains(String fileName) {
		for(FileInformation fi : fileInformation) {
			if (fi.fileName.equals(fileName)) return true;
		}
		return false;
	}
	
	//checks whether the connection is still live
	//if not, the connection is properly terminated
	private void checkConnection(int i) throws IOException {
		if(i == -1) throw new IOException();
		return;
	}
	
    //allocate resources needed for the connection
    private  void initializeStreams() {
    	
    	//check for an active socket
    	try {
    		assert(connection != null && !connection.isClosed());
    	} catch (AssertionError ae) {
    		System.err.println("socket is null or closed - clean up connection");
    		//terminate connection
    		closeStreams();
    	}

    	//allocate streams
    	try {
			bos = new BufferedOutputStream(connection.getOutputStream());
			bis = new BufferedInputStream(connection.getInputStream());
			assert(bis != null && bos != null);
			System.out.println("streams have been successfully initialized");
		} catch (IOException | AssertionError e) {
			System.err.println("steams could not be initialized - terminating connection");
			e.printStackTrace();
			//terminate connection
			closeStreams();
		} 
    }
    
    //release all allocated resources
    private void closeStreams() {
    	System.out.println("closing streams & connection ...");
    	//release BufferedOutputStream
    	try {
        	assert(bos != null);
    		bos.close();
    		bos = null;
    		System.out.println("bos successfully released");
    	} catch (AssertionError ae) {
    		System.err.println("bos is already released");
    	} catch (IOException e) {
    		System.err.println("bos could not be closed - setting bos null");
    		bos = null;
		}
    	
    	//release BufferedInputStream
    	try {
        	assert(bis != null);
    		bis.close();
    		bis = null;
    		System.out.println("bis successfully released");
    	} catch (AssertionError ae) {
    		System.err.println("bis is already released");
    	} catch (IOException e) {
    		System.err.println("bos could not be closed - setting bis null");
    		bis = null;
		}
    	
    	//release socket
    	try {
    		assert(connection != null);
    		connection.close();
    		System.out.println("socket successfully closed - new connection can be established");
    	} catch (AssertionError ae) {
    		System.err.println("socket is already null");
    	} catch (IOException ioe) {
    		System.err.println("socket could not be closed - setting socket null");
    		ioe.printStackTrace();
    		connection = null;
    	}
    	Platform.runLater(() -> clients.remove(sockAddr));
    }
    
    //send client information about the files he can request a download for
	public void shareDirInformation() {
		try {
			System.out.println("sending directory information...");
			//read information about shared files
			fileInformation = FileUtils.getFileInformation(sharedDir);
			byte[] header = marshalling(fileInformation.size());
			//tell client how many files the server will be sending
			//with each file containing 2 additional send operations
			bos.write(header);
			System.out.println("sending "+unmarshalling(header)+" fileInformation-Sets...");
			for(FileInformation fi : fileInformation) {
				//tell client how long the message will be (in bytes)
				bos.write(marshalling(fi.fileName.getBytes().length));
				//send the message
				bos.write(fi.fileName.getBytes());
				bos.flush();
				System.out.print("sending "+fi.fileName+" in "+fi.fileName.getBytes().length+" bytes - ");
				//do the same for the fileLength
				bos.write(marshalling(fi.fileLength.getBytes().length));
				bos.write(fi.fileLength.getBytes());
				bos.flush();
				System.out.println("sending "+fi.fileLength+" in "+fi.fileLength.getBytes().length+" bytes");
			}
			System.out.println("directory information sent");

        } catch (IOException e) {
			System.err.println("error occured while writing in streams - terminating connection");
			e.printStackTrace();
			closeStreams();
		}
	}
	
	
	public void sendFileToClient(String fileName) {
		System.out.println("transmitting file "+fileName+"...");
		//build path to file
		String filePath = sharedDir + fileName;
		System.out.println(filePath);
 	    File myFile = new File(filePath);
 	    
 	    byte[] data = new byte[(int) myFile.length()];
 	    byte[] header = marshalling((int)myFile.length());
 	    
 	    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile))) {
 	    	assert(bis != null);
 	    	//read file from disk into the data byte-array 
	    	bis.read(data);
	    	//tell client how long our data will be (in bytes)
	    	bos.write(header);
	    	//send data
	        bos.write(data);
	        bos.flush();
			System.out.println("file "+recievedFileName+" transmitted");
 	    } catch (FileNotFoundException e) {
 	    	System.err.println("specified file could not be found");
 	        e.printStackTrace();
 	        closeStreams();
 	    } catch (IOException ioe) {
			System.err.println("error occured while writing in streams - terminating connection");
			ioe.printStackTrace();
			closeStreams();
		}	

	}

}

