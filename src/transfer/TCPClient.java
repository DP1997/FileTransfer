package transfer;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import datatypes.FileInformation;
import datatypes.ProgressStream;
import utils.FileUtils;

public class TCPClient {
    
    public static String sharePath = "";
    private static Socket clientSocket = null;
    
    private static ObjectInputStream ois = null;
    private static ObjectOutputStream oos = null;
    private static InputStream is = null;
    
    public static ArrayList<FileInformation> fileInformation = null;

    
    public static void connectToServer(String serverIP, String serverPort) throws Exception{
    		SocketAddress sockaddr = new InetSocketAddress(serverIP, Integer.valueOf(serverPort));
    		
    		clientSocket = new Socket();
    		// Connect with 2 s timeout
    		clientSocket.connect(sockaddr, 1000);
    		System.out.println("connection with server successfully established");
    		initializeStreams();
    }

    public static void setDownloadPath(String sharePath) {	
    	if(TCPClient.sharePath != "BITTE PFAD ANGEBEN" && TCPClient.sharePath != null) {
        	TCPClient.sharePath = sharePath; 	
    	}
    	//otherwise error
    	
    }
    public static void showInExplorer() throws IOException {
    	Desktop.getDesktop().open(new File(sharePath));
    }

    
    //versucht die alle benötigten Streams zu initialisieren
    private static void initializeStreams() {
    	
    	try {
    		assert(clientSocket != null && !clientSocket.isClosed());
    	} catch (AssertionError ae) {
    		System.err.println("clientSocket is null or closed");
    	}

    	try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(clientSocket.getInputStream());
			is = clientSocket.getInputStream();
			assert(oos != null && ois != null && is != null);
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
    public static void deleteConnection () throws IOException {
    	if(clientSocket != null) {
    		clientSocket.close();
    	}
    }
    
//    //versucht alle offenen Streams zu schließen
//    private static void closeStreams() {
//    	
//    	try {
//    		assert(clientSocket != null && !clientSocket.isClosed());
//    	} catch (AssertionError ae) {
//    		System.err.println("clientSocket is null or closed");
//    	}
//
//    	try {
//			assert(oos != null && ois != null);
//			oos.close();
//			ois.close();
//			oos = null;
//			ois = null;
//    	} catch (AssertionError ae) {
//			System.err.println("ObjectStream(s) is(are) null. Cannot close Streams properly.");
//			ae.printStackTrace();
//			System.exit(1);
//    	} catch (IOException ioe) {
//    		System.err.println("error occured while closing ObjectStreams");
//    		ioe.printStackTrace();
//    		System.exit(1);
//		}
//    }
    
    //schickt dem Server einen String anhand dieser entscheidet, welche Aktion er auszuführen hat
    // refresh -> shareDirInformation oder fileName -> sendFileToClient
    public static void contactServer(String action){
		try {
			assert(oos != null);
			oos.writeObject(action);
			System.out.println("contacting server with request: "+action);
		} catch (AssertionError e) {
			System.err.println("action is null");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("error occured while writing in ObjectOutputStream");
			e.printStackTrace();
			System.exit(1);
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
            is.read(fileLengthInBytes);
            int fileLength = unmarshalling(fileLengthInBytes);
            
            // send data
        	byte[] file = new byte[fileLength];
            is.read(file);
            
            // write data in boas to put it on the disk
            baos.write(file);
            bos.write(baos.toByteArray());
            bos.flush();   
            System.out.println("Die Datei wurde empfangen");
		
        } catch (AssertionError ae) {
			System.err.println("some client streams are null!");
			ae.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
        	System.err.println("some streams could not be initialized!");
			e.printStackTrace();
			System.exit(1);
		}  
    }
    private static int unmarshalling(byte[] buffer) {
    	int n = 0;
    	for(int i = 0; i < 4; i++) {
    		n<<=8;
    		n |= (int) buffer[i] & 0xFF;
    	}
    	return n;
    }
    
    // empfange Share-Ordner Informationen von Server
    public static void receiveDirInformation() { 
			try {
				System.out.println("receiving server-directory information...");
				fileInformation = new ArrayList<FileInformation>();
				int fileCount = (Integer)ois.readObject();
				System.out.println(fileCount);
				for(int i = 0; i < fileCount; i++) {
					fileInformation.add(new FileInformation((String)ois.readObject(), (String)ois.readObject()));
				}
	        	System.out.println("directory content recieved");
			} catch (ClassNotFoundException e) {
				System.err.println("error occured while reading from ObjectInputStream");
				e.printStackTrace();
				System.exit(1);
			} catch (IOException e) {
				System.err.println("error occured while reading from ObjectInputStream");
				e.printStackTrace();
				System.exit(1);
			} 

    }
    
}

