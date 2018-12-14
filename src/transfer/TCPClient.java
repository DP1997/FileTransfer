package transfer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import datatypes.FileInformation;
import utils.FileUtils;

public class TCPClient {

    private final static String serverIP = "192.168.0.7";
    private final static int serverPort = 3248;
    
    private final static String sharePath = "C:\\Users\\Mirco\\Desktop\\testordner";
    private static Socket clientSocket = null;

    private static ArrayList<String> fileNames;
    
    public static void main(String args[]) throws IOException {
        
        try {
        	
            // Verbindungsaufbau
            clientSocket = new Socket( serverIP , serverPort );
            System.out.println("Connection established");
            
            // Empfange DirInformation
            receiveDirInformation();
            
            Scanner sc = new Scanner(System.in);
            String fileName = sc.nextLine();
        	// teile Server den angeforderten Dateinamen mit
            contactServer(fileName);
            
            //lade Datei herunter
            downloadFileFromServer(fileName);
            System.out.println("Download erfolgreich");
        } catch (IOException ex) {
            ex.printStackTrace();
        }		
    }
    
    public static void contactServer(String action){
    	try (PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true)){
    		// auto-flush
    		pw.println((action));
    		System.out.println("Suche nach File: " + action);
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    }
    
    public static void downloadFileFromServer(String fileName) {
    	
    	String filePath = sharePath + "\\" + fileName;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	InputStream is = clientSocket.getInputStream();
        	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            
            if (is != null && bos != null && baos != null) {
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
            }
        } catch (IOException e) {
			e.printStackTrace();
		}

        
    }
    
    public static void receiveDirInformation() {
        // empfange Share-Ordner Informationen
        try (ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream())){
        	
        	Object fileNamesObj = objectInput.readObject();
        	Object fileLengthsObj = objectInput.readObject();
        	fileNames = (ArrayList<String>) fileNamesObj;
        	ArrayList<Long> fileLengths  = (ArrayList<Long>) fileLengthsObj;
        	System.out.println("Ordner des Servers:");
			for (int i = 0; i < fileNames.size(); i++) {
				System.out.println(fileNames.get(i) + " " + fileLengths.get(i) + " Bytes");
			}
			System.out.println("Share-Ordner empfangen.");
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
}

