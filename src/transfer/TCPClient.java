package transfer;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import application.FileTransferController;
import datatypes.FileInformation;
import datatypes.ProgressStream;
import static utils.MarshallingUtils.*;

public class TCPClient {
    
    public static String sharePath;
    private static Socket clientSocket = null;
    
    private static BufferedOutputStream bos = null;
    private static BufferedInputStream bis = null;
    private static ProgressStream ps = null;
    
    public static ArrayList<FileInformation> fileInformation = null;

    
    public static void connectToServer(String serverIP, String serverPort) throws Exception {
    		
    		SocketAddress sockaddr = new InetSocketAddress(serverIP, Integer.valueOf(serverPort));
    		
    		clientSocket = new Socket();
    		// Connect with 2 s timeout
    		clientSocket.connect(sockaddr, 1000);
    		System.out.println("connection with server successfully established");
    		initializeStreams();
    }

    public static void setDownloadPath(String sharePath) throws AssertionError {	
    	assert(TCPClient.sharePath != "BITTE PFAD ANGEBEN" && TCPClient.sharePath != null); 
        TCPClient.sharePath = sharePath; 	
    }
    
    public static void showInExplorer() throws Exception {
    	Desktop.getDesktop().open(new File(sharePath));
    }

    
    //allocate resources needed for the connection
    public static  void initializeStreams() {
    	
    	//check for an active socket
    	try {
    		assert(clientSocket != null && !clientSocket.isClosed());
    	} catch (AssertionError ae) {
    		System.err.println("socket is null or closed - terminate connection");
    		//terminate connection
    		closeStreams();
    	}

    	//allocate streams
    	try {
			bos = new BufferedOutputStream(clientSocket.getOutputStream());
			bis = new BufferedInputStream(new ProgressStream(clientSocket.getInputStream()));
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
    public static void closeStreams() {
    	
    	//release BufferedOutputStream
    	try {
        	assert(bos != null);
    		bos.close();
    		bos = null;
    		System.out.println("bos successfully released");
    	} catch (AssertionError ae) {
    		System.err.println("bos is already released");
    		ae.printStackTrace();
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
    		ae.printStackTrace();
    	} catch (IOException e) {
    		System.err.println("bos could not be closed - setting bis null");
    		bis = null;
		}
    	
    	//release socket
    	try {
    		assert(clientSocket != null);
    		clientSocket.close();
    		System.out.println("socket successfully closed - new connection can be established");
    	} catch (AssertionError ae) {
    		System.err.println("socket is already null");
    		ae.printStackTrace();
    	} catch (IOException ioe) {
    		System.err.println("socket could not be closed - setting socket null");
    		ioe.printStackTrace();
    		clientSocket = null;
    	}
    }
    
    //schickt dem Server einen String anhand dieser entscheidet, welche Aktion er auszufÃ¼hren hat
    // refresh -> shareDirInformation oder fileName -> sendFileToClient
    public static void contactServer(String action){
		try {
			assert(action != null);
			//tell server how long our message will be (in bytes)
			bos.write(marshalling(action.getBytes().length));
			//write our message
			bos.write(action.getBytes());
			bos.flush();
			System.out.println("contacting server with request: "+action+", length in bytes: "+action.getBytes().length);
		} catch (AssertionError e) {
			System.err.println("action is null");
			e.printStackTrace();
			//TODO ALERT ->
		} catch (IOException e) {
			System.err.println("error occured while writing in streams - terminating connection");
			e.printStackTrace();
			closeStreams();
		}
    }
    
    //lese Datei von Server aus InputStream
    public static void downloadFileFromServer(String fileName) {
    	String filePath = sharePath + fileName;
    	
     
    	try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            assert(bos != null && baos != null);
    		// read fileLength from Client in order to avoid read-blocking and closing the socket on serverside (EOF)
            // marshalling
            byte[] fileLengthInBytes = new byte[4];
            bis.read(fileLengthInBytes);
            int fileLength = unmarshalling(fileLengthInBytes);
            
            // send data
        	byte[] file = new byte[fileLength];
            ps.read(file);
            
            // write data in boas to put it on the disk
            baos.write(file);
            bos.write(baos.toByteArray());
            bos.flush();   
            System.out.println("file recieved");
		
        } catch (AssertionError ae) {
			System.err.println("some client streams are null!");
			ae.printStackTrace();
			//TODO ALERT -> retry
		} catch (IOException e) {
        	System.err.println("some streams could not be initialized!");
			e.printStackTrace();
			//TODO ALERT -> retry
		}  
    }

    // empfange Share-Ordner Informationen von Server
    public static void receiveDirInformation() { 
			try {
				System.out.println("receiving server-directory information...");
				fileInformation = new ArrayList<FileInformation>();
				
				//retrieve how many fileInformation-sets will be send
	            byte[] fileCountBBuffer = new byte[4];
	            bis.read(fileCountBBuffer);
	            int fileCount = unmarshalling(fileCountBBuffer);
	            System.out.println(fileCount+" fileInformation-Sets expected");
	            
	            //retrieve header information of fileInformation sets
	        	byte[] lengthOfDataBBuffer = new byte[4];
	        	int lengthOfData;
	        	byte[] dataBBuffer;
	        	String fileName;
	        	String fileLength;
				for(int i = 0; i < fileCount; i++) {
					//read length of incoming message in bytes
		            bis.read(lengthOfDataBBuffer);
		            //convert length in bytes to int
		            lengthOfData = unmarshalling(lengthOfDataBBuffer);
		            System.out.print("incoming msg expected to be "+lengthOfData+" bytes long - ");
		            //initialize byte array which will contain the received message as bytes
		            dataBBuffer = new byte[lengthOfData];
		            //read message as bytes and write it in our buffer
		            bis.read(dataBBuffer);
		            //convert recieved data to a String
		            fileName = new String(dataBBuffer);
		            System.out.println("file "+fileName+" recieved");
		            
		            //do the same for the fileLength
		            bis.read(lengthOfDataBBuffer);
		            lengthOfData = unmarshalling(lengthOfDataBBuffer);
		            System.out.print("incoming msg expected to be "+lengthOfData+" bytes long - ");
		            dataBBuffer = new byte[lengthOfData];
		            bis.read(dataBBuffer);
		            fileLength = new String(dataBBuffer);
		            System.out.println("fileLength recieved: "+fileLength);
		            //create fileInformation object
					fileInformation.add(new FileInformation(fileName, fileLength));
				}
	        	System.out.println("directory content recieved");
			} catch (IOException e) {
				System.out.println("error occured while reading from streams - terminating connection");
				e.printStackTrace();
				closeStreams();
			}
			

    }
    
}

