package transfer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class TCPClient {
    
    private final static String sharePath = "C:\\Users\\Mirco\\Desktop\\testordner";
    private static Socket clientSocket = null;

    public static ArrayList<String> fileNames = null;
    public static ArrayList<Long> fileLengths = null;
    
    private static ObjectInputStream ois 		 = null;
    private static ObjectOutputStream oos        = null;
    
    public static void connectToServer(String serverIP, int serverPort) throws IOException {

	    // Verbindungsaufbau
	    clientSocket = new Socket(serverIP , serverPort);
	    System.out.println("connection with server successfully established");
	    initializeStreams();

        if(clientSocket == null) {   	
            // Verbindungsaufbau
            clientSocket = new Socket( serverIP , serverPort );
            System.out.println("Connection established");
            
            // Empfange DirInformation
            receiveDirInformation();
        }
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
    public static void deleteConnection () {
    	if(clientSocket != null || clientSocket.isConnected()) {
    		clientSocket.isClosed();
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
    /*
    public static void downloadFileFromServer(String fileName) {
    	String filePath = sharePath + "\\" + fileName;
     
    	try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
        	
            assert(bos != null && baos != null);
            	byte[] aByte = new byte[1];
                int bytesRead;
                int byteCounter = 0;

                bytesRead = is.read(aByte, 0, aByte.length);
                do {
                		byteCounter++;
                        baos.write(aByte);
                        bytesRead = is.read(aByte);
                } while (bytesRead != -1);

                System.out.println("Die Datei wurde empfangen");
                System.out.println("Dateigroese: " + byteCounter);
                                
                bos.write(baos.toByteArray());
                bos.flush();             
		
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
    */
    // empfange Share-Ordner Informationen von Server
    public static void receiveDirInformation() { 
			try {
				System.out.println("receiving server-directory information...");
	        	fileNames = (ArrayList<String>) ois.readObject();
	        	fileLengths  = (ArrayList<Long>) ois.readObject();
	        	System.out.println("directory content recieved:");
				for (int i = 0; i < fileNames.size(); i++) {
					System.out.println(fileNames.get(i) + " " + fileLengths.get(i) + " Bytes");
				}
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("error occured while reading from ObjectInputStream");
				e.printStackTrace();
				System.exit(1);
			}

    }
    
}

